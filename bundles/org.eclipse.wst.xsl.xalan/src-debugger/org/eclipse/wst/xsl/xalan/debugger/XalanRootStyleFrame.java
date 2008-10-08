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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xalan.templates.ElemVariable;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.trace.TracerEvent;
import org.apache.xpath.VariableStack;
import org.eclipse.wst.xsl.jaxp.debug.debugger.Variable;

public class XalanRootStyleFrame extends XalanStyleFrame
{
	private static final Log log = LogFactory.getLog(XalanRootStyleFrame.class);
	private Map variables;
	private ArrayList globals;

	public XalanRootStyleFrame(TracerEvent event)
	{
		super(null, event);
		fillGlobals(event);
	}

	protected List getGlobals()
	{
		return globals;
	}
	
	private void fillGlobals(TracerEvent event)
	{
		VariableStack vs = event.m_processor.getXPathContext().getVarStack();
	    StylesheetRoot sr = event.m_styleNode.getStylesheetRoot();
	    Vector vars = sr.getVariablesAndParamsComposed();
		variables = new HashMap(vars.size());
		globals = new ArrayList(vars.size());
	    int i = vars.size();
	    while (--i >= 0)
		{
			ElemVariable variable = (ElemVariable) vars.elementAt(i);
			XalanVariable xvar = new XalanVariable(this,vs,Variable.GLOBAL_SCOPE,i,variable);
			addVariable(xvar);
			globals.add(xvar);
		}
	}

	/**
	 * Gets a Variable by ID
	 * @since 1.0
	 */
	public Variable getVariable(int id)
	{
//		log.debug("Getting variable with id "+id+" from variables "+variables.size());
		return (Variable)variables.get(new Integer(id));
	}

	public void addVariable(XalanVariable xvar)
	{
//		log.debug("Adding variable index="+xvar.getSlotNumber()+" val="+xvar);
//		variables.add(xvar.getSlotNumber(),xvar);
		log.debug("Adding variable id="+xvar.getId());
		variables.put(new Integer(xvar.getId()),xvar);
	}
}
