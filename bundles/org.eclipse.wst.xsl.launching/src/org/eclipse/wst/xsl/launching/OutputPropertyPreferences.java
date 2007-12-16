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
package org.eclipse.wst.xsl.launching;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.xsl.internal.launching.PreferenceUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OutputPropertyPreferences
{
	private final Map typeProperties = new HashMap();

	public Properties getOutputPropertyValues(String typeId)
	{
		return (Properties) typeProperties.get(typeId);
	}

	public void setOutputPropertyValues(String typeId, Properties properties)
	{
		typeProperties.put(typeId, properties);
	}

	public String getAsXML() throws ParserConfigurationException, IOException, TransformerException
	{
		Document doc = PreferenceUtil.getDocument();
		Element config = doc.createElement("outputPropertyPreferences");
		doc.appendChild(config);

		for (Iterator iter = typeProperties.entrySet().iterator(); iter.hasNext();)
		{
			Map.Entry entry = (Map.Entry) iter.next();
			String type = (String) entry.getKey();
			Element processorTypeElement = typeAsElement(doc, type);
			Properties propertyValues = (Properties) entry.getValue();
			featureValuesAsElement(doc, processorTypeElement, propertyValues);
			config.appendChild(processorTypeElement);
		}

		// Serialize the Document and return the resulting String
		return PreferenceUtil.serializeDocument(doc);
	}

	public static OutputPropertyPreferences fromXML(InputStream inputStream) throws CoreException
	{
		OutputPropertyPreferences prefs = new OutputPropertyPreferences();

		// Do the parsing and obtain the top-level node
		Document doc = PreferenceUtil.getDocument(inputStream);
		Element config = doc.getDocumentElement();

		Element[] processorTypeEls = PreferenceUtil.getChildElements(config, "processorType");
		for (int i = 0; i < processorTypeEls.length; ++i)
		{
			Element processorTypeEl = processorTypeEls[i];
			String type = elementAsType(processorTypeEl);
			Properties featureValues = elementAsPropertyValues(processorTypeEl);
			prefs.setOutputPropertyValues(type, featureValues);
		}

		return prefs;
	}

	private static String elementAsType(Element parent)
	{
		String id = parent.getAttribute("id");
		return id;
	}

	private static Element typeAsElement(Document doc, String type)
	{
		Element element = doc.createElement("processorType");
		element.setAttribute("id", type);
		return element;
	}

	private static Properties elementAsPropertyValues(Element element)
	{
		Element[] propertyEls = PreferenceUtil.getChildElements(element, "property");
		Properties propertyValues = new Properties();
		for (Element featureEl : propertyEls)
		{
			String name = featureEl.getAttribute("name");
			String value = featureEl.getAttribute("value");
			propertyValues.put(name, value);
		}
		return propertyValues;
	}

	private static void featureValuesAsElement(Document doc, Element featuresEl, Properties propertyValues)
	{
		if (propertyValues != null)
		{
			for (Object element2 : propertyValues.entrySet())
			{
				Map.Entry entry2 = (Map.Entry) element2;
				String name = (String) entry2.getKey();
				String value = (String) entry2.getValue();
				Element element = doc.createElement("property");
				element.setAttribute("name", name);
				element.setAttribute("value", value);
				featuresEl.appendChild(element);
			}
		}
	}
}
