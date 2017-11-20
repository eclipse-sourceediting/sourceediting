/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.document;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.html.core.internal.cleanup.HTMLCleanupProcessorImpl;
import org.eclipse.wst.html.core.internal.preferences.HTMLCorePreferenceNames;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.cleanup.IStructuredCleanupPreferences;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocumentType;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/**
 */
public class HTMLConverter {

	/**
	 */
	public HTMLConverter() {
		super();

	}

	public void cleanupModel(IDOMModel model) {
		if (model == null)
			return;

		HTMLCleanupProcessorImpl processor = new HTMLCleanupProcessorImpl();
		IStructuredCleanupPreferences pref = processor.getCleanupPreferences();

		// backup options
		boolean compressEmptyElementTags = pref.getCompressEmptyElementTags();
		boolean insertRequiredAttrs = pref.getInsertRequiredAttrs();
		boolean insertMissingTags = pref.getInsertMissingTags();
		boolean quoteAttrValues = pref.getQuoteAttrValues();
		boolean formatSource = pref.getFormatSource();
		int tagNameCase = pref.getTagNameCase();
		int attrNameCase = pref.getAttrNameCase();

		// setup options
		pref.setCompressEmptyElementTags(true);
		pref.setInsertRequiredAttrs(true);
		pref.setInsertMissingTags(true);
		pref.setQuoteAttrValues(true);
		pref.setFormatSource(false);
		if (model.getDocument().isXMLType()) { // XHTML
			pref.setTagNameCase(HTMLCorePreferenceNames.LOWER);
			pref.setAttrNameCase(HTMLCorePreferenceNames.LOWER);
		}
		else {
			pref.setTagNameCase(HTMLCorePreferenceNames.ASIS);
			pref.setAttrNameCase(HTMLCorePreferenceNames.ASIS);
		}

		processor.cleanupModel(model);

		// set back options
		pref.setCompressEmptyElementTags(compressEmptyElementTags);
		pref.setInsertRequiredAttrs(insertRequiredAttrs);
		pref.setInsertMissingTags(insertMissingTags);
		pref.setQuoteAttrValues(quoteAttrValues);
		pref.setFormatSource(formatSource);
		pref.setTagNameCase(tagNameCase);
		pref.setAttrNameCase(attrNameCase);
	}

	/**
	 * declaratoin: "data" for XML declaration, such as "version=\"1.0\""
	 * publicId: publicId for DOCTYPE declaration
	 */
	public void convert(IDOMModel model, String declaration, String publicId) {
		if (model == null)
			return;
		setDeclaration(model, declaration, publicId);
		cleanupModel(model);
	}

	/**
	 * declaratoin: "data" for XML declaration, such as "version=\"1.0\""
	 * publicId: publicId for DOCTYPE declaration
	 */
	public void convert(InputStream input, OutputStream output, String declaration, String publicId) throws UnsupportedEncodingException, IOException, CoreException {
		IDOMModel model = readModel(input);
		if (model == null)
			return;
		try {
			convert(model, declaration, publicId);
			writeModel(model, output);
		}
		finally {
			if (model != null)
				model.releaseFromEdit();
		}
	}

	/**
	 * declaratoin: "data" for XML declaration, such as "version=\"1.0\""
	 * publicId: publicId for DOCTYPE declaration
	 */
	public void convert(IFile file, String declaration, String publicId) throws IOException, CoreException {
		IDOMModel model = readModel(file);
		if (model == null)
			return;
		try {
			convert(model, declaration, publicId);
			writeModel(model, file);
		}
		finally {
			if (model != null)
				model.releaseFromEdit();
		}
	}

	/**
	 */
	private static void insertBreak(IDOMModel model, Node node) {
		if (model == null || node == null)
			return;
		if (node.getNodeType() == Node.TEXT_NODE)
			return;

		// get delimiter string
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		if (structuredDocument == null)
			return;
		String delim = structuredDocument.getLineDelimiter();
		if (delim == null || delim.length() == 0)
			return;

		Node parent = node.getParentNode();
		if (parent == null)
			return;
		Document document = node.getOwnerDocument();
		if (document == null)
			return;
		Text text = document.createTextNode(delim);
		parent.insertBefore(text, node);
	}

	/**
	 */
	private IDOMModel readModel(InputStream input) throws IOException, UnsupportedEncodingException {
		if (input == null)
			return null;
		// create temporary model
		String id = input.toString() + ".html"; //$NON-NLS-1$
		IModelManager manager = StructuredModelManager.getModelManager();
		IStructuredModel model = manager.getModelForEdit(id, input, null);
		if (!(model instanceof IDOMModel)) {
			if (model != null)
				model.releaseFromEdit();
			return null;
		}
		return (IDOMModel) model;
	}

	/**
	 */
	private IDOMModel readModel(IFile file) throws IOException, CoreException {
		if (file == null)
			return null;
		IModelManager manager = StructuredModelManager.getModelManager();
		IStructuredModel model = manager.getModelForEdit(file);
		if (!(model instanceof IDOMModel)) {
			if (model != null)
				model.releaseFromEdit();
			return null;
		}
		return (IDOMModel) model;
	}

	/**
	 */
	public void setDeclaration(IDOMModel model, String declaration, String publicId) {
		if (model == null)
			return;
		IDOMDocument document = model.getDocument();
		if (document == null)
			return;

		try {
			model.aboutToChangeModel();

			ProcessingInstruction pi = null;
			Node child = document.getFirstChild();
			if (child != null && child.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
				String target = child.getNodeName();
				if (target != null && target.equals("xml")) { //$NON-NLS-1$
					pi = (ProcessingInstruction) child;
					child = child.getNextSibling();
				}
			}
			IDOMDocumentType docType = (IDOMDocumentType) document.getDoctype();

			if (declaration != null) {
				if (pi != null) {
					pi.setData(declaration);
				}
				else {
					pi = document.createProcessingInstruction("xml", declaration); //$NON-NLS-1$
					document.insertBefore(pi, child);
					insertBreak(model, child);
				}
			}

			if (publicId != null) {
				HTMLDocumentTypeEntry entry = HTMLDocumentTypeRegistry.getInstance().getEntry(publicId);
				String name = (entry != null ? entry.getName() : null);
				if (name == null || name.length() == 0)
					name = "HTML"; // default //$NON-NLS-1$
				if (docType != null) {
					if (!name.equals(docType.getName())) { // replace
						Node parent = docType.getParentNode();
						child = docType;
						docType = (IDOMDocumentType) document.createDoctype(name);
						parent.insertBefore(docType, child);
						parent.removeChild(child);
					}
				}
				else {
					docType = (IDOMDocumentType) document.createDoctype(name);
					document.insertBefore(docType, child);
					insertBreak(model, child);
				}
				docType.setPublicId(publicId);
				if (entry != null) {
					String systemId = entry.getSystemId();
					if (systemId != null)
						docType.setSystemId(systemId);
					String namespaceURI = entry.getNamespaceURI();
					if (namespaceURI != null) {
						Element element = document.getDocumentElement();
						if (element != null) {
							element.setAttribute("xmlns", namespaceURI); //$NON-NLS-1$
						}
					}
				}
			}
		}
		finally {
			model.changedModel();
		}
	}

	/**
	 */
	private void writeModel(IDOMModel model, OutputStream output) throws UnsupportedEncodingException, IOException, CoreException {
		if (model == null || output == null)
			return;
		model.save();
	}

	/**
	 */
	private void writeModel(IDOMModel model, IFile file) throws IOException, CoreException {
		if (model == null || file == null)
			return;
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		if (structuredDocument == null)
			return;
		//ByteArrayOutputStream output = null;
		ByteArrayInputStream input = null;
		try {
			//output = new
			// ByteArrayOutputStream(structuredDocument.getLength());
			model.save();
			//input = new ByteArrayInputStream(output.toByteArray());
			//file.setContents(input, true, true, null);
		}
		finally {
			//			if (output != null)
			//				output.close();
			if (input != null)
				input.close();
		}

	}
}
