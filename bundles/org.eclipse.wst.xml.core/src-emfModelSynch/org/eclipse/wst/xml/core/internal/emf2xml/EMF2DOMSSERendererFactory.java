/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.core.internal.emf2xml;

import org.eclipse.wst.common.internal.emf.resource.Renderer;
import org.eclipse.wst.common.internal.emf.resource.RendererFactory;

/**
 * @author schacher
 */
public class EMF2DOMSSERendererFactory extends RendererFactory {

	public static final EMF2DOMSSERendererFactory INSTANCE = new EMF2DOMSSERendererFactory();

	public EMF2DOMSSERendererFactory() {
		// Default constructor
	}


	public Renderer createRenderer() {
		return new EMF2DOMSSERenderer();
	}
}
