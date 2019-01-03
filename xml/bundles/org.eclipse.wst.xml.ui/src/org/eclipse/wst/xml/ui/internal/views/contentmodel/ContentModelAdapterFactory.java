/*******************************************************************************
 * Copyright (c) 2010 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Carver - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.ui.internal.views.contentmodel;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class ContentModelAdapterFactory implements IAdapterFactory {
	private static Class[] ADAPTORLIST = new Class[] { IWorkbenchAdapter.class };
	private IWorkbenchAdapter adapter = new ContentModelWorkbenchAdapter();

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (IWorkbenchAdapter.class.equals(adapterType))
			return adapter;
		return null;
	}

	public Class[] getAdapterList() {
		return ADAPTORLIST;
	}

}
