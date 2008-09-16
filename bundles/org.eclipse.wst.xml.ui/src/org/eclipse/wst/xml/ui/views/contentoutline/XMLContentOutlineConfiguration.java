/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
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
package org.eclipse.wst.xml.ui.views.contentoutline;

import java.util.List;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.sse.ui.internal.contentoutline.PropertyChangeUpdateAction;
import org.eclipse.wst.sse.ui.internal.contentoutline.PropertyChangeUpdateActionContributionItem;
import org.eclipse.wst.sse.ui.internal.editor.EditorPluginImageHelper;
import org.eclipse.wst.sse.ui.internal.editor.EditorPluginImages;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.basic.CMNamedNodeMapImpl;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.ui.internal.XMLUIMessages;
import org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeContentProvider;
import org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeLabelProvider;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Outline Configuration for generic XML support, expects that the viewer's
 * input will be the DOM Model.
 * 
 * @see org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration
 * @since 1.0

 TODO: Add Sort and Hide Comment actions

 */
public class XMLContentOutlineConfiguration extends AbstractXMLContentOutlineConfiguration {

	private class AttributeShowingLabelProvider extends JFaceNodeLabelProvider {
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
		 */
		public String getText(Object o) {
			StringBuffer text = null;
			if (o instanceof Node) {
				text = new StringBuffer(super.getText(o));
				Node node = (Node) o;
				if ((node.getNodeType() == Node.ELEMENT_NODE) && fShowAttributes) {
					// https://bugs.eclipse.org/bugs/show_bug.cgi?id=88444
					if (node.hasAttributes()) {
						Element element = (Element) node;
						NamedNodeMap attributes = element.getAttributes();
						Node idTypedAttribute = null;
						Node requiredAttribute = null;
						boolean hasId = false;
						boolean hasName = false;
						Node shownAttribute = null;

						// try to get content model element
						// declaration
						CMElementDeclaration elementDecl = null;
						ModelQuery mq = ModelQueryUtil.getModelQuery(element.getOwnerDocument());
						if (mq != null) {
							elementDecl = mq.getCMElementDeclaration(element);
						}
						// find an attribute of type (or just named)
						// ID
						if (elementDecl != null) {
							int i = 0;
							while ((i < attributes.getLength()) && (idTypedAttribute == null)) {
								Node attr = attributes.item(i);
								String attrName = attr.getNodeName();
								CMNamedNodeMap attributeDeclarationMap = elementDecl.getAttributes();
								
								CMNamedNodeMapImpl allAttributes = new CMNamedNodeMapImpl(attributeDeclarationMap);
								List nodes = ModelQueryUtil.getModelQuery(node.getOwnerDocument()).getAvailableContent(element, elementDecl, ModelQuery.INCLUDE_ATTRIBUTES);
								for (int k = 0; k < nodes.size(); k++) {
									CMNode cmnode = (CMNode) nodes.get(k);
									if (cmnode.getNodeType() == CMNode.ATTRIBUTE_DECLARATION) {
										allAttributes.put(cmnode);
									}
								}
								attributeDeclarationMap = allAttributes;

								CMAttributeDeclaration attrDecl = (CMAttributeDeclaration) attributeDeclarationMap.getNamedItem(attrName);
								if (attrDecl != null) {
									if ((attrDecl.getAttrType() != null) && (CMDataType.ID.equals(attrDecl.getAttrType().getDataTypeName()))) {
										idTypedAttribute = attr;
									}
									else if ((attrDecl.getUsage() == CMAttributeDeclaration.REQUIRED) && (requiredAttribute == null)) {
										// as a backup, keep tabs on
										// any required
										// attributes
										requiredAttribute = attr;
									}
									else {
										hasId = hasId || attrName.equals("id"); //$NON-NLS-1$
										hasName = hasName || attrName.equals("name"); //$NON-NLS-1$
									}
								}
								++i;
							}
						}

						/*
						 * If no suitable attribute was found, try using a
						 * required attribute, if none, then prefer "id" or
						 * "name", otherwise just use first attribute
						 */
						if (idTypedAttribute != null) {
							shownAttribute = idTypedAttribute;
						}
						else if (requiredAttribute != null) {
							shownAttribute = requiredAttribute;
						}
						else if (hasId) {
							shownAttribute = attributes.getNamedItem("id"); //$NON-NLS-1$
						}
						else if (hasName) {
							shownAttribute = attributes.getNamedItem("name"); //$NON-NLS-1$
						}
						if (shownAttribute == null) {
							shownAttribute = attributes.item(0);
						}

						// display the attribute and value (without quotes)
						String attributeName = shownAttribute.getNodeName();
						if ((attributeName != null) && (attributeName.length() > 0)) {
							text.append(" "); //$NON-NLS-1$
							text.append(attributeName);
							String attributeValue = shownAttribute.getNodeValue();
							if ((attributeValue != null) && (attributeValue.length() > 0)) {
								text.append("="); //$NON-NLS-1$
								text.append(StringUtils.strip(attributeValue));
							}
						}
					}
				}
			}
			else {
				return super.toString();
			}
			return text.toString();
		}
	}

	/**
	 * Toggle action for whether or not to display element's first attribute
	 */
	private class ToggleShowAttributeAction extends PropertyChangeUpdateAction {
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=88444
		private TreeViewer fTreeViewer;

		public ToggleShowAttributeAction(IPreferenceStore store, String preference, TreeViewer treeViewer) {
			super(XMLUIMessages.XMLContentOutlineConfiguration_0, store, preference, true);
			setToolTipText(getText());
			// images needed
			// setDisabledImageDescriptor(SYNCED_D);
			// (nsd) temporarily re-use Properties view image
			setImageDescriptor(EditorPluginImageHelper.getInstance().getImageDescriptor(EditorPluginImages.IMG_OBJ_PROP_PS));
			fTreeViewer = treeViewer;
			update();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.ui.texteditor.IUpdate#update()
		 */
		public void update() {
			super.update();
			fShowAttributes = isChecked();

			// notify the configuration of the change
			enableShowAttributes(fShowAttributes, fTreeViewer);

			// refresh the outline view
			fTreeViewer.refresh(true);
		}
	}
	private ILabelProvider fAttributeShowingLabelProvider;
	private IContentProvider fContentProvider = null;

	boolean fShowAttributes = false;

	/*
	 * Preference key for Show Attributes
	 */
	private final String OUTLINE_SHOW_ATTRIBUTE_PREF = "outline-show-attribute-editor"; //$NON-NLS-1$

	/**
	 * Create new instance of XMLContentOutlineConfiguration
	 */
	public XMLContentOutlineConfiguration() {
		// Must have empty constructor to createExecutableExtension
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration#createMenuContributions(org.eclipse.jface.viewers.TreeViewer)
	 */
	protected IContributionItem[] createMenuContributions(TreeViewer viewer) {
		IContributionItem[] items;
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=88444
		IContributionItem showAttributeItem = new PropertyChangeUpdateActionContributionItem(new ToggleShowAttributeAction(getPreferenceStore(), OUTLINE_SHOW_ATTRIBUTE_PREF, viewer));
		items = super.createMenuContributions(viewer);
		if (items == null) {
			items = new IContributionItem[]{showAttributeItem};
		}
		else {
			IContributionItem[] combinedItems = new IContributionItem[items.length + 1];
			System.arraycopy(items, 0, combinedItems, 0, items.length);
			combinedItems[items.length] = showAttributeItem;
			items = combinedItems;
		}
		return items;
	}

	/**
	 * Notifies this configuration that the flag that indicates whether or not
	 * to show attribute values in the tree viewer has changed. The tree
	 * viewer is automatically refreshed afterwards to update the labels.
	 * 
	 * Clients should not call this method, but rather should react to it.
	 * 
	 * @param showAttributes
	 *            flag indicating whether or not to show attribute values in
	 *            the tree viewer
	 * @param treeViewer
	 *            the TreeViewer associated with this configuration
	 */
	protected void enableShowAttributes(boolean showAttributes, TreeViewer treeViewer) {
		// nothing by default
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration#getContentProvider(org.eclipse.jface.viewers.TreeViewer)
	 */
	public IContentProvider getContentProvider(TreeViewer viewer) {
		if (fContentProvider == null) {
			fContentProvider = new JFaceNodeContentProvider();
		}
		return fContentProvider;
	}

	private Object getFilteredNode(Object object) {
		if (object instanceof Node) {
			Node node = (Node) object;
	
			// replace attribute node in selection with its parent
			if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
				node = ((Attr) node).getOwnerElement();
			}
			else if (node.getNodeType() == Node.TEXT_NODE) {
				node = node.getParentNode();
			}
			return node;
		}
		return object;
	}

	private Object[] getFilteredNodes(Object[] filteredNodes) {
		for (int i = 0; i < filteredNodes.length; i++) {
			filteredNodes[i] = getFilteredNode(filteredNodes[i]);
		}
		return filteredNodes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration#getLabelProvider(org.eclipse.jface.viewers.TreeViewer)
	 */
	public ILabelProvider getLabelProvider(TreeViewer viewer) {
		if (fAttributeShowingLabelProvider == null) {
			fAttributeShowingLabelProvider = new AttributeShowingLabelProvider();
		}
		return fAttributeShowingLabelProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration#getSelection(org.eclipse.jface.viewers.TreeViewer,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public ISelection getSelection(TreeViewer viewer, ISelection selection) {
		ISelection filteredSelection = selection;
		if (selection instanceof IStructuredSelection) {
			Object[] filteredNodes = getFilteredNodes(((IStructuredSelection) selection).toArray());
			filteredSelection = new StructuredSelection(filteredNodes);
		}
		return filteredSelection;
	}

}
