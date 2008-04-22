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
 * $Id: xpathPackage.java,v 1.1 2008/04/22 21:07:29 dacarver Exp $
 */
package org.eclipse.wst.xml.xpath.core.internal.model.XPath;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

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
 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.xpathFactory
 * @model kind="package"
 * @generated
 */
public interface xpathPackage extends EPackage {
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
	String eNAME = "XPath";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "org.eclipse.wst.xml.xpath.core.internal.model";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "xpath";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	xpathPackage eINSTANCE = org.eclipse.wst.xml.xpath.core.internal.model.XPath.impl.xpathPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.impl.XpathCommonImpl <em>Xpath Common</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.impl.XpathCommonImpl
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.impl.xpathPackageImpl#getXpathCommon()
	 * @generated
	 */
	int XPATH_COMMON = 1;

	/**
	 * The feature id for the '<em><b>Begin Column</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int XPATH_COMMON__BEGIN_COLUMN = 0;

	/**
	 * The feature id for the '<em><b>End Column</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int XPATH_COMMON__END_COLUMN = 1;

	/**
	 * The feature id for the '<em><b>Begin Line Number</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int XPATH_COMMON__BEGIN_LINE_NUMBER = 2;

	/**
	 * The feature id for the '<em><b>End Line Number</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int XPATH_COMMON__END_LINE_NUMBER = 3;

	/**
	 * The number of structural features of the '<em>Xpath Common</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int XPATH_COMMON_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.impl.XPathImpl <em>XPath</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.impl.XPathImpl
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.impl.xpathPackageImpl#getXPath()
	 * @generated
	 */
	int XPATH = 0;

	/**
	 * The feature id for the '<em><b>Begin Column</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int XPATH__BEGIN_COLUMN = XPATH_COMMON__BEGIN_COLUMN;

	/**
	 * The feature id for the '<em><b>End Column</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int XPATH__END_COLUMN = XPATH_COMMON__END_COLUMN;

	/**
	 * The feature id for the '<em><b>Begin Line Number</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int XPATH__BEGIN_LINE_NUMBER = XPATH_COMMON__BEGIN_LINE_NUMBER;

	/**
	 * The feature id for the '<em><b>End Line Number</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int XPATH__END_LINE_NUMBER = XPATH_COMMON__END_LINE_NUMBER;

	/**
	 * The feature id for the '<em><b>Components</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int XPATH__COMPONENTS = XPATH_COMMON_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>XPath</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int XPATH_FEATURE_COUNT = XPATH_COMMON_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Component <em>Component</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Component
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.impl.xpathPackageImpl#getComponent()
	 * @generated
	 */
	int COMPONENT = 2;

	/**
	 * The number of structural features of the '<em>Component</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_FEATURE_COUNT = 0;


	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.XPath <em>XPath</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>XPath</em>'.
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.XPath
	 * @generated
	 */
	EClass getXPath();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.XPath#getComponentsList <em>Components</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Components</em>'.
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.XPath#getComponentsList()
	 * @see #getXPath()
	 * @generated
	 */
	EReference getXPath_Components();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.XpathCommon <em>Xpath Common</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Xpath Common</em>'.
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.XpathCommon
	 * @generated
	 */
	EClass getXpathCommon();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.XpathCommon#getBeginColumn <em>Begin Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Begin Column</em>'.
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.XpathCommon#getBeginColumn()
	 * @see #getXpathCommon()
	 * @generated
	 */
	EAttribute getXpathCommon_BeginColumn();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.XpathCommon#getEndColumn <em>End Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>End Column</em>'.
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.XpathCommon#getEndColumn()
	 * @see #getXpathCommon()
	 * @generated
	 */
	EAttribute getXpathCommon_EndColumn();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.XpathCommon#getBeginLineNumber <em>Begin Line Number</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Begin Line Number</em>'.
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.XpathCommon#getBeginLineNumber()
	 * @see #getXpathCommon()
	 * @generated
	 */
	EAttribute getXpathCommon_BeginLineNumber();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.XpathCommon#getEndLineNumber <em>End Line Number</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>End Line Number</em>'.
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.XpathCommon#getEndLineNumber()
	 * @see #getXpathCommon()
	 * @generated
	 */
	EAttribute getXpathCommon_EndLineNumber();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Component <em>Component</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Component</em>'.
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Component
	 * @generated
	 */
	EClass getComponent();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	xpathFactory getxpathFactory();

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
		 * The meta object literal for the '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.impl.XPathImpl <em>XPath</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.impl.XPathImpl
		 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.impl.xpathPackageImpl#getXPath()
		 * @generated
		 */
		EClass XPATH = eINSTANCE.getXPath();

		/**
		 * The meta object literal for the '<em><b>Components</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference XPATH__COMPONENTS = eINSTANCE.getXPath_Components();

		/**
		 * The meta object literal for the '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.impl.XpathCommonImpl <em>Xpath Common</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.impl.XpathCommonImpl
		 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.impl.xpathPackageImpl#getXpathCommon()
		 * @generated
		 */
		EClass XPATH_COMMON = eINSTANCE.getXpathCommon();

		/**
		 * The meta object literal for the '<em><b>Begin Column</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute XPATH_COMMON__BEGIN_COLUMN = eINSTANCE.getXpathCommon_BeginColumn();

		/**
		 * The meta object literal for the '<em><b>End Column</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute XPATH_COMMON__END_COLUMN = eINSTANCE.getXpathCommon_EndColumn();

		/**
		 * The meta object literal for the '<em><b>Begin Line Number</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute XPATH_COMMON__BEGIN_LINE_NUMBER = eINSTANCE.getXpathCommon_BeginLineNumber();

		/**
		 * The meta object literal for the '<em><b>End Line Number</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute XPATH_COMMON__END_LINE_NUMBER = eINSTANCE.getXpathCommon_EndLineNumber();

		/**
		 * The meta object literal for the '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Component <em>Component</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Component
		 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.impl.xpathPackageImpl#getComponent()
		 * @generated
		 */
		EClass COMPONENT = eINSTANCE.getComponent();

	}

} //xpathPackage
