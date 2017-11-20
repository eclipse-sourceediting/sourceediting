/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.refactor.structure;

import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;

public final class MakeAnonymousTypeGlobalCommand extends AbstractCommand {

	String fNewName;

	public MakeAnonymousTypeGlobalCommand(XSDConcreteComponent element,
			String newName) {
		super(element.getContainer());
		setModelObject(element);
		fNewName = newName;
	}

	public void run() {
		XSDConcreteComponent model = getModelObject();
		XSDConcreteComponent parent = model.getContainer();
		XSDTypeDefinition globalTypeDef = null;
		if (model instanceof XSDComplexTypeDefinition) {
			if (parent instanceof XSDElementDeclaration) {
				// clone typedef with it's content and set it global
				globalTypeDef = (XSDComplexTypeDefinition) model
						.cloneConcreteComponent(true, false);
				globalTypeDef.setName(fNewName);
				parent.getSchema().getContents().add(globalTypeDef);
				((XSDElementDeclaration) parent)
						.setTypeDefinition(globalTypeDef);
			}
		} else if (model instanceof XSDSimpleTypeDefinition) {

			XSDSimpleTypeDefinition typeDef = (XSDSimpleTypeDefinition) model;
			if (parent instanceof XSDElementDeclaration) {
				// clone typedef with it's content and set it global
				globalTypeDef = (XSDSimpleTypeDefinition) typeDef
						.cloneConcreteComponent(true, false);
				globalTypeDef.setName(fNewName);
				parent.getSchema().getContents().add(globalTypeDef);
				((XSDElementDeclaration) parent)
						.setTypeDefinition(globalTypeDef);
				formatChild(globalTypeDef.getElement());

			} else if (parent instanceof XSDAttributeDeclaration) {
				// clone typedef with it's content and set it global
				globalTypeDef = (XSDSimpleTypeDefinition) typeDef
						.cloneConcreteComponent(true, false);
				globalTypeDef.setName(fNewName);
				parent.getSchema().getContents().add(globalTypeDef);
				((XSDAttributeDeclaration) parent)
						.setTypeDefinition((XSDSimpleTypeDefinition) globalTypeDef);

			}

		}

		formatChild(globalTypeDef.getElement());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xsd.ui.internal.commands.AbstractCommand#adopt(org.eclipse.xsd.XSDConcreteComponent)
	 */
	protected boolean adopt(XSDConcreteComponent model) {
		// TODO Auto-generated method stub
		return true;
	}
}
