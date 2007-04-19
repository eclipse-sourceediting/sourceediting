/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.modelhandler;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jst.jsp.core.internal.modelquery.ModelQueryAdapterFactoryForTag;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.xml.core.internal.ssemodelquery.ModelQueryAdapter;

public class TagModelLoader extends JSPModelLoader {

	public TagModelLoader() {
	}

	public List getAdapterFactories() {
		List factories = super.getAdapterFactories();
		/*
		 * Replace the default JSP model query by using our own factory
		 */

		Iterator i = factories.iterator();
		while (i.hasNext()) {
			if (((INodeAdapterFactory) i.next()).isFactoryForType(ModelQueryAdapter.class)) {
				i.remove();
			}
		}

		factories.add(new ModelQueryAdapterFactoryForTag());
		return factories;
	}
}
