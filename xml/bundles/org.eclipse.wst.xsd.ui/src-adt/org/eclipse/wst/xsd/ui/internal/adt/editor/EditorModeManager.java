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
package org.eclipse.wst.xsd.ui.internal.adt.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

public class EditorModeManager implements IAdaptable
{
  private List modeList = new ArrayList();
  private EditorMode currentMode = null;
  private List listeners = new ArrayList();
  private String extensionPointId;
  private ProductCustomizationProvider productCustomizationProvider;
  
  public EditorModeManager(String extensionPointId)
  {
    this.extensionPointId = extensionPointId;
  }
  
  public void init()
  { 
    readRegistry(extensionPointId);    
    currentMode = getDefaultMode();
  }
  
  protected EditorMode getDefaultMode()
  {
    return (EditorMode)modeList.get(0);    
  }
  
  protected void addMode(EditorMode mode)
  {
    modeList.add(mode);
  }
  
  public EditorMode getEditorMode(String editorModeId)
  {
    for (Iterator i = modeList.iterator(); i.hasNext(); )
    {
      EditorMode editorMode = (EditorMode)i.next();
      if (editorModeId.equals(editorMode.getId()))
      {
        return editorMode;
      }  
    }  
    return null;
  }
  
  public void setCurrentMode(EditorMode mode)
  {
    if (modeList.contains(mode))
    {
      currentMode = mode;
      List clonedList = new ArrayList();
      clonedList.addAll(listeners);
      for (Iterator i = clonedList.iterator(); i.hasNext(); )
      {
        IEditorModeListener listener = (IEditorModeListener)i.next();
        listener.editorModeChanged(mode);
      }  
    }  
  }
  
  public EditorMode getCurrentMode()
  {
    return currentMode;
  }
  
  public EditorMode[] getModes()
  {
    EditorMode[] modes = new EditorMode[modeList.size()];
    modeList.toArray(modes);
    return modes;
  }
  
  public void addListener(IEditorModeListener listener)
  {
    if (!listeners.contains(listener))
    {  
      listeners.add(listener);
    }  
  }
  
  public void removeListener(IEditorModeListener listener)
  {
    listeners.remove(listener);  
  }  
  
  private void readRegistry(String id)
  {    
    IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(id);  
    for (int i = 0; i < elements.length; i++)
    {
      IConfigurationElement element = elements[i];
      String editorModeId = element.getAttribute("id"); //$NON-NLS-1$
      if (editorModeId != null && 
          productCustomizationProvider != null &&
          !productCustomizationProvider.isEditorModeApplicable(editorModeId))
      {
        continue;
      }  
      try
      {
        EditorMode mode = (EditorMode)element.createExecutableExtension("class");
        modeList.add(mode);        
      }
      catch (Exception e)
      {        
      }
    }
  }  
  
  public Object getAdapter(Class adapter)
  {
    return null;
  }


  public void setProductCustomizationProvider(ProductCustomizationProvider productCustomizationProvider)
  {
    this.productCustomizationProvider = productCustomizationProvider;
  }
}
