/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.validate;

import java.text.MessageFormat;
import java.util.Hashtable;

import org.eclipse.wst.html.core.internal.HTMLCorePlugin;
import org.eclipse.wst.html.core.internal.Logger;
import org.eclipse.wst.sse.core.validate.ValidationMessage;
import org.w3c.dom.Node;

class MessageFactory implements ErrorState {


	private static class ErrorTable {
		private class Packet {
			public Packet(String msg, int severity) {
				this.msg = msg;
				this.severity = severity;
			}

			public String getMessage() {
				return msg;
			}

			public int getSeverity() {
				return severity;
			}

			private String msg = null;
			private int severity = -1;
		}

		public ErrorTable() {
		}

		public void put(int state, String msg, int severity) {
			Packet packet = new Packet(msg, severity);
			map.put(new Integer(state), packet);
		}

		public String getMessage(int state) {
			return getPacket(state).getMessage();
		}

		public int getSeverity(int state) {
			return getPacket(state).getSeverity();
		}

		private Packet getPacket(int state) {
			return (Packet) map.get(new Integer(state));
		}

		private Hashtable map = new Hashtable();
	}

	private static interface NodeType {
		static final int ATTRIBUTE = 0;
		static final int ELEMENT = 1;
		static final int DOCUMENT_TYPE = 2;
		static final int TEXT = 3;
		static final int COMMENT = 4;
		static final int CDATA_SECTION = 5;
		static final int PROCESSING_INSTRUCTION = 6;
		static final int ENTITY_REFERENCE = 7;

		static final int MAX_TYPE = 8;
	}

	// error messages
	private static final String MSG_NO_ERROR = HTMLCorePlugin.getResourceString("%No_error._UI_"); //$NON-NLS-1$ = "No error."
	private static final String MSG_UNDEFINED_ATTR_ERROR = HTMLCorePlugin.getResourceString("%Undefined_attribute_name_(_ERROR_"); //$NON-NLS-1$ = "Undefined attribute name ({0})."
	private static final String MSG_UNDEFINED_VALUE_ERROR = HTMLCorePlugin.getResourceString("%Undefined_attribute_value__ERROR_"); //$NON-NLS-1$ = "Undefined attribute value ({0})."
	private static final String MSG_DUPLICATE_ATTR_ERROR = HTMLCorePlugin.getResourceString("%Multiple_values_specified__ERROR_"); //$NON-NLS-1$ = "Multiple values specified for an attribute ({0})."
	private static final String MSG_MISMATCHED_ATTR_ERROR = HTMLCorePlugin.getResourceString("%Attribute_name_({0})_uses__ERROR_"); //$NON-NLS-1$ = "Attribute name ({0}) uses wrong case character."
	private static final String MSG_INVALID_ATTR_ERROR = HTMLCorePlugin.getResourceString("%Invalid_attribute_name_({0_ERROR_"); //$NON-NLS-1$ = "Invalid attribute name ({0})."
	private static final String MSG_ATTR_NO_VALUE_ERROR = HTMLCorePlugin.getResourceString("%Invalid_attribute_({0})._ERROR_"); //$NON-NLS-1$ = "Invalid attribute ({0})."
	private static final String MSG_INVALID_CONTENT_ERROR = HTMLCorePlugin.getResourceString("%Invalid_location_of_tag_({_ERROR_"); //$NON-NLS-1$ = "Invalid location of tag ({0})."
	private static final String MSG_DUPLICATE_TAG_ERROR = HTMLCorePlugin.getResourceString("%Duplicate_tag_({0})._ERROR_"); //$NON-NLS-1$ = "Duplicate tag ({0})."
	private static final String MSG_MISSING_START_TAG_ERROR = HTMLCorePlugin.getResourceString("%No_start_tag_(<{0}>)._ERROR_"); //$NON-NLS-1$ = "No start tag (<{0}>)."
	private static final String MSG_MISSING_END_TAG_ERROR = HTMLCorePlugin.getResourceString("%No_end_tag_(</{0}>)._ERROR_"); //$NON-NLS-1$ = "No end tag (</{0}>)."
	private static final String MSG_UNNECESSARY_END_TAG_ERROR = HTMLCorePlugin.getResourceString("%End_tag_(</{0}>)_not_neede_ERROR_"); //$NON-NLS-1$ = "End tag (</{0}>) not needed."
	private static final String MSG_UNDEFINED_TAG_ERROR = HTMLCorePlugin.getResourceString("%Unknown_tag_({0})._ERROR_"); //$NON-NLS-1$ = "Unknown tag ({0})."
	private static final String MSG_MISMATCHED_TAG_ERROR = HTMLCorePlugin.getResourceString("%Tag_name_({0})_uses_wrong__ERROR_"); //$NON-NLS-1$ = "Tag name ({0}) uses wrong case character."
	private static final String MSG_INVALID_TAG_ERROR = HTMLCorePlugin.getResourceString("%Invalid_tag_name_({0})._ERROR_"); //$NON-NLS-1$ = "Invalid tag name ({0})."
	private static final String MSG_INVALID_DIRECTIVE_ERROR = HTMLCorePlugin.getResourceString("%Invalid_JSP_directive_({0}_ERROR_"); //$NON-NLS-1$ = "Invalid JSP directive ({0})."
	private static final String MSG_INVALID_TEXT_ERROR = HTMLCorePlugin.getResourceString("%Invalid_text_string_({0})._ERROR_"); //$NON-NLS-1$ = "Invalid text string ({0})."
	private static final String MSG_INVALID_CHAR_ERROR = HTMLCorePlugin.getResourceString("%Invalid_character_used_in__ERROR_"); //$NON-NLS-1$ = "Invalid character used in text string ({0})."
	private static final String MSG_UNKNOWN_ERROR = HTMLCorePlugin.getResourceString("%Unknown_error._ERROR_"); //$NON-NLS-1$ = "Unknown error."
	private static final String MSG_UNCLOSED_START_TAG_ERROR = HTMLCorePlugin.getResourceString("%Start_tag_(<{0}>)_not_clos_ERROR_"); //$NON-NLS-1$ = "Start tag (<{0}>) not closed."
	private static final String MSG_UNCLOSED_END_TAG_ERROR = HTMLCorePlugin.getResourceString("%End_tag_(</{0}>)_not_close_ERROR_"); //$NON-NLS-1$ = "End tag (</{0}>) not closed."
	private static final String MSG_UNCLOSED_TAG_ERROR = HTMLCorePlugin.getResourceString("%Tag_({0})_not_closed._ERROR_"); //$NON-NLS-1$ = "Tag ({0}) not closed."
	private static final String MSG_MISMATCHED_ATTR_VALUE_ERROR = HTMLCorePlugin.getResourceString("%Attribute_value_({0})_uses_ERROR_"); //$NON-NLS-1$ = "Attribute value ({0}) uses wrong case character."
	private static final String MSG_UNCLOSED_COMMENT_ERROR = HTMLCorePlugin.getResourceString("%Comment_not_closed._ERROR_"); //$NON-NLS-1$ = "Comment not closed."
	private static final String MSG_UNCLOSED_DOCTYPE_ERROR = HTMLCorePlugin.getResourceString("%DOCTYPE_declaration_not_cl_ERROR_"); //$NON-NLS-1$ = "DOCTYPE declaration not closed."
	private static final String MSG_UNCLOSED_PI_ERROR = HTMLCorePlugin.getResourceString("%Processing_instruction_not_ERROR_"); //$NON-NLS-1$ = "Processing instruction not closed."
	private static final String MSG_UNCLOSED_CDATA_SECTION_ERROR = HTMLCorePlugin.getResourceString("%CDATA_section_not_closed._ERROR_"); //$NON-NLS-1$ = "CDATA section not closed."
	private static final String MSG_INVALID_EMPTY_ELEMENT_TAG = HTMLCorePlugin.getResourceString("%_ERROR_Tag_({0})_should_be_an_empty-element_tag_1"); //$NON-NLS-1$ = "Tag ({0}) should be an empty-element tag."
	private static final String MSG_UNCLOSED_ATTR_VALUE_ERROR = HTMLCorePlugin.getResourceString("%_ERROR_Attribute_value_({0})_not_closed._1"); //$NON-NLS-1$ ="Attribute value ({0}) not closed."
	private static ErrorTable[] errTables = new ErrorTable[NodeType.MAX_TYPE];

	static {
		for (int i = 0; i < NodeType.MAX_TYPE; i++) {
			errTables[i] = new ErrorTable();
		}
		// NOTE: The severities are just stub.  They must be reviewed.
		// -- 8/30/2001

		// attribute error map
		ErrorTable attrTable = errTables[NodeType.ATTRIBUTE];// short hand
		attrTable.put(NONE_ERROR, MSG_NO_ERROR, 0);
		attrTable.put(UNDEFINED_NAME_ERROR, MSG_UNDEFINED_ATTR_ERROR, ValidationMessage.WARNING);
		attrTable.put(UNDEFINED_VALUE_ERROR, MSG_UNDEFINED_VALUE_ERROR, ValidationMessage.WARNING);
		attrTable.put(MISMATCHED_ERROR, MSG_MISMATCHED_ATTR_ERROR, ValidationMessage.WARNING);
		attrTable.put(INVALID_NAME_ERROR, MSG_INVALID_ATTR_ERROR, ValidationMessage.WARNING);
		attrTable.put(INVALID_ATTR_ERROR, MSG_ATTR_NO_VALUE_ERROR, ValidationMessage.WARNING);
		attrTable.put(DUPLICATE_ERROR, MSG_DUPLICATE_ATTR_ERROR, ValidationMessage.WARNING);
		attrTable.put(MISMATCHED_VALUE_ERROR, MSG_MISMATCHED_ATTR_VALUE_ERROR, ValidationMessage.ERROR);
		//<<D210422
		attrTable.put(UNCLOSED_ATTR_VALUE, MSG_UNCLOSED_ATTR_VALUE_ERROR, ValidationMessage.WARNING);
		//D210422
		// element error map
		ErrorTable elemTable = errTables[NodeType.ELEMENT];// short hand
		elemTable.put(NONE_ERROR, MSG_NO_ERROR, 0);
		elemTable.put(UNDEFINED_NAME_ERROR, MSG_UNDEFINED_TAG_ERROR, ValidationMessage.WARNING);
		elemTable.put(INVALID_NAME_ERROR, MSG_INVALID_TAG_ERROR, ValidationMessage.ERROR);
		elemTable.put(MISMATCHED_ERROR, MSG_MISMATCHED_TAG_ERROR, ValidationMessage.WARNING);
		elemTable.put(MISMATCHED_END_TAG_ERROR, MSG_MISMATCHED_TAG_ERROR, ValidationMessage.ERROR);
		elemTable.put(MISSING_START_TAG_ERROR, MSG_MISSING_START_TAG_ERROR, ValidationMessage.ERROR);
		elemTable.put(MISSING_END_TAG_ERROR, MSG_MISSING_END_TAG_ERROR, ValidationMessage.WARNING);
		elemTable.put(UNNECESSARY_END_TAG_ERROR, MSG_UNNECESSARY_END_TAG_ERROR, ValidationMessage.WARNING);
		elemTable.put(INVALID_DIRECTIVE_ERROR, MSG_INVALID_DIRECTIVE_ERROR, ValidationMessage.ERROR);
		elemTable.put(INVALID_CONTENT_ERROR, MSG_INVALID_CONTENT_ERROR, ValidationMessage.WARNING);
		elemTable.put(DUPLICATE_ERROR, MSG_DUPLICATE_TAG_ERROR, ValidationMessage.WARNING);
		elemTable.put(COEXISTENCE_ERROR, MSG_INVALID_CONTENT_ERROR, ValidationMessage.WARNING);
		elemTable.put(UNCLOSED_TAG_ERROR, MSG_UNCLOSED_START_TAG_ERROR, ValidationMessage.ERROR);
		elemTable.put(UNCLOSED_END_TAG_ERROR, MSG_UNCLOSED_END_TAG_ERROR, ValidationMessage.ERROR);
		elemTable.put(INVALID_EMPTY_ELEMENT_TAG, MSG_INVALID_EMPTY_ELEMENT_TAG, ValidationMessage.WARNING);

		// document type error map
		ErrorTable docTable = errTables[NodeType.DOCUMENT_TYPE];// short hand
		docTable.put(NONE_ERROR, MSG_NO_ERROR, 0);
		docTable.put(DUPLICATE_ERROR, MSG_DUPLICATE_TAG_ERROR, ValidationMessage.ERROR);
		docTable.put(INVALID_CONTENT_ERROR, MSG_INVALID_CONTENT_ERROR, ValidationMessage.WARNING);
		docTable.put(UNCLOSED_TAG_ERROR, MSG_UNCLOSED_DOCTYPE_ERROR, ValidationMessage.ERROR);

		// text error map
		ErrorTable textTable = errTables[NodeType.TEXT];
		textTable.put(NONE_ERROR, MSG_NO_ERROR, 0);
		textTable.put(INVALID_CONTENT_ERROR, MSG_INVALID_TEXT_ERROR, ValidationMessage.WARNING);
		textTable.put(INVALID_CHAR_ERROR, MSG_INVALID_CHAR_ERROR, ValidationMessage.WARNING);

		// comment error map
		ErrorTable commTable = errTables[NodeType.COMMENT];
		commTable.put(NONE_ERROR, MSG_NO_ERROR, 0);
		commTable.put(INVALID_CONTENT_ERROR, MSG_INVALID_CONTENT_ERROR, ValidationMessage.WARNING);
		commTable.put(UNCLOSED_TAG_ERROR, MSG_UNCLOSED_COMMENT_ERROR, ValidationMessage.ERROR);

		// cdata section error map
		ErrorTable cdatTable = errTables[NodeType.CDATA_SECTION];
		cdatTable.put(NONE_ERROR, MSG_NO_ERROR, 0);
		cdatTable.put(INVALID_CONTENT_ERROR, MSG_INVALID_CONTENT_ERROR, ValidationMessage.WARNING);
		cdatTable.put(UNCLOSED_TAG_ERROR, MSG_UNCLOSED_CDATA_SECTION_ERROR, ValidationMessage.ERROR);

		// processing instruction error map
		ErrorTable piTable = errTables[NodeType.PROCESSING_INSTRUCTION];
		piTable.put(NONE_ERROR, MSG_NO_ERROR, 0);
		piTable.put(INVALID_CONTENT_ERROR, MSG_INVALID_CONTENT_ERROR, ValidationMessage.WARNING);
		piTable.put(UNCLOSED_TAG_ERROR, MSG_UNCLOSED_PI_ERROR, ValidationMessage.ERROR);

		// entity reference error map
		ErrorTable erTable = errTables[NodeType.ENTITY_REFERENCE];
		erTable.put(NONE_ERROR, MSG_NO_ERROR, 0);
		erTable.put(UNDEFINED_NAME_ERROR, MSG_UNDEFINED_TAG_ERROR, ValidationMessage.WARNING);
		erTable.put(INVALID_CONTENT_ERROR, MSG_INVALID_CONTENT_ERROR, ValidationMessage.WARNING);
	}

	/**
	 */
	public static ValidationMessage createMessage(ErrorInfo info) {
		String errorMsg = getErrorMessage(info);
		int errorSeverity = getErrorSeverity(info);
		return new ValidationMessage(errorMsg, info.getOffset(), info.getLength(), errorSeverity);
	}

	private static String getErrorMessage(ErrorInfo info) {
		ErrorTable tab = getErrorTable(info.getTargetType());
		if (tab == null)
			return MSG_UNKNOWN_ERROR;

		String template = tab.getMessage(info.getState());
		Object[] arguments = {info.getHint()};
		String s = null;
		try {
			s = MessageFormat.format(template, arguments);
		}
		catch (IllegalArgumentException e) {
			Logger.logException(e);
			s = template + ":" + arguments.toString(); //$NON-NLS-1$
		}
		return s;
	}

	/**
	 */
	private static int getErrorSeverity(ErrorInfo info) {
		ErrorTable tab = getErrorTable(info.getTargetType());
		if (tab == null)
			return 0;
		return tab.getSeverity(info.getState());
	}

	private static ErrorTable getErrorTable(short nodetype) {
		ErrorTable tab = null;
		switch (nodetype) {
			case Node.ATTRIBUTE_NODE :
				tab = errTables[NodeType.ATTRIBUTE];
				break;
			case Node.ELEMENT_NODE :
				tab = errTables[NodeType.ELEMENT];
				break;
			case Node.DOCUMENT_TYPE_NODE :
				tab = errTables[NodeType.DOCUMENT_TYPE];
				break;
			case Node.TEXT_NODE :
				tab = errTables[NodeType.TEXT];
				break;
			case Node.COMMENT_NODE :
				tab = errTables[NodeType.COMMENT];
				break;
			case Node.CDATA_SECTION_NODE :
				tab = errTables[NodeType.CDATA_SECTION];
				break;
			case Node.PROCESSING_INSTRUCTION_NODE :
				tab = errTables[NodeType.PROCESSING_INSTRUCTION];
				break;
			case Node.ENTITY_REFERENCE_NODE :
				tab = errTables[NodeType.ENTITY_REFERENCE];
				break;
			default :
				return null;
		}
		return tab;
	}
}