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
package org.eclipse.wst.html.core.internal.contentmodel;



import java.util.Iterator;

import org.eclipse.wst.html.core.internal.provisional.HTMLCMProperties;
import org.eclipse.wst.xml.core.internal.contentmodel.CMContent;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

/**
 * Base class for all Hed???? classes.
 */
abstract class HTMLElemDeclImpl extends CMContentImpl implements HTMLElementDeclaration, HTMLPropertyDeclaration {

	// DTD
	protected CMNamedNodeMapImpl attributes = null;
	protected String typeDefinitionName = ComplexTypeDefinitionFactory.CTYPE_EMPTY;
	/** Never access this field directly.  Instead, use getComplexTypeDefinition method. */
	private ComplexTypeDefinition typeDefinition = null;
	protected CMGroupImpl inclusion = null;
	protected CMGroupImpl exclusion = null;
	// advanced information
	protected CMNamedNodeMap prohibitedAncestors = null;
	protected int correctionType = CORRECT_NONE;
	protected int formatType = FORMAT_HTML;
	protected int layoutType = LAYOUT_NONE;
	protected int omitType = OMIT_NONE;
	protected boolean keepSpaces = false;
	protected boolean indentChild = false;
	protected ElementCollection elementCollection = null;
	protected AttributeCollection attributeCollection = null;
	private boolean is_obsolete = false;
	protected final static CMNamedNodeMap EMPTY_MAP = new CMNamedNodeMap() {
		public int getLength() {
			return 0;
		}

		public CMNode getNamedItem(String name) {
			return null;
		}

		public CMNode item(int index) {
			return null;
		}

		public Iterator iterator() {
			return new Iterator() {
				public boolean hasNext() {
					return false;
				}

				public Object next() {
					return null;
				}

				public void remove() {
				}
			};
		}
	};

	/**
	 * HTMLElemDeclImpl constructor.
	 * In the HTML DTD, an element declaration has no specification
	 * for its occurrence.  Occurrence is specifed in content model, like
	 * <code>(LI)+</code>.  To avoid confusion (and complexity),
	 * occurrence of an element declaration is always 1 (it means, min = 1 and
	 * max = 1).  Instead, occurrence of CMGroup represents actual occurrence
	 * of the content.
	 * <br>
	 * @param name java.lang.String
	 */
	public HTMLElemDeclImpl(String elementName, ElementCollection collection) {
		super(elementName, 1, 1);
		elementCollection = collection;
		attributeCollection = collection.getAttributeCollection();
	}

	/**
	 */
	protected abstract void createAttributeDeclarations();

	private ComplexTypeDefinition createComplexTypeDefinition() {
		if (typeDefinitionName.equals(ComplexTypeDefinitionFactory.CTYPE_CDATA) || typeDefinitionName.equals(ComplexTypeDefinitionFactory.CTYPE_EMPTY) || typeDefinitionName.equals(ComplexTypeDefinitionFactory.CTYPE_PCDATA))
			return null;

		ComplexTypeDefinitionFactory factory = ComplexTypeDefinitionFactory.getInstance();
		if (factory == null)
			return null; // fatal error.

		ComplexTypeDefinition def = factory.createTypeDefinition(typeDefinitionName, elementCollection);
		return def;
	}

	/**
	 * Get an attribute declaration.
	 */
	public HTMLAttributeDeclaration getAttributeDeclaration(String attrName) {
		if (attributes == null) {
			createAttributeDeclarations();
			if (attributes == null)
				return null; // fail to create
		}

		CMNode cmnode = attributes.getNamedItem(attrName);
		if (cmnode == null) {
			return null;
		}
		else {
			return (HTMLAttributeDeclaration) cmnode; // already exists.
		}
	}

	/**
	 * @see org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration
	 */
	public CMNamedNodeMap getAttributes() {
		if (attributes == null)
			createAttributeDeclarations(); // lazy eval.
		return attributes;
	}

	/**
	 * Get an instance of complex type definition.
	 */
	private ComplexTypeDefinition getComplexTypeDefinition() {
		if (typeDefinition == null)
			typeDefinition = createComplexTypeDefinition();
		return typeDefinition;
	}

	/**
	 * Content.<br>
	 * Element declarations which type is EMPTY or CDATA (maybe PCDATA)
	 * <strong>MUST</strong> override this method and always return null.
	 * This default implementation always tries to create a complex type definition
	 * instance and access to it.
	 * <br>
	 * @see org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration
	 */
	public CMContent getContent() {
		ComplexTypeDefinition def = getComplexTypeDefinition(); // lazy eval.
		return (def != null) ? def.getContent() : null;
	}

	/**
	 * Content type.<br>
	 * Element declarations which type is EMPTY or CDATA (maybe PCDATA)
	 * <strong>MUST</strong> override this method and return an appropriate type.
	 * This default implementation always tries to create a complex type definition
	 * instance and access to it.
	 * <br>
	 * @see org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration
	 */
	public int getContentType() {
		ComplexTypeDefinition def = getComplexTypeDefinition(); // lazy eval.
		return (def != null) ? def.getContentType() : CMElementDeclaration.CDATA;
	}

	/**
	 * @see HTMLElementDeclaration#getCorrectionType
	 */
	public int getCorrectionType() {
		return correctionType;
	}

	/**
	 * HTML element doesn't have any data type.  So, this method always
	 * returns <code>null</code>.<br>
	 * @see org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration
	 */
	public CMDataType getDataType() {
		return null;
	}

	/**
	 * @see org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration
	 */
	public String getElementName() {
		return getNodeName();
	}

	/**
	 * Exclusion.
	 * Almost elements don't have a exclusion.
	 * Only classes those have exclusion should override this method.
	 */
	public CMContent getExclusion() {
		return null;
	}

	/**
	 * Default format type is <code>FORMAT_HTML</code>.<br>
	 */
	public int getFormatType() {
		return formatType;
	}

	/**
	 * Inclusion.
	 * Almost elements don't have a inclusion.
	 * Only classes those have inclusion should override this method.
	 */
	public CMContent getInclusion() {
		return null;
	}

	/**
	 */
	public int getLayoutType() {
		return layoutType;
	}

	/**
	 * Line break hint is strongly related to layout type.
	 * Indeed, in the C++DOM, it is determined from layout type only.
	 * So, this implementation, as the default implementation for all declarations,
	 * also determines from layout type only.<br>
	 * @return int
	 */
	public int getLineBreakHint() {
		switch (getLayoutType()) {
			case HTMLElementDeclaration.LAYOUT_BLOCK :
				return HTMLElementDeclaration.BREAK_BEFORE_START_AND_AFTER_END;
			case HTMLElementDeclaration.LAYOUT_BREAK :
				return HTMLElementDeclaration.BREAK_AFTER_START;
			case HTMLElementDeclaration.LAYOUT_HIDDEN :
				return HTMLElementDeclaration.BREAK_BEFORE_START_AND_AFTER_END;
			default :
				return HTMLElementDeclaration.BREAK_NONE;
		}
	}

	/**
	 * No HTML element has local elements.  So, this method always
	 * returns an empty map.
	 * @see org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration
	 */
	public CMNamedNodeMap getLocalElements() {
		return EMPTY_MAP;
	}

	/**
	 * @see org.eclipse.wst.xml.core.internal.contentmodel.CMNode
	 */
	public int getNodeType() {
		return CMNode.ELEMENT_DECLARATION;
	}

	/**
	 */
	public int getOmitType() {
		return omitType;
	}

	/**
	 */
	public CMNamedNodeMap getProhibitedAncestors() {
		return EMPTY_MAP;
	}

	/**
	 */
	public boolean supports(String propertyName) {
		if (propertyName.equals(HTMLCMProperties.SHOULD_IGNORE_CASE)) {
			return true;
		}
		else if (propertyName.equals(HTMLCMProperties.CONTENT_HINT)) {
			ComplexTypeDefinition def = getComplexTypeDefinition();
			return (def != null);
		}
		if (propertyName.equals(HTMLCMProperties.IS_OBSOLETE)) {
			return is_obsolete;
		}
		else {
			PropertyProvider pp = PropertyProviderFactory.getProvider(propertyName);
			if (pp == null)
				return false;
			return pp.supports(this);
		}

	}

	/**
	 */
	public Object getProperty(String propertyName) {
		if (propertyName.equals(HTMLCMProperties.SHOULD_IGNORE_CASE)) {
			return new Boolean(true);
		}
		else if (propertyName.equals(HTMLCMProperties.CONTENT_HINT)) {
			ComplexTypeDefinition def = getComplexTypeDefinition();
			return (def != null) ? def.getPrimaryCandidate() : null;
		}
		else if (propertyName.equals(HTMLCMProperties.IS_OBSOLETE)){
			return new Boolean(is_obsolete);
		}
		else {
			PropertyProvider pp = PropertyProviderFactory.getProvider(propertyName);
			if (pp == null)
				return null;
			return pp.get(this);
		}
	}

	/**
	 * Return element names which terminates this element.<br>
	 * @return java.util.Iterator
	 */
	protected Iterator getTerminators() {
		return null;
	}

	/**
	 * return true when the element is a JSP element.
	 */
	public boolean isJSP() {
		return false;
	}

	/**
	 * In some elements, such as APPLET, a source generator should indent child
	 * elements that their parents.  That is, a source generator should generate
	 * source  of APPLET and PARAMS like this:
	 * <PRE>
	 *   &lt;APPLET ...&gt;
	 *     &lt;PARAM ... &gt;
	 *     &lt;PARAM ... &gt;
	 *   &lt;/APPLET&gt;
	 * <PRE>
	 * @return boolean
	 */
	public boolean shouldIndentChildSource() {
		return indentChild;
	}

	/**
	 * Most of elements can compact spaces in their child text nodes.
	 * Some special elements should keep them in their source.
	 * @return boolean
	 */
	public boolean shouldKeepSpaces() {
		return keepSpaces;
	}

	/**
	 * @return boolean
	 */
	public boolean shouldTerminateAt(HTMLElementDeclaration nextElement) {
		Iterator i = getTerminators();
		if (i == null)
			return false;
		String nextName = nextElement.getElementName();
		while (i.hasNext()) {
			if (nextName.equals(i.next()))
				return true;
		}
		return false;
	}
	
	public void obsolete(boolean is_obsolete){
		this.is_obsolete = is_obsolete;
	}
}
