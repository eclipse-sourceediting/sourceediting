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
package org.eclipse.wst.xsd.ui.internal.adt.actions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.wst.xsd.ui.internal.adt.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;


/**
 * Show the properties view in the current perspective.
 */
public class ShowPropertiesViewAction extends BaseSelectionAction
{
	public static final String ID = "org.eclipse.wst.xsd.ui.internal.adt.actions.ShowPropertiesViewAction"; //$NON-NLS-1$
	public static final String PROPERTIES_VIEW_ID = "org.eclipse.ui.views.PropertySheet"; //$NON-NLS-1$
	
  protected static ImageDescriptor enabledImage, disabledImage;

	public ShowPropertiesViewAction(IWorkbenchPart part)
  {
		super(part);
		setId(ID);
		setText(Messages._UI_ACTION_SHOW_PROPERTIES);
		setToolTipText(getText());
    setImageDescriptor(XSDEditorPlugin.getImageDescriptor("icons/elcl16/showproperties_obj.gif") ); //$NON-NLS-1$
	  setDisabledImageDescriptor(XSDEditorPlugin.getImageDescriptor("icons/dlcl16/showproperties_obj.gif") ); //$NON-NLS-1$
	}
  
  protected boolean calculateEnabled()
  {
    return true;
  }
  
  public void run()
  {
    try
    {
      getWorkbenchPart().getSite().getPage().showView(PROPERTIES_VIEW_ID);
    }
    catch (PartInitException pie)
    {

    }
  }
}
