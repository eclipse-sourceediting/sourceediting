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
package org.eclipse.wst.xsd.ui.internal.refactor.wizard;

import org.eclipse.osgi.util.NLS;

public class RefactoringWizardMessages  extends NLS {

	private static final String BUNDLE_NAME= "org.eclipse.wst.xsd.ui.internal.refactor.wizard.messages";//$NON-NLS-1$

	public static String RefactorMenu_label;
	public static String RefactorActionGroup_no_refactoring_available;

	public static String RenameAction_rename;
	public static String RenameAction_unavailable;
	public static String RenameAction_text;

	public static String RenameInputWizardPage_new_name;
	public static String RenameRefactoringWizard_internal_error;
	
	public static String RenameTargetNamespace_text;

	public static String RenameXSDElementAction_exception;
	public static String RenameXSDElementAction_not_available;
	public static String RenameXSDElementAction_name;

	public static String RenameSupport_dialog_title;
	public static String RenameSupport_not_available;

	public static String RenameComponentWizard_defaultPageTitle;
	public static String RenameComponentWizard_inputPage_description;

	public static String RenameInputWizardPage_update_references;
	public static String XSDComponentRenameChange_name;
	public static String XSDComponentRenameChange_Renaming;
	public static String ResourceRenameParticipant_compositeChangeName;
	public static String RenameResourceChange_rename_resource_reference_change;
	public static String XSDRenameResourceChange_name;
	public static String RenameResourceRefactoring_Internal_Error;
	public static String RenameResourceRefactoring_alread_exists;
	public static String RenameResourceRefactoring_invalidName;
	public static String RenameResourceProcessor_name;
	public static String MakeAnonymousTypeGlobalAction_text; 
	public static String MakeLocalElementGlobalAction_text;
	public static String XSDComponentRenameParticipant_Component_Refactoring_updates;
	public static String WSDLComponentRenameParticipant_Component_Refactoring_updates;
	public static String RenameComponentProcessor_Component_Refactoring_updates;
	public static String RenameComponentProcessor_Component_Refactoring_update_declatation;
	public static String RenameComponentProcessor_Component_Refactoring_update_reference;
	public static String XSDComponentRenameParticipant_xsd_component_rename_participant;
	public static String WSDLComponentRenameParticipant_wsdl_component_rename_participant;
	public static String ResourceRenameParticipant_File_Rename_update_reference;


	private RefactoringWizardMessages() {
		// Do not instantiate
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME, RefactoringWizardMessages.class);
	}
}
