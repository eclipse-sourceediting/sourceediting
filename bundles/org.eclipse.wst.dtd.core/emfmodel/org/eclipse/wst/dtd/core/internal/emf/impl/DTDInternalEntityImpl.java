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

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.wst.dtd.core.internal.emf.DTDEntity;
import org.eclipse.wst.dtd.core.internal.emf.DTDInternalEntity;
import org.eclipse.wst.dtd.core.internal.emf.DTDPackage;


/**
 * @generated
 */
public class DTDInternalEntityImpl extends DTDEntityContentImpl implements DTDInternalEntity {
	/**
	 * The default value of the '{@link #getValue() <em>Value</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getValue()
	 * @generated
	 * @ordered
	 */
	protected static final String VALUE_EDEFAULT = null;

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	protected String value = VALUE_EDEFAULT;

	public DTDInternalEntityImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected EClass eStaticClass() {
		return DTDPackage.eINSTANCE.getDTDInternalEntity();
	}

	public String unparse() {
		StringBuffer sb = new StringBuffer(64);
		if (getValue() == null)
			sb.append("\"\""); //$NON-NLS-1$
		else {
			sb.append("\"").append(replaceDoubleQuotes(getValue())).append("\""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return sb.toString();
	}

	// this method replaces any occurrence of ' " ' with the equivalent value
	// of &#34;
	private String replaceDoubleQuotes(String input) {
		StringBuffer newValue = new StringBuffer();

		StringCharacterIterator characters = new StringCharacterIterator(input);

		while (characters.current() != CharacterIterator.DONE) {
			char current = characters.current();

			if (current == '"') {
				newValue.append("&#34;"); //$NON-NLS-1$
			}
			else {
				newValue.append(current);
			}
			characters.next();
		}

		return newValue.toString();
	}

	/**
	 * @generated This field/method will be replaced during code generation
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public void setValue(String newValue) {
		String oldValue = value;
		value = newValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_INTERNAL_ENTITY__VALUE, oldValue, value));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
		if (featureID >= 0) {
			switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
				case DTDPackage.DTD_INTERNAL_ENTITY__DTD_ENTITY :
					if (eContainer != null)
						msgs = eBasicRemoveFromContainer(msgs);
					return eBasicSetContainer(otherEnd, DTDPackage.DTD_INTERNAL_ENTITY__DTD_ENTITY, msgs);
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
				case DTDPackage.DTD_INTERNAL_ENTITY__DTD_ENTITY :
					return eBasicSetContainer(null, DTDPackage.DTD_INTERNAL_ENTITY__DTD_ENTITY, msgs);
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
			switch (eContainerFeatureID) {
				case DTDPackage.DTD_INTERNAL_ENTITY__DTD_ENTITY :
					return eContainer.eInverseRemove(this, DTDPackage.DTD_ENTITY__CONTENT, DTDEntity.class, msgs);
				default :
					return eDynamicBasicRemoveFromContainer(msgs);
			}
		}
		return eContainer.eInverseRemove(this, EOPPOSITE_FEATURE_BASE - eContainerFeatureID, null, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Object eGet(EStructuralFeature eFeature, boolean resolve) {
		switch (eDerivedStructuralFeatureID(eFeature.getFeatureID(), eFeature.getContainerClass())) {
			case DTDPackage.DTD_INTERNAL_ENTITY__DTD_ENTITY :
				return getDTDEntity();
			case DTDPackage.DTD_INTERNAL_ENTITY__VALUE :
				return getValue();
		}
		return eDynamicGet(eFeature, resolve);
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public boolean eIsSet(EStructuralFeature eFeature) {
		switch (eDerivedStructuralFeatureID(eFeature.getFeatureID(), eFeature.getContainerClass())) {
			case DTDPackage.DTD_INTERNAL_ENTITY__DTD_ENTITY :
				return getDTDEntity() != null;
			case DTDPackage.DTD_INTERNAL_ENTITY__VALUE :
				return VALUE_EDEFAULT == null ? value != null : !VALUE_EDEFAULT.equals(value);
		}
		return eDynamicIsSet(eFeature);
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public void eSet(EStructuralFeature eFeature, Object newValue) {
		switch (eDerivedStructuralFeatureID(eFeature.getFeatureID(), eFeature.getContainerClass())) {
			case DTDPackage.DTD_INTERNAL_ENTITY__DTD_ENTITY :
				setDTDEntity((DTDEntity) newValue);
				return;
			case DTDPackage.DTD_INTERNAL_ENTITY__VALUE :
				setValue((String) newValue);
				return;
		}
		eDynamicSet(eFeature, newValue);
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public void eUnset(EStructuralFeature eFeature) {
		switch (eDerivedStructuralFeatureID(eFeature.getFeatureID(), eFeature.getContainerClass())) {
			case DTDPackage.DTD_INTERNAL_ENTITY__DTD_ENTITY :
				setDTDEntity((DTDEntity) null);
				return;
			case DTDPackage.DTD_INTERNAL_ENTITY__VALUE :
				setValue(VALUE_EDEFAULT);
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
		result.append(" (value: "); //$NON-NLS-1$
		result.append(value);
		result.append(')');
		return result.toString();
	}

}
