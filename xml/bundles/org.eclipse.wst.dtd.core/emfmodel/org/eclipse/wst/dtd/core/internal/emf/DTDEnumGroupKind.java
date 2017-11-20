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
 * <!-- begin-user-doc --> A representation of the literals of the enumeration '<em><b>Enum Group Kind</b></em>',
 * and utility methods for working with them. <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * NAME_TOKEN_GROUP=1 NOTATION_GROUP=2
 * <!-- end-model-doc -->
 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDEnumGroupKind()
 * @model
 * @generated
 */
public final class DTDEnumGroupKind extends AbstractEnumerator {
	/**
	 * The '<em><b>NAME TOKEN GROUP</b></em>' literal value. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #NAME_TOKEN_GROUP_LITERAL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int NAME_TOKEN_GROUP = 1;

	/**
	 * The '<em><b>NOTATION GROUP</b></em>' literal value. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #NOTATION_GROUP_LITERAL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int NOTATION_GROUP = 2;

	/**
	 * The '<em><b>NAME TOKEN GROUP</b></em>' literal object. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>NAME TOKEN GROUP</b></em>' literal
	 * object isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @see #NAME_TOKEN_GROUP
	 * @generated
	 * @ordered
	 */
	public static final DTDEnumGroupKind NAME_TOKEN_GROUP_LITERAL = new DTDEnumGroupKind(NAME_TOKEN_GROUP, "NAME_TOKEN_GROUP", "NAME_TOKEN_GROUP"); //$NON-NLS-1$

	/**
	 * The '<em><b>NOTATION GROUP</b></em>' literal object. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>NOTATION GROUP</b></em>' literal object
	 * isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @see #NOTATION_GROUP
	 * @generated
	 * @ordered
	 */
	public static final DTDEnumGroupKind NOTATION_GROUP_LITERAL = new DTDEnumGroupKind(NOTATION_GROUP, "NOTATION_GROUP", "NOTATION_GROUP"); //$NON-NLS-1$

	/**
	 * An array of all the '<em><b>Enum Group Kind</b></em>' enumerators.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private static final DTDEnumGroupKind[] VALUES_ARRAY = new DTDEnumGroupKind[] {
			NAME_TOKEN_GROUP_LITERAL,
			NOTATION_GROUP_LITERAL,
		};

	/**
	 * A public read-only list of all the '<em><b>Enum Group Kind</b></em>' enumerators.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Enum Group Kind</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public static DTDEnumGroupKind get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			DTDEnumGroupKind result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Enum Group Kind</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static DTDEnumGroupKind getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			DTDEnumGroupKind result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Enum Group Kind</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public static DTDEnumGroupKind get(int value) {
		switch (value) {
			case NAME_TOKEN_GROUP: return NAME_TOKEN_GROUP_LITERAL;
			case NOTATION_GROUP: return NOTATION_GROUP_LITERAL;
		}
		return null;
	}

	/**
	 * Only this class can construct instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private DTDEnumGroupKind(int value, String name, String literal) {
		super(value, name, literal);
	}

} // DTDEnumGroupKind
