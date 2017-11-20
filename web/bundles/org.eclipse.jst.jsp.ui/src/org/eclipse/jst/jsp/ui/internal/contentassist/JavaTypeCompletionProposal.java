/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.contentassist;



import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jst.jsp.core.internal.provisional.JSP11Namespace;
import org.eclipse.jst.jsp.core.internal.provisional.JSP12Namespace;
import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.sse.ui.internal.contentassist.IRelevanceCompletionProposal;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

/**
 * An implementation of ICompletionProposal whose values can be
 * read after creation.
 * 
 * @plannedfor 1.0
 */
public class JavaTypeCompletionProposal extends CustomCompletionProposal implements IRelevanceCompletionProposal {

	private int fCursorPosition = 0;
	private String fLocalDisplayString;
	private String fShortName;
	private String fQualifiedName;
	private String fContainerName;

	public JavaTypeCompletionProposal(String replacementString, int replacementOffset, int replacementLength, String qualifiedName, Image image, String typeName, String containerName,  int relevence,  boolean updateReplacementLengthOnValidate) {
		super(replacementString, replacementOffset, replacementLength, qualifiedName.length() + 2, image, 
                (containerName != null && containerName.length() > 0) ? typeName + " - " + containerName : typeName, null, null, relevence, true); //$NON-NLS-1$
		// CMVC 243817, superclass was comparing incorrect display string in validate method...
		//super(replacementString, replacementOffset, replacementLength, image, (containerName != null && containerName.length() > 0)? typeName + " - " + containerName:typeName/*qualifiedName*/, relevence);
		fShortName = typeName;
		fQualifiedName = qualifiedName;
		fContainerName = containerName;
		fCursorPosition = fQualifiedName.length() + 2;
		//fProposalInfo = proposalInfo;
		if (containerName != null && containerName.length() > 0)
			fLocalDisplayString = typeName + " - " + containerName; //$NON-NLS-1$
		else
			fLocalDisplayString = typeName;
	}

	public String getDisplayString() {
		return fLocalDisplayString;
	}

	public int getCursorPosition() {
		return fCursorPosition;
	}

	public void setCursorPosition(int cursorPosition) {
		super.setCursorPosition(cursorPosition);
		fCursorPosition = cursorPosition;
	}

	public String getQualifiedName() {
		return fQualifiedName;
	}

	public String getAdditionalProposalInfo() {
		//		String info = super.getAdditionalProposalInfo();
		//		if (info == null || info.length() == 0 && fProposalInfo != null)
		//			return fProposalInfo.getInfo();
		//		return info;
		return null; // unexplained NPE
	}

	public String getShortName() {
		return fShortName;
	}

	protected String getImport(IStructuredDocumentRegion flatNode) {
		ITextRegionList regions = flatNode.getRegions();
		String importSpec = null;
		boolean isImport = false;
		for (int i = 0; i < regions.size(); i++) {
			ITextRegion region = regions.get(i);
			if (region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
				if (flatNode.getText(region).equals(JSP11Namespace.ATTR_NAME_IMPORT)) {
					isImport = true;
				}
				else {
					isImport = false;
				}
			}
			else if (isImport && region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
				importSpec = flatNode.getText(region);
			}
		}
		return importSpec;
	}

	/**
	 * Add an import page directive for the current type name
	 */
	protected int applyImport(IStructuredDocument model) {
		// if the type is in the default package or java.lang, skip it
		if (fContainerName == null || fContainerName.length() == 0 || fContainerName.equals("java.lang")) //$NON-NLS-1$
			return 0;
		// collect page directives and store their import values
		List imports = new ArrayList();
		IStructuredDocumentRegion node = model.getFirstStructuredDocumentRegion();

		// use the last position of a page directive as a hint as to where to add
		// a new one
		int hint = 0;
		// watch for jsp:root so that we use the right XML/JSP format for the directive
		boolean useXML = false;

		while (node != null) {
			// Can't just look for all StructuredDocumentRegions starting with JSP_DIRECTIVE_OPEN
			// since the XML form is required, too
			ITextRegionList regions = node.getRegions();
			if (regions.size() > 1) {
				ITextRegion name = regions.get(1);
				// verify that this is a JSP directive
				if (name.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME) {
					// verify that this is a *page* directive
					if (node.getText(name).equals(JSP11Namespace.ATTR_NAME_PAGE) || node.getText(name).equals(JSP12Namespace.ElementName.DIRECTIVE_PAGE)) {
						if (node.getEndOffset() < getReplacementOffset())
							hint = node.getEndOffset();
						String importSpec = getImport(node);
						if (importSpec != null) {
							imports.add(importSpec);
						}
					}
				}
				else {
					// if this is a jsp:root tag, use the XML form
					useXML = useXML || name.getType() == DOMJSPRegionContexts.JSP_ROOT_TAG_NAME;
				}
			}
			node = node.getNext();
		}

		// evaluate requirements for a "new" import directive
		boolean needsImport = !importHandles(fQualifiedName, imports);
		int adjustmentLength = 0;
		// insert "new" import directive
		if (needsImport) {
			String directive = null;

			// vary the XML behavior
			if (useXML) {
				directive = "<jsp:directive.page import=\"" + fQualifiedName + "\"/>"; //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				directive = "<%@ page import=\"" + fQualifiedName + "\" %>"; //$NON-NLS-1$ //$NON-NLS-2$
			}

			try {
				IRegion line = model.getLineInformationOfOffset(hint);
				boolean prependNewLine = line.getOffset() + line.getLength() == hint;
				boolean appendNewLine = hint == 0;
				if (prependNewLine)
					directive = model.getLineDelimiter() + directive;
				if (appendNewLine)
					directive = directive + model.getLineDelimiter();
				adjustmentLength = directive.length();
			}
			catch (BadLocationException e) {
				// ignore
			}

			try {
				model.replace(hint, 0, directive);

			}
			catch (BadLocationException e) {
				// Not that we should ever get a BLE, but if so, our
				// replacement offset from the Content Assist call should
				// work
				try {
					model.replace(getReplacementOffset(), 0, directive);
					adjustmentLength = directive.length();
				}
				catch (BadLocationException e2) {
					// now what?
				}
			}
		}
		return adjustmentLength;
	}

	/**
	 * See if the import specification is a wildcard import, and if so, that
	 * it applies to the given type.
	 */
	protected boolean isWildcardMatch(String importSpec, String type) {
		int specLength = importSpec.length();
		if (importSpec.endsWith("*") && specLength > 2 && type.length() >= specLength) { //$NON-NLS-1$
			// pull out the package name including the final '.'
			String container = importSpec.substring(0, specLength - 1);
			// verify that the type is in the container's hierarchy and that
			// there are no other package separators afterwards
			if (type.startsWith(container) && type.indexOf('.', specLength - 1) < 0) {
				// container matches
				return true;
			}
		}
		return false;
	}

	protected boolean importHandles(String type, List listOfImports) {
		Iterator imports = listOfImports.iterator();
		while (imports.hasNext()) {
			String importSpec = StringUtils.strip(imports.next().toString());
			if (importSpec.equals(type) || isWildcardMatch(importSpec, type))
				return true;
		}
		return false;
	}

	public void apply(IDocument document, char trigger, int offset) {
		// If we have a parsed IStructuredDocument, insert the short name instead of the
		// fully qualified name and a import page directive if
		// needed.  Do the import first so the cursor goes to the right location
		// and we don't surprise the user.

		boolean addShortForm = false; //document instanceof IStructuredDocument && fContainerName != null && fContainerName.length() > 0;
		if (addShortForm) {
			setReplacementString('"' + fShortName + '"');
			int importLength = applyImport((IStructuredDocument) document);
			setReplacementOffset(getReplacementOffset() + importLength);
		}

		setCursorPosition(getReplacementString().length());
		super.apply(document, trigger, offset);

	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension1#apply(org.eclipse.jface.text.ITextViewer, char, int, int)
	 */
	public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {
		// CMVC 243815
		// (pa) this is overridden to get around replacement length modification
		// which is done in the super (since eclipse 2.1)
		apply(viewer.getDocument(), trigger, offset);
	}

	// code is borrowed from JavaCompletionProposal
	protected boolean startsWith(IDocument document, int offset, String word) {
		int wordLength = word == null ? 0 : word.length();
		if (offset > getReplacementOffset() + wordLength)
			return false;

		try {
			int length = offset - getReplacementOffset();
			// CMVC 243817
			// slightly modified to be a little more flexible for attribute value string matching..
			String start = StringUtils.stripQuotes(document.get(getReplacementOffset(), length));
			return word.toLowerCase().startsWith(start.toLowerCase()) || fQualifiedName.toLowerCase().startsWith(start.toLowerCase());
			//return word.substring(0, length).equalsIgnoreCase(start);
		}
		catch (BadLocationException x) {
			// ignore
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.text.java.JavaCompletionProposal#validate(org.eclipse.jface.text.IDocument, int, org.eclipse.jface.text.DocumentEvent)
	 //	 */
	//	public boolean validate(IDocument document, int offset, org.eclipse.jface.text.DocumentEvent event) {
	//		return super.validate(document, offset, event);
	//	}
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.text.java.JavaCompletionProposal#selected(org.eclipse.jface.text.ITextViewer, boolean)
	 */
	public void selected(ITextViewer viewer, boolean smartToggle) {
		// (pa) we currently don't use smart toggle...
		super.selected(viewer, false);
	}

}
