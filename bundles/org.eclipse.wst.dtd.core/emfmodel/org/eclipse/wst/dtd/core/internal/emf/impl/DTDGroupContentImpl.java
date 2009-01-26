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

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.wst.dtd.core.internal.emf.DTDElement;
import org.eclipse.wst.dtd.core.internal.emf.DTDElementContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDGroupContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDGroupKind;
import org.eclipse.wst.dtd.core.internal.emf.DTDOccurrenceType;
import org.eclipse.wst.dtd.core.internal.emf.DTDPackage;
import org.eclipse.wst.dtd.core.internal.emf.util.DTDPrinter;


/**
 * @generated
 */
public class DTDGroupContentImpl extends DTDRepeatableContentImpl implements DTDGroupContent {
	/**
	 * The default value of the '{@link #getGroupKind() <em>Group Kind</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getGroupKind()
	 * @generated
	 * @ordered
	 */
	protected static final DTDGroupKind GROUP_KIND_EDEFAULT = DTDGroupKind.SEQUENCE_LITERAL;

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	protected DTDGroupKind groupKind = GROUP_KIND_EDEFAULT;
	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	protected EList content = null;

	public DTDGroupContentImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected EClass eStaticClass() {
		return DTDPackage.eINSTANCE.getDTDGroupContent();
	}

	public int getContentPosition(DTDElementContent content) {
		EList list = getContent();
		return list.indexOf(content);
	}

	public String getContentName() {
		return ""; //$NON-NLS-1$
	}

	public String getContentDetail() {
		DTDPrinter printer = new DTDPrinter(false);
		printer.visitDTDGroupContent(this);

		return printer.getBuffer().toString();
	}

	public String unparseRepeatableContent() {
		return ""; //$NON-NLS-1$
	}

	/**
	 * @generated This field/method will be replaced during code generation
	 */
	public DTDGroupKind getGroupKind() {
		return groupKind;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setGroupKind(DTDGroupKind newGroupKind) {
		DTDGroupKind oldGroupKind = groupKind;
		groupKind = newGroupKind == null ? GROUP_KIND_EDEFAULT : newGroupKind;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_GROUP_CONTENT__GROUP_KIND, oldGroupKind, groupKind));
	}

	/**
	 * @generated This field/method will be replaced during code generation
	 */
	public EList getContent() {
		if (content == null) {
			content = new EObjectContainmentWithInverseEList(DTDElementContent.class, this, DTDPackage.DTD_GROUP_CONTENT__CONTENT, DTDPackage.DTD_ELEMENT_CONTENT__GROUP);
		}
		return content;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
		if (featureID >= 0) {
			switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
				case DTDPackage.DTD_GROUP_CONTENT__GROUP :
					if (eContainer != null)
						msgs = eBasicRemoveFromContainer(msgs);
					return eBasicSetContainer(otherEnd, DTDPackage.DTD_GROUP_CONTENT__GROUP, msgs);
				case DTDPackage.DTD_GROUP_CONTENT__ELEMENT :
					if (eContainer != null)
						msgs = eBasicRemoveFromContainer(msgs);
					return eBasicSetContainer(otherEnd, DTDPackage.DTD_GROUP_CONTENT__ELEMENT, msgs);
				case DTDPackage.DTD_GROUP_CONTENT__CONTENT :
					return ((InternalEList) getContent()).basicAdd(otherEnd, msgs);
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
				case DTDPackage.DTD_GROUP_CONTENT__GROUP :
					return eBasicSetContainer(null, DTDPackage.DTD_GROUP_CONTENT__GROUP, msgs);
				case DTDPackage.DTD_GROUP_CONTENT__ELEMENT :
					return eBasicSetContainer(null, DTDPackage.DTD_GROUP_CONTENT__ELEMENT, msgs);
				case DTDPackage.DTD_GROUP_CONTENT__CONTENT :
					return ((InternalEList) getContent()).basicRemove(otherEnd, msgs);
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
				case DTDPackage.DTD_GROUP_CONTENT__GROUP :
					return eContainer.eInverseRemove(this, DTDPackage.DTD_GROUP_CONTENT__CONTENT, DTDGroupContent.class, msgs);
				case DTDPackage.DTD_GROUP_CONTENT__ELEMENT :
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
			case DTDPackage.DTD_GROUP_CONTENT__GROUP :
				return getGroup();
			case DTDPackage.DTD_GROUP_CONTENT__ELEMENT :
				return getElement();
			case DTDPackage.DTD_GROUP_CONTENT__OCCURRENCE :
				return getOccurrence();
			case DTDPackage.DTD_GROUP_CONTENT__GROUP_KIND :
				return getGroupKind();
			case DTDPackage.DTD_GROUP_CONTENT__CONTENT :
				return getContent();
		}
		return eDynamicGet(eFeature, resolve);
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public boolean eIsSet(EStructuralFeature eFeature) {
		switch (eDerivedStructuralFeatureID(eFeature.getFeatureID(), eFeature.getContainerClass())) {
			case DTDPackage.DTD_GROUP_CONTENT__GROUP :
				return getGroup() != null;
			case DTDPackage.DTD_GROUP_CONTENT__ELEMENT :
				return getElement() != null;
			case DTDPackage.DTD_GROUP_CONTENT__OCCURRENCE :
				return occurrence != OCCURRENCE_EDEFAULT;
			case DTDPackage.DTD_GROUP_CONTENT__GROUP_KIND :
				return groupKind != GROUP_KIND_EDEFAULT;
			case DTDPackage.DTD_GROUP_CONTENT__CONTENT :
				return content != null && !getContent().isEmpty();
		}
		return eDynamicIsSet(eFeature);
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public void eSet(EStructuralFeature eFeature, Object newValue) {
		switch (eDerivedStructuralFeatureID(eFeature.getFeatureID(), eFeature.getContainerClass())) {
			case DTDPackage.DTD_GROUP_CONTENT__GROUP :
				setGroup((DTDGroupContent) newValue);
				return;
			case DTDPackage.DTD_GROUP_CONTENT__ELEMENT :
				setElement((DTDElement) newValue);
				return;
			case DTDPackage.DTD_GROUP_CONTENT__OCCURRENCE :
				setOccurrence((DTDOccurrenceType) newValue);
				return;
			case DTDPackage.DTD_GROUP_CONTENT__GROUP_KIND :
				setGroupKind((DTDGroupKind) newValue);
				return;
			case DTDPackage.DTD_GROUP_CONTENT__CONTENT :
				getContent().clear();
				getContent().addAll((Collection) newValue);
				return;
		}
		eDynamicSet(eFeature, newValue);
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public void eUnset(EStructuralFeature eFeature) {
		switch (eDerivedStructuralFeatureID(eFeature.getFeatureID(), eFeature.getContainerClass())) {
			case DTDPackage.DTD_GROUP_CONTENT__GROUP :
				setGroup((DTDGroupContent) null);
				return;
			case DTDPackage.DTD_GROUP_CONTENT__ELEMENT :
				setElement((DTDElement) null);
				return;
			case DTDPackage.DTD_GROUP_CONTENT__OCCURRENCE :
				setOccurrence(OCCURRENCE_EDEFAULT);
				return;
			case DTDPackage.DTD_GROUP_CONTENT__GROUP_KIND :
				setGroupKind(GROUP_KIND_EDEFAULT);
				return;
			case DTDPackage.DTD_GROUP_CONTENT__CONTENT :
				getContent().clear();
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
		result.append(" (groupKind: "); //$NON-NLS-1$
		result.append(groupKind);
		result.append(')');
		return result.toString();
	}

}
