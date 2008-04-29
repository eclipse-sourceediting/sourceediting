/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.editor;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDAttributeDeclarationAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDElementDeclarationAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.design.IKeyboardDrag;
import org.eclipse.wst.xsd.ui.internal.adt.design.editpolicies.KeyBoardAccessibilityEditPolicy;
import org.eclipse.wst.xsd.ui.internal.commands.BaseDragAndDropCommand;
import org.eclipse.wst.xsd.ui.internal.commands.XSDAttributeDragAndDropCommand;
import org.eclipse.wst.xsd.ui.internal.commands.XSDElementDragAndDropCommand;
import org.eclipse.wst.xsd.ui.internal.design.editparts.XSDBaseFieldEditPart;
import org.eclipse.xsd.XSDWildcard;

public class KeyboardDragImpl implements IKeyboardDrag
{
  public void performKeyboardDrag(GraphicalEditPart movingElement, int direction)
  {
    KeyBoardAccessibilityEditPolicy policy = (KeyBoardAccessibilityEditPolicy) movingElement.getEditPolicy(KeyBoardAccessibilityEditPolicy.KEY);

    EditPart rightElement = policy.getRelativeEditPart(movingElement, direction);
    policy = (KeyBoardAccessibilityEditPolicy) rightElement.getEditPolicy(KeyBoardAccessibilityEditPolicy.KEY);
    EditPart leftElement = (policy != null) ? policy.getRelativeEditPart(rightElement, direction) : null;

    // TODO: add support for extenders
    if (!(movingElement instanceof XSDBaseFieldEditPart)) return;
    
    XSDBaseFieldEditPart movingField = (XSDBaseFieldEditPart) movingElement;
    XSDBaseFieldEditPart leftField = (XSDBaseFieldEditPart) leftElement;
    XSDBaseFieldEditPart rightField = (XSDBaseFieldEditPart) rightElement;
    
    Object movingObject = movingField.getModel();
    
    BaseDragAndDropCommand command = null;
    if (movingObject instanceof XSDElementDeclarationAdapter || movingObject instanceof XSDWildcard)
    {
      command = new XSDElementDragAndDropCommand(movingField, leftField, rightField, direction);
    }
    else if (movingObject instanceof XSDAttributeDeclarationAdapter)
    {
      command = new XSDAttributeDragAndDropCommand(movingField, leftField, rightField, direction);
    }
    
    if (command != null && command.canExecute())
    {
      command.execute();
      // This is to reselect the moved item
      try
      {
        IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
        if (editor != null && editor.getAdapter(ISelectionProvider.class) != null)
        {
          ISelectionProvider provider = (ISelectionProvider) editor.getAdapter(ISelectionProvider.class);
          if (provider != null)
          {
            provider.setSelection(new StructuredSelection(movingElement.getModel()));
          }
        }
      }
      catch (Exception e)
      {

      }
    }
  }
}
