/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.document;



import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.ui.contentproperties.ContentSettings;
import org.eclipse.wst.sse.ui.contentproperties.ContentSettingsChangeSubject;
import org.eclipse.wst.sse.ui.contentproperties.IContentSettings;
import org.eclipse.wst.sse.ui.contentproperties.IContentSettingsListener;
import org.eclipse.wst.xml.core.document.DOMDocument;
import org.eclipse.wst.xml.core.document.DOMDocumentType;
import org.eclipse.wst.xml.core.document.DOMModel;
import org.eclipse.wst.xml.core.internal.document.DocumentTypeAdapter;
import org.eclipse.wst.xml.core.internal.document.DocumentTypeAdapterImpl;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;

/**
 */
public class HTMLDocumentTypeAdapter extends DocumentTypeAdapterImpl implements IContentSettingsListener, HTMLDocumentTypeConstants {

	private HTMLDocumentTypeAdapterFactory factory = null;
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
	protected HTMLDocumentTypeAdapter(DOMDocument document, HTMLDocumentTypeAdapterFactory factory) {
		super(document);

		this.factory = factory;

		// initialize
		documentTypeChanged();

		ContentSettingsChangeSubject.getSubject().addListener(this);
	}

	/**
	 */
	public void contentSettingsChanged(IResource resource) {
		if (resource == null)
			return;
		DOMDocument document = getDocument();
		if (document == null)
			return;
		DOMModel model = document.getModel();
		if (model == null)
			return;
		IFile file = getFile(model);
		if (file == null)
			return;
		IProject project = file.getProject();
		if (project == null)
			return;
		if (!project.equals(resource.getProject()))
			return;
		documentTypeChanged();
	}

	/**
	 */
	private void documentTypeChanged() {
		DOMDocument document = getDocument();
		if (document == null)
			return; // error
		DOMModel model = document.getModel();
		if (model == null)
			return; // error

		IFile file = getFile(model);

		// find DOCTYPE delcaration and Public ID
		String publicId = null;
		DocumentType newDocumentType = findDocumentType(document);
		if (newDocumentType != null) {
			publicId = newDocumentType.getPublicId();
		} else {
			// lookup default set by contentsettings
			publicId = getDefaultPublicId(file);
		}

		// lookup DOCTYPE registry
		HTMLDocumentTypeEntry newEntry = null;
		if (publicId != null) {
			newEntry = HTMLDocumentTypeRegistry.getInstance().getEntry(publicId);
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

			} else {
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
					String systemId = newEntry.getSystemId();
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
	private DOMDocumentType findDocumentType(DOMDocument document) {
		DOMDocumentType documentType = (DOMDocumentType) document.getDoctype();
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
			return LOWER_CASE; // XHTML
		return this.factory.getAttrNameCase();
	}

	/**
	 */
	private String getDefaultPublicId(IFile file) {
		if (file == null)
			return null;
		IProject project = file.getProject();
		if (project == null)
			return null;
		IContentSettings settings = ContentSettings.getInstance();
		if (settings == null)
			return null;
		String publicId = settings.getProperty(file, IContentSettings.HTML_DOCUMENT_TYPE);
		if (publicId == null || publicId.length() == 0) {
			// look up project default
			publicId = settings.getProperty(project, IContentSettings.HTML_DOCUMENT_TYPE);
		}
		return publicId;
	}

	private IFile getFile(IStructuredModel model) {
		IFile result = null;
		String location = model.getBaseLocation();
		if (location != null) {
			IPath path = new Path(location);
			if (!path.toFile().exists() && path.segmentCount() > 1) {
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
		return this.factory.getTagNameCase();
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
		if (notifier instanceof DOMDocument) {
			if (eventType != INodeNotifier.STRUCTURE_CHANGED)
				return;
		} else {
			if (eventType != INodeNotifier.CHANGE)
				return;
		}
		documentTypeChanged();
	}

	/**
	 */
	public void release() {
		ContentSettingsChangeSubject.getSubject().removeListener(this);
		super.release();
	}
}