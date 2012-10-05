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
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EEnumImpl;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
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
	 * The default value of the '{@link #getKind() <em>Kind</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
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
	 * 
	 * @generated
	 */
	protected EClass eStaticClass() {
		return DTDPackage.eINSTANCE.getDTDEnumerationType();
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
	 * 
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
		if (eContainerFeatureID() != DTDPackage.DTD_ENUMERATION_TYPE__DTD_FILE)
			return null;
		return (DTDFile) eContainer;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public void setDTDFile(DTDFile newDTDFile) {
		if (newDTDFile != eContainer || (eContainerFeatureID() != DTDPackage.DTD_ENUMERATION_TYPE__DTD_FILE && newDTDFile != null)) {
			if (EcoreUtil.isAncestor(this, newDTDFile))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString() + "."); //$NON-NLS-1$ //$NON-NLS-2$
			NotificationChain msgs = null;
			if (eContainer != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newDTDFile != null)
				msgs = ((InternalEObject) newDTDFile).eInverseAdd(this, DTDPackage.DTD_FILE__DTD_ENUMERATION_TYPE, DTDFile.class, msgs);
			msgs = eBasicSetContainer((InternalEObject) newDTDFile, DTDPackage.DTD_ENUMERATION_TYPE__DTD_FILE, msgs);
			if (msgs != null)
				msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ENUMERATION_TYPE__DTD_FILE, newDTDFile, newDTDFile));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
		if (featureID >= 0) {
			switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
				case DTDPackage.DTD_ENUMERATION_TYPE__EANNOTATIONS :
					return ((InternalEList) getEAnnotations()).basicAdd(otherEnd, msgs);
				case DTDPackage.DTD_ENUMERATION_TYPE__EPACKAGE :
					if (eContainer != null)
						msgs = eBasicRemoveFromContainer(msgs);
					return eBasicSetContainer(otherEnd, DTDPackage.DTD_ENUMERATION_TYPE__EPACKAGE, msgs);
				case DTDPackage.DTD_ENUMERATION_TYPE__ELITERALS :
					return ((InternalEList) getELiterals()).basicAdd(otherEnd, msgs);
				case DTDPackage.DTD_ENUMERATION_TYPE__DTD_FILE :
					if (eContainer != null)
						msgs = eBasicRemoveFromContainer(msgs);
					return eBasicSetContainer(otherEnd, DTDPackage.DTD_ENUMERATION_TYPE__DTD_FILE, msgs);
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
				case DTDPackage.DTD_ENUMERATION_TYPE__EANNOTATIONS :
					return ((InternalEList) getEAnnotations()).basicRemove(otherEnd, msgs);
				case DTDPackage.DTD_ENUMERATION_TYPE__EPACKAGE :
					return eBasicSetContainer(null, DTDPackage.DTD_ENUMERATION_TYPE__EPACKAGE, msgs);
				case DTDPackage.DTD_ENUMERATION_TYPE__ELITERALS :
					return ((InternalEList) getELiterals()).basicRemove(otherEnd, msgs);
				case DTDPackage.DTD_ENUMERATION_TYPE__DTD_FILE :
					return eBasicSetContainer(null, DTDPackage.DTD_ENUMERATION_TYPE__DTD_FILE, msgs);
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
				case DTDPackage.DTD_ENUMERATION_TYPE__EPACKAGE :
					return eContainer.eInverseRemove(this, EcorePackage.EPACKAGE__ECLASSIFIERS, EPackage.class, msgs);
				case DTDPackage.DTD_ENUMERATION_TYPE__DTD_FILE :
					return eContainer.eInverseRemove(this, DTDPackage.DTD_FILE__DTD_ENUMERATION_TYPE, DTDFile.class, msgs);
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
			case DTDPackage.DTD_ENUMERATION_TYPE__EANNOTATIONS :
				return getEAnnotations();
			case DTDPackage.DTD_ENUMERATION_TYPE__NAME :
				return getName();
			case DTDPackage.DTD_ENUMERATION_TYPE__INSTANCE_CLASS_NAME :
				return getInstanceClassName();
			case DTDPackage.DTD_ENUMERATION_TYPE__INSTANCE_CLASS :
				return getInstanceClass();
			case DTDPackage.DTD_ENUMERATION_TYPE__DEFAULT_VALUE :
				return getDefaultValue();
			case DTDPackage.DTD_ENUMERATION_TYPE__EPACKAGE :
				return getEPackage();
			case DTDPackage.DTD_ENUMERATION_TYPE__SERIALIZABLE :
				return isSerializable() ? Boolean.TRUE : Boolean.FALSE;
			case DTDPackage.DTD_ENUMERATION_TYPE__ELITERALS :
				return getELiterals();
			case DTDPackage.DTD_ENUMERATION_TYPE__KIND :
				return getKind();
			case DTDPackage.DTD_ENUMERATION_TYPE__DTD_FILE :
				return getDTDFile();
		}
		return eDynamicGet(eFeature, resolve);
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public boolean eIsSet(EStructuralFeature eFeature) {
		switch (eDerivedStructuralFeatureID(eFeature.getFeatureID(), eFeature.getContainerClass())) {
			case DTDPackage.DTD_ENUMERATION_TYPE__EANNOTATIONS :
				return eAnnotations != null && !getEAnnotations().isEmpty();
			case DTDPackage.DTD_ENUMERATION_TYPE__NAME :
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case DTDPackage.DTD_ENUMERATION_TYPE__INSTANCE_CLASS_NAME :
				return INSTANCE_CLASS_NAME_EDEFAULT == null ? instanceClassName != null : !INSTANCE_CLASS_NAME_EDEFAULT.equals(instanceClassName);
			case DTDPackage.DTD_ENUMERATION_TYPE__INSTANCE_CLASS :
				return INSTANCE_CLASS_EDEFAULT == null ? instanceClass != null : !INSTANCE_CLASS_EDEFAULT.equals(instanceClass);
			case DTDPackage.DTD_ENUMERATION_TYPE__DEFAULT_VALUE :
				return getDefaultValue() != null;
			case DTDPackage.DTD_ENUMERATION_TYPE__EPACKAGE :
				return getEPackage() != null;
			case DTDPackage.DTD_ENUMERATION_TYPE__SERIALIZABLE :
				return ((eFlags & SERIALIZABLE_EFLAG) != 0) != SERIALIZABLE_EDEFAULT;
			case DTDPackage.DTD_ENUMERATION_TYPE__ELITERALS :
				return eLiterals != null && !getELiterals().isEmpty();
			case DTDPackage.DTD_ENUMERATION_TYPE__KIND :
				return kind != KIND_EDEFAULT;
			case DTDPackage.DTD_ENUMERATION_TYPE__DTD_FILE :
				return getDTDFile() != null;
		}
		return eDynamicIsSet(eFeature);
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public void eSet(EStructuralFeature eFeature, Object newValue) {
		switch (eDerivedStructuralFeatureID(eFeature.getFeatureID(), eFeature.getContainerClass())) {
			case DTDPackage.DTD_ENUMERATION_TYPE__EANNOTATIONS :
				getEAnnotations().clear();
				getEAnnotations().addAll((Collection) newValue);
				return;
			case DTDPackage.DTD_ENUMERATION_TYPE__NAME :
				setName((String) newValue);
				return;
			case DTDPackage.DTD_ENUMERATION_TYPE__INSTANCE_CLASS_NAME :
				setInstanceClassName((String) newValue);
				return;
			case DTDPackage.DTD_ENUMERATION_TYPE__INSTANCE_CLASS :
				setInstanceClass((Class) newValue);
				return;
			case DTDPackage.DTD_ENUMERATION_TYPE__SERIALIZABLE :
				setSerializable(((Boolean) newValue).booleanValue());
				return;
			case DTDPackage.DTD_ENUMERATION_TYPE__ELITERALS :
				getELiterals().clear();
				getELiterals().addAll((Collection) newValue);
				return;
			case DTDPackage.DTD_ENUMERATION_TYPE__KIND :
				setKind((DTDEnumGroupKind) newValue);
				return;
			case DTDPackage.DTD_ENUMERATION_TYPE__DTD_FILE :
				setDTDFile((DTDFile) newValue);
				return;
		}
		eDynamicSet(eFeature, newValue);
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public void eUnset(EStructuralFeature eFeature) {
		switch (eDerivedStructuralFeatureID(eFeature.getFeatureID(), eFeature.getContainerClass())) {
			case DTDPackage.DTD_ENUMERATION_TYPE__EANNOTATIONS :
				getEAnnotations().clear();
				return;
			case DTDPackage.DTD_ENUMERATION_TYPE__NAME :
				setName(NAME_EDEFAULT);
				return;
			case DTDPackage.DTD_ENUMERATION_TYPE__INSTANCE_CLASS_NAME :
				setInstanceClassName(INSTANCE_CLASS_NAME_EDEFAULT);
				return;
			case DTDPackage.DTD_ENUMERATION_TYPE__INSTANCE_CLASS :
				setInstanceClass(INSTANCE_CLASS_EDEFAULT);
				return;
			case DTDPackage.DTD_ENUMERATION_TYPE__SERIALIZABLE :
				setSerializable(SERIALIZABLE_EDEFAULT);
				return;
			case DTDPackage.DTD_ENUMERATION_TYPE__ELITERALS :
				getELiterals().clear();
				return;
			case DTDPackage.DTD_ENUMERATION_TYPE__KIND :
				setKind(KIND_EDEFAULT);
				return;
			case DTDPackage.DTD_ENUMERATION_TYPE__DTD_FILE :
				setDTDFile((DTDFile) null);
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
		result.append(" (kind: "); //$NON-NLS-1$
		result.append(kind);
		result.append(')');
		return result.toString();
	}

}
