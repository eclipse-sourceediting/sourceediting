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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

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
import org.eclipse.wst.sse.core.internal.util.URIResolver;
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

	private String fClassname = "_JSSourceFile"; //$NON-NLS-1$
	public static final String EXPRESSION_PREFIX = "if(document) document.write(\"\"+"; //$NON-NLS-1$
	public static final String EXPRESSION_SUFFIX = ");"; //$NON-NLS-1$
	public static final String FUNCTION_PREFIX = "function ";
	public static final String FUNCTION_SUFFIX = "} ";
	/* use java script by default */
	private boolean isGlobalJs = true;

	/* map of JS type ranges */
	HashMap fJsContentRanges = new HashMap();

	/* map of ALL ranges */
	HashMap fJava2JspRanges = new HashMap();

	StringBuffer fScriptText = new StringBuffer();

	/* the big string buffers curser position */
	private int fCursorPosition = -1;

	/* Buffer where the cursor is */
	private StringBuffer fCursorOwner = null; // the buffer where the cursor
	// is

	private int scriptOffset = 0;

	// for debugging
	private static final boolean DEBUG;
	private static final boolean DEBUG_SAVE_OUTPUT = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.jsdt.web.core/debug/jsptranslationstodisk")); //$NON-NLS-1$  //$NON-NLS-2$

	static {
		String value = Platform
				.getDebugOption("org.eclipse.wst.jsdt.web.core/debug/jspjavamapping"); //$NON-NLS-1$
		DEBUG = value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
	}

	public static final String ENDL = "\n"; //$NON-NLS-1$

	/* map of imports */
	private HashMap fImportRanges = new HashMap();

	/** user defined imports */
	private StringBuffer fUserImports = new StringBuffer();

	private StringBuffer fResult; // the final traslated java document
	// string buffer

	private IDOMModel fStructuredModel = null;
	private IStructuredDocument fStructuredDocument = null;
	private IStructuredDocumentRegion fCurrentNode;

	/** used to avoid infinite looping include files */
	private Stack fIncludes = null;

	private IProgressMonitor fProgressMonitor = null;

	/**
	 * save JSP document text for later use may just want to read this from the
	 * file or strucdtured document depending what is available
	 */
	private StringBuffer fJspTextBuffer = new StringBuffer();

	/**
	 * configure using an XMLNode
	 * 
	 * @param node
	 * @param monitor
	 */
	private void configure(IDOMNode node, IProgressMonitor monitor) {

		fProgressMonitor = monitor;
		fStructuredModel = node.getModel();
		String baseLocation = fStructuredModel.getBaseLocation();

		fStructuredDocument = fStructuredModel.getStructuredDocument();

		String className = createClassname(node);
		if (className.length() > 0) {
			setClassname(className);
		}
	}

	/**
	 * memory saving configure (no StructuredDocument in memory) currently
	 * doesn't handle included files
	 * 
	 * @param jspFile
	 * @param monitor
	 */
	private void configure(IFile jspFile, IProgressMonitor monitor) {
		// when configured on a file
		// fStructuredModel, fPositionNode, fModelQuery, fStructuredDocument
		// are all null
		fProgressMonitor = monitor;

		String className = createClassname(jspFile);
		if (className.length() > 0) {
			setClassname(className);
		}
	}

	/* Cursors source position */
	final public void setSourceCursor(int i) {
	}

	/**
	 * Set the jsp text from an IFile
	 * 
	 * @param jspFile
	 */
	private void setJspText(IFile jspFile) {
		try {
			BufferedInputStream in = new BufferedInputStream(jspFile
					.getContents());
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			String line = null;
			while ((line = reader.readLine()) != null) {
				fJspTextBuffer.append(line);
				fJspTextBuffer.append(ENDL);
			}
			reader.close();
		} catch (CoreException e) {
			Logger.logException(e);
		} catch (IOException e) {
			Logger.logException(e);
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
			classname = JSP2ServletNameUtil.mangle(base);
		}
		return classname;
	}

	/**
	 * @param jspFile
	 * @return
	 */
	private String createClassname(IFile jspFile) {

		String classname = ""; //$NON-NLS-1$
		if (jspFile != null) {
			classname = JSP2ServletNameUtil.mangle(jspFile.getFullPath()
					.toString());
		}
		return classname;
	}

	public void setClassname(String classname) {
		this.fClassname = classname;
	}

	public String getClassname() {
		return this.fClassname != null ? this.fClassname
				: "GenericJSSourceFile"; //$NON-NLS-1$
	}

	/**
	 * So that the JSPTranslator can be reused.
	 */
	public void reset(IDOMNode node, IProgressMonitor progress) {

		// initialize some things on node
		configure(node, progress);
		reset();
		// set the jsp text buffer
		fJspTextBuffer.append(fStructuredDocument.get());
	}

	/**
	 * conservative version (no StructuredDocument/Model)
	 * 
	 * @param jspFile
	 * @param progress
	 */
	public void reset(IFile jspFile, IProgressMonitor progress) {

		// initialize some things on node
		configure(jspFile, progress);
		reset();
		// set the jsp text buffer
		setJspText(jspFile);
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

		// reinit fields

		fCursorPosition = -1;

		fUserImports = new StringBuffer();
		fScriptText = new StringBuffer();

		fResult = null;

		fCurrentNode = null;

		if (fIncludes != null) {
			fIncludes.clear();
		}
		fJava2JspRanges.clear();
		fImportRanges.clear();
		fJsContentRanges.clear();

		fJspTextBuffer = new StringBuffer();

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

	/**
	 * put the final java document together
	 */
	private final void buildResult() {

		// to build the java document this is the order:
		// 
		// + default imports
		// + user imports
		// + class header
		// [+ error page]
		// + user declarations
		// + service method header
		// + try/catch start
		// + user code
		// + try/catch end
		// + service method footer

		// fResult = new StringBuffer(fImplicitImports.length() +
		// fUserImports.length() + fClassHeader.length() +
		// fUserDeclarations.length() + fServiceHeader.length() +
		// fTryCatchStart.length() // try/catch
		// // start
		// + fUserCode.length() + fTryCatchEnd.length() // try/catch
		// // end
		// + fFooter.length());

		fResult = new StringBuffer(fScriptText.length());
		int javaOffset = 0;
		append(fScriptText);
		javaOffset += fScriptText.length();

		// user imports
		updateRanges(fImportRanges, javaOffset);
		append(fUserImports);
		javaOffset += fUserImports.length();

		fJava2JspRanges.putAll(fJsContentRanges);
		fJava2JspRanges.putAll(fImportRanges);

	}

	final public StringBuffer getCursorOwner() {
		return fCursorOwner;
	}

	protected void append(StringBuffer buf) {
		/*
		 * if (getCursorOwner() == buf) { fCursorPosition = fResult.length() +
		 * getRelativeOffset(); }
		 */
		fCursorPosition = fResult.length();

		fResult.append(buf.toString());
	}

	/**
	 * @param javaRanges
	 * @param offsetInJava
	 */
	private void updateRanges(HashMap rangeMap, int offsetInJava) {
		// just need to update java ranges w/ the offset we now know
		Iterator it = rangeMap.keySet().iterator();
		while (it.hasNext()) {
			((Position) it.next()).offset += offsetInJava;
		}
	}

	/**
	 * map of ranges (positions) in java document to ranges in jsp document
	 * 
	 * @return a map of java positions to jsp positions.
	 */
	public HashMap getJava2JspRanges() {
		return fJava2JspRanges;
	}

	/**
	 * map of ranges in jsp document to ranges in java document.
	 * 
	 * @return a map of jsp positions to java positions, or null if no
	 *         translation has occured yet (the map hasn't been built).
	 */
	public HashMap getJsp2JavaRanges() {
		if (fJava2JspRanges == null) {
			return null;
		}
		HashMap flipFlopped = new HashMap();
		Iterator keys = fJava2JspRanges.keySet().iterator();

		Object range = null;
		while (keys.hasNext()) {
			range = keys.next();
			// System.out.println("Offset:"+ p.offset + " length:" + p.length);
			flipFlopped.put(fJava2JspRanges.get(range), range);
		}
		return flipFlopped;
	}

	public HashMap getJava2JspImportRanges() {
		return fImportRanges;
	}

	public HashMap getJava2JspUseBeanRanges() {
		// Return nothing for now
		return new HashMap();
	}

	public HashMap getJava2JspIndirectRanges() {
		// Return nothing for now
		return new HashMap();
	}

	/**
	 * Only valid after a configure(...), translate(...) or
	 * translateFromFile(...) call
	 * 
	 * @return the current result (java translation) buffer
	 */
	public final StringBuffer getTranslation() {

		if (DEBUG) {
			StringBuffer debugString = new StringBuffer();
			try {
				Iterator it = fJava2JspRanges.keySet().iterator();
				while (it.hasNext()) {
					debugString
							.append("--------------------------------------------------------------\n"); //$NON-NLS-1$
					Position java = (Position) it.next();
					debugString
							.append("Java range:[" + java.offset + ":" + java.length + "]\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					debugString
							.append("[" + fResult.toString().substring(java.offset, java.offset + java.length) + "]\n"); //$NON-NLS-1$ //$NON-NLS-2$
					debugString
							.append("--------------------------------------------------------------\n"); //$NON-NLS-1$
					debugString.append("|maps to...|\n"); //$NON-NLS-1$
					debugString
							.append("==============================================================\n"); //$NON-NLS-1$
					Position jsp = (Position) fJava2JspRanges.get(java);
					debugString
							.append("JSP range:[" + jsp.offset + ":" + jsp.length + "]\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					debugString
							.append("[" + fJspTextBuffer.toString().substring(jsp.offset, jsp.offset + jsp.length) + "]\n"); //$NON-NLS-1$ //$NON-NLS-2$
					debugString
							.append("==============================================================\n"); //$NON-NLS-1$
					debugString.append("\n"); //$NON-NLS-1$
					debugString.append("\n"); //$NON-NLS-1$
				}
			} catch (Exception e) {
				Logger.logException("JSPTranslation error", e); //$NON-NLS-1$
			}
			Logger.log(Logger.INFO_DEBUG, debugString.toString());
		}

		if (true) {
			IProject project = getFile().getProject();
			String shortenedClassname = StringUtils.replace(
					getFile().getName(), ".", "_");
			String filename = shortenedClassname + ".js";
			IPath path = project.getFullPath().append("src/" + filename);
			try {
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(
						path);
				if (!file.exists()) {
					file.create(new ByteArrayInputStream(new byte[0]), true,
							new NullProgressMonitor());
				}
				ITextFileBufferManager textFileBufferManager = FileBuffers
						.getTextFileBufferManager();
				textFileBufferManager.connect(path, new NullProgressMonitor());
				ITextFileBuffer javaOutputBuffer = textFileBufferManager
						.getTextFileBuffer(path);
				javaOutputBuffer.getDocument().set(
						StringUtils.replace(fResult.toString(), getClassname(),
								shortenedClassname));
				javaOutputBuffer.commit(new NullProgressMonitor(), true);
				textFileBufferManager.disconnect(path,
						new NullProgressMonitor());
			} catch (Exception e) {
				// this is just for debugging, ignore
			}
			System.out.println("Updated translation: " + path);
		}

		return fResult;
	}

	/**
	 * Only valid after a configure(...), translate(...) or
	 * translateFromFile(...) call
	 * 
	 * @return the text in the JSP file
	 */
	public final String getJspText() {
		return fJspTextBuffer.toString();
	}

	private IFile getFile() {
		IFile f = null;
		IStructuredModel sModel = StructuredModelManager.getModelManager()
				.getExistingModelForRead(getStructuredDocument());
		try {
			if (sModel != null) {
				f = FileBuffers.getWorkspaceFileAtLocation(new Path(sModel
						.getBaseLocation()));
			}
		} finally {
			if (sModel != null) {
				sModel.releaseFromRead();
			}
		}
		return f;
	}

	public void translateJSNode(IStructuredDocumentRegion container) {
		System.out
				.println("JSPTranslator.translateJSNode Entered w/ScriptOffset:"
						+ scriptOffset);
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
				int scriptTextEnd = container.getEndOffset()
						- container.getStartOffset();

				//	   			
				// System.out.println("Container getStartOffset():" +
				// container.getStartOffset());
				// System.out.println("Container getEnd():" +
				// container.getEnd());
				// System.out.println("Container getEndOffset():" +
				// container.getEndOffset());
				// System.out.println("Container getType():" +
				// container.getType());
				//	   			
				// System.out.println("Region getStart():" + region.getStart());
				// System.out.println("Region getEnd():" + region.getStart());
				// System.out.println("Region getType():" + region.getType());
				// System.out.println("Container Text Length:" +
				// container.getText().length());
				//	   			
				// System.out.println("Container Text:" + container.getText());

				String regionText = container.getText().substring(
						region.getStart(), region.getEnd());
				int regionLength = regionText.length();
				Position inScript = new Position(scriptOffset, regionLength);
				Position inHtml = new Position(scriptStart, scriptTextEnd);
				System.out
						.println("START-----------------JS Translator Script loop---------------");
				System.out.println("Translated to:\n" + regionText + "\n");
				System.out.println("HTML Position:[" + inHtml.getOffset() + ","
						+ inHtml.getLength() + "]");
				System.out.println("Script Position:[" + inScript.getOffset()
						+ "," + inScript.getLength() + "]");
				System.out
						.println("END-----------------JS Translator Script loop---------------");
				//				

				fJsContentRanges.put(inScript, inHtml);
				fScriptText.append(regionText);

				scriptOffset = fScriptText.length();

				System.out
						.println("JSPTranslator.translateJSNode Left w/ScriptOffset:"
								+ scriptOffset);
			}
		}
	}

	public void translateScriptImportNode(IStructuredDocumentRegion region) {

	}

	public void translateInlineJSNode(IStructuredDocumentRegion container) {
		System.out
				.println("JSPTranslator.translateInlineJSNode Entered w/ScriptOffset:"
						+ scriptOffset);

		NodeHelper nh = new NodeHelper(container);
		// System.out.println("inline js node looking at:\n" + nh);
		/* start a function header.. will amend later */
		String header = "function " + "_" + nh.getElementAsFlatString();
		String footer = "}";

		/* Start looping through the region. May have mutlipel even attribs */
		if (container == null) {
			return;
		}

		ITextRegionList t = container.getRegions();
		ITextRegion r;
		Iterator regionIterator = t.iterator();
		while (regionIterator.hasNext()) {
			r = (ITextRegion) regionIterator.next();
			if (r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
				int start = r.getStart();
				int offset = r.getTextEnd();

				String tagAttrname = container.getText().substring(start,
						offset).trim();

				/*
				 * Attribute values aren't case sensative, also make sure next
				 * region is attrib value
				 */
				if (NodeHelper
						.isInArray(JsDataTypes.HTMLATREVENTS, tagAttrname)) {
					if (regionIterator.hasNext()) {
						regionIterator.next();
					}
					if (regionIterator.hasNext()) {
						r = ((ITextRegion) regionIterator.next());
					}

					if (r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {

						int valStartOffset = container.getStartOffset(r);
						// int valEndOffset = r.getTextEnd();
						String rawText = container.getText().substring(
								r.getStart(), r.getTextEnd());

						String newFunctionHeader = header + "_" + scriptOffset
								+ tagAttrname + "(){";
						String rawFunction = nh.stripEndQuotes(rawText);

						/*
						 * Determine if we should put a ; after the raw function
						 * text
						 */
						boolean needsSemiColon = !(rawFunction.length() > 0 && rawFunction
								.trim().charAt(rawFunction.trim().length() - 1) == ';');

						int offsetForQutoes = (nh.isQuoted(rawText)) ? 1 : 0;

						Position inScript = new Position(scriptOffset
								+ newFunctionHeader.length(), rawFunction
								.length());

						/* Quoted text starts +1 and ends -1 char */
						Position inHtml = new Position(valStartOffset
								+ offsetForQutoes, rawText.length() - 2
								* offsetForQutoes);

						/*
						 * build the function. Addiotional function "baggage"
						 * not of concern to editor
						 */
						String emulatedFunction = newFunctionHeader
								+ rawFunction + (needsSemiColon ? ";" : "")
								+ footer;

						fJsContentRanges.put(inScript, inHtml);
						fScriptText.append(emulatedFunction);
						scriptOffset = fScriptText.length();

						System.out
								.println("START-----------------JS Translator Script loop---------------");
						System.out.println("Translated to:\n"
								+ emulatedFunction + "\n");
						System.out.println("HTML Position:["
								+ inHtml.getOffset() + "," + inHtml.getLength()
								+ "]");
						System.out.println("Script Position:["
								+ inScript.getOffset() + ","
								+ inScript.getLength() + "]");
						System.out.println("Added (js) Text length:"
								+ emulatedFunction.length());
						System.out
								.println("END-----------------JS Translator Script loop---------------");

					}
				}
			}

		}
		System.out
				.println("JSPTranslator.translateInlineJSNode Left w/ScriptOffset:"
						+ scriptOffset);

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

				if ((!nh.isEndTag() || nh.isSelfClosingTag())
						&& nh.nameEquals("script")) {
					/*
					 * Handles the following cases: <script
					 * type="javascriptype"> <script language="javascriptype>
					 * <script src='' type=javascriptype> <script src=''
					 * language=javascripttype <script src=''> global js type.
					 * <script> (global js type)
					 */
					if (NodeHelper.isInArray(JsDataTypes.JSVALIDDATATYPES, nh
							.getAttributeValue("type"))
							|| NodeHelper.isInArray(
									JsDataTypes.JSVALIDDATATYPES, nh
											.getAttributeValue("language"))
							|| isGlobalJs) {
						if (nh.containsAttribute(new String[] { "src" })) {
							// Handle import
							translateScriptImportNode(getCurrentNode());
						} else {
							// handle script section
							if (getCurrentNode().getNext() != null
									&& getCurrentNode().getNext().getType() == DOMRegionContext.BLOCK_TEXT) {
								translateJSNode(getCurrentNode().getNext());
							}
						}
					} // End search for <script> sections
				} else if (nh.containsAttribute(JsDataTypes.HTMLATREVENTS)) {
					/* Check for embeded JS events in any tags */
					translateInlineJSNode(getCurrentNode());
				} else if (nh.nameEquals("META")
						&& nh.attrEquals("http-equiv", "Content-Script-Type")
						&& nh.containsAttribute(new String[] { "content" })) {
					// <META http-equiv="Content-Script-Type" content="type">
					isGlobalJs = NodeHelper.isInArray(
							JsDataTypes.JSVALIDDATATYPES, nh
									.getAttributeValue("content"));
				} // End big if of JS types
			}
			if (getCurrentNode() != null) {
				advanceNextNode();
			}
		} // end while loop
		buildResult();

	}

	protected void setDocumentContent(IDocument document,
			InputStream contentStream, String charset) {
		Reader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(contentStream,
					charset), 2048);
			StringBuffer buffer = new StringBuffer(2048);
			char[] readBuffer = new char[2048];
			int n = in.read(readBuffer);
			while (n > 0) {
				buffer.append(readBuffer, 0, n);
				n = in.read(readBuffer);
			}
			document.set(buffer.toString());
		} catch (IOException x) {
			// ignore
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException x) {
					// ignore
				}
			}
		}
	}

	/**
	 * 
	 * @return the status of the translator's progrss monitor, false if the
	 *         monitor is null
	 */
	private boolean isCanceled() {
		return (fProgressMonitor == null) ? false : fProgressMonitor
				.isCanceled();
	}

	private void advanceNextNode() {
		setCurrentNode(getCurrentNode().getNext());

	}

	/*
	 * returns string minus CDATA open and close text
	 */
	final public String stripCDATA(String text) {
		String resultText = ""; //$NON-NLS-1$
		String CDATA_OPEN = "<![CDATA["; //$NON-NLS-1$
		String CDATA_CLOSE = "]]>"; //$NON-NLS-1$
		int start = 0;
		int end = text.length();
		while (start < text.length()) {
			if (text.indexOf(CDATA_OPEN, start) > -1) {
				end = text.indexOf(CDATA_OPEN, start);
				resultText += text.substring(start, end);
				start = end + CDATA_OPEN.length();
			} else if (text.indexOf(CDATA_CLOSE, start) > -1) {
				end = text.indexOf(CDATA_CLOSE, start);
				resultText += text.substring(start, end);
				start = end + CDATA_CLOSE.length();
			} else {
				end = text.length();
				resultText += text.substring(start, end);
				break;
			}
		}
		return resultText;
	}

	private URIResolver getResolver() {
		return (fStructuredModel != null) ? fStructuredModel.getResolver()
				: null;
	}

	/**
	 * @param r
	 *            the region to be unescaped (XMLContent, XML ENTITY REFERENCE,
	 *            or CDATA)
	 * @param sb
	 *            the stringbuffer to append the text to
	 * @return the number of characters removed in unescaping this text
	 */
	protected int unescapeRegion(ITextRegion r, StringBuffer sb) {
		String s = ""; //$NON-NLS-1$
		int lengthBefore = 0, lengthAfter = 0, cdata_tags_length = 0;
		if (r != null
				&& (r.getType() == DOMRegionContext.XML_CONTENT || r.getType() == DOMRegionContext.XML_ENTITY_REFERENCE)) {
			lengthBefore = (getCurrentNode() != r) ? getCurrentNode()
					.getFullText(r).length() : getCurrentNode().getFullText()
					.length();
			s = EscapedTextUtil.getUnescapedText(getCurrentNode(), r);
			lengthAfter = s.length();
			sb.append(s);
		} else if (r != null && r.getType() == DOMRegionContext.XML_CDATA_TEXT) {
			if (r instanceof ITextRegionContainer) // only interested in
			// contents
			{
				// navigate to next region container (which should be a JSP
				// region)
				Iterator it = ((ITextRegionContainer) r).getRegions()
						.iterator();
				ITextRegion temp = null;
				while (it.hasNext()) {
					temp = (ITextRegion) it.next();
					if (temp instanceof ITextRegionContainer
							|| temp.getType() == DOMRegionContext.XML_CDATA_TEXT) {
						sb.append(getCurrentNode().getFullText(temp));
					} else if (temp.getType() == DOMRegionContext.XML_CDATA_OPEN
							|| temp.getType() == DOMRegionContext.XML_CDATA_CLOSE) {
						cdata_tags_length += temp.getLength();
					}
				}
			}
		}
		return (lengthBefore - lengthAfter + cdata_tags_length);
	}

	final public int getCursorPosition() {
		return fCursorPosition;
	}



	private IStructuredDocumentRegion setCurrentNode(
			IStructuredDocumentRegion currentNode) {
		return this.fCurrentNode = currentNode;
	}

	final public IStructuredDocumentRegion getCurrentNode() {
		return fCurrentNode;
	}

	public IStructuredDocument getStructuredDocument() {
		return fStructuredDocument;
	}
}