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
/**
 * 
 */
package org.eclipse.wst.jsdt.web.core.javascript;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
* <br><br>
* 
* this class attaches to a "cloned" document, listens for changes to that document then translates
* the changes to text edits.  these changes can then be applied back to the original document.
* 
*
*/
public class DocumentChangeListenerToTextEdit implements IDocumentListener {
	private MultiTextEdit textEdit;
	
	public DocumentChangeListenerToTextEdit() {
		textEdit = new MultiTextEdit();
	}
	
	public void documentAboutToBeChanged(DocumentEvent event) {
	// System.out.println("Unimplemented
	// method:DocumentChangeListenerToTextEdit.documentAboutToBeChanged");
	}
	
	public void documentChanged(DocumentEvent event) {
		int length = event.getLength();
		int offset = event.getOffset();
		String text = event.getText();
		if (length < 0) {
			return;
		}
		if (length == 0) {
			/* inserting text operation */
			InsertEdit edit = new InsertEdit(offset, text);
			textEdit.addChild(edit);
			
		} else if (text == null || text.equals("")) { //$NON-NLS-1$
			/* delete operation */
			DeleteEdit edit = new DeleteEdit(offset, length);
			textEdit.addChild(edit);
			
		} else if (length > 0) {
			/* replace text operation */
			ReplaceEdit edit = new ReplaceEdit(offset, length, text);
			textEdit.addChild(edit);
		
		}
	}
	
	public MultiTextEdit getTextEdits() {
		return textEdit;
	}
}
