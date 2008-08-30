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
 * $Id: FunctionImpl.java,v 1.4 2008/08/30 03:41:09 dacarver Exp $
 */
package org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import org.eclipse.wst.xml.xpath.core.internal.model.XPath.DataTypes.DataTypes;

import org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Argument;
import org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Function;
import org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.FunctionPackage;

import org.eclipse.wst.xml.xpath.core.internal.model.XPath.impl.XpathCommonImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Function</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.impl.FunctionImpl#getNamespace <em>Namespace</em>}</li>
 *   <li>{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.impl.FunctionImpl#getPrefix <em>Prefix</em>}</li>
 *   <li>{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.impl.FunctionImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.impl.FunctionImpl#getReturns <em>Returns</em>}</li>
 *   <li>{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.impl.FunctionImpl#getArgumentsList <em>Arguments</em>}</li>
 *   <li>{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.impl.FunctionImpl#getMinimumArguments <em>Minimum Arguments</em>}</li>
 *   <li>{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.impl.FunctionImpl#getMaximumArguments <em>Maximum Arguments</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class FunctionImpl extends XpathCommonImpl implements Function {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = " Copyright (c) 2008 Standards for Technology in Automotive Retail and others.\n All rights reserved. This program and the accompanying materials\n are made available under the terms of the Eclipse Public License v1.0\n which accompanies this distribution, and is available at\n http://www.eclipse.org/legal/epl-v10.html\n\n Contributors:\n     David Carver - initial API and implementation";

	/**
	 * The default value of the '{@link #getNamespace() <em>Namespace</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNamespace()
	 * @generated
	 * @ordered
	 */
	protected static final String NAMESPACE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getNamespace() <em>Namespace</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNamespace()
	 * @generated
	 * @ordered
	 */
	protected String namespace = NAMESPACE_EDEFAULT;

	/**
	 * The default value of the '{@link #getPrefix() <em>Prefix</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPrefix()
	 * @generated
	 * @ordered
	 */
	protected static final String PREFIX_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPrefix() <em>Prefix</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPrefix()
	 * @generated
	 * @ordered
	 */
	protected String prefix = PREFIX_EDEFAULT;

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
	 * The default value of the '{@link #getReturns() <em>Returns</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReturns()
	 * @generated
	 * @ordered
	 */
	protected static final DataTypes RETURNS_EDEFAULT = DataTypes.NODESET;

	/**
	 * The cached value of the '{@link #getReturns() <em>Returns</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReturns()
	 * @generated
	 * @ordered
	 */
	protected DataTypes returns = RETURNS_EDEFAULT;

	/**
	 * The cached value of the '{@link #getArgumentsList() <em>Arguments</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getArgumentsList()
	 * @generated
	 * @ordered
	 */
	protected EList<Argument> arguments;

	/**
	 * The empty value for the '{@link #getArguments() <em>Arguments</em>}' array accessor.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getArguments()
	 * @generated
	 * @ordered
	 */
	protected static final Argument[] ARGUMENTS_EEMPTY_ARRAY = new Argument [0];

	/**
	 * The default value of the '{@link #getMinimumArguments() <em>Minimum Arguments</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMinimumArguments()
	 * @generated
	 * @ordered
	 */
	protected static final int MINIMUM_ARGUMENTS_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getMinimumArguments() <em>Minimum Arguments</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMinimumArguments()
	 * @generated
	 * @ordered
	 */
	protected int minimumArguments = MINIMUM_ARGUMENTS_EDEFAULT;

	/**
	 * The default value of the '{@link #getMaximumArguments() <em>Maximum Arguments</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMaximumArguments()
	 * @generated
	 * @ordered
	 */
	protected static final int MAXIMUM_ARGUMENTS_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getMaximumArguments() <em>Maximum Arguments</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMaximumArguments()
	 * @generated
	 * @ordered
	 */
	protected int maximumArguments = MAXIMUM_ARGUMENTS_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected FunctionImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return FunctionPackage.Literals.FUNCTION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setNamespace(String newNamespace) {
		String oldNamespace = namespace;
		namespace = newNamespace;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, FunctionPackage.FUNCTION__NAMESPACE, oldNamespace, namespace));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPrefix(String newPrefix) {
		String oldPrefix = prefix;
		prefix = newPrefix;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, FunctionPackage.FUNCTION__PREFIX, oldPrefix, prefix));
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
			eNotify(new ENotificationImpl(this, Notification.SET, FunctionPackage.FUNCTION__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DataTypes getReturns() {
		return returns;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setReturns(DataTypes newReturns) {
		DataTypes oldReturns = returns;
		returns = newReturns == null ? RETURNS_EDEFAULT : newReturns;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, FunctionPackage.FUNCTION__RETURNS, oldReturns, returns));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	public Argument[] getArguments() {
		if (arguments == null || arguments.isEmpty()) return ARGUMENTS_EEMPTY_ARRAY;
		BasicEList<Argument> list = (BasicEList<Argument>)arguments;
		list.shrink();
		return (Argument[])list.data();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Argument getArguments(int index) {
		return getArgumentsList().get(index);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getArgumentsLength() {
		return arguments == null ? 0 : arguments.size();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setArguments(Argument[] newArguments) {
		((BasicEList<Argument>)getArgumentsList()).setData(newArguments.length, newArguments);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setArguments(int index, Argument element) {
		getArgumentsList().set(index, element);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Argument> getArgumentsList() {
		if (arguments == null) {
			arguments = new EObjectResolvingEList<Argument>(Argument.class, this, FunctionPackage.FUNCTION__ARGUMENTS);
		}
		return arguments;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getMinimumArguments() {
		return minimumArguments;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMinimumArguments(int newMinimumArguments) {
		int oldMinimumArguments = minimumArguments;
		minimumArguments = newMinimumArguments;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, FunctionPackage.FUNCTION__MINIMUM_ARGUMENTS, oldMinimumArguments, minimumArguments));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getMaximumArguments() {
		return maximumArguments;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMaximumArguments(int newMaximumArguments) {
		int oldMaximumArguments = maximumArguments;
		maximumArguments = newMaximumArguments;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, FunctionPackage.FUNCTION__MAXIMUM_ARGUMENTS, oldMaximumArguments, maximumArguments));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case FunctionPackage.FUNCTION__NAMESPACE:
				return getNamespace();
			case FunctionPackage.FUNCTION__PREFIX:
				return getPrefix();
			case FunctionPackage.FUNCTION__NAME:
				return getName();
			case FunctionPackage.FUNCTION__RETURNS:
				return getReturns();
			case FunctionPackage.FUNCTION__ARGUMENTS:
				return getArgumentsList();
			case FunctionPackage.FUNCTION__MINIMUM_ARGUMENTS:
				return new Integer(getMinimumArguments());
			case FunctionPackage.FUNCTION__MAXIMUM_ARGUMENTS:
				return new Integer(getMaximumArguments());
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
			case FunctionPackage.FUNCTION__NAMESPACE:
				setNamespace((String)newValue);
				return;
			case FunctionPackage.FUNCTION__PREFIX:
				setPrefix((String)newValue);
				return;
			case FunctionPackage.FUNCTION__NAME:
				setName((String)newValue);
				return;
			case FunctionPackage.FUNCTION__RETURNS:
				setReturns((DataTypes)newValue);
				return;
			case FunctionPackage.FUNCTION__ARGUMENTS:
				getArgumentsList().clear();
				getArgumentsList().addAll((Collection<? extends Argument>)newValue);
				return;
			case FunctionPackage.FUNCTION__MINIMUM_ARGUMENTS:
				setMinimumArguments(((Integer)newValue).intValue());
				return;
			case FunctionPackage.FUNCTION__MAXIMUM_ARGUMENTS:
				setMaximumArguments(((Integer)newValue).intValue());
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
			case FunctionPackage.FUNCTION__NAMESPACE:
				setNamespace(NAMESPACE_EDEFAULT);
				return;
			case FunctionPackage.FUNCTION__PREFIX:
				setPrefix(PREFIX_EDEFAULT);
				return;
			case FunctionPackage.FUNCTION__NAME:
				setName(NAME_EDEFAULT);
				return;
			case FunctionPackage.FUNCTION__RETURNS:
				setReturns(RETURNS_EDEFAULT);
				return;
			case FunctionPackage.FUNCTION__ARGUMENTS:
				getArgumentsList().clear();
				return;
			case FunctionPackage.FUNCTION__MINIMUM_ARGUMENTS:
				setMinimumArguments(MINIMUM_ARGUMENTS_EDEFAULT);
				return;
			case FunctionPackage.FUNCTION__MAXIMUM_ARGUMENTS:
				setMaximumArguments(MAXIMUM_ARGUMENTS_EDEFAULT);
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
			case FunctionPackage.FUNCTION__NAMESPACE:
				return NAMESPACE_EDEFAULT == null ? namespace != null : !NAMESPACE_EDEFAULT.equals(namespace);
			case FunctionPackage.FUNCTION__PREFIX:
				return PREFIX_EDEFAULT == null ? prefix != null : !PREFIX_EDEFAULT.equals(prefix);
			case FunctionPackage.FUNCTION__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case FunctionPackage.FUNCTION__RETURNS:
				return returns != RETURNS_EDEFAULT;
			case FunctionPackage.FUNCTION__ARGUMENTS:
				return arguments != null && !arguments.isEmpty();
			case FunctionPackage.FUNCTION__MINIMUM_ARGUMENTS:
				return minimumArguments != MINIMUM_ARGUMENTS_EDEFAULT;
			case FunctionPackage.FUNCTION__MAXIMUM_ARGUMENTS:
				return maximumArguments != MAXIMUM_ARGUMENTS_EDEFAULT;
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
		result.append(" (namespace: ");
		result.append(namespace);
		result.append(", prefix: ");
		result.append(prefix);
		result.append(", name: ");
		result.append(name);
		result.append(", returns: ");
		result.append(returns);
		result.append(", minimumArguments: ");
		result.append(minimumArguments);
		result.append(", maximumArguments: ");
		result.append(maximumArguments);
		result.append(')');
		return result.toString();
	}

} //FunctionImpl
