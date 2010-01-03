/*******************************************************************************
 * Copyright (c) 2007, 2010 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver (Intalio) - clean up find bugs
 *******************************************************************************/
package org.eclipse.wst.xsl.launching.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.xsl.internal.launching.LaunchingPlugin;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class LaunchPipeline {
	private List<LaunchTransform> transformDefs = new ArrayList<LaunchTransform>();
	private final Properties outputProperties = new Properties();

	public Properties getOutputProperties() {
		return outputProperties;
	}

	public void addOutputProperty(String name, String value) {
		outputProperties.put(name, value);
	}

	public List<LaunchTransform> getTransformDefs() {
		return transformDefs;
	}

	public void setTransformDefs(List<LaunchTransform> transforms) {
		transformDefs = transforms;
	}

	public void addTransformDef(LaunchTransform tdef) {
		tdef.setPipeline(this);
		transformDefs.add(tdef);
	}

	public void removeTransformDef(LaunchTransform tdef) {
		transformDefs.remove(tdef);
	}

	public String toXML() throws CoreException {
		String xml = null;
		try {
			Document doc = PreferenceUtil.getDocument();
			Element rootEl = doc.createElement("Pipeline"); //$NON-NLS-1$
			doc.appendChild(rootEl);

			Element opEl = doc.createElement("OutputProperties"); //$NON-NLS-1$
			rootEl.appendChild(opEl);
			for (Object element : outputProperties.entrySet()) {
				Map.Entry entry = (Map.Entry) element;
				Element propEl = doc.createElement("Property"); //$NON-NLS-1$
				propEl.setAttribute("name", (String) entry.getKey()); //$NON-NLS-1$
				propEl.setAttribute("value", (String) entry.getValue()); //$NON-NLS-1$
				opEl.appendChild(propEl);
			}

			for (Iterator<LaunchTransform> iter = transformDefs.iterator(); iter
					.hasNext();) {
				LaunchTransform tdef = iter.next();
				Element tdefEl = tdef.asXML(doc);
				rootEl.appendChild(tdefEl);
			}

			xml = PreferenceUtil.serializeDocument(doc);
		} catch (DOMException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					LaunchingPlugin.PLUGIN_ID, 0, e.getMessage(), e));
		} catch (ParserConfigurationException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					LaunchingPlugin.PLUGIN_ID, 0, e.getMessage(), e));
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					LaunchingPlugin.PLUGIN_ID, 0, e.getMessage(), e));
		} catch (TransformerException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					LaunchingPlugin.PLUGIN_ID, 0, e.getMessage(), e));
		}
		return xml;
	}

	public static LaunchPipeline fromXML(InputStream inputStream)
			throws CoreException {
		Document doc = PreferenceUtil.getDocument(inputStream);

		LaunchPipeline pdef = new LaunchPipeline();

		Element rootEl = doc.getDocumentElement();

		Element opEl = (Element) rootEl
				.getElementsByTagName("OutputProperties").item(0); //$NON-NLS-1$
		if (opEl != null) {
			NodeList propEls = opEl.getElementsByTagName("Property"); //$NON-NLS-1$
			for (int i = 0; i < propEls.getLength(); i++) {
				Element propEl = (Element) propEls.item(i);
				String name = propEl.getAttribute("name"); //$NON-NLS-1$
				String value = propEl.getAttribute("value"); //$NON-NLS-1$
				pdef.addOutputProperty(name, value);
			}
		}

		NodeList transformEls = rootEl.getElementsByTagName("Transform"); //$NON-NLS-1$
		for (int i = 0; i < transformEls.getLength(); i++) {
			Element transformEl = (Element) transformEls.item(i);
			LaunchTransform tdef = LaunchTransform.fromXML(transformEl);
			pdef.addTransformDef(tdef);
		}

		return pdef;
	}
}
