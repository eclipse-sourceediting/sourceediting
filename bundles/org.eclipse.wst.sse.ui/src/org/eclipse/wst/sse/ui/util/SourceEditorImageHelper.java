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
package org.eclipse.wst.sse.ui.util;



import java.util.HashMap;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.sse.ui.EditorPlugin;


/**
 * @deprecated use EditorPluginImageHelper for internal and 
 * SharedEditorPluginImageHelper for public images
 */
public class SourceEditorImageHelper {
	protected HashMap descRegistry = null; // save a descriptor for each image

	/**
	 * XMLProposalHelper constructor comment.
	 */
	public SourceEditorImageHelper() {
		super();
	}

	public Image createImage(String resource) {
		Object o = getImageDescriptorRegistry().get(resource);
		ImageDescriptor desc = null;
		if (o != null)
			desc = (ImageDescriptor) o;
		else {
			desc = createImageDescriptor(resource);
		}
		
		Image image = desc.createImage();
		getImageRegistry().put(resource, image);
		return image;
	}

	public Image getImage(String resource) {
		Image image = getImageRegistry().get(resource);
		if (image == null) {
			image = createImage(resource);
		}
		return image;
	}

	public ImageDescriptor getImageDescriptor(String resource) {
		ImageDescriptor imageDescriptor = null;
		Object o = getImageDescriptorRegistry().get(resource);
		if (o == null) {
			//create a descriptor
			createImageDescriptor(resource);
		}
		return imageDescriptor;
	}
	
	private ImageDescriptor createImageDescriptor(String resource) {
		ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(EditorPlugin.ID, resource);
		if (imageDescriptor != null) {
			getImageDescriptorRegistry().put(resource, imageDescriptor);
		} else {
			imageDescriptor = ImageDescriptor.getMissingImageDescriptor();
		}
		
		return imageDescriptor;
	}
	
	private ImageRegistry getImageRegistry() {
		return JFaceResources.getImageRegistry();
	}

	protected HashMap getImageDescriptorRegistry() {
		if (descRegistry == null)
			descRegistry = new HashMap();
		return descRegistry;
	}

	public void release() {
		descRegistry = null;
	}
}
