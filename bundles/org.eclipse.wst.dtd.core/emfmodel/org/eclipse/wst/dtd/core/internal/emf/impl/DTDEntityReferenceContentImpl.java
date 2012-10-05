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
import org.eclipse.wst.dtd.core.internal.emf.DTDEntity;
import org.eclipse.wst.dtd.core.internal.emf.DTDEntityReferenceContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDLexicalInfo;
import org.eclipse.wst.dtd.core.internal.emf.DTDPackage;


/**
 * @generated
 */
public class DTDEntityReferenceContentImpl extends DTDRepeatableContentImpl implements DTDEntityReferenceContent {

	public DTDEntityReferenceContentImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return DTDPackage.Literals.DTD_ENTITY_REFERENCE_CONTENT;
	}

	public String getContentName() {
		DTDEntity e = getElementReferencedEntity();
		if (e == null)
			return ""; //$NON-NLS-1$
		return e.getName();
	}

	public String unparseRepeatableContent() {
		DTDEntity entityRef = getElementReferencedEntity();
		if (entityRef.isParameterEntity()) {
			return "%" + getContentName() + ";"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		else {
			return "&" + getContentName() + ";"; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	// ugly hack for now since we don't have multiple inheritance.
	// Would rather have all this stuff in a base class but these
	// classes are inheriting from sometimes different mof classes
	DTDLexicalInfo lexInfo = new DTDLexicalInfo();
	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	protected DTDEntity elementReferencedEntity;

	/**
	 * Get the value of startOffset.
	 * 
	 * @return value of startOffset.
	 */
	public int getStartOffset() {
		return lexInfo.getStartOffset();
	}

	/**
	 * Set the value of startOffset.
	 * 
	 * @param v
	 *            Value to assign to startOffset.
	 */
	public void setStartOffset(int v) {
		lexInfo.setStartOffset(v);
	}

	/**
	 * Get the value of endOffset.
	 * 
	 * @return value of endOffset.
	 */
	public int getEndOffset() {
		return lexInfo.getEndOffset();
	}

	/**
	 * Set the value of endOffset.
	 * 
	 * @param v
	 *            Value to assign to endOffset.
	 */
	public void setEndOffset(int v) {
		lexInfo.setEndOffset(v);
	}

	/**
	 * @generated This field/method will be replaced during code generation
	 */
	public DTDEntity getElementReferencedEntity() {
		if (elementReferencedEntity != null && elementReferencedEntity.eIsProxy()) {
			InternalEObject oldElementReferencedEntity = (InternalEObject)elementReferencedEntity;
			elementReferencedEntity = (DTDEntity)eResolveProxy(oldElementReferencedEntity);
			if (elementReferencedEntity != oldElementReferencedEntity) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__ELEMENT_REFERENCED_ENTITY, oldElementReferencedEntity, elementReferencedEntity));
			}
		}
		return elementReferencedEntity;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public DTDEntity basicGetElementReferencedEntity() {
		return elementReferencedEntity;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetElementReferencedEntity(DTDEntity newElementReferencedEntity, NotificationChain msgs) {
		DTDEntity oldElementReferencedEntity = elementReferencedEntity;
		elementReferencedEntity = newElementReferencedEntity;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__ELEMENT_REFERENCED_ENTITY, oldElementReferencedEntity, newElementReferencedEntity);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public void setElementReferencedEntity(DTDEntity newElementReferencedEntity) {
		if (newElementReferencedEntity != elementReferencedEntity) {
			NotificationChain msgs = null;
			if (elementReferencedEntity != null)
				msgs = ((InternalEObject)elementReferencedEntity).eInverseRemove(this, DTDPackage.DTD_ENTITY__ENTITY_REFERENCE, DTDEntity.class, msgs);
			if (newElementReferencedEntity != null)
				msgs = ((InternalEObject)newElementReferencedEntity).eInverseAdd(this, DTDPackage.DTD_ENTITY__ENTITY_REFERENCE, DTDEntity.class, msgs);
			msgs = basicSetElementReferencedEntity(newElementReferencedEntity, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__ELEMENT_REFERENCED_ENTITY, newElementReferencedEntity, newElementReferencedEntity));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__ELEMENT_REFERENCED_ENTITY:
				if (elementReferencedEntity != null)
					msgs = ((InternalEObject)elementReferencedEntity).eInverseRemove(this, DTDPackage.DTD_ENTITY__ENTITY_REFERENCE, DTDEntity.class, msgs);
				return basicSetElementReferencedEntity((DTDEntity)otherEnd, msgs);
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
			case DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__ELEMENT_REFERENCED_ENTITY:
				return basicSetElementReferencedEntity(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__ELEMENT_REFERENCED_ENTITY:
				if (resolve) return getElementReferencedEntity();
				return basicGetElementReferencedEntity();
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
			case DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__ELEMENT_REFERENCED_ENTITY:
				setElementReferencedEntity((DTDEntity)newValue);
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
			case DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__ELEMENT_REFERENCED_ENTITY:
				setElementReferencedEntity((DTDEntity)null);
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
			case DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__ELEMENT_REFERENCED_ENTITY:
				return elementReferencedEntity != null;
		}
		return super.eIsSet(featureID);
	}

}
