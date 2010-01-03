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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.wst.xsl.jaxp.debug.invoker.internal.CreationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A definition of a particular step in the transformation pipeline.
 * 
 * @author Doug Satchwell
 */
public class TransformDefinition {
	public static final String DEFAULT_CATALOG_RESOLVER = "org.apache.xml.resolver.tools.CatalogResolver"; //$NON-NLS-1$
	private String stylesheetURL;
	private String resolverClass;
	private Properties outputProperties = new Properties();
	private final Set<TypedValue> parameters = new HashSet<TypedValue>();
	
	public TransformDefinition() {
		this.resolverClass = DEFAULT_CATALOG_RESOLVER;
	}

	/**
	 * Get the parameters as a map of name (<code>String</code>) v. value
	 * <code>TypedValue</code>.
	 * 
	 * @return a map of names and values
	 * @throws CreationException
	 *             if an exception occurred during object creation
	 */
	public Map<String, Object> getParametersAsMap() throws CreationException {
		Map<String, Object> m = new ConcurrentHashMap<String, Object>();
		for (Iterator<TypedValue> iter = parameters.iterator(); iter.hasNext();) {
			TypedValue tv = iter.next();
			String key = tv.name;
			Object value = tv.createValue();
			m.put(key, value);
		}
		return m;
	}

	/**
	 * The set of parameters
	 * 
	 * @return a set of <code>TypedValue</code>'s
	 */
	public Set<TypedValue> getParameters() {
		return parameters;
	}

	/**
	 * Add a parameter to the set of parameters
	 * 
	 * @param parameter
	 *            the parameter to add
	 */
	public void addParameter(TypedValue parameter) {
		parameters.add(parameter);
	}

	/**
	 * Remove a parameter.
	 * 
	 * @param parameter
	 *            the parameter to remove
	 */
	public void removeParameter(TypedValue parameter) {
		parameters.remove(parameter);
	}

	/**
	 * Get the output properties for this.
	 * 
	 * @return the output properties
	 */
	public Properties getOutputProperties() {
		return outputProperties;
	}

	/**
	 * Set the output properties for this.
	 * 
	 * @param outputProperties
	 *            the output properties to set
	 */
	public void setOutputProperties(Properties outputProperties) {
		this.outputProperties = outputProperties;
	}

	/**
	 * Set the value of a specific output property.  If the property
	 * does not already exist, it will be created.
	 * 
	 * @param name
	 *            the output property
	 * @param value
	 *            the value
	 */
	public void setOutputProperty(String name, String value) {
		outputProperties.put(name, value);
	}

	/**
	 * Remove an output property.
	 * 
	 * @param name
	 *            the output property to remove
	 */
	public void removeOutputProperty(String name) {
		outputProperties.remove(name);
	}

	/**
	 * Get the name of the <code>URIResolver</code> class to use.
	 * 
	 * @return the resolver's class name
	 */
	public String getResolverClass() {
		return resolverClass;
	}

	/**
	 * Set the name of the <code>URIResolver</code> class to use.
	 * 
	 * @param resolver
	 *            the resolver's class name
	 */
	public void setResolverClass(String resolver) {
		resolverClass = resolver;
	}

	/**
	 * Get the URL of the stylesheet.
	 * 
	 * @return the stylesheet URL
	 */
	public String getStylesheetURL() {
		return stylesheetURL;
	}

	/**
	 * Set the URL of the stylesheet.
	 * 
	 * @param stylesheet
	 *            the stylesheet URL
	 */
	public void setStylesheetURL(String stylesheet) {
		stylesheetURL = stylesheet;
	}

	/**
	 * Serialize this to a Document fragment.
	 * 
	 * @param doc
	 *            the document to attach to
	 * @return the root element of the fragment
	 */
	public Element asXML(Document doc) {
		Element tdefEl = doc.createElement("Transform"); //$NON-NLS-1$
		tdefEl.setAttribute(
				Messages.getString("TransformDefinition.1"), stylesheetURL); //$NON-NLS-1$
		if (resolverClass != null)
			tdefEl.setAttribute("uriResolver", resolverClass); //$NON-NLS-1$
		Element opEl = doc.createElement("OutputProperties"); //$NON-NLS-1$
		tdefEl.appendChild(opEl);
		for (Iterator<?> iter = outputProperties.entrySet().iterator(); iter
				.hasNext();) {
			Map.Entry<?, ?> entry = (Map.Entry<?, ?>) iter.next();
			Element propEl = doc.createElement("Property"); //$NON-NLS-1$
			propEl.setAttribute("name", (String) entry.getKey()); //$NON-NLS-1$
			propEl.setAttribute("value", (String) entry.getValue()); //$NON-NLS-1$
			opEl.appendChild(propEl);
		}
		Element paramsEl = doc.createElement("Parameters"); //$NON-NLS-1$
		tdefEl.appendChild(paramsEl);
		for (Iterator<TypedValue> iter = parameters.iterator(); iter.hasNext();) {
			Element propEl = doc.createElement("Parameter"); //$NON-NLS-1$
			TypedValue param = iter.next();
			propEl.setAttribute("name", param.name); //$NON-NLS-1$
			propEl.setAttribute("type", param.type); //$NON-NLS-1$
			propEl.setAttribute("value", param.value); //$NON-NLS-1$
			paramsEl.appendChild(propEl);
		}
		return tdefEl;
	}

	/**
	 * Create a new instance of this from its serialized form.
	 * 
	 * @param transformEl
	 *            the element to create this from
	 * @return a new instance of this
	 */
	public static TransformDefinition fromXML(Element transformEl) {
		TransformDefinition tdef = new TransformDefinition();
		String url = transformEl.getAttribute("url"); //$NON-NLS-1$
		tdef.setStylesheetURL(url);
		String uriResolver = transformEl.getAttribute("uriResolver"); //$NON-NLS-1$
		tdef.setResolverClass(uriResolver);

		Element opEl = (Element) transformEl.getElementsByTagName(
				"OutputProperties").item(0); //$NON-NLS-1$
		if (opEl != null) {
			NodeList propEls = opEl.getElementsByTagName("Property"); //$NON-NLS-1$
			for (int i = 0; i < propEls.getLength(); i++) {
				Element propEl = (Element) propEls.item(i);
				String name = propEl.getAttribute("name"); //$NON-NLS-1$
				String value = propEl.getAttribute("value"); //$NON-NLS-1$
				tdef.setOutputProperty(name, value);
			}
		}

		Element paramsEl = (Element) transformEl.getElementsByTagName(
				Messages.getString("TransformDefinition.18")).item(0); //$NON-NLS-1$
		if (paramsEl != null) {
			NodeList paramEls = paramsEl.getElementsByTagName(Messages
					.getString("TransformDefinition.19")); //$NON-NLS-1$
			for (int i = 0; i < paramEls.getLength(); i++) {
				Element paramEl = (Element) paramEls.item(i);
				String name = paramEl.getAttribute(Messages
						.getString("TransformDefinition.20")); //$NON-NLS-1$
				String type = paramEl.getAttribute(Messages
						.getString("TransformDefinition.21")); //$NON-NLS-1$
				String value = paramEl.getAttribute(Messages
						.getString("TransformDefinition.22")); //$NON-NLS-1$
				tdef.addParameter(new TypedValue(name, type, value));
			}
		}

		return tdef;
	}
}
