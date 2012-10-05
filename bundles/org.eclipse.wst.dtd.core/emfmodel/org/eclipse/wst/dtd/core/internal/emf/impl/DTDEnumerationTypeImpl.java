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
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EEnumImpl;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.wst.dtd.core.internal.DTDCoreMessages;
import org.eclipse.wst.dtd.core.internal.emf.DTDEnumGroupKind;
import org.eclipse.wst.dtd.core.internal.emf.DTDEnumerationType;
import org.eclipse.wst.dtd.core.internal.emf.DTDFile;
import org.eclipse.wst.dtd.core.internal.emf.DTDObject;
import org.eclipse.wst.dtd.core.internal.emf.DTDPackage;


/**
 * @generated
 */
public class DTDEnumerationTypeImpl extends EEnumImpl implements DTDEnumerationType {
	/**
	 * The default value of the '{@link #getKind() <em>Kind</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getKind()
	 * @generated
	 * @ordered
	 */
	protected static final DTDEnumGroupKind KIND_EDEFAULT = DTDEnumGroupKind.NAME_TOKEN_GROUP_LITERAL;

	public final static String enumerationTypeDescriptions[] = {DTDCoreMessages._UI_ENUM_NAME_TOKENS_DESC, DTDCoreMessages._UI_ENUM_NOTATION_DESC}; //$NON-NLS-1$ //$NON-NLS-2$

	public final static int enumerationTypeKinds[] = {DTDEnumGroupKind.NAME_TOKEN_GROUP, DTDEnumGroupKind.NOTATION_GROUP};

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	protected DTDEnumGroupKind kind = KIND_EDEFAULT;

	public DTDEnumerationTypeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return DTDPackage.Literals.DTD_ENUMERATION_TYPE;
	}

	public EList getEnumLiterals() {
		return getELiterals();
	}

	public String getEnumerationTypeDescription() {
		return getEnumerationTypeDescription(getKind().getValue());
	}

	public static String getEnumerationTypeDescription(int kind) {
		// it can only be 1 or 2, but maybe later...
		if (kind >= 1 && kind <= 2) {
			// we subtract 1 since the kind is 1 - based not zero based
			return enumerationTypeDescriptions[kind - 1];
		}
		return null;
	}

	public String getPathname() {
		// TBD
		return ""; //$NON-NLS-1$
	}

	public DTDObject findObject(String relativePath) {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation
	 */
	public DTDEnumGroupKind getKind() {
		return kind;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setKind(DTDEnumGroupKind newKind) {
		DTDEnumGroupKind oldKind = kind;
		kind = newKind == null ? KIND_EDEFAULT : newKind;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ENUMERATION_TYPE__KIND, oldKind, kind));
	}

	/**
	 * @generated This field/method will be replaced during code generation
	 */
	public DTDFile getDTDFile() {
		if (eContainerFeatureID() != DTDPackage.DTD_ENUMERATION_TYPE__DTD_FILE) return null;
		return (DTDFile)eContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetDTDFile(DTDFile newDTDFile, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject)newDTDFile, DTDPackage.DTD_ENUMERATION_TYPE__DTD_FILE, msgs);
		return msgs;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public void setDTDFile(DTDFile newDTDFile) {
		if (newDTDFile != eInternalContainer() || (eContainerFeatureID() != DTDPackage.DTD_ENUMERATION_TYPE__DTD_FILE && newDTDFile != null)) {
			if (EcoreUtil.isAncestor(this, newDTDFile))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newDTDFile != null)
				msgs = ((InternalEObject)newDTDFile).eInverseAdd(this, DTDPackage.DTD_FILE__DTD_ENUMERATION_TYPE, DTDFile.class, msgs);
			msgs = basicSetDTDFile(newDTDFile, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ENUMERATION_TYPE__DTD_FILE, newDTDFile, newDTDFile));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case DTDPackage.DTD_ENUMERATION_TYPE__DTD_FILE:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetDTDFile((DTDFile)otherEnd, msgs);
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
			case DTDPackage.DTD_ENUMERATION_TYPE__DTD_FILE:
				return basicSetDTDFile(null, msgs);
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
			case DTDPackage.DTD_ENUMERATION_TYPE__DTD_FILE:
				return eInternalContainer().eInverseRemove(this, DTDPackage.DTD_FILE__DTD_ENUMERATION_TYPE, DTDFile.class, msgs);
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
			case DTDPackage.DTD_ENUMERATION_TYPE__KIND:
				return getKind();
			case DTDPackage.DTD_ENUMERATION_TYPE__DTD_FILE:
				return getDTDFile();
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
			case DTDPackage.DTD_ENUMERATION_TYPE__KIND:
				setKind((DTDEnumGroupKind)newValue);
				return;
			case DTDPackage.DTD_ENUMERATION_TYPE__DTD_FILE:
				setDTDFile((DTDFile)newValue);
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
			case DTDPackage.DTD_ENUMERATION_TYPE__KIND:
				setKind(KIND_EDEFAULT);
				return;
			case DTDPackage.DTD_ENUMERATION_TYPE__DTD_FILE:
				setDTDFile((DTDFile)null);
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
			case DTDPackage.DTD_ENUMERATION_TYPE__KIND:
				return kind != KIND_EDEFAULT;
			case DTDPackage.DTD_ENUMERATION_TYPE__DTD_FILE:
				return getDTDFile() != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (kind: ");
		result.append(kind);
		result.append(')');
		return result.toString();
	}

}
