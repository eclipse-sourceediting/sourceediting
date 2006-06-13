/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.refactor.actions;

import java.util.ArrayList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.wst.xsd.ui.internal.refactor.wizard.RefactorActionGroup;
import org.eclipse.xsd.XSDSchema;

public class XSDRefactorActionGroup extends RefactorActionGroup {

	private static final String MAKE_ELEMENT_GLOBAL = "org.eclipse.wst.xsd.ui.refactor.makeElementGlobal"; //$NON-NLS-1$

	private static final String MAKE_TYPE_GLOBAL = "org.eclipse.wst.xsd.ui.refactor.makeTypeGlobal"; //$NON-NLS-1$

	private static final String RENAME_ELEMENT = "org.eclipse.wst.xsd.ui.refactor.rename.element"; //$NON-NLS-1$

	//private static final String RENAME_TARGET_NAMESPCE = "org.eclipse.wst.xsd.ui.refactor.renameTargetNamespace"; //$NON-NLS-1$

	private SelectionDispatchAction fMakeLocalElementGlobal;

	private SelectionDispatchAction fMakeLocalTypeGlobal;

	public XSDRefactorActionGroup(ISelection selection,
			XSDSchema schema) {
		super(selection);
		fEditorActions = new ArrayList();
		fRenameAction = new RenameAction(selection, schema);
		fRenameAction.setActionDefinitionId(RENAME_ELEMENT);
		fEditorActions.add(fRenameAction);

		//fRenameTargetNamespace = new RenameTargetNamespaceAction(
		//		selection, schema);
		//fRenameTargetNamespace.setActionDefinitionId(RENAME_TARGET_NAMESPCE);
		//fEditorActions.add(fRenameTargetNamespace);

		fMakeLocalElementGlobal = new MakeLocalElementGlobalAction(
				selection, schema);
		fMakeLocalElementGlobal.setActionDefinitionId(MAKE_ELEMENT_GLOBAL);
		fEditorActions.add(fMakeLocalElementGlobal);

		fMakeLocalTypeGlobal = new MakeAnonymousTypeGlobalAction(
				selection, schema);
		fMakeLocalTypeGlobal.setActionDefinitionId(MAKE_TYPE_GLOBAL);
		fEditorActions.add(fMakeLocalTypeGlobal);

		initAction(fRenameAction, selection);
		//initAction(fRenameTargetNamespace, selection);
		initAction(fMakeLocalElementGlobal, selection);
		initAction(fMakeLocalTypeGlobal, selection);
	}

	public void dispose() {
//		disposeAction(fRenameAction, selection);
//		disposeAction(fMakeLocalElementGlobal, selection);
//		disposeAction(fMakeLocalTypeGlobal, selection);
//		disposeAction(fRenameTargetNamespace, selection);
		super.dispose();
	}

}
