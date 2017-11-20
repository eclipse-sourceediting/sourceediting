/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - bug 261588 - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.xpath.ui.internal.views;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import org.eclipse.wst.xml.core.internal.contentmodel.util.NamespaceInfo;

/**
 * Provides a jaxp NamespaceContext information based on information
 * from the NamespaceInfo class.
 * @author dcarver
 *
 */
public class DefaultNamespaceContext implements NamespaceContext {

	List<NamespaceInfo> nsinfoList;
	public DefaultNamespaceContext(List<NamespaceInfo> namespaceInfo) {
		nsinfoList = namespaceInfo;
	}
	
	public String getNamespaceURI(String prefix) {
		for (NamespaceInfo ni : nsinfoList) {
			if (prefix.equals(ni.prefix)) {
				return ni.uri;
			}
		}
		return null;
	}

	public String getPrefix(String uri) {
		for (NamespaceInfo ni : nsinfoList) {
			if (uri.equals(ni.uri)) {
				return ni.prefix;
			}
		}
		return null;
	}

	public Iterator<String> getPrefixes(String uri) {
		List<String> prefixes = new LinkedList<String>();
		for (NamespaceInfo ni : nsinfoList) {
			if (uri.equals(ni.uri)) {
				prefixes.add(ni.prefix);
			}
		}
		return prefixes.iterator();
	}
}