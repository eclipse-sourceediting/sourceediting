/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.contentmodel.tld;

import java.io.File;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.jst.jsp.core.internal.contentmodel.TaglibController;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.JSP11TLDNames;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.JSP12TLDNames;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.JSP20TLDNames;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDDocument;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDElementDeclaration;
import org.eclipse.jst.jsp.core.internal.contenttype.DeploymentDescriptorPropertyCache;
import org.eclipse.jst.jsp.core.internal.contenttype.DeploymentDescriptorPropertyCache.PropertyGroup;
import org.eclipse.jst.jsp.core.internal.provisional.JSP12Namespace;
import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.jst.jsp.core.internal.util.FacetModuleCoreSupport;
import org.eclipse.jst.jsp.core.internal.util.FileContentCache;
import org.eclipse.jst.jsp.core.internal.util.ZeroStructuredDocumentRegion;
import org.eclipse.jst.jsp.core.taglib.IJarRecord;
import org.eclipse.jst.jsp.core.taglib.ITLDRecord;
import org.eclipse.jst.jsp.core.taglib.ITagDirRecord;
import org.eclipse.jst.jsp.core.taglib.ITaglibIndexDelta;
import org.eclipse.jst.jsp.core.taglib.ITaglibIndexListener;
import org.eclipse.jst.jsp.core.taglib.ITaglibRecord;
import org.eclipse.jst.jsp.core.taglib.IURLRecord;
import org.eclipse.jst.jsp.core.taglib.TaglibIndex;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverPlugin;
import org.eclipse.wst.sse.core.internal.ltk.parser.BlockMarker;
import org.eclipse.wst.sse.core.internal.ltk.parser.JSPCapableParser;
import org.eclipse.wst.sse.core.internal.ltk.parser.StructuredDocumentRegionHandler;
import org.eclipse.wst.sse.core.internal.ltk.parser.StructuredDocumentRegionHandlerExtension;
import org.eclipse.wst.sse.core.internal.ltk.parser.TagMarker;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionCollection;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.internal.util.Assert;
import org.eclipse.wst.sse.core.internal.util.Debug;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.parser.XMLSourceParser;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

public class TLDCMDocumentManager implements ITaglibIndexListener {

	protected class DirectiveStructuredDocumentRegionHandler implements StructuredDocumentRegionHandler, StructuredDocumentRegionHandlerExtension {
		/**
		 * Adds a block tagname (fully namespace qualified) into the list of
		 * block tag names for the parser. The marker
		 * IStructuredDocumentRegion along with position cues during reparses
		 * allow the XMLSourceParser to enable/ignore the tags as blocks.
		 */
		protected void addBlockTag(String tagnameNS, ITextRegionCollection marker) {
			if (getParser() == null)
				return;
			if (getParser().getBlockMarker(tagnameNS) == null) {
				getParser().addBlockMarker(new BlockMarker(tagnameNS, marker, DOMRegionContext.BLOCK_TEXT, true, false));
				if (_debug) {
					System.out.println("TLDCMDocumentManager added block marker: " + tagnameNS + "@" + marker.getStartOffset()); //$NON-NLS-2$//$NON-NLS-1$
				}
			}
		}

		protected void addTaglibTracker(String prefix, String uri, IStructuredDocumentRegion anchorStructuredDocumentRegion, CMDocument tldCMDocument) {
			getTaglibTrackers().add(new TaglibTracker(uri, prefix, tldCMDocument, anchorStructuredDocumentRegion));
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
			enableTags(prefix, uri, anchorStructuredDocumentRegion);
			if (_debug) {
				System.out.println("TLDCMDocumentManager registered a tracker for " + uri + " with prefix " + prefix); //$NON-NLS-2$//$NON-NLS-1$
			}
		}

		private void enableTags(String prefix, String uri, IStructuredDocumentRegion anchorStructuredDocumentRegion) {
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
			registerTaglib(prefix, uri, anchorStructuredDocumentRegion, tld);
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
		protected void enableTagsInDir(String prefix, String tagdir, IStructuredDocumentRegion anchorStructuredDocumentRegion) {
			enableTags(prefix, tagdir, anchorStructuredDocumentRegion);
			if (_debug) {
				System.out.println("TLDCMDocumentManager registered a tracker for directory" + tagdir + " with prefix " + prefix); //$NON-NLS-2$//$NON-NLS-1$
			}
		}
		
		protected void processRegionCollection(ITextRegionCollection regionCollection, IStructuredDocumentRegion anchorStructuredDocumentRegion, XMLSourceParser textSource) {
			/*
			 * Would test > 1, but since we only care if there are 8 (<%@,
			 * taglib, uri, =, where, prefix, =, what) [or 4 for include
			 * directives]
			 */
			if (regionCollection.getNumberOfRegions() > 4 && regionCollection.getRegions().get(1).getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME) {
				ITextRegion name = regionCollection.getRegions().get(1);
				boolean taglibDetected = false;
				boolean taglibDirectiveDetected = false;
				boolean includeDetected = false;
				boolean includeDirectiveDetected = false;
				int startOffset = regionCollection.getStartOffset(name);
				int textLength = name.getTextLength();

				taglibDetected = textSource.regionMatches(startOffset, textLength, JSP12TLDNames.TAGLIB);
				if (!taglibDetected)
					taglibDirectiveDetected = textSource.regionMatches(startOffset, textLength, JSP12Namespace.ElementName.DIRECTIVE_TAGLIB);
				if (!taglibDirectiveDetected)
					includeDetected = textSource.regionMatches(startOffset, textLength, JSP12TLDNames.INCLUDE);
				if (!includeDetected)
					includeDirectiveDetected = textSource.regionMatches(startOffset, textLength, JSP12Namespace.ElementName.DIRECTIVE_INCLUDE);
				if (taglibDetected || taglibDirectiveDetected) {
					processTaglib(regionCollection, anchorStructuredDocumentRegion, textSource);
				}
				else if (includeDetected || includeDirectiveDetected) {
					processInclude(regionCollection, anchorStructuredDocumentRegion, textSource);
				}
			}
			else if (regionCollection.getNumberOfRegions() > 1 && DOMRegionContext.XML_TAG_OPEN.equals(regionCollection.getFirstRegion().getType())) {
				processXMLStartTag(regionCollection, anchorStructuredDocumentRegion, textSource);
			}			
		}

		public void nodeParsed(IStructuredDocumentRegion structuredDocumentRegion) {
			if (!preludesHandled) {
				handlePreludes();
				preludesHandled = true;
			}
			processRegionCollection(structuredDocumentRegion, structuredDocumentRegion, getParser());
		}

		/**
		 * Process an include directive found by the textSource parser and
		 * anchor any taglibs found within at the
		 * anchorStructuredDocumentRegion. Includes use the including file as
		 * the point of reference, not necessarily the "top" file.
		 */
		protected void processInclude(ITextRegionCollection includeDirectiveCollection, IStructuredDocumentRegion anchorStructuredDocumentRegion, XMLSourceParser textSource) {
			ITextRegionList regions = includeDirectiveCollection.getRegions();
			String includedFile = null;
			boolean isFilename = false;
			try {
				for (int i = 2; includedFile == null && i < regions.size(); i++) {
					ITextRegion region = regions.get(i);
					if (region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
						if (textSource.regionMatches(includeDirectiveCollection.getStartOffset(region), region.getTextLength(), JSP12TLDNames.FILE)) {
							isFilename = true;
						}
						else {
							isFilename = false;
						}
					}
					else if (isFilename && region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
						includedFile = textSource.getText(includeDirectiveCollection.getStartOffset(region), region.getTextLength());
						isFilename = false;
					}
				}
			}
			catch (StringIndexOutOfBoundsException sioobExc) {
				// nothing to be done
				includedFile = null;
			}

			if (fProcessIncludes && includedFile != null) {
				// strip any extraneous quotes and white space
				includedFile = StringUtils.strip(includedFile).trim();
				IPath filePath = null;
				/*
				 * The resolution of the included fragment should use the file
				 * containing the directive as the base reference, not always
				 * the main JSP being invoked. Verified behavior with Apache
				 * Tomcat 5.5.20.
				 */
				IPath modelBaseLocation = TaglibController.getLocation(TLDCMDocumentManager.this);
				if(modelBaseLocation != null) {
					if (getIncludes().isEmpty())
						filePath = FacetModuleCoreSupport.resolve(modelBaseLocation, includedFile);
					else
						filePath = FacetModuleCoreSupport.resolve((IPath) getIncludes().peek(), includedFile);
				}

				// check for "loops"
				if (filePath != null && !getIncludes().contains(filePath) && !filePath.equals(modelBaseLocation)) {
					/*
					 * Prevent slow performance when editing scriptlet part of
					 * the JSP by only processing includes if they've been
					 * modified. The IncludeHelper remembers any CMDocuments
					 * created from the files it parses. Caching the URI and
					 * prefix/tagdir allows us to just enable the CMDocument
					 * when the previously parsed files.
					 * 
					 * REMAINING PROBLEM: fTLDCMReferencesMap does not map
					 * from a fragment's path and also include all of the CM
					 * references in fragments that *it* includes. The
					 * fragments that it includes won't have its CM references
					 * loaded, but then we'd need to record the URI and
					 * location of the included fragment to resolve them
					 * correctly, modifying enableTaglib() to also take a base
					 * path and resolve the URI appropriately.
					 */
					if (hasAnyIncludeBeenModified(filePath)) {
						getIncludes().push(filePath);

						IncludeHelper includeHelper = new IncludeHelper(anchorStructuredDocumentRegion, getParser());
						includeHelper.parse(filePath);
						List references = includeHelper.taglibReferences;
						fTLDCMReferencesMap.put(filePath, references);
						if (getParser() instanceof JSPCapableParser) {
							for (int i = 0; references != null && i < references.size(); i++) {
								TLDCMDocumentReference reference = (TLDCMDocumentReference) references.get(i);
								((JSPCapableParser) getParser()).addNestablePrefix(new TagMarker(reference.prefix + ":")); //$NON-NLS-1$
							}
						}
						/*
						 * TODO: walk up the include hierarchy and add
						 * these references to each of the parents?
						 */

						getIncludes().pop();
					}
					else {
						// Add from that saved list of uris/prefixes/documents
						List references = (List) fTLDCMReferencesMap.get(filePath);
						for (int i = 0; references != null && i < references.size(); i++) {
							TLDCMDocumentReference reference = (TLDCMDocumentReference) references.get(i);
							/*
							 * The uri might not be resolved properly if
							 * relative to the JSP fragment.
							 */
							enableTaglibFromURI(reference.prefix, reference.uri, anchorStructuredDocumentRegion);
							if (getParser() instanceof JSPCapableParser) {
								((JSPCapableParser) getParser()).addNestablePrefix(new TagMarker(reference.prefix + ":")); //$NON-NLS-1$
							}
						}
					}
				}
				else if (getIncludes().contains(filePath)) {
					if (Debug.debugTokenizer)
						System.out.println("LOOP IN @INCLUDES FOUND: " + filePath); //$NON-NLS-1$
				}
			}
		}

		protected void processXMLStartTag(ITextRegionCollection startTagRegionCollection, IStructuredDocumentRegion anchorStructuredDocumentRegion, XMLSourceParser textSource) {
			ITextRegionList regions = startTagRegionCollection.getRegions();
			String uri = null;
			String prefix = null;
			boolean isTaglibValue = false;
			// skip the first two, they're the open bracket and name
			for (int i = 2; i < regions.size(); i++) {
				ITextRegion region = regions.get(i);
				if (region instanceof ITextRegionCollection) {
					// Handle nested directives
					processRegionCollection((ITextRegionCollection) region, anchorStructuredDocumentRegion, textSource);
				}
				else {
					// Handle xmlns:xxx=yyy
					int regionStartOffset = startTagRegionCollection.getStartOffset(region);
					int regionTextLength = region.getTextLength();
					if (region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
						if (regionTextLength > XMLNS_LENGTH && textSource.regionMatches(regionStartOffset, XMLNS_LENGTH, XMLNS)) {
							prefix = textSource.getText(regionStartOffset + XMLNS_LENGTH, regionTextLength - XMLNS_LENGTH);
							if (!bannedPrefixes.contains(prefix))
								isTaglibValue = true;
						}
						else {
							prefix = null;
							isTaglibValue = false;
						}
					}
					else if (isTaglibValue && region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
						if (prefix != null && prefix.length() > 0) {
							uri = textSource.getText(regionStartOffset, regionTextLength);
							uri = StringUtils.strip(uri);
							int uriLength = uri.length();
							if (uri != null && uriLength > 0) {
								if (uriLength > URN_TLD_LENGTH && uri.startsWith(URN_TLD)) {
									uri = uri.substring(URN_TLD_LENGTH);
								}
								else if (uriLength > URN_TAGDIR_LENGTH && uri.startsWith(URN_TAGDIR)) {
									uri = uri.substring(URN_TAGDIR_LENGTH);
								}
								enableTags(prefix, uri, anchorStructuredDocumentRegion);
								uri = null;
								prefix = null;
							}
						}
					}
				}
			}
		}

		/**
		 * Pulls the URI and prefix from the given taglib directive
		 * IStructuredDocumentRegion and makes sure the tags are known.
		 */
		protected void processTaglib(ITextRegionCollection taglibDirectiveCollection, IStructuredDocumentRegion anchorStructuredDocumentRegion, XMLSourceParser textSource) {
			ITextRegionList regions = taglibDirectiveCollection.getRegions();
			String uri = null;
			String prefix = null;
			String tagdir = null;
			String attrName = null;
			try {
				for (int i = 2; i < regions.size(); i++) {
					ITextRegion region = regions.get(i);
					// remember attribute name
					int startOffset = taglibDirectiveCollection.getStartOffset(region);
					int textLength = region.getTextLength();
					if (region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
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
					else if (region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
						if (JSP11TLDNames.PREFIX.equals(attrName))
							prefix = StringUtils.strip(textSource.getText(startOffset, textLength));
						else if (JSP11TLDNames.URI.equals(attrName))
							uri = StringUtils.strip(textSource.getText(startOffset, textLength));
						else if (JSP20TLDNames.TAGDIR.equals(attrName))
							tagdir = StringUtils.strip(textSource.getText(startOffset, textLength));
					}
				}
			}
			catch (StringIndexOutOfBoundsException sioobExc) {
				// nothing to be done
				uri = null;
				prefix = null;
			}
			if (uri != null && prefix != null && uri.length() > 0 && prefix.length() > 0) {
				enableTaglibFromURI(prefix, StringUtils.strip(uri), anchorStructuredDocumentRegion);
			}
			else if (tagdir != null && prefix != null && tagdir.length() > 0 && prefix.length() > 0) {
				enableTagsInDir(StringUtils.strip(prefix), StringUtils.strip(tagdir), anchorStructuredDocumentRegion);
			}
		}

		private void registerTaglib(String prefix, String uri, IStructuredDocumentRegion anchorStructuredDocumentRegion, CMDocument tld) {
			CMNamedNodeMap elements = tld.getElements();
			/*
			 * Go through the CMDocument for any tags that must be marked as
			 * block tags starting at the anchoring IStructuredDocumentRegion.
			 * As the document is edited and the IStructuredDocumentRegion
			 * moved around, the block tag enablement will automatically
			 * follow it.
			 */
			for (int i = 0; i < elements.getLength(); i++) {
				TLDElementDeclaration ed = (TLDElementDeclaration) elements.item(i);
				if (ed.getBodycontent() == JSP12TLDNames.CONTENT_TAGDEPENDENT)
					addBlockTag(prefix + ":" + ed.getNodeName(), anchorStructuredDocumentRegion); //$NON-NLS-1$
			}
			/*
			 * Since modifications to StructuredDocumentRegions adjacent to a
			 * taglib directive can cause that IStructuredDocumentRegion to be
			 * reported, filter out any duplicated URIs. When the taglib is
			 * actually modified, a full rebuild will occur and no duplicates
			 * will/should be found.
			 */
			boolean doTrack = true;
			List trackers = getTaglibTrackers();
			for (int i = 0; i < trackers.size(); i++) {
				TaglibTracker tracker = (TaglibTracker) trackers.get(i);
				if (tracker.getPrefix().equals(prefix) && tracker.getURI().equals(uri)) {
					doTrack = false;
				}
			}
			if (doTrack) {
				addTaglibTracker(prefix, uri, anchorStructuredDocumentRegion, tld);
			}
		}

		private void resetBlockTags() {
			if (getParser() == null)
				return;
			Iterator names = getParser().getBlockMarkers().iterator();
			while (names.hasNext()) {
				BlockMarker marker = (BlockMarker) names.next();
				if (!marker.isGlobal() && marker.getContext() == DOMRegionContext.BLOCK_TEXT) {
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
			Assert.isTrue(newDocument != null, "null document"); //$NON-NLS-1$
			Assert.isTrue(newDocument.getParser() != null, "null document parser"); //$NON-NLS-1$
			Assert.isTrue(newDocument.getParser() instanceof XMLSourceParser, "can only listen to document with a XMLSourceParser"); //$NON-NLS-1$
			getSourceParser().removeStructuredDocumentRegionHandler(this);
			setSourceParser((XMLSourceParser) newDocument.getParser());
			getSourceParser().addStructuredDocumentRegionHandler(this);
		}
	}

	protected class IncludeHelper extends DirectiveStructuredDocumentRegionHandler {
		protected IStructuredDocumentRegion fAnchor = null;
		protected XMLSourceParser fLocalParser = null;
		protected XMLSourceParser fParentParser = null;
		List taglibReferences = null;

		public IncludeHelper(IStructuredDocumentRegion anchor, XMLSourceParser rootParser) {
			super();
			fAnchor = anchor;
			fParentParser = rootParser;
			taglibReferences = new ArrayList(0);
		}

		protected void addTaglibTracker(String prefix, String uri, IStructuredDocumentRegion anchorStructuredDocumentRegion, CMDocument tldCMDocument) {
			super.addTaglibTracker(prefix, uri, anchorStructuredDocumentRegion, tldCMDocument);
			TLDCMDocumentReference reference = new TLDCMDocumentReference();
			reference.prefix = prefix;
			reference.uri = uri;
			taglibReferences.add(reference);
		}

		protected String getContents(IPath filePath) {
			return FileContentCache.getInstance().getContents(filePath);
		}

		public void nodeParsed(IStructuredDocumentRegion structuredDocumentRegion) {
			processRegionCollection(structuredDocumentRegion, fAnchor, fLocalParser);
		}

		/**
		 * @param path -
		 *            the fullpath for the resource to be parsed
		 */
		void parse(IPath path) {
			XMLSourceParser p = (XMLSourceParser) getParser().newInstance();
			fLocalParser = p;
			String s = getContents(path);
			// Should we consider preludes on this segment?
			fLocalParser.addStructuredDocumentRegionHandler(IncludeHelper.this);
			fLocalParser.reset(s);
			List blockTags = fParentParser.getBlockMarkers();
			for (int i = 0; i < blockTags.size(); i++) {
				BlockMarker marker = (BlockMarker) blockTags.get(i);
				fLocalParser.addBlockMarker(new BlockMarker(marker.getTagName(), null, marker.getContext(), marker.isCaseSensitive()));
			}
			if (fParentParser instanceof JSPCapableParser && fLocalParser instanceof JSPCapableParser) {
				TagMarker[] knownPrefixes = (TagMarker[]) ((JSPCapableParser) fParentParser).getNestablePrefixes().toArray(new TagMarker[0]);
				for (int i = 0; i < knownPrefixes.length; i++) {
					((JSPCapableParser) fLocalParser).addNestablePrefix(new TagMarker(knownPrefixes[i].getTagName(), null));
				}
			}
			// force parse
			fLocalParser.getDocumentRegions();
			fLocalParser = null;
		}

		public void resetNodes() {
		}
	}

	/**
	 * An entry in the shared cache map
	 */
	static class TLDCacheEntry {
		CMDocument document;
		long modificationStamp;
		int referenceCount;
	}

	private class TLDCMDocumentReference {
		String prefix;
		String uri;
	}

	static final boolean _debug = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/tldcmdocument/manager")); //$NON-NLS-1$ //$NON-NLS-2$
	static final boolean _debugCache = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/tldcmdocument/cache")); //$NON-NLS-1$ //$NON-NLS-2$
	// will hold the prefixes banned by the specification; taglibs may not use
	// them
	protected static List bannedPrefixes = null;

	private static Hashtable fCache = null;
	final String XMLNS = "xmlns:"; //$NON-NLS-1$ 
	final String URN_TAGDIR = "urn:jsptagdir:";
	final String URN_TLD = "urn:jsptld:";

	final int XMLNS_LENGTH = XMLNS.length();
	final int URN_TAGDIR_LENGTH = URN_TAGDIR.length();
	final int URN_TLD_LENGTH = URN_TLD.length();

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

	/**
	 * Gets all of the known documents.
	 * 
	 * @return Returns a Hashtable of either TLDCacheEntrys or WeakReferences
	 *         to TLD CMDocuments
	 */
	public static Hashtable getSharedDocumentCache() {
		if (fCache == null) {
			fCache = new Hashtable();
		}
		return fCache;
	}


	public static Object getUniqueIdentifier(ITaglibRecord reference) {
		if (reference == null)
			return null;
		Object identifier = null;
		switch (reference.getRecordType()) {
			case (ITaglibRecord.TLD) : {
				ITLDRecord record = (ITLDRecord) reference;
				identifier = record.getPath();
			}
				break;
			case (ITaglibRecord.JAR) : {
				IJarRecord record = (IJarRecord) reference;
				identifier = record.getLocation();
			}
				break;
			case (ITaglibRecord.TAGDIR) : {
				ITagDirRecord record = (ITagDirRecord) reference;
				identifier = record.getPath();
			}
				break;
			case (ITaglibRecord.URL) : {
				IURLRecord record = (IURLRecord) reference;
				identifier = record.getURL();
			}
				break;
			default :
				identifier = reference;
				break;
		}
		return identifier;
	}

	private CMDocumentFactoryTLD fCMDocumentBuilder = null;

	private DirectiveStructuredDocumentRegionHandler fDirectiveHandler = null;

	/**
	 * The locally-know list of CMDocuments
	 */
	private Hashtable fDocuments = null;

	// timestamp cache to prevent excessive reparsing
	// of included files
	// IPath (filepath) > Long (modification stamp)
	HashMap fInclude2TimestampMap = new HashMap();

	private Stack fIncludes = null;

	private XMLSourceParser fParser = null;

	private List fTaglibTrackers = null;

	Map fTLDCMReferencesMap = new HashMap();
	boolean fProcessIncludes = true;
	boolean preludesHandled = false;

	public TLDCMDocumentManager() {
		super();
	}

	public void clearCache() {
		if (_debugCache) {
			System.out.println("TLDCMDocumentManager cleared its private CMDocument cache"); //$NON-NLS-1$
		}
		for (Iterator iter = getDocuments().keySet().iterator(); iter.hasNext();) {
			Object key = iter.next();
			synchronized (getSharedDocumentCache()) {
				Object o = getSharedDocumentCache().get(key);
				if (o instanceof TLDCacheEntry) {
					TLDCacheEntry entry = (TLDCacheEntry) o;
					entry.referenceCount--;
					if (entry.referenceCount <= 0) {
						getSharedDocumentCache().put(key, new SoftReference(entry));
					}
				}
			}
		}
	}

	/**
	 * Derives an unique cache key for the give URI. The URI is "resolved" and
	 * a unique value generated from the result. This ensures that two
	 * different relative references from different files do not have
	 * overlapping TLD records in the shared cache if they don't resolve to
	 * the same TLD.
	 * 
	 * @param uri
	 * @return
	 */
	protected Object getCacheKey(String uri) {
		IPath currentParserPath = getCurrentParserPath();
		if (currentParserPath == null)
			return null;
		
		ITaglibRecord record = TaglibIndex.resolve(currentParserPath.toString(), uri, false);
		if (record != null) {
			return getUniqueIdentifier(record);
		}
		String location = URIResolverPlugin.createResolver().resolve(getCurrentBaseLocation().toString(), null, uri);
		return location;
	}

	/**
	 * Return the CMDocument at the uri (cached)
	 */
	protected CMDocument getCMDocument(String uri) {
		if (uri == null || uri.length() == 0)
			return null;
		String reference = uri;
		Object cacheKey = getCacheKey(reference);
		if (cacheKey == null)
			return null;
		
		CMDocument doc = (CMDocument) getDocuments().get(cacheKey);
		if (doc == null) {
			long lastModified = getModificationStamp(reference);
			/*
			 * If hasn't been moved into the local table, do so and increment
			 * the count. A local URI reference can be different depending on
			 * the file from which it was referenced. Use a computed key to
			 * keep them straight.
			 */
			Object o = getSharedDocumentCache().get(cacheKey);
			if (o != null) {
				if (o instanceof TLDCacheEntry) {
					TLDCacheEntry entry = (TLDCacheEntry) o;
					if (_debugCache) {
						System.out.println("TLDCMDocument cache hit on " + cacheKey);
					}
					if (entry != null && entry.modificationStamp != IResource.NULL_STAMP && entry.modificationStamp >= lastModified) {
						doc = entry.document;
						entry.referenceCount++;
					}
					else {
						getSharedDocumentCache().remove(cacheKey);
					}
				}
				else if (o instanceof Reference) {
					TLDCacheEntry entry = (TLDCacheEntry) ((Reference) o).get();
					if (entry != null) {
						if (entry.modificationStamp != IResource.NULL_STAMP && entry.modificationStamp >= lastModified) {
							doc = entry.document;
							entry.referenceCount = 1;
							getSharedDocumentCache().put(cacheKey, entry);
						}
					}
					else {
						getSharedDocumentCache().remove(cacheKey);
					}
				}
			}
			/* No document was found cached, create a new one and share it */
			if (doc == null) {
				if (_debugCache) {
					System.out.println("TLDCMDocument cache miss on " + cacheKey);
				}
				CMDocument document = loadTaglib(reference);
				if (document != null) {
					TLDCacheEntry entry = new TLDCacheEntry();
					doc = entry.document = document;
					entry.referenceCount = 1;
					entry.modificationStamp = lastModified;
					getSharedDocumentCache().put(cacheKey, entry);
				}
			}
			if (doc != null) {
				getDocuments().put(cacheKey, doc);
			}
		}
		return doc;
	}

	private long getModificationStamp(String reference) {
		IPath currentParserPath = getCurrentParserPath();
		if (currentParserPath == null) {
			return IResource.NULL_STAMP;
		}
		
		ITaglibRecord record = TaglibIndex.resolve(currentParserPath.toString(), reference, false);
		long modificationStamp = IResource.NULL_STAMP;
		if (record != null) {
			switch (record.getRecordType()) {
				case (ITaglibRecord.TLD) : {
					IFile tldfile = ResourcesPlugin.getWorkspace().getRoot().getFile(((ITLDRecord) record).getPath());
					if (tldfile.isAccessible()) {
						modificationStamp = tldfile.getModificationStamp();
					}
				}
					break;
				case (ITaglibRecord.JAR) : {
					File jarfile = new File(((IJarRecord) record).getLocation().toOSString());
					if (jarfile.exists()) {
						try {
							modificationStamp = jarfile.lastModified();
						}
						catch (SecurityException e) {
							modificationStamp = IResource.NULL_STAMP;
						}
					}
				}
					break;
				case (ITaglibRecord.TAGDIR) : {
					IFolder tagFolder = ResourcesPlugin.getWorkspace().getRoot().getFolder(((ITagDirRecord) record).getPath());
					if (tagFolder.isAccessible()) {
						IResource[] members;
						try {
							members = tagFolder.members();
							for (int i = 0; i < members.length; i++) {
								modificationStamp = Math.max(modificationStamp, members[i].getModificationStamp());
							}
						}
						catch (CoreException e) {
							modificationStamp = IResource.NULL_STAMP;
						}
					}
				}
					break;
				case (ITaglibRecord.URL) : {
                    String loc = ((IURLRecord) record).getBaseLocation();
                    if (loc != null && loc.endsWith(".jar")) { //$NON-NLS-1$
                        File jarfile = new File(loc);
                        if (jarfile.exists()) {
                            try {
                                modificationStamp = jarfile.lastModified();
                            }
                            catch (SecurityException e) {
                                modificationStamp = IResource.NULL_STAMP;
                            }
                        }
                    }
				}
					break;
				default :
					break;
			}
		}
		return modificationStamp;
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
		Object[] alldocs = getTaglibTrackers().toArray();
		for (int i = 0; i < alldocs.length; i++) {
			TaglibTracker aTracker = (TaglibTracker) alldocs[i];
			if (aTracker.getStructuredDocumentRegion().getStartOffset() <= offset || offset < 0) {
				validDocs.add(aTracker);
			}
		}
		return validDocs;
	}

	public List getCMDocumentTrackers(String prefix, int offset) {
		List validDocs = new ArrayList();
		Object[] alldocs = getTaglibTrackers().toArray();
		for (int i = 0; i < alldocs.length; i++) {
			TaglibTracker aTracker = (TaglibTracker) alldocs[i];
			/**
			 * '<' is used to support the immediate use of a custom tag in jspx files (instead of '<=')
			 */
			if ((aTracker.getStructuredDocumentRegion().getStartOffset() <= offset || offset < 0) && aTracker.getPrefix().equals(prefix)) {
				validDocs.add(aTracker);
			}
		}
		return validDocs;
	}

	/**
	 * Return the filesystem location in the current parser. This method is
	 * called while recursing through included fragments, so it much check the
	 * include stack. The filesystem location is needed for common URI
	 * resolution in case the Taglib Index doesn't know the URI being loaded.
	 * 
	 * @return
	 */
	IPath getCurrentBaseLocation() {
		IPath baseLocation = null;
		IPath path = getCurrentParserPath();
		if (path == null || path.segmentCount() < 2)
			return path;
		baseLocation = ResourcesPlugin.getWorkspace().getRoot().getFile(path).getLocation();
		if (baseLocation == null) {
			baseLocation = path;
		}
		return baseLocation;
	}

	/**
	 * Return the path used in the current parser. This method is called while
	 * recursing through included fragments, so it much check the include
	 * stack.
	 * 
	 * @return
	 */
	IPath getCurrentParserPath() {
		IPath path = null;
		if (!getIncludes().isEmpty()) {
			path = (IPath) getIncludes().peek();
		}
		else {
			path = TaglibController.getLocation(this);
		}

		return path;
	}

	protected DirectiveStructuredDocumentRegionHandler getDirectiveStructuredDocumentRegionHandler() {
		if (fDirectiveHandler == null)
			fDirectiveHandler = new DirectiveStructuredDocumentRegionHandler();
		return fDirectiveHandler;
	}

	/**
	 * Gets the documents.
	 * 
	 * @return Returns a java.util.Hashtable
	 */
	public Hashtable getDocuments() {
		if (fDocuments == null)
			fDocuments = new Hashtable();
		return fDocuments;
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

	XMLSourceParser getParser() {
		return fParser;
	}

	public XMLSourceParser getSourceParser() {
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

	void handlePreludes() {
		IStructuredDocumentRegion anchor = new ZeroStructuredDocumentRegion(null, -1);
		fProcessIncludes = false;

		IPath currentPath = getCurrentParserPath();
		if (currentPath != null) {
			PropertyGroup[] propertyGroups = DeploymentDescriptorPropertyCache.getInstance().getPropertyGroups(currentPath);
			for(int k = 0; k < propertyGroups.length; k++) {
				IPath[] preludes = propertyGroups[k].getIncludePrelude();
				for (int i = 0; i < preludes.length; i++) {
					if (!getIncludes().contains(preludes[i]) && !preludes[i].equals(currentPath)) {
						getIncludes().push(preludes[i]);
						if (getParser() != null) {
							IncludeHelper includeHelper = new IncludeHelper(anchor, getParser());
							includeHelper.parse(preludes[i]);
							List references = includeHelper.taglibReferences;
							fTLDCMReferencesMap.put(preludes[i], references);
							if (getParser() instanceof JSPCapableParser) {
								for (int j = 0; j < references.size(); j++) {
									TLDCMDocumentReference reference = (TLDCMDocumentReference) references.get(j);
									((JSPCapableParser) getParser()).addNestablePrefix(new TagMarker(reference.prefix + ":")); //$NON-NLS-1$
								}
							}
						}
						else
							Logger.log(Logger.WARNING, "Warning: parser text was requested by " + getClass().getName() + " but none was available; taglib support disabled"); //$NON-NLS-1$ //$NON-NLS-2$
						getIncludes().pop();
					}
				}
			}
		}

		fProcessIncludes = true;
	}

	/**
	 * @param filePath
	 *            the path to check for modification
	 */
	boolean hasAnyIncludeBeenModified(IPath filePath) {
		boolean result = false;
		// check the top level
		if (hasBeenModified(filePath)) {
			result = true;
		}
		else {
			// check all includees
			Iterator iter = fInclude2TimestampMap.keySet().iterator();
			while (iter.hasNext()) {
				if (hasBeenModified((IPath) iter.next())) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * @param filename
	 * @return
	 */
	boolean hasBeenModified(IPath filePath) {
		boolean result = false;
		// quick filename/timestamp cache check here...
		IFile f = null;
		if (f == null && filePath.segmentCount() > 1) {
			f = ResourcesPlugin.getWorkspace().getRoot().getFile(filePath);
		}
		if (f != null && f.exists()) {
			Long currentStamp = new Long(f.getModificationStamp());
			Object o = fInclude2TimestampMap.get(filePath);
			if (o != null) {
				Long previousStamp = (Long) o;
				// stamps don't match, file changed
				if (currentStamp.longValue() != previousStamp.longValue()) {
					result = true;
					// store for next time
					fInclude2TimestampMap.put(filePath, currentStamp);
				}
			}
			else {
				// return true, since we've not encountered this file yet.
				result = true;
				// store for next time
				fInclude2TimestampMap.put(filePath, currentStamp);
			}
		}
		return result;
	}

	public void indexChanged(ITaglibIndexDelta event) {
		synchronized (getSharedDocumentCache()) {
			Iterator values = getSharedDocumentCache().values().iterator();
			while (values.hasNext()) {
				Object o = values.next();
				if (o instanceof Reference) {
					values.remove();
				}
			}
		}
	}

	/**
	 * Loads the taglib from the specified URI. It must point to a valid
	 * taglib descriptor to work.
	 */
	protected CMDocument loadTaglib(String uri) {
		CMDocument document = null;
		IPath currentPath = getCurrentParserPath();
		if (currentPath != null) {
			ITaglibRecord record = TaglibIndex.resolve(currentPath.toString(), uri, false);
			if (record != null) {
				document = getCMDocumentBuilder().createCMDocument(record);
			}
			else {
				/* Not a very-often used code path (we hope) */
				IPath currentBaseLocation = getCurrentBaseLocation();
				if (currentBaseLocation != null) {
					String location = URIResolverPlugin.createResolver().resolve(currentBaseLocation.toString(), null, uri);
					if (location != null) {
						if (_debug) {
							System.out.println("Loading tags from " + uri + " at " + location); //$NON-NLS-2$//$NON-NLS-1$
						}
						document = getCMDocumentBuilder().createCMDocument(location);
					}
				}
			}
		}
		return document;
	}

	protected void resetTaglibTrackers() {
		if (_debug) {
			System.out.println("TLDCMDocumentManager cleared its taglib trackers\n"); //$NON-NLS-1$
		}
		preludesHandled = false;
		getTaglibTrackers().clear();
	}

	public void setSourceParser(XMLSourceParser parser) {
		if (fParser != null)
			fParser.removeStructuredDocumentRegionHandler(getStructuredDocumentRegionHandler());
		fParser = parser;
		if (fParser != null)
			fParser.addStructuredDocumentRegionHandler(getStructuredDocumentRegionHandler());
	}
}
