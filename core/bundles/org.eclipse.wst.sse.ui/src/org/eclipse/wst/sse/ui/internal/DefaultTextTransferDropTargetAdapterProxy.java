/*******************************************************************************
 * Copyright (c) 2019 Dawid Pakula and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Dawid Pakula - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.sse.ui.internal;

import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;

public class DefaultTextTransferDropTargetAdapterProxy extends ExtendedEditorDropTargetAdapter {

	private DropTargetListener fDefaultTargetListener;

	public DefaultTextTransferDropTargetAdapterProxy(DropTargetListener defaultTargetListener) {
		super(true);
		fDefaultTargetListener = defaultTargetListener;
	}

	@Override
	public void dragEnter(DropTargetEvent event) {
		if (useSSE(event)) {
			super.dragEnter(event);
		} else {
			fDefaultTargetListener.dragEnter(event);
		}
	}

	@Override
	public void dragLeave(DropTargetEvent event) {
		if (useSSE(event)) {
			super.dragLeave(event);
		} else {
			fDefaultTargetListener.dragLeave(event);
		}
	}

	@Override
	public void dragOperationChanged(DropTargetEvent event) {
		if (useSSE(event)) {
			super.dragOperationChanged(event);
		} else {
			fDefaultTargetListener.dragOperationChanged(event);
		}
	}

	@Override
	public void dragOver(DropTargetEvent event) {
		if (useSSE(event)) {
			super.dragOver(event);
		} else {
			fDefaultTargetListener.dragOver(event);
		}
	}

	@Override
	public void drop(DropTargetEvent event) {
		if (useSSE(event)) {
			super.drop(event);
		} else {
			fDefaultTargetListener.drop(event);
		}
	}

	@Override
	public void dropAccept(DropTargetEvent event) {
		if (useSSE(event)) {
			super.dropAccept(event);
		} else {
			fDefaultTargetListener.dropAccept(event);
		}
	}

	private boolean useSSE(DropTargetEvent event) {
		for (Transfer tr : getTransfers()) {
			for (TransferData td : event.dataTypes) {
				if (tr.isSupportedType(td)) {
					return true;
				}
			}
		}

		return false;
	}
}
