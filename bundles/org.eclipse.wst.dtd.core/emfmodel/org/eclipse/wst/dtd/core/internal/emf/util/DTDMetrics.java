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

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.wst.dtd.core.internal.emf.DTDElement;
import org.eclipse.wst.dtd.core.internal.emf.DTDElementReferenceContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDFile;


public class DTDMetrics {
	protected DTDFile dtdFile;
	protected Hashtable elementReferenceMap;

	public DTDMetrics(DTDFile dtdFile) {
		this.dtdFile = dtdFile;
	}

	public Hashtable getElementReferences() {
		if (elementReferenceMap == null) {
			elementReferenceMap = new Hashtable();
			new DTDVisitor() {

				public void visitDTDElementReferenceContent(DTDElementReferenceContent elementReferenceContent) {
					DTDElement dtdElement = elementReferenceContent.getReferencedElement();
					Object visitation = elementReferenceMap.get(dtdElement);
					if (visitation == null) {
						elementReferenceMap.put(dtdElement, visitation = new Vector());
					}
					((Vector) visitation).addElement(elementReferenceContent);
				}
			}.visitDTDFile(dtdFile);
		}

		return elementReferenceMap;
	}

	public int getElementReferenceCount(DTDElement dtdElement) {
		Object elementReferences = getElementReferences().get(dtdElement);
		return elementReferences == null ? 0 : ((Vector) elementReferences).size();
	}

	public DTDElement getLeastReferencedElement() {
		DTDElement result = null;
		int lowestReferenceCount = Integer.MAX_VALUE;

		Collection elements = dtdFile.listDTDElement();
		for (Iterator i = elements.iterator(); i.hasNext();) {
			DTDElement element = (DTDElement) i.next();
			int count = getElementReferenceCount(element);
			if (count < lowestReferenceCount) {
				result = element;
				lowestReferenceCount = count;
			}
		}

		return result;
	}

	/**
	 * @generated
	 */
	protected Hashtable getElementReferencesGen() {

		if (elementReferenceMap == null) {
			elementReferenceMap = new Hashtable();
			new DTDVisitor() {

				public void visitDTDElementReferenceContent(DTDElementReferenceContent elementReferenceContent) {
					DTDElement dtdElement = elementReferenceContent.getReferencedElement();
					Object visitation = elementReferenceMap.get(dtdElement);
					if (visitation == null) {
						elementReferenceMap.put(dtdElement, visitation = new Vector());
					}
					((Vector) visitation).addElement(elementReferenceContent);
				}
			}.visitDTDFile(dtdFile);
		}

		return elementReferenceMap;
	}

	/**
	 * @generated
	 */
	protected int getElementReferenceCountGen(DTDElement dtdElement) {

		Object elementReferences = getElementReferences().get(dtdElement);
		return elementReferences == null ? 0 : ((Vector) elementReferences).size();
	}

	/**
	 * @generated
	 */
	protected DTDElement getLeastReferencedElementGen() {

		DTDElement result = null;
		int lowestReferenceCount = Integer.MAX_VALUE;

		Collection elements = dtdFile.listDTDElement();
		for (Iterator i = elements.iterator(); i.hasNext();) {
			DTDElement element = (DTDElement) i.next();
			int count = getElementReferenceCount(element);
			if (count < lowestReferenceCount) {
				result = element;
				lowestReferenceCount = count;
			}
		}

		return result;
	}
}
