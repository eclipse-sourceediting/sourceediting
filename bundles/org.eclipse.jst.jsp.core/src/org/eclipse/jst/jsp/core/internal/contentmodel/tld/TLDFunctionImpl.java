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
package org.eclipse.jst.jsp.core.internal.contentmodel.tld;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDFunction;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;

public class TLDFunctionImpl implements TLDFunction {
	private String fClassName = null;
	private String fDescription = null;
	private String fDisplayName = null;
	private String fExample = null;
	private List fExtensions = new ArrayList(0);
	private String fIcon = null;
	private String fName = null;

	private CMDocument fOwnerDocument = null;
	private String fSignature = null;

	public TLDFunctionImpl(CMDocument owner) {
		super();
		fOwnerDocument = owner;
	}

	/**
	 * @return Returns the className.
	 */
	public String getClassName() {
		return fClassName;
	}
	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return fDescription;
	}

	/**
	 * @return Returns the displayName.
	 */
	public String getDisplayName() {
		return fDisplayName;
	}

	/**
	 * @return Returns the example.
	 */
	public String getExample() {
		return fExample;
	}

	/**
	 * @return Returns the extensions.
	 */
	public List getExtensions() {
		return fExtensions;
	}

	/**
	 * @return Returns the icon.
	 */
	public String getIcon() {
		return fIcon;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return fName;
	}

	/**
	 * @return Returns the ownerDocument.
	 */
	public CMDocument getOwnerDocument() {
		return fOwnerDocument;
	}

	/**
	 * @return Returns the signature.
	 */
	public String getSignature() {
		return fSignature;
	}

	/**
	 * @param className
	 *            The className to set.
	 */
	public void setClassName(String className) {
		fClassName = className;
	}
	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		fDescription = description;
	}

	/**
	 * @param displayName
	 *            The displayName to set.
	 */
	public void setDisplayName(String displayName) {
		fDisplayName = displayName;
	}

	/**
	 * @param example
	 *            The example to set.
	 */
	public void setExample(String example) {
		fExample = example;
	}

	/**
	 * @param icon
	 *            The icon to set.
	 */
	public void setIcon(String icon) {
		fIcon = icon;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		fName = name;
	}

	/**
	 * @param signature
	 *            The signature to set.
	 */
	public void setSignature(String signature) {
		fSignature = signature;
	}
}
