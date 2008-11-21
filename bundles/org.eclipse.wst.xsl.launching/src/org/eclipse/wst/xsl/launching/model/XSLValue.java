/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver (STAR) - bug 214235 - Node List expansion
 *******************************************************************************/
package org.eclipse.wst.xsl.launching.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XSLValue extends XSLDebugElement implements IValue
{
	private String fValue;
	private String type;
	private boolean hasVariables;
	private Node actualNode;

	public XSLValue(IDebugTarget target, String type, Node node) {
		super(target);
		actualNode = node;
		String value = "";
		if (node.getNodeValue() != null) {
			value = node.getNodeValue();
		}
		init(target, type, value);
	}
	public XSLValue(IDebugTarget target, String type, String value)
	{
		super(target);
		init(target, type, value);
	}
	
	private void init(IDebugTarget target, String type, String value) {
		this.type = type;
		fValue = value;
	}
	


	public String getReferenceTypeName() throws DebugException
	{
		return type;
	}

	public String getValueString() throws DebugException
	{
		if ("string".equals(type)) //$NON-NLS-1$
			return "'"+fValue+"'"; //$NON-NLS-1$ //$NON-NLS-2$
		return fValue;
	}

	public boolean isAllocated() throws DebugException
	{
		return true;
	}

	public IVariable[] getVariables() throws DebugException
	{
		if (actualNode != null) {
			return getNodeListVariables(actualNode.getChildNodes());
		}
		if (type.equals("nodeset") && !(fValue.equals("<EMPTY NODESET>"))) {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			NodeList nodeList = null;
			try {
				DocumentBuilder builder = builderFactory.newDocumentBuilder();
				InputStream is = new ByteArrayInputStream(fValue.getBytes());
				Document doc = builder.parse(is);
				nodeList = doc.getChildNodes();
				return getNodeListVariables(nodeList);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new IVariable[0];
	}
	private IVariable[] getNodeListVariables(NodeList nodeList) {
		List<IVariable> variableList = new ArrayList<IVariable>();
		IVariable[] returnVars = new IVariable[nodeList.getLength()];
		if (nodeList != null) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				IVariable variable = new NodeListVariable(getDebugTarget(), node);
				variableList.add(variable);
			}
		}
		return 	variableList.toArray(returnVars);
	}

	public boolean hasVariables() throws DebugException
	{
		hasVariables = false;
		if (actualNode != null) {
			hasVariables = actualNode.hasChildNodes();
		} else 	if (type.equals("nodeset")) {
			hasVariables = true;
		} else {
			hasVariables = false;
		}
		if (fValue.equals("<EMPTY NODESET>")) {
			hasVariables = false;
		}
		
		return hasVariables; 
	}
}
