/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
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



import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.DocumentType;


/**
 */
public class DocumentTypeAdapterImpl implements DocumentTypeAdapter {

	private IDOMDocument document = null;
	private DocumentType documentType = null;

	/**
	 */
	protected DocumentTypeAdapterImpl() {
		super();
	}

	/**
	 */
	protected DocumentTypeAdapterImpl(IDOMDocument document) {
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
	protected IDOMDocument getDocument() {
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
		if (notifier == null || !(notifier instanceof IDOMDocument))
			return;
		this.documentType = ((IDOMDocument) notifier).getDoctype();
	}

	/**
	 */
	protected void notifyDocumentTypeChanged() {
		if (this.document == null)
			return;
		IDOMModel model = this.document.getModel();
		if (model == null)
			return;
		((DOMModelImpl) model).documentTypeChanged();
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
