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
package org.eclipse.wst.xsl.core.internal.validation;

import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.xsl.core.model.XSLNode;

/**
 * A validation message specific to the XSL validator which adds the ability 
 * to specify the XSL node.
 * 
 * @author Doug Satchwell
 * @see org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage
 */
public class XSLValidationMessage extends ValidationMessage
{
	private XSLNode node;
	private int realSeverity;

	/**
	 * Create a new instance of this.
	 * 
	 * @param message
	 *            The message for the validation message.
	 * @param lineNumber
	 *            The line location of the message.
	 * @param columnNumber
	 *            The column location of the message.
	 * @param uri
	 *            The uri of the file the message is for.
	 */
	public XSLValidationMessage(String message, int lineNumber, int columnNumber, String uri)
	{
		super(message, lineNumber, columnNumber, uri);
	}
	
	/**
	 * Set the node that this message applies to.
	 * 
	 * @param node the node
	 */
	public void setNode(XSLNode node)
	{
		this.node = node;
	}

	/**
	 * Get the node that this message applies to.
	 * @return the node
	 */
	public XSLNode getNode()
	{
		return node;
	}
	
	/**
	 * The severity set here should be the org.eclipse.wst.validation.internal.provisional.core.IMessage severity.
	 */
	@Override
	public void setSeverity(int sev)
	{
		this.realSeverity = sev;
		// the superclass only understands high and low.
		int severity;
		switch(sev)
		{
			case IMessage.HIGH_SEVERITY:
				severity = ValidationMessage.SEV_HIGH;
				break;
			default:
				severity = ValidationMessage.SEV_LOW;
		}
		super.setSeverity(severity);
	}
	
	/**
	 * Workaround for superclass's bizarre handling of severity
	 * 
	 * @return
	 */
	public int getRealSeverity()
	{
		return realSeverity;
	}
	
	@Override
	public String toString()
	{
		return node.toString();
	}
}
