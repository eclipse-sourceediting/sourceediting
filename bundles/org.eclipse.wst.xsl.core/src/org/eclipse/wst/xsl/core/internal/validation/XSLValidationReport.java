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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.eclipse.wst.xsl.core.internal.model.Stylesheet;
import org.eclipse.wst.xsl.core.internal.model.XSLNode;

/**
 * @author Doug Satchwell
 *
 */
public class XSLValidationReport implements ValidationReport
{
	private boolean valid = true;
	private Stylesheet stylesheet;
	private List<ValidationMessage> messages = new ArrayList<ValidationMessage>();

	/**
	 * TODO: Add Javadoc
	 * @param stylesheet
	 */
	public XSLValidationReport(Stylesheet stylesheet)
	{
		this.stylesheet = stylesheet;
	}
	
	/**
	 * TODO: Add Javadoc
	 * @param node
	 * @param message
	 * @return
	 */
	public ValidationMessage addError(XSLNode node, String message)
	{
		valid = false;
		XSLValidationMessage msg = new XSLValidationMessage(message,node.getLineNumber()+1,node.getColumnNumber()+1,getFileURI());
		msg.setSeverity(ValidationMessage.SEV_HIGH);
		addMessage(node,msg);
		return msg;
	}
	
	/**
	 * TODO: Add Javadoc
	 * @param node
	 * @param message
	 * @return
	 */
	public ValidationMessage addWarning(XSLNode node, String message)
	{
		XSLValidationMessage msg = new XSLValidationMessage(message,node.getLineNumber()+1,node.getColumnNumber()+1,getFileURI());
		msg.setSeverity(ValidationMessage.SEV_LOW);
		addMessage(node,msg);
		return msg;
	}

	private void addMessage(XSLNode node, XSLValidationMessage message)
	{
		message.setNode(node);
		messages.add(message);
	}

	/**
	 * TODO: Add Java Doc
	 */
	public String getFileURI()
	{
		return stylesheet.getFile().getLocationURI().toString();
	}

	/**
	 * TODO: Add Javadoc
	 */
	public HashMap getNestedMessages()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * TODO: Add Javadoc
	 */
	public ValidationMessage[] getValidationMessages()
	{
		return messages.toArray(new ValidationMessage[0]);
	}

	/**
	 * TODO: Add Javadoc
	 */
	public boolean isValid()
	{
		return valid;
	}
}
