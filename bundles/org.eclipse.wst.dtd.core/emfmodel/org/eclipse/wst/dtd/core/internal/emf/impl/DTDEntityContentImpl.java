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
	 * @generated
	 */
	protected EClass eStaticClass() {
		return DTDPackage.Literals.DTD_ENTITY_CONTENT;
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
		if (eContainerFeatureID() != DTDPackage.DTD_ENTITY_CONTENT__DTD_ENTITY) return null;
		return (DTDEntity)eContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetDTDEntity(DTDEntity newDTDEntity, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject)newDTDEntity, DTDPackage.DTD_ENTITY_CONTENT__DTD_ENTITY, msgs);
		return msgs;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public void setDTDEntity(DTDEntity newDTDEntity) {
		if (newDTDEntity != eInternalContainer() || (eContainerFeatureID() != DTDPackage.DTD_ENTITY_CONTENT__DTD_ENTITY && newDTDEntity != null)) {
			if (EcoreUtil.isAncestor(this, newDTDEntity))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newDTDEntity != null)
				msgs = ((InternalEObject)newDTDEntity).eInverseAdd(this, DTDPackage.DTD_ENTITY__CONTENT, DTDEntity.class, msgs);
			msgs = basicSetDTDEntity(newDTDEntity, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ENTITY_CONTENT__DTD_ENTITY, newDTDEntity, newDTDEntity));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case DTDPackage.DTD_ENTITY_CONTENT__DTD_ENTITY:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetDTDEntity((DTDEntity)otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case DTDPackage.DTD_ENTITY_CONTENT__DTD_ENTITY:
				return basicSetDTDEntity(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
		switch (eContainerFeatureID()) {
			case DTDPackage.DTD_ENTITY_CONTENT__DTD_ENTITY:
				return eInternalContainer().eInverseRemove(this, DTDPackage.DTD_ENTITY__CONTENT, DTDEntity.class, msgs);
		}
		return super.eBasicRemoveFromContainerFeature(msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DTDPackage.DTD_ENTITY_CONTENT__DTD_ENTITY:
				return getDTDEntity();
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
			case DTDPackage.DTD_ENTITY_CONTENT__DTD_ENTITY:
				setDTDEntity((DTDEntity)newValue);
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
			case DTDPackage.DTD_ENTITY_CONTENT__DTD_ENTITY:
				setDTDEntity((DTDEntity)null);
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
			case DTDPackage.DTD_ENTITY_CONTENT__DTD_ENTITY:
				return getDTDEntity() != null;
		}
		return super.eIsSet(featureID);
	}

	/*
	 * @see DTDEntityContent#unparse()
	 */
	public String unparse() {
		return null;
	}

}
