/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
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
package org.eclipse.wst.sse.ui.internal.quickoutline;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.information.IInformationPresenter;

public class QuickOutlineHandler extends AbstractHandler {

	IInformationPresenter fPresenter;

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (fPresenter != null)
			fPresenter.showInformation();
		return null;
	}

	public void configure(IInformationPresenter presenter) {
		fPresenter = presenter;
	}

	public void dispose() {
		super.dispose();
		if (fPresenter != null) {
			fPresenter.uninstall();
			fPresenter = null;
		}
	}
}
