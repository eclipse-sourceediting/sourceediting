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
package org.eclipse.wst.xsd.ui.internal.properties;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.xsd.ui.internal.util.ViewUtility;


public abstract class OptionsTextCellEditor extends CellEditor implements SelectionListener, KeyListener
{
	private Composite fEditor;
	protected Text fText;
  protected boolean isTextReadOnly;
	Button moreButton;
	Shell dialog;

	private String fSelection;
	protected Object fValue;
	int selection;
	Object typeObject;
		
	private class ComboCellLayout extends Layout 
	{
		public void layout(Composite editor, boolean force)
		{
			Rectangle bounds= editor.getClientArea();
			Point size= moreButton.computeSize(SWT.DEFAULT, bounds.height, force);
			fText.setBounds(0, 0, bounds.width - size.x, bounds.height);
			moreButton.setBounds(bounds.width - size.x, 0, size.x, size.y);
		}
		
		public Point computeSize(Composite editor, int wHint, int hHint, boolean force)
		{
			if (wHint != SWT.DEFAULT && hHint != SWT.DEFAULT)
			{
				return new Point(wHint, hHint);
			}
			Point size= fText.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
//			size.x += moreButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, force).x;
			return size;
		}
	}

/**
 * Creates a new combo box cell editor with the given choices.
 */
	 public OptionsTextCellEditor(Composite parent)
	 {
		 super(parent);
		 fSelection = "";
	 }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.CellEditor#createControl(org.eclipse.swt.widgets.Composite)
   */
	protected Control createControl(Composite parent)
	{
		fEditor = ViewUtility.createComposite(parent, 2);
		fEditor.setLayout(new ComboCellLayout());
 
    if (isTextReadOnly)
    {
		  fText = new Text(fEditor, SWT.LEFT | SWT.READ_ONLY);
    }
    else
    {
      fText = new Text(fEditor, SWT.LEFT);
    }
//    fText.setEnabled(false);

		fText.setBackground(parent.getBackground());
		fText.setText("");
    fText.addKeyListener(this);
		fText.addFocusListener(new FocusAdapter()
    {
			public void focusLost(FocusEvent e)
      {
				if (!moreButton.isFocusControl())
				{
					OptionsTextCellEditor.this.focusLost();
				}
			}
		});
			
		moreButton = ViewUtility.createPushButton(fEditor, "...");
		moreButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				// System.out.println("More Button Clicked");
				openDialog();
			}
		});
    moreButton.addKeyListener(this);
		moreButton.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				if (!fText.isFocusControl() && (dialog==null || 
								dialog.isDisposed() || 
								(dialog!=null && !dialog.isFocusControl())))
				{
          // System.out.println("MoreButton focusLost");
					OptionsTextCellEditor.this.focusLost();
				}
			}
		});


		setValueValid(true);

		return fEditor;
	}
  
  public void activate()
  {
    // System.out.println("Cell editor activated");
    fText.setText(fValue == null ? "" : fValue.toString());
  }

	protected void focusLost() {
		// System.out.println("CELLEDITOR FOCUS LOST");
		if (isActivated()) {
			applyEditorValueAndDeactivate();
		}
	}

	void applyEditorValueAndDeactivate() {
		//	must set the selection before getting value
//		if (dialog != null  && !dialog.isDisposed())
//		{
//			dialog.close();
//			dialog.dispose();
//		}
		fireApplyEditorValue();
		deactivate();
	}

  public void keyPressed(KeyEvent e)
  {
    if (e.character == SWT.ESC)
     { // Escape character
      fireCancelEditor();
    }
    else if ((e.character == SWT.CR) || (e.character == SWT.LF))
     { // Return key
      applyEditorValueAndDeactivate();
    }
  }
  
  public void keyReleased(KeyEvent e)
  {
    
  }
  
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.CellEditor#doGetValue()
   * Returns the cell editor's value.
	 */
	protected Object doGetValue() 
	{
		return fValue;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.CellEditor#doSetFocus()
   * Set the focus to the cell editor's UI representation.
   */
	protected void doSetFocus()
	{
//		fButton.setFocus();
//		System.out.println("doSetFocus() " + moreButton.setFocus());
		fText.setFocus();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.CellEditor#doSetValue(java.lang.Object)
	 * Sets the value of the cell editor to the given value.
   */
	protected void doSetValue(Object value)
	{
		fValue = value;
	}

  protected Point getButtonAbsoluteLocation()
  {
    Rectangle buttonBounds = moreButton.getBounds();
    int x = buttonBounds.x;
    int y = buttonBounds.y;
    Control c = moreButton;
    while (c != null)
     {
      c = c.getParent();
      if (c == null)
        break;
      x += c.getBounds().x;
      y += c.getBounds().y;
    }
    x += buttonBounds.width + 5;
    y += buttonBounds.height;
    Point p = new Point(x,y);
    return p;
  }
  
  protected void cancel()
  {
    dialog.close();
    dialog.dispose();
  }

	protected abstract void openDialog();	
  
	public void widgetSelected(SelectionEvent e)
	{
	}
	public void widgetDefaultSelected(SelectionEvent e)
  {
  }
}
