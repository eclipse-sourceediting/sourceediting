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

package org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.wst.xsl.core.internal.model.xpath.XPath.XPathPackage;

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
 * @see org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.FunctionFactory
 * @model kind="package"
 * @generated
 */
public interface FunctionPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "Function";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "org.eclipse.wst.xsl.core.internal.model.xpath.functions";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "func";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	FunctionPackage eINSTANCE = org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.impl.FunctionPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.impl.FunctionImpl <em>Function</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.impl.FunctionImpl
	 * @see org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.impl.FunctionPackageImpl#getFunction()
	 * @generated
	 */
	int FUNCTION = 0;

	/**
	 * The feature id for the '<em><b>Begin Column</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION__BEGIN_COLUMN = XPathPackage.XPATH_COMMON__BEGIN_COLUMN;

	/**
	 * The feature id for the '<em><b>End Column</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION__END_COLUMN = XPathPackage.XPATH_COMMON__END_COLUMN;

	/**
	 * The feature id for the '<em><b>Begin Line Number</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION__BEGIN_LINE_NUMBER = XPathPackage.XPATH_COMMON__BEGIN_LINE_NUMBER;

	/**
	 * The feature id for the '<em><b>End Line Number</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION__END_LINE_NUMBER = XPathPackage.XPATH_COMMON__END_LINE_NUMBER;

	/**
	 * The feature id for the '<em><b>Namespace</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION__NAMESPACE = XPathPackage.XPATH_COMMON_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Prefix</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION__PREFIX = XPathPackage.XPATH_COMMON_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION__NAME = XPathPackage.XPATH_COMMON_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Returns</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION__RETURNS = XPathPackage.XPATH_COMMON_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Arguments</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION__ARGUMENTS = XPathPackage.XPATH_COMMON_FEATURE_COUNT + 4;

	/**
	 * The number of structural features of the '<em>Function</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION_FEATURE_COUNT = XPathPackage.XPATH_COMMON_FEATURE_COUNT + 5;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.impl.ArgumentImpl <em>Argument</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.impl.ArgumentImpl
	 * @see org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.impl.FunctionPackageImpl#getArgument()
	 * @generated
	 */
	int ARGUMENT = 1;

	/**
	 * The feature id for the '<em><b>Begin Column</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARGUMENT__BEGIN_COLUMN = XPathPackage.XPATH_COMMON__BEGIN_COLUMN;

	/**
	 * The feature id for the '<em><b>End Column</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARGUMENT__END_COLUMN = XPathPackage.XPATH_COMMON__END_COLUMN;

	/**
	 * The feature id for the '<em><b>Begin Line Number</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARGUMENT__BEGIN_LINE_NUMBER = XPathPackage.XPATH_COMMON__BEGIN_LINE_NUMBER;

	/**
	 * The feature id for the '<em><b>End Line Number</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARGUMENT__END_LINE_NUMBER = XPathPackage.XPATH_COMMON__END_LINE_NUMBER;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARGUMENT__NAME = XPathPackage.XPATH_COMMON_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Required</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARGUMENT__REQUIRED = XPathPackage.XPATH_COMMON_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Componet</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARGUMENT__COMPONET = XPathPackage.XPATH_COMMON_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Argument</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARGUMENT_FEATURE_COUNT = XPathPackage.XPATH_COMMON_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Occurrence <em>Occurrence</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Occurrence
	 * @see org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.impl.FunctionPackageImpl#getOccurrence()
	 * @generated
	 */
	int OCCURRENCE = 2;


	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Function <em>Function</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Function</em>'.
	 * @see org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Function
	 * @generated
	 */
	EClass getFunction();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Function#getNamespace <em>Namespace</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Namespace</em>'.
	 * @see org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Function#getNamespace()
	 * @see #getFunction()
	 * @generated
	 */
	EAttribute getFunction_Namespace();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Function#getPrefix <em>Prefix</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Prefix</em>'.
	 * @see org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Function#getPrefix()
	 * @see #getFunction()
	 * @generated
	 */
	EAttribute getFunction_Prefix();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Function#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Function#getName()
	 * @see #getFunction()
	 * @generated
	 */
	EAttribute getFunction_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Function#getReturns <em>Returns</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Returns</em>'.
	 * @see org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Function#getReturns()
	 * @see #getFunction()
	 * @generated
	 */
	EAttribute getFunction_Returns();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Function#getArguments <em>Arguments</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Arguments</em>'.
	 * @see org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Function#getArguments()
	 * @see #getFunction()
	 * @generated
	 */
	EReference getFunction_Arguments();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Argument <em>Argument</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Argument</em>'.
	 * @see org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Argument
	 * @generated
	 */
	EClass getArgument();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Argument#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Argument#getName()
	 * @see #getArgument()
	 * @generated
	 */
	EAttribute getArgument_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Argument#getRequired <em>Required</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Required</em>'.
	 * @see org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Argument#getRequired()
	 * @see #getArgument()
	 * @generated
	 */
	EAttribute getArgument_Required();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Argument#getComponet <em>Componet</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Componet</em>'.
	 * @see org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Argument#getComponet()
	 * @see #getArgument()
	 * @generated
	 */
	EReference getArgument_Componet();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Occurrence <em>Occurrence</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Occurrence</em>'.
	 * @see org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Occurrence
	 * @generated
	 */
	EEnum getOccurrence();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	FunctionFactory getFunctionFactory();

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
		 * The meta object literal for the '{@link org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.impl.FunctionImpl <em>Function</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.impl.FunctionImpl
		 * @see org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.impl.FunctionPackageImpl#getFunction()
		 * @generated
		 */
		EClass FUNCTION = eINSTANCE.getFunction();

		/**
		 * The meta object literal for the '<em><b>Namespace</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FUNCTION__NAMESPACE = eINSTANCE.getFunction_Namespace();

		/**
		 * The meta object literal for the '<em><b>Prefix</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FUNCTION__PREFIX = eINSTANCE.getFunction_Prefix();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FUNCTION__NAME = eINSTANCE.getFunction_Name();

		/**
		 * The meta object literal for the '<em><b>Returns</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FUNCTION__RETURNS = eINSTANCE.getFunction_Returns();

		/**
		 * The meta object literal for the '<em><b>Arguments</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FUNCTION__ARGUMENTS = eINSTANCE.getFunction_Arguments();

		/**
		 * The meta object literal for the '{@link org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.impl.ArgumentImpl <em>Argument</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.impl.ArgumentImpl
		 * @see org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.impl.FunctionPackageImpl#getArgument()
		 * @generated
		 */
		EClass ARGUMENT = eINSTANCE.getArgument();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ARGUMENT__NAME = eINSTANCE.getArgument_Name();

		/**
		 * The meta object literal for the '<em><b>Required</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ARGUMENT__REQUIRED = eINSTANCE.getArgument_Required();

		/**
		 * The meta object literal for the '<em><b>Componet</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ARGUMENT__COMPONET = eINSTANCE.getArgument_Componet();

		/**
		 * The meta object literal for the '{@link org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Occurrence <em>Occurrence</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Occurrence
		 * @see org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.impl.FunctionPackageImpl#getOccurrence()
		 * @generated
		 */
		EEnum OCCURRENCE = eINSTANCE.getOccurrence();

	}

} //FunctionPackage
