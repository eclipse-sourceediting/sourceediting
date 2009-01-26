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

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.wst.dtd.core.internal.emf.DTDElement;
import org.eclipse.wst.dtd.core.internal.emf.DTDGroupContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDPCDataContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDPackage;

/**
 * @generated
 */
public class DTDPCDataContentImpl extends DTDElementContentImpl implements DTDPCDataContent {

	public DTDPCDataContentImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected EClass eStaticClass() {
		return DTDPackage.eINSTANCE.getDTDPCDataContent();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
		if (featureID >= 0) {
			switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
				case DTDPackage.DTD_PC_DATA_CONTENT__GROUP :
					if (eContainer != null)
						msgs = eBasicRemoveFromContainer(msgs);
					return eBasicSetContainer(otherEnd, DTDPackage.DTD_PC_DATA_CONTENT__GROUP, msgs);
				case DTDPackage.DTD_PC_DATA_CONTENT__ELEMENT :
					if (eContainer != null)
						msgs = eBasicRemoveFromContainer(msgs);
					return eBasicSetContainer(otherEnd, DTDPackage.DTD_PC_DATA_CONTENT__ELEMENT, msgs);
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
				case DTDPackage.DTD_PC_DATA_CONTENT__GROUP :
					return eBasicSetContainer(null, DTDPackage.DTD_PC_DATA_CONTENT__GROUP, msgs);
				case DTDPackage.DTD_PC_DATA_CONTENT__ELEMENT :
					return eBasicSetContainer(null, DTDPackage.DTD_PC_DATA_CONTENT__ELEMENT, msgs);
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
				case DTDPackage.DTD_PC_DATA_CONTENT__GROUP :
					return eContainer.eInverseRemove(this, DTDPackage.DTD_GROUP_CONTENT__CONTENT, DTDGroupContent.class, msgs);
				case DTDPackage.DTD_PC_DATA_CONTENT__ELEMENT :
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
			case DTDPackage.DTD_PC_DATA_CONTENT__GROUP :
				return getGroup();
			case DTDPackage.DTD_PC_DATA_CONTENT__ELEMENT :
				return getElement();
		}
		return eDynamicGet(eFeature, resolve);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void eSet(EStructuralFeature eFeature, Object newValue) {
		switch (eDerivedStructuralFeatureID(eFeature.getFeatureID(), eFeature.getContainerClass())) {
			case DTDPackage.DTD_PC_DATA_CONTENT__GROUP :
				setGroup((DTDGroupContent) newValue);
				return;
			case DTDPackage.DTD_PC_DATA_CONTENT__ELEMENT :
				setElement((DTDElement) newValue);
				return;
		}
		eDynamicSet(eFeature, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void eUnset(EStructuralFeature eFeature) {
		switch (eDerivedStructuralFeatureID(eFeature.getFeatureID(), eFeature.getContainerClass())) {
			case DTDPackage.DTD_PC_DATA_CONTENT__GROUP :
				setGroup((DTDGroupContent) null);
				return;
			case DTDPackage.DTD_PC_DATA_CONTENT__ELEMENT :
				setElement((DTDElement) null);
				return;
		}
		eDynamicUnset(eFeature);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public boolean eIsSet(EStructuralFeature eFeature) {
		switch (eDerivedStructuralFeatureID(eFeature.getFeatureID(), eFeature.getContainerClass())) {
			case DTDPackage.DTD_PC_DATA_CONTENT__GROUP :
				return getGroup() != null;
			case DTDPackage.DTD_PC_DATA_CONTENT__ELEMENT :
				return getElement() != null;
		}
		return eDynamicIsSet(eFeature);
	}

	public String getContentName() {
		return "#PCDATA"; //$NON-NLS-1$
	}
}
