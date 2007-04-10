/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.tests.contentmodel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.wst.xml.core.internal.Logger;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAnyElement;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMContent;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMEntityDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNodeList;
import org.eclipse.wst.xml.core.internal.contentmodel.util.CMVisitor;


public class CMPrinter extends CMVisitor
{
  protected StringBuffer fStringBuffer = new StringBuffer();
  protected Vector visitedElements = new Vector(); 
    
  /******   formatting    ******/
  protected String indent = ""; //$NON-NLS-1$

  protected void incrementIndent()
  {
    indent = indent + "  "; //$NON-NLS-1$
  }

  protected void decrementIndent()
  {
    indent = indent.substring(0, indent.length()-2);
  }


  public void printCMNode(String filename, CMNode cmnode)
  {
    visitCMNode(cmnode);
    try 
    {
      FileOutputStream fileStream = new FileOutputStream(filename);
      OutputStreamWriter writer = new OutputStreamWriter(fileStream);
      writer.write(fStringBuffer.toString());
      writer.flush();
      fileStream.close();
    }
    catch (IOException e) {
    	Logger.logException("PMPrinter Debug: ", e);
    }  
  }

  public void visitCMAnyElement(CMAnyElement anyElement)
  {
    fStringBuffer.append(indent + "<CMAnyElement"); //$NON-NLS-1$
    printAttributes(fStringBuffer, anyElement);
    fStringBuffer.append(">/n"); //$NON-NLS-1$
    incrementIndent();
    printProperties(fStringBuffer, anyElement);
    decrementIndent();
    fStringBuffer.append(indent + "</CMAnyElement>/n"); //$NON-NLS-1$
  }

  public void visitCMAttributeDeclaration(CMAttributeDeclaration ad)
  {
    fStringBuffer.append(indent + "<CMAttributeDeclaration"); //$NON-NLS-1$
    printAttributes(fStringBuffer, ad);
    fStringBuffer.append(">\n"); //$NON-NLS-1$
    incrementIndent();
    printProperties(fStringBuffer, ad);
    decrementIndent();
    visitCMNode(ad.getAttrType());
    fStringBuffer.append(indent + "</CMAttributeDeclaration>\n"); //$NON-NLS-1$
  }

  public void visitCMDataType(CMDataType dataType)
  {
    fStringBuffer.append(indent + "<CMDataType"); //$NON-NLS-1$
    printAttributes(fStringBuffer, dataType);
    fStringBuffer.append(">\n"); //$NON-NLS-1$
    incrementIndent();
    fStringBuffer.append(indent + "<ImpliedValue kind=\"" + dataType.getImpliedValueKind() + "\">"); //$NON-NLS-1$ //$NON-NLS-2$
    fStringBuffer.append(dataType.getImpliedValue() + "</ImpliedValue>\n"); //$NON-NLS-1$
    printEnumeration(fStringBuffer, dataType.getEnumeratedValues());
    decrementIndent();
    fStringBuffer.append(indent + "</CMDataType>\n"); //$NON-NLS-1$
  }

  public void visitCMDocument(CMDocument document)
  {
    fStringBuffer.append(indent + "<CMDocument"); //$NON-NLS-1$
    printAttributes(fStringBuffer, document);
    fStringBuffer.append(">\n"); //$NON-NLS-1$
    incrementIndent();

    //    printEntities(sb, document.getEntities()); 
    printProperties(fStringBuffer, document);

    CMNamedNodeMap map = document.getElements();
    int size = map.getLength();
    for (int i = 0; i < size; i++)
    {
      visitCMNode(map.item(i));
    }

    decrementIndent();
    fStringBuffer.append(indent + "</CMDocument>\n"); //$NON-NLS-1$
  }

  public void visitCMGroup(CMGroup group)
  {
    fStringBuffer.append(indent + "<CMGroup"); //$NON-NLS-1$
    printAttributes(fStringBuffer, group);
    fStringBuffer.append(">\n"); //$NON-NLS-1$
    incrementIndent();

    CMNodeList nodeList = group.getChildNodes();
    int size = nodeList.getLength();
    for (int i = 0; i < size; i++)
    {
      visitCMNode(nodeList.item(i));
    }

    decrementIndent();
    fStringBuffer.append(indent + "</CMGroup>\n"); //$NON-NLS-1$
  }

  public void visitCMElementDeclaration(CMElementDeclaration ed)
  {
    if (!visitedElements.contains(ed))
    {
      visitedElements.add(ed);
      fStringBuffer.append(indent + "<CMElementDeclaration"); //$NON-NLS-1$
      printAttributes(fStringBuffer, ed);
      fStringBuffer.append(">\n"); //$NON-NLS-1$
      incrementIndent();
      printProperties(fStringBuffer, ed);

      CMNamedNodeMap nodeMap = ed.getAttributes();
      int size = nodeMap.getLength();
      for (int i = 0; i < size; i++)
      {
        visitCMNode(nodeMap.item(i));
      }

      visitCMNode(ed.getContent());

      CMDataType dataType = ed.getDataType();
      if (dataType != null)
        visitCMNode(dataType);

      decrementIndent();
      fStringBuffer.append(indent + "</CMElementDeclaration>\n"); //$NON-NLS-1$
    }
  }


  public void printEnumeration(StringBuffer sb, String[] values)
  {
    if ((values != null) && (values.length > 0))
    {
      sb.append(indent + "<Enumeration>\n"); //$NON-NLS-1$
      incrementIndent();

      for (int i=0; i<values.length; i++)
      {
        sb.append(indent + "<Value>" + values[i] + "</Values>\n"); //$NON-NLS-1$ //$NON-NLS-2$
      }

      decrementIndent();
      sb.append(indent + "</Enumeration>\n"); //$NON-NLS-1$
    }
  }


  public void printAttributes(StringBuffer sb, CMNode cmnode)
  {
    if (cmnode != null)
    {
      sb.append(" nodeName=\"" + cmnode.getNodeName() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
      sb.append(" nodeType=\"" + cmnode.getNodeType() + "\""); //$NON-NLS-1$ //$NON-NLS-2$

      if (cmnode instanceof CMContent)
      {
        sb.append(" minOccur=\"" + ((CMContent)cmnode).getMinOccur() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append(" maxOccur=\"" + ((CMContent)cmnode).getMaxOccur() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
      }
      if (cmnode instanceof CMAnyElement)
      {
        sb.append(" namespaceURI=\"" + ((CMAnyElement)cmnode).getNamespaceURI() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
      }
      if (cmnode instanceof CMAttributeDeclaration)
      {
        sb.append(" uasage=\"" + ((CMAttributeDeclaration)cmnode).getUsage() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append(" defaultValue=\"" + ((CMAttributeDeclaration)cmnode).getDefaultValue() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
      }
      if (cmnode instanceof CMDataType)
      { 
        sb.append(" instanceValue=\"" + ((CMDataType)cmnode).generateInstanceValue() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
      }
      if (cmnode instanceof CMEntityDeclaration)
      {
        String value = ((CMEntityDeclaration)cmnode).getValue();
        if (value.indexOf("\"") == -1) sb.append(" value=\"" + value + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        else sb.append(" value=\"" + value + "\""); //$NON-NLS-1$ //$NON-NLS-2$
      }
      if (cmnode instanceof CMGroup)
      {
        sb.append(" operator=\"" + ((CMGroup)cmnode).getOperator() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
      }
      if (cmnode instanceof CMElementDeclaration)
      {
        sb.append(" contentType=\"" + ((CMElementDeclaration)cmnode).getContentType() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
      }
    }
  }


  public void printEntities(StringBuffer sb, CMNamedNodeMap entities)
  {
    if ((entities != null) && (entities.getLength()>0))
    {
      sb.append(indent + "<Entities>\n"); //$NON-NLS-1$
      incrementIndent();

      for (Iterator i = entities.iterator(); i.hasNext();)
      {
        CMEntityDeclaration entity = (CMEntityDeclaration)i.next();
        sb.append(indent + "<Entity"); //$NON-NLS-1$
        printAttributes(sb, entity);
        sb.append("/>\n"); //$NON-NLS-1$
      }

      decrementIndent();
      sb.append(indent + "</Entities>\n"); //$NON-NLS-1$
    }
  }

  public void printProperties(StringBuffer sb, CMNode cmnode)
  {
    List properties = getProperties(cmnode);
    sb.append(indent + "<Properties>\n"); //$NON-NLS-1$
    incrementIndent();

    for (int i=0; i<properties.size(); i++)
    {
      String property = (String)properties.get(i);
      sb.append(indent + "<Property name=\"" + property + "\""); //$NON-NLS-1$ //$NON-NLS-2$

      if (cmnode.supports(property))
      {
        sb.append(" supports=\"true\""); //$NON-NLS-1$
      }
      else
      {
        sb.append(" supports=\"false\""); //$NON-NLS-1$
      }
      Object value = cmnode.getProperty(property);
      if (value instanceof String) sb.append(" value=\"" + (String)value + "\""); //$NON-NLS-1$ //$NON-NLS-2$
      else if (value instanceof Boolean) sb.append(" value=\"" + value + "\""); //$NON-NLS-1$ //$NON-NLS-2$
      else if (value != null) sb.append(" value=\"" + value.getClass() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
      else sb.append(" value=\"null\""); //$NON-NLS-1$
      sb.append("/>\n"); //$NON-NLS-1$
    }

    decrementIndent();
    sb.append(indent + "</Properties>\n"); //$NON-NLS-1$
  }

  public List getProperties(CMNode cmnode)
  {
    List properties = new Vector();
      
    if (cmnode != null)
    {
      properties.add("CMDocument"); //$NON-NLS-1$
      properties.add("documentation"); //$NON-NLS-1$
      properties.add("http://org.eclipse.wst/cm/properties/usesLocalElementDeclarations"); //$NON-NLS-1$
      properties.add("http://org.eclipse.wst/cm/properties/isNameSpaceAware"); //$NON-NLS-1$
      properties.add("http://org.eclipse.wst/cm/properties/nsPrefixQualification"); //$NON-NLS-1$
      properties.add("http://org.eclipse.wst/cm/properties/nillable"); //$NON-NLS-1$
      properties.add("http://org.eclipse.wst/cm/properties/mofNotifier"); //$NON-NLS-1$
      properties.add("spec"); //$NON-NLS-1$

      if (cmnode instanceof CMElementDeclaration)
      {
        properties.add("http://org.eclipse.wst/cm/properties/definitionInfo"); //$NON-NLS-1$
        properties.add("http://org.eclipse.wst/cm/properties/definition"); //$NON-NLS-1$
        properties.add("XSITypes"); //$NON-NLS-1$
        properties.add("DerivedElementDeclaration"); //$NON-NLS-1$
        // properties.add("SubstitutionGroup"); Currrently broken for nexted xsd files
        properties.add("Abstract"); //$NON-NLS-1$
      }
      if (cmnode instanceof CMDocument)
      {
        properties.add("http://org.eclipse.wst/cm/properties/targetNamespaceURI"); //$NON-NLS-1$
        properties.add("http://org.eclipse.wst/cm/properties/importedNamespaceInfo"); //$NON-NLS-1$
        properties.add("http://org.eclipse.wst/cm/properties/namespaceInfo"); //$NON-NLS-1$
        properties.add("http://org.eclipse.wst/cm/properties/elementFormDefault"); //$NON-NLS-1$
        properties.add("annotationMap"); //$NON-NLS-1$
      }
    }
    return properties;
  }
  
}

 
