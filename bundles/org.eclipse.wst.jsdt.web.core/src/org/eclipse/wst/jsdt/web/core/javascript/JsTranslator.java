/*******************************************************************************
 * Copyright (c) 2008, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 * Provisional API: This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 *     
 *******************************************************************************/


package org.eclipse.wst.jsdt.web.core.javascript;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.wst.jsdt.core.IBuffer;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionCollection;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.

 * Translates a web page into its JavaScript pieces. 
 * 
 */
public class JsTranslator extends Job implements IJsTranslator, IDocumentListener {
	
	protected static final boolean DEBUG;
	private static final boolean DEBUG_SAVE_OUTPUT = false;  //"true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.jsdt.web.core/debug/jstranslationstodisk")); //$NON-NLS-1$  //$NON-NLS-2$
//	private static final String ENDL = "\n"; //$NON-NLS-1$
	
	private static final String XML_COMMENT_START = "<!--"; //$NON-NLS-1$
//	private static final String XML_COMMENT_END = "-->"; //$NON-NLS-1$
	private static final boolean REPLACE_INNER_BLOCK_SECTIONS_WITH_SPACE = false;
	
	//TODO: should be an inclusive rule rather than exclusive
	private static final Pattern fClientSideTagPattern = Pattern.compile("<[^<%?)>]+/?>"); //$NON-NLS-1$

	// FIXME: Workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=307401
	private String[][] fServerSideDelimiters = new String[][]{{"<%","%>"},{"<?","?>"}};
	private int fShortestServerSideDelimiterPairLength = 4;
	
	static {
		String value = Platform.getDebugOption("org.eclipse.wst.jsdt.web.core/debug/jsjavamapping"); //$NON-NLS-1$
 		DEBUG = value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
	}

	private IStructuredDocumentRegion fCurrentNode;
	protected StringBuffer fScriptText = new StringBuffer();
	protected IStructuredDocument fStructuredDocument = null;
	protected ArrayList importLocationsInHtml = new ArrayList();
	/* use java script by default */
	protected boolean fIsGlobalJs = true;
	protected ArrayList rawImports = new ArrayList(); // translated
	protected ArrayList scriptLocationInHtml = new ArrayList();
	protected int scriptOffset = 0;
	
	protected byte[] fLock = new byte[0];
	protected byte[] finished = new byte[0];
	
	protected IBuffer fCompUnitBuff;
	protected boolean cancelParse = false;
	protected int missingEndTagRegionStart = -1;
	protected static final boolean ADD_SEMICOLON_AT_INLINE=true;

	/*
	 * org.eclipse.jface.text.Regions that contain purely generated code, for
	 * which no validation messages should be reported to the user
	 */
	private List fGeneratedRanges = new ArrayList();
	
	protected boolean isGlobalJs() {
		return fIsGlobalJs;
	}
	
	protected IBuffer getCompUnitBuffer() {
		return fCompUnitBuff;
	}
	
	protected StringBuffer getScriptTextBuffer() {
		return fScriptText;
	}
	
	
	protected void setIsGlobalJs(boolean value) {
		this.fIsGlobalJs = value;
	}
	
	protected void advanceNextNode() {
		setCurrentNode(getCurrentNode().getNext());
	}	
	
	public JsTranslator(IStructuredDocument document, 	String fileName) {
		this(document, fileName, false);
	}
	
	/**
	 * @deprecated
	 */
	public JsTranslator() {
		super("JavaScript Translation");
	}
	
	public JsTranslator(IStructuredDocument document, 	String fileName, boolean listenForChanges) {
		super("JavaScript translation for : "  + fileName); //$NON-NLS-1$
		fStructuredDocument = document;
		if(listenForChanges) {
			fStructuredDocument.addDocumentListener(this);
			setPriority(Job.LONG);
			setSystem(true);
			schedule();
		}
		reset();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.web.core.javascript.IJsTranslator#getJsText()
	 */
	public String getJsText() {
		synchronized(finished) {
			return fScriptText.toString();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.web.core.javascript.IJsTranslator#getCurrentNode()
	 */
	protected final IStructuredDocumentRegion getCurrentNode() {
		
		return fCurrentNode;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.web.core.javascript.IJsTranslator#setBuffer(org.eclipse.wst.jsdt.core.IBuffer)
	 */
	public void setBuffer(IBuffer buffer) {
		fCompUnitBuff = buffer;
		synchronized(finished) {
			fCompUnitBuff.setContents(fScriptText.toString());
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.web.core.javascript.IJsTranslator#getHtmlLocations()
	 */
	public Position[] getHtmlLocations() {
		synchronized(finished) {
			return (Position[]) scriptLocationInHtml.toArray(new Position[scriptLocationInHtml.size()]);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.web.core.javascript.IJsTranslator#getMissingEndTagRegionStart()
	 */
	public int getMissingEndTagRegionStart() {
		return missingEndTagRegionStart;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.web.core.javascript.IJsTranslator#getImportHtmlRanges()
	 */
	public Position[] getImportHtmlRanges() {
		synchronized(finished) {
			return (Position[]) importLocationsInHtml.toArray(new Position[importLocationsInHtml.size()]);
		}
	}
	

	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.web.core.javascript.IJsTranslator#getRawImports()
	 */
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
	protected boolean isCanceled() {
		return cancelParse;
	}
	
	/**
	 * Reinitialize some fields
	 */
	protected void reset() {
		synchronized(fLock) {
			scriptOffset = 0;
			// reset progress monitor
			fScriptText = new StringBuffer();
			fCurrentNode = fStructuredDocument.getFirstStructuredDocumentRegion();
			rawImports.clear();
			importLocationsInHtml.clear();
			scriptLocationInHtml.clear();
			missingEndTagRegionStart = -1;
			cancelParse = false;
			fGeneratedRanges.clear();
		}
		translate();
	}


	
	protected IStructuredDocumentRegion setCurrentNode(IStructuredDocumentRegion currentNode) {
		synchronized(fLock) {
			return this.fCurrentNode = currentNode;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.web.core.javascript.IJsTranslator#translate()
	 */
	public void translate() {
		//setCurrentNode(fStructuredDocument.getFirstStructuredDocumentRegion());
		
		synchronized(finished) {
			if(getCurrentNode() != null) {
			NodeHelper nh = new NodeHelper(getCurrentNode());
			while (getCurrentNode() != null && !isCanceled()) {
				nh.setDocumentRegion(getCurrentNode());
				
				// System.out.println("Translator Looking at Node
				// type:"+getCurrentNode().getType()+"---------------------------------:");
				// System.out.println(new NodeHelper(getCurrentNode()));
				// i.println("/---------------------------------------------------");
				if (getCurrentNode().getType() == DOMRegionContext.XML_TAG_NAME) {
					if ((!nh.isEndTag() || nh.isSelfClosingTag()) && nh.nameEquals("script")) { //$NON-NLS-1$
						/*
						 * Handles the following cases: <script
						 * type="javascriptype"> <script language="javascriptype>
						 * <script src='' type=javascriptype> <script src=''
						 * language=javascripttype <script src=''> global js type.
						 * <script> (global js type)
						 */
						if (NodeHelper.isInArray(JsDataTypes.JSVALIDDATATYPES, nh.getAttributeValue("type")) || NodeHelper.isInArray(JsDataTypes.JSVALIDDATATYPES, nh.getAttributeValue("language")) || isGlobalJs()) { //$NON-NLS-1$ //$NON-NLS-2$
							if (nh.containsAttribute(new String[] { "src" })) { //$NON-NLS-1$
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
						/* Check for embedded JS events in any tags */
						translateInlineJSNode(getCurrentNode());
					} else if (nh.nameEquals("META") && nh.attrEquals("http-equiv", "Content-Script-Type") && nh.containsAttribute(new String[] { "content" })) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						// <META http-equiv="Content-Script-Type" content="type">
						setIsGlobalJs( NodeHelper.isInArray(JsDataTypes.JSVALIDDATATYPES, nh.getAttributeValue("content"))); //$NON-NLS-1$
					} // End big if of JS types
				}
				if (getCurrentNode() != null) {
					advanceNextNode();
				}
			} // end while loop
			if(getCompUnitBuffer()!=null) getCompUnitBuffer().setContents(fScriptText.toString());
		}
		finishedTranslation();
		}
	}
	
	protected void finishedTranslation() {
		if(DEBUG_SAVE_OUTPUT){
			IDOMModel xmlModel = null;
			String baseLocation = null;
			FileOutputStream fout = null;
			PrintStream out = null;
			try {
				xmlModel = (IDOMModel) StructuredModelManager.getModelManager().getExistingModelForRead(fStructuredDocument);
				if (xmlModel == null) {
					xmlModel = (IDOMModel) StructuredModelManager.getModelManager().getModelForRead(fStructuredDocument);
				}
				baseLocation = xmlModel.getBaseLocation();
			}
			finally {
				if (xmlModel != null)
					xmlModel.releaseFromRead();
			}
			
			if(baseLocation!=null){
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IWorkspaceRoot root = workspace.getRoot();
				IFile tFile = workspace.getRoot().getFile(new Path(baseLocation + ".js"));
				File tempFile = tFile.getLocation().toFile();
				
				  if(tempFile.exists()){
					  tempFile.delete();
				  }
				 
				  try {
					  tempFile.createNewFile();
					  fout = new FileOutputStream(tempFile);
					  out = new PrintStream(fout);
					  out.println(fScriptText);
					  out.close();
				} catch (FileNotFoundException e) {
				
				} catch (IOException e) {
					
				}finally{
					if(out!=null) out.close();
				
					
				}
				 try {
					root.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
				} catch (CoreException e) {
					
				}
			}
			
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.web.core.javascript.IJsTranslator#translateInlineJSNode(org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion)
	 */
	public void translateInlineJSNode(IStructuredDocumentRegion container) {
		// System.out
		// .println("JSPTranslator.translateInlineJSNode Entered
		// w/ScriptOffset:"
		// + scriptOffset);
		
			//NodeHelper nh = new NodeHelper(container);
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
							if (rawText == null || rawText.length() == 0) {
								return;
							}
							switch (rawText.charAt(rawText.length() - 1)) {
								case '\'':
								case '"':
									rawText = rawText.substring(0, rawText.length() - 1);
							}
							// Position inScript = new Position(scriptOffset,
							// rawText.length());
							/* Quoted text starts +1 and ends -1 char */
							if(ADD_SEMICOLON_AT_INLINE) rawText = rawText + ";"; //$NON-NLS-1$
							Position inHtml = new Position(valStartOffset, rawText.length());
							scriptLocationInHtml.add(inHtml);
							/* need to pad the script text with spaces */
							char[] spaces = Util.getPad(valStartOffset - scriptOffset);
							fScriptText.append(spaces);
							fScriptText.append(rawText);
							scriptOffset = fScriptText.length();
						}
					}
				}
			}
		}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.web.core.javascript.IJsTranslator#translateJSNode(org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion)
	 */
	public void translateJSNode(IStructuredDocumentRegion container) {
		ITextRegionCollection containerRegion = container;
		Iterator regions = containerRegion.getRegions().iterator();
		ITextRegion region = null;
		
		if(container==null) return;
		
		char[] spaces = Util.getPad(container.getStartOffset() - scriptOffset);
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
			
			//System.out.println("Region text: " + container.getText().substring(region.getStart(), region.getEnd()));
			boolean isContainerRegion = region instanceof ITextRegionContainer;
			/* make sure its not a sub container region, probably JSP */
			if (type == DOMRegionContext.BLOCK_TEXT ) {
				int scriptStart = container.getStartOffset();
				int scriptTextLength = container.getLength();
				String regionText = container.getFullText(region);
				int regionLength = region.getLength();
				
				spaces = Util.getPad(scriptStart - scriptOffset);
				fScriptText.append(spaces); 	
				// container regions get replaced entirely
				if(isContainerRegion && REPLACE_INNER_BLOCK_SECTIONS_WITH_SPACE) {
					spaces = Util.getPad(regionLength);
					fScriptText.append(spaces); 	
				}
				// skip over XML/HTML comment starts
				else if (regionText.indexOf(XML_COMMENT_START) >= 0) {
					int index = regionText.indexOf(XML_COMMENT_START);
					int leadingTrim = index + XML_COMMENT_START.length();
					for (int i = 0; i < index; i++) {
						/*
						 * ignore the comment start when it's preceded only
						 * by white space
						 */
						if (!Character.isWhitespace(regionText.charAt(i))) {
							leadingTrim = 0;
							break;
						}
					}
					spaces = Util.getPad(leadingTrim);
					fScriptText.append(spaces);
					fScriptText.append(regionText.substring(leadingTrim));
				}
				// server-side code
//				else {
					/*
					 * Fix for
					 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=284774
					 * end of last valid JS source, start of next content to
					 * skip
					 */
					// last offset of valid JS source, after which there's server-side stuff
					int validJSend = 0;
					// start of next valid JS source, last offset of content that was skipped
					int validJSstart = 0;

					Matcher matcher = fClientSideTagPattern.matcher(regionText);
					// note the start of a HTML tag if one's present
					int clientMatchStart = matcher.find() ? matcher.start() : -1;

					StringBuffer contents = new StringBuffer();
					
					int serverSideStart = -1;
					int serverSideDelimiter = 0;

					// find any instance of server code blocks in the region text
					for (int i = 0; i < fServerSideDelimiters.length; i++) {
						int index = regionText.indexOf(fServerSideDelimiters[i][0]);
						if (serverSideStart < 0) {
							serverSideStart = index;
							serverSideDelimiter = i;
						}
						else if (index >= 0) {
							serverSideStart = Math.min(serverSideStart, index);
							if (serverSideStart == index) {
								serverSideDelimiter = i;
							}
						}
					}
					// contains something other than pure JavaScript
					while (serverSideStart > -1 || clientMatchStart > -1) { //$NON-NLS-1$
						validJSend = validJSstart;
						boolean biasClient = false;
						boolean biasServer = false;
						// update the start of content to skip
						if (clientMatchStart > -1 && serverSideStart > -1) {
							validJSend = Math.min(clientMatchStart, serverSideStart);
							biasClient = validJSend == clientMatchStart;
							biasServer = validJSend == serverSideStart;
						}
						else if (clientMatchStart > -1 && serverSideStart < 0) {
							validJSend = clientMatchStart;
							biasClient = true;
						}
						else if (clientMatchStart < 0 && serverSideStart > -1) {
							validJSend = serverSideStart;
							biasServer = true;
						}
						
						// append if there's something we want to include
						if (-1 < validJSstart && -1 < validJSend) {
							// append what we want to include
							contents.append(regionText.substring(validJSstart, validJSend));
							
							// change the skipped content to a valid variable name and append it as a placeholder
							int startOffset = container.getStartOffset(region) + validJSend;

							String serverEnd = fServerSideDelimiters[serverSideDelimiter][1];
							int serverSideEnd = (regionLength > validJSend + serverEnd.length()) ? regionText.indexOf(serverEnd, validJSend + fServerSideDelimiters[serverSideDelimiter][1].length()) : -1;
							if (serverSideEnd > -1)
								serverSideEnd += serverEnd.length();
							int clientMatchEnd = matcher.find(validJSend) ? matcher.end() : -1;
							// update end of what we skipped
							validJSstart = -1;
							if (clientMatchEnd > validJSend && serverSideEnd > validJSend) {
								if (biasClient)
									validJSstart = clientMatchEnd;
								else if (biasServer)
									validJSstart = serverSideEnd;
								else
									validJSstart = Math.min(clientMatchEnd, serverSideEnd);
							}
							if (clientMatchEnd >= validJSend && serverSideEnd < 0)
								validJSstart = matcher.end();
							if (clientMatchEnd < 0 && serverSideEnd >= validJSend)
								validJSstart = serverSideEnd;
							int line = container.getParentDocument().getLineOfOffset(startOffset);
							int column;
							try {
								column = startOffset - container.getParentDocument().getLineOffset(line);
							}
							catch (BadLocationException e) {
								column = -1;
							}
							// substituted text length much match original length exactly, find text of the right length
							int start = validJSend + container.getStartOffset(region);
							contents.append('_');
							for (int i = validJSend + 1; i < validJSstart; i++) {
								switch (i - validJSend) {
									case 1 :
										contents.append('$');
										break;
									case 2 :
										contents.append('t');
										break;
									case 3 :
										contents.append('a');
										break;
									case 4 :
										contents.append('g');
										break;
									default :
										contents.append('_');
								}
							}
							int end = validJSstart + container.getStartOffset(region);
							// remember that this source range w
							fGeneratedRanges.add(new Region(start, end - start));
						}
						// set up to end while if no end for valid
						if (validJSstart > 0) {
							int serverSideStartGuess = -1;
							for (int i = 0; i < fServerSideDelimiters.length; i++) {
								int index = regionText.indexOf(fServerSideDelimiters[i][0], validJSstart);
								if (serverSideStartGuess < 0) {
									serverSideStartGuess = index;
									serverSideDelimiter = i;
								}
								else if (index >= 0) {
									serverSideStartGuess = Math.min(serverSideStartGuess, index);
									if (serverSideStartGuess == index) {
										serverSideDelimiter = i;
									}
								}
							}
							serverSideStart = validJSstart < regionLength - fShortestServerSideDelimiterPairLength ? serverSideStartGuess : -1;
							clientMatchStart = validJSstart < regionLength ? (matcher.find(validJSstart + 1) ? matcher.start() : -1) : -1;
						}
						else {
							serverSideStart = clientMatchStart = -1;
						}
					}
					if (validJSstart >= 0) {
						contents.append(regionText.substring(validJSstart));
					}
					if (contents.length() != 0) {
						fScriptText.append(contents.toString());
					}
					else {
						fScriptText.append(regionText);
					}
					Position inHtml = new Position(scriptStart, scriptTextLength);
					scriptLocationInHtml.add(inHtml);
//				}
								
				scriptOffset = fScriptText.length();
			}
		}
		
		IStructuredDocumentRegion endTag = container.getNext();
		
		if(endTag==null) {
			missingEndTagRegionStart = container.getStartOffset();
		}else if(endTag!=null) {
			NodeHelper nh = new NodeHelper(endTag);
			String name = nh.getTagName();
			
			if(name==null || !name.trim().equalsIgnoreCase("script") || !nh.isEndTag()) { //$NON-NLS-1$
				missingEndTagRegionStart = container.getStartOffset();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.web.core.javascript.IJsTranslator#translateScriptImportNode(org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion)
	 */
	public void translateScriptImportNode(IStructuredDocumentRegion region) {
		NodeHelper nh = new NodeHelper(region);
		String importName = nh.getAttributeValue("src"); //$NON-NLS-1$
		if (importName != null && !importName.equals("")) { //$NON-NLS-1$
			rawImports.add(importName);
			Position inHtml = new Position(region.getStartOffset(), region.getEndOffset());
			importLocationsInHtml.add(inHtml);
		}
	}



	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.IDocumentListener#documentAboutToBeChanged(org.eclipse.jface.text.DocumentEvent)
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.web.core.javascript.IJsTranslator#documentAboutToBeChanged(org.eclipse.jface.text.DocumentEvent)
	 */
	public void documentAboutToBeChanged(DocumentEvent event) {
		cancelParse = true;
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.IDocumentListener#documentChanged(org.eclipse.jface.text.DocumentEvent)
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.web.core.javascript.IJsTranslator#documentChanged(org.eclipse.jface.text.DocumentEvent)
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
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.web.core.javascript.IJsTranslator#release()
	 */
	public void release() {
		fStructuredDocument.removeDocumentListener(this);
	}

	/**
	 * @return the fGeneratedRanges
	 */
	Region[] getGeneratedRanges() {
		return (Region[]) fGeneratedRanges.toArray(new Region[fGeneratedRanges.size()]);
	}
}