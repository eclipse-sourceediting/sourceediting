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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.transform.SourceLocator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xalan.templates.ElemCallTemplate;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.ElemVariable;
import org.apache.xalan.trace.TracerEvent;
import org.apache.xml.dtm.ref.DTMNodeProxy;
import org.apache.xml.utils.QName;
import org.apache.xpath.VariableStack;
import org.apache.xpath.XPath;
import org.eclipse.wst.xsl.jaxp.debug.debugger.StyleFrame;
import org.eclipse.wst.xsl.jaxp.debug.debugger.Variable;
import org.w3c.dom.Node;

public class XalanStyleFrame extends StyleFrame
{
	private static final Log log = LogFactory.getLog(XalanStyleFrame.class);

	private final Map varNames;
	private final Stack eventStack = new Stack();
	final TracerEvent event;
	private int currentLine;
	private XalanRootStyleFrame rootStyleFrame;

	/**
	 * @since 1.0
	 */
	public XalanStyleFrame(StyleFrame parent, TracerEvent event)
	{
		super(parent);
		this.event = event;
		if (parent != null)
			this.varNames = new HashMap(((XalanStyleFrame) parent).varNames);
		else
			this.varNames = new HashMap();
		pushElement(event);
		findRootStyleFrame();
	}

	private void findRootStyleFrame()
	{
		XalanStyleFrame frame = this;
		do
		{
			if (frame instanceof XalanRootStyleFrame)
				rootStyleFrame = (XalanRootStyleFrame) frame;
		}
		while ((frame = (XalanStyleFrame) frame.getParent()) != null);
	}

	public String getFilename()
	{
		return event.m_styleNode.getStylesheet().getSystemId();
	}

	public String getName()
	{
		String name = event.m_styleNode.getNodeName();
		if (event.m_styleNode instanceof ElemTemplate)
		{
			ElemTemplate et = (ElemTemplate) event.m_styleNode;
			QName q = et.getName();
			if (q != null)
			{
				name += " name=\"" + q.getLocalName() + "\"";
			}
			XPath xp = et.getMatch();
			if (xp != null)
			{
				name += " match=\"" + xp.getPatternString() + "\"";
			}
		}
		else if (event.m_styleNode instanceof ElemCallTemplate)
		{
			ElemCallTemplate et = (ElemCallTemplate) event.m_styleNode;
			QName q = et.getName();
			if (q != null)
			{
				name += " name=\"" + q.getLocalName() + "\"";
			}
		}
		return name;
	}

	public List getVariableStack()
	{
		List vars = new ArrayList();
		vars.addAll(getLocals());
		vars.addAll(getGlobals());
		return vars;
	}

	public int getCurrentLine()
	{
		return currentLine;
	}

	public void pushElement(TracerEvent e)
	{
		currentLine = e.m_styleNode.getLineNumber();
		eventStack.push(e);
		if (log.isDebugEnabled())
			log.debug("Pushed element " + TracerEvent.printNode(e.m_styleNode) + " at line " + currentLine);
	}

	public TracerEvent popElement()
	{
		TracerEvent e = (TracerEvent) eventStack.pop();
		currentLine = e.m_styleNode.getEndLineNumber();
		if (log.isDebugEnabled())
			log.debug("Popped element " + TracerEvent.printNode(e.m_styleNode) + " at line " + currentLine);

		ElemTemplateElement element = e.m_styleNode;
		String name = element.getNodeName();
		log.debug(" name " + name);
		if (name.equals("param") || name.equals("variable"))
			addVariable((ElemVariable) e.m_styleNode);

		return e;
	}

	public TracerEvent peekElement()
	{
		if (eventStack.isEmpty())
			return null;
		return (TracerEvent) eventStack.peek();
	}

	private void addVariable(ElemVariable variable)
	{
		String scope = variable.getIsTopLevel() ? Variable.GLOBAL_SCOPE : Variable.LOCAL_SCOPE;
		VariableStack vs = event.m_processor.getXPathContext().getVarStack();
		XalanVariable xvar = new XalanVariable(this, vs, scope, variable.getIndex(), variable);
		rootStyleFrame.addVariable(xvar);
		varNames.put(variable.getName(), xvar);
	}

	private List getLocals()
	{
		List locals = new ArrayList(varNames.values());
		// sort by slotNumber
		Collections.sort(locals);
		return new ArrayList(varNames.values());
	}

	protected List getGlobals()
	{
		return rootStyleFrame.getGlobals();
	}

	public String getSourceFilename()
	{
		SourceLocator locator = getSourceLocator();
		if (locator != null)
			return locator.getSystemId();
		return "";
	}

	public int getSourceCurrentLine()
	{
		SourceLocator locator = getSourceLocator();
		if (locator != null)
			return locator.getLineNumber();
		return 0;
	}
	
	private SourceLocator getSourceLocator()
	{
		Node sourceNode = event.m_sourceNode;
        SourceLocator locator = null;
		if (sourceNode instanceof DTMNodeProxy)
		{
			int nodeHandler = ((DTMNodeProxy) sourceNode).getDTMNodeNumber();
			return ((DTMNodeProxy) sourceNode).getDTM().getSourceLocatorFor(nodeHandler);
		}
		return null;
	}
}
