/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.dtd.core.internal.emf.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.wst.dtd.core.internal.emf.DTDEntityReferenceContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDOccurrenceType;
import org.eclipse.wst.dtd.core.internal.emf.DTDPackage;
import org.eclipse.wst.dtd.core.internal.emf.DTDRepeatableContent;

/**
 * @generated
 */
public abstract class DTDRepeatableContentImpl extends DTDElementContentImpl implements DTDRepeatableContent {
	/**
	 * The default value of the '{@link #getOccurrence() <em>Occurrence</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getOccurrence()
	 * @generated
	 * @ordered
	 */
	protected static final DTDOccurrenceType OCCURRENCE_EDEFAULT = DTDOccurrenceType.ONE_LITERAL;

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	protected DTDOccurrenceType occurrence = OCCURRENCE_EDEFAULT;

	public DTDRepeatableContentImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return DTDPackage.Literals.DTD_REPEATABLE_CONTENT;
	}

	public String unparse() {
		StringBuffer sb = new StringBuffer(128);
		if (getOccurrence().getValue() != DTDOccurrenceType.ONE) {
			if (this instanceof DTDEntityReferenceContent) {
				sb.append("(").append(unparseRepeatableContent()).append(")"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				sb.append(unparseRepeatableContent());
			}

			// MOF2EMF Port
			// EEnumLiteral lit = getOccurrence();
			DTDOccurrenceType lit = getOccurrence();
			if (lit != null) {
				sb.append((char) lit.getValue());
			}
		}
		else {
			sb.append(unparseRepeatableContent());
		}

		return sb.toString();
	}

	/**
	 * @generated This field/method will be replaced during code generation
	 *            JUST_ONE= -1 ONE_OR_MORE=43 OPTIONAL=63 ZERO_OR_MORE=42
	 */
	public DTDOccurrenceType getOccurrence() {
		return occurrence;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setOccurrence(DTDOccurrenceType newOccurrence) {
		DTDOccurrenceType oldOccurrence = occurrence;
		occurrence = newOccurrence == null ? OCCURRENCE_EDEFAULT : newOccurrence;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_REPEATABLE_CONTENT__OCCURRENCE, oldOccurrence, occurrence));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DTDPackage.DTD_REPEATABLE_CONTENT__OCCURRENCE:
				return getOccurrence();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case DTDPackage.DTD_REPEATABLE_CONTENT__OCCURRENCE:
				setOccurrence((DTDOccurrenceType)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void eUnset(int featureID) {
		switch (featureID) {
			case DTDPackage.DTD_REPEATABLE_CONTENT__OCCURRENCE:
				setOccurrence(OCCURRENCE_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case DTDPackage.DTD_REPEATABLE_CONTENT__OCCURRENCE:
				return occurrence != OCCURRENCE_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (occurrence: ");
		result.append(occurrence);
		result.append(')');
		return result.toString();
	}

	/*
	 * @see DTDRepeatableContent#unparseRepeatableContent()
	 */
	public String unparseRepeatableContent() {
		return null;
	}

	/*
	 * @see DTDElementContent#getContentName()
	 */
	public String getContentName() {
		return null;
	}

}
