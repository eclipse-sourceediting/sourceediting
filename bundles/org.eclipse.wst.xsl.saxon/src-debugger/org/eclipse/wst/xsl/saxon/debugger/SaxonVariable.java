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
package org.eclipse.wst.xsl.saxon.debugger;

import net.sf.saxon.om.ValueRepresentation;
import net.sf.saxon.trans.XPathException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.wst.xsl.debugger.Variable;

public class SaxonVariable extends Variable
{
	private static final Log log = LogFactory.getLog(SaxonVariable.class);
	private final SaxonStyleFrame saxonStyleFrame;

	public SaxonVariable(SaxonStyleFrame saxonStyleFrame, String name, String scope, int slotNumber)
	{
		super(name,scope,slotNumber);
		this.saxonStyleFrame = saxonStyleFrame;
	}
	
	public int getType()
	{
		return Variable.STRING;
	}

	public String getValue()
	{
		log.debug("Getting variable '"+name+"' with slot number = "+slotNumber+" from scope "+scope);
		ValueRepresentation valueRepresentation = null;
		if (LOCAL_SCOPE.equals(scope))
			valueRepresentation = saxonStyleFrame.context.evaluateLocalVariable(slotNumber);
//		else if (TUNNEL_SCOPE.equals(scope))
//			valueRepresentation = saxonStyleFrame.context.
		else if (GLOBAL_SCOPE.equals(scope))
			valueRepresentation = saxonStyleFrame.context.getController().getBindery().getGlobalVariable(slotNumber);
		String value;
		try
		{
			value = valueRepresentation == null ? "null" : valueRepresentation.getStringValue();
		}
		catch (XPathException e)
		{
			value = "???";
		}
		return value;
	}
}
