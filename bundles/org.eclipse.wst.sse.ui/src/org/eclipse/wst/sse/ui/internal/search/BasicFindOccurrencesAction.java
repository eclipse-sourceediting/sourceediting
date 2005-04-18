/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.ui.internal.search;

import java.util.ResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.ui.internal.StructuredTextEditor;


/**
 * <p>
 * Finds occurrences of a specified region type w/ region text in an
 * IStructuredDocument. Clients must implement getPartitionTypes() and
 * getRegionTypes() to indicate which partition types and region types it can
 * operate on.
 * </p>
 * 
 * <p>
 * Clients should override <code>getSearchQuery()</code> in order to provide
 * their own type of "search" (eg. searching for XML start tags, searching for
 * Java elements, etc...)
 * </p>
 * 
 * @author pavery
 */
public class BasicFindOccurrencesAction extends TextEditorAction {
	private IStructuredDocument fDocument = null;

	private IFile fFile = null;
	private String fMatchRegionType = null;
	private String fMatchText = null;

	public BasicFindOccurrencesAction(ResourceBundle bundle, String prefix, ITextEditor editor) {
		super(bundle, prefix, editor);
	}

	/**
	 * @param editor
	 * @param sdRegion
	 * @param r
	 * @param type
	 */
	private void configure(StructuredTextEditor editor, IStructuredDocumentRegion sdRegion, ITextRegion r, String type) {

		this.fFile = editor.getFileInEditor();
		this.fDocument = (IStructuredDocument) editor.getDocument();
		this.fMatchText = sdRegion.getText(r);
		this.fMatchRegionType = type;
	}

	/**
	 * @param partitionType
	 * @return <code>true</code> if this action can operate on this type of
	 *         partition, otherwise <code>false</code>.
	 */
	public boolean enabledForParitition(String partitionType) {

		String[] accept = getPartitionTypes();
		for (int i = 0; i < accept.length; i++) {
			if (partitionType.equals(accept[i]))
				return true;
		}
		return false;
	}

	/**
	 * @param regionType
	 * @return <code>true</code> if this action can operate on this region
	 *         type (ITextRegion), otherwise false.
	 */
	public boolean enabledForRegionType(String regionType) {

		String[] accept = getRegionTypes();
		for (int i = 0; i < accept.length; i++) {
			if (regionType.equals(accept[i]))
				return true;
		}
		return false;
	}

	/**
	 * Clients should override this to enable find occurrences on certain
	 * partition(s).
	 */
	protected String[] getPartitionTypes() {
		return new String[0];
	}

	/**
	 * Clients should override this to enable find occurrences on different
	 * region type(s).
	 */
	protected String[] getRegionTypes() {
		return new String[0];
	}

	/**
	 * Clients should override to provide their own search for the file.
	 *  
	 */
	public ISearchQuery getSearchQuery() {
		return new OccurrencesSearchQuery(this.fFile, this.fDocument, this.fMatchText, this.fMatchRegionType);
	}

	public void run() {

		if (this.fDocument != null && this.fMatchText != null && this.fMatchRegionType != null)
			NewSearchUI.runQuery(getSearchQuery());

		unconfigure();
	}

	private void unconfigure() {

		this.fFile = null;
		this.fDocument = null;
	}

	/**
	 * Enables and initialzies the action, or disables.
	 * 
	 * @see org.eclipse.ui.texteditor.TextEditorAction#update()
	 */
	public void update() {

		super.update();

		// determine if action should be enabled or not
		StructuredTextEditor editor = (StructuredTextEditor) getTextEditor();
		IStructuredDocumentRegion sdRegion = editor.getSelectedDocumentRegion();
		if (sdRegion != null) {

			ITextRegion r = editor.getSelectedTextRegion(sdRegion);
			if (r != null) {

				String type = r.getType();
				if (enabledForRegionType(type)) {
					configure(editor, sdRegion, r, type);
					setEnabled(true);
				} else {
					unconfigure();
					setEnabled(false);
				}
			}
		}
	}
}
