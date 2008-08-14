/*******************************************************************************
 * Copyright (c) 2008 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.jaxp.debug.ui.internal.views;

import java.io.IOException;
import java.io.Reader;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;
import org.eclipse.wst.xml.core.internal.provisional.contenttype.ContentTypeIdForXML;
import org.eclipse.wst.xsl.launching.model.XSLDebugTarget;
import org.eclipse.wst.xsl.ui.internal.StructuredTextViewerConfigurationXSL;

/**
 * TODO handle multiple concurrent debugging processes (and bring the current results to the top depending on which selected in Debug view)
 * TODO handle different output methods - xml, html, text - will need a call back from the debugger to tell us this (output method defined in XSL stylesheet)
 * 
 * @author Doug Satchwell
 */
public class ResultView extends ViewPart implements IDebugEventSetListener
{
	private SourceViewer sv;

	@Override
	public void dispose()
	{
		DebugPlugin.getDefault().removeDebugEventListener(this);
		super.dispose();
	}

	@Override
	public void createPartControl(Composite parent)
	{
		this.sv = createViewer(parent);
		
		// handle any launches already added
		IDebugTarget[] targets = DebugPlugin.getDefault().getLaunchManager().getDebugTargets();
		for (IDebugTarget debugTarget : targets)
		{
			if (debugTarget instanceof XSLDebugTarget)
			{
				handleDebugTarget((XSLDebugTarget)debugTarget);
			}			
		}
		// listen to further launches
		DebugPlugin.getDefault().addDebugEventListener(this);
	}

	private SourceViewer createViewer(Composite parent)
	{
		SourceViewerConfiguration sourceViewerConfiguration = new StructuredTextViewerConfiguration() {
			StructuredTextViewerConfiguration baseConfiguration = new StructuredTextViewerConfigurationXSL();

			public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
				return baseConfiguration.getConfiguredContentTypes(sourceViewer);
			}

			public LineStyleProvider[] getLineStyleProviders(ISourceViewer sourceViewer, String partitionType) {
				return baseConfiguration.getLineStyleProviders(sourceViewer, partitionType);
			}
		};
		SourceViewer viewer = new StructuredTextViewer(parent, null, null, false, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		((StructuredTextViewer) viewer).getTextWidget().setFont(JFaceResources.getFont("org.eclipse.wst.sse.ui.textfont")); //$NON-NLS-1$
		IStructuredModel scratchModel = StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		
		IDocument document = scratchModel.getStructuredDocument();
		viewer.configure(sourceViewerConfiguration);
		viewer.setDocument(document);
		viewer.setEditable(false);
		return viewer;
	}

	@Override
	public void setFocus()
	{}

	public void handleDebugEvents(DebugEvent[] events)
	{
		for (DebugEvent debugEvent : events)
		{
			if (debugEvent.getKind() == DebugEvent.CREATE && debugEvent.getSource() instanceof XSLDebugTarget)
			{
				handleDebugTarget((XSLDebugTarget)debugEvent.getSource());
			}
		}
	}
	
	private void handleDebugTarget(XSLDebugTarget xdt)
	{
		// first, clear the viewer
		sv.getDocument().set("");
		
		final Reader reader = xdt.getGenerateReader();
		IWorkbenchSiteProgressService service = (IWorkbenchSiteProgressService)getSite().getService(IWorkbenchSiteProgressService.class);
		service.schedule(new Job("Result view job"){
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				IStatus status = Status.OK_STATUS;
				try
				{
					char[] c = new char[8192]; // this is the default BufferedWriter size, so we will usually get chunks of this size
					int size;
					while((size = reader.read(c)) != -1)
					{
						writeString(new String(c,0,size));
					}
				}
				catch (IOException e)
				{
					// ignore
				}
				finally
				{
					monitor.done();
				}
				return status;
			}
			
			private void writeString(final String s)
			{
				getSite().getShell().getDisplay().syncExec(new Runnable(){

					public void run()
					{
						sv.getDocument().set(sv.getDocument().get()+s);
					}
				});
			}
		});
	}

}
