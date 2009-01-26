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
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.wst.dtd.core.internal.emf.DTDEntity;
import org.eclipse.wst.dtd.core.internal.emf.DTDEntityContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDObject;
import org.eclipse.wst.dtd.core.internal.emf.DTDPackage;
import org.eclipse.wst.dtd.core.internal.emf.util.DTDPathnameUtil;

/**
 * @generated
 */
public abstract class DTDEntityContentImpl extends EObjectImpl implements DTDEntityContent {

	public DTDEntityContentImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected EClass eStaticClass() {
		return DTDPackage.eINSTANCE.getDTDEntityContent();
	}

	public String getPathname() {
		return DTDPathnameUtil.makePath(getDTDEntity().getPathname(), "Content", null, -1); //$NON-NLS-1$
	}

	public DTDObject findObject(String relativePath) {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation
	 */
	public DTDEntity getDTDEntity() {
		if (eContainerFeatureID() != DTDPackage.DTD_ENTITY_CONTENT__DTD_ENTITY)
			return null;
		return (DTDEntity) eContainer;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public void setDTDEntity(DTDEntity newDTDEntity) {
		if (newDTDEntity != eContainer || (eContainerFeatureID() != DTDPackage.DTD_ENTITY_CONTENT__DTD_ENTITY && newDTDEntity != null)) {
			if (EcoreUtil.isAncestor(this, newDTDEntity))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString() + "."); //$NON-NLS-1$ //$NON-NLS-2$
			NotificationChain msgs = null;
			if (eContainer != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newDTDEntity != null)
				msgs = ((InternalEObject) newDTDEntity).eInverseAdd(this, DTDPackage.DTD_ENTITY__CONTENT, DTDEntity.class, msgs);
			msgs = eBasicSetContainer((InternalEObject) newDTDEntity, DTDPackage.DTD_ENTITY_CONTENT__DTD_ENTITY, msgs);
			if (msgs != null)
				msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ENTITY_CONTENT__DTD_ENTITY, newDTDEntity, newDTDEntity));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
		if (featureID >= 0) {
			switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
				case DTDPackage.DTD_ENTITY_CONTENT__DTD_ENTITY :
					if (eContainer != null)
						msgs = eBasicRemoveFromContainer(msgs);
					return eBasicSetContainer(otherEnd, DTDPackage.DTD_ENTITY_CONTENT__DTD_ENTITY, msgs);
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
				case DTDPackage.DTD_ENTITY_CONTENT__DTD_ENTITY :
					return eBasicSetContainer(null, DTDPackage.DTD_ENTITY_CONTENT__DTD_ENTITY, msgs);
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
				case DTDPackage.DTD_ENTITY_CONTENT__DTD_ENTITY :
					return eContainer.eInverseRemove(this, DTDPackage.DTD_ENTITY__CONTENT, DTDEntity.class, msgs);
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
			case DTDPackage.DTD_ENTITY_CONTENT__DTD_ENTITY :
				return getDTDEntity();
		}
		return eDynamicGet(eFeature, resolve);
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public boolean eIsSet(EStructuralFeature eFeature) {
		switch (eDerivedStructuralFeatureID(eFeature.getFeatureID(), eFeature.getContainerClass())) {
			case DTDPackage.DTD_ENTITY_CONTENT__DTD_ENTITY :
				return getDTDEntity() != null;
		}
		return eDynamicIsSet(eFeature);
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public void eSet(EStructuralFeature eFeature, Object newValue) {
		switch (eDerivedStructuralFeatureID(eFeature.getFeatureID(), eFeature.getContainerClass())) {
			case DTDPackage.DTD_ENTITY_CONTENT__DTD_ENTITY :
				setDTDEntity((DTDEntity) newValue);
				return;
		}
		eDynamicSet(eFeature, newValue);
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public void eUnset(EStructuralFeature eFeature) {
		switch (eDerivedStructuralFeatureID(eFeature.getFeatureID(), eFeature.getContainerClass())) {
			case DTDPackage.DTD_ENTITY_CONTENT__DTD_ENTITY :
				setDTDEntity((DTDEntity) null);
				return;
		}
		eDynamicUnset(eFeature);
	}

	/*
	 * @see DTDEntityContent#unparse()
	 */
	public String unparse() {
		return null;
	}

}
