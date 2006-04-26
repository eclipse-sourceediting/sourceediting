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
package org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObject;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObjectListener;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IField;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IStructure;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IType;

public class ReferencedTypeColumn extends AbstractModelCollection
{
  List listenerList = new ArrayList();
  InternalListener internalListener = new InternalListener();
  
  // todo... really this this model object should listen
  // to the parent of the IType
  //
  public ReferencedTypeColumn(IADTObject model)
  {
    super(model, "ReferencedTypeColumn"); //$NON-NLS-1$
    model.registerListener(internalListener);
    internalListener.recomputeSubListeners();
  }

  public List getChildren()
  {
    List result = new ArrayList();  
    if (model instanceof IStructure)
    {
      IStructure structure = (IStructure)model;
      for (Iterator i = structure.getFields().iterator(); i.hasNext(); )
      {
        IField field = (IField)i.next();
        IType type = field.getType();
        if (type != null)  // && type.isComplexType())
        {
          if (!result.contains(type))
          {
            if (type instanceof IGraphElement)
            {
              if (((IGraphElement)type).isFocusAllowed())
                result.add(type);
            }
          }  
        }  
      }        
    }  
    else if (model instanceof IField)
    {
      IField field = (IField)model;
      IType type = field.getType();
      if (type != null) //  && type.isComplexType())
      {
        if (type instanceof IGraphElement)
        {
          if (((IGraphElement)type).isFocusAllowed())
            result.add(type);        
        }
      }
    }  
    return result;
  }  
  
  public void registerListener(IADTObjectListener listener)
  {
    listenerList.add(listener);
  }

  public void unregisterListener(IADTObjectListener listener)
  {
    listenerList.remove(listener);
  }   
  
  protected void notifyListeners(Object changedObject, String property)
  {
    List clonedListenerList = new ArrayList();
    clonedListenerList.addAll(listenerList);
    for (Iterator i = clonedListenerList.iterator(); i.hasNext(); )
    {
      IADTObjectListener listener = (IADTObjectListener)i.next();
      listener.propertyChanged(this, null);
    } 
  }   
  
  protected class InternalListener implements IADTObjectListener
  {
    List fields = new ArrayList();

    void recomputeSubListeners()
    {
      if (model instanceof IStructure)
      {  
        // remove old ones
        for (Iterator i = fields.iterator(); i.hasNext();)
        {
          IField field = (IField) i.next();
          field.unregisterListener(this);
        }
        // add new ones
        fields.clear();
        IStructure complexType = (IStructure) model;
        for (Iterator i = complexType.getFields().iterator(); i.hasNext();)
        {
          IField field = (IField) i.next();
          fields.add(field);
          field.registerListener(this);
        }
      }
    }

    public void propertyChanged(Object object, String property)
    {
      if (object == model)
      {
        // we need to update the fields we're listening too
        // since these may have changed
        recomputeSubListeners();
      }
      else if (object instanceof IField)
      {
      }  
      notifyListeners(object, property);
    }
  }
}
