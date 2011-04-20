/*******************************************************************************
 * Copyright (c) 2008 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver (STAR) - bug 261588 - Add Edit Namespace support for XPath view
 *******************************************************************************/
package org.eclipse.wst.xml.xpath.ui.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.xml.core.internal.contentmodel.util.NamespaceInfo;
import org.eclipse.wst.xml.core.internal.contentmodel.util.NamespaceTable;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class XPathUIPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.wst.xml.xpath.ui";

	// The shared instance
	private static XPathUIPlugin plugin;

	/**
	 * The template store for xpath.
	 */
	private TemplateStore fXPathTemplateStore;

	/**
	 * The template context type registry for xpath.
	 */
	private ContributionContextTypeRegistry fXPathContextTypeRegistry;
	
	private Map<String, List<NamespaceInfo>> namespaceInfo = new ConcurrentHashMap<String, List<NamespaceInfo>>();
	

	/**
	 * The constructor
	 */
	public XPathUIPlugin() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static XPathUIPlugin getDefault() {
		return plugin;
	}

	public static void log(Exception e) {
		getDefault().getLog().log(
				new Status(IStatus.ERROR, PLUGIN_ID, 0, "", e)); //$NON-NLS-1$
	}

	public static void log(CoreException e) {
		getDefault().getLog().log(e.getStatus());
	}

	/**
	 * Returns the template store for the xpath templates.
	 * 
	 * @return the template store for the xpath templates
	 */
	public TemplateStore getXPathTemplateStore() {
		if (fXPathTemplateStore == null) {
			fXPathTemplateStore = new ContributionTemplateStore(
					getXPathTemplateContextRegistry(), getPreferenceStore(),
					"org.eclipse.wst.xml.xpath.ui.xpath_custom_templates"); //$NON-NLS-1$
			try {
				fXPathTemplateStore.load();
			} catch (IOException e) {
			}
		}
		return fXPathTemplateStore;
	}

	/**
	 * Returns the template context type registry for xpath
	 * 
	 * @return the template context type registry for xpath
	 */
	public ContextTypeRegistry getXPathTemplateContextRegistry() {
		if (fXPathContextTypeRegistry == null) {
			ContributionContextTypeRegistry registry = new ContributionContextTypeRegistry();
			registry.addContextType("xsl_xpath"); //$NON-NLS-1$
			registry.addContextType("xpath_operator"); //$NON-NLS-1$
			registry.addContextType("xpath_axis"); //$NON-NLS-1$
			registry.addContextType("exslt_function"); //$NON-NLS-1$
			registry.addContextType("xpath_2"); //$NON-NLS-1$
			registry.addContextType("extension_function"); //$NON-NLS-1$
			fXPathContextTypeRegistry = registry;
		}

		return fXPathContextTypeRegistry;
	}

	public void setNamespaceInfo(Map<String, List<NamespaceInfo>> namespaceInfo) {
		this.namespaceInfo = namespaceInfo;
	}

	public Map<String, List<NamespaceInfo>> getNamespaceInfo() {
		return namespaceInfo;
	}

	public List<NamespaceInfo> getNamespaceInfo(IDOMDocument document) {
		XPathUIPlugin plugin = XPathUIPlugin.getDefault();
		String modelID = document.getModel().getId();
		Map<String, List<NamespaceInfo>> namespaceInfo = plugin.getNamespaceInfo();
		List<NamespaceInfo> info = namespaceInfo.get(modelID);

		if (info == null) {
			if (document.getDocumentElement() != null) {
				info = new ArrayList<NamespaceInfo>();
				NamespaceTable namespaceTable = new NamespaceTable(document);
				namespaceTable.visitElement(document.getDocumentElement());
				Collection<?> namespaces = namespaceTable
						.getNamespaceInfoCollection();
				info.addAll((Collection<NamespaceInfo>) namespaces);
				namespaceInfo.put(modelID, info);
				plugin.setNamespaceInfo(namespaceInfo);
				
				ensureDefault(namespaceTable, "xs", "http://www.w3.org/2001/XMLSchema");
			}
		}
		return info;
	}

	private void ensureDefault(NamespaceTable namespaceTable, String prefix,
			String uri) {
		if (namespaceTable.getURIForPrefix(prefix) == null) namespaceTable.addNamespaceInfo(prefix, uri, null);
	}
	
}
