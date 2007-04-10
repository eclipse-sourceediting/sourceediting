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
package org.eclipse.wst.sse.core.internal.provisional;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.sse.core.internal.encoding.EncodingRule;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IModelHandler;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;


/**
 * Responsible for creating a new Model from a resource, or as a new, empty
 * instance.
 * 
 */
public interface IModelLoader {
	/**
	 * This method should perform all the model initialization required before
	 * it contains content, namely, it should call newModel, the
	 * createNewStructuredDocument(), then setAdapterFactories. (this is
	 * tentative)
	 */
	IStructuredModel createModel();

	/**
	 * Method createModel. Creates a new model based on old one.
	 * 
	 * @param oldModel
	 * @return IStructuredModel
	 */
	IStructuredModel createModel(IStructuredModel oldModel);

	/**
	 * This method must return those factories which must be attached to the
	 * structuredModel before content is applied.
	 */
	List getAdapterFactories();

	void load(IFile file, IStructuredModel model) throws IOException, CoreException;

	void load(InputStream inputStream, IStructuredModel model, EncodingRule encodingRule) throws IOException;

	void load(String filename, InputStream inputStream, IStructuredModel model, String encodingName, String lineDelimiter) throws IOException;

	IModelLoader newInstance();

	/**
	 * This method should always return an new, empty Structured Model
	 * appropriate for itself.
	 */
	IStructuredModel newModel();

	IStructuredModel reinitialize(IStructuredModel model);

	/**
	 * This method should get a fresh copy of the data, and repopulate the
	 * models ... normally by a call to setText on the structuredDocument, for
	 * StructuredModels. This method is needed in some cases where clients are
	 * sharing a model and then changes canceled. Say for example, one editor
	 * and several "displays" are sharing a model, if the editor is closed
	 * without saving changes, then the displays still need a model, but they
	 * should revert to the original unsaved version.
	 */
	void reload(InputStream inputStream, IStructuredModel model);

	/**
	 * Create a Structured Model with the given StructuredDocument instance as
	 * its document (instead of a new document instance as well)
	 */
	IStructuredModel createModel(IStructuredDocument document, String baseLocation, IModelHandler handler);

}
