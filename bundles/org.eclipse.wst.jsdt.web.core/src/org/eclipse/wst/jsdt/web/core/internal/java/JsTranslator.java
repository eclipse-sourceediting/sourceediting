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
package org.eclipse.wst.jsdt.web.core.internal.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.Position;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionCollection;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

/**
 * Translates a JSP document into a HttpServlet. Keeps two way mapping from java
 * translation to the original JSP source, which can be obtained through
 * getJava2JspRanges() and getJsp2JavaRanges().
 * 
 * @author pavery
 */
public class JsTranslator {
	// for debugging
	private static final boolean DEBUG;
	private static final boolean DEBUG_SAVE_OUTPUT = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.jsdt.web.core/debug/jsptranslationstodisk")); //$NON-NLS-1$  //$NON-NLS-2$
	public static final String ENDL = "\n"; //$NON-NLS-1$
	static {
		String value = Platform.getDebugOption("org.eclipse.wst.jsdt.web.core/debug/jspjavamapping"); //$NON-NLS-1$
		DEBUG = value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
	}
	private String fClassname;
	private IStructuredDocumentRegion fCurrentNode;
	/* map of ALL ranges */
	/**
	 * save JSP document text for later use may just want to read this from the
	 * file or strucdtured document depending what is available
	 */
	private StringBuffer fHtmlTextBuffer = new StringBuffer();
	private IProgressMonitor fProgressMonitor = null;
	StringBuffer fScriptText = new StringBuffer();
	private IStructuredDocument fStructuredDocument = null;
	private IDOMModel fStructuredModel = null;
	private ArrayList importLocationsInHtml = new ArrayList();
	/* use java script by default */
	private boolean isGlobalJs = true;
	private ArrayList rawImports = new ArrayList(); // traslated
	private ArrayList scriptLocationInHtml = new ArrayList();
	private int scriptOffset = 0;
	
	private void advanceNextNode() {
		setCurrentNode(getCurrentNode().getNext());
	}
	
	private void configure(IDOMNode node, IProgressMonitor monitor) {
		fProgressMonitor = monitor;
		fStructuredModel = node.getModel();
		fStructuredDocument = fStructuredModel.getStructuredDocument();
		String className = createClassname(node);
		if (className.length() > 0) {
			setClassname(className);
		}
	}
	
	/**
	 * @param node
	 * @return
	 */
	private String createClassname(IDOMNode node) {
		String classname = ""; //$NON-NLS-1$
		if (node != null) {
			String base = node.getModel().getBaseLocation();
			classname = JsNameManglerUtil.mangle(base);
		}
		return classname;
	}
	
	public String getClassname() {
		return this.fClassname != null ? this.fClassname : "GenericJSSourceFile"; //$NON-NLS-1$
	}
	
	final public IStructuredDocumentRegion getCurrentNode() {
		return fCurrentNode;
	}
	
	/**
	 * @return just the "shell" of a servlet, nothing contributed from the JSP
	 *         doc
	 */
	public final StringBuffer getEmptyTranslation() {
		reset();
		return getTranslation();
	}
	
	public IFile getFile() {
		return FileBuffers.getWorkspaceFileAtLocation(new Path(fStructuredModel.getBaseLocation()));
	}
	
	public Position[] getHtmlLocations() {
		return (Position[]) scriptLocationInHtml.toArray(new Position[scriptLocationInHtml.size()]);
	}
	
	/**
	 * Only valid after a configure(...), translate(...) or
	 * translateFromFile(...) call
	 * 
	 * @return the text in the JSP file
	 */
	public final String getHtmlText() {
		return fHtmlTextBuffer.toString();
	}
	
	public Position[] getImportHtmlRanges() {
		return (Position[]) importLocationsInHtml.toArray(new Position[importLocationsInHtml.size()]);
	}
	
	private char[] getPad(int numberOfChars) {
		final char[] spaceArray = new char[numberOfChars];
		Arrays.fill(spaceArray, ' ');
		return spaceArray;
	}
	
	public String[] getRawImports() {
		return (String[]) this.rawImports.toArray(new String[rawImports.size()]);
	}
	
	public IStructuredDocument getStructuredDocument() {
		return fStructuredDocument;
	}
	
	/**
	 * Only valid after a configure(...), translate(...) or
	 * translateFromFile(...) call
	 * 
	 * @return the current result (java translation) buffer
	 */
	public final StringBuffer getTranslation() {
		return fScriptText;
	}
	
	/**
	 * 
	 * @return the status of the translator's progrss monitor, false if the
	 *         monitor is null
	 */
	private boolean isCanceled() {
		return (fProgressMonitor == null) ? false : fProgressMonitor.isCanceled();
	}
	
	/**
	 * Reinitialize some fields
	 */
	private void reset() {
		scriptOffset = 0;
		// reset progress monitor
		if (fProgressMonitor != null) {
			fProgressMonitor.setCanceled(false);
		}
		fScriptText = new StringBuffer();
		fCurrentNode = null;
		fHtmlTextBuffer = new StringBuffer();
		rawImports.clear();
		importLocationsInHtml.clear();
		scriptLocationInHtml.clear();
	}
	
	/**
	 * So that the JSPTranslator can be reused.
	 */
	public void reset(IDOMNode node, IProgressMonitor progress) {
		// initialize some things on node
		configure(node, progress);
		reset();
		fHtmlTextBuffer.append(fStructuredDocument == null ? "" : fStructuredDocument.get());
	}
	
	public void setClassname(String classname) {
		this.fClassname = classname;
	}
	
	private IStructuredDocumentRegion setCurrentNode(IStructuredDocumentRegion currentNode) {
		return this.fCurrentNode = currentNode;
	}
	
	public void translate() {
		setCurrentNode(fStructuredDocument.getFirstStructuredDocumentRegion());
		while (getCurrentNode() != null && !isCanceled()) {
			// System.out.println("Translator Looking at Node
			// type:"+getCurrentNode().getType()+"---------------------------------:");
			// System.out.println(new NodeHelper(getCurrentNode()));
			// i.println("/---------------------------------------------------");
			if (getCurrentNode().getType() == DOMRegionContext.XML_TAG_NAME) {
				NodeHelper nh = new NodeHelper(getCurrentNode());
				if ((!nh.isEndTag() || nh.isSelfClosingTag()) && nh.nameEquals("script")) {
					/*
					 * Handles the following cases: <script
					 * type="javascriptype"> <script language="javascriptype>
					 * <script src='' type=javascriptype> <script src=''
					 * language=javascripttype <script src=''> global js type.
					 * <script> (global js type)
					 */
					if (NodeHelper.isInArray(JsDataTypes.JSVALIDDATATYPES, nh.getAttributeValue("type")) || NodeHelper.isInArray(JsDataTypes.JSVALIDDATATYPES, nh.getAttributeValue("language")) || isGlobalJs) {
						if (nh.containsAttribute(new String[] { "src" })) {
							// Handle import
							translateScriptImportNode(getCurrentNode());
						}
						// } else {
						// handle script section
						if (getCurrentNode().getNext() != null && getCurrentNode().getNext().getType() == DOMRegionContext.BLOCK_TEXT) {
							translateJSNode(getCurrentNode().getNext());
						}
					} // End search for <script> sections
				} else if (nh.containsAttribute(JsDataTypes.HTMLATREVENTS)) {
					/* Check for embeded JS events in any tags */
					translateInlineJSNode(getCurrentNode());
				} else if (nh.nameEquals("META") && nh.attrEquals("http-equiv", "Content-Script-Type") && nh.containsAttribute(new String[] { "content" })) {
					// <META http-equiv="Content-Script-Type" content="type">
					isGlobalJs = NodeHelper.isInArray(JsDataTypes.JSVALIDDATATYPES, nh.getAttributeValue("content"));
				} // End big if of JS types
			}
			if (getCurrentNode() != null) {
				advanceNextNode();
			}
		} // end while loop
	}
	
	public void translateInlineJSNode(IStructuredDocumentRegion container) {
		// System.out
		// .println("JSPTranslator.translateInlineJSNode Entered
		// w/ScriptOffset:"
		// + scriptOffset);
		NodeHelper nh = new NodeHelper(container);
		// System.out.println("inline js node looking at:\n" + nh);
		/* start a function header.. will amend later */
		ITextRegionList t = container.getRegions();
		ITextRegion r;
		Iterator regionIterator = t.iterator();
		while (regionIterator.hasNext()) {
			r = (ITextRegion) regionIterator.next();
			if (r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
				int start = r.getStart();
				int offset = r.getTextEnd();
				String tagAttrname = container.getText().substring(start, offset).trim();
				/*
				 * Attribute values aren't case sensative, also make sure next
				 * region is attrib value
				 */
				if (NodeHelper.isInArray(JsDataTypes.HTMLATREVENTS, tagAttrname)) {
					if (regionIterator.hasNext()) {
						regionIterator.next();
					}
					if (regionIterator.hasNext()) {
						r = ((ITextRegion) regionIterator.next());
					}
					if (r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
						int valStartOffset = container.getStartOffset(r);
						// int valEndOffset = r.getTextEnd();
						String rawText = container.getText().substring(r.getStart(), r.getTextEnd());
						if (rawText == null || rawText.length() == 0) {
							return;
						}
						/* Strip quotes */
						switch (rawText.charAt(0)) {
							case '\'':
							case '"':
								rawText = rawText.substring(1);
								valStartOffset++;
						}
						switch (rawText.charAt(rawText.length() - 1)) {
							case '\'':
							case '"':
								rawText = rawText.substring(0, rawText.length() - 1);
						}
						// Position inScript = new Position(scriptOffset,
						// rawText.length());
						/* Quoted text starts +1 and ends -1 char */
						Position inHtml = new Position(valStartOffset, rawText.length());
						scriptLocationInHtml.add(inHtml);
						/* need to pad the script text with spaces */
						char[] spaces = getPad(valStartOffset - scriptOffset);
						fScriptText.append(spaces);
						fScriptText.append(rawText);
						scriptOffset = fScriptText.length();
					}
				}
			}
		}
	}
	
	public void translateJSNode(IStructuredDocumentRegion container) {
		ITextRegionCollection containerRegion = container;
		Iterator regions = containerRegion.getRegions().iterator();
		ITextRegion region = null;
		while (regions.hasNext()) {
			region = (ITextRegion) regions.next();
			String type = region.getType();
			// content assist was not showing up in JSP inside a javascript
			// region
			if (type == DOMRegionContext.BLOCK_TEXT) {
				int scriptStart = container.getStartOffset();
				int scriptTextEnd = container.getEndOffset() - container.getStartOffset();
				String regionText = container.getText().substring(region.getStart(), region.getEnd());
				int regionLength = regionText.length();
				// /Position inScript = new Position(scriptOffset,
				// regionLength);
				Position inHtml = new Position(scriptStart, scriptTextEnd);
				scriptLocationInHtml.add(inHtml);
				char[] spaces = getPad(scriptStart - scriptOffset);
				fScriptText.append(spaces);
				// fJsToHTMLRanges.put(inScript, inHtml);
				fScriptText.append(regionText);
				scriptOffset = fScriptText.length();
			}
		}
	}
	
	public void translateScriptImportNode(IStructuredDocumentRegion region) {
		NodeHelper nh = new NodeHelper(region);
		String importName = nh.getAttributeValue("src");
		if (importName != null && !importName.equals("")) {
			rawImports.add(importName);
			Position inHtml = new Position(region.getStartOffset(), region.getEndOffset());
			importLocationsInHtml.add(inHtml);
		}
	}
}