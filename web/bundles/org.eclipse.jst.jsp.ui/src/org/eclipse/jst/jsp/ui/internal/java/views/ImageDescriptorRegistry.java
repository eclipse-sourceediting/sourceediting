/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.java.views;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

public class ImageDescriptorRegistry {

	private static final Map registry = new HashMap(0);

	public static synchronized Image getImage(ImageDescriptor descriptor) {
		if (descriptor == null) {
			descriptor = ImageDescriptor.getMissingImageDescriptor();
		}

		Image image = (Image) registry.get(descriptor);
		if (image != null) {
			return image;
		}
		image = descriptor.createImage();
		if (image != null) {
			registry.put(descriptor, image);
		}
		return image;
	}

	public static void dispose() {
		for (Iterator it = registry.values().iterator(); it.hasNext();) {
			((Image) it.next()).dispose();
		}
		registry.clear();
	}

}
