package org.eclipse.jst.jsp.ui.internal.hyperlink;

import java.io.File;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.jst.jsp.ui.internal.Logger;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * Hyperlink for external files.
 */
class ExternalFileHyperlink implements IHyperlink {
	// copies of this class exist in:
	// org.eclipse.wst.xml.ui.internal.hyperlink
	// org.eclipse.wst.html.ui.internal.hyperlink
	// org.eclipse.jst.jsp.ui.internal.hyperlink
	
	private IRegion fHyperlinkRegion;
	private File fHyperlinkFile;

	public ExternalFileHyperlink(IRegion region, File file) {
		fHyperlinkFile = file;
		fHyperlinkRegion = region;
	}
	
	public IRegion getHyperlinkRegion() {
		return fHyperlinkRegion;
	}

	public String getTypeLabel() {
		return null;
	}

	public String getHyperlinkText() {
		String path = fHyperlinkFile.getPath();
		if (path.length() > 60) {
			path = path.substring(0, 25) + "..." + path.substring(path.length() - 25, path.length());
		}
		return NLS.bind(JSPUIMessages.Open, path);
	}

	public void open() {
		if (fHyperlinkFile != null) {
			IEditorInput input = new ExternalFileEditorInput(fHyperlinkFile);
			IEditorDescriptor descriptor;
			try {
				descriptor = IDE.getEditorDescriptor(input.getName(), true);
				if (descriptor != null) {
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					IDE.openEditor(page, input, descriptor.getId(), true);
				}
			}
			catch (PartInitException e) {
				Logger.log(Logger.WARNING_DEBUG, e.getMessage(), e);
			}
		}
	}
}
