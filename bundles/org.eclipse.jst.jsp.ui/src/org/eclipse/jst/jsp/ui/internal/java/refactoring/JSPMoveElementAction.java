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
package org.eclipse.jst.jsp.ui.internal.java.refactoring;

import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.codemanipulation.CodeGenerationSettings;
import org.eclipse.jdt.internal.corext.refactoring.reorg.JavaMoveProcessor;
import org.eclipse.jdt.internal.corext.util.CodeFormatterUtil;
import org.eclipse.jdt.internal.ui.refactoring.RefactoringMessages;
import org.eclipse.jdt.internal.ui.refactoring.actions.RefactoringStarter;
import org.eclipse.jdt.internal.ui.refactoring.reorg.CreateTargetQueries;
import org.eclipse.jdt.internal.ui.refactoring.reorg.ReorgMoveWizard;
import org.eclipse.jdt.internal.ui.refactoring.reorg.ReorgQueries;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jst.jsp.ui.StructuredTextEditorJSP;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.jst.jsp.ui.internal.Logger;
import org.eclipse.ltk.core.refactoring.participants.MoveRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;
import org.eclipse.wst.sse.ui.util.PlatformStatusLineUtil;

/**
 * A TextEditorAction that launches JDT move element wizard
 * 
 * Still relies heavily on internal API
 * will change post 3.0 with public move support
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=61817 
 * @author pavery
 */
public class JSPMoveElementAction extends TextEditorAction {

	public JSPMoveElementAction(ResourceBundle bundle, String prefix, ITextEditor editor) {

		super(bundle, prefix, editor);
	}
	
	public boolean isEnabled() {
		// always enabled, just print appropriate status to window
		// if for some reason the action can't run (like multiple java elements selected)
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.TextEditorAction#update()
	 */
	public void update() {
		super.update();
		PlatformStatusLineUtil.clearStatusLine();
	}
	
	private IJavaElement[] getSelectedElements() {
		
		IJavaElement[] elements = new IJavaElement[0];
		if(getTextEditor() != null) 
			elements = ((StructuredTextEditorJSP)getTextEditor()).getJavaElementsForCurrentSelection();
		return elements;
	}
	
	private IResource[] getResources(IJavaElement[] elements) {

		IResource[] resources = new IResource[elements.length];
		for (int i = 0; i < elements.length; i++) {
			try {
				resources[i] = elements[i].getPrimaryElement().getCorrespondingResource();//elements[i].getResource();
			} catch (JavaModelException e) {
				Logger.logException(e);
			}
		}
		return resources;
	}

	/**
	 * will change post 3.0 with public move support
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=61817
	 */
	public void run() {
		IJavaElement[] elements = getSelectedElements();
		if(elements.length > 0) {	
			
			// need to check if it's movable
			try {
				JavaMoveProcessor processor= JavaMoveProcessor.create(getResources(elements), elements, getCodeGenerationSettings());
				Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				
				MoveRefactoring refactoring= new MoveRefactoring(processor);
			
				RefactoringWizard wizard= createWizard(refactoring);
				
				
				/*
				 * We want to get the shell from the refactoring dialog but it's not
				 * known at this point, so we pass the wizard and then, once the dialog
				 * is open, we will have access to its shell.
				 */
				
				processor.setCreateTargetQueries(new CreateTargetQueries(wizard));
				processor.setReorgQueries(new ReorgQueries(wizard));
				
				new RefactoringStarter().activate(refactoring, wizard, parent, 
					RefactoringMessages.getString("OpenRefactoringWizardAction.refactoring"), //$NON-NLS-1$ 
					true);
				
				PlatformStatusLineUtil.clearStatusLine();
				
			} catch (JavaModelException e) {
				Logger.logException(e);
			}
		}
		else  {
			PlatformStatusLineUtil.displayErrorMessage(JSPUIPlugin.getResourceString("%JSPMoveElementAction.0")); //$NON-NLS-1$
		}
	}

	// these methods are copied from internal JDT class org.eclipse.jdt.internal.ui.preferences.JavaPreferencesSettings
	/**
	 * will change post 3.0 with public move support
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=61817
	 */
	public static CodeGenerationSettings getCodeGenerationSettings() {
		
		IPreferenceStore store= PreferenceConstants.getPreferenceStore();
		
		CodeGenerationSettings res= new CodeGenerationSettings();
		res.createComments= store.getBoolean(PreferenceConstants.CODEGEN_ADD_COMMENTS);
		res.useKeywordThis= store.getBoolean(PreferenceConstants.CODEGEN_KEYWORD_THIS);
		res.importOrder= getImportOrderPreference(store);
		res.importThreshold= getImportNumberThreshold(store);
		res.tabWidth= CodeFormatterUtil.getTabWidth();
		return res;
	}

	/**
	 * will change post 3.0 with public move support
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=61817
	 */
	public static int getImportNumberThreshold(IPreferenceStore prefs) {
		int threshold= prefs.getInt(PreferenceConstants.ORGIMPORTS_ONDEMANDTHRESHOLD);
		if (threshold < 0) {
			threshold= Integer.MAX_VALUE;
		}
		return threshold;
	}

	/**
	 * will change post 3.0 with public move support
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=61817
	 */
	public static String[] getImportOrderPreference(IPreferenceStore prefs) {
		String str= prefs.getString(PreferenceConstants.ORGIMPORTS_IMPORTORDER);
		if (str != null) {
			return unpackList(str, ";"); //$NON-NLS-1$
		}
		return new String[0];
	}

	/**
	 * will change post 3.0 with public move support
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=61817
	 */
	private static String[] unpackList(String str, String separator) {
		StringTokenizer tok= new StringTokenizer(str, separator); //$NON-NLS-1$
		int nTokens= tok.countTokens();
		String[] res= new String[nTokens];
		for (int i= 0; i < nTokens; i++) {
			res[i]= tok.nextToken().trim();
		}
		return res;
	}
	
	/**
	 * will change post 3.0 with public move support
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=61817
	 */
	private RefactoringWizard createWizard(MoveRefactoring refactoring) {
		return new ReorgMoveWizard(refactoring);
	}
}