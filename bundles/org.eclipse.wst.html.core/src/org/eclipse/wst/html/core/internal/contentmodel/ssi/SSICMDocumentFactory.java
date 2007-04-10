/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.contentmodel.ssi;

import java.util.Hashtable;
import java.util.Iterator;

import org.eclipse.wst.html.core.internal.contentmodel.HTMLCMDocumentFactory;
import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamespace;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.provisional.contentmodel.CMDocType;

/**
 * CMDocument factory for SSI documents.
 */
public final class SSICMDocumentFactory {

	private final static String PREFIX = "SSI";//$NON-NLS-1$
	private final static String DOC_TYPE_NAME = "SSI";//$NON-NLS-1$

	static class CMNamespaceImpl implements CMNamespace {
		public CMNamespaceImpl() {
			super();
		}

		public String getPrefix() {
			return PREFIX;
		}

		public String getURI() {
			return ""; //$NON-NLS-1$
		}

		public String getNodeName() {
			return DOC_TYPE_NAME;
		}

		public int getNodeType() {
			return CMNode.NAME_SPACE;
		}

		public boolean supports(String propertyName) {
			return false;
		}

		public Object getProperty(String propertyName) {
			return null;
		}

	}

	static class CMDocImpl implements CMDocument {
		private static CMDocument hcm = HTMLCMDocumentFactory.getCMDocument(CMDocType.HTML_DOC_TYPE);

		static class Elements implements CMNamedNodeMap {
			private static String[] names = {HTML40Namespace.ElementName.SSI_CONFIG, HTML40Namespace.ElementName.SSI_ECHO, HTML40Namespace.ElementName.SSI_EXEC, HTML40Namespace.ElementName.SSI_FSIZE, HTML40Namespace.ElementName.SSI_FLASTMOD, HTML40Namespace.ElementName.SSI_INCLUDE, HTML40Namespace.ElementName.SSI_PRINTENV, HTML40Namespace.ElementName.SSI_SET};
			private Hashtable map = new Hashtable();

			public Elements() {
				CMNamedNodeMap elems = hcm.getElements();
				for (int i = 0; i < names.length; i++) {
					String name = names[i];
					CMElementDeclaration dec = (CMElementDeclaration) elems.getNamedItem(name);
					if (dec != null)
						map.put(name, dec);
				}
			}

			public int getLength() {
				return map.size();
			}

			public CMNode getNamedItem(String name) {
				String cooked = getCanonicalName(name);
				if (!map.containsKey(cooked))
					return null;
				return (CMNode) map.get(cooked);
			}

			public CMNode item(int index) {
				Iterator iter = iterator();
				while (iter.hasNext()) {
					Object node = iter.next();
					if (--index < 0)
						return (CMNode) node;
				}
				return null;
			}

			public Iterator iterator() {
				return map.values().iterator();
			}

			private String getCanonicalName(String rawName) {
				return rawName.toUpperCase();
			}
		}

		static private Elements elements = new Elements();


		public CMDocImpl() {
			super();
		}

		public String getNodeName() {
			return ""; //$NON-NLS-1$
		}

		public int getNodeType() {
			return CMNode.DOCUMENT;
		}

		public CMNamedNodeMap getElements() {
			return elements;
		}

		public CMNamedNodeMap getEntities() {
			return null;
		}

		public CMNamespace getNamespace() {
			return ssins;
		}

		public Object getProperty(String propertyName) {
			return null;
		}

		public boolean supports(String propertyName) {
			return false;
		}
	}

	private static CMNamespace ssins = new CMNamespaceImpl();
	private static CMDocument mycm = new CMDocImpl();

	private SSICMDocumentFactory() {
		super();
	}

	public static CMDocument getCMDocument() {
		return mycm;
	}

}
