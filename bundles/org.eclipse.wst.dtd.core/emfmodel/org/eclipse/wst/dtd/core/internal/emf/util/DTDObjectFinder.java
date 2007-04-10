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

import org.eclipse.wst.dtd.core.internal.emf.DTDAttribute;
import org.eclipse.wst.dtd.core.internal.emf.DTDElement;
import org.eclipse.wst.dtd.core.internal.emf.DTDElementContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDEntity;
import org.eclipse.wst.dtd.core.internal.emf.DTDNotation;
import org.eclipse.wst.dtd.core.internal.emf.DTDObject;
import org.eclipse.wst.dtd.core.internal.emf.DTDParameterEntityReference;
import org.eclipse.wst.dtd.core.internal.emf.DTDSourceOffset;

public class DTDObjectFinder extends DTDVisitor {
	public class SourceOffset {
		// Convenience class to find containment of object
		private int start, end;

		public SourceOffset(int start, int end) {
			this.start = start;
			this.end = end;
		}

		public boolean contains(SourceOffset other) {
			if (this.start <= other.start && other.start <= this.end) {
				return true;
			} // end of if ()
			return false;
		}

	}

	SourceOffset searchLocation;

	private SourceOffset offsetObjectFor(DTDSourceOffset s) {
		return new SourceOffset(s.getStartOffset(), s.getEndOffset());
	}

	private void checkContainment(DTDObject o) {
		if (offsetObjectFor((DTDSourceOffset) o).contains(searchLocation)) {
			closestObject = o;
		} // end of if ()
	}

	public DTDObjectFinder(int startOffset, int endOffset) {
		searchLocation = new SourceOffset(startOffset, endOffset);
	}

	public void visitDTDNotation(DTDNotation notation) {
		checkContainment(notation);
		super.visitDTDNotation(notation);
	}

	public void visitDTDEntity(DTDEntity entity) {
		checkContainment(entity);
		super.visitDTDEntity(entity);
	}

	public void visitDTDElement(DTDElement element) {
		checkContainment(element);
		super.visitDTDElement(element);
	}

	public void visitDTDAttribute(DTDAttribute attribute) {
		checkContainment(attribute);
		super.visitDTDAttribute(attribute);
	}

	public void visitDTDParameterEntityReference(DTDParameterEntityReference parmEntity) {
		checkContainment(parmEntity);
		super.visitDTDParameterEntityReference(parmEntity);
	}

	public void visitDTDElementContent(DTDElementContent content) {
		checkContainment(content);
		super.visitDTDElementContent(content);
	}

	private DTDObject closestObject = null;

	public DTDObject getClosestObject() {
		return closestObject;
	}

	/**
	 * @generated
	 */
	protected SourceOffset offsetObjectForGen(DTDSourceOffset s) {

		return new SourceOffset(s.getStartOffset(), s.getEndOffset());
	}

	/**
	 * @generated
	 */
	protected void checkContainmentGen(DTDObject o) {

		if (offsetObjectFor((DTDSourceOffset) o).contains(searchLocation)) {
			closestObject = o;
		} // end of if ()
	}

	/**
	 * @generated
	 */
	protected void visitDTDNotationGen(DTDNotation notation) {

		checkContainment(notation);
		super.visitDTDNotation(notation);
	}

	/**
	 * @generated
	 */
	protected void visitDTDEntityGen(DTDEntity entity) {

		checkContainment(entity);
		super.visitDTDEntity(entity);
	}

	/**
	 * @generated
	 */
	protected void visitDTDElementGen(DTDElement element) {

		checkContainment(element);
		super.visitDTDElement(element);
	}

	/**
	 * @generated
	 */
	protected void visitDTDAttributeGen(DTDAttribute attribute) {

		checkContainment(attribute);
		super.visitDTDAttribute(attribute);
	}

	/**
	 * @generated
	 */
	protected void visitDTDParameterEntityReferenceGen(DTDParameterEntityReference parmEntity) {

		checkContainment(parmEntity);
		super.visitDTDParameterEntityReference(parmEntity);
	}

	/**
	 * @generated
	 */
	protected void visitDTDElementContentGen(DTDElementContent content) {

		checkContainment(content);
		super.visitDTDElementContent(content);
	}

	/**
	 * @generated
	 */
	protected DTDObject getClosestObjectGen() {

		return closestObject;
	}
}// DTDObjectFinder
