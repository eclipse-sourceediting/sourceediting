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

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;
import org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;
import org.eclipse.wst.xsl.jaxp.launching.model.JAXPDebugTarget;
import org.eclipse.wst.xsl.ui.internal.StructuredTextViewerConfigurationXSL;

/**
 * TODO handle multiple concurrent debugging processes (and bring the current results to the top depending on which selected in Debug view)
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
			if (debugTarget instanceof JAXPDebugTarget)
			{
				handleDebugTarget((JAXPDebugTarget)debugTarget);
			}			
		}
		// listen to further launches
		DebugPlugin.getDefault().addDebugEventListener(this);
	}

	private SourceViewer createViewer(Composite parent)
	{
		SourceViewerConfiguration sourceViewerConfiguration = new StructuredTextViewerConfiguration() {
			StructuredTextViewerConfiguration baseConfiguration = new StructuredTextViewerConfigurationXSL();

			@Override
			public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
				return baseConfiguration.getConfiguredContentTypes(sourceViewer);
			}

			@Override
			public LineStyleProvider[] getLineStyleProviders(ISourceViewer sourceViewer, String partitionType) {
				return baseConfiguration.getLineStyleProviders(sourceViewer, partitionType);
			}
		};
		SourceViewer viewer = new StructuredTextViewer(parent, null, null, false, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		((StructuredTextViewer) viewer).getTextWidget().setFont(JFaceResources.getFont("org.eclipse.wst.sse.ui.textfont")); //$NON-NLS-1$
		viewer.configure(sourceViewerConfiguration);
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
			if (debugEvent.getKind() == DebugEvent.CREATE && debugEvent.getSource() instanceof JAXPDebugTarget)
			{
				handleDebugTarget((JAXPDebugTarget)debugEvent.getSource());
			}
		}
	}
	
	private void handleDebugTarget(JAXPDebugTarget xdt)
	{
		// first, clear the viewer
		sv.setDocument(null);
		IWorkbenchSiteProgressService service = (IWorkbenchSiteProgressService)getSite().getService(IWorkbenchSiteProgressService.class);
		service.schedule(new ResultViewJob(Messages.ResultView_0, getSite(), xdt, sv));
	}

}
