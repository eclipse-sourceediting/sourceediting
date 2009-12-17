package org.eclipse.wst.xml.xpath.ui.internal.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocumentAdapter;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xml.core.internal.contentmodel.util.NamespaceInfo;
import org.eclipse.wst.xml.core.internal.contentmodel.util.NamespaceTable;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Document;

public class NamespaceSelectionAdapter extends SelectionAdapter {

	protected Map<Document, List<NamespaceInfo>> namespaceInfo;

	public NamespaceSelectionAdapter(Map<Document, List<NamespaceInfo>> namespaces) {
		namespaceInfo = namespaces;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		IEditorPart activeEditor = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		IFile file = (IFile) activeEditor.getEditorInput().getAdapter(
				IFile.class);
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IDOMModel model = null;
		try {
			model = (IDOMModel) modelManager.getModelForRead(file);
			IDOMDocument document = model.getDocument();

			if (document != null) {
				List<NamespaceInfo> info = createNamespaceInfo(document);

				IPathEditorInput editorInput = (IPathEditorInput) activeEditor
						.getEditorInput();

				EditNamespacePrefixDialog dlg = new EditNamespacePrefixDialog(
						activeEditor.getSite().getShell(), editorInput
								.getPath());
				dlg.setNamespaceInfoList(info);
				if (SWT.OK == dlg.open()) {
					// Apply changes
				}
			}
		} catch (Exception ex) {

		} finally {
			if (model != null) {
				model.releaseFromRead();
			}
		}
	}

	private List<NamespaceInfo> createNamespaceInfo(Document document) {
		List<NamespaceInfo> info = namespaceInfo.get(document);
		if (info == null) {
			info = new ArrayList<NamespaceInfo>();
			NamespaceTable namespaceTable = new NamespaceTable(document);
			namespaceTable.visitElement(document.getDocumentElement());
			Collection<?> namespaces = namespaceTable
					.getNamespaceInfoCollection();
			info.addAll((Collection<NamespaceInfo>) namespaces);
			namespaceInfo.put(document, info);
		}
		return info;
	}

}
