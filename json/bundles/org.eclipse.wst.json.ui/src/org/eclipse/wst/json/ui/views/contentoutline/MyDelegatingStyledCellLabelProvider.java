/**
 *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.wst.json.ui.views.contentoutline;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;

class MyDelegatingStyledCellLabelProvider extends DelegatingStyledCellLabelProvider implements ILabelProvider {

	public MyDelegatingStyledCellLabelProvider(
			IStyledLabelProvider labelProvider) {
		super(labelProvider);
	}

	@Override
	public String getText(Object element) {
		return getStyledText(element).getString();
	}

}
