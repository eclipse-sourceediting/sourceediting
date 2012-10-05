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
 * <!-- begin-user-doc --> A representation of the literals of the enumeration '<em><b>Occurrence Type</b></em>',
 * and utility methods for working with them. <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * ONE=49 
 * OPTIONAL=63 ONE_OR_MORE=43 ZERO_OR_MORE=42
 * <!-- end-model-doc -->
 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDOccurrenceType()
 * @model
 * @generated
 */
public final class DTDOccurrenceType extends AbstractEnumerator {
	/**
	 * The '<em><b>ONE</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ONE_LITERAL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int ONE = 49;

	/**
	 * The '<em><b>OPTIONAL</b></em>' literal value.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * @see #OPTIONAL_LITERAL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int OPTIONAL = 63;

	/**
	 * The '<em><b>ONE OR MORE</b></em>' literal value. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #ONE_OR_MORE_LITERAL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int ONE_OR_MORE = 43;

	/**
	 * The '<em><b>ZERO OR MORE</b></em>' literal value. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #ZERO_OR_MORE_LITERAL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int ZERO_OR_MORE = 42;

	/**
	 * The '<em><b>ONE</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>ONE</b></em>' literal object isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #ONE
	 * @generated
	 * @ordered
	 */
	public static final DTDOccurrenceType ONE_LITERAL = new DTDOccurrenceType(ONE, "ONE", "ONE"); //$NON-NLS-1$

	/**
	 * The '<em><b>OPTIONAL</b></em>' literal object.
	 * <!-- begin-user-doc
	 * -->
	 * <p>
	 * If the meaning of '<em><b>OPTIONAL</b></em>' literal object isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #OPTIONAL
	 * @generated
	 * @ordered
	 */
	public static final DTDOccurrenceType OPTIONAL_LITERAL = new DTDOccurrenceType(OPTIONAL, "OPTIONAL", "OPTIONAL"); //$NON-NLS-1$

	/**
	 * The '<em><b>ONE OR MORE</b></em>' literal object. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>ONE OR MORE</b></em>' literal object
	 * isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @see #ONE_OR_MORE
	 * @generated
	 * @ordered
	 */
	public static final DTDOccurrenceType ONE_OR_MORE_LITERAL = new DTDOccurrenceType(ONE_OR_MORE, "ONE_OR_MORE", "ONE_OR_MORE"); //$NON-NLS-1$

	/**
	 * The '<em><b>ZERO OR MORE</b></em>' literal object. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>ZERO OR MORE</b></em>' literal object
	 * isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @see #ZERO_OR_MORE
	 * @generated
	 * @ordered
	 */
	public static final DTDOccurrenceType ZERO_OR_MORE_LITERAL = new DTDOccurrenceType(ZERO_OR_MORE, "ZERO_OR_MORE", "ZERO_OR_MORE"); //$NON-NLS-1$

	/**
	 * An array of all the '<em><b>Occurrence Type</b></em>' enumerators.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private static final DTDOccurrenceType[] VALUES_ARRAY = new DTDOccurrenceType[] {
			ONE_LITERAL,
			OPTIONAL_LITERAL,
			ONE_OR_MORE_LITERAL,
			ZERO_OR_MORE_LITERAL,
		};

	/**
	 * A public read-only list of all the '<em><b>Occurrence Type</b></em>' enumerators.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Occurrence Type</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public static DTDOccurrenceType get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			DTDOccurrenceType result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Occurrence Type</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static DTDOccurrenceType getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			DTDOccurrenceType result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Occurrence Type</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public static DTDOccurrenceType get(int value) {
		switch (value) {
			case ONE: return ONE_LITERAL;
			case OPTIONAL: return OPTIONAL_LITERAL;
			case ONE_OR_MORE: return ONE_OR_MORE_LITERAL;
			case ZERO_OR_MORE: return ZERO_OR_MORE_LITERAL;
		}
		return null;
	}

	/**
	 * Only this class can construct instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private DTDOccurrenceType(int value, String name, String literal) {
		super(value, name, literal);
	}

} // DTDOccurrenceType
