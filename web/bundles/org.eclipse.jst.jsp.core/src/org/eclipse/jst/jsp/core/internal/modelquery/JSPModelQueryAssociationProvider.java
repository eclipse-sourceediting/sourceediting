/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.modelquery;

import org.eclipse.wst.xml.core.internal.contentmodel.modelqueryimpl.SimpleAssociationProvider;

/**
 * @deprecated
 */
public class JSPModelQueryAssociationProvider extends SimpleAssociationProvider {

	/**
	 * @param modelQueryCMProvider
	 *            org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQueryCMProvider
	 */
	public JSPModelQueryAssociationProvider() {
		super(new JSPModelQueryCMProvider());
	}
}
