/*******************************************************************************
 * Copyright (c) 2001, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from  org.eclipse.wst.xml.ui.StructuredTextViewerConfigurationXML
 *                                           modified in order to process JSON Objects.          
 *******************************************************************************/
package org.eclipse.wst.json.ui;

import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.formatter.MultiPassContentFormatter;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.json.core.format.FormatProcessorJSON;
import org.eclipse.wst.json.core.text.IJSONPartitions;
import org.eclipse.wst.json.ui.internal.style.LineStyleProviderForJSON;
import org.eclipse.wst.sse.core.text.IStructuredPartitions;
import org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration;
import org.eclipse.wst.sse.ui.internal.format.StructuredFormattingStrategy;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;

/**
 * Configuration for a source viewer which shows JSON.
 * <p>
 * Clients can subclass and override just those methods which must be specific
 * to their needs.
 * </p>
 * 
 * @see org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration
 */
public class StructuredTextViewerConfigurationJSON extends
		StructuredTextViewerConfiguration {

	/*
	 * One instance per configuration because not sourceviewer-specific and it's
	 * a String array
	 */
	private String[] fConfiguredContentTypes;

	/*
	 * One instance per configuration
	 */
	private LineStyleProvider fLineStyleProviderForJSON;

	@Override
	public LineStyleProvider[] getLineStyleProviders(
			ISourceViewer sourceViewer, String partitionType) {
		LineStyleProvider[] providers = null;

		if ((partitionType == IJSONPartitions.JSON || partitionType == IJSONPartitions.COMMENT)) {
			providers = new LineStyleProvider[] { getLineStyleProviderForJSON() };
		}

		return providers;
	}

	private LineStyleProvider getLineStyleProviderForJSON() {
		if (fLineStyleProviderForJSON == null) {
			fLineStyleProviderForJSON = new LineStyleProviderForJSON();
		}
		return fLineStyleProviderForJSON;
	}

	@Override
	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
		IContentFormatter formatter = super.getContentFormatter(sourceViewer);
		// super was unable to create a formatter, probably because
		// sourceViewer does not have document set yet, so just create a
		// generic one
		if (!(formatter instanceof MultiPassContentFormatter))
			formatter = new MultiPassContentFormatter(
					getConfiguredDocumentPartitioning(sourceViewer),
					IJSONPartitions.JSON);

		((MultiPassContentFormatter) formatter)
				.setMasterStrategy(new StructuredFormattingStrategy(
						new FormatProcessorJSON()));

		return formatter;
	}

	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		if (fConfiguredContentTypes == null) {
			fConfiguredContentTypes = new String[] { IJSONPartitions.JSON,
					IJSONPartitions.COMMENT,
					IStructuredPartitions.DEFAULT_PARTITION,
					IStructuredPartitions.UNKNOWN_PARTITION };
		}
		return fConfiguredContentTypes;
	}
}
