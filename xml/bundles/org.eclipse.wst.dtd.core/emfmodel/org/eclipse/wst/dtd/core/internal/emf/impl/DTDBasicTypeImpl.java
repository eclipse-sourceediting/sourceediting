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
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.wst.dtd.core.internal.DTDCoreMessages;
import org.eclipse.wst.dtd.core.internal.emf.DTDBasicType;
import org.eclipse.wst.dtd.core.internal.emf.DTDBasicTypeKind;
import org.eclipse.wst.dtd.core.internal.emf.DTDObject;
import org.eclipse.wst.dtd.core.internal.emf.DTDPackage;
import org.eclipse.wst.dtd.core.internal.emf.DTDType;


/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Basic Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDBasicTypeImpl#getKind <em>Kind</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DTDBasicTypeImpl extends EClassImpl implements DTDBasicType {

	public final static String basicTypeStrings[] = {DTDType.NONE, DTDType.CDATA, DTDType.ID, DTDType.IDREF, DTDType.IDREFS, DTDType.ENTITY, DTDType.ENTITIES, DTDType.NMTOKEN, DTDType.NMTOKENS};

	public final static String basicTypeDescriptions[] = {DTDCoreMessages._UI_NONE_DESC, DTDCoreMessages._UI_CHARACTER_DATA_DESC, DTDCoreMessages._UI_IDENTIFIER_DESC, DTDCoreMessages._UI_ID_REFERENCE_DESC, DTDCoreMessages._UI_ID_REFERENCES_DESC, DTDCoreMessages._UI_ENTITY_NAME_DESC, DTDCoreMessages._UI_ENTITY_NAMES_DESC, DTDCoreMessages._UI_NAME_TOKEN_DESC, DTDCoreMessages._UI_NAME_TOKENS_DESC}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$

	public final static int basicTypeKinds[] = {DTDBasicTypeKind.NONE, DTDBasicTypeKind.CDATA, DTDBasicTypeKind.ID, DTDBasicTypeKind.IDREF, DTDBasicTypeKind.IDREFS, DTDBasicTypeKind.ENTITY, DTDBasicTypeKind.ENTITIES, DTDBasicTypeKind.NMTOKEN, DTDBasicTypeKind.NMTOKENS};

	public String unparse() {
		return getTypeString();
	}

	public String getTypeString() {
		int kind = getKind().getValue();
		if (kind >= 0 && kind < basicTypeStrings.length) {
			return basicTypeStrings[kind];
		} // end of if ()

		return null;
	}


	public String getTypeDescription() {
		return getTypeDescription(getKind().getValue());
	}


	public static String getTypeDescription(int typeKind) {
		if (typeKind >= 0 && typeKind < basicTypeDescriptions.length) {
			return basicTypeDescriptions[typeKind];
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
	 * The default value of the '{@link #getKind() <em>Kind</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getKind()
	 * @generated
	 * @ordered
	 */
	protected static final DTDBasicTypeKind KIND_EDEFAULT = DTDBasicTypeKind.NONE_LITERAL;

	/**
	 * The cached value of the '{@link #getKind() <em>Kind</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getKind()
	 * @generated
	 * @ordered
	 */
	protected DTDBasicTypeKind kind = KIND_EDEFAULT;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected DTDBasicTypeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return DTDPackage.Literals.DTD_BASIC_TYPE;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public DTDBasicTypeKind getKind() {
		return kind;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setKind(DTDBasicTypeKind newKind) {
		DTDBasicTypeKind oldKind = kind;
		kind = newKind == null ? KIND_EDEFAULT : newKind;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_BASIC_TYPE__KIND, oldKind, kind));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DTDPackage.DTD_BASIC_TYPE__KIND:
				return getKind();
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
			case DTDPackage.DTD_BASIC_TYPE__KIND:
				setKind((DTDBasicTypeKind)newValue);
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
			case DTDPackage.DTD_BASIC_TYPE__KIND:
				setKind(KIND_EDEFAULT);
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
			case DTDPackage.DTD_BASIC_TYPE__KIND:
				return kind != KIND_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (kind: ");
		result.append(kind);
		result.append(')');
		return result.toString();
	}

} // DTDBasicTypeImpl
