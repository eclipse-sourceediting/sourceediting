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
package org.eclipse.jst.jsp.ui.internal.contentassist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jst.jsp.core.JSP11Namespace;
import org.eclipse.jst.jsp.core.JSP12Namespace;
import org.eclipse.jst.jsp.core.PageDirectiveAdapter;
import org.eclipse.jst.jsp.core.contentmodel.TaglibController;
import org.eclipse.jst.jsp.core.contentmodel.tld.TLDCMDocumentManager;
import org.eclipse.jst.jsp.core.document.PageDirectiveAdapterFactory;
import org.eclipse.jst.jsp.core.internal.text.rules.StructuredTextPartitionerForJSP;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.jst.jsp.ui.internal.Logger;
import org.eclipse.wst.common.contentmodel.CMDocument;
import org.eclipse.wst.common.contentmodel.CMElementDeclaration;
import org.eclipse.wst.common.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.common.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.common.contentmodel.modelquery.ModelQueryAction;
import org.eclipse.wst.common.encoding.content.IContentTypeIdentifier;
import org.eclipse.wst.css.ui.contentassist.CSSContentAssistProcessor;
import org.eclipse.wst.html.core.HTMLCMProperties;
import org.eclipse.wst.html.core.contentmodel.HTMLCMDocumentFactory;
import org.eclipse.wst.html.core.contentmodel.HTMLElementDeclaration;
import org.eclipse.wst.html.core.contentmodel.JSPCMDocument;
import org.eclipse.wst.html.core.internal.text.rules.StructuredTextPartitionerForHTML;
import org.eclipse.wst.html.ui.internal.contentassist.HTMLContentAssistProcessor;
import org.eclipse.wst.javascript.common.ui.contentassist.JavaScriptContentAssistProcessor;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.contentmodel.CMDocType;
import org.eclipse.wst.sse.core.contentmodel.CMDocumentTracker;
import org.eclipse.wst.sse.core.contentmodel.CMNodeWrapper;
import org.eclipse.wst.sse.core.modelquery.ModelQueryAdapter;
import org.eclipse.wst.sse.core.parser.BlockMarker;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.text.ITextRegionList;
import org.eclipse.wst.sse.core.text.rules.StructuredTextPartitioner;
import org.eclipse.wst.sse.core.util.StringUtils;
import org.eclipse.wst.sse.ui.StructuredTextViewer;
import org.eclipse.wst.sse.ui.contentassist.IRelevanceCompletionProposal;
import org.eclipse.wst.sse.ui.contentassist.IResourceDependentProcessor;
import org.eclipse.wst.sse.ui.edit.util.SharedEditorPluginImageHelper;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.sse.ui.internal.contentassist.IRelevanceConstants;
import org.eclipse.wst.sse.ui.preferences.CommonEditorPreferenceNames;
import org.eclipse.wst.sse.ui.registry.AdapterFactoryProvider;
import org.eclipse.wst.sse.ui.registry.AdapterFactoryRegistry;
import org.eclipse.wst.xml.core.document.XMLModel;
import org.eclipse.wst.xml.core.document.XMLNode;
import org.eclipse.wst.xml.core.internal.parser.XMLSourceParser;
import org.eclipse.wst.xml.core.jsp.model.parser.temp.XMLJSPRegionContexts;
import org.eclipse.wst.xml.core.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;
import org.eclipse.wst.xml.core.text.rules.StructuredTextPartitionerForXML;
import org.eclipse.wst.xml.ui.contentassist.AbstractContentAssistProcessor;
import org.eclipse.wst.xml.ui.contentassist.AbstractTemplateCompletionProcessor;
import org.eclipse.wst.xml.ui.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.contentassist.NonValidatingModelQueryAction;
import org.eclipse.wst.xml.ui.contentassist.ProposalComparator;
import org.eclipse.wst.xml.ui.contentassist.XMLContentAssistProcessor;
import org.eclipse.wst.xml.ui.contentassist.XMLContentAssistUtilities;
import org.eclipse.wst.xml.ui.contentassist.XMLRelevanceConstants;
import org.eclipse.wst.xml.ui.templates.TemplateContextTypeIds;
import org.eclipse.wst.xml.ui.util.SharedXMLEditorPluginImageHelper;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Adds proposals not normally covered by the generic behavior with the
 * content model
 */
public class JSPContentAssistProcessor extends AbstractContentAssistProcessor implements IResourceDependentProcessor {

	protected int depthCount = 0;
	protected ITextViewer fViewer = null;
	protected boolean useEmbeddedResults = true;
	protected boolean isInternalAdapter = false;
	protected HashMap fNameToProcessorMap = null;
	protected HashMap fPartitionToProcessorMap = null;
	private final ICompletionProposal[] EMPTY_PROPOSAL_SET = new ICompletionProposal[0];
	protected IResource fResource = null;
	protected AbstractTemplateCompletionProcessor fTemplateProcessor = null;

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
		JavaScriptContentAssistProcessor javascriptProcessor = new JavaScriptContentAssistProcessor();

		fPartitionToProcessorMap.put(StructuredTextPartitionerForHTML.ST_DEFAULT_HTML, htmlProcessor);
		fPartitionToProcessorMap.put(StructuredTextPartitionerForXML.ST_DEFAULT_XML, xmlProcessor);
		fPartitionToProcessorMap.put(StructuredTextPartitioner.ST_DEFAULT_PARTITION, htmlProcessor);
		fPartitionToProcessorMap.put(StructuredTextPartitionerForJSP.ST_DEFAULT_JSP, jspJavaProcessor);
		fPartitionToProcessorMap.put(StructuredTextPartitionerForJSP.ST_JSP_DIRECTIVE, xmlProcessor);
		fPartitionToProcessorMap.put(StructuredTextPartitionerForHTML.ST_HTML_COMMENT, htmlProcessor);
		fPartitionToProcessorMap.put(StructuredTextPartitionerForJSP.ST_JSP_CONTENT_JAVASCRIPT, javascriptProcessor);
		fPartitionToProcessorMap.put(StructuredTextPartitionerForHTML.ST_SCRIPT, javascriptProcessor); // default
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
		// CMVC 261162 (248603)
		// macros for JSP attribute values weren't showing up
		addTemplates(contentAssistRequest, TemplateContextTypeIds.ATTRIBUTE);

		// specific fix for CMVC 274033
		// no attribute proposals for <jsp:useBean />
		String nodeName = contentAssistRequest.getNode().getNodeName();
		if (nodeName.indexOf(':') != -1) { //$NON-NLS-1$
			super.addAttributeNameProposals(contentAssistRequest);
		}
	}

	/**
	 * add proposals for tags in attribute values
	 */
	protected void addAttributeValueProposals(ContentAssistRequest contentAssistRequest) {
		XMLNode node = (XMLNode) contentAssistRequest.getNode();
		// CMVC 248603
		// macros for JSP attribute values weren't showing up
		addTemplates(contentAssistRequest, TemplateContextTypeIds.ATTRIBUTEVALUE);

		// add JSP extra proposals from JSPBeanInfoContentAssistProcessor
		// JSPPropertyContentAssistProcessor

		// 2.1
		// get results from JSPUseBean and JSPProperty here
		// (look up processor in a map based on node name)
		JSPDummyContentAssistProcessor extraProcessor = (JSPDummyContentAssistProcessor) fNameToProcessorMap.get(node.getNodeName());
		if (extraProcessor != null && contentAssistRequest != null) {
			extraProcessor.initialize(fResource);
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
			if (nameRegion.getType() == XMLRegionContext.XML_TAG_ATTRIBUTE_NAME)
				break;
		}

		String attributeName = null;
		if (nameRegion != null)
			attributeName = open.getText(nameRegion);
		String currentValue = null;
		if (attributeName != null)
			currentValue = node.getAttributes().getNamedItem(attributeName).getNodeValue();

		// on an empty value, add all the JSP and taglib tags
		if ((contentAssistRequest.getRegion().getType() == XMLRegionContext.XML_TAG_ATTRIBUTE_EQUALS && contentAssistRequest.getReplacementLength() == 0) || (contentAssistRequest.getRegion().getType() == XMLRegionContext.XML_TAG_ATTRIBUTE_VALUE && (currentValue == null || currentValue.length() == 0))) {
			List rejectElements = new ArrayList();
			rejectElements.add(JSP12Namespace.ElementName.SCRIPTLET);
			rejectElements.add(JSP12Namespace.ElementName.EXPRESSION);
			rejectElements.add(JSP12Namespace.ElementName.DECLARATION);
			rejectElements.add(JSP12Namespace.ElementName.DIRECTIVE_INCLUDE);
			rejectElements.add(JSP12Namespace.ElementName.DIRECTIVE_PAGE);
			rejectElements.add(JSP12Namespace.ElementName.DIRECTIVE_TAGLIB);
			rejectElements.add(JSP12Namespace.ElementName.FALLBACK);
			rejectElements.add(JSP12Namespace.ElementName.USEBEAN);
			rejectElements.add(JSP12Namespace.ElementName.SETPROPERTY);
			rejectElements.add(JSP12Namespace.ElementName.FORWARD);
			rejectElements.add(JSP12Namespace.ElementName.PLUGIN);
			rejectElements.add(JSP12Namespace.ElementName.FALLBACK);
			rejectElements.add(JSP12Namespace.ElementName.PARAMS);

			List additionalElements = getAdditionalChildren(new ArrayList(), node, -1);
			for (i = 0; i < additionalElements.size(); i++) {
				CMElementDeclaration ed = (CMElementDeclaration) additionalElements.get(i);
				if (rejectElements.contains(ed.getNodeName()))
					continue;
				String tagname = getContentGenerator().getRequiredName(node, ed);
				StringBuffer contents = new StringBuffer("\""); //$NON-NLS-1$
				getContentGenerator().generateTag(node, ed, contents);
				contents.append('"');
				CustomCompletionProposal proposal = new CustomCompletionProposal(contents.toString(), contentAssistRequest.getReplacementBeginPosition(), contentAssistRequest.getReplacementLength(), contents.length(), SharedEditorPluginImageHelper.getImage(SharedXMLEditorPluginImageHelper.IMG_OBJ_TAG_GENERIC), tagname, null, null, XMLRelevanceConstants.R_JSP_ATTRIBUTE_VALUE);
				contentAssistRequest.addProposal(proposal);

				addTemplates(contentAssistRequest, TemplateContextTypeIds.TAG);
				addTemplates(contentAssistRequest, TemplateContextTypeIds.ALL);
			}
		}

		else if (contentAssistRequest.getRegion().getType() == XMLRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
			try {
				// Create a new model for Content Assist to operate on. This
				// will simulate
				// a full Document and then adjust the offset numbers in the
				// list of results.
				IStructuredModel internalModel = null;
				IModelManager mmanager = StructuredModelManager.getModelManager();
				internalModel = mmanager.createUnManagedStructuredModelFor(IContentTypeIdentifier.ContentTypeID_JSP);
				XMLNode xmlNode = null;
				XMLModel xmlOuterModel = null;
				if (contentAssistRequest.getNode() instanceof XMLNode) {
					xmlNode = (XMLNode) contentAssistRequest.getNode();
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
						XMLNode useBean = (XMLNode) useBeans.item(k);
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

					// the internal adapter does all the real work of
					// using the JSP content model to form proposals
					// TODO : Nitin and Phil, please take a look
					//// ContentAssistAdapter internalAdapter =
					// (ContentAssistAdapter) ((INodeNotifier)
					// internalNode).getAdapterFor(ContentAssistAdapter.class);
					ICompletionProposal[] results = null;
					//// if (internalAdapter != null) {
					//// internalAdapter.initialize(resource);
					//// if(internalAdapter instanceof
					// JSPContentAssistProcessor)
					////
					// ((JSPContentAssistProcessor)internalAdapter).isInternalAdapter
					// = true;
					//// results =
					// internalAdapter.computeCompletionProposals(fViewer,
					// internalOffset, internalNode);
					//// }
					//// // results = computeCompletionProposals(null,
					// internalOffset, internalNode);
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

	private List getAdditionalChildren(List elementDecls, Node node, int childIndex) {
		if (node instanceof XMLNode) {
			// find the location of the intended insertion as it will give us
			// the
			// correct offset for checking position dependent CMDocuments
			int textInsertionOffset = 0;
			NodeList children = node.getChildNodes();
			if (children.getLength() >= childIndex && childIndex >= 0) {
				Node nodeAlreadyAtIndex = children.item(childIndex);
				if (nodeAlreadyAtIndex instanceof XMLNode)
					textInsertionOffset = ((XMLNode) nodeAlreadyAtIndex).getEndOffset();
			}
			else {
				textInsertionOffset = ((XMLNode) node).getStartOffset();
			}
			TLDCMDocumentManager mgr = TaglibController.getTLDCMDocumentManager(((XMLNode) node).getStructuredDocument());
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
				mqAdapter = (ModelQueryAdapter) ((XMLNode) node).getAdapterFor(ModelQueryAdapter.class);
			else
				mqAdapter = (ModelQueryAdapter) ((XMLNode) node.getOwnerDocument()).getAdapterFor(ModelQueryAdapter.class);

			if (mqAdapter != null) {
				CMDocument doc = mqAdapter.getModelQuery().getCorrespondingCMDocument(node);
				// this shouldn't have to have the prefix coded in
				if (!(doc != null && (doc instanceof JSPCMDocument || doc instanceof CMNodeWrapper || node.getNodeName().startsWith("jsp:")))) { //$NON-NLS-1$
					// get jsp namespace elements and insert their contents
					CMDocument JCMDoc = HTMLCMDocumentFactory.getCMDocument(CMDocType.JSP11_DOC_TYPE);
					CMNamedNodeMap jspelements = JCMDoc.getElements();

					if (jspelements != null) {
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
						// XML tag forms
						if (!isXMLFormat(domDoc)) {
							rejectElements.add(JSP12Namespace.ElementName.SCRIPTLET);
							rejectElements.add(JSP12Namespace.ElementName.EXPRESSION);
							rejectElements.add(JSP12Namespace.ElementName.DECLARATION);
							rejectElements.add(JSP12Namespace.ElementName.DIRECTIVE_INCLUDE);
							rejectElements.add(JSP12Namespace.ElementName.DIRECTIVE_PAGE);
							rejectElements.add(JSP12Namespace.ElementName.TEXT);
						}
						// always exclude jsp:directive.taglib
						if (isInternalAdapter) {
							rejectElements.add(JSP12Namespace.ElementName.DIRECTIVE_TAGLIB);
							rejectElements.add(JSP12Namespace.ElementName.DIRECTIVE_TAGLIB);
							rejectElements.add(JSP12Namespace.ElementName.FALLBACK);
							rejectElements.add(JSP12Namespace.ElementName.USEBEAN);
							rejectElements.add(JSP12Namespace.ElementName.SETPROPERTY);
							rejectElements.add(JSP12Namespace.ElementName.FORWARD);
							rejectElements.add(JSP12Namespace.ElementName.PLUGIN);
							rejectElements.add(JSP12Namespace.ElementName.FALLBACK);
							rejectElements.add(JSP12Namespace.ElementName.PARAMS);
						}

						// don't show jsp:root if a document element already
						// exists
						Element docElement = domDoc.getDocumentElement();
						if (docElement != null && ((docElement.getNodeName().equals("jsp:root")) || ((((XMLNode) docElement).getStartStructuredDocumentRegion() != null || ((XMLNode) docElement).getEndStructuredDocumentRegion() != null)))) //$NON-NLS-1$
							rejectElements.add(JSP12Namespace.ElementName.ROOT);

						for (int j = 0; j < jspelements.getLength(); j++) {
							CMElementDeclaration ed = (CMElementDeclaration) jspelements.item(j);
							if (rejectElements.contains(ed.getNodeName()))
								continue;
							else
								elementDecls.add(ed);
						}
					}
				}
			}
		}
		return elementDecls;
	}

	protected List getAvailableChildrenAtIndex(Element parent, int index) {
		List list = new ArrayList();
		List additionalElements = getAdditionalChildren(new ArrayList(), parent, index);
		for (int i = 0; i < additionalElements.size(); i++) {
			ModelQueryAction insertAction = new NonValidatingModelQueryAction((CMElementDeclaration) additionalElements.get(i), ModelQueryAction.INSERT, 0, parent.getChildNodes().getLength(), null);
			list.add(insertAction);
		}

		// add allowed children of implicit tags that don't already exist
		NodeList children = parent.getChildNodes();
		List childNames = new ArrayList();
		if (children != null) {
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE)
					childNames.add(child.getNodeName().toLowerCase());
			}
		}
		List allActions = new ArrayList();
		Iterator iterator = list.iterator();
		ModelQuery modelQuery = ModelQueryUtil.getModelQuery(parent.getOwnerDocument());
		while (iterator.hasNext()) {
			ModelQueryAction action = (ModelQueryAction) iterator.next();
			allActions.add(action);
			if (action.getCMNode() instanceof HTMLElementDeclaration) {
				HTMLElementDeclaration ed = (HTMLElementDeclaration) action.getCMNode();
				String ommission = (String) ed.getProperty(HTMLCMProperties.OMIT_TYPE);
				if (!childNames.contains(ed.getNodeName().toLowerCase()) && ((ommission != null) && (ommission.equals(HTMLCMProperties.Values.OMIT_BOTH)))) {
					List implicitValidActions = new ArrayList();
					modelQuery.getInsertActions(parent, ed, 0, ModelQuery.INCLUDE_CHILD_NODES, ModelQuery.VALIDITY_NONE, implicitValidActions);
					if (implicitValidActions != null) {
						Iterator implicitValidActionsIterator = implicitValidActions.iterator();
						while (implicitValidActionsIterator.hasNext()) {
							ModelQueryAction insertAction = new NonValidatingModelQueryAction(((ModelQueryAction) implicitValidActionsIterator.next()).getCMNode(), ModelQueryAction.INSERT, 0, parent.getChildNodes().getLength(), null);
							allActions.add(insertAction);
						}
					}
				}
			}
		}
		return allActions;
	}

	protected List getAvailableRootChildren(Document document, int childIndex) {
		List list = new ArrayList();
		if (!isXMLFormat(document))
			getAdditionalChildren(list, document, childIndex);
		return list;
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
		IStructuredDocumentRegion sdRegion = ContentAssistUtils.getStructuredDocumentRegion((StructuredTextViewer) viewer, documentPosition);
		fViewer = viewer;
		ICompletionProposal[] jspResults = EMPTY_PROPOSAL_SET;
		ICompletionProposal[] embeddedResults = EMPTY_PROPOSAL_SET;

		// check the actual partition type
		String partitionType = getPartitionType((StructuredTextViewer) viewer, documentPosition);
		IStructuredModel sModel = StructuredModelManager.getModelManager().getExistingModelForRead(viewer.getDocument());
		IStructuredDocument structuredDocument = sModel.getStructuredDocument();
		sModel.releaseFromRead();

		IStructuredDocumentRegion fn = structuredDocument.getRegionAtCharacterOffset(documentPosition);

		//////////////////////////////////////////////////////////////////////////////
		// ANOTHER WORKAROUND UNTIL PARTITIONING TAKES CARE OF THIS
		// check for xml-jsp tags...
		// CMVC 243657
		if (partitionType == StructuredTextPartitionerForJSP.ST_JSP_DIRECTIVE && fn != null) {
			IStructuredDocumentRegion possibleXMLJSP = ((fn.getType() == XMLRegionContext.XML_CONTENT) && fn.getPrevious() != null) ? fn.getPrevious() : fn;
			ITextRegionList regions = possibleXMLJSP.getRegions();
			if (regions.size() > 1) {
				// check bounds cases
				ITextRegion xmlOpenOrClose = regions.get(0);
				if (xmlOpenOrClose.getType() == XMLRegionContext.XML_TAG_OPEN && documentPosition == possibleXMLJSP.getStartOffset()) {
					// do regular jsp content assist
				}
				else if (xmlOpenOrClose.getType() == XMLRegionContext.XML_END_TAG_OPEN && documentPosition > possibleXMLJSP.getStartOffset()) {
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

		//////////////////////////////////////////////////////////////////////////////
		// ** THIS IS A TEMP FIX UNTIL PARTITIONING TAKES CARE OF THIS...
		// CMVC 241882
		// check for XML-JSP in a <script> region
		if (partitionType == StructuredTextPartitionerForJSP.ST_JSP_CONTENT_JAVASCRIPT || partitionType == StructuredTextPartitionerForHTML.ST_SCRIPT) {
			//fn should be block text
			IStructuredDocumentRegion decodedSDRegion = decodeScriptBlock(fn.getFullText());
			//System.out.println("decoded > " +
			// blockOfText.substring(decodedSDRegion.getStartOffset(),
			// decodedSDRegion.getEndOffset()));
			if (decodedSDRegion != null) {
				IStructuredDocumentRegion sdr = decodedSDRegion;
				while (sdr != null) {
					//System.out.println("sdr " + sdr.getType());
					//System.out.println("sdr > " +
					// blockOfText.substring(sdr.getStartOffset(),
					// sdr.getEndOffset()));
					if (sdr.getType() == XMLJSPRegionContexts.JSP_CONTENT) {
						if (documentPosition >= fn.getStartOffset() + sdr.getStartOffset() && documentPosition <= fn.getStartOffset() + sdr.getEndOffset()) {
							return getJSPJavaCompletionProposals(viewer, documentPosition);
						}
					}
					else if (sdr.getType() == XMLRegionContext.XML_TAG_NAME) {
						if (documentPosition > fn.getStartOffset() + sdr.getStartOffset() && documentPosition < fn.getStartOffset() + sdr.getEndOffset()) {
							return EMPTY_PROPOSAL_SET;
						}
						else if (documentPosition == fn.getStartOffset() + sdr.getEndOffset() && sdr.getNext() != null && sdr.getNext().getType() == XMLJSPRegionContexts.JSP_CONTENT) {
							// the end of an open tag <script>
							// <jsp:scriptlet>| blah </jsp:scriptlet>
							return getJSPJavaCompletionProposals(viewer, documentPosition);
						}
						else if (documentPosition == fn.getStartOffset() + sdr.getStartOffset() && sdr.getPrevious() != null && sdr.getPrevious().getType() == XMLRegionContext.XML_TAG_NAME) {
							return getJSPJavaCompletionProposals(viewer, documentPosition);
						}
					}
					sdr = sdr.getNext();
				}
			}
		}
		///////////////////////////////////////////////////////////////////////////

		// check special JSP delimiter cases
		if (fn != null && partitionType == StructuredTextPartitionerForJSP.ST_JSP_CONTENT_DELIMITER) {
			IStructuredDocumentRegion fnDelim = fn;

			// if it's a nested JSP region, need to get the correct
			// StructuredDocumentRegion
			// not sure why this check was there...
			//if (fnDelim.getType() == XMLRegionContext.BLOCK_TEXT) {
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
									partitionType = getPartitionType((StructuredTextViewer) viewer, documentPosition - 1);
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
				//}
			}

			// take care of XML-JSP delimter cases
			if (XMLContentAssistUtilities.isXMLJSPDelimiter(fnDelim)) {
				// since it's a delimiter, we know it's a ITextRegionContainer
				ITextRegion firstRegion = fnDelim.getRegions().get(0);
				if (fnDelim.getStartOffset() == documentPosition && (firstRegion.getType() == XMLRegionContext.XML_TAG_OPEN)) {
					// |<jsp:scriptlet> </jsp:scriptlet>
					// (pa) commented out so that we get regular behavior JSP
					// macros etc...
					//return getHTMLCompletionProposals(viewer,
					// documentPosition);
				}
				else if (fnDelim.getStartOffset() == documentPosition && (firstRegion.getType() == XMLRegionContext.XML_END_TAG_OPEN)) {
					// <jsp:scriptlet> |</jsp:scriptlet>
					// check previous partition type to see if it's JAVASCRIPT
					// if it is, we're just gonna let the embedded JAVASCRIPT
					// adapter get the proposals
					if (documentPosition > 0) {
						String checkType = getPartitionType((StructuredTextViewer) viewer, documentPosition - 1);
						if (checkType != StructuredTextPartitionerForJSP.ST_JSP_CONTENT_JAVASCRIPT) { // this
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
						else {
							partitionType = StructuredTextPartitionerForJSP.ST_JSP_CONTENT_JAVASCRIPT;
						}
					}
				}
				else if ((firstRegion.getType() == XMLRegionContext.XML_TAG_OPEN) && documentPosition >= fnDelim.getEndOffset()) {
					// anything else inbetween
					return getJSPJavaCompletionProposals(viewer, documentPosition);
				}
			}
			else if (XMLContentAssistUtilities.isJSPDelimiter(fnDelim)) {
				//	the delimiter <%, <%=, <%!, ...
				if (XMLContentAssistUtilities.isJSPCloseDelimiter(fnDelim)) {
					if (documentPosition == fnDelim.getStartOffset()) {
						// check previous partition type to see if it's
						// JAVASCRIPT
						// if it is, we're just gonna let the embedded
						// JAVASCRIPT adapter get the proposals
						if (documentPosition > 0) {
							String checkType = getPartitionType((StructuredTextViewer) viewer, documentPosition - 1);
							if (checkType != StructuredTextPartitionerForJSP.ST_JSP_CONTENT_JAVASCRIPT) {
								return getJSPJavaCompletionProposals(viewer, documentPosition);
							}
							else {
								partitionType = StructuredTextPartitionerForJSP.ST_JSP_CONTENT_JAVASCRIPT;
							}
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
		if (fn != null && (fn.getType() == XMLRegionContext.XML_CDATA_TEXT || fn.getType() == XMLRegionContext.XML_COMMENT_TEXT)) {
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
							if (jspRegion.getType() == XMLJSPRegionContexts.JSP_CLOSE) {
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
			if (attrContainer.getType() == XMLRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
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
				if (testRegion.getType().equals(XMLJSPRegionContexts.JSP_CONTENT))
					return getJSPJavaCompletionProposals(viewer, documentPosition);
				else
					return EMPTY_PROPOSAL_SET;
			}
		}

		IContentAssistProcessor p = (IContentAssistProcessor) fPartitionToProcessorMap.get(partitionType);
		if (p != null) {
			embeddedResults = p.computeCompletionProposals(viewer, documentPosition);
			// get bean methods, objects, and constants if there are any...
			if (partitionType == StructuredTextPartitionerForJSP.ST_JSP_CONTENT_JAVASCRIPT || partitionType == StructuredTextPartitionerForHTML.ST_SCRIPT) {
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

		// fix for CMVC 253000
		if (!(p instanceof JavaScriptContentAssistProcessor || p instanceof CSSContentAssistProcessor)) {
			super.macroContexts.clear();
			jspResults = super.computeCompletionProposals(viewer, documentPosition);
		}
		if (useEmbeddedResults) {
			if (embeddedResults != null && embeddedResults.length > 0) {
				List results = new ArrayList();
				for (int i = 0; i < embeddedResults.length; i++)
					results.add(embeddedResults[i]);
				if (jspResults != null) {
					for (int i = 0; i < jspResults.length; i++)
						results.add(jspResults[i]);
				}
				jspResults = new ICompletionProposal[results.size()];
				Collections.sort(results, new ProposalComparator());
				for (int i = 0; i < results.size(); i++)
					jspResults[i] = (ICompletionProposal) results.get(i);

			}
		}
		if (jspResults == null)
			jspResults = EMPTY_PROPOSAL_SET;
		setErrorMessage(jspResults.length == 0 ? UNKNOWN_CONTEXT : null);

		// CMVC 269718
		// check for |<%-- --%> first position of jsp comment
		if (partitionType == StructuredTextPartitionerForJSP.ST_JSP_COMMENT) {
			if (sdRegion.getStartOffset() == documentPosition) {
				ICompletionProposal[] htmlResults = getHTMLCompletionProposals(viewer, documentPosition);
				jspResults = merge(jspResults, htmlResults);
			}
		}
		return jspResults;
	}

	/**
	 * Adds 2 arrays of ICompletionProposals and sorts them with a
	 * ProposalComparator.
	 * 
	 * @param jspResults
	 * @param htmlResults
	 * @return
	 */
	private ICompletionProposal[] merge(ICompletionProposal[] jspResults, ICompletionProposal[] htmlResults) {
		List results = new ArrayList();
		List jsps = Arrays.asList(jspResults);
		List htmls = Arrays.asList(htmlResults);

		results.addAll(jsps);
		results.addAll(htmls);

		Collections.sort(results, new ProposalComparator());
		return (ICompletionProposal[]) results.toArray(new ICompletionProposal[results.size()]);
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

						System.out.println("proposal > " + test.getDisplayString());
            System.out.println("relevance > " + ((CustomCompletionProposal) test).getRelevance());
						
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

		IContentAssistProcessor p = (IContentAssistProcessor) fPartitionToProcessorMap.get(StructuredTextPartitionerForHTML.ST_DEFAULT_HTML);
		return p.computeCompletionProposals(viewer, documentPosition);
	}

	/**
	 * 
	 * @param viewer
	 * @param documentPosition
	 * @return ICompletionProposal[]
	 */
	protected ICompletionProposal[] getJSPJavaCompletionProposals(ITextViewer viewer, int documentPosition) {
		JSPJavaContentAssistProcessor p = (JSPJavaContentAssistProcessor) fPartitionToProcessorMap.get(StructuredTextPartitionerForJSP.ST_DEFAULT_JSP);
		p.initialize(fResource);
		return p.computeCompletionProposals(viewer, documentPosition);
	}

	/**
	 * @param viewer
	 * @param documentPosition
	 * @return String
	 */
	protected String getPartitionType(StructuredTextViewer viewer, int documentPosition) {
		String partitionType = null;
		try {
			partitionType = TextUtilities.getContentType(viewer.getDocument(), IStructuredDocument.DEFAULT_STRUCTURED_PARTITIONING, documentPosition + viewer.getVisibleRegion().getOffset(), false);
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
		parser.addBlockMarker(new BlockMarker("jsp:scriptlet", null, XMLJSPRegionContexts.JSP_CONTENT, false, false)); //$NON-NLS-1$
		parser.addBlockMarker(new BlockMarker("jsp:expression", null, XMLJSPRegionContexts.JSP_CONTENT, false, false)); //$NON-NLS-1$
		parser.addBlockMarker(new BlockMarker("jsp:declaration", null, XMLJSPRegionContexts.JSP_CONTENT, false, false)); //$NON-NLS-1$
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
	 */
	public char[] getCompletionProposalAutoActivationCharacters() {
		// CMVC 260546
		// return from HTML preference,
		// since we know that we are inhereiting the preference for auto
		// activation from HTML
//		AbstractUIPlugin htmlPlugin = (AbstractUIPlugin) Platform.getPlugin(HTMLEditorPlugin.ID);
		IPreferenceStore store = JSPUIPlugin.getDefault().getPreferenceStore();
		String key = CommonEditorPreferenceNames.AUTO_PROPOSE_CODE;

		String chars = store.getString(key);
		return (chars != null) ? chars.toCharArray() : new char[0];
	}

	/*
	 * @see ContentAssistAdapter#getContextInformationValidator()
	 */
	public IContextInformationValidator getContextInformationValidator() {
		return super.getContextInformationValidator();
	}

	/*
	 * @see ContentAssistAdapter#initialize(IResource)
	 */
	public void initialize(IResource resourceToInit) {
		fResource = resourceToInit;
		if (fNameToProcessorMap != null) {
			
			if(fNameToProcessorMap.isEmpty())
				initNameToProcessorMap();
				
			// init some embedded processors
			JSPUseBeanContentAssistProcessor useBeanProcessor = (JSPUseBeanContentAssistProcessor) fNameToProcessorMap.get(JSP11Namespace.ElementName.USEBEAN);
			JSPPropertyContentAssistProcessor propProcessor = (JSPPropertyContentAssistProcessor) fNameToProcessorMap.get(JSP11Namespace.ElementName.SETPROPERTY);
			useBeanProcessor.initialize(resourceToInit);
			propProcessor.initialize(resourceToInit);
		}
		if(fPartitionToProcessorMap != null) {
			if(fPartitionToProcessorMap.isEmpty())
				initPartitionToProcessorMap();
		}
	}

	protected boolean isXMLFormat(Document doc) {
		if (doc == null)
			return false;
		Element docElement = doc.getDocumentElement();
		return docElement != null && ((docElement.getNodeName().equals("jsp:root")) || ((((XMLNode) docElement).getStartStructuredDocumentRegion() == null && ((XMLNode) docElement).getEndStructuredDocumentRegion() == null))); //$NON-NLS-1$
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
					// TODO (pa) need to make sure processors w/ release()
					// implement releasable
					if (map.get(key) instanceof AbstractContentAssistProcessor) {
						((AbstractContentAssistProcessor) map.get(key)).release();
					}
				}
			}
			map.clear();
			map = null;
		}
	}

	/**
	 * @see AbstractContentAssistProcessor#computeCompletionProposals(int,
	 *      String, ITextRegion, XMLNode, XMLNode)
	 */
	protected ContentAssistRequest computeCompletionProposals(int documentPosition, String matchString, ITextRegion completionRegion, XMLNode treeNode, XMLNode xmlnode) {

		ContentAssistRequest request = super.computeCompletionProposals(documentPosition, matchString, completionRegion, treeNode, xmlnode);
		IStructuredDocumentRegion sdRegion = ContentAssistUtils.getStructuredDocumentRegion((StructuredTextViewer) fTextViewer, documentPosition);

		Document doc = null;
		if (xmlnode != null) {
			if (xmlnode.getNodeType() == Node.DOCUMENT_NODE)
				doc = (Document) xmlnode;
			else
				doc = xmlnode.getOwnerDocument();
		}
		String[] directiveNames = {"page", "include", "taglib"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		// suggest JSP Expression inside of XML comments
		if (completionRegion.getType() == XMLRegionContext.XML_COMMENT_TEXT && !isXMLFormat(doc)) {
			if (request == null)
				request = newContentAssistRequest(treeNode, xmlnode, sdRegion, completionRegion, documentPosition, 0, ""); //$NON-NLS-1$
			request.addProposal(new CustomCompletionProposal("<%=  %>", documentPosition, 0, 4, SharedXMLEditorPluginImageHelper.getImage(SharedXMLEditorPluginImageHelper.IMG_OBJ_TAG_MACRO), "jsp:expression", null, "&lt;%= %&gt;", XMLRelevanceConstants.R_JSP)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
		// handle proposals in and around JSP_DIRECTIVE_OPEN and
		// JSP_DIRECTIVE_NAME
		else if ((completionRegion.getType() == XMLJSPRegionContexts.JSP_DIRECTIVE_OPEN && documentPosition >= sdRegion.getTextEndOffset(completionRegion)) || (completionRegion.getType() == XMLJSPRegionContexts.JSP_DIRECTIVE_NAME && documentPosition <= sdRegion.getTextEndOffset(completionRegion))) {
			if (completionRegion.getType() == XMLJSPRegionContexts.JSP_DIRECTIVE_OPEN) {
				if (request == null)
					request = newContentAssistRequest(xmlnode, xmlnode, sdRegion, completionRegion, documentPosition, 0, matchString);
				Iterator regions = sdRegion.getRegions().iterator();
				String nameString = null;
				int begin = request.getReplacementBeginPosition();
				int length = request.getReplacementLength();
				while (regions.hasNext()) {
					ITextRegion region = (ITextRegion) regions.next();
					if (region.getType() == XMLJSPRegionContexts.JSP_DIRECTIVE_NAME) {
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
						request.addProposal(new CustomCompletionProposal(directiveNames[i], begin, length, directiveNames[i].length(), SharedXMLEditorPluginImageHelper.getImage(SharedXMLEditorPluginImageHelper.IMG_OBJ_TAG_GENERIC), directiveNames[i], null, null, XMLRelevanceConstants.R_JSP));
				}
			}
			else { // by default, JSP_DIRECTIVE_NAME
				if (request == null)
					request = newContentAssistRequest(xmlnode, xmlnode, sdRegion, completionRegion, sdRegion.getStartOffset(completionRegion), completionRegion.getTextLength(), matchString);
				for (int i = 0; i < directiveNames.length; i++) {
					if (directiveNames[i].startsWith(matchString))
						request.addProposal(new CustomCompletionProposal(directiveNames[i], request.getReplacementBeginPosition(), request.getReplacementLength(), directiveNames[i].length(), SharedXMLEditorPluginImageHelper.getImage(SharedXMLEditorPluginImageHelper.IMG_OBJ_TAG_GENERIC), directiveNames[i], null, null, XMLRelevanceConstants.R_JSP));
				}
			}
		}
		else if ((completionRegion.getType() == XMLJSPRegionContexts.JSP_DIRECTIVE_NAME && documentPosition > sdRegion.getTextEndOffset(completionRegion)) || (completionRegion.getType() == XMLJSPRegionContexts.JSP_DIRECTIVE_CLOSE && documentPosition <= sdRegion.getStartOffset(completionRegion))) {
			if (request == null)
				request = computeAttributeProposals(documentPosition, matchString, completionRegion, treeNode, xmlnode);
			super.addTagCloseProposals(request);
			//  CMVC 274033, this is being added for all <jsp:* tags
			// in addAttributeNameProposals(contentAssistRequest)
			//super.addAttributeNameProposals(request);
		}
		// no name?: <%@ %>
		else if (completionRegion.getType() == XMLJSPRegionContexts.JSP_DIRECTIVE_CLOSE && documentPosition <= sdRegion.getStartOffset(completionRegion)) {
			if (request != null)
				request = computeAttributeProposals(documentPosition, matchString, completionRegion, treeNode, xmlnode);
			Iterator regions = sdRegion.getRegions().iterator();
			String nameString = null;
			while (regions.hasNext()) {
				ITextRegion region = (ITextRegion) regions.next();
				if (region.getType() == XMLJSPRegionContexts.JSP_DIRECTIVE_NAME) {
					nameString = sdRegion.getText(region);
					break;
				}
			}
			if (nameString == null) {
				for (int i = 0; i < directiveNames.length; i++) {
					request.addProposal(new CustomCompletionProposal(directiveNames[i], request.getReplacementBeginPosition(), request.getReplacementLength(), directiveNames[i].length(), SharedXMLEditorPluginImageHelper.getImage(SharedXMLEditorPluginImageHelper.IMG_OBJ_TAG_GENERIC), directiveNames[i], null, null, XMLRelevanceConstants.R_JSP));
				}
			}
		}
		return request;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.editor.xml.contentassist.AbstractContentAssistProcessor#getTemplateCompletionProcessor()
	 */
	protected AbstractTemplateCompletionProcessor getTemplateCompletionProcessor() {
		if (fTemplateProcessor == null) {
			fTemplateProcessor = new JSPTemplateCompletionProcessor();
		}
		return fTemplateProcessor;
	}

	/**
	 * @see com.ibm.sed.contentassist.old.xml.AbstractContentAssistProcessor#addEntityProposals(ContentAssistRequest,
	 *      int, ITextRegion, XMLNode)
	 */
	protected void addEntityProposals(ContentAssistRequest contentAssistRequest, int documentPosition, ITextRegion completionRegion, XMLNode treeNode) {
	}

	/**
	 * @see com.ibm.sed.contentassist.old.xml.AbstractContentAssistProcessor#addTagInsertionProposals(ContentAssistRequest,
	 *      int)
	 */
	protected void addTagInsertionProposals(ContentAssistRequest contentAssistRequest, int childPosition) {
		super.addTagInsertionProposals(contentAssistRequest, childPosition);
		if (isInternalAdapter)
			useEmbeddedResults = false;
	}
}