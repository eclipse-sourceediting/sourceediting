/*******************************************************************************
 * Copyright (c) 2002, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.ui.internal.catalog;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogElement;


public abstract class ElementNodePage {

	Control fControl;

	public ElementNodePage() {
		super();

	}

	public abstract Control createControl(Composite parent);

	public Control getControl() {
		return fControl;
	}

	public abstract void saveData();

	public abstract ICatalogElement getData();
}
