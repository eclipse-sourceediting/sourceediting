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
 * $Id: FunctionPackage.java,v 1.4 2008/08/30 03:41:05 dacarver Exp $
 */
package org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

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
 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.FunctionFactory
 * @model kind="package"
 * @generated
 */
public interface FunctionPackage extends EPackage {
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
	String eNAME = "Function";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "org.eclipse.wst.xml.xpath.core.internal.model.functions";

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
	FunctionPackage eINSTANCE = org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.impl.FunctionPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.impl.FunctionImpl <em>Function</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.impl.FunctionImpl
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.impl.FunctionPackageImpl#getFunction()
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
	int FUNCTION__BEGIN_COLUMN = xpathPackage.XPATH_COMMON__BEGIN_COLUMN;

	/**
	 * The feature id for the '<em><b>End Column</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION__END_COLUMN = xpathPackage.XPATH_COMMON__END_COLUMN;

	/**
	 * The feature id for the '<em><b>Begin Line Number</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION__BEGIN_LINE_NUMBER = xpathPackage.XPATH_COMMON__BEGIN_LINE_NUMBER;

	/**
	 * The feature id for the '<em><b>End Line Number</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION__END_LINE_NUMBER = xpathPackage.XPATH_COMMON__END_LINE_NUMBER;

	/**
	 * The feature id for the '<em><b>Namespace</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION__NAMESPACE = xpathPackage.XPATH_COMMON_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Prefix</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION__PREFIX = xpathPackage.XPATH_COMMON_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION__NAME = xpathPackage.XPATH_COMMON_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Returns</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION__RETURNS = xpathPackage.XPATH_COMMON_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Arguments</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION__ARGUMENTS = xpathPackage.XPATH_COMMON_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Minimum Arguments</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION__MINIMUM_ARGUMENTS = xpathPackage.XPATH_COMMON_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Maximum Arguments</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION__MAXIMUM_ARGUMENTS = xpathPackage.XPATH_COMMON_FEATURE_COUNT + 6;

	/**
	 * The number of structural features of the '<em>Function</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION_FEATURE_COUNT = xpathPackage.XPATH_COMMON_FEATURE_COUNT + 7;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.impl.ArgumentImpl <em>Argument</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.impl.ArgumentImpl
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.impl.FunctionPackageImpl#getArgument()
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
	int ARGUMENT__BEGIN_COLUMN = xpathPackage.XPATH_COMMON__BEGIN_COLUMN;

	/**
	 * The feature id for the '<em><b>End Column</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARGUMENT__END_COLUMN = xpathPackage.XPATH_COMMON__END_COLUMN;

	/**
	 * The feature id for the '<em><b>Begin Line Number</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARGUMENT__BEGIN_LINE_NUMBER = xpathPackage.XPATH_COMMON__BEGIN_LINE_NUMBER;

	/**
	 * The feature id for the '<em><b>End Line Number</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARGUMENT__END_LINE_NUMBER = xpathPackage.XPATH_COMMON__END_LINE_NUMBER;

	/**
	 * The feature id for the '<em><b>Data Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARGUMENT__DATA_TYPE = xpathPackage.XPATH_COMMON_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Required</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARGUMENT__REQUIRED = xpathPackage.XPATH_COMMON_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Componet</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARGUMENT__COMPONET = xpathPackage.XPATH_COMMON_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Argument</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARGUMENT_FEATURE_COUNT = xpathPackage.XPATH_COMMON_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Occurrence <em>Occurrence</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Occurrence
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.impl.FunctionPackageImpl#getOccurrence()
	 * @generated
	 */
	int OCCURRENCE = 2;


	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Function <em>Function</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Function</em>'.
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Function
	 * @generated
	 */
	EClass getFunction();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Function#getNamespace <em>Namespace</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Namespace</em>'.
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Function#getNamespace()
	 * @see #getFunction()
	 * @generated
	 */
	EAttribute getFunction_Namespace();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Function#getPrefix <em>Prefix</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Prefix</em>'.
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Function#getPrefix()
	 * @see #getFunction()
	 * @generated
	 */
	EAttribute getFunction_Prefix();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Function#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Function#getName()
	 * @see #getFunction()
	 * @generated
	 */
	EAttribute getFunction_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Function#getReturns <em>Returns</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Returns</em>'.
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Function#getReturns()
	 * @see #getFunction()
	 * @generated
	 */
	EAttribute getFunction_Returns();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Function#getArgumentsList <em>Arguments</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Arguments</em>'.
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Function#getArgumentsList()
	 * @see #getFunction()
	 * @generated
	 */
	EReference getFunction_Arguments();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Function#getMinimumArguments <em>Minimum Arguments</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Minimum Arguments</em>'.
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Function#getMinimumArguments()
	 * @see #getFunction()
	 * @generated
	 */
	EAttribute getFunction_MinimumArguments();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Function#getMaximumArguments <em>Maximum Arguments</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Maximum Arguments</em>'.
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Function#getMaximumArguments()
	 * @see #getFunction()
	 * @generated
	 */
	EAttribute getFunction_MaximumArguments();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Argument <em>Argument</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Argument</em>'.
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Argument
	 * @generated
	 */
	EClass getArgument();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Argument#getDataType <em>Data Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Data Type</em>'.
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Argument#getDataType()
	 * @see #getArgument()
	 * @generated
	 */
	EAttribute getArgument_DataType();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Argument#getRequired <em>Required</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Required</em>'.
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Argument#getRequired()
	 * @see #getArgument()
	 * @generated
	 */
	EAttribute getArgument_Required();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Argument#getComponetList <em>Componet</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Componet</em>'.
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Argument#getComponetList()
	 * @see #getArgument()
	 * @generated
	 */
	EReference getArgument_Componet();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Occurrence <em>Occurrence</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Occurrence</em>'.
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Occurrence
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
		 * The meta object literal for the '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.impl.FunctionImpl <em>Function</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.impl.FunctionImpl
		 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.impl.FunctionPackageImpl#getFunction()
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
		 * The meta object literal for the '<em><b>Minimum Arguments</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FUNCTION__MINIMUM_ARGUMENTS = eINSTANCE.getFunction_MinimumArguments();

		/**
		 * The meta object literal for the '<em><b>Maximum Arguments</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FUNCTION__MAXIMUM_ARGUMENTS = eINSTANCE.getFunction_MaximumArguments();

		/**
		 * The meta object literal for the '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.impl.ArgumentImpl <em>Argument</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.impl.ArgumentImpl
		 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.impl.FunctionPackageImpl#getArgument()
		 * @generated
		 */
		EClass ARGUMENT = eINSTANCE.getArgument();

		/**
		 * The meta object literal for the '<em><b>Data Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ARGUMENT__DATA_TYPE = eINSTANCE.getArgument_DataType();

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
		 * The meta object literal for the '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Occurrence <em>Occurrence</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Occurrence
		 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.impl.FunctionPackageImpl#getOccurrence()
		 * @generated
		 */
		EEnum OCCURRENCE = eINSTANCE.getOccurrence();

	}

} //FunctionPackage
