/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.formatter;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.preferences.XMLCorePreferenceNames;

public class XMLFormattingPreferences {
	public final static String PRESERVE = XMLFormattingConstraints.PRESERVE;
	public final static String COLLAPSE = XMLFormattingConstraints.COLLAPSE;
	public final static String IGNORE = XMLFormattingConstraints.IGNORE;

	public final static String INDENT = XMLFormattingConstraints.INDENT;
	public final static String NEW_LINE = XMLFormattingConstraints.NEW_LINE;
	public final static String INLINE = XMLFormattingConstraints.INLINE;

	private int fMaxLineWidth = 72;
	private boolean fAlignFinalBracket = false;
	private boolean fSpaceBeforeEmptyCloseTag = true;
	private boolean fIndentMultipleAttributes = false;
	private boolean fFormatCommentText = true;
	private boolean fJoinCommentLines = false;

	private String fPCDataWhitespaceStrategy = XMLFormattingConstraints.PRESERVE;
	private String fTextIndentStrategy = XMLFormattingConstraints.INLINE;
	private String fTextWhitespaceStrategy = XMLFormattingConstraints.COLLAPSE;
	private String fElementIndentStrategy = XMLFormattingConstraints.INDENT;
	private String fElementWhitespaceStrategy = XMLFormattingConstraints.IGNORE;
	private String fMixedIndentStrategy = XMLFormattingConstraints.INDENT;
	private String fMixedWhitespaceStrategy = XMLFormattingConstraints.IGNORE;
	private String fOneIndent = "\t"; //$NON-NLS-1$
	private boolean fClearAllBlankLines = false;

	public XMLFormattingPreferences() {
		Preferences preferences = XMLCorePlugin.getDefault().getPluginPreferences();
		if (preferences != null) {
			setFormatCommentText(preferences.getBoolean(XMLCorePreferenceNames.FORMAT_COMMENT_TEXT));
			setJoinCommentLines(preferences.getBoolean(XMLCorePreferenceNames.FORMAT_COMMENT_JOIN_LINES));

			setMaxLineWidth(preferences.getInt(XMLCorePreferenceNames.LINE_WIDTH));
			setIndentMultipleAttributes(preferences.getBoolean(XMLCorePreferenceNames.SPLIT_MULTI_ATTRS));
			setAlignFinalBracket(preferences.getBoolean(XMLCorePreferenceNames.ALIGN_END_BRACKET));
			setSpaceBeforeEmptyCloseTag(preferences.getBoolean(XMLCorePreferenceNames.SPACE_BEFORE_EMPTY_CLOSE_TAG));
			
			boolean preservepcdata = preferences.getBoolean(XMLCorePreferenceNames.PRESERVE_CDATACONTENT);
			if (preservepcdata)
				fPCDataWhitespaceStrategy = XMLFormattingPreferences.PRESERVE;
			else
				fPCDataWhitespaceStrategy = XMLFormattingPreferences.COLLAPSE;

			char indentChar = ' ';
			String indentCharPref = preferences.getString(XMLCorePreferenceNames.INDENTATION_CHAR);
			if (XMLCorePreferenceNames.TAB.equals(indentCharPref)) {
				indentChar = '\t';
			}
			int indentationWidth = preferences.getInt(XMLCorePreferenceNames.INDENTATION_SIZE);

			StringBuffer indent = new StringBuffer();
			for (int i = 0; i < indentationWidth; i++) {
				indent.append(indentChar);
			}
			setOneIndent(indent.toString());
			setClearAllBlankLines(preferences.getBoolean(XMLCorePreferenceNames.CLEAR_ALL_BLANK_LINES));
		}
	}

	public int getMaxLineWidth() {
		return fMaxLineWidth;
	}

	public boolean getFormatCommentText() {
		return fFormatCommentText;
	}

	public boolean getAlignFinalBracket() {
		return fAlignFinalBracket;
	}

	public boolean getSpaceBeforeEmptyCloseTag() {
		return fSpaceBeforeEmptyCloseTag;
	}
	
	public boolean getIndentMultipleAttributes() {
		return fIndentMultipleAttributes;
	}

	public String getPCDataWhitespaceStrategy() {
		return fPCDataWhitespaceStrategy;
	}

	public String getTextIndentStrategy() {
		return fTextIndentStrategy;
	}

	public String getTextWhitespaceStrategy() {
		return fTextWhitespaceStrategy;
	}

	public String getElementIndentStrategy() {
		return fElementIndentStrategy;
	}

	public String getElementWhitespaceStrategy() {
		return fElementWhitespaceStrategy;
	}
	
	public boolean getJoinCommentLines() {
		return fJoinCommentLines;
	}

	public void setJoinCommentLines(boolean joinCommentLines) {
		fJoinCommentLines = joinCommentLines;
	}

	public void setFormatCommentText(boolean formatCommentText) {
		fFormatCommentText = formatCommentText;
	}

	public void setSpaceBeforeEmptyCloseTag(boolean spaceBeforeEmptyCloseTag) {
		fSpaceBeforeEmptyCloseTag = spaceBeforeEmptyCloseTag;
	}

	public void setIndentMultipleAttributes(boolean indentMultipleAttributes) {
		fIndentMultipleAttributes = indentMultipleAttributes;
	}

	public void setPCDataWhitespaceStrategy(String dataWhitespaceStrategy) {
		fPCDataWhitespaceStrategy = dataWhitespaceStrategy;
	}

	public void setAlignFinalBracket(boolean alignFinalBracket) {
		fAlignFinalBracket = alignFinalBracket;
	}

	public String getMixedIndentStrategy() {
		return fMixedIndentStrategy;
	}

	public void setMixedIndentStrategy(String mixedIndentStrategy) {
		fMixedIndentStrategy = mixedIndentStrategy;
	}

	public String getMixedWhitespaceStrategy() {
		return fMixedWhitespaceStrategy;
	}

	public void setMixedWhitespaceStrategy(String mixedWhitespaceStrategy) {
		fMixedWhitespaceStrategy = mixedWhitespaceStrategy;
	}

	public String getOneIndent() {
		return fOneIndent;
	}

	protected void setMaxLineWidth(int maxLineWidth) {
		fMaxLineWidth = maxLineWidth;
	}

	protected void setOneIndent(String oneIndent) {
		fOneIndent = oneIndent;
	}

	public boolean getClearAllBlankLines() {
		return fClearAllBlankLines;
	}

	public void setClearAllBlankLines(boolean clearAllBlankLines) {
		fClearAllBlankLines = clearAllBlankLines;
	}
}
