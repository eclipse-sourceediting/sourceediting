package org.eclipse.jst.jsp.ui.internal.hyperlink;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jst.jsp.core.taglib.IJarRecord;
import org.eclipse.jst.jsp.core.taglib.ITaglibRecord;
import org.eclipse.jst.jsp.core.taglib.IURLRecord;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;

/**
 * Hyperlink for taglib files in jars or specified by urls.
 */
class TaglibJarUriHyperlink implements IHyperlink {
	private IRegion fRegion;
	private ITaglibRecord fTaglibRecord;
	private IHyperlink fHyperlink;

	public TaglibJarUriHyperlink(IRegion region, ITaglibRecord record) {
		fRegion = region;
		fTaglibRecord = record;
	}

	private IHyperlink getHyperlink() {
		if (fHyperlink == null && fTaglibRecord != null) {
			switch (fTaglibRecord.getRecordType()) {
				case (ITaglibRecord.JAR) : {
					IJarRecord record = (IJarRecord) fTaglibRecord;
					fHyperlink = new TaglibJarHyperlink(fRegion, record.getLocation());
				}
					break;
				case (ITaglibRecord.URL) : {
					IURLRecord record = (IURLRecord) fTaglibRecord;
					fHyperlink = new URLFileHyperlink(fRegion, record.getURL());
				}
			}
		}
		return fHyperlink;
	}

	public IRegion getHyperlinkRegion() {
		IRegion region = null;

		IHyperlink link = getHyperlink();
		if (link != null) {
			region = link.getHyperlinkRegion();
		}
		return region;
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
		return JSPUIMessages.TLDHyperlink_hyperlinkText;
	}

	public void open() {
		IHyperlink link = getHyperlink();
		if (link != null) {
			link.open();
		}
	}
}
