/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.contentmodel.tld;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jst.jsp.core.contentmodel.tld.TLDCMDocumentManager;
import org.eclipse.jst.jsp.core.contentmodel.tld.TaglibSupport;
import org.eclipse.jst.jsp.core.internal.parser.JSPSourceParser;
import org.eclipse.wst.common.contentmodel.CMDocument;
import org.eclipse.wst.common.contentmodel.CMElementDeclaration;
import org.eclipse.wst.common.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.common.contentmodel.CMNode;
import org.eclipse.wst.sse.core.contentmodel.CMDocumentTracker;
import org.eclipse.wst.sse.core.parser.RegionParser;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.util.Assert;

public abstract class AbstractTaglibSupport implements TaglibSupport {
	protected IStructuredDocument fm = null;
	static final boolean _debug = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/taglibsupport")); //$NON-NLS-1$ //$NON-NLS-2$


	protected TLDCMDocumentManager fTaglibMgr = null;

	public AbstractTaglibSupport() {
		super();
	}

	public void clearCMDocumentCache() {
		getTaglibManager().clearCache();
	}

	public List getCMDocuments(int offset) {
		return getCMDocumentTrackers(offset);
	}

	protected List getCMDocuments(List cmDocumentTrackers) {
		List newList = new ArrayList();
		if (cmDocumentTrackers != null)
			for (int i = 0; i < cmDocumentTrackers.size(); i++)
				newList.add(((CMDocumentTracker) cmDocumentTrackers.get(i)).getDocument());
		return newList;
	}

	public List getCMDocuments(String prefix, int offset) {
		return getCMDocumentTrackers(prefix, offset);
	}

	public List getCMDocumentTrackers(int offset) {
		return getTaglibManager().getCMDocumentTrackers(offset);
	}

	public List getCMDocumentTrackers(String prefix, int offset) {
		return getTaglibManager().getCMDocumentTrackers(prefix, offset);
	}

	/**
	 * Helper method for accessing the content model
	 */
	public CMElementDeclaration getDeclaration(String tagName, int offset) {
		// skip tagnames lacking a or starting with a ':'
		if (tagName.indexOf(':') <= 0)
			return null;
		// determine the prefix
		String prefix = tagName.substring(0, tagName.indexOf(':'));
		// find the eligible CMDocuments (when valid, only one exists)
		List docs = getCMDocuments(prefix, offset);
		for (int i = 0; i < docs.size(); i++) {
			CMDocument doc = (CMDocument) docs.get(i);
			CMNamedNodeMap elementMap = doc.getElements();
			if (elementMap == null)
				continue;
			CMNode decl = elementMap.getNamedItem(tagName);
			if (decl != null)
				return (CMElementDeclaration) decl;
		}
		return null;
	}

	public IStructuredDocument getStructuredDocument() {
		return fm;
	}

	/**
	 * Gets the taglibMgr.
	 * @return Returns a TLDCMDocumentManager
	 */
	protected TLDCMDocumentManager getTaglibManager() {
		if (fTaglibMgr == null)
			fTaglibMgr = new TLDCMDocumentManager();
		return fTaglibMgr;
	}

	protected List getTrackers() {
		return getTaglibManager().getTaglibTrackers();
	}

	protected void hookParser() {
		if (fm == null) {
			getTaglibManager().setSourceParser(null);
			return;
		}
		RegionParser parser = fm.getParser();
		// support not possible without this parser; it supports getText() apart from the IStructuredDocument
		Assert.isTrue(parser instanceof JSPSourceParser, "IStructuredDocument.getParser() must be a JSPSourceParser"); //$NON-NLS-1$
		JSPSourceParser jspParser = (JSPSourceParser) parser;
		getTaglibManager().setSourceParser(jspParser);
		jspParser.addStructuredDocumentRegionHandler(getTaglibManager().getStructuredDocumentRegionHandler());
	}

	public void setStructuredDocument(IStructuredDocument m) {
		if (_debug && !(m == null || fm == null)) {
			System.out.println("AbstractTaglibSupport updated its IStructuredDocument instance");
		}
		unhookParser();
		fm = m;
		hookParser();
	}

	protected void unhookParser() {
		if (fm == null)
			return;
		RegionParser parser = fm.getParser();
		// support not possible without this parser; it supports getText() apart from the IStructuredDocument
		Assert.isTrue(parser instanceof JSPSourceParser, "IStructuredDocument.getParser() must be a JSPSourceParser"); //$NON-NLS-1$
		JSPSourceParser jspParser = (JSPSourceParser) parser;
		getTaglibManager().setSourceParser(null);
		jspParser.removeStructuredDocumentRegionHandler(getTaglibManager().getStructuredDocumentRegionHandler());
	}

}