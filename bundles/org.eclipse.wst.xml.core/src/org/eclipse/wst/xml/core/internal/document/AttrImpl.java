/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *     Balazs Banfai: Bug 154737 getUserData/setUserData support for Node
 *     https://bugs.eclipse.org/bugs/show_bug.cgi?id=154737
 *     
 *     David Carver (STAR) - bug 296999 - Inefficient use of new String()
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.document;



import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.core.internal.provisional.IXMLCharEntity;
import org.eclipse.wst.xml.core.internal.provisional.IXMLNamespace;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.UserDataHandler;


/**
 * AttrImpl class
 */
public class AttrImpl extends NodeImpl implements IDOMAttr {

	private ITextRegion equalRegion = null;

	private char[] fName = null;
	private ITextRegion nameRegion = null;
	private ElementImpl ownerElement = null;
	private ITextRegion fValueRegion = null;
	private char[] fValueSource = null;
	private char[] fNamespaceURI = null;

	/**
	 * AttrImpl constructor
	 */
	protected AttrImpl() {
		super();
	}

	/**
	 * AttrImpl constructor
	 * 
	 * @param that
	 *            AttrImpl
	 */
	protected AttrImpl(AttrImpl that) {
		super(that);

		if (that != null) {
			this.fName = that.fName;
			String valueSource = that.getValueSource();
			if (valueSource != null)
				this.fValueSource = valueSource.toCharArray();
		}
	}

	/**
	 * cloneNode method
	 * 
	 * @return org.w3c.dom.Node
	 */
	public Node cloneNode(boolean deep) {
		AttrImpl cloned = new AttrImpl(this);
		notifyUserDataHandlers(UserDataHandler.NODE_CLONED, cloned);
		return cloned;
	}

	/**
	 */
	protected CMAttributeDeclaration getDeclaration() {
		ElementImpl element = (ElementImpl) getOwnerElement();
		if (element == null)
			return null;
		CMElementDeclaration elementDecl = element.getDeclaration();
		if (elementDecl == null)
			return null;

		List nodes = ModelQueryUtil.getModelQuery(getOwnerDocument()).getAvailableContent(getOwnerElement(), elementDecl, ModelQuery.INCLUDE_ATTRIBUTES);
		String name = getName();
		for (int k = 0; k < nodes.size(); k++) {
			CMNode cmnode = (CMNode) nodes.get(k);
			if (cmnode.getNodeType() == CMNode.ATTRIBUTE_DECLARATION && name.equals(cmnode.getNodeName())) {
				return (CMAttributeDeclaration) cmnode;
			}
		}
		return null;
	}

	/**
	 * getEndOffset method
	 * 
	 * @return int
	 */
	public int getEndOffset() {
		if (this.ownerElement == null)
			return 0;
		int offset = this.ownerElement.getStartOffset();
		if (this.fValueRegion != null) {
			return (offset + this.fValueRegion.getEnd());
		}
		if (this.equalRegion != null) {
			return (offset + this.equalRegion.getEnd());
		}
		if (this.nameRegion != null) {
			return (offset + this.nameRegion.getEnd());
		}
		return 0;
	}


	public ITextRegion getEqualRegion() {
		return this.equalRegion;
	}

	public String getLocalName() {
		if (this.fName == null)
			return null;
		int index = CharOperation.indexOf(this.fName, ':');
		if (index < 0)
			return new String(this.fName);
		return new String(this.fName, index + 1, this.fName.length - index - 1);
	}

	/**
	 * getName method
	 * 
	 * @return java.lang.String
	 */
	public String getName() {
		if (this.fName == null)
			return NodeImpl.EMPTY_STRING;
		return new String(this.fName);
	}


	public ITextRegion getNameRegion() {
		return this.nameRegion;
	}

	public int getNameRegionEndOffset() {
		if (this.ownerElement == null)
			return 0;
		// assuming the firstStructuredDocumentRegion is the one that contains
		// attributes
		IStructuredDocumentRegion flatNode = this.ownerElement.getFirstStructuredDocumentRegion();
		if (flatNode == null)
			return 0;
		return flatNode.getEndOffset(this.nameRegion);
	}

	public int getNameRegionStartOffset() {
		if (this.ownerElement == null)
			return 0;
		// assuming the firstStructuredDocumentRegion is the one that contains
		// attributes
		IStructuredDocumentRegion flatNode = this.ownerElement.getFirstStructuredDocumentRegion();
		if (flatNode == null)
			return 0;
		return flatNode.getStartOffset(this.nameRegion);
	}

	public String getNameRegionText() {
		if (this.ownerElement == null)
			return null;
		// assuming the firstStructuredDocumentRegion is the one that contains
		// attributes
		IStructuredDocumentRegion flatNode = this.ownerElement.getFirstStructuredDocumentRegion();
		if (flatNode == null)
			return null;
		return flatNode.getText(this.nameRegion);
	}

	public int getNameRegionTextEndOffset() {
		if (this.ownerElement == null)
			return 0;
		// assuming the firstStructuredDocumentRegion is the one that contains
		// attributes
		IStructuredDocumentRegion flatNode = this.ownerElement.getFirstStructuredDocumentRegion();
		if (flatNode == null)
			return 0;
		return flatNode.getTextEndOffset(this.nameRegion);
	}

	public String getNamespaceURI() {
		String nsAttrName = null;
		String prefix = getPrefix();
		if (prefix != null && prefix.length() > 0) {
			if (prefix.equals(IXMLNamespace.XMLNS)) {
				// fixed URI
				return IXMLNamespace.XMLNS_URI;
			}
			else if (prefix.equals(IXMLNamespace.XML)) {
				// fixed URI
				return IXMLNamespace.XML_URI;
			}

			nsAttrName = IXMLNamespace.XMLNS_PREFIX + prefix;
		}
		else {
			String name = getName();
			if (name != null && name.equals(IXMLNamespace.XMLNS)) {
				// fixed URI
				return IXMLNamespace.XMLNS_URI;
			}
			// does not inherit namespace from owner element
			// if (this.ownerElement != null) return
			// this.ownerElement.getNamespaceURI();
			if (this.fNamespaceURI == null)
				return null;
			return new String(this.fNamespaceURI);
		}

		for (Node node = this.ownerElement; node != null; node = node.getParentNode()) {
			if (node.getNodeType() != ELEMENT_NODE)
				break;
			Element element = (Element) node;
			Attr attr = element.getAttributeNode(nsAttrName);
			if (attr != null)
				return attr.getValue();
		}

		if (this.fNamespaceURI == null)
			return null;
		return new String(this.fNamespaceURI);
	}

	/**
	 * getNodeName method
	 * 
	 * @return java.lang.String
	 */
	public String getNodeName() {
		return getName();
	}

	/**
	 * getNodeType method
	 * 
	 * @return short
	 */
	public short getNodeType() {
		return ATTRIBUTE_NODE;
	}

	/**
	 * getNodeValue method
	 * 
	 * @return java.lang.String
	 */
	public String getNodeValue() {
		return getValue();
	}

	/**
	 * getOwnerElement method
	 * 
	 * @return org.w3c.dom.Element
	 */
	public Element getOwnerElement() {
		return this.ownerElement;
	}

	/**
	 */
	public String getPrefix() {
		if (this.fName == null)
			return null;
		int index = CharOperation.indexOf(this.fName, ':');
		if (index <= 0)
			return null;
		// exclude JSP tag in name
		if (this.fName[0] == '<')
			return null;
		return new String(this.fName, 0, index);
	}

	/**
	 * getSpecified method
	 * 
	 * @return boolean
	 */
	public boolean getSpecified() {
		// if there is no underlying document region, 
		// then this attributes value has not really be specified
		// yet in the document, and any returned values, such as 
		// an empty string or a default value are being supplied
		// as per spec, not per what's in the users document.
		return this.fValueRegion != null;
	}

	/**
	 * getStartOffset method
	 * 
	 * @return int
	 */
	public int getStartOffset() {
		if (this.ownerElement == null)
			return 0;
		int offset = this.ownerElement.getStartOffset();
		if (this.nameRegion != null) {
			return (offset + this.nameRegion.getStart());
		}
		if (this.equalRegion != null) {
			return (offset + this.equalRegion.getStart());
		}
		if (this.fValueRegion != null) {
			return (offset + this.fValueRegion.getStart());
		}
		return 0;
	}

	/**
	 * getValue method
	 * 
	 * @return java.lang.String
	 */
	public String getValue() {
		return getValue(getValueSource());
	}

	/**
	 * Returns value for the source
	 */
	private String getValue(String source) {
		if (source == null)
			return NodeImpl.EMPTY_STRING;
		if (source.length() == 0)
			return source;
		StringBuffer buffer = null;
		int offset = 0;
		int length = source.length();
		int ref = source.indexOf('&');
		while (ref >= 0) {
			int end = source.indexOf(';', ref + 1);
			if (end > ref + 1) {
				String name = source.substring(ref + 1, end);
				String value = getCharValue(name);
				if (value != null) {
					if (buffer == null)
						buffer = new StringBuffer(length);
					if (ref > offset)
						buffer.append(source.substring(offset, ref));
					buffer.append(value);
					offset = end + 1;
					ref = end;
				}
			}
			ref = source.indexOf('&', ref + 1);
		}
		if (buffer == null)
			return source;
		if (length > offset)
			buffer.append(source.substring(offset));
		return buffer.toString();
	}

	public ITextRegion getValueRegion() {
		return this.fValueRegion;
	}

	/**
	 * ISSUE: what should behavior be if this.value == null? It's an "error"
	 * to be in that state, but seems to occur relatively easily ... probably
	 * due to threading bugs ... but this just shows its needs to be spec'd.
	 * 
	 */
	public int getValueRegionStartOffset() {
		if (this.ownerElement == null)
			return 0;
		// assuming the firstStructuredDocumentRegion is the one that contains
		// the valueRegion -- should make smarter?
		IStructuredDocumentRegion structuredDocumentRegion = this.ownerElement.getFirstStructuredDocumentRegion();
		if (structuredDocumentRegion == null)
			return 0;
		// ensure we never pass null to getStartOffset.
		if (this.fValueRegion == null) {
			return 0;
		}
		return structuredDocumentRegion.getStartOffset(this.fValueRegion);
	}

	public String getValueRegionText() {
		if (this.ownerElement == null)
			return null;
		// assuming the firstStructuredDocumentRegion is the one that contains
		// attributes
		IStructuredDocumentRegion flatNode = this.ownerElement.getFirstStructuredDocumentRegion();
		if (flatNode == null)
			return null;
		if (this.fValueRegion == null)
			return null;
		return flatNode.getText(this.fValueRegion);
	}

	/**
	 */
	public String getValueSource() {
		if (this.fValueSource != null)
			return new String(this.fValueSource);
		// DW: 4/16/2003 due to change in structuredDocument ... we need a
		// flatnode to
		// get at region values. For now I'll assume this is always the first
		// flatnode .. may need to make smarter later (e.g. to search for
		// the flatnode that this.valueRegion belongs to.
		// DW: 4/30/2003 For some reason, this method is getting called a lot
		// Not sure if its a threading problem, or a fundamental error
		// elsewhere.
		// It needs more investigation, but in the use cases I've seen,
		// doesn't
		// seem to hurt to simply return null in those cases. I saw this null
		// case,
		// when trying go format an XML file.
		if (this.ownerElement == null)
			return null;
		//attribute values will always be in the start region
		IStructuredDocumentRegion ownerRegion = this.ownerElement.getStartStructuredDocumentRegion();
		if (ownerRegion == null)
			return null;
		if (this.fValueRegion != null)
			return StructuredDocumentRegionUtil.getAttrValue(ownerRegion, this.fValueRegion);
		return NodeImpl.EMPTY_STRING;
	}

	private String getValueSource(ElementImpl ownerElement) {
		if (this.fValueSource != null)
			return new String(this.fValueSource);
		// DW: 4/16/2003 due to change in structuredDocument ... we need a
		// flatnode to
		// get at region values. For now I'll assume this is always the first
		// flatnode .. may need to make smarter later (e.g. to search for
		// the flatnode that this.valueRegion belongs to.
		if (this.fValueRegion != null)
			return StructuredDocumentRegionUtil.getAttrValue(ownerElement.getStructuredDocumentRegion(), this.fValueRegion);
		return NodeImpl.EMPTY_STRING;
	}

	/**
	 */
	private String getValueSource(String value) {
		if (value == null)
			return null;
		if (value.length() == 0)
			return value;
		StringBuffer buffer = null;
		int offset = 0;
		int length = value.length();
		int amp = value.indexOf('&');
		while (amp >= 0) {
			if (buffer == null)
				buffer = new StringBuffer(length + 4);
			if (amp > offset)
				buffer.append(value.substring(offset, amp));
			buffer.append(IXMLCharEntity.AMP_REF);
			offset = amp + 1;
			amp = value.indexOf('&', offset);
		}
		if (buffer == null)
			return value;
		if (length > offset)
			buffer.append(value.substring(offset));
		return buffer.toString();
	}

	/**
	 * Check if Attr has JSP in value
	 */
	public boolean hasNestedValue() {
		if (this.fValueRegion == null)
			return false;
		if (!(this.fValueRegion instanceof ITextRegionContainer))
			return false;
		ITextRegionList regions = ((ITextRegionContainer) this.fValueRegion).getRegions();
		if (regions == null)
			return false;
		Iterator e = regions.iterator();
		while (e.hasNext()) {
			ITextRegion region = (ITextRegion) e.next();
			if (region == null)
				continue;
			String regionType = region.getType();
			if (regionType == DOMRegionContext.XML_TAG_OPEN || isNestedLanguageOpening(regionType))
				return true;
		}
		return false;
	}

	/**
	 * Check if Attr has only name but not equal sign nor value
	 */
	public boolean hasNameOnly() {
		return (this.nameRegion != null && this.equalRegion == null && this.fValueRegion == null);
	}

	/**
	 */
	protected final boolean hasPrefix() {
		if (this.fName == null || this.fName.length == 0)
			return false;
		return CharOperation.indexOf(this.fName, ':') > 0 && this.fName[0] != '<';
	}
	
	/**
	 */
	protected final boolean ignoreCase() {
		if (this.ownerElement != null) {
			if (this.ownerElement.ignoreCase()) {
				return !hasPrefix();
			}
		}
		else {
			DocumentImpl document = (DocumentImpl) getOwnerDocument();
			if (document != null && document.ignoreCase()) {
				// even in case insensitive document, if having prefix, it's
				// case sensitive
				return !hasPrefix();
			}
		}
		return false;
	}

	/**
	 */
	public boolean isGlobalAttr() {
		if (hasPrefix())
			return false;
		if (this.ownerElement == null)
			return false;
		return this.ownerElement.isGlobalTag();
	}

	/**
	 */
	public final boolean isXMLAttr() {
		if (this.ownerElement != null) {
			if (!this.ownerElement.isXMLTag()) {
				return hasPrefix();
			}
		}
		else {
			DocumentImpl document = (DocumentImpl) getOwnerDocument();
			if (document != null && !document.isXMLType()) {
				// even in non-XML document, if having prefix, it's XML tag
				return hasPrefix();
			}
		}
		return true;
	}

	/**
	 * matchName method
	 * 
	 * @return boolean
	 * @param name
	 *            java.lang.String
	 */
	protected boolean matchName(String name) {
		if (name == null)
			return (this.fName == null);
		if (this.fName == null)
			return false;
		return CharOperation.equals(this.fName, name.toCharArray(), ignoreCase());
	}

	protected boolean matchName(char[] name) {
		if (name == null)
			return (this.fName == null);
		if (this.fName == null)
			return false;
		return CharOperation.equals(this.fName, name, ignoreCase());
	}


	/**
	 * notifyValueChanged method
	 */
	protected void notifyNameChanged() {
		if (this.ownerElement == null)
			return;
		DocumentImpl document = (DocumentImpl) this.ownerElement.getContainerDocument();
		if (document == null)
			return;
		DOMModelImpl model = (DOMModelImpl) document.getModel();
		if (model == null)
			return;
		model.nameChanged(this);
	}

	/**
	 * notifyValueChanged method
	 */
	protected void notifyValueChanged() {
		if (this.ownerElement == null)
			return;
		DocumentImpl document = (DocumentImpl) this.ownerElement.getContainerDocument();
		if (document == null)
			return;
		DOMModelImpl model = (DOMModelImpl) document.getModel();
		if (model == null)
			return;
		model.valueChanged(this);
	}

	/**
	 * removeRegions method
	 */
	void removeRegions() {
		this.nameRegion = null;
		this.fValueRegion = null;
		this.equalRegion = null;
	}

	/**
	 */
	void resetRegions() {
		this.fValueSource = getValueSource().toCharArray();
		removeRegions();
	}

	/**
	 */
	void resetRegions(ElementImpl ownerElement) {
		this.fValueSource = getValueSource(ownerElement).toCharArray();
		removeRegions();
	}

	void setEqualRegion(ITextRegion equalRegion) {
		this.equalRegion = equalRegion;
	}

	/**
	 * setName method
	 * 
	 * @param name
	 *            java.lang.String
	 */
	protected void setName(String name) {
		String value = null;
		int startOffset = 0;
		if (this.ownerElement != null) {
			value = getValue();
			startOffset = this.ownerElement.getStartOffset();
			this.ownerElement.notify(REMOVE, this, value, null, startOffset);
		}
		this.fName = CharacterStringPool.getCharString(name);
		if (this.ownerElement != null) {
			this.ownerElement.notify(ADD, this, null, value, startOffset);
		}
	}

	void setNameRegion(ITextRegion nameRegion) {
		this.nameRegion = nameRegion;
	}

	protected void setNamespaceURI(String namespaceURI) {
		if (namespaceURI == null)
			this.fNamespaceURI = null;
		else
			this.fNamespaceURI = namespaceURI.toCharArray();
	}

	/**
	 * setNodeValue method
	 * 
	 * @param nodeValue
	 *            java.lang.String
	 */
	public void setNodeValue(String nodeValue) throws DOMException {
		setValue(nodeValue);
	}

	/**
	 * setOwnerElement method
	 * 
	 * @param ownerElement
	 *            org.w3c.dom.Element
	 */
	protected void setOwnerElement(Element ownerElement) {
		this.ownerElement = (ElementImpl) ownerElement;
	}

	/**
	 */
	public void setPrefix(String prefix) throws DOMException {
		if (this.ownerElement != null && !this.ownerElement.isDataEditable()) {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, DOMMessages.NO_MODIFICATION_ALLOWED_ERR);
		}
		int prefixLength = (prefix != null ? prefix.length() : 0);
		String localName = getLocalName();
		if (prefixLength == 0) {
			setName(localName);
			return;
		}
		if (localName == null)
			localName = NodeImpl.EMPTY_STRING;
		int localLength = localName.length();
		StringBuffer buffer = new StringBuffer(prefixLength + 1 + localLength);
		buffer.append(prefix);
		buffer.append(':');
		buffer.append(localName);
		setName(buffer.toString());

		notifyNameChanged();
	}

	/**
	 * setValue method
	 * 
	 * @param value
	 *            java.lang.String
	 */
	public void setValue(String value) {
		// Remember: as we account for "floaters" in
		// future, remember that some are created
		// in the natural process of implementing
		// DOM apis.
		// this "self notification" of about/changed,
		// is added for this case, because it known to
		// be called from properties pages. Should be a
		// added to all DOM Modifiying APIs eventually.
		try {
			getModel().aboutToChangeModel();
			setValueSource(getValueSource(value));
		}
		finally {
			getModel().changedModel();
		}
	}

	void setValueRegion(ITextRegion valueRegion) {
		this.fValueRegion = valueRegion;
		if (valueRegion != null)
			this.fValueSource = null;
	}

	public void setValueSource(String source) {
		if (this.ownerElement != null && !this.ownerElement.isDataEditable()) {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, DOMMessages.NO_MODIFICATION_ALLOWED_ERR);
		}
		this.fValueSource = (source != null) ? source.toCharArray() : null;

		notifyValueChanged();
	}

	/**
	 * Subclasses must override
	 * 
	 * @param regionType
	 * @return
	 */
	protected boolean isNestedLanguageOpening(String regionType) {
		boolean result = false;
		return result;
	}
}
