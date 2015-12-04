/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.xml.ui.internal.editor.XMLEditorPluginImageHelper
 *                                           modified in order to process JSON Objects.      
 *******************************************************************************/
package org.eclipse.wst.json.ui.internal.editor;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.json.schema.JSONSchemaType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.json.core.document.IJSONNode;
import org.eclipse.wst.json.ui.internal.JSONUIPlugin;
import org.eclipse.wst.json.ui.internal.Logger;

/**
 * Helper class to handle images provided by this plug-in.
 * 
 * NOTE: For internal use only. For images used externally, please use the
 * Shared***ImageHelper class instead.
 * 
 */
public class JSONEditorPluginImageHelper {

	private static JSONEditorPluginImageHelper instance = null;
	private static final String IMAGE_DIR = "wtp-json-images"; //$NON-NLS-1$

	private final Map<ImageDescriptor, URL> fURLMap;
	private final File fTempDir;
	private int fImageCount;

	/**
	 * Gets the instance.
	 * 
	 * @return Returns a JSONEditorPluginImageHelper
	 */
	public synchronized static JSONEditorPluginImageHelper getInstance() {
		if (instance == null) {
			instance = new JSONEditorPluginImageHelper();
		}
		return instance;
	}

	// save a descriptor for each image
	private Map<String, ImageDescriptor> fImageDescRegistry = null;
	private final String PLUGINID = JSONUIPlugin.PLUGIN_ID;

	public JSONEditorPluginImageHelper() {
		fURLMap = new HashMap<ImageDescriptor, URL>();
		fTempDir = getTempDir();
		fImageCount = 0;
	}

	/**
	 * Creates an image from the given resource and adds the image to the image
	 * registry.
	 * 
	 * @param resource
	 * @return Image
	 */
	private Image createImage(String resource) {
		ImageDescriptor desc = getImageDescriptor(resource);
		Image image = null;

		if (desc != null) {
			image = desc.createImage();
			// dont add the missing image descriptor image to the image
			// registry
			if (!desc.equals(ImageDescriptor.getMissingImageDescriptor())) {
				getImageRegistry().put(resource, image);
			}
		}
		return image;
	}

	/**
	 * Creates an image descriptor from the given imageFilePath and adds the
	 * image descriptor to the image descriptor registry. If an image descriptor
	 * could not be created, the default "missing" image descriptor is returned
	 * but not added to the image descriptor registry.
	 * 
	 * @param imageFilePath
	 * @return ImageDescriptor image descriptor for imageFilePath or default
	 *         "missing" image descriptor if resource could not be found
	 */
	private ImageDescriptor createImageDescriptor(String imageFilePath) {
		ImageDescriptor imageDescriptor = AbstractUIPlugin
				.imageDescriptorFromPlugin(PLUGINID, imageFilePath);
		if (imageDescriptor != null) {
			getImageDescriptorRegistry().put(imageFilePath, imageDescriptor);
		} else {
			imageDescriptor = ImageDescriptor.getMissingImageDescriptor();
		}

		return imageDescriptor;
	}

	public Image getImage(short nodeType) {
		String imageName = getImageName(nodeType);
		return imageName != null ? getImage(imageName) : null;
	}

	public ImageDescriptor getImageDescriptor(short nodeType) {
		String imageName = getImageName(nodeType);
		return imageName != null ? getImageDescriptor(imageName) : null;
	}

	private String getImageName(short nodeType) {
		switch (nodeType) {
		case IJSONNode.OBJECT_NODE:
			return JSONEditorPluginImages.IMG_OBJ_OBJECT;
		case IJSONNode.ARRAY_NODE:
			return JSONEditorPluginImages.IMG_OBJ_ARRAY;
		case IJSONNode.VALUE_BOOLEAN_NODE:
			return JSONEditorPluginImages.IMG_OBJ_VALUE_BOOLEAN;
		case IJSONNode.VALUE_NULL_NODE:
			return JSONEditorPluginImages.IMG_OBJ_VALUE_NULL;
		case IJSONNode.VALUE_NUMBER_NODE:
			return JSONEditorPluginImages.IMG_OBJ_VALUE_NUMBER;
		case IJSONNode.VALUE_STRING_NODE:
			return JSONEditorPluginImages.IMG_OBJ_VALUE_STRING;
		default:
			return null;
		}
	}

	public ImageDescriptor getImageDescriptor(JSONSchemaType type) {
		String imageName = getImageName(type);
		return imageName != null ? getImageDescriptor(imageName) : null;
	}

	public Image getImage(JSONSchemaType type) {
		String imageName = getImageName(type);
		return imageName != null ? getImage(imageName) : null;
	}

	private String getImageName(JSONSchemaType type) {
		if (type == null) {
			return null;
		}
		switch (type) {
		case Object:
			return JSONEditorPluginImages.IMG_OBJ_OBJECT;
		case Array:
			return JSONEditorPluginImages.IMG_OBJ_ARRAY;
		case Boolean:
			return JSONEditorPluginImages.IMG_OBJ_VALUE_BOOLEAN;
		case Null:
			return JSONEditorPluginImages.IMG_OBJ_VALUE_NULL;
		case Number:
			return JSONEditorPluginImages.IMG_OBJ_VALUE_NUMBER;
		case String:
			return JSONEditorPluginImages.IMG_OBJ_VALUE_STRING;
		default:
			return null;
		}
	}

	/**
	 * Retrieves the image associated with resource from the image registry. If
	 * the image cannot be retrieved, attempt to find and load the image at the
	 * location specified in resource.
	 * 
	 * @param resource
	 *            the image to retrieve
	 * @return Image the image associated with resource or null if one could not
	 *         be found
	 */
	public Image getImage(String resource) {
		Image image = getImageRegistry().get(resource);
		if (image == null) {
			// create an image
			image = createImage(resource);
		}
		return image;
	}

	/**
	 * Retrieves the image descriptor associated with resource from the image
	 * descriptor registry. If the image descriptor cannot be retrieved, attempt
	 * to find and load the image descriptor at the location specified in
	 * resource.
	 * 
	 * @param resource
	 *            the image descriptor to retrieve
	 * @return ImageDescriptor the image descriptor assocated with resource or
	 *         the default "missing" image descriptor if one could not be found
	 */
	public ImageDescriptor getImageDescriptor(String resource) {
		ImageDescriptor imageDescriptor = getImageDescriptorRegistry().get(
				resource);
		if (imageDescriptor == null) {
			// create a descriptor
			imageDescriptor = createImageDescriptor(resource);
		}
		return imageDescriptor;
	}

	/**
	 * Returns the image descriptor registry for this plugin.
	 * 
	 * @return HashMap - image descriptor registry for this plugin
	 */
	private Map<String, ImageDescriptor> getImageDescriptorRegistry() {
		if (fImageDescRegistry == null) {
			fImageDescRegistry = new HashMap<String, ImageDescriptor>();
		}
		return fImageDescRegistry;
	}

	/**
	 * Returns the image registry for this plugin.
	 * 
	 * @return ImageRegistry - image registry for this plugin
	 */
	private ImageRegistry getImageRegistry() {
		return JSONUIPlugin.getDefault().getImageRegistry();
	}

	public URL getImageURL(ImageDescriptor descriptor) {
		if (fTempDir == null)
			return null;

		URL url = fURLMap.get(descriptor);
		if (url != null)
			return url;

		File imageFile = getNewFile();
		ImageData imageData = descriptor.getImageData();
		if (imageData == null) {
			return null;
		}

		ImageLoader loader = new ImageLoader();
		loader.data = new ImageData[] { imageData };
		loader.save(imageFile.getAbsolutePath(), SWT.IMAGE_PNG);

		try {
			url = imageFile.toURI().toURL();
			fURLMap.put(descriptor, url);
			return url;
		} catch (MalformedURLException e) {
			Logger.logException("Failed to create image directory ", e); //$NON-NLS-1$
		}
		return null;
	}

	private File getNewFile() {
		File file;
		do {
			file = new File(fTempDir, String.valueOf(getImageCount()) + ".png"); //$NON-NLS-1$
		} while (file.exists());
		return file;
	}

	private synchronized int getImageCount() {
		return fImageCount++;
	}

	private File getTempDir() {
		try {
			File imageDir = JSONUIPlugin.getDefault().getStateLocation()
					.append(IMAGE_DIR).toFile();
			if (imageDir.exists()) {
				// has not been deleted on previous shutdown
				delete(imageDir);
			}
			if (!imageDir.exists()) {
				imageDir.mkdir();
			}
			if (!imageDir.isDirectory()) {
				Logger.log(
						Logger.ERROR,
						"Failed to create image directory " + imageDir.toString()); //$NON-NLS-1$
				return null;
			}
			return imageDir;
		} catch (IllegalStateException e) {
			// no state location
			return null;
		}
	}

	private void delete(File file) {
		if (file.isDirectory()) {
			File[] listFiles = file.listFiles();
			for (int i = 0; i < listFiles.length; i++) {
				delete(listFiles[i]);
			}
		}
		file.delete();
	}

}
