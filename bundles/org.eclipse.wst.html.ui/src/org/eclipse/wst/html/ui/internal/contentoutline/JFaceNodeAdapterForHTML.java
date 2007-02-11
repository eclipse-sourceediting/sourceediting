/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.ui.internal.contentoutline;



import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.html.ui.internal.editor.HTMLEditorPluginImageHelper;
import org.eclipse.wst.html.ui.internal.editor.HTMLEditorPluginImages;
import org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeAdapter;
import org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeAdapterFactory;
import org.w3c.dom.Node;

/**
 * Adapts a DOM node to a JFace viewer.
 */
public class JFaceNodeAdapterForHTML extends JFaceNodeAdapter {

	private Image createHTMLImage(String imageResourceName) {
		return HTMLEditorPluginImageHelper.getInstance().getImage(imageResourceName);
	}

	/**
	 * Constructor for JFaceNodeAdapterForHTML.
	 * 
	 * @param adapterFactory
	 */
	public JFaceNodeAdapterForHTML(JFaceNodeAdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	protected Image createImage(Object object) {
		Image image = null;

		Node node = (Node) object;
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
}