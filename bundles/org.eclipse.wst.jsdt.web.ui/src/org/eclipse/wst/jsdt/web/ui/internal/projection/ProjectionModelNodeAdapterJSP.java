/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.internal.projection;

/**
 * Updates projection annotation model with projection annotations for this
 * adapter node's children
 */
public class ProjectionModelNodeAdapterJSP extends
		ProjectionModelNodeAdapterHTML {
	public ProjectionModelNodeAdapterJSP(
			ProjectionModelNodeAdapterFactoryJSP factory) {
		super(factory);
	}

	@Override
	public boolean isAdapterForType(Object type) {
		return type == ProjectionModelNodeAdapterJSP.class;
	}
}
