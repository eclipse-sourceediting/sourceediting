/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.contentmodel;



import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.wst.html.core.internal.provisional.HTMLCMProperties;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMContent;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

/**
 * Factory for element declarations of the JSP 1.1 and 1.2.
 */
class JSPElementCollection extends DeclCollection implements JSP11Namespace.ElementName {


	class TypePacket {
		public String name = null;
		public int content = CMElementDeclaration.EMPTY;
		public int omit = HTMLElementDeclaration.OMIT_NONE;
		public int lineBreak = HTMLElementDeclaration.BREAK_NONE;
		public int layout = HTMLElementDeclaration.LAYOUT_NONE;
		public int correct = HTMLElementDeclaration.CORRECT_NONE;
		public int format = HTMLElementDeclaration.FORMAT_XML;
		public boolean indentChild = false;

		public TypePacket() {
		}
	}

	/** JSP element declaration. */
	class ElemDecl extends CMContentImpl implements HTMLElementDeclaration, HTMLPropertyDeclaration {
		private TypePacket type = null;
		private CMGroupImpl content = null;
		private CMNamedNodeMapImpl attributes = null;

		public ElemDecl(TypePacket t) {
			super(t.name, 1, 1);
			type = t;
		}

		public void setContent(CMGroupImpl group) {
			content = group;
		}

		public void setAttributes(CMNamedNodeMapImpl attrs) {
			attributes = attrs;
		}

		// implements CMNode
		public int getNodeType() {
			return CMNode.ELEMENT_DECLARATION;
		}

		public boolean supports(String propertyName) {
			if (propertyName.equals(HTMLCMProperties.SHOULD_IGNORE_CASE)) {
				return true;
			}
			else if (propertyName.equals(HTMLCMProperties.CONTENT_HINT)) {
				return true;
			}
			else {
				PropertyProvider pp = PropertyProviderFactory.getProvider(propertyName);
				if (pp == null)
					return false;
				return pp.supports(this);
			}
		}

		public Object getProperty(String propertyName) {
			if (propertyName.equals(HTMLCMProperties.SHOULD_IGNORE_CASE)) {
				return Boolean.FALSE; //D208839
			}
			else if (propertyName.equals(HTMLCMProperties.CONTENT_HINT)) {
				String myName = getElementName();
				if (myName == JSP11Namespace.ElementName.PLUGIN) {
					return getNamedItem(JSP11Namespace.ElementName.PARAMS); // outer class method.
				}
				else if (myName == JSP11Namespace.ElementName.PARAMS) {
					return getNamedItem(JSP11Namespace.ElementName.PARAM); // outer class method.
				}
				else {
					return null;
				}
			}
			else {
				PropertyProvider pp = PropertyProviderFactory.getProvider(propertyName);
				if (pp == null)
					return null;
				return pp.get(this);
			}
		}

		// implementes CMElementDeclaration
		public CMNamedNodeMap getAttributes() {
			return attributes;
		}

		public CMContent getContent() {
			return content;
		}

		public int getContentType() {
			return type.content;
		}

		public CMDataType getDataType() {
			return null;
		}

		public String getElementName() {
			return getNodeName();
		}

		public CMNamedNodeMap getLocalElements() {
			return null;
		}

		// implementes HTMLElementDeclaration
		public HTMLAttributeDeclaration getAttributeDeclaration(String attrName) {
			if (attributes == null)
				return null;
			return (HTMLAttributeDeclaration) attributes.getNamedItem(attrName);
		}

		public int getCorrectionType() {
			return type.correct;
		}

		public CMContent getExclusion() {
			return null;
		}

		public CMContent getInclusion() {
			return null;
		}

		public CMNamedNodeMap getProhibitedAncestors() {
			return EMPTY_MAP;
		}

		public int getFormatType() {
			return type.format;
		}

		public int getLayoutType() {
			return type.layout;
		}

		public int getLineBreakHint() {
			return type.lineBreak;
		}

		public int getOmitType() {
			return type.omit;
		}

		public boolean shouldTerminateAt(HTMLElementDeclaration dec) {
			return false;
		}

		public boolean shouldKeepSpaces() {
			return false;
		}

		public boolean shouldIndentChildSource() {
			return type.indentChild;
		}

		public boolean isJSP() {
			return true;
		}
	}

	// element IDs
	static class Ids {
		public static final int ID_SCRIPTLET = 0;
		public static final int ID_EXPRESSION = 1;
		public static final int ID_DECLARATION = 2;
		public static final int ID_DIRECTIVE_PAGE = 3;
		public static final int ID_DIRECTIVE_INCLUDE = 4;
		public static final int ID_DIRECTIVE_TAGLIB = 5;
		public static final int ID_USEBEAN = 6;
		public static final int ID_SETPROPERTY = 7;
		public static final int ID_GETPROPERTY = 8;
		public static final int ID_INCLUDE = 9;
		public static final int ID_FORWARD = 10;
		public static final int ID_PLUGIN = 11;
		public static final int ID_PARAMS = 12;
		public static final int ID_FALLBACK = 13;
		public static final int ID_PARAM = 14;
		public static final int ID_ROOT = 15;
		public static final int ID_TEXT = 16;

		public static int getNumOfIds() {
			if (numofids != -1)
				return numofids;

			// NOTE: If the reflection is too slow, this method should
			// just return the literal value, like 105.
			// -- 5/25/2001
			Class clazz = Ids.class;
			Field[] fields = clazz.getFields();
			numofids = 0;
			for (int i = 0; i < fields.length; i++) {
				String name = fields[i].getName();
				if (name.startsWith("ID_"))//$NON-NLS-1$
					numofids++;
			}
			return numofids;
		}

		// chache the result of the reflection.
		private static int numofids = -1;
	}

	// attribute creater
	class JACreater implements JSP11Namespace {
		// attribute declaration
		class AttrDecl extends CMNodeImpl implements HTMLAttributeDeclaration {
			HTMLCMDataTypeImpl type = null;
			int usage = CMAttributeDeclaration.OPTIONAL;

			// methods
			public AttrDecl(String attrName) {
				super(attrName);
			}

			public String getAttrName() {
				return getNodeName();
			}

			public CMDataType getAttrType() {
				return type;
			}

			/** @deprecated by superclass */
			public String getDefaultValue() {
				if (type.getImpliedValueKind() != CMDataType.IMPLIED_VALUE_DEFAULT)
					return null;
				return type.getImpliedValue();
			}

			/** @deprecated  by superclass */
			public Enumeration getEnumAttr() {
				Vector v = new Vector(Arrays.asList(type.getEnumeratedValues()));
				return v.elements();
			}

			public int getNodeType() {
				return CMNode.ATTRIBUTE_DECLARATION;
			}

			public int getUsage() {
				return usage;
			}

			public boolean shouldIgnoreCase() {
				return false;
			}
		}

		CMNamedNodeMapImpl declarations = null;

		public JACreater() {
			declarations = new CMNamedNodeMapImpl();
		}

		public CMNamedNodeMapImpl getDeclarations(int eid) {
			switch (eid) {
				case Ids.ID_DIRECTIVE_PAGE :
					createForDirPage();
					break;
				case Ids.ID_DIRECTIVE_INCLUDE :
					createForDirInclude();
					break;
				case Ids.ID_DIRECTIVE_TAGLIB :
					createForDirTaglib();
					break;
				case Ids.ID_USEBEAN :
					createForUseBean();
					break;
				case Ids.ID_SETPROPERTY :
					createForSetProperty();
					break;
				case Ids.ID_GETPROPERTY :
					createForGetProperty();
					break;
				case Ids.ID_INCLUDE :
					createForInclude();
					break;
				case Ids.ID_FORWARD :
					createForForward();
					break;
				case Ids.ID_PLUGIN :
					createForPlugin();
					break;
				case Ids.ID_PARAM :
					createForParam();
					break;
				case Ids.ID_ROOT :
					createForRoot();
					break;
				default :
					// should warn.
					break;
			}
			return declarations;
		}

		AttrDecl createBoolType(String name, String defValue) {
			AttrDecl adec = new AttrDecl(name);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_TRUE, ATTR_VALUE_FALSE};
			adec.type.setEnumValues(values);
			adec.type.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, defValue);
			return adec;
		}

		private void createForDirPage() {
			AttrDecl adec = null;
			// ("language" CDATA DECLARED "java")
			adec = new AttrDecl(ATTR_NAME_LANGUAGE);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.type.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_JAVA);
			declarations.putNamedItem(ATTR_NAME_LANGUAGE, adec);

			// ("extends" CDATA IMPLIED)
			adec = new AttrDecl(ATTR_NAME_EXTENDS);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			declarations.putNamedItem(ATTR_NAME_EXTENDS, adec);

			// ("contentType" CDATA DECLARED "text/html; ISO-8859-1")
			adec = new AttrDecl(ATTR_NAME_CONTENT_TYPE);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.type.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_CT_DEFAULT);
			declarations.putNamedItem(ATTR_NAME_CONTENT_TYPE, adec);

			// ("import" CDATA IMPLIED)
			adec = new AttrDecl(ATTR_NAME_IMPORT);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			declarations.putNamedItem(ATTR_NAME_IMPORT, adec);

			// ("session" ENUM DECLARED (true|false) "true")
			adec = createBoolType(ATTR_NAME_SESSION, ATTR_VALUE_TRUE);
			if (adec != null)
				declarations.putNamedItem(ATTR_NAME_SESSION, adec);

			// ("buffer" CDATA DECLARED "8kb")
			adec = new AttrDecl(ATTR_NAME_BUFFER);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.type.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_BUFSIZ_DEFAULT);
			declarations.putNamedItem(ATTR_NAME_BUFFER, adec);

			// ("autoFlush" ENUM DECLARED (true|false) "true")
			adec = createBoolType(ATTR_NAME_AUTOFLUSH, ATTR_VALUE_TRUE);
			if (adec != null)
				declarations.putNamedItem(ATTR_NAME_AUTOFLUSH, adec);

			// ("isThreadSafe" ENUM DECLARED (true|false) "true")
			adec = createBoolType(ATTR_NAME_IS_THREAD_SAFE, ATTR_VALUE_TRUE);
			if (adec != null)
				declarations.putNamedItem(ATTR_NAME_IS_THREAD_SAFE, adec);

			// ("info" CDATA IMPLIED)
			adec = new AttrDecl(ATTR_NAME_INFO);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			declarations.putNamedItem(ATTR_NAME_INFO, adec);

			// ("errorPage" URI IMPLIED)
			adec = new AttrDecl(ATTR_NAME_ERROR_PAGE);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.URI);
			declarations.putNamedItem(ATTR_NAME_ERROR_PAGE, adec);

			// ("isErrorPage" ENUM DECLARED (true|false) "false")
			adec = createBoolType(ATTR_NAME_IS_ERROR_PAGE, ATTR_VALUE_FALSE);
			if (adec != null)
				declarations.putNamedItem(ATTR_NAME_IS_ERROR_PAGE, adec);

			// ("pageEncoding" CDATA IMPLIED)
			adec = new AttrDecl(ATTR_NAME_PAGE_ENCODING);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			declarations.putNamedItem(ATTR_NAME_PAGE_ENCODING, adec);

		}

		private void createForDirInclude() {
			// ("file" URI REQUIRED); Defect TORO:185241
			AttrDecl adec = new AttrDecl(ATTR_NAME_FILE);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.URI);
			adec.usage = CMAttributeDeclaration.REQUIRED;
			declarations.putNamedItem(ATTR_NAME_FILE, adec);
		}

		private void createForDirTaglib() {
			// ("uri" URI REQUIRED)
			AttrDecl adec = new AttrDecl(ATTR_NAME_URI);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.URI);
			adec.usage = CMAttributeDeclaration.REQUIRED;
			declarations.putNamedItem(ATTR_NAME_URI, adec);

			// ("prefix" CDATA REQUIRED)
			adec = new AttrDecl(ATTR_NAME_PREFIX);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.REQUIRED;
			declarations.putNamedItem(ATTR_NAME_PREFIX, adec);
		}

		private void createForUseBean() {
			// ("id" ID REQUIRED)
			AttrDecl adec = new AttrDecl(ATTR_NAME_ID);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.ID);
			adec.usage = CMAttributeDeclaration.REQUIRED;
			declarations.putNamedItem(ATTR_NAME_ID, adec);

			// ("scope" ENUM DECLARED (page|session|request|application) "page")
			adec = new AttrDecl(ATTR_NAME_SCOPE);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_PAGE, ATTR_VALUE_SESSION, ATTR_VALUE_REQUEST, ATTR_VALUE_APPLICATION};
			adec.type.setEnumValues(values);
			adec.type.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_PAGE);
			declarations.putNamedItem(ATTR_NAME_SCOPE, adec);

			// ("class" CDATA IMPLIED)
			adec = new AttrDecl(ATTR_NAME_CLASS);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			declarations.putNamedItem(ATTR_NAME_CLASS, adec);

			// ("beanName" CDATA IMPLIED)
			adec = new AttrDecl(ATTR_NAME_BEAN_NAME);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			declarations.putNamedItem(ATTR_NAME_BEAN_NAME, adec);

			// ("type" CDATA IMPLIED)
			adec = new AttrDecl(ATTR_NAME_TYPE);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			declarations.putNamedItem(ATTR_NAME_TYPE, adec);
		}

		private void createForSetProperty() {
			// ("name" IDREF REQUIRED)
			AttrDecl adec = new AttrDecl(ATTR_NAME_NAME);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.IDREF);
			adec.usage = CMAttributeDeclaration.REQUIRED;
			declarations.putNamedItem(ATTR_NAME_NAME, adec);

			// ("property" CDATA REQUIRED)
			adec = new AttrDecl(ATTR_NAME_PROPERTY);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.REQUIRED;
			declarations.putNamedItem(ATTR_NAME_PROPERTY, adec);

			// ("value" CDATA IMPLIED)
			adec = new AttrDecl(ATTR_NAME_VALUE);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			declarations.putNamedItem(ATTR_NAME_VALUE, adec);

			// ("param" CDATA IMPLIED)
			adec = new AttrDecl(ATTR_NAME_PARAM);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			declarations.putNamedItem(ATTR_NAME_PARAM, adec);

		}

		private void createForGetProperty() {
			// ("name" IDREF REQUIRED)
			AttrDecl adec = new AttrDecl(ATTR_NAME_NAME);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.IDREF);
			adec.usage = CMAttributeDeclaration.REQUIRED;
			declarations.putNamedItem(ATTR_NAME_NAME, adec);

			// ("property" CDATA REQUIRED)
			adec = new AttrDecl(ATTR_NAME_PROPERTY);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.REQUIRED;
			declarations.putNamedItem(ATTR_NAME_PROPERTY, adec);

		}

		private void createForInclude() {
			AttrDecl adec = null;
			// ("page" URI REQUIRED)
			adec = new AttrDecl(ATTR_NAME_PAGE);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.URI);
			adec.usage = CMAttributeDeclaration.REQUIRED;
			declarations.putNamedItem(ATTR_NAME_PAGE, adec);

			// ("flush" ENUM OPTIONAL (true|false)); Defect TORO:185241
			adec = new AttrDecl(ATTR_NAME_FLUSH);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			String[] values = {ATTR_VALUE_TRUE, ATTR_VALUE_FALSE};
			adec.type.setEnumValues(values);
			adec.type.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_FALSE);
			declarations.putNamedItem(ATTR_NAME_FLUSH, adec);
		}

		private void createForForward() {
			// ("page" URI REQUIRED)
			AttrDecl adec = new AttrDecl(ATTR_NAME_PAGE);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.URI);
			adec.usage = CMAttributeDeclaration.REQUIRED;
			declarations.putNamedItem(ATTR_NAME_PAGE, adec);
		}

		private void createForPlugin() {
			// ("type" ENUM REQUIRED (bean|applet))
			AttrDecl adec = new AttrDecl(ATTR_NAME_TYPE);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			adec.usage = CMAttributeDeclaration.REQUIRED;
			String[] values = {ATTR_VALUE_BEAN, ATTR_VALUE_APPLET};
			adec.type.setEnumValues(values);
			declarations.putNamedItem(ATTR_NAME_TYPE, adec);

			// ("code" CDATA REQUIRED)
			adec = new AttrDecl(ATTR_NAME_CODE);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.URI);
			adec.usage = CMAttributeDeclaration.REQUIRED;
			declarations.putNamedItem(ATTR_NAME_CODE, adec);

			// ("codebase" URI REQUIRED)
			adec = new AttrDecl(ATTR_NAME_CODEBASE);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.URI);
			adec.usage = CMAttributeDeclaration.REQUIRED;
			declarations.putNamedItem(ATTR_NAME_CODEBASE, adec);

			// ("align" ENUM IMPLIED (top|middle|bottom|left|right))
			adec = new AttrDecl(ATTR_NAME_ALIGN);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] aligns = {ATTR_VALUE_TOP, ATTR_VALUE_MIDDLE, ATTR_VALUE_BOTTOM, ATTR_VALUE_LEFT, ATTR_VALUE_RIGHT};
			adec.type.setEnumValues(aligns);
			declarations.putNamedItem(ATTR_NAME_ALIGN, adec);

			// ("archive" CDATA IMPLIED)
			adec = new AttrDecl(ATTR_NAME_ARCHIVE);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			declarations.putNamedItem(ATTR_NAME_ARCHIVE, adec);

			// ("height" CDATA IMPLIED)
			adec = new AttrDecl(ATTR_NAME_HEIGHT);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			declarations.putNamedItem(ATTR_NAME_HEIGHT, adec);

			// ("hspace" CDATA IMPLIED)
			adec = new AttrDecl(ATTR_NAME_HSPACE);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			declarations.putNamedItem(ATTR_NAME_HSPACE, adec);

			// ("jreversion" CDATA DECLARED "1.1")
			adec = new AttrDecl(ATTR_NAME_JREVERSION);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.type.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_JVER11);
			declarations.putNamedItem(ATTR_NAME_JREVERSION, adec);

			// ("name" CDATA IMPLIED)
			adec = new AttrDecl(ATTR_NAME_NAME);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			declarations.putNamedItem(ATTR_NAME_NAME, adec);

			// ("vspace" CDATA IMPLIED)
			adec = new AttrDecl(ATTR_NAME_VSPACE);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			declarations.putNamedItem(ATTR_NAME_VSPACE, adec);

			// ("width" CDATA IMPLIED)
			adec = new AttrDecl(ATTR_NAME_WIDTH);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			declarations.putNamedItem(ATTR_NAME_WIDTH, adec);

			// ("nspluginurl" URI IMPLIED)
			adec = new AttrDecl(ATTR_NAME_NSPLUGINURL);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.URI);
			declarations.putNamedItem(ATTR_NAME_NSPLUGINURL, adec);

			// ("iepluginurl" URI IMPLIED)
			adec = new AttrDecl(ATTR_NAME_IEPLUGINURL);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.URI);
			declarations.putNamedItem(ATTR_NAME_IEPLUGINURL, adec);
		}

		private void createForParam() {
			// ("name" CDATA REQUIRED)
			AttrDecl adec = new AttrDecl(ATTR_NAME_NAME);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.REQUIRED;
			declarations.putNamedItem(ATTR_NAME_NAME, adec);

			// ("value" CDATA REQUIRED); Defect TORO:185241
			adec = new AttrDecl(ATTR_NAME_VALUE);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.REQUIRED;
			declarations.putNamedItem(ATTR_NAME_VALUE, adec);

		}

		private void createForRoot() {
			// ("xmlns:jsp" CDATA "http://java.sun.com/JSP/Page")
			AttrDecl adec = new AttrDecl(ATTR_NAME_XMLNS_JSP);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.type.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_XMLNS_JSP);
			declarations.putNamedItem(ATTR_NAME_XMLNS_JSP, adec);
			// ("version" CDATA REQUIRED)
			adec = new AttrDecl(ATTR_NAME_VERSION);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.REQUIRED;
			declarations.putNamedItem(ATTR_NAME_VERSION, adec);
		}
	}

	private final static CMNamedNodeMap EMPTY_MAP = new CMNamedNodeMap() {
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
	private static String[] names = null;

	static {
		names = new String[Ids.getNumOfIds()];
		names[Ids.ID_SCRIPTLET] = SCRIPTLET;
		names[Ids.ID_EXPRESSION] = EXPRESSION;
		names[Ids.ID_DECLARATION] = DECLARATION;
		names[Ids.ID_DIRECTIVE_PAGE] = DIRECTIVE_PAGE;
		names[Ids.ID_DIRECTIVE_INCLUDE] = DIRECTIVE_INCLUDE;
		names[Ids.ID_DIRECTIVE_TAGLIB] = DIRECTIVE_TAGLIB;
		names[Ids.ID_USEBEAN] = USEBEAN;
		names[Ids.ID_SETPROPERTY] = SETPROPERTY;
		names[Ids.ID_GETPROPERTY] = GETPROPERTY;
		names[Ids.ID_INCLUDE] = INCLUDE;
		names[Ids.ID_FORWARD] = FORWARD;
		names[Ids.ID_PLUGIN] = PLUGIN;
		names[Ids.ID_PARAMS] = PARAMS;
		names[Ids.ID_FALLBACK] = FALLBACK;
		names[Ids.ID_PARAM] = PARAM;
		names[Ids.ID_ROOT] = ROOT;
		names[Ids.ID_TEXT] = TEXT;
	}

	JSPElementCollection(String[] names, boolean tolerant) {
		super(names, tolerant);
	}

	/**
	 */
	public JSPElementCollection() {
		super(names, TOLERANT_CASE);
	}

	/**
	 * @return org.eclipse.wst.xml.core.internal.contentmodel.CMNode
	 * @param elementName java.lang.String
	 */
	protected CMNode create(String elementName) {
		return createElemDecl(getID(elementName));
	}

	/**
	 * @param eid int
	 */
	CMGroupImpl createContent(int eid) {
		if (eid == ID_UNKNOWN)
			return null;

		CMGroupImpl content = null;
		CMNode child = null;

		switch (eid) {
			case Ids.ID_INCLUDE :
			case Ids.ID_FORWARD :
				// (jsp:param)*
				content = new CMGroupImpl(CMGroup.SEQUENCE, 0, CMContentImpl.UNBOUNDED);
				child = item(Ids.ID_PARAM);
				if (child != null)
					content.appendChild(child);
				break;
			case Ids.ID_PLUGIN :
				// (jsp:params | jsp:fallback)?
				content = new CMGroupImpl(CMGroup.CHOICE, 0, 1);
				// jsp:params
				child = item(Ids.ID_PARAMS);
				if (child != null)
					content.appendChild(child);
				// jsp:fallback
				child = item(Ids.ID_FALLBACK);
				if (child != null)
					content.appendChild(child);
				break;
			case Ids.ID_PARAMS :
				// (jsp:param)+
				content = new CMGroupImpl(CMGroup.SEQUENCE, 1, CMContentImpl.UNBOUNDED);
				child = item(Ids.ID_PARAM);
				if (child != null)
					content.appendChild(child);
				break;
			case Ids.ID_ROOT :
				// %Body;
				// --> (jsp:text|%Directives;|%Scripts;|%Actions;)*
				//     %Directives --> jsp:directive.page|jsp:directive.include
				//     %Scripts; --> jsp:scriptlet|jsp:declaration|jsp:expression
				//     %Actions --> jsp:useBean|jsp.setProperty|jsp:getProperty
				//                  |jsp:include|jsp:forward|jsp:plugin
				content = new CMGroupImpl(CMGroup.CHOICE, 0, CMContentImpl.UNBOUNDED);
				int validChildren[] = {Ids.ID_TEXT,
				// %Directves;
							Ids.ID_DIRECTIVE_PAGE, Ids.ID_DIRECTIVE_INCLUDE,
							// %Scripts;
							Ids.ID_SCRIPTLET, Ids.ID_DECLARATION, Ids.ID_EXPRESSION,
							// %Actions;
							Ids.ID_USEBEAN, Ids.ID_SETPROPERTY, Ids.ID_GETPROPERTY, Ids.ID_INCLUDE, Ids.ID_FORWARD, Ids.ID_PLUGIN};
				for (int i = 0; i < validChildren.length; i++) {
					child = item(validChildren[i]);
					if (child != null)
						content.appendChild(child);
				}
				break;
		}

		return content;
	}

	/**
	 * @param eid int
	 */
	HTMLElementDeclaration createElemDecl(int eid) {
		if (eid == ID_UNKNOWN)
			return null;

		TypePacket packet = new TypePacket();
		switch (eid) {
			case Ids.ID_SCRIPTLET :
				// content, omit, lineBreak, layout, correct, format
				// scriptlet
				packet.name = SCRIPTLET;
				packet.content = CMElementDeclaration.CDATA;
				packet.layout = HTMLElementDeclaration.LAYOUT_OBJECT;
				packet.format = HTMLElementDeclaration.FORMAT_JSP_SCRIPT;
				break;
			case Ids.ID_EXPRESSION :
				// expression
				packet.name = EXPRESSION;
				packet.content = CMElementDeclaration.CDATA;
				packet.layout = HTMLElementDeclaration.LAYOUT_OBJECT;
				packet.format = HTMLElementDeclaration.FORMAT_JSP_SCRIPT;
				break;
			case Ids.ID_DECLARATION :
				// declaration
				packet.name = DECLARATION;
				packet.content = CMElementDeclaration.CDATA;
				packet.layout = HTMLElementDeclaration.LAYOUT_HIDDEN;
				packet.format = HTMLElementDeclaration.FORMAT_JSP_SCRIPT;
				break;
			case Ids.ID_DIRECTIVE_PAGE :
				// directive.page
				packet.name = DIRECTIVE_PAGE;
				packet.omit = HTMLElementDeclaration.OMIT_END;
				packet.layout = HTMLElementDeclaration.LAYOUT_HIDDEN;
				packet.format = HTMLElementDeclaration.FORMAT_JSP_DIRECTIVE;
				break;
			case Ids.ID_DIRECTIVE_INCLUDE :
				// directive.inlcude
				packet.name = DIRECTIVE_INCLUDE;
				packet.omit = HTMLElementDeclaration.OMIT_END;
				packet.layout = HTMLElementDeclaration.LAYOUT_HIDDEN;
				packet.format = HTMLElementDeclaration.FORMAT_JSP_DIRECTIVE;
				break;
			case Ids.ID_DIRECTIVE_TAGLIB :
				// directive.taglib
				packet.name = DIRECTIVE_TAGLIB;
				packet.omit = HTMLElementDeclaration.OMIT_END;
				packet.layout = HTMLElementDeclaration.LAYOUT_HIDDEN;
				packet.format = HTMLElementDeclaration.FORMAT_JSP_DIRECTIVE;
				break;
			case Ids.ID_USEBEAN :
				// useBean
				packet.name = USEBEAN;
				packet.content = CMElementDeclaration.ANY;
				packet.layout = HTMLElementDeclaration.LAYOUT_HIDDEN;
				packet.indentChild = true;
				break;
			case Ids.ID_SETPROPERTY :
				// setProperty
				packet.name = SETPROPERTY;
				packet.omit = HTMLElementDeclaration.OMIT_END;
				packet.layout = HTMLElementDeclaration.LAYOUT_HIDDEN;
				break;
			case Ids.ID_GETPROPERTY :
				// getProperty
				packet.name = GETPROPERTY;
				packet.omit = HTMLElementDeclaration.OMIT_END;
				packet.layout = HTMLElementDeclaration.LAYOUT_OBJECT;
				break;
			case Ids.ID_INCLUDE :
				// include
				packet.name = INCLUDE;
				packet.content = CMElementDeclaration.ELEMENT;
				packet.omit = HTMLElementDeclaration.OMIT_END;
				packet.layout = HTMLElementDeclaration.LAYOUT_OBJECT;
				packet.indentChild = true;
				break;
			case Ids.ID_FORWARD :
				// forward
				packet.name = FORWARD;
				packet.content = CMElementDeclaration.ELEMENT;
				packet.omit = HTMLElementDeclaration.OMIT_END;
				packet.layout = HTMLElementDeclaration.LAYOUT_OBJECT;
				packet.indentChild = true;
				break;
			case Ids.ID_PLUGIN :
				// plugin
				packet.name = PLUGIN;
				packet.content = CMElementDeclaration.ELEMENT;
				packet.layout = HTMLElementDeclaration.LAYOUT_OBJECT;
				packet.indentChild = true;
				break;
			case Ids.ID_PARAMS :
				// params
				packet.name = PARAMS;
				packet.content = CMElementDeclaration.ELEMENT;
				packet.layout = HTMLElementDeclaration.LAYOUT_HIDDEN;
				packet.indentChild = true;
				break;
			case Ids.ID_FALLBACK :
				// fallback
				packet.name = FALLBACK;
				packet.content = CMElementDeclaration.CDATA;
				packet.layout = HTMLElementDeclaration.LAYOUT_HIDDEN;
				break;
			case Ids.ID_PARAM :
				// param
				packet.name = PARAM;
				packet.omit = HTMLElementDeclaration.OMIT_END;
				packet.layout = HTMLElementDeclaration.LAYOUT_HIDDEN;
				break;
			case Ids.ID_ROOT :
				packet.name = ROOT;
				packet.content = CMElementDeclaration.ELEMENT;
				packet.layout = HTMLElementDeclaration.LAYOUT_HIDDEN; // same as HTML
				break;
			case Ids.ID_TEXT :
				packet.name = TEXT;
				packet.content = CMElementDeclaration.PCDATA;
				break;
			default :
				// unknown ID
				return null;
		}

		ElemDecl dec = new ElemDecl(packet);

		CMGroupImpl content = createContent(eid);
		if (content != null)
			dec.setContent(content);

		JACreater creater = new JACreater();
		dec.setAttributes(creater.getDeclarations(eid));

		return dec;
	}
}
