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
import org.eclipse.wst.dtd.core.internal.emf.DTDExternalEntity;
import org.eclipse.wst.dtd.core.internal.emf.DTDFile;
import org.eclipse.wst.dtd.core.internal.emf.DTDNotation;
import org.eclipse.wst.dtd.core.internal.emf.DTDPackage;


/**
 * @generated
 */
public class DTDExternalEntityImpl extends DTDEntityContentImpl implements DTDExternalEntity {
	/**
	 * The default value of the '{@link #getSystemID() <em>System ID</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getSystemID()
	 * @generated
	 * @ordered
	 */
	protected static final String SYSTEM_ID_EDEFAULT = null;

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	protected String systemID = SYSTEM_ID_EDEFAULT;
	/**
	 * The default value of the '{@link #getPublicID() <em>Public ID</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getPublicID()
	 * @generated
	 * @ordered
	 */
	protected static final String PUBLIC_ID_EDEFAULT = null;

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	protected String publicID = PUBLIC_ID_EDEFAULT;
	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	protected DTDNotation notation;
	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	protected DTDFile entityReferencedFromAnotherFile;

	public DTDExternalEntityImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return DTDPackage.Literals.DTD_EXTERNAL_ENTITY;
	}

	public String unparse() {
		StringBuffer text = new StringBuffer(100);
		if (getPublicID() == null || getPublicID().equals("")) { //$NON-NLS-1$
			text.append("SYSTEM "); //$NON-NLS-1$
		}
		else {
			text.append("PUBLIC \"").append(getPublicID()).append("\" "); //$NON-NLS-1$ //$NON-NLS-2$
		}
		String systemId = getSystemID();

		text.append("\"").append(systemId).append("\""); //$NON-NLS-1$ //$NON-NLS-2$

		DTDNotation notation = getNotation();
		if (notation != null)
			text.append(" NDATA ").append(notation.getName()); //$NON-NLS-1$

		return text.toString();
	}

	/**
	 * @generated This field/method will be replaced during code generation
	 */
	public String getSystemID() {
		return systemID;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public void setSystemID(String newSystemID) {
		String oldSystemID = systemID;
		systemID = newSystemID;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_EXTERNAL_ENTITY__SYSTEM_ID, oldSystemID, systemID));
	}

	/**
	 * @generated This field/method will be replaced during code generation
	 */
	public String getPublicID() {
		return publicID;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public void setPublicID(String newPublicID) {
		String oldPublicID = publicID;
		publicID = newPublicID;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_EXTERNAL_ENTITY__PUBLIC_ID, oldPublicID, publicID));
	}

	/**
	 * @generated This field/method will be replaced during code generation
	 */
	public DTDNotation getNotation() {
		if (notation != null && notation.eIsProxy()) {
			InternalEObject oldNotation = (InternalEObject)notation;
			notation = (DTDNotation)eResolveProxy(oldNotation);
			if (notation != oldNotation) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, DTDPackage.DTD_EXTERNAL_ENTITY__NOTATION, oldNotation, notation));
			}
		}
		return notation;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public DTDNotation basicGetNotation() {
		return notation;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetNotation(DTDNotation newNotation, NotificationChain msgs) {
		DTDNotation oldNotation = notation;
		notation = newNotation;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_EXTERNAL_ENTITY__NOTATION, oldNotation, newNotation);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public void setNotation(DTDNotation newNotation) {
		if (newNotation != notation) {
			NotificationChain msgs = null;
			if (notation != null)
				msgs = ((InternalEObject)notation).eInverseRemove(this, DTDPackage.DTD_NOTATION__ENTITY, DTDNotation.class, msgs);
			if (newNotation != null)
				msgs = ((InternalEObject)newNotation).eInverseAdd(this, DTDPackage.DTD_NOTATION__ENTITY, DTDNotation.class, msgs);
			msgs = basicSetNotation(newNotation, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_EXTERNAL_ENTITY__NOTATION, newNotation, newNotation));
	}

	/**
	 * @generated This field/method will be replaced during code generation
	 */
	public DTDFile getEntityReferencedFromAnotherFile() {
		if (entityReferencedFromAnotherFile != null && entityReferencedFromAnotherFile.eIsProxy()) {
			InternalEObject oldEntityReferencedFromAnotherFile = (InternalEObject)entityReferencedFromAnotherFile;
			entityReferencedFromAnotherFile = (DTDFile)eResolveProxy(oldEntityReferencedFromAnotherFile);
			if (entityReferencedFromAnotherFile != oldEntityReferencedFromAnotherFile) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, DTDPackage.DTD_EXTERNAL_ENTITY__ENTITY_REFERENCED_FROM_ANOTHER_FILE, oldEntityReferencedFromAnotherFile, entityReferencedFromAnotherFile));
			}
		}
		return entityReferencedFromAnotherFile;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public DTDFile basicGetEntityReferencedFromAnotherFile() {
		return entityReferencedFromAnotherFile;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public void setEntityReferencedFromAnotherFile(DTDFile newEntityReferencedFromAnotherFile) {
		DTDFile oldEntityReferencedFromAnotherFile = entityReferencedFromAnotherFile;
		entityReferencedFromAnotherFile = newEntityReferencedFromAnotherFile;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_EXTERNAL_ENTITY__ENTITY_REFERENCED_FROM_ANOTHER_FILE, oldEntityReferencedFromAnotherFile, entityReferencedFromAnotherFile));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case DTDPackage.DTD_EXTERNAL_ENTITY__NOTATION:
				if (notation != null)
					msgs = ((InternalEObject)notation).eInverseRemove(this, DTDPackage.DTD_NOTATION__ENTITY, DTDNotation.class, msgs);
				return basicSetNotation((DTDNotation)otherEnd, msgs);
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
			case DTDPackage.DTD_EXTERNAL_ENTITY__NOTATION:
				return basicSetNotation(null, msgs);
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
			case DTDPackage.DTD_EXTERNAL_ENTITY__SYSTEM_ID:
				return getSystemID();
			case DTDPackage.DTD_EXTERNAL_ENTITY__PUBLIC_ID:
				return getPublicID();
			case DTDPackage.DTD_EXTERNAL_ENTITY__NOTATION:
				if (resolve) return getNotation();
				return basicGetNotation();
			case DTDPackage.DTD_EXTERNAL_ENTITY__ENTITY_REFERENCED_FROM_ANOTHER_FILE:
				if (resolve) return getEntityReferencedFromAnotherFile();
				return basicGetEntityReferencedFromAnotherFile();
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
			case DTDPackage.DTD_EXTERNAL_ENTITY__SYSTEM_ID:
				setSystemID((String)newValue);
				return;
			case DTDPackage.DTD_EXTERNAL_ENTITY__PUBLIC_ID:
				setPublicID((String)newValue);
				return;
			case DTDPackage.DTD_EXTERNAL_ENTITY__NOTATION:
				setNotation((DTDNotation)newValue);
				return;
			case DTDPackage.DTD_EXTERNAL_ENTITY__ENTITY_REFERENCED_FROM_ANOTHER_FILE:
				setEntityReferencedFromAnotherFile((DTDFile)newValue);
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
			case DTDPackage.DTD_EXTERNAL_ENTITY__SYSTEM_ID:
				setSystemID(SYSTEM_ID_EDEFAULT);
				return;
			case DTDPackage.DTD_EXTERNAL_ENTITY__PUBLIC_ID:
				setPublicID(PUBLIC_ID_EDEFAULT);
				return;
			case DTDPackage.DTD_EXTERNAL_ENTITY__NOTATION:
				setNotation((DTDNotation)null);
				return;
			case DTDPackage.DTD_EXTERNAL_ENTITY__ENTITY_REFERENCED_FROM_ANOTHER_FILE:
				setEntityReferencedFromAnotherFile((DTDFile)null);
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
			case DTDPackage.DTD_EXTERNAL_ENTITY__SYSTEM_ID:
				return SYSTEM_ID_EDEFAULT == null ? systemID != null : !SYSTEM_ID_EDEFAULT.equals(systemID);
			case DTDPackage.DTD_EXTERNAL_ENTITY__PUBLIC_ID:
				return PUBLIC_ID_EDEFAULT == null ? publicID != null : !PUBLIC_ID_EDEFAULT.equals(publicID);
			case DTDPackage.DTD_EXTERNAL_ENTITY__NOTATION:
				return notation != null;
			case DTDPackage.DTD_EXTERNAL_ENTITY__ENTITY_REFERENCED_FROM_ANOTHER_FILE:
				return entityReferencedFromAnotherFile != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (systemID: ");
		result.append(systemID);
		result.append(", publicID: ");
		result.append(publicID);
		result.append(')');
		return result.toString();
	}

}
