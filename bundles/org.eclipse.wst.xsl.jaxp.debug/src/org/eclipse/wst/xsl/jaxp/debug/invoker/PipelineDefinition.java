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
package org.eclipse.wst.xsl.jaxp.debug.invoker;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.URIResolver;

import org.eclipse.wst.xsl.jaxp.debug.invoker.internal.ConfigurationException;
import org.eclipse.wst.xsl.jaxp.debug.invoker.internal.CreationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The definition of the transformation pipeline.
 * <p>
 * This class is loaded on both the Eclipse classpath and the transformation
 * process's classpath. The whole definition is serialized to an XML document by
 * the Eclipse launcher, and is then read by the transformation process when
 * launched.
 * </p>
 * 
 * @author Doug Satchwell
 */
public class PipelineDefinition {
	private String sourceURL;
	private String targetFile;

	private final List transformDefs = new ArrayList();
	private final Set attributes = new HashSet();
	private boolean useEmbedded;

	/**
	 * Create a new empty instance of this.
	 */
	public PipelineDefinition() {
	}

	/**
	 * Create a new instance of this by reading the specified XML file.
	 * 
	 * @param launchFile
	 *            the XSL file to load
	 * @throws SAXException
	 *             if problems occur during parsing
	 * @throws IOException
	 *             if problems occur during parsing
	 * @throws ParserConfigurationException
	 *             if problems occur during parsing
	 */
	public PipelineDefinition(File launchFile) throws SAXException,
			IOException, ParserConfigurationException {
		this(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
				new BufferedInputStream(new FileInputStream(launchFile))));
	}

	/**
	 * Create a new instance of this from the specified document
	 * 
	 * @param doc
	 *            the Document to create this from
	 */
	public PipelineDefinition(Document doc) {
		Element rootEl = doc.getDocumentElement();

		Element attributesEl = (Element) rootEl.getElementsByTagName(
				"Attributes").item(0); //$NON-NLS-1$
		NodeList attributeEls = attributesEl.getElementsByTagName("Attribute"); //$NON-NLS-1$
		for (int i = 0; i < attributeEls.getLength(); i++) {
			Element attributeEl = (Element) attributeEls.item(i);
			String name = attributeEl.getAttribute("name"); //$NON-NLS-1$
			String type = attributeEl.getAttribute("type"); //$NON-NLS-1$
			String value = attributeEl.getAttribute("value"); //$NON-NLS-1$
			addAttribute(new TypedValue(name, type, value));
		}

		Element transformsEl = (Element) rootEl.getElementsByTagName(
				"Transforms").item(0); //$NON-NLS-1$
		String useEmbedded = transformsEl.getAttribute("useEmbedded"); //$NON-NLS-1$
		boolean embedded = Boolean.getBoolean(useEmbedded);
		setUseEmbedded(embedded);
		if (!embedded) {
			NodeList transformEls = transformsEl
					.getElementsByTagName("Transform"); //$NON-NLS-1$
			for (int i = 0; i < transformEls.getLength(); i++) {
				Element transformEl = (Element) transformEls.item(i);
				TransformDefinition tdef = TransformDefinition
						.fromXML(transformEl);
				addTransformDef(tdef);
			}
		}
	}

	/**
	 * Configure the invoker from this.
	 * 
	 * @param invoker
	 *            the invoker to configure
	 * @throws ConfigurationException
	 *             if an exception occurs during configuration
	 */
	public void configure(IProcessorInvoker invoker)
			throws ConfigurationException {
		Map attVals = new ConcurrentHashMap();
		for (Iterator iter = attributes.iterator(); iter.hasNext();) {
			TypedValue att = (TypedValue) iter.next();
			Object value;
			try {
				value = att.createValue();
			} catch (CreationException e) {
				throw new ConfigurationException(e.getMessage(), e);
			}
			attVals.put(att.name, value);
		}
		invoker.setAttributes(attVals);

		for (Iterator iter = transformDefs.iterator(); iter.hasNext();) {
			TransformDefinition tdef = (TransformDefinition) iter.next();
			Map params = null;
			try {
				params = tdef.getParametersAsMap();
			} catch (CreationException e) {
				throw new ConfigurationException(Messages
						.getString("PipelineDefinition.8"), e); //$NON-NLS-1$
			}
			URL url = null;
			try {
				url = new URL(tdef.getStylesheetURL());
			} catch (MalformedURLException e) {
				throw new ConfigurationException(
						Messages.getString("PipelineDefinition.9") + tdef.getStylesheetURL(), e); //$NON-NLS-1$
			}
			Properties properties = tdef.getOutputProperties();
			URIResolver resolver = null;

//			if (tdef.getResolverClass() != null) {
//				try {
//					resolver = (URIResolver) Class.forName(
//							tdef.getResolverClass()).newInstance();
//				} catch (InstantiationException e) {
//					throw new ConfigurationException(
//							Messages.getString("PipelineDefinition.10") + tdef.getResolverClass(), null); //$NON-NLS-1$
//				} catch (IllegalAccessException e) {
//					throw new ConfigurationException(
//							Messages.getString("PipelineDefinition.10") + tdef.getResolverClass(), null); //$NON-NLS-1$
//				} catch (ClassNotFoundException e) {
//					throw new ConfigurationException(
//							Messages.getString("PipelineDefinition.10") + tdef.getResolverClass(), null); //$NON-NLS-1$
//				}
//			}

			try {
				invoker.addStylesheet(url, params, properties, resolver);
			} catch (TransformerConfigurationException e) {
				throw new ConfigurationException(
						Messages.getString("PipelineDefinition.10") + tdef.getStylesheetURL(), null); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Get the set of attributes defined.
	 * 
	 * @return the set of attributes
	 */
	public Set getAttributes() {
		return attributes;
	}

	/**
	 * Add a attribute to this configuration
	 * 
	 * @param attribute
	 *            the attribute to add
	 */
	public void addAttribute(TypedValue attribute) {
		attributes.add(attribute);
	}

	/**
	 * Remove a attribute from the set of attributes
	 * 
	 * @param attribute
	 *            the attribute to remove
	 */
	public void removeAttribute(TypedValue attribute) {
		attributes.remove(attribute);
	}

	/**
	 * Get the list of <code>TransformDefinition</code>'s.
	 * 
	 * @return the list of transform definitions
	 */
	public List getTransformDefs() {
		return transformDefs;
	}

	/**
	 * Add a transform definition to this.
	 * 
	 * @param tdef
	 *            the transform definition to add
	 */
	public void addTransformDef(TransformDefinition tdef) {
		transformDefs.add(tdef);
	}

	/**
	 * Remove a transform definition from this.
	 * 
	 * @param tdef
	 *            the transform definition to remove
	 */
	public void removeTransformDef(TransformDefinition tdef) {
		transformDefs.remove(tdef);
	}

	/**
	 * Set whether to use an XSL declaration embedded in the XML file
	 * 
	 * @param embedded
	 *            true to use embedded
	 */
	public void setUseEmbedded(boolean embedded) {
		useEmbedded = embedded;
	}

	/**
	 * Get whether to use an XSL declaration embedded in the XML file
	 * 
	 * @return true if embedded
	 */
	public boolean useEmbedded() {
		return useEmbedded;
	}

	/**
	 * Serialize this to a DOM Document.
	 * 
	 * @return the serialized document
	 * @throws ParserConfigurationException
	 *             if a problem occurs during serialization
	 */
	public Document toXML() throws ParserConfigurationException {
		Document doc = newDocument();
		Element rootEl = doc.createElement("Pipeline"); //$NON-NLS-1$
		rootEl.setAttribute("source", sourceURL); //$NON-NLS-1$
		rootEl.setAttribute("target", targetFile); //$NON-NLS-1$
		doc.appendChild(rootEl);

		Element attributesEl = doc.createElement("Attributes"); //$NON-NLS-1$
		rootEl.appendChild(attributesEl);
		for (Iterator iter = attributes.iterator(); iter.hasNext();) {
			TypedValue attribute = (TypedValue) iter.next();
			Element attributeEl = doc.createElement("Attribute"); //$NON-NLS-1$
			attributeEl.setAttribute("name", attribute.name); //$NON-NLS-1$
			attributeEl.setAttribute("type", attribute.type); //$NON-NLS-1$
			attributeEl.setAttribute("value", attribute.value); //$NON-NLS-1$
			attributesEl.appendChild(attributeEl);
		}

		rootEl.setAttribute("useEmbedded", String.valueOf(useEmbedded)); //$NON-NLS-1$
		if (!useEmbedded) {
			Element transformsEl = doc.createElement("Transforms"); //$NON-NLS-1$
			rootEl.appendChild(transformsEl);
			for (Iterator iter = transformDefs.iterator(); iter.hasNext();) {
				TransformDefinition tdef = (TransformDefinition) iter.next();
				Element tdefEl = tdef.asXML(doc);
				transformsEl.appendChild(tdefEl);
			}
		}

		return doc;
	}

	private static Document newDocument() throws ParserConfigurationException {
		DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		return doc;
	}
}
