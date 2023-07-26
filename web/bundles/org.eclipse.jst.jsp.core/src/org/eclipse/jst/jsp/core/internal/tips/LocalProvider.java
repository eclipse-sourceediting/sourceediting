/*******************************************************************************
 * Copyright (c) 2023 Nitin Dahyabhai.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Nitin Dahyabhai - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.tips;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.tips.core.TipImage;

public class LocalProvider extends org.eclipse.tips.core.TipProvider {

	@Override
	public void dispose() {
		// TODO Auto-generated method stub		
	}

	@Override
	public String getDescription() {
		return "Tips for working with JSPs";
	}

	@Override
	public String getID() {
		return "org.eclipse.jst.jsp.core.internal.tips.LocalProvider";
	}

	@Override
	public TipImage getImage() {
		return null;
	}

	@Override
	public IStatus loadNewTips(IProgressMonitor monitor) {
		return Status.OK_STATUS;
	}

}
