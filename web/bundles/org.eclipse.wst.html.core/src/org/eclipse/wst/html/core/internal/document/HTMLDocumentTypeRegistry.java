/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.document;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class HTMLDocumentTypeRegistry {
	static class HTMLDocumentTypeEntryComparator implements Comparator {
		static Collator delegate = Collator.getInstance();

		public int compare(Object o1, Object o2) {
			if (o1 instanceof HTMLDocumentTypeEntry && o2 instanceof HTMLDocumentTypeEntry) {
				if (((HTMLDocumentTypeEntry) o1).getDisplayName() != null && ((HTMLDocumentTypeEntry) o2).getDisplayName() != null) {
					return delegate.compare(((HTMLDocumentTypeEntry) o1).getDisplayName(), ((HTMLDocumentTypeEntry) o2).getDisplayName());
				}
			}
			return 0;
		}
	}

	private static HTMLDocumentTypeRegistry instance = null;
	private Hashtable entries = null;
	private HTMLDocumentTypeEntry defaultEntry = null;
	private HTMLDocumentTypeEntry defaultXHTMLEntry = null;
	private HTMLDocumentTypeEntry defaultWMLEntry = null;
	private HTMLDocumentTypeEntry defaultCHTMLEntry = null;
	private HTMLDocumentTypeEntry defaultHTML5Entry = null;
	
	final static int DEFAULT_HTML = 0;
	final static int DEFAULT_XHTML = 1;
	final static int DEFAULT_WML = 2;
	final static int DEFAULT_CHTML = 3;
	final static int DEFAULT_HTML5 = 4;
	
	public static final String CHTML_PUBLIC_ID = "-//W3C//DTD Compact HTML 1.0 Draft//EN";//$NON-NLS-1$

	/**
	 */
	private HTMLDocumentTypeRegistry() {
		super();

		this.entries = new Hashtable();
		// HTML 4.01
		String name = "HTML";//$NON-NLS-1$
		String publicId = "-//W3C//DTD HTML 4.01 Transitional//EN";//$NON-NLS-1$
		String systemId = "http://www.w3.org/TR/html4/loose.dtd";//$NON-NLS-1$
		String displayName = "HTML 4.01 Transitional"; //$NON-NLS-1$
		this.defaultEntry = new HTMLDocumentTypeEntry(name, publicId, systemId, null, false, false, displayName, false, false, false, true);
		this.entries.put(publicId, this.defaultEntry);
		publicId = "-//W3C//DTD HTML 4.01//EN";//$NON-NLS-1$
		systemId = "http://www.w3.org/TR/html4/strict.dtd";//$NON-NLS-1$
		displayName = "HTML 4.01 Strict"; //$NON-NLS-1$
		this.entries.put(publicId, new HTMLDocumentTypeEntry(name, publicId, systemId, null, false, false, displayName, false, false, false, true));
		publicId = "-//W3C//DTD HTML 4.01 Frameset//EN";//$NON-NLS-1$
		systemId = "http://www.w3.org/TR/html4/frameset.dtd";//$NON-NLS-1$
		displayName = "HTML 4.01 Frameset"; //$NON-NLS-1$
		this.entries.put(publicId, new HTMLDocumentTypeEntry(name, publicId, systemId, null, false, true, displayName, false, false, false, true));
		// CHTML
		name = "HTML";//$NON-NLS-1$
		publicId = CHTML_PUBLIC_ID;
		displayName = "Compact HTML 1.0 Draft"; //$NON-NLS-1$
		this.defaultCHTMLEntry = new HTMLDocumentTypeEntry(name, publicId, null, null, false, false, displayName, false, false, false, true);
		this.entries.put(publicId, this.defaultCHTMLEntry);

		//HTML5
		name = "HTML5";//$NON-NLS-1$
		publicId = "";
		displayName = "HTML5"; //$NON-NLS-1$
		this.defaultHTML5Entry = new HTMLDocumentTypeEntry(name, publicId, null, null, false, false, displayName, false, false, false, true);
		this.entries.put(publicId, this.defaultHTML5Entry);
		
		

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
			case DEFAULT_HTML5 :
				entry = this.defaultHTML5Entry;
				break;
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
		List values = new ArrayList(this.entries.values());
		Collections.sort(values, new HTMLDocumentTypeEntryComparator());
		return new Vector(values).elements();
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