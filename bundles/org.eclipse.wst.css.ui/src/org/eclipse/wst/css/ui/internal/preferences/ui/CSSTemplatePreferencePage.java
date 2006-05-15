/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.ui.internal.preferences.ui;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.templates.TemplatePreferencePage;
import org.eclipse.wst.css.ui.internal.CSSUIPlugin;
import org.eclipse.wst.css.ui.internal.editor.IHelpContextIds;

/**
 * Preference page for CSS templates
 */
public class CSSTemplatePreferencePage extends TemplatePreferencePage {
	
	public CSSTemplatePreferencePage() {
		CSSUIPlugin cssUIPlugin = CSSUIPlugin.getDefault();
		
		setPreferenceStore(cssUIPlugin.getPreferenceStore());
		setTemplateStore(cssUIPlugin.getTemplateStore());
		setContextTypeRegistry(cssUIPlugin.getTemplateContextRegistry());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
  	  boolean ok = super.performOk();
  	  CSSUIPlugin.getDefault().savePluginPreferences();
	  return ok;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.templates.TemplatePreferencePage#isShowFormatterSetting()
	 */
	protected boolean isShowFormatterSetting() {
		// template formatting has not been implemented
		return false;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite ancestor) {
		Control c = super.createContents(ancestor);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(c, IHelpContextIds.CSS_PREFWEBX_TEMPLATES_HELPID);
		return c;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.templates.TemplatePreferencePage#createViewer(org.eclipse.swt.widgets.Composite)
	 */
	protected SourceViewer createViewer(Composite parent) {
		SourceViewer viewer= new SourceViewer(parent, null, null, false, SWT.LEFT_TO_RIGHT | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		SourceViewerConfiguration configuration= new SourceViewerConfiguration();
		viewer.configure(configuration);
		IDocument document= new Document();
		viewer.setDocument(document);
		return viewer;
	}
}
