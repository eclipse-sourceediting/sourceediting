/*******************************************************************************
 * Copyright (c) 2004, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.document;



import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.html.core.internal.contentproperties.HTMLContentProperties;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.document.DocumentTypeAdapter;
import org.eclipse.wst.xml.core.internal.document.DocumentTypeAdapterImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocumentType;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;

/**
 */
public class HTMLDocumentTypeAdapter extends DocumentTypeAdapterImpl implements HTMLDocumentTypeConstants {

	private HTMLDocumentTypeAdapterFactory fFactory = null;
	private HTMLDocumentTypeEntry entry = null;
	private boolean isXMLType = false;
	private final static String XML = "xml"; //$NON-NLS-1$
	private final static String XHTML = "xhtml"; //$NON-NLS-1$
	private final static String WML = "wml"; //$NON-NLS-1$

	/**
	 */
	protected HTMLDocumentTypeAdapter() {
		super();
	}

	/**
	 */
	protected HTMLDocumentTypeAdapter(IDOMDocument document, HTMLDocumentTypeAdapterFactory factory) {
		super(document);

		this.fFactory = factory;

		// initialize
		documentTypeChanged();
	}

	/**
	 */
	private void documentTypeChanged() {
		IDOMDocument document = getDocument();
		if (document == null)
			return; // error
		IDOMModel model = document.getModel();
		if (model == null)
			return; // error

		IFile file = getFile(model);

		// find DOCTYPE delcaration and Public ID
		String publicId = null;
		String systemId = null;
		DocumentType newDocumentType = findDocumentType(document);
		if (newDocumentType != null) {
			publicId = newDocumentType.getPublicId();
			systemId = newDocumentType.getSystemId();
		}
		else {
			// lookup default set by contentsettings
			publicId = HTMLContentProperties.getProperty(HTMLContentProperties.DOCUMENT_TYPE, file, true);
		}

		// lookup DOCTYPE registry
		HTMLDocumentTypeEntry newEntry = null;
		if (publicId != null) {
			newEntry = HTMLDocumentTypeRegistry.getInstance().getEntry(publicId);
		}
		else if (systemId == null){
				newEntry = HTMLDocumentTypeRegistry.getInstance().getDefaultEntry(HTMLDocumentTypeRegistry.DEFAULT_HTML5);
		}

		boolean newXMLType = (newEntry != null ? newEntry.isXMLType() : false);
		boolean newWMLType = (newEntry != null ? newEntry.isWMLType() : false);

		if (!newXMLType) {
			// find XML declaration
			if (findXMLNode(document) != null) {
				newXMLType = true;
			}

			// check file extension
			if (file != null) {
				String ext = file.getFileExtension();
				if (ext != null && ext.equalsIgnoreCase(XHTML)) {
					newXMLType = true;
				}

				if (ext != null && ext.equalsIgnoreCase(WML)) {
					newXMLType = true;
					newWMLType = true;
				}
			}

		}

		if (newEntry == null) {
			// lookup system default
			if (newXMLType && newDocumentType == null) {
				// use default XHTML, if it's XML and no document type
				// declared
				if (newWMLType)
					newEntry = HTMLDocumentTypeRegistry.getInstance().getDefaultEntry(HTMLDocumentTypeRegistry.DEFAULT_WML);
				else
					newEntry = HTMLDocumentTypeRegistry.getInstance().getDefaultEntry(HTMLDocumentTypeRegistry.DEFAULT_XHTML);

			}
			else {
				newEntry = HTMLDocumentTypeRegistry.getInstance().getDefaultEntry(HTMLDocumentTypeRegistry.DEFAULT_HTML);
			}
			if (newEntry == null)
				return; // error
		}

		if (newDocumentType == null) {
			DocumentType oldDocumentType = getDocumentType();
			if (oldDocumentType == null || oldDocumentType.getName() != newEntry.getName()) {
				// create implicit DocumentType
				DOMImplementation impl = document.getImplementation();
				if (impl != null) {
					String name = newEntry.getName();
					publicId = newEntry.getPublicId();
					systemId = newEntry.getSystemId();
					newDocumentType = impl.createDocumentType(name, publicId, systemId);
				}
			}
		}

		boolean notify = false;
		if (this.entry != null) { // do not notify on initialization
			notify = (newEntry != this.entry || newXMLType != this.isXMLType);
		}

		if (newDocumentType != null)
			setDocumentType(newDocumentType);
		this.entry = newEntry;
		this.isXMLType = newXMLType;

		if (notify)
			notifyDocumentTypeChanged();
	}

	/**
	 */
	private IDOMDocumentType findDocumentType(IDOMDocument document) {
		IDOMDocumentType documentType = (IDOMDocumentType) document.getDoctype();
		if (documentType != null && documentType.getExistingAdapter(DocumentTypeAdapter.class) == null) {
			// watch future changes
			documentType.addAdapter(this);
		}
		return documentType;
	}

	/**
	 */
	private Node findXMLNode(Document document) {
		for (Node child = document.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() != Node.PROCESSING_INSTRUCTION_NODE)
				continue;
			String target = child.getNodeName();
			if (target != null && target.equals(XML)) {
				return child;
			}
		}
		return null;
	}

	/**
	 */
	public int getAttrNameCase() {
		if (isXMLType())
			return super.getAttrNameCase(); // XHTML
		return this.fFactory.getAttrNameCase();
	}

	private IFile getFile(IStructuredModel model) {
		IFile result = null;
		String location = model.getBaseLocation();
		if (location != null) {
			IPath path = new Path(location);
			if (path.segmentCount() > 1) {
				result = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			}
		}
		return result;
	}

	/**
	 */
	public int getTagNameCase() {
		if (isXMLType())
			return LOWER_CASE; // XHTML
		return this.fFactory.getTagNameCase();
	}

	/**
	 */
	public boolean hasFeature(String feature) {
		if (feature == null)
			return false;
		if (feature.equals(HTML))
			return true;
		if (feature.equals(SSI))
			return true;
		if (feature.equals(FRAMESET)) {
			if (this.entry == null)
				return false;
			return this.entry.hasFrameset();
		}
		if (feature.equals(HTML5)) {
			if (this.entry == null)
				return false;
			return this.entry == HTMLDocumentTypeRegistry.getInstance().getDefaultEntry(HTMLDocumentTypeRegistry.DEFAULT_HTML5);
		}
		return false;
	}

	/**
	 */
	public boolean isXMLType() {
		return this.isXMLType;
	}

	/**
	 */
	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
		if (notifier == null)
			return;
		if (notifier instanceof IDOMDocument) {
			if (eventType != INodeNotifier.STRUCTURE_CHANGED)
				return;
		}
		else {
			if (eventType != INodeNotifier.CHANGE)
				return;
		}
		documentTypeChanged();
	}

	/**
	 */
	public void release() {
		super.release();
	}
}
