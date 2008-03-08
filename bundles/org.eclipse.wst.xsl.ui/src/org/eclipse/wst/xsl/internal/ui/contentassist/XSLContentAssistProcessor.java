/**
 * 
 */
package org.eclipse.wst.xsl.internal.ui.contentassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateException;
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
import org.eclipse.wst.xsl.internal.XSLUIPlugin;
import org.eclipse.wst.xsl.internal.ui.templates.TemplateContextTypeIdsXPath;

/**
 * @author dcarver
 *
 */
@SuppressWarnings("restriction")
public class XSLContentAssistProcessor extends XMLContentAssistProcessor implements IPropertyChangeListener {

	private String xslNamespace = "http://www.w3.org/1999/XSL/Transform";
	
	protected IPreferenceStore fPreferenceStore = null;
	protected IResource fResource = null;
	private XPathTemplateCompletionProcessor fTemplateProcessor = null;
	private List fTemplateContexts = new ArrayList();
	
	/**
	 * 
	 */
	public XSLContentAssistProcessor() {
		super();
	}
	
	
	/**
	 * Adds Attribute proposals based on the element and the attribute
	 * where the content proposal was instatiated.
	 * 
	 * @param contentAssistRequest Content Assist Request that initiated the proposal request
	 * 
	 */
	@Override
	protected void addAttributeValueProposals(
			ContentAssistRequest contentAssistRequest) {
		IDOMNode node = (IDOMNode)contentAssistRequest.getNode();
        String namespace = DOMNamespaceHelper.getNamespaceURI(node);
		String nodeName = DOMNamespaceHelper.getUnprefixedName(node.getNodeName());
		String attributeName = getAttributeName(contentAssistRequest);
		
		if (attributeName != null) {
			// Current node belongs in the XSL Namespace
			if (namespace.equals(this.xslNamespace)) {
				if (attributeName.equals("select")) {
					if (nodeName.equals("param") ||
						nodeName.equals("variable")){
						addTemplates(contentAssistRequest, TemplateContextTypeIdsXPath.AXIS);
						addTemplates(contentAssistRequest, TemplateContextTypeIdsXPath.XPATH);
					}
				}
			}
		}
		super.addAttributeValueProposals(contentAssistRequest);
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
	
	protected IPreferenceStore getPreferenceStore() {
		if (fPreferenceStore == null) {
			fPreferenceStore = XSLUIPlugin.getDefault().getPreferenceStore();
		}
		return fPreferenceStore;
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
