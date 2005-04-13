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



import org.eclipse.core.resources.IResource;
import org.eclipse.jst.jsp.core.JSP11Namespace;
import org.eclipse.jst.jsp.ui.internal.editor.JSPEditorPluginImageHelper;
import org.eclipse.jst.jsp.ui.internal.editor.JSPEditorPluginImages;
import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.core.internal.util.StringUtils;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.text.ITextRegionList;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xml.core.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.ui.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.contentassist.XMLRelevanceConstants;
import org.eclipse.wst.xml.ui.util.SharedXMLEditorPluginImageHelper;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class computes attribute value completion proposals for &lt;jsp:[gs]etProperty&gt; tags.
 * @since 1.0
 */
public class JSPPropertyContentAssistProcessor extends JSPDummyContentAssistProcessor {

	protected IResource resource = null;

	public JSPPropertyContentAssistProcessor() {
		super();
	}

	protected void addAttributeValueProposals(ContentAssistRequest contentAssistRequest) {
		IDOMNode node = (IDOMNode) contentAssistRequest.getNode();

		// Find the attribute name for which this position should have a value
		IStructuredDocumentRegion open = node.getFirstStructuredDocumentRegion();
		ITextRegionList openRegions = open.getRegions();
		int i = openRegions.indexOf(contentAssistRequest.getRegion());
		if (i < 0)
			return;

		// get the attribute in question (first attr name to the left of the cursor)
		ITextRegion attrNameRegion = null;
		String attributeName = null;
		while (i >= 0) {
			attrNameRegion = openRegions.get(i--);
			if (attrNameRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME)
				break;
		}
		if (attrNameRegion != null)
			attributeName = open.getText(attrNameRegion);

		// determine get or set
		ITextRegion tagNameRegion = null;
		boolean isGetProperty = true;
		for (int j = 0; j < openRegions.size(); j++) {
			tagNameRegion = openRegions.get(j);
			if (tagNameRegion.getType() == DOMRegionContext.XML_TAG_NAME && open.getText(tagNameRegion).trim().equals("jsp:setProperty")) { //$NON-NLS-1$
				isGetProperty = false;
				break;
			}
		}

		String currentValue = null;
		if (contentAssistRequest.getRegion().getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE)
			currentValue = contentAssistRequest.getText();
		else
			currentValue = ""; //$NON-NLS-1$
		String matchString = null;
		// fixups since the matchString computations don't care if there are quotes around the value
		if (currentValue.length() > StringUtils.strip(currentValue).length() && (currentValue.startsWith("\"") || currentValue.startsWith("'")) && contentAssistRequest.getMatchString().length() > 0) //$NON-NLS-1$ //$NON-NLS-2$
			matchString = currentValue.substring(1, contentAssistRequest.getMatchString().length());
		else
			matchString = currentValue.substring(0, contentAssistRequest.getMatchString().length());

		// for now we ignore complicated values such as jsp embedded in an attribute
		boolean existingComplicatedValue = contentAssistRequest.getRegion() != null && contentAssistRequest.getRegion() instanceof ITextRegionContainer;
		if (existingComplicatedValue) {
			contentAssistRequest.getProposals().clear();
			contentAssistRequest.getMacros().clear();
		}
		else {
			if (attributeName.equals(JSP11Namespace.ATTR_NAME_NAME)) {
				addBeanNameProposals(contentAssistRequest, node, matchString);
			}
			else if (attributeName.equals(JSP11Namespace.ATTR_NAME_PROPERTY)) {
				addBeanPropertyProposals(contentAssistRequest, node, isGetProperty, matchString);
			}
		}
	}

	private void addBeanPropertyProposals(ContentAssistRequest contentAssistRequest, IDOMNode node, boolean isGetProperty, String matchString) {
		// assumes that the node is the [gs]etProperty tag
		String useBeanName = ((Element) node).getAttribute(JSP11Namespace.ATTR_NAME_NAME);
		// properties can only be provided if a class/type/beanName has been declared
		if (useBeanName != null && useBeanName.length() > 0) {
			NodeList useBeans = node.getOwnerDocument().getElementsByTagName(JSP11Namespace.ElementName.USEBEAN);
			if (useBeans != null) {
				String typeName = null;
				for (int j = 0; j < useBeans.getLength(); j++) {
					if (useBeans.item(j).getNodeType() != Node.ELEMENT_NODE)
						continue;
					Element useBean = (Element) useBeans.item(j);
					if (useBean instanceof IndexedRegion && ((IndexedRegion) useBean).getStartOffset() < node.getStartOffset()) {
						if (useBean.getAttribute(JSP11Namespace.ATTR_NAME_ID).equals(useBeanName)) {
							typeName = useBean.getAttribute(JSP11Namespace.ATTR_NAME_CLASS);
							if (typeName == null || typeName.length() < 1) {
								typeName = useBean.getAttribute(JSP11Namespace.ATTR_NAME_TYPE);
							}
							if (typeName == null || typeName.length() < 1) {
								typeName = useBean.getAttribute(JSP11Namespace.ATTR_NAME_BEAN_NAME);
							}
						}
					}
				}
				if (typeName != null && typeName.length() > 0) {
					// find the class/type/beanName definition and obtain the list of properties
					IBeanInfoProvider provider = new BeanInfoProvider();
					IJavaPropertyDescriptor[] descriptors = provider.getRuntimeProperties(resource, typeName);
					CustomCompletionProposal proposal = null;
					String displayString = ""; //$NON-NLS-1$
					for (int j = 0; j < descriptors.length; j++) {
						IJavaPropertyDescriptor pd = descriptors[j];
						// check whether it's get or set kinda property
						if (pd.getReadable() && isGetProperty || pd.getWriteable() && !isGetProperty) {
							// filter attr value name
							if (matchString.length() == 0 || pd.getName().toLowerCase().startsWith(matchString.toLowerCase())) {
								displayString = pd.getDisplayName();
								if (pd.getDeclaredType() != null && pd.getDeclaredType().length() > 0)
									displayString += " - " + pd.getDeclaredType(); //$NON-NLS-1$
								proposal = new CustomCompletionProposal("\"" + pd.getName() + "\"", //$NON-NLS-1$ //$NON-NLS-2$
											contentAssistRequest.getReplacementBeginPosition(), contentAssistRequest.getReplacementLength(), pd.getName().length() + 2, SharedXMLEditorPluginImageHelper.getImage(SharedXMLEditorPluginImageHelper.IMG_OBJ_ATTRIBUTE),
											displayString, null, pd.getDeclaredType(), XMLRelevanceConstants.R_XML_ATTRIBUTE_VALUE);
								contentAssistRequest.addProposal(proposal);
							}
						}
					}
				}
			}
		}
	}

	private void addBeanNameProposals(ContentAssistRequest contentAssistRequest, IDOMNode node, String matchString) {
		// will not catch useBeans specified using other than actual DOM Nodes
		NodeList useBeans = node.getOwnerDocument().getElementsByTagName(JSP11Namespace.ElementName.USEBEAN);
		if (useBeans != null) {
			String id = ""; //$NON-NLS-1$
			String displayString = null;
			String classOrType = null;
			String imageName = JSPEditorPluginImages.IMG_OBJ_CLASS_OBJ;
			for (int j = 0; j < useBeans.getLength(); j++) {
				if (useBeans.item(j).getNodeType() != Node.ELEMENT_NODE)
					continue;
				Element useBean = (Element) useBeans.item(j);
				if (useBean instanceof IndexedRegion && ((IndexedRegion) useBean).getStartOffset() < node.getStartOffset()) {
					id = StringUtils.strip(useBean.getAttribute(JSP11Namespace.ATTR_NAME_ID));
					displayString = null;
					classOrType = null;
					imageName = JSPEditorPluginImages.IMG_OBJ_CLASS_OBJ;
					// set the Image based on whether the class, type, or beanName attribute is present
					classOrType = useBean.getAttribute(JSP11Namespace.ATTR_NAME_CLASS);
					if (classOrType == null || classOrType.length() < 1) {
						classOrType = useBean.getAttribute(JSP11Namespace.ATTR_NAME_TYPE);
						imageName = JSPEditorPluginImages.IMG_OBJ_PUBLIC;
					}
					if (classOrType == null || classOrType.length() < 1) {
						classOrType = useBean.getAttribute(JSP11Namespace.ATTR_NAME_BEAN_NAME);
						imageName = JSPEditorPluginImages.IMG_OBJ_PUBLIC;
					}
					if (classOrType != null && classOrType.length() > 0)
						displayString = id + " - " + classOrType; //$NON-NLS-1$
					else
						displayString = id;

					// https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=2341
					if(id != null) {
					    // filter
						if (matchString.length() == 0 || id.startsWith(matchString)) {
							CustomCompletionProposal proposal = new CustomCompletionProposal("\"" + id + "\"", //$NON-NLS-1$ //$NON-NLS-2$
										contentAssistRequest.getReplacementBeginPosition(), 
                                        contentAssistRequest.getReplacementLength(), 
                                        id.length() + 2, 
                                        JSPEditorPluginImageHelper.getInstance().getImage(imageName), 
                                        displayString, 
                                        null, 
                                        null, 
                                        XMLRelevanceConstants.R_XML_ATTRIBUTE_VALUE);
							contentAssistRequest.addProposal(proposal);
						}
					}
				}
			}
		}
	}

	public void release() {
		resource = null;
	}
	
	public void initialize(IResource iResource) {
		this.resource = iResource;
	}

}