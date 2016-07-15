/*************************************************************************************
 * Copyright (c) 2014-2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package org.eclipse.wst.json.core.internal.schema.catalog;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.wst.json.core.JSONCorePlugin;

@SuppressWarnings("nls")
public class EntryParser {

	public static final String JSON_CATALOG_ENTRIES = "catalogEntries"; //$NON-NLS-1$
	private static final JAXBContext jaxbContext;
	
	static {
		try {
			jaxbContext = JAXBContext.newInstance(EntriesWrapper.class);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	public Set<UserEntry> parse(String xml) throws CoreException {
		if (xml == null || xml.trim().isEmpty()) {
			return null;
		}
		try {
			EntriesWrapper list = (EntriesWrapper) unmarshall(jaxbContext, xml);
			return list.entries == null ? Collections.<UserEntry>emptySet() : list.entries;
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, JSONCorePlugin.PLUGIN_ID,
					"Unable to parse entry", e));
		}
	}
	
	public String serialize(Set<UserEntry> entries) throws CoreException {
		try {
			EntriesWrapper list = new EntriesWrapper();
			list.entries = entries;
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			StringWriter writer = new StringWriter();
			marshaller.marshal(list, writer);
			return writer.toString();
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
	
	@XmlRootElement(name = "entries")
	@XmlAccessorType (XmlAccessType.FIELD)
	static class EntriesWrapper {
		@XmlElement(name = "entry", type=UserEntry.class)
		Set<UserEntry> entries;
	}
	
	protected Object unmarshall(JAXBContext jaxbContext, String xml) throws JAXBException, IOException, XMLStreamException {
		return unmarshall(jaxbContext, new StringReader(xml));
	}
	
	protected Object unmarshall(JAXBContext jaxbContext, File file) throws JAXBException, IOException, XMLStreamException {
		return unmarshall(jaxbContext, new FileReader(file));
	}

	protected Object unmarshall(JAXBContext jaxbContext, Reader reader) throws JAXBException, IOException, XMLStreamException {
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		XMLInputFactory xmlif = XMLInputFactory.newInstance();
		Reader r = null;
		try {
			r = reader;
			XMLStreamReader xmler = xmlif.createXMLStreamReader(r);
			return jaxbUnmarshaller.unmarshal(xmler);
		} finally {
			if (r != null) {
				r.close();
			}
		}
	}
}
