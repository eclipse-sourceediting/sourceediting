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
package org.eclipse.wst.xsl.core.internal.model;

import org.eclipse.core.runtime.PlatformObject;
import org.w3c.dom.Node;

/**
 * @author Doug Satchwell
 *
 */
public abstract class XSLNode extends PlatformObject
{
	/**
	 * TODO: Add Javadoc
	 */
	public static final short ELEMENT_NODE = Node.ELEMENT_NODE;
	
	/**
	 * TODO: Add Javadoc
	 */
	public static final short ATTRIBUTE_NODE = Node.ATTRIBUTE_NODE;
	
	
	final Stylesheet stylesheet;
	int lineNumber;
	int columnNumber;
	private int offset;
	private int length;
	private short type;
	
	/**
	 * TODO: Javadoc
	 * @param stylesheet
	 * @param type
	 */
	public XSLNode(Stylesheet stylesheet, short type)
	{
		this.stylesheet = stylesheet;
		this.type = type;
	}

	/**
	 * TODO: Add Javadoc
	 * @param lineNumber
	 */
	public void setLineNumber(int lineNumber)
	{
		this.lineNumber = lineNumber;
	}

	/**
	 * TODO: Add Javadoc
	 * @param columnNumber
	 */
	public void setColumnNumber(int columnNumber)
	{
		this.columnNumber = columnNumber;
	}

	/**
	 * TODO: Add Javadoc
	 * @return
	 */
	public Stylesheet getStylesheet()
	{
		return stylesheet;
	}

	/**
	 * TODO: Add Javadoc
	 * @return
	 */
	public int getLineNumber()
	{
		return lineNumber;
	}

	/**
	 * TODO: Add Javadoc
	 * @return
	 */
	public int getColumnNumber()
	{
		return columnNumber;
	}

	/**
	 * TODO: Add Javadoc
	 * @param offset
	 */
	public void setOffset(int offset)
	{
		this.offset = offset;
	}
	
	/**
	 * TODO: Add Javadoc
	 * @return
	 */
	public int getOffset()
	{
		return offset;
	}

	/**
	 * TODO: Add Javadoc
	 * @param length
	 */
	public void setLength(int length)
	{
		this.length = length;
	}
	
	/**
	 * TODO: Add Javadoc
	 * @return
	 */
	public int getLength()
	{
		return length;
	}
	
	/**
	 * TODO: Add Javadoc
	 * @return
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
