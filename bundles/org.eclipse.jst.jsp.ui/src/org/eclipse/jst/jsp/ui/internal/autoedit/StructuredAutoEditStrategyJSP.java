/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.autoedit;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.jst.jsp.ui.internal.Logger;
import org.eclipse.jst.jsp.ui.internal.preferences.JSPUIPreferenceNames;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorExtension3;
import org.eclipse.wst.html.ui.internal.HTMLUIPlugin;
import org.eclipse.wst.html.ui.internal.preferences.HTMLUIPreferenceNames;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.w3c.dom.Node;

public class StructuredAutoEditStrategyJSP implements IAutoEditStrategy {
	public void customizeDocumentCommand(IDocument document, DocumentCommand command) {
		Object textEditor = getActiveTextEditor();
		if (!(textEditor instanceof ITextEditorExtension3 && ((ITextEditorExtension3) textEditor).getInsertMode() == ITextEditorExtension3.SMART_INSERT))
			return;

		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager().getExistingModelForRead(document);

			if (model != null) {
				if (command.text != null) {
					smartInsertForEndTag(command, document, model);
					smartRemoveEndTag(command, document, model);
					if (command.text.equals("%") && isPreferenceEnabled(JSPUIPreferenceNames.TYPING_COMPLETE_SCRIPTLETS)) { //$NON-NLS-1$
						// scriptlet - add end %>
						IDOMNode node = (IDOMNode) model.getIndexedRegion(command.offset);
						if (node != null && prefixedWith(document, command.offset, "<") && !node.getSource().endsWith("%>")) { //$NON-NLS-1$ //$NON-NLS-2$
							command.text += " %>"; //$NON-NLS-1$
							command.shiftsCaret = false;
							command.caretOffset = command.offset + 1;
							command.doit = false;
						}
					}
					if (command.text.equals("{") && isPreferenceEnabled(JSPUIPreferenceNames.TYPING_COMPLETE_EL_BRACES)) { //$NON-NLS-1$
						IDOMNode node = (IDOMNode) model.getIndexedRegion(command.offset);
						if (node != null && (prefixedWith(document, command.offset, "$") || prefixedWith(document, command.offset, "#")) && //$NON-NLS-1$ //$NON-NLS-2$
									!node.getSource().endsWith("}")) { //$NON-NLS-1$ //$NON-NLS-2$
							command.text += " }"; //$NON-NLS-1$
							command.shiftsCaret = false;
							command.caretOffset = command.offset + 1;
							command.doit = false;
						}
					}
				}
			}
		}
		finally {
			if (model != null)
				model.releaseFromRead();
		}
	}
	
	private boolean isPreferenceEnabled(String key) {
		return (key != null && JSPUIPlugin.getDefault().getPreferenceStore().getBoolean(key));
	}

	/**
	 * Return the active text editor if possible, otherwise the active editor
	 * part.
	 * 
	 * @return
	 */
	private Object getActiveTextEditor() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				IEditorPart editor = page.getActiveEditor();
				if (editor != null) {
					if (editor instanceof ITextEditor)
						return editor;
					ITextEditor textEditor = (ITextEditor) editor.getAdapter(ITextEditor.class);
					if (textEditor != null)
						return textEditor;
					return editor;
				}
			}
		}
		return null;
	}

	private boolean prefixedWith(IDocument document, int offset, String string) {

		try {
			return document.getLength() >= string.length() && document.get(offset - string.length(), string.length()).equals(string);
		}
		catch (BadLocationException e) {
			Logger.logException(e);
			return false;
		}
	}
	
	private boolean isCommentNode(IDOMNode node) {
		return ((node != null) && (node instanceof IDOMElement) && ((IDOMElement) node).isCommentTag());
	}

	private boolean isDocumentNode(IDOMNode node) {
		return ((node != null) && (node.getNodeType() == Node.DOCUMENT_NODE));
	}
	
	private void smartInsertForEndTag(DocumentCommand command, IDocument document, IStructuredModel model) {
		try {
			if (command.text.equals("/") && (document.getLength() >= 1) && document.get(command.offset - 1, 1).equals("<") && HTMLUIPlugin.getDefault().getPreferenceStore().getBoolean(HTMLUIPreferenceNames.TYPING_COMPLETE_END_TAGS)) { //$NON-NLS-1$ //$NON-NLS-2$
				IDOMNode parentNode = (IDOMNode) ((IDOMNode) model.getIndexedRegion(command.offset - 1)).getParentNode();
				if (isCommentNode(parentNode)) {
					// loop and find non comment node parent
					while ((parentNode != null) && isCommentNode(parentNode)) {
						parentNode = (IDOMNode) parentNode.getParentNode();
					}
				}

				if (!isDocumentNode(parentNode)) {
					// only add end tag if one does not already exist or if
					// add '/' does not create one already
					IStructuredDocumentRegion endTagStructuredDocumentRegion = parentNode.getEndStructuredDocumentRegion();
					IDOMNode ancestor = parentNode;
					boolean smartInsertForEnd = false;
					if(endTagStructuredDocumentRegion != null) {
						// Look for ancestors by the same name that are missing end tags
						while((ancestor = (IDOMNode) ancestor.getParentNode()) != null) {
							if(ancestor.getEndStructuredDocumentRegion() == null && parentNode.getNodeName().equals(ancestor.getNodeName())) {
								smartInsertForEnd = true;
								break;
							}
						}
					}
					if (endTagStructuredDocumentRegion == null || smartInsertForEnd) {
						StringBuffer toAdd = new StringBuffer(parentNode.getNodeName());
						if (toAdd.length() > 0) {
							toAdd.append(">"); //$NON-NLS-1$
							String suffix = toAdd.toString();
							if ((document.getLength() < command.offset + suffix.length()) || (!suffix.equals(document.get(command.offset, suffix.length())))) {
								command.text += suffix;
							}
						}
					}
				}
			}
		}
		catch (BadLocationException e) {
			Logger.logException(e);
		}
	}

	/**
	 * Attempts to clean up an end-tag if a start-tag is converted into an empty-element
	 * tag (e.g., <node />) and the original element was empty.
	 * 
	 * @param command the document command describing the change
	 * @param document the document that will be changed
	 * @param model the model based on the document
	 */
	private void smartRemoveEndTag(DocumentCommand command, IDocument document, IStructuredModel model) {
		try {
			// An opening tag is now a self-terminated end-tag
			if ("/".equals(command.text) && ">".equals(document.get(command.offset, 1)) && command.length == 0 && HTMLUIPlugin.getDefault().getPreferenceStore().getBoolean(HTMLUIPreferenceNames.TYPING_REMOVE_END_TAGS)) { //$NON-NLS-1$ //$NON-NLS-2$
				IDOMNode node = (IDOMNode) model.getIndexedRegion(command.offset);
				if (node != null && !node.hasChildNodes()) {
					IStructuredDocumentRegion region = node.getFirstStructuredDocumentRegion();
					if(region.getFirstRegion().getType() == DOMRegionContext.XML_TAG_OPEN && command.offset <= region.getEnd()) {
						
						/* if the region before the command offset is a an attribute value region
						 * check to see if it has both and opening and closing quote
						 */
						ITextRegion prevTextRegion = region.getRegionAtCharacterOffset(command.offset-1);
						boolean inUnclosedAttValueRegion = false;
						if(prevTextRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
							//get the text of the attribute value region
							String prevText = region.getText(prevTextRegion);
							inUnclosedAttValueRegion = (prevText.startsWith("'") && ((prevText.length() == 1) || !prevText.endsWith("'"))) ||
								(prevText.startsWith("\"") && ((prevText.length() == 1) || !prevText.endsWith("\"")));
						} 
					
						//if command offset is in an unclosed attribute value region then done remove the end tag
						if(!inUnclosedAttValueRegion) {
							region = node.getEndStructuredDocumentRegion();
							if (region != null && region.isEnded()) {
								document.replace(region.getStartOffset(), region.getLength(), ""); //$NON-NLS-1$
							}
						}
					}
				}
			}
		}
		catch (BadLocationException e) {
			Logger.logException(e);
		}
	}
}
