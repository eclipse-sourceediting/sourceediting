/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.ui.tests.breakpoints;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.wst.sse.ui.internal.IExtendedEditorAction;
import org.eclipse.wst.sse.ui.internal.IExtendedSimpleEditor;
import org.eclipse.wst.xml.ui.tests.XMLUITestsPlugin;


public class EnableWarningAction extends Action implements IExtendedEditorAction {
	IExtendedSimpleEditor activeEditor;

	public EnableWarningAction() {
		super();
		XMLUITestsPlugin.getDefault().getPreferenceStore().setDefault("break-error", false);
		boolean enabled = XMLUITestsPlugin.getDefault().getPreferenceStore().getBoolean("break-error");
		if(enabled)
			setText("Disable Breakpoint Errors");
		else
			setText("Enable Breakpoint Errors");
	}

	/**
	 * @param text
	 */
	public EnableWarningAction(String text) {
		XMLUITestsPlugin.getDefault().getPreferenceStore().setDefault("break-error", false);
		boolean enabled = XMLUITestsPlugin.getDefault().getPreferenceStore().getBoolean("break-error");
		if(enabled)
			setText("Disable Breakpoint Errors");
		else
			setText("Enable Breakpoint Errors");
	}

	/**
	 * @param text
	 * @param image
	 */
	public EnableWarningAction(String text, ImageDescriptor image) {
		XMLUITestsPlugin.getDefault().getPreferenceStore().setDefault("break-error", false);
		boolean enabled = XMLUITestsPlugin.getDefault().getPreferenceStore().getBoolean("break-error");
		if(enabled)
			setText("Disable Breakpoint Errors");
		else
			setText("Enable Breakpoint Errors");
	}

	/**
	 * @param text
	 * @param style
	 */
	public EnableWarningAction(String text, int style) {
		XMLUITestsPlugin.getDefault().getPreferenceStore().setDefault("break-error", false);
		boolean enabled = XMLUITestsPlugin.getDefault().getPreferenceStore().getBoolean("break-error");
		if(enabled)
			setText("Disable Breakpoint Errors");
		else
			setText("Enable Breakpoint Errors");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.extension.IExtendedEditorAction#isVisible()
	 */
	public boolean isVisible() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		super.run();
		XMLUITestsPlugin.getDefault().getPreferenceStore().setDefault("break-error", false);
		boolean value = XMLUITestsPlugin.getDefault().getPreferenceStore().getBoolean("break-error");
		XMLUITestsPlugin.getDefault().getPreferenceStore().setValue("break-error", !value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.extension.IExtendedEditorAction#setActiveExtendedEditor(org.eclipse.wst.sse.ui.extension.IExtendedSimpleEditor)
	 */
	public void setActiveExtendedEditor(IExtendedSimpleEditor targetEditor) {
		activeEditor = targetEditor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.IUpdate#update()
	 */
	public void update() {
		XMLUITestsPlugin.getDefault().getPreferenceStore().setDefault("break-error", false);
		boolean enabled = XMLUITestsPlugin.getDefault().getPreferenceStore().getBoolean("break-error");
		if(enabled)
			setText("Disable Breakpoint Errors");
		else
			setText("Enable Breakpoint Errors");
	}
}
