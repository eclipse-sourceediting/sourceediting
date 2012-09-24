/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
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

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.wst.sse.core.internal.Logger;
import org.eclipse.wst.sse.core.internal.encoding.CodedIO;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IModelHandler;
import org.eclipse.wst.sse.core.internal.util.Utilities;


public class ModelHandlerRegistry {
	private static ModelHandlerRegistry instance = null;
	static final String INTERNAL_DEFAULT_EXTENSION = "org.eclipse.wst.xml.core.internal.modelhandler"; //$NON-NLS-1$

	public synchronized static ModelHandlerRegistry getInstance() {
		if (instance == null) {
			instance = new ModelHandlerRegistry();
		}
		return instance;
	}

	private IModelHandler defaultHandler = null;
	private ModelHandlerRegistryReader reader = new ModelHandlerRegistryReader();

	private ModelHandlerRegistry() {
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
					/*
					 * If, here within the search loop we've already found one
					 * defaultHandler, then something is wrong!
					 */
					if (defaultHandler == null) {
						defaultHandler = reader.getInstance(elements[i]);
					}
					else {
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
	 * Finds a ModelHandler based on literal extension id. It's basically a
	 * "first found first returned". No specific order is guaranteed and the
	 * uniqueness of IDs is not considered.
	 * 
	 * @param extensionId
	 * @return the given extension, or null
	 */
	private IModelHandler getHandlerExtension(String extensionId) {
		IModelHandler found = null;
		IConfigurationElement[] elements = reader.elements;
		if (elements != null) {
			for (int i = 0; i < elements.length; i++) {
				String currentId = reader.getId(elements[i]);
				if (extensionId.equals(currentId)) {
					IModelHandler item = reader.getInstance(elements[i]);
					found = item;
				}
			}
		}
		else if (Logger.DEBUG){
			Logger.log(Logger.WARNING, "There were no Model Handler found in registry"); //$NON-NLS-1$
		}
		return found;
	}

	/**
	 * Finds the registered IModelHandler for a given named file's content
	 * type.
	 * 
	 * @param file
	 * @param provideDefault should the default extension be used in the absence of other methods
	 * @return The IModelHandler registered for the content type of the given
	 *         file. If an exact match is not found, the most-specific match
	 *         according to IContentType.isKindOf() will be returned. If none
	 *         are found, either a default or null will be returned.
	 * @throws CoreException
	 */
	public IModelHandler getHandlerFor(IFile file, boolean provideDefault) throws CoreException {
		IModelHandler modelHandler = null;
		IContentDescription contentDescription = null;
		IContentType contentType = null;
		boolean accessible = file.isAccessible();
		if (accessible) {
			/* Try the optimized method first as the description may be cached */
			contentDescription = file.getContentDescription();
			if (contentDescription != null) {
				// use the provided description
				contentType = contentDescription.getContentType();
			}
			else {
				/* use the more thorough discovery method to get a description */
				InputStream contents = null;
				try {
					contents = file.getContents(false);
					contentDescription = Platform.getContentTypeManager().getDescriptionFor(contents, file.getName(), IContentDescription.ALL);
					if (contentDescription != null) {
						contentType = contentDescription.getContentType();
					}
				}
				catch (IOException e) {
					// nothing further can be done, but will log for debugging
					Logger.logException(e);
				}
				finally {
					if (contents != null) {
						try {
							contents.close();
						}
						catch (IOException e1) {
							// nothing can be done
						}
					}
				}
			}
		}

		/*
		 * If we couldn't get the content type from a description, try basing
		 * it on just the filename
		 */
		if (contentType == null) {
			contentType = Platform.getContentTypeManager().findContentTypeFor(file.getName());
		}

		if (contentType != null) {
			modelHandler = getHandlerForContentType(contentType);
		}
		else if (contentType == null && provideDefault) {
			// hard coding for null content type
			modelHandler = getHandlerExtension(INTERNAL_DEFAULT_EXTENSION); //$NON-NLS-1$
		}

		return modelHandler;
	}

	/**
	 * Finds the registered IModelHandler for a given named file's content
	 * type. Will check for a default.
	 * 
	 * @param file
	 * @return The IModelHandler registered for the content type of the given
	 *         file. If an exact match is not found, the most-specific match
	 *         according to IContentType.isKindOf() will be returned. If none
	 *         are found, either a default or null will be returned.
	 * @throws CoreException
	 */
	public IModelHandler getHandlerFor(IFile file) throws CoreException {
		return getHandlerFor(file, true);
	}


	/**
	 * Finds the registered IModelHandler for a given named InputStream.
	 * 
	 * @param inputName
	 * @param inputStream
	 * @return The IModelHandler registered for the content type of the given
	 *         input. If an exact match is not found, the most-specific match
	 *         according to IContentType.isKindOf() will be returned. If none
	 *         are found, either a default or null will be returned.
	 * @throws IOException
	 */
	public IModelHandler getHandlerFor(String inputName, InputStream inputStream) throws IOException {
		InputStream iStream = Utilities.getMarkSupportedStream(inputStream);
		IModelHandler modelHandler = null;
		IContentType contentType = null;
		if (inputStream != null) {
			try {
				iStream.mark(CodedIO.MAX_MARK_SIZE);
				contentType = Platform.getContentTypeManager().findContentTypeFor(Utilities.getLimitedStream(iStream), inputName);
			}
			finally {
				if (iStream != null && iStream.markSupported()) {
					iStream.reset();
				}
			}

		}
		if (contentType == null) {
			contentType = Platform.getContentTypeManager().findContentTypeFor(inputName);
		}
		// if all else failed, try to detect solely on contents; done last for
		// performance reasons
		if (contentType == null) {
			contentType = Platform.getContentTypeManager().findContentTypeFor(Utilities.getLimitedStream(iStream), null);
		}
		modelHandler = getHandlerForContentType(contentType);
		return modelHandler;
	}

	/**
	 * Finds the registered IModelHandler for a given IContentType.
	 * 
	 * @param contentType
	 * @return The IModelHandler registered for the given content type. If an
	 *         exact match is not found, the most-specific match according to
	 *         IContentType.isKindOf() will be returned. If none are found,
	 *         either a default or null will be returned.
	 */
	private IModelHandler getHandlerForContentType(IContentType contentType) {
		IModelHandler handler = null;
		if (contentType != null) {
			IConfigurationElement exactContentTypeElement = null;
			IConfigurationElement kindOfContentTypeElement = null;
			int kindOfContentTypeDepth = 0;
			IConfigurationElement[] elements = reader.elements;
			if (elements != null) {
				for (int i = 0; i < elements.length && exactContentTypeElement == null; i++) {
					String currentId = reader.getAssociatedContentTypeId(elements[i]);
					IContentType associatedContentType = Platform.getContentTypeManager().getContentType(currentId);
					if (contentType.equals(associatedContentType)) {
						exactContentTypeElement = elements[i];
					}
					else if (contentType.isKindOf(associatedContentType)) {
						/*
						 * Update the kindOfElement variable only if this
						 * element's content type is "deeper" (depth test
						 * ensures the first content type is remembered)
						 */
						IContentType testContentType = associatedContentType;
						int testDepth = 0;
						while (testContentType != null) {
							testDepth++;
							testContentType = testContentType.getBaseType();
						}
						if (testDepth > kindOfContentTypeDepth) {
							kindOfContentTypeElement = elements[i];
							kindOfContentTypeDepth = testDepth;
						}
					}
				}
			}
			else if (Logger.DEBUG){
				Logger.log(Logger.WARNING, "There were no Model Handler found in registry"); //$NON-NLS-1$
			}
			if (exactContentTypeElement != null) {
				handler = reader.getInstance(exactContentTypeElement);
			}
			else if (kindOfContentTypeElement != null) {
				handler = reader.getInstance(kindOfContentTypeElement);
			}
		}

		if (handler == null) {
			// temp hard coding for null content type arguments
			handler = getHandlerExtension(INTERNAL_DEFAULT_EXTENSION); //$NON-NLS-1$
		}
		return handler;
	}

	/**
	 * Finds the registered IModelHandler for a given content type ID. No
	 * specific order is guaranteed and the uniqueness of IDs is not
	 * considered.
	 * 
	 * @param contentType
	 * @return The IModelHandler registered for the given content type ID. If
	 *         an exact match is not found, the most-specific match according
	 *         to IContentType.isKindOf() will be returned. If none are found,
	 *         either a default or null will be returned.
	 */
	public IModelHandler getHandlerForContentTypeId(String contentTypeId) {
		IContentType contentType = Platform.getContentTypeManager().getContentType(contentTypeId);
		return getHandlerForContentType(contentType);
	}
}
