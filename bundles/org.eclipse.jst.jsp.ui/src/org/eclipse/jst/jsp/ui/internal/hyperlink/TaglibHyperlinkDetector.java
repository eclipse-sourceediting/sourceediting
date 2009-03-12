/*******************************************************************************
 * Copyright (c) 2006, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.hyperlink;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.URLHyperlink;
import org.eclipse.jst.jsp.core.internal.contentmodel.TaglibController;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.CMElementDeclarationImpl;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.TLDCMDocumentManager;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.TaglibTracker;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.JSP11TLDNames;
import org.eclipse.jst.jsp.core.internal.provisional.JSP11Namespace;
import org.eclipse.jst.jsp.core.internal.provisional.JSP12Namespace;
import org.eclipse.jst.jsp.core.taglib.ITLDRecord;
import org.eclipse.jst.jsp.core.taglib.ITaglibRecord;
import org.eclipse.jst.jsp.core.taglib.TaglibIndex;
import org.eclipse.jst.jsp.core.text.IJSPPartitions;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.jst.jsp.ui.internal.Logger;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredPartitioning;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionCollection;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.provisional.contentmodel.CMNodeWrapper;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Detects hyperlinks for taglibs.
 */
public class TaglibHyperlinkDetector extends AbstractHyperlinkDetector {
	private final String HTTP_PROTOCOL = "http://";//$NON-NLS-1$
	private final String JAR_PROTOCOL = "jar:file:";//$NON-NLS-1$
	// private String URN_TAGDIR = "urn:jsptagdir:";
	private String URN_TLD = "urn:jsptld:";
	private String XMLNS = "xmlns:"; //$NON-NLS-1$ 
	
	static final int TAG = 1;
	static final int ATTRIBUTE = 2;
	
	static IRegion findDefinition(IDOMModel model, String searchName, int searchType) {
		NodeList declarations = null;
		if (searchType == TAG)
			declarations = model.getDocument().getElementsByTagNameNS("*", JSP11TLDNames.TAG);
		else if (searchType == ATTRIBUTE)
			declarations = model.getDocument().getElementsByTagNameNS("*", JSP11TLDNames.ATTRIBUTE);
		if (declarations == null || declarations.getLength() == 0) {
			if (searchType == TAG)
				declarations = model.getDocument().getElementsByTagName(JSP11TLDNames.TAG);
			else if (searchType == ATTRIBUTE)
				declarations = model.getDocument().getElementsByTagName(JSP11TLDNames.ATTRIBUTE);
		}
		for (int i = 0; i < declarations.getLength(); i++) {
			NodeList names = model.getDocument().getElementsByTagName(JSP11TLDNames.NAME);
			for (int j = 0; j < names.getLength(); j++) {
				String name = getContainedText(names.item(j));
				if (searchName.compareTo(name) == 0) {
					int start = -1;
					int end = -1;
					Node caret = names.item(j).getFirstChild();
					if (caret != null) {
						start = ((IDOMNode) caret).getStartOffset();
					}
					while (caret != null) {
						end = ((IDOMNode) caret).getEndOffset();
						caret = caret.getNextSibling();
					}
					if (start > 0) {
						return new Region(start, end - start);
					}
				}
			}
		}

		return null;
	}

	private static String getContainedText(Node parent) {
		NodeList children = parent.getChildNodes();
		if (children.getLength() == 1) {
			return children.item(0).getNodeValue().trim();
		}
		StringBuffer s = new StringBuffer();
		Node child = parent.getFirstChild();
		while (child != null) {
			if (child.getNodeType() == Node.ENTITY_REFERENCE_NODE) {
				String reference = ((EntityReference) child).getNodeValue();
				if (reference == null && child.getNodeName() != null) {
					reference = "&" + child.getNodeName() + ";"; //$NON-NLS-1$ //$NON-NLS-2$
				}
				if (reference != null) {
					s.append(reference.trim());
				}
			}
			else {
				s.append(child.getNodeValue().trim());
			}
			child = child.getNextSibling();
		}
		return s.toString().trim();
	}

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		IHyperlink hyperlink = null;

		if (textViewer != null && region != null) {
			IDocument doc = textViewer.getDocument();
			if (doc != null) {
				try {
					// check if jsp tag/directive first
					ITypedRegion partition = TextUtilities.getPartition(doc, IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING, region.getOffset(), false);
					if (partition != null && partition.getType() == IJSPPartitions.JSP_DIRECTIVE) {
						IStructuredModel sModel = null;
						try {
							sModel = StructuredModelManager.getModelManager().getExistingModelForRead(doc);
							// check if jsp taglib directive
							Node currentNode = getCurrentNode(sModel, region.getOffset());
							if (currentNode != null && currentNode.getNodeType() == Node.ELEMENT_NODE) {
								String baseLocationForTaglib = getBaseLocationForTaglib(doc);
								if (baseLocationForTaglib != null && JSP11Namespace.ElementName.DIRECTIVE_TAGLIB.equalsIgnoreCase(currentNode.getNodeName())) {
									/**
									 * The taglib directive itself
									 */
									// get the uri attribute
									Attr taglibURINode = ((Element) currentNode).getAttributeNode(JSP11Namespace.ATTR_NAME_URI);
									if (taglibURINode != null) {
										ITaglibRecord reference = TaglibIndex.resolve(baseLocationForTaglib, taglibURINode.getValue(), false);
										// when using a tagdir
										// (ITaglibRecord.TAGDIR),
										// there's nothing to link to
										if (reference != null) {
											// handle taglibs
											switch (reference.getRecordType()) {
												case (ITaglibRecord.TLD) : {
													ITLDRecord record = (ITLDRecord) reference;
													String uriString = record.getPath().toString();
													IRegion hyperlinkRegion = getHyperlinkRegion(taglibURINode, region);
													if (hyperlinkRegion != null) {
														hyperlink = createHyperlink(uriString, hyperlinkRegion, doc, null);
													}
												}
													break;
												case (ITaglibRecord.JAR) :
												case (ITaglibRecord.URL) : {
													IRegion hyperlinkRegion = getHyperlinkRegion(taglibURINode, region);
													if (hyperlinkRegion != null) {
														hyperlink = new TaglibJarUriHyperlink(hyperlinkRegion, reference);
													}
												}
											}
										}
									}
								}
								else if (baseLocationForTaglib != null && JSP12Namespace.ElementName.ROOT.equalsIgnoreCase(currentNode.getNodeName())) {
									/**
									 * The jsp:root element
									 */
									NamedNodeMap attrs = currentNode.getAttributes();
									for (int i = 0; i < attrs.getLength(); i++) {
										Attr attr = (Attr) attrs.item(i);
										if (attr.getNodeName().startsWith(XMLNS)) {
											String uri = StringUtils.strip(attr.getNodeValue());
											if (uri.startsWith(URN_TLD)) {
												uri = uri.substring(URN_TLD.length());
											}
											ITaglibRecord reference = TaglibIndex.resolve(baseLocationForTaglib, uri, false);
											// when using a tagdir
											// (ITaglibRecord.TAGDIR),
											// there's nothing to link to
											if (reference != null) {
												// handle taglibs
												switch (reference.getRecordType()) {
													case (ITaglibRecord.TLD) : {
														ITLDRecord record = (ITLDRecord) reference;
														String uriString = record.getPath().toString();
														IRegion hyperlinkRegion = getHyperlinkRegion(attr, region);
														if (hyperlinkRegion != null) {
															hyperlink = createHyperlink(uriString, hyperlinkRegion, doc, null);
														}
													}
														break;
													case (ITaglibRecord.JAR) :
													case (ITaglibRecord.URL) : {
														IRegion hyperlinkRegion = getHyperlinkRegion(attr, region);
														if (hyperlinkRegion != null) {
															hyperlink = new TaglibJarUriHyperlink(hyperlinkRegion, reference);
														}
													}
												}
											}
										}
									}
								}
								else {
									/**
									 * Hyperlink custom tag to its TLD or tag file
									 */
									TLDCMDocumentManager documentManager = TaglibController.getTLDCMDocumentManager(doc);
									if (documentManager != null) {
										List documentTrackers = documentManager.getCMDocumentTrackers(currentNode.getPrefix(), region.getOffset());
										for (int i = 0; i < documentTrackers.size(); i++) {
											TaglibTracker tracker = (TaglibTracker) documentTrackers.get(i);
											CMElementDeclaration decl = (CMElementDeclaration) tracker.getElements().getNamedItem(currentNode.getNodeName());
											if (decl != null) {
												decl = (CMElementDeclaration) ((CMNodeWrapper) decl).getOriginNode();
												if (decl instanceof CMElementDeclarationImpl) {
													String base = ((CMElementDeclarationImpl) decl).getLocationString();
													IRegion hyperlinkRegion = getHyperlinkRegion(currentNode, region);
													if (hyperlinkRegion != null) {
														hyperlink = createHyperlink(base, hyperlinkRegion, doc, currentNode);
													}
												}
											}
										}
									}
								}
							}
						}
						finally {
							if (sModel != null)
								sModel.releaseFromRead();
						}							
					}
				}
				catch (BadLocationException e) {
					Logger.log(Logger.WARNING_DEBUG, e.getMessage(), e);
				}
			}
		}
		if (hyperlink != null)
			return new IHyperlink[]{hyperlink};
		return null;
	}

	/**
	 * Get the base location from the current model (if within workspace,
	 * location is relative to workspace, otherwise, file system path)
	 */
	private String getBaseLocationForTaglib(IDocument document) {
		String baseLoc = null;

		// get the base location from the current model
		IStructuredModel sModel = null;
		try {
			sModel = StructuredModelManager.getModelManager().getExistingModelForRead(document);
			if (sModel != null) {
				baseLoc = sModel.getBaseLocation();
			}
		}
		finally {
			if (sModel != null) {
				sModel.releaseFromRead();
			}
		}
		return baseLoc;
	}

	// the below methods were copied from URIHyperlinkDetector

	private IRegion getHyperlinkRegion(Node node, IRegion boundingRegion) {
		IRegion hyperRegion = null;

		if (node != null) {
			short nodeType = node.getNodeType();
			if (nodeType == Node.DOCUMENT_TYPE_NODE) {
				// handle doc type node
				IDOMNode docNode = (IDOMNode) node;
				hyperRegion = new Region(docNode.getStartOffset(), docNode.getEndOffset() - docNode.getStartOffset());
			}
			else if (nodeType == Node.ATTRIBUTE_NODE) {
				// handle attribute nodes
				IDOMAttr att = (IDOMAttr) node;
				// do not include quotes in attribute value region
				int regOffset = att.getValueRegionStartOffset();
				ITextRegion valueRegion = att.getValueRegion();
				if (valueRegion != null) {
					int regLength = valueRegion.getTextLength();
					String attValue = att.getValueRegionText();
					if (StringUtils.isQuoted(attValue)) {
						++regOffset;
						regLength = regLength - 2;
					}
					hyperRegion = new Region(regOffset, regLength);
				}
			}
			if (nodeType == Node.ELEMENT_NODE) {
				// Handle doc type node
				IDOMNode docNode = (IDOMNode) node;
				hyperRegion = getNameRegion(docNode.getFirstStructuredDocumentRegion());
				if (hyperRegion == null) {
					hyperRegion = new Region(docNode.getStartOffset(), docNode.getFirstStructuredDocumentRegion().getTextLength());
				}
			}
		}
		/**
		 * Only return a hyperlink region that overlaps the search region.
		 * This will help us to not underline areas not under the cursor.
		 */
		if (hyperRegion != null && intersects(hyperRegion, boundingRegion))
			return hyperRegion;
		return null;
	}

	private boolean intersects(IRegion hyperlinkRegion, IRegion detectionRegion) {
		int hyperLinkStart = hyperlinkRegion.getOffset();
		int hyperLinkEnd = hyperlinkRegion.getOffset() + hyperlinkRegion.getLength();
		int detectionStart = detectionRegion.getOffset();
		int detectionEnd = detectionRegion.getOffset() + detectionRegion.getLength();
		return (hyperLinkStart <= detectionStart && detectionStart < hyperLinkEnd) || (hyperLinkStart <= detectionEnd && detectionEnd <= hyperLinkEnd);// ||
	}

	private IRegion getNameRegion(ITextRegionCollection containerRegion) {
		ITextRegionList regions = containerRegion.getRegions();
		ITextRegion nameRegion = null;
		for (int i = 0; i < regions.size(); i++) {
			ITextRegion r = regions.get(i);
			if (r.getType() == DOMRegionContext.XML_TAG_NAME) {
				nameRegion = r;
				break;
			}
		}
		if (nameRegion != null)
			return new Region(containerRegion.getStartOffset(nameRegion), nameRegion.getTextLength());
		return null;
	}

	/**
	 * Create the appropriate hyperlink
	 * 
	 * @param uriString
	 * @param hyperlinkRegion
	 * @return IHyperlink
	 */
	private IHyperlink createHyperlink(String uriString, IRegion hyperlinkRegion, IDocument document, Node node) {
		IHyperlink link = null;

		if (uriString != null) {
			String temp = uriString.toLowerCase();
			if (temp.startsWith(HTTP_PROTOCOL)) {
				// this is a URLHyperlink since this is a web address
				link = new URLHyperlink(hyperlinkRegion, uriString);
			}
			else if (temp.startsWith(JAR_PROTOCOL)) {
				// this is a URLFileHyperlink since this is a local address
				try {
					link = new URLFileRegionHyperlink(hyperlinkRegion, TAG, node.getLocalName(), new URL(uriString)) {
						public String getHyperlinkText() {
							return JSPUIMessages.CustomTagHyperlink_hyperlinkText;
						}
					};
				}
				catch (MalformedURLException e) {
					Logger.log(Logger.WARNING_DEBUG, e.getMessage(), e);
				}
			}
			else {
				// try to locate the file in the workspace
				IPath path = new Path(uriString);
				if (path.segmentCount() > 1) {
					IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
					if (file.getType() == IResource.FILE && file.isAccessible()) {
						if (node != null) {
							link = new TLDFileHyperlink(file, TAG, node.getLocalName(), hyperlinkRegion) {
								public String getHyperlinkText() {
									return JSPUIMessages.CustomTagHyperlink_hyperlinkText;
								}
							};
						}
						else {
							link = new WorkspaceFileHyperlink(hyperlinkRegion, file);
						}
					}
				}
			}
			if (link == null) {
				// this is an ExternalFileHyperlink since file does not exist
				// in workspace
				File externalFile = new File(uriString);
				link = new ExternalFileHyperlink(hyperlinkRegion, externalFile);
			}
		}

		return link;
	}

	private Node getCurrentNode(IStructuredModel model, int offset) {
		// get the current node at the offset (returns either: element,
		// doctype, text)
		IndexedRegion inode = null;
		if (model != null) {
			inode = model.getIndexedRegion(offset);
			if (inode == null) {
				inode = model.getIndexedRegion(offset - 1);
			}
		}

		if (inode instanceof Node) {
			return (Node) inode;
		}
		return null;
	}
}
