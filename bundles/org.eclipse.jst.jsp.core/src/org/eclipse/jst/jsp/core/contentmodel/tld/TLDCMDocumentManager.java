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
package org.eclipse.jst.jsp.core.contentmodel.tld;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jst.jsp.core.JSP12Namespace;
import org.eclipse.jst.jsp.core.contentmodel.ITaglibRecord;
import org.eclipse.jst.jsp.core.contentmodel.TaglibController;
import org.eclipse.jst.jsp.core.contentmodel.TaglibIndex;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.jst.jsp.core.internal.parser.JSPSourceParser;
import org.eclipse.wst.common.contentmodel.CMDocument;
import org.eclipse.wst.common.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.common.uriresolver.URIResolverPlugin;
import org.eclipse.wst.sse.core.parser.BlockMarker;
import org.eclipse.wst.sse.core.parser.StructuredDocumentRegionHandler;
import org.eclipse.wst.sse.core.parser.StructuredDocumentRegionHandlerExtension;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.ITextRegionList;
import org.eclipse.wst.sse.core.util.Assert;
import org.eclipse.wst.sse.core.util.Debug;
import org.eclipse.wst.sse.core.util.StringUtils;
import org.eclipse.wst.xml.core.jsp.model.parser.temp.XMLJSPRegionContexts;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;

public class TLDCMDocumentManager {

	protected class DirectiveStructuredDocumentRegionHandler implements StructuredDocumentRegionHandler, StructuredDocumentRegionHandlerExtension {

		/**
		 * Adds a block tagname (fully namespace qualified) into the list of
		 * block tag names for the parser. The marker
		 * IStructuredDocumentRegion along with position cues during reparses
		 * allow the JSPSourceParser to enable/ignore the tags as blocks.
		 */
		protected void addBlockTag(String tagnameNS, IStructuredDocumentRegion marker) {
			if (getParser() == null)
				return;
			if (getParser().getBlockMarker(tagnameNS) == null) {
				getParser().addBlockMarker(new BlockMarker(tagnameNS, marker, XMLRegionContext.BLOCK_TEXT, true, false));
				if (_debug) {
					System.out.println("TLDCMDocumentManager added block marker: " + tagnameNS + "@" + marker.getStartOffset()); //$NON-NLS-2$//$NON-NLS-1$
				}
			}
		}

		/**
		 * Enables a TLD owning the given prefix loaded from the given URI at
		 * the anchorStructuredDocumentRegion. The list of
		 * additionalCMDocuments will claim to not know any of its tags at
		 * positions earlier than that IStructuredDocumentRegion's position.
		 * 
		 * For taglib directives, the taglib is the anchor while taglibs
		 * registered through include directives use the parent document's
		 * include directive as their anchor.
		 * 
		 * @param prefix
		 * @param uri
		 * @param anchorStructuredDocumentRegion
		 */
		protected void enableTaglibFromURI(String prefix, String uri, IStructuredDocumentRegion anchorStructuredDocumentRegion) {
			if (prefix == null || uri == null || bannedPrefixes.contains(prefix))
				return;
			// Try to load the CMDocument for this URI
			CMDocument tld = getCMDocument(uri);
			if (tld == null || !(tld instanceof TLDDocument)) {
				if (_debug) {
					System.out.println("TLDCMDocumentManager failed to create a CMDocument for " + uri); //$NON-NLS-1$
				}
				return;
			}
			CMNamedNodeMap elements = tld.getElements();
			// Go through the CMDocument for any tags that must be marked as
			// block tags
			// starting at the anchoring IStructuredDocumentRegion. As the
			// document is edited and the
			// IStructuredDocumentRegion moved around, the block tag
			// enablement will automatically follow
			// it.
			for (int i = 0; i < elements.getLength(); i++) {
				TLDElementDeclaration ed = (TLDElementDeclaration) elements.item(i);
				if (ed.getBodycontent() == JSP12TLDNames.CONTENT_TAGDEPENDENT)
					addBlockTag(prefix + ":" + ed.getNodeName(), anchorStructuredDocumentRegion); //$NON-NLS-1$
			}
			// Since modifications to StructuredDocumentRegions adjacent to a
			// taglib directive can cause
			// that IStructuredDocumentRegion to be reported, filter out any
			// duplicated URIs. When the
			// taglib is actually modified, a full rebuild will occur and no
			// duplicates
			// will/should be found.
			List trackers = getTaglibTrackers();
			for (int i = 0; i < trackers.size(); i++) {
				TaglibTracker tracker = (TaglibTracker) trackers.get(i);
				if (tracker.getPrefix().equals(prefix) && tracker.getURI().equals(uri)) {
					return;
				}
			}
			if (_debug) {
				System.out.println("TLDCMDocumentManager registered a tracker for " + uri + " with prefix " + prefix); //$NON-NLS-2$//$NON-NLS-1$
			}
			getTaglibTrackers().add(new TaglibTracker(uri, prefix, tld, anchorStructuredDocumentRegion));
		}

		/**
		 * Enables a TLD owning the given prefix loaded from the given URI at
		 * the anchorStructuredDocumentRegion. The list of
		 * additionalCMDocuments will claim to not know any of its tags at
		 * positions earlier than that IStructuredDocumentRegion's position.
		 * 
		 * For taglib directives, the taglib is the anchor while taglibs
		 * registered through include directives use the parent document's
		 * include directive as their anchor.
		 * 
		 * @param prefix
		 * @param uri
		 * @param taglibStructuredDocumentRegion
		 */
		protected void enableTagsInDir(String prefix, String tagdir, IStructuredDocumentRegion taglibStructuredDocumentRegion) {
			if (prefix == null || tagdir == null || bannedPrefixes.contains(prefix))
				return;
			if (_debug) {
				System.out.println("TLDCMDocumentManager enabling tags from directory" + tagdir + " for prefix " + prefix); //$NON-NLS-2$//$NON-NLS-1$
			}
			// Try to load the CMDocument for this URI
			CMDocument tld = getImplicitCMDocument(tagdir);
			if (tld == null || !(tld instanceof TLDDocument))
				return;
			CMNamedNodeMap elements = tld.getElements();
			// Go through the CMDocument for any tags that must be marked as
			// block tags
			// starting at the anchoring IStructuredDocumentRegion. As the
			// document is edited and the
			// IStructuredDocumentRegion moved around, the block tag
			// enablement will automatically follow
			// it.
			for (int i = 0; i < elements.getLength(); i++) {
				TLDElementDeclaration ed = (TLDElementDeclaration) elements.item(i);
				if (ed.getBodycontent() == JSP12TLDNames.CONTENT_TAGDEPENDENT)
					addBlockTag(prefix + ":" + ed.getNodeName(), taglibStructuredDocumentRegion); //$NON-NLS-1$
			}
			// Since modifications to StructuredDocumentRegions adjacent to a
			// taglib directive can cause
			// that IStructuredDocumentRegion to be reported, filter out any
			// duplicated URIs. When the
			// taglib is actually modified, a full rebuild will occur and no
			// duplicates
			// will/should be found.
			List trackers = getTaglibTrackers();
			for (int i = 0; i < trackers.size(); i++) {
				TaglibTracker tracker = (TaglibTracker) trackers.get(i);
				if (tracker.getPrefix().equals(prefix) && tracker.getURI().equals(tagdir)) {
					return;
				}
			}
			if (_debug) {
				System.out.println("TLDCMDocumentManager registered a tracker for directory" + tagdir + " with prefix " + prefix); //$NON-NLS-2$//$NON-NLS-1$
			}
			getTaglibTrackers().add(new TaglibTracker(tagdir, prefix, tld, taglibStructuredDocumentRegion));
		}

		public void nodeParsed(IStructuredDocumentRegion aCoreStructuredDocumentRegion) {
			// could test > 1, but since we only care if there are 8 (<%@,
			// taglib, uri, =, where, prefix, =, what) [or 4 for includes]
			if (aCoreStructuredDocumentRegion.getNumberOfRegions() > 4 && aCoreStructuredDocumentRegion.getRegions().get(1).getType() == XMLJSPRegionContexts.JSP_DIRECTIVE_NAME) {
				ITextRegion name = aCoreStructuredDocumentRegion.getRegions().get(1);
				try {
					if (getParser() == null) {
						Logger.log(Logger.WARNING, "Warning: parser text was requested by " + getClass().getName() + " but none was available; taglib support disabled"); //$NON-NLS-1$ //$NON-NLS-2$
					}
					else {
						boolean taglibDetected = false;
						boolean taglibDirectiveDetected = false;
						boolean includeDetected = false;
						boolean includeDirectiveDetected = false;
						int startOffset = aCoreStructuredDocumentRegion.getStartOffset(name);
						int textLength = name.getTextLength();

						if (getParser() != null) {
							taglibDetected = getParser().regionMatches(startOffset, textLength, JSP12TLDNames.TAGLIB);
							taglibDirectiveDetected = getParser().regionMatches(startOffset, textLength, JSP12Namespace.ElementName.DIRECTIVE_TAGLIB);
							includeDetected = getParser().regionMatches(startOffset, textLength, JSP12TLDNames.INCLUDE);
							includeDirectiveDetected = getParser().regionMatches(startOffset, textLength, JSP12Namespace.ElementName.DIRECTIVE_INCLUDE);
						}
						else {
							// old fashioned way
							String directiveName = getParser().getText(startOffset, textLength);
							taglibDetected = directiveName.equals(JSP12TLDNames.TAGLIB);
							taglibDirectiveDetected = directiveName.equals(JSP12Namespace.ElementName.DIRECTIVE_TAGLIB);
							includeDetected = directiveName.equals(JSP12TLDNames.INCLUDE);
							includeDirectiveDetected = directiveName.equals(JSP12Namespace.ElementName.DIRECTIVE_INCLUDE);
						}
						if (taglibDetected || taglibDirectiveDetected) {
							processTaglib(aCoreStructuredDocumentRegion);
						}
						else if (includeDetected || includeDirectiveDetected) {
							processInclude(aCoreStructuredDocumentRegion);
						}
					}
				}
				catch (StringIndexOutOfBoundsException sioobExc) {
					// do nothing
				}
			}
			// could test > 1, but since we only care if there are 5 (<,
			// jsp:root, xmlns:prefix, =, where)
			else if (aCoreStructuredDocumentRegion.getNumberOfRegions() > 4 && aCoreStructuredDocumentRegion.getRegions().get(1).getType() == XMLJSPRegionContexts.JSP_ROOT_TAG_NAME) {
				if (getParser() == null) {
					Logger.log(Logger.WARNING, "Warning: parser text was requested by " + getClass().getName() + " but none was available; taglib support disabled"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				else {
					processJSPRoot(aCoreStructuredDocumentRegion);
				}
			}
		}

		protected void processInclude(IStructuredDocumentRegion aCoreStructuredDocumentRegion) {
			processInclude(aCoreStructuredDocumentRegion, aCoreStructuredDocumentRegion, getParser());
		}

		/**
		 * Process an include directive found by the textSource parser and
		 * anchor any taglibs found within at the
		 * anchorStructuredDocumentRegion.
		 */
		protected void processInclude(IStructuredDocumentRegion includeStructuredDocumentRegion, IStructuredDocumentRegion anchorStructuredDocumentRegion, JSPSourceParser textSource) {
			ITextRegionList regions = includeStructuredDocumentRegion.getRegions();
			String file = null;
			boolean isFilename = false;
			try {
				for (int i = 0; i < regions.size(); i++) {
					ITextRegion region = regions.get(i);
					if (region.getType() == XMLRegionContext.XML_TAG_ATTRIBUTE_NAME) {
						if (textSource.getText(includeStructuredDocumentRegion.getStartOffset(region), region.getTextLength()).equals(JSP12TLDNames.FILE)) {
							isFilename = true;
						}
						else {
							isFilename = false;
						}
					}
					else if (isFilename && region.getType() == XMLRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
						file = textSource.getText(includeStructuredDocumentRegion.getStartOffset(region), region.getTextLength());
						isFilename = false;
					}
				}
			}
			catch (StringIndexOutOfBoundsException sioobExc) {
				// nothing to be done
				file = null;
			}
			if (file != null) {
				String fileLocation = getBaseLocation();
				// hopefully, a resolver is present and has returned a
				// canonical file path
				if (!getIncludes().contains(fileLocation) && getBaseLocation() != null && !fileLocation.equals(getBaseLocation())) {
					getIncludes().push(fileLocation);
					if (getParser() != null)
						new IncludeHelper(anchorStructuredDocumentRegion, getParser()).parse(fileLocation);
					else
						Logger.log(Logger.WARNING, "Warning: parser text was requested by " + getClass().getName() + " but none was available; taglib support disabled"); //$NON-NLS-1$ //$NON-NLS-2$
					getIncludes().pop();
				}
				else {
					if (Debug.debugTokenizer)
						System.out.println("LOOP IN @INCLUDES FOUND: " + fileLocation); //$NON-NLS-1$
				}
			}
		}

		// Pulls the URI and prefix from the given jsp:root
		// IStructuredDocumentRegion and
		// makes sure the tags are known.
		protected void processJSPRoot(IStructuredDocumentRegion jspRootStructuredDocumentRegion) {
			processJSPRoot(jspRootStructuredDocumentRegion, jspRootStructuredDocumentRegion, getParser());
		}

		protected void processJSPRoot(IStructuredDocumentRegion taglibStructuredDocumentRegion, IStructuredDocumentRegion anchorStructuredDocumentRegion, JSPSourceParser textSource) {
			ITextRegionList regions = taglibStructuredDocumentRegion.getRegions();
			String uri = null;
			String prefix = null;
			boolean taglib = false;
			try {
				for (int i = 0; i < regions.size(); i++) {
					ITextRegion region = regions.get(i);
					if (region.getType() == XMLRegionContext.XML_TAG_ATTRIBUTE_NAME) {
						String name = textSource.getText(taglibStructuredDocumentRegion.getStartOffset(region), region.getTextLength());
						if (name.startsWith(XMLNS)) { //$NON-NLS-1$
							prefix = name.substring(XMLNS_LENGTH);
							if (!bannedPrefixes.contains(prefix))
								taglib = true;
						}
						else {
							prefix = null;
							taglib = false;
						}
					}
					else if (taglib && region.getType() == XMLRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
						uri = textSource.getText(taglibStructuredDocumentRegion.getStartOffset(region), region.getTextLength());
						if (uri != null && prefix != null && (StringUtils.strip(uri).length() > 0) && (StringUtils.strip(prefix).length() > 0)) {
							if (anchorStructuredDocumentRegion == null)
								enableTaglibFromURI(StringUtils.strip(prefix), StringUtils.strip(uri), taglibStructuredDocumentRegion);
							else
								enableTaglibFromURI(StringUtils.strip(prefix), StringUtils.strip(uri), anchorStructuredDocumentRegion);
							uri = null;
							prefix = null;
						}
					}
				}
			}
			catch (StringIndexOutOfBoundsException sioobExc) {
				// nothing to be done
				uri = null;
				prefix = null;
			}
		}

		protected void processTaglib(IStructuredDocumentRegion taglibStructuredDocumentRegion) {
			processTaglib(taglibStructuredDocumentRegion, taglibStructuredDocumentRegion, getParser());
		}

		/**
		 * Pulls the URI and prefix from the given taglib directive
		 * IStructuredDocumentRegion and makes sure the tags are known.
		 */
		protected void processTaglib(IStructuredDocumentRegion taglibStructuredDocumentRegion, IStructuredDocumentRegion anchorStructuredDocumentRegion, JSPSourceParser textSource) {
			ITextRegionList regions = taglibStructuredDocumentRegion.getRegions();
			String uri = null;
			String prefix = null;
			String tagdir = null;
			String attrName = null;
			try {
				for (int i = 0; i < regions.size(); i++) {
					ITextRegion region = regions.get(i);
					// remember attribute name
					int startOffset = taglibStructuredDocumentRegion.getStartOffset(region);
					int textLength = region.getTextLength();
					if (region.getType() == XMLRegionContext.XML_TAG_ATTRIBUTE_NAME) {
						// String name = textSource.getText(startOffset,
						// textLength);
						if (textSource.regionMatches(startOffset, textLength, JSP11TLDNames.PREFIX)) {
							attrName = JSP11TLDNames.PREFIX;
						}
						else if (textSource.regionMatches(startOffset, textLength, JSP12TLDNames.URI)) {
							attrName = JSP11TLDNames.URI;
						}
						else if (textSource.regionMatches(startOffset, textLength, JSP20TLDNames.TAGDIR)) {
							attrName = JSP20TLDNames.TAGDIR;
						}
						else {
							attrName = null;
						}
					}
					// process value
					else if (region.getType() == XMLRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
						if (JSP11TLDNames.PREFIX.equals(attrName))
							prefix = textSource.getText(startOffset, textLength);
						else if (JSP11TLDNames.URI.equals(attrName))
							uri = textSource.getText(startOffset, textLength);
						else if (JSP20TLDNames.TAGDIR.equals(attrName))
							tagdir = textSource.getText(startOffset, textLength);
					}
				}
			}
			catch (StringIndexOutOfBoundsException sioobExc) {
				// nothing to be done
				uri = null;
				prefix = null;
			}
			if (uri != null && prefix != null && (StringUtils.strip(uri).length() > 0) && (StringUtils.strip(prefix).length() > 0)) {
				if (anchorStructuredDocumentRegion == null)
					enableTaglibFromURI(StringUtils.strip(prefix), StringUtils.strip(uri), taglibStructuredDocumentRegion);
				else
					enableTaglibFromURI(StringUtils.strip(prefix), StringUtils.strip(uri), anchorStructuredDocumentRegion);
			}
			else if (tagdir != null && prefix != null && (StringUtils.strip(tagdir).length() > 0) && (StringUtils.strip(prefix).length() > 0)) {
				if (anchorStructuredDocumentRegion == null)
					enableTagsInDir(StringUtils.strip(prefix), StringUtils.strip(tagdir), taglibStructuredDocumentRegion);
				else
					enableTagsInDir(StringUtils.strip(prefix), StringUtils.strip(tagdir), anchorStructuredDocumentRegion);
			}
		}

		private void resetBlockTags() {
			if (getParser() == null)
				return;
			Iterator names = getParser().getBlockMarkers().iterator();
			while (names.hasNext()) {
				BlockMarker marker = (BlockMarker) names.next();
				if (!marker.isGlobal() && marker.getContext() == XMLRegionContext.BLOCK_TEXT) {
					if (_debug) {
						System.out.println("TLDCMDocumentManager removing block tag named: " + marker.getTagName()); //$NON-NLS-1$
					}
					names.remove();
				}
			}
		}

		public void resetNodes() {
			if (Debug.debugTaglibs)
				System.out.println(getClass().getName() + ": resetting"); //$NON-NLS-1$
			getIncludes().clear();
			resetBlockTags();
			resetTaglibTrackers();
		}

		public void setStructuredDocument(IStructuredDocument newDocument) {
			Assert.isTrue(newDocument != null, "null document");
			Assert.isTrue(newDocument.getParser() != null, "null document parser");
			Assert.isTrue(newDocument.getParser() instanceof JSPSourceParser, "can only listen to document with a JSPSourceParser");
			getSourceParser().removeStructuredDocumentRegionHandler(this);
			setSourceParser((JSPSourceParser) newDocument.getParser());
			getSourceParser().addStructuredDocumentRegionHandler(this);
		}
	}

	protected class IncludeHelper extends DirectiveStructuredDocumentRegionHandler {
		protected IStructuredDocumentRegion fAnchor = null;
		protected JSPSourceParser fLocalParser = null;
		protected JSPSourceParser fParentParser = null;

		public IncludeHelper(IStructuredDocumentRegion anchor, JSPSourceParser rootParser) {
			super();
			fAnchor = anchor;
			fParentParser = rootParser;
		}

		protected String getContents(String fileName) {
			StringBuffer s = new StringBuffer();
			int c = 0;
			int length = 0;
			int count = 0;
			File file = null;
			FileInputStream fis = null;
			try {
				file = new File(fileName);
				length = (int) file.length();
				fis = new FileInputStream(file);
				while (((c = fis.read()) >= 0) && (count < length)) {
					count++;
					s.append((char) c);
				}
			}
			catch (FileNotFoundException e) {
				if (Debug.debugStructuredDocument)
					System.out.println("File not found : \"" + fileName + "\""); //$NON-NLS-2$//$NON-NLS-1$
			}
			catch (ArrayIndexOutOfBoundsException e) {
				if (Debug.debugStructuredDocument)
					System.out.println("Usage wrong: specify inputfile"); //$NON-NLS-1$
				//$NON-NLS-1$
			}
			catch (IOException e) {
				if (Debug.debugStructuredDocument)
					System.out.println("An I/O error occured while scanning :"); //$NON-NLS-1$
				//$NON-NLS-1$
			}
			catch (Exception e) {
				if (Debug.debugStructuredDocument)
					e.printStackTrace();
			}
			finally {
				try {
					if (fis != null) {
						fis.close();
					}
				}
				catch (Exception e) {
					// nothing to do
				}
			}
			return s.toString();
		}

		public void nodeParsed(IStructuredDocumentRegion aCoreStructuredDocumentRegion) {
			// could test > 1, but since we only care if there are 8 (<%@,
			// taglib, uri, =, where, prefix, =, what)
			if (aCoreStructuredDocumentRegion.getNumberOfRegions() > 1 && aCoreStructuredDocumentRegion.getRegions().get(1).getType() == XMLJSPRegionContexts.JSP_DIRECTIVE_NAME) {
				ITextRegion name = aCoreStructuredDocumentRegion.getRegions().get(1);
				try {
					String directiveName = fLocalParser.getText(aCoreStructuredDocumentRegion.getStartOffset(name), name.getTextLength());
					if (directiveName.equals(JSP12TLDNames.TAGLIB) || directiveName.equals(JSP12Namespace.ElementName.DIRECTIVE_TAGLIB)) {
						processTaglib(aCoreStructuredDocumentRegion, fAnchor, fLocalParser);
					}
					if (directiveName.equals(JSP12TLDNames.INCLUDE) || directiveName.equals(JSP12Namespace.ElementName.DIRECTIVE_INCLUDE)) {
						processInclude(aCoreStructuredDocumentRegion, fAnchor, fLocalParser);
					}
				}
				catch (StringIndexOutOfBoundsException sioobExc) {
					// do nothing
				}
			}
			// could test > 1, but since we only care if there are 5 (<,
			// jsp:root, xmlns:prefix, =, where)
			else if (aCoreStructuredDocumentRegion.getNumberOfRegions() > 4 && aCoreStructuredDocumentRegion.getRegions().get(1).getType() == XMLJSPRegionContexts.JSP_ROOT_TAG_NAME) {
				processJSPRoot(aCoreStructuredDocumentRegion, fAnchor, fLocalParser);
			}
		}

		public void parse(String filename) {
			JSPSourceParser p = new JSPSourceParser();
			fLocalParser = p;
			List blockTags = fParentParser.getBlockMarkers();
			String includedFilename = filename;
			File baseFile = FileBuffers.getSystemFileAtLocation(new Path(includedFilename));
			try {
				if (baseFile != null)
					includedFilename = baseFile.getCanonicalPath();
			}
			catch (IOException e) {
			}
			String s = getContents(includedFilename);
			fLocalParser.addStructuredDocumentRegionHandler(this);
			fLocalParser.reset(s);
			for (int i = 0; i < blockTags.size(); i++) {
				BlockMarker marker = (BlockMarker) blockTags.get(i);
				fLocalParser.addBlockMarker(new BlockMarker(marker.getTagName(), null, marker.getContext(), marker.isCaseSensitive()));
			}
			// force parse
			fLocalParser.getDocumentRegions();
			fLocalParser = null;
		}

		public void resetNodes() {
		}
	}

	static final boolean _debug = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/tldcmdocument/manager")); //$NON-NLS-1$ //$NON-NLS-2$

	// will hold the prefixes banned by the specification; taglibs may not use
	// them
	protected static List bannedPrefixes = null;
	static final String XMLNS = "xmlns:"; //$NON-NLS-1$ 
	static final int XMLNS_LENGTH = XMLNS.length();

	static {
		bannedPrefixes = new ArrayList(7);
		bannedPrefixes.add("jsp"); //$NON-NLS-1$
		bannedPrefixes.add("jspx"); //$NON-NLS-1$
		bannedPrefixes.add("java"); //$NON-NLS-1$
		bannedPrefixes.add("javax"); //$NON-NLS-1$
		bannedPrefixes.add("servlet"); //$NON-NLS-1$
		bannedPrefixes.add("sun"); //$NON-NLS-1$
		bannedPrefixes.add("sunw"); //$NON-NLS-1$
	}

	private CMDocumentFactoryTLD fCMDocumentBuilder = null;
	private DirectiveStructuredDocumentRegionHandler fDirectiveHandler = null;
	private Hashtable fDocuments = null;
	private Stack fIncludes = null;

	private JSPSourceParser fParser = null;

//	 trivial hand edit to remove unused variable  	private URIResolverProvider fResolverProvider = null;

	private List fTaglibTrackers = null;

	public TLDCMDocumentManager() {
		super();
	}

	public void clearCache() {
		if (_debug) {
			System.out.println("TLDCMDocumentManager cleared its CMDocument cache"); //$NON-NLS-1$
		}
		getDocuments().clear();
	}

	/**
	 * 
	 * @return java.lang.String
	 */
	public String getBaseLocation() {
		return TaglibController.getFileBuffer(this).getLocation().toString();
	}

	/**
	 * Return the CMDocument at the uri (cached)
	 */
	protected CMDocument getCMDocument(String uri) {
		if (uri == null || uri.length() == 0)
			return null;
		String reference = uri;
		/**
		 * JSP 1.2 Specification, section 5.2.2 jsp-1_2-fcs-spec.pdf, page 87
		 */
		String URNprefix = "urn:jsptld:"; //$NON-NLS-1$
		if (reference.startsWith(URNprefix)) {
			/**
			 * @see section 7.3.2
			 */
			if (reference.length() > URNprefix.length())
				reference = reference.substring(11);
		}
		else {
			/**
			 * @see section 7.3.6
			 */
		}
		CMDocument doc = (CMDocument) getDocuments().get(reference);
		if (doc == null) {
			doc = loadTaglib(reference);
			if (doc != null)
				getDocuments().put(reference, doc);
		}
		return doc;
	}

	/**
	 * Gets the cMDocumentBuilder.
	 * 
	 * @return Returns a CMDocumentFactoryTLD, since it has more builder
	 *         methods
	 */
	protected CMDocumentFactoryTLD getCMDocumentBuilder() {
		if (fCMDocumentBuilder == null)
			fCMDocumentBuilder = new CMDocumentFactoryTLD();
		return fCMDocumentBuilder;
	}

	public List getCMDocumentTrackers(int offset) {
		List validDocs = new ArrayList();
		Iterator alldocs = getTaglibTrackers().iterator();
		while (alldocs.hasNext()) {
			TaglibTracker aTracker = (TaglibTracker) alldocs.next();
			if (aTracker.getStructuredDocumentRegion().getStartOffset() < offset || offset < 0) {
				validDocs.add(aTracker);
			}
		}
		return validDocs;
	}

	public List getCMDocumentTrackers(String prefix, int offset) {
		List validDocs = new ArrayList();
		Iterator alldocs = getTaglibTrackers().iterator();
		while (alldocs.hasNext()) {
			TaglibTracker aTracker = (TaglibTracker) alldocs.next();
			if ((aTracker.getStructuredDocumentRegion().getStartOffset() < offset || offset < 0) && aTracker.getPrefix().equals(prefix)) {
				validDocs.add(aTracker);
			}
		}
		return validDocs;
	}

	protected DirectiveStructuredDocumentRegionHandler getDirectiveStructuredDocumentRegionHandler() {
		if (fDirectiveHandler == null)
			fDirectiveHandler = new DirectiveStructuredDocumentRegionHandler();
		return fDirectiveHandler;
	}

	/**
	 * Gets the documents.
	 * 
	 * @return Returns a Hashtable
	 */
	public Hashtable getDocuments() {
		if (fDocuments == null)
			fDocuments = new Hashtable();
		return fDocuments;
	}

	/**
	 * Return the CMDocument at the tagdir (cached)
	 */
	protected CMDocument getImplicitCMDocument(String tagdir) {
		if (tagdir == null || tagdir.length() == 0)
			return null;
		String reference = tagdir;
		/**
		 * JSP 1.2 Specification, section 5.2.2 jsp-1_2-fcs-spec.pdf, page 87
		 */
		String URNprefix = "urn:jsptld:"; //$NON-NLS-1$
		if (reference.startsWith(URNprefix)) {
			/**
			 * @see section 7.3.2
			 */
			if (reference.length() > URNprefix.length())
				reference = reference.substring(11);
		}
		else {
			/**
			 * @see section 7.3.6
			 */
		}
		CMDocument doc = (CMDocument) getDocuments().get(reference);
		if (doc == null) {
			doc = loadTagDir(reference);
			if (doc != null)
				getDocuments().put(reference, doc);
		}
		return doc;
	}

	/**
	 * Gets the includes.
	 * 
	 * @return Returns a Stack
	 */
	protected Stack getIncludes() {
		if (fIncludes == null)
			fIncludes = new Stack();
		return fIncludes;
	}

	JSPSourceParser getParser() {
		return fParser;
	}

	/**
	 * @deprecated
	 */
	public URIResolverProvider getResolverProvider() {
		return null;
	}

	public JSPSourceParser getSourceParser() {
		return fParser;
	}

	public StructuredDocumentRegionHandler getStructuredDocumentRegionHandler() {
		return getDirectiveStructuredDocumentRegionHandler();
	}

	/**
	 * 
	 * @return java.util.List
	 */
	public List getTaglibTrackers() {
		if (fTaglibTrackers == null)
			fTaglibTrackers = new ArrayList();
		return fTaglibTrackers;
	}

	/**
	 * Loads the tags from the specified URI. It must point to a URL of valid
	 * tag files to work.
	 */
	protected CMDocument loadTagDir(String uri) {
		ITaglibRecord reference = TaglibIndex.resolve(getBaseLocation(), uri, false);
		if (reference != null) {
			CMDocument document = getCMDocumentBuilder().createCMDocument(reference);
			if (document != null) {
				return document;
			}
		}
		// JSP2_TODO: implement for JSP 2.0
		String location = URIResolverPlugin.createResolver().resolve(getBaseLocation(), null, uri);
		if (location == null)
			return null;
		if (_debug) {
			System.out.println("Loading tags from dir" + uri + " at " + location); //$NON-NLS-2$//$NON-NLS-1$
		}
		return getCMDocumentBuilder().createCMDocument(location);
	}

	/**
	 * Loads the taglib from the specified URI. It must point to a valid
	 * taglib descriptor or valid JAR file to work.
	 */
	protected CMDocument loadTaglib(String uri) {
		CMDocument document = null;
		ITaglibRecord reference = TaglibIndex.resolve(getBaseLocation(), uri, false);
		if (reference != null) {
			document = getCMDocumentBuilder().createCMDocument(reference);
		}
		return document;
	}

	protected void resetTaglibTrackers() {
		if (_debug) {
			System.out.println("TLDCMDocumentManager cleared its taglib trackers\n"); //$NON-NLS-1$
		}
		getTaglibTrackers().clear();
	}

	public void setSourceParser(JSPSourceParser parser) {
		if (fParser != null)
			fParser.removeStructuredDocumentRegionHandler(getStructuredDocumentRegionHandler());
		fParser = parser;
		if (fParser != null)
			fParser.addStructuredDocumentRegionHandler(getStructuredDocumentRegionHandler());
	}
}