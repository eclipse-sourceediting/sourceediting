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
package org.eclipse.jst.jsp.core.internal.java;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.jst.jsp.core.internal.parser.JSPSourceParser;
import org.eclipse.jst.jsp.core.model.parser.DOMJSPRegionContexts;
import org.eclipse.wst.sse.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.sse.core.internal.contentmodel.CMNode;
import org.eclipse.wst.sse.core.parser.BlockMarker;
import org.eclipse.wst.sse.core.parser.StructuredDocumentRegionHandler;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.util.Debug;
import org.eclipse.wst.sse.core.util.StringUtils;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;


/**
 * Parser/helper class for JSPTranslator.  Used for parsing XML-JSP regions (in a script block)
 * A lot of logic borrowed from TLDCMDocumentManager.  There should be only one XMLJSPRegionHelper per text file
 * 
 * @author pavery
 */
class XMLJSPRegionHelper implements StructuredDocumentRegionHandler {
	private final JSPTranslator fTranslator;
	protected JSPSourceParser fLocalParser = null;
	protected String fTextToParse = null;
	// need this if not at the start of the document (eg. parsing just a script block)
	protected int fStartOfTextToParse = 0;
	// buffers for text that this class parses	
	protected List fScriptlets = new ArrayList();
	protected List fExpressions = new ArrayList();
	protected List fDeclarations = new ArrayList();
	// name of the open tag that was last handled (if we are interested in it)
	protected String fTagname = null;
	protected String fTextBefore = ""; //$NON-NLS-1$
	protected String fUnescapedText = ""; //$NON-NLS-1$
	protected String fStrippedText = ""; //$NON-NLS-1$
	// for reconciling cursor position later
	int fPossibleOwner = JSPTranslator.SCRIPTLET;

	public XMLJSPRegionHelper(JSPTranslator translator) {
		getLocalParser().addStructuredDocumentRegionHandler(this);
		this.fTranslator = translator;
	}

	protected JSPSourceParser getLocalParser() {
		if (fLocalParser == null)
			fLocalParser = new JSPSourceParser();
		return fLocalParser;
	}

	public void addBlockMarker(BlockMarker marker) {
		fLocalParser.addBlockMarker(marker);
	}

	public void reset(String textToParse) {
		reset(textToParse, 0);
	}

	public void reset(String textToParse, int start) {
		fStartOfTextToParse = start;
		getLocalParser().reset(textToParse);
		fTextToParse = textToParse;
	}

	public void forceParse() {
		getLocalParser().getDocumentRegions();
		fLocalParser = null;
	}

	/*
	 * parse an entire file
	 */
	public void parse(String filename) {
		// from outer class
		List blockMarkers = this.fTranslator.getBlockMarkers();
		reset(getContents(filename));
		// this adds the current markers from the outer class list
		// to this parser so parsing works correctly
		for (int i = 0; i < blockMarkers.size(); i++) {
			addBlockMarker((BlockMarker) blockMarkers.get(i));
		}
		forceParse();
	}

	/*
	 * writes out scriptlet, expression, and declaration buffers
	 * to the ongoing buffers in the JSPTranslator (calls to outer JSPTranslator methods)
	 */
	public void writeToBuffers() {
		IStructuredDocumentRegion currentNode = fTranslator.getCurrentNode();
		// currentNode should be the <%@page include="xxx"%> StructuredDocumentRegion
		for (int i = 0; i < fScriptlets.size(); i++) {
			this.fTranslator.translateScriptletString((String) fScriptlets.get(i), currentNode, currentNode.getStartOffset(), currentNode.getLength());
		}
		for (int i = 0; i < fExpressions.size(); i++) {
			this.fTranslator.translateExpressionString((String) fExpressions.get(i), currentNode, currentNode.getStartOffset(), currentNode.getLength());
		}
		for (int i = 0; i < fDeclarations.size(); i++) {
			this.fTranslator.translateDeclarationString((String) fDeclarations.get(i), currentNode, currentNode.getStartOffset(), currentNode.getLength());
		}
	}

	/*
	 * listens to parser node parsed events
	 * adds to local scriplet, expression, declaration buffers
	 * determines which type of region the cursor is in, and adjusts cursor offset accordingly 
	 */
	public void nodeParsed(IStructuredDocumentRegion sdRegion) {
		//				System.out.println("tagname > " + fTagname);
		//				System.out.println("sdRegion > " + sdRegion.getType());
		//				System.out.println("sdRegion text is >> " + fTextToParse.substring(sdRegion.getStartOffset(), sdRegion.getEndOffset()));
		//				System.out.println("+++=======================");
		try {
			if (isJSPStartRegion(sdRegion)) {
				String nameStr = getRegionName(sdRegion);
				if (isJSPRegion(nameStr))
					fTagname = nameStr;
				else
					fTagname = null;
			}
			else if (sdRegion.getFirstRegion().getType() == DOMJSPRegionContexts.JSP_CONTENT) {
				if (fTagname != null) {
					// assign contents to one of the tables
					if (isScriptlet(fTagname)) {
						processScriptlet(sdRegion);
					}
					else if (isExpression(fTagname)) {
						processExpression(sdRegion);
					}
					else if (isDeclaration(fTagname)) {
						processDeclaration(sdRegion);
					}
					else {
						processOtherRegions(sdRegion);
					}
				}
			}
			else if (sdRegion.getFirstRegion().getType() == XMLRegionContext.XML_CONTENT) {
				if (fTagname != null) {
					processUseBean(sdRegion);
					processOtherRegions(sdRegion);
				}
			}
			else {
				fTagname = null;
			}
			// this updates cursor position
			checkCursorInRegion(sdRegion);
		}
		catch (NullPointerException e) {
			// logging this exception that I've seen a couple of times...
			// seems to happen during shutdown of unit tests, at which 
			// point Logger has already been unloaded
			try {
			Logger.logException("XMLJSPRegionHelper: exception in node parsing", e); //$NON-NLS-1$
			}
			catch (NoClassDefFoundError ex) {
				// do nothing, since we're just ending
			}
		}
	}

	public void resetNodes() {
		// do nothing
	}

	private void checkCursorInRegion(IStructuredDocumentRegion sdRegion) {
		// if cursor is in this region...
		if (this.fTranslator.getSourcePosition() >= fStartOfTextToParse + sdRegion.getStartOffset() && this.fTranslator.getSourcePosition() <= fStartOfTextToParse + sdRegion.getEndOffset()) {
			int endOfNameTag = sdRegion.getStartOffset();
			int offset = fTextBefore.length() - fStrippedText.length();
			// offset in addtion to what's already in the buffer
			this.fTranslator.setRelativeOffset(this.fTranslator.getSourcePosition() - (fStartOfTextToParse + endOfNameTag) - offset);
			// outer class method
			this.fTranslator.setCursorOwner(fPossibleOwner);
			// add length of what's already in the buffer
			this.fTranslator.setRelativeOffset(this.fTranslator.getRelativeOffset() + this.fTranslator.getCursorOwner().length());
			if (fPossibleOwner == JSPTranslator.EXPRESSION) {
				// add length of expression prefix if necessary...
				this.fTranslator.setRelativeOffset(this.fTranslator.getRelativeOffset() + JSPTranslator.EXPRESSION_PREFIX.length());
			}
		}
	}

	protected void processDeclaration(IStructuredDocumentRegion sdRegion) {
		prepareText(sdRegion);
		fDeclarations.add(fStrippedText);
		fPossibleOwner = JSPTranslator.DECLARATION;
	}

	protected void processExpression(IStructuredDocumentRegion sdRegion) {
		prepareText(sdRegion);
		fExpressions.add(fStrippedText);
		fPossibleOwner = JSPTranslator.EXPRESSION;
	}

	protected void processScriptlet(IStructuredDocumentRegion sdRegion) {
		prepareText(sdRegion);
		fScriptlets.add(fStrippedText);
		fPossibleOwner = JSPTranslator.SCRIPTLET;
	}

	/*
	 * Substitutes values for entity references, strips CDATA tags, and keeps
	 * track of string length(s) for cursor position calculation later.
	 * @param sdRegion
	 */
	protected void prepareText(IStructuredDocumentRegion sdRegion) {
		fTextBefore = fTextToParse.substring(sdRegion.getStartOffset(), sdRegion.getEndOffset());
		fUnescapedText = EscapedTextUtil.getUnescapedText(fTextBefore);
		fStrippedText = this.fTranslator.stripCDATA(fUnescapedText);
	}

	protected void processUseBean(IStructuredDocumentRegion sdRegion) {
		if (fTagname != null && isUseBean(fTagname)) {
			// previous region has the actual attributes
			sdRegion = sdRegion.getPrevious();
			String beanClass, beanType, beanId, beanDecl = ""; //$NON-NLS-1$
			beanClass = getAttributeValue("class", sdRegion); //$NON-NLS-1$
			beanType = getAttributeValue("type", sdRegion); //$NON-NLS-1$
			beanId = getAttributeValue("id", sdRegion); //$NON-NLS-1$
			
			if (beanId != null && (beanType != null || beanClass != null)) {
				if (beanType.equals(""))
				    beanType = beanClass;
				String prefix = beanType + " " + beanId + " = "; //$NON-NLS-1$ //$NON-NLS-2$
				String suffix = "null;\n"; //$NON-NLS-1$
				if (beanClass != null)
					suffix = "new " + beanClass + "();\n"; //$NON-NLS-1$ //$NON-NLS-2$
				beanDecl = prefix + suffix;
			}	
			
			fScriptlets.add(beanDecl);
			fPossibleOwner = JSPTranslator.SCRIPTLET;
		}
	}

	protected void processOtherRegions(IStructuredDocumentRegion sdRegion) {
		processIncludeDirective(sdRegion);
		processPageDirective(sdRegion);
	}

	protected void processIncludeDirective(IStructuredDocumentRegion sdRegion) {
		if (isIncludeDirective(fTagname)) {
			// the directive name region itself contains the attrs...
			if (sdRegion.getRegions().get(0).getType() == XMLRegionContext.XML_CONTENT)
				sdRegion = sdRegion.getPrevious();
			String fileLocation = getAttributeValue("file", sdRegion); //$NON-NLS-1$
			this.fTranslator.handleIncludeFile(fileLocation);
		}
		else if (isPossibleCustomTag(fTagname)) {
			// this custom tag may define variables
			this.fTranslator.addTaglibVariables(fTagname);
		}
		else if (isTaglibDirective(fTagname)) {
			// also add the ones created here to the parent document
			String prefix = getAttributeValue("prefix", sdRegion); //$NON-NLS-1$
			List docs = this.fTranslator.getTLDCMDocumentManager().getCMDocumentTrackers(prefix, this.fTranslator.getCurrentNode().getEnd());
			Iterator it = docs.iterator();
			Iterator elements = null;
			CMNode node = null;
			CMDocument doc = null;
			BlockMarker marker = null;
			while (it.hasNext()) {
				doc = (CMDocument) it.next();
				elements = doc.getElements().iterator();
				while (elements.hasNext()) {
					node = (CMNode) elements.next();
					marker = new BlockMarker(node.getNodeName(), null, DOMJSPRegionContexts.JSP_CONTENT, true);
					// global scope is OK because we have encountered this <@taglib> directive
					// so it all markers from it should will be in scope
					// add to this local parser
					addBlockMarker(marker);
					// add to outer class marker list, for 
					this.fTranslator.getBlockMarkers().add(marker);
				}
			}
		}
	}

	protected void processPageDirective(IStructuredDocumentRegion sdRegion) {
		if (isPageDirective(fTagname)) {
			while (sdRegion != null) {
				if (sdRegion.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME)
					break;
				sdRegion = sdRegion.getPrevious();
			}
			String importValue = getAttributeValue("import", sdRegion); //$NON-NLS-1$
			if (importValue != "") { //$NON-NLS-1$
				// had to add "false" parameter to ensure these 
				// imports don't get added to jsp <-> java map (since they are from an included file)
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=81687
				this.fTranslator.addImports(importValue, false);
			}
		}
	}	

	/*
	 * convenience method to get an attribute value from attribute name
	 */
	protected String getAttributeValue(String attrName, IStructuredDocumentRegion sdRegion) {
		String sdRegionText = fTextToParse.substring(sdRegion.getStartOffset(), sdRegion.getEndOffset());
		String textRegionText, attrValue = ""; //$NON-NLS-1$
		Iterator it = sdRegion.getRegions().iterator();
		ITextRegion nameRegion, valueRegion = null;
		while (it.hasNext()) {
			nameRegion = (ITextRegion) it.next();
			if (nameRegion.getType() == XMLRegionContext.XML_TAG_ATTRIBUTE_NAME) {
				textRegionText = sdRegionText.substring(nameRegion.getStart(), nameRegion.getEnd());
				if (textRegionText.equalsIgnoreCase(attrName)) {
					while (it.hasNext()) {
						valueRegion = (ITextRegion) it.next();
						if (valueRegion.getType() == XMLRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
							attrValue = sdRegionText.substring(valueRegion.getStart(), valueRegion.getEnd());
							break; // inner
						}
					}
					break; // outer
				}
			}
		}
		return StringUtils.stripQuotes(attrValue);
	}

	// these methods determine what content gets added to the local scriplet, expression, declaration buffers
	/*
	 * return true for elements whose contents we might want to add to the java file we are building
	 */
	protected boolean isJSPStartRegion(IStructuredDocumentRegion sdRegion) {
		return (sdRegion.getFirstRegion().getType() == XMLRegionContext.XML_TAG_OPEN || sdRegion.getFirstRegion().getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_OPEN);
	}

	protected boolean isJSPRegion(String tagName) {
		return isDeclaration(tagName) || isExpression(tagName) || isScriptlet(tagName) || isUseBean(tagName) || isIncludeDirective(tagName) || isPossibleCustomTag(tagName) || isTaglibDirective(tagName) || isPageDirective(tagName);
	}

	protected boolean isDeclaration(String tagName) {
		return tagName.equalsIgnoreCase("jsp:declaration"); //$NON-NLS-1$		
	}

	protected boolean isExpression(String tagName) {
		return tagName.equalsIgnoreCase("jsp:expression"); //$NON-NLS-1$		
	}

	protected boolean isScriptlet(String tagName) {
		return tagName.equalsIgnoreCase("jsp:scriptlet"); //$NON-NLS-1$
	}

	protected boolean isUseBean(String tagName) {
		return tagName.equalsIgnoreCase("jsp:useBean"); //$NON-NLS-1$
	}

	protected boolean isIncludeDirective(String tagName) {
		return tagName.equalsIgnoreCase("jsp:directive.include"); //$NON-NLS-1$
	}

	protected boolean isPossibleCustomTag(String tagName) {
		return tagName.indexOf(":") > 1; //$NON-NLS-1$
	}

	protected boolean isTaglibDirective(String tagName) {
		return tagName.equalsIgnoreCase("jsp:directive.taglib"); //$NON-NLS-1$
	}

	protected boolean isPageDirective(String tagName) {
		return tagName.equalsIgnoreCase("jsp:directive.page"); //$NON-NLS-1$
	}

	protected String getRegionName(IStructuredDocumentRegion sdRegion) {
		ITextRegion nameRegion = null;
		String nameStr = ""; //$NON-NLS-1$
		int size = sdRegion.getRegions().size();
		if (size > 1) {
			// presumably XML-JSP <jsp:scriptlet> | <jsp:expression> | <jsp:declaration>
			nameRegion = sdRegion.getRegions().get(1);
			nameStr = fTextToParse.substring(sdRegion.getStartOffset(nameRegion), sdRegion.getTextEndOffset(nameRegion));
		}
		return nameStr.trim();
	}

	/*
	 * get the contents of a file as a String
	 */
	protected String getContents(String fileName) {
		StringBuffer s = new StringBuffer();
		int c = 0;
		int count = 0;
		InputStream is = null;
		try {
			IPath filePath = new Path(fileName);
			IFile f = ResourcesPlugin.getWorkspace().getRoot().getFile(filePath);
			if(f != null && !f.exists()) {
				f = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(filePath);
			}
            if (f != null && f.exists()) {
    			is = f.getContents();
    			while ((c = is.read()) != -1) {
    				count++;
    				s.append((char) c);
    			}
            }
		}
		catch (Exception e) {
			if (Debug.debugStructuredDocument)
				e.printStackTrace();
		}
		finally {
			try {
				if (is != null) {
					is.close();
				}
			}
			catch (Exception e) {
				// nothing to do
			}
		}
		return s.toString();
	}
}