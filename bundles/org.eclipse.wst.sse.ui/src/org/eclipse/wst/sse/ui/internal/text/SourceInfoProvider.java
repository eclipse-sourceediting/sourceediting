package org.eclipse.wst.sse.ui.internal.text;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.IInformationProviderExtension;
import org.eclipse.ui.texteditor.ITextEditor;

public class SourceInfoProvider implements IInformationProvider, IInformationProviderExtension  {

	private ITextEditor fEditor;

	public SourceInfoProvider(ITextEditor editor) {
		fEditor = editor;
	}
	public String getInformation(ITextViewer textViewer, IRegion subject) {
		return getInformation2(textViewer, subject).toString();
	}

	public IRegion getSubject(ITextViewer textViewer, int offset) {
		if (textViewer != null && fEditor != null) {
			IRegion region= WordFinder.findWord(textViewer.getDocument(), offset);
			if (region != null)
				return region;
			else
				return new Region(offset, 0);
		}
		return null;
	}

	public Object getInformation2(ITextViewer textViewer, IRegion subject) {
		if (fEditor == null)
			return null;

		Object selection = fEditor.getSelectionProvider().getSelection();
		if (selection == null)
			selection = new Object();
		return selection;
	}

}
