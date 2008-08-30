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
 * $Id: AxisPackageImpl.java,v 1.4 2008/08/30 03:41:05 dacarver Exp $
 */
package org.eclipse.wst.xml.xpath.core.internal.model.XPath.Axis.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.wst.xml.xpath.core.internal.model.XPath.Axis.Axis;
import org.eclipse.wst.xml.xpath.core.internal.model.XPath.Axis.AxisFactory;
import org.eclipse.wst.xml.xpath.core.internal.model.XPath.Axis.AxisPackage;

import org.eclipse.wst.xml.xpath.core.internal.model.XPath.DataTypes.DataTypesPackage;

import org.eclipse.wst.xml.xpath.core.internal.model.XPath.DataTypes.impl.DataTypesPackageImpl;

import org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.FunctionPackage;

import org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.impl.FunctionPackageImpl;

import org.eclipse.wst.xml.xpath.core.internal.model.XPath.Keywords.KeywordsPackage;

import org.eclipse.wst.xml.xpath.core.internal.model.XPath.Keywords.impl.KeywordsPackageImpl;

import org.eclipse.wst.xml.xpath.core.internal.model.XPath.impl.xpathPackageImpl;

import org.eclipse.wst.xml.xpath.core.internal.model.XPath.xpathPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class AxisPackageImpl extends EPackageImpl implements AxisPackage {
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
	private EClass axisEClass = null;

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
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Axis.AxisPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private AxisPackageImpl() {
		super(eNS_URI, AxisFactory.eINSTANCE);
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
	public static AxisPackage init() {
		if (isInited) return (AxisPackage)EPackage.Registry.INSTANCE.getEPackage(AxisPackage.eNS_URI);

		// Obtain or create and register package
		AxisPackageImpl theAxisPackage = (AxisPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof AxisPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new AxisPackageImpl());

		isInited = true;

		// Obtain or create and register interdependencies
		xpathPackageImpl thexpathPackage = (xpathPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(xpathPackage.eNS_URI) instanceof xpathPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(xpathPackage.eNS_URI) : xpathPackage.eINSTANCE);
		DataTypesPackageImpl theDataTypesPackage = (DataTypesPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(DataTypesPackage.eNS_URI) instanceof DataTypesPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(DataTypesPackage.eNS_URI) : DataTypesPackage.eINSTANCE);
		FunctionPackageImpl theFunctionPackage = (FunctionPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(FunctionPackage.eNS_URI) instanceof FunctionPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(FunctionPackage.eNS_URI) : FunctionPackage.eINSTANCE);
		KeywordsPackageImpl theKeywordsPackage = (KeywordsPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(KeywordsPackage.eNS_URI) instanceof KeywordsPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(KeywordsPackage.eNS_URI) : KeywordsPackage.eINSTANCE);

		// Create package meta-data objects
		theAxisPackage.createPackageContents();
		thexpathPackage.createPackageContents();
		theDataTypesPackage.createPackageContents();
		theFunctionPackage.createPackageContents();
		theKeywordsPackage.createPackageContents();

		// Initialize created meta-data
		theAxisPackage.initializePackageContents();
		thexpathPackage.initializePackageContents();
		theDataTypesPackage.initializePackageContents();
		theFunctionPackage.initializePackageContents();
		theKeywordsPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theAxisPackage.freeze();

		return theAxisPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getAxis() {
		return axisEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAxis_Name() {
		return (EAttribute)axisEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAxis_Shortcut() {
		return (EAttribute)axisEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AxisFactory getAxisFactory() {
		return (AxisFactory)getEFactoryInstance();
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
		axisEClass = createEClass(AXIS);
		createEAttribute(axisEClass, AXIS__NAME);
		createEAttribute(axisEClass, AXIS__SHORTCUT);
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
		xpathPackage thexpathPackage = (xpathPackage)EPackage.Registry.INSTANCE.getEPackage(xpathPackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		axisEClass.getESuperTypes().add(thexpathPackage.getXpathCommon());
		axisEClass.getESuperTypes().add(thexpathPackage.getComponent());

		// Initialize classes and features; add operations and parameters
		initEClass(axisEClass, Axis.class, "Axis", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getAxis_Name(), ecorePackage.getEString(), "name", null, 0, 1, Axis.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAxis_Shortcut(), ecorePackage.getEString(), "shortcut", null, 0, 1, Axis.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
	}

} //AxisPackageImpl
