/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.ui.ui.actions;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.wst.contentmodel.util.DOMVisitor;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class ReplacePrefixAction extends NodeAction {
	protected AbstractNodeActionManager manager;
	protected Element element;
	protected Map prefixMapping;

	protected static ImageDescriptor imageDescriptor;

	public ReplacePrefixAction(AbstractNodeActionManager manager, Element element, Map prefixMapping) {
		this.manager = manager;
		this.element = element;
		this.prefixMapping = prefixMapping;
	}

	public String getUndoDescription() {
		return ""; //$NON-NLS-1$
	}

	public void run() {
		NodeCollectingDOMVisitor visitor = new NodeCollectingDOMVisitor();
		visitor.visitNode(element);
		for (Iterator i = visitor.list.iterator(); i.hasNext();) {
			Node node = (Node) i.next();
			String key = node.getPrefix() != null ? node.getPrefix() : ""; //$NON-NLS-1$
			String newPrefix = (String) prefixMapping.get(key);
			if (newPrefix != null) {
				node.setPrefix(newPrefix);
			}
		}
	}

	class NodeCollectingDOMVisitor extends DOMVisitor {
		public List list = new Vector();

		protected void visitElement(Element element) {
			super.visitElement(element);
			if (isPrefixChangedNeeded(element)) {
				list.add(element);
			}
		}

		public void visitAttr(Attr attr) {
			/*
			 if (isPrefixChangedNeeded(element))
			 {
			 list.add(attr);
			 }
			 */
		}

		protected boolean isPrefixChangedNeeded(Node node) {
			String key = node.getPrefix() != null ? node.getPrefix() : ""; //$NON-NLS-1$
			return prefixMapping.get(key) != null;
		}
	}
}
