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
                                                  
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Text;

public abstract class TextCellEditorManager extends DirectEditManager implements DirectEditPolicyDelegate
{
  protected Label label;                                 

  public TextCellEditorManager(GraphicalEditPart source,	Label label)
  {
	  super(source, TextCellEditor.class, new InternalCellEditorLocator(label));
    this.label = label;     
  }

  protected void initCellEditor() 
  {                                             
  	String initialLabelText = label.getText();
  	getCellEditor().setValue(initialLabelText);
  	Text text = (Text)getCellEditor().getControl();
  	text.setFont(label.getFont());
    text.setForeground(label.getForegroundColor());
    text.setBackground(label.getBackgroundColor());
  	text.selectAll();
  }	          

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
      Text text = (Text)celleditor.getControl();  
	    Point sel = text.getSelection();
	    Point pref = text.computeSize(-1, -1);
	    Rectangle rect = label.getTextBounds().getCopy();
	    label.translateToAbsolute(rect);
	    text.setBounds(rect.x-4, rect.y-1, pref.x+1, pref.y+1);	
	    text.setSelection(0);
	    text.setSelection(sel); 
    }
  }    

  // implements DirectEditPolicyDelegate
  // 
  public void performEdit(CellEditor cellEditor)
  {
    performModify((String)cellEditor.getValue());
  }
}