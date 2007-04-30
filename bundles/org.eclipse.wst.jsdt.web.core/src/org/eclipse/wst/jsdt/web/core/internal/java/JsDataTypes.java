package org.eclipse.wst.jsdt.web.core.internal.java;

import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.html.core.text.IHTMLPartitions;

public interface JsDataTypes extends HTML40Namespace {
	/*
	 * remove when when we refactor (need to add this content type to many
	 * project types in wst)
	 */

	public static final String BASE_FILE_EXTENSION = ".js";

	public static String[] CONSTANTS = { "false", "null", "true" }; //$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$

	public static final String[] EVENTS = { HTML40Namespace.ATTR_NAME_ONCLICK, HTML40Namespace.ATTR_NAME_ONDBLCLICK, HTML40Namespace.ATTR_NAME_ONMOUSEDOWN,
			HTML40Namespace.ATTR_NAME_ONMOUSEUP, HTML40Namespace.ATTR_NAME_ONMOUSEOVER, HTML40Namespace.ATTR_NAME_ONMOUSEMOVE,
			HTML40Namespace.ATTR_NAME_ONMOUSEOUT, HTML40Namespace.ATTR_NAME_ONKEYPRESS, HTML40Namespace.ATTR_NAME_ONKEYDOWN, HTML40Namespace.ATTR_NAME_ONKEYUP,
			HTML40Namespace.ATTR_NAME_ONHELP };

	public static final String[] HTMLATREVENTS = { "onload ", "onunload", "onclick",

	"onmousedown", "onmouseup", "onmouseover", "onmousemove", "onmouseout", "onfocus", "onblur", "onkeypress", "onkeydown", "onkeyup", "onsubmit", "onreset",
			"onselect", "onchange", };

	public static final String[] JSVALIDDATATYPES = { "JAVASCRIPT", "TEXT/JAVASCRIPT" }; //$NON-NLS-1$

	public static String[] KEYWORDS = { "abstract",

	"break",

	"case", "catch",

	"class", "const", "continue", "debugger", "default", "delete", "do",

	"else", "enum", "export", "extends",

	"final", "finally",

	"for", "function", "goto", "if", "implements", "import", "in", "instanceof",

	"interface",

	"native", "new",

	"package", "private", "protected", "public", "return",

	"static", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "typeof", "volatile", "while", "with" }; //$NON-NLS-1$

	// public static final String
	// NEW_PARTITION_TYPE="org.eclipse.wst.jsdt.StructuredJs";
	// public static final String NEW_PARTITION_TYPE=IHTMLPartitions.SCRIPT;
	public static final String NEW_PARTITION_TYPE = IHTMLPartitions.SCRIPT;

	public static final String[] TAKEOVER_PARTITION_TYPES = { "none" };

	public static String[] TYPES = { "boolean", "byte", "char", "double", "int", "long", "short", "float", "var", "void" }; //$NON-NLS-1$

}
