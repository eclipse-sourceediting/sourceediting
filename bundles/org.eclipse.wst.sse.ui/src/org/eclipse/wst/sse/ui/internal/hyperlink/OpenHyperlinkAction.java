package org.eclipse.wst.sse.ui.internal.hyperlink;

import java.util.ResourceBundle;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;
import org.eclipse.wst.sse.ui.extension.IExtendedSimpleEditor;

/**
 * Open hyperlink action
 */
public class OpenHyperlinkAction extends TextEditorAction {
	private IHyperlinkDetector[] fHyperlinkDetectors;
	private ITextViewer fTextViewer;

	public OpenHyperlinkAction(ResourceBundle bundle, String prefix, ITextEditor editor, ITextViewer viewer) {
		super(bundle, prefix, editor);
		fTextViewer = viewer;
	}

	public void setHyperlinkDetectors(IHyperlinkDetector[] detectors) {
		fHyperlinkDetectors = detectors;
	}

	public void run() {
		if (getTextEditor() instanceof IExtendedSimpleEditor) {
			int offset = ((IExtendedSimpleEditor) getTextEditor()).getCaretPosition();
			if (offset == -1 || fHyperlinkDetectors == null)
				return;
			IRegion region = new Region(offset, 0);
			IHyperlink hyperlink = null;

			synchronized (fHyperlinkDetectors) {
				for (int i = 0, length = fHyperlinkDetectors.length; i < length && hyperlink == null; i++) {
					IHyperlinkDetector detector = fHyperlinkDetectors[i];
					if (detector == null)
						continue;

					IHyperlink[] hyperlinks = detector.detectHyperlinks(fTextViewer, region, false);
					if (hyperlinks == null)
						continue;

					if (hyperlinks.length > 0)
						hyperlink = hyperlinks[0];
				}
			}
			if (hyperlink != null) {
				getTextEditor().setHighlightRange(offset, 0, false);
				hyperlink.open();
			}
		}
	}
}
