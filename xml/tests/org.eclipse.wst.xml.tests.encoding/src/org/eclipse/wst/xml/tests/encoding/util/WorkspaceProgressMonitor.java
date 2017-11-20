/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.tests.encoding.util;

import org.eclipse.core.runtime.IProgressMonitor;

class WorkspaceProgressMonitor implements IProgressMonitor {
	private boolean finished = false;

	public void beginTask(String name, int totalWork) {
		// noop
	}

	public void done() {
		finished = true;
	}

	public boolean isFinished() {
		return finished;
	}

	public void internalWorked(double work) {
		// noop
	}

	public boolean isCanceled() {
		return finished;
	}

	public void setCanceled(boolean value) {
		if (value == true)
			finished = true;
	}

	public void setTaskName(String name) {
		// noop
	}

	public void subTask(String name) {
		// noop
	}

	public void worked(int work) {
		// noop
	}
}