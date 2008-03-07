package org.eclipse.wst.xsl.internal.ui.contentassist;

import org.apache.xpath.compiler.Keywords;
import org.apache.xpath.functions.Function;

import java.lang.reflect.Array;
import java.util.HashMap;
import javax.xml.transform.TransformerException;

/**
 * 
 * @author dcarver
 *
 */
public class XPathFunctions {

	/** The 'current()' id. */
	public static final int FUNC_CURRENT = 0;

	/** The 'last()' id. */
	public static final int FUNC_LAST = 1;

	/** The 'position()' id. */
	public static final int FUNC_POSITION = 2;

	/** The 'count()' id. */
	public static final int FUNC_COUNT = 3;

	/** The 'id()' id. */
	public static final int FUNC_ID = 4;

	/** The 'key()' id (XSLT). */
	public static final int FUNC_KEY = 5;

	/** The 'local-name()' id. */
	public static final int FUNC_LOCAL_PART = 7;

	/** The 'namespace-uri()' id. */
	public static final int FUNC_NAMESPACE = 8;

	/** The 'name()' id. */
	public static final int FUNC_QNAME = 9;

	/** The 'generate-id()' id. */
	public static final int FUNC_GENERATE_ID = 10;

	/** The 'not()' id. */
	public static final int FUNC_NOT = 11;

	/** The 'true()' id. */
	public static final int FUNC_TRUE = 12;

	/** The 'false()' id. */
	public static final int FUNC_FALSE = 13;

	/** The 'boolean()' id. */
	public static final int FUNC_BOOLEAN = 14;

	/** The 'number()' id. */
	public static final int FUNC_NUMBER = 15;

	/** The 'floor()' id. */
	public static final int FUNC_FLOOR = 16;

	/** The 'ceiling()' id. */
	public static final int FUNC_CEILING = 17;

	/** The 'round()' id. */
	public static final int FUNC_ROUND = 18;

	/** The 'sum()' id. */
	public static final int FUNC_SUM = 19;

	/** The 'string()' id. */
	public static final int FUNC_STRING = 20;

	/** The 'starts-with()' id. */
	public static final int FUNC_STARTS_WITH = 21;

	/** The 'contains()' id. */
	public static final int FUNC_CONTAINS = 22;

	/** The 'substring-before()' id. */
	public static final int FUNC_SUBSTRING_BEFORE = 23;

	/** The 'substring-after()' id. */
	public static final int FUNC_SUBSTRING_AFTER = 24;

	/** The 'normalize-space()' id. */
	public static final int FUNC_NORMALIZE_SPACE = 25;

	/** The 'translate()' id. */
	public static final int FUNC_TRANSLATE = 26;

	/** The 'concat()' id. */
	public static final int FUNC_CONCAT = 27;

	/** The 'substring()' id. */
	public static final int FUNC_SUBSTRING = 29;

	/** The 'string-length()' id. */
	public static final int FUNC_STRING_LENGTH = 30;

	/** The 'system-property()' id. */
	public static final int FUNC_SYSTEM_PROPERTY = 31;

	/** The 'lang()' id. */
	public static final int FUNC_LANG = 32;

	/** The 'function-available()' id (XSLT). */
	public static final int FUNC_EXT_FUNCTION_AVAILABLE = 33;

	/** The 'element-available()' id (XSLT). */
	public static final int FUNC_EXT_ELEM_AVAILABLE = 34;

	/** The 'unparsed-entity-uri()' id (XSLT). */
	public static final int FUNC_UNPARSED_ENTITY_URI = 36;

	// Proprietary

	/** The 'document-location()' id (Proprietary). */
	public static final int FUNC_DOCLOCATION = 35;

	/**
	 * Number of built in functions. Be sure to update this as built-in
	 * functions are added.
	 */
	private static final int NUM_BUILT_IN_FUNCS = 37;
	
	/** Table of function name to function ID associations. */
	private static String[] m_functionID = new String[NUM_BUILT_IN_FUNCS];

	
	/**
	 * The function table contains customized functions
	 */
	private Class m_functions_customer[] = new Class[NUM_ALLOWABLE_ADDINS];

	/**
	 * Table of function name to function ID associations for customized
	 * functions
	 */
	private Array m_functionID_customer = null;


	/**
	 * Number of built-in functions that may be added.
	 */
	private static final int NUM_ALLOWABLE_ADDINS = 30;

	/**
	 * The index to the next free function index.
	 */
	private int m_funcNextFreeIndex = NUM_BUILT_IN_FUNCS;



	/**
	 * 
	 */
	public XPathFunctions() {
		m_functionID[XPathFunctions.FUNC_CURRENT] = Keywords.FUNC_CURRENT_STRING;
		m_functionID[XPathFunctions.FUNC_LAST] = Keywords.FUNC_LAST_STRING;
		m_functionID[XPathFunctions.FUNC_POSITION] = Keywords.FUNC_POSITION_STRING;
		m_functionID[XPathFunctions.FUNC_COUNT] = Keywords.FUNC_COUNT_STRING;
		m_functionID[XPathFunctions.FUNC_ID] = Keywords.FUNC_GENERATE_ID_STRING;
		m_functionID[XPathFunctions.FUNC_KEY] = Keywords.FUNC_KEY_STRING;
		m_functionID[XPathFunctions.FUNC_LOCAL_PART] = Keywords.FUNC_LOCAL_PART_STRING;
		m_functionID[XPathFunctions.FUNC_NAMESPACE] = Keywords.FUNC_NAMESPACE_STRING;
		m_functionID[XPathFunctions.FUNC_QNAME] = Keywords.FUNC_NAME_STRING;
		m_functionID[XPathFunctions.FUNC_GENERATE_ID] = Keywords.FUNC_GENERATE_ID_STRING;
		m_functionID[XPathFunctions.FUNC_NOT] = Keywords.FUNC_NOT_STRING;
		m_functionID[XPathFunctions.FUNC_TRUE] = Keywords.FUNC_TRUE_STRING;
		m_functionID[XPathFunctions.FUNC_FALSE] = Keywords.FUNC_FALSE_STRING;
		m_functionID[XPathFunctions.FUNC_BOOLEAN] = Keywords.FUNC_BOOLEAN_STRING;
		m_functionID[XPathFunctions.FUNC_LANG] = Keywords.FUNC_LANG_STRING;
		m_functionID[XPathFunctions.FUNC_NUMBER] = Keywords.FUNC_NUMBER_STRING;
		m_functionID[XPathFunctions.FUNC_FLOOR] = Keywords.FUNC_FLOOR_STRING;
		m_functionID[XPathFunctions.FUNC_CEILING] = Keywords.FUNC_CEILING_STRING;
		m_functionID[XPathFunctions.FUNC_ROUND] = Keywords.FUNC_ROUND_STRING;
		m_functionID[XPathFunctions.FUNC_SUM] = Keywords.FUNC_SUM_STRING;
		m_functionID[XPathFunctions.FUNC_STRING] = Keywords.FUNC_STRING_STRING;
		m_functionID[XPathFunctions.FUNC_STARTS_WITH] = Keywords.FUNC_STARTS_WITH_STRING;
		m_functionID[XPathFunctions.FUNC_CONTAINS] = Keywords.FUNC_CONTAINS_STRING;
		m_functionID[XPathFunctions.FUNC_SUBSTRING_BEFORE] = Keywords.FUNC_SUBSTRING_BEFORE_STRING;
		m_functionID[XPathFunctions.FUNC_SUBSTRING_AFTER] = Keywords.FUNC_SUBSTRING_AFTER_STRING;
		m_functionID[XPathFunctions.FUNC_NORMALIZE_SPACE] = Keywords.FUNC_NORMALIZE_SPACE_STRING;
		m_functionID[XPathFunctions.FUNC_TRANSLATE] = Keywords.FUNC_TRANSLATE_STRING;
		m_functionID[XPathFunctions.FUNC_CONCAT] = Keywords.FUNC_CONCAT_STRING;
		m_functionID[XPathFunctions.FUNC_SYSTEM_PROPERTY] = Keywords.FUNC_SYSTEM_PROPERTY_STRING;
		m_functionID[XPathFunctions.FUNC_EXT_FUNCTION_AVAILABLE] = Keywords.FUNC_EXT_FUNCTION_AVAILABLE_STRING;
		m_functionID[XPathFunctions.FUNC_EXT_ELEM_AVAILABLE] = Keywords.FUNC_EXT_ELEM_AVAILABLE_STRING;
		m_functionID[XPathFunctions.FUNC_SUBSTRING] = Keywords.FUNC_SUBSTRING_STRING;
		m_functionID[XPathFunctions.FUNC_STRING_LENGTH] = Keywords.FUNC_STRING_LENGTH_STRING;
		m_functionID[XPathFunctions.FUNC_UNPARSED_ENTITY_URI] = Keywords.FUNC_UNPARSED_ENTITY_URI_STRING;
		m_functionID[XPathFunctions.FUNC_DOCLOCATION] = Keywords.FUNC_DOCLOCATION_STRING;
	}

	/**
	 * Return the name of the a function in the static table. Needed to avoid
	 * making the table publicly available.
	 * 
	 * @param funcID
	 * @return
	 */
	public String getFunctionName(int funcID) {
		if (funcID < NUM_BUILT_IN_FUNCS)
			return m_functionID[funcID];
		else
			return m_functions_customer[funcID - NUM_BUILT_IN_FUNCS].getName();
	}

	
	/**
	 * Returns the size of the Function built in Function Table.
	 * 
	 * @return
	 */
	public int size() {
		return m_functionID.length;
	}
}
