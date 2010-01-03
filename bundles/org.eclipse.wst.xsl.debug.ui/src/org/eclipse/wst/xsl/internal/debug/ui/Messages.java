/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver (STAR) - updated to meet Galileo requirements
 *     Stuart Harper - added "open files" selector
 *******************************************************************************/
package org.eclipse.wst.xsl.internal.debug.ui;

import org.eclipse.osgi.util.NLS;

/**
 * Messages for the debug.ui package.
 * 
 * @author Doug Satchwell
 * @since 1.0
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.wst.xsl.internal.debug.ui.messages"; //$NON-NLS-1$

	/**
	 * A name for the remove action.
	 */
	public static String RemoveAction_Text;

	/**
	 * A name for the <code>AddWorkspaceFileAction</code> action.
	 */
	public static String AddWorkspaceFileAction_Text;

	/**
	 * A name for the <code>AddExternalFileAction</code> action.
	 */
	public static String AddExternalFileAction_Text;

	/**
	 * A name for the <code>MoveDownAction</code> action.
	 */
	public static String MoveDownAction_Text;

	/**
	 * A name for the <code>MoveUpAction</code> action.
	 */
	public static String MoveUpAction_Text;

	/**
	 * A heading for the <code>AddExternalFileAction</code> dialog.
	 */
	public static String AddExternalFileAction_Selection_3;

	/**
	 * A message for the <code>AddWorkspaceFileAction</code> dialog.
	 */
	public static String AddWorkspaceFileAction_DialogMessage;

	/**
	 * A title for the <code>AddWorkspaceFileAction</code> dialog.
	 */
	public static String AddWorkspaceFileAction_DialogTitle;

	/**
	 * A name for the <code>AddParameterAction</code> action.
	 */
	public static String AddParameterAction;

	/**
	 * A name for the <code>RemoveParameterAction</code> action.
	 */
	public static String RemoveParameterAction;

	/**
	 * A label for the Variables button.
	 */
	public static String VariablesFieldButton_Text;

	/**
	 * A title for the <code>AddParameterAction</code> dialog.
	 */
	public static String AddParameterAction_Dialog;

	/**
	 * A label for the <code>AddParameterAction</code> name text box.
	 */
	public static String AddParameterAction_Dialog_Name;

	/**
	 * A label for the <code>AddParameterAction</code> type combo.
	 */
	public static String AddParameterAction_Dialog_Type;

	/**
	 * A label for the <code>AddParameterAction</code> value text box.
	 */
	public static String AddParameterAction_Dialog_Value;

	public static String XSLBreakpointProvider_0;

	public static String StylesheetEntryLabelProvider_Invalid_path;

	public static String TransformsBlock_0;

	public static String TransformsBlock_Name;

	public static String InputFileBlock_DIRECTORY_NOT_SPECIFIED;

	public static String InputFileBlock_DIRECTORY_DOES_NOT_EXIST;

	public static String InputFileBlock_GROUP_NAME;

	public static String InputFileBlock_DEFAULT_RADIO;

	public static String InputFileBlock_OTHER_RADIO;

	public static String InputFileBlock_DIALOG_MESSAGE;

	public static String InputFileBlock_WORKSPACE_DIALOG_MESSAGE;

	public static String InputFileBlock_VARIABLES_BUTTON;

	public static String InputFileBlock_FILE_SYSTEM_BUTTON;

	public static String InputFileBlock_WORKSPACE_BUTTON;

	public static String InputFileBlock_OPENFILES_BUTTON;

	public static String InputFileBlock_OPENFILES_DIALOG;

	public static String InputFileBlock_Name;

	public static String InputFileBlock_Exception_occurred_reading_configuration;

	public static String InputFileBlock_WORKSPACE_DIALOG_TITLE;

	public static String XSLMainTab_TabName;

	public static String TransformsBlock_ParametersLabel;

	public static String TransformsBlock_StylesheetsLabel;

	public static String ParametersBlock_0;
	public static String ParametersBlock_1;
	public static String ParametersBlock_10;
	public static String ParametersBlock_11;
	public static String ParametersBlock_2;
	public static String ParametersBlock_3;
	public static String ParametersBlock_4;
	public static String ParametersBlock_5;
	public static String ParametersBlock_6;
	public static String ParametersBlock_7;
	public static String ParametersBlock_8;
	public static String ParametersBlock_9;

	public static String OutputPropertiesBlock_0;
	public static String OutputPropertiesBlock_1;
	public static String OutputPropertiesBlock_13;
	public static String OutputPropertiesBlock_3;
	public static String OutputPropertiesBlock_4;
	public static String OutputPropertiesBlock_5;
	public static String OutputPropertiesBlock_7;
	public static String OutputPropertiesBlock_8;
	public static String OutputPropertiesBlock_9;
	public static String OutputTab_0;
	public static String OutputTab_1;

	public static String OutputTypeBlock_Group_Name;

	public static String OutputFileBlock_0;

	public static String OutputFileBlock_1;

	public static String OutputFileBlock_2;

	public static String OutputFileBlock_7;

	public static String OutputFileBlock_8;

	public static String OutputFileBlock_9;

	public static String OutputFileBlock_DIRECTORY_NOT_SPECIFIED;

	public static String OutputFileBlock_DIRECTORY_DOES_NOT_EXIST;

	public static String OutputFileBlock_GROUP_NAME;

	public static String OutputFileBlock_DEFAULT_RADIO;

	public static String OutputFileBlock_OTHER_RADIO;

	public static String OutputFileBlock_DIALOG_MESSAGE;

	public static String OutputFileBlock_WORKSPACE_DIALOG_MESSAGE;

	public static String OutputFileBlock_VARIABLES_BUTTON;

	public static String OutputFileBlock_FILE_SYSTEM_BUTTON;

	public static String OutputFileBlock_WORKSPACE_BUTTON;

	public static String OutputFileBlock_Name;

	public static String OutputFileBlock_Exception_occurred_reading_configuration;

	public static String OutputFileBlock_WORKSPACE_DIALOG_TITLE;

	public static String OutputFileBlock_Exception_occurred_saving_configuration;

	public static String OutputFOFileBlock_DIRECTORY_NOT_SPECIFIED;

	public static String OutputFOFileBlock_DIRECTORY_DOES_NOT_EXIST;

	public static String OutputFOFileBlock_GROUP_NAME;

	public static String OutputFOFileBlock_DEFAULT_RADIO;

	public static String OutputFOFileBlock_OTHER_RADIO;

	public static String OutputFOFileBlock_DIALOG_MESSAGE;

	public static String OutputFOFileBlock_WORKSPACE_DIALOG_MESSAGE;

	public static String OutputFOFileBlock_VARIABLES_BUTTON;

	public static String OutputFOFileBlock_FILE_SYSTEM_BUTTON;

	public static String OutputFOFileBlock_WORKSPACE_BUTTON;

	public static String OutputFOFileBlock_Name;

	public static String OutputFOFileBlock_Exception_occurred_reading_configuration;

	public static String OutputFOFileBlock_WORKSPACE_DIALOG_TITLE;

	public static String OutputFOFileBlock_Exception_occurred_saving_configuration;

	public static String ResourceSelectionBlock_0;
	public static String XSLLaunchShortcut_0;
	public static String XSLLaunchShortcut_1;
	public static String XSLLaunchShortcut_2;
	public static String XSLLaunchShortcut_6;
	public static String XSLSelectExisting;

	private Messages() {
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

}
