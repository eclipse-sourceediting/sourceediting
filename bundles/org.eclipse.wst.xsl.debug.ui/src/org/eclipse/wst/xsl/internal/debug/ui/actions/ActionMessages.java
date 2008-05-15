/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.internal.debug.ui.actions;

import org.eclipse.osgi.util.NLS;

/**
 * Messages for the debug UI actions.
 * 
 * @author Doug Satchwell
 */
public final class ActionMessages extends NLS
{
	private static final String BUNDLE_NAME = "org.eclipse.wst.xsl.internal.debug.ui.actions.ActionMessages"; //$NON-NLS-1$

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

	static
	{
		NLS.initializeMessages(BUNDLE_NAME, ActionMessages.class);
	}

}
