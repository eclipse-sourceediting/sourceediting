/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.ui.internal.correction;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.sse.ui.internal.correction.IQuickFixProcessor;
import org.eclipse.wst.sse.ui.internal.reconcile.TemporaryAnnotation;
import org.eclipse.wst.xml.ui.internal.editor.XMLEditorPluginImageHelper;
import org.eclipse.wst.xml.ui.internal.editor.XMLEditorPluginImages;
import org.eclipse.wst.xml.ui.nls.ResourceHandler;


public class QuickFixProcessorXML implements IQuickFixProcessor {

	/*
	 * (non-Javadoc)
	 * 
	 */
	public boolean canFix(Annotation annotation) {
		boolean result = false;

		if (annotation instanceof TemporaryAnnotation) {
			TemporaryAnnotation tempAnnotation = (TemporaryAnnotation) annotation;
			int problemID = tempAnnotation.getProblemID();
			switch (problemID) {
				case ProblemIDsXML.EmptyTag :
				case ProblemIDsXML.MissingEndTag :
				case ProblemIDsXML.AttrsInEndTag :
				case ProblemIDsXML.MissingAttrValue :
				case ProblemIDsXML.NoAttrValue :
				case ProblemIDsXML.SpacesBeforeTagName :
				case ProblemIDsXML.SpacesBeforePI :
				case ProblemIDsXML.NamespaceInPI :
				case ProblemIDsXML.UnknownElement :
				case ProblemIDsXML.UnknownAttr :
				case ProblemIDsXML.InvalidAttrValue :
				case ProblemIDsXML.MissingRequiredAttr :
				case ProblemIDsXML.AttrValueNotQuoted :
				case ProblemIDsXML.MissingClosingBracket :
					result = true;
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	public ICompletionProposal[] getProposals(Annotation annotation) throws CoreException {
		ArrayList proposals = new ArrayList();

		if (annotation instanceof TemporaryAnnotation) {
			TemporaryAnnotation tempAnnotation = (TemporaryAnnotation) annotation;
			int problemID = tempAnnotation.getProblemID();
			switch (problemID) {
				case ProblemIDsXML.EmptyTag :
					proposals.add(new CompletionProposal("", tempAnnotation.getPosition().getOffset(), tempAnnotation.getPosition().getLength(), 0, getImage(), ResourceHandler.getString("QuickFixProcessorXML.0"), null, "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					break;
				case ProblemIDsXML.MissingEndTag :
					String tagName = (String) ((Object[]) tempAnnotation.getAdditionalFixInfo())[0];
					String tagClose = (String) ((Object[]) tempAnnotation.getAdditionalFixInfo())[1];
					int tagCloseOffset = ((Integer) ((Object[]) tempAnnotation.getAdditionalFixInfo())[2]).intValue();
					int startTagEndOffset = ((Integer) ((Object[]) tempAnnotation.getAdditionalFixInfo())[3]).intValue();
					int firstChildStartOffset = ((Integer) ((Object[]) tempAnnotation.getAdditionalFixInfo())[4]).intValue();
					int endOffset = ((Integer) ((Object[]) tempAnnotation.getAdditionalFixInfo())[5]).intValue();
					proposals.add(new CompletionProposal(tagClose, tagCloseOffset, 0, 0, getImage(), ResourceHandler.getString("QuickFixProcessorXML.1"), null, "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					proposals.add(new CompletionProposal("", tempAnnotation.getPosition().getOffset(), startTagEndOffset - tempAnnotation.getPosition().getOffset(), 0, getImage(), ResourceHandler.getString("QuickFixProcessorXML.2"), null, "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					proposals.add(new CompletionProposal("</" + tagName + ">", firstChildStartOffset, 0, 0, getImage(), ResourceHandler.getString("QuickFixProcessorXML.3"), null, "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					proposals.add(new CompletionProposal("</" + tagName + ">", endOffset, 0, 0, getImage(), ResourceHandler.getString("QuickFixProcessorXML.4"), null, "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					break;
				case ProblemIDsXML.AttrsInEndTag :
					proposals.add(new CompletionProposal("", tempAnnotation.getPosition().getOffset(), tempAnnotation.getPosition().getLength(), 0, getImage(), ResourceHandler.getString("QuickFixProcessorXML.5"), null, "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					break;
				case ProblemIDsXML.MissingAttrValue :
					String defaultAttrValue = (String) ((Object[]) tempAnnotation.getAdditionalFixInfo())[0];
					int insertOffset = ((Integer) ((Object[]) tempAnnotation.getAdditionalFixInfo())[1]).intValue();
					proposals.add(new CompletionProposal("\"" + defaultAttrValue + "\"", tempAnnotation.getPosition().getOffset() + tempAnnotation.getPosition().getLength() + insertOffset, 0, defaultAttrValue.length() + 2, getImage(), ResourceHandler.getString("QuickFixProcessorXML.6"), null, "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					proposals.add(new CompletionProposal("", tempAnnotation.getPosition().getOffset(), tempAnnotation.getPosition().getLength(), 0, getImage(), ResourceHandler.getString("QuickFixProcessorXML.7"), null, "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					break;
				case ProblemIDsXML.NoAttrValue :
					defaultAttrValue = (String) tempAnnotation.getAdditionalFixInfo();
					proposals.add(new CompletionProposal("=\"" + defaultAttrValue + "\"", tempAnnotation.getPosition().getOffset() + tempAnnotation.getPosition().getLength(), 0, defaultAttrValue.length() + 3, getImage(), ResourceHandler.getString("QuickFixProcessorXML.6"), null, "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					proposals.add(new CompletionProposal("", tempAnnotation.getPosition().getOffset(), tempAnnotation.getPosition().getLength(), 0, getImage(), ResourceHandler.getString("QuickFixProcessorXML.7"), null, "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					break;
				case ProblemIDsXML.SpacesBeforeTagName :
					proposals.add(new CompletionProposal("", tempAnnotation.getPosition().getOffset(), tempAnnotation.getPosition().getLength(), 0, getImage(), ResourceHandler.getString("QuickFixProcessorXML.8"), null, "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					break;
				case ProblemIDsXML.SpacesBeforePI :
					proposals.add(new CompletionProposal("", tempAnnotation.getPosition().getOffset(), tempAnnotation.getPosition().getLength(), 0, getImage(), ResourceHandler.getString("QuickFixProcessorXML.9"), null, "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					break;
				case ProblemIDsXML.NamespaceInPI :
					proposals.add(new CompletionProposal("", tempAnnotation.getPosition().getOffset(), tempAnnotation.getPosition().getLength(), 0, getImage(), ResourceHandler.getString("QuickFixProcessorXML.10"), null, "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					break;
				case ProblemIDsXML.UnknownElement :
					proposals.add(new RemoveUnknownElementQuickFixProposal(tempAnnotation.getAdditionalFixInfo(), getImage(), ResourceHandler.getString("QuickFixProcessorXML.11"))); //$NON-NLS-1$
					proposals.add(new RenameInFileQuickAssistProposal());
					break;
				case ProblemIDsXML.UnknownAttr :
					proposals.add(new CompletionProposal("", tempAnnotation.getPosition().getOffset(), tempAnnotation.getPosition().getLength(), 0, getImage(), ResourceHandler.getString("QuickFixProcessorXML.7"), null, "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					proposals.add(new RenameInFileQuickAssistProposal());
					break;
				case ProblemIDsXML.InvalidAttrValue :
					proposals.add(new CompletionProposal("", tempAnnotation.getPosition().getOffset(), tempAnnotation.getPosition().getLength(), 0, getImage(), ResourceHandler.getString("QuickFixProcessorXML.12"), null, "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					break;
				case ProblemIDsXML.MissingRequiredAttr :
					String requiredAttr = (String) ((Object[]) tempAnnotation.getAdditionalFixInfo())[0];
					insertOffset = ((Integer) ((Object[]) tempAnnotation.getAdditionalFixInfo())[1]).intValue();
					proposals.add(new CompletionProposal(requiredAttr, tempAnnotation.getPosition().getOffset() + insertOffset, 0, requiredAttr.length(), getImage(), ResourceHandler.getString("QuickFixProcessorXML.13"), null, "")); //$NON-NLS-1$ //$NON-NLS-2$
					break;
				case ProblemIDsXML.AttrValueNotQuoted :
					String attrValue = (String) tempAnnotation.getAdditionalFixInfo();
					proposals.add(new CompletionProposal("\"" + attrValue + "\"", tempAnnotation.getPosition().getOffset(), tempAnnotation.getPosition().getLength(), attrValue.length() + 2, getImage(), ResourceHandler.getString("QuickFixProcessorXML.14"), null, "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					break;
				case ProblemIDsXML.MissingClosingBracket :
					proposals.add(new CompletionProposal(">", tempAnnotation.getPosition().getOffset() + tempAnnotation.getPosition().getLength(), 0, 1, getImage(), ResourceHandler.getString("QuickFixProcessorXML.15"), null, "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					break;
			}
		}

		return (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[proposals.size()]);
	}

	public Image getImage() {
		//return JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_CHANGE);
		return XMLEditorPluginImageHelper.getInstance().getImage(XMLEditorPluginImages.IMG_OBJ_CORRECTION_CHANGE);
	}
}
