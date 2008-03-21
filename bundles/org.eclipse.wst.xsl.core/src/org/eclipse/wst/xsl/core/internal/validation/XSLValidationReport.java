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
		ValidationMessage msg = new ValidationMessage(message,node.getLineNumber()+1,node.getColumnNumber()+1,getFileURI());
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
		ValidationMessage msg = new ValidationMessage(message,node.getLineNumber()+1,node.getColumnNumber()+1,getFileURI());
		msg.setSeverity(ValidationMessage.SEV_NORMAL);
		addMessage(node,msg);
		return msg;
	}

	private void addMessage(XSLNode node, ValidationMessage message)
	{
		if (node.getNodeType() == XSLNode.ATTRIBUTE_NODE)
		{
//			message.setAttribute("ERROR_SIDE", "ERROR_SIDE_RIGHT");
//			message.setAttribute(COLUMN_NUMBER_ATTRIBUTE, new Integer(validationMessage.getColumnNumber()));
//			message.setAttribute(SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE, "START_TAG"); // whether to squiggle the element, attribute or text			
		}
		else if (node.getNodeType() == XSLNode.ELEMENT_NODE)
		{
			
		}
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
