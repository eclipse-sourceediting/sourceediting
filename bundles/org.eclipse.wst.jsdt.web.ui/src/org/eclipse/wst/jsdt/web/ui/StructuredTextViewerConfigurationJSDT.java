/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.formatter.IFormattingStrategy;
import org.eclipse.jface.text.formatter.MultiPassContentFormatter;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.wst.html.core.text.IHTMLPartitions;
import org.eclipse.wst.html.ui.StructuredTextViewerConfigurationHTML;
import org.eclipse.wst.jsdt.web.ui.internal.autoedit.AutoEditStrategyForTabs;
import org.eclipse.wst.jsdt.web.ui.internal.contentassist.JSDTContentAssistant;
import org.eclipse.wst.sse.ui.internal.ExtendedConfigurationBuilder;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;
import org.eclipse.wst.sse.ui.internal.taginfo.TextHoverManager;
import org.eclipse.wst.sse.ui.internal.util.EditorUtility;

/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
* 
 * Configuration for a source viewer which shows Html and supports JSDT.
 * <p>
 * Clients can subclass and override just those methods which must be specific
 * to their needs.
 * </p>
 * 
 * @see org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration
 * @since 1.0
 */
public class StructuredTextViewerConfigurationJSDT extends StructuredTextViewerConfigurationHTML {
	/*
	 * Extension point identifications for externalalized content providers
	 * [Bradley Childs - childsb@us.ibm.com]
	 */
	public static final class externalTypeExtension {
		public static final String AUTOEDIT_ID = "autoeditstrategy";
		public static final String CONTENT_ASSIST = "contentassistprocessor";
		public static final String CONTENT_FORMATER = "contentformater";
		public static final String HOVER_ID = "texthover";
		public static final String HYPERLINK_DETECTOR = "hyperlinkdetector";
		public static final String HYPERLINK_DETECTOR_TARGETS = "hyperlinkdetector";
		public static final String INFORMATIONPROVIDER_ID = "informationpresenter";
	}
	/*
	 * One instance per configuration because not sourceviewer-specific and it's
	 * a String array
	 */
	private String[] fConfiguredContentTypes;
	private ILabelProvider fStatusLineLabelProvider;
	
	/**
	 * Create new instance of StructuredTextViewerConfigurationHTML
	 */
	public StructuredTextViewerConfigurationJSDT() {
		// Must have empty constructor to createExecutableExtension
		super();
	}
	
	
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		List allStrategies = new ArrayList(0);
		Object externalAutoEditProvider = ExtendedConfigurationBuilder.getInstance().getConfiguration(externalTypeExtension.AUTOEDIT_ID, contentType);
		if (externalAutoEditProvider != null) {
			allStrategies.add(externalAutoEditProvider);
		} else {
			IAutoEditStrategy[] superStrategies = super.getAutoEditStrategies(sourceViewer, contentType);
			for (int i = 0; i < superStrategies.length; i++) {
				allStrategies.add(superStrategies[i]);
			}
		}
		// be sure this is added last in list, so it has a change to modify
		// previous results.
		// add auto edit strategy that handles when tab key is pressed
		allStrategies.add(new AutoEditStrategyForTabs());
		return (IAutoEditStrategy[]) allStrategies.toArray(new IAutoEditStrategy[allStrategies.size()]);
	}
	
	
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		if (fConfiguredContentTypes == null) {
			fConfiguredContentTypes = super.getConfiguredContentTypes(sourceViewer);
		}
		return fConfiguredContentTypes;
	}
	
	/* Content assist procesors are contributed by extension for SSE now */
	
	protected IContentAssistProcessor[] getContentAssistProcessors(ISourceViewer sourceViewer, String partitionType) {
		
		IContentAssistProcessor[] processors = null;

		if (partitionType == IHTMLPartitions.SCRIPT) {
			processors = new IContentAssistProcessor[]{new JSDTContentAssistant()};
		}
		else{
			processors = super.getContentAssistProcessors(sourceViewer, partitionType);
		}
		
		return processors;
	}
	
	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
		final IContentFormatter formatter = super.getContentFormatter(sourceViewer);
		/*
		 * Check for any externally supported auto edit strategies from EP.
		 * [Bradley Childs - childsb@us.ibm.com]
		 */
		String[] contentTypes = getConfiguredContentTypes(sourceViewer);
		for (int i = 0; i < contentTypes.length; i++) {
			IFormattingStrategy cf = (IFormattingStrategy) ExtendedConfigurationBuilder.getInstance().getConfiguration(externalTypeExtension.CONTENT_FORMATER, contentTypes[i]);
			if (cf != null && formatter instanceof MultiPassContentFormatter) {
				((MultiPassContentFormatter) formatter).setSlaveStrategy(cf, contentTypes[i]);
			}
		}
		return formatter;
	}
	
	
	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
		return super.getDoubleClickStrategy(sourceViewer, contentType);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.editors.text.TextSourceViewerConfiguration#getHyperlinkDetectors(org.eclipse.jface.text.source.ISourceViewer)
	 */
	
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		if (sourceViewer == null || !fPreferenceStore.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_HYPERLINKS_ENABLED)) {
			return null;
		}
		List allDetectors = new ArrayList(0);
		IHyperlinkDetector[] superDetectors = super.getHyperlinkDetectors(sourceViewer);
		for (int m = 0; m < superDetectors.length; m++) {
			IHyperlinkDetector detector = superDetectors[m];
			if (!allDetectors.contains(detector)) {
				allDetectors.add(detector);
			}
		}
		/* Check for external HyperLink Detectors */
		String[] contentTypes = getConfiguredContentTypes(sourceViewer);
		for (int i = 0; i < contentTypes.length; i++) {
			IHyperlinkDetector hl = (IHyperlinkDetector) ExtendedConfigurationBuilder.getInstance().getConfiguration(externalTypeExtension.HYPERLINK_DETECTOR, contentTypes[i]);
			if (hl != null) {
				allDetectors.add(hl);
			}
		}
		return (IHyperlinkDetector[]) allDetectors.toArray(new IHyperlinkDetector[0]);
	}
	
	
	public String[] getIndentPrefixes(ISourceViewer sourceViewer, String contentType) {
		return super.getIndentPrefixes(sourceViewer, contentType);
	}
	
	
	protected IInformationProvider getInformationProvider(ISourceViewer sourceViewer, String partitionType) {
		IInformationProvider provider = null;
		/*
		 * IInformationProvider now provided by extension point [Bradley Childs -
		 * childsb@us.ibm.com]
		 */
		Object externalInfoProvider = ExtendedConfigurationBuilder.getInstance().getConfiguration(externalTypeExtension.INFORMATIONPROVIDER_ID, partitionType);
		if (externalInfoProvider != null) {
			provider = (IInformationProvider) externalInfoProvider;
		} else {
			provider = super.getInformationProvider(sourceViewer, partitionType);
		}
		return provider;
	}
	
	
	public LineStyleProvider[] getLineStyleProviders(ISourceViewer sourceViewer, String partitionType) {
		LineStyleProvider[] providers = super.getLineStyleProviders(sourceViewer, partitionType);
		return providers;
	}
	
	
	public ILabelProvider getStatusLineLabelProvider(ISourceViewer sourceViewer) {
		if (fStatusLineLabelProvider == null) {
			fStatusLineLabelProvider = super.getStatusLineLabelProvider(sourceViewer);
		}
		return fStatusLineLabelProvider;
	}
	
	
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType, int stateMask) {
		ITextHover textHover = null;
		// look for appropriate text hover processor to return based on
		// content type and state mask
		TextHoverManager manager = SSEUIPlugin.getDefault().getTextHoverManager();
		TextHoverManager.TextHoverDescriptor[] hoverDescs = manager.getTextHovers();
		int i = 0;
		while (i < hoverDescs.length && textHover == null) {
			if (hoverDescs[i].isEnabled() && EditorUtility.computeStateMask(hoverDescs[i].getModifierString()) == stateMask) {
				String hoverType = hoverDescs[i].getId();
				if (TextHoverManager.COMBINATION_HOVER.equalsIgnoreCase(hoverType)) {
					/*
					 * Check extension for TextHover providers [Bradley Childs -
					 * childsb@us.ibm.com]
					 */
					Object externalHover = ExtendedConfigurationBuilder.getInstance().getConfiguration(externalTypeExtension.HOVER_ID, contentType);
					if (externalHover != null) {
						textHover = manager.createBestMatchHover((ITextHover) externalHover);
					} else {
						textHover = super.getTextHover(sourceViewer, contentType, stateMask);
					}
				} else if (TextHoverManager.DOCUMENTATION_HOVER.equalsIgnoreCase(hoverType)) {
					/*
					 * Check extension for TextHover providers [Bradley Childs -
					 * childsb@us.ibm.com]
					 */
					Object externalHover = ExtendedConfigurationBuilder.getInstance().getConfiguration(externalTypeExtension.HOVER_ID, contentType);
					if (externalHover != null) {
						textHover = manager.createBestMatchHover((ITextHover) externalHover);
					} else {
						textHover = super.getTextHover(sourceViewer, contentType, stateMask);
					}
				}
			}
			i++;
		}
		// no appropriate text hovers found, try super
		if (textHover == null) {
			textHover = super.getTextHover(sourceViewer, contentType, stateMask);
		}
		return textHover;
	}
}
