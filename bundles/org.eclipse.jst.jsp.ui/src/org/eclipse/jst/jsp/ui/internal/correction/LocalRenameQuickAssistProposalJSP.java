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
package org.eclipse.jst.jsp.ui.internal.correction;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.internal.corext.dom.LinkedNodeFinder;
import org.eclipse.jdt.internal.corext.dom.NodeFinder;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.text.correction.CorrectionMessages;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationUtil;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;
import org.eclipse.wst.sse.ui.StructuredTextViewer;

/**
 * This is a copy of
 * org.eclipse.jdt.internal.ui.text.correction.LinkedNamesAssistProposal with
 * the following modifications:
 * - set viewer redraw to true before apply to work around a repaint problem
 * - translate AST element offsets to JSP document offsets
 */
public class LocalRenameQuickAssistProposalJSP implements IJavaCompletionProposal, ICompletionProposalExtension2 {
	private SimpleName fNode;
	private IRegion fSelectedRegion; // initialized by apply()
	private ICompilationUnit fCompilationUnit;

	public LocalRenameQuickAssistProposalJSP(ICompilationUnit cu, SimpleName node) {
		fNode = node;
		fCompilationUnit = cu;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension2#apply(org.eclipse.jface.text.ITextViewer,
	 *      char, int, int)
	 */
	public void apply(ITextViewer viewer, char trigger, int stateMask, final int offset) {
		try {
			// get full ast
			CompilationUnit root = JavaPlugin.getDefault().getASTProvider().getAST(fCompilationUnit, true, null);

			ASTNode nameNode = NodeFinder.perform(root, fNode.getStartPosition(), fNode.getLength());
			final int pos = fNode.getStartPosition();

			ASTNode[] sameNodes;
			if (nameNode instanceof SimpleName) {
				sameNodes = LinkedNodeFinder.findByNode(root, (SimpleName) nameNode);
			}
			else {
				sameNodes = new ASTNode[]{nameNode};
			}

			// sort for iteration order, starting with the node @ offset
			Arrays.sort(sameNodes, new Comparator() {

				public int compare(Object o1, Object o2) {
					return rank((ASTNode) o1) - rank((ASTNode) o2);
				}

				/**
				 * Returns the absolute rank of an <code>ASTNode</code>.
				 * Nodes preceding <code>offset</code> are ranked last.
				 * 
				 * @param node
				 *            the node to compute the rank for
				 * @return the rank of the node with respect to the invocation
				 *         offset
				 */
				private int rank(ASTNode node) {
					int relativeRank = node.getStartPosition() + node.getLength() - pos;
					if (relativeRank < 0)
						return Integer.MAX_VALUE + relativeRank;
					else
						return relativeRank;
				}

			});

			// TODO CompletionProposalPopup#insertProposal() calls
			// IRewriteTarget.beginCompoundChange()
			//      which disables redraw in ITextViewer. Workaround for now.
			((StructuredTextViewer) viewer).setRedraw(true);

			IDocument document = viewer.getDocument();
			LinkedPositionGroup group = new LinkedPositionGroup();
			JSPTranslationUtil translationUtil = new JSPTranslationUtil(document);
			for (int i = 0; i < sameNodes.length; i++) {
				ASTNode elem = sameNodes[i];
				group.addPosition(new LinkedPosition(document, translationUtil.getTranslation().getJspOffset(elem.getStartPosition()), elem.getLength(), i));
			}

			LinkedModeModel linkedModeModel = new LinkedModeModel();
			linkedModeModel.addGroup(group);
			linkedModeModel.forceInstall();

			LinkedModeUI ui= new EditorLinkedModeUI(linkedModeModel, viewer);
			ui.setExitPosition(viewer, offset, 0, LinkedPositionGroup.NO_STOP);
			ui.enter();

			fSelectedRegion = ui.getSelectedRegion();
		}
		catch (BadLocationException e) {
			JavaPlugin.log(e);
		}
	}

	/*
	 * @see ICompletionProposal#apply(IDocument)
	 */
	public void apply(IDocument document) {
		// can't do anything
	}

	/*
	 * @see ICompletionProposal#getSelection(IDocument)
	 */
	public Point getSelection(IDocument document) {
		return new Point(fSelectedRegion.getOffset(), fSelectedRegion.getLength());
	}

	/*
	 * @see ICompletionProposal#getAdditionalProposalInfo()
	 */
	public String getAdditionalProposalInfo() {
		return CorrectionMessages.getString("LinkedNamesAssistProposal.proposalinfo"); //$NON-NLS-1$
	}

	/*
	 * @see ICompletionProposal#getDisplayString()
	 */
	public String getDisplayString() {
		return CorrectionMessages.getString("LinkedNamesAssistProposal.description"); //$NON-NLS-1$
	}

	/*
	 * @see ICompletionProposal#getImage()
	 */
	public Image getImage() {
		return JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_LOCAL);
	}

	/*
	 * @see ICompletionProposal#getContextInformation()
	 */
	public IContextInformation getContextInformation() {
		return null;
	}

	/*
	 * @see IJavaCompletionProposal#getRelevance()
	 */
	public int getRelevance() {
		return 1;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension2#selected(org.eclipse.jface.text.ITextViewer,
	 *      boolean)
	 */
	public void selected(ITextViewer textViewer, boolean smartToggle) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension2#unselected(org.eclipse.jface.text.ITextViewer)
	 */
	public void unselected(ITextViewer textViewer) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension2#validate(org.eclipse.jface.text.IDocument,
	 *      int, org.eclipse.jface.text.DocumentEvent)
	 */
	public boolean validate(IDocument document, int offset, DocumentEvent event) {
		return false;
	}
}