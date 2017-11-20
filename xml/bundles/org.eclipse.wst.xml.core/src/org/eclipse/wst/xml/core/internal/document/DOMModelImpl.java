/*******************************************************************************
 * Copyright (c) 2001, 2011 IBM Corporation and others.
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

import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.model.AbstractStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.events.IStructuredDocumentListener;
import org.eclipse.wst.sse.core.internal.provisional.events.NewDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.NoChangeEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionChangedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentRegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegionList;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.xml.core.internal.Logger;
import org.eclipse.wst.xml.core.internal.provisional.IXMLNamespace;
import org.eclipse.wst.xml.core.internal.provisional.NameValidator;
import org.eclipse.wst.xml.core.internal.provisional.contenttype.ContentTypeIdForXML;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.provisional.document.ISourceGenerator;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * XMLModelImpl class
 */
public class DOMModelImpl extends AbstractStructuredModel implements IStructuredDocumentListener, IDOMModel, DOMImplementation {
	private static String TRACE_PARSER_MANAGEMENT_EXCEPTION = "parserManagement"; //$NON-NLS-1$
	private Object active = null;
	private DocumentImpl document = null;
	private ISourceGenerator generator = null;
	private XMLModelNotifier notifier = null;
	private XMLModelParser parser = null;
	private boolean refresh = false;
	private XMLModelUpdater updater = null;

	/**
	 * XMLModelImpl constructor
	 */
	public DOMModelImpl() {
		super();
		this.document = (DocumentImpl) internalCreateDocument();
	}

	/**
	 * This API allows clients to declare that they are about to make a
	 * "large" change to the model. This change might be in terms of content
	 * or it might be in terms of the model id or base location.
	 * 
	 * Note that in the case of embedded calls, notification to listeners is
	 * sent only once.
	 * 
	 * Note that the client who is making these changes has the responsibility
	 * to restore the models state once finished with the changes. See
	 * getMemento and restoreState.
	 * 
	 * The method isModelStateChanging can be used by a client to determine if
	 * the model is already in a change sequence.
	 */
	public void aboutToChangeModel() {
		super.aboutToChangeModel();
		// technically, no need to call beginChanging so often,
		// since aboutToChangeModel can be nested.
		// but will leave as is for this release.
		// see modelChanged, and be sure stays coordinated there.
		getModelNotifier().beginChanging();
	}

	public void aboutToReinitializeModel() {
		XMLModelNotifier notifier = getModelNotifier();
		notifier.cancelPending();
		super.aboutToReinitializeModel();
	}

	/**
	 * attrReplaced method
	 * 
	 * @param element
	 *            org.w3c.dom.Element
	 * @param newAttr
	 *            org.w3c.dom.Attr
	 * @param oldAttr
	 *            org.w3c.dom.Attr
	 */
	protected void attrReplaced(Element element, Attr newAttr, Attr oldAttr) {
		if (element == null)
			return;
		if (getActiveParser() == null) {
			XMLModelUpdater updater = getModelUpdater();
			setActive(updater);
			updater.initialize();
			updater.replaceAttr(element, newAttr, oldAttr);
			setActive(null);
		}
		getModelNotifier().attrReplaced(element, newAttr, oldAttr);
	}

	/**
	 * This API allows a client controlled way of notifying all ModelEvent
	 * listners that the model has been changed. This method is a matched pair
	 * to aboutToChangeModel, and must be called after aboutToChangeModel ...
	 * or some listeners could be left waiting indefinitely for the changed
	 * event. So, its suggested that changedModel always be in a finally
	 * clause. Likewise, a client should never call changedModel without
	 * calling aboutToChangeModel first.
	 * 
	 * In the case of embedded calls, the notification is just sent once.
	 * 
	 */
	public void changedModel() {
		// NOTE: the order of 'changedModel' and 'endChanging' is significant.
		// By calling changedModel first, this basically decrements the
		// "isChanging" counter
		// in super class and when zero all listeners to model state events
		// will be notified
		// that the model has been changed. 'endChanging' will notify all
		// deferred adapters.
		// So, the significance of order is that adapters (and methods they
		// call)
		// can count on the state of model "isChanging" to be accurate.
		// But, remember, that this means the "modelChanged" event can be
		// received before all
		// adapters have finished their processing.
		// NOTE NOTE: The above note is obsolete in fact (though still states
		// issue correctly).
		// Due to popular demand, the order of these calls were reversed and
		// behavior
		// changed on 07/22/2004.
		// 
		// see also
		// https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=4302
		// for motivation for this 'on verge of' call.
		// this could be improved in future if notifier also used counting
		// flag to avoid nested calls. If/when changed be sure to check if
		// aboutToChangeModel needs any changes too.
		if (isModelChangeStateOnVergeOfEnding()) {
			// end lock before noticiation loop, since directly or indirectly
			// we may be "called from foriegn code" during notification.
			endLock();
			// we null out here to avoid spurious"warning" message while debug
			// tracing is enabled
			fLockObject = null;
			// the notifier is what controls adaper notification, which
			// should be sent out before the 'modelChanged' event.
			getModelNotifier().endChanging();
		}
		// changedModel handles 'nesting', so only one event sent out
		// when mulitple calls to 'aboutToChange/Changed'.
		super.changedModel();
		handleRefresh();
	}

	/**
	 * childReplaced method
	 * 
	 * @param parentNode
	 *            org.w3c.dom.Node
	 * @param newChild
	 *            org.w3c.dom.Node
	 * @param oldChild
	 *            org.w3c.dom.Node
	 */
	protected void childReplaced(Node parentNode, Node newChild, Node oldChild) {
		if (parentNode == null)
			return;
		if (getActiveParser() == null) {
			XMLModelUpdater updater = getModelUpdater();
			setActive(updater);
			updater.initialize();
			updater.replaceChild(parentNode, newChild, oldChild);
			setActive(null);
		}
		getModelNotifier().childReplaced(parentNode, newChild, oldChild);
	}

	/**
	 * Creates an XML <code>Document</code> object of the specified type
	 * with its document element. HTML-only DOM implementations do not need to
	 * implement this method.
	 * 
	 * @param namespaceURIThe
	 *            namespace URI of the document element to create.
	 * @param qualifiedNameThe
	 *            qualified name of the document element to be created.
	 * @param doctypeThe
	 *            type of document to be created or <code>null</code>. When
	 *            <code>doctype</code> is not <code>null</code>, its
	 *            <code>Node.ownerDocument</code> attribute is set to the
	 *            document being created.
	 * @return A new <code>Document</code> object.
	 * @exception DOMException
	 *                INVALID_CHARACTER_ERR: Raised if the specified qualified
	 *                name contains an illegal character. <br>
	 *                NAMESPACE_ERR: Raised if the <code>qualifiedName</code>
	 *                is malformed, if the <code>qualifiedName</code> has a
	 *                prefix and the <code>namespaceURI</code> is
	 *                <code>null</code>, or if the
	 *                <code>qualifiedName</code> has a prefix that is "xml"
	 *                and the <code>namespaceURI</code> is different from "
	 *                http://www.w3.org/XML/1998/namespace" .<br>
	 *                WRONG_DOCUMENT_ERR: Raised if <code>doctype</code> has
	 *                already been used with a different document or was
	 *                created from a different implementation.
	 * @see DOM Level 2
	 */
	public Document createDocument(String namespaceURI, String qualifiedName, DocumentType doctype) throws DOMException {
		final DocumentImpl document = new DocumentImpl();
		if (namespaceURI == null && qualifiedName == null && doctype == null)
			return document;

		if (qualifiedName != null) {

			final int idx = qualifiedName.indexOf(':');
			if (idx > 0) {
				if (namespaceURI == null)
					throw new DOMException(DOMException.NAMESPACE_ERR, null);
				final String prefix = qualifiedName.substring(0, idx);
				if (prefix.equals(IXMLNamespace.XML) && !namespaceURI.equals(IXMLNamespace.XML_URI))
					throw new DOMException(DOMException.NAMESPACE_ERR, null);

				// Check if the qualifiedName is malformed
				if (idx == qualifiedName.length() - 1) // No local name
					throw new DOMException(DOMException.NAMESPACE_ERR, null);

				String localName = qualifiedName.substring(idx + 1);
				final int length = localName.length();
				if (length == 0)
					throw new DOMException(DOMException.NAMESPACE_ERR, null);

				switch (localName.charAt(0)) {
					case '-':
					case '.':
						throw new DOMException(DOMException.NAMESPACE_ERR, null);
				}

				final int qualifiedLength = qualifiedName.length();
				for (int i = 0; i < qualifiedLength; i++) {
					final char c = qualifiedName.charAt(i);
					if (Character.isWhitespace(c))
						throw new DOMException(DOMException.INVALID_CHARACTER_ERR, null);
					else if (c == ':' && i != idx)
						throw new DOMException(DOMException.NAMESPACE_ERR, null);
				}
			}
			if (!NameValidator.isValid(qualifiedName))
				throw new DOMException(DOMException.INVALID_CHARACTER_ERR, null);
		}
		else if (namespaceURI != null){
				throw new DOMException(DOMException.NAMESPACE_ERR, null);
		}

		final DOMModelImpl model = (DOMModelImpl) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		if (model != null) {
			document.setModel(model);
			model.document = document;
		}

		if (doctype != null) {
			if (doctype.getOwnerDocument() != null)
				throw new DOMException(DOMException.WRONG_DOCUMENT_ERR, null);
			document.appendChild(doctype);
		}

		final ElementImpl root = new ElementImpl();
		document.appendChild(root);
		root.setNamespaceURI(namespaceURI);
		root.setTagName(qualifiedName);

		return document;
	}

	/**
	 * Creates an empty <code>DocumentType</code> node. Entity declarations
	 * and notations are not made available. Entity reference expansions and
	 * default attribute additions do not occur. It is expected that a future
	 * version of the DOM will provide a way for populating a
	 * <code>DocumentType</code>.<br>
	 * HTML-only DOM implementations do not need to implement this method.
	 * 
	 * @param qualifiedNameThe
	 *            qualified name of the document type to be created.
	 * @param publicIdThe
	 *            external subset public identifier.
	 * @param systemIdThe
	 *            external subset system identifier.
	 * @return A new <code>DocumentType</code> node with
	 *         <code>Node.ownerDocument</code> set to <code>null</code>.
	 * @exception DOMException
	 *                INVALID_CHARACTER_ERR: Raised if the specified qualified
	 *                name contains an illegal character. <br>
	 *                NAMESPACE_ERR: Raised if the <code>qualifiedName</code>
	 *                is malformed.
	 * @see DOM Level 2
	 */
	public DocumentType createDocumentType(String qualifiedName, String publicId, String systemId) throws DOMException {
		DocumentTypeImpl documentType = new DocumentTypeImpl();
		documentType.setName(qualifiedName);
		documentType.setPublicId(publicId);
		documentType.setSystemId(systemId);
		return documentType;
	}

	/**
	 */
	protected void documentTypeChanged() {
		if (this.refresh)
			return;
		// unlike 'resfresh', 'reinitialize' finishes loop
		// and flushes remaining notification que before
		// actually reinitializing.
		// ISSUE: should reinit be used instead of handlerefresh?
		// this.setReinitializeNeeded(true);
		if (this.active != null || getModelNotifier().isChanging())
			return; // defer
		handleRefresh();
	}

	protected void editableChanged(Node node) {
		if (node != null) {
			getModelNotifier().editableChanged(node);
		}
	}

	/**
	 */
	protected void endTagChanged(Element element) {
		if (element == null)
			return;
		if (getActiveParser() == null) {
			XMLModelUpdater updater = getModelUpdater();
			setActive(updater);
			updater.initialize();
			updater.changeEndTag(element);
			setActive(null);
		}
		getModelNotifier().endTagChanged(element);
	}

	/**
	 */
	private XMLModelParser getActiveParser() {
		if (this.parser == null)
			return null;
		if (this.parser != this.active)
			return null;
		return this.parser;
	}

	/**
	 */
	private XMLModelUpdater getActiveUpdater() {
		if (this.updater == null)
			return null;
		if (this.updater != this.active)
			return null;
		return this.updater;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		if (Document.class.equals(adapter))
			return getDocument();
		return super.getAdapter(adapter);
	}

	/**
	 * getDocument method
	 * 
	 * @return XMLDocument
	 */
	public IDOMDocument getDocument() {
		return this.document;
	}

	public ISourceGenerator getGenerator() {
		if (this.generator == null) {
			this.generator = XMLGeneratorImpl.getInstance();
		}
		return this.generator;
	}

	/**
	 * getNode method
	 * 
	 * @param offset
	 *            int
	 */
	public IndexedRegion getIndexedRegion(int offset) {
		if (this.document == null)
			return null;
		// search in document children
		IDOMNode parent = null;
		int length = this.document.getEndOffset();
		if (offset * 2 < length) {
			// search from the first
			IDOMNode child = (IDOMNode) this.document.getFirstChild();
			while (child != null) {
				if (child.getEndOffset() <= offset) {
					child = (IDOMNode) child.getNextSibling();
					continue;
				}
				if (child.getStartOffset() > offset) {
					break;
				}
				IStructuredDocumentRegion startStructuredDocumentRegion = child.getStartStructuredDocumentRegion();
				if (startStructuredDocumentRegion != null) {
					if (startStructuredDocumentRegion.getEnd() > offset)
						return child;
				}
				IStructuredDocumentRegion endStructuredDocumentRegion = child.getEndStructuredDocumentRegion();
				if (endStructuredDocumentRegion != null) {
					if (endStructuredDocumentRegion.getStart() <= offset)
						return child;
				}
				// dig more
				parent = child;
				child = (IDOMNode) parent.getFirstChild();
			}
		}
		else {
			// search from the last
			IDOMNode child = (IDOMNode) this.document.getLastChild();
			while (child != null) {
				if (child.getStartOffset() > offset) {
					child = (IDOMNode) child.getPreviousSibling();
					continue;
				}
				if (child.getEndOffset() <= offset) {
					break;
				}
				IStructuredDocumentRegion startStructuredDocumentRegion = child.getStartStructuredDocumentRegion();
				if (startStructuredDocumentRegion != null) {
					if (startStructuredDocumentRegion.getEnd() > offset)
						return child;
				}
				IStructuredDocumentRegion endStructuredDocumentRegion = child.getEndStructuredDocumentRegion();
				if (endStructuredDocumentRegion != null) {
					if (endStructuredDocumentRegion.getStart() <= offset)
						return child;
				}
				// dig more
				parent = child;
				child = (IDOMNode) parent.getLastChild();
			}
		}
		return parent;
	}

	/**
	 */
	public XMLModelNotifier getModelNotifier() {
		if (this.notifier == null) {
			this.notifier = new XMLModelNotifierImpl();
		}
		return this.notifier;
	}

	/**
	 */
	private XMLModelParser getModelParser() {
		if (this.parser == null) {
			this.parser = createModelParser();
		}
		return this.parser;
	}

	protected XMLModelParser createModelParser() {
		return new XMLModelParser(this);
	}

	/**
	 */
	private XMLModelUpdater getModelUpdater() {
		if (this.updater == null) {
			this.updater = createModelUpdater();
		}
		return this.updater;
	}

	protected XMLModelUpdater createModelUpdater() {
		return new XMLModelUpdater(this);
	}

	/**
	 */
	private void handleRefresh() {
		if (!this.refresh)
			return;
		XMLModelNotifier notifier = getModelNotifier();
		boolean isChanging = notifier.isChanging();
		if (!isChanging)
			notifier.beginChanging(true);
		XMLModelParser parser = getModelParser();
		setActive(parser);
		this.document.removeChildNodes();
		try {
			this.refresh = false;
			parser.replaceStructuredDocumentRegions(getStructuredDocument().getRegionList(), null);
		}
		catch (Exception ex) {
			Logger.logException(ex);
		}
		finally {
			setActive(null);
			if (!isChanging)
				notifier.endChanging();
		}
	}

	/**
	 * Test if the DOM implementation implements a specific feature.
	 * 
	 * @param featureThe
	 *            name of the feature to test (case-insensitive). The values
	 *            used by DOM features are defined throughout the DOM Level 2
	 *            specifications and listed in the section. The name must be
	 *            an XML name. To avoid possible conflicts, as a convention,
	 *            names referring to features defined outside the DOM
	 *            specification should be made unique by reversing the name of
	 *            the Internet domain name of the person (or the organization
	 *            that the person belongs to) who defines the feature,
	 *            component by component, and using this as a prefix. For
	 *            instance, the W3C SVG Working Group defines the feature
	 *            "org.w3c.dom.svg".
	 * @param versionThis
	 *            is the version number of the feature to test. In Level 2,
	 *            the string can be either "2.0" or "1.0". If the version is
	 *            not specified, supporting any version of the feature causes
	 *            the method to return <code>true</code>.
	 * @return <code>true</code> if the feature is implemented in the
	 *         specified version, <code>false</code> otherwise.
	 */
	public boolean hasFeature(String feature, String version) {
		if (feature == null)
			return false;
		if (version != null) {
			if (!version.equals("1.0") && !version.equals("2.0")) { //$NON-NLS-2$//$NON-NLS-1$
				return false;
			}
		}
		if (feature.equalsIgnoreCase("Core")) //$NON-NLS-1$
			return true; //$NON-NLS-1$
		if (feature.equalsIgnoreCase("XML")) //$NON-NLS-1$
			return true; //$NON-NLS-1$
		return false;
	}

	/**
	 * createDocument method
	 * 
	 * @return org.w3c.dom.Document
	 */
	protected Document internalCreateDocument() {
		DocumentImpl document = new DocumentImpl();
		document.setModel(this);
		return document;
	}

	boolean isReparsing() {
		return (active != null);
	}

	/**
	 * nameChanged method
	 * 
	 * @param node
	 *            org.w3c.dom.Node
	 */
	protected void nameChanged(Node node) {
		if (node == null)
			return;
		if (getActiveParser() == null) {
			XMLModelUpdater updater = getModelUpdater();
			setActive(updater);
			updater.initialize();
			updater.changeName(node);
			setActive(null);
		}
		// notification is already sent
	}

	/**
	 * newModel method
	 * 
	 */
	public void newModel(NewDocumentEvent structuredDocumentEvent) {
		if (structuredDocumentEvent == null)
			return;
		IStructuredDocument structuredDocument = structuredDocumentEvent.getStructuredDocument();
		if (structuredDocument == null)
			return;
		// this should not happen, but for the case
		if (fStructuredDocument != null && fStructuredDocument != structuredDocument)
			setStructuredDocument(structuredDocument);

		internalSetNewDocument(structuredDocument);
	}

	private void internalSetNewDocument(IStructuredDocument structuredDocument) {
		if (structuredDocument == null)
			return;
		IStructuredDocumentRegionList flatNodes = structuredDocument.getRegionList();
		if ((flatNodes == null) || (flatNodes.getLength() == 0)) {
			return;
		}
		if (this.document == null)
			return; // being constructed
		XMLModelUpdater updater = getActiveUpdater();
		if (updater != null) { // being updated
			try {
				updater.replaceStructuredDocumentRegions(flatNodes, null);
			}
			catch (Exception ex) {
				Logger.logException(ex);
				this.refresh = true;
				handleRefresh();
			}
			finally {
				setActive(null);
			}
			// // for new model, we might need to
			// // re-init, e.g. if someone calls setText
			// // on an existing model
			// checkForReinit();
			return;
		}
		XMLModelNotifier notifier = getModelNotifier();
		boolean isChanging = notifier.isChanging();
		// call even if changing to notify doing new model
		getModelNotifier().beginChanging(true);
		XMLModelParser parser = getModelParser();
		setActive(parser);
		this.document.removeChildNodes();
		try {
			parser.replaceStructuredDocumentRegions(flatNodes, null);
		}
		catch (Exception ex) {
			Logger.logException(ex);
			// meaningless to refresh, because the result might be the same
		}
		finally {
			setActive(null);
			if (!isChanging) {
				getModelNotifier().endChanging();
			}
			// ignore refresh
			this.refresh = false;
		}
	}

	/**
	 */
	public void noChange(NoChangeEvent event) {
		XMLModelUpdater updater = getActiveUpdater();
		if (updater != null) { // being updated
			// cleanup updater staffs
			try {
				updater.replaceStructuredDocumentRegions(null, null);
			}
			catch (Exception ex) {
				Logger.logException(ex);
				this.refresh = true;
				handleRefresh();
			}
			finally {
				setActive(null);
			}
			// I guess no chanage means the model could not need re-init
			// checkForReinit();
			return;
		}
	}

	/**
	 * nodesReplaced method
	 * 
	 */
	public void nodesReplaced(StructuredDocumentRegionsReplacedEvent event) {
		if (event == null)
			return;
		IStructuredDocumentRegionList oldStructuredDocumentRegions = event.getOldStructuredDocumentRegions();
		IStructuredDocumentRegionList newStructuredDocumentRegions = event.getNewStructuredDocumentRegions();
		XMLModelUpdater updater = getActiveUpdater();
		if (updater != null) { // being updated
			try {
				updater.replaceStructuredDocumentRegions(newStructuredDocumentRegions, oldStructuredDocumentRegions);
			}
			catch (Exception ex) {
				if (ex.getClass().equals(StructuredDocumentRegionManagementException.class)) {
					Logger.traceException(TRACE_PARSER_MANAGEMENT_EXCEPTION, ex);
				}
				else {
					Logger.logException(ex);
				}
				this.refresh = true;
				handleRefresh();
			}
			finally {
				setActive(null);
			}
			// checkForReinit();
			return;
		}
		XMLModelNotifier notifier = getModelNotifier();
		boolean isChanging = notifier.isChanging();
		if (!isChanging)
			notifier.beginChanging();
		XMLModelParser parser = getModelParser();
		setActive(parser);
		try {
			parser.replaceStructuredDocumentRegions(newStructuredDocumentRegions, oldStructuredDocumentRegions);
		}
		catch (Exception ex) {
			Logger.logException(ex);
			this.refresh = true;
			handleRefresh();
		}
		finally {
			setActive(null);
			if (!isChanging) {
				notifier.endChanging();
				handleRefresh();
			}
		}

	}

	/**
	 * regionChanged method
	 * 
	 * @param structuredDocumentEvent
	 */
	public void regionChanged(RegionChangedEvent event) {
		if (event == null)
			return;
		IStructuredDocumentRegion flatNode = event.getStructuredDocumentRegion();
		if (flatNode == null)
			return;
		ITextRegion region = event.getRegion();
		if (region == null)
			return;
		XMLModelUpdater updater = getActiveUpdater();
		if (updater != null) { // being updated
			try {
				updater.changeRegion(event, flatNode, region);
			}
			catch (Exception ex) {
				Logger.logException(ex);
				this.refresh = true;
				handleRefresh();
			}
			finally {
				setActive(null);
			}
			// checkForReinit();
			return;
		}
		XMLModelNotifier notifier = getModelNotifier();
		boolean isChanging = notifier.isChanging();
		if (!isChanging)
			notifier.beginChanging();
		XMLModelParser parser = getModelParser();
		setActive(parser);
		try {
			parser.changeRegion(event, flatNode, region);
		}
		catch (Exception ex) {
			Logger.logException(ex);
			this.refresh = true;
			handleRefresh();
		}
		finally {
			setActive(null);
			if (!isChanging) {
				notifier.endChanging();
				handleRefresh();
			}
		}
		// checkForReinit();
	}

	/**
	 * regionsReplaced method
	 * 
	 * @param event
	 */
	public void regionsReplaced(RegionsReplacedEvent event) {
		if (event == null)
			return;
		IStructuredDocumentRegion flatNode = event.getStructuredDocumentRegion();
		if (flatNode == null)
			return;
		ITextRegionList oldRegions = event.getOldRegions();
		ITextRegionList newRegions = event.getNewRegions();
		if (oldRegions == null && newRegions == null)
			return;
		XMLModelUpdater updater = getActiveUpdater();
		if (updater != null) { // being updated
			try {
				updater.replaceRegions(flatNode, newRegions, oldRegions);
			}
			catch (Exception ex) {
				Logger.logException(ex);
				this.refresh = true;
				handleRefresh();
			}
			finally {
				setActive(null);
			}
			// checkForReinit();
			return;
		}
		XMLModelNotifier notifier = getModelNotifier();
		boolean isChanging = notifier.isChanging();
		if (!isChanging)
			notifier.beginChanging();
		XMLModelParser parser = getModelParser();
		setActive(parser);
		try {
			parser.replaceRegions(flatNode, newRegions, oldRegions);
		}
		catch (Exception ex) {
			Logger.logException(ex);
			this.refresh = true;
			handleRefresh();
		}
		finally {
			setActive(null);
			if (!isChanging) {
				notifier.endChanging();
				handleRefresh();
			}
		}
		// checkForReinit();
	}

	/**
	 */
	public void releaseFromEdit() {
		if (!isShared()) {
			// this.document.releaseStyleSheets();
			// this.document.releaseDocumentType();
		}
		super.releaseFromEdit();
	}

	/**
	 */
	public void releaseFromRead() {
		if (!isShared()) {
			// this.document.releaseStyleSheets();
			// this.document.releaseDocumentType();
		}
		super.releaseFromRead();
	}

	/**
	 */
	private void setActive(Object active) {
		this.active = active;
		// side effect
		// when ever becomes active, besure tagNameCache is cleared
		// (and not used)
		if (active == null) {
			document.activateTagNameCache(true);
		}
		else {
			document.activateTagNameCache(false);
		}

	}

	/**
	 */
	public void setGenerator(ISourceGenerator generator) {
		this.generator = generator;
	}

	/**
	 */
	public void setModelNotifier(XMLModelNotifier notifier) {
		this.notifier = notifier;
	}

	/**
	 */
	public void setModelParser(XMLModelParser parser) {
		this.parser = parser;
	}

	/**
	 */
	public void setModelUpdater(XMLModelUpdater updater) {
		this.updater = updater;
	}

	/**
	 * setStructuredDocument method
	 * 
	 * @param structuredDocument
	 */
	public void setStructuredDocument(IStructuredDocument structuredDocument) {
		IStructuredDocument oldStructuredDocument = super.getStructuredDocument();
		if (structuredDocument == oldStructuredDocument)
			return; // nothing to do
		if (oldStructuredDocument != null)
			oldStructuredDocument.removeDocumentChangingListener(this);
		super.setStructuredDocument(structuredDocument);
		if (structuredDocument != null) {
			internalSetNewDocument(structuredDocument);
			structuredDocument.addDocumentChangingListener(this);
		}
	}

	/**
	 */
	protected void startTagChanged(Element element) {
		if (element == null)
			return;
		if (getActiveParser() == null) {
			XMLModelUpdater updater = getModelUpdater();
			setActive(updater);
			updater.initialize();
			updater.changeStartTag(element);
			setActive(null);
		}
		getModelNotifier().startTagChanged(element);
	}

	/**
	 * valueChanged method
	 * 
	 * @param node
	 *            org.w3c.dom.Node
	 */
	protected void valueChanged(Node node) {
		if (node == null)
			return;
		if (getActiveParser() == null) {
			XMLModelUpdater updater = getModelUpdater();
			setActive(updater);
			updater.initialize();
			updater.changeValue(node);
			setActive(null);
		}
		getModelNotifier().valueChanged(node);
	}

	/**
	 * NOT IMPLEMENTED. Is defined here in preparation of DOM 3.
	 */
	public Object getFeature(String feature, String version) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented in this version."); //$NON-NLS-1$
	}
}
