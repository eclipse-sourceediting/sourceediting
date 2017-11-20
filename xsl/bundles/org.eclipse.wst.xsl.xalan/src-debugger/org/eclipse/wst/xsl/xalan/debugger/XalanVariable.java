/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver (STAR) - bug 214235 - Allows nodes to be expanded.
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
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;

public class XalanVariable extends Variable implements Comparable {
	private final Log log = LogFactory.getLog(XalanVariable.class);
	private final ElemVariable elemVariable;
	private final VariableStack varStack;
	private int stackFrame;
	private XObject xobject;

	public XalanVariable(XalanStyleFrame xalanStyleFrame,
			VariableStack varStack, String scope, int slotNumber,
			ElemVariable elemVariable) {
		super(getName(elemVariable, scope, xalanStyleFrame), scope, slotNumber
				+ varStack.getStackFrame());
		this.elemVariable = elemVariable;
		this.varStack = varStack;
		// get the stack frame at this current point in time
		this.stackFrame = varStack.getStackFrame();
		try {
			xobject = getXObject();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static String getName(ElemVariable elemVariable, String scope,
			XalanStyleFrame xalanStyleFrame) {
		String name = elemVariable.getName().getLocalName();
		String systemId = elemVariable.getStylesheet().getSystemId();
		if (GLOBAL_SCOPE.equals(scope) && systemId != null) {
			int index;
			if ((index = systemId.lastIndexOf('/')) > 0)
				name += " (" + systemId.substring(index + 1) + ")";
			else
				name += " (" + systemId + ")";
		}
		// else if (LOCAL_SCOPE.equals(scope))
		// {
		// name += " (" + xalanStyleFrame.getName() + ")";
		// }
		return name;
	}

	public String getType() {
		String value = UNKNOWN;
		// XObject xobject = getXObject();
		if (xobject != null) {
			int xalanType = xobject.getType();
			switch (xalanType) {
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
		// catch (TransformerException e)
		// {
		// e.printStackTrace();
		// }
		return value;
	}

	public String getValue() {
		String value = "???";
		try {
			xobject = getXObject();
			if (xobject != null) {
				int xalanType = xobject.getType();
				switch (xalanType) {
				case XObject.CLASS_UNRESOLVEDVARIABLE:
					value = "";
					break;
				case XObject.CLASS_NODESET:
					XNodeSet xns = (XNodeSet) xobject;
					if (xns.nodelist().getLength() > 0) {
					    value = convertNode(xns);
					}
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
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		// value = getScope()+"."+getSlotNumber()+")"+getName();
		// log.debug(getScope()+"."+getSlotNumber()+")"+getName() + "=" +
		// value);
		return value;
	}

	private String convertNode(XNodeSet xns) throws TransformerException {
		NodeList nodeList = xns.nodelist();
		String value = processNodeList(nodeList);
		return value;
	}

	private String processNodeList(NodeList nodeList) {
		String value = "";
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node != null) {
				int nodeType = node.getNodeType();
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					value = createElement(value, node);
				}
				if (nodeType == Node.COMMENT_NODE ) {
					value = value + "<!-- " + node.getNodeValue() + " -->";
				}
				if (nodeType == Node.PROCESSING_INSTRUCTION_NODE) {
					ProcessingInstruction pi = (ProcessingInstruction) node;
					value = value + "<?" + pi.getData() + " ?>";
				}
			}
		}
		return value;
	}

	private String createElement(String value, Node node) {
		value = value + "<";
//		if (node.getPrefix() != null && node.getPrefix().length() > 0) {
//			value = value + node.getPrefix() + ":";
//		}
		if (node.getNodeName() != null) {
			value = value + node.getNodeName();
			if (node.hasAttributes()) {
				NamedNodeMap attr = node.getAttributes();
				value = value + buildAttributes(attr);
			}
			value = value + ">";
			if (node.getNodeValue() != null) {
				value = value + node.getNodeValue();
			}
		}
		if (node.hasChildNodes()) {
			value = value + processNodeList(node.getChildNodes());
		}
		value = value + "</" + node.getNodeName() + ">";
		return value;
	}
	
	private String buildAttributes(NamedNodeMap attributes) {
		String value = " ";
		for (int a = 0; a < attributes.getLength(); a++) {
			Attr attribute = (Attr)attributes.item(a);
//			if (attribute.getPrefix() != null) {
//				value = value + attribute.getPrefix() + ":";
//			}
			value = value + attribute.getName() + "=\"" + attribute.getValue() + "\" ";
		}
		value = value + " ";
		return value;
	}

	private XObject getXObject() throws TransformerException {
		XObject xvalue;
		if (elemVariable.getIsTopLevel())
			xvalue = varStack.elementAt(slotNumber);
		else
			xvalue = varStack.getLocalVariable(elemVariable.getIndex(),
					stackFrame);
		return xvalue;
	}

	public int compareTo(Object arg0) {
		XalanVariable xvar = (XalanVariable) arg0;
		int comp = xvar.stackFrame - stackFrame;
		if (comp == 0)
			comp = slotNumber - xvar.slotNumber;
		return comp;
	}
}
