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
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.wst.dtd.core.internal.emf.DTDElementContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDGroupContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDGroupKind;
import org.eclipse.wst.dtd.core.internal.emf.DTDPackage;
import org.eclipse.wst.dtd.core.internal.emf.util.DTDPrinter;


/**
 * @generated
 */
public class DTDGroupContentImpl extends DTDRepeatableContentImpl implements DTDGroupContent {
	/**
	 * The default value of the '{@link #getGroupKind() <em>Group Kind</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
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
	protected EList content;

	public DTDGroupContentImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return DTDPackage.Literals.DTD_GROUP_CONTENT;
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
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case DTDPackage.DTD_GROUP_CONTENT__CONTENT:
				return ((InternalEList)getContent()).basicAdd(otherEnd, msgs);
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
			case DTDPackage.DTD_GROUP_CONTENT__CONTENT:
				return ((InternalEList)getContent()).basicRemove(otherEnd, msgs);
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
			case DTDPackage.DTD_GROUP_CONTENT__GROUP_KIND:
				return getGroupKind();
			case DTDPackage.DTD_GROUP_CONTENT__CONTENT:
				return getContent();
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
			case DTDPackage.DTD_GROUP_CONTENT__GROUP_KIND:
				setGroupKind((DTDGroupKind)newValue);
				return;
			case DTDPackage.DTD_GROUP_CONTENT__CONTENT:
				getContent().clear();
				getContent().addAll((Collection)newValue);
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
			case DTDPackage.DTD_GROUP_CONTENT__GROUP_KIND:
				setGroupKind(GROUP_KIND_EDEFAULT);
				return;
			case DTDPackage.DTD_GROUP_CONTENT__CONTENT:
				getContent().clear();
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
			case DTDPackage.DTD_GROUP_CONTENT__GROUP_KIND:
				return groupKind != GROUP_KIND_EDEFAULT;
			case DTDPackage.DTD_GROUP_CONTENT__CONTENT:
				return content != null && !content.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (groupKind: ");
		result.append(groupKind);
		result.append(')');
		return result.toString();
	}

}
