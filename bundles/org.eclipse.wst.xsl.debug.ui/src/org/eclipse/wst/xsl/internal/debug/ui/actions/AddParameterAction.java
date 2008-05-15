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

import org.eclipse.jface.window.Window;
import org.eclipse.wst.xsl.internal.debug.ui.tabs.main.ParameterViewer;
import org.eclipse.wst.xsl.launching.config.LaunchAttribute;

/**
 * An action that opens a dialog to allow the user to add a parameter to a transform.
 * 
 * @author Doug Satchwell
 */
public class AddParameterAction extends AbstractParameterAction
{
	final String[] types = new String[]
	{ LaunchAttribute.TYPE_STRING, LaunchAttribute.TYPE_BOOLEAN, LaunchAttribute.TYPE_INT, LaunchAttribute.TYPE_DOUBLE, LaunchAttribute.TYPE_FLOAT, LaunchAttribute.TYPE_OBJECT,
			LaunchAttribute.TYPE_CLASS, };

	/**
	 * Create a new instance of this.
	 * 
	 * @param viewer teh viewer
	 */
	public AddParameterAction(ParameterViewer viewer)
	{
		super(ActionMessages.AddParameterAction, viewer);
	}

	@Override
	public void run()
	{
		MultipleInputDialog dialog = new MultipleInputDialog(getShell(), ActionMessages.AddParameterAction_Dialog);
		String namelabel = ActionMessages.AddParameterAction_Dialog_Name;
		dialog.addTextField(namelabel, null, false);
		String typelabel = ActionMessages.AddParameterAction_Dialog_Type;
		dialog.addComboField(typelabel, 0, types);
		String variableslabel = ActionMessages.AddParameterAction_Dialog_Value;
		dialog.addVariablesField(variableslabel, null, false);
		dialog.open();

		if (dialog.getReturnCode() == Window.OK)
		{
			String name = dialog.getStringValue(namelabel);
			int type = dialog.getIntValue(typelabel);
			String value = dialog.getStringValue(variableslabel);
			LaunchAttribute parameter = null;
			if (value != null && value.indexOf("${") > -1) //$NON-NLS-1$
				parameter = new LaunchAttribute(name, types[type], value);
			else
				parameter = new LaunchAttribute(name, types[type], value);
			getViewer().addParameter(parameter);
		}
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		// do nothing
	}

}
