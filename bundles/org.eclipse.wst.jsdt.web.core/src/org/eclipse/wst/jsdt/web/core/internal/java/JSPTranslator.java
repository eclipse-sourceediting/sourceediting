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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.wst.jsdt.web.core.internal.Logger;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionCollection;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.utils.StringUtils;
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
public class JSPTranslator {
	// for debugging
	private static final boolean DEBUG;
	private static final boolean DEBUG_SAVE_OUTPUT = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.jsdt.web.core/debug/jsptranslationstodisk")); //$NON-NLS-1$  //$NON-NLS-2$
	public static final String ENDL = "\n"; //$NON-NLS-1$
	static {
		String value = Platform.getDebugOption("org.eclipse.wst.jsdt.web.core/debug/jspjavamapping"); //$NON-NLS-1$
		DEBUG = value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
	}
	private IStructuredDocumentRegion fCurrentNode;
	private String fClassname;
	/* map of ALL ranges */
	HashMap fJsToHTMLRanges = new HashMap();
	/**
	 * save JSP document text for later use may just want to read this from the
	 * file or strucdtured document depending what is available
	 */
	private StringBuffer fHtmlTextBuffer = new StringBuffer();
	private IProgressMonitor fProgressMonitor = null;
	private StringBuffer fResult;
	private ArrayList rawImports = new ArrayList(); // traslated
	StringBuffer fScriptText = new StringBuffer();
	private IStructuredDocument fStructuredDocument = null;
	private IDOMModel fStructuredModel = null;
	/* use java script by default */
	private boolean isGlobalJs = true;
	private int scriptOffset = 0;
	private ArrayList importLocationsInHtml = new ArrayList();
	
	private void advanceNextNode() {
		setCurrentNode(getCurrentNode().getNext());
	}
	
	protected void append(StringBuffer buf) {
		fResult.append(buf.toString());
	}
	
	/**
	 * put the final java document together
	 */
	private final void buildResult() {
		fResult = new StringBuffer(fScriptText.length());
		int javaOffset = 0;
		append(fScriptText);
		javaOffset += fScriptText.length();
	}
	
	/**
	 * configure using an XMLNode
	 * 
	 * @param node
	 * @param monitor
	 */
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
			// classname = base;
			classname = JSP2ServletNameUtil.mangle(base);
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
		buildResult();
		return getTranslation();
	}
	
	public List getExcludedElements() {
		return null;
	}
	
	public IFile getFile() {
			return FileBuffers.getWorkspaceFileAtLocation(new Path(fStructuredModel.getBaseLocation()));
	}
	
	/**
	 * map of ranges (positions) in java document to ranges in jsp document
	 * 
	 * @return a map of java positions to jsp positions.
	 */
	public HashMap getJs2HtmlRanges() {
		return fJsToHTMLRanges;
	}
	
	/**
	 * map of ranges in jsp document to ranges in java document.
	 * 
	 * @return a map of jsp positions to java positions, or null if no
	 *         translation has occured yet (the map hasn't been built).
	 */
	public HashMap getHtml2JsRanges() {
		if (fJsToHTMLRanges == null) {
			return null;
		}
		HashMap flipFlopped = new HashMap();
		Iterator keys = fJsToHTMLRanges.keySet().iterator();
		Object range = null;
		while (keys.hasNext()) {
			range = keys.next();
			// System.out.println("Offset:"+ p.offset + " length:" + p.length);
			flipFlopped.put(fJsToHTMLRanges.get(range), range);
		}
		return flipFlopped;
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
		if (JSPTranslator.DEBUG) {
			StringBuffer debugString = new StringBuffer();
			try {
				Iterator it = fJsToHTMLRanges.keySet().iterator();
				while (it.hasNext()) {
					debugString.append("--------------------------------------------------------------\n"); //$NON-NLS-1$
					Position java = (Position) it.next();
					debugString.append("Java range:[" + java.offset + ":" + java.length + "]\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					debugString.append("[" + fResult.toString().substring(java.offset, java.offset + java.length) + "]\n"); //$NON-NLS-1$ //$NON-NLS-2$
					debugString.append("--------------------------------------------------------------\n"); //$NON-NLS-1$
					debugString.append("|maps to...|\n"); //$NON-NLS-1$
					debugString.append("==============================================================\n"); //$NON-NLS-1$
					Position jsp = (Position) fJsToHTMLRanges.get(java);
					debugString.append("JSP range:[" + jsp.offset + ":" + jsp.length + "]\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					debugString.append("[" + fHtmlTextBuffer.toString().substring(jsp.offset, jsp.offset + jsp.length) + "]\n"); //$NON-NLS-1$ //$NON-NLS-2$
					debugString.append("==============================================================\n"); //$NON-NLS-1$
					debugString.append("\n"); //$NON-NLS-1$
					debugString.append("\n"); //$NON-NLS-1$
				}
			} catch (Exception e) {
				Logger.logException("JSPTranslation error", e); //$NON-NLS-1$
			}
			Logger.log(Logger.INFO_DEBUG, debugString.toString());
		
			IProject project = getFile().getProject();
			String shortenedClassname = StringUtils.replace(getFile().getName(), ".", "_");
			String filename = shortenedClassname + ".js";
			IPath path = project.getFullPath().append("src/" + filename);
			try {
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
				if (!file.exists()) {
					file.create(new ByteArrayInputStream(new byte[0]), true, new NullProgressMonitor());
				}
				ITextFileBufferManager textFileBufferManager = FileBuffers.getTextFileBufferManager();
				textFileBufferManager.connect(path, new NullProgressMonitor());
				ITextFileBuffer javaOutputBuffer = textFileBufferManager.getTextFileBuffer(path);
				javaOutputBuffer.getDocument().set(StringUtils.replace(fResult.toString(), getClassname(), shortenedClassname));
				javaOutputBuffer.commit(new NullProgressMonitor(), true);
				textFileBufferManager.disconnect(path, new NullProgressMonitor());
			} catch (Exception e) {
				// this is just for debugging, ignore
			}
			System.out.println("Updated translation: " + path);
		}
		return fResult;
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
		fResult = null;
		fCurrentNode = null;
		fJsToHTMLRanges.clear();
		fHtmlTextBuffer = new StringBuffer();
		rawImports.clear();
		importLocationsInHtml.clear();
	}
	
	public ArrayList getImportHtmlRanges() {
		return importLocationsInHtml;
	}
	
	/**
	 * So that the JSPTranslator can be reused.
	 */
	public void reset(IDOMNode node, IProgressMonitor progress) {
		// initialize some things on node
		configure(node, progress);
		reset();
		// set the jsp text buffer
		fHtmlTextBuffer.append(fStructuredDocument==null?"":fStructuredDocument.get());
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
		buildResult();
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
						if(rawText==null || rawText.length()==0) return;
						
						/* Strip quotes */
						switch(rawText.charAt(0)) {
							case '\'':
							case '"':
								rawText = rawText.substring(1);
								valStartOffset++;
						}
						switch(rawText.charAt(rawText.length()-1)) {
							case '\'':
							case '"':
								rawText = rawText.substring(0,rawText.length()-1);
								
						}
						Position inScript = new Position(scriptOffset, rawText.length());
						/* Quoted text starts +1 and ends -1 char */
						Position inHtml = new Position(valStartOffset, rawText.length());
						/*
						 * build the function. Addiotional function "baggage"
						 * not of concern to editor
						 */
						fJsToHTMLRanges.put(inScript, inHtml);
						fScriptText.append(rawText);
						scriptOffset = fScriptText.length();
						if (JSPTranslator.DEBUG) {
							System.out.println("START-----------------JS Translator Script loop---------------");
							System.out.println("Translated to:\n" + rawText + "\n");
							System.out.println("HTML Position:[" + inHtml.getOffset() + "," + inHtml.getLength() + "]");
							System.out.println("Script Position:[" + inScript.getOffset() + "," + inScript.getLength() + "]");
							System.out.println("Added (js) Text length:" + rawText.length());
							System.out.println("END-----------------JS Translator Script loop---------------");
						}
					}
				}
			}
		}
	}
	
	public ArrayList getRawImports() {
		return this.rawImports;
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
				Position inScript = new Position(scriptOffset, regionLength);
				Position inHtml = new Position(scriptStart, scriptTextEnd);
				if (JSPTranslator.DEBUG) {
					System.out.println("START-----------------JS Translator Script loop---------------");
					 System.out.println("Container getStartOffset():" +
					 container.getStartOffset());
					 System.out.println("Container getEnd():" +
					 container.getEnd());
					 System.out.println("Container getEndOffset():" +
					 container.getEndOffset());
					 System.out.println("Container getType():" +
					 container.getType());
						   			
					 System.out.println("Region getStart():" + region.getStart());
					 System.out.println("Region getEnd():" + region.getStart());
					 System.out.println("Region getType():" + region.getType());
					 System.out.println("Container Text Length:" +
					 container.getText().length());
						   			
					 System.out.println("Container Text:" + container.getText());
					
					System.out.println("Translated to:\n" + regionText + "\n");
					System.out.println("HTML Position:[" + inHtml.getOffset() + "," + inHtml.getLength() + "]");
					System.out.println("Script Position:[" + inScript.getOffset() + "," + inScript.getLength() + "]");
					System.out.println("END-----------------JS Translator Script loop---------------");
					//				
				}
				fJsToHTMLRanges.put(inScript, inHtml);
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