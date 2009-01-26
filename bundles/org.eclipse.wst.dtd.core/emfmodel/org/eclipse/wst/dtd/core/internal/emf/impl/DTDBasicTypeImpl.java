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
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.InternalEList;
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
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.internal.impl.DTDBasicTypeImpl#getKind <em>Kind</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class DTDBasicTypeImpl extends EClassImpl implements DTDBasicType, EClass {

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
	 * The default value of the '{@link #getKind() <em>Kind</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getKind()
	 * @generated
	 * @ordered
	 */
	protected static final DTDBasicTypeKind KIND_EDEFAULT = DTDBasicTypeKind.NONE_LITERAL;

	/**
	 * The cached value of the '{@link #getKind() <em>Kind</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getKind()
	 * @generated
	 * @ordered
	 */
	protected DTDBasicTypeKind kind = KIND_EDEFAULT;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected DTDBasicTypeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected EClass eStaticClass() {
		return DTDPackage.eINSTANCE.getDTDBasicType();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public DTDBasicTypeKind getKind() {
		return kind;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setKind(DTDBasicTypeKind newKind) {
		DTDBasicTypeKind oldKind = kind;
		kind = newKind == null ? KIND_EDEFAULT : newKind;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_BASIC_TYPE__KIND, oldKind, kind));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
		if (featureID >= 0) {
			switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
				case DTDPackage.DTD_BASIC_TYPE__EANNOTATIONS :
					return ((InternalEList) getEAnnotations()).basicAdd(otherEnd, msgs);
				case DTDPackage.DTD_BASIC_TYPE__EPACKAGE :
					if (eContainer != null)
						msgs = eBasicRemoveFromContainer(msgs);
					return eBasicSetContainer(otherEnd, DTDPackage.DTD_BASIC_TYPE__EPACKAGE, msgs);
				case DTDPackage.DTD_BASIC_TYPE__EOPERATIONS :
					return ((InternalEList) getEOperations()).basicAdd(otherEnd, msgs);
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
				case DTDPackage.DTD_BASIC_TYPE__EANNOTATIONS :
					return ((InternalEList) getEAnnotations()).basicRemove(otherEnd, msgs);
				case DTDPackage.DTD_BASIC_TYPE__EPACKAGE :
					return eBasicSetContainer(null, DTDPackage.DTD_BASIC_TYPE__EPACKAGE, msgs);
				case DTDPackage.DTD_BASIC_TYPE__EOPERATIONS :
					return ((InternalEList) getEOperations()).basicRemove(otherEnd, msgs);
				case DTDPackage.DTD_BASIC_TYPE__EREFERENCES :
					return ((InternalEList) getEReferences()).basicRemove(otherEnd, msgs);
				case DTDPackage.DTD_BASIC_TYPE__EATTRIBUTES :
					return ((InternalEList) getEAttributes()).basicRemove(otherEnd, msgs);
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
				case DTDPackage.DTD_BASIC_TYPE__EPACKAGE :
					return eContainer.eInverseRemove(this, EcorePackage.EPACKAGE__ECLASSIFIERS, EPackage.class, msgs);
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
			case DTDPackage.DTD_BASIC_TYPE__EANNOTATIONS :
				return getEAnnotations();
			case DTDPackage.DTD_BASIC_TYPE__NAME :
				return getName();
			case DTDPackage.DTD_BASIC_TYPE__INSTANCE_CLASS_NAME :
				return getInstanceClassName();
			case DTDPackage.DTD_BASIC_TYPE__INSTANCE_CLASS :
				return getInstanceClass();
			case DTDPackage.DTD_BASIC_TYPE__DEFAULT_VALUE :
				return getDefaultValue();
			case DTDPackage.DTD_BASIC_TYPE__EPACKAGE :
				return getEPackage();
			case DTDPackage.DTD_BASIC_TYPE__ABSTRACT :
				return isAbstract() ? Boolean.TRUE : Boolean.FALSE;
			case DTDPackage.DTD_BASIC_TYPE__INTERFACE :
				return isInterface() ? Boolean.TRUE : Boolean.FALSE;
			case DTDPackage.DTD_BASIC_TYPE__ESUPER_TYPES :
				return getESuperTypes();
			case DTDPackage.DTD_BASIC_TYPE__EOPERATIONS :
				return getEOperations();
			case DTDPackage.DTD_BASIC_TYPE__EALL_ATTRIBUTES :
				return getEAllAttributes();
			case DTDPackage.DTD_BASIC_TYPE__EALL_REFERENCES :
				return getEAllReferences();
			case DTDPackage.DTD_BASIC_TYPE__EREFERENCES :
				return getEReferences();
			case DTDPackage.DTD_BASIC_TYPE__EATTRIBUTES :
				return getEAttributes();
			case DTDPackage.DTD_BASIC_TYPE__EALL_CONTAINMENTS :
				return getEAllContainments();
			case DTDPackage.DTD_BASIC_TYPE__EALL_OPERATIONS :
				return getEAllOperations();
			case DTDPackage.DTD_BASIC_TYPE__EALL_STRUCTURAL_FEATURES :
				return getEAllStructuralFeatures();
			case DTDPackage.DTD_BASIC_TYPE__EALL_SUPER_TYPES :
				return getEAllSuperTypes();
			case DTDPackage.DTD_BASIC_TYPE__EID_ATTRIBUTE :
				return getEIDAttribute();
			case DTDPackage.DTD_BASIC_TYPE__KIND :
				return getKind();
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
			case DTDPackage.DTD_BASIC_TYPE__EANNOTATIONS :
				getEAnnotations().clear();
				getEAnnotations().addAll((Collection) newValue);
				return;
			case DTDPackage.DTD_BASIC_TYPE__NAME :
				setName((String) newValue);
				return;
			case DTDPackage.DTD_BASIC_TYPE__INSTANCE_CLASS_NAME :
				setInstanceClassName((String) newValue);
				return;
			case DTDPackage.DTD_BASIC_TYPE__INSTANCE_CLASS :
				setInstanceClass((Class) newValue);
				return;
			case DTDPackage.DTD_BASIC_TYPE__ABSTRACT :
				setAbstract(((Boolean) newValue).booleanValue());
				return;
			case DTDPackage.DTD_BASIC_TYPE__INTERFACE :
				setInterface(((Boolean) newValue).booleanValue());
				return;
			case DTDPackage.DTD_BASIC_TYPE__ESUPER_TYPES :
				getESuperTypes().clear();
				getESuperTypes().addAll((Collection) newValue);
				return;
			case DTDPackage.DTD_BASIC_TYPE__EOPERATIONS :
				getEOperations().clear();
				getEOperations().addAll((Collection) newValue);
				return;
			case DTDPackage.DTD_BASIC_TYPE__EREFERENCES :
				getEReferences().clear();
				getEReferences().addAll((Collection) newValue);
				return;
			case DTDPackage.DTD_BASIC_TYPE__EATTRIBUTES :
				getEAttributes().clear();
				getEAttributes().addAll((Collection) newValue);
				return;
			case DTDPackage.DTD_BASIC_TYPE__KIND :
				setKind((DTDBasicTypeKind) newValue);
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
			case DTDPackage.DTD_BASIC_TYPE__EANNOTATIONS :
				getEAnnotations().clear();
				return;
			case DTDPackage.DTD_BASIC_TYPE__NAME :
				setName(NAME_EDEFAULT);
				return;
			case DTDPackage.DTD_BASIC_TYPE__INSTANCE_CLASS_NAME :
				setInstanceClassName(INSTANCE_CLASS_NAME_EDEFAULT);
				return;
			case DTDPackage.DTD_BASIC_TYPE__INSTANCE_CLASS :
				setInstanceClass(INSTANCE_CLASS_EDEFAULT);
				return;
			case DTDPackage.DTD_BASIC_TYPE__ABSTRACT :
				setAbstract(ABSTRACT_EDEFAULT);
				return;
			case DTDPackage.DTD_BASIC_TYPE__INTERFACE :
				setInterface(INTERFACE_EDEFAULT);
				return;
			case DTDPackage.DTD_BASIC_TYPE__ESUPER_TYPES :
				getESuperTypes().clear();
				return;
			case DTDPackage.DTD_BASIC_TYPE__EOPERATIONS :
				getEOperations().clear();
				return;
			case DTDPackage.DTD_BASIC_TYPE__EREFERENCES :
				getEReferences().clear();
				return;
			case DTDPackage.DTD_BASIC_TYPE__EATTRIBUTES :
				getEAttributes().clear();
				return;
			case DTDPackage.DTD_BASIC_TYPE__KIND :
				setKind(KIND_EDEFAULT);
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
			case DTDPackage.DTD_BASIC_TYPE__EANNOTATIONS :
				return eAnnotations != null && !getEAnnotations().isEmpty();
			case DTDPackage.DTD_BASIC_TYPE__NAME :
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case DTDPackage.DTD_BASIC_TYPE__INSTANCE_CLASS_NAME :
				return INSTANCE_CLASS_NAME_EDEFAULT == null ? instanceClassName != null : !INSTANCE_CLASS_NAME_EDEFAULT.equals(instanceClassName);
			case DTDPackage.DTD_BASIC_TYPE__INSTANCE_CLASS :
				return INSTANCE_CLASS_EDEFAULT == null ? instanceClass != null : !INSTANCE_CLASS_EDEFAULT.equals(instanceClass);
			case DTDPackage.DTD_BASIC_TYPE__DEFAULT_VALUE :
				return getDefaultValue() != null;
			case DTDPackage.DTD_BASIC_TYPE__EPACKAGE :
				return getEPackage() != null;
			case DTDPackage.DTD_BASIC_TYPE__ABSTRACT :
				return ((eFlags & ABSTRACT_EFLAG) != 0) != ABSTRACT_EDEFAULT;
			case DTDPackage.DTD_BASIC_TYPE__INTERFACE :
				return ((eFlags & INTERFACE_EFLAG) != 0) != INTERFACE_EDEFAULT;
			case DTDPackage.DTD_BASIC_TYPE__ESUPER_TYPES :
				return eSuperTypes != null && !getESuperTypes().isEmpty();
			case DTDPackage.DTD_BASIC_TYPE__EOPERATIONS :
				return eOperations != null && !getEOperations().isEmpty();
			case DTDPackage.DTD_BASIC_TYPE__EALL_ATTRIBUTES :
				return !getEAllAttributes().isEmpty();
			case DTDPackage.DTD_BASIC_TYPE__EALL_REFERENCES :
				return !getEAllReferences().isEmpty();
			case DTDPackage.DTD_BASIC_TYPE__EREFERENCES :
				return eReferences != null && !getEReferences().isEmpty();
			case DTDPackage.DTD_BASIC_TYPE__EATTRIBUTES :
				return eAttributes != null && !getEAttributes().isEmpty();
			case DTDPackage.DTD_BASIC_TYPE__EALL_CONTAINMENTS :
				return !getEAllContainments().isEmpty();
			case DTDPackage.DTD_BASIC_TYPE__EALL_OPERATIONS :
				return !getEAllOperations().isEmpty();
			case DTDPackage.DTD_BASIC_TYPE__EALL_STRUCTURAL_FEATURES :
				return !getEAllStructuralFeatures().isEmpty();
			case DTDPackage.DTD_BASIC_TYPE__EALL_SUPER_TYPES :
				return !getEAllSuperTypes().isEmpty();
			case DTDPackage.DTD_BASIC_TYPE__EID_ATTRIBUTE :
				return getEIDAttribute() != null;
			case DTDPackage.DTD_BASIC_TYPE__KIND :
				return kind != KIND_EDEFAULT;
		}
		return eDynamicIsSet(eFeature);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String toString() {
		if (eIsProxy())
			return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (kind: "); //$NON-NLS-1$
		result.append(kind);
		result.append(')');
		return result.toString();
	}

} // DTDBasicTypeImpl
