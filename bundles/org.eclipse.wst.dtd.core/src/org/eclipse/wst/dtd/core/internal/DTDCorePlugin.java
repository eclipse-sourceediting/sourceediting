/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
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
package org.eclipse.wst.dtd.core.internal;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class DTDCorePlugin extends AbstractUIPlugin {
	private static DTDCorePlugin instance;

	public static Image getDTDImage(String iconName) {
		return getInstance().getImage(iconName);
	}

	public static ImageDescriptor getDTDImageDescriptor(String iconName) {
		String thisID = getInstance().getBundle().getSymbolicName();
		return AbstractUIPlugin.imageDescriptorFromPlugin(thisID, iconName);
	}

	public synchronized static DTDCorePlugin getInstance() {
		return instance;
	}

	public static DTDCorePlugin getPlugin() {
		return instance;
	}

	public DTDCorePlugin() {
		super();
		instance = this;
	}

	public Image getImage(String iconName) {
		ImageRegistry imageRegistry = getImageRegistry();
		Image image = imageRegistry.get(iconName);

		if (image == null) {
			String thisID = getInstance().getBundle().getSymbolicName();
			imageRegistry.put(iconName, imageDescriptorFromPlugin(thisID, iconName));
			image = imageRegistry.get(iconName);
		}

		return image;
	}
}
