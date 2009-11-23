/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
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

import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

/**
 * Factory for element declarations of JSP 1.1 and JSP 1.2.
 */
class JSP20ElementCollection extends JSPElementCollection implements JSP20Namespace.ElementName {

	// element IDs
	private static class Ids20 extends Ids {
		public static final int ID_BODY = 17;
		public static final int ID_ATTRIBUTE = 18;
		public static final int ID_ELEMENT = 19;
		public static final int ID_OUTPUT = 20;

		public static int getNumOfIds() {
			if (numofids != -1)
				return numofids;

			// NOTE: If the reflection is too slow, this method should
			// just return the literal value, like 105.
			// -- 5/25/2001
			Class clazz = Ids20.class;
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
	class JACreater20 extends JACreater {
		public JACreater20() {
			super();
		}

		public CMNamedNodeMapImpl getDeclarations(int eid) {
			switch (eid) {
				case Ids20.ID_ATTRIBUTE :
					createForAttribute();
					break;
				case Ids20.ID_ELEMENT :
					createForElement();
					break;
				case Ids20.ID_BODY :
					createForBody();
					break;
				case Ids20.ID_OUTPUT :
					createForOutput();
					break;
				case Ids.ID_DIRECTIVE_TAGLIB :
					createForDirTaglib();
					break;
				case Ids.ID_DIRECTIVE_PAGE :
					createForDirPage();
					break;
				case Ids.ID_ROOT :
					createForRoot();
					break;
				case Ids.ID_PLUGIN :
					createForPlugin();
					break;
				default :
					super.getDeclarations(eid);
			}
			return declarations;
		}

		private void createForBody() {
		}

		private void createForOutput() {
			AttrDecl adec = new AttrDecl(JSP20Namespace.ATTR_NAME_OMIT_XML_DECL);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_TRUE, ATTR_VALUE_FALSE, JSP20Namespace.ATTR_VALUE_YES, JSP20Namespace.ATTR_VALUE_NO};
			adec.type.setEnumValues(values);
			adec.type.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, JSP20Namespace.ATTR_VALUE_NO);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_OMIT_XML_DECL, adec);

			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_DOCTYPE_ROOT_ELEMENT);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_DOCTYPE_ROOT_ELEMENT, adec);

			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_DOCTYPE_SYSTEM);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_DOCTYPE_SYSTEM, adec);

			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_DOCTYPE_PUBLIC);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_DOCTYPE_PUBLIC, adec);
		}

		/**
		 * Changed in 2.0
		 */
		void createForDirTaglib() {
			// ("uri" URI OPTIONAL)
			AttrDecl adec = new AttrDecl(ATTR_NAME_URI);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.URI);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(ATTR_NAME_URI, adec);

			// ("tagdir" URI OPTIONAL)
			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_TAGDIR);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.URI);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_TAGDIR, adec);

			// ("prefix" CDATA REQUIRED)
			adec = new AttrDecl(ATTR_NAME_PREFIX);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.REQUIRED;
			declarations.putNamedItem(ATTR_NAME_PREFIX, adec);
		}
		
		void createForDirPage() {
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

			// ("contentType" CDATA DECLARED "text/html; ISO-8859-1")
			adec = new AttrDecl(ATTR_NAME_CONTENT_TYPE);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.type.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_CT_DEFAULT);
			declarations.putNamedItem(ATTR_NAME_CONTENT_TYPE, adec);

			// ("pageEncoding" CDATA IMPLIED)
			adec = new AttrDecl(ATTR_NAME_PAGE_ENCODING);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.type.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, JSP20Namespace.ATTR_VALUE_ENCODING_DEFAULT);
			declarations.putNamedItem(ATTR_NAME_PAGE_ENCODING, adec);

			// ("isELIgnored" ENUM DECLARED (true|false) "false")
			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_ISELIGNORED);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = new String[]{ATTR_VALUE_TRUE, ATTR_VALUE_FALSE};
			adec.type.setEnumValues(values);
			adec.type.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_FALSE);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_ISELIGNORED, adec);
		}
		
		private void createForElement() {
			AttrDecl adec = new AttrDecl(ATTR_NAME_NAME);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.REQUIRED;
			declarations.putNamedItem(ATTR_NAME_NAME, adec);
		}

		private void createForAttribute() {
			AttrDecl adec = new AttrDecl(ATTR_NAME_NAME);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			adec.usage = CMAttributeDeclaration.REQUIRED;
			declarations.putNamedItem(ATTR_NAME_NAME, adec);

			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_TRIM);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_TRUE, ATTR_VALUE_FALSE};
			adec.type.setEnumValues(values);
			adec.type.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_TRUE);
			adec.usage = CMAttributeDeclaration.OPTIONAL;
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_TRIM, adec);
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
			adec.type.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, JSP20Namespace.ATTR_VALUE_JSP_VER_20);
			adec.usage = CMAttributeDeclaration.REQUIRED;
			declarations.putNamedItem(ATTR_NAME_VERSION, adec);
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
			adec.type.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_BOTTOM);
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
			adec.type.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, JSP20Namespace.ATTR_VALUE_JVER12);
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
			
			// ("mayscript" ENUM IMPLIED (true | false)
			adec = new AttrDecl(JSP20Namespace.ATTR_NAME_MAYSCRIPT);
			adec.type = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			adec.type.setEnumValues(new String[] {ATTR_VALUE_TRUE, ATTR_VALUE_FALSE});
			declarations.putNamedItem(JSP20Namespace.ATTR_NAME_MAYSCRIPT, adec);
		}
	}

	private static String[] names = null;

	static {
		names = new String[Ids20.getNumOfIds()];
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
		names[Ids20.ID_BODY] = JSP20Namespace.ElementName.BODY;
		names[Ids20.ID_ATTRIBUTE] = JSP20Namespace.ElementName.ATTRIBUTE;
		names[Ids20.ID_ELEMENT] = JSP20Namespace.ElementName.ELEMENT;
		names[Ids20.ID_OUTPUT] = JSP20Namespace.ElementName.OUTPUT;
	}

	/**
	 */
	public JSP20ElementCollection() {
		super(JSP20ElementCollection.names, TOLERANT_CASE);
	}

	/**
	 * @param eid
	 *            int
	 */
	CMGroupImpl createContent(int eid) {
		if (eid == ID_UNKNOWN)
			return null;

		CMGroupImpl content = null;
		CMNode child = null;

		switch (eid) {
			case Ids.ID_ROOT :
				content = new CMGroupImpl(CMGroup.CHOICE, 0, CMContentImpl.UNBOUNDED);
				int validChildren[] = {
				// %Directives;
							Ids.ID_TEXT, Ids.ID_DIRECTIVE_PAGE, Ids.ID_DIRECTIVE_INCLUDE, Ids20.ID_BODY, Ids20.ID_ATTRIBUTE,
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
			default :
				content = super.createContent(eid);
				break;
		}

		return content;
	}

	/**
	 * @param eid
	 *            int
	 */
	HTMLElementDeclaration createElemDecl(int eid) {
		if (eid == ID_UNKNOWN)
			return null;

		TypePacket packet = new TypePacket();
		switch (eid) {
			case Ids20.ID_BODY :
				// declaration
				packet.name = JSP20Namespace.ElementName.BODY;
				packet.content = CMElementDeclaration.CDATA;
				packet.layout = HTMLElementDeclaration.LAYOUT_OBJECT;
				packet.indentChild = true;
				packet.format = HTMLElementDeclaration.FORMAT_JSP_SCRIPT;
				break;
			case Ids20.ID_ELEMENT :
				// declaration
				packet.name = JSP20Namespace.ElementName.ELEMENT;
				packet.content = CMElementDeclaration.CDATA;
				packet.layout = HTMLElementDeclaration.LAYOUT_OBJECT;
				packet.indentChild = true;
				packet.format = HTMLElementDeclaration.FORMAT_JSP_SCRIPT;
				break;
			case Ids20.ID_ATTRIBUTE :
				// declaration
				packet.name = JSP20Namespace.ElementName.ATTRIBUTE;
				packet.content = CMElementDeclaration.CDATA;
				packet.layout = HTMLElementDeclaration.LAYOUT_OBJECT;
				packet.indentChild = true;
				packet.format = HTMLElementDeclaration.FORMAT_JSP_SCRIPT;
				break;
			case Ids20.ID_OUTPUT :
				// declaration
				packet.name = JSP20Namespace.ElementName.OUTPUT;
				packet.content = CMElementDeclaration.CDATA;
				packet.layout = HTMLElementDeclaration.LAYOUT_OBJECT;
				packet.indentChild = true;
				packet.format = HTMLElementDeclaration.FORMAT_JSP_SCRIPT;
				break;
			case Ids.ID_DIRECTIVE_TAGLIB :
				// directive.taglib
				packet.name = DIRECTIVE_TAGLIB;
				packet.omit = HTMLElementDeclaration.OMIT_END;
				packet.layout = HTMLElementDeclaration.LAYOUT_HIDDEN;
				packet.format = HTMLElementDeclaration.FORMAT_JSP_DIRECTIVE;
				break;
			case Ids.ID_DIRECTIVE_PAGE :
				// directive.page
				packet.name = DIRECTIVE_PAGE;
				packet.omit = HTMLElementDeclaration.OMIT_END;
				packet.layout = HTMLElementDeclaration.LAYOUT_HIDDEN;
				packet.format = HTMLElementDeclaration.FORMAT_JSP_DIRECTIVE;
				break;
			case Ids.ID_ROOT :
				packet.name = ROOT;
				packet.content = CMElementDeclaration.ELEMENT;
				packet.layout = HTMLElementDeclaration.LAYOUT_HIDDEN; // same as HTML
				break;
			case Ids.ID_PLUGIN :
				// plugin
				packet.name = PLUGIN;
				packet.content = CMElementDeclaration.ELEMENT;
				packet.layout = HTMLElementDeclaration.LAYOUT_OBJECT;
				packet.indentChild = true;
				break;
			default :
				return super.createElemDecl(eid);
		}
		ElemDecl decl = new ElemDecl(packet);

		CMGroupImpl content = createContent(eid);
		if (content != null)
			decl.setContent(content);

		JACreater20 creater = getAttributeCreator();
		decl.setAttributes(creater.getDeclarations(eid));

		return decl;
	}

	protected JACreater20 getAttributeCreator() {
		return new JACreater20();
	}
}
