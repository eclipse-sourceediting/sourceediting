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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.xsl.internal.launching.PreferenceUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class LaunchFeatures
{
	private final Set<LaunchAttribute> features = new HashSet<LaunchAttribute>();

	public Set<LaunchAttribute> getFeatures()
	{
		return features;
	}

	public void addFeature(LaunchAttribute feature)
	{
		features.add(feature);
	}

	public LaunchAttribute getFeature(String uri)
	{
		for (Iterator<LaunchAttribute> iter = features.iterator(); iter.hasNext();)
		{
			LaunchAttribute tv = (LaunchAttribute) iter.next();
			if (tv.uri.equals(uri))
				return tv;
		}
		return null;
	}

	public void removeFeature(String uri)
	{
		for (Iterator<LaunchAttribute> iter = features.iterator(); iter.hasNext();)
		{
			LaunchAttribute feature = (LaunchAttribute) iter.next();
			if (feature.uri.equals(uri))
				iter.remove();
		}
	}

	public String toXML() throws ParserConfigurationException, IOException, TransformerException
	{
		Document doc = PreferenceUtil.getDocument();

		Element featuresEl = doc.createElement("Features"); //$NON-NLS-1$
		doc.appendChild(featuresEl);

		for (Iterator<LaunchAttribute> iter = features.iterator(); iter.hasNext();)
		{
			LaunchAttribute feature = (LaunchAttribute) iter.next();
			if (feature.value == null)
				continue;
			Element featureEl = doc.createElement("Feature"); //$NON-NLS-1$
			featureEl.setAttribute("name", feature.uri); //$NON-NLS-1$
			featureEl.setAttribute("type", feature.type); //$NON-NLS-1$
			featureEl.setAttribute("value", feature.value); //$NON-NLS-1$
			featuresEl.appendChild(featureEl);
		}

		return PreferenceUtil.serializeDocument(doc);
	}

	public static LaunchFeatures fromXML(InputStream inputStream) throws CoreException
	{
		Document doc = PreferenceUtil.getDocument(inputStream);

		LaunchFeatures pdef = new LaunchFeatures();

		Element featuresEl = doc.getDocumentElement();

		NodeList featureEls = featuresEl.getElementsByTagName("Feature"); //$NON-NLS-1$
		for (int i = 0; i < featureEls.getLength(); i++)
		{
			Element featureEl = (Element) featureEls.item(i);
			String name = featureEl.getAttribute("name"); //$NON-NLS-1$
			String type = featureEl.getAttribute("type"); //$NON-NLS-1$
			String value = featureEl.getAttribute("value"); //$NON-NLS-1$
			pdef.addFeature(new LaunchAttribute(name, type, value));
		}

		return pdef;
	}
}
