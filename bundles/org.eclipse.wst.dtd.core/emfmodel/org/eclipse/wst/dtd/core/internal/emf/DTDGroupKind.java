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
 * <!-- begin-user-doc --> A representation of the literals of the enumeration '<em><b>Group Kind</b></em>',
 * and utility methods for working with them. <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * SEQUENCE=1 CHOICE=2
 * <!-- end-model-doc -->
 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDGroupKind()
 * @model
 * @generated
 */
public final class DTDGroupKind extends AbstractEnumerator {
	/**
	 * The '<em><b>SEQUENCE</b></em>' literal value.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * @see #SEQUENCE_LITERAL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int SEQUENCE = 1;

	/**
	 * The '<em><b>CHOICE</b></em>' literal value.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * @see #CHOICE_LITERAL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int CHOICE = 2;

	/**
	 * The '<em><b>SEQUENCE</b></em>' literal object.
	 * <!-- begin-user-doc
	 * -->
	 * <p>
	 * If the meaning of '<em><b>SEQUENCE</b></em>' literal object isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #SEQUENCE
	 * @generated
	 * @ordered
	 */
	public static final DTDGroupKind SEQUENCE_LITERAL = new DTDGroupKind(SEQUENCE, "SEQUENCE", "SEQUENCE"); //$NON-NLS-1$

	/**
	 * The '<em><b>CHOICE</b></em>' literal object.
	 * <!-- begin-user-doc
	 * -->
	 * <p>
	 * If the meaning of '<em><b>CHOICE</b></em>' literal object isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CHOICE
	 * @generated
	 * @ordered
	 */
	public static final DTDGroupKind CHOICE_LITERAL = new DTDGroupKind(CHOICE, "CHOICE", "CHOICE"); //$NON-NLS-1$

	/**
	 * An array of all the '<em><b>Group Kind</b></em>' enumerators. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private static final DTDGroupKind[] VALUES_ARRAY = new DTDGroupKind[] {
			SEQUENCE_LITERAL,
			CHOICE_LITERAL,
		};

	/**
	 * A public read-only list of all the '<em><b>Group Kind</b></em>' enumerators.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Group Kind</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public static DTDGroupKind get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			DTDGroupKind result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Group Kind</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static DTDGroupKind getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			DTDGroupKind result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Group Kind</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public static DTDGroupKind get(int value) {
		switch (value) {
			case SEQUENCE: return SEQUENCE_LITERAL;
			case CHOICE: return CHOICE_LITERAL;
		}
		return null;
	}

	/**
	 * Only this class can construct instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private DTDGroupKind(int value, String name, String literal) {
		super(value, name, literal);
	}

} // DTDGroupKind
