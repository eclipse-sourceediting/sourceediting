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
package org.eclipse.wst.xml.ui.internal.provisional;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
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
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredPartitionTypes;
import org.eclipse.wst.sse.ui.internal.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.format.StructuredFormattingStrategy;
import org.eclipse.wst.sse.ui.internal.provisional.StructuredTextViewerConfiguration;
import org.eclipse.wst.sse.ui.internal.provisional.preferences.CommonEditorPreferenceNames;
import org.eclipse.wst.sse.ui.internal.provisional.style.IHighlighter;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;
import org.eclipse.wst.sse.ui.internal.reconcile.StructuredRegionProcessor;
import org.eclipse.wst.sse.ui.internal.taginfo.AnnotationHoverProcessor;
import org.eclipse.wst.sse.ui.internal.taginfo.ProblemAnnotationHoverProcessor;
import org.eclipse.wst.sse.ui.internal.taginfo.TextHoverManager;
import org.eclipse.wst.sse.ui.internal.util.EditorUtility;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.preferences.XMLCorePreferenceNames;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;
import org.eclipse.wst.xml.core.internal.provisional.text.IXMLPartitions;
import org.eclipse.wst.xml.core.internal.text.rules.StructuredTextPartitionerForXML;
import org.eclipse.wst.xml.ui.internal.autoedit.StructuredAutoEditStrategyXML;
import org.eclipse.wst.xml.ui.internal.contentassist.NoRegionContentAssistProcessor;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLContentAssistProcessor;
import org.eclipse.wst.xml.ui.internal.correction.CorrectionProcessorXML;
import org.eclipse.wst.xml.ui.internal.doubleclick.XMLDoubleClickStrategy;
import org.eclipse.wst.xml.ui.internal.hyperlink.XMLHyperlinkDetector;
import org.eclipse.wst.xml.ui.internal.style.LineStyleProviderForXML;
import org.eclipse.wst.xml.ui.internal.taginfo.XMLBestMatchHoverProcessor;
import org.eclipse.wst.xml.ui.internal.taginfo.XMLInformationProvider;
import org.eclipse.wst.xml.ui.internal.taginfo.XMLTagInfoHoverProcessor;
import org.eclipse.wst.xml.ui.internal.validation.StructuredTextReconcilingStrategyForMarkup;

/**
 * This class provides
 * 
 * @since 1.0
 */
public class StructuredTextViewerConfigurationXML extends StructuredTextViewerConfiguration {

	InformationPresenter fInformationPresenter = null;

	public StructuredTextViewerConfigurationXML() {
		super();
	}

	public StructuredTextViewerConfigurationXML(IPreferenceStore store) {
		super(store);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getAutoEditStrategies(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String)
	 */
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		List allStrategies = new ArrayList(0);

		IAutoEditStrategy[] superStrategies = super.getAutoEditStrategies(sourceViewer, contentType);
		for (int i = 0; i < superStrategies.length; i++) {
			allStrategies.add(superStrategies[i]);
		}

		if (contentType == IXMLPartitions.XML_DEFAULT) {
			allStrategies.add(new StructuredAutoEditStrategyXML());
		}

		return (IAutoEditStrategy[]) allStrategies.toArray(new IAutoEditStrategy[0]);
	}

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {

		if (configuredContentTypes == null) {
			String[] xmlTypes = StructuredTextPartitionerForXML.getConfiguredContentTypes();
			configuredContentTypes = new String[xmlTypes.length + 2];
			configuredContentTypes[0] = IStructuredPartitionTypes.DEFAULT_PARTITION;
			configuredContentTypes[1] = IStructuredPartitionTypes.UNKNOWN_PARTITION;
			int index = 0;
			System.arraycopy(xmlTypes, 0, configuredContentTypes, index += 2, xmlTypes.length);
		}
		return configuredContentTypes;
	}

	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {

		IContentAssistant ca = super.getContentAssistant(sourceViewer);
		if (ca != null && ca instanceof ContentAssistant) {
			ContentAssistant contentAssistant = (ContentAssistant) ca;
			IContentAssistProcessor xmlContentAssistProcessor = new XMLContentAssistProcessor();
			IContentAssistProcessor noRegionProcessor = new NoRegionContentAssistProcessor();
			setContentAssistProcessor(contentAssistant, xmlContentAssistProcessor, IStructuredPartitionTypes.DEFAULT_PARTITION);
			setContentAssistProcessor(contentAssistant, xmlContentAssistProcessor, IXMLPartitions.XML_DEFAULT);
			setContentAssistProcessor(contentAssistant, noRegionProcessor, IStructuredPartitionTypes.UNKNOWN_PARTITION);
		}
		return ca;
	}

	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
		final MultiPassContentFormatter formatter = new MultiPassContentFormatter(getConfiguredDocumentPartitioning(sourceViewer), IXMLPartitions.XML_DEFAULT);

		formatter.setMasterStrategy(new StructuredFormattingStrategy(new FormatProcessorXML()));

		return formatter;
	}

	public IContentAssistant getCorrectionAssistant(ISourceViewer sourceViewer) {
		IContentAssistant ca = super.getCorrectionAssistant(sourceViewer);

		if (ca != null && ca instanceof ContentAssistant) {
			ContentAssistant correctionAssistant = (ContentAssistant) ca;
			ITextEditor editor = getTextEditor();
			if (editor != null) {
				IContentAssistProcessor correctionProcessor = new CorrectionProcessorXML(editor);
				correctionAssistant.setContentAssistProcessor(correctionProcessor, IXMLPartitions.XML_DEFAULT);
				correctionAssistant.setContentAssistProcessor(correctionProcessor, IXMLPartitions.XML_CDATA);
				correctionAssistant.setContentAssistProcessor(correctionProcessor, IXMLPartitions.XML_COMMENT);
				correctionAssistant.setContentAssistProcessor(correctionProcessor, IXMLPartitions.XML_DECLARATION);
				correctionAssistant.setContentAssistProcessor(correctionProcessor, IXMLPartitions.XML_PI);
				correctionAssistant.setContentAssistProcessor(correctionProcessor, IXMLPartitions.DTD_SUBSET);
			}
		}
		return ca;
	}

	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {

		ITextDoubleClickStrategy doubleClickStrategy = null;
		if (contentType.compareTo(IXMLPartitions.XML_DEFAULT) == 0)
			doubleClickStrategy = new XMLDoubleClickStrategy();
		else
			doubleClickStrategy = super.getDoubleClickStrategy(sourceViewer, contentType);
		return doubleClickStrategy;
	}

	public IHighlighter getHighlighter(ISourceViewer sourceViewer) {

		IHighlighter highlighter = super.getHighlighter(sourceViewer);
		if (highlighter != null) {
			LineStyleProvider xmlProvider = new LineStyleProviderForXML();
			highlighter.addProvider(IXMLPartitions.XML_DEFAULT, xmlProvider);
			highlighter.addProvider(IXMLPartitions.XML_CDATA, xmlProvider);
			highlighter.addProvider(IXMLPartitions.XML_COMMENT, xmlProvider);
			highlighter.addProvider(IXMLPartitions.XML_DECLARATION, xmlProvider);
			highlighter.addProvider(IXMLPartitions.XML_PI, xmlProvider);
		}
		return highlighter;
	}

	public IInformationPresenter getInformationPresenter(ISourceViewer sourceViewer) {

		if (fInformationPresenter == null) {
			fInformationPresenter = new InformationPresenter(getInformationPresenterControlCreator(sourceViewer));
			IInformationProvider xmlInformationProvider = new XMLInformationProvider();
			fInformationPresenter.setInformationProvider(xmlInformationProvider, IStructuredPartitionTypes.DEFAULT_PARTITION);
			fInformationPresenter.setInformationProvider(xmlInformationProvider, IXMLPartitions.XML_DEFAULT);
			fInformationPresenter.setSizeConstraints(60, 10, true, true);
			fInformationPresenter.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		}
		return fInformationPresenter;
	}

	public IReconciler getReconciler(ISourceViewer sourceViewer) {

		if (fReconciler != null) {
			// a reconciler should always be installed or disposed of
			if (!fReconciler.isInstalled()) {
				fReconciler = null;
			}
		}

		if (fReconciler == null) {
			fReconciler = new StructuredRegionProcessor();
			fReconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		}

		boolean reconcilingEnabled = fPreferenceStore.getBoolean(CommonEditorPreferenceNames.EVALUATE_TEMPORARY_PROBLEMS);

		if (!reconcilingEnabled)
			return fReconciler;

		// the second time through, the strategies are set
		if (fReconciler != null) {

			IDocument doc = ((StructuredTextEditor) editorPart).getDocumentProvider().getDocument(editorPart.getEditorInput());
			IStructuredModel sModel = StructuredModelManager.getModelManager().getExistingModelForRead(doc);

			try {

				if (sModel != null) {

					IReconcilingStrategy markupStrategy = new StructuredTextReconcilingStrategyForMarkup((ITextEditor) editorPart);
					fReconciler.setReconcilingStrategy(markupStrategy, IXMLPartitions.XML_DEFAULT);
					fReconciler.setDefaultStrategy(markupStrategy);

					String contentTypeId = sModel.getContentTypeIdentifier();
					if (contentTypeId != null)
						fReconciler.setValidatorStrategy(createValidatorStrategy(contentTypeId));
				}
			} finally {
				if (sModel != null)
					sModel.releaseFromRead();
			}
		}
		return fReconciler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getTextHover(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String, int)
	 */
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType, int stateMask) {
		// look for appropriate text hover processor to return based on
		// content type and state mask
		if ((contentType == IStructuredPartitionTypes.DEFAULT_PARTITION) || (contentType == IXMLPartitions.XML_DEFAULT)) {
			// check which of xml's text hover is handling stateMask
			TextHoverManager.TextHoverDescriptor[] hoverDescs = getTextHovers();
			int i = 0;
			while (i < hoverDescs.length) {
				if (hoverDescs[i].isEnabled() && EditorUtility.computeStateMask(hoverDescs[i].getModifierString()) == stateMask) {
					String hoverType = hoverDescs[i].getId();
					if (TextHoverManager.COMBINATION_HOVER.equalsIgnoreCase(hoverType))
						return new XMLBestMatchHoverProcessor();
					else if (TextHoverManager.PROBLEM_HOVER.equalsIgnoreCase(hoverType))
						return new ProblemAnnotationHoverProcessor();
					else if (TextHoverManager.ANNOTATION_HOVER.equalsIgnoreCase(hoverType))
						return new AnnotationHoverProcessor();
					else if (TextHoverManager.DOCUMENTATION_HOVER.equalsIgnoreCase(hoverType))
						return new XMLTagInfoHoverProcessor();
				}
				i++;
			}
		}
		return super.getTextHover(sourceViewer, contentType, stateMask);
	}

	public void unConfigure(ISourceViewer viewer) {

		super.unConfigure(viewer);

		// InformationPresenters
		if (fInformationPresenter != null) {
			fInformationPresenter.uninstall();
		}
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
			} else {
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
}
