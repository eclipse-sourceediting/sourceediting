/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.metamodelimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.wst.css.core.internal.metamodel.CSSMMNode;


abstract class CSSMMNodeImpl implements CSSMMNode {

	public CSSMMNodeImpl() {
		super();
	}

	public abstract String getType();

	public String getDescription() {
		return fDescription;
	}

	public String getAttribute(String name) {
		if (fAttributes == null) {
			return null;
		}
		return (String) fAttributes.get(name);
	}

	public Iterator getChildNodes() {
		if (fChildNodes == null) {
			return Collections.EMPTY_LIST.iterator();
		}
		else {
			return Collections.unmodifiableCollection(fChildNodes).iterator();
		}
	}

	public Iterator getDescendants() {
		List descendants = new ArrayList();
		Iterator iChild = getChildNodes();
		while (iChild.hasNext()) {
			CSSMMNode child = (CSSMMNode) iChild.next();
			Iterator iDescendant = child.getDescendants();
			if (iDescendant.hasNext()) {
				while (iDescendant.hasNext()) {
					CSSMMNode descendant = (CSSMMNode) iDescendant.next();
					if (!descendants.contains(descendant)) {
						descendants.add(descendant);
					}
				}
			}
			else {
				if (!descendants.contains(child)) {
					descendants.add(child);
				}
			}
		}
		return Collections.unmodifiableCollection(descendants).iterator();
	}

	void appendChild(CSSMMNode child) {
		if (child == null) {
			return;
		}
		if (fChildNodes == null) {
			fChildNodes = new ArrayList();
		}
		if (!fChildNodes.contains(child)) {
			fChildNodes.add(child);
			((CSSMMNodeImpl) child).fRefCount++;
		}
	}

	void removeChild(CSSMMNode child) {
		if (child == null || fChildNodes == null) {
			return;
		}
		Iterator i = fChildNodes.iterator();
		while (i.hasNext()) {
			if (i.next() == child) {
				i.remove();
				((CSSMMNodeImpl) child).fRefCount--;
				return;
			}
		}
	}

	void removeAllChildNodes() {
		if (fChildNodes == null) {
			return;
		}
		Iterator i = fChildNodes.iterator();
		while (i.hasNext()) {
			((CSSMMNodeImpl) i.next()).fRefCount--;
		}
		fChildNodes.clear();
	}

	abstract boolean canContain(CSSMMNode child);

	void initializeAttribute(Map attributes) throws IllegalArgumentException {
		if (fAttributes == null) {
			fAttributes = new HashMap();
		}
		fAttributes.putAll(attributes);
		postSetAttribute();
	}

	void setDescription(String description) {
		fDescription = description;
	}

	void postSetAttribute() throws IllegalArgumentException {
		// default : nop
	}

	abstract short getError();

	int getChildCount() {
		return (fChildNodes != null) ? fChildNodes.size() : 0;
	}

	int getReferenceCount() {
		return fRefCount;
	}


	private List fChildNodes = null;
	private String fDescription = null;
	private Map fAttributes = null;
	private int fRefCount = 0; // for error check

	static final String ATTR_NAME = "name"; //$NON-NLS-1$
	static final String NAME_NOT_AVAILABLE = ""; //$NON-NLS-1$
}
