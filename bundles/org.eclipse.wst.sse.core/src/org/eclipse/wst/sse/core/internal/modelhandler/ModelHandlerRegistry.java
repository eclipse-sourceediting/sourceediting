/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.internal.modelhandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.wst.common.encoding.CodedIO;
import org.eclipse.wst.sse.core.internal.Logger;
import org.eclipse.wst.sse.core.modelhandler.IModelHandler;
import org.eclipse.wst.sse.core.util.Utilities;


public class ModelHandlerRegistry {
	private static ModelHandlerRegistry instance = null;

	private static IContentTypeManager getContentTypeRegistry() {
		IContentTypeManager registry = Platform.getContentTypeManager();
		return registry;
	}

	/**
	 */
	public synchronized static ModelHandlerRegistry getInstance() {
		if (instance == null) {
			instance = new ModelHandlerRegistry();
		}
		return instance;
	}

	private IModelHandler defaultHandler = null;
	private ModelHandlerRegistryReader reader = new ModelHandlerRegistryReader();

	/*
	 * @see ContentTypeRegistry#getTypeFor(String)
	 */
	/**
	 * Constructor for ContentTypeRegistryImpl.
	 */
	protected ModelHandlerRegistry() {
		super();
		reader = new ModelHandlerRegistryReader().readRegistry();
	}

	/**
	 * Finds the default model handler. Note: we still go through the registry
	 * to be sure to get the existing instance, but then we do remember it, so
	 * subsequent requests will be faster. The first time through, we do check
	 * the whole list, to be sure there is only one.
	 *  
	 */
	final public IModelHandler getDefault() {
		if (defaultHandler == null) {
			IConfigurationElement[] elements = reader.elements;
			for (int i = 0; i < elements.length; i++) {
				boolean ofInterest = reader.isElementDefault(elements[i]);
				if (ofInterest) {
					// if, here within the search loop we've already found
					// one defaultHandler, then something is wrong!
					if (defaultHandler == null) {
						defaultHandler = reader.getInstance(elements[i]);
					} else {
						String errorString = "Program or configuration error. More than one default content handler found"; //$NON-NLS-1$
						Logger.log(Logger.ERROR, errorString);
						throw new IllegalStateException(errorString);
					}
				}
			}
		}
		if (defaultHandler == null) {
			String errorString = "Program or configuration error. No default content type handler found."; //$NON-NLS-1$
			Logger.log(Logger.ERROR, errorString);
			throw new IllegalStateException(errorString);
		}
		return defaultHandler;
	}

	/**
	 * Finds the contentTypeDescription based on outcome of the
	 * ContentTypeDescription's canHandle(IResource) method.
	 * 
	 * @throws CoreException
	 */
	public IModelHandler getHandlerFor(IFile iFile) throws CoreException {
		IModelHandler modelHandler = null;
		IContentDescription contentDescription = null;
		IContentType contentType = null;
		boolean exists = iFile.exists();
		if (exists) {
			// try the optimized method first as the description may be cached
			contentDescription = iFile.getContentDescription();
			if (contentDescription != null) {
				// use the provided description
				contentType = contentDescription.getContentType();
			} else {
				// use the more thorough discovery method to get a description
				InputStream contents = null;
				try {
					contents = iFile.getContents(true);
					contentDescription = Platform.getContentTypeManager().getDescriptionFor(contents, iFile.getName(), IContentDescription.ALL);
					if (contentDescription != null) {
						contentType = contentDescription.getContentType();
					}
				} catch (IOException e) {
					// nothing further can be done, but will log for debugging
					Logger.logException(e);
				} finally {
					if (contents != null) {
						try {
							contents.close();
						} catch (IOException e1) {
							// nothing can be done
						}
					}
				}
			}
		}

		// if we couldn't get the content type from a description, try basing
		// it on the filename
		if (contentType == null) {
			contentType = Platform.getContentTypeManager().findContentTypeFor(iFile.getName());
		}

		if (contentType != null) {
			modelHandler = getHandlerForContentType(contentType);
		} else {
			// temp hard coding for null content type
			modelHandler = getHandlerForID("org.eclipse.wst.sse.core.handler.xml"); //$NON-NLS-1$
		}

		return modelHandler;
	}

	/**
	 * @see ContentTypeRegistry#add(ContentTypeDescription)
	 */
	//	void add(IModelHandler contentTypeDescription) {
	//		arrayList.add(contentTypeDescription);
	//	}
	/*
	 * @see ContentTypeRegistry#getModelFor(String)
	 */
	/**
	 * @throws IOException
	 * @see ContentTypeRegistry#getTypeFor(String, InputStream)
	 */
	public IModelHandler getHandlerFor(String filename, InputStream inputStream) throws IOException {
		InputStream iStream = Utilities.getMarkSupportedStream(inputStream);
		IModelHandler modelHandler = null;
		IContentType contentType = null;
		if (inputStream != null) {
			// XXX: NPE check is temporary for pre-M9 base
			try {
				iStream.mark(CodedIO.MAX_MARK_SIZE);
				contentType = getContentTypeRegistry().findContentTypeFor(Utilities.getLimitedStream(iStream), filename);
			} catch (NullPointerException e) {
				System.out.println(contentType);
			}
			// XXX: Remove when we build with the fix for Eclipse bug #63625
			catch (FileNotFoundException fnfe) {
				Logger.logException(fnfe);
			} finally {
				if (iStream != null && iStream.markSupported()) {
					iStream.reset();
				}
			}

		}
		if (contentType == null) {
			contentType = getContentTypeRegistry().findContentTypeFor(filename);
		}
		modelHandler = getHandlerForContentType(contentType);
		return modelHandler;
	}


	/**
	 * Gets registered modelHandlers for given content type. TODO: eventually
	 * need to look at contentType's parent types to see if more general type
	 * can handle (e.g. if we got xsl which was a subtype of xml).
	 * 
	 * @param contentType
	 * @return
	 */
	private IModelHandler getHandlerForContentType(IContentType contentType) {

		IModelHandler found = null;
		// temp hard coding for null content type
		if (contentType == null) {
			found = getHandlerForID("org.eclipse.wst.sse.core.handler.xml"); //$NON-NLS-1$
		} else {
			//String associatedContentTypeId = contentType.getId();
			IConfigurationElement[] elements = reader.elements;
			if (elements != null) {
				for (int i = 0; i < elements.length; i++) {
					String currentId = reader.getAssociatedContentTypeId(elements[i]);
					IContentType modelContentType = Platform.getContentTypeManager().getContentType(currentId);
					if (contentType.isKindOf(modelContentType)) {
						IModelHandler item = reader.getInstance(elements[i]);
						found = item;
					}
				}
			} else {
				Logger.log(Logger.WARNING_DEBUG, "There were no Model Handler found in registry"); //$NON-NLS-1$
			}
		}
		return found;
	}

	/**
	 * Finds the ModelHandler based on literal content type id. This should
	 * not normally be needed, in is in cases when a model needs to be
	 * created, and there is no resource. Its basically a "first found first
	 * returned". Note the order is fairly unpredictable, so non-unique ids
	 * would cause problems, and are not checked.
	 */
	public IModelHandler getHandlerForContentTypeId(String contentTypeId) {
		IContentType contentType = Platform.getContentTypeManager().getContentType(contentTypeId);
		return getHandlerForContentType(contentType);
		//		IModelHandler found = null;
		//		IConfigurationElement[] elements = reader.elements;
		//		if (elements != null) {
		//			for (int i = 0; i < elements.length; i++) {
		//				String currentId = reader.getAssociatedContentTypeId(elements[i]);
		//				if (contentTypeId.equals(currentId)) {
		//					IModelHandler item = reader.getInstance(elements[i]);
		//					found = item;
		//				}
		//			}
		//		}
		//		else {
		//			Logger.log(Logger.WARNING_DEBUG, "There were no Model Handler found
		// in registry");
		//		}
		//		return found;
	}

	/**
	 * Finds the ModelHandler based on literal id. Its basically a "first
	 * found first returned". Note the order is fairly unpredictable, so
	 * non-unique ids would cause problems, and are not checked.
	 */
	private IModelHandler getHandlerForID(String modelId) {
		IModelHandler found = null;
		IConfigurationElement[] elements = reader.elements;
		if (elements != null) {
			for (int i = 0; i < elements.length; i++) {
				String currentId = reader.getId(elements[i]);
				if (modelId.equals(currentId)) {
					IModelHandler item = reader.getInstance(elements[i]);
					found = item;
				}
			}
		} else {
			Logger.log(Logger.WARNING_DEBUG, "There were no Model Handler found in registry"); //$NON-NLS-1$
		}
		return found;
	}
}
