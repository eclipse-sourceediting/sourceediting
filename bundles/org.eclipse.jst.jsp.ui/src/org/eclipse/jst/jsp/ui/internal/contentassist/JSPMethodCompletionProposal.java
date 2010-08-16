/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.contentassist;

import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedModeUI.ExitFlags;
import org.eclipse.jface.text.link.LinkedModeUI.IExitPolicy;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;

public class JSPMethodCompletionProposal extends JSPCompletionProposal {

	private CompletionProposal fProposal;
	private IJavaProject fProject;
	private FormatterPrefs fFormatterPrefs;

	private static final String LPAREN = "("; //$NON-NLS-1$
	private static final String RPAREN = ")"; //$NON-NLS-1$
	private static final String COMMA = ","; //$NON-NLS-1$
	private static final String SPACE = " "; //$NON-NLS-1$

	private IRegion fSelectedRegion;

	public JSPMethodCompletionProposal(CompletionProposal proposal, IJavaProject project, String replacementString, int replacementOffset, int replacementLength, int cursorPosition, Image image, String displayString, IContextInformation contextInformation, String additionalProposalInfo, int relevance, boolean updateReplacementLengthOnValidate) {
		super(replacementString, replacementOffset, replacementLength, cursorPosition, image, displayString, contextInformation, additionalProposalInfo, relevance, updateReplacementLengthOnValidate);
		fProposal = proposal;
		fProject = project;
	}

	public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {
		Position[] positions = null;
		FormatterPrefs formatterPrefs = getFormatterPrefs();
		StringBuffer buffer = new StringBuffer();

		buffer.append(fProposal.getName());

		if (formatterPrefs.beforeOpeningParen)
			buffer.append(SPACE);

		buffer.append(LPAREN);

		if (formatterPrefs.afterOpeningParen)
			buffer.append(SPACE);

		if (Signature.getParameterCount(fProposal.getSignature()) > 0) {
			char[][] parameters = fProposal.findParameterNames(null);
			if (parameters != null) {
				positions = new Position[parameters.length];
				
				int base = getReplacementOffset();
				for (int i = 0; i < parameters.length; i++) {
					if (i != 0) {
						if (formatterPrefs.beforeComma)
							buffer.append(SPACE);

						buffer.append(COMMA);

						if (formatterPrefs.afterComma)
							buffer.append(SPACE);
					}
					Position position = new Position(0,0);
					position.setOffset(base + buffer.length());
					position.setLength(parameters[i].length);
					positions[i] = position;
					buffer.append(parameters[i]);
				}
			}
		}

		if (formatterPrefs.beforeClosingParen)
			buffer.append(SPACE);

		buffer.append(RPAREN);
		setReplacementString(buffer.toString());

		super.apply(viewer, trigger, stateMask, offset);

		try {
			if (positions != null) {
				LinkedModeModel model = new LinkedModeModel();
	
				for (int i = 0; i < positions.length; i++) {
					LinkedPositionGroup group = new LinkedPositionGroup();
					Position position = positions[i];
					group.addPosition(new LinkedPosition(viewer.getDocument(), position.getOffset(), position.getLength(), i));
					model.addGroup(group);
				}
				model.forceInstall();

				LinkedModeUI ui = new EditorLinkedModeUI(model, viewer);
				ui.setExitPosition(viewer, getReplacementOffset() + getReplacementString().length(), 0, Integer.MAX_VALUE);
				ui.setExitPolicy(new ExitPolicy(')', viewer.getDocument()));
				ui.setCyclingMode(LinkedModeUI.CYCLE_WHEN_NO_PARENT);
				ui.setDoContextInfo(true);
				ui.enter();
				fSelectedRegion = ui.getSelectedRegion();
			}
		}
		catch (BadLocationException e) {
			
		}
	}

	protected final FormatterPrefs getFormatterPrefs() {
		if (fFormatterPrefs == null)
			fFormatterPrefs= new FormatterPrefs(fProject);
		return fFormatterPrefs;
	}

	/* Copied from org.eclipse.jdt.internal.ui.text.java.AbstractJavaCompletionProposal.ExitPolicy */
	protected static final class ExitPolicy implements IExitPolicy {

		final char fExitCharacter;
		private final IDocument fDocument;

		public ExitPolicy(char exitCharacter, IDocument document) {
			fExitCharacter= exitCharacter;
			fDocument= document;
		}

		/*
		 * @see org.eclipse.jdt.internal.ui.text.link.LinkedPositionUI.ExitPolicy#doExit(org.eclipse.jdt.internal.ui.text.link.LinkedPositionManager, org.eclipse.swt.events.VerifyEvent, int, int)
		 */
		public ExitFlags doExit(LinkedModeModel environment, VerifyEvent event, int offset, int length) {

			if (event.character == fExitCharacter) {
				if (environment.anyPositionContains(offset))
					return new ExitFlags(ILinkedModeListener.UPDATE_CARET, false);
				else
					return new ExitFlags(ILinkedModeListener.UPDATE_CARET, true);
			}

			switch (event.character) {
				case ';':
					return new ExitFlags(ILinkedModeListener.NONE, true);
				case SWT.CR:
					// when entering an anonymous class as a parameter, we don't want
					// to jump after the parenthesis when return is pressed
					if (offset > 0) {
						try {
							if (fDocument.getChar(offset - 1) == '{')
								return new ExitFlags(ILinkedModeListener.EXIT_ALL, true);
						} catch (BadLocationException e) {
						}
					}
					return null;
				default:
					return null;
			}
		}

	}

	/* Copied from org.eclipse.jdt.internal.ui.text.java.LazyJavaCompletionProposal.FormatterPrefs */
	protected static final class FormatterPrefs {
		/* Methods & constructors */
		public final boolean beforeOpeningParen;
		public final boolean afterOpeningParen;
		public final boolean beforeComma;
		public final boolean afterComma;
		public final boolean beforeClosingParen;
		public final boolean inEmptyList;

		/* type parameters */
		public final boolean beforeOpeningBracket;
		public final boolean afterOpeningBracket;
		public final boolean beforeTypeArgumentComma;
		public final boolean afterTypeArgumentComma;
		public final boolean beforeClosingBracket;

		FormatterPrefs(IJavaProject project) {
			beforeOpeningParen = getCoreOption(project, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_METHOD_INVOCATION, false);
			afterOpeningParen = getCoreOption(project, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_METHOD_INVOCATION, false);
			beforeComma = getCoreOption(project, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_METHOD_INVOCATION_ARGUMENTS, false);
			afterComma = getCoreOption(project, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_METHOD_INVOCATION_ARGUMENTS, true);
			beforeClosingParen = getCoreOption(project, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_METHOD_INVOCATION, false);
			inEmptyList = getCoreOption(project, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_METHOD_INVOCATION, false);

			beforeOpeningBracket = getCoreOption(project, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_ANGLE_BRACKET_IN_PARAMETERIZED_TYPE_REFERENCE, false);
			afterOpeningBracket = getCoreOption(project, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_ANGLE_BRACKET_IN_PARAMETERIZED_TYPE_REFERENCE, false);
			beforeTypeArgumentComma = getCoreOption(project, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_PARAMETERIZED_TYPE_REFERENCE, false);
			afterTypeArgumentComma = getCoreOption(project, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_PARAMETERIZED_TYPE_REFERENCE, true);
			beforeClosingBracket = getCoreOption(project, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_ANGLE_BRACKET_IN_PARAMETERIZED_TYPE_REFERENCE, false);
		}

		protected final boolean getCoreOption(IJavaProject project, String key, boolean def) {
			String option = getCoreOption(project, key);
			if (JavaCore.INSERT.equals(option))
				return true;
			if (JavaCore.DO_NOT_INSERT.equals(option))
				return false;
			return def;
		}

		protected final String getCoreOption(IJavaProject project, String key) {
			if (project == null)
				return JavaCore.getOption(key);
			return project.getOption(key, true);
		}
	}

	public Point getSelection(IDocument document) {
		if (fSelectedRegion != null) {
			return new Point(fSelectedRegion.getOffset(), fSelectedRegion.getLength());
		}
		return super.getSelection(document);
	}
}
