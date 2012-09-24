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
package org.eclipse.wst.sse.core.internal.ltk.modelhandler;

import java.util.List;

import org.eclipse.wst.sse.core.internal.provisional.IModelLoader;


/**
 * Responsible for providing the mechanisms used in the correct loading of an
 * IStructuredModel's contents and initialization of its adapter factories.
 */
public interface IModelHandler extends IDocumentTypeHandler {
	/**
	 * This method should return Factories which are added automatically by
	 * IModelManager. This can and will often be an empty List (or null),
	 * since some AdapterFactories must be added by Loader directly, and most
	 * should be added by Editors. FormatAdapterFactory is an example of one
	 * that can be returned here, since the timing of adding it is not
	 * critical, but it may be needed even when an editor is not being used.
	 */
	List getAdapterFactories();

	/**
	 * Returns the ID for the associated ContentTypeHandler But is needed for
	 * now.
	 */
	String getAssociatedContentTypeId();

	/**
	 * The Loader is reponsible for decoding the Resource,
	 */
	IModelLoader getModelLoader();

	boolean isDefault();
}
