/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.document;



import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.css.core.internal.provisional.adapters.IStyleSelectorAdapter;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSelector;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSelectorCombinator;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSelectorItem;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSimpleSelector;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xml.core.internal.provisional.NameValidator;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ibm.icu.util.StringTokenizer;


/**
 * 
 */
class CSSSelector implements ICSSSelector {

	private int fSpecificity = -1;
	private String fCachedString = null;
	private List fTokens = null;
	private List fItems = null;
	private List fParseErrors = null;
	private List fNameErrors = null;

	CSSSelector(List tokens) {
		fTokens = new ArrayList(tokens);
	}

	/**
	 * @return boolean
	 * @param obj
	 *            java.lang.Object
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || this.getClass() != obj.getClass())
			return false;

		CSSSelector foreign = (CSSSelector) obj;

		// if (fSpecificity != foreign.fSpecificity) return false;

		if (getLength() != foreign.getLength())
			return false;

		for (int i = 0; i < getLength(); i++) {
			if (!getItem(i).equals(foreign.getItem(i)))
				return false;
		}

		return true;
	}

	public ICSSSelectorItem getItem(int index) {
		if (fItems == null) {
			fItems = parseSelector(fTokens);
		}
		if (fItems != null && 0 <= index && index < fItems.size()) {
			return (ICSSSelectorItem) fItems.get(index);
		}
		else {
			return null;
		}
	}

	/**
	 * @return java.util.Iterator
	 */
	public Iterator getIterator() {
		if (fItems == null) {
			fItems = parseSelector(fTokens);
		}
		return (fItems != null) ? fItems.iterator() : null;
	}

	/**
	 * @return int
	 */
	public int getLength() {
		if (fItems == null) {
			fItems = parseSelector(fTokens);
		}
		return (fItems != null) ? fItems.size() : 0;
	}

	/**
	 * @return org.w3c.dom.Element
	 */
	private Element getParentElement(Element element) {
		Node node = element.getParentNode();
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			return (Element) node;
		}
		return null;

	}

	/**
	 * @return org.w3c.dom.Element
	 */
	private Element getPreviousElement(Element element) {
		Element p = null;
		for (Node node = element.getPreviousSibling(); node != null; node = node.getPreviousSibling()) {
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				p = (Element) node;
				break;
			}
		}
		return p;
	}

	/**
	 * Calculate a selector's specificity a = the number of ID attributes in
	 * the selector b = the number of other attributes and pseudo-classes in
	 * the selector c = the number of element names in the selector (ignore
	 * pseudo-elements) Concatenating the three numbers a-b-c (in a number
	 * system with a large base) gives the specificity.
	 * 
	 * @return int
	 */
	public int getSpecificity() {
		if (fSpecificity < 0) {
			int nIDs = 0;
			int nAttributes = 0;
			int nElements = 0;
			Iterator i = getIterator();
			while (i.hasNext()) {
				ICSSSelectorItem item = (ICSSSelectorItem) i.next();
				if (item instanceof CSSSimpleSelector) {
					CSSSimpleSelector selector = (CSSSimpleSelector) item;
					nIDs += selector.getNumOfIDs();
					nAttributes += selector.getNumOfAttributes();
					nAttributes += selector.getNumOfClasses();
					nAttributes += selector.getNumOfPseudoNames();
					if (!selector.isUniversal()) {
						nElements++;
					}
				}
			}
			fSpecificity = nIDs * 10000 + nAttributes * 100 + nElements;
		}

		return fSpecificity;
	}

	/**
	 * @return java.lang.String
	 */
	public String getString() {
		if (fCachedString == null) {
			StringBuffer buf = new StringBuffer();
			Iterator i = getIterator();
			while (i.hasNext()) {
				ICSSSelectorItem item = (ICSSSelectorItem) i.next();
				if (item instanceof CSSSelectorCombinator) {
					// If item is DESCENDANT combinator, it is just single
					// space.
					// Then, you dont have to append string..
					if (((CSSSelectorCombinator) item).getCombinatorType() != ICSSSelectorCombinator.DESCENDANT) {
						buf.append(" ");//$NON-NLS-1$
						buf.append(item.getString());
					}
					buf.append(" ");//$NON-NLS-1$
				}
				else {
					buf.append(item.getString());
				}
			}
			fCachedString = buf.toString();
		}

		return fCachedString;
	}

	/**
	 * @return boolean
	 * @param element
	 *            org.w3c.dom.Element
	 */
	public boolean match(org.w3c.dom.Element element, java.lang.String pseudoName) {
		Element target = element;
		char combinatorType = ICSSSelectorCombinator.UNKNOWN;
		Element chunkStartElement = null; // for CHILD and ADJACENT
											// combinator
		int chunkStartItem = -1; // for CHILD and ADJACENT combinator
		int numItems = getLength();
		for (int iItem = numItems - 1; iItem >= 0; iItem--) {
			// Check Selector Items
			ICSSSelectorItem item = getItem(iItem);
			if (item instanceof ICSSSimpleSelector) {
				// Simple Selector
				if (target == null)
					return false;
				if (!matchExactly((ICSSSimpleSelector) item, target, pseudoName)) {
					switch (combinatorType) {
						case ICSSSelectorCombinator.DESCENDANT :
							do {
								target = getParentElement(target);
								if (target == null)
									return false;
							}
							while (!matchExactly((ICSSSimpleSelector) item, target, pseudoName));
							break;
						case ICSSSelectorCombinator.CHILD :
						case ICSSSelectorCombinator.ADJACENT :
							if (chunkStartElement != null && chunkStartElement != element) {
								// previous conbinator must be DESCENDENT.
								// goto parent
								target = getParentElement(chunkStartElement);
								iItem = chunkStartItem + 1;
								chunkStartElement = null;
								chunkStartItem = -1;
								break;
							}
						default :
							// other combinators are not supported yet.
							return false;
					}
				}
			}
			else if (item instanceof ICSSSelectorCombinator) {
				// Combinator ( "+", ">", " ", ...)
				if (iItem == numItems - 1)
					return false; // last item is combinator

				ICSSSelectorCombinator sc = (ICSSSelectorCombinator) item;
				combinatorType = sc.getCombinatorType();
				switch (combinatorType) {
					case ICSSSelectorCombinator.DESCENDANT :
						target = getParentElement(target);
						break;
					case ICSSSelectorCombinator.CHILD :
					case ICSSSelectorCombinator.ADJACENT :
						if (chunkStartElement == null) {
							chunkStartElement = target;
							chunkStartItem = iItem + 1; // safe because this
														// is not a last item.
						}
						if (combinatorType == ICSSSelectorCombinator.CHILD) {
							target = getParentElement(target);
						}
						else {
							target = getPreviousElement(target);
						}
						break;
				}
			}
			else {
				// what is this item ???
				return false;
			}
		}
		// OK this selector maches the element.
		return true;

	}

	/**
	 * @return boolean
	 */
	private boolean matchExactly(ICSSSimpleSelector selector, Element element, String pseudoName) {
		IStyleSelectorAdapter adapter = (IStyleSelectorAdapter) ((INodeNotifier) element).getAdapterFor(IStyleSelectorAdapter.class);
		if (adapter != null) {
			return adapter.match(selector, element, pseudoName);
		}

		if (element == null)
			return false;
		int i;
		String key;

		// TODO: PseudoName

		// check tag name
		if (!selector.isUniversal() && !element.getNodeName().equals(selector.getName()))
			return false;

		// check id
		i = selector.getNumOfIDs();
		if (i > 0) {
			if (i > 1)
				return false;
			if (!element.hasAttribute("id") || (key = element.getAttribute("id")).length() == 0)//$NON-NLS-1$ //$NON-NLS-2$
				return false;
			if (!selector.getID(0).equals(key))
				return false;
		}

		// check class
		i = selector.getNumOfClasses();
		if (i > 0) {
			if (!element.hasAttribute("class") || (key = element.getAttribute("class")).length() == 0) //$NON-NLS-1$  //$NON-NLS-2$
				return false;
			StringTokenizer tokenizer = new StringTokenizer(key);
			for (i = i - 1; i >= 0; i--) {
				boolean ok = false;
				while (tokenizer.hasMoreTokens()) {
					if (selector.getClass(i).equals(tokenizer.nextToken())) {
						ok = true;
						break;
					}
				}
				if (!ok)
					return false;
			}
		}

		// check attributes
		for (i = selector.getNumOfAttributes() - 1; i >= 0; i--) {
			StringTokenizer tokenizer = new StringTokenizer(selector.getAttribute(i), "=~| \t\r\n\f");//$NON-NLS-1$
			int countTokens = tokenizer.countTokens();
			if (countTokens > 0) {
				String attrName = tokenizer.nextToken();
				String attrValue = null;
				if (!element.hasAttribute(attrName) || (attrValue = element.getAttribute(attrName)).length() == 0)
					return false;
				if (countTokens > 1) {
					String token = tokenizer.nextToken("= \t\r\n\f");//$NON-NLS-1$
					StringTokenizer attrValueTokenizer = null;
					if (token.equals("~")) {//$NON-NLS-1$
						attrValueTokenizer = new StringTokenizer(attrValue);
					}
					else if (token.equals("|")) {//$NON-NLS-1$
						attrValueTokenizer = new StringTokenizer(attrValue, "-");//$NON-NLS-1$
					}
					if (attrValueTokenizer != null) {
						if (tokenizer.hasMoreTokens()) {
							token = tokenizer.nextToken();
							boolean ok = false;
							while (attrValueTokenizer.hasMoreTokens()) {
								if (token.equals(attrValueTokenizer.nextToken())) {
									ok = true;
									break;
								}
							}
							if (!ok)
								return false;
						}
					}
					else {
						if (!attrValue.equals(token))
							return false;
					}
				}
			}
		}

		return true;
	}

	private List parseSelector(List tokens) {
		CSSSelectorParser parser = new CSSSelectorParser(tokens);
		List selector = parser.getSelector();
		Iterator i = parser.getErrors();
		fParseErrors = new ArrayList();
		while (i.hasNext()) {
			fParseErrors.add(i.next());
		}
		return selector;
	}

	public Iterator getErrors() {
		if (fNameErrors == null) {
			fNameErrors = new ArrayList();
			Iterator iItem = getIterator();
			while (iItem.hasNext()) {
				ICSSSelectorItem item = (ICSSSelectorItem) iItem.next();
				if (item instanceof ICSSSimpleSelector) {
					if (!((ICSSSimpleSelector) item).isUniversal()) {
						String name = ((ICSSSimpleSelector) item).getName();
						if (!NameValidator.isValid(name)) {
							fNameErrors.add(item);
						}
					}
				}
			}
		}
		List errors = new ArrayList(fParseErrors);
		errors.addAll(fNameErrors);
		return errors.iterator();
	}

	public String toString() {
		return getString();
	}

	/**
	 * @see ICSSSelector#getErrorCount()
	 */
	public int getErrorCount() {
		int nErrors = 0;
		Iterator i = getErrors();
		while (i.hasNext()) {
			nErrors++;
			i.next();
		}
		return nErrors;
	}
}
