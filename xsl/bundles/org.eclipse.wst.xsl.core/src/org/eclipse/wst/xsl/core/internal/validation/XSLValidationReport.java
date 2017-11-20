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

import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.eclipse.wst.xsl.core.model.XSLNode;


/**
 * A validation report for the XSL validator.
 * 
 * @author Doug Satchwell
 * @see org.eclipse.wst.xml.core.internal.validation.core.ValidationReport
 */
public class XSLValidationReport implements ValidationReport
{
	private boolean valid = true;
	private String uri;
	private List<XSLValidationMessage> errors = new ArrayList<XSLValidationMessage>();
	private List<XSLValidationMessage> warnings = new ArrayList<XSLValidationMessage>();
	private List<XSLValidationMessage> infos = new ArrayList<XSLValidationMessage>();

	/**
	 * Create a new instance of this.
	 * 
	 * @param uri
	 *            the URI of the entity that this report applies to
	 */
	public XSLValidationReport(String uri)
	{
		this.uri = uri;
	}

	/**
	 * Get the validation messages.
	 * 
	 * @return validation messages
	 */
	public List<XSLValidationMessage> getErrors()
	{
		return errors;
	}

	/**
	 * Get the validation messages.
	 * 
	 * @return validation messages
	 */
	public List<XSLValidationMessage> getWarnings()
	{
		return warnings;
	}

	/**
	 * Get the validation messages.
	 * 
	 * @return validation messages
	 */
	public List<XSLValidationMessage> getInfos()
	{
		return infos;
	}

	/**
	 * Add an error message for the given XSL node.
	 * 
	 * @param node
	 *            the node the warning applies to
	 * @param message
	 *            the message to associate with the node
	 * @return the validation message created
	 */
	public ValidationMessage addError(XSLNode node, String message)
	{
		valid = false;
		XSLValidationMessage msg = new XSLValidationMessage(message, node.getLineNumber() + 1, node.getColumnNumber() + 1, getFileURI());
		msg.setSeverity(IMessage.HIGH_SEVERITY);
		msg.setNode(node);
		errors.add(msg);
		return msg;
	}

	/**
	 * Add an warning message for the given XSL node.
	 * 
	 * @param node
	 *            the node the warning applies to
	 * @param message
	 *            the message to associate with the node
	 * @return the validation message created
	 */
	public ValidationMessage addWarning(XSLNode node, String message)
	{
		XSLValidationMessage msg = new XSLValidationMessage(message, node.getLineNumber() + 1, node.getColumnNumber() + 1, getFileURI());
		msg.setSeverity(IMessage.NORMAL_SEVERITY);
		msg.setNode(node);
		warnings.add(msg);
		return msg;
	}

	/**
	 * Add an info message for the given XSL node.
	 * 
	 * @param node
	 *            the node the warning applies to
	 * @param message
	 *            the message to associate with the node
	 * @return the validation message created
	 */
	public ValidationMessage addInfo(XSLNode node, String message)
	{
		XSLValidationMessage msg = new XSLValidationMessage(message, node.getLineNumber() + 1, node.getColumnNumber() + 1, getFileURI());
		msg.setSeverity(IMessage.LOW_SEVERITY);
		msg.setNode(node);
		infos.add(msg);
		return msg;
	}

	/**
	 * Get the URI that this report applies to.
	 * 
	 * @return the URI
	 */
	public String getFileURI()
	{
		return uri;
	}

	/**
	 * @see org.eclipse.wst.xml.core.internal.validation.core.getNestedMessages
	 * 
	 * @return null
	 */
	public HashMap<?, ?> getNestedMessages()
	{
		return null;
	}

	/**
	 * Returns an array of validation messages.
	 * 
	 * @return An array of validation messages.
	 */
	public ValidationMessage[] getValidationMessages()
	{
		List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
		messages.addAll(errors);
		messages.addAll(warnings);
		messages.addAll(infos);
		return messages.toArray(new ValidationMessage[0]);
	}

	/**
	 * Returns whether the file is valid. The file may have warnings associated with it.
	 * 
	 * @return True if the file is valid, false otherwise.
	 */
	public boolean isValid()
	{
		return valid;
	}
}
