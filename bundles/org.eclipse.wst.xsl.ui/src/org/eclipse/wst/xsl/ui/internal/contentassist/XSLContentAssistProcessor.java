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

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.xml.core.internal.contentmodel.util.DOMNamespaceHelper;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLContentAssistProcessor;
import org.eclipse.wst.xml.ui.internal.preferences.XMLUIPreferenceNames;
import org.eclipse.wst.xsl.ui.internal.XSLUIPlugin;
import org.eclipse.wst.xsl.ui.internal.templates.TemplateContextTypeIdsXPath;

/**
 * The XSL Content Assist Processor provides content assistance for various attributes
 * values within the XSL Editor.   This includes support for xpaths on select statements
 * as well as on
 * @author David Carver
 *
 */
@SuppressWarnings("restriction")
public class XSLContentAssistProcessor extends XMLContentAssistProcessor implements IPropertyChangeListener {

	private static final String ELEMENT_VARIABLE = "variable";
	private static final String ELEMENT_PARAM = "param";
	private static final String ELEMENT_COPYOF = "copy-of";
	private static final String ELEMENT_WITHPARM = "with-parm";
	private static final String ELEMENT_APPLYTEMPLATE = "apply-templates";

	private static final String ATTR_SELECT = "select";
	private static final String ATTR_TEST = "test";

	private String xslNamespace = "http://www.w3.org/1999/XSL/Transform";
	
	protected IPreferenceStore fPreferenceStore = null;
	protected IResource fResource = null;
	private XPathTemplateCompletionProcessor fTemplateProcessor = null;
	private List<String> fTemplateContexts = new ArrayList<String>();
	
	/**
	 * Constructor 
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
		
		if (attributeName != null) {
			// Current node belongs in the XSL Namespace.  We only want to do this
			// for the namespace.  Regardless of what the prefix is set too.
			if (namespace.equals(this.xslNamespace)) {
				// Note that this really should be the cursor position, so that the contents can be inserted
				// starting at the cursor position.
				int offset = contentAssistRequest.getStartOffset() + 1;
				if (attributeName.equals(ATTR_SELECT)) {
					
					addTemplates(contentAssistRequest, TemplateContextTypeIdsXPath.AXIS, offset);
					addTemplates(contentAssistRequest, TemplateContextTypeIdsXPath.XPATH, offset);
					addTemplates(contentAssistRequest, TemplateContextTypeIdsXPath.CUSTOM, offset);
					// Need to add special handling for variables.  Should query the
					// current document and provide a list of variables from param and variable
					// elements.   This should be limited to both the local scope and the
					// global variables.
					
				} else if (attributeName.equals(ATTR_TEST)) {
					addTemplates(contentAssistRequest, TemplateContextTypeIdsXPath.OPERATOR);
					addTemplates(contentAssistRequest, TemplateContextTypeIdsXPath.AXIS, offset);
					addTemplates(contentAssistRequest, TemplateContextTypeIdsXPath.XPATH, offset);
					addTemplates(contentAssistRequest, TemplateContextTypeIdsXPath.CUSTOM, offset);
				}
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
	 * Adds templates to the list of proposals
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
	
}
