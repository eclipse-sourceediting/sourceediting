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
 * @author childsb
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
