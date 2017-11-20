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
 * <!-- begin-user-doc --> A representation of the literals of the enumeration '<em><b>Default Kind</b></em>',
 * and utility methods for working with them. <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * IMPLIED=1 REQUIRED=2 FIXED=3 NOFIXED=4
 * <!-- end-model-doc -->
 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDDefaultKind()
 * @model
 * @generated
 */
public final class DTDDefaultKind extends AbstractEnumerator {
	/**
	 * The '<em><b>IMPLIED</b></em>' literal value.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * @see #IMPLIED_LITERAL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int IMPLIED = 1;

	/**
	 * The '<em><b>REQUIRED</b></em>' literal value.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * @see #REQUIRED_LITERAL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int REQUIRED = 2;

	/**
	 * The '<em><b>FIXED</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #FIXED_LITERAL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int FIXED = 3;

	/**
	 * The '<em><b>NOFIXED</b></em>' literal value.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * @see #NOFIXED_LITERAL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int NOFIXED = 4;

	/**
	 * The '<em><b>IMPLIED</b></em>' literal object.
	 * <!-- begin-user-doc
	 * -->
	 * <p>
	 * If the meaning of '<em><b>IMPLIED</b></em>' literal object isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #IMPLIED
	 * @generated
	 * @ordered
	 */
	public static final DTDDefaultKind IMPLIED_LITERAL = new DTDDefaultKind(IMPLIED, "IMPLIED", "IMPLIED"); //$NON-NLS-1$

	/**
	 * The '<em><b>REQUIRED</b></em>' literal object.
	 * <!-- begin-user-doc
	 * -->
	 * <p>
	 * If the meaning of '<em><b>REQUIRED</b></em>' literal object isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #REQUIRED
	 * @generated
	 * @ordered
	 */
	public static final DTDDefaultKind REQUIRED_LITERAL = new DTDDefaultKind(REQUIRED, "REQUIRED", "REQUIRED"); //$NON-NLS-1$

	/**
	 * The '<em><b>FIXED</b></em>' literal object.
	 * <!-- begin-user-doc
	 * -->
	 * <p>
	 * If the meaning of '<em><b>FIXED</b></em>' literal object isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #FIXED
	 * @generated
	 * @ordered
	 */
	public static final DTDDefaultKind FIXED_LITERAL = new DTDDefaultKind(FIXED, "FIXED", "FIXED"); //$NON-NLS-1$

	/**
	 * The '<em><b>NOFIXED</b></em>' literal object.
	 * <!-- begin-user-doc
	 * -->
	 * <p>
	 * If the meaning of '<em><b>NOFIXED</b></em>' literal object isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #NOFIXED
	 * @generated
	 * @ordered
	 */
	public static final DTDDefaultKind NOFIXED_LITERAL = new DTDDefaultKind(NOFIXED, "NOFIXED", "NOFIXED"); //$NON-NLS-1$

	/**
	 * An array of all the '<em><b>Default Kind</b></em>' enumerators.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private static final DTDDefaultKind[] VALUES_ARRAY = new DTDDefaultKind[] {
			IMPLIED_LITERAL,
			REQUIRED_LITERAL,
			FIXED_LITERAL,
			NOFIXED_LITERAL,
		};

	/**
	 * A public read-only list of all the '<em><b>Default Kind</b></em>' enumerators.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Default Kind</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public static DTDDefaultKind get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			DTDDefaultKind result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Default Kind</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static DTDDefaultKind getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			DTDDefaultKind result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Default Kind</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public static DTDDefaultKind get(int value) {
		switch (value) {
			case IMPLIED: return IMPLIED_LITERAL;
			case REQUIRED: return REQUIRED_LITERAL;
			case FIXED: return FIXED_LITERAL;
			case NOFIXED: return NOFIXED_LITERAL;
		}
		return null;
	}

	/**
	 * Only this class can construct instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private DTDDefaultKind(int value, String name, String literal) {
		super(value, name, literal);
	}

} // DTDDefaultKind
