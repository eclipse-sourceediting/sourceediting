/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.ui.reconcile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.validation.core.IFileDelta;
import org.eclipse.wst.validation.core.IHelper;
import org.eclipse.wst.validation.core.IMessage;
import org.eclipse.wst.validation.core.IMessageAccess;
import org.eclipse.wst.validation.core.IReporter;
import org.eclipse.wst.validation.core.IValidator;
import org.eclipse.wst.validation.core.ValidationException;
import org.eclipse.wst.xml.core.document.XMLAttr;
import org.eclipse.wst.xml.core.document.XMLDocument;
import org.eclipse.wst.xml.core.document.XMLElement;
import org.eclipse.wst.xml.core.document.XMLModel;
import org.eclipse.wst.xml.core.document.XMLNode;
import org.eclipse.wst.xml.core.document.XMLText;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A DelegatingReconcileValidator calls its delegate validator
 * to get a list of validation error IMessages.  Using information
 * in this IMessage the DelegatingReconcileValidator updates the IMessage with
 * an offset and length to give a good range to be "squiggled"
 * and adds the messages to the IReporter
 * 
 * @author Mark Hutchinson
 * 
 */
public class DelegatingReconcileValidator implements IValidator
{ //these are the selection Strategies:
  protected static final String ALL_ATTRIBUTES = "ALL_ATTRIBUTES";
  protected static final String ATTRIBUTE_NAME ="ATTRIBUTE_NAME";
  protected static final String ATTRIBUTE_VALUE = "ATTRIBUTE_VALUE";
  protected static final String START_TAG = "START_TAG";
  protected static final String TEXT = "TEXT";
  protected static final String FIRST_NON_WHITESPACE_TEXT = "FIRST_NON_WHITESPACE_TEXT";
  protected static final String TEXT_ENTITY_REFERENCE = "TEXT_ENTITY_REFERENCE"; 
  protected static final String NAME_OF_ATTRIBUTE_WITH_GIVEN_VALUE = "NAME_OF_ATTRIBUTE_WITH_GIVEN_VALUE";
  
  protected static final String COLUMN_NUMBER_ATTRIBUTE = "columnNumber";
  protected static final String SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE = "squiggleSelectionStrategy";
  protected static final String SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE = "squiggleNameOrValue";
  
  public DelegatingReconcileValidator()
  { super(); //constructor
  }
  
  public void cleanup(IReporter arg0)
  { // don't need to implement
  }
  
  // My Implementation of IHelper
  class MyHelper implements IHelper
  {
    InputStream inputStream;

    IFile file;

    public MyHelper(InputStream inputStream, IFile file)
    { this.inputStream = inputStream;
      this.file = file;
    }

    public Object loadModel(String symbolicName, Object[] parms)
    { if (symbolicName.equals("getFile"))
      { return file;
      }
      return null;
    }

    public Object loadModel(String symbolicName)
    { if (symbolicName.equals("inputStream"))
      { return inputStream;
      }
      return null;
    }
  }
  //My Implementation of IReporter
  class MyReporter implements IReporter
  {
    List list = new ArrayList();
    
    public MyReporter()
    { super();      
    }
    
    public void addMessage(IValidator origin, IMessage message)
    { list.add(message);
    }

    public void displaySubtask(IValidator validator, IMessage message)
    {//do not need to implement
    }

    public IMessageAccess getMessageAccess()
    { return null; //do not need to implement
    }

    public boolean isCancelled()
    { return false; //do not need to implement
    }

    public void removeAllMessages(IValidator origin, Object object)
    { //do not need to implement
    }

    public void removeAllMessages(IValidator origin)
    {//do not need to implement
    }

    public void removeMessageSubset(IValidator validator, Object obj, String groupName)
    {//do not need to implement
    }
  }

  protected IValidator getDelegateValidator()
  {   return null;//this class must be overridden by a subclass
  }
  
  /**
   * Calls a delegate validator getting and updates it's list of ValidationMessages
   * with a good squiggle offset and length.
   * 
   * @param helper 
   *      loads an object. 
   * @param reporter
   *      Is an instance of an IReporter interface, which is used for
   * interaction with the user.
   * @param delta
   *      an array containing the file to be validated.  Only position 0 will be validated
   */
  public void validate(IHelper helper, IReporter reporter, IFileDelta[] delta) throws ValidationException
  {
    if (delta.length > 0)
    {
      // get the file, model and document:
      IFile file = getFile(delta[0]);
      XMLModel xmlModel = getModelForResource(file);
      try
      {
        XMLDocument document = xmlModel.getDocument();

        // store the text in a byte array; make a full copy to ease any threading problems
        byte[] byteArray = xmlModel.getStructuredDocument().get().getBytes();
        
        IValidator validator = getDelegateValidator();
        if (validator != null)
        {
          //Validate the file:
          IHelper vHelper = new MyHelper(new ByteArrayInputStream(byteArray), file);
          MyReporter vReporter = new MyReporter();
          validator.validate(vHelper, vReporter, delta);
          List messages = vReporter.list;
  
          //set the offset and length
          updateValidationMessages(messages, document, reporter);
        }
      }

      finally
      {
        if (xmlModel != null)
          xmlModel.releaseFromRead();
      }
    }
  }

  /**
   * iterates through the messages and calculates a "better" offset and length
   * 
   * @param messages - a List of IMessages
   * @param document - the document
   * @param reporter - the reporter the messages are to be added to
   */
  protected void updateValidationMessages(List messages, XMLDocument document, IReporter reporter)
  {
    for (int i = 0; i < messages.size(); i++)
    {
      IMessage message = (IMessage) messages.get(i);    
      try
      { 
        int column = ((Integer)message.getAttribute(COLUMN_NUMBER_ATTRIBUTE)).intValue();
        String selectionStrategy = (String)message.getAttribute(SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE);
        String nameOrValue = (String)message.getAttribute(SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE);
        
        // convert the line and Column numbers to an offset:        
        int start = document.getStructuredDocument().getLineOffset(message.getLineNumber() - 1) + column - 1;

        // calculate the "better" start and end offset:
        int[] result = computeStartEndLocation(start, message.getText(), selectionStrategy, nameOrValue, document);
        if (result != null)
        {
          message.setOffset(result[0]);
          message.setLength(result[1] - result[0]);
          reporter.addMessage(this, message);
        }
      }
      catch(BadLocationException e)
      {  //this exception should not occur - it is thrown if trying to convert an invalid line number to and offset       
      }

    }
  }
 /**
  * @param delta the IFileDelta containing the file name to get
  * @return the IFile
  */
  public IFile getFile(IFileDelta delta)
  {
    IResource res = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(delta.getFileName()));
    return res instanceof IFile ? (IFile) res : null;
  }
  /**
   * 
   * @param file the file to get the model for
   * @return the file's XMLModel
   */
  protected XMLModel getModelForResource(IFile file)
  {
    IStructuredModel model = null;
    IModelManager manager = StructuredModelManager.getModelManager();

    try
    { model = manager.getModelForRead(file);
      // TODO.. HTML validator tries again to get a model a 2nd way
    }
    catch (Exception e)
    {
      //e.printStackTrace();
    }

    return model instanceof XMLModel ? (XMLModel) model : null;
  }
  
  /**
   * Calculates the "better" offsets.
   *    
   * @param startOffset - the offset given by Xerces
   * @param errorMessage - the Xerces error Message
   * @param selectionStrategy - the selectionStrategy
   * @param document - the document
   * @return int[] - position 0 has the start offset of the squiggle range, position 1 has the endOffset
   */
  /*
   *   The way the offsets is calculated is:
   *   
   *    - find the indexed region (element) closest to the given offset
   *    - if we are between two elements, the one on the left is the one we want
   *    - based on the selectionStrategy choose the underlining strategy (eg START_TAG
   *       means underline the start tag of that element)
   *    - use information from nameOrValue and the DOM to get better offsets
   *    
   */
  protected int[] computeStartEndLocation(int startOffset, String errorMessage, String selectionStrategy, String nameOrValue, XMLDocument document)
  {
    try
    {
      int startEndPositions[] = new int[2];

      IndexedRegion region = document.getModel().getIndexedRegion(startOffset);
      IndexedRegion prevRegion = document.getModel().getIndexedRegion(startOffset - 1);
      
      if (prevRegion != region)
      { // if between two regions, the one on the left is the one we are interested in
        region = prevRegion;
      }

      // initialize start and end positions to be the start positions this means if the
      // special case is not taken care of below the start and end offset are set to be
      // the start of the region where the error was
      if (region != null)
      {
        startEndPositions[0] = region.getStartOffset();
        startEndPositions[1] = startEndPositions[0];
      }
      else
      { //this will message will not get added to the IReporter since the length is 0
        startEndPositions[0] = 0;
        startEndPositions[1] = 0;
      }
      if (region instanceof Node)
      {
        Node node = (Node) region;
        
        if (selectionStrategy.equals(START_TAG))
        {// then we want to highlight the opening tag
          if (node.getNodeType() == Node.ELEMENT_NODE)
          {
            XMLElement element = (XMLElement) node;
            startEndPositions[0] = element.getStartOffset() + 1;
            startEndPositions[1] = startEndPositions[0] + element.getTagName().length();
          }
        }
        else if (selectionStrategy.equals(ATTRIBUTE_NAME))
        { // in this case we want to underline the offending attribute name
          if (node.getNodeType() == Node.ELEMENT_NODE)
          {
            XMLElement element = (XMLElement) node;
            XMLNode attributeNode = (XMLNode) (element.getAttributeNode(nameOrValue));
            if (attributeNode != null)
            {
              startEndPositions[0] = attributeNode.getStartOffset();
              startEndPositions[1] = attributeNode.getStartOffset() + nameOrValue.length();
            }
          }
        }
        else if (selectionStrategy.equals(ATTRIBUTE_VALUE))
        {// in this case we want to underline the attribute's value
          if (node.getNodeType() == Node.ELEMENT_NODE)
          {
            XMLElement element = (XMLElement) node;
            XMLAttr attributeNode = (XMLAttr) (element.getAttributeNode(nameOrValue));
            if (attributeNode != null)
            {
              startEndPositions[0] = attributeNode.getValueRegionStartOffset();
              startEndPositions[1] = startEndPositions[0] + attributeNode.getValueRegionText().length();
            }
          }
        }
        else if (selectionStrategy.equals(ALL_ATTRIBUTES))
        {// then underline ALL attributes
          if (node.getNodeType() == Node.ELEMENT_NODE)
          {
            XMLElement element = (XMLElement) node;
            NamedNodeMap attributes = element.getAttributes();
            if (attributes != null)
            {
              XMLNode first = (XMLNode) attributes.item(0);
              XMLNode last = (XMLNode) attributes.item(attributes.getLength() - 1);
              if (first != null && last != null)
              {
                startEndPositions[0] = first.getStartOffset();
                startEndPositions[1] = last.getEndOffset();
              }
            }
          }
        }
        else if (selectionStrategy.equals(TEXT))
        {// in this case we want to underline the text (but not any extra whitespace)
          if (node.getNodeType() == Node.TEXT_NODE)
          {
            XMLText textNode = (XMLText) node;
            int start = textNode.getStartOffset();
            String value = textNode.getNodeValue();
            int index = 0;
            char curChar = value.charAt(index);
            //here we are finding start offset by skipping over whitespace:
            while (curChar == '\n' || curChar == '\t' || curChar == '\r' || curChar == ' ')
            {
              curChar = value.charAt(index);
              index++;
              start++;
            }
            startEndPositions[0] = start - 1;
            startEndPositions[1] = start + value.trim().length() - 1;
          }
          else if (node.getNodeType() == Node.ELEMENT_NODE)
          {
            XMLElement element = (XMLElement) node;
            Node child = element.getFirstChild();
            if (child instanceof XMLNode)
            {
              XMLNode xmlChild = ((XMLNode) child);
              startEndPositions[0] = xmlChild.getStartOffset();
              startEndPositions[1] = xmlChild.getEndOffset();
            }
          }
        }
        else if (selectionStrategy.equals(FIRST_NON_WHITESPACE_TEXT))
        {  //here we search through all the child nodes, and give the range
           // of the first non-whitespace node
            if (node.getNodeType() == Node.ELEMENT_NODE)
            { NodeList nodes = node.getChildNodes();
              for (int i = 0; i< nodes.getLength(); i++)
              {
                Node currentNode = nodes.item(i);
                if (currentNode.getNodeType() == Node.TEXT_NODE)
                {
                    XMLText textNode = (XMLText)currentNode;
                    if (textNode.getNodeValue().trim().length() > 0)
                    {
                        String value = textNode.getNodeValue();
                        int index = 0;
                        int start = textNode.getStartOffset();
                        char curChar = value.charAt(index);
                        //here we are finding start offset by skipping over whitespace:
                        while (curChar == '\n' || curChar == '\t' || curChar == '\r' || curChar == ' ')
                        {
                          curChar = value.charAt(index);
                          index++;
                        }
                        if (index > 0)
                        { index--;
                        
                        }
                        start = start + index;
                        startEndPositions[0] = start;
                        startEndPositions[1] = start + value.trim().length(); 
                        break;
                    }
                }
                
              }
            }
        }
            
        else if (selectionStrategy.equals(TEXT_ENTITY_REFERENCE))
        { if (node.getNodeType() == Node.ENTITY_REFERENCE_NODE)
            { startEndPositions[0] = region.getStartOffset();
              startEndPositions[1] = region.getEndOffset();
            }
        }
        else if (selectionStrategy.equals(NAME_OF_ATTRIBUTE_WITH_GIVEN_VALUE))
        {//underline the name of the attribute containing the given value
          if (node.getNodeType() == Node.ELEMENT_NODE)
          {
            //here we will search through all attributes for the one with the
            //with the value we want:            
            NamedNodeMap attributes = node.getAttributes();
            for (int i=0; i<attributes.getLength(); i++)
            {
              XMLAttr attr = (XMLAttr)attributes.item(i);
              String nodeValue = attr.getNodeValue().trim();
              if (nodeValue.equals(nameOrValue))
              { startEndPositions[0] = attr.getValueRegionStartOffset() + 1;
                startEndPositions[1] = startEndPositions[0] + nodeValue.length();
                break;
              }
            }
          }
        }
      }
      return startEndPositions;
    }
    catch (Exception e)
    { //e.printStackTrace();
    }
    return null;
  }  
}
