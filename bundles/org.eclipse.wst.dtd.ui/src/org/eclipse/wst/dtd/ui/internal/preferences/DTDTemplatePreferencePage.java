/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.dtd.ui.internal.preferences;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.templates.TemplatePreferencePage;
import org.eclipse.wst.dtd.core.internal.provisional.contenttype.ContentTypeIdForDTD;
import org.eclipse.wst.dtd.ui.StructuredTextViewerConfigurationDTD;
import org.eclipse.wst.dtd.ui.internal.DTDUIPlugin;
import org.eclipse.wst.dtd.ui.internal.editor.IHelpContextIds;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;

/**
 * Preference page for DTD templates
 */
public class DTDTemplatePreferencePage extends TemplatePreferencePage {

	public DTDTemplatePreferencePage() {
		DTDUIPlugin dtdUIPlugin = DTDUIPlugin.getDefault();

		setPreferenceStore(dtdUIPlugin.getPreferenceStore());
		setTemplateStore(dtdUIPlugin.getTemplateStore());
		setContextTypeRegistry(dtdUIPlugin.getTemplateContextRegistry());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
		boolean ok = super.performOk();
		DTDUIPlugin.getDefault().savePluginPreferences();
		return ok;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.templates.TemplatePreferencePage#isShowFormatterSetting()
	 */
	protected boolean isShowFormatterSetting() {
		// template formatting has not been implemented
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite ancestor) {
		Control c = super.createContents(ancestor);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(c, IHelpContextIds.DTD_PREFWEBX_TEMPLATES_HELPID);
		return c;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.templates.TemplatePreferencePage#createViewer(org.eclipse.swt.widgets.Composite)
	 */
	protected SourceViewer createViewer(Composite parent) {
		SourceViewer viewer = null;
		String contentTypeID = ContentTypeIdForDTD.ContentTypeID_DTD;
		SourceViewerConfiguration sourceViewerConfiguration = new StructuredTextViewerConfiguration() {
			StructuredTextViewerConfiguration baseConfiguration = new StructuredTextViewerConfigurationDTD();

			public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
				return baseConfiguration.getConfiguredContentTypes(sourceViewer);
			}

			public LineStyleProvider[] getLineStyleProviders(ISourceViewer sourceViewer, String partitionType) {
				return baseConfiguration.getLineStyleProviders(sourceViewer, partitionType);
			}
		};
		viewer = new StructuredTextViewer(parent, null, null, false, SWT.LEFT_TO_RIGHT | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		((StructuredTextViewer) viewer).getTextWidget().setFont(JFaceResources.getTextFont());
		IStructuredModel scratchModel = StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(contentTypeID);
		IDocument document = scratchModel.getStructuredDocument();
		viewer.configure(sourceViewerConfiguration);
		viewer.setDocument(document);
		return viewer;
	}
}
