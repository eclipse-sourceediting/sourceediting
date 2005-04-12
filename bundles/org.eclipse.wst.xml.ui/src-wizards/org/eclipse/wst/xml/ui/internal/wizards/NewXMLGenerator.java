/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.ui.internal.wizards;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.util.Assert;
import org.eclipse.wst.sse.core.internal.encoding.CommonEncodingPreferenceNames;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.ContentModelManager;
import org.eclipse.wst.xml.core.internal.contentmodel.util.ContentBuilder;
import org.eclipse.wst.xml.core.internal.contentmodel.util.DOMContentBuilderImpl;
import org.eclipse.wst.xml.core.internal.contentmodel.util.DOMWriter;
import org.eclipse.wst.xml.core.internal.contentmodel.util.NamespaceInfo;
import org.eclipse.wst.xml.uriresolver.XMLCatalog;
import org.eclipse.wst.xml.uriresolver.XMLCatalogEntry;
import org.eclipse.wst.xml.uriresolver.XMLCatalogPlugin;
import org.eclipse.wst.xml.uriresolver.util.IdResolver;
import org.eclipse.wst.xml.uriresolver.util.IdResolverImpl;
import org.eclipse.wst.xml.uriresolver.util.URIHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class NewXMLGenerator {

	protected String grammarURI;
	protected CMDocument cmDocument;
	protected int buildPolicy;
	protected String rootElementName;

	protected XMLCatalogEntry xmlCatalogEntry;

	// info for dtd
	protected String publicId;
	protected String systemId;
	protected String defaultSystemId;

	// info for xsd
	public List namespaceInfoList;

	public NewXMLGenerator() {
	}

	public NewXMLGenerator(String grammarURI, CMDocument cmDocument) {
		this.grammarURI = grammarURI;
		this.cmDocument = cmDocument;
	}


	public static CMDocument createCMDocument(String uri, String[] errorInfo) {
		String title = null;
		String message = null;
		List errorList = new Vector();
		CMDocument cmDocument = null;

		if (URIHelper.isReadableURI(uri, true)) {
			uri = URIHelper.normalize(uri, null, null);
			cmDocument = ContentModelManager.getInstance().createCMDocument(uri, null);

			if (uri.endsWith(".dtd")) { //$NON-NLS-1$
				if (errorList.size() > 0) {
					title = XMLWizardsMessages._UI_INVALID_GRAMMAR_ERROR;
					message = XMLWizardsMessages._UI_LABEL_ERROR_DTD_INVALID_INFO;
				}
			}
			else // ".xsd"
			{
				// To be consistent with the schema editor validation
				XMLSchemaValidationChecker validator = new XMLSchemaValidationChecker();
				if (!validator.isValid(uri)) {
					title = XMLWizardsMessages._UI_INVALID_GRAMMAR_ERROR;
					message = XMLWizardsMessages._UI_LABEL_ERROR_SCHEMA_INVALID_INFO;
				}
				else if (cmDocument != null) {
					int globalElementCount = cmDocument.getElements().getLength();
					if (globalElementCount == 0) {
						title = XMLWizardsMessages._UI_WARNING_TITLE_NO_ROOT_ELEMENTS;
						message = XMLWizardsMessages._UI_WARNING_MSG_NO_ROOT_ELEMENTS;
					}
				}
			}
		}
		else {
			title = XMLWizardsMessages._UI_WARNING_TITLE_NO_ROOT_ELEMENTS;
			message = XMLWizardsMessages._UI_WARNING_URI_NOT_FOUND_COLON + " " + uri; //$NON-NLS-1$
		}
		errorInfo[0] = title;
		errorInfo[1] = message;

		return cmDocument;
	}


	public void createEmptyXMLDocument(IFile newFile) throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Preferences preference = XMLCorePlugin.getDefault().getPluginPreferences();
		String charSet = preference.getString(CommonEncodingPreferenceNames.OUTPUT_CODESET);

		PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, charSet));
		writer.println("<?xml version=\"1.0\" encoding=\"" + charSet + "\"?>"); //$NON-NLS-1$ //$NON-NLS-2$
		writer.flush();
		outputStream.close();

		ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		newFile.setContents(inputStream, true, true, null);
		inputStream.close();
	}

	public void createXMLDocument(String xmlFileName) throws Exception {
		ByteArrayOutputStream outputStream = createXMLDocument(xmlFileName, false);

		File file = new File(xmlFileName);
		FileOutputStream fos = new FileOutputStream(file);
		outputStream.writeTo(fos);
		fos.close();
	}


	public void createXMLDocument(IFile newFile, String xmlFileName) throws Exception {
		ByteArrayOutputStream outputStream = createXMLDocument(xmlFileName, false);

		ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		newFile.setContents(inputStream, true, true, null);
		inputStream.close();
	}


	public ByteArrayOutputStream createXMLDocument(String xmlFileName, boolean junk) throws Exception {
		CMDocument cmDocument = getCMDocument();

		Assert.isNotNull(cmDocument);
		Assert.isNotNull(getRootElementName());

		// create the xml model
		CMNamedNodeMap nameNodeMap = cmDocument.getElements();
		CMElementDeclaration cmElementDeclaration = (CMElementDeclaration) nameNodeMap.getNamedItem(getRootElementName());

		Document xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		DOMContentBuilderImpl contentBuilder = new DOMContentBuilderImpl(xmlDocument);

		contentBuilder.setBuildPolicy(buildPolicy);
		contentBuilder.setExternalCMDocumentSupport(new MyExternalCMDocumentSupport(namespaceInfoList, xmlFileName));
		contentBuilder.uglyTempHack = true; // todo... this line should be
											// removed when 169191 is fixed
		contentBuilder.createDefaultRootContent(cmDocument, cmElementDeclaration, namespaceInfoList);

		String[] encodingInfo = (String[]) cmDocument.getProperty("encodingInfo"); //$NON-NLS-1$
		if (encodingInfo == null) {
			encodingInfo = new String[2];
		}

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		OutputStreamWriter outputStreamWriter = encodingInfo[0] != null ? new OutputStreamWriter(outputStream, encodingInfo[1]) : new OutputStreamWriter(outputStream);

		DOMWriter domWriter = new DOMWriter(outputStreamWriter);
		domWriter.print(xmlDocument, encodingInfo[1], cmDocument.getNodeName(), getNonWhitespaceString(getPublicId()), getNonWhitespaceString(getSystemId())); // todo...
																																								// replace
																																								// with
																																								// domWriter.print(xmlDocument);
																																								// when
																																								// 169191
																																								// is
																																								// fixed
		outputStream.flush();
		outputStream.close();

		return outputStream;
	}


	public void createNamespaceInfoList() {
		List result = new Vector();
		XMLCatalog xmlCatalog = XMLCatalogPlugin.getInstance().getDefaultXMLCatalog();
		if (cmDocument != null) {
			result = (List) cmDocument.getProperty("http://org.eclipse.wst/cm/properties/namespaceInfo"); //$NON-NLS-1$
			if (result != null) {
				int size = result.size();
				for (int i = 0; i < size; i++) {
					NamespaceInfo info = (NamespaceInfo) result.get(i);
					if (i == 0) {
						String locationInfo = null;
						if (xmlCatalogEntry != null) {
							if (xmlCatalogEntry.getType() == XMLCatalogEntry.PUBLIC) {
								locationInfo = xmlCatalogEntry.getWebAddress();
							}
							else {
								locationInfo = xmlCatalogEntry.getKey();
							}
						}
						if (locationInfo == null) {
							locationInfo = defaultSystemId;
						}
						info.locationHint = locationInfo;
						info.setProperty("locationHint-readOnly", "true"); //$NON-NLS-1$ //$NON-NLS-2$
					}
					info.setProperty("uri-readOnly", "true"); //$NON-NLS-1$ //$NON-NLS-2$
					info.setProperty("unremovable", "true"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}

			NamespaceInfoContentBuilder builder = new NamespaceInfoContentBuilder();
			builder.setBuildPolicy(ContentBuilder.BUILD_ONLY_REQUIRED_CONTENT);
			builder.visitCMNode(cmDocument);
			result.addAll(builder.list);
		}
		namespaceInfoList = result;
	}


	public boolean isMissingNamespaceLocation() {
		boolean result = false;
		for (Iterator i = namespaceInfoList.iterator(); i.hasNext();) {
			NamespaceInfo info = (NamespaceInfo) i.next();
			if (info.locationHint == null) {
				result = true;
				break;
			}
		}
		return result;
	}


	public String[] getNamespaceInfoErrors() {
		String[] errorList = null;

		if (namespaceInfoList != null && isMissingNamespaceLocation()) {
			String title = XMLWizardsMessages._UI_LABEL_NO_LOCATION_HINT;
			String message = XMLWizardsMessages._UI_WARNING_MSG_NO_LOCATION_HINT_1 + " " + XMLWizardsMessages._UI_WARNING_MSG_NO_LOCATION_HINT_2 + "\n\n" + XMLWizardsMessages._UI_WARNING_MSG_NO_LOCATION_HINT_3; //$NON-NLS-1$ //$NON-NLS-2$

			errorList = new String[2];
			errorList[0] = title;
			errorList[1] = message;
		}

		return errorList;
	}


	public void setXMLCatalogEntry(XMLCatalogEntry catalogEntry) {
		xmlCatalogEntry = catalogEntry;
	}

	public XMLCatalogEntry getXMLCatalogEntry() {
		return xmlCatalogEntry;
	}


	public void setBuildPolicy(int policy) {
		buildPolicy = policy;
	}


	public void setDefaultSystemId(String sysId) {
		defaultSystemId = sysId;
	}

	public String getDefaultSystemId() {
		return defaultSystemId;
	}

	public void setSystemId(String sysId) {
		systemId = sysId;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setPublicId(String pubId) {
		publicId = pubId;
	}

	public String getPublicId() {
		return publicId;
	}

	public void setGrammarURI(String gramURI) {
		grammarURI = gramURI;
	}

	public String getGrammarURI() {
		return grammarURI;
	}

	public void setCMDocument(CMDocument cmDoc) {
		cmDocument = cmDoc;
	}

	public CMDocument getCMDocument() {
		return cmDocument;
	}

	public void setRootElementName(String rootName) {
		rootElementName = rootName;
	}

	public String getRootElementName() {
		return rootElementName;
	}


	protected class MyExternalCMDocumentSupport implements DOMContentBuilderImpl.ExternalCMDocumentSupport {
		protected List namespaceInfoList;
		protected IdResolver idResolver;

		protected MyExternalCMDocumentSupport(List namespaceInfoList, String resourceLocation) {
			this.namespaceInfoList = namespaceInfoList;
			idResolver = new IdResolverImpl(resourceLocation);
		}

		public CMDocument getCMDocument(Element element, String namespaceURI) {
			CMDocument result = null;
			if (namespaceURI != null && namespaceURI.trim().length() > 0) {
				String locationHint = null;
				for (Iterator i = namespaceInfoList.iterator(); i.hasNext();) {
					NamespaceInfo info = (NamespaceInfo) i.next();
					if (namespaceURI.equals(info.uri)) {
						locationHint = info.locationHint;
						break;
					}
				}
				if (locationHint != null) {
					grammarURI = idResolver.resolveId(locationHint, locationHint);
					result = ContentModelManager.getInstance().createCMDocument(getGrammarURI(), null);
				}
			}
			else {
				result = cmDocument;
			}
			return result;
		}
	}

	public static String getNonWhitespaceString(String string) {
		String result = null;
		if (string != null) {
			if (string.trim().length() > 0) {
				result = string;
			}
		}
		return result;
	}


}
