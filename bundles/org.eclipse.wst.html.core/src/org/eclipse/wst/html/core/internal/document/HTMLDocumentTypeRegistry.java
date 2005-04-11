/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.document;



import java.util.Enumeration;
import java.util.Hashtable;

/**
 */
public class HTMLDocumentTypeRegistry {

	private static HTMLDocumentTypeRegistry instance = null;
	private Hashtable entries = null;
	private HTMLDocumentTypeEntry defaultEntry = null;
	private HTMLDocumentTypeEntry defaultXHTMLEntry = null;
	private HTMLDocumentTypeEntry defaultWMLEntry = null;
	private HTMLDocumentTypeEntry defaultCHTMLEntry = null;

	final static int DEFAULT_HTML = 0;
	final static int DEFAULT_XHTML = 1;
	final static int DEFAULT_WML = 2;
	final static int DEFAULT_CHTML = 3;

	public static final String CHTML_PUBLIC_ID = "-//W3C//DTD Compact HTML 1.0 Draft//EN";//$NON-NLS-1$

	/**
	 */
	private HTMLDocumentTypeRegistry() {
		super();

		this.entries = new Hashtable();
		// HTML 4.01
		String name = "HTML";//$NON-NLS-1$
		String publicId = "-//W3C//DTD HTML 4.01 Transitional//EN";//$NON-NLS-1$
		String displayName = "HTML 4.01 Transitional"; //$NON-NLS-1$
		this.defaultEntry = new HTMLDocumentTypeEntry(name, publicId, null, null, false, false, displayName, false, false, false);
		this.entries.put(publicId, this.defaultEntry);
		publicId = "-//W3C//DTD HTML 4.01//EN";//$NON-NLS-1$
		displayName = "HTML 4.01 Strict"; //$NON-NLS-1$
		this.entries.put(publicId, new HTMLDocumentTypeEntry(name, publicId, null, null, false, false, displayName, false, false, false));
		publicId = "-//W3C//DTD HTML 4.01 Frameset//EN";//$NON-NLS-1$
		displayName = "HTML 4.01 Frameset"; //$NON-NLS-1$
		this.entries.put(publicId, new HTMLDocumentTypeEntry(name, publicId, null, null, false, true, displayName, false, false, false));
		// CHTML
		name = "HTML";//$NON-NLS-1$
		publicId = CHTML_PUBLIC_ID;
		displayName = "Compact HTML 1.0 Draft"; //$NON-NLS-1$
		this.defaultCHTMLEntry = new HTMLDocumentTypeEntry(name, publicId, null, null, false, false, displayName, false, false, false);
		this.entries.put(publicId, this.defaultCHTMLEntry);

		HTMLDocumentTypeRegistryReader reader = new HTMLDocumentTypeRegistryReader();
		if (reader != null)
			reader.readRegistry(this);
	}

	/**
	 */
	void regist(String pid, HTMLDocumentTypeEntry doctype) {
		if (pid == null || doctype == null)
			return;
		this.entries.put(pid, doctype);
		if (this.defaultXHTMLEntry == null) {
			if (doctype.isDefaultXHTML())
				this.defaultXHTMLEntry = doctype;
		}
		if (this.defaultWMLEntry == null) {
			if (doctype.isDefaultWML())
				this.defaultWMLEntry = doctype;
		}
	}

	/**
	 * 
	 */
	public HTMLDocumentTypeEntry getDefaultEntry(int type) {
		HTMLDocumentTypeEntry entry = null;
		switch (type) {
			case DEFAULT_XHTML :
				entry = this.defaultXHTMLEntry;
				break;
			case DEFAULT_WML :
				entry = this.defaultWMLEntry;
				break;
			case DEFAULT_CHTML :
				entry = this.defaultCHTMLEntry;
				break;
			case DEFAULT_HTML :
			default :
				entry = this.defaultEntry;
				break;
		}
		return entry;
	}

	/**
	 * 
	 */
	public HTMLDocumentTypeEntry getDefaultEntry() {
		return this.defaultEntry;
	}

	/**
	 */
	public HTMLDocumentTypeEntry getXHTMLDefaultEntry() {
		return this.defaultXHTMLEntry;
	}

	/**
	 */
	public Enumeration getEntries() {
		return this.entries.elements();
	}

	/**
	 */
	public HTMLDocumentTypeEntry getEntry(String publicId) {
		if (publicId == null)
			return null;
		return (HTMLDocumentTypeEntry) this.entries.get(publicId);
	}

	/**
	 */
	public synchronized static HTMLDocumentTypeRegistry getInstance() {
		if (instance == null)
			instance = new HTMLDocumentTypeRegistry();
		return instance;
	}
}