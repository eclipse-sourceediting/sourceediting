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
package org.eclipse.wst.xml.ui.contentassist;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.xml.ui.XMLEditorPlugin;


/**
 * @deprecated use internal XMLEditorPluginImageHelper or external
 *             SharedXMLEditorPluginImageHelper instead
 */
public class SourceEditorImageHelper {

	public SourceEditorImageHelper() {
		super();
	}

	public Image createImage(String resource) {
		ImageDescriptor desc = AbstractUIPlugin.imageDescriptorFromPlugin(XMLEditorPlugin.ID, resource);
		Image image = desc.createImage();
		JFaceResources.getImageRegistry().put(resource, image);
		return image;
	}

	public Image getImage(String resource) {
		Image image = JFaceResources.getImageRegistry().get(resource);
		if (image == null) {
			image = createImage(resource);
		}
		return image;
	}


}
