/*****************************************************************************
 * Copyright (c) 2004 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and
 * is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ****************************************************************************/
package org.eclipse.wst.css.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.formatter.MultiPassContentFormatter;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.css.core.internal.CSSCorePlugin;
import org.eclipse.wst.css.core.internal.format.FormatProcessorCSS;
import org.eclipse.wst.css.core.internal.preferences.CSSCorePreferenceNames;
import org.eclipse.wst.css.core.internal.provisional.text.ICSSPartitionTypes;
import org.eclipse.wst.css.ui.internal.autoedit.StructuredAutoEditStrategyCSS;
import org.eclipse.wst.css.ui.internal.contentassist.CSSContentAssistProcessor;
import org.eclipse.wst.css.ui.internal.style.LineStyleProviderForCSS;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredPartitionTypes;
import org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration;
import org.eclipse.wst.sse.ui.internal.format.StructuredFormattingStrategy;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;

public class StructuredTextViewerConfigurationCSS extends StructuredTextViewerConfiguration {
	/*
	 * One instance per configuration because not sourceviewer-specific and
	 * it's a String array
	 */
	private String[] fConfiguredContentTypes;
	/*
	 * One instance per configuration
	 */
	private LineStyleProvider fLineStyleProviderForCSS;

	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		List allStrategies = new ArrayList(0);

		IAutoEditStrategy[] superStrategies = super.getAutoEditStrategies(sourceViewer, contentType);
		for (int i = 0; i < superStrategies.length; i++) {
			allStrategies.add(superStrategies[i]);
		}

		if (contentType == ICSSPartitionTypes.STYLE) {
			allStrategies.add(new StructuredAutoEditStrategyCSS());
		}

		return (IAutoEditStrategy[]) allStrategies.toArray(new IAutoEditStrategy[0]);
	}

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		if (fConfiguredContentTypes == null) {
			fConfiguredContentTypes = new String[]{ICSSPartitionTypes.STYLE, IStructuredPartitionTypes.DEFAULT_PARTITION, IStructuredPartitionTypes.UNKNOWN_PARTITION};
		}
		return fConfiguredContentTypes;
	}

	protected IContentAssistProcessor[] getContentAssistProcessors(ISourceViewer sourceViewer, String partitionType) {
		IContentAssistProcessor[] processors = null;

		if ((partitionType == ICSSPartitionTypes.STYLE) || (partitionType == IStructuredPartitionTypes.UNKNOWN_PARTITION)) {
			processors = new IContentAssistProcessor[]{new CSSContentAssistProcessor()};
		}

		return processors;
	}

	/**
	 * Returns the content formatter ready to be used with the given source
	 * viewer.
	 * 
	 * @param sourceViewer
	 *            the source viewer to be configured by this configuration
	 * @return a content formatter or <code>null</code> if formatting should
	 *         not be supported
	 */
	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
		final MultiPassContentFormatter formatter = new MultiPassContentFormatter(getConfiguredDocumentPartitioning(sourceViewer), ICSSPartitionTypes.STYLE);

		formatter.setMasterStrategy(new StructuredFormattingStrategy(new FormatProcessorCSS()));

		return formatter;
	}

	public String[] getIndentPrefixes(ISourceViewer sourceViewer, String contentType) {
		Vector vector = new Vector();

		// prefix[0] is either '\t' or ' ' x tabWidth, depending on preference
		Preferences preferences = CSSCorePlugin.getDefault().getPluginPreferences();
		int indentationWidth = preferences.getInt(CSSCorePreferenceNames.INDENTATION_SIZE);
		String indentCharPref = preferences.getString(CSSCorePreferenceNames.INDENTATION_CHAR);
		boolean useSpaces = CSSCorePreferenceNames.SPACE.equals(indentCharPref);

		for (int i = 0; i <= indentationWidth; i++) {
			StringBuffer prefix = new StringBuffer();
			boolean appendTab = false;

			if (useSpaces) {
				for (int j = 0; j + i < indentationWidth; j++)
					prefix.append(' ');

				if (i != 0)
					appendTab = true;
			}
			else {
				for (int j = 0; j < i; j++)
					prefix.append(' ');

				if (i != indentationWidth)
					appendTab = true;
			}

			if (appendTab) {
				prefix.append('\t');
				vector.add(prefix.toString());
				// remove the tab so that indentation - tab is also an indent
				// prefix
				prefix.deleteCharAt(prefix.length() - 1);
			}
			vector.add(prefix.toString());
		}

		vector.add(""); //$NON-NLS-1$

		return (String[]) vector.toArray(new String[vector.size()]);
	}

	public LineStyleProvider[] getLineStyleProviders(ISourceViewer sourceViewer, String partitionType) {
		LineStyleProvider[] providers = null;

		if (partitionType == ICSSPartitionTypes.STYLE) {
			providers = new LineStyleProvider[]{getLineStyleProviderForCSS()};
		}

		return providers;
	}

	private LineStyleProvider getLineStyleProviderForCSS() {
		if (fLineStyleProviderForCSS == null) {
			fLineStyleProviderForCSS = new LineStyleProviderForCSS();
		}
		return fLineStyleProviderForCSS;
	}
}