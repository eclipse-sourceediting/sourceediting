/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.css.core.internal.document;

import java.util.regex.Pattern;

import org.eclipse.jst.jsp.css.core.internal.parserz.JSPedCSSRegionContexts;
import org.eclipse.wst.css.core.internal.document.CSSModelCreationContext;
import org.eclipse.wst.css.core.internal.document.CSSModelParser;
import org.eclipse.wst.css.core.internal.document.CSSNodeImpl;
import org.eclipse.wst.css.core.internal.document.CSSStructuredDocumentRegionContainer;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSDocument;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSRuleContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocumentRegion;


/**
 * 
 */
class JSPedCSSModelParser extends CSSModelParser {

	public JSPedCSSModelParser(ICSSDocument doc) {
		super(doc);
	}
	/**
	 * 
	 */
	protected CSSNodeImpl insertStructuredDocumentRegion(IStructuredDocumentRegion region) {
		CSSModelCreationContext creationContext = getCreationContext();
		if (creationContext == null || region == null) {
			return null;
		}

		String type = ((BasicStructuredDocumentRegion) region).getType();
		CSSNodeImpl modified = null;

		//ICSSNode target = fCreationContext.getTargetNode();
		
		if (type == JSPedCSSRegionContexts.CSS_JSP_DIRECTIVE){
			
			boolean isInclude = region.getText().indexOf("include") != -1;
			if (isInclude){
				modified = insertUnknownImport(region);
			} else {
				modified = insertUnknownRule(region);					
			}
		}

		// post process
		if (modified != null) {
			if (modified instanceof CSSStructuredDocumentRegionContainer) {
				((CSSStructuredDocumentRegionContainer) modified).propagateRangeStructuredDocumentRegion();
			}
		}

		return modified != null ? modified : super.insertStructuredDocumentRegion(region);
	}

	private CSSNodeImpl insertUnknownImport(IStructuredDocumentRegion region) {
		CSSModelCreationContext creationContext = getCreationContext();
		CSSNodeImpl parent = creationContext.getTargetNode();
		ICSSDocument sheet = parent.getOwnerDocument();
		
		String text = region.getText();
		Pattern pattern = Pattern.compile(" ");
	    String[] strs = pattern.split(text);
	    String hrefValue = null;
	    for (int i=0;i<strs.length;i++) {
	    	String hrefStr = "file=\"";
	      if (strs[i].startsWith(hrefStr)){
				int hrefStr_length = hrefStr.length();
				// minus 1 to avoid quote?
				int hrefValue_length = strs[i].length() - 1;
				if (hrefValue_length > hrefStr_length) {
					hrefValue = strs[i].substring(hrefStr_length, hrefValue_length);
				}
				else {
					/*
					 * ISSUE: this handles cases where, e.g. "file=" has no
					 * subsequent 'value' ... and from code in insertStructuredDocumentRegion
					 * I believe should return null, rather than empty string, but, this may 
					 * need some fine tuning eventually.
					 */
					hrefValue = null;
				}
	    	  break;
	      }
	    }
		
		if (hrefValue == null) {
			return null;
		}

		JSPCSSImportRuleImpl rule = new JSPCSSImportRuleImpl();
		rule.setOwnerDocument(sheet);
		rule.appendChild((CSSNodeImpl)sheet.createMediaList());
		rule.setRangeStructuredDocumentRegion(region, region);

		
		if (!isUpdateContextActive()) {
			rule.setHref(hrefValue);//Attribute(ICSSImportRule.HREF, hrefValue);
		}


		// insert to tree
		if (!isUpdateContextActive() && parent != null) {
			//propagateRangePreInsert(sheet, rule);
			CSSNodeImpl next = creationContext.getNextNode();
			if (next != null) {
				((CSSNodeImpl)sheet).insertBefore(rule, next);
			}
			else {
				((CSSNodeImpl)sheet).appendChild(rule);
			}
		}
		//creationContext.setTargetNode(rule);
		return rule;
	}

	private CSSNodeImpl insertUnknownRule(IStructuredDocumentRegion flatNode) {
		CSSModelCreationContext creationContext = getCreationContext();
		CSSNodeImpl parent = creationContext.getTargetNode();
		if (!isParseFloating() && !(parent instanceof ICSSRuleContainer)) {
			return null;
		}

		JSPCSSNodeImpl rule = new JSPCSSNodeImpl(flatNode.getText());
		rule.setOwnerDocument(parent.getOwnerDocument());

		// setup flat container
		rule.setRangeStructuredDocumentRegion(flatNode, flatNode);


		// insert to tree
		if (!isUpdateContextActive() && parent != null) {
			propagateRangePreInsert(parent, rule);
			CSSNodeImpl next = creationContext.getNextNode();
			if (next != null) {
				parent.insertBefore(rule, next);
			}
			else {
				parent.appendChild(rule);
			}
		}

		//creationContext.setTargetNode(parent.getOwnerDocument());
		// TargetNext is set to null automatically

		return rule;
	}

}
