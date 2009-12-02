/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0 
 *     Jesper Moller- bug 281159 - debugging convenience toString method 
 *     David Carver (STAR) - bug 288886 - add unit tests and fix fn:resolve-qname function
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.types;

import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.function.*;

/**
 * A representation of a QName datatype (name of a node)
 */
public class QName extends CtrType implements CmpEq {
	private String _namespace;
	private String _prefix;
	private String _local_part;
	private boolean _expanded;

	/**
	 * Initialises using the supplied parameters
	 * 
	 * @param prefix
	 *            Prefix of the node name
	 * @param local_part
	 *            The node name itself
	 * @param ns
	 *            The namespace this nodename belongs to
	 */
	public QName(String prefix, String local_part, String ns) {
		this(prefix, local_part);
		if (ns != null) {
			set_namespace(ns);
		}
	}

	/**
	 * Initialises using the supplied parameters
	 * 
	 * @param prefix
	 *            Prefix of the node name
	 * @param local_part
	 *            The node name itself
	 */
	public QName(String prefix, String local_part) {
		_prefix = prefix;
		_local_part = local_part;
		_expanded = false;
	}

	/**
	 * Initialises using only the node name (no prefix)
	 * 
	 * @param local_part
	 *            The node name
	 */
	public QName(String local_part) {
		this(null, local_part);
		set_namespace(null);
	}

	/**
	 * Initialises with a null prefix and null node name
	 */
	public QName() {
		this(null, null);
	}

	/**
	 * Creates a new QName by parsing a String representation of the node name
	 * 
	 * @param str
	 *            String representation of the name
	 * @return null
	 */
	public static QName parse_QName(String str) {
		int occurs = 0;
		for (Character ch : str.toCharArray()) {
			if (ch == ':') {
				occurs +=1;
			}
		}
		
		if (occurs > 1) {
			return null;
		}
		
		String[] tokens = str.split(":");

		if (tokens.length == 1)
			return new QName(tokens[0]);
		
		if (tokens.length == 2) {
				return new QName(tokens[0], tokens[1]);
		}

		return null;
	}

	/**
	 * Creates a new ResultSequence consisting of the extractable QName in the
	 * supplied ResultSequence
	 * 
	 * @param arg
	 *            The ResultSequence to extract from
	 * @return New ResultSequence consisting of the QName supplied
	 * @throws DynamicError
	 */
	@Override
	public ResultSequence constructor(ResultSequence arg) throws DynamicError {
		ResultSequence rs = ResultSequenceFactory.create_new();

		if (arg.empty())
			DynamicError.throw_type_error();

		AnyAtomicType aat = (AnyAtomicType) arg.first();

		if (!(aat instanceof XSString) && !(aat instanceof QName))
			DynamicError.throw_type_error();
		
		String sarg = aat.string_value();

		QName qname = parse_QName(sarg);
		if (qname == null)
			return null;
		rs.add(qname);

		return rs;
	}

	/**
	 * Retrieves a String representation of the node name. This method is
	 * functionally identical to string()
	 * 
	 * @return String representation of the node name
	 */
	@Override
	public String string_value() {
		return string();
	}

	/**
	 * Retrieves the datatype's full pathname
	 * 
	 * @return "xs:QName" which is the datatype's full pathname
	 */
	@Override
	public String string_type() {
		return "xs:QName";
	}

	/**
	 * Retrieves the datatype's name
	 * 
	 * @return "QName" which is the datatype's name
	 */
	@Override
	public String type_name() {
		return "QName";
	}

	/**
	 * Retrieves a String representation of the node name. This method is
	 * functionally identical to string_value()
	 * 
	 * @return String representation of the node name
	 */
	public String string() {
		String res = "";

		if (_prefix != null) {
			res = _prefix + ":";
		}

		res += _local_part;
		return res;
	}

	/**
	 * Retrieves the full pathname including the namespace. This method must not
	 * be called if a namespace does exist for this node
	 * 
	 * @return Full pathname including namespace
	 */
	public String expanded_name() {
		assert _expanded;
		// if(!_expanded)
		// return null;

		String e = "";
		if (_namespace != null)
			e += _namespace + ":";

		return e + _local_part;
	}

	/**
	 * Retrieves the prefix of the node's pathname
	 * 
	 * @return Prefix of the node's pathname
	 */
	public String prefix() {
		return _prefix;
	}

	/**
	 * Sets the namespace for this node
	 * 
	 * @param n
	 *            Namespace this node belongs in
	 */
	public void set_namespace(String n) {
		_namespace = n;
		_expanded = true;
	}

	/**
	 * Retrieves the namespace that this node belongs in. This method must not
	 * be called if the node does not belong in a namespace
	 * 
	 * @return Namespace that this node belongs in
	 */
	public String namespace() {
		assert _expanded;
		return _namespace;
	}

	/**
	 * Retrieves the node's name
	 * 
	 * @return Node's name
	 */
	public String local() {
		return _local_part;
	}

	/**
	 * Check for whether a namespace has been defined for this node
	 * 
	 * @return True if a namespace has been defined for node. False otherwise
	 */
	public boolean expanded() {
		return _expanded;
	}

	/**
	 * Equality comparison between this QName and a supplied QName
	 * 
	 * @param obj
	 *            The object to compare with. Should be of type QName
	 * @return True if the two represent the same node. False otherwise
	 */
	@Override
	public boolean equals(Object obj) {

		// make sure we are comparing a qname
		if (!(obj instanceof QName))
			return false;

		QName arg = (QName) obj;

		// if they aren't expanded... we can't compare them
		if (!_expanded || !arg.expanded()) {
			assert false; // XXX not stricly necessary
			return false;
		}

		// two cases: null == null, or .equals(other)
		String argn = arg.namespace();
		if (_namespace != null) {
			if (!_namespace.equals(argn))
				return false;
		} else {
			if (argn != null)
				return false;
		}

		String argl = arg.local();
		// XXX local part should always be non null ?
		if (_local_part != null) {
			if (!_local_part.equals(argl))
				return false;
		} else {
			if (argl != null)
				return false;
		}

		return true;
	}

	/**
	 * Calculates the hashcode for the full pathname
	 * 
	 * @return The hashcode for the full pathname
	 */
	@Override
	public int hashCode() {
		int namespace = 3;
		int local = 4;
		int result = 0;

		assert expanded();

		if (_namespace != null)
			namespace = _namespace.hashCode();
		if (_local_part != null)
			local = _local_part.hashCode();

		result = namespace;
		result ^= (2 * local);

		if (_expanded)
			result ^= (result + 1);

		return result;
	}

	/**
	 * Equality comparison between this QName and the supplied QName
	 * 
	 * @param arg
	 *            The QName to compare with
	 * @return True if the two represent the same node. False otherwise
	 * @throws DynamicError
	 */
	public boolean eq(AnyType arg, DynamicContext context) throws DynamicError {
		QName val = (QName) NumericType.get_single_type(arg, QName.class);
		return equals(val);
	}
	
	@Override
	public String toString() {
		return string();
	}
}
