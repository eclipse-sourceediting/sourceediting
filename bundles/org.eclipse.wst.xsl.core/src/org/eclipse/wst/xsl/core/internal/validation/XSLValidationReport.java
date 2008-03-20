package org.eclipse.wst.xsl.core.internal.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.eclipse.wst.xsl.core.internal.model.Stylesheet;
import org.eclipse.wst.xsl.core.internal.model.XSLNode;

public class XSLValidationReport implements ValidationReport
{
	private boolean valid = true;
	private Stylesheet stylesheet;
	private List<ValidationMessage> messages = new ArrayList<ValidationMessage>();

	public XSLValidationReport(Stylesheet stylesheet)
	{
		this.stylesheet = stylesheet;
	}
	
	public ValidationMessage addError(XSLNode node, String message)
	{
		valid = false;
		ValidationMessage msg = new ValidationMessage(message,node.getLineNumber()+1,node.getColumnNumber()+1,getFileURI());
		msg.setSeverity(ValidationMessage.SEV_HIGH);
		addMessage(node,msg);
		return msg;
	}
	
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

	@Override
	public String getFileURI()
	{
		return stylesheet.getFile().getLocationURI().toString();
	}

	@Override
	public HashMap getNestedMessages()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValidationMessage[] getValidationMessages()
	{
		return messages.toArray(new ValidationMessage[0]);
	}

	@Override
	public boolean isValid()
	{
		return valid;
	}
}
