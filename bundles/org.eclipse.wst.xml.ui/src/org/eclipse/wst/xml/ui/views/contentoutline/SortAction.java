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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.sse.ui.internal.contentoutline.PropertyChangeUpdateAction;
import org.eclipse.wst.xml.ui.internal.XMLUIMessages;
import org.eclipse.wst.xml.ui.internal.editor.XMLEditorPluginImageHelper;
import org.eclipse.wst.xml.ui.internal.editor.XMLEditorPluginImages;
import org.eclipse.wst.xml.ui.internal.preferences.XMLUIPreferenceNames;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Toggling sort action for XML outlines
 */
class SortAction extends PropertyChangeUpdateAction {
	static class ViewerNodeComparator extends ViewerComparator {
		private int[] categories = new int[12];

		public ViewerNodeComparator(IPreferenceStore store) {
			super();
			initCategoryOrder(store);
		}

		/**
		 * @param store
		 */
		private void initCategoryOrder(IPreferenceStore store) {
			String[] order = new String[12];
			order[Node.ELEMENT_NODE - 1] = StringUtils.unpack(store.getString(XMLUIPreferenceNames.OUTLINE_BEHAVIOR.ELEMENT_NODE))[0];
			order[Node.ATTRIBUTE_NODE - 1] = StringUtils.unpack(store.getString(XMLUIPreferenceNames.OUTLINE_BEHAVIOR.ATTRIBUTE_NODE))[0];
			order[Node.TEXT_NODE - 1] = StringUtils.unpack(store.getString(XMLUIPreferenceNames.OUTLINE_BEHAVIOR.TEXT_NODE))[0];
			order[Node.CDATA_SECTION_NODE - 1] = StringUtils.unpack(store.getString(XMLUIPreferenceNames.OUTLINE_BEHAVIOR.CDATA_SECTION_NODE))[0];
			order[Node.ENTITY_REFERENCE_NODE - 1] = StringUtils.unpack(store.getString(XMLUIPreferenceNames.OUTLINE_BEHAVIOR.ENTITY_REFERENCE_NODE))[0];
			order[Node.ENTITY_NODE - 1] = StringUtils.unpack(store.getString(XMLUIPreferenceNames.OUTLINE_BEHAVIOR.ENTITY_NODE))[0];
			order[Node.PROCESSING_INSTRUCTION_NODE - 1] = StringUtils.unpack(store.getString(XMLUIPreferenceNames.OUTLINE_BEHAVIOR.PROCESSING_INSTRUCTION_NODE))[0];
			order[Node.COMMENT_NODE - 1] = StringUtils.unpack(store.getString(XMLUIPreferenceNames.OUTLINE_BEHAVIOR.COMMENT_NODE))[0];
			order[Node.DOCUMENT_NODE - 1] = StringUtils.unpack(store.getString(XMLUIPreferenceNames.OUTLINE_BEHAVIOR.DOCUMENT_NODE))[0];
			order[Node.DOCUMENT_TYPE_NODE - 1] = StringUtils.unpack(store.getString(XMLUIPreferenceNames.OUTLINE_BEHAVIOR.DOCUMENT_TYPE_NODE))[0];
			order[Node.DOCUMENT_FRAGMENT_NODE - 1] = StringUtils.unpack(store.getString(XMLUIPreferenceNames.OUTLINE_BEHAVIOR.DOCUMENT_FRAGMENT_NODE))[0];
			order[Node.NOTATION_NODE - 1] = StringUtils.unpack(store.getString(XMLUIPreferenceNames.OUTLINE_BEHAVIOR.NOTATION_NODE))[0];
			for (int i = 0; i < order.length; i++) {
				try {
					categories[i] = Integer.parseInt(order[i]);
				}
				catch (NumberFormatException e) {
				}
			}
		}

		public int category(Object element) {
			if (element instanceof Node) {
				return (categories[((Node) element).getNodeType() - 1]);
			}

			return super.category(element);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ViewerComparator#isSorterProperty(java
		 * .lang.Object, java.lang.String)
		 */
		public boolean isSorterProperty(Object element, String property) {
			if (element instanceof Element) {
				return ((Element) element).hasAttribute(property);
			}
			return super.isSorterProperty(element, property);
		}
	}

	private TreeViewer treeViewer;

	public SortAction(TreeViewer viewer, IPreferenceStore store, String preferenceKey) {
		super(XMLUIMessages._UI_BUTTON_SORT, store, preferenceKey, false); //$NON-NLS-1$
		setImageDescriptor(XMLEditorPluginImageHelper.getInstance().getImageDescriptor(XMLEditorPluginImages.IMG_OBJ_SORT));
		setToolTipText(getText());
		treeViewer = viewer;
		if (isChecked()) {
			treeViewer.setComparator(createComparator());
		}
	}

	private ViewerNodeComparator createComparator() {
		return new ViewerNodeComparator(getPreferenceStore());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.IUpdate#update()
	 */
	public void update() {
		super.update();
		treeViewer.getControl().setRedraw(false);
		Object[] expandedElements = treeViewer.getExpandedElements();
		if (isChecked()) {
			treeViewer.setComparator(createComparator());
		}
		else {
			treeViewer.setComparator(null);
		}
		treeViewer.setInput(treeViewer.getInput());
		treeViewer.setExpandedElements(expandedElements);
		treeViewer.getControl().setRedraw(true);
	}
}
