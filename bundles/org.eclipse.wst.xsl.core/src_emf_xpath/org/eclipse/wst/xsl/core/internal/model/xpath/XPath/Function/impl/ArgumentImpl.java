/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Component;

import org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Argument;
import org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.FunctionPackage;
import org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Occurrence;

import org.eclipse.wst.xsl.core.internal.model.xpath.XPath.impl.XpathCommonImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Argument</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.impl.ArgumentImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.impl.ArgumentImpl#getRequired <em>Required</em>}</li>
 *   <li>{@link org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.impl.ArgumentImpl#getComponet <em>Componet</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ArgumentImpl extends XpathCommonImpl implements Argument {
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
	 * The default value of the '{@link #getRequired() <em>Required</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRequired()
	 * @generated
	 * @ordered
	 */
	protected static final Occurrence REQUIRED_EDEFAULT = Occurrence.NO;

	/**
	 * The cached value of the '{@link #getRequired() <em>Required</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRequired()
	 * @generated
	 * @ordered
	 */
	protected Occurrence required = REQUIRED_EDEFAULT;

	/**
	 * The cached value of the '{@link #getComponet() <em>Componet</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getComponet()
	 * @generated
	 * @ordered
	 */
	protected EList<Component> componet;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ArgumentImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return FunctionPackage.Literals.ARGUMENT;
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
			eNotify(new ENotificationImpl(this, Notification.SET, FunctionPackage.ARGUMENT__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Occurrence getRequired() {
		return required;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRequired(Occurrence newRequired) {
		Occurrence oldRequired = required;
		required = newRequired == null ? REQUIRED_EDEFAULT : newRequired;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, FunctionPackage.ARGUMENT__REQUIRED, oldRequired, required));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Component> getComponet() {
		if (componet == null) {
			componet = new EObjectResolvingEList<Component>(Component.class, this, FunctionPackage.ARGUMENT__COMPONET);
		}
		return componet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case FunctionPackage.ARGUMENT__NAME:
				return getName();
			case FunctionPackage.ARGUMENT__REQUIRED:
				return getRequired();
			case FunctionPackage.ARGUMENT__COMPONET:
				return getComponet();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case FunctionPackage.ARGUMENT__NAME:
				setName((String)newValue);
				return;
			case FunctionPackage.ARGUMENT__REQUIRED:
				setRequired((Occurrence)newValue);
				return;
			case FunctionPackage.ARGUMENT__COMPONET:
				getComponet().clear();
				getComponet().addAll((Collection<? extends Component>)newValue);
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
			case FunctionPackage.ARGUMENT__NAME:
				setName(NAME_EDEFAULT);
				return;
			case FunctionPackage.ARGUMENT__REQUIRED:
				setRequired(REQUIRED_EDEFAULT);
				return;
			case FunctionPackage.ARGUMENT__COMPONET:
				getComponet().clear();
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
			case FunctionPackage.ARGUMENT__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case FunctionPackage.ARGUMENT__REQUIRED:
				return required != REQUIRED_EDEFAULT;
			case FunctionPackage.ARGUMENT__COMPONET:
				return componet != null && !componet.isEmpty();
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
		result.append(", required: ");
		result.append(required);
		result.append(')');
		return result.toString();
	}

} //ArgumentImpl
