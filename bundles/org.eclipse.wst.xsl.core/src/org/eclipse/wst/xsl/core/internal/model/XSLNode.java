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

public abstract class XSLNode extends PlatformObject
{
	public static final short ELEMENT_NODE = Node.ELEMENT_NODE;
	public static final short ATTRIBUTE_NODE = Node.ATTRIBUTE_NODE;
	
	final Stylesheet stylesheet;
	int lineNumber;
	int columnNumber;
	private int offset;
	private int length;
	private short type;
	
	public XSLNode(Stylesheet stylesheet, short type)
	{
		this.stylesheet = stylesheet;
		this.type = type;
	}

	public void setLineNumber(int lineNumber)
	{
		this.lineNumber = lineNumber;
	}

	public void setColumnNumber(int columnNumber)
	{
		this.columnNumber = columnNumber;
	}

	public Stylesheet getStylesheet()
	{
		return stylesheet;
	}

	public int getLineNumber()
	{
		return lineNumber;
	}

	public int getColumnNumber()
	{
		return columnNumber;
	}

	public void setOffset(int offset)
	{
		this.offset = offset;
	}
	
	public int getOffset()
	{
		return offset;
	}

	public void setLength(int length)
	{
		this.length = length;
	}
	
	public int getLength()
	{
		return length;
	}
	
	public short getNodeType()
	{
		return type;
	}
	
	@Override
	public String toString() {
		return "file="+stylesheet+", line="+lineNumber+", col="+columnNumber;
	}
}
