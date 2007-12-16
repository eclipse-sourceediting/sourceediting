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
package org.eclipse.wst.xsl.xalan.debugger;

import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xalan.templates.ElemVariable;
import org.apache.xpath.VariableStack;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.eclipse.wst.xsl.debugger.Variable;

public class XalanVariable extends Variable
{
	private final Log log = LogFactory.getLog(XalanVariable.class);
	private final ElemVariable elemVariable;
	private final VariableStack varStack;

	public XalanVariable(VariableStack varStack, String scope, int slotNumber, ElemVariable elemVariable)
	{
		super(getName(elemVariable), scope, slotNumber);
		this.elemVariable = elemVariable;
		this.varStack = varStack;
	}

	private static String getName(ElemVariable elemVariable)
	{
		String name = elemVariable.getName().getLocalName();
		String systemId = elemVariable.getStylesheet().getSystemId();
		if (systemId != null)
		{
			int index;
			if ((index = systemId.lastIndexOf('/')) > 0)
				name += " (" + systemId.substring(index + 1) + ")";
			else
				name += " (" + systemId + ")";
		}

		return name;
	}

	public String getValue()
	{
		log.debug("Getting value for " + getName() + " in scope " + getScope() + " with index " + getSlotNumber());
		String value = "???";
		try
		{
			XObject xobject = getXObject();
			if (xobject != null)
			{
				int xalanType = xobject.getType();
				switch (xalanType)
				{
					case XObject.CLASS_BOOLEAN:
					case XObject.CLASS_NUMBER:
					case XObject.CLASS_STRING:
					case XObject.CLASS_UNKNOWN:
						value = xobject.toString();
						break;
					case XObject.CLASS_NODESET:
						value = ((XNodeSet) xobject).xstr().toString();
						break;
					case XObject.CLASS_UNRESOLVEDVARIABLE:
					default:
						value = "";
						break;
				}
			}
		}
		catch (TransformerException e)
		{
			e.printStackTrace();
		}
		return value;
	}

	private XObject getXObject() throws TransformerException
	{
		XObject xvalue = null;
		int index = elemVariable.getIndex();
		int frame = varStack.getStackFrame();
		if (elemVariable.getIsTopLevel())
			xvalue = varStack.elementAt(index);
		else
			xvalue = varStack.getLocalVariable(index, frame);
		return xvalue;
	}
}
