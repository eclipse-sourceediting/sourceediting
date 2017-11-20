/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.dtd.ui;

import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.dtd.core.text.IDTDPartitions;
import org.eclipse.wst.dtd.ui.internal.style.LineStyleProviderForDTD;
import org.eclipse.wst.sse.core.text.IStructuredPartitions;
import org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;


/**
 * Configuration for a source viewer which shows DTD content.
 * <p>
 * Clients can subclass and override just those methods which must be specific
 * to their needs.
 * </p>
 * 
 * @see org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration
 * @since 1.0
 */
public class StructuredTextViewerConfigurationDTD extends StructuredTextViewerConfiguration {
	/*
	 * One instance per configuration because not sourceviewer-specific and
	 * it's a String array
	 */
	private String[] fConfiguredContentTypes;
	/*
	 * One instance per configuration
	 */
	private LineStyleProvider[] fLineStyleProviders;

	/**
	 * Create new instance of StructuredTextViewerConfigurationDTD
	 */
	public StructuredTextViewerConfigurationDTD() {
		// Must have empty constructor to createExecutableExtension
		super();
	}

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		if (fConfiguredContentTypes == null) {
			fConfiguredContentTypes = new String[]{IDTDPartitions.DTD_DEFAULT, IStructuredPartitions.DEFAULT_PARTITION, IStructuredPartitions.UNKNOWN_PARTITION};
		}
		return fConfiguredContentTypes;
	}

	public LineStyleProvider[] getLineStyleProviders(ISourceViewer sourceViewer, String partitionType) {
		if (fLineStyleProviders == null) {
			fLineStyleProviders = new LineStyleProvider[]{createLineStyleProviderForDTD()};
		}
		return fLineStyleProviders;
	}

	private LineStyleProvider createLineStyleProviderForDTD() {
		return new LineStyleProviderForDTD();
	}

}
