/*******************************************************************************
 * Copyright (c) 2013, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.ui.internal.text.correction;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.wst.html.core.internal.HTMLCorePlugin;
import org.eclipse.wst.html.core.internal.preferences.HTMLCorePreferenceNames;
import org.eclipse.wst.html.ui.internal.HTMLUIPlugin;
import org.eclipse.wst.html.ui.internal.Logger;
import org.eclipse.wst.html.ui.internal.editor.HTMLEditorPluginImageHelper;
import org.eclipse.wst.html.ui.internal.editor.HTMLEditorPluginImages;
import org.eclipse.wst.html.ui.internal.preferences.ui.HTMLValidationPreferencePage;
import org.osgi.service.prefs.BackingStoreException;
import org.w3c.dom.Node;

public class IgnoreAttributeNameCompletionProposal implements ICompletionProposal {
	/** The string to be added to the Ignored HTML Attributes list. */
	private String fPattern;
	/** The target node */
	private Node fTarget;
	/** The string to be displayed in the completion proposal popup. */
	private String fDisplayString;
	/** The replacement offset. */
	private int fReplacementOffset;
	/** The context information of this proposal. */
	private IContextInformation fContextInformation;
	/** The additional info of this proposal. */
	private String fAdditionalProposalInfo;
	
	private IPreferencesService fPreferenceService;

	public IgnoreAttributeNameCompletionProposal(String pattern, int offset, String displayString, String additionalProposalInfo, Node target) {
		fReplacementOffset= offset;
		fPattern = pattern;
		fDisplayString= displayString;
		fAdditionalProposalInfo= additionalProposalInfo;
		fTarget = target;
		fPreferenceService = Platform.getPreferencesService();
	}

	/*
	 * @see ICompletionProposal#apply(IDocument)
	 */
	public void apply(IDocument document) {
		Object adapter = (fTarget instanceof IAdaptable ? ((IAdaptable)fTarget).getAdapter(IResource.class) : null);
		IProject project = (adapter instanceof IResource ? ((IResource)adapter).getProject() : null);

		IScopeContext[] fLookupOrder = new IScopeContext[] {new InstanceScope(), new DefaultScope()};
		boolean hasProjectSettings = false;
		if (project != null) {
			ProjectScope projectScope = new ProjectScope(project);
			if(projectScope.getNode(getPreferenceNodeQualifier())
					.getBoolean(getProjectSettingsKey(), false)) {
				hasProjectSettings = true;
				fLookupOrder = new IScopeContext[] {projectScope, new InstanceScope(), new DefaultScope()};
			}
		}
		
		boolean originalEnableIgnore = fPreferenceService.getBoolean(
				getPreferenceNodeQualifier(), HTMLCorePreferenceNames.IGNORE_ATTRIBUTE_NAMES, 
				HTMLCorePreferenceNames.IGNORE_ATTRIBUTE_NAMES_DEFAULT, fLookupOrder);
		
		String originalAttributeNames = fPreferenceService.getString(
				getPreferenceNodeQualifier(), HTMLCorePreferenceNames.ATTRIBUTE_NAMES_TO_IGNORE, 
				HTMLCorePreferenceNames.ATTRIBUTE_NAMES_TO_IGNORE_DEFAULT, fLookupOrder);
		
		StringBuffer ignoreList = new StringBuffer(originalAttributeNames);
	
		if (!containsPattern(originalAttributeNames, fPattern)) { 
			if (ignoreList.length() > 0)
				ignoreList.append(',');
			
			ignoreList.append(fPattern.toLowerCase());
		}

		fLookupOrder[0].getNode(getPreferenceNodeQualifier())
			.putBoolean(HTMLCorePreferenceNames.IGNORE_ATTRIBUTE_NAMES, true); 

		fLookupOrder[0].getNode(getPreferenceNodeQualifier())
			.put(HTMLCorePreferenceNames.ATTRIBUTE_NAMES_TO_IGNORE, ignoreList.toString()); 

		PreferenceDialog dialog = hasProjectSettings ? 
				PreferencesUtil.createPropertyDialogOn(getShell(), project, HTMLValidationPreferencePage.PROPERTY_PAGE_ID, null, null) :
					PreferencesUtil.createPreferenceDialogOn(getShell(), HTMLValidationPreferencePage.PREFERENCE_PAGE_ID, null, null);

		int result = Window.CANCEL;
		if (dialog != null) {
			Object page = dialog.getSelectedPage();
			if (page instanceof HTMLValidationPreferencePage) {
				((HTMLValidationPreferencePage)page).overrideIgnoredAttributesOriginValues(originalEnableIgnore, originalAttributeNames);
			}
			result = dialog.open();
		}

		if (Window.CANCEL == result) {
			fLookupOrder[0].getNode(getPreferenceNodeQualifier())
			.putBoolean(HTMLCorePreferenceNames.IGNORE_ATTRIBUTE_NAMES, originalEnableIgnore); 

			fLookupOrder[0].getNode(getPreferenceNodeQualifier())
				.put(HTMLCorePreferenceNames.ATTRIBUTE_NAMES_TO_IGNORE, originalAttributeNames); 
		
			for(int i = 0; i < fLookupOrder.length; i++) {
				try {
					fLookupOrder[i].getNode(getPreferenceNodeQualifier()).flush();
				} catch (BackingStoreException e) {
					Logger.logException(e);
				}
			}
		}
	}
	
	private boolean containsPattern(String ignoreList, String pattern) {
		Set result = new HashSet();
		if (ignoreList.trim().length() > 0) {
			String[] names = ignoreList.split(","); //$NON-NLS-1$
			for (int i = 0; names != null && i < names.length; i++) {
				String name = names[i] == null ? null : names[i].trim();
				if (name != null && name.length() > 0) 
					result.add(name.toLowerCase());
			}
		}
		return result.contains(pattern.toLowerCase());
	}

	
	/*
	 * @see ICompletionProposal#getDisplayString()
	 */
	public String getDisplayString() {
		if (fDisplayString != null)
			return fDisplayString;
		return ""; //$NON-NLS-1$
	}

	/*
	 * @see ICompletionProposal#getAdditionalProposalInfo()
	 */
	public String getAdditionalProposalInfo() {
		return fAdditionalProposalInfo;
	}

	/*
	 * @see ICompletionProposal#getImage()
	 */
	public Image getImage() {
		return HTMLEditorPluginImageHelper.getInstance().getImage(HTMLEditorPluginImages.IMG_DTOOL_DO_NOT_VALIDATE);
	}

	/*
	 * @see ICompletionProposal#getSelection(IDocument)
	 */
	public Point getSelection(IDocument document) {
		return new Point(fReplacementOffset /*+ fCursorPosition*/, 0);
	}

	/*
	 * @see ICompletionProposal#getContextInformation()
	 */
	public IContextInformation getContextInformation() {
		return fContextInformation;
	}

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof IgnoreAttributeNameCompletionProposal) {
			IgnoreAttributeNameCompletionProposal p = (IgnoreAttributeNameCompletionProposal)obj;
			return (this.fPattern.equals(p.fPattern) && this.fTarget == p.fTarget && this.fReplacementOffset == p.fReplacementOffset);
		}
		return false;
	}
	
	private String getPreferenceNodeQualifier() {
		return HTMLCorePlugin.getDefault().getBundle().getSymbolicName();
	}

	private String getProjectSettingsKey() {
		return HTMLCorePreferenceNames.USE_PROJECT_SETTINGS;
	}
	
	private Shell getShell() {
		IWorkbench workBench = HTMLUIPlugin.getDefault().getWorkbench();
		IWorkbenchWindow workBenchWindow = workBench == null ? null : workBench.getActiveWorkbenchWindow();
		return workBenchWindow == null ? null : workBenchWindow.getShell();
	}
}
