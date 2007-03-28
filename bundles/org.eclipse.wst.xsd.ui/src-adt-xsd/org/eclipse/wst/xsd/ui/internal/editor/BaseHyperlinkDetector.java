/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *******************************************************************************/

package org.eclipse.wst.xsd.ui.internal.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Base class for hyperlinks detectors. Provides a framework and common code for
 * hyperlink detectors. TODO: Can we pull this class further up the inheritance
 * hierarchy?
 */
public abstract class BaseHyperlinkDetector extends AbstractHyperlinkDetector
{
  /*
   * (non-Javadoc)
   */
  public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks)
  {
    if (region == null || textViewer == null)
    {
      return null;
    }

    List hyperlinks = new ArrayList(0);
    IDocument document = textViewer.getDocument();
    int offset = region.getOffset();

    IDOMNode node = getCurrentNode(document, offset);

    // This call allows us to determine whether an attribute is linkable,
    // without incurring the cost of asking for the target component.

    if (!isLinkable(node))
    {
      return null;
    }

    IRegion hyperlinkRegion = getHyperlinkRegion(node);

    // createHyperlink is a template method. Derived classes, should override.

    IHyperlink hyperlink = createHyperlink(document, node, hyperlinkRegion);

    if (hyperlink != null)
    {
      hyperlinks.add(hyperlink);
    }

    if (hyperlinks.size() == 0)
    {
      return null;
    }

    return (IHyperlink[]) hyperlinks.toArray(new IHyperlink[0]);
  }

  /**
   * Determines whether a node is "linkable" that is, the component it refers to
   * can be the target of a "go to definition" navigation.
   * 
   * @param node the node to test, must not be null;
   * @return true if the node is linkable, false otherwise.
   */
  private boolean isLinkable(IDOMNode node)
  {
    if (node == null)
    {
      return false;
    }

    short nodeType = node.getNodeType();

    boolean isLinkable = false;

    if (nodeType == Node.ATTRIBUTE_NODE)
    {
      IDOMAttr attr = (IDOMAttr) node;
      String name = attr.getName();

      // isLinkableAttribute is a template method. Derived classes should
      // override.

      isLinkable = isLinkableAttribute(name);
    }

    return isLinkable;
  }

  /**
   * Determines whether an attribute is "linkable" that is, the component it
   * points to can be the target of a "go to definition" navigation. Derived
   * classes should override.
   * 
   * @param name the attribute name. Must not be null.
   * @return true if the attribute is linkable, false otherwise.
   */
  protected abstract boolean isLinkableAttribute(String name);

  /**
   * Creates a hyperlink based on the selected node. Derived classes should
   * override.
   * 
   * @param document the source document.
   * @param node the node under the cursor.
   * @param region the text region to use to create the hyperlink.
   * @return a new IHyperlink for the node or null if one cannot be created.
   */
  protected abstract IHyperlink createHyperlink(IDocument document, IDOMNode node, IRegion region);

  /**
   * Locates the attribute node under the cursor.
   * 
   * @param offset the cursor offset.
   * @param parent the parent node
   * @return an IDOMNode representing the attribute if one is found at the
   *         offset or null otherwise.
   */
  protected IDOMNode getAttributeNode(int offset, IDOMNode parent)
  {
    IDOMAttr attrNode = null;
    NamedNodeMap map = parent.getAttributes();

    for (int index = 0; index < map.getLength(); index++)
    {
      attrNode = (IDOMAttr) map.item(index);
      boolean located = attrNode.contains(offset);
      if (located)
      {
        if (attrNode.hasNameOnly())
        {
          attrNode = null;
        }
        break;
      }
    }

    if (attrNode == null)
    {
      return parent;
    }
    return attrNode;
  }

  /**
   * Returns the node the cursor is currently on in the document or null if no
   * node is selected
   * 
   * @param offset the current cursor offset.
   * @return IDOMNode either element, doctype, text, attribute or null
   */
  private IDOMNode getCurrentNode(IDocument document, int offset)
  {
    IndexedRegion inode = null;
    IStructuredModel sModel = null;

    try
    {
      sModel = StructuredModelManager.getModelManager().getExistingModelForRead(document);
      inode = sModel.getIndexedRegion(offset);
      if (inode == null)
        inode = sModel.getIndexedRegion(offset - 1);
    }
    finally
    {
      if (sModel != null)
        sModel.releaseFromRead();
    }

    if (inode instanceof IDOMNode)
    {
      IDOMNode node = (IDOMNode) inode;

      if (node.hasAttributes())
      {
        node = getAttributeNode(offset, node);
      }
      return node;
    }

    return null;
  }

  /**
   * Get the text region corresponding to an IDOMNode.
   * 
   * @param node the node for which we want the text region. Must not be null.
   * @return an IRegion for the node, or null if the node is not recognized.
   */
  protected IRegion getHyperlinkRegion(IDOMNode node)
  {
    if (node == null)
    {
      return null;
    }

    IRegion hyperRegion = null;
    short nodeType = node.getNodeType();

    switch (nodeType)
    {
      case Node.ELEMENT_NODE : 
        {
          hyperRegion = new Region(node.getStartOffset(), node.getEndOffset() - node.getStartOffset());
        }
      break;
      case Node.ATTRIBUTE_NODE : 
        {
          IDOMAttr att = (IDOMAttr) node;
  
          int regOffset = att.getValueRegionStartOffset();
  
          // ISSUE: We are using a deprecated method here. Is there
          // a better way to get what we need?
  
          ITextRegion valueRegion = att.getValueRegion();
          if (valueRegion != null)
          {
            int regLength = valueRegion.getTextLength();
            String attValue = att.getValueRegionText();
  
            // Do not include quotes in attribute value region and only
            // underline the actual value, not the quotes.
            
            if (StringUtils.isQuoted(attValue))
            {
              regLength = regLength - 2;
              regOffset++;
            }
            hyperRegion = new Region(regOffset, regLength);
          }
        }
        break;
      default :
        // Do nothing.
        break;
    }
 
    return hyperRegion;
  }
}
