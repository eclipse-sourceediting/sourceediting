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
package org.eclipse.wst.xsl.internal.launching;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.wst.xsl.launching.XSLLaunchConfigurationConstants;
import org.eclipse.wst.xsl.launching.XSLTRuntime;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer
{

	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences node = new DefaultScope().getNode(LaunchingPlugin.PLUGIN_ID);

		ProcessorPreferences prefs = new ProcessorPreferences();
		prefs.setDefaultProcessorId(XSLTRuntime.JRE_DEFAULT_PROCESSOR_ID);

		OutputPropertyPreferences outputPrefs = new OutputPropertyPreferences();
		outputPrefs.setOutputPropertyValues(XSLTRuntime.JRE_DEFAULT_PROCESSOR_TYPE_ID, XSLTRuntime.createDefaultOutputProperties(XSLTRuntime.JRE_DEFAULT_PROCESSOR_TYPE_ID));
		outputPrefs.setOutputPropertyValues(XSLLaunchConfigurationConstants.XALAN_TYPE_ID, XSLTRuntime.createDefaultOutputProperties(XSLLaunchConfigurationConstants.XALAN_TYPE_ID));
		outputPrefs.setOutputPropertyValues(XSLLaunchConfigurationConstants.SAXONB_TYPE_ID, XSLTRuntime.createDefaultOutputProperties(XSLLaunchConfigurationConstants.SAXONB_TYPE_ID));

		try
		{
			String xml = prefs.getAsXML();
			node.put(XSLTRuntime.PREF_PROCESSOR_XML, xml);

			xml = outputPrefs.getAsXML();
			node.put(XSLTRuntime.PREF_OUTPUT_PROPERTIES_XML, xml);

			node.put(XSLLaunchConfigurationConstants.ATTR_DEFAULT_DEBUGGING_INSTALL_ID, "org.eclipse.wst.xsl.launching.xalan.processor"); //$NON-NLS-1$
		}
		catch (ParserConfigurationException e)
		{
			LaunchingPlugin.log(e);
		}
		catch (IOException e)
		{
			LaunchingPlugin.log(e);
		}
		catch (TransformerException e)
		{
			LaunchingPlugin.log(e);
		}
	}
}
