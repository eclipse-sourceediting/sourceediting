/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.style.java;

import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

/**
 * Colors used in the Java editor
 */
public class JavaColorProvider {
	
	// people should not be setting these, even though they are currently not final
	public static RGB MULTI_LINE_COMMENT = new RGB(128, 0, 0);
	public static RGB SINGLE_LINE_COMMENT = new RGB(128, 128, 0);
	public static RGB KEYWORD = new RGB(0, 0, 128);
	public static RGB TYPE = new RGB(0, 0, 128);
	public static RGB STRING = new RGB(0, 128, 0);
	public static RGB DEFAULT = new RGB(0, 0, 0);
	public static RGB JAVADOC_KEYWORD = new RGB(0, 128, 0);
	public static RGB JAVADOC_TAG = new RGB(128, 128, 128);
	public static RGB JAVADOC_LINK = new RGB(128, 128, 128);
	public static RGB JAVADOC_DEFAULT = new RGB(0, 128, 128);
	
	public static int MULTI_LINE_COMMENT_BOLD = SWT.NORMAL;
	public static int SINGLE_LINE_COMMENT_BOLD = SWT.NORMAL;
	public static int KEYWORD_BOLD = SWT.BOLD;
	public static int TYPE_BOLD = SWT.BOLD;
	public static int STRING_BOLD = SWT.NORMAL;
	public static int DEFAULT_BOLD = SWT.NORMAL;
	public static int JAVADOC_KEYWORD_BOLD = SWT.BOLD;
	public static int JAVADOC_TAG_BOLD = SWT.NORMAL;
	public static int JAVADOC_LINK_BOLD = SWT.NORMAL;
	public static int JAVADOC_DEFAULT_BOLD = SWT.NORMAL;
	
	private static JavaColorProvider fInstance = null;
	
	public static JavaColorProvider getInstance() {
		if (fInstance == null) {
			fInstance = new JavaColorProvider();
		}
		return fInstance;
	}
	
	/**
	 * Use colors from JDT plugin
	 */
	public void loadJavaColors() {
		IPreferenceStore jdtStore = PreferenceConstants.getPreferenceStore();
		MULTI_LINE_COMMENT = PreferenceConverter.getColor(jdtStore, PreferenceConstants.EDITOR_MULTI_LINE_COMMENT_COLOR);
		SINGLE_LINE_COMMENT = PreferenceConverter.getColor(jdtStore, PreferenceConstants.EDITOR_SINGLE_LINE_COMMENT_COLOR);
		KEYWORD = PreferenceConverter.getColor(jdtStore, PreferenceConstants.EDITOR_JAVA_KEYWORD_COLOR);
		TYPE = KEYWORD;
		STRING = PreferenceConverter.getColor(jdtStore, PreferenceConstants.EDITOR_STRING_COLOR);
		DEFAULT = PreferenceConverter.getColor(jdtStore, PreferenceConstants.EDITOR_JAVA_DEFAULT_COLOR);
		JAVADOC_KEYWORD = PreferenceConverter.getColor(jdtStore, PreferenceConstants.EDITOR_JAVADOC_KEYWORD_COLOR);
		JAVADOC_TAG = PreferenceConverter.getColor(jdtStore, PreferenceConstants.EDITOR_JAVADOC_TAG_COLOR);
		JAVADOC_LINK = PreferenceConverter.getColor(jdtStore, PreferenceConstants.EDITOR_JAVADOC_LINKS_COLOR);
		JAVADOC_DEFAULT = PreferenceConverter.getColor(jdtStore, PreferenceConstants.EDITOR_JAVADOC_DEFAULT_COLOR);
		
		MULTI_LINE_COMMENT_BOLD = jdtStore.getBoolean(PreferenceConstants.EDITOR_MULTI_LINE_COMMENT_BOLD) ? SWT.BOLD : SWT.NORMAL;
		SINGLE_LINE_COMMENT_BOLD = jdtStore.getBoolean(PreferenceConstants.EDITOR_SINGLE_LINE_COMMENT_BOLD) ? SWT.BOLD : SWT.NORMAL;
		KEYWORD_BOLD = jdtStore.getBoolean(PreferenceConstants.EDITOR_JAVA_KEYWORD_BOLD) ? SWT.BOLD : SWT.NORMAL;
		TYPE_BOLD = jdtStore.getBoolean(PreferenceConstants.EDITOR_JAVA_KEYWORD_BOLD) ? SWT.BOLD : SWT.NORMAL;
		STRING_BOLD = jdtStore.getBoolean(PreferenceConstants.EDITOR_STRING_BOLD) ? SWT.BOLD : SWT.NORMAL;
		DEFAULT_BOLD = jdtStore.getBoolean(PreferenceConstants.EDITOR_JAVA_DEFAULT_BOLD) ? SWT.BOLD : SWT.NORMAL;
		JAVADOC_TAG_BOLD = jdtStore.getBoolean(PreferenceConstants.EDITOR_JAVADOC_TAG_BOLD) ? SWT.BOLD : SWT.NORMAL;
		JAVADOC_LINK_BOLD = jdtStore.getBoolean(PreferenceConstants.EDITOR_JAVADOC_LINKS_BOLD) ? SWT.BOLD : SWT.NORMAL;
		JAVADOC_DEFAULT_BOLD = jdtStore.getBoolean(PreferenceConstants.EDITOR_JAVADOC_DEFAULT_BOLD) ? SWT.BOLD : SWT.NORMAL;
	}
}
