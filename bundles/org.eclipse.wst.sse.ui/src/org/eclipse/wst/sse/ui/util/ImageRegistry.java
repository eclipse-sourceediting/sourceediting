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
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * An image registry maintains a mapping between symbolic image names and SWT
 * image objects or special image descriptor objects which defer the creation
 * of SWT image objects until they are needed.
 * <p>
 * An image registry owns all of the image objects registered with it, and can
 * automatically dispose of them when the registered SWT Display is disposed.
 * Clients may also request that the images be disposed of manually, but <em>utmost care must be taken when
 * doing so</em>.
 * </p>
 * <p>
 * 
 * <p>
 * Unlike the FontRegistry, it is an error to replace images. As a result there
 * are no events that fire when values are changed in the registry. Images
 * which are replaced may not be disposed of correctly.
 * </p>
 * 
 * @deprecated - use base ImageRegistry (JFaceResources.getImageRegistry() instead TODO remove in C5 or earlier  
 */
public class ImageRegistry {
	/**
	 * Table of known images keyed by symbolic image name (key type: <code>String</code>,
	 * value type: <code>org.eclipse.swt.graphics.Image</code> or <code>ImageDescriptor</code>).
	 */
	protected Map table = new HashMap(10);

	/**
	 * Creates an empty image registry.
	 * <p>
	 * There must be an SWT Display created in the current thread before
	 * calling this method.
	 * </p>
	 */
	public ImageRegistry() {
		Display display = getDisplay();
		if (display != null) {
			hookDisplayDispose(display);
		}
	}

	/**
	 * Creates an empty image registry.
	 */
	public ImageRegistry(Display display) {
		Assert.isNotNull(display);
		hookDisplayDispose(display);
	}

	/**
	 * Shuts down this image registry and disposes of all registered images.
	 */
	public void dispose() {
		if (table != null) {
			for (Iterator e = table.values().iterator(); e.hasNext();) {
				Object next = e.next();
				if (next instanceof Image) {
					((Image) next).dispose();
				}
			}
			table = new HashMap(10);
		}
	}

	/**
	 * Returns the image associated with the given key in this registry, or
	 * <code>null</code> if none.
	 * 
	 * @param key
	 *            the key
	 * @return the image, or <code>null</code> if none
	 */
	public Image get(String key) {
		Object entry = table.get(key);
		if (entry == null) {
			return null;
		}
		if (entry instanceof Image) {
			return (Image) entry;
		}
		Image image = ((ImageDescriptor) entry).createImage();
		table.put(key, image);
		return image;
	}

	/**
	 * Hook a dispose listener on the SWT display.
	 * 
	 * @param display
	 *            the Display
	 */
	private void hookDisplayDispose(Display display) {
		display.disposeExec(new Runnable() {
			public void run() {
				dispose();
			}
		});
	}

	/**
	 * Adds (or replaces) an image descriptor to this registry. The first time
	 * this new entry is retrieved, the image descriptor's image will be
	 * computed (via</code> ImageDescriptor.createImage</code>) and
	 * remembered. This method replaces an existing image descriptor associated
	 * with the given key, but fails if there is a real image associated with
	 * it.
	 * 
	 * @param key
	 *            the key
	 * @param descriptor
	 *            the ImageDescriptor
	 * @exception IllegalArgumentException
	 *                if the key already exists
	 */
	public void put(String key, ImageDescriptor descriptor) {
		Object entry = table.get(key);
		if (entry == null || entry instanceof ImageDescriptor) {
			// replace with the new descriptor
			table.put(key, descriptor);
			return;
		}
		throw new IllegalArgumentException("ImageRegistry key already in use: " + key);//$NON-NLS-1$
	}

	/**
	 * Adds an image to this registry. This method fails if there is already an
	 * image with the given key.
	 * <p>
	 * Note that an image registry owns all of the image objects registered
	 * with it, and automatically disposes of them the SWT Display is disposed.
	 * Because of this, clients must not register an image object that is
	 * managed by another object.
	 * </p>
	 * 
	 * @param key
	 *            the key
	 * @param image
	 *            the image
	 * @exception IllegalArgumentException
	 *                if the key already exists
	 */
	public void put(String key, Image image) {
		Object entry = table.get(key);
		if (entry == null || entry instanceof ImageDescriptor) {
			// replace with the new descriptor
			table.put(key, image);
			return;
		}
		throw new IllegalArgumentException("ImageRegistry key already in use: " + key);//$NON-NLS-1$
	}

	private Display getDisplay() {
		return PlatformUI.getWorkbench().getDisplay();
	}
}
