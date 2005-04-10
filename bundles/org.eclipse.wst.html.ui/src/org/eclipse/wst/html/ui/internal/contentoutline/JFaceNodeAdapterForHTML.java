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
package org.eclipse.wst.html.ui.internal.contentoutline;



import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.html.core.HTML40Namespace;
import org.eclipse.wst.html.ui.internal.editor.HTMLEditorPluginImageHelper;
import org.eclipse.wst.html.ui.internal.editor.HTMLEditorPluginImages;
import org.eclipse.wst.sse.core.INodeAdapterFactory;
import org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeAdapter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Adapts a DOM node to a JFace viewer.
 */
public class JFaceNodeAdapterForHTML extends JFaceNodeAdapter {

	private Image createHTMLImage(String imageResourceName) {
		ImageDescriptor imageDescriptor = HTMLEditorPluginImageHelper.getInstance().getImageDescriptor(imageResourceName);
		if (imageDescriptor != null)
			return imageDescriptor.createImage();
		return null;
	}
	
	/**
	 * Constructor for JFaceNodeAdapterForHTML.
	 * @param adapterFactory
	 */
	public JFaceNodeAdapterForHTML(INodeAdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	protected Image createImage(Node node) {
		Image image = null;

		if (node.getNodeType() == Node.ELEMENT_NODE) {
			if (node.getNodeName().equalsIgnoreCase("table")) //$NON-NLS-1$
				image = createHTMLImage(HTMLEditorPluginImages.IMG_OBJ_TABLE);
			else if (node.getNodeName().equalsIgnoreCase("a")) //$NON-NLS-1$
				image = createHTMLImage(HTMLEditorPluginImages.IMG_OBJ_TAG_ANCHOR);
			else if (node.getNodeName().equalsIgnoreCase("body")) //$NON-NLS-1$
				image = createHTMLImage(HTMLEditorPluginImages.IMG_OBJ_TAG_BODY);
			else if (node.getNodeName().equalsIgnoreCase("button")) //$NON-NLS-1$
				image = createHTMLImage(HTMLEditorPluginImages.IMG_OBJ_TAG_BUTTON);
			else if (node.getNodeName().equalsIgnoreCase("font")) //$NON-NLS-1$
				image = createHTMLImage(HTMLEditorPluginImages.IMG_OBJ_TAG_FONT);
			else if (node.getNodeName().equalsIgnoreCase("form")) //$NON-NLS-1$
				image = createHTMLImage(HTMLEditorPluginImages.IMG_OBJ_TAG_FORM);
			else if (node.getNodeName().equalsIgnoreCase("html")) //$NON-NLS-1$
				image = createHTMLImage(HTMLEditorPluginImages.IMG_OBJ_TAG_HTML);
			else if (node.getNodeName().equalsIgnoreCase("img")) //$NON-NLS-1$
				image = createHTMLImage(HTMLEditorPluginImages.IMG_OBJ_TAG_IMAGE);
			else if (node.getNodeName().equalsIgnoreCase("map")) //$NON-NLS-1$
				image = createHTMLImage(HTMLEditorPluginImages.IMG_OBJ_TAG_IMAGE_MAP);
			else if (node.getNodeName().equalsIgnoreCase("title")) //$NON-NLS-1$
				image = createHTMLImage(HTMLEditorPluginImages.IMG_OBJ_TAG_TITLE);
			else
				image = createHTMLImage(HTMLEditorPluginImages.IMG_OBJ_TAG);
		}
		if (image == null) {
			image = super.createImage(node);
		}
		return image;
	}

	/*
	 * @see IJFaceNodeAdapter#getLabelText(Node)
	 */
	public String getLabelText(Node node) {
		// TODO (pa) eventually showing ID, NAME, etc..might be a preference 
		// - like in the package explorer for java.. (along w/ being able to customize delimiters)
		// - possibly for all (or filtered list of) elements
		String text = null;
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			if (node.getNodeName().equalsIgnoreCase("table")) { //$NON-NLS-1$
				// won't update properly as-is
				// it will only update if you change the attr name, 
				// delete a ", or add a new element to the tree
				text = node.getNodeName();

				// get attr values
				String tableID = null;
				tableID = ((Element) node).getAttribute(HTML40Namespace.ATTR_NAME_ID);
				String tableName = null;
				tableName = ((Element) node).getAttribute(HTML40Namespace.ATTR_NAME_NAME);

				// if there's ID or NAME, add a ">"
				if ((tableID != null && tableID.length() > 0) || (tableName != null && tableName.length() > 0)) {
					text += " > "; //$NON-NLS-1$
				}

				if (tableID != null && tableID.length() > 0)
					text += " ID:[" + tableID + "]"; //$NON-NLS-1$ //$NON-NLS-2$
				if (tableName != null && tableName.length() > 0)
					text += " NAME:[" + tableName + "]"; //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		if (text == null)
			text = super.getLabelText(node);
		return text;
	}
}