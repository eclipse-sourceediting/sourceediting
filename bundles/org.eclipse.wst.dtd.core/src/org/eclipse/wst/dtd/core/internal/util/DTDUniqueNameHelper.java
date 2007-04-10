/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.dtd.core.internal.util;

import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.dtd.core.internal.Attribute;
import org.eclipse.wst.dtd.core.internal.CMBasicNode;
import org.eclipse.wst.dtd.core.internal.DTDFile;
import org.eclipse.wst.dtd.core.internal.Element;
import org.eclipse.wst.dtd.core.internal.Entity;
import org.eclipse.wst.dtd.core.internal.Notation;


public class DTDUniqueNameHelper {

	static public String getName(Object obj) {
		if (obj instanceof Element) {
			return ((Element) obj).getName();
		}
		else if (obj instanceof Entity) {
			return ((Entity) obj).getName();
		}
		else if (obj instanceof Notation) {
			return ((Notation) obj).getName();
		}
		else if (obj instanceof Attribute) {
			return ((Attribute) obj).getName();
		}
		else if (obj instanceof CMBasicNode) // Model Group Content
		{
			return ((CMBasicNode) obj).getName();
		}
		return ""; //$NON-NLS-1$
	}

	static public String getUniqueAttributeName(Element element) {
		List attrs = element.getElementAttributes();
		return getUniqueName(attrs, "NewAttribute"); //$NON-NLS-1$
	}

	static public String getUniqueElementName(DTDFile dtdFile) {
		List elements = dtdFile.getElementsAndParameterEntityReferences().getNodes();
		return getUniqueName(elements, "NewElement"); //$NON-NLS-1$
	}

	static public String getUniqueEntityName(DTDFile dtdFile) {
		List entities = dtdFile.getEntities().getNodes();
		return getUniqueName(entities, "NewEntity"); //$NON-NLS-1$
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

	static public String getUniqueNotationName(DTDFile dtdFile) {
		List notations = dtdFile.getNotations().getNodes();
		return getUniqueName(notations, "NewNotation"); //$NON-NLS-1$
	}

	public DTDUniqueNameHelper() {
	}

} // DTDUniqueNameHelper
