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
package org.eclipse.wst.xsl.jaxp.launching;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.xsl.launching.config.PreferenceUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class LaunchProperties
{
	private final Map<String, String> properties = new HashMap<String, String>();

	public Map<String, String> getProperties()
	{
		return properties;
	}

	public void setProperty(String name, String value)
	{
		properties.put(name, value);
	}

	public String getProperty(String name)
	{
		return properties.get(name);
	}

	public void removeProperty(String name)
	{
		properties.remove(name);
	}

	public String toXML() throws ParserConfigurationException, IOException, TransformerException
	{
		Document doc = PreferenceUtil.getDocument();

		Element propertysEl = doc.createElement("Properties"); //$NON-NLS-1$
		doc.appendChild(propertysEl);

		for (Map.Entry<String, String> property : properties.entrySet())
		{
			String name = property.getKey();
			String value = property.getValue();
			Element propertyEl = doc.createElement("Property"); //$NON-NLS-1$
			propertyEl.setAttribute("name", name); //$NON-NLS-1$
			propertyEl.setAttribute("value", value); //$NON-NLS-1$
			propertysEl.appendChild(propertyEl);
		}

		return PreferenceUtil.serializeDocument(doc);
	}

	public static LaunchProperties fromXML(InputStream inputStream) throws CoreException
	{
		Document doc = PreferenceUtil.getDocument(inputStream);

		LaunchProperties pdef = new LaunchProperties();

		Element propertysEl = doc.getDocumentElement();

		NodeList propertyEls = propertysEl.getElementsByTagName("Property"); //$NON-NLS-1$
		for (int i = 0; i < propertyEls.getLength(); i++)
		{
			Element propertyEl = (Element) propertyEls.item(i);
			String name = propertyEl.getAttribute("name"); //$NON-NLS-1$
			String value = propertyEl.getAttribute("value"); //$NON-NLS-1$
			pdef.setProperty(name, value);
		}

		return pdef;
	}
}
