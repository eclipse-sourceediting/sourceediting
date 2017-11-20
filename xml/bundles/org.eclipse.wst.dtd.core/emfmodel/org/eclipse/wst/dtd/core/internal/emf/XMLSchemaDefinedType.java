/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.dtd.core.internal.emf;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.AbstractEnumerator;

/**
 * <!-- begin-user-doc --> A representation of the literals of the enumeration '<em><b>XML Schema Defined Type</b></em>',
 * and utility methods for working with them. <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * NONE=-1
 * STRING=1
 * BOOLEAN=2
 * FLOAT=3
 * DOUBLE=4
 * DECIMAL=5
 * TIMEINSTANT=6
 * TIMEDURATION=7
 * RECURRINGINSTANT=8
 * BINARY=9
 * URI=10
 * INTEGER=11
 * DATE=12
 * TIME=13
 * <!-- end-model-doc -->
 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getXMLSchemaDefinedType()
 * @model
 * @generated
 */
public final class XMLSchemaDefinedType extends AbstractEnumerator {
	/**
	 * The '<em><b>NONE</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #NONE_LITERAL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int NONE = -1;

	/**
	 * The '<em><b>STRING</b></em>' literal value.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * @see #STRING_LITERAL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int STRING = 1;

	/**
	 * The '<em><b>BOOLEAN</b></em>' literal value.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * @see #BOOLEAN_LITERAL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int BOOLEAN = 2;

	/**
	 * The '<em><b>FLOAT</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #FLOAT_LITERAL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int FLOAT = 3;

	/**
	 * The '<em><b>DOUBLE</b></em>' literal value.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * @see #DOUBLE_LITERAL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int DOUBLE = 4;

	/**
	 * The '<em><b>DECIMAL</b></em>' literal value.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * @see #DECIMAL_LITERAL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int DECIMAL = 5;

	/**
	 * The '<em><b>TIMEINSTANT</b></em>' literal value. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #TIMEINSTANT_LITERAL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int TIMEINSTANT = 6;

	/**
	 * The '<em><b>TIMEDURATION</b></em>' literal value. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #TIMEDURATION_LITERAL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int TIMEDURATION = 7;

	/**
	 * The '<em><b>RECURRINGINSTANT</b></em>' literal value. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #RECURRINGINSTANT_LITERAL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int RECURRINGINSTANT = 8;

	/**
	 * The '<em><b>BINARY</b></em>' literal value.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * @see #BINARY_LITERAL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int BINARY = 9;

	/**
	 * The '<em><b>URI</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #URI_LITERAL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int URI = 10;

	/**
	 * The '<em><b>INTEGER</b></em>' literal value.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * @see #INTEGER_LITERAL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int INTEGER = 11;

	/**
	 * The '<em><b>DATE</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #DATE_LITERAL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int DATE = 12;

	/**
	 * The '<em><b>TIME</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #TIME_LITERAL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int TIME = 13;

	/**
	 * The '<em><b>NONE</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>NONE</b></em>' literal object isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #NONE
	 * @generated
	 * @ordered
	 */
	public static final XMLSchemaDefinedType NONE_LITERAL = new XMLSchemaDefinedType(NONE, "NONE", "NONE"); //$NON-NLS-1$

	/**
	 * The '<em><b>STRING</b></em>' literal object.
	 * <!-- begin-user-doc
	 * -->
	 * <p>
	 * If the meaning of '<em><b>STRING</b></em>' literal object isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #STRING
	 * @generated
	 * @ordered
	 */
	public static final XMLSchemaDefinedType STRING_LITERAL = new XMLSchemaDefinedType(STRING, "STRING", "STRING"); //$NON-NLS-1$

	/**
	 * The '<em><b>BOOLEAN</b></em>' literal object.
	 * <!-- begin-user-doc
	 * -->
	 * <p>
	 * If the meaning of '<em><b>BOOLEAN</b></em>' literal object isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #BOOLEAN
	 * @generated
	 * @ordered
	 */
	public static final XMLSchemaDefinedType BOOLEAN_LITERAL = new XMLSchemaDefinedType(BOOLEAN, "BOOLEAN", "BOOLEAN"); //$NON-NLS-1$

	/**
	 * The '<em><b>FLOAT</b></em>' literal object.
	 * <!-- begin-user-doc
	 * -->
	 * <p>
	 * If the meaning of '<em><b>FLOAT</b></em>' literal object isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #FLOAT
	 * @generated
	 * @ordered
	 */
	public static final XMLSchemaDefinedType FLOAT_LITERAL = new XMLSchemaDefinedType(FLOAT, "FLOAT", "FLOAT"); //$NON-NLS-1$

	/**
	 * The '<em><b>DOUBLE</b></em>' literal object.
	 * <!-- begin-user-doc
	 * -->
	 * <p>
	 * If the meaning of '<em><b>DOUBLE</b></em>' literal object isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #DOUBLE
	 * @generated
	 * @ordered
	 */
	public static final XMLSchemaDefinedType DOUBLE_LITERAL = new XMLSchemaDefinedType(DOUBLE, "DOUBLE", "DOUBLE"); //$NON-NLS-1$

	/**
	 * The '<em><b>DECIMAL</b></em>' literal object.
	 * <!-- begin-user-doc
	 * -->
	 * <p>
	 * If the meaning of '<em><b>DECIMAL</b></em>' literal object isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #DECIMAL
	 * @generated
	 * @ordered
	 */
	public static final XMLSchemaDefinedType DECIMAL_LITERAL = new XMLSchemaDefinedType(DECIMAL, "DECIMAL", "DECIMAL"); //$NON-NLS-1$

	/**
	 * The '<em><b>TIMEINSTANT</b></em>' literal object. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>TIMEINSTANT</b></em>' literal object
	 * isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @see #TIMEINSTANT
	 * @generated
	 * @ordered
	 */
	public static final XMLSchemaDefinedType TIMEINSTANT_LITERAL = new XMLSchemaDefinedType(TIMEINSTANT, "TIMEINSTANT", "TIMEINSTANT"); //$NON-NLS-1$

	/**
	 * The '<em><b>TIMEDURATION</b></em>' literal object. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>TIMEDURATION</b></em>' literal object
	 * isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @see #TIMEDURATION
	 * @generated
	 * @ordered
	 */
	public static final XMLSchemaDefinedType TIMEDURATION_LITERAL = new XMLSchemaDefinedType(TIMEDURATION, "TIMEDURATION", "TIMEDURATION"); //$NON-NLS-1$

	/**
	 * The '<em><b>RECURRINGINSTANT</b></em>' literal object. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>RECURRINGINSTANT</b></em>' literal
	 * object isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @see #RECURRINGINSTANT
	 * @generated
	 * @ordered
	 */
	public static final XMLSchemaDefinedType RECURRINGINSTANT_LITERAL = new XMLSchemaDefinedType(RECURRINGINSTANT, "RECURRINGINSTANT", "RECURRINGINSTANT"); //$NON-NLS-1$

	/**
	 * The '<em><b>BINARY</b></em>' literal object.
	 * <!-- begin-user-doc
	 * -->
	 * <p>
	 * If the meaning of '<em><b>BINARY</b></em>' literal object isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #BINARY
	 * @generated
	 * @ordered
	 */
	public static final XMLSchemaDefinedType BINARY_LITERAL = new XMLSchemaDefinedType(BINARY, "BINARY", "BINARY"); //$NON-NLS-1$

	/**
	 * The '<em><b>URI</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>URI</b></em>' literal object isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #URI
	 * @generated
	 * @ordered
	 */
	public static final XMLSchemaDefinedType URI_LITERAL = new XMLSchemaDefinedType(URI, "URI", "URI"); //$NON-NLS-1$

	/**
	 * The '<em><b>INTEGER</b></em>' literal object.
	 * <!-- begin-user-doc
	 * -->
	 * <p>
	 * If the meaning of '<em><b>INTEGER</b></em>' literal object isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #INTEGER
	 * @generated
	 * @ordered
	 */
	public static final XMLSchemaDefinedType INTEGER_LITERAL = new XMLSchemaDefinedType(INTEGER, "INTEGER", "INTEGER"); //$NON-NLS-1$

	/**
	 * The '<em><b>DATE</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>DATE</b></em>' literal object isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #DATE
	 * @generated
	 * @ordered
	 */
	public static final XMLSchemaDefinedType DATE_LITERAL = new XMLSchemaDefinedType(DATE, "DATE", "DATE"); //$NON-NLS-1$

	/**
	 * The '<em><b>TIME</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>TIME</b></em>' literal object isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #TIME
	 * @generated
	 * @ordered
	 */
	public static final XMLSchemaDefinedType TIME_LITERAL = new XMLSchemaDefinedType(TIME, "TIME", "TIME"); //$NON-NLS-1$

	/**
	 * An array of all the '<em><b>XML Schema Defined Type</b></em>' enumerators.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private static final XMLSchemaDefinedType[] VALUES_ARRAY = new XMLSchemaDefinedType[] {
			NONE_LITERAL,
			STRING_LITERAL,
			BOOLEAN_LITERAL,
			FLOAT_LITERAL,
			DOUBLE_LITERAL,
			DECIMAL_LITERAL,
			TIMEINSTANT_LITERAL,
			TIMEDURATION_LITERAL,
			RECURRINGINSTANT_LITERAL,
			BINARY_LITERAL,
			URI_LITERAL,
			INTEGER_LITERAL,
			DATE_LITERAL,
			TIME_LITERAL,
		};

	/**
	 * A public read-only list of all the '<em><b>XML Schema Defined Type</b></em>' enumerators.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>XML Schema Defined Type</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public static XMLSchemaDefinedType get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			XMLSchemaDefinedType result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>XML Schema Defined Type</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static XMLSchemaDefinedType getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			XMLSchemaDefinedType result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>XML Schema Defined Type</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public static XMLSchemaDefinedType get(int value) {
		switch (value) {
			case NONE: return NONE_LITERAL;
			case STRING: return STRING_LITERAL;
			case BOOLEAN: return BOOLEAN_LITERAL;
			case FLOAT: return FLOAT_LITERAL;
			case DOUBLE: return DOUBLE_LITERAL;
			case DECIMAL: return DECIMAL_LITERAL;
			case TIMEINSTANT: return TIMEINSTANT_LITERAL;
			case TIMEDURATION: return TIMEDURATION_LITERAL;
			case RECURRINGINSTANT: return RECURRINGINSTANT_LITERAL;
			case BINARY: return BINARY_LITERAL;
			case URI: return URI_LITERAL;
			case INTEGER: return INTEGER_LITERAL;
			case DATE: return DATE_LITERAL;
			case TIME: return TIME_LITERAL;
		}
		return null;
	}

	/**
	 * Only this class can construct instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private XMLSchemaDefinedType(int value, String name, String literal) {
		super(value, name, literal);
	}

} // XMLSchemaDefinedType
