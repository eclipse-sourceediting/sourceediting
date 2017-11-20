/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.dtd.core.internal.emf.util;

import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.dtd.core.internal.emf.DTDAttribute;
import org.eclipse.wst.dtd.core.internal.emf.DTDElement;
import org.eclipse.wst.dtd.core.internal.emf.DTDEntity;
import org.eclipse.wst.dtd.core.internal.emf.DTDFile;
import org.eclipse.wst.dtd.core.internal.emf.DTDNotation;



public class DTDUniqueNameHelper {
	public DTDUniqueNameHelper() {

	}

	static public String getUniqueElementName(DTDFile dtdFile) {
		List elements = dtdFile.listDTDElement();
		return getUniqueName(elements, "NewElement"); //$NON-NLS-1$
	}

	static public String getUniqueEntityName(DTDFile dtdFile) {
		List entities = dtdFile.listDTDEntity();
		return getUniqueName(entities, "NewEntity"); //$NON-NLS-1$
	}

	static public String getUniqueNotationName(DTDFile dtdFile) {
		List notations = dtdFile.listDTDNotation();
		return getUniqueName(notations, "NewNotation"); //$NON-NLS-1$
	}

	static public String getUniqueAttributeName(DTDElement element) {
		List attrs = element.getDTDAttribute();
		return getUniqueName(attrs, "NewAttribute"); //$NON-NLS-1$
	}

	static public String getUniqueName(List objs, String token) {
		int counter = 1;

		boolean uniqueName = false;
		while (!uniqueName) {
			String newName = token + new Integer(counter++);
			uniqueName = true;
			Iterator iter = objs.iterator();
			while (iter.hasNext()) {
				Object obj = iter.next();
				String objName = getName(obj);
				if (objName.equals(newName)) {
					uniqueName = false;
					break;
				}
			}
			if (uniqueName) {
				return newName;
			}
		}
		// we shouldn't get here
		return "No Name found"; //$NON-NLS-1$
	}

	static public String getName(Object obj) {
		if (obj instanceof DTDElement) {
			return ((DTDElement) obj).getName();
		}
		else if (obj instanceof DTDEntity) {
			return ((DTDEntity) obj).getName();
		}
		else if (obj instanceof DTDNotation) {
			return ((DTDNotation) obj).getName();
		}
		else if (obj instanceof DTDAttribute) {
			return ((DTDAttribute) obj).getName();
		}
		return ""; //$NON-NLS-1$
	}


	/**
	 * @generated
	 */
	protected static String getUniqueElementNameGen(DTDFile dtdFile) {

		List elements = dtdFile.listDTDElement();
		return getUniqueName(elements, "NewElement"); //$NON-NLS-1$
	}

	/**
	 * @generated
	 */
	protected static String getUniqueEntityNameGen(DTDFile dtdFile) {

		List entities = dtdFile.listDTDEntity();
		return getUniqueName(entities, "NewEntity"); //$NON-NLS-1$
	}

	/**
	 * @generated
	 */
	protected static String getUniqueNotationNameGen(DTDFile dtdFile) {

		List notations = dtdFile.listDTDNotation();
		return getUniqueName(notations, "NewNotation"); //$NON-NLS-1$
	}

	/**
	 * @generated
	 */
	protected static String getUniqueAttributeNameGen(DTDElement element) {

		List attrs = element.getDTDAttribute();
		return getUniqueName(attrs, "NewAttribute"); //$NON-NLS-1$
	}

	/**
	 * @generated
	 */
	protected static String getUniqueNameGen(List objs, String token) {

		int counter = 1;

		boolean uniqueName = false;
		while (!uniqueName) {
			String newName = token + new Integer(counter++);
			uniqueName = true;
			Iterator iter = objs.iterator();
			while (iter.hasNext()) {
				Object obj = iter.next();
				String objName = getName(obj);
				if (objName.equals(newName)) {
					uniqueName = false;
					break;
				}
			}
			if (uniqueName) {
				return newName;
			}
		}
		// we shouldn't get here
		return "No Name found"; //$NON-NLS-1$
	}

	/**
	 * @generated
	 */
	protected static String getNameGen(Object obj) {

		if (obj instanceof DTDElement) {
			return ((DTDElement) obj).getName();
		}
		else if (obj instanceof DTDEntity) {
			return ((DTDEntity) obj).getName();
		}
		else if (obj instanceof DTDNotation) {
			return ((DTDNotation) obj).getName();
		}
		else if (obj instanceof DTDAttribute) {
			return ((DTDAttribute) obj).getName();
		}
		return ""; //$NON-NLS-1$
	}
}// DTDUniqueNameHelper
