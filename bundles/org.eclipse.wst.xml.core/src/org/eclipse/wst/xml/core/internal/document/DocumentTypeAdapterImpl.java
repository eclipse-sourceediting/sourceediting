/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.document;



import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.xml.core.document.XMLDocument;
import org.eclipse.wst.xml.core.document.XMLModel;
import org.w3c.dom.DocumentType;


/**
 */
public class DocumentTypeAdapterImpl implements DocumentTypeAdapter {

	private XMLDocument document = null;
	private DocumentType documentType = null;

	/**
	 */
	protected DocumentTypeAdapterImpl() {
		super();
	}

	/**
	 */
	protected DocumentTypeAdapterImpl(XMLDocument document) {
		this.document = document;
		if (document != null) {
			this.documentType = document.getDoctype();
		}
	}

	/**
	 */
	public int getAttrNameCase() {
		return STRICT_CASE;
	}

	/**
	 */
	protected XMLDocument getDocument() {
		return this.document;
	}

	/**
	 */
	public DocumentType getDocumentType() {
		return this.documentType;
	}

	/**
	 */
	public int getTagNameCase() {
		return STRICT_CASE;
	}

	/**
	 */
	public boolean hasFeature(String feature) {
		return false;
	}

	/**
	 */
	public boolean isAdapterForType(Object type) {
		return (type == DocumentTypeAdapter.class);
	}

	/**
	 */
	public boolean isXMLType() {
		return true;
	}

	/**
	 */
	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
		if (eventType != INodeNotifier.STRUCTURE_CHANGED)
			return;
		if (notifier == null || !(notifier instanceof XMLDocument))
			return;
		this.documentType = ((XMLDocument) notifier).getDoctype();
	}

	/**
	 */
	protected void notifyDocumentTypeChanged() {
		if (this.document == null)
			return;
		XMLModel model = this.document.getModel();
		if (model == null)
			return;
		((XMLModelImpl) model).documentTypeChanged();
	}

	/**
	 */
	public void release() {
		// nothing to do
	}

	/**
	 */
	protected void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}
}
