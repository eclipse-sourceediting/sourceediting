/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
package org.eclipse.wst.xml.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.formatter.MultiPassContentFormatter;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.InformationPresenter;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredPartitionTypes;
import org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.internal.format.StructuredFormattingStrategy;
import org.eclipse.wst.sse.ui.internal.provisional.preferences.CommonEditorPreferenceNames;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;
import org.eclipse.wst.sse.ui.internal.reconcile.StructuredRegionProcessor;
import org.eclipse.wst.sse.ui.internal.taginfo.TextHoverManager;
import org.eclipse.wst.sse.ui.internal.util.EditorUtility;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.preferences.XMLCorePreferenceNames;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;
import org.eclipse.wst.xml.core.internal.provisional.text.IXMLPartitions;
import org.eclipse.wst.xml.core.internal.text.rules.StructuredTextPartitionerForXML;
import org.eclipse.wst.xml.ui.internal.autoedit.AutoEditStrategyForTabs;
import org.eclipse.wst.xml.ui.internal.autoedit.StructuredAutoEditStrategyXML;
import org.eclipse.wst.xml.ui.internal.contentassist.NoRegionContentAssistProcessor;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLContentAssistProcessor;
import org.eclipse.wst.xml.ui.internal.derived.HTMLTextPresenter;
import org.eclipse.wst.xml.ui.internal.doubleclick.XMLDoubleClickStrategy;
import org.eclipse.wst.xml.ui.internal.hyperlink.XMLHyperlinkDetector;
import org.eclipse.wst.xml.ui.internal.style.LineStyleProviderForXML;
import org.eclipse.wst.xml.ui.internal.taginfo.XMLInformationProvider;
import org.eclipse.wst.xml.ui.internal.taginfo.XMLTagInfoHoverProcessor;

/**
 * This class provides a SourceViewerConfiguration for editing XML content
 * type. Not intended to be subclassed.
 * 
 * @plannedfor 1.0
 */
public class StructuredTextViewerConfigurationXML extends StructuredTextViewerConfiguration {
	/*
	 * One instance per configuration because not sourceviewer-specific and
	 * it's a String array
	 */
	private String[] fConfiguredContentTypes;
	/*
	 * One instance per configuration
	 */
	private LineStyleProvider fLineStyleProviderForXML;
	/*
	 * One instance per configuration
	 */
	private IReconciler fReconciler;

	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		List allStrategies = new ArrayList(0);

		IAutoEditStrategy[] superStrategies = super.getAutoEditStrategies(sourceViewer, contentType);
		for (int i = 0; i < superStrategies.length; i++) {
			allStrategies.add(superStrategies[i]);
		}

		if (contentType == IXMLPartitions.XML_DEFAULT) {
			allStrategies.add(new StructuredAutoEditStrategyXML());
		}

		// be sure this is last, so it can modify any results form previous
		// commands that might on on same partiion type.
		// add auto edit strategy that handles when tab key is pressed
		allStrategies.add(new AutoEditStrategyForTabs());

		return (IAutoEditStrategy[]) allStrategies.toArray(new IAutoEditStrategy[allStrategies.size()]);
	}

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {

		if (fConfiguredContentTypes == null) {
			String[] xmlTypes = StructuredTextPartitionerForXML.getConfiguredContentTypes();
			fConfiguredContentTypes = new String[xmlTypes.length + 2];
			fConfiguredContentTypes[0] = IStructuredPartitionTypes.DEFAULT_PARTITION;
			fConfiguredContentTypes[1] = IStructuredPartitionTypes.UNKNOWN_PARTITION;
			int index = 0;
			System.arraycopy(xmlTypes, 0, fConfiguredContentTypes, index += 2, xmlTypes.length);
		}
		return fConfiguredContentTypes;
	}

	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		ContentAssistant assistant = (ContentAssistant) super.getContentAssistant(sourceViewer);

		// create content assist processors to be used
		IContentAssistProcessor xmlContentAssistProcessor = new XMLContentAssistProcessor();
		IContentAssistProcessor noRegionProcessor = new NoRegionContentAssistProcessor();

		// add processors to content assistant
		assistant.setContentAssistProcessor(xmlContentAssistProcessor, IStructuredPartitionTypes.DEFAULT_PARTITION);
		assistant.setContentAssistProcessor(xmlContentAssistProcessor, IXMLPartitions.XML_DEFAULT);
		assistant.setContentAssistProcessor(noRegionProcessor, IStructuredPartitionTypes.UNKNOWN_PARTITION);

		return assistant;
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
		final MultiPassContentFormatter formatter = new MultiPassContentFormatter(getConfiguredDocumentPartitioning(sourceViewer), IXMLPartitions.XML_DEFAULT);

		formatter.setMasterStrategy(new StructuredFormattingStrategy(new FormatProcessorXML()));

		return formatter;
	}

	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {

		ITextDoubleClickStrategy doubleClickStrategy = null;
		if (contentType.compareTo(IXMLPartitions.XML_DEFAULT) == 0)
			doubleClickStrategy = new XMLDoubleClickStrategy();
		else
			doubleClickStrategy = super.getDoubleClickStrategy(sourceViewer, contentType);
		return doubleClickStrategy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.editors.text.TextSourceViewerConfiguration#getHyperlinkDetectors(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		if (sourceViewer == null || !fPreferenceStore.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_HYPERLINKS_ENABLED))
			return null;

		List allDetectors = new ArrayList(0);
		allDetectors.add(new XMLHyperlinkDetector());

		IHyperlinkDetector[] superDetectors = super.getHyperlinkDetectors(sourceViewer);
		for (int m = 0; m < superDetectors.length; m++) {
			IHyperlinkDetector detector = superDetectors[m];
			if (!allDetectors.contains(detector)) {
				allDetectors.add(detector);
			}
		}
		return (IHyperlinkDetector[]) allDetectors.toArray(new IHyperlinkDetector[0]);
	}

	public String[] getIndentPrefixes(ISourceViewer sourceViewer, String contentType) {
		Vector vector = new Vector();

		// prefix[0] is either '\t' or ' ' x tabWidth, depending on preference
		Preferences preferences = XMLCorePlugin.getDefault().getPluginPreferences();
		int indentationWidth = preferences.getInt(XMLCorePreferenceNames.INDENTATION_SIZE);
		String indentCharPref = preferences.getString(XMLCorePreferenceNames.INDENTATION_CHAR);
		boolean useSpaces = XMLCorePreferenceNames.SPACE.equals(indentCharPref);

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

	/**
	 * Returns the information control creator. The creator is a factory
	 * creating information controls for the given source viewer.
	 * 
	 * @param sourceViewer
	 *            the source viewer to be configured by this configuration
	 * @return the information control creator or <code>null</code> if no
	 *         information support should be installed
	 * @since 2.0
	 */
	public IInformationControlCreator getInformationControlCreator(ISourceViewer sourceViewer) {
		// used by hover help
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				return new DefaultInformationControl(parent, SWT.NONE, new HTMLTextPresenter(false));
			}
		};
	}

	public IInformationPresenter getInformationPresenter(ISourceViewer sourceViewer) {
		InformationPresenter presenter = new InformationPresenter(getInformationPresenterControlCreator(sourceViewer));

		// information presenter configurations
		presenter.setSizeConstraints(60, 10, true, true);
		presenter.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

		// information providers to be used
		IInformationProvider xmlInformationProvider = new XMLInformationProvider();

		// add information providers to information presenter
		presenter.setInformationProvider(xmlInformationProvider, IStructuredPartitionTypes.DEFAULT_PARTITION);
		presenter.setInformationProvider(xmlInformationProvider, IXMLPartitions.XML_DEFAULT);

		return presenter;
	}

	/**
	 * Returns the information presenter control creator. The creator is a
	 * factory creating the presenter controls for the given source viewer.
	 * 
	 * @param sourceViewer
	 *            the source viewer to be configured by this configuration
	 * @return an information control creator
	 */
	private IInformationControlCreator getInformationPresenterControlCreator(ISourceViewer sourceViewer) {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				int shellStyle = SWT.RESIZE | SWT.TOOL;
				int style = SWT.V_SCROLL | SWT.H_SCROLL;
				return new DefaultInformationControl(parent, shellStyle, style, new HTMLTextPresenter(false));
			}
		};
	}

	public LineStyleProvider[] getLineStyleProviders(ISourceViewer sourceViewer, String partitionType) {
		LineStyleProvider[] providers = null;

		if (partitionType == IXMLPartitions.XML_DEFAULT || partitionType == IXMLPartitions.XML_CDATA || partitionType == IXMLPartitions.XML_COMMENT || partitionType == IXMLPartitions.XML_DECLARATION || partitionType == IXMLPartitions.XML_PI) {
			providers = new LineStyleProvider[]{getLineStyleProviderForXML()};
		}

		return providers;
	}

	private LineStyleProvider getLineStyleProviderForXML() {
		if (fLineStyleProviderForXML == null)
			fLineStyleProviderForXML = new LineStyleProviderForXML();
		return fLineStyleProviderForXML;
	}


	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		boolean reconcilingEnabled = fPreferenceStore.getBoolean(CommonEditorPreferenceNames.EVALUATE_TEMPORARY_PROBLEMS);
		if (sourceViewer == null || !reconcilingEnabled)
			return null;

		/*
		 * Only create reconciler if sourceviewer is present
		 */
		if (fReconciler == null && sourceViewer != null) {
			StructuredRegionProcessor reconciler = new StructuredRegionProcessor();

			// reconciler configurations
			reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

//			// reconciling strategies for reconciler
//			IReconcilingStrategy markupStrategy = new StructuredTextReconcilingStrategyForMarkup(sourceViewer);
//
//			// add reconciling strategies
//			reconciler.setReconcilingStrategy(markupStrategy, IXMLPartitions.XML_DEFAULT);
//			reconciler.setDefaultStrategy(markupStrategy);

			fReconciler = reconciler;
		}
		return fReconciler;
	}

	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType, int stateMask) {
		ITextHover textHover = null;

		// look for appropriate text hover processor to return based on
		// content type and state mask
		if ((contentType == IStructuredPartitionTypes.DEFAULT_PARTITION) || (contentType == IXMLPartitions.XML_DEFAULT)) {
			// check which of xml's text hover is handling stateMask
			TextHoverManager manager = SSEUIPlugin.getDefault().getTextHoverManager();
			TextHoverManager.TextHoverDescriptor[] hoverDescs = manager.getTextHovers();
			int i = 0;
			while (i < hoverDescs.length && textHover == null) {
				if (hoverDescs[i].isEnabled() && EditorUtility.computeStateMask(hoverDescs[i].getModifierString()) == stateMask) {
					String hoverType = hoverDescs[i].getId();
					if (TextHoverManager.COMBINATION_HOVER.equalsIgnoreCase(hoverType))
						textHover = manager.createBestMatchHover(new XMLTagInfoHoverProcessor());
					else if (TextHoverManager.DOCUMENTATION_HOVER.equalsIgnoreCase(hoverType))
						textHover = new XMLTagInfoHoverProcessor();
				}
				i++;
			}
		}

		// no appropriate text hovers found, try super
		if (textHover == null)
			textHover = super.getTextHover(sourceViewer, contentType, stateMask);

		return textHover;
	}
}
