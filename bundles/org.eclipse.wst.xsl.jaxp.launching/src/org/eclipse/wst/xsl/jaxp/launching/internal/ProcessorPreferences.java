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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.xsl.jaxp.launching.IProcessorInstall;
import org.eclipse.wst.xsl.jaxp.launching.IProcessorJar;
import org.eclipse.wst.xsl.jaxp.launching.ProcessorInstall;
import org.eclipse.wst.xsl.launching.config.PreferenceUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class ProcessorPreferences
{
	private String defaultProcessorId;
	private List<IProcessorInstall> processors = new ArrayList<IProcessorInstall>();

	public void setProcessors(List<IProcessorInstall> processors)
	{
		this.processors = processors;
	}

	public List<IProcessorInstall> getProcessors()
	{
		return processors;
	}

	public String getDefaultProcessorId()
	{
		return defaultProcessorId;
	}

	public void setDefaultProcessorId(String defaultProcessorId)
	{
		this.defaultProcessorId = defaultProcessorId;
	}

	public String getAsXML() throws ParserConfigurationException, IOException, TransformerException
	{
		Document doc = PreferenceUtil.getDocument();
		Element config = doc.createElement("processorSettings");  //$NON-NLS-1$
		doc.appendChild(config);

		// Set the defaultVM attribute on the top-level node
		if (defaultProcessorId != null)
		{
			config.setAttribute("defaultProcessor", defaultProcessorId);  //$NON-NLS-1$
		}

		for (Iterator<IProcessorInstall> iter = processors.iterator(); iter.hasNext();)
		{
			IProcessorInstall install = iter.next();
			if (!install.isContributed())
			{
				Element vmTypeElement = installAsElement(doc, install);
				config.appendChild(vmTypeElement);
			}
		}

		// Serialize the Document and return the resulting String
		return PreferenceUtil.serializeDocument(doc);
	}

	public static ProcessorPreferences fromXML(InputStream inputStream) throws CoreException
	{
		ProcessorPreferences prefs = new ProcessorPreferences();

		Document doc = PreferenceUtil.getDocument(inputStream);
		Element config = doc.getDocumentElement();

		// Populate the default VM-related fields
		prefs.setDefaultProcessorId(config.getAttribute("defaultProcessor")); //$NON-NLS-1$

		List<IProcessorInstall> processors = new ArrayList<IProcessorInstall>();
		// Traverse the parsed structure and populate the VMType to VM Map
		Element[] processorEls = PreferenceUtil.getChildElements(config, "processor"); //$NON-NLS-1$
		for (int i = 0; i < processorEls.length; ++i)
		{
			IProcessorInstall processor = elementAsInstall(processorEls[i]);
			processors.add(processor);
		}

		prefs.setProcessors(processors);

		return prefs;
	}

	private static IProcessorInstall elementAsInstall(Element parent)
	{
		String id = parent.getAttribute("id"); //$NON-NLS-1$
		String label = parent.getAttribute("label"); //$NON-NLS-1$
		String typeId = parent.getAttribute("type"); //$NON-NLS-1$
		String supports = parent.getAttribute("supports"); //$NON-NLS-1$
		String debuggerId = parent.getAttribute("debuggerId"); //$NON-NLS-1$

		IProcessorJar[] jars = null;
		Element[] jarsEls = PreferenceUtil.getChildElements(parent, "jars"); //$NON-NLS-1$
		if (jarsEls.length == 1)
		{
			jars = elementAsJars(jarsEls[0]);
		}
		IProcessorInstall install = new ProcessorInstall(id, label, typeId, jars, debuggerId, supports, false);
		return install;
	}

	private static Element installAsElement(Document doc, IProcessorInstall install)
	{
		Element element = doc.createElement("processor"); //$NON-NLS-1$
		element.setAttribute("id", install.getId()); //$NON-NLS-1$
		element.setAttribute("label", install.getName()); //$NON-NLS-1$
		element.setAttribute("type", install.getProcessorType().getId()); //$NON-NLS-1$
		element.setAttribute("supports", install.getSupports()); //$NON-NLS-1$
		element.setAttribute("debuggerId", install.getDebugger() != null ? install.getDebugger().getId() : null); //$NON-NLS-1$
		element.appendChild(jarsAsElement(doc, install.getProcessorJars()));
		return element;
	}

	private static IProcessorJar[] elementAsJars(Element element)
	{
		Element[] jarEls = PreferenceUtil.getChildElements(element, "jar"); //$NON-NLS-1$
		List<ProcessorJar> jars = new ArrayList<ProcessorJar>(jarEls.length);
		for (Element jarEl : jarEls)
		{
			Node node = jarEl.getFirstChild();
			if (node != null && node.getNodeType() == Node.TEXT_NODE)
			{
				String path = ((Text) node).getNodeValue();
				jars.add(new ProcessorJar(Path.fromPortableString(path)));
			}
		}
		return jars.toArray(new IProcessorJar[0]);
	}

	private static Element jarsAsElement(Document doc, IProcessorJar[] jars)
	{
		Element jarsEl = doc.createElement("jars");  //$NON-NLS-1$
		for (IProcessorJar jar : jars)
		{
			if (jar != null && jar.getPath() != null)
			{
				Element jarEl = doc.createElement("jar"); //$NON-NLS-1$
				Text text = doc.createTextNode(jar.getPath().toPortableString());
				jarEl.appendChild(text);
				jarsEl.appendChild(jarEl);
			}
		}
		return jarsEl;
	}
}
