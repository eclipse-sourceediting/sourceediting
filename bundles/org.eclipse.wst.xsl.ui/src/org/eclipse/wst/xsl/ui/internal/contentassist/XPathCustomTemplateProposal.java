/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - STAR - bug 213849 - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xsl.ui.internal.contentassist;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.jface.text.templates.GlobalTemplateVariables;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.jface.text.templates.TemplateProposal;
import org.eclipse.jface.text.templates.TemplateVariable;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.sse.ui.internal.contentassist.IRelevanceCompletionProposal;

/**
 * Purpose of this class is to make the additional proposal info into content
 * fit for an HTML viewer (by escaping characters)
 */
@SuppressWarnings("restriction")
class XPathCustomTemplateProposal extends TemplateProposal implements IRelevanceCompletionProposal {
	// copies of this class exist in:
	// org.eclipse.jst.jsp.ui.internal.contentassist
	// org.eclipse.wst.html.ui.internal.contentassist
	// org.eclipse.wst.xml.ui.internal.contentassist
	// org.eclipse.wst.xsl.ui.internal.contentassist

	private String fDisplayString = null;
	private final Template fTemplate;
	private IRegion fSelectedRegion; // initialized by apply()
	private final TemplateContext fContext;
	private IRegion fRegion; 
	private InclusivePositionUpdater fUpdater;

	
	public XPathCustomTemplateProposal(Template template, TemplateContext context, IRegion region, Image image, int relevance) {
		super(template, context, region, image, relevance);
		
			Assert.isNotNull(template);
			Assert.isNotNull(context);
			Assert.isNotNull(region);

			fTemplate= template;
			fDisplayString= null;
			fContext = context;
			fRegion = region;
	}

	public String getAdditionalProposalInfo() {
		String additionalInfo = "Description:\r\n" + fTemplate.getDescription(); 
		return additionalInfo;
	}
	
	/*
	 * @see ICompletionProposal#getDisplayString()
	 */
	public String getDisplayString() {
		if (fDisplayString == null) {
			fDisplayString = fTemplate.getName();
		}
		return fDisplayString;
	}
	
	/**
	 * @param viewer 
	 * @param trigger 
	 * @param stateMask 
	 * @param offset 
	 * 
	 */
	public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) { 

	    IDocument document = viewer.getDocument();
	
		try {
			fContext.setReadOnly(false);
			int start;
			int oldReplaceOffset= getReplaceOffset();
			TemplateBuffer templateBuffer = fContext.evaluate(fTemplate);

			start = offset;
			int end = Math.max(getReplaceEndOffset(), offset);

			// insert template string
			String templateString= templateBuffer.getString();
			document.replace(start, end - start, templateString);
			
			// translate positions
			LinkedModeModel model= new LinkedModeModel();
			TemplateVariable[] variables= templateBuffer.getVariables();
			boolean hasPositions= false;
			for (int i= 0; i != variables.length; i++) {
				TemplateVariable variable= variables[i];

//				if (variable.isUnambiguous())
//					continue;

				LinkedPositionGroup group= new LinkedPositionGroup();

				int[] offsets= variable.getOffsets();
				int length= variable.getLength();

				LinkedPosition first = getFirst(variable, start, variable.getOffsets(), length, document);

				for (int j= 0; j != offsets.length; j++)
					if (j == 0)
						group.addPosition(first);
					else
						group.addPosition(new LinkedPosition(document, offsets[j] + start, length));

				model.addGroup(group);
				hasPositions= true;
			}

			if (hasPositions) {
				model.forceInstall();
				LinkedModeUI ui= new LinkedModeUI(model, viewer);
				ui.setExitPosition(viewer, getCaretOffset(templateBuffer) + start, 0, Integer.MAX_VALUE);
				ui.enter();

				fSelectedRegion= ui.getSelectedRegion();
			} else {
				ensurePositionCategoryRemoved(document);
				fSelectedRegion= new Region(getCaretOffset(templateBuffer) + start, 0);
			}
			
		} catch (BadLocationException ex) {
			fSelectedRegion = fRegion;
//		} catch (BadPositionCategoryException bex) {
//			fSelectedRegion = fRegion;
		} catch (TemplateException tex) {
			fSelectedRegion = fRegion;
		}

	}
	
	private LinkedPosition getFirst(TemplateVariable variable, int start, int[] offsets, int length, IDocument document ) {
		String[] values= variable.getValues();
		LinkedPosition first;
		ICompletionProposal[] proposals= new ICompletionProposal[values.length];
		try {
			for (int j= 0; j < values.length; j++) {
				//ensurePositionCategoryInstalled(document, model);
				Position pos= new Position(offsets[0] + start, length);
				document.addPosition(pos);
				//document.addPosition(getCategory(), pos);
				proposals[j]= new PositionBasedCompletionProposal(values[j], pos, length);
			}
		} catch (BadLocationException ex) {
			
		}
		
		if (proposals.length > 1)
			first= new ProposalPosition(document, offsets[0] + start, length, proposals);
		else
			first= new LinkedPosition(document, offsets[0] + start, length);
		return first;
	}
	
	private void ensurePositionCategoryInstalled(final IDocument document, LinkedModeModel model) {
		if (!document.containsPositionCategory(getCategory())) {
			document.addPositionCategory(getCategory());
			fUpdater= new InclusivePositionUpdater(getCategory());
			document.addPositionUpdater(fUpdater);

			model.addLinkingListener(new ILinkedModeListener() {

				/*
				 * @see org.eclipse.jface.text.link.ILinkedModeListener#left(org.eclipse.jface.text.link.LinkedModeModel, int)
				 */
				public void left(LinkedModeModel environment, int flags) {
					ensurePositionCategoryRemoved(document);
				}

				public void suspend(LinkedModeModel environment) {}
				public void resume(LinkedModeModel environment, int flags) {}
			});
		}
	}

	private void ensurePositionCategoryRemoved(IDocument document) {
		if (document.containsPositionCategory(getCategory())) {
			try {
				document.removePositionCategory(getCategory());
			} catch (BadPositionCategoryException e) {
				// ignore
			}
			document.removePositionUpdater(fUpdater);
		}
	}

	private String getCategory() {
		return "TemplateProposalCategory_TemplateProposal"; //$NON-NLS-1$
	}

	private int getCaretOffset(TemplateBuffer buffer) {

	    TemplateVariable[] variables= buffer.getVariables();
		for (int i= 0; i != variables.length; i++) {
			TemplateVariable variable= variables[i];
			if (variable.getType().equals(GlobalTemplateVariables.Cursor.NAME))
				return variable.getOffsets()[0];
		}

		return buffer.getString().length();
	}
	
}
