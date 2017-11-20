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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.xsl.launching.config.LaunchAttribute;
import org.eclipse.wst.xsl.launching.config.PreferenceUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class LaunchAttributes
{
	private final Set<LaunchAttribute> attributes = new HashSet<LaunchAttribute>();

	public Set<LaunchAttribute> getAttributes()
	{
		return attributes;
	}

	public void addAttribute(LaunchAttribute attribute)
	{
		attributes.add(attribute);
	}

	public LaunchAttribute getAttribute(String uri)
	{
		for (Iterator<LaunchAttribute> iter = attributes.iterator(); iter.hasNext();)
		{
			LaunchAttribute tv = iter.next();
			if (tv.uri.equals(uri))
				return tv;
		}
		return null;
	}

	public void removeAtribute(String uri)
	{
		for (Iterator<LaunchAttribute> iter = attributes.iterator(); iter.hasNext();)
		{
			LaunchAttribute attribute = iter.next();
			if (attribute.uri.equals(uri))
				iter.remove();
		}
	}

	public String toXML() throws ParserConfigurationException, IOException, TransformerException
	{
		Document doc = PreferenceUtil.getDocument();

		Element attributesEl = doc.createElement("Attributes"); //$NON-NLS-1$
		doc.appendChild(attributesEl);

		for (Iterator<LaunchAttribute> iter = attributes.iterator(); iter.hasNext();)
		{
			LaunchAttribute attribute = iter.next();
			Element attributeEl = doc.createElement("Attribute"); //$NON-NLS-1$
			attributeEl.setAttribute("name", attribute.uri); //$NON-NLS-1$
			attributeEl.setAttribute("type", attribute.type); //$NON-NLS-1$
			attributeEl.setAttribute("value", attribute.value); //$NON-NLS-1$
			attributesEl.appendChild(attributeEl);
		}

		return PreferenceUtil.serializeDocument(doc);
	}

	public static LaunchAttributes fromXML(InputStream inputStream) throws CoreException
	{
		Document doc = PreferenceUtil.getDocument(inputStream);

		LaunchAttributes pdef = new LaunchAttributes();

		Element attributesEl = doc.getDocumentElement();

		NodeList attributeEls = attributesEl.getElementsByTagName("Attribute"); //$NON-NLS-1$
		for (int i = 0; i < attributeEls.getLength(); i++)
		{
			Element attributeEl = (Element) attributeEls.item(i);
			String name = attributeEl.getAttribute("name"); //$NON-NLS-1$
			String type = attributeEl.getAttribute("type"); //$NON-NLS-1$
			String value = attributeEl.getAttribute("value"); //$NON-NLS-1$
			pdef.addAttribute(new LaunchAttribute(name, type, value));
		}

		return pdef;
	}
}
