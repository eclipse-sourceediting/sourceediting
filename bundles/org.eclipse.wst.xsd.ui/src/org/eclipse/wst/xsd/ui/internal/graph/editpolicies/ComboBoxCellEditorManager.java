/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.graph.editpolicies;
                                                  
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public abstract class ComboBoxCellEditorManager extends DirectEditManager implements DirectEditPolicyDelegate
{
  protected Label label;                                 

  public ComboBoxCellEditorManager(GraphicalEditPart source,	Label label)
  {
	  super(source, ComboBoxCellEditor.class, new InternalCellEditorLocator(label));
    this.label = label;                        
  }

  protected void initCellEditor() 
  {                                             
  	String initialLabelText = label.getText();   

    CCombo combo = (CCombo)getCellEditor().getControl();
   	combo.setFont(label.getFont());
    combo.setForeground(label.getForegroundColor());
    combo.setBackground(label.getBackgroundColor());
    /*
	combo.addKeyListener(new KeyAdapter() {
			// hook key pressed - see PR 14201  
			public void keyPressed(KeyEvent keyEvent) {
				if (keyEvent.character == 'z') { 
					getCellEditor().applyEditorValue();
				}
			}
		});
*/
  ICellEditorListener cellEditorListener = new ICellEditorListener()
  {
    public void cancelEditor()
    {
//      System.out.println("cancelEditor");
    }
    public void applyEditorValue()
    {
//      System.out.println("applyEditorValue");
    }
    public void editorValueChanged(boolean old, boolean newState)
    {
//      System.out.println("editorValueChanged");
    }
  };
  getCellEditor().addListener(cellEditorListener);

    String[] item = combo.getItems();
    int index = 0;
    for (int i = 0; i < item.length; i++)
    {
      if (item[i].equals(initialLabelText))
      {
	      getCellEditor().setValue(new Integer(i));
        break;
      }
    } 	
  }	 
         
  // hack... for some reason the ComboBoxCellEditor does't fire an editorValueChanged to set the dirty flag
  // unless we overide this method to return true, the manager is not notified of changes made in the cell editor
  protected boolean isDirty()
  {
	  return true;
  }

  protected CellEditor createCellEditorOn(Composite composite)
  { 
    boolean isLabelTextInList = false;                                       
    List list = computeComboContent();
    for (Iterator i = list.iterator(); i.hasNext(); )
    {
      String string = (String)i.next();
      if (string.equals(label.getText()))
      {                               
         isLabelTextInList = true;
         break;
      }
    } 
         
    if (!isLabelTextInList)
    {
      list.add(label.getText());
    }
                                               
    List sortedList = computeSortedList(list);
    String[] stringArray = new String[sortedList.size()];
    for (int i = 0; i < stringArray.length; i++)
    {
      stringArray[i] = (String)sortedList.get(i);
    }
    return new ComboBoxCellEditor(composite, stringArray);
	}                                                                 

  protected List computeSortedList(List list)
  {
    return list;
  }

  protected abstract List computeComboContent(); 

  protected abstract void performModify(String value);

  public static class InternalCellEditorLocator implements CellEditorLocator
  {
    protected Label label;

    public InternalCellEditorLocator(Label label)
    {
      this.label = label;
    }                   

    public void relocate(CellEditor celleditor) 
    {
      CCombo combo = (CCombo)celleditor.getControl();  
	    Point pref = combo.computeSize(-1, -1);
	    Rectangle rect = label.getTextBounds().getCopy();
	    label.translateToAbsolute(rect);
	    combo.setBounds(rect.x-4, rect.y-1, pref.x+1, pref.y+1);
    }
  } 
   
  // implements DirectEditPolicyDelegate
  // 
  public void performEdit(CellEditor cellEditor)
  {   
    CCombo combo = (CCombo)getCellEditor().getControl();
    int index = combo.getSelectionIndex();              
    if (index != -1)
    {      
      String value = combo.getItem(index);
      performModify(combo.getItem(index));
    }  
    else
    {
      String typedValue = combo.getText();	
      if (combo.indexOf(typedValue) != -1)
      {	      
	    performModify(typedValue);
      }      	
      else
      {
      	String closeMatch = getCloseMatch(typedValue, combo.getItems());
      	if (closeMatch != null)
      	{
		  performModify(closeMatch);      	
      	}
      	else
      	{      	
      	  Display.getCurrent().beep();
      	}
      }
    }    	                                                
  }
  
  protected String getCloseMatch(String value, String[] items)
  {
    int matchIndex = -1;

    for (int i = 0; i < items.length; i++)
    {
    	String item = items[i];
    	String a = getLocalName(value);
    	String b = getLocalName(item);
		  if (a.equalsIgnoreCase(b))
		  { 
			  matchIndex = i;
			  break;				
		  }	    	     
    }  
    return matchIndex != -1 ? items[matchIndex] : null;
  }
  
  protected String getLocalName(String string)
  {
		int index = string.indexOf(":");
	  return (index != -1) ? string.substring(index + 1) : string;  
  }
}
