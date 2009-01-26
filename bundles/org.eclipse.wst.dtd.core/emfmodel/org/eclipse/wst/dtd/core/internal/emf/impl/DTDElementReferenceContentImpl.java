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
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.wst.dtd.core.internal.emf.DTDElement;
import org.eclipse.wst.dtd.core.internal.emf.DTDElementReferenceContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDGroupContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDOccurrenceType;
import org.eclipse.wst.dtd.core.internal.emf.DTDPackage;


/**
 * @generated
 */
public class DTDElementReferenceContentImpl extends DTDRepeatableContentImpl implements DTDElementReferenceContent {

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	protected DTDElement referencedElement = null;

	public DTDElementReferenceContentImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected EClass eStaticClass() {
		return DTDPackage.eINSTANCE.getDTDElementReferenceContent();
	}

	public String unparseRepeatableContent() {
		return getContentName();
	}

	public String getContentName() {
		DTDElement e = getReferencedElement();
		if (e == null)
			return ""; //$NON-NLS-1$
		return e.getName();
	}

	/**
	 * @generated This field/method will be replaced during code generation
	 */
	public DTDElement getReferencedElement() {
		if (referencedElement != null && referencedElement.eIsProxy()) {
			DTDElement oldReferencedElement = referencedElement;
			referencedElement = (DTDElement) EcoreUtil.resolve(referencedElement, this);
			if (referencedElement != oldReferencedElement) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, DTDPackage.DTD_ELEMENT_REFERENCE_CONTENT__REFERENCED_ELEMENT, oldReferencedElement, referencedElement));
			}
		}
		return referencedElement;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public DTDElement basicGetReferencedElement() {
		return referencedElement;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public void setReferencedElement(DTDElement newReferencedElement) {
		DTDElement oldReferencedElement = referencedElement;
		referencedElement = newReferencedElement;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ELEMENT_REFERENCE_CONTENT__REFERENCED_ELEMENT, oldReferencedElement, referencedElement));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
		if (featureID >= 0) {
			switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
				case DTDPackage.DTD_ELEMENT_REFERENCE_CONTENT__GROUP :
					if (eContainer != null)
						msgs = eBasicRemoveFromContainer(msgs);
					return eBasicSetContainer(otherEnd, DTDPackage.DTD_ELEMENT_REFERENCE_CONTENT__GROUP, msgs);
				case DTDPackage.DTD_ELEMENT_REFERENCE_CONTENT__ELEMENT :
					if (eContainer != null)
						msgs = eBasicRemoveFromContainer(msgs);
					return eBasicSetContainer(otherEnd, DTDPackage.DTD_ELEMENT_REFERENCE_CONTENT__ELEMENT, msgs);
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
				case DTDPackage.DTD_ELEMENT_REFERENCE_CONTENT__GROUP :
					return eBasicSetContainer(null, DTDPackage.DTD_ELEMENT_REFERENCE_CONTENT__GROUP, msgs);
				case DTDPackage.DTD_ELEMENT_REFERENCE_CONTENT__ELEMENT :
					return eBasicSetContainer(null, DTDPackage.DTD_ELEMENT_REFERENCE_CONTENT__ELEMENT, msgs);
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
				case DTDPackage.DTD_ELEMENT_REFERENCE_CONTENT__GROUP :
					return eContainer.eInverseRemove(this, DTDPackage.DTD_GROUP_CONTENT__CONTENT, DTDGroupContent.class, msgs);
				case DTDPackage.DTD_ELEMENT_REFERENCE_CONTENT__ELEMENT :
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
			case DTDPackage.DTD_ELEMENT_REFERENCE_CONTENT__GROUP :
				return getGroup();
			case DTDPackage.DTD_ELEMENT_REFERENCE_CONTENT__ELEMENT :
				return getElement();
			case DTDPackage.DTD_ELEMENT_REFERENCE_CONTENT__OCCURRENCE :
				return getOccurrence();
			case DTDPackage.DTD_ELEMENT_REFERENCE_CONTENT__REFERENCED_ELEMENT :
				if (resolve)
					return getReferencedElement();
				return basicGetReferencedElement();
		}
		return eDynamicGet(eFeature, resolve);
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public boolean eIsSet(EStructuralFeature eFeature) {
		switch (eDerivedStructuralFeatureID(eFeature.getFeatureID(), eFeature.getContainerClass())) {
			case DTDPackage.DTD_ELEMENT_REFERENCE_CONTENT__GROUP :
				return getGroup() != null;
			case DTDPackage.DTD_ELEMENT_REFERENCE_CONTENT__ELEMENT :
				return getElement() != null;
			case DTDPackage.DTD_ELEMENT_REFERENCE_CONTENT__OCCURRENCE :
				return occurrence != OCCURRENCE_EDEFAULT;
			case DTDPackage.DTD_ELEMENT_REFERENCE_CONTENT__REFERENCED_ELEMENT :
				return referencedElement != null;
		}
		return eDynamicIsSet(eFeature);
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public void eSet(EStructuralFeature eFeature, Object newValue) {
		switch (eDerivedStructuralFeatureID(eFeature.getFeatureID(), eFeature.getContainerClass())) {
			case DTDPackage.DTD_ELEMENT_REFERENCE_CONTENT__GROUP :
				setGroup((DTDGroupContent) newValue);
				return;
			case DTDPackage.DTD_ELEMENT_REFERENCE_CONTENT__ELEMENT :
				setElement((DTDElement) newValue);
				return;
			case DTDPackage.DTD_ELEMENT_REFERENCE_CONTENT__OCCURRENCE :
				setOccurrence((DTDOccurrenceType) newValue);
				return;
			case DTDPackage.DTD_ELEMENT_REFERENCE_CONTENT__REFERENCED_ELEMENT :
				setReferencedElement((DTDElement) newValue);
				return;
		}
		eDynamicSet(eFeature, newValue);
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public void eUnset(EStructuralFeature eFeature) {
		switch (eDerivedStructuralFeatureID(eFeature.getFeatureID(), eFeature.getContainerClass())) {
			case DTDPackage.DTD_ELEMENT_REFERENCE_CONTENT__GROUP :
				setGroup((DTDGroupContent) null);
				return;
			case DTDPackage.DTD_ELEMENT_REFERENCE_CONTENT__ELEMENT :
				setElement((DTDElement) null);
				return;
			case DTDPackage.DTD_ELEMENT_REFERENCE_CONTENT__OCCURRENCE :
				setOccurrence(OCCURRENCE_EDEFAULT);
				return;
			case DTDPackage.DTD_ELEMENT_REFERENCE_CONTENT__REFERENCED_ELEMENT :
				setReferencedElement((DTDElement) null);
				return;
		}
		eDynamicUnset(eFeature);
	}

}
