/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.ui.reconcile;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcileResult;
import org.eclipse.jface.text.reconciler.IReconcileStep;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.ITextRegionList;
import org.eclipse.wst.sse.ui.StructuredTextReconciler;
import org.eclipse.wst.sse.ui.internal.reconcile.IReconcileAnnotationKey;
import org.eclipse.wst.sse.ui.internal.reconcile.StructuredReconcileStep;
import org.eclipse.wst.sse.ui.internal.reconcile.TemporaryAnnotation;
import org.eclipse.wst.xml.core.document.XMLModel;
import org.eclipse.wst.xml.core.document.XMLNode;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;
import org.eclipse.wst.xml.ui.Logger;
import org.eclipse.wst.xml.ui.internal.correction.ProblemIDsXML;
import org.eclipse.wst.xml.ui.nls.ResourceHandler;
import org.w3c.dom.Node;


/**
 * Basic XML syntax checking step.
 * 
 * @author pavery
 */
public class ReconcileStepForMarkup extends StructuredReconcileStep {

	protected String SEVERITY_ATTR_MISSING_VALUE = TemporaryAnnotation.ANNOT_ERROR;
	protected String SEVERITY_ATTR_NO_VALUE = TemporaryAnnotation.ANNOT_ERROR;
	// severities for the problems discoverable by this reconciler; possibly user configurable later
	protected String SEVERITY_GENERIC_ILLFORMED_SYNTAX = TemporaryAnnotation.ANNOT_WARNING;
	protected String SEVERITY_STRUCTURE = TemporaryAnnotation.ANNOT_ERROR;
	protected String SEVERITY_SYNTAX_ERROR = TemporaryAnnotation.ANNOT_ERROR;
	// used for attribute quote checking
	private String SQUOTE = "'"; //$NON-NLS-1$
	private String DQUOTE = "\""; //$NON-NLS-1$

	public ReconcileStepForMarkup() {
		super();
	}

	public ReconcileStepForMarkup(IReconcileStep step) {
		super(step);
	}

	/*
	 * @see org.eclipse.text.reconcilerpipe.AbstractReconcilePipeParticipant#reconcileModel(org.eclipse.jface.text.reconciler.DirtyRegion, org.eclipse.jface.text.IRegion)
	 */
	protected IReconcileResult[] reconcileModel(DirtyRegion dirtyRegion, IRegion subRegion) {
		if (dirtyRegion == null)
			return EMPTY_RECONCILE_RESULT_SET;
		Logger.trace(StructuredTextReconciler.TRACE_FILTER, "[trace reconciler] > reconcile model in MARKUP step w/ dirty region: [" + dirtyRegion.getOffset() + ":" + dirtyRegion.getLength() + "]" + (dirtyRegion == null ? "null" : dirtyRegion.getText())); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		IReconcileResult[] results = EMPTY_RECONCILE_RESULT_SET;
		IStructuredDocumentRegion[] regions = getStructuredDocumentRegions(dirtyRegion);
		for (int i = 0; i < regions.length; i++) {
			results = merge(results, reconcile(regions[i]));
		}

		Logger.trace(StructuredTextReconciler.TRACE_FILTER, "[trace reconciler] > MARKUP step done"); //$NON-NLS-1$
		return results;
	}

	/**
	 * A DirtyRegion can span multiple StructuredDocumentRegions.
	 * This method returns the StructuredDocumentRegions in a given dirty region.
	 * 
	 * @param dirtyRegion
	 */
	private IStructuredDocumentRegion[] getStructuredDocumentRegions(DirtyRegion dirtyRegion) {
		List regions = new ArrayList();
		IStructuredDocumentRegion sdRegion = getStructuredDocument().getRegionAtCharacterOffset(dirtyRegion.getOffset());
		regions.add(sdRegion);
		while ((sdRegion = sdRegion.getNext()) != null && sdRegion.getEndOffset() <= getXMLNode(sdRegion).getEndOffset())
			regions.add(sdRegion);
		return (IStructuredDocumentRegion[]) regions.toArray(new IStructuredDocumentRegion[regions.size()]);
	}

	/*
	 * check syntax of dirty region
	 */
	protected IReconcileResult[] reconcile(IStructuredDocumentRegion structuredDocumentRegion) {
		List results = new ArrayList();

// fix for https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=1939
// not sure why this was being done before
//		if (structuredDocumentRegion.getType() == XMLRegionContext.XML_CONTENT) {
//			// rollback to an open tag 
//			// ** can this be bad? removal region must exactly match add region
//			// 		or else we may get duplicates
//			while ((structuredDocumentRegion = structuredDocumentRegion.getPrevious()) != null && !isStartTag(structuredDocumentRegion)) {
//				continue;
//			}
//		}
		if (structuredDocumentRegion == null)
			return EMPTY_RECONCILE_RESULT_SET;

		if (isStartTag(structuredDocumentRegion)) {
			// check for attributes without a value
			checkForAttributeValue(structuredDocumentRegion, results);
			// check if started tag is ended
			checkStartEndTagPairs(structuredDocumentRegion, results);
			// check empty tag <>
			checkEmptyTag(structuredDocumentRegion, results);
			// check that each attribute has quotes
			checkQuotesForAttributeValues(structuredDocumentRegion, results);
			// check that the closing '>' is there
			checkClosingBracket(structuredDocumentRegion, results);
		}
		else if (isEndTag(structuredDocumentRegion)) {
			checkAttributesInEndTag(structuredDocumentRegion, results);
			// check that the closing '>' is there
			checkClosingBracket(structuredDocumentRegion, results);
		}
		else if (isPI(structuredDocumentRegion)) {
			// check validity of processing instruction
			checkStartingSpaceForPI(structuredDocumentRegion, results);
			checkNoNamespaceInPI(structuredDocumentRegion, results);
		}
		else if (isXMLContent(structuredDocumentRegion)) {
			checkForSpaceBeforeName(structuredDocumentRegion, results);
		}

		return (IReconcileResult[]) results.toArray(new IReconcileResult[results.size()]);
	}


	/**
	 * @param structuredDocumentRegion
	 * @param results
	 */
	private void checkClosingBracket(IStructuredDocumentRegion structuredDocumentRegion, List results) {
		ITextRegionList regions = structuredDocumentRegion.getRegions();
		ITextRegion r = null;
		boolean closed = false;
		for (int i = 0; i < regions.size(); i++) {
			r = regions.get(i);
			if(r.getType() == XMLRegionContext.XML_TAG_CLOSE || r.getType() == XMLRegionContext.XML_EMPTY_TAG_CLOSE)
				closed = true;
		}
		if(!closed) {
			
			String message = ResourceHandler.getString("ReconcileStepForMarkup.6"); //$NON-NLS-1$
			
			int start = structuredDocumentRegion.getStartOffset();
			int length = structuredDocumentRegion.getText().trim().length();
			Position p = new Position(start, length);
			IReconcileAnnotationKey key = createKey(structuredDocumentRegion, getScope());
	
			TemporaryAnnotation annotation = new TemporaryAnnotation(p, SEVERITY_SYNTAX_ERROR, message, key, ProblemIDsXML.MissingClosingBracket);
			results.add(annotation);
		}
	}

	/**
	 * @param structuredDocumentRegion
	 * @param results
	 */
	private void checkQuotesForAttributeValues(IStructuredDocumentRegion structuredDocumentRegion, List results) {
		ITextRegionList regions = structuredDocumentRegion.getRegions();
		ITextRegion r = null;
		String attrValueText = ""; //$NON-NLS-1$
		int errorCount = 0;
		for (int i = 0; i < regions.size() && errorCount < ELEMENT_ERROR_LIMIT; i++) {
			r = regions.get(i);
			if (r.getType() != XMLRegionContext.XML_TAG_ATTRIBUTE_VALUE)
				continue;

			attrValueText = structuredDocumentRegion.getText(r);
			// attribute value includes quotes in the string
			// split up attribute value on quotes
			StringTokenizer st = new StringTokenizer(attrValueText, "\"'", true); //$NON-NLS-1$
			int size = st.countTokens();
			// get the pieces of the attribute value
			String one = "", two = ""; //$NON-NLS-1$ //$NON-NLS-2$
			if (size > 0)
				one = st.nextToken();
			if (size > 1)
				two = st.nextToken();
			if (size > 2) {
				// should be handled by parsing...
				// as in we can't have an attribute value like: <element attr="a"b"c"/>
				// and <element attr='a"b"c' /> is legal
				continue;
			}


			if (size == 1) {
				if (one.equals(DQUOTE) || one.equals(SQUOTE)) {
					// missing closing quote
					String message = ResourceHandler.getString("ReconcileStepForMarkup.0"); //$NON-NLS-1$
					addAttributeError(message, attrValueText, structuredDocumentRegion.getStartOffset(r), attrValueText.trim().length(), ProblemIDsXML.Unclassified, structuredDocumentRegion, results);
					errorCount++;
				}
				else {
					// missing both
					String message = ResourceHandler.getString("ReconcileStepForMarkup.1"); //$NON-NLS-1$
					addAttributeError(message, attrValueText, structuredDocumentRegion.getStartOffset(r), attrValueText.trim().length(), ProblemIDsXML.AttrValueNotQuoted, structuredDocumentRegion, results);
					errorCount++;
				}
			}
			else if (size == 2) {
				if (one.equals(SQUOTE) && !two.equals(SQUOTE) || one.equals(DQUOTE) && !two.equals(DQUOTE)) {
					// missing closing quote
					String message = ResourceHandler.getString("ReconcileStepForMarkup.0"); //$NON-NLS-1$
					addAttributeError(message, attrValueText, structuredDocumentRegion.getStartOffset(r), attrValueText.trim().length(), ProblemIDsXML.Unclassified, structuredDocumentRegion, results);
					errorCount++;
				}
			}
		}
		// end of region for loop
	}

	private void addAttributeError(String message, String attributeValueText, int start, int length, int problemId, IStructuredDocumentRegion sdRegion, List results) {
		Position p = new Position(start, length);
		IReconcileAnnotationKey key = createKey(sdRegion, getScope());
		TemporaryAnnotation annotation = new TemporaryAnnotation(p, SEVERITY_SYNTAX_ERROR, message, key, problemId);
		annotation.setAdditionalFixInfo(attributeValueText);
		results.add(annotation);
	}

	/**
	 * @param annotationModel
	 * @param structuredDocumentRegion
	 */
	private void checkForSpaceBeforeName(IStructuredDocumentRegion structuredDocumentRegion, List results) {
		String sdRegionText = structuredDocumentRegion.getFullText();
		if (sdRegionText.startsWith(" ")) { //$NON-NLS-1$
			IStructuredDocumentRegion prev = structuredDocumentRegion.getPrevious();
			if (prev != null) {
				// this is possibly the case of "< tag"
				if (prev.getRegions().size() == 1 && isStartTag(prev)) {
					// add the error for preceding space in tag name
					String message = ResourceHandler.getString("ReconcileStepForMarkup.2"); //$NON-NLS-1$
					int start = structuredDocumentRegion.getStartOffset();
					// find length of whitespace
					int length  = sdRegionText.trim().equals("") ? sdRegionText.length() : sdRegionText.indexOf(sdRegionText.trim()); //$NON-NLS-1$
					
					Position p = new Position(start, length);
					IReconcileAnnotationKey key = createKey(structuredDocumentRegion, getScope());

					TemporaryAnnotation annotation = new TemporaryAnnotation(p, SEVERITY_SYNTAX_ERROR, message, key, ProblemIDsXML.SpacesBeforeTagName);
					results.add(annotation);
				}
			}
		}
	}

	/**
	 * @param annotationModel
	 * @param structuredDocumentRegion
	 */
	private void checkEmptyTag(IStructuredDocumentRegion structuredDocumentRegion, List results) {
		// navigate to name
		ITextRegionList regions = structuredDocumentRegion.getRegions();
		ITextRegion r = null;
		if(regions.size() == 2) {
			// missing name region
			if(regions.get(0).getType() == XMLRegionContext.XML_TAG_OPEN && regions.get(1).getType() == XMLRegionContext.XML_TAG_CLOSE) {
				String message = ResourceHandler.getString("ReconcileStepForMarkup.3"); //$NON-NLS-1$
				int start = structuredDocumentRegion.getStartOffset();
				int length = structuredDocumentRegion.getLength();
				Position p = new Position(start, length);
				IReconcileAnnotationKey key = createKey(structuredDocumentRegion, getScope());
		
				TemporaryAnnotation annotation = new TemporaryAnnotation(p, SEVERITY_SYNTAX_ERROR, message, key, ProblemIDsXML.EmptyTag);
				results.add(annotation);
			}
		}
	}

	/**
	 * @param annotationModel
	 * @param structuredDocumentRegion
	 */
	private void checkNoNamespaceInPI(IStructuredDocumentRegion structuredDocumentRegion, List results) {
		// navigate to name
		ITextRegionList regions = structuredDocumentRegion.getRegions();
		ITextRegion r = null;
		int errorCount = 0;
		for (int i = 0; i < regions.size() && errorCount < ELEMENT_ERROR_LIMIT; i++) {
			r = regions.get(i);
			if (r.getType() == XMLRegionContext.XML_TAG_NAME) {
				String piText = structuredDocumentRegion.getText(r);
				int index = piText.indexOf(":"); //$NON-NLS-1$
				if (index != -1) {
					String message = ResourceHandler.getString("ReconcileStepForMarkup.4"); //$NON-NLS-1$
					int start = structuredDocumentRegion.getStartOffset(r) + index;
					int length = piText.trim().length() - index;
					Position p = new Position(start, length);
					IReconcileAnnotationKey key = createKey(structuredDocumentRegion, getScope());

					TemporaryAnnotation annotation = new TemporaryAnnotation(p, SEVERITY_SYNTAX_ERROR, message, key, ProblemIDsXML.NamespaceInPI);
					results.add(annotation);
					errorCount++;
				}
			}
		}
	}

	/**
	 * @param annotationModel
	 * @param structuredDocumentRegion
	 */
	private void checkStartingSpaceForPI(IStructuredDocumentRegion structuredDocumentRegion, List results) {
		IStructuredDocumentRegion prev = structuredDocumentRegion.getPrevious();
		if (prev != null) {
			String prevText = prev.getFullText();
			if (prev.getType() == XMLRegionContext.XML_CONTENT && prevText.endsWith(" ")) { //$NON-NLS-1$
				String message = ResourceHandler.getString("ReconcileStepForMarkup.5"); //$NON-NLS-1$
				int start = prev.getStartOffset();
				int length = prev.getLength();
				Position p = new Position(start, length);
				IReconcileAnnotationKey key = createKey(structuredDocumentRegion, getScope());

				TemporaryAnnotation annotation = new TemporaryAnnotation(p, SEVERITY_SYNTAX_ERROR, message, key, ProblemIDsXML.SpacesBeforePI);
				results.add(annotation);
			}
		}
	}

	private void checkForAttributeValue(IStructuredDocumentRegion structuredDocumentRegion, List results) {
		// check for attributes without a value

		// track the attribute/equals/value sequence using a state of 0, 1 ,2
		// representing the name, =, and value, respectively
		int attrState = 0;
		ITextRegionList textRegions = structuredDocumentRegion.getRegions();
		IReconcileAnnotationKey key = createKey(structuredDocumentRegion, getScope());
		int errorCount = 0;
		for (int i = 0; i < textRegions.size() && errorCount < ELEMENT_ERROR_LIMIT; i++) {
			ITextRegion textRegion = textRegions.get(i);
			if (textRegion.getType() == XMLRegionContext.XML_TAG_ATTRIBUTE_NAME || isTagCloseTextRegion(textRegion)) {
				// dangling name and '='
				if (attrState == 2 && i >= 2) {
					// create annotation
					ITextRegion nameRegion = textRegions.get(i - 2);
					MessageFormat messageFormat = new MessageFormat(ResourceHandler.getString("Attribute_{0}_is_missing_a_value")); //$NON-NLS-1$
					Object[] args = {structuredDocumentRegion.getText(nameRegion)};
					String message = messageFormat.format(args);
					int start = structuredDocumentRegion.getStartOffset(nameRegion);
					int end = structuredDocumentRegion.getTextEndOffset(nameRegion);
					Position p = new Position(start, end - start);
					TemporaryAnnotation annotation = new TemporaryAnnotation(p, SEVERITY_ATTR_MISSING_VALUE, message, key, ProblemIDsXML.MissingAttrValue);

					// quick fix info
					ITextRegion equalsRegion = textRegions.get(i - 2 + 1);
					int insertOffset = structuredDocumentRegion.getTextEndOffset(equalsRegion) - end;
					Object[] additionalFixInfo = {structuredDocumentRegion.getText(nameRegion), new Integer(insertOffset)};
					annotation.setAdditionalFixInfo(additionalFixInfo);

					results.add(annotation);
					errorCount++;
				}
				// name but no '=' (XML only)
				else if (attrState == 1 && i >= 1) {
					// create annotation
					ITextRegion previousRegion = textRegions.get(i - 1);
					MessageFormat messageFormat = new MessageFormat(ResourceHandler.getString("Attribute_{0}_has_no_value")); //$NON-NLS-1$
					Object[] args = {structuredDocumentRegion.getText(previousRegion)};
					String message = messageFormat.format(args);
					int start = structuredDocumentRegion.getStartOffset(previousRegion);
					int end = structuredDocumentRegion.getTextEndOffset(previousRegion);
					Position p = new Position(start, end - start);
					TemporaryAnnotation annotation = new TemporaryAnnotation(p, SEVERITY_ATTR_NO_VALUE, message, key, ProblemIDsXML.NoAttrValue);

					// quick fix info
					annotation.setAdditionalFixInfo(structuredDocumentRegion.getText(previousRegion));

					results.add(annotation);
					errorCount++;
				}
				attrState = 1;
			}
			else if (textRegion.getType() == XMLRegionContext.XML_TAG_ATTRIBUTE_EQUALS) {
				attrState = 2;
			}
			else if (textRegion.getType() == XMLRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
				attrState = 0;
			}
		}

	}

	private void checkAttributesInEndTag(IStructuredDocumentRegion structuredDocumentRegion, List results) {
		ITextRegionList textRegions = structuredDocumentRegion.getRegions();
		int errorCount = 0;
		int start = structuredDocumentRegion.getEndOffset();
		int end = structuredDocumentRegion.getEndOffset();
		for (int i = 0; i < textRegions.size() && errorCount < ELEMENT_ERROR_LIMIT; i++) {
			ITextRegion textRegion = textRegions.get(i);
			if (textRegion.getType() == XMLRegionContext.XML_TAG_ATTRIBUTE_NAME || textRegion.getType() == XMLRegionContext.XML_TAG_ATTRIBUTE_EQUALS || textRegion.getType() == XMLRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
				if(start > structuredDocumentRegion.getStartOffset(textRegion)) 
					start = structuredDocumentRegion.getStartOffset(textRegion);
				end = structuredDocumentRegion.getEndOffset(textRegion);
				errorCount++;
			}
		}
		// create one error for all attributes in the end tag
		if(errorCount > 0) {
			Position p = new Position(start, end - start);
			String message = ResourceHandler.getString("End_tag_has_attributes"); //$NON-NLS-1$
			results.add(new TemporaryAnnotation(p, SEVERITY_GENERIC_ILLFORMED_SYNTAX, message, createKey(structuredDocumentRegion, getScope()), ProblemIDsXML.AttrsInEndTag));
		}
	}

	private void checkStartEndTagPairs(IStructuredDocumentRegion sdRegion, List results) {
		// check start/end tag pairs
		XMLNode xmlNode = getXMLNode(sdRegion);
		boolean selfClosed = false;
		String tagName = null;
		int length = 0;

		if (xmlNode.isContainer()) {
			IStructuredDocumentRegion endNode = xmlNode.getEndStructuredDocumentRegion();
			if (endNode == null) {
				// analyze the tag (check self closing)
				ITextRegionList regions = xmlNode.getStartStructuredDocumentRegion().getRegions();
				ITextRegion r = null;
				for (int i = 0; i < regions.size(); i++) {
					r = regions.get(i);
					if (r.getType() == XMLRegionContext.XML_TAG_OPEN || r.getType() == XMLRegionContext.XML_TAG_CLOSE) {
						length++;
					}
					else if (r.getType() == XMLRegionContext.XML_TAG_NAME) {
						tagName = sdRegion.getText(r);
						length += tagName.length();
					}
					else if (r.getType() == XMLRegionContext.XML_EMPTY_TAG_CLOSE) {
						selfClosed = true;
					}
				}

				if (!selfClosed && tagName != null) {
					MessageFormat messageFormat = new MessageFormat(ResourceHandler.getString("Missing_end_tag_{0}")); //$NON-NLS-1$
					Object[] args = {tagName};
					String message = messageFormat.format(args);

					int start = sdRegion.getStart();
					Position p = new Position(start, length);
					TemporaryAnnotation annotation = new TemporaryAnnotation(p, SEVERITY_STRUCTURE, message, createKey(sdRegion, getScope()), ProblemIDsXML.MissingEndTag);

					// quick fix info
					String tagClose = "/>"; //$NON-NLS-1$
					int tagCloseOffset = xmlNode.getFirstStructuredDocumentRegion().getEndOffset();
					if (r != null && r.getType() == XMLRegionContext.XML_TAG_CLOSE) {
						tagClose = "/"; //$NON-NLS-1$
						tagCloseOffset--;
					}
					XMLNode firstChild = (XMLNode) xmlNode.getFirstChild();
					while (firstChild != null && firstChild.getNodeType() == Node.TEXT_NODE) {
						firstChild = (XMLNode) firstChild.getNextSibling();
					}
					int endOffset = xmlNode.getEndOffset();
					int firstChildStartOffset = firstChild == null ? endOffset : firstChild.getStartOffset();
					Object[] additionalFixInfo = {tagName,
						tagClose,
						new Integer(tagCloseOffset),
						new Integer(xmlNode.getFirstStructuredDocumentRegion().getEndOffset()),	// startTagEndOffset
						new Integer(firstChildStartOffset),	// firstChildStartOffset
						new Integer(endOffset)};	// endOffset
					annotation.setAdditionalFixInfo(additionalFixInfo);

					results.add(annotation);
				}
			}

		}
	}

	private XMLNode getXMLNode(IStructuredDocumentRegion sdRegion) {
		XMLModel xModel = (XMLModel) getModelManager().getExistingModelForRead(getDocument());
		XMLNode xmlNode = (XMLNode) xModel.getIndexedRegion(sdRegion.getStart());
		xModel.releaseFromRead();
		return xmlNode;
	}


	/**
	 * Determines whether the IStructuredDocumentRegion is a XML "end tag" since they're not allowed to have
	 * attribute ITextRegions
	 * @param structuredDocumentRegion
	 * @return
	 */
	private boolean isEndTag(IStructuredDocumentRegion structuredDocumentRegion) {
		if (structuredDocumentRegion == null)
			return false;
		return structuredDocumentRegion.getFirstRegion().getType() == XMLRegionContext.XML_END_TAG_OPEN;
	}

	/**
	 * Determines whether the IStructuredDocumentRegion is a XML "start tag" since they need to be
	 * checked for proper XML attribute region sequences
	 * @param structuredDocumentRegion
	 * @return
	 */
	private boolean isStartTag(IStructuredDocumentRegion structuredDocumentRegion) {
		if (structuredDocumentRegion == null)
			return false;
		return structuredDocumentRegion.getFirstRegion().getType() == XMLRegionContext.XML_TAG_OPEN;
	}

	/**
	 * Determines if the IStructuredDocumentRegion is an XML Processing Instruction
	 * @param structuredDocumentRegion
	 * @return
	 */
	private boolean isPI(IStructuredDocumentRegion structuredDocumentRegion) {
		return structuredDocumentRegion.getFirstRegion().getType() == XMLRegionContext.XML_PI_OPEN;
	}

	/**
	 * Determines if the IStructuredDocumentRegion is XML Content
	 * @param structuredDocumentRegion
	 * @return
	 */
	private boolean isXMLContent(IStructuredDocumentRegion structuredDocumentRegion) {
		return structuredDocumentRegion.getFirstRegion().getType() == XMLRegionContext.XML_CONTENT;
	}

	// Because we check the "proper" closing separately from attribute sequencing, we need to know what's
	// an appropriate close.
	private boolean isTagCloseTextRegion(ITextRegion textRegion) {
		return textRegion.getType() == XMLRegionContext.XML_TAG_CLOSE || textRegion.getType() == XMLRegionContext.XML_EMPTY_TAG_CLOSE;
	}

	public int getScope() {
		return IReconcileAnnotationKey.PARTIAL;
	}
}
