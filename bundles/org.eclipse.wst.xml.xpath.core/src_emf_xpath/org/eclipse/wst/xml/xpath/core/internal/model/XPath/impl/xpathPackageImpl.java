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
 * $Id: xpathPackageImpl.java,v 1.3 2008/08/30 03:41:06 dacarver Exp $
 */
package org.eclipse.wst.xml.xpath.core.internal.model.XPath.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.wst.xml.xpath.core.internal.model.XPath.Axis.AxisPackage;

import org.eclipse.wst.xml.xpath.core.internal.model.XPath.Axis.impl.AxisPackageImpl;

import org.eclipse.wst.xml.xpath.core.internal.model.XPath.Component;

import org.eclipse.wst.xml.xpath.core.internal.model.XPath.DataTypes.DataTypesPackage;

import org.eclipse.wst.xml.xpath.core.internal.model.XPath.DataTypes.impl.DataTypesPackageImpl;

import org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.FunctionPackage;

import org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.impl.FunctionPackageImpl;

import org.eclipse.wst.xml.xpath.core.internal.model.XPath.Keywords.KeywordsPackage;

import org.eclipse.wst.xml.xpath.core.internal.model.XPath.Keywords.impl.KeywordsPackageImpl;

import org.eclipse.wst.xml.xpath.core.internal.model.XPath.XPath;
import org.eclipse.wst.xml.xpath.core.internal.model.XPath.XpathCommon;
import org.eclipse.wst.xml.xpath.core.internal.model.XPath.xpathFactory;
import org.eclipse.wst.xml.xpath.core.internal.model.XPath.xpathPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class xpathPackageImpl extends EPackageImpl implements xpathPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = " Copyright (c) 2008 Standards for Technology in Automotive Retail and others.\n All rights reserved. This program and the accompanying materials\n are made available under the terms of the Eclipse Public License v1.0\n which accompanies this distribution, and is available at\n http://www.eclipse.org/legal/epl-v10.html\n\n Contributors:\n     David Carver - initial API and implementation";

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass xPathEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass xpathCommonEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass componentEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.xpathPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private xpathPackageImpl() {
		super(eNS_URI, xpathFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this
	 * model, and for any others upon which it depends.  Simple
	 * dependencies are satisfied by calling this method on all
	 * dependent packages before doing anything else.  This method drives
	 * initialization for interdependent packages directly, in parallel
	 * with this package, itself.
	 * <p>Of this package and its interdependencies, all packages which
	 * have not yet been registered by their URI values are first created
	 * and registered.  The packages are then initialized in two steps:
	 * meta-model objects for all of the packages are created before any
	 * are initialized, since one package's meta-model objects may refer to
	 * those of another.
	 * <p>Invocation of this method will not affect any packages that have
	 * already been initialized.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static xpathPackage init() {
		if (isInited) return (xpathPackage)EPackage.Registry.INSTANCE.getEPackage(xpathPackage.eNS_URI);

		// Obtain or create and register package
		xpathPackageImpl thexpathPackage = (xpathPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof xpathPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new xpathPackageImpl());

		isInited = true;

		// Obtain or create and register interdependencies
		DataTypesPackageImpl theDataTypesPackage = (DataTypesPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(DataTypesPackage.eNS_URI) instanceof DataTypesPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(DataTypesPackage.eNS_URI) : DataTypesPackage.eINSTANCE);
		FunctionPackageImpl theFunctionPackage = (FunctionPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(FunctionPackage.eNS_URI) instanceof FunctionPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(FunctionPackage.eNS_URI) : FunctionPackage.eINSTANCE);
		KeywordsPackageImpl theKeywordsPackage = (KeywordsPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(KeywordsPackage.eNS_URI) instanceof KeywordsPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(KeywordsPackage.eNS_URI) : KeywordsPackage.eINSTANCE);
		AxisPackageImpl theAxisPackage = (AxisPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(AxisPackage.eNS_URI) instanceof AxisPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(AxisPackage.eNS_URI) : AxisPackage.eINSTANCE);

		// Create package meta-data objects
		thexpathPackage.createPackageContents();
		theDataTypesPackage.createPackageContents();
		theFunctionPackage.createPackageContents();
		theKeywordsPackage.createPackageContents();
		theAxisPackage.createPackageContents();

		// Initialize created meta-data
		thexpathPackage.initializePackageContents();
		theDataTypesPackage.initializePackageContents();
		theFunctionPackage.initializePackageContents();
		theKeywordsPackage.initializePackageContents();
		theAxisPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		thexpathPackage.freeze();

		return thexpathPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getXPath() {
		return xPathEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getXPath_Components() {
		return (EReference)xPathEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getXpathCommon() {
		return xpathCommonEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getXpathCommon_BeginColumn() {
		return (EAttribute)xpathCommonEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getXpathCommon_EndColumn() {
		return (EAttribute)xpathCommonEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getXpathCommon_BeginLineNumber() {
		return (EAttribute)xpathCommonEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getXpathCommon_EndLineNumber() {
		return (EAttribute)xpathCommonEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getComponent() {
		return componentEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public xpathFactory getxpathFactory() {
		return (xpathFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		xPathEClass = createEClass(XPATH);
		createEReference(xPathEClass, XPATH__COMPONENTS);

		xpathCommonEClass = createEClass(XPATH_COMMON);
		createEAttribute(xpathCommonEClass, XPATH_COMMON__BEGIN_COLUMN);
		createEAttribute(xpathCommonEClass, XPATH_COMMON__END_COLUMN);
		createEAttribute(xpathCommonEClass, XPATH_COMMON__BEGIN_LINE_NUMBER);
		createEAttribute(xpathCommonEClass, XPATH_COMMON__END_LINE_NUMBER);

		componentEClass = createEClass(COMPONENT);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		DataTypesPackage theDataTypesPackage = (DataTypesPackage)EPackage.Registry.INSTANCE.getEPackage(DataTypesPackage.eNS_URI);
		FunctionPackage theFunctionPackage = (FunctionPackage)EPackage.Registry.INSTANCE.getEPackage(FunctionPackage.eNS_URI);
		KeywordsPackage theKeywordsPackage = (KeywordsPackage)EPackage.Registry.INSTANCE.getEPackage(KeywordsPackage.eNS_URI);
		AxisPackage theAxisPackage = (AxisPackage)EPackage.Registry.INSTANCE.getEPackage(AxisPackage.eNS_URI);

		// Add subpackages
		getESubpackages().add(theDataTypesPackage);
		getESubpackages().add(theFunctionPackage);
		getESubpackages().add(theKeywordsPackage);
		getESubpackages().add(theAxisPackage);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		xPathEClass.getESuperTypes().add(this.getXpathCommon());

		// Initialize classes and features; add operations and parameters
		initEClass(xPathEClass, XPath.class, "XPath", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getXPath_Components(), this.getComponent(), null, "components", null, 1, -1, XPath.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		EOperation op = addEOperation(xPathEClass, null, "setComponents", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getComponent(), "components", 1, -1, IS_UNIQUE, IS_ORDERED);

		initEClass(xpathCommonEClass, XpathCommon.class, "XpathCommon", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getXpathCommon_BeginColumn(), ecorePackage.getEInt(), "beginColumn", null, 1, 1, XpathCommon.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getXpathCommon_EndColumn(), ecorePackage.getEInt(), "endColumn", null, 1, 1, XpathCommon.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getXpathCommon_BeginLineNumber(), ecorePackage.getEInt(), "beginLineNumber", null, 1, 1, XpathCommon.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getXpathCommon_EndLineNumber(), ecorePackage.getEInt(), "endLineNumber", null, 1, 1, XpathCommon.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(componentEClass, Component.class, "Component", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		// Create resource
		createResource(eNS_URI);
	}

} //xpathPackageImpl
