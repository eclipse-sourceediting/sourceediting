/*******************************************************************************
 * Copyright (c) 2004, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.validate;

import java.util.List;
import java.util.Locale;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class HTMLAttributeValidator extends PrimeValidator {

	private static final int REGION_NAME = 1;
	private static final int REGION_VALUE = 2;
	// <<D210422
	private static final char SINGLE_QUOTE = '\'';
	private static final char DOUBLE_QUOTE = '\"';

	// HTML(5) data attributes
	private static final String ATTR_NAME_DATA = "data-"; //$NON-NLS-1$
	private static final int ATTR_NAME_DATA_LENGTH = ATTR_NAME_DATA.length();
	
	//WHATWG x-vendor-feature attributes
	private static final String ATTR_NAME_USER_AGENT_FEATURE = "x-"; //$NON-NLS-1$
	private static final int ATTR_NAME_USER_AGENT_FEATURE_LENGTH = ATTR_NAME_USER_AGENT_FEATURE.length();

	// D210422
	/**
	 * HTMLAttributeValidator constructor comment.
	 */
	public HTMLAttributeValidator() {
		super();
	}

	/**
	 */
	private Segment getErrorSegment(IDOMNode errorNode, int regionType) {
		ITextRegion rgn = null;
		switch (regionType) {
			case REGION_NAME :
				rgn = errorNode.getNameRegion();
				break;
			case REGION_VALUE :
				rgn = errorNode.getValueRegion();
				break;
			default :
				// nothing to do.
				break;
		}
		if (rgn != null) {
			if (errorNode instanceof IDOMAttr) {
				IDOMElement ownerElement = (IDOMElement) ((IDOMAttr) errorNode).getOwnerElement();
				if (ownerElement != null) {
					//if editor closed during validation this could be null
					IStructuredDocumentRegion firstRegion = ownerElement.getFirstStructuredDocumentRegion();
					if(firstRegion != null) {
						int regionStartOffset = firstRegion.getStartOffset(rgn);
						int regionLength = rgn.getTextLength();
						return new Segment(regionStartOffset, regionLength);
					}
				}
			}
		}
		return new Segment(errorNode.getStartOffset(), 1);
	}

	/**
	 * Allowing the INodeAdapter to compare itself against the type allows it
	 * to return true in more than one case.
	 */
	public boolean isAdapterForType(Object type) {
		return ((type == HTMLAttributeValidator.class) || super.isAdapterForType(type));
	}

	/**
	 */
	public void validate(IndexedRegion node) {
		Element target = (Element) node;
		if (CMUtil.isForeign(target))
			return;
		CMElementDeclaration edec = CMUtil.getDeclaration(target);
		if (edec == null)
			return;
		CMNamedNodeMap declarations = edec.getAttributes();

		List modelQueryNodes = null;
		NamedNodeMap attrs = target.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			int rgnType = REGION_NAME;
			int state = ErrorState.NONE_ERROR;
			Attr a = (Attr) attrs.item(i);
			// D203637; If the target attr has prefix, the validator should
			// not
			// warn about it. That is, just ignore. It is able to check
			// whether
			// an attr has prefix or not by calling XMLAttr#isGlobalAttr().
			// When a attr has prefix (not global), it returns false.
			boolean isXMLAttr = a instanceof IDOMAttr;
			if (isXMLAttr) {
				IDOMAttr xmlattr = (IDOMAttr) a;
				if (!xmlattr.isGlobalAttr())
					continue; // skip futher validation and begin next loop.
			}

			CMAttributeDeclaration adec = (CMAttributeDeclaration) declarations.getNamedItem(a.getName());
			final String attrName = a.getName().toLowerCase(Locale.US);
			/* Check the modelquery if nothing is declared by the element declaration */
			if (adec == null) {
				if (modelQueryNodes == null)
					modelQueryNodes = ModelQueryUtil.getModelQuery(target.getOwnerDocument()).getAvailableContent((Element) node, edec, ModelQuery.INCLUDE_ATTRIBUTES);
				
				
				for (int k = 0; k < modelQueryNodes.size(); k++) {
					CMNode cmnode = (CMNode) modelQueryNodes.get(k);
					if (cmnode.getNodeType() == CMNode.ATTRIBUTE_DECLARATION && cmnode.getNodeName().toLowerCase(Locale.US).equals(attrName)) {
						adec = (CMAttributeDeclaration) cmnode;
						break;
					}
				}
			}
			
			if (adec == null) {
				if ((attrName.startsWith(ATTR_NAME_DATA) && attrName.length() > ATTR_NAME_DATA_LENGTH) || (attrName.startsWith(ATTR_NAME_USER_AGENT_FEATURE) && attrName.length() > ATTR_NAME_USER_AGENT_FEATURE_LENGTH))
					continue;
				// No attr declaration was found. That is, the attr name is
				// undefined.
				// but not regard it as undefined name if it includes nested
				// region
				if (!hasNestedRegion(((IDOMNode) a).getNameRegion())) {
					rgnType = REGION_NAME;
					state = ErrorState.UNDEFINED_NAME_ERROR;
				}
			} else {
				// The attr declaration was found.
				// At 1st, the name should be checked.
				if (CMUtil.isObsolete(adec)){
					state = ErrorState.OBSOLETE_ATTR_NAME_ERROR;
				}
				if (CMUtil.isHTML(edec) && (!CMUtil.isXHTML(edec))) {
					// If the target element is pure HTML (not XHTML), some
					// attributes
					// might be written in boolean format. It should be check
					// specifically.
					if (CMUtil.isBooleanAttr(adec) && ((IDOMAttr) a).hasNameOnly())
						continue; // OK, keep going. No more check is needed
					// against this attr.
				} else {
					// If the target is other than pure HTML (JSP or XHTML),
					// the name
					// must be checked exactly (ie in case sensitive way).
					String actual = a.getName();
					String desired = adec.getAttrName();
					if (!actual.equals(desired)) { // case mismatch
						rgnType = REGION_NAME;
						state = ErrorState.MISMATCHED_ERROR;
					}
				}
				// Then, the value must be checked.
				if (state == ErrorState.NONE_ERROR) { // Need more check.
					// Now, the value should be checked, if the type is ENUM.
					CMDataType attrType = adec.getAttrType();
					if (a instanceof IDOMAttr) {
						final ITextRegion region = ((IDOMAttr) a).getEqualRegion();
						if (region == null) {
							rgnType = REGION_NAME;
							state = ErrorState.MISSING_ATTR_VALUE_EQUALS_ERROR;
						}
					}
					String actualValue = a.getValue();
					if (attrType.getImpliedValueKind() == CMDataType.IMPLIED_VALUE_FIXED) {
						// Check FIXED value.
						String validValue = attrType.getImpliedValue();
						if (!actualValue.equals(validValue)) {
							rgnType = REGION_VALUE;
							state = ErrorState.UNDEFINED_VALUE_ERROR;
						}
					}
					else if (CMDataType.URI.equals(attrType.getDataTypeName())) {
						// TODO: URI validation?
						if (false && actualValue.indexOf('#') < 0 && actualValue.indexOf(":/") == -1 && CMUtil.isHTML(edec)) { //$NON-NLS-1$ //$NON-NLS-2$
							IStructuredDocumentRegion start = ((IDOMNode) node).getStartStructuredDocumentRegion();
							if (start != null && start.getFirstRegion().getTextLength() == 1) {
								IPath basePath = new Path(((IDOMNode) node).getModel().getBaseLocation());
								if (basePath.segmentCount() > 1) {
									IPath path = ModuleCoreSupport.resolve(basePath, actualValue);
									IResource found = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
									if (found == null || !found.isAccessible()) {
										rgnType = REGION_VALUE;
										state = ErrorState.RESOURCE_NOT_FOUND;
									}
								}
							}
						}
					}
					else if (CMDataType.ENUM.equals(attrType.getDataTypeName())) {
						/*
						 * Check current value is valid among a known list.
						 * There may be enumerated values provided even when
						 * the datatype is not ENUM, but we'll only validate
						 * against that list if the type matches.
						 */
						String[] enumeratedValues = attrType.getEnumeratedValues();
						// several candidates are found.
						boolean found = false;
						for (int j = 0; j < enumeratedValues.length; j++) {
							// At 1st, compare ignoring case.
							if (actualValue.equalsIgnoreCase(enumeratedValues[j])) {
								found = true;
								if (CMUtil.isCaseSensitive(edec) && (!actualValue.equals(enumeratedValues[j]))) {
									rgnType = REGION_VALUE;
									state = ErrorState.MISMATCHED_VALUE_ERROR;
								}
								break; // exit the loop.
							}
						}
						if (!found) {
							// retrieve and check extended values (retrieval can call extensions, which may take longer)
							String[] modelQueryExtensionValues = ModelQueryUtil.getModelQuery(target.getOwnerDocument()).getPossibleDataTypeValues((Element) node, adec);
							// copied loop from above
							for (int j = 0; j < modelQueryExtensionValues.length; j++) {
								// At 1st, compare ignoring case.
								if (actualValue.equalsIgnoreCase(modelQueryExtensionValues[j])) {
									found = true;
									if (CMUtil.isCaseSensitive(edec) && (!actualValue.equals(modelQueryExtensionValues[j]))) {
										rgnType = REGION_VALUE;
										state = ErrorState.MISMATCHED_VALUE_ERROR;
									}
									break; // exit the loop.
								}
							}
							// No candidate was found. That is,
							// actualValue is invalid.
							// but not regard it as undefined value if it
							// includes nested region.
							if (!hasNestedRegion(((IDOMNode) a).getValueRegion())) {
								rgnType = REGION_VALUE;
								state = ErrorState.UNDEFINED_VALUE_ERROR;
							}
						}
					}
				}
				// <<D210422
				if (state == ErrorState.NONE_ERROR) { // Need more check.
					if (isXMLAttr) {
						String source = ((IDOMAttr) a).getValueRegionText();
						if (source != null) {
							char firstChar = source.charAt(0);
							char lastChar = source.charAt(source.length() - 1);
							if (isQuote(firstChar) || isQuote(lastChar)) {
								if (lastChar != firstChar) {
									rgnType = REGION_VALUE;
									state = ErrorState.UNCLOSED_ATTR_VALUE;
								}
							}
						}
					}
				}
				// D210422
			}
			if (state != ErrorState.NONE_ERROR) {
				Segment seg = getErrorSegment((IDOMNode) a, rgnType);
				if (seg != null)
					reporter.report(new ErrorInfoImpl(state, seg, a));
			}
		}
	}

	/**
	 * True if container has nested regions, meaning container is probably too
	 * complicated (like JSP regions) to validate with this validator.
	 */
	private boolean hasNestedRegion(ITextRegion container) {
		if (!(container instanceof ITextRegionContainer))
			return false;
		ITextRegionList regions = ((ITextRegionContainer) container).getRegions();
		if (regions == null)
			return false;
		// BUG207194: return true by default as long as container is an
		// ITextRegionContainer with at least 1 region
		return true;
	}

	// <<D214022
	private boolean isQuote(char c) {
		return (c == SINGLE_QUOTE) || (c == DOUBLE_QUOTE);
	}
	// D210422
}
