/**
 * 
 */
package org.eclipse.wst.jsdt.web.core.internal.java;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

/**
 * @author childsb
 *
 */
public class DocumentChangeListenerToTextEdit implements IDocumentListener{

	private MultiTextEdit textEdit;
	
	public DocumentChangeListenerToTextEdit() {
		textEdit = new MultiTextEdit();
	}
	public void documentAboutToBeChanged(DocumentEvent event) {
	
		//System.out.println("Unimplemented method:DocumentChangeListenerToTextEdit.documentAboutToBeChanged");
		
	}

	public void documentChanged(DocumentEvent event) {
		int length = event.getLength();
		int offset = event.getOffset();
		String text = event.getText();
		
		if(length<0) return;
		
		if(length==0) {
			/*  inserting text operation */
			InsertEdit edit = new InsertEdit(offset,text);
			textEdit.addChild(edit);
			System.out.println("-------------Insert Text Edit");
		}else if(text==null || text.equals("")) {
			/* delete operation */
			DeleteEdit edit = new DeleteEdit(offset,length);
			textEdit.addChild(edit);
			System.out.println("-------------Delete Text Edit");
		}else if(length>0) {
			/* replace text operation */
			ReplaceEdit edit = new ReplaceEdit(offset,length,text);
			textEdit.addChild(edit);
			System.out.println("-------------Replace Text Edit");
		}
		
	}
	
	public MultiTextEdit getTextEdits() {
		return textEdit;
	}

}
