/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.modelhandler;

import org.eclipse.wst.sse.core.internal.provisional.IModelLoader;

public class ModelHandlerForTag extends ModelHandlerForJSP {

	/**
	 * Needs to match what's in plugin registry. In fact, can be overwritten
	 * at run time with what's in registry! (so should never be 'final')
	 */
	static String AssociatedContentTypeID = "org.eclipse.jst.jsp.core.tagsource"; //$NON-NLS-1$
	/**
	 * Needs to match what's in plugin registry. In fact, can be overwritten
	 * at run time with what's in registry! (so should never be 'final')
	 */
	private static String ModelHandlerID = "org.eclipse.jst.jsp.core.modelhandler.tag"; //$NON-NLS-1$


	public ModelHandlerForTag() {
		super();
		setId(ModelHandlerID);
		setAssociatedContentTypeId(AssociatedContentTypeID);
	}

	public IModelLoader getModelLoader() {
		return new TagModelLoader();
	}
}
