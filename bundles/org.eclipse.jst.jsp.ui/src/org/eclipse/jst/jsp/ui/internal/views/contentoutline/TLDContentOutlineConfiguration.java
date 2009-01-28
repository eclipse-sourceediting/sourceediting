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
package org.eclipse.jst.jsp.ui.internal.views.contentoutline;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.JSP11TLDNames;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.JSP12TLDNames;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.sse.ui.internal.contentoutline.PropertyChangeUpdateAction;
import org.eclipse.wst.sse.ui.internal.contentoutline.PropertyChangeUpdateActionContributionItem;
import org.eclipse.wst.sse.ui.internal.editor.EditorPluginImageHelper;
import org.eclipse.wst.sse.ui.internal.editor.EditorPluginImages;
import org.eclipse.wst.xml.ui.views.contentoutline.XMLContentOutlineConfiguration;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TLDContentOutlineConfiguration extends XMLContentOutlineConfiguration {

	private class ContentLabelProvider implements ILabelProvider {
		ILabelProvider fParentProvider = null;

		ContentLabelProvider(ILabelProvider parent) {
			super();
			fParentProvider = parent;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse
		 * .jface.viewers.ILabelProviderListener)
		 */
		public void addListener(ILabelProviderListener listener) {
			fParentProvider.addListener(listener);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
		 */
		public void dispose() {
			fParentProvider.dispose();
		}

		private String firstLineOf(String text) {
			if (text == null || text.length() < 1 || (text.indexOf('\r') < 0 && text.indexOf('\n') < 0)) {
				return text;
			}

			int start = 0;
			int maxLength = text.length();
			while (start < maxLength && text.charAt(start) == '\r' || text.charAt(start) == '\n')
				start++;
			int endN = text.indexOf('\n', start);
			int endR = text.indexOf('\r', start);
			// no more line delimiters
			if (endN < 0 && endR < 0) {
				if (start == 0) {
					// no leading line delimiters, return as-is
					return text;
				}
				else {
					// cut leading line delimiters
					return text.substring(start);
				}
			}
			if (endN < 0) {
				/* no \r cut leading line delimiters up to first \r */
				return text.substring(start, endR);
			}
			if (endR < 0) {
				/* no \r cut leading line delimiters up to first \n */
				return text.substring(start, endN);
			}

			/*
			 * Both \n and \r, cut leading line delimiters up to whichever is
			 * first
			 */
			return text.substring(start, Math.min(endN, endR));
		}

		private String getContainedText(Node parent) {
			NodeList children = parent.getChildNodes();
			if (children.getLength() == 1) {
				return getValue(children.item(0));
			}
			StringBuffer s = new StringBuffer();
			Node child = parent.getFirstChild();
			while (child != null) {
				if (child.getNodeType() == Node.ENTITY_REFERENCE_NODE) {
					String reference = ((EntityReference) child).getNodeValue();
					if (reference == null && child.getNodeName() != null) {
						reference = "&" + child.getNodeName() + ";"; //$NON-NLS-1$ //$NON-NLS-2$
					}
					if (reference != null) {
						s.append(reference.trim());
					}
				}
				else {
					s.append(getValue(child));
				}
				child = child.getNextSibling();
			}
			return s.toString().trim();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
		 */
		public Image getImage(Object element) {
			return fParentProvider.getImage(element);
		}

		/**
		 * @param domElement
		 * @return
		 */
		private String getNameValue(Element domElement) {
			NodeList names = domElement.getElementsByTagName(JSP11TLDNames.NAME);
			String name = null;
			for (int i = 0; i < names.getLength() && (name == null || name.length() == 0); i++) {
				name = getContainedText(names.item(i));
			}
			return name;
		}

		/**
		 * @param domElement
		 * @return
		 */
		private String getShortNameValue(Element domElement) {
			NodeList names = domElement.getElementsByTagName(JSP12TLDNames.SHORT_NAME);
			String name = null;
			for (int i = 0; i < names.getLength() && (name == null || name.length() == 0); i++) {
				name = getContainedText(names.item(i));
			}
			names = domElement.getElementsByTagName(JSP11TLDNames.SHORTNAME);
			for (int i = 0; i < names.getLength() && (name == null || name.length() == 0); i++) {
				name = getContainedText(names.item(i));
			}
			return name;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
		 */
		public String getText(Object element) {
			if (fShowContentValues && element instanceof Element) {
				Element domElement = (Element) element;
				String name = domElement.getNodeName();
				boolean showName = JSP11TLDNames.TAG.equals(name);
				showName = showName || JSP11TLDNames.ATTRIBUTE.equals(name);
				if (showName) {
					String value = getNameValue(domElement);
					if (value != null && value.length() > 0) {
						/**
						 * Currently not externalized since it's analagous to
						 * a decorator.
						 */
						return fParentProvider.getText(domElement) + " [" + firstLineOf(value) + "]"; //$NON-NLS-1$
					}
				}

				boolean showContents = JSP11TLDNames.NAME.equals(name);
				showContents = showContents || JSP11TLDNames.BODYCONTENT.equals(name);
				showContents = showContents || JSP12TLDNames.BODY_CONTENT.equals(name);
				showContents = showContents || JSP11TLDNames.TAGCLASS.equals(name);
				showContents = showContents || JSP12TLDNames.TAG_CLASS.equals(name);
				showContents = showContents || JSP11TLDNames.TEICLASS.equals(name);
				showContents = showContents || JSP12TLDNames.TEI_CLASS.equals(name);
				showContents = showContents || JSP11TLDNames.REQUIRED.equals(name);
				showContents = showContents || JSP11TLDNames.RTEXPRVALUE.equals(name);
				showContents = showContents || JSP11TLDNames.URI.equals(name);
				showContents = showContents || JSP11TLDNames.SHORTNAME.equals(name);
				showContents = showContents || JSP12TLDNames.SHORT_NAME.equals(name);
				showContents = showContents || JSP12TLDNames.DISPLAY_NAME.equals(name);
				showContents = showContents || JSP11TLDNames.JSPVERSION.equals(name);
				showContents = showContents || JSP12TLDNames.JSP_VERSION.equals(name);
				showContents = showContents || JSP11TLDNames.TLIBVERSION.equals(name);
				showContents = showContents || JSP12TLDNames.TLIB_VERSION.equals(name);
				showContents = showContents || JSP12TLDNames.LISTENER_CLASS.equals(name);
				showContents = showContents || JSP12TLDNames.VARIABLE_SCOPE.equals(name);
				showContents = showContents || JSP12TLDNames.VARIABLE_CLASS.equals(name);
				showContents = showContents || JSP12TLDNames.VARIABLE_DECLARE.equals(name);
				showContents = showContents || JSP12TLDNames.VARIABLE_NAME_FROM_ATTRIBUTE.equals(name);
				showContents = showContents || JSP12TLDNames.VARIABLE_NAME_GIVEN.equals(name);
				showContents = showContents || JSP12TLDNames.VALIDATOR_CLASS.equals(name);
				showContents = showContents || JSP12TLDNames.SMALL_ICON.equals(name);
				showContents = showContents || JSP12TLDNames.LARGE_ICON.equals(name);

				if (showContents) {
					return fParentProvider.getText(domElement) + ": " + getContainedText(domElement); //$NON-NLS-1$
				}

				if (JSP11TLDNames.TAGLIB.equals(name)) {
					String value = getShortNameValue(domElement);
					if (value != null && value.length() > 0) {
						/**
						 * Currently not externalized since it's analagous to
						 * a decorator.
						 */
						return fParentProvider.getText(domElement) + " [" + value + "]"; //$NON-NLS-1$
					}
				}
			}
			return fParentProvider.getText(element);
		}

		private String getValue(Node n) {
			if (n == null)
				return ""; //$NON-NLS-1$
			String value = n.getNodeValue();
			if (value == null)
				return ""; //$NON-NLS-1$
			return value.trim();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java
		 * .lang.Object, java.lang.String)
		 */
		public boolean isLabelProperty(Object element, String property) {
			return fParentProvider.isLabelProperty(element, property);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org
		 * .eclipse.jface.viewers.ILabelProviderListener)
		 */
		public void removeListener(ILabelProviderListener listener) {
			fParentProvider.removeListener(listener);
		}
	}

	/**
	 * Toggle action for whether or not to display element's first attribute
	 */
	private class ToggleShowValueAction extends PropertyChangeUpdateAction {
		private TreeViewer fTreeViewer;

		public ToggleShowValueAction(IPreferenceStore store, String preference, TreeViewer treeViewer) {
			super(JSPUIMessages.TLDContentOutlineConfiguration_0, store, preference, true);
			setToolTipText(getText());
			// TODO: image needed
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
			fShowContentValues = isChecked();

			// refresh the outline view
			fTreeViewer.refresh(true);
		}
	}

	/*
	 * Preference key for Show Attributes
	 */
	private final String OUTLINE_SHOW_VALUE_PREF = "outline-show-value"; //$NON-NLS-1$

	boolean fShowContentValues = true;
	ILabelProvider fLabelProvider = null;

	public TLDContentOutlineConfiguration() {
		super();
	}

	protected IContributionItem[] createMenuContributions(TreeViewer viewer) {
		IContributionItem[] items;
		IContributionItem showValueItem = new PropertyChangeUpdateActionContributionItem(new ToggleShowValueAction(getPreferenceStore(), OUTLINE_SHOW_VALUE_PREF, viewer));

		items = super.createMenuContributions(viewer);
		if (items == null) {
			items = new IContributionItem[]{showValueItem};
		}
		else {
			IContributionItem[] combinedItems = new IContributionItem[items.length + 1];
			System.arraycopy(items, 0, combinedItems, 0, items.length);
			combinedItems[items.length] = showValueItem;
			items = combinedItems;
		}
		return items;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.wst.xml.ui.views.contentoutline.XMLContentOutlineConfiguration
	 * #getLabelProvider(org.eclipse.jface.viewers.TreeViewer)
	 */
	public ILabelProvider getLabelProvider(TreeViewer viewer) {
		if (fLabelProvider == null) {
			fLabelProvider = new ContentLabelProvider(super.getLabelProvider(viewer));
		}
		return fLabelProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration
	 * #getPreferenceStore()
	 */
	protected IPreferenceStore getPreferenceStore() {
		return JSPUIPlugin.getDefault().getPreferenceStore();
	}
}
