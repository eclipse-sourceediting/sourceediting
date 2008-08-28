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
import org.eclipse.wst.xsl.jaxp.debug.debugger.Variable;

public class XalanVariable extends Variable implements Comparable
{
	private final Log log = LogFactory.getLog(XalanVariable.class);
	private final ElemVariable elemVariable;
	private final VariableStack varStack;
	private int stackFrame;

	public XalanVariable(XalanStyleFrame xalanStyleFrame, VariableStack varStack, String scope, int slotNumber, ElemVariable elemVariable)
	{
		super(getName(elemVariable,scope,xalanStyleFrame), scope, slotNumber+varStack.getStackFrame());
		this.elemVariable = elemVariable;
		this.varStack = varStack;
		// get the stack frame at this current point in time
		this.stackFrame = varStack.getStackFrame();
	}

	private static String getName(ElemVariable elemVariable, String scope, XalanStyleFrame xalanStyleFrame)
	{
		String name = elemVariable.getName().getLocalName();
		String systemId = elemVariable.getStylesheet().getSystemId();
		if (GLOBAL_SCOPE.equals(scope) && systemId != null)
		{
			int index;
			if ((index = systemId.lastIndexOf('/')) > 0)
				name += " (" + systemId.substring(index + 1) + ")";
			else
				name += " (" + systemId + ")";
		}
//		else if (LOCAL_SCOPE.equals(scope))
//		{
//			name += " (" + xalanStyleFrame.getName() + ")";
//		}
		return name;
	}
	
	public String getType()
	{
		String value = UNKNOWN;
		try
		{
			XObject xobject = getXObject();
			if (xobject != null)
			{
				int xalanType = xobject.getType();
				switch (xalanType)
				{
					case XObject.CLASS_UNRESOLVEDVARIABLE:
						value = UNRESOLVED;
						break;
					case XObject.CLASS_NODESET:
						value = NODESET;
						break;
					case XObject.CLASS_BOOLEAN:
						value = BOOLEAN;
						break;
					case XObject.CLASS_NUMBER:
						value = NUMBER;
						break;
					case XObject.CLASS_UNKNOWN:
						value = UNKNOWN;
						break;
					case XObject.CLASS_STRING:
					default:
						value = STRING;
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

	public String getValue()
	{
		String value = "???";
		try
		{
			XObject xobject = getXObject();
			if (xobject != null)
			{
				int xalanType = xobject.getType();
				switch (xalanType)
				{
					case XObject.CLASS_UNRESOLVEDVARIABLE:
						value = "";
						break;
					case XObject.CLASS_NODESET:
						XNodeSet xns = (XNodeSet) xobject;
						if (xns.nodelist().getLength() > 0)
							value = ((XNodeSet) xobject).nodelist().item(0).toString();
						else
							value = "<EMPTY NODESET>";
						break;
					case XObject.CLASS_BOOLEAN:
					case XObject.CLASS_NUMBER:
					case XObject.CLASS_STRING:
					case XObject.CLASS_UNKNOWN:
					default:
						value = xobject.toString();
						break;
				}
			}
		}
		catch (TransformerException e)
		{
			e.printStackTrace();
		}
//		value = getScope()+"."+getSlotNumber()+")"+getName();	
//		log.debug(getScope()+"."+getSlotNumber()+")"+getName() + "=" + value);
		return value;
	}

	private XObject getXObject() throws TransformerException
	{
		XObject xvalue;
		if (elemVariable.getIsTopLevel())
			xvalue = varStack.elementAt(slotNumber);
		else
			xvalue = varStack.getLocalVariable(elemVariable.getIndex(), stackFrame);
		return xvalue;
	}

	public int compareTo(Object arg0)
	{
		XalanVariable xvar = (XalanVariable)arg0;
		int comp = xvar.stackFrame - stackFrame;
		if (comp == 0)
			comp = slotNumber - xvar.slotNumber;
		return comp;
	}
}
