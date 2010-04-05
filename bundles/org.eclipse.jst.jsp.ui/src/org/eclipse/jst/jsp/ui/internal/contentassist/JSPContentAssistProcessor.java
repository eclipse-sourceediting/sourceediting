/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jst.jsp.core.internal.contentmodel.JSPCMDocumentFactory;
import org.eclipse.jst.jsp.core.internal.contentmodel.TaglibController;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.TLDCMDocumentManager;
import org.eclipse.jst.jsp.core.internal.contenttype.DeploymentDescriptorPropertyCache;
import org.eclipse.jst.jsp.core.internal.document.PageDirectiveAdapter;
import org.eclipse.jst.jsp.core.internal.document.PageDirectiveAdapterFactory;
import org.eclipse.jst.jsp.core.internal.provisional.JSP11Namespace;
import org.eclipse.jst.jsp.core.internal.provisional.JSP12Namespace;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.jst.jsp.core.text.IJSPPartitions;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.jst.jsp.ui.internal.Logger;
import org.eclipse.jst.jsp.ui.internal.editor.JSPEditorPluginImageHelper;
import org.eclipse.jst.jsp.ui.internal.editor.JSPEditorPluginImages;
import org.eclipse.jst.jsp.ui.internal.templates.TemplateContextTypeIdsJSP;
import org.eclipse.wst.css.ui.internal.contentassist.CSSContentAssistProcessor;
import org.eclipse.wst.html.core.internal.contentmodel.JSPCMDocument;
import org.eclipse.wst.html.core.text.IHTMLPartitions;
import org.eclipse.wst.html.ui.StructuredTextViewerConfigurationHTML;
import org.eclipse.wst.html.ui.internal.contentassist.HTMLContentAssistProcessor;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.ltk.parser.BlockMarker;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredPartitioning;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.text.IStructuredPartitions;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.sse.ui.internal.IReleasable;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.sse.ui.internal.contentassist.IRelevanceCompletionProposal;
import org.eclipse.wst.sse.ui.internal.contentassist.IRelevanceConstants;
import org.eclipse.wst.sse.ui.internal.provisional.registry.AdapterFactoryProvider;
import org.eclipse.wst.sse.ui.internal.provisional.registry.AdapterFactoryRegistry;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.core.internal.parser.XMLSourceParser;
import org.eclipse.wst.xml.core.internal.provisional.contentmodel.CMDocType;
import org.eclipse.wst.xml.core.internal.provisional.contentmodel.CMDocumentTracker;
import org.eclipse.wst.xml.core.internal.provisional.contentmodel.CMNodeWrapper;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.core.text.IXMLPartitions;
import org.eclipse.wst.xml.ui.internal.contentassist.AbstractContentAssistProcessor;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.ProposalComparator;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLContentAssistProcessor;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLContentAssistUtilities;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLContentModelGenerator;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLRelevanceConstants;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Adds proposals not normally covered by the generic behavior with the
 * content model
 * 
 * @deprecated This class is no longer used locally and will be removed in the future
 * @see JSPStructuredContentAssistProcessor
 */
public class JSPContentAssistProcessor extends AbstractContentAssistProcessor {

	protected int depthCount = 0;
	protected ITextViewer fViewer = null;
	protected boolean useEmbeddedResults = true;
	protected boolean isInternalAdapter = false;
	protected HashMap fNameToProcessorMap = null;
	protected HashMap fPartitionToProcessorMap = null;
	private final ICompletionProposal[] EMPTY_PROPOSAL_SET = new ICompletionProposal[0];
	private JSPTemplateCompletionProcessor fTemplateProcessor = null;
	private List fTemplateContexts = new ArrayList();
	private IContentAssistProcessor fJSContentAssistProcessor;

	public JSPContentAssistProcessor() {
		super();
		initNameToProcessorMap();
		initPartitionToProcessorMap();
	}

	/**
	 * init map for extra content assist processors (useBean,
	 * get/setProperty). points [tagname > processor]
	 */
	protected void initNameToProcessorMap() {
		fNameToProcessorMap = new HashMap();
		JSPPropertyContentAssistProcessor jspPropertyCAP = new JSPPropertyContentAssistProcessor();
		fNameToProcessorMap.put(JSP11Namespace.ElementName.SETPROPERTY, jspPropertyCAP);
		fNameToProcessorMap.put(JSP11Namespace.ElementName.GETPROPERTY, jspPropertyCAP);
		fNameToProcessorMap.put(JSP11Namespace.ElementName.USEBEAN, new JSPUseBeanContentAssistProcessor());
		fNameToProcessorMap.put(JSP11Namespace.ElementName.DIRECTIVE_TAGLIB, new JSPTaglibDirectiveContentAssistProcessor());
	}

	/**
	 * int map that points [partition > processor]. This takes place of
	 * embedded adapters for now.
	 */
	protected void initPartitionToProcessorMap() {
		fPartitionToProcessorMap = new HashMap();
		HTMLContentAssistProcessor htmlProcessor = new HTMLContentAssistProcessor();
		JSPJavaContentAssistProcessor jspJavaProcessor = new JSPJavaContentAssistProcessor();
		XMLContentAssistProcessor xmlProcessor = new XMLContentAssistProcessor();
		IContentAssistProcessor javascriptProcessor = getJSContentAssistProcessor();

		fPartitionToProcessorMap.put(IHTMLPartitions.HTML_DEFAULT, htmlProcessor);
		fPartitionToProcessorMap.put(IXMLPartitions.XML_DEFAULT, xmlProcessor);
		fPartitionToProcessorMap.put(IStructuredPartitions.DEFAULT_PARTITION, htmlProcessor);
		fPartitionToProcessorMap.put(IJSPPartitions.JSP_DEFAULT, jspJavaProcessor);
		fPartitionToProcessorMap.put(IJSPPartitions.JSP_DIRECTIVE, xmlProcessor);
		fPartitionToProcessorMap.put(IHTMLPartitions.HTML_COMMENT, htmlProcessor);
		fPartitionToProcessorMap.put(IJSPPartitions.JSP_DEFAULT_EL, jspJavaProcessor);
		if (javascriptProcessor != null) {
			fPartitionToProcessorMap.put(IJSPPartitions.JSP_CONTENT_JAVASCRIPT, javascriptProcessor);
			fPartitionToProcessorMap.put(IHTMLPartitions.SCRIPT, javascriptProcessor); // default
		}
		// to
		// javascript
		// for
		// all
		// script
	}

	protected void addCommentProposal(ContentAssistRequest contentAssistRequest) {
		// do nothing
	}

	protected void addDocTypeProposal(ContentAssistRequest contentAssistRequest) {
		// do nothing
	}

	protected void addEmptyDocumentProposals(ContentAssistRequest contentAssistRequest) {
		if (ContentTypeIdForJSP.ContentTypeID_JSPTAG.equals(((IDOMNode) contentAssistRequest.getNode()).getModel().getContentTypeIdentifier())) {
			addTemplates(contentAssistRequest, TemplateContextTypeIdsJSP.NEW_TAG);
		}
		else {
			addTemplates(contentAssistRequest, TemplateContextTypeIdsJSP.NEW);
		}

		super.addEmptyDocumentProposals(contentAssistRequest);
		addTagInsertionProposals(contentAssistRequest, 0);
	}

	protected void addEndTagNameProposals(ContentAssistRequest contentAssistRequest) {
		// do nothing
	}

	protected void addPCDATAProposal(String nodeName, ContentAssistRequest contentAssistRequest) {
		// do nothing
	}

	protected void addStartDocumentProposals(ContentAssistRequest contentAssistRequest) {
		// do nothing
	}

	protected void addTagCloseProposals(ContentAssistRequest contentAssistRequest) {
		// do nothing
	}

	protected void addXMLProposal(ContentAssistRequest contentAssistRequest) {
		// do nothing
	}

	protected void addEndTagProposals(ContentAssistRequest contentAssistRequest) {
		// do nothing
	}

	protected void addAttributeNameProposals(ContentAssistRequest contentAssistRequest) {
		addTemplates(contentAssistRequest, TemplateContextTypeIdsJSP.ATTRIBUTE);
	}

	/**
	 * add proposals for tags in attribute values
	 */
	protected void addAttributeValueProposals(ContentAssistRequest contentAssistRequest) {
		addTemplates(contentAssistRequest, TemplateContextTypeIdsJSP.ATTRIBUTE_VALUE);

		IDOMNode node = (IDOMNode) contentAssistRequest.getNode();

		// add JSP extra proposals from JSPBeanInfoContentAssistProcessor
		// JSPPropertyContentAssistProcessor

		// 2.1
		// get results from JSPUseBean and JSPProperty here
		// (look up processor in a map based on node name)
		JSPDummyContentAssistProcessor extraProcessor = (JSPDummyContentAssistProcessor) fNameToProcessorMap.get(node.getNodeName());
		if (extraProcessor != null && contentAssistRequest != null) {
			extraProcessor.addAttributeValueProposals(contentAssistRequest);
		}

		ModelQuery mq = ModelQueryUtil.getModelQuery(node.getOwnerDocument());
		if (mq != null) {
			CMDocument doc = mq.getCorrespondingCMDocument(node);
			// this shouldn't have to have the prefix coded in
			if (doc instanceof JSPCMDocument || doc instanceof CMNodeWrapper || node.getNodeName().startsWith("jsp:")) //$NON-NLS-1$
				return;
		}

		// Find the attribute name for which this position should have a value
		IStructuredDocumentRegion open = node.getFirstStructuredDocumentRegion();
		ITextRegionList openRegions = open.getRegions();
		int i = openRegions.indexOf(contentAssistRequest.getRegion());
		if (i < 0)
			return;
		ITextRegion nameRegion = null;
		while (i >= 0) {
			nameRegion = openRegions.get(i--);
			if (nameRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME)
				break;
		}
		
		// on an empty value, add all the JSP and taglib tags
		CMElementDeclaration elementDecl = getCMElementDeclaration(node);
		if (nameRegion != null && elementDecl != null) {
			String attributeName = open.getText(nameRegion);
			if (attributeName != null) {
				String currentValue = node.getAttributes().getNamedItem(attributeName).getNodeValue();
				
				if(currentValue == null || currentValue.length() == 0) { //$NON-NLS-1$
					List additionalElements = ModelQueryUtil.getModelQuery(node.getOwnerDocument()).getAvailableContent((Element) node, elementDecl, ModelQuery.INCLUDE_ATTRIBUTES);
					for (i = 0; i < additionalElements.size(); i++) {
						Object additionalElement = additionalElements.get(i);
						if(additionalElement instanceof CMElementDeclaration) {
							CMElementDeclaration ed = (CMElementDeclaration) additionalElement;
	
							String tagname = getContentGenerator().getRequiredName(node, ed);
							StringBuffer contents = new StringBuffer("\""); //$NON-NLS-1$
							getContentGenerator().generateTag(node, ed, contents);
							contents.append('"'); //$NON-NLS-1$
							CustomCompletionProposal proposal = new CustomCompletionProposal(contents.toString(), contentAssistRequest.getReplacementBeginPosition(), contentAssistRequest.getReplacementLength(), contents.length(), JSPEditorPluginImageHelper.getInstance().getImage(JSPEditorPluginImages.IMG_OBJ_TAG_GENERIC), tagname, null, null, XMLRelevanceConstants.R_JSP_ATTRIBUTE_VALUE);
							contentAssistRequest.addProposal(proposal);
						}
					}
				
				}
			}
		}
		else if (contentAssistRequest.getRegion().getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
			try {
				// Create a new model for Content Assist to operate on. This
				// will simulate
				// a full Document and then adjust the offset numbers in the
				// list of results.
				IStructuredModel internalModel = null;
				IModelManager mmanager = StructuredModelManager.getModelManager();
				internalModel = mmanager.createUnManagedStructuredModelFor(ContentTypeIdForJSP.ContentTypeID_JSP);
				IDOMNode xmlNode = null;
				IDOMModel xmlOuterModel = null;
				if (contentAssistRequest.getNode() instanceof IDOMNode) {
					xmlNode = (IDOMNode) contentAssistRequest.getNode();
					xmlOuterModel = xmlNode.getModel();
					internalModel.setResolver(xmlOuterModel.getResolver());
					internalModel.setBaseLocation(xmlOuterModel.getBaseLocation());
				}
				String contents = StringUtils.strip(contentAssistRequest.getText());
				if (xmlNode != null && contents != null) {
					int additionalShifts = 0;
					// Be sure that custom tags from taglibs also show up
					// by
					// adding taglib declarations to the internal model.
					TLDCMDocumentManager mgr = TaglibController.getTLDCMDocumentManager(xmlOuterModel.getStructuredDocument());
					if (mgr != null) {
						List trackers = mgr.getCMDocumentTrackers(contentAssistRequest.getReplacementBeginPosition());
						if (trackers != null) {
							for (i = 0; i < trackers.size(); i++) {
								CMDocumentTracker tracker = (CMDocumentTracker) trackers.get(i);
								String declaration = tracker.getStructuredDocumentRegion().getText();
								if (declaration != null) {
									contents = declaration + contents;
									additionalShifts += declaration.length();
								}
							}
						}
					}
					// Also copy any jsp:useBean tags so that
					// jsp:[gs]etProperty will function
					Document doc = null;
					if (contentAssistRequest.getNode().getNodeType() == Node.DOCUMENT_NODE)
						doc = (Document) node;
					else
						doc = node.getOwnerDocument();
					NodeList useBeans = doc.getElementsByTagName(JSP12Namespace.ElementName.USEBEAN);
					for (int k = 0; k < useBeans.getLength(); k++) {
						IDOMNode useBean = (IDOMNode) useBeans.item(k);
						if (useBean.getStartOffset() < contentAssistRequest.getReplacementBeginPosition()) {
							StringBuffer useBeanText = new StringBuffer("<jsp:useBean"); //$NON-NLS-1$
							for (int j = 0; j < useBean.getAttributes().getLength(); j++) {
								Attr attr = (Attr) useBean.getAttributes().item(j);
								useBeanText.append(' ');
								useBeanText.append(attr.getName());
								useBeanText.append("=\""); //$NON-NLS-1$
								useBeanText.append(attr.getValue());
								useBeanText.append('"');
							}
							useBeanText.append("/>"); //$NON-NLS-1$
							additionalShifts += useBeanText.length();
							contents = useBeanText.toString() + contents;
						}
					}
					internalModel.getStructuredDocument().set(contents);
					int internalOffset = 0;
					boolean quoted = false;
					// if quoted, use position inside and shift by one
					if (contentAssistRequest.getMatchString().length() > 0 && (contentAssistRequest.getMatchString().charAt(0) == '\'' || contentAssistRequest.getMatchString().charAt(0) == '"')) {
						internalOffset = contentAssistRequest.getMatchString().length() - 1 + additionalShifts;
						quoted = true;
					}
					// if unquoted, use position inside
					else if (contentAssistRequest.getMatchString().length() > 0 && contentAssistRequest.getMatchString().charAt(0) == '<')
						internalOffset = contentAssistRequest.getMatchString().length() + additionalShifts;
					else
						internalOffset = contentAssistRequest.getReplacementBeginPosition() - contentAssistRequest.getStartOffset() + additionalShifts;
					depthCount++;
					IndexedRegion internalNode = null;
					int tmpOffset = internalOffset;
					while (internalNode == null && tmpOffset >= 0)
						internalNode = internalModel.getIndexedRegion(tmpOffset--);

					if (internalModel.getFactoryRegistry() != null) {
						// set up the internal model
						if (internalModel.getFactoryRegistry().getFactoryFor(PageDirectiveAdapter.class) == null) {
							internalModel.getFactoryRegistry().addFactory(new PageDirectiveAdapterFactory());
						}
						PageDirectiveAdapter outerEmbeddedTypeAdapter = (PageDirectiveAdapter) xmlOuterModel.getDocument().getAdapterFor(PageDirectiveAdapter.class);
						PageDirectiveAdapter internalEmbeddedTypeAdapter = (PageDirectiveAdapter) ((INodeNotifier) ((Node) internalNode).getOwnerDocument()).getAdapterFor(PageDirectiveAdapter.class);
						internalEmbeddedTypeAdapter.setEmbeddedType(outerEmbeddedTypeAdapter.getEmbeddedType());
					}

					AdapterFactoryRegistry adapterRegistry = JSPUIPlugin.getDefault().getAdapterFactoryRegistry();
					Iterator adapterList = adapterRegistry.getAdapterFactories();
					// And all those appropriate for this particular type
					// of content
					while (adapterList.hasNext()) {
						try {
							AdapterFactoryProvider provider = (AdapterFactoryProvider) adapterList.next();
							if (provider.isFor(internalModel.getModelHandler())) {
								provider.addAdapterFactories(internalModel);
							}
						}
						catch (Exception e) {
							Logger.logException(e);
						}
					}

					/**
					 * the internal adapter does all the real work of using
					 * the JSP content model to form proposals
					 */
					ICompletionProposal[] results = null;
					depthCount--;
					if (results != null) {
						for (i = 0; i < results.length; i++) {
							contentAssistRequest.addProposal(new CustomCompletionProposal(((CustomCompletionProposal) results[i]).getReplacementString(), ((CustomCompletionProposal) results[i]).getReplacementOffset() - additionalShifts + contentAssistRequest.getStartOffset() + (quoted ? 1 : 0), ((CustomCompletionProposal) results[i]).getReplacementLength(), ((CustomCompletionProposal) results[i]).getCursorPosition(), results[i].getImage(), results[i].getDisplayString(), ((CustomCompletionProposal) results[i]).getContextInformation(), ((CustomCompletionProposal) results[i]).getAdditionalProposalInfo(), (results[i] instanceof IRelevanceCompletionProposal) ? ((IRelevanceCompletionProposal) results[i]).getRelevance() : IRelevanceConstants.R_NONE));
						}
					}
				}
			}
			catch (Exception e) {
				Logger.logException("Error in embedded JSP Content Assist", e); //$NON-NLS-1$
			}
		}


	}

	/**
	 * For JSP files and segments, this is just the JSP
	 *         document, but when editing tag files and their fragments, it
	 *         should be the tag document.
	 * 
	 * It may also vary based on the model being edited in the future.
	 * 
	 * @return the default non-embedded CMDocument for the document being
	 *         edited. 
	 */
	CMDocument getDefaultJSPCMDocument(IDOMNode node) {
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

	protected void init() {
		super.init();
	}

	public void setErrorMessage(String errorMessage) {
		if (depthCount == 0)
			fErrorMessage = errorMessage;
	}

	/**
	 * This method is acting as a "catch all" for pulling together content
	 * assist proposals from different Processors when document partitioning
	 * alone couldn't determine definitively what content assist should show
	 * up at that particular position in the document
	 * 
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeCompletionProposals(ITextViewer,
	 *      int)
	 */
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int documentPosition) {
		fTemplateContexts.clear();

		IStructuredDocumentRegion sdRegion = ContentAssistUtils.getStructuredDocumentRegion(viewer, documentPosition);
		fViewer = viewer;
		ICompletionProposal[] jspResults = EMPTY_PROPOSAL_SET;
		ICompletionProposal[] embeddedResults = EMPTY_PROPOSAL_SET;

		// check the actual partition type
		String partitionType = getPartitionType(viewer, documentPosition);
		IStructuredDocument structuredDocument = (IStructuredDocument) viewer.getDocument();

		IStructuredDocumentRegion fn = structuredDocument.getRegionAtCharacterOffset(documentPosition);

		// ////////////////////////////////////////////////////////////////////////////
		// ANOTHER WORKAROUND UNTIL PARTITIONING TAKES CARE OF THIS
		// check for xml-jsp tags...
		if (partitionType == IJSPPartitions.JSP_DIRECTIVE && fn != null) {
			IStructuredDocumentRegion possibleXMLJSP = ((fn.getType() == DOMRegionContext.XML_CONTENT) && fn.getPrevious() != null) ? fn.getPrevious() : fn;
			ITextRegionList regions = possibleXMLJSP.getRegions();
			if (regions.size() > 1) {
				// check bounds cases
				ITextRegion xmlOpenOrClose = regions.get(0);
				if (xmlOpenOrClose.getType() == DOMRegionContext.XML_TAG_OPEN && documentPosition == possibleXMLJSP.getStartOffset()) {
					// do regular jsp content assist
				}
				else if (xmlOpenOrClose.getType() == DOMRegionContext.XML_END_TAG_OPEN && documentPosition > possibleXMLJSP.getStartOffset()) {
					// do regular jsp content assist
				}
				else {
					// possible xml-jsp
					ITextRegion nameRegion = regions.get(1);
					String name = possibleXMLJSP.getText(nameRegion);
					if (name.equals("jsp:scriptlet") || name.equals("jsp:expression") || name.equals("jsp:declaration")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						return getJSPJavaCompletionProposals(viewer, documentPosition);
					}
				}
			}
		}

		// ////////////////////////////////////////////////////////////////////////////
		// ** THIS IS A TEMP FIX UNTIL PARTITIONING TAKES CARE OF THIS...
		// check for XML-JSP in a <script> region
		if (partitionType == IJSPPartitions.JSP_CONTENT_JAVASCRIPT || partitionType == IHTMLPartitions.SCRIPT) {
			// fn should be block text
			IStructuredDocumentRegion decodedSDRegion = decodeScriptBlock(fn.getFullText());
			// System.out.println("decoded > " +
			// blockOfText.substring(decodedSDRegion.getStartOffset(),
			// decodedSDRegion.getEndOffset()));
			if (decodedSDRegion != null) {
				IStructuredDocumentRegion sdr = decodedSDRegion;
				while (sdr != null) {
					// System.out.println("sdr " + sdr.getType());
					// System.out.println("sdr > " +
					// blockOfText.substring(sdr.getStartOffset(),
					// sdr.getEndOffset()));
					if (sdr.getType() == DOMJSPRegionContexts.JSP_CONTENT) {
						if (documentPosition >= fn.getStartOffset() + sdr.getStartOffset() && documentPosition <= fn.getStartOffset() + sdr.getEndOffset()) {
							return getJSPJavaCompletionProposals(viewer, documentPosition);
						}
					}
					else if (sdr.getType() == DOMRegionContext.XML_TAG_NAME) {
						if (documentPosition > fn.getStartOffset() + sdr.getStartOffset() && documentPosition < fn.getStartOffset() + sdr.getEndOffset()) {
							return EMPTY_PROPOSAL_SET;
						}
						else if (documentPosition == fn.getStartOffset() + sdr.getEndOffset() && sdr.getNext() != null && sdr.getNext().getType() == DOMJSPRegionContexts.JSP_CONTENT) {
							// the end of an open tag <script>
							// <jsp:scriptlet>| blah </jsp:scriptlet>
							return getJSPJavaCompletionProposals(viewer, documentPosition);
						}
						else if (documentPosition == fn.getStartOffset() + sdr.getStartOffset() && sdr.getPrevious() != null && sdr.getPrevious().getType() == DOMRegionContext.XML_TAG_NAME) {
							return getJSPJavaCompletionProposals(viewer, documentPosition);
						}
					}
					sdr = sdr.getNext();
				}
			}
		}
		// /////////////////////////////////////////////////////////////////////////
		// check special JSP delimiter cases
		if (fn != null && partitionType == IJSPPartitions.JSP_CONTENT_DELIMITER) {
			IStructuredDocumentRegion fnDelim = fn;

			// if it's a nested JSP region, need to get the correct
			// StructuredDocumentRegion
			// not sure why this check was there...
			// if (fnDelim.getType() == XMLRegionContext.BLOCK_TEXT) {
			Iterator blockRegions = fnDelim.getRegions().iterator();
			ITextRegion temp = null;
			ITextRegionContainer trc;
			while (blockRegions.hasNext()) {
				temp = (ITextRegion) blockRegions.next();
				// we hit a nested
				if (temp instanceof ITextRegionContainer) {
					trc = (ITextRegionContainer) temp;
					// it's in this region
					if (documentPosition >= trc.getStartOffset() && documentPosition < trc.getEndOffset()) {
						Iterator nestedJSPRegions = trc.getRegions().iterator();
						while (nestedJSPRegions.hasNext()) {
							temp = (ITextRegion) nestedJSPRegions.next();
							if (XMLContentAssistUtilities.isJSPOpenDelimiter(temp.getType()) && documentPosition == trc.getStartOffset(temp)) {
								// HTML content assist
								// we actually want content assist for the
								// previous type of region,
								// well get those proposals from the embedded
								// adapter
								if (documentPosition > 0) {
									partitionType = getPartitionType(viewer, documentPosition - 1);
									break;
								}
							}
							else if (XMLContentAssistUtilities.isJSPCloseDelimiter(temp.getType()) && documentPosition == trc.getStartOffset(temp)) {
								// JSP content assist
								return getJSPJavaCompletionProposals(viewer, documentPosition);
							}
						}
					}
				}
				// }
			}

			// take care of XML-JSP delimter cases
			if (XMLContentAssistUtilities.isXMLJSPDelimiter(fnDelim)) {
				// since it's a delimiter, we know it's a ITextRegionContainer
				ITextRegion firstRegion = fnDelim.getRegions().get(0);
				if (fnDelim.getStartOffset() == documentPosition && (firstRegion.getType() == DOMRegionContext.XML_TAG_OPEN)) {
					// |<jsp:scriptlet> </jsp:scriptlet>
					// (pa) commented out so that we get regular behavior JSP
					// macros etc...
					// return getHTMLCompletionProposals(viewer,
					// documentPosition);
				}
				else if (fnDelim.getStartOffset() == documentPosition && (firstRegion.getType() == DOMRegionContext.XML_END_TAG_OPEN)) {
					// <jsp:scriptlet> |</jsp:scriptlet>
					// check previous partition type to see if it's JAVASCRIPT
					// if it is, we're just gonna let the embedded JAVASCRIPT
					// adapter get the proposals
					if (documentPosition > 0) {
						String checkType = getPartitionType(viewer, documentPosition - 1);
						if (checkType != IJSPPartitions.JSP_CONTENT_JAVASCRIPT) { // this
							// check
							// is
							// failing
							// for
							// XML-JSP
							// (region
							// is
							// not
							// javascript...)
							return getJSPJavaCompletionProposals(viewer, documentPosition);
						}
						partitionType = IJSPPartitions.JSP_CONTENT_JAVASCRIPT;
					}
				}
				else if ((firstRegion.getType() == DOMRegionContext.XML_TAG_OPEN) && documentPosition >= fnDelim.getEndOffset()) {
					// anything else inbetween
					return getJSPJavaCompletionProposals(viewer, documentPosition);
				}
			}
			else if (XMLContentAssistUtilities.isJSPDelimiter(fnDelim)) {
				// the delimiter <%, <%=, <%!, ...
				if (XMLContentAssistUtilities.isJSPCloseDelimiter(fnDelim)) {
					if (documentPosition == fnDelim.getStartOffset()) {
						// check previous partition type to see if it's
						// JAVASCRIPT
						// if it is, we're just gonna let the embedded
						// JAVASCRIPT adapter get the proposals
						if (documentPosition > 0) {
							String checkType = getPartitionType(viewer, documentPosition - 1);
							if (checkType != IJSPPartitions.JSP_CONTENT_JAVASCRIPT) {
								return getJSPJavaCompletionProposals(viewer, documentPosition);
							}
							partitionType = IJSPPartitions.JSP_CONTENT_JAVASCRIPT;
						}
					}
				}
				else if (XMLContentAssistUtilities.isJSPOpenDelimiter(fnDelim)) {
					// if it's the first position of open delimiter
					// use embedded HTML results
					if (documentPosition == fnDelim.getStartOffset()) {
						embeddedResults = getHTMLCompletionProposals(viewer, documentPosition);
					}
					else if (documentPosition == fnDelim.getEndOffset()) {
						// it's at the EOF <%|
						return getJSPJavaCompletionProposals(viewer, documentPosition);
					}
				}
			}
		}

		// need to check if it's JSP region inside of CDATA w/ no region
		// <![CDATA[ <%|%> ]]>
		// or a comment region
		// <!-- <% |%> -->
		if (fn != null && (fn.getType() == DOMRegionContext.XML_CDATA_TEXT || fn.getType() == DOMRegionContext.XML_COMMENT_TEXT)) {
			if (fn instanceof ITextRegionContainer) {
				Object[] cdataRegions = fn.getRegions().toArray();
				ITextRegion r = null;
				ITextRegion jspRegion = null;
				for (int i = 0; i < cdataRegions.length; i++) {
					r = (ITextRegion) cdataRegions[i];
					if (r instanceof ITextRegionContainer) {
						// CDATA embedded container, or comment container
						Object[] jspRegions = ((ITextRegionContainer) r).getRegions().toArray();
						for (int j = 0; j < jspRegions.length; j++) {
							jspRegion = (ITextRegion) jspRegions[j];
							if (jspRegion.getType() == DOMJSPRegionContexts.JSP_CLOSE) {
								if (sdRegion.getStartOffset(jspRegion) == documentPosition)
									return getJSPJavaCompletionProposals(viewer, documentPosition);
							}
						}
					}
				}

			}
		}

		// check if it's in an attribute value, if so, don't add CDATA
		// proposal
		ITextRegion attrContainer = (fn != null) ? fn.getRegionAtCharacterOffset(documentPosition) : null;
		if (attrContainer != null && attrContainer instanceof ITextRegionContainer) {
			if (attrContainer.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
				// test location of the cursor
				// return null if it's in the middle of an open/close
				// delimeter
				Iterator attrRegions = ((ITextRegionContainer) attrContainer).getRegions().iterator();
				ITextRegion testRegion = null;
				while (attrRegions.hasNext()) {
					testRegion = (ITextRegion) attrRegions.next();
					// need to check for other valid attribute regions
					if (XMLContentAssistUtilities.isJSPOpenDelimiter(testRegion.getType())) {
						if (!(((ITextRegionContainer) attrContainer).getEndOffset(testRegion) <= documentPosition))
							return EMPTY_PROPOSAL_SET;
					}
					else if (XMLContentAssistUtilities.isJSPCloseDelimiter(testRegion.getType())) {
						if (!(((ITextRegionContainer) attrContainer).getStartOffset(testRegion) >= documentPosition))
							return EMPTY_PROPOSAL_SET;
					}
				}
				// TODO: handle non-Java code such as nested tags
				if (testRegion.getType().equals(DOMJSPRegionContexts.JSP_CONTENT))
					return getJSPJavaCompletionProposals(viewer, documentPosition);
				return EMPTY_PROPOSAL_SET;
			}
		}

		IContentAssistProcessor p = (IContentAssistProcessor) fPartitionToProcessorMap.get(partitionType);
		if (p != null) {
			embeddedResults = p.computeCompletionProposals(viewer, documentPosition);
			// get bean methods, objects, and constants if there are any...
			if (partitionType == IJSPPartitions.JSP_CONTENT_JAVASCRIPT || partitionType == IHTMLPartitions.SCRIPT) {
				ICompletionProposal[] beanResults = getJSPJavaBeanProposals(viewer, documentPosition);
				if (beanResults != null && beanResults.length > 0) {
					ICompletionProposal[] added = new ICompletionProposal[beanResults.length + embeddedResults.length];
					System.arraycopy(beanResults, 0, added, 0, beanResults.length);
					System.arraycopy(embeddedResults, 0, added, beanResults.length, embeddedResults.length);
					embeddedResults = added;
				}
			}
		}
		else {
			// the partition type is probably not mapped
		}

		// fix for:
		// HTML content assist give JSP tags in between empty script tags
		if (!((getJSContentAssistProcessor() != null && getJSContentAssistProcessor().getClass().isInstance(p)) || p instanceof CSSContentAssistProcessor)) {
			fTemplateContexts.clear();
			jspResults = super.computeCompletionProposals(viewer, documentPosition);
		}
		
		//merge the embedded results
		if (useEmbeddedResults && embeddedResults != null && embeddedResults.length > 0) {
			jspResults = merge(jspResults, embeddedResults);
		}
		if (jspResults == null)
			jspResults = EMPTY_PROPOSAL_SET;
		setErrorMessage(jspResults.length == 0 ? UNKNOWN_CONTEXT : null);

		// fix for:
		// check for |<%-- --%> first position of jsp comment
		if (partitionType == IJSPPartitions.JSP_COMMENT) {
			if (sdRegion.getStartOffset() == documentPosition) {
				ICompletionProposal[] htmlResults = getHTMLCompletionProposals(viewer, documentPosition);
				jspResults = merge(jspResults, htmlResults);
			}
		}

		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=86656
		if (partitionType == IJSPPartitions.JSP_DIRECTIVE) {
			ICompletionProposal[] importProposals = getImportProposals(viewer, documentPosition);
			if (importProposals.length > 0)
				jspResults = merge(jspResults, importProposals);
		}
		return jspResults;
	}

	private ICompletionProposal[] getImportProposals(ITextViewer viewer, int documentPosition) {
		List importProposals = new ArrayList();
		ICompletionProposal[] proposals = getJSPJavaCompletionProposals(viewer, documentPosition);
		for (int i = 0; i < proposals.length; i++) {
			if (proposals[i] instanceof JSPCompletionProposal) {

				ICompletionProposal importProposal = adjustImportProposal((JSPCompletionProposal) proposals[i]);
				importProposals.add(importProposal);
			}
		}
		return (ICompletionProposal[]) importProposals.toArray(new ICompletionProposal[importProposals.size()]);
	}


	private ICompletionProposal adjustImportProposal(JSPCompletionProposal importProposal) {

		// just need to remove the ";"
		// and adjust offsets for the change
		String newReplace = importProposal.getReplacementString().replaceAll(";", ""); //$NON-NLS-1$ //$NON-NLS-2$
		importProposal.setReplacementString(newReplace);

		String newDisplay = importProposal.getDisplayString().replaceAll(";", ""); //$NON-NLS-1$ //$NON-NLS-2$
		importProposal.setDisplayString(newDisplay);

		int newReplacementLength = importProposal.getReplacementLength() - 1;
		if (newReplacementLength >= 0)
			importProposal.setReplacementLength(newReplacementLength);

		int newCursorPosition = importProposal.getCursorPosition() - 1;
		importProposal.setCursorPosition(newCursorPosition);

		return importProposal;
	}

	/**
	 * Adds 2 arrays of {@link ICompletionProposal}s to a {@link TreeSet}
	 * eliminating duplicates and sorting with a {@link ProposalComparator}
	 * then returning the new merged, filtered, sorted, array of {@link ICompletionProposal}s.
	 * 
	 * @param proposalsOne
	 * @param proposalsTwo
	 * @return a new merged, filtered, sorted array of {@link ICompletionProposal}s created from
	 * the two given arrays of {@link ICompletionProposal}s.
	 */
	private ICompletionProposal[] merge(ICompletionProposal[] proposalsOne, ICompletionProposal[] proposalsTwo) {
		Set results = new TreeSet(new ProposalComparator());

		if (proposalsOne != null) {
			for (int i = 0; i < proposalsOne.length; i++)
				results.add(proposalsOne[i]);
		}
		if (proposalsTwo != null) {
			for (int i = 0; i < proposalsTwo.length; i++)
				results.add(proposalsTwo[i]);
		}

		return (ICompletionProposal[]) results.toArray(new ICompletionProposal[results.size()]);
	}

	private IContentAssistProcessor getJSContentAssistProcessor() {
		if (fJSContentAssistProcessor == null) {
			fJSContentAssistProcessor = new StructuredTextViewerConfigurationHTML().getContentAssistant(null).getContentAssistProcessor(IHTMLPartitions.SCRIPT);
		}
		return fJSContentAssistProcessor;
	}

	/*
	 * This method will return JSPJava Proposals that are relevant to any java
	 * beans that in scope at the documentPosition
	 * 
	 * TODO (pa) are taglib vars getting filtered?
	 * 
	 * @param viewer @param documentPosition @return ICompletionProposal[]
	 */
	private ICompletionProposal[] getJSPJavaBeanProposals(ITextViewer viewer, int documentPosition) {
		ICompletionProposal[] regularJSPResults = getJSPJavaCompletionProposals(viewer, documentPosition);
		Vector filteredProposals = new Vector();
		ICompletionProposal[] finalResults = EMPTY_PROPOSAL_SET;
		for (int i = 0; i < regularJSPResults.length; i++) {
			ICompletionProposal test = regularJSPResults[i];

			System.out.println("proposal > " + test.getDisplayString()); //$NON-NLS-1$
			System.out.println("relevance > " + ((CustomCompletionProposal) test).getRelevance()); //$NON-NLS-1$

			if (isRelevanceAllowed(((CustomCompletionProposal) test).getRelevance())) {
				filteredProposals.add(test);
			}
		}
		if (filteredProposals.size() > 0) {
			finalResults = new ICompletionProposal[filteredProposals.size()];
			Iterator it = filteredProposals.iterator();
			int j = 0;
			while (it.hasNext()) {
				finalResults[j++] = (ICompletionProposal) it.next();
			}
		}
		return finalResults;
	}

	// These are the only things I'm allowing for use bean if the language is
	// JAVASCRIPT
	// I'm filtering based on JavaContentAssistProposal relevance
	//
	// 485 > method that belongs to the bean
	// 486 > bean object
	// 386 > bean CONSTANT
	private boolean isRelevanceAllowed(int relevance) {
		return (relevance == 485 || relevance == 486 || relevance == 326);
	}


	/**
	 * 
	 * @param viewer
	 * @param documentPosition
	 * @return ICompletionProposal[]
	 */
	private ICompletionProposal[] getHTMLCompletionProposals(ITextViewer viewer, int documentPosition) {

		IContentAssistProcessor p = (IContentAssistProcessor) fPartitionToProcessorMap.get(IHTMLPartitions.HTML_DEFAULT);
		return p.computeCompletionProposals(viewer, documentPosition);
	}

	/**
	 * 
	 * @param viewer
	 * @param documentPosition
	 * @return ICompletionProposal[]
	 */
	protected ICompletionProposal[] getJSPJavaCompletionProposals(ITextViewer viewer, int documentPosition) {
		JSPJavaContentAssistProcessor p = (JSPJavaContentAssistProcessor) fPartitionToProcessorMap.get(IJSPPartitions.JSP_DEFAULT);
		return p.computeCompletionProposals(viewer, documentPosition);
	}

	/**
	 * @param viewer
	 * @param documentPosition
	 * @return String
	 */
	protected String getPartitionType(ITextViewer viewer, int documentPosition) {
		String partitionType = null;
		try {
			if (viewer instanceof ITextViewerExtension5)
				partitionType = TextUtilities.getContentType(viewer.getDocument(), IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING, ((ITextViewerExtension5) viewer).modelOffset2WidgetOffset(documentPosition), false);
			else
				partitionType = TextUtilities.getContentType(viewer.getDocument(), IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING, documentPosition, false);
		}
		catch (BadLocationException e) {
			partitionType = IDocument.DEFAULT_CONTENT_TYPE;
		}
		return partitionType;
	}

	/*
	 * ** TEMP WORKAROUND FOR CMVC 241882 Takes a String and blocks out
	 * jsp:scriptlet, jsp:expression, and jsp:declaration @param blockText
	 * @return
	 */
	private IStructuredDocumentRegion decodeScriptBlock(String blockText) {
		XMLSourceParser parser = new XMLSourceParser();
		// use JSP_CONTENT for region type
		parser.addBlockMarker(new BlockMarker("jsp:scriptlet", null, DOMJSPRegionContexts.JSP_CONTENT, false, false)); //$NON-NLS-1$
		parser.addBlockMarker(new BlockMarker("jsp:expression", null, DOMJSPRegionContexts.JSP_CONTENT, false, false)); //$NON-NLS-1$
		parser.addBlockMarker(new BlockMarker("jsp:declaration", null, DOMJSPRegionContexts.JSP_CONTENT, false, false)); //$NON-NLS-1$
		parser.reset(blockText);
		return parser.getDocumentRegions();
	}

	/*
	 * @see ContentAssistAdapter#computeContextInformation(ITextViewer, int,
	 *      IndexedRegion)
	 */
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int documentOffset, IndexedRegion indexedNode) {
		return super.computeContextInformation(viewer, documentOffset);
	}

	/*
	 * @see ContentAssistAdapter#getContextInformationAutoActivationCharacters()
	 */
	public char[] getContextInformationAutoActivationCharacters() {
		return super.getContextInformationAutoActivationCharacters();
	}


	public char[] getCompletionProposalAutoActivationCharacters() {
		IContentAssistProcessor p = (IContentAssistProcessor) fPartitionToProcessorMap.get(IHTMLPartitions.HTML_DEFAULT);
		return p.getCompletionProposalAutoActivationCharacters();
	}

	/*
	 * @see ContentAssistAdapter#getContextInformationValidator()
	 */
	public IContextInformationValidator getContextInformationValidator() {
		return super.getContextInformationValidator();
	}

	protected boolean isXMLFormat(Document doc) {
		if (doc == null)
			return false;
		Element docElement = doc.getDocumentElement();
		return docElement != null && ((docElement.getNodeName().equals("jsp:root")) || ((((IDOMNode) docElement).getStartStructuredDocumentRegion() == null && ((IDOMNode) docElement).getEndStructuredDocumentRegion() == null))); //$NON-NLS-1$
	}

	/*
	 * @see ContentAssistAdapter#release()
	 */
	public void release() {
		super.release();
		// release *ContentAssistProcessors in maps
		// CMVC 254023
		releasePartitionToProcessorMap();
		releaseNameToProcessorMap();
	}

	protected void releasePartitionToProcessorMap() {
		releaseMap(fPartitionToProcessorMap);
	}

	protected void releaseNameToProcessorMap() {
		releaseMap(fNameToProcessorMap);
	}

	protected void releaseMap(HashMap map) {
		if (map != null) {
			if (!map.isEmpty()) {
				Iterator it = map.keySet().iterator();
				Object key = null;
				while (it.hasNext()) {
					key = it.next();
					if (map.get(key) instanceof IReleasable) {
						((IReleasable) map.get(key)).release();
					}
				}
			}
			map.clear();
			map = null;
		}
	}

	/**
	 * @see AbstractContentAssistProcessor#computeCompletionProposals(int,
	 *      String, ITextRegion, IDOMNode, IDOMNode)
	 */
	protected ContentAssistRequest computeCompletionProposals(int documentPosition, String matchString, ITextRegion completionRegion, IDOMNode treeNode, IDOMNode xmlnode) {

		ContentAssistRequest request = super.computeCompletionProposals(documentPosition, matchString, completionRegion, treeNode, xmlnode);
		IStructuredDocumentRegion sdRegion = ContentAssistUtils.getStructuredDocumentRegion(fTextViewer, documentPosition);

		Document doc = null;
		if (xmlnode != null) {
			if (xmlnode.getNodeType() == Node.DOCUMENT_NODE)
				doc = (Document) xmlnode;
			else
				doc = xmlnode.getOwnerDocument();
		}
		String[] directiveNames = {"page", "include", "taglib"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		// suggest JSP Expression inside of XML comments
		if (completionRegion.getType() == DOMRegionContext.XML_COMMENT_TEXT && !isXMLFormat(doc)) {
			if (request == null)
				request = newContentAssistRequest(treeNode, xmlnode, sdRegion, completionRegion, documentPosition, 0, ""); //$NON-NLS-1$
			request.addProposal(new CustomCompletionProposal("<%=  %>", documentPosition, 0, 4, JSPEditorPluginImageHelper.getInstance().getImage(JSPEditorPluginImages.IMG_OBJ_TAG_GENERIC), "jsp:expression", null, "&lt;%= %&gt;", XMLRelevanceConstants.R_JSP)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
		// handle proposals in and around JSP_DIRECTIVE_OPEN and
		// JSP_DIRECTIVE_NAME
		else if ((completionRegion.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_OPEN && documentPosition >= sdRegion.getTextEndOffset(completionRegion)) || (completionRegion.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME && documentPosition <= sdRegion.getTextEndOffset(completionRegion))) {
			if (completionRegion.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_OPEN) {
				if (request == null)
					request = newContentAssistRequest(xmlnode, xmlnode, sdRegion, completionRegion, documentPosition, 0, matchString);
				Iterator regions = sdRegion.getRegions().iterator();
				String nameString = null;
				int begin = request.getReplacementBeginPosition();
				int length = request.getReplacementLength();
				while (regions.hasNext()) {
					ITextRegion region = (ITextRegion) regions.next();
					if (region.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME) {
						nameString = sdRegion.getText(region);
						begin = sdRegion.getStartOffset(region);
						length = region.getTextLength();
						break;
					}
				}
				if (nameString == null)
					nameString = ""; //$NON-NLS-1$
				for (int i = 0; i < directiveNames.length; i++) {
					if (directiveNames[i].startsWith(nameString) || documentPosition <= begin)
						request.addProposal(new CustomCompletionProposal(directiveNames[i], begin, length, directiveNames[i].length(), JSPEditorPluginImageHelper.getInstance().getImage(JSPEditorPluginImages.IMG_OBJ_TAG_GENERIC), directiveNames[i], null, null, XMLRelevanceConstants.R_JSP));
				}
			}
			else { // by default, JSP_DIRECTIVE_NAME
				if (request == null)
					request = newContentAssistRequest(xmlnode, xmlnode, sdRegion, completionRegion, sdRegion.getStartOffset(completionRegion), completionRegion.getTextLength(), matchString);
				for (int i = 0; i < directiveNames.length; i++) {
					if (directiveNames[i].startsWith(matchString))
						request.addProposal(new CustomCompletionProposal(directiveNames[i], request.getReplacementBeginPosition(), request.getReplacementLength(), directiveNames[i].length(), JSPEditorPluginImageHelper.getInstance().getImage(JSPEditorPluginImages.IMG_OBJ_TAG_GENERIC), directiveNames[i], null, null, XMLRelevanceConstants.R_JSP));
				}
			}
		}
		else if ((completionRegion.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME && documentPosition > sdRegion.getTextEndOffset(completionRegion)) || (completionRegion.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_CLOSE && documentPosition <= sdRegion.getStartOffset(completionRegion))) {
			if (request == null)
				request = computeAttributeProposals(documentPosition, matchString, completionRegion, treeNode, xmlnode);
			super.addTagCloseProposals(request);
			// CMVC 274033, this is being added for all <jsp:* tags
			// in addAttributeNameProposals(contentAssistRequest)
			// super.addAttributeNameProposals(request);
		}
		// no name?: <%@ %>
		else if (completionRegion.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_CLOSE && documentPosition <= sdRegion.getStartOffset(completionRegion)) {
			if (request != null)
				request = computeAttributeProposals(documentPosition, matchString, completionRegion, treeNode, xmlnode);
			Iterator regions = sdRegion.getRegions().iterator();
			String nameString = null;
			while (regions.hasNext()) {
				ITextRegion region = (ITextRegion) regions.next();
				if (region.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME) {
					nameString = sdRegion.getText(region);
					break;
				}
			}
			if (nameString == null) {
				for (int i = 0; i < directiveNames.length; i++) {
					request.addProposal(new CustomCompletionProposal(directiveNames[i], request.getReplacementBeginPosition(), request.getReplacementLength(), directiveNames[i].length(), JSPEditorPluginImageHelper.getInstance().getImage(JSPEditorPluginImages.IMG_OBJ_TAG_GENERIC), directiveNames[i], null, null, XMLRelevanceConstants.R_JSP));
				}
			}
		}

		// bug115927 use original document position for all/any region
		// templates
		addTemplates(request, TemplateContextTypeIdsJSP.ALL, documentPosition);
		return request;
	}

	private JSPTemplateCompletionProcessor getTemplateCompletionProcessor() {
		if (fTemplateProcessor == null) {
			fTemplateProcessor = new JSPTemplateCompletionProcessor();
		}
		return fTemplateProcessor;
	}

	/**
	 * Adds templates to the list of proposals
	 * 
	 * @param contentAssistRequest
	 * @param context
	 */
	private void addTemplates(ContentAssistRequest contentAssistRequest, String context) {
		addTemplates(contentAssistRequest, context, contentAssistRequest.getReplacementBeginPosition());
	}

	/**
	 * Adds templates to the list of proposals
	 * 
	 * @param contentAssistRequest
	 * @param context
	 */
	private void addTemplates(ContentAssistRequest contentAssistRequest, String context, int startOffset) {
		if (contentAssistRequest == null)
			return;

		// if already adding template proposals for a certain context type, do
		// not add again
		if (!fTemplateContexts.contains(context)) {
			fTemplateContexts.add(context);
			boolean useProposalList = !contentAssistRequest.shouldSeparate();

			if (getTemplateCompletionProcessor() != null) {
				getTemplateCompletionProcessor().setContextType(context);
				ICompletionProposal[] proposals = getTemplateCompletionProcessor().computeCompletionProposals(fTextViewer, startOffset);
				for (int i = 0; i < proposals.length; ++i) {
					if (useProposalList)
						contentAssistRequest.addProposal(proposals[i]);
					else
						contentAssistRequest.addMacro(proposals[i]);
				}
			}
		}
	}

	protected void addEntityProposals(ContentAssistRequest contentAssistRequest, int documentPosition, ITextRegion completionRegion, IDOMNode treeNode) {
		// ignore
	}

	protected void addTagInsertionProposals(ContentAssistRequest contentAssistRequest, int childPosition) {
		addTemplates(contentAssistRequest, TemplateContextTypeIdsJSP.TAG);
		//don't need to call super here because otherwise we duplicate what the HTMLCOntentAssistProcessor that is running is already doing
	}

	/**
	 * Use the embedded content assist processor to determine the content generator
	 * 
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.AbstractContentAssistProcessor#getContentGenerator()
	 */
	public XMLContentModelGenerator getContentGenerator() {
		if (fGenerator == null) {
			fGenerator = ((AbstractContentAssistProcessor) fPartitionToProcessorMap.get(IHTMLPartitions.HTML_DEFAULT)).getContentGenerator();
		}
		return fGenerator;
	}
}
