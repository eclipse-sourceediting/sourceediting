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
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.xsl.launching.config.PreferenceUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FeaturePreferences
{
	private Map<String, Map<String, String>> typeFeatures;

	public Map<String, String> getFeaturesValues(String typeId)
	{
		return typeFeatures.get(typeId);
	}

	public void setTypeFeatures(Map<String, Map<String, String>> typeFeatures)
	{
		this.typeFeatures = typeFeatures;
	}

	public String getAsXML() throws ParserConfigurationException, IOException, TransformerException
	{
		Document doc = PreferenceUtil.getDocument();
		Element config = doc.createElement("featurePreferences");  //$NON-NLS-1$
		doc.appendChild(config);
		
		for (String typeId : typeFeatures.keySet())
		{
			Element processorTypeElement = typeAsElement(doc, typeId);
			Map<String, String> featureValues = typeFeatures.get(typeId);
			featureValuesAsElement(doc, processorTypeElement, featureValues);
			config.appendChild(processorTypeElement);
		}

		// Serialize the Document and return the resulting String
		return PreferenceUtil.serializeDocument(doc);
	}

	public static FeaturePreferences fromXML(InputStream inputStream) throws CoreException
	{
		FeaturePreferences prefs = new FeaturePreferences();

		// Do the parsing and obtain the top-level node
		Document doc = PreferenceUtil.getDocument(inputStream);
		Element config = doc.getDocumentElement();

		Map<String, Map<String, String>> typeFeatures = new HashMap<String, Map<String, String>>();
		Element[] processorTypeEls = PreferenceUtil.getChildElements(config, "processorType"); //$NON-NLS-1$
		for (int i = 0; i < processorTypeEls.length; ++i)
		{
			Element processorTypeEl = processorTypeEls[i];
			String type = elementAsType(processorTypeEl);
			Map<String, String> featureValues = elementAsFeatureValues(processorTypeEl);
			typeFeatures.put(type, featureValues);
		}

		prefs.setTypeFeatures(typeFeatures);

		return prefs;
	}

	private static String elementAsType(Element parent)
	{
		String id = parent.getAttribute("id"); //$NON-NLS-1$
		return id;
	}

	private static Element typeAsElement(Document doc, String type)
	{
		Element element = doc.createElement("processorType"); //$NON-NLS-1$
		element.setAttribute("id", type); //$NON-NLS-1$
		return element;
	}

	private static Map<String, String> elementAsFeatureValues(Element element)
	{
		Element[] featureEls = PreferenceUtil.getChildElements(element, "feature"); //$NON-NLS-1$
		Map<String, String> featureValues = new HashMap<String, String>(featureEls.length);
		for (Element featureEl : featureEls)
		{
			String uri = featureEl.getAttribute("uri"); //$NON-NLS-1$
			String value = featureEl.getAttribute("value"); //$NON-NLS-1$
			featureValues.put(uri, value);
		}
		return featureValues;
	}

	private static void featureValuesAsElement(Document doc, Element featuresEl, Map<String, String> featureValues)
	{
		if (featureValues != null)
		{
			for (Map.Entry<String,String> entry2 : featureValues.entrySet())
			{
				String uri = entry2.getKey();
				String value = entry2.getValue();
				Element element = doc.createElement("feature"); //$NON-NLS-1$
				element.setAttribute("uri", uri); //$NON-NLS-1$
				element.setAttribute("value", value); //$NON-NLS-1$
				featuresEl.appendChild(element);
			}
		}
	}
}
