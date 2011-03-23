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
 *     David Carver (STAR) - bug 296999 - Inefficient use of new String()
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.document;

import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMText;
import org.eclipse.wst.xml.core.internal.provisional.document.ISourceGenerator;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;

/**
 * TextImpl class
 */
public class TextImpl extends CharacterDataImpl implements IDOMText {

	/**
	 */
	private class StringPair {
		private String fFirst = null;
		private String fSecond = null;

		StringPair(String first, String second) {
			this.fFirst = first;
			this.fSecond = second;
		}

		String getFirst() {
			return this.fFirst;
		}

		String getSecond() {
			return this.fSecond;
		}
	}

	private String fSource = null;

	/**
	 * TextImpl constructor
	 */
	protected TextImpl() {
		super();
	}

	/**
	 * TextImpl constructor
	 * 
	 * @param that
	 *            TextImpl
	 */
	protected TextImpl(TextImpl that) {
		super(that);

		if (that != null) {
			this.fSource = that.getSource();
		}
	}

	/**
	 * appendData method
	 * 
	 * @param arg
	 *            java.lang.String
	 */
	public void appendData(String arg) throws DOMException {
		if (arg == null || arg.length() == 0)
			return;

		if (!isDataEditable()) {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, DOMMessages.NO_MODIFICATION_ALLOWED_ERR);
		}

		String newSource = getSource(arg);
		if (newSource == null)
			return;
		String source = getSource();
		if (source != null)
			setTextSource(source + newSource);
		else
			setTextSource(newSource);
	}

	/**
	 */
	IStructuredDocumentRegion appendStructuredDocumentRegion(IStructuredDocumentRegion newStructuredDocumentRegion) {
		if (newStructuredDocumentRegion == null)
			return null;

		IStructuredDocumentRegion flatNode = getStructuredDocumentRegion();
		if (flatNode == null) {
			setStructuredDocumentRegion(newStructuredDocumentRegion);
			return newStructuredDocumentRegion;
		}

		if (flatNode instanceof StructuredDocumentRegionContainer) {
			StructuredDocumentRegionContainer container = (StructuredDocumentRegionContainer) flatNode;
			container.appendStructuredDocumentRegion(newStructuredDocumentRegion);
		}
		else {
			StructuredDocumentRegionContainer container = new StructuredDocumentRegionContainer();
			container.appendStructuredDocumentRegion(flatNode);
			container.appendStructuredDocumentRegion(newStructuredDocumentRegion);
			setStructuredDocumentRegion(container);
		}

		return newStructuredDocumentRegion;
	}

	/**
	 * appendText method
	 * 
	 * @param text
	 *            org.w3c.dom.Text
	 */
	public void appendText(Text newText) {
		if (newText == null)
			return;

		if (!isDataEditable()) {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, DOMMessages.NO_MODIFICATION_ALLOWED_ERR);
		}

		TextImpl text = (TextImpl) newText;
		String newSource = text.getSource();
		if (newSource == null || newSource.length() == 0)
			return;
		String source = getSource();
		if (source != null)
			setTextSource(source + newSource);
		else
			setTextSource(newSource);
	}

	/**
	 * cloneNode method
	 * 
	 * @return org.w3c.dom.Node
	 * @param deep
	 *            boolean
	 */
	public Node cloneNode(boolean deep) {
		TextImpl cloned = new TextImpl(this);
		notifyUserDataHandlers(UserDataHandler.NODE_CLONED, cloned);
		return cloned;
	}

	/**
	 * deleteData method
	 * 
	 * @param offset
	 *            int
	 * @param count
	 *            int
	 */
	public void deleteData(int offset, int count) throws DOMException {
		if (count == 0)
			return;
		if (!isDataEditable()) {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, DOMMessages.NO_MODIFICATION_ALLOWED_ERR);
		}
		if (count < 0 || offset < 0) {
			throw new DOMException(DOMException.INDEX_SIZE_ERR, DOMMessages.INDEX_SIZE_ERR);
		}

		String source = getSource();
		if (source == null || source.length() == 0) {
			throw new DOMException(DOMException.INDEX_SIZE_ERR, DOMMessages.INDEX_SIZE_ERR);
		}
		StringPair pair = substringSourceExcluded(source, offset, count);
		if (pair == null)
			return;
		source = null;
		String first = pair.getFirst();
		if (first != null)
			source = first;
		String second = pair.getSecond();
		if (second != null) {
			if (source != null)
				source += second;
			else
				source = second;
		}
		if (source == null)
			source = NodeImpl.EMPTY_STRING; // delete all
		setTextSource(source);
	}

	/**
	 * getData method
	 * 
	 * @return java.lang.String
	 */
	public String getData() throws DOMException {
		if (this.fSource != null)
			return getData(this.fSource);
		String data = super.getData();
		if (data != null)
			return data;
		return getData(getStructuredDocumentRegion());
	}

	/**
	 */
	private String getData(IStructuredDocumentRegion flatNode) {
		if (flatNode == null)
			return NodeImpl.EMPTY_STRING;

		if (flatNode instanceof StructuredDocumentRegionContainer) {
			StructuredDocumentRegionContainer container = (StructuredDocumentRegionContainer) flatNode;
			int length = container.getLength();
			if (length < 16)
				length = 16; // default
			StringBuffer buffer = new StringBuffer(length);
			int count = container.getStructuredDocumentRegionCount();
			for (int i = 0; i < count; i++) {
				IStructuredDocumentRegion content = container.getStructuredDocumentRegion(i);
				String data = getData(content);
				if (data == null)
					continue;
				buffer.append(data);
			}
			return buffer.toString();
		}

		if (flatNode instanceof StructuredDocumentRegionProxy) {
			return flatNode.getText();
		}

		ITextRegion region = StructuredDocumentRegionUtil.getFirstRegion(flatNode);
		if (region != null) {
			String regionType = region.getType();
			if (regionType == DOMRegionContext.XML_ENTITY_REFERENCE || regionType == DOMRegionContext.XML_CHAR_REFERENCE) {
				String name = StructuredDocumentRegionUtil.getEntityRefName(flatNode, region);
				if (name != null) {
					DocumentImpl document = (DocumentImpl) getOwnerDocument();
					if (document != null) {
						String value = document.getCharValue(name);
						if (value != null)
							return value;
					}
				}
			}
		}

		return flatNode.getText();
	}

	/**
	 * Returns data for the source
	 */
	private String getData(String source) {
		if (source == null)
			return null;
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

	/**
	 * getFirstStructuredDocumentRegion method
	 * 
	 */
	public IStructuredDocumentRegion getFirstStructuredDocumentRegion() {
		return StructuredDocumentRegionUtil.getFirstStructuredDocumentRegion(getStructuredDocumentRegion());
	}

	/**
	 * getLastStructuredDocumentRegion method
	 * 
	 */
	public IStructuredDocumentRegion getLastStructuredDocumentRegion() {
		return StructuredDocumentRegionUtil.getLastStructuredDocumentRegion(getStructuredDocumentRegion());
	}

	/**
	 * getNodeName method
	 * 
	 * @return java.lang.String
	 */
	public String getNodeName() {
		return "#text";//$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#getNodeType()
	 */
	public short getNodeType() {
		return TEXT_NODE;
	}

	/**
	 */
	public String getSource() {
		if (this.fSource != null)
			return this.fSource;
		String data = super.getData();
		if (data != null && data.length() > 0) {
			String source = getSource(data);
			if (source != null)
				return source;
		}
		return super.getSource();
	}

	/**
	 * Returns source for the data
	 */
	private String getSource(String data) {
		if (data == null)
			return null;
		IDOMModel model = getModel();
		if (model == null)
			return null; // error
		ISourceGenerator generator = model.getGenerator();
		if (generator == null)
			return null; // error
		return generator.generateTextData(this, data);
	}

	/**
	 */
	String getTextSource() {
		return this.fSource;
	}

	/**
	 */
	public String getValueSource() {
		return getSource();
	}

	/**
	 */
	boolean hasStructuredDocumentRegion(IStructuredDocumentRegion askedStructuredDocumentRegion) {
		if (askedStructuredDocumentRegion == null)
			return false;

		IStructuredDocumentRegion flatNode = getStructuredDocumentRegion();
		if (flatNode == null)
			return false;

		if (flatNode == askedStructuredDocumentRegion)
			return true;

		if (flatNode instanceof StructuredDocumentRegionProxy) {
			StructuredDocumentRegionProxy proxy = (StructuredDocumentRegionProxy) flatNode;
			if (proxy.getStructuredDocumentRegion() == askedStructuredDocumentRegion)
				return true;
			return false;
		}

		if (flatNode instanceof StructuredDocumentRegionContainer) {
			StructuredDocumentRegionContainer container = (StructuredDocumentRegionContainer) flatNode;
			int count = container.getStructuredDocumentRegionCount();
			for (int i = 0; i < count; i++) {
				IStructuredDocumentRegion content = container.getStructuredDocumentRegion(i);
				if (content == null)
					continue;
				if (content == askedStructuredDocumentRegion)
					return true;
				if (content instanceof StructuredDocumentRegionProxy) {
					StructuredDocumentRegionProxy proxy = (StructuredDocumentRegionProxy) content;
					if (proxy.getStructuredDocumentRegion() == askedStructuredDocumentRegion)
						return true;
				}
			}
			return false;
		}

		return false;
	}

	/**
	 * insertData method
	 * 
	 * @param offset
	 *            int
	 * @param arg
	 *            java.lang.String
	 */
	public void insertData(int offset, String arg) throws DOMException {
		if (arg == null || arg.length() == 0)
			return;
		if (!isDataEditable()) {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, DOMMessages.NO_MODIFICATION_ALLOWED_ERR);
		}
		if (offset < 0) {
			throw new DOMException(DOMException.INDEX_SIZE_ERR, DOMMessages.INDEX_SIZE_ERR);
		}

		String source = getSource();
		if (source == null || source.length() == 0) {
			if (offset > 0) {
				throw new DOMException(DOMException.INDEX_SIZE_ERR, DOMMessages.INDEX_SIZE_ERR);
			}
			source = getSource(arg);
			if (source != null)
				setTextSource(source);
			return;
		}

		StringPair pair = substringSourceExcluded(source, offset, 0);
		if (pair == null)
			return; // error
		StringBuffer buffer = new StringBuffer(source.length() + arg.length());
		String first = pair.getFirst();
		if (first != null)
			buffer.append(first);
		source = getSource(arg);
		if (source != null)
			buffer.append(source);
		String second = pair.getSecond();
		if (second != null)
			buffer.append(second);
		setTextSource(buffer.toString());
	}

	/**
	 */
	IStructuredDocumentRegion insertStructuredDocumentRegion(IStructuredDocumentRegion newStructuredDocumentRegion, IStructuredDocumentRegion nextStructuredDocumentRegion) {
		if (newStructuredDocumentRegion == null)
			return null;
		if (nextStructuredDocumentRegion == null)
			return appendStructuredDocumentRegion(newStructuredDocumentRegion);

		IStructuredDocumentRegion flatNode = getStructuredDocumentRegion();
		if (flatNode == null)
			return null; // error

		if (flatNode == nextStructuredDocumentRegion) {
			StructuredDocumentRegionContainer container = new StructuredDocumentRegionContainer();
			container.appendStructuredDocumentRegion(newStructuredDocumentRegion);
			container.appendStructuredDocumentRegion(flatNode);
			setStructuredDocumentRegion(container);
			return newStructuredDocumentRegion;
		}

		if (flatNode instanceof StructuredDocumentRegionContainer) {
			StructuredDocumentRegionContainer container = (StructuredDocumentRegionContainer) flatNode;
			int count = container.getStructuredDocumentRegionCount();
			for (int i = 0; i < count; i++) {
				IStructuredDocumentRegion content = container.getStructuredDocumentRegion(i);
				if (content == nextStructuredDocumentRegion) {
					container.insertStructuredDocumentRegion(newStructuredDocumentRegion, i);
					return newStructuredDocumentRegion;
				}
			}
			return null; // error
		}

		return null; // error
	}

	/**
	 * insertText method
	 * 
	 * @param text
	 *            org.w3c.dom.Text
	 * @param offset
	 *            int
	 */
	public void insertText(Text newText, int offset) throws DOMException {
		if (newText == null)
			return;
		if (!isDataEditable()) {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, DOMMessages.NO_MODIFICATION_ALLOWED_ERR);
		}
		TextImpl text = (TextImpl) newText;
		String newSource = text.getSource();
		if (newSource == null || newSource.length() == 0)
			return;
		if (offset < 0) {
			throw new DOMException(DOMException.INDEX_SIZE_ERR, DOMMessages.INDEX_SIZE_ERR);
		}

		String source = getSource();
		if (source == null || source.length() == 0) {
			if (offset > 0) {
				throw new DOMException(DOMException.INDEX_SIZE_ERR, DOMMessages.INDEX_SIZE_ERR);
			}
			setTextSource(newSource);
			return;
		}

		StringPair pair = substringSourceExcluded(source, offset, 0);
		if (pair == null)
			return; // error
		StringBuffer buffer = new StringBuffer(source.length() + newSource.length());
		String first = pair.getFirst();
		if (first != null)
			buffer.append(first);
		buffer.append(newSource);
		String second = pair.getSecond();
		if (second != null)
			buffer.append(second);
		setTextSource(buffer.toString());
	}

	/**
	 * isCDATAContent method
	 * 
	 * @return boolean
	 */
	public boolean isCDATAContent() {
		Node parent = getParentNode();
		if (parent == null || parent.getNodeType() != Node.ELEMENT_NODE)
			return false;
		ElementImpl element = (ElementImpl) parent;
		return element.isCDATAContainer();
	}

	/**
	 */
	public boolean isInvalid() {
		return isInvalid(getStructuredDocumentRegion());
	}

	/**
	 */
	private boolean isInvalid(IStructuredDocumentRegion flatNode) {
		if (flatNode == null)
			return false;

		if (flatNode instanceof StructuredDocumentRegionContainer) {
			StructuredDocumentRegionContainer container = (StructuredDocumentRegionContainer) flatNode;
			int count = container.getStructuredDocumentRegionCount();
			for (int i = 0; i < count; i++) {
				IStructuredDocumentRegion content = container.getStructuredDocumentRegion(i);
				if (isInvalid(content))
					return true;
			}
			return false;
		}

		if (flatNode instanceof StructuredDocumentRegionProxy) {
			StructuredDocumentRegionProxy proxy = (StructuredDocumentRegionProxy) flatNode;
			return isInvalid(proxy.getStructuredDocumentRegion());
		}

		String regionType = StructuredDocumentRegionUtil.getFirstRegionType(flatNode);
		if (regionType != DOMRegionContext.XML_CONTENT && isNotNestedContent(regionType) && regionType != DOMRegionContext.XML_ENTITY_REFERENCE && regionType != DOMRegionContext.XML_CHAR_REFERENCE && regionType != DOMRegionContext.BLOCK_TEXT && regionType != DOMRegionContext.WHITE_SPACE) {
			return true;
		}

		return false;
	}

	protected boolean isNotNestedContent(String regionType) {
		boolean result = true;
		return result;
	}

	/**
	 */
	boolean isSharingStructuredDocumentRegion(IStructuredDocumentRegion sharedStructuredDocumentRegion) {
		if (sharedStructuredDocumentRegion == null)
			return false;

		IStructuredDocumentRegion flatNode = getStructuredDocumentRegion();
		if (flatNode == null)
			return false;

		if (flatNode == sharedStructuredDocumentRegion)
			return false;

		if (flatNode instanceof StructuredDocumentRegionProxy) {
			StructuredDocumentRegionProxy proxy = (StructuredDocumentRegionProxy) flatNode;
			if (proxy.getStructuredDocumentRegion() == sharedStructuredDocumentRegion)
				return true;
			return false;
		}

		if (flatNode instanceof StructuredDocumentRegionContainer) {
			StructuredDocumentRegionContainer container = (StructuredDocumentRegionContainer) flatNode;
			int count = container.getStructuredDocumentRegionCount();
			for (int i = 0; i < count; i++) {
				IStructuredDocumentRegion content = container.getStructuredDocumentRegion(i);
				if (content == null)
					continue;
				if (content == sharedStructuredDocumentRegion)
					return false;
				if (content instanceof StructuredDocumentRegionProxy) {
					StructuredDocumentRegionProxy proxy = (StructuredDocumentRegionProxy) content;
					if (proxy.getStructuredDocumentRegion() == sharedStructuredDocumentRegion)
						return true;
				}
			}
			return false;
		}

		return false;
	}

	/**
	 * Returns whether this text node contains <a
	 * href='http://www.w3.org/TR/2004/REC-xml-infoset-20040204#infoitem.character'>
	 * element content whitespace</a>, often abusively called "ignorable
	 * whitespace". The text node is determined to contain whitespace in
	 * element content during the load of the document or if validation occurs
	 * while using <code>Document.normalizeDocument()</code>.
	 * 
	 * @see DOM Level 3
	 */
	public boolean isElementContentWhitespace() {
		return isWhitespace();
	}

	/**
	 */
	public boolean isWhitespace() {
		String data = getData();
		if (data == null)
			return true;
		int length = data.length();
		for (int i = 0; i < length; i++) {
			if (!Character.isWhitespace(data.charAt(i)))
				return false;
		}
		return true;
	}

	/**
	 */
	IStructuredDocumentRegion removeStructuredDocumentRegion(IStructuredDocumentRegion oldStructuredDocumentRegion) {
		if (oldStructuredDocumentRegion == null)
			return null;

		IStructuredDocumentRegion flatNode = getStructuredDocumentRegion();
		if (flatNode == null)
			return null; // error

		if (flatNode == oldStructuredDocumentRegion) {
			setStructuredDocumentRegion(null);
			return oldStructuredDocumentRegion;
		}

		if (flatNode instanceof StructuredDocumentRegionProxy) {
			StructuredDocumentRegionProxy proxy = (StructuredDocumentRegionProxy) flatNode;
			if (proxy.getStructuredDocumentRegion() == oldStructuredDocumentRegion) {
				// removed with proxy
				setStructuredDocumentRegion(null);
				return oldStructuredDocumentRegion;
			}
			return null; // error
		}

		if (flatNode instanceof StructuredDocumentRegionContainer) {
			StructuredDocumentRegionContainer container = (StructuredDocumentRegionContainer) flatNode;
			int count = container.getStructuredDocumentRegionCount();
			for (int i = 0; i < count; i++) {
				IStructuredDocumentRegion content = container.getStructuredDocumentRegion(i);
				if (content == oldStructuredDocumentRegion) {
					container.removeStructuredDocumentRegion(i);
					if (container.getStructuredDocumentRegionCount() == 1) {
						// get back to single IStructuredDocumentRegion
						setStructuredDocumentRegion(container.getStructuredDocumentRegion(0));
					}
					return oldStructuredDocumentRegion;
				}

				if (content instanceof StructuredDocumentRegionProxy) {
					StructuredDocumentRegionProxy proxy = (StructuredDocumentRegionProxy) content;
					if (proxy.getStructuredDocumentRegion() == oldStructuredDocumentRegion) {
						// removed with proxy
						container.removeStructuredDocumentRegion(i);
						if (container.getStructuredDocumentRegionCount() == 1) {
							// get back to single IStructuredDocumentRegion
							setStructuredDocumentRegion(container.getStructuredDocumentRegion(0));
						}
						return oldStructuredDocumentRegion;
					}
				}
			}
			return null; // error
		}

		return null; // error
	}

	/**
	 * replaceData method
	 * 
	 * @param offset
	 *            int
	 * @param count
	 *            int
	 * @param arg
	 *            java.lang.String
	 */
	public void replaceData(int offset, int count, String arg) throws DOMException {
		if (!isDataEditable()) {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, DOMMessages.NO_MODIFICATION_ALLOWED_ERR);
		}
		if (arg == null || arg.length() == 0) {
			deleteData(offset, count);
			return;
		}
		if (count == 0) {
			insertData(offset, arg);
			return;
		}
		if (offset < 0 || count < 0) {
			throw new DOMException(DOMException.INDEX_SIZE_ERR, DOMMessages.INDEX_SIZE_ERR);
		}

		String source = getSource();
		if (source == null || source.length() == 0) {
			throw new DOMException(DOMException.INDEX_SIZE_ERR, DOMMessages.INDEX_SIZE_ERR);
		}

		StringPair pair = substringSourceExcluded(source, offset, count);
		if (pair == null)
			return; // error
		StringBuffer buffer = new StringBuffer(source.length() + arg.length());
		String first = pair.getFirst();
		if (first != null)
			buffer.append(first);
		source = getSource(arg);
		if (source != null)
			buffer.append(source);
		String second = pair.getSecond();
		if (second != null)
			buffer.append(second);
		setTextSource(buffer.toString());
	}

	/**
	 */
	IStructuredDocumentRegion replaceStructuredDocumentRegion(IStructuredDocumentRegion newStructuredDocumentRegion, IStructuredDocumentRegion oldStructuredDocumentRegion) {
		if (oldStructuredDocumentRegion == null)
			return null;
		if (newStructuredDocumentRegion == null)
			return removeStructuredDocumentRegion(oldStructuredDocumentRegion);

		IStructuredDocumentRegion flatNode = getStructuredDocumentRegion();
		if (flatNode == null)
			return null; // error

		if (flatNode == oldStructuredDocumentRegion) {
			setStructuredDocumentRegion(newStructuredDocumentRegion);
			return oldStructuredDocumentRegion;
		}

		if (flatNode instanceof StructuredDocumentRegionProxy) {
			StructuredDocumentRegionProxy proxy = (StructuredDocumentRegionProxy) flatNode;
			if (proxy.getStructuredDocumentRegion() == oldStructuredDocumentRegion) {
				if (newStructuredDocumentRegion instanceof StructuredDocumentRegionProxy) {
					// proxy must not be nested
					setStructuredDocumentRegion(newStructuredDocumentRegion);
				}
				else {
					proxy.setStructuredDocumentRegion(newStructuredDocumentRegion);
				}
				return oldStructuredDocumentRegion;
			}
			return null; // error
		}

		if (flatNode instanceof StructuredDocumentRegionContainer) {
			StructuredDocumentRegionContainer container = (StructuredDocumentRegionContainer) flatNode;
			int count = container.getStructuredDocumentRegionCount();
			for (int i = 0; i < count; i++) {
				IStructuredDocumentRegion content = container.getStructuredDocumentRegion(i);
				if (content == null)
					continue; // error
				if (content == oldStructuredDocumentRegion) {
					container.replaceStructuredDocumentRegion(newStructuredDocumentRegion, i);
					return oldStructuredDocumentRegion;
				}

				if (content instanceof StructuredDocumentRegionProxy) {
					StructuredDocumentRegionProxy proxy = (StructuredDocumentRegionProxy) content;
					if (proxy.getStructuredDocumentRegion() == oldStructuredDocumentRegion) {
						if (newStructuredDocumentRegion instanceof StructuredDocumentRegionProxy) {
							// proxy must not be nested
							container.replaceStructuredDocumentRegion(newStructuredDocumentRegion, i);
						}
						else {
							proxy.setStructuredDocumentRegion(newStructuredDocumentRegion);
						}
						return oldStructuredDocumentRegion;
					}
				}
			}
			return null; // error
		}

		return null; // error
	}

	/**
	 */
	void resetStructuredDocumentRegions() {
		String source = getSource();
		if (source != null && source.length() > 0)
			this.fSource = source;
		super.resetStructuredDocumentRegions();
	}

	/**
	 * getData method
	 * 
	 * @return java.lang.String
	 */
	public void setData(String data) throws DOMException {
		if (!isDataEditable()) {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, DOMMessages.NO_MODIFICATION_ALLOWED_ERR);
		}

		this.fSource = null;
		super.setData(data);
	}

	/**
	 */
	public void setSource(String source) throws InvalidCharacterException {
		if (!isDataEditable()) {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, DOMMessages.NO_MODIFICATION_ALLOWED_ERR);
		}

		SourceValidator validator = new SourceValidator(this);
		if (validator.validateSource(source))
			setTextSource(source);
	}

	/**
	 */
	void setStructuredDocumentRegion(IStructuredDocumentRegion flatNode) {
		super.setStructuredDocumentRegion(flatNode);
		if (flatNode != null)
			this.fSource = null;
	}

	/**
	 */
	public void setTextSource(String source) {
		if (!isDataEditable()) {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, DOMMessages.NO_MODIFICATION_ALLOWED_ERR);
		}

		this.fSource = source;

		notifyValueChanged();
	}

	/**
	 */
	public void setValueSource(String source) {
		if (!isDataEditable()) {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, DOMMessages.NO_MODIFICATION_ALLOWED_ERR);
		}

		SourceValidator validator = new SourceValidator(this);
		setTextSource(validator.convertSource(source));
	}

	/**
	 * splitText method
	 * 
	 * @return org.w3c.dom.Text
	 * @param offset
	 *            int
	 */
	public Text splitText(int offset) throws DOMException {
		if (!isDataEditable()) {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, DOMMessages.NO_MODIFICATION_ALLOWED_ERR);
		}
		if (offset < 0) {
			throw new DOMException(DOMException.INDEX_SIZE_ERR, DOMMessages.INDEX_SIZE_ERR);
		}
		int length = getLength();
		if (offset > length) {
			throw new DOMException(DOMException.INDEX_SIZE_ERR, DOMMessages.INDEX_SIZE_ERR);
		}
		Document document = getOwnerDocument();
		if (document == null)
			return null;

		String source = null;
		if (offset < length) {
			int count = length - offset;
			source = substringSource(offset, count);
			deleteData(offset, count);
		}
		TextImpl text = (TextImpl) document.createTextNode(null);
		if (source != null)
			text.setTextSource(source);

		Node parent = getParentNode();
		if (parent != null)
			parent.insertBefore(text, getNextSibling());

		return text;
	}

	/**
	 */
	Text splitText(IStructuredDocumentRegion nextStructuredDocumentRegion) {
		if (nextStructuredDocumentRegion == null)
			return null;

		IStructuredDocumentRegion flatNode = getStructuredDocumentRegion();
		if (flatNode == null || !(flatNode instanceof StructuredDocumentRegionContainer))
			return null; // error

		StructuredDocumentRegionContainer container = (StructuredDocumentRegionContainer) flatNode;
		int count = container.getStructuredDocumentRegionCount();
		int index = 0;
		for (; index < count; index++) {
			if (container.getStructuredDocumentRegion(index) == nextStructuredDocumentRegion)
				break;
		}
		if (index >= count) {
			// this is the case nextStructuredDocumentRegion is a new
			// IStructuredDocumentRegion
			// search gap by offset
			int offset = nextStructuredDocumentRegion.getStart();
			for (index = 0; index < count; index++) {
				IStructuredDocumentRegion content = container.getStructuredDocumentRegion(index);
				if (content == null)
					continue; // error
				if (content.getStart() >= offset)
					break;
			}
			if (index >= count)
				return null; // error
		}
		if (index == 0)
			return this; // nothing to do

		Document document = getOwnerDocument();
		if (document == null)
			return null; // error
		Node parent = getParentNode();
		if (parent == null)
			return null; // error
		TextImpl nextText = (TextImpl) document.createTextNode(null);
		if (nextText == null)
			return null; // error

		for (; index < count; count--) {
			nextText.appendStructuredDocumentRegion(container.removeStructuredDocumentRegion(index));
		}

		// normalize IStructuredDocumentRegion
		if (index == 1) {
			setStructuredDocumentRegion(container.getStructuredDocumentRegion(0));
		}

		parent.insertBefore(nextText, getNextSibling());
		return nextText;
	}

	/**
	 * Retruns data for the range
	 */
	private String substringData(String data, int offset, int count) throws DOMException {
		// sure offset and count are non-negative
		if (count == 0)
			return NodeImpl.EMPTY_STRING;
		if (data == null) {
			throw new DOMException(DOMException.INDEX_SIZE_ERR, DOMMessages.INDEX_SIZE_ERR);
		}
		int length = data.length();
		if (offset > length) {
			throw new DOMException(DOMException.INDEX_SIZE_ERR, DOMMessages.INDEX_SIZE_ERR);
		}
		int end = offset + count;
		if (end > length) {
			throw new DOMException(DOMException.INDEX_SIZE_ERR, DOMMessages.INDEX_SIZE_ERR);
		}
		return data.substring(offset, end);
	}

	/**
	 * Returns source for the range specified by: offset: data offset count:
	 * data count
	 */
	private String substringSource(int offset, int count) throws DOMException {
		// sure offset and count are non-negative
		if (this.fSource != null)
			return substringSource(this.fSource, offset, count);

		String data = super.getData();
		if (data != null && data.length() > 0) {
			data = substringData(data, offset, count);
			if (data == null)
				return NodeImpl.EMPTY_STRING;
			String source = getSource(data);
			if (source != null)
				return source;
		}

		return substringSource(getSource(), offset, count);
	}

	/**
	 * Returns source for the range specified by: offset: data offset count:
	 * data count
	 */
	private String substringSource(String source, int offset, int count) throws DOMException {
		// sure offset and count are non-negative
		if (count == 0)
			return NodeImpl.EMPTY_STRING;
		if (source == null) {
			throw new DOMException(DOMException.INDEX_SIZE_ERR, DOMMessages.INDEX_SIZE_ERR);
		}

		int length = source.length();
		int end = offset + count;

		// find character reference
		int ref = source.indexOf('&');
		while (ref >= 0) {
			if (ref >= end)
				break;
			int refEnd = source.indexOf(';', ref + 1);
			if (refEnd > ref + 1) {
				String name = source.substring(ref + 1, refEnd);
				if (getCharValue(name) != null) {
					// found, shift for source offsets
					int refCount = refEnd - ref;
					if (ref < offset)
						offset += refCount;
					if (ref < end)
						end += refCount;
					ref = refEnd;
				}
			}
			ref = source.indexOf('&', ref + 1);
		}

		if (offset > length || end > length) {
			throw new DOMException(DOMException.INDEX_SIZE_ERR, DOMMessages.INDEX_SIZE_ERR);
		}

		return source.substring(offset, end);
	}

	/**
	 * Returns sources before and after the range specified by: offset: data
	 * offset count: data count
	 */
	private StringPair substringSourceExcluded(String source, int offset, int count) throws DOMException {
		// sure offset and count are non-negative
		if (source == null) {
			if (offset == 0 && count == 0)
				return new StringPair(null, null);
			throw new DOMException(DOMException.INDEX_SIZE_ERR, DOMMessages.INDEX_SIZE_ERR);
		}

		int length = source.length();
		int end = offset + count;

		// find character reference
		int ref = source.indexOf('&');
		while (ref >= 0) {
			if (ref >= end)
				break;
			int refEnd = source.indexOf(';', ref + 1);
			if (refEnd > ref + 1) {
				String name = source.substring(ref + 1, refEnd);
				if (getCharValue(name) != null) {
					// found, shift for source offsets
					int refCount = refEnd - ref;
					if (ref < offset)
						offset += refCount;
					if (ref < end)
						end += refCount;
					ref = refEnd;
				}
			}
			ref = source.indexOf('&', ref + 1);
		}

		if (offset > length) {
			throw new DOMException(DOMException.INDEX_SIZE_ERR, DOMMessages.INDEX_SIZE_ERR);
		}

		String first = (offset > 0 ? source.substring(0, offset) : null);
		String second = (end < length ? source.substring(end, length) : null);
		return new StringPair(first, second);
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Text#getWholeText()
	 */
	public String getWholeText() {
		Node current = this;
		// rewind
		while (current.getPreviousSibling() != null)
			current = current.getPreviousSibling();
		// playback
		StringBuffer buffer = new StringBuffer();
		while (current != null) {
			if (current.getNodeType() == Node.TEXT_NODE || current.getNodeType() == Node.CDATA_SECTION_NODE) {
				buffer.append(current.getNodeValue());
			}
			current = current.getNextSibling();
		}
		return buffer.toString();
	}
    /**
	 * Replaces the text of the current node and all logically-adjacent text
	 * nodes with the specified text. All logically-adjacent text nodes are
	 * removed including the current node unless it was the recipient of the
	 * replacement text. <br>
	 * This method returns the node which received the replacement text. The
	 * returned node is:
	 * <ul>
	 * <li><code>null</code>, when the replacement text is the empty
	 * string; </li>
	 * <li>the current node, except when the current node is read-only; </li>
	 * <li> a new <code>Text</code> node of the same type (
	 * <code>Text</code> or <code>CDATASection</code>) as the current
	 * node inserted at the location of the replacement. </li>
	 * </ul>
	 * <br>
	 * For instance, in the above example calling
	 * <code>replaceWholeText</code> on the <code>Text</code> node that
	 * contains "bar" with "yo" in argument results in the following: <br>
	 * Where the nodes to be removed are read-only descendants of an
	 * <code>EntityReference</code>, the <code>EntityReference</code>
	 * must be removed instead of the read-only nodes. If any
	 * <code>EntityReference</code> to be removed has descendants that are
	 * not <code>EntityReference</code>, <code>Text</code>, or
	 * <code>CDATASection</code> nodes, the <code>replaceWholeText</code>
	 * method must fail before performing any modification of the document,
	 * raising a <code>DOMException</code> with the code
	 * <code>NO_MODIFICATION_ALLOWED_ERR</code>. <br>
	 * For instance, in the example below calling
	 * <code>replaceWholeText</code> on the <code>Text</code> node that
	 * contains "bar" fails, because the <code>EntityReference</code> node
	 * "ent" contains an <code>Element</code> node which cannot be removed.
	 * 
	 * @param content
	 *            The content of the replacing <code>Text</code> node.
	 * @return The <code>Text</code> node created with the specified
	 *         content.
	 * @exception DOMException
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if one of the
	 *                <code>Text</code> nodes being replaced is readonly.
	 * @see DOM Level 3
	 */
    public Text replaceWholeText(String content)
                                 throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented"); //$NON-NLS-1$
    }
}
