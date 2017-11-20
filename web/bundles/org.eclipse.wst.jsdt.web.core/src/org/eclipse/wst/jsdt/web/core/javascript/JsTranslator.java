/*******************************************************************************
 * Copyright (c) 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.jsdt.web.core.javascript;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.DocumentRewriteSessionEvent;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IDocumentRewriteSessionListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.wst.jsdt.core.IBuffer;
import org.eclipse.wst.jsdt.web.core.internal.Logger;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionCollection;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
/**
 *
 * Provisional API: This class/interface is part of an interim API that is still under development and expected to
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
 * (repeatedly) as the API evolves.
 * 
 * Translates a web page into its JavaScript pieces. 
 */
public class JsTranslator extends Job implements IJsTranslator, IDocumentListener {
	
	protected static final boolean DEBUG;
	private static final boolean DEBUG_SAVE_OUTPUT = false;  //"true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.jsdt.web.core/debug/jstranslationstodisk")); //$NON-NLS-1$  //$NON-NLS-2$
//	private static final String ENDL = "\n"; //$NON-NLS-1$
	
	private static final String XML_COMMENT_START = "<!--"; //$NON-NLS-1$
//	private static final String XML_COMMENT_END = "-->"; //$NON-NLS-1$
	
	private static final String CDATA_START = "<![CDATA["; //$NON-NLS-1$
	private static final String CDATA_START_PAD = new String(Util.getPad(CDATA_START.length()));
	private static final String CDATA_END = "]]>"; //$NON-NLS-1$
	private static final String CDATA_END_PAD = new String(Util.getPad(CDATA_END.length()));
	
	private static final String EVENT_HANDLER_PRE  = "(function(){"; //$NON-NLS-1$
	private static final int EVENT_HANDLER_PRE_LENGTH = EVENT_HANDLER_PRE.length();
	private static final String EVENT_HANDLER_POST = "})();"; //$NON-NLS-1$
	private static final int EVENT_HANDLER_POST_LENGTH = EVENT_HANDLER_POST.length();
	
	//TODO: should be an inclusive rule rather than exclusive
	private static final Pattern fClientSideTagPattern = Pattern.compile("<[^<%?)!>]+/?>|<!--|-->|<\\!\\[CDATA\\[|]]>"); //$NON-NLS-1$

	// FIXME: Workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=307401
	private String[][] fServerSideDelimiters = new String[][]{{"<%","%>"},{"<?","?>"}};
	private int fShortestServerSideDelimiterPairLength = 4;
	
	static {
		String value = Platform.getDebugOption("org.eclipse.wst.jsdt.web.core/debug/jsjavamapping"); //$NON-NLS-1$
 		DEBUG = value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
	}
	
	private class DocumentRewriteSessionListener implements IDocumentRewriteSessionListener {
		public void documentRewriteSessionChanged(DocumentRewriteSessionEvent event) {
			if (DocumentRewriteSessionEvent.SESSION_START.equals(event.getChangeType())) {
				fIsInRewriteSession = true;
			}
			else if (DocumentRewriteSessionEvent.SESSION_STOP.equals(event.getChangeType())) {
				fIsInRewriteSession = false;
				schedule();
			}
		}		
	}

	private IStructuredDocumentRegion fCurrentNode;
	boolean fIsInRewriteSession = false;
	protected StringBuffer fScriptText = new StringBuffer();
	protected IStructuredDocument fStructuredDocument = null;
	protected ArrayList importLocationsInHtml = new ArrayList();
	/* use java script by default */
	protected boolean fIsGlobalJs = true;
	protected ArrayList rawImports = new ArrayList(); // translated
	/**
	 * deprecated
	 */
	protected ArrayList scriptLocationInHtml = new ArrayList();
	/**
	 * Map of web page ranges to translation source rangers.  Order matters.
	 */
	private Map fPositionMap = new LinkedHashMap();
	protected int scriptOffset = 0;
	
	protected byte[] fLock = new byte[0];
	protected byte[] finished = new byte[0];
	
	protected IBuffer fCompUnitBuff;
	protected boolean cancelParse = false;
	protected int missingEndTagRegionStart = -1;
	protected static final boolean ADD_SEMICOLON_AT_INLINE=true;
	private IDocumentRewriteSessionListener fDocumentRewriteSessionListener = new DocumentRewriteSessionListener();

	/*
	 * org.eclipse.jface.text.Regions of the web page that contain purely
	 * generated code, for which no validation messages should be reported to
	 * the user.
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
		super("JavaScript translation for : " + fileName); //$NON-NLS-1$
		fStructuredDocument = document;
		if (listenForChanges) {
			fStructuredDocument.addDocumentListener(this);
			if (fStructuredDocument instanceof IDocumentExtension4) {
				((IDocumentExtension4) fStructuredDocument).addDocumentRewriteSessionListener(fDocumentRewriteSessionListener);
			}
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
			/* if mid re-write session doc changes have been ignored,
			 * but if jsText is specifically request we should re-translate
			 * to pick up any changes thus far
			 * 
			 * Same goes for if the job has been scheduled to run but has not yet run
			 */
			if(this.fIsInRewriteSession || this.getState() != Job.NONE) {
				this.reset();
			}
			
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
			fCompUnitBuff.setContents(getJsText());
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
		synchronized (finished) {
			synchronized (fLock) {
				scriptOffset = 0;
				// reset progress monitor
				fScriptText = new StringBuffer();
				fCurrentNode = fStructuredDocument.getFirstStructuredDocumentRegion();
				rawImports.clear();
				importLocationsInHtml.clear();
				fPositionMap.clear();
				scriptLocationInHtml.clear();
				missingEndTagRegionStart = -1;
				cancelParse = false;
				fGeneratedRanges.clear();
			}
			translate();
		}
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
						if (NodeHelper.isInArray(JsDataTypes.JSVALIDDATATYPES, nh.getAttributeValue("type")) || NodeHelper.isInArray(JsDataTypes.JSVALIDDATATYPES, nh.getAttributeValue("language")) || (nh.getAttributeValue("type")==null && nh.getAttributeValue("language")==null && isGlobalJs())) { //$NON-NLS-1$ //$NON-NLS-2$
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
	}
	
	/**
	 * @deprecated
	 */
	protected void appendAndTrack(String javaScript, int scriptStart, int scriptTextLength) {
		appendAndTrack(javaScript, scriptStart);
	}
	
	protected void appendAndTrack(String javaScript, int webPageScriptStart) {
		int length = javaScript.length();
		Position inHtml = new Position(webPageScriptStart, length);
		fPositionMap.put(inHtml, new Position(fScriptText.length(), length));
		fScriptText.append(javaScript);
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
					String tagAttrname = container.getText(r);
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
							String rawText = container.getText(r);
							if (rawText == null || rawText.length() == 0) {
								continue;
							}
							/* Strip quotes */
							switch (rawText.charAt(0)) {
								case '\'':
								case '"':
									rawText = rawText.substring(1);
									valStartOffset++;
							}
							if (rawText == null || rawText.length() == 0) {
								continue;
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
							/* need to pad the script text with spaces */
							char[] spaces = Util.getPad(Math.max(0, valStartOffset - scriptOffset - EVENT_HANDLER_PRE_LENGTH));
							for (int i = 0; i < spaces.length; i++) {
								try {
									char c = fStructuredDocument.getChar(scriptOffset + i);
									if (c == '\n' || c == '\r' || c == '\t')
										spaces[i] = c;
								}
								catch (BadLocationException e) {
									Logger.logException(e);
								}
							}
							fScriptText.append(spaces);
							fScriptText.append(EVENT_HANDLER_PRE);
							appendAndTrack(rawText, valStartOffset);
							if(ADD_SEMICOLON_AT_INLINE) fScriptText.append(";"); //$NON-NLS-1$
							if(r.getLength() > rawText.length()) {
								fScriptText.append(EVENT_HANDLER_POST);
								spaces = Util.getPad(Math.max(0, r.getLength() - rawText.length() - EVENT_HANDLER_POST_LENGTH));
								fScriptText.append(spaces);
							}
							scriptOffset = container.getEndOffset(r);
						}
					}
				}
			}
		}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.web.core.javascript.IJsTranslator#translateJSNode(org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion)
	 */
	public void translateJSNode(IStructuredDocumentRegion container) {
		if(container==null) return;
		
		ITextRegionCollection containerRegion = container;
		Iterator regions = containerRegion.getRegions().iterator();
		ITextRegion region = null;
		
		char[] spaces = Util.getPad(container.getStartOffset() - scriptOffset);
		for (int i = 0; i < spaces.length; i++) {
			try {
				char c = fStructuredDocument.getChar(scriptOffset + i);
				if (c == '\n' || c == '\r' || c == '\t')
					spaces[i] = c;
			}
			catch (BadLocationException e) {
				Logger.logException(e);
			}
		}
		fScriptText.append(spaces);
		scriptOffset = container.getStartOffset();
	
		if(container.getType()!=DOMRegionContext.BLOCK_TEXT && container.getType()!= DOMRegionContext.XML_CDATA_TEXT) {
			// add place holder in position map (for formatting)
			appendAndTrack("", scriptOffset);
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
				int scriptStartOffset = container.getStartOffset(region);
				int scriptTextLength = container.getLength();
				String regionText = container.getFullText(region);
//				regionText = StringUtils.replace(regionText, CDATA_START, CDATA_START_PAD);
//				regionText = StringUtils.replace(regionText, CDATA_END, CDATA_END_PAD);
				int regionLength = region.getLength();
				
				spaces = Util.getPad(scriptStartOffset - scriptOffset);
				for (int i = 0; i < spaces.length; i++) {
					try {
						char c = fStructuredDocument.getChar(scriptOffset + i);
						if (c == '\n' || c == '\r' || c == '\t')
							spaces[i] = c;
					}
					catch (BadLocationException e) {
						Logger.logException(e);
					}
				}
				fScriptText.append(spaces); 	
				// skip over XML/HTML comment starts
				if (regionText.indexOf(XML_COMMENT_START) >= 0) {
					int index = regionText.indexOf(XML_COMMENT_START);
					
					boolean replaceCommentStart = true;
					for (int i = 0; i < index; i++) {
						/*
						 * replace the comment start in the translation when
						 * it's preceded only by white space or '/'
						 */
						replaceCommentStart = replaceCommentStart && (Character.isWhitespace(regionText.charAt(i)) || '/' == regionText.charAt(i));
					}
					
					if (replaceCommentStart) {
						IRegion line;
						int end;
						int length;
						try {
							/*
							 * try to find where the line with the comment
							 * ends (it is the end of what we'll replace)
							 */
							line = container.getParentDocument().getLineInformationOfOffset(index + scriptStartOffset);
							end = line.getOffset() + line.getLength() - scriptStartOffset;
							if(end > regionText.length()) {
								end = regionText.length();
							}
							length = end - index;
						} catch (BadLocationException e) {
							Logger.logException("Could not get web page's comment line information", e); //$NON-NLS-1$
							
							end = index + XML_COMMENT_START.length();
							length = XML_COMMENT_START.length();
						}
						scriptStartOffset += end;
						regionText = regionText.substring(end);
						appendAndTrack(new String(Util.getPad(end)), fScriptText.length());
					}
				}

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

				StringBuffer generatedContent = new StringBuffer();
				
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
						appendAndTrack(regionText.substring(validJSstart, validJSend), scriptStartOffset + validJSstart);
						
						// change the skipped content to a valid variable name and append it as a placeholder
						int startOffset = scriptStartOffset + validJSend;

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

						/*
						 * the substitution text length much match original
						 * length exactly, generate text of the right length
						 */
						int start = scriptStartOffset + validJSend;
						int end = scriptStartOffset + validJSstart;
						generatedContent.append('_');
						for (int i = validJSend + 1; i < validJSstart; i++) {
							switch (i - validJSend) {
								case 1 :
									generatedContent.append('$');
									break;
								case 2 :
									generatedContent.append('t');
									break;
								case 3 :
									generatedContent.append('a');
									break;
								case 4 :
									generatedContent.append('g');
									break;
								default :
									generatedContent.append('_');
							}
						}
						/*
						 * Remember this source range, it may be needed to
						 * find the original contents for which we're
						 * placeholding
						 */
						fGeneratedRanges.add(new Region(start, end - start));
						appendAndTrack(generatedContent.toString(), start);
						// reset now that it's been appended
						generatedContent.setLength(0);
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
						clientMatchStart = validJSstart < regionLength ? (matcher.find(validJSstart) ? matcher.start() : -1) : -1;
					}
					else {
						serverSideStart = clientMatchStart = -1;
					}
				}
				if (validJSstart >= 0) {
					appendAndTrack(regionText.substring(validJSstart), scriptStartOffset + validJSstart);
					Position inHtml = new Position(scriptStartOffset + validJSstart, regionText.length() - validJSstart);
					scriptLocationInHtml.add(inHtml);
				}
				else {
					appendAndTrack(regionText, scriptStartOffset);
					Position inHtml = new Position(scriptStartOffset, regionText.length());
					scriptLocationInHtml.add(inHtml);
				}
								
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
		if (fIsInRewriteSession) {
			return;
		}

		cancel();
		schedule();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor) {
		reset();
		return Status.OK_STATUS;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.web.core.javascript.IJsTranslator#release()
	 */
	public void release() {
		cancel();
		fStructuredDocument.removeDocumentListener(this);
		if (fStructuredDocument instanceof IDocumentExtension4) {
			((IDocumentExtension4) fStructuredDocument).removeDocumentRewriteSessionListener(fDocumentRewriteSessionListener);
		}
	}

	/**
	 * @return the generated pages, in web page offsets
	 */
	public Region[] getGeneratedRanges() {
		return (Region[]) fGeneratedRanges.toArray(new Region[fGeneratedRanges.size()]);
	}

	/**
	 * @param offset
	 *            web page offset
	 * @return the corresponding JavaScript offset, or -1 if one can not be
	 *         calculated
	 */
	int getJavaScriptOffset(int offset) {
		synchronized (finished) {
			Map.Entry[] entries = (Map.Entry[]) fPositionMap.entrySet().toArray(new Map.Entry[fPositionMap.size()]);
			for (int i = entries.length - 1; i >= 0; i--) {
				Position position = (Position) entries[i].getKey();
				if (position.includes(offset) || (offset == position.getOffset() + position.getLength())) {
					return offset - position.getOffset() + ((Position) entries[i].getValue()).getOffset();
				}
			}
			return -1;
		}
	}

	/**
	 * @param offset JavaScript offset
	 * @return the corresponding web page offset, or -1 if one can not be
	 *         calculated
	 */
	int getWebOffset(int offset) {
		synchronized (finished) {
			Map.Entry[] entries = (Map.Entry[]) fPositionMap.entrySet().toArray(new Map.Entry[fPositionMap.size()]);
			for (int i = entries.length - 1; i >= 0; i--) {
				Position position = (Position) entries[i].getValue();
				if (position.includes(offset) || (offset == position.getOffset() + position.getLength())) {
					return offset - position.getOffset() + ((Position) entries[i].getKey()).getOffset();
				}
			}
			return -1;
		}
	}
}