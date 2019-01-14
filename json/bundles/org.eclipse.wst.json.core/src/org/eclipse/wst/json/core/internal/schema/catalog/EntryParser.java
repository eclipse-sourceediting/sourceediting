/*************************************************************************************
 * Copyright (c) 2014-2019 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 *     Nitin Dahyabhai <nitind@us.ibm.com> - remove JAXB dependency
 ************************************************************************************/
package org.eclipse.wst.json.core.internal.schema.catalog;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.net.URI;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.wst.json.core.JSONCorePlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class EntryParser {

	public static final String JSON_CATALOG_ENTRIES = "catalogEntries"; //$NON-NLS-1$

	public Set<UserEntry> parse(String xml) throws CoreException {
		if (xml == null || xml.trim().isEmpty()) {
			return null;
		}
		Set<UserEntry> list = new HashSet<>();
		try {
			Document parsed = CommonXML.getDocumentBuilder(false).parse(new InputSource(new StringReader(xml)));
			NodeList entryElements = parsed.getElementsByTagName("entry");
			for (int i = 0; i < entryElements.getLength(); i++) {
				Element el = (Element) entryElements.item(i);
				UserEntry entry = new UserEntry();
				entry.setFileMatch(el.getAttribute("fileMatch"));
				entry.setUrl(new URI(el.getAttribute("url")));
				list.add(entry);
			}
			return list;
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, JSONCorePlugin.PLUGIN_ID,
					"Unable to parse entry", e));
		}
	}
	
	public String serialize(Set<UserEntry> entries) throws CoreException {
		try {
			Document document = CommonXML.getDocumentBuilder(false).newDocument();
			Element entriesElement = document.createElement("entries");
			document.appendChild(entriesElement);
			for(UserEntry userEntry: entries) {
				Element entryElement = document.createElement("entry");
				entriesElement.appendChild(entryElement);
				entryElement.setAttribute("fileMatch", userEntry.getFileMatch());
				entryElement.setAttribute("url", userEntry.getUrl().toString());
			}
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			CommonXML.serialize(document, bytes);
			return new String(bytes.toByteArray(), "utf8");
			
		} catch (Exception shouldntHappen) {
			throw new CoreException(new Status(IStatus.ERROR, JSONCorePlugin.PLUGIN_ID,
					"Unable to serialize entries ", shouldntHappen));
		}
	}
	
	public static Set<UserEntry> getUserEntries() {
		Set<UserEntry> entries = new LinkedHashSet<UserEntry>();
		IEclipsePreferences prefs = getPreferences();
		String xml = prefs.get(JSON_CATALOG_ENTRIES, null);
		if (xml != null && !xml.trim().isEmpty()) {
			try {
				Set<UserEntry> set = new EntryParser().parse(xml);
				if (set != null) {
					entries.addAll(set);
				}
			} catch (CoreException e) {
				IStatus status = new Status(IStatus.ERROR, JSONCorePlugin.PLUGIN_ID, e
						.getLocalizedMessage(), e);
				JSONCorePlugin.getDefault().getLog().log(status);
			}
		}
		return entries;
	}
	
	private static IEclipsePreferences getPreferences() {
		IEclipsePreferences preferences = InstanceScope.INSTANCE
				  .getNode("org.eclipse.wst.json.ui"); //$NON-NLS-1$
		return preferences;
	}
}
