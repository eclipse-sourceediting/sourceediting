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
package org.eclipse.wst.html.ui.internal;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.html.ui.internal.editor.HTMLEditorPluginImageHelper;
import org.eclipse.wst.html.ui.internal.editor.HTMLEditorPluginImages;


public class SharedHTMLEditorPluginImageHelper {
	public static final String IMG_OBJ_TABLE = HTMLEditorPluginImages.IMG_OBJ_TABLE;
	public static final String IMG_OBJ_TAG_ANCHOR = HTMLEditorPluginImages.IMG_OBJ_TAG_ANCHOR;
	public static final String IMG_OBJ_TAG_BODY = HTMLEditorPluginImages.IMG_OBJ_TAG_BODY;
	public static final String IMG_OBJ_TAG_BUTTON = HTMLEditorPluginImages.IMG_OBJ_TAG_BUTTON;
	public static final String IMG_OBJ_TAG_FONT = HTMLEditorPluginImages.IMG_OBJ_TAG_FONT;
	public static final String IMG_OBJ_TAG_FORM = HTMLEditorPluginImages.IMG_OBJ_TAG_FORM;
	public static final String IMG_OBJ_TAG_HTML = HTMLEditorPluginImages.IMG_OBJ_TAG_HTML;
	public static final String IMG_OBJ_TAG_IMAGE_MAP = HTMLEditorPluginImages.IMG_OBJ_TAG_IMAGE_MAP;
	public static final String IMG_OBJ_TAG_IMAGE = HTMLEditorPluginImages.IMG_OBJ_TAG_IMAGE;
	public static final String IMG_OBJ_TAG_JSP = HTMLEditorPluginImages.IMG_OBJ_TAG_JSP;
	public static final String IMG_OBJ_TAG_TITLE = HTMLEditorPluginImages.IMG_OBJ_TAG_TITLE;
	public static final String IMG_OBJ_TAG = HTMLEditorPluginImages.IMG_OBJ_TAG;
	
	/**
	 * Retrieves the specified image from the html source editor plugin's image registry.
	 * Note: The returned <code>Image</code> is managed by the workbench; clients
	 * must <b>not</b> dispose of the returned image.
	 *
	 * @param symbolicName the symbolic name of the image; there are constants
	 * declared in this class for build-in images that come with the html source editor
	 * @return the image, or <code>null</code> if not found
	 */
	public static Image getImage(String symbolicName) {
		return HTMLEditorPluginImageHelper.getInstance().getImage(symbolicName);
	}
	
	/**
	 * Retrieves the image descriptor for specified image from the html source editor plugin's
	 * image registry. Unlike <code>Image</code>s, image descriptors themselves do
	 * not need to be disposed.
	 *
	 * @param symbolicName the symbolic name of the image; there are constants
	 * declared in this interface for build-in images that come with the html source editor
	 * @return the image descriptor, or <code>null</code> if not found
	 */
	public static ImageDescriptor getImageDescriptor(String symbolicName) {
		return HTMLEditorPluginImageHelper.getInstance().getImageDescriptor(symbolicName);
	}
}
