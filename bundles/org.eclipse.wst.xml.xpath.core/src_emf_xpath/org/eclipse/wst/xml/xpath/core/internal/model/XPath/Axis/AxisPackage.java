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
 * $Id: AxisPackage.java,v 1.1 2008/04/22 21:07:30 dacarver Exp $
 */
package org.eclipse.wst.xml.xpath.core.internal.model.XPath.Axis;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.wst.xml.xpath.core.internal.model.XPath.xpathPackage;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Axis.AxisFactory
 * @model kind="package"
 * @generated
 */
public interface AxisPackage extends EPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = " Copyright (c) 2008 Standards for Technology in Automotive Retail and others.\n All rights reserved. This program and the accompanying materials\n are made available under the terms of the Eclipse Public License v1.0\n which accompanies this distribution, and is available at\n http://www.eclipse.org/legal/epl-v10.html\n\n Contributors:\n     David Carver - initial API and implementation";

	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "Axis";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "org.eclipse.wst.xml.xpath.core.internal.model.axis";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "axis";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	AxisPackage eINSTANCE = org.eclipse.wst.xml.xpath.core.internal.model.XPath.Axis.impl.AxisPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Axis.impl.AxisImpl <em>Axis</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Axis.impl.AxisImpl
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Axis.impl.AxisPackageImpl#getAxis()
	 * @generated
	 */
	int AXIS = 0;

	/**
	 * The feature id for the '<em><b>Begin Column</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int AXIS__BEGIN_COLUMN = xpathPackage.XPATH_COMMON__BEGIN_COLUMN;

	/**
	 * The feature id for the '<em><b>End Column</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int AXIS__END_COLUMN = xpathPackage.XPATH_COMMON__END_COLUMN;

	/**
	 * The feature id for the '<em><b>Begin Line Number</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int AXIS__BEGIN_LINE_NUMBER = xpathPackage.XPATH_COMMON__BEGIN_LINE_NUMBER;

	/**
	 * The feature id for the '<em><b>End Line Number</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int AXIS__END_LINE_NUMBER = xpathPackage.XPATH_COMMON__END_LINE_NUMBER;

	/**
	 * The number of structural features of the '<em>Axis</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int AXIS_FEATURE_COUNT = xpathPackage.XPATH_COMMON_FEATURE_COUNT + 0;


	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Axis.Axis <em>Axis</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Axis</em>'.
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Axis.Axis
	 * @generated
	 */
	EClass getAxis();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	AxisFactory getAxisFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Axis.impl.AxisImpl <em>Axis</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Axis.impl.AxisImpl
		 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Axis.impl.AxisPackageImpl#getAxis()
		 * @generated
		 */
		EClass AXIS = eINSTANCE.getAxis();

	}

} //AxisPackage
