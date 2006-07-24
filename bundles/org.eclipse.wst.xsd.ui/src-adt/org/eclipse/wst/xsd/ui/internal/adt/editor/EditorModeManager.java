package org.eclipse.wst.xsd.ui.internal.adt.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

public class EditorModeManager
{
  private List modeList = new ArrayList();
  private EditorMode currentMode = null;
  private List listeners = new ArrayList();
  private String extensionPointId;
  
  public EditorModeManager(String extensionPointId)
  {
    this.extensionPointId = extensionPointId;
  }
  
  public void init()
  { 
    readRegistry(extensionPointId);
    currentMode = (EditorMode)modeList.get(0);
  }
  
  protected void addMode(EditorMode mode)
  {
    modeList.add(mode);
  }
  
  public void setCurrentMode(EditorMode mode)
  {
    if (modeList.contains(mode))
    {
      System.out.println("setCurrentMode:" + mode.getDisplayName());
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
}
