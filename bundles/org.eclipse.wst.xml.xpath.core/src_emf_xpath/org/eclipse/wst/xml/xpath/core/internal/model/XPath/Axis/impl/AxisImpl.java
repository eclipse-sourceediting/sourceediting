/**
 *  Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      David Carver - initial API and implementation
 *
 * $Id: AxisImpl.java,v 1.4 2008/08/30 03:41:05 dacarver Exp $
 */
package org.eclipse.wst.xml.xpath.core.internal.model.XPath.Axis.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.wst.xml.xpath.core.internal.model.XPath.Axis.Axis;
import org.eclipse.wst.xml.xpath.core.internal.model.XPath.Axis.AxisPackage;

import org.eclipse.wst.xml.xpath.core.internal.model.XPath.impl.XpathCommonImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Axis</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Axis.impl.AxisImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Axis.impl.AxisImpl#getShortcut <em>Shortcut</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class AxisImpl extends XpathCommonImpl implements Axis {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = " Copyright (c) 2008 Standards for Technology in Automotive Retail and others.\n All rights reserved. This program and the accompanying materials\n are made available under the terms of the Eclipse Public License v1.0\n which accompanies this distribution, and is available at\n http://www.eclipse.org/legal/epl-v10.html\n\n Contributors:\n     David Carver - initial API and implementation";

	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;
	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;
	/**
	 * The default value of the '{@link #getShortcut() <em>Shortcut</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getShortcut()
	 * @generated
	 * @ordered
	 */
	protected static final String SHORTCUT_EDEFAULT = null;
	/**
	 * The cached value of the '{@link #getShortcut() <em>Shortcut</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getShortcut()
	 * @generated
	 * @ordered
	 */
	protected String shortcut = SHORTCUT_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AxisImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return AxisPackage.Literals.AXIS;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, AxisPackage.AXIS__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getShortcut() {
		return shortcut;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setShortcut(String newShortcut) {
		String oldShortcut = shortcut;
		shortcut = newShortcut;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, AxisPackage.AXIS__SHORTCUT, oldShortcut, shortcut));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case AxisPackage.AXIS__NAME:
				return getName();
			case AxisPackage.AXIS__SHORTCUT:
				return getShortcut();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case AxisPackage.AXIS__NAME:
				setName((String)newValue);
				return;
			case AxisPackage.AXIS__SHORTCUT:
				setShortcut((String)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case AxisPackage.AXIS__NAME:
				setName(NAME_EDEFAULT);
				return;
			case AxisPackage.AXIS__SHORTCUT:
				setShortcut(SHORTCUT_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case AxisPackage.AXIS__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case AxisPackage.AXIS__SHORTCUT:
				return SHORTCUT_EDEFAULT == null ? shortcut != null : !SHORTCUT_EDEFAULT.equals(shortcut);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (name: ");
		result.append(name);
		result.append(", shortcut: ");
		result.append(shortcut);
		result.append(')');
		return result.toString();
	}

} //AxisImpl
