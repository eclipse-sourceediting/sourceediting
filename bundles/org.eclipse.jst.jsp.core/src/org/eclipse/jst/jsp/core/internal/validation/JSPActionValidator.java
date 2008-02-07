/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.validation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jst.jsp.core.internal.JSPCoreMessages;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.jst.jsp.core.internal.contentmodel.TaglibController;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.TLDCMDocumentManager;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.TaglibTracker;
import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.basic.CMNamedNodeMapImpl;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;

/**
 * Checks for: missing required attributes & undefined attributes in jsp
 * action tags such as jsp directives and jsp custom tags as well as non-empty
 * inline jsp action tags
 */
public class JSPActionValidator extends JSPValidator {
	private int fSeverityMissingRequiredAttribute = IMessage.HIGH_SEVERITY;
	private int fSeverityUnknownAttribute = IMessage.NORMAL_SEVERITY;
	private int fSeverityNonEmptyInlineTag = IMessage.NORMAL_SEVERITY;
	private IValidator fMessageOriginator;
	private HashSet fTaglibPrefixes = new HashSet();

	public JSPActionValidator() {
		this.fMessageOriginator = this;
	}

	public JSPActionValidator(IValidator validator) {
		this.fMessageOriginator = validator;
	}

	private void checkNonEmptyInlineTag(IDOMElement element, CMElementDeclaration cmElementDecl, IReporter reporter, IFile file, IStructuredDocument document) {
		if (cmElementDecl.getContentType() == CMElementDeclaration.EMPTY && element.getChildNodes().getLength() > 0) {
			String msgText = NLS.bind(JSPCoreMessages.JSPActionValidator_0, element.getNodeName());
			LocalizedMessage message = new LocalizedMessage(fSeverityNonEmptyInlineTag, msgText, file);
			int start = element.getStartOffset();
			int length = element.getStartEndOffset() - start;
			int lineNo = document.getLineOfOffset(start);
			message.setLineNo(lineNo);
			message.setOffset(start);
			message.setLength(length);

			reporter.addMessage(fMessageOriginator, message);
		}
	}

	private void checkRequiredAttributes(IDOMElement element, CMNamedNodeMap attrMap, IReporter reporter, IFile file, IStructuredDocument document, IStructuredDocumentRegion documentRegion) {
		Iterator it = attrMap.iterator();
		CMAttributeDeclaration attr = null;
		while (it.hasNext()) {
			attr = (CMAttributeDeclaration) it.next();
			if (attr.getUsage() == CMAttributeDeclaration.REQUIRED) {
				Attr a = element.getAttributeNode(attr.getAttrName());
				if (a == null) {
					String msgText = NLS.bind(JSPCoreMessages.JSPDirectiveValidator_5, attr.getAttrName());
					LocalizedMessage message = new LocalizedMessage(fSeverityMissingRequiredAttribute, msgText, file);
					int start = element.getStartOffset();
					int length = element.getStartEndOffset() - start;
					int lineNo = document.getLineOfOffset(start);
					message.setLineNo(lineNo);
					message.setOffset(start);
					message.setLength(length);

					reporter.addMessage(fMessageOriginator, message);
				}
			}
		}
	}

	private boolean checkUnknownAttributes(IDOMElement element, CMNamedNodeMap cmAttrs, IReporter reporter, IFile file, IStructuredDocument document, IStructuredDocumentRegion documentRegion) {
		boolean foundjspattribute = false;

		NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			Attr a = (Attr) attrs.item(i);
			CMAttributeDeclaration adec = (CMAttributeDeclaration) cmAttrs.getNamedItem(a.getName());
			if (adec == null) {
				// No attr declaration was found. That is, the attr name is
				// undefined.
				// but not regard it as undefined name if it includes JSP
				if (!hasJSPRegion(((IDOMNode) a).getNameRegion())) {
					String msgText = NLS.bind(JSPCoreMessages.JSPDirectiveValidator_6, a.getName());
					LocalizedMessage message = new LocalizedMessage(fSeverityUnknownAttribute, msgText, file);
					int start = ((IDOMAttr) a).getNameRegionStartOffset();
					int length = ((IDOMAttr) a).getNameRegionEndOffset() - start;
					int lineNo = document.getLineOfOffset(start);
					message.setLineNo(lineNo);
					message.setOffset(start);
					message.setLength(length);

					reporter.addMessage(fMessageOriginator, message);
				}
				else {
					foundjspattribute = true;
				}
			}
		}
		return foundjspattribute;
	}

	public void cleanup(IReporter reporter) {
		super.cleanup(reporter);
		fTaglibPrefixes.clear();
	}

	private String getStartTagName(IStructuredDocumentRegion sdr) {
		String name = new String();
		ITextRegionList subRegions = sdr.getRegions();
		if (subRegions.size() > 2) {
			ITextRegion subRegion = subRegions.get(0);
			if (subRegion.getType() == DOMRegionContext.XML_TAG_OPEN) {
				subRegion = subRegions.get(1);
				if (subRegion.getType() == DOMRegionContext.XML_TAG_NAME) {
					name = sdr.getText(subRegion);
				}
			}
		}
		return name;
	}

	private HashSet getTaglibPrefixes(IStructuredDocument document) {
		if (fTaglibPrefixes.isEmpty()) {
			// add all reserved prefixes
			fTaglibPrefixes.add("jsp"); //$NON-NLS-1$
			fTaglibPrefixes.add("jspx"); //$NON-NLS-1$
			fTaglibPrefixes.add("java"); //$NON-NLS-1$
			fTaglibPrefixes.add("javax"); //$NON-NLS-1$ 
			fTaglibPrefixes.add("servlet"); //$NON-NLS-1$ 
			fTaglibPrefixes.add("sun"); //$NON-NLS-1$ 
			fTaglibPrefixes.add("sunw"); //$NON-NLS-1$

			// add all taglib prefixes
			TLDCMDocumentManager manager = TaglibController.getTLDCMDocumentManager(document);
			List trackers = manager.getTaglibTrackers();
			for (Iterator it = trackers.iterator(); it.hasNext();) {
				TaglibTracker tracker = (TaglibTracker) it.next();
				String prefix = tracker.getPrefix();
				fTaglibPrefixes.add(prefix);
			}
		}
		return fTaglibPrefixes;
	}

	private boolean hasJSPRegion(ITextRegion container) {
		if (!(container instanceof ITextRegionContainer))
			return false;
		ITextRegionList regions = ((ITextRegionContainer) container).getRegions();
		if (regions == null)
			return false;
		Iterator e = regions.iterator();
		while (e.hasNext()) {
			ITextRegion region = (ITextRegion) e.next();
			if (region == null)
				continue;
			String regionType = region.getType();
			if (regionType == DOMRegionContext.XML_TAG_OPEN || (isNestedTagName(regionType)))
				return true;
		}
		return false;
	}

	private boolean isNestedTagName(String regionType) {
		boolean result = regionType.equals(DOMJSPRegionContexts.JSP_SCRIPTLET_OPEN) || regionType.equals(DOMJSPRegionContexts.JSP_EXPRESSION_OPEN) || regionType.equals(DOMJSPRegionContexts.JSP_DECLARATION_OPEN) || regionType.equals(DOMJSPRegionContexts.JSP_DIRECTIVE_OPEN);
		return result;
	}

	void performValidation(IFile f, IReporter reporter, IStructuredModel model) {
		fTaglibPrefixes.clear();
		int length = model.getStructuredDocument().getLength();
		performValidation(f, reporter, model, new Region(0, length));
	}

	protected void performValidation(IFile f, IReporter reporter, IStructuredModel model, IRegion validateRegion) {
		IStructuredDocument sDoc = model.getStructuredDocument();

		// iterate all document regions
		IStructuredDocumentRegion region = sDoc.getRegionAtCharacterOffset(validateRegion.getOffset());
		while (region != null && !reporter.isCancelled() && (region.getStartOffset() <= (validateRegion.getOffset() + validateRegion.getLength()))) {
			if (region.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME) {
				// only checking directives
				processDirective(reporter, f, model, region);
				fTaglibPrefixes.clear();
			}
			else if (region.getType() == DOMRegionContext.XML_TAG_NAME) {
				// and jsp tags
				String tagName = getStartTagName(region);
				int colonPosition = tagName.indexOf(':');
				if (colonPosition > -1) {
					// get tag's prefix and check if it's really a jsp action
					// tag
					String prefix = tagName.substring(0, colonPosition);
					if (getTaglibPrefixes(sDoc).contains(prefix))
						processDirective(reporter, f, model, region);
				}
			}
			region = region.getNext();
		}
	}

	private void processDirective(IReporter reporter, IFile file, IStructuredModel model, IStructuredDocumentRegion documentRegion) {
		IndexedRegion ir = model.getIndexedRegion(documentRegion.getStartOffset());
		if (ir instanceof IDOMElement) {
			IDOMElement element = (IDOMElement) ir;
			ModelQuery query = ModelQueryUtil.getModelQuery(model);
			if (query != null) {
				CMElementDeclaration cmElement = query.getCMElementDeclaration(element);
				if (cmElement != null) {
					CMNamedNodeMap cmAttributes = cmElement.getAttributes();
					
					CMNamedNodeMapImpl allAttributes = new CMNamedNodeMapImpl(cmAttributes);
					if (cmElement != null) {
						List nodes = query.getAvailableContent(element, cmElement, ModelQuery.INCLUDE_ATTRIBUTES);
						for (int k = 0; k < nodes.size(); k++) {
							CMNode cmnode = (CMNode) nodes.get(k);
							if (cmnode.getNodeType() == CMNode.ATTRIBUTE_DECLARATION) {
								allAttributes.put(cmnode);
							}
						}
					}
					cmAttributes = allAttributes;

					boolean foundjspattribute = checkUnknownAttributes(element, cmAttributes, reporter, file, model.getStructuredDocument(), documentRegion);
					// required attributes could be hidden in jsp regions in
					// tags, so if jsp regions were detected, do not check for
					// missing required attributes
					if (!foundjspattribute)
						checkRequiredAttributes(element, cmAttributes, reporter, file, model.getStructuredDocument(), documentRegion);
					
					checkNonEmptyInlineTag(element, cmElement, reporter, file, model.getStructuredDocument());
				}
			}
		}
	}

	protected void validateFile(IFile f, IReporter reporter) {
		if (DEBUG) {
			Logger.log(Logger.INFO, getClass().getName() + " validating: " + f); //$NON-NLS-1$
		}

		IStructuredModel sModel = null;
		try {
			sModel = StructuredModelManager.getModelManager().getModelForRead(f);
			if (sModel != null && !reporter.isCancelled()) {
				performValidation(f, reporter, sModel);
			}
		}
		catch (Exception e) {
			Logger.logException(e);
		}
		finally {
			if (sModel != null)
				sModel.releaseFromRead();
		}
	}
}
