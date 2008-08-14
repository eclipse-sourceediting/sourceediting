/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.jaxp.launching.internal;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.wst.xsl.jaxp.launching.JAXPLaunchConfigurationConstants;
import org.eclipse.wst.xsl.jaxp.launching.JAXPRuntime;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer
{

	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences node = new DefaultScope().getNode(JAXPLaunchingPlugin.PLUGIN_ID);

		ProcessorPreferences prefs = new ProcessorPreferences();
		prefs.setDefaultProcessorId(JAXPRuntime.JRE_DEFAULT_PROCESSOR_ID);

		OutputPropertyPreferences outputPrefs = new OutputPropertyPreferences();
		outputPrefs.setOutputPropertyValues(JAXPRuntime.JRE_DEFAULT_PROCESSOR_TYPE_ID, JAXPRuntime.createDefaultOutputProperties(JAXPRuntime.JRE_DEFAULT_PROCESSOR_TYPE_ID));
		outputPrefs.setOutputPropertyValues(JAXPLaunchConfigurationConstants.XALAN_TYPE_ID, JAXPRuntime.createDefaultOutputProperties(JAXPLaunchConfigurationConstants.XALAN_TYPE_ID));
		outputPrefs.setOutputPropertyValues(JAXPLaunchConfigurationConstants.SAXON_TYPE_ID, JAXPRuntime.createDefaultOutputProperties(JAXPLaunchConfigurationConstants.SAXON_TYPE_ID));
		outputPrefs.setOutputPropertyValues(JAXPLaunchConfigurationConstants.SAXON_1_0_TYPE_ID, JAXPRuntime.createDefaultOutputProperties(JAXPLaunchConfigurationConstants.SAXON_1_0_TYPE_ID));

		try
		{
			String xml = prefs.getAsXML();
			node.put(JAXPRuntime.PREF_PROCESSOR_XML, xml);

			xml = outputPrefs.getAsXML();
			node.put(JAXPRuntime.PREF_OUTPUT_PROPERTIES_XML, xml);

			node.put(JAXPLaunchConfigurationConstants.ATTR_DEFAULT_DEBUGGING_INSTALL_ID, "org.eclipse.wst.xsl.launching.xalan.processor"); //$NON-NLS-1$
		}
		catch (ParserConfigurationException e)
		{
			JAXPLaunchingPlugin.log(e);
		}
		catch (IOException e)
		{
			JAXPLaunchingPlugin.log(e);
		}
		catch (TransformerException e)
		{
			JAXPLaunchingPlugin.log(e);
		}
	}
}
