/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
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
package org.eclipse.wst.sse.ui.internal.contentproperties.ui;



import org.eclipse.swt.widgets.Composite;

/**
 * @deprecated People should manage their own combo/list
 */
public final class ComboListOnPropertyPage extends ComboList {


	private String currentApplyValue;

	public ComboListOnPropertyPage(Composite parent, int style) {
		super(parent, style);
	}

	public final String getApplyValue() {
		return currentApplyValue;
	}

	public final void setApplyValue(String currentApplyValue) {
		this.currentApplyValue = currentApplyValue;
	}

}
