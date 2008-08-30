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
 * $Id: ArgumentImpl.java,v 1.4 2008/08/30 03:41:10 dacarver Exp $
 */
package org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import org.eclipse.wst.xml.xpath.core.internal.model.XPath.Component;

import org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Argument;
import org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.FunctionPackage;
import org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Occurrence;

import org.eclipse.wst.xml.xpath.core.internal.model.XPath.impl.XpathCommonImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Argument</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.impl.ArgumentImpl#getDataType <em>Data Type</em>}</li>
 *   <li>{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.impl.ArgumentImpl#getRequired <em>Required</em>}</li>
 *   <li>{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.impl.ArgumentImpl#getComponetList <em>Componet</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ArgumentImpl extends XpathCommonImpl implements Argument {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = " Copyright (c) 2008 Standards for Technology in Automotive Retail and others.\n All rights reserved. This program and the accompanying materials\n are made available under the terms of the Eclipse Public License v1.0\n which accompanies this distribution, and is available at\n http://www.eclipse.org/legal/epl-v10.html\n\n Contributors:\n     David Carver - initial API and implementation";

	/**
	 * The default value of the '{@link #getDataType() <em>Data Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDataType()
	 * @generated
	 * @ordered
	 */
	protected static final String DATA_TYPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDataType() <em>Data Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDataType()
	 * @generated
	 * @ordered
	 */
	protected String dataType = DATA_TYPE_EDEFAULT;

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
	 * The cached value of the '{@link #getComponetList() <em>Componet</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getComponetList()
	 * @generated
	 * @ordered
	 */
	protected EList<Component> componet;

	/**
	 * The empty value for the '{@link #getComponet() <em>Componet</em>}' array accessor.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getComponet()
	 * @generated
	 * @ordered
	 */
	protected static final Component[] COMPONET_EEMPTY_ARRAY = new Component [0];

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
	public String getDataType() {
		return dataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDataType(String newDataType) {
		String oldDataType = dataType;
		dataType = newDataType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, FunctionPackage.ARGUMENT__DATA_TYPE, oldDataType, dataType));
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
	@SuppressWarnings("unchecked")
	public Component[] getComponet() {
		if (componet == null || componet.isEmpty()) return COMPONET_EEMPTY_ARRAY;
		BasicEList<Component> list = (BasicEList<Component>)componet;
		list.shrink();
		return (Component[])list.data();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Component getComponet(int index) {
		return getComponetList().get(index);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getComponetLength() {
		return componet == null ? 0 : componet.size();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setComponet(Component[] newComponet) {
		((BasicEList<Component>)getComponetList()).setData(newComponet.length, newComponet);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setComponet(int index, Component element) {
		getComponetList().set(index, element);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Component> getComponetList() {
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
			case FunctionPackage.ARGUMENT__DATA_TYPE:
				return getDataType();
			case FunctionPackage.ARGUMENT__REQUIRED:
				return getRequired();
			case FunctionPackage.ARGUMENT__COMPONET:
				return getComponetList();
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
			case FunctionPackage.ARGUMENT__DATA_TYPE:
				setDataType((String)newValue);
				return;
			case FunctionPackage.ARGUMENT__REQUIRED:
				setRequired((Occurrence)newValue);
				return;
			case FunctionPackage.ARGUMENT__COMPONET:
				getComponetList().clear();
				getComponetList().addAll((Collection<? extends Component>)newValue);
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
			case FunctionPackage.ARGUMENT__DATA_TYPE:
				setDataType(DATA_TYPE_EDEFAULT);
				return;
			case FunctionPackage.ARGUMENT__REQUIRED:
				setRequired(REQUIRED_EDEFAULT);
				return;
			case FunctionPackage.ARGUMENT__COMPONET:
				getComponetList().clear();
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
			case FunctionPackage.ARGUMENT__DATA_TYPE:
				return DATA_TYPE_EDEFAULT == null ? dataType != null : !DATA_TYPE_EDEFAULT.equals(dataType);
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
		result.append(" (dataType: ");
		result.append(dataType);
		result.append(", required: ");
		result.append(required);
		result.append(')');
		return result.toString();
	}

} //ArgumentImpl
