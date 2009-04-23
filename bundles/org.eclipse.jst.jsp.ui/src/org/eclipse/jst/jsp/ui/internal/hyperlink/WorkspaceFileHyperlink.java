package org.eclipse.jst.jsp.ui.internal.hyperlink;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.jst.jsp.ui.internal.Logger;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Hyperlink for files within the workspace. (As long as there is an IFile,
 * this can be used) Opens the default editor for the file.
 */
class WorkspaceFileHyperlink implements IHyperlink {
	// copies of this class exist in:
	// org.eclipse.wst.xml.ui.internal.hyperlink
	// org.eclipse.wst.html.ui.internal.hyperlink
	// org.eclipse.jst.jsp.ui.internal.hyperlink

	private IRegion fRegion;
	private IFile fFile;
	private IRegion fHighlightRange;

	public WorkspaceFileHyperlink(IRegion region, IFile file) {
		fRegion = region;
		fFile = file;
	}

	public WorkspaceFileHyperlink(IRegion region, IFile file, IRegion range) {
		fRegion = region;
		fFile = file;
		fHighlightRange = range;
	}

	public IRegion getHyperlinkRegion() {
		return fRegion;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.hyperlink.IHyperlink#getTypeLabel()
	 */
	public String getTypeLabel() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.hyperlink.IHyperlink#getHyperlinkText()
	 */
	public String getHyperlinkText() {
		String path = fFile.getFullPath().toString();
		if (path.length() > 60) {
			path = path.substring(0, 25) + "..." + path.substring(path.length() - 25, path.length());
		}
		return NLS.bind(JSPUIMessages.Open, path);
	}

	public void open() {
		if (fFile != null && fFile.exists()) {
			try {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IEditorPart editor = IDE.openEditor(page, fFile, true);
				// highlight range in editor if possible
				if (fHighlightRange != null && editor instanceof ITextEditor) {
					((ITextEditor) editor).setHighlightRange(fHighlightRange.getOffset(), fHighlightRange.getLength(), true);
				}
			}
			catch (PartInitException pie) {
				Logger.log(Logger.WARNING_DEBUG, pie.getMessage(), pie);
			}
		}
	}

}
