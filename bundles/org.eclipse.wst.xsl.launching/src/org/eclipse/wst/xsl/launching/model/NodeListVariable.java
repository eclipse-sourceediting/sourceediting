/*******************************************************************************
 *Copyright (c) 2008 STAR and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *Contributors:
 *    David Carver (STAR) - bug 214235 - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.launching.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.w3c.dom.Node;

public class NodeListVariable extends XSLDebugElement implements IVariable {
	
	private Node node = null;
	private IDebugTarget debugTarget;
	
	public NodeListVariable(IDebugTarget target, Node nodeListNode) {
		super(target);
		node = nodeListNode;
		debugTarget = target;
	}

	public String getName() throws DebugException {
		// TODO Auto-generated method stub
		String nodeName = "";
		if (node.getPrefix() != null) {
			nodeName = nodeName + node.getPrefix() + ":";
		}
		if (node.getNodeName() != null) {
			nodeName = nodeName + node.getNodeName();
		}
		return nodeName;
	}

	public String getReferenceTypeName() throws DebugException {
		// TODO Auto-generated method stub
		return null;
	}

	public IValue getValue() throws DebugException {
		String nodeValue = "";
		if (node.getNodeValue() != null) {
		   nodeValue = node.getNodeValue();
		}
		//IValue value = new XSLValue(debugTarget, "string", nodeValue);
		IValue value = new XSLValue(debugTarget, "string", node);
		return value;
	}

	public boolean hasValueChanged() throws DebugException {
		return false;
	}

	public boolean supportsValueModification() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean verifyValue(String expression) throws DebugException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean verifyValue(IValue value) throws DebugException {
		// TODO Auto-generated method stub
		return false;
	}

	public void setValue(String expression) throws DebugException {
		// TODO Auto-generated method stub
		
	}

	public void setValue(IValue value) throws DebugException {
		// TODO Auto-generated method stub
		
	}

}
