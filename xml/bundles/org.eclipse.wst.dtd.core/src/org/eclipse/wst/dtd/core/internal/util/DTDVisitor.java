/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.dtd.core.internal.util;

import java.util.List;

import org.eclipse.wst.dtd.core.internal.Attribute;
import org.eclipse.wst.dtd.core.internal.AttributeList;
import org.eclipse.wst.dtd.core.internal.CMBasicNode;
import org.eclipse.wst.dtd.core.internal.CMGroupNode;
import org.eclipse.wst.dtd.core.internal.CMNode;
import org.eclipse.wst.dtd.core.internal.DTDFile;
import org.eclipse.wst.dtd.core.internal.DTDNode;
import org.eclipse.wst.dtd.core.internal.Element;
import org.eclipse.wst.dtd.core.internal.ParameterEntityReference;


public class DTDVisitor {

	public DTDVisitor() {

	}

	// utility method
	public boolean isParameterEntityRef(String reference) {
		if (reference.length() > 0) {
			return reference.charAt(0) == '%' && reference.charAt(reference.length() - 1) == ';';
		}
		return false;
	}

	public void visit(DTDFile file) {
		List nodes = file.getNodes();
		for (int i = 0; i < nodes.size(); i++) {
			DTDNode currentNode = (DTDNode) nodes.get(i);
			if (currentNode instanceof Element) {
				visitElement((Element) currentNode);
			}
			else if (currentNode instanceof AttributeList) {
				visitAttributeList((AttributeList) currentNode);
			}
			else if (currentNode instanceof ParameterEntityReference) {
				visitExternalParameterEntityReference((ParameterEntityReference) currentNode);
			}
		}
	}

	public void visitAttribute(Attribute attr) {
	}

	public void visitAttributeList(AttributeList attList) {
		// note that we don't visit attributes here because we
		// want the element to visit them with it's consolidated list
		// that it creates by gathering all attribute lists together
	}

	public void visitAttributes(List attributes) {
		int size = attributes.size();
		for (int i = 0; i < size; i++) {
			Attribute attr = (Attribute) attributes.get(i);
			visitAttribute(attr);
		}
	}

	public void visitContentNode(CMNode content) {
		if (content instanceof CMBasicNode) {
			CMBasicNode basicNode = (CMBasicNode) content;
			if (basicNode.isReference()) {
				visitReference(basicNode);
			}
		}
		else if (content instanceof CMGroupNode) {
			visitGroupNode((CMGroupNode) content);
		}
	}

	public void visitElement(Element element) {
		CMNode content = element.getContentModel();
		visitContentNode(content);
		visitAttributes(element.getElementAttributes());
	}

	public void visitExternalParameterEntityReference(ParameterEntityReference parmEntityRef) {
	}

	public void visitGroupNode(CMGroupNode group) {
		List children = group.getChildrenList();
		int size = children.size();
		for (int i = 0; i < size; i++) {
			visitContentNode((CMNode) children.get(i));
		}
	}

	public void visitReference(CMBasicNode node) {
	}


}
