/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.ui.taginfo;



import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.contentmodel.CMElementDeclaration;
import org.eclipse.wst.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.contentmodel.CMNode;
import org.eclipse.wst.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.contentmodel.util.DOMNamespaceHelper;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.ITextRegionList;
import org.eclipse.wst.sse.core.util.Debug;
import org.eclipse.wst.sse.ui.StructuredTextViewer;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.eclipse.wst.xml.core.document.XMLNode;
import org.eclipse.wst.xml.core.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;
import org.eclipse.wst.xml.ui.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Provides hover help documentation for xml tags
 * 
 * @author amywu
 * @see org.eclipse.jface.text.ITextHover
 */
public class XMLTagInfoHoverProcessor implements ITextHover {
	private static final String EDITOR_PLUGIN_ID = "org.eclipse.wst.sse.ui"; //$NON-NLS-1$
	/**
	 * @deprecated hover processor no longer preference store-dependent
	 */
	protected IPreferenceStore fPreferenceStore = null;
	protected MarkupTagInfoProvider fInfoProvider = null;

	/**
	 * Constructor for XMLTextHoverProcessor.
	 */
	public XMLTagInfoHoverProcessor() {
	}

	/**
	 * Retreives documentation to display in the hover help popup.
	 * 
	 * @return String any documentation information to display
	 * <code>null</code> if there is nothing to display.
	 * 
	 */
	protected String computeHoverHelp(ITextViewer textViewer, int documentPosition) {
		String result = null;

		IndexedRegion treeNode = ContentAssistUtils.getNodeAt((StructuredTextViewer) textViewer, documentPosition);
		if (treeNode == null)
			return null;
		Node node = (Node) treeNode;

		while (node != null && node.getNodeType() == Node.TEXT_NODE && node.getParentNode() != null)
			node = node.getParentNode();
		XMLNode parentNode = (XMLNode) node;

		IStructuredDocumentRegion flatNode = ((IStructuredDocument) textViewer.getDocument()).getRegionAtCharacterOffset(documentPosition);
		if (flatNode != null) {
			ITextRegion region = flatNode.getRegionAtCharacterOffset(documentPosition);
			if (region != null) {
				result = computeRegionHelp(treeNode, parentNode, flatNode, region);
			}
		}

		return result;
	}

	/**
	 * Computes the hoverhelp based on region
	 * @return String hoverhelp
	 */
	protected String computeRegionHelp(IndexedRegion treeNode, XMLNode parentNode, IStructuredDocumentRegion flatNode, ITextRegion region) {
		String result = null;
		if (region == null)
			return null;
		String regionType = region.getType();
		if (regionType == XMLRegionContext.XML_TAG_NAME)
			result = computeTagNameHelp((XMLNode) treeNode, parentNode, flatNode, region);
		else if (regionType == XMLRegionContext.XML_TAG_ATTRIBUTE_NAME)
			result = computeTagAttNameHelp((XMLNode) treeNode, parentNode, flatNode, region);
		else if (regionType == XMLRegionContext.XML_TAG_ATTRIBUTE_VALUE)
			result = computeTagAttValueHelp((XMLNode) treeNode, parentNode, flatNode, region);
		return result;
	}

	/**
	 * Computes the hover help for the tag name
	 */
	protected String computeTagNameHelp(XMLNode xmlnode, XMLNode parentNode, IStructuredDocumentRegion flatNode, ITextRegion region) {
		CMElementDeclaration elementDecl = getCMElementDeclaration(xmlnode);
		CMElementDeclaration pelementDecl = getCMElementDeclaration(parentNode);
		return getAdditionalInfo(pelementDecl, elementDecl);
	}

	/**
	 * Computes the hover help for the attribute name
	 */
	protected String computeTagAttNameHelp(XMLNode xmlnode, XMLNode parentNode, IStructuredDocumentRegion flatNode, ITextRegion region) {
		CMElementDeclaration elementDecl = getCMElementDeclaration(xmlnode);
		String attName = flatNode.getText(region);
		CMAttributeDeclaration attDecl = getCMAttributeDeclaration(elementDecl, attName);
		return getAdditionalInfo(elementDecl, attDecl);
	}

	/**
	 * Computes the hover help for the attribute value (this is the same as the attribute name's help)
	 */
	protected String computeTagAttValueHelp(XMLNode xmlnode, XMLNode parentNode, IStructuredDocumentRegion flatNode, ITextRegion region) {
		CMElementDeclaration elementDecl = getCMElementDeclaration(xmlnode);
		ITextRegion attrNameRegion = getAttrNameRegion(xmlnode, region);

		String attName = flatNode.getText(attrNameRegion);
		CMAttributeDeclaration attDecl = getCMAttributeDeclaration(elementDecl, attName);
		return getAdditionalInfo(elementDecl, attDecl);
	}

	/**
	 * Retreives CMElementDeclaration for given node
	 * @param Node node
	 * 
	 * @return CMElementDeclaration - CMElementDeclaration of node or <code>null</code> if not possible
	 */
	protected CMElementDeclaration getCMElementDeclaration(Node node) {
		CMElementDeclaration result = null;
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			ModelQuery modelQuery = ModelQueryUtil.getModelQuery(node.getOwnerDocument());
			if (modelQuery != null)
				result = modelQuery.getCMElementDeclaration((Element) node);
		}
		return result;
	}

	/**
	 * Retreives CMAttributeDeclaration indicated by attribute name within elementDecl
	 */
	protected CMAttributeDeclaration getCMAttributeDeclaration(CMElementDeclaration elementDecl, String attName) {
		CMAttributeDeclaration attrDecl = null;

		if (elementDecl != null) {
			CMNamedNodeMap attributes = elementDecl.getAttributes();
			String noprefixName = DOMNamespaceHelper.getUnprefixedName(attName);
			if (attributes != null) {
				attrDecl = (CMAttributeDeclaration) attributes.getNamedItem(noprefixName);
				if (attrDecl == null) {
					attrDecl = (CMAttributeDeclaration) attributes.getNamedItem(attName);
				}
			}
		}
		return attrDecl;
	}

	/**
	 * Find the region of the attribute name for the given attribute value region
	 * @param XMLNode node - parent node
	 * @param ITextRegion region - region of attribute value
	 * 
	 * @return region - attribute name region for given region
	 */
	protected ITextRegion getAttrNameRegion(XMLNode node, ITextRegion region) {
		// Find the attribute name for which this position should have a value
		IStructuredDocumentRegion open = node.getFirstStructuredDocumentRegion();
		ITextRegionList openRegions = open.getRegions();
		int i = openRegions.indexOf(region);
		if (i < 0)
			return null;
		ITextRegion nameRegion = null;
		while (i >= 0) {
			nameRegion = openRegions.get(i--);
			if (nameRegion.getType() == XMLRegionContext.XML_TAG_ATTRIBUTE_NAME)
				break;
		}
		return nameRegion;
	}

	/**
	 * Retreives cmnode's documentation to display in the hover help popup.
	 * If no documentation exists for cmnode, try displaying
	 * parentOrOwner's documentation
	 * 
	 * @return String any documentation information to display for cmnode.  
	 * <code>null</code> if there is nothing to display.
	 */
	protected String getAdditionalInfo(CMNode parentOrOwner, CMNode cmnode) {
		String addlInfo = null;

		if (cmnode == null) {
			if (Debug.displayWarnings) {
				new IllegalArgumentException("Null declaration!").printStackTrace(); //$NON-NLS-1$
			}
			return null;
		}

		addlInfo = getInfoProvider().getInfo(cmnode);
		if (addlInfo == null && parentOrOwner != null)
			addlInfo = getInfoProvider().getInfo(parentOrOwner);
		return addlInfo;
	}

	/**
	 * Gets the infoProvider.
	 * @return Returns fInfoProvider and if fInfoProvider was <code>null</code> set fInfoProvider to DefaultInfoProvider
	 */
	public MarkupTagInfoProvider getInfoProvider() {
		if (fInfoProvider == null) {
			fInfoProvider = new MarkupTagInfoProvider();
		}
		return fInfoProvider;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHover#getHoverInfo(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
	 */
	public String getHoverInfo(ITextViewer viewer, IRegion hoverRegion) {
		if ((hoverRegion == null) || (viewer == null) || (viewer.getDocument() == null))
			return null;

		String displayText = null;
		int documentOffset = hoverRegion.getOffset();
		displayText = computeHoverHelp(viewer, documentOffset);

		return displayText;
	}

	/**
	 * @deprecated if enabled flag is false, dont call getHoverInfo in the first place if true, use getHoverInfo(ITextViewer, int)
	 */
	public String getHoverInfo(ITextViewer viewer, IRegion hoverRegion, boolean enabled) {
		if ((!enabled) || (hoverRegion == null) || (viewer == null) || (viewer.getDocument() == null))
			return null;

		String displayText = null;
		int documentOffset = hoverRegion.getOffset();
		displayText = computeHoverHelp(viewer, documentOffset);

		return displayText;
	}

	/**
	 * Returns the region to hover the text over based on the offset.
	 * @param textViewer
	 * @param offset
	 * 
	 * @return IRegion region to hover over if offset is within tag name, attribute
	 * name, or attribute value and if offset is
	 * not over invalid whitespace.  otherwise, returns <code>null</code>
	 * 
	 * @see ITextHover#getHoverRegion(ITextViewer, int)
	 */
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		if ((textViewer == null) || (textViewer.getDocument() == null))
			return null;

		IStructuredDocumentRegion flatNode = ((IStructuredDocument) textViewer.getDocument()).getRegionAtCharacterOffset(offset);
		ITextRegion region = null;

		if (flatNode != null) {
			region = flatNode.getRegionAtCharacterOffset(offset);
		}

		if (region != null) {
			// only supply hoverhelp for tag name, attribute name, or attribute value
			String regionType = region.getType();
			if ((regionType == XMLRegionContext.XML_TAG_NAME) || (regionType == XMLRegionContext.XML_TAG_ATTRIBUTE_NAME) || (regionType == XMLRegionContext.XML_TAG_ATTRIBUTE_VALUE)) {
				try {
					// check if we are at whitespace before or after line
					IRegion line = textViewer.getDocument().getLineInformationOfOffset(offset);
					if ((offset > (line.getOffset())) && (offset < (line.getOffset() + line.getLength()))) {
						// check if we are in region's trailing whitespace (whitespace after relevant info)
						if (offset < flatNode.getTextEndOffset(region)) {
							return new Region(flatNode.getStartOffset(region), region.getTextLength());
						}
					}
				}
				catch (BadLocationException e) {
					Logger.logException(e);
				}
			}
		}
		return null;
	}

	/**
	 * @deprecated if enabled flag is false, dont call getHoverRegion in the first place if true, use getHoverRegion(ITextViewer, int)
	 */
	public IRegion getHoverRegion(ITextViewer textViewer, int offset, boolean enabled) {
		if ((!enabled) || (textViewer == null) || (textViewer.getDocument() == null))
			return null;

		IStructuredDocumentRegion flatNode = ((IStructuredDocument) textViewer.getDocument()).getRegionAtCharacterOffset(offset);
		ITextRegion region = null;

		if (flatNode != null) {
			region = flatNode.getRegionAtCharacterOffset(offset);
		}

		if (region != null) {
			// only supply hoverhelp for tag name, attribute name, or attribute value
			String regionType = region.getType();
			if ((regionType == XMLRegionContext.XML_TAG_NAME) || (regionType == XMLRegionContext.XML_TAG_ATTRIBUTE_NAME) || (regionType == XMLRegionContext.XML_TAG_ATTRIBUTE_VALUE)) {
				try {
					// check if we are at whitespace before or after line
					IRegion line = textViewer.getDocument().getLineInformationOfOffset(offset);
					if ((offset > (line.getOffset())) && (offset < (line.getOffset() + line.getLength()))) {
						// check if we are in region's trailing whitespace (whitespace after relevant info)
						if (offset < flatNode.getTextEndOffset(region)) {
							return new Region(flatNode.getStartOffset(region), region.getTextLength());
						}
					}
				}
				catch (BadLocationException e) {
					Logger.logException(e);
				}
			}
		}
		return null;
	}

	/**
	 * @deprecated hover processor should not be preference store-dependent
	 */
	protected IPreferenceStore getPreferenceStore() {
		if (fPreferenceStore == null) {
			fPreferenceStore = ((AbstractUIPlugin) Platform.getPlugin(EDITOR_PLUGIN_ID)).getPreferenceStore();
		}
		return fPreferenceStore;
	}

	/**
	 * @deprecated this method will not longer be used once other deprecated methods are removed
	 */
	private IModelManager getModelManager() {
		IModelManagerPlugin plugin = (IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID);
		return plugin.getModelManager();
	}

	/**
	 * @deprecated text hovers should not be preference dependent.  see EditorPlugin.getTextHovers
	 */
	protected boolean isHoverHelpEnabled(ITextViewer viewer) {
		// returning false becaues this method is deprecated
		return false;
	}
}
