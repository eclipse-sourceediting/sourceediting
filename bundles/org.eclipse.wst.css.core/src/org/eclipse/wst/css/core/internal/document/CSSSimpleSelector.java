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
package org.eclipse.wst.css.core.internal.document;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.css.core.internal.provisional.document.ICSSSimpleSelector;


/**
 * 
 */
class CSSSimpleSelector extends CSSSelectorItem implements ICSSSimpleSelector {

	private String fName = null;
	private String fCachedString = null;
	private StringBuffer fStringBuf = null;
	private List fPseudoName = null;
	private List fAttribute = null;
	private List fClass = null;
	private List fID = null;

	/**
	 * CSSSimpleSelector constructor comment.
	 */
	public CSSSimpleSelector() {
		super();
	}

	/**
	 * 
	 */
	void addAttribute(String attribute) {
		if (fAttribute == null) {
			fAttribute = new ArrayList();
		}
		fAttribute.add(attribute);
		addToBuf("[");//$NON-NLS-1$
		addToBuf(attribute);
		addToBuf("]");//$NON-NLS-1$
		fCachedString = null;
	}

	/**
	 * 
	 */
	void addClass(String cls) {
		if (fClass == null) {
			fClass = new ArrayList();
		}
		fClass.add(cls);
		addToBuf(".");//$NON-NLS-1$
		addToBuf(cls);
		fCachedString = null;
	}

	/**
	 * 
	 */
	void addID(String id) {
		if (fID == null) {
			fID = new ArrayList();
		}
		fID.add(id);
		addToBuf("#");//$NON-NLS-1$
		addToBuf(id);
		fCachedString = null;
	}

	/**
	 * 
	 */
	void addPseudoName(String cls) {
		if (fPseudoName == null) {
			fPseudoName = new ArrayList();
		}
		fPseudoName.add(cls);
		addToBuf(":");//$NON-NLS-1$
		addToBuf(cls);
		fCachedString = null;
	}

	/**
	 * 
	 */
	private void addToBuf(String str) {
		if (fStringBuf == null) {
			fStringBuf = new StringBuffer();
		}
		fStringBuf.append(str);
	}

	/**
	 * @return boolean
	 * @param obj
	 *            java.lang.Object
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || this.getClass() != obj.getClass())
			return false;

		CSSSimpleSelector foreign = (CSSSimpleSelector) obj;

		if (getName().compareToIgnoreCase(foreign.getName()) != 0)
			return false;

		int i;

		// compare pseudo-classes / pseudo-elements
		if (getNumOfPseudoNames() != foreign.getNumOfPseudoNames())
			return false;
		for (i = 0; i < getNumOfPseudoNames(); i++) {
			if (getPseudoName(i).compareToIgnoreCase(foreign.getPseudoName(i)) != 0)
				return false;
		}

		// compare classes
		if (getNumOfClasses() != foreign.getNumOfClasses())
			return false;
		for (i = 0; i < getNumOfClasses(); i++) {
			if (getClass(i).compareToIgnoreCase(foreign.getClass(i)) != 0)
				return false;
		}

		// compare IDs
		if (getNumOfIDs() != foreign.getNumOfIDs())
			return false;
		for (i = 0; i < getNumOfIDs(); i++) {
			if (getID(i).compareToIgnoreCase(foreign.getID(i)) != 0)
				return false;
		}

		// compare Attributes
		if (getNumOfAttributes() != foreign.getNumOfAttributes())
			return false;
		for (i = 0; i < getNumOfAttributes(); i++) {
			if (getAttribute(i).compareToIgnoreCase(foreign.getAttribute(i)) != 0)
				return false;
		}
		return true;
	}

	/**
	 * @return java.lang.String
	 * @param index
	 *            int
	 */
	public String getAttribute(int index) {
		if (fAttribute != null && 0 <= index && index < fAttribute.size()) {
			return (String) fAttribute.get(index);
		}
		else {
			return "";//$NON-NLS-1$
		}
	}

	/**
	 * @return java.lang.String
	 * @param index
	 *            int
	 */
	public String getClass(int index) {
		if (fClass != null && 0 <= index && index < fClass.size()) {
			return (String) fClass.get(index);
		}
		else {
			return "";//$NON-NLS-1$
		}
	}

	/**
	 * @return java.lang.String
	 * @param index
	 *            int
	 */
	public String getID(int index) {
		if (fID != null && 0 <= index && index < fID.size()) {
			return (String) fID.get(index);
		}
		else {
			return "";//$NON-NLS-1$
		}
	}

	/**
	 * @return int
	 */
	public int getItemType() {
		return SIMPLE;
	}

	/**
	 * @return java.lang.String
	 */
	public String getName() {
		return (fName != null) ? fName : "";//$NON-NLS-1$
	}

	/**
	 * @return boolean
	 */
	public int getNumOfAttributes() {
		return (fAttribute != null) ? fAttribute.size() : 0;
	}

	/**
	 * @return boolean
	 */
	public int getNumOfClasses() {
		return (fClass != null) ? fClass.size() : 0;
	}

	/**
	 * @return boolean
	 */
	public int getNumOfIDs() {
		return (fID != null) ? fID.size() : 0;
	}

	/**
	 * @return boolean
	 */
	public int getNumOfPseudoNames() {
		return (fPseudoName != null) ? fPseudoName.size() : 0;
	}

	/**
	 * @return java.lang.String
	 * @param index
	 *            int
	 */
	public String getPseudoName(int index) {
		if (fPseudoName != null && 0 <= index && index < fPseudoName.size()) {
			return (String) fPseudoName.get(index);
		}
		else {
			return "";//$NON-NLS-1$
		}
	}

	/**
	 * @return java.lang.String
	 */
	public String getString() {
		if (fCachedString == null) {
			StringBuffer buf = new StringBuffer(getName());
			if (fStringBuf != null) {
				buf.append(fStringBuf.toString());
			}
			fCachedString = buf.toString();
		}
		return fCachedString;
	}

	/**
	 * @return boolean
	 */
	public boolean isUniversal() {
		return (fName == null || fName.equals("*"));//$NON-NLS-1$
	}

	/**
	 * 
	 */
	void setName(String name) {
		fName = name;
		fCachedString = null;
	}
}
