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
import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.dtd.core.internal.emf.DTDAnyContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDAttribute;
import org.eclipse.wst.dtd.core.internal.emf.DTDElement;
import org.eclipse.wst.dtd.core.internal.emf.DTDElementContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDElementReferenceContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDEmptyContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDEntity;
import org.eclipse.wst.dtd.core.internal.emf.DTDEntityReferenceContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDFile;
import org.eclipse.wst.dtd.core.internal.emf.DTDGroupContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDGroupKind;
import org.eclipse.wst.dtd.core.internal.emf.DTDNotation;
import org.eclipse.wst.dtd.core.internal.emf.DTDObject;
import org.eclipse.wst.dtd.core.internal.emf.DTDPCDataContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDParameterEntityReference;
import org.eclipse.wst.dtd.core.internal.emf.DTDRepeatableContent;



public class DTDVisitor {

	public DTDVisitor() {
	}

	public void visitDTDFile(DTDFile file) {
		Collection notations = file.listDTDNotation();
		for (Iterator i = notations.iterator(); i.hasNext();) {
			visitDTDNotation((DTDNotation) i.next());
		}

		Collection entities = file.listDTDEntity();
		for (Iterator i = entities.iterator(); i.hasNext();) {
			visitDTDEntity((DTDEntity) i.next());
		}

		Collection objects = file.listDTDElementAndDTDParameterEntityReference();
		for (Iterator i = objects.iterator(); i.hasNext();) {
			DTDObject object = (DTDObject) i.next();
			if (object instanceof DTDElement) {
				visitDTDElement((DTDElement) object);
			} // end of if ()
			else {
				visitDTDParameterEntityReference((DTDParameterEntityReference) object);
			} // end of if ()
		}

	}

	public void visitDTDNotation(DTDNotation notation) {
	}

	public void visitDTDEntity(DTDEntity entity) {
	}

	public void visitDTDElement(DTDElement element) {
		visitDTDElementContent(element.getContent());
		visitDTDAttributeList(element.getDTDAttribute());
	}

	public void visitDTDParameterEntityReference(DTDParameterEntityReference parmEntity) {

	}

	public void visitDTDElementContent(DTDElementContent content) {
		// System.out.println("visitDTDElementContent : " + content);
		if (content instanceof DTDEmptyContent) {
			visitDTDEmptyContent((DTDEmptyContent) content);
		}
		else if (content instanceof DTDAnyContent) {
			visitDTDAnyContent((DTDAnyContent) content);
		}
		else if (content instanceof DTDPCDataContent) {
			visitDTDPCDataContent((DTDPCDataContent) content);
		}
		if (content instanceof DTDRepeatableContent) {
			if (content instanceof DTDGroupContent) {
				visitDTDGroupContent((DTDGroupContent) content);
			}
			else if (content instanceof DTDElementReferenceContent) {
				visitDTDElementReferenceContent((DTDElementReferenceContent) content);
			}
			else {
				visitDTDEntityReferenceContent((DTDEntityReferenceContent) content);
			} // end of else

			// System.out.println("occurrence: " +
			// (char)((DTDRepeatableContent)content).getOccurrence());
		}
	}

	public void visitDTDEmptyContent(DTDEmptyContent dtdEmptyContent) {
		// System.out.println("content: EMPTY");
	}

	public void visitDTDAnyContent(DTDAnyContent dtdAnyContent) {
		// System.out.println("content: ANY");
	}

	public void visitDTDPCDataContent(DTDPCDataContent pcDataContent) {
		// System.out.println("content: PCDATA");
	}

	public void visitDTDGroupContent(DTDGroupContent groupContent) {
		if (groupContent.getGroupKind().getValue() == DTDGroupKind.SEQUENCE) {
			// System.out.println("content: Sequence");
		}
		else // if (contentgetGroupKind() == DTDGroupKind.CHOICE)
		{
			// System.out.println("content: Choice");
		}

		Collection groupContents = groupContent.getContent();
		for (Iterator i = groupContents.iterator(); i.hasNext();) {
			visitDTDElementContent((DTDElementContent) i.next());
		}
	}

	public void visitDTDElementReferenceContent(DTDElementReferenceContent elementReferenceContent) {
	}

	public void visitDTDEntityReferenceContent(DTDEntityReferenceContent entityReferenceContent) {
	}

	public void visitDTDAttributeList(List attrs) {
		Iterator iter = attrs.iterator();

		while (iter.hasNext()) {
			visitDTDAttribute((DTDAttribute) iter.next());
		} // end of while ()
	}

	public void visitDTDAttribute(DTDAttribute attribute) {
	}

	/**
	 * @generated
	 */
	protected void visitDTDFileGen(DTDFile file) {

		Collection notations = file.listDTDNotation();
		for (Iterator i = notations.iterator(); i.hasNext();) {
			visitDTDNotation((DTDNotation) i.next());
		}

		Collection entities = file.listDTDEntity();
		for (Iterator i = entities.iterator(); i.hasNext();) {
			visitDTDEntity((DTDEntity) i.next());
		}

		Collection objects = file.listDTDElementAndDTDParameterEntityReference();
		for (Iterator i = objects.iterator(); i.hasNext();) {
			DTDObject object = (DTDObject) i.next();
			if (object instanceof DTDElement) {
				visitDTDElement((DTDElement) object);
			} // end of if ()
			else {
				visitDTDParameterEntityReference((DTDParameterEntityReference) object);
			} // end of if ()
		}
	}

	/**
	 * @generated
	 */
	protected void visitDTDNotationGen(DTDNotation notation) {

	}

	/**
	 * @generated
	 */
	protected void visitDTDEntityGen(DTDEntity entity) {

	}

	/**
	 * @generated
	 */
	protected void visitDTDElementGen(DTDElement element) {

		visitDTDElementContent(element.getContent());
		visitDTDAttributeList(element.getDTDAttribute());
	}

	/**
	 * @generated
	 */
	protected void visitDTDParameterEntityReferenceGen(DTDParameterEntityReference parmEntity) {

	}

	/**
	 * @generated
	 */
	protected void visitDTDElementContentGen(DTDElementContent content) {

		// System.out.println("visitDTDElementContent : " + content);
		if (content instanceof DTDEmptyContent) {
			visitDTDEmptyContent((DTDEmptyContent) content);
		}
		else if (content instanceof DTDAnyContent) {
			visitDTDAnyContent((DTDAnyContent) content);
		}
		else if (content instanceof DTDPCDataContent) {
			visitDTDPCDataContent((DTDPCDataContent) content);
		}
		if (content instanceof DTDRepeatableContent) {
			if (content instanceof DTDGroupContent) {
				visitDTDGroupContent((DTDGroupContent) content);
			}
			else if (content instanceof DTDElementReferenceContent) {
				visitDTDElementReferenceContent((DTDElementReferenceContent) content);
			}
			else {
				visitDTDEntityReferenceContent((DTDEntityReferenceContent) content);
			} // end of else

			// System.out.println("occurrence: " +
			// (char)((DTDRepeatableContent)content).getOccurrence());
		}
	}

	/**
	 * @generated
	 */
	protected void visitDTDEmptyContentGen(DTDEmptyContent dtdEmptyContent) {

		// System.out.println("content: EMPTY");
	}

	/**
	 * @generated
	 */
	protected void visitDTDAnyContentGen(DTDAnyContent dtdAnyContent) {

		// System.out.println("content: ANY");
	}

	/**
	 * @generated
	 */
	protected void visitDTDPCDataContentGen(DTDPCDataContent pcDataContent) {

		// System.out.println("content: PCDATA");
	}

	/**
	 * @generated
	 */
	protected void visitDTDGroupContentGen(DTDGroupContent groupContent) {

		if (groupContent.getGroupKind().getValue() == DTDGroupKind.SEQUENCE) {
			// System.out.println("content: Sequence");
		}
		else // if (contentgetGroupKind() == DTDGroupKind.CHOICE)
		{
			// System.out.println("content: Choice");
		}

		Collection groupContents = groupContent.getContent();
		for (Iterator i = groupContents.iterator(); i.hasNext();) {
			visitDTDElementContent((DTDElementContent) i.next());
		}
	}

	/**
	 * @generated
	 */
	protected void visitDTDElementReferenceContentGen(DTDElementReferenceContent elementReferenceContent) {

	}

	/**
	 * @generated
	 */
	protected void visitDTDEntityReferenceContentGen(DTDEntityReferenceContent entityReferenceContent) {

	}

	/**
	 * @generated
	 */
	protected void visitDTDAttributeListGen(List attrs) {

		Iterator iter = attrs.iterator();

		while (iter.hasNext()) {
			visitDTDAttribute((DTDAttribute) iter.next());
		} // end of while ()
	}

	/**
	 * @generated
	 */
	protected void visitDTDAttributeGen(DTDAttribute attribute) {

	}
}
