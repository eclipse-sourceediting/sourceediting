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
package org.eclipse.wst.xml.ui.tests.breakpoints;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.wst.sse.ui.extension.IExtendedEditorAction;
import org.eclipse.wst.sse.ui.extension.IExtendedSimpleEditor;
import org.eclipse.wst.xml.ui.tests.SSEForXMLTestsPlugin;


public class EnableWarningAction extends Action implements IExtendedEditorAction {
	public EnableWarningAction() {
		super();
		SSEForXMLTestsPlugin.getDefault().getPreferenceStore().setDefault("break-error", false);
		boolean enabled = SSEForXMLTestsPlugin.getDefault().getPreferenceStore().getBoolean("break-error");
		if(enabled)
			setText("Disable Breakpoint Errors");
		else
			setText("Enable Breakpoint Errors");
	}

	/**
	 * @param text
	 */
	public EnableWarningAction(String text) {
		SSEForXMLTestsPlugin.getDefault().getPreferenceStore().setDefault("break-error", false);
		boolean enabled = SSEForXMLTestsPlugin.getDefault().getPreferenceStore().getBoolean("break-error");
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
		SSEForXMLTestsPlugin.getDefault().getPreferenceStore().setDefault("break-error", false);
		boolean enabled = SSEForXMLTestsPlugin.getDefault().getPreferenceStore().getBoolean("break-error");
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
		SSEForXMLTestsPlugin.getDefault().getPreferenceStore().setDefault("break-error", false);
		boolean enabled = SSEForXMLTestsPlugin.getDefault().getPreferenceStore().getBoolean("break-error");
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
		SSEForXMLTestsPlugin.getDefault().getPreferenceStore().setDefault("break-error", false);
		boolean value = SSEForXMLTestsPlugin.getDefault().getPreferenceStore().getBoolean("break-error");
		SSEForXMLTestsPlugin.getDefault().getPreferenceStore().setValue("break-error", !value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.extension.IExtendedEditorAction#setActiveExtendedEditor(org.eclipse.wst.sse.ui.extension.IExtendedSimpleEditor)
	 */
	public void setActiveExtendedEditor(IExtendedSimpleEditor targetEditor) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.IUpdate#update()
	 */
	public void update() {
		SSEForXMLTestsPlugin.getDefault().getPreferenceStore().setDefault("break-error", false);
		boolean enabled = SSEForXMLTestsPlugin.getDefault().getPreferenceStore().getBoolean("break-error");
		if(enabled)
			setText("Disable Breakpoint Errors");
		else
			setText("Enable Breakpoint Errors");
	}
}