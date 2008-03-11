/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - STAR - bug 213849 - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.contentassist;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.xml.core.internal.contentmodel.util.DOMNamespaceHelper;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLContentAssistProcessor;
import org.eclipse.wst.xml.ui.internal.preferences.XMLUIPreferenceNames;
import org.eclipse.wst.xsl.ui.internal.XSLUIPlugin;
import org.eclipse.wst.xsl.ui.internal.templates.TemplateContextTypeIdsXPath;
import org.eclipse.wst.xsl.ui.internal.util.XSLPluginImageHelper;
import org.eclipse.wst.xsl.ui.internal.util.XSLPluginImages;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The XSL Content Assist Processor provides content assistance for various attributes
 * values within the XSL Editor.   This includes support for xpaths on select statements
 * as well as on
 * @author David Carver
 *
 */
@SuppressWarnings("restriction") //$NON-NLS-1$
public class XSLContentAssistProcessor extends XMLContentAssistProcessor implements IPropertyChangeListener {

	private static final String ATTR_SELECT = "select"; //$NON-NLS-1$
	private static final String ATTR_TEST = "test"; //$NON-NLS-1$
	private static final String ATTR_MATCH = "match"; //$NON-NLS-1$
	/**
	 * Retireve all global variables in the stylesheet.
	 */
	private static final String XPATH_GLOBAL_VARIABLES = "/xsl:stylesheet/xsl:variable";
	
	/**
	 * Retrieve all global parameters in the stylesheet.
	 */
	private static final String XPATH_GLOBAL_PARAMS = "/xsl:stylesheet/xsl:param";
	
	/**
	 * Limit selection of variables to those that are in the local scope.
	 */
	private static final String XPATH_LOCAL_VARIABLES = "ancestor::xsl:template/descendant::xsl:variable";
	
	/**
	 * Limit selection of params to those that are in the local scope.
	 */
	private static final String XPATH_LOCAL_PARAMS = "ancestor::xsl:template/descendant::xsl:param";

	/**
	 * XSL Namespace.  We rely on the namespace not the prefix for identification.
	 */
	private String xslNamespace = "http://www.w3.org/1999/XSL/Transform"; //$NON-NLS-1$
	
	protected IPreferenceStore fPreferenceStore = null;
	protected IResource fResource = null;
	private XPathTemplateCompletionProcessor fTemplateProcessor = null;
	private List<String> fTemplateContexts = new ArrayList<String>();
	private static final byte[] XPATH_LOCK = new byte[0];
	
	/**
	 * The XSL Content Assist Processor handles XSL specific functionality for
	 * content assistance.   It leverages several XPath selection variables
	 * to help with the selection of elements and template names.
	 * 
	 */
	public XSLContentAssistProcessor() {
		super();
	}
	
	
	/**
	 * Adds Attribute proposals based on the element and the attribute
	 * where the content proposal was instantiated.
	 * 
	 * @param contentAssistRequest Content Assist Request that initiated the proposal request
	 * 
	 */
	@Override
	protected void addAttributeValueProposals(
			ContentAssistRequest contentAssistRequest) {
		// Make sure to handle any existing Content Assist based on Attributes
		// for XML content.
		super.addAttributeValueProposals(contentAssistRequest);
		IDOMNode node = (IDOMNode)contentAssistRequest.getNode();
        String namespace = DOMNamespaceHelper.getNamespaceURI(node);
        
		String nodeName = DOMNamespaceHelper.getUnprefixedName(node.getNodeName());
		String attributeName = getAttributeName(contentAssistRequest);
		
		// Retrieve the Editor so that we may get a w3c DOM representation of the document.
		IEditorPart editor = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		IStructuredModel model = this.getEditorModel(editor);
		Document document = (Document) model.getAdapter(Document.class);
		Element rootElement = (IDOMElement) document.getDocumentElement();
		
		if (attributeName != null) {
			// Current node belongs in the XSL Namespace.  We only want to do this
			// for the namespace.  Regardless of what the prefix is set too.
			if (namespace.equals(this.xslNamespace)) {
				// Adjust the offset so we don't overwrite the " due to the 
				// fact that SSE includes " in the value region for attributes.
				int offset = contentAssistRequest.getStartOffset() + 1;
				if (attributeName.equals(ATTR_SELECT) ||
						attributeName.equals(ATTR_TEST)) {
					addGlobalProposals(rootElement, contentAssistRequest, offset);
					addLocalProposals(node, contentAssistRequest, offset);
				}
				
				// Add the common XPath proposals to Select, Test, and Match attributes that
				// appear in the xsl namespace.
				if (attributeName.equals(ATTR_SELECT) ||
					attributeName.equals(ATTR_TEST) ||
					attributeName.equals(ATTR_MATCH)) {
					addTemplates(contentAssistRequest, TemplateContextTypeIdsXPath.AXIS, offset);
					addTemplates(contentAssistRequest, TemplateContextTypeIdsXPath.XPATH, offset);
					addTemplates(contentAssistRequest, TemplateContextTypeIdsXPath.CUSTOM, offset);
				}

				// Operators like And, Or, greater than, are more likely to be used in test statements
				if (attributeName.equals(ATTR_TEST)) {
					addTemplates(contentAssistRequest, TemplateContextTypeIdsXPath.OPERATOR);
				}
				// Match attributes probably need to get a listing of all available elements from the
				// content model by namespace so that those values can be added as possible content assistance
				
			}
		}
	}

	private void addLocalProposals(Node xpathnode,
			ContentAssistRequest contentAssistRequest, int offset) {
		addvariablesProposals(XPATH_LOCAL_VARIABLES, xpathnode, contentAssistRequest, offset);
		addvariablesProposals(XPATH_LOCAL_PARAMS, xpathnode, contentAssistRequest, offset);
	}

	private void addGlobalProposals(Node xpathnode,
			ContentAssistRequest contentAssistRequest, int offset) {
		addvariablesProposals(XPATH_GLOBAL_VARIABLES, xpathnode, contentAssistRequest, offset);
		addvariablesProposals(XPATH_GLOBAL_PARAMS, xpathnode, contentAssistRequest, offset);
	}
	
	/**
	 * addvariableProposals adds Parameter and Variables as proposals.  This information is
	 * selected based on the XPath statement that is sent to it and the input Node passed.
	 * It uses a custom composer to XSL Variable proposal.
	 * @param xpath
	 * @param xpathnode
	 * @param contentAssistRequest
	 * @param offset
	 */
	private void addvariablesProposals(String xpath, Node xpathnode, ContentAssistRequest contentAssistRequest, int offset) {
		synchronized (XPATH_LOCK)
		{
			try {
				NodeList nodes =  XPathAPI.selectNodeList(xpathnode, xpath);
				if (nodes != null && nodes.getLength() > 0) {
				   for (int nodecnt = 0; nodecnt < nodes.getLength(); nodecnt++) {
						Node node = nodes.item(nodecnt);
						String variableName = "$" + node.getAttributes().getNamedItem("name").getNodeValue();
						contentAssistRequest.getReplacementLength();
						XSLVariableCustomCompletionProposal proposal = new XSLVariableCustomCompletionProposal(variableName, offset, 0, variableName.length() + 1, XSLPluginImageHelper.getInstance().getImage(XSLPluginImages.IMG_VARIABLES), variableName, null, null, 0);
						contentAssistRequest.addProposal(proposal);
				   }
				}
				
			} catch (TransformerException ex) {
				XSLUIPlugin.log(ex);
			}
		}
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
	 * Adds XPath related templates to the list of proposals
	 * 
	 * @param contentAssistRequest
	 * @param context
	 * @param startOffset
	 */
	private void addTemplates(ContentAssistRequest contentAssistRequest, String context, int startOffset) {
		if (contentAssistRequest == null) {
			return;
		}

		// if already adding template proposals for a certain context type, do
		// not add again
		if (!fTemplateContexts.contains(context)) {
			fTemplateContexts.add(context);
			boolean useProposalList = !contentAssistRequest.shouldSeparate();

			if (getTemplateCompletionProcessor() != null) {
				getTemplateCompletionProcessor().setContextType(context);
				ICompletionProposal[] proposals = getTemplateCompletionProcessor().computeCompletionProposals(fTextViewer, startOffset);
				for (int i = 0; i < proposals.length; ++i) {
					if (useProposalList) {
						contentAssistRequest.addProposal(proposals[i]);
					}
					else {
						contentAssistRequest.addMacro(proposals[i]);
					}
				}
			}
		}
	}
	


	private XPathTemplateCompletionProcessor getTemplateCompletionProcessor() {
		if (fTemplateProcessor == null) {
			fTemplateProcessor = new XPathTemplateCompletionProcessor();
		}
		return fTemplateProcessor;
	}

	/**
	 * Gets the attribute name that the content assist was triggered on.
	 * @param contentAssistRequest
	 * @return
	 */
	private String getAttributeName(ContentAssistRequest contentAssistRequest) {
		// Find the attribute region and name for which this position should
		// have a value proposed
		String attributeName = null;
		IDOMNode node = (IDOMNode)contentAssistRequest.getNode();
		IStructuredDocumentRegion open = node.getFirstStructuredDocumentRegion();
		ITextRegionList openRegions = open.getRegions();
		int i = openRegions.indexOf(contentAssistRequest.getRegion());
		if (i >= 0) {
			
			ITextRegion nameRegion = null;
			while (i >= 0) {
				nameRegion = openRegions.get(i--);
				if (nameRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
					break;
				}
			}
			
			// String attributeName = nameRegion.getText();
			attributeName = open.getText(nameRegion);
		}
		return attributeName;
	}
	
	protected ContentAssistRequest computeCompletionProposals(int documentPosition, String matchString, ITextRegion completionRegion, IDOMNode treeNode, IDOMNode xmlnode) {
		return super.computeCompletionProposals(documentPosition, matchString, completionRegion, treeNode, xmlnode);
	}
	
	
	/**
	 * TODO: Add Javadoc
	 * @param textViewer 
	 * @param documentPosition 
	 * @return 
	 * 
	 * @see org.eclipse.wst.xml.ui.contentassist.AbstractContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text.ITextViewer,
	 *      int)
	 */
	public ICompletionProposal[] computeCompletionProposals(ITextViewer textViewer, int documentPosition) {
		fTemplateContexts.clear();
		return super.computeCompletionProposals(textViewer, documentPosition);
	}
	
	
	protected IPreferenceStore getPreferenceStore() {
		if (fPreferenceStore == null) {
			fPreferenceStore = XSLUIPlugin.getDefault().getPreferenceStore();
		}
		return fPreferenceStore;
	}
	
	
	protected void init() {
		getPreferenceStore().addPropertyChangeListener(this);
		reinit();
	}	
	
	/**
	 * @param event 
	 * 
	 */
	public void propertyChange(PropertyChangeEvent event) {
		String property = event.getProperty();

		if ((property.compareTo(XMLUIPreferenceNames.AUTO_PROPOSE) == 0) || (property.compareTo(XMLUIPreferenceNames.AUTO_PROPOSE_CODE) == 0)) {
			reinit();
		}
	}
	
	protected void reinit() {
		String key = XMLUIPreferenceNames.AUTO_PROPOSE;
		boolean doAuto = getPreferenceStore().getBoolean(key);
		if (doAuto) {
			key = XMLUIPreferenceNames.AUTO_PROPOSE_CODE;
			completionProposalAutoActivationCharacters = getPreferenceStore().getString(key).toCharArray();
		}
		else {
			completionProposalAutoActivationCharacters = null;
		}
	}	
	
	private IStructuredModel getEditorModel(IEditorPart editor)
	{
		return (IStructuredModel) editor.getAdapter(IStructuredModel.class);
	}	
}
