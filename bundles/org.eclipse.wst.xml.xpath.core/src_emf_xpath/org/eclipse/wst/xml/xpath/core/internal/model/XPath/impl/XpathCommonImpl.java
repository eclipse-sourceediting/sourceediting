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
 * $Id: XpathCommonImpl.java,v 1.1 2008/04/22 21:07:27 dacarver Exp $
 */
package org.eclipse.wst.xml.xpath.core.internal.model.XPath.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.wst.xml.xpath.core.internal.model.XPath.XpathCommon;
import org.eclipse.wst.xml.xpath.core.internal.model.XPath.xpathPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Xpath Common</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.impl.XpathCommonImpl#getBeginColumn <em>Begin Column</em>}</li>
 *   <li>{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.impl.XpathCommonImpl#getEndColumn <em>End Column</em>}</li>
 *   <li>{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.impl.XpathCommonImpl#getBeginLineNumber <em>Begin Line Number</em>}</li>
 *   <li>{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.impl.XpathCommonImpl#getEndLineNumber <em>End Line Number</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class XpathCommonImpl extends EObjectImpl implements XpathCommon {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = " Copyright (c) 2008 Standards for Technology in Automotive Retail and others.\n All rights reserved. This program and the accompanying materials\n are made available under the terms of the Eclipse Public License v1.0\n which accompanies this distribution, and is available at\n http://www.eclipse.org/legal/epl-v10.html\n\n Contributors:\n     David Carver - initial API and implementation";

	/**
	 * The default value of the '{@link #getBeginColumn() <em>Begin Column</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBeginColumn()
	 * @generated
	 * @ordered
	 */
	protected static final int BEGIN_COLUMN_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getBeginColumn() <em>Begin Column</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBeginColumn()
	 * @generated
	 * @ordered
	 */
	protected int beginColumn = BEGIN_COLUMN_EDEFAULT;

	/**
	 * The default value of the '{@link #getEndColumn() <em>End Column</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEndColumn()
	 * @generated
	 * @ordered
	 */
	protected static final int END_COLUMN_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getEndColumn() <em>End Column</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEndColumn()
	 * @generated
	 * @ordered
	 */
	protected int endColumn = END_COLUMN_EDEFAULT;

	/**
	 * The default value of the '{@link #getBeginLineNumber() <em>Begin Line Number</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBeginLineNumber()
	 * @generated
	 * @ordered
	 */
	protected static final int BEGIN_LINE_NUMBER_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getBeginLineNumber() <em>Begin Line Number</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBeginLineNumber()
	 * @generated
	 * @ordered
	 */
	protected int beginLineNumber = BEGIN_LINE_NUMBER_EDEFAULT;

	/**
	 * The default value of the '{@link #getEndLineNumber() <em>End Line Number</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEndLineNumber()
	 * @generated
	 * @ordered
	 */
	protected static final int END_LINE_NUMBER_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getEndLineNumber() <em>End Line Number</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEndLineNumber()
	 * @generated
	 * @ordered
	 */
	protected int endLineNumber = END_LINE_NUMBER_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected XpathCommonImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return xpathPackage.Literals.XPATH_COMMON;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getBeginColumn() {
		return beginColumn;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setBeginColumn(int newBeginColumn) {
		int oldBeginColumn = beginColumn;
		beginColumn = newBeginColumn;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, xpathPackage.XPATH_COMMON__BEGIN_COLUMN, oldBeginColumn, beginColumn));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getEndColumn() {
		return endColumn;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setEndColumn(int newEndColumn) {
		int oldEndColumn = endColumn;
		endColumn = newEndColumn;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, xpathPackage.XPATH_COMMON__END_COLUMN, oldEndColumn, endColumn));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getBeginLineNumber() {
		return beginLineNumber;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setBeginLineNumber(int newBeginLineNumber) {
		int oldBeginLineNumber = beginLineNumber;
		beginLineNumber = newBeginLineNumber;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, xpathPackage.XPATH_COMMON__BEGIN_LINE_NUMBER, oldBeginLineNumber, beginLineNumber));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getEndLineNumber() {
		return endLineNumber;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setEndLineNumber(int newEndLineNumber) {
		int oldEndLineNumber = endLineNumber;
		endLineNumber = newEndLineNumber;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, xpathPackage.XPATH_COMMON__END_LINE_NUMBER, oldEndLineNumber, endLineNumber));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case xpathPackage.XPATH_COMMON__BEGIN_COLUMN:
				return new Integer(getBeginColumn());
			case xpathPackage.XPATH_COMMON__END_COLUMN:
				return new Integer(getEndColumn());
			case xpathPackage.XPATH_COMMON__BEGIN_LINE_NUMBER:
				return new Integer(getBeginLineNumber());
			case xpathPackage.XPATH_COMMON__END_LINE_NUMBER:
				return new Integer(getEndLineNumber());
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
			case xpathPackage.XPATH_COMMON__BEGIN_COLUMN:
				setBeginColumn(((Integer)newValue).intValue());
				return;
			case xpathPackage.XPATH_COMMON__END_COLUMN:
				setEndColumn(((Integer)newValue).intValue());
				return;
			case xpathPackage.XPATH_COMMON__BEGIN_LINE_NUMBER:
				setBeginLineNumber(((Integer)newValue).intValue());
				return;
			case xpathPackage.XPATH_COMMON__END_LINE_NUMBER:
				setEndLineNumber(((Integer)newValue).intValue());
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
			case xpathPackage.XPATH_COMMON__BEGIN_COLUMN:
				setBeginColumn(BEGIN_COLUMN_EDEFAULT);
				return;
			case xpathPackage.XPATH_COMMON__END_COLUMN:
				setEndColumn(END_COLUMN_EDEFAULT);
				return;
			case xpathPackage.XPATH_COMMON__BEGIN_LINE_NUMBER:
				setBeginLineNumber(BEGIN_LINE_NUMBER_EDEFAULT);
				return;
			case xpathPackage.XPATH_COMMON__END_LINE_NUMBER:
				setEndLineNumber(END_LINE_NUMBER_EDEFAULT);
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
			case xpathPackage.XPATH_COMMON__BEGIN_COLUMN:
				return beginColumn != BEGIN_COLUMN_EDEFAULT;
			case xpathPackage.XPATH_COMMON__END_COLUMN:
				return endColumn != END_COLUMN_EDEFAULT;
			case xpathPackage.XPATH_COMMON__BEGIN_LINE_NUMBER:
				return beginLineNumber != BEGIN_LINE_NUMBER_EDEFAULT;
			case xpathPackage.XPATH_COMMON__END_LINE_NUMBER:
				return endLineNumber != END_LINE_NUMBER_EDEFAULT;
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
		result.append(" (beginColumn: ");
		result.append(beginColumn);
		result.append(", endColumn: ");
		result.append(endColumn);
		result.append(", beginLineNumber: ");
		result.append(beginLineNumber);
		result.append(", endLineNumber: ");
		result.append(endLineNumber);
		result.append(')');
		return result.toString();
	}

} //XpathCommonImpl
