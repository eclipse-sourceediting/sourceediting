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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.instruct.SlotManager;
import net.sf.saxon.om.NamePool;
import net.sf.saxon.trace.InstructionInfo;

import org.eclipse.wst.xsl.debugger.StyleFrame;
import org.eclipse.wst.xsl.debugger.Variable;

public class SaxonStyleFrame extends StyleFrame
{
	final XPathContext context;
	private InstructionInfo info;
	private String tag;

	public SaxonStyleFrame(StyleFrame parent, InstructionInfo info, XPathContext context, String tag)
	{
		super(parent);
		this.info = info;
		this.context = context;
		this.tag = tag;
	}

	public List getVariableStack()
	{
		List variables = new ArrayList();
		// local variables
		fillVariables(context.getStackFrame().getStackFrameMap(),context.getNamePool(),variables,Variable.LOCAL_SCOPE);
		// global variables
		fillVariables(context.getController().getBindery().getGlobalVariableMap(),context.getController().getNamePool(),variables,Variable.GLOBAL_SCOPE);
		return variables;
	}
	
	public Variable getVariable(String scope, int slotNumber)
	{
		List vars = getVariableStack(scope);
		return (Variable)vars.get(slotNumber);
	}
	
	private List getVariableStack(String scope)
	{
		List variables = new ArrayList();
		if (Variable.LOCAL_SCOPE.equals(scope))
			fillVariables(context.getStackFrame().getStackFrameMap(),context.getNamePool(),variables,Variable.LOCAL_SCOPE);
		else if (Variable.GLOBAL_SCOPE.equals(scope))
			fillVariables(context.getController().getBindery().getGlobalVariableMap(),context.getController().getNamePool(),variables,Variable.GLOBAL_SCOPE);
		return variables;
	}
	
	private void fillVariables(SlotManager slotManager, NamePool namePool, List toFill, String scope)
	{
		if (slotManager != null)
		{
			List fingerprints = slotManager.getVariableMap();
			int i = 0;
			for (Iterator iterator = fingerprints.iterator(); iterator.hasNext();)
			{
				Integer fingerprint = (Integer) iterator.next();
				String name = context.getNamePool().getClarkName(fingerprint.intValue());
				toFill.add(new SaxonVariable(this,name,scope,i++));
			}
		}
	}

	public int getColumn()
	{
		return info.getColumnNumber();
	}

	public String getFilename()
	{
		return info.getSystemId();
	}

	public int getLine()
	{
		return info.getLineNumber();
	}
	
	public int getEndLine()
	{
		return info.getLineNumber();
	}
	
	public String getName()
	{
		String name = tag;
		String nameAtt = (String) info.getProperty("name");
		if (nameAtt != null)
			name += " name=\"" + nameAtt + "\"";
		String match = (String) info.getProperty("match");
		if (match != null)
			name += " match=\"" + match + "\"";
		return name;
	}

	public Object getTemplate()
	{
		// TODO get template
		return null;
	}
}
