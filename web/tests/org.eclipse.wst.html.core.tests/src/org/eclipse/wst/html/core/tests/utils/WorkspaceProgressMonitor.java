/*******************************************************************************
 * Copyright (c) 2004, 2017 IBM Corporation and others.
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
package org.eclipse.wst.html.core.tests.utils;

import org.eclipse.core.runtime.IProgressMonitor;

class WorkspaceProgressMonitor implements IProgressMonitor {
	private boolean finished = false;

	public void beginTask(String name, int totalWork) {
		// we dont' care
	}

	public void done() {
		finished = true;
	}

	public boolean isFinished() {
		return finished;
	}

	public void internalWorked(double work) {
		// we dont' care
	}

	public boolean isCanceled() {
		return finished;
	}

	public void setCanceled(boolean value) {
		if (value == true)
			finished = true;
	}

	public void setTaskName(String name) {
		// we don't care
	}

	public void subTask(String name) {
		// we don't care
	}

	public void worked(int work) {
		// we don't care
	}
}