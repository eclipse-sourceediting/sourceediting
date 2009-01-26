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
import org.eclipse.emf.common.notify.impl.NotificationChainImpl;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.wst.dtd.core.internal.emf.DTDElement;
import org.eclipse.wst.dtd.core.internal.emf.DTDEntity;
import org.eclipse.wst.dtd.core.internal.emf.DTDEntityReferenceContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDGroupContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDLexicalInfo;
import org.eclipse.wst.dtd.core.internal.emf.DTDOccurrenceType;
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
	 * 
	 * @generated
	 */
	protected EClass eStaticClass() {
		return DTDPackage.eINSTANCE.getDTDEntityReferenceContent();
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
	protected DTDEntity elementReferencedEntity = null;

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
			DTDEntity oldElementReferencedEntity = elementReferencedEntity;
			elementReferencedEntity = (DTDEntity) EcoreUtil.resolve(elementReferencedEntity, this);
			if (elementReferencedEntity != oldElementReferencedEntity) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__ELEMENT_REFERENCED_ENTITY, oldElementReferencedEntity, elementReferencedEntity));
			}
		}
		return elementReferencedEntity;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public DTDEntity basicGetElementReferencedEntity() {
		return elementReferencedEntity;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetElementReferencedEntity(DTDEntity newElementReferencedEntity, NotificationChain msgs) {
		DTDEntity oldElementReferencedEntity = elementReferencedEntity;
		elementReferencedEntity = newElementReferencedEntity;
		if (eNotificationRequired()) {
			if (msgs == null)
				msgs = new NotificationChainImpl(4);
			msgs.add(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__ELEMENT_REFERENCED_ENTITY, oldElementReferencedEntity, newElementReferencedEntity));
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
				msgs = ((InternalEObject) elementReferencedEntity).eInverseRemove(this, DTDPackage.DTD_ENTITY__ENTITY_REFERENCE, DTDEntity.class, msgs);
			if (newElementReferencedEntity != null)
				msgs = ((InternalEObject) newElementReferencedEntity).eInverseAdd(this, DTDPackage.DTD_ENTITY__ENTITY_REFERENCE, DTDEntity.class, msgs);
			msgs = basicSetElementReferencedEntity(newElementReferencedEntity, msgs);
			if (msgs != null)
				msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__ELEMENT_REFERENCED_ENTITY, newElementReferencedEntity, newElementReferencedEntity));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
		if (featureID >= 0) {
			switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
				case DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__GROUP :
					if (eContainer != null)
						msgs = eBasicRemoveFromContainer(msgs);
					return eBasicSetContainer(otherEnd, DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__GROUP, msgs);
				case DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__ELEMENT :
					if (eContainer != null)
						msgs = eBasicRemoveFromContainer(msgs);
					return eBasicSetContainer(otherEnd, DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__ELEMENT, msgs);
				case DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__ELEMENT_REFERENCED_ENTITY :
					if (elementReferencedEntity != null)
						msgs = ((InternalEObject) elementReferencedEntity).eInverseRemove(this, DTDPackage.DTD_ENTITY__ENTITY_REFERENCE, DTDEntity.class, msgs);
					return basicSetElementReferencedEntity((DTDEntity) otherEnd, msgs);
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
				case DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__GROUP :
					return eBasicSetContainer(null, DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__GROUP, msgs);
				case DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__ELEMENT :
					return eBasicSetContainer(null, DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__ELEMENT, msgs);
				case DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__ELEMENT_REFERENCED_ENTITY :
					return basicSetElementReferencedEntity(null, msgs);
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
				case DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__GROUP :
					return eContainer.eInverseRemove(this, DTDPackage.DTD_GROUP_CONTENT__CONTENT, DTDGroupContent.class, msgs);
				case DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__ELEMENT :
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
			case DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__GROUP :
				return getGroup();
			case DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__ELEMENT :
				return getElement();
			case DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__OCCURRENCE :
				return getOccurrence();
			case DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__ELEMENT_REFERENCED_ENTITY :
				if (resolve)
					return getElementReferencedEntity();
				return basicGetElementReferencedEntity();
		}
		return eDynamicGet(eFeature, resolve);
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public boolean eIsSet(EStructuralFeature eFeature) {
		switch (eDerivedStructuralFeatureID(eFeature.getFeatureID(), eFeature.getContainerClass())) {
			case DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__GROUP :
				return getGroup() != null;
			case DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__ELEMENT :
				return getElement() != null;
			case DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__OCCURRENCE :
				return occurrence != OCCURRENCE_EDEFAULT;
			case DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__ELEMENT_REFERENCED_ENTITY :
				return elementReferencedEntity != null;
		}
		return eDynamicIsSet(eFeature);
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public void eSet(EStructuralFeature eFeature, Object newValue) {
		switch (eDerivedStructuralFeatureID(eFeature.getFeatureID(), eFeature.getContainerClass())) {
			case DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__GROUP :
				setGroup((DTDGroupContent) newValue);
				return;
			case DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__ELEMENT :
				setElement((DTDElement) newValue);
				return;
			case DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__OCCURRENCE :
				setOccurrence((DTDOccurrenceType) newValue);
				return;
			case DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__ELEMENT_REFERENCED_ENTITY :
				setElementReferencedEntity((DTDEntity) newValue);
				return;
		}
		eDynamicSet(eFeature, newValue);
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public void eUnset(EStructuralFeature eFeature) {
		switch (eDerivedStructuralFeatureID(eFeature.getFeatureID(), eFeature.getContainerClass())) {
			case DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__GROUP :
				setGroup((DTDGroupContent) null);
				return;
			case DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__ELEMENT :
				setElement((DTDElement) null);
				return;
			case DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__OCCURRENCE :
				setOccurrence(OCCURRENCE_EDEFAULT);
				return;
			case DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__ELEMENT_REFERENCED_ENTITY :
				setElementReferencedEntity((DTDEntity) null);
				return;
		}
		eDynamicUnset(eFeature);
	}

}
