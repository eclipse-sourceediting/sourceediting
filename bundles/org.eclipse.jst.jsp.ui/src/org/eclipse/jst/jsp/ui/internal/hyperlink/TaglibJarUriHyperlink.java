package org.eclipse.jst.jsp.ui.internal.hyperlink;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jst.jsp.core.contentmodel.ITaglibRecord;
import org.eclipse.jst.jsp.core.contentmodel.JarRecord;
import org.eclipse.jst.jsp.core.contentmodel.URLRecord;

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
					JarRecord record = (JarRecord) fTaglibRecord;
					fHyperlink = new TaglibJarHyperlink(fRegion, record.getLocation());
				}
					break;
				case (ITaglibRecord.URL) : {
					URLRecord record = (URLRecord) fTaglibRecord;
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
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.hyperlink.IHyperlink#getHyperlinkText()
	 */
	public String getHyperlinkText() {
		// TODO Auto-generated method stub
		return null;
	}

	public void open() {
		IHyperlink link = getHyperlink();
		if (link != null) {
			link.open();
		}
	}
}
