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
package org.eclipse.wst.xsl.core.internal.validation.xalan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xml.utils.SAXSourceLocator;
import org.apache.xml.utils.WrappedRuntimeException;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.eclipse.wst.xsl.core.internal.model.Stylesheet;
import org.eclipse.wst.xsl.core.internal.model.XSLNode;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Doug Satchwell
 *
 */
public class XSLValidationReport implements ValidationReport, ErrorListener
{
	private List<ValidationMessage> messages = new ArrayList<ValidationMessage>();

    private boolean isValid = false;	
	
	
	/**
	 * Construct a new validation reporter.
	 */
	public XSLValidationReport() {
		
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
		return isValid;
	}

	public void error(TransformerException exception)
			throws TransformerException {
		addMessage(exception, ValidationMessage.SEV_NORMAL);		
	}

	private void addMessage(TransformerException exception, int severity) {
		isValid = false;
		org.apache.xml.utils.DefaultErrorHandler.ensureLocationSet(exception);
        SourceLocator location = getRootSourceLocator(exception);
		int lineNumber = location.getLineNumber();
		int columnNumber = location.getColumnNumber();
		String message = exception.getMessage();
		
		ValidationMessage msg = new ValidationMessage(message, lineNumber, columnNumber);		
		msg.setSeverity(severity);
		exception.printStackTrace();
		messages.add(msg);
	}

	public void fatalError(TransformerException exception)
			throws TransformerException {
		exception.printStackTrace();
		addMessage(exception, ValidationMessage.SEV_HIGH);				
	}

	public void warning(TransformerException exception)
			throws TransformerException {
		addMessage(exception, ValidationMessage.SEV_LOW);		
	}

	public String getFileURI() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static SourceLocator getRootSourceLocator(Throwable exception)
	{
	  SourceLocator locator = null;
	  Throwable cause = exception;
	    
	  // Try to find the locator closest to the cause.
	  do
	  {
	    if(cause instanceof SAXParseException)
	    {
	      locator = new SAXSourceLocator((SAXParseException)cause);
	    }
	    else if (cause instanceof TransformerException)
	    {
	      SourceLocator causeLocator = 
	                    ((TransformerException)cause).getLocator();
	      if(null != causeLocator)
	        locator = causeLocator;
	    }
	    if(cause instanceof TransformerException)
	      cause = ((TransformerException)cause).getCause();
	    else if(cause instanceof WrappedRuntimeException)
	      cause = ((WrappedRuntimeException)cause).getException();
	    else if(cause instanceof SAXException)
	      cause = ((SAXException)cause).getException();
	    else
	      cause = null;
	  }
	  while(null != cause);
	        
	  return locator;
	}	
}
