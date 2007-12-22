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
package org.eclipse.wst.xsl.launching.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.xsl.internal.launching.PreferenceUtil;
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
		return (String) properties.get(name);
	}

	public void removeProperty(String name)
	{
		properties.remove(name);
	}

	public String toXML() throws ParserConfigurationException, IOException, TransformerException
	{
		Document doc = PreferenceUtil.getDocument();

		Element featuresEl = doc.createElement("Properties");
		doc.appendChild(featuresEl);

		for (Iterator<?> iter = properties.entrySet().iterator(); iter.hasNext();)
		{
			Map.Entry entry = (Map.Entry) iter.next();
			String name = (String) entry.getKey();
			String value = (String) entry.getValue();
			if (value == null)
				continue;
			Element featureEl = doc.createElement("Property");
			featureEl.setAttribute("name", name);
			featureEl.setAttribute("value", value);
			featuresEl.appendChild(featureEl);
		}

		return PreferenceUtil.serializeDocument(doc);
	}

	public static LaunchProperties fromXML(InputStream inputStream) throws CoreException
	{
		Document doc = PreferenceUtil.getDocument(inputStream);

		LaunchProperties pdef = new LaunchProperties();

		Element featuresEl = doc.getDocumentElement();

		NodeList featureEls = featuresEl.getElementsByTagName("Property");
		for (int i = 0; i < featureEls.getLength(); i++)
		{
			Element featureEl = (Element) featureEls.item(i);
			String name = featureEl.getAttribute("name");
			String value = featureEl.getAttribute("value");
			pdef.setProperty(name, value);
		}

		return pdef;
	}
}
