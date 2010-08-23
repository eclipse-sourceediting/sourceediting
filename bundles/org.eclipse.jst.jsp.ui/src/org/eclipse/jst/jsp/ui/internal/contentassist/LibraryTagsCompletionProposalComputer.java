/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.contentassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jst.jsp.core.internal.contentmodel.JSPCMDocumentFactory;
import org.eclipse.jst.jsp.core.internal.contentmodel.TaglibController;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.TLDCMDocumentManager;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDElementDeclaration;
import org.eclipse.jst.jsp.core.internal.contenttype.DeploymentDescriptorPropertyCache;
import org.eclipse.jst.jsp.core.internal.provisional.JSP12Namespace;
import org.eclipse.jst.jsp.core.internal.provisional.JSP20Namespace;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.jst.jsp.ui.internal.editor.JSPEditorPluginImageHelper;
import org.eclipse.jst.jsp.ui.internal.editor.JSPEditorPluginImages;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.html.core.internal.contentmodel.HTMLPropertyDeclaration;
import org.eclipse.wst.html.core.internal.contentmodel.JSPCMDocument;
import org.eclipse.wst.html.ui.internal.contentassist.HTMLTagsCompletionProposalComputer;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.core.internal.provisional.contentmodel.CMDocType;
import org.eclipse.wst.xml.core.internal.provisional.contentmodel.CMNodeWrapper;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.core.internal.ssemodelquery.ModelQueryAdapter;
import org.eclipse.wst.xml.ui.internal.contentassist.AbstractXMLModelQueryCompletionProposalComputer;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLContentModelGenerator;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLRelevanceConstants;
import org.eclipse.wst.xml.ui.internal.editor.CMImageUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>Computes tags provided by tag libraries completion proposals.</p>
 * 
 * <p>Extends the {@link HTMLTagsCompletionProposalComputer} to benefit from
 * its work for determining the correct {@link XMLContentModelGenerator} to use</p>
 */
public class LibraryTagsCompletionProposalComputer extends
	HTMLTagsCompletionProposalComputer {
	
	private int fDepthCount;

	/**
	 * @see org.eclipse.wst.html.ui.internal.contentassist.HTMLTagsCompletionProposalComputer#computeContextInformation(org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public List computeContextInformation(
			CompletionProposalInvocationContext context,
			IProgressMonitor monitor) {
		
		return Collections.EMPTY_LIST;
	}
	
	/**
	 * @see org.eclipse.wst.html.ui.internal.contentassist.HTMLTagsCompletionProposalComputer#validModelQueryNode(org.eclipse.wst.xml.core.internal.contentmodel.CMNode)
	 */
	protected boolean validModelQueryNode(CMNode node) {
		boolean isValid = false;
		
		//unwrap
		if(node instanceof CMNodeWrapper) {
			node = ((CMNodeWrapper)node).getOriginNode();
		}
		
		//determine if is valid
		if(node instanceof HTMLPropertyDeclaration) {
			HTMLPropertyDeclaration propDec = (HTMLPropertyDeclaration)node;
			isValid = propDec.isJSP();
		} else if(node.supports(TLDElementDeclaration.IS_LIBRARY_TAG)){
			Boolean isLibraryTag = (Boolean)node.getProperty(TLDElementDeclaration.IS_LIBRARY_TAG);
			isValid = isLibraryTag != null && isLibraryTag.booleanValue();
		}
		
		return isValid;
	}
	
	/**
	 * <p>JSP has none.  This overrides the default behavior to add in doctype proposals which
	 * should really be contributed by the HTML tag computer</p>
	 * 
	 * @see org.eclipse.wst.html.ui.internal.contentassist.HTMLTagsCompletionProposalComputer#addStartDocumentProposals(org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest, org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext)
	 */
	protected void addStartDocumentProposals(
			ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context) {
		//jsp has none
	}
	
	/**
	 * @see org.eclipse.wst.html.ui.internal.contentassist.HTMLTagsCompletionProposalComputer#addEmptyDocumentProposals(org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest, org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext)
	 */
	protected void addEmptyDocumentProposals(
			ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context) {
		//jsp has none
	}
	
	/**
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.AbstractXMLModelQueryCompletionProposalComputer#addTagCloseProposals(org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest, org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext)
	 */
	protected void addTagCloseProposals(ContentAssistRequest contentAssistRequest, CompletionProposalInvocationContext context) {
		//do nothing, html computer will take care of adding the > and /> suggestions
	}
	
	/**
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.DefaultXMLCompletionProposalComputer#addAttributeValueProposals(org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest, org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext)
	 */
	protected void addAttributeValueProposals(
			ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context) {

		if(!this.isXHTML) {
			IDOMNode node = (IDOMNode) contentAssistRequest.getNode();
	
			ModelQuery mq = ModelQueryUtil.getModelQuery(node.getOwnerDocument());
			if (mq != null) {
				CMDocument doc = mq.getCorrespondingCMDocument(node);
				// this shouldn't have to have the prefix coded in
				if (doc instanceof JSPCMDocument || doc instanceof CMNodeWrapper ||
						node.getNodeName().startsWith("jsp:")) { //$NON-NLS-1$
					return;
				}
			}
	
			// Find the attribute name for which this position should have a value
			IStructuredDocumentRegion open = node.getFirstStructuredDocumentRegion();
			ITextRegionList openRegions = open.getRegions();
			int i = openRegions.indexOf(contentAssistRequest.getRegion());
			if (i < 0) {
				return;
			}
			ITextRegion nameRegion = null;
			while (i >= 0) {
				nameRegion = openRegions.get(i--);
				if (nameRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
					break;
				}
			}
			
			// on an empty value, add all the JSP and taglib tags
			CMElementDeclaration elementDecl =
				AbstractXMLModelQueryCompletionProposalComputer.getCMElementDeclaration(node);
			if (nameRegion != null && elementDecl != null) {
				String attributeName = open.getText(nameRegion);
				if (attributeName != null) {
					Node parent = contentAssistRequest.getParent();
					
					//ignore start quote in match string
					String matchString = contentAssistRequest.getMatchString().trim();
					if(matchString.startsWith("'") || matchString.startsWith("\"")) { //$NON-NLS-1$ //$NON-NLS-2$
						matchString = matchString.substring(1);
					}
					
					//get all the proposals
					List additionalElements = ModelQueryUtil.getModelQuery(node.getOwnerDocument()).getAvailableContent(
							(Element) node, elementDecl, ModelQuery.INCLUDE_ALL);
					Iterator nodeIterator = additionalElements.iterator();
					
					//check each suggestion
					while (nodeIterator.hasNext()) {
						CMNode additionalElementDecl = (CMNode) nodeIterator.next();
						if (additionalElementDecl != null && additionalElementDecl instanceof CMElementDeclaration &&
								validModelQueryNode(additionalElementDecl)) {
							CMElementDeclaration ed = (CMElementDeclaration) additionalElementDecl;
							// https://bugs.eclipse.org/bugs/show_bug.cgi?id=89811
							StringBuffer sb = new StringBuffer();
							getContentGenerator().generateTag(parent, ed, sb);
	
							String proposedText = sb.toString();
	
							//filter out any proposals that dont match matchString
							if (beginsWith(proposedText, matchString)) {
								//wrap with ' because JSP attributes are warped with "
								proposedText = "'" + proposedText; //$NON-NLS-1$
								
								//if its a container its possible the closing quote is already there
								//don't want to risk injecting an extra
								if(!(contentAssistRequest.getRegion() instanceof ITextRegionContainer)) {
									proposedText += "'"; //$NON-NLS-1$
								}
								
								//get the image
								Image image = CMImageUtil.getImage(elementDecl);
								if (image == null) {
									image = this.getGenericTagImage();
								}
								
								//create the proposal
								int cursorAdjustment = getCursorPositionForProposedText(proposedText);
								String proposedInfo = AbstractXMLModelQueryCompletionProposalComputer.getAdditionalInfo(
										AbstractXMLModelQueryCompletionProposalComputer.getCMElementDeclaration(parent), elementDecl);
								String tagname = getContentGenerator().getRequiredName(node, ed);
								CustomCompletionProposal proposal = new CustomCompletionProposal(
										proposedText, contentAssistRequest.getReplacementBeginPosition(),
										contentAssistRequest.getReplacementLength(), cursorAdjustment, image, tagname, null, proposedInfo,
										XMLRelevanceConstants.R_XML_ATTRIBUTE_VALUE);
								contentAssistRequest.addProposal(proposal);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.AbstractXMLCompletionProposalComputer#setErrorMessage(java.lang.String)
	 */
	public void setErrorMessage(String errorMessage) {
		if (fDepthCount == 0) {
			super.setErrorMessage(errorMessage);
		}
	}
	
	/**
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.AbstractXMLModelQueryCompletionProposalComputer#getGenericTagImage()
	 */
	protected Image getGenericTagImage() {
		return JSPEditorPluginImageHelper.getInstance().getImage(JSPEditorPluginImages.IMG_OBJ_TAG_JSP);
	}
	
	/**
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.AbstractXMLModelQueryCompletionProposalComputer#getDeemphasizedTagImage()
	 */
	protected Image getDeemphasizedTagImage() {
		return JSPEditorPluginImageHelper.getInstance().getImage(JSPEditorPluginImages.IMG_OBJ_TAG_JSP);
	}
	
	/**
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.AbstractXMLModelQueryCompletionProposalComputer#getEmphasizedTagImage()
	 */
	protected Image getEmphasizedTagImage() {
		return JSPEditorPluginImageHelper.getInstance().getImage(JSPEditorPluginImages.IMG_OBJ_TAG_JSP);
	}
	
	//----------------------BELOW HERE SHOULD BE REMOVED ONCE BUG 211961 IS FIXED ---------------------
	
	/**
	 * <p><b>NOTE: </b>This should be removed as soon as Bug 311961 is fixed</p>
	 * 
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.AbstractXMLModelQueryCompletionProposalComputer#addTagInsertionProposals(org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest, int, org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext)
	 */
	protected void addTagInsertionProposals(
			ContentAssistRequest contentAssistRequest, int childPosition,
			CompletionProposalInvocationContext context) {
		
		//get the default proposals
		super.addTagInsertionProposals(contentAssistRequest, childPosition, context);
		
		/**
		 * TODO: REMOVE THIS HACK - Bug 311961
		 */
		if(contentAssistRequest.getParent().getNodeType() == Node.DOCUMENT_NODE) {
			this.forciblyAddTagLibAndJSPPropsoals((Document)contentAssistRequest.getParent(),
					contentAssistRequest, childPosition);
		}
	}
	
	/**
	 * <p><b>NOTE: </b>This should be removed as soon as Bug 311961 is fixed</p>
	 * 
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.AbstractXMLModelQueryCompletionProposalComputer#addTagNameProposals(org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest, int, org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext)
	 */
	protected void addTagNameProposals(
			ContentAssistRequest contentAssistRequest, int childPosition,
			CompletionProposalInvocationContext context) {
		
		//get the default proposals
		super.addTagNameProposals(contentAssistRequest, childPosition, context);
		
		/**
		 * TODO: REMOVE THIS HACK - Bug 311961
		 */
		if(contentAssistRequest.getParent().getNodeType() == Node.DOCUMENT_NODE) {
			this.forciblyAddTagLibAndJSPPropsoals((Document)contentAssistRequest.getParent(),
					contentAssistRequest, childPosition);
		}
	}
	
	/**
	 * <p><b>NOTE: </b>This should be removed as soon as Bug 311961 is fixed</p>
	 * <p>This is bad because it does not use the ModelQuery framework</p>
	 * 
	 * @param document
	 * @param contentAssistRequest
	 * @param childPosition
	 */
	private void forciblyAddTagLibAndJSPPropsoals(Document document, ContentAssistRequest contentAssistRequest, int childPosition) {
		if (!isXMLFormat(document)) {
			List additionalElements = forciblyGetTagLibAndJSPElements(new ArrayList(), document, childPosition);
			
			//convert CMElementDeclartions to proposals
			for (int i = 0; i < additionalElements.size(); i++) {
				CMElementDeclaration ed = (CMElementDeclaration) additionalElements.get(i);
				if (ed != null) {
					Image image = CMImageUtil.getImage(ed);
					if (image == null) {
						image = this.getGenericTagImage();
					}
					String proposedText = getRequiredText(document, ed);
					String tagname = getRequiredName(document, ed);
					// account for the &lt; and &gt;
					int markupAdjustment = getContentGenerator().getMinimalStartTagLength(document, ed);
					String proposedInfo = getAdditionalInfo(null, ed);
					CustomCompletionProposal proposal = new CustomCompletionProposal(
							proposedText, contentAssistRequest.getReplacementBeginPosition(),
							contentAssistRequest.getReplacementLength(), markupAdjustment, image,
							tagname, null, proposedInfo, XMLRelevanceConstants.R_TAG_INSERTION);
					contentAssistRequest.addProposal(proposal);
				}
			}
		}
	}
	
	/**
	 * <p><b>NOTE: </b>This should be removed as soon as Bug 311961 is fixed</p>
	 * <p>This is bad because it does not use the ModelQuery framework, it
	 * access the TLDCMDocumentManager directly</p>
	 * <p>This is essentially a combination of the {@link TaglibModelQueryExtension} and
	 * the {@link JSPModelQueryExtension} but it means any other extensions get left
	 * out when creating content assist suggestion at the document root level</p>
	 * 
	 * @param elementDecls
	 * @param node
	 * @param childIndex
	 * @return
	 */
	private List forciblyGetTagLibAndJSPElements(List elementDecls, Node node, int childIndex) {
		if (node instanceof IDOMNode) {
			/*
			 * find the location of the intended insertion as it will give us
			 * the correct offset for checking position dependent CMDocuments
			 */
			int textInsertionOffset = 0;
			NodeList children = node.getChildNodes();
			if (children.getLength() >= childIndex && childIndex >= 0) {
				Node nodeAlreadyAtIndex = children.item(childIndex);
				if (nodeAlreadyAtIndex instanceof IDOMNode)
					textInsertionOffset = ((IDOMNode) nodeAlreadyAtIndex).getEndOffset();
			}
			else {
				textInsertionOffset = ((IDOMNode) node).getStartOffset();
			}
			TLDCMDocumentManager mgr = TaglibController.getTLDCMDocumentManager(((IDOMNode) node).getStructuredDocument());
			if (mgr != null) {
				List moreCMDocuments = mgr.getCMDocumentTrackers(textInsertionOffset);
				if (moreCMDocuments != null) {
					for (int i = 0; i < moreCMDocuments.size(); i++) {
						CMDocument doc = (CMDocument) moreCMDocuments.get(i);
						CMNamedNodeMap elements = doc.getElements();
						if (elements != null) {
							for (int j = 0; j < elements.getLength(); j++) {
								CMElementDeclaration ed = (CMElementDeclaration) elements.item(j);
								elementDecls.add(ed);
							}
						}
					}
				}
			}

			// get position dependent CMDocuments and insert their tags as
			// proposals

			ModelQueryAdapter mqAdapter = null;
			if (node.getNodeType() == Node.DOCUMENT_NODE)
				mqAdapter = (ModelQueryAdapter) ((IDOMNode) node).getAdapterFor(ModelQueryAdapter.class);
			else
				mqAdapter = (ModelQueryAdapter) ((IDOMNode) node.getOwnerDocument()).getAdapterFor(ModelQueryAdapter.class);

			if (mqAdapter != null) {
				CMDocument doc = mqAdapter.getModelQuery().getCorrespondingCMDocument(node);
				if (doc != null) {
					CMDocument jcmdoc = getDefaultJSPCMDocument((IDOMNode) node);
					CMNamedNodeMap jspelements = jcmdoc.getElements();

					/*
					 * For a built-in JSP action the content model is properly
					 * set up, so don't just blindly add the rest--unless this
					 * will be a direct child of the document
					 */
					if (jspelements != null && (!(doc instanceof JSPCMDocument) || node.getNodeType() == Node.DOCUMENT_NODE)) {
						List rejectElements = new ArrayList();

						// determine if the document is in XML form
						Document domDoc = null;
						if (node.getNodeType() == Node.DOCUMENT_NODE)
							domDoc = (Document) node;
						else
							domDoc = node.getOwnerDocument();

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
						if (docElement != null && ((docElement.getNodeName().equals("jsp:root")) || ((((IDOMNode) docElement).getStartStructuredDocumentRegion() != null || ((IDOMNode) docElement).getEndStructuredDocumentRegion() != null)))) //$NON-NLS-1$
							rejectElements.add(JSP12Namespace.ElementName.ROOT);

						for (int j = 0; j < jspelements.getLength(); j++) {
							CMElementDeclaration ed = (CMElementDeclaration) jspelements.item(j);
							if (rejectElements.contains(ed.getNodeName()))
								continue;
							elementDecls.add(ed);
						}

					}
				}
				// No cm document (such as for the Document (a non-Element) node itself)
				else {
					CMNamedNodeMap jspElements = getDefaultJSPCMDocument((IDOMNode) node).getElements();
					int length = jspElements.getLength();
					for (int i = 0; i < length; i++) {
						elementDecls.add(jspElements.item(i));
					}
				}
			}
		}
		return elementDecls;
	}
	
	/**
	 * <p><b>NOTE: </b>This should be removed as soon as Bug 311961 is fixed</p>
	 * 
	 * For JSP files and segments, this is just the JSP document, but when
	 * editing tag files and their fragments, it should be the tag document.
	 * 
	 * It may also vary based on the model being edited in the future.
	 * 
	 * @return the default non-embedded CMDocument for the document being
	 *         edited.
	 */
	private CMDocument getDefaultJSPCMDocument(IDOMNode node) {
		// handle tag files here
		String contentType = node.getModel().getContentTypeIdentifier();
		if (ContentTypeIdForJSP.ContentTypeID_JSPTAG.equals(contentType))
			return JSPCMDocumentFactory.getCMDocument(CMDocType.TAG20_DOC_TYPE);

		CMDocument jcmdoc = null;
		String modelPath = node.getModel().getBaseLocation();
		if (modelPath != null && !IModelManager.UNMANAGED_MODEL.equals(modelPath)) {
			float version = DeploymentDescriptorPropertyCache.getInstance().getJSPVersion(new Path(modelPath));
			jcmdoc = JSPCMDocumentFactory.getCMDocument(version);
		}
		if (jcmdoc == null) {
			jcmdoc = JSPCMDocumentFactory.getCMDocument();
		}

		return jcmdoc;
	}
	
	private boolean isXMLFormat(Document doc) {
		if (doc == null)
			return false;
		Element docElement = doc.getDocumentElement();
		return docElement != null && ((docElement.getNodeName().equals("jsp:root")) ||
				((((IDOMNode) docElement).getStartStructuredDocumentRegion() == null &&
						((IDOMNode) docElement).getEndStructuredDocumentRegion() == null))); //$NON-NLS-1$
	}
}