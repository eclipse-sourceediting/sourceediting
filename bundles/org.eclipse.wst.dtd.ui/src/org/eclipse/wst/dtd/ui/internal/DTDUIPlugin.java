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
package org.eclipse.wst.dtd.ui.internal;

import java.io.IOException;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.dtd.ui.internal.preferences.DTDUIPreferenceNames;
import org.eclipse.wst.dtd.ui.internal.templates.TemplateContextTypeIdsDTD;

/**
 * The main plugin class to be used in the desktop.
 */
public class DTDUIPlugin extends AbstractUIPlugin {
	// The shared instance.
	private static DTDUIPlugin plugin;
	
	/**
	 * The template store for the dtd ui.
	 */
	private TemplateStore fTemplateStore;

	/**
	 * The template context type registry for dtd ui.
	 */
	private ContextTypeRegistry fContextTypeRegistry;

	/**
	 * Returns the shared instance.
	 */
	public static DTDUIPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * The constructor.
	 */
	public DTDUIPlugin() {
		super();
		plugin = this;
	}
	
	/**
	 * Returns the template store for the dtd editor templates.
	 * 
	 * @return the template store for the dtd editor templates
	 */
	public TemplateStore getTemplateStore() {
		if (fTemplateStore == null) {
			fTemplateStore = new ContributionTemplateStore(getTemplateContextRegistry(), getPreferenceStore(), DTDUIPreferenceNames.TEMPLATES_KEY);

			try {
				fTemplateStore.load();
			} catch (IOException e) {
				Logger.logException(e);
			}
		}
		return fTemplateStore;
	}

	/**
	 * Returns the template context type registry for the dtd plugin.
	 * 
	 * @return the template context type registry for the dtd plugin
	 */
	public ContextTypeRegistry getTemplateContextRegistry() {
		if (fContextTypeRegistry == null) {
			ContributionContextTypeRegistry registry = new ContributionContextTypeRegistry();
			registry.addContextType(TemplateContextTypeIdsDTD.NEW);

			fContextTypeRegistry = registry;
		}

		return fContextTypeRegistry;
	}

	public Image getImage(String iconName) {
		ImageRegistry imageRegistry = getImageRegistry();
		Image image = imageRegistry.get(iconName);

		if (image == null) {
			String thisID = getDefault().getBundle().getSymbolicName();
			imageRegistry.put(iconName, imageDescriptorFromPlugin(thisID, iconName));
			image = imageRegistry.get(iconName);
		}

		return image;
	}

	public static Image getDTDImage(String iconName) {
		return getDefault().getImage(iconName);
	}

	public static ImageDescriptor getDTDImageDescriptor(String iconName) {
		String thisID = getDefault().getBundle().getSymbolicName();
		return AbstractUIPlugin.imageDescriptorFromPlugin(thisID, iconName);
	}
}
