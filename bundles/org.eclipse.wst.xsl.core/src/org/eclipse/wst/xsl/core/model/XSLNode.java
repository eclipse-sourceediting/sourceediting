/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.core.model;

import org.w3c.dom.Node;

/**
 * A node in the XSL namespace.
 * 
 * @author Doug Satchwell
 * @since 1.0
 */
public abstract class XSLNode extends XSLModelObject
{
	/**
	 * A constant for the element node (= <code>org.w3c.dom.Node.ELEMENT_NODE</code>)
	 */
	public static final short ELEMENT_NODE = Node.ELEMENT_NODE;
	
	/**
	 * A constant for the attribute node (= <code>org.w3c.dom.Node.ATTRIBUTE_NODE</code>)
	 */
	public static final short ATTRIBUTE_NODE = Node.ATTRIBUTE_NODE;
	
	
	private final Stylesheet stylesheet;
	int lineNumber;
	int columnNumber;
	private int offset;
	private int length;
	private short type;
	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Create a new instance of this.
	 * 
	 * @param stylesheet the stylesheet this belongs to
	 * @param type one of <code>ELEMENT_NODE</code> or <code>ATTRIBUTE_NODE</code>
	 */
	public XSLNode(Stylesheet stylesheet, short type)
	{
		this.stylesheet = stylesheet;
		this.type = type;
	}

	/**
	 * Set the line number where this node occurs in the XSL file.
	 * 
	 * @param lineNumber the line number
	 */
	public void setLineNumber(int lineNumber)
	{
		this.lineNumber = lineNumber;
	}

	/**
	 * Set the columns number where this node occurs in the XSL file.
	 * 
	 * @param columnNumber the column number
	 */
	public void setColumnNumber(int columnNumber)
	{
		this.columnNumber = columnNumber;
	}

	/**
	 * Get the stylesheet that this belongs to.
	 * 
	 * @return the stylesheet
	 */
	public Stylesheet getStylesheet()
	{
		return stylesheet;
	}

	/**
	 * Set the line number where this node occurs in the XSL file.
	 * @return the line number where this node occurs in the XSL file.
	 */
	public int getLineNumber()
	{
		return lineNumber;
	}

	/**
	 * Set the column number where this node occurs in the XSL file.
	 * @return the column number where this node occurs in the XSL file.
	 */
	public int getColumnNumber()
	{
		return columnNumber;
	}

	/**
	 * Set the document offset where this node occurs.
	 * @param offset the document offset
	 */
	public void setOffset(int offset)
	{
		this.offset = offset;
	}
	
	/**
	 * Get the document offset where this node occurs.
	 * @return the document offset
	 */
	public int getOffset()
	{
		return offset;
	}

	/**
	 * Set the length of this node.
	 * @param length the node length
	 */
	public void setLength(int length)
	{
		this.length = length;
	}
	
	/**
	 * Get the length of this node.
	 * @return the node length
	 */
	public int getLength()
	{
		return length;
	}
	
	/**
	 * Get the type of node.
	 * 
	 * @return one of <code>ELEMENT_NODE</code> or <code>ATTRIBUTE_NODE</code>
	 */
	public short getNodeType()
	{
		return type;
	}
	
	@Override
	public String toString() {
		return "file="+stylesheet+", line="+lineNumber+", col="+columnNumber;  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
	}
}
