/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.modelquery;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Path;
import org.eclipse.jst.jsp.core.internal.contentmodel.JSPCMDocumentFactory;
import org.eclipse.jst.jsp.core.internal.contenttype.DeploymentDescriptorPropertyCache;
import org.eclipse.jst.jsp.core.internal.provisional.JSP12Namespace;
import org.eclipse.jst.jsp.core.internal.provisional.JSP20Namespace;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.wst.html.core.internal.contentmodel.JSPCMDocument;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.extension.ModelQueryExtension;
import org.eclipse.wst.xml.core.internal.provisional.contentmodel.CMDocType;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.ssemodelquery.ModelQueryAdapter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * An implementation of {@link ModelQueryExtension} for JSP tags in JSP documents
 */
public class JSPModelQueryExtension extends ModelQueryExtension {
	
	private static final String TAG_JSP_ROOT = "jsp:root";

	/**
	 * Originally taken from JSPContentAssistProcessor
	 * 
	 * @see org.eclipse.wst.xml.core.internal.contentmodel.modelquery.extension.ModelQueryExtension#getAvailableElementContent(org.w3c.dom.Element, java.lang.String, int)
	 */
	public CMNode[] getAvailableElementContent(Element parentElement,
			String namespace, int includeOptions) {
		
		CMNode[] nodes = EMPTY_CMNODE_ARRAY;
		ArrayList nodeList = new ArrayList();
		
		//only returns anything if looking for child nodes
		if(((includeOptions & ModelQuery.INCLUDE_CHILD_NODES) != 0) && parentElement instanceof IDOMNode) {
			IDOMNode node = (IDOMNode)parentElement;
			// get position dependent CMDocuments and insert their tags as
			// proposals
			ModelQueryAdapter mqAdapter = null;
			if (node.getNodeType() == Node.DOCUMENT_NODE) {
				mqAdapter = (ModelQueryAdapter) node.getAdapterFor(ModelQueryAdapter.class);
			} else {
				mqAdapter = (ModelQueryAdapter) ((IDOMNode) node.getOwnerDocument()).getAdapterFor(ModelQueryAdapter.class);
			}

			if (mqAdapter != null) {
				CMDocument doc = mqAdapter.getModelQuery().getCorrespondingCMDocument(node);
				if (doc != null) {
					CMDocument jcmdoc = getDefaultJSPCMDocument(node);
					CMNamedNodeMap jspelements = jcmdoc.getElements();

					/* For a built-in JSP action the content model is properly
					 * set up, so don't just blindly add the rest--unless this
					 * will be a direct child of the document
					 */
					if (jspelements != null && (!(doc instanceof JSPCMDocument) || node.getNodeType() == Node.DOCUMENT_NODE)) {
						List rejectElements = new ArrayList();

						// determine if the document is in XML form
						Document domDoc = null;
						if (node.getNodeType() == Node.DOCUMENT_NODE) {
							domDoc = (Document) node;
						} else {
							domDoc = node.getOwnerDocument();
						}

						// Show XML tag forms of JSP markers if jsp:root is
						// the document element OR it's HTML but
						// isn't really in the text.
						// If the document isn't strictly XML, pull out the
						// XML tag forms it is xml format
						rejectElements.add(JSP12Namespace.ElementName.SCRIPTLET);
						rejectElements.add(JSP12Namespace.ElementName.EXPRESSION);
						rejectElements.add(JSP12Namespace.ElementName.DECLARATION);
						rejectElements.add(JSP12Namespace.ElementName.DIRECTIVE_INCLUDE);
						rejectElements.add(JSP12Namespace.ElementName.DIRECTIVE_PAGE);
						rejectElements.add(JSP12Namespace.ElementName.TEXT);
						rejectElements.add(JSP12Namespace.ElementName.DIRECTIVE_TAGLIB);
						rejectElements.add(JSP20Namespace.ElementName.DIRECTIVE_TAG);
						rejectElements.add(JSP20Namespace.ElementName.DIRECTIVE_ATTRIBUTE);
						rejectElements.add(JSP20Namespace.ElementName.DIRECTIVE_VARIABLE);
						if (isXMLFormat(domDoc)) {
							// jsp actions
							rejectElements.add(JSP12Namespace.ElementName.FALLBACK);
							rejectElements.add(JSP12Namespace.ElementName.USEBEAN);
							rejectElements.add(JSP12Namespace.ElementName.GETPROPERTY);
							rejectElements.add(JSP12Namespace.ElementName.SETPROPERTY);
							rejectElements.add(JSP12Namespace.ElementName.INCLUDE);
							rejectElements.add(JSP12Namespace.ElementName.FORWARD);
							rejectElements.add(JSP12Namespace.ElementName.PLUGIN);
							rejectElements.add(JSP12Namespace.ElementName.FALLBACK);
							rejectElements.add(JSP12Namespace.ElementName.PARAM);
							rejectElements.add(JSP12Namespace.ElementName.PARAMS);
						}


						// don't show jsp:root if a document element already
						// exists
						Element docElement = domDoc.getDocumentElement();
						if (docElement != null &&((docElement.getNodeName().equals(TAG_JSP_ROOT)) ||
								((((IDOMNode) docElement).getStartStructuredDocumentRegion() != null ||
										((IDOMNode) docElement).getEndStructuredDocumentRegion() != null)))) {
							
							rejectElements.add(JSP12Namespace.ElementName.ROOT);
						}

						for (int j = 0; j < jspelements.getLength(); j++) {
							CMElementDeclaration ed = (CMElementDeclaration) jspelements.item(j);
							if (!rejectElements.contains(ed.getNodeName())) {
								nodeList.add(ed);
							}
						}

					}
				}
				// No cm document (such as for the Document (a non-Element) node itself)
				else {
					CMNamedNodeMap jspElements = getDefaultJSPCMDocument(node).getElements();
					int length = jspElements.getLength();
					for (int i = 0; i < length; i++) {
						nodeList.add(jspElements.item(i));
					}
				}
			}
			
			nodes = (CMNode[])nodeList.toArray(new CMNode[nodeList.size()]);
		}
		
		return nodes;
	}
	
	/**
	 * <p>For JSP files and segments, this is just the JSP
	 *         document, but when editing tag files and their fragments, it
	 *         should be the tag document.</p>
	 * 
	 * <p>It may also vary based on the model being edited in the future.</p>
	 * 
	 * <p><b>NOTE:</b>Copied from JSPContentAssistProcessor</p>
	 *
	 * @return the default non-embedded CMDocument for the document being
	 *         edited. 
	 */
	private CMDocument getDefaultJSPCMDocument(IDOMNode node) {
		CMDocument jcmdoc = null; 
		
		// handle tag files here
		String contentType = node.getModel().getContentTypeIdentifier();
		if (ContentTypeIdForJSP.ContentTypeID_JSPTAG.equals(contentType)) {
			jcmdoc =  JSPCMDocumentFactory.getCMDocument(CMDocType.TAG20_DOC_TYPE);
		} else {
			String modelPath = node.getModel().getBaseLocation();
			if (modelPath != null && !IModelManager.UNMANAGED_MODEL.equals(modelPath)) {
				float version = DeploymentDescriptorPropertyCache.getInstance().getJSPVersion(new Path(modelPath));
				jcmdoc = JSPCMDocumentFactory.getCMDocument(version);
			}
			if (jcmdoc == null) {
				jcmdoc = JSPCMDocumentFactory.getCMDocument();
			}
		}

		return jcmdoc;
	}
	
	/**
	 * <p><b>NOTE:</b>Copied from JSPContentAssistProcessor</p>
	 *
	 * @param doc determine if this {@link Document} is in an XML format
	 * @return is the given document in an XML format
	 */
	private boolean isXMLFormat(Document doc) {
		boolean result = false;
		if (doc != null) {
			Element docElement = doc.getDocumentElement();
			result = docElement != null &&
				((docElement.getNodeName().equals(TAG_JSP_ROOT)) ||
						((((IDOMNode) docElement).getStartStructuredDocumentRegion() == null &&
								((IDOMNode) docElement).getEndStructuredDocumentRegion() == null)));
		}
		return result;
	}
}
