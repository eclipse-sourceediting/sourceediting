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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.Position;
import org.eclipse.wst.jsdt.core.IBuffer;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionCollection;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

/**
 * Translates a JSP document into a HttpServlet. Keeps two way mapping from java
 * translation to the original JSP source, which can be obtained through
 * getJava2JspRanges() and getJsp2JavaRanges().
 * 
 * @author pavery
 */
public class JsTranslator extends Job implements IDocumentListener{
	
	private static final boolean DEBUG;
	private static final boolean DEBUG_SAVE_OUTPUT = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.jsdt.web.core/debug/jsptranslationstodisk")); //$NON-NLS-1$  //$NON-NLS-2$
	public static final String ENDL = "\n"; //$NON-NLS-1$
	static {
		String value = Platform.getDebugOption("org.eclipse.wst.jsdt.web.core/debug/jspjavamapping"); //$NON-NLS-1$
		DEBUG = value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
	}

	private IStructuredDocumentRegion fCurrentNode;
	private StringBuffer fScriptText = new StringBuffer();
	private IStructuredDocument fStructuredDocument = null;
	private ArrayList importLocationsInHtml = new ArrayList();
	/* use java script by default */
	private boolean isGlobalJs = true;
	private ArrayList rawImports = new ArrayList(); // traslated
	private ArrayList scriptLocationInHtml = new ArrayList();
	private int scriptOffset = 0;
	private byte[] fLock = new byte[0];
	private byte[] finished = new byte[0];
	private IBuffer compUnitBuff;
	private boolean cancelParse = false;
	private int missingEndTagRegionStart = -1;
	private static final boolean ADD_SEMICOLON_AT_INLINE=true;
	
	
	
	private void advanceNextNode() {
		setCurrentNode(getCurrentNode().getNext());
	}
	
	public JsTranslator(IStructuredDocument document, 	String fileName) {
		super("JavaScript translation for : "  + fileName);
		fStructuredDocument = document;
		
		fStructuredDocument.addDocumentListener(this);
		setPriority(Job.LONG);
		setSystem(true);
		schedule();
		reset();
	}
		
	public JsTranslator(IStructuredDocument document, 	String fileName, boolean listenForChanges) {
		super("JavaScript translation for : "  + fileName);
		fStructuredDocument = document;
		if(listenForChanges) {
			fStructuredDocument.addDocumentListener(this);
			setPriority(Job.LONG);
			setSystem(true);
			schedule();
		}
		reset();
	}
	
	public String getJsText() {
		synchronized(finished) {
			return fScriptText.toString();
		}
	}
	
	final public IStructuredDocumentRegion getCurrentNode() {
		return fCurrentNode;
	}
	
	public void setBuffer(IBuffer buffer) {
		compUnitBuff = buffer;
		synchronized(finished) {
			compUnitBuff.setContents(fScriptText.toString());
		}
	}

	public Position[] getHtmlLocations() {
		synchronized(finished) {
			return (Position[]) scriptLocationInHtml.toArray(new Position[scriptLocationInHtml.size()]);
		}
	}

	public int getMissingEndTagRegionStart() {
		return missingEndTagRegionStart;
	}
	
	public Position[] getImportHtmlRanges() {
		synchronized(finished) {
			return (Position[]) importLocationsInHtml.toArray(new Position[importLocationsInHtml.size()]);
		}
	}
	
	private char[] getPad(int numberOfChars) {
		if(numberOfChars < 0) return new char[0];
		final char[] spaceArray = new char[numberOfChars];
		Arrays.fill(spaceArray, ' ');
		return spaceArray;
	}
	
	public String[] getRawImports() {
		synchronized(finished) {
			return (String[]) this.rawImports.toArray(new String[rawImports.size()]);
		}
	}

		
	
	/**
	 * 
	 * @return the status of the translator's progrss monitor, false if the
	 *         monitor is null
	 */
	private boolean isCanceled() {
		return cancelParse;
	}
	
	/**
	 * Reinitialize some fields
	 */
	private void reset() {
		synchronized(fLock) {
			scriptOffset = 0;
			// reset progress monitor
			cancelParse = false;
			fScriptText = new StringBuffer();
			fCurrentNode = fStructuredDocument.getFirstStructuredDocumentRegion();
			rawImports.clear();
			importLocationsInHtml.clear();
			scriptLocationInHtml.clear();
			missingEndTagRegionStart = -1;
		}
		translate();
	}


	
	private IStructuredDocumentRegion setCurrentNode(IStructuredDocumentRegion currentNode) {
		synchronized(fLock) {
			return this.fCurrentNode = currentNode;
		}
	}
	
	public void translate() {
		//setCurrentNode(fStructuredDocument.getFirstStructuredDocumentRegion());
		synchronized(finished) {
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
							
							if (getCurrentNode().getNext() != null /*&& getCurrentNode().getNext().getType() == DOMRegionContext.BLOCK_TEXT*/) {
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
		finishedTranslation();
	}
	
	private void finishedTranslation() {
		if(compUnitBuff!=null) compUnitBuff.setContents(fScriptText.toString());
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
			while (regionIterator.hasNext() && !isCanceled() ) {
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
							if(ADD_SEMICOLON_AT_INLINE) rawText = rawText + ";";
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
		
		if(container==null) return;
		
		char[] spaces = getPad(container.getStartOffset() - scriptOffset);
		fScriptText.append(spaces);
		scriptOffset = container.getStartOffset();
	
		if(container.getType()!=DOMRegionContext.BLOCK_TEXT && container.getType()!= DOMRegionContext.XML_CDATA_TEXT) {
			return;
		}
		
		while (regions.hasNext() && !isCanceled()) {
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
				spaces = getPad(scriptStart - scriptOffset);
				fScriptText.append(spaces); 	
				// fJsToHTMLRanges.put(inScript, inHtml);
				fScriptText.append(regionText);
				scriptOffset = fScriptText.length();
			}
		}
		
		IStructuredDocumentRegion endTag = container.getNext();
		
		if(endTag==null) {
			missingEndTagRegionStart = container.getStartOffset();
		}else if(endTag!=null) {
			NodeHelper nh = new NodeHelper(endTag);
			String name = nh.getTagName();
			
			if(name==null || !name.trim().equalsIgnoreCase("script") || !nh.isEndTag()) {
				missingEndTagRegionStart = container.getStartOffset();
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

	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.IDocumentListener#documentAboutToBeChanged(org.eclipse.jface.text.DocumentEvent)
	 */
	public void documentAboutToBeChanged(DocumentEvent event) {
		cancelParse = true;
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.IDocumentListener#documentChanged(org.eclipse.jface.text.DocumentEvent)
	 */
	public void documentChanged(DocumentEvent event) {
		reset();
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor) {
	
		return Status.OK_STATUS;
	}
	
	public void release() {
		fStructuredDocument.removeDocumentListener(this);
	}
	
}