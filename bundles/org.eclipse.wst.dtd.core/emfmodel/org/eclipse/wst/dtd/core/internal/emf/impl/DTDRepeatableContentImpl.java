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
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.wst.dtd.core.internal.emf.DTDElement;
import org.eclipse.wst.dtd.core.internal.emf.DTDEntityReferenceContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDGroupContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDOccurrenceType;
import org.eclipse.wst.dtd.core.internal.emf.DTDPackage;
import org.eclipse.wst.dtd.core.internal.emf.DTDRepeatableContent;

/**
 * @generated
 */
public abstract class DTDRepeatableContentImpl extends DTDElementContentImpl implements DTDRepeatableContent {
	/**
	 * The default value of the '{@link #getOccurrence() <em>Occurrence</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
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
	 * 
	 * @generated
	 */
	protected EClass eStaticClass() {
		return DTDPackage.eINSTANCE.getDTDRepeatableContent();
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
	 * 
	 * @generated
	 */
	public void setOccurrence(DTDOccurrenceType newOccurrence) {
		DTDOccurrenceType oldOccurrence = occurrence;
		occurrence = newOccurrence == null ? OCCURRENCE_EDEFAULT : newOccurrence;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_REPEATABLE_CONTENT__OCCURRENCE, oldOccurrence, occurrence));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
		if (featureID >= 0) {
			switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
				case DTDPackage.DTD_REPEATABLE_CONTENT__GROUP :
					if (eContainer != null)
						msgs = eBasicRemoveFromContainer(msgs);
					return eBasicSetContainer(otherEnd, DTDPackage.DTD_REPEATABLE_CONTENT__GROUP, msgs);
				case DTDPackage.DTD_REPEATABLE_CONTENT__ELEMENT :
					if (eContainer != null)
						msgs = eBasicRemoveFromContainer(msgs);
					return eBasicSetContainer(otherEnd, DTDPackage.DTD_REPEATABLE_CONTENT__ELEMENT, msgs);
				default :
					return eDynamicInverseAdd(otherEnd, featureID, baseClass, msgs);
			}
		}
		if (eContainer != null)
			msgs = eBasicRemoveFromContainer(msgs);
		return eBasicSetContainer(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
		if (featureID >= 0) {
			switch (baseClass == null ? featureID : eDerivedStructuralFeatureID(featureID, baseClass)) {
				case DTDPackage.DTD_REPEATABLE_CONTENT__GROUP :
					return eBasicSetContainer(null, DTDPackage.DTD_REPEATABLE_CONTENT__GROUP, msgs);
				case DTDPackage.DTD_REPEATABLE_CONTENT__ELEMENT :
					return eBasicSetContainer(null, DTDPackage.DTD_REPEATABLE_CONTENT__ELEMENT, msgs);
				default :
					return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
			}
		}
		return eBasicSetContainer(null, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain eBasicRemoveFromContainer(NotificationChain msgs) {
		if (eContainerFeatureID() >= 0) {
			switch (eContainerFeatureID()) {
				case DTDPackage.DTD_REPEATABLE_CONTENT__GROUP :
					return eContainer.eInverseRemove(this, DTDPackage.DTD_GROUP_CONTENT__CONTENT, DTDGroupContent.class, msgs);
				case DTDPackage.DTD_REPEATABLE_CONTENT__ELEMENT :
					return eContainer.eInverseRemove(this, DTDPackage.DTD_ELEMENT__CONTENT, DTDElement.class, msgs);
				default :
					return eDynamicBasicRemoveFromContainer(msgs);
			}
		}
		return eContainer.eInverseRemove(this, EOPPOSITE_FEATURE_BASE - eContainerFeatureID(), null, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Object eGet(EStructuralFeature eFeature, boolean resolve) {
		switch (eDerivedStructuralFeatureID(eFeature.getFeatureID(), eFeature.getContainerClass())) {
			case DTDPackage.DTD_REPEATABLE_CONTENT__GROUP :
				return getGroup();
			case DTDPackage.DTD_REPEATABLE_CONTENT__ELEMENT :
				return getElement();
			case DTDPackage.DTD_REPEATABLE_CONTENT__OCCURRENCE :
				return getOccurrence();
		}
		return eDynamicGet(eFeature, resolve);
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public boolean eIsSet(EStructuralFeature eFeature) {
		switch (eDerivedStructuralFeatureID(eFeature.getFeatureID(), eFeature.getContainerClass())) {
			case DTDPackage.DTD_REPEATABLE_CONTENT__GROUP :
				return getGroup() != null;
			case DTDPackage.DTD_REPEATABLE_CONTENT__ELEMENT :
				return getElement() != null;
			case DTDPackage.DTD_REPEATABLE_CONTENT__OCCURRENCE :
				return occurrence != OCCURRENCE_EDEFAULT;
		}
		return eDynamicIsSet(eFeature);
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public void eSet(EStructuralFeature eFeature, Object newValue) {
		switch (eDerivedStructuralFeatureID(eFeature.getFeatureID(), eFeature.getContainerClass())) {
			case DTDPackage.DTD_REPEATABLE_CONTENT__GROUP :
				setGroup((DTDGroupContent) newValue);
				return;
			case DTDPackage.DTD_REPEATABLE_CONTENT__ELEMENT :
				setElement((DTDElement) newValue);
				return;
			case DTDPackage.DTD_REPEATABLE_CONTENT__OCCURRENCE :
				setOccurrence((DTDOccurrenceType) newValue);
				return;
		}
		eDynamicSet(eFeature, newValue);
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public void eUnset(EStructuralFeature eFeature) {
		switch (eDerivedStructuralFeatureID(eFeature.getFeatureID(), eFeature.getContainerClass())) {
			case DTDPackage.DTD_REPEATABLE_CONTENT__GROUP :
				setGroup((DTDGroupContent) null);
				return;
			case DTDPackage.DTD_REPEATABLE_CONTENT__ELEMENT :
				setElement((DTDElement) null);
				return;
			case DTDPackage.DTD_REPEATABLE_CONTENT__OCCURRENCE :
				setOccurrence(OCCURRENCE_EDEFAULT);
				return;
		}
		eDynamicUnset(eFeature);
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public String toString() {
		if (eIsProxy())
			return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (occurrence: "); //$NON-NLS-1$
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
