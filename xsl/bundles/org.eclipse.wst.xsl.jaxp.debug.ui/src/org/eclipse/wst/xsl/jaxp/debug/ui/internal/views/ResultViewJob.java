/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Carver - refactored from ResultView.
 *******************************************************************************/
package org.eclipse.wst.xsl.jaxp.debug.ui.internal.views;

import java.io.IOException;
import java.io.Reader;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.wst.xsl.jaxp.launching.model.JAXPDebugTarget;

public class ResultViewJob extends Job {

	private IWorkbenchPartSite viewSite = null;
	final Reader reader;
	private SourceViewer sourceViewer = null;

	public ResultViewJob(String name, IWorkbenchPartSite site,
			JAXPDebugTarget xdt, SourceViewer viewer) {
		super(name);
		viewSite = site;
		reader = xdt.getGenerateReader();
		sourceViewer = viewer;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		IStatus status = Status.OK_STATUS;
		try {
			// this is the default BufferedWriter
			// size, so we will usually get chunks
			// of this size
			
			char[] c = new char[8192];
			int size;
			while ((size = reader.read(c)) != -1) {
				writeString(new String(c, 0, size));
			}
		} catch (IOException e) {
			// ignore
		} finally {
			monitor.done();
		}
		return status;
	}

	private void writeString(final String s) {
		Shell shell = viewSite.getShell();
		Display display = shell.getDisplay();
		display.syncExec(new ResultRunnable(sourceViewer, s, viewSite));
	}
}
