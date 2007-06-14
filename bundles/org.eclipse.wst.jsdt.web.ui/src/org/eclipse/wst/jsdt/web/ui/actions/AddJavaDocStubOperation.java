/**
 * 
 */
package org.eclipse.wst.jsdt.web.ui.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.projection.ProjectionDocument;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.wst.jsdt.core.ICompilationUnit;
import org.eclipse.wst.jsdt.core.IMember;
import org.eclipse.wst.jsdt.internal.corext.codemanipulation.CodeGenerationMessages;
import org.eclipse.wst.jsdt.web.core.internal.java.DocumentChangeListenerToTextEdit;
import org.eclipse.wst.jsdt.web.core.internal.java.IJSPTranslation;
import org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslationExtension;
import org.eclipse.wst.jsdt.web.ui.views.contentoutline.JsJfaceNode;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;

/**
 * @author childsb
 * 
 */
public class AddJavaDocStubOperation extends org.eclipse.wst.jsdt.internal.corext.codemanipulation.AddJavaDocStubOperation {
	/**
	 * @param members
	 */
	private JsJfaceNode node;
	private IDocument copy;
	private DocumentChangeListenerToTextEdit textEditListener;
;
	
	
	public AddJavaDocStubOperation(IMember[] members, JsJfaceNode node) {
		super(members);
		this.node = node;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.internal.corext.codemanipulation.AddJavaDocStubOperation#getDocument(org.eclipse.wst.jsdt.core.ICompilationUnit,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IDocument getDocument(ICompilationUnit cu, IProgressMonitor monitor) throws CoreException {
		return getJavaDocumentFromNode();
	}
	
	protected IDocument getJavaDocumentFromNode() {
		if (copy == null) {
			JSPTranslationExtension tran = (JSPTranslationExtension) node.getTranslation();
			copy = new Document(tran.getJavaDocument().get());
			
			textEditListener = new DocumentChangeListenerToTextEdit();
			copy.addDocumentListener(textEditListener);
		}
		return copy;
	}
	
	public void run(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
		super.run(monitor);
		applyChanges();
		/* need to apply the text edits back to the original doc */
	}
	
	protected void applyChanges() {
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IStructuredModel model = null;
		IStructuredDocument doc = node.getStructuredDocument();
		try {
			MultiTextEdit edits = textEditListener.getTextEdits();
			
			JSPTranslationExtension tran = (JSPTranslationExtension) node.getTranslation();	
			TextEdit[] jspEdit = tran.getJspEdits(edits);

			model = modelManager.getExistingModelForEdit(doc);
			model.aboutToChangeModel();
			model.beginRecording(this, "Generate JsDoc", "Generate JsDoc");
			for(int i = 0;i<jspEdit.length;i++) {
				jspEdit[i].apply(doc);
			}
			
		} catch (MalformedTreeException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} catch (BadLocationException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} finally {
			if (model != null) {
				model.endRecording(this);
				model.changedModel();
				model.releaseFromEdit();
			}
		}
	}
}
