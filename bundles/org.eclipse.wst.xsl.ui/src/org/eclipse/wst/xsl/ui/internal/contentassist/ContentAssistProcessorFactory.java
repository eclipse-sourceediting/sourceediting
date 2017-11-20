/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver (STAR) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.contentassist;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.wst.xsl.ui.internal.XSLUIPlugin;

/**
 * A factory class that reads an eclipse extension point for Content Assist
 * Processors.
 * @author dcarver
 * @since 1.1
 */
public class ContentAssistProcessorFactory {
	private static IContentAssistProcessor[] processors = null;
	
	/**
	 * Creates the necessary Content Assist Processors that have been
	 * Contributed to the XSL Editor configuration.
	 * @return
	 */
	public static IContentAssistProcessor[] createProcessors() {
		IExtensionPoint point = getExtensionPoint();
		if (point == null) {
			return processors;
		}
		getProcessors(point);
		return processors;
	}

	private static void getProcessors(IExtensionPoint point) {
		ArrayList<Object> eprocessors = new ArrayList<Object>();
		IConfigurationElement[] configElems = point.getConfigurationElements();
		
		for (IConfigurationElement processor : configElems) {
			try {
				final Object o = processor.createExecutableExtension("class"); //$NON-NLS-1$
				eprocessors.add(o);
			} catch (CoreException ex) {
				
			}
		}
		if (!eprocessors.isEmpty()) {
			processors = new IContentAssistProcessor[eprocessors.size()];
			eprocessors.toArray(processors);
		}
	}

	private static IExtensionPoint getExtensionPoint() {
		IExtensionRegistry pluginRegistry = Platform.getExtensionRegistry();
		IExtensionPoint point = pluginRegistry.getExtensionPoint(XSLUIPlugin.PLUGIN_ID, "contentAssistProcessor"); //$NON-NLS-1$
		return point;
	}
}
