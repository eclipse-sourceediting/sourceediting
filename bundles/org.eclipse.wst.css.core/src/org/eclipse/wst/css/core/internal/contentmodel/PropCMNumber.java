/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.contentmodel;



import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.eclipse.wst.css.core.internal.provisional.document.ICSSPrimitiveValue;
import org.w3c.dom.css.CSSPrimitiveValue;


/**
 * 
 */
public class PropCMNumber extends PropCMNode {

	private static java.util.Hashtable instances = null;
	public final static java.lang.String VAL_ANGLE = "angle";//$NON-NLS-1$
	public final static java.lang.String VAL_FREQUENCY = "frequency";//$NON-NLS-1$
	public final static java.lang.String VAL_INTEGER = "integer";//$NON-NLS-1$
	public final static java.lang.String VAL_LENGTH = "length";//$NON-NLS-1$
	public final static java.lang.String VAL_PERCENTAGE = "percentage";//$NON-NLS-1$
	public final static java.lang.String VAL_TIME = "time";//$NON-NLS-1$
	public final static java.lang.String VAL_HASH = "hash"; //$NON-NLS-1$
	public final static java.lang.String VAL_NUM = "number";//$NON-NLS-1$
	public final static String DIM_CM = "cm";//$NON-NLS-1$
	public final static String DIM_DEG = "deg";//$NON-NLS-1$
	public final static String DIM_EMS = "em";//$NON-NLS-1$
	public final static String DIM_EXS = "ex";//$NON-NLS-1$
	public final static String DIM_GRAD = "grad";//$NON-NLS-1$
	public final static String DIM_HASH = "#";//$NON-NLS-1$
	public final static String DIM_HZ = "Hz";//$NON-NLS-1$
	public final static String DIM_IN = "in";//$NON-NLS-1$
	public final static String DIM_INTEGER = "INTEGER";//$NON-NLS-1$
	public final static String DIM_KHZ = "kHz";//$NON-NLS-1$
	public final static String DIM_MM = "mm";//$NON-NLS-1$
	public final static String DIM_MS = "ms";//$NON-NLS-1$
	public final static String DIM_NUMBER = "NUMBER";//$NON-NLS-1$
	public final static String DIM_PC = "pc";//$NON-NLS-1$
	public final static String DIM_PERCENTAGE = "%";//$NON-NLS-1$
	public final static String DIM_PT = "pt";//$NON-NLS-1$
	public final static String DIM_PX = "px";//$NON-NLS-1$
	public final static String DIM_RAD = "rad";//$NON-NLS-1$
	public final static String DIM_S = "s";//$NON-NLS-1$
	private java.util.Vector fDims = new Vector();

	/**
	 * 
	 */
	protected PropCMNumber(String name) {
		super(name);
	}

	/**
	 * 
	 */
	public static String getDimension(short primitiveType) {
		switch (primitiveType) {
			case CSSPrimitiveValue.CSS_CM :
				return DIM_CM;
			case CSSPrimitiveValue.CSS_DEG :
				return DIM_DEG;
			case CSSPrimitiveValue.CSS_EMS :
				return DIM_EMS;
			case CSSPrimitiveValue.CSS_EXS :
				return DIM_EXS;
			case CSSPrimitiveValue.CSS_GRAD :
				return DIM_GRAD;
			case ICSSPrimitiveValue.CSS_HASH :
				return DIM_HASH; // prefix dimension ....
			case CSSPrimitiveValue.CSS_HZ :
				return DIM_HZ;
			case CSSPrimitiveValue.CSS_IN :
				return DIM_IN;
			case ICSSPrimitiveValue.CSS_INTEGER :
				return "";//$NON-NLS-1$
			case CSSPrimitiveValue.CSS_KHZ :
				return DIM_KHZ;
			case CSSPrimitiveValue.CSS_MM :
				return DIM_MM;
			case CSSPrimitiveValue.CSS_MS :
				return DIM_MS;
			case CSSPrimitiveValue.CSS_NUMBER :
				return "";//$NON-NLS-1$
			case CSSPrimitiveValue.CSS_PC :
				return DIM_PC;
			case CSSPrimitiveValue.CSS_PERCENTAGE :
				return DIM_PERCENTAGE;
			case CSSPrimitiveValue.CSS_PT :
				return DIM_PT;
			case CSSPrimitiveValue.CSS_PX :
				return DIM_PX;
			case CSSPrimitiveValue.CSS_RAD :
				return DIM_RAD;
			case CSSPrimitiveValue.CSS_S :
				return DIM_S;
		}

		return null;
	}

	/**
	 * 
	 */
	public Set getDimensions() {
		HashSet vals = new HashSet();
		Iterator it = fDims.iterator();
		while (it.hasNext()) {
			vals.add(it.next());
		}

		return vals;
	}

	/**
	 * 
	 */
	void getIdentifiers(Set indents) {
	}

	/**
	 * 
	 */
	static public PropCMNumber getInstanceOf(String name) {
		if (name == null)
			return null;

		// initialize
		if (instances == null)
			instances = new Hashtable(20);

		// Initialize of DB
		if (isNeedInitialize())
			PropCMNode.initPropertyCM();

		// query
		Object node = instances.get(name);
		if (node != null)
			return (PropCMNumber) node;

		// register
		if (PropCMNode.isLoading()) {
			node = new PropCMNumber(name);
			instances.put(name, node);
		}

		return (PropCMNumber) node;
	}

	/**
	 * 
	 */
	public short getType() {
		return VAL_NUMBER;
	}

	/**
	 * 
	 */
	void getValues(Collection values) {
		if (values != null && !values.contains(this))
			values.add(this);
	}

	/**
	 * 
	 */
	static void initNumberCMDim() {
		PropCMNumber num = getInstanceOf(VAL_ANGLE);
		num.fDims.add(DIM_DEG);
		num.fDims.add(DIM_GRAD);
		num.fDims.add(DIM_RAD);

		num = getInstanceOf(VAL_FREQUENCY);
		num.fDims.add(DIM_HZ);
		num.fDims.add(DIM_KHZ);

		num = getInstanceOf(VAL_HASH);
		num.fDims.add(DIM_HASH);

		// num = getInstanceOf(VAL_INTEGER);
		// num.fDims.add(DIM_INTEGER);

		num = getInstanceOf(VAL_LENGTH);
		num.fDims.add(DIM_CM);
		num.fDims.add(DIM_EMS);
		num.fDims.add(DIM_EXS);
		num.fDims.add(DIM_IN);
		num.fDims.add(DIM_MM);
		num.fDims.add(DIM_PC);
		num.fDims.add(DIM_PT);
		num.fDims.add(DIM_PX);

		// num = getInstanceOf(VAL_NUMBER);
		// num.fDims.add(DIM_NUMBER);

		num = getInstanceOf(VAL_PERCENTAGE);
		num.fDims.add(DIM_PERCENTAGE);

		num = getInstanceOf(VAL_TIME);
		num.fDims.add(DIM_MS);
		num.fDims.add(DIM_S);

	}
}
