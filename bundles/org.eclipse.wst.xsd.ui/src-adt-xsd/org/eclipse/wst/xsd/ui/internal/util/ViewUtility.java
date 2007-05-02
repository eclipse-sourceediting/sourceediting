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
package org.eclipse.wst.xsd.ui.internal.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

// issue (cs) can we get rid of this class?
// I've stripped it down a whole lot... but it'd be great to get rid of it
//
public class ViewUtility
{
  private static Font font;

  private static Font getFont()
  {
    if (font == null)
    {              
      font = new Font(Display.getCurrent(), "ms sans serif", 8, SWT.NORMAL);  
    }
    return font;
  }

  public static void setComposite(Composite comp)
  {
    // deprecated.  Remove later
  }
  public static Composite createComposite(Composite parent, int numColumns)
  {
    Composite composite = new Composite(parent, SWT.NONE);

    composite.setFont(getFont());

    GridLayout layout = new GridLayout();
    layout.numColumns = numColumns;
    composite.setLayout(layout);

    GridData data = new GridData();
    data.verticalAlignment = GridData.FILL;
    data.horizontalAlignment = GridData.FILL;
    composite.setLayoutData(data);
    return composite;
  }

  public static Composite createComposite(Composite parent, int numColumns, boolean horizontalFill)
  {
    if (!horizontalFill)
    {
      createComposite(parent, numColumns);
    }

    Composite composite = new Composite(parent, SWT.NONE);

    composite.setFont(getFont());

    GridLayout layout = new GridLayout();
    layout.numColumns = numColumns;
    composite.setLayout(layout);

    GridData data = new GridData();
    data.verticalAlignment = GridData.FILL;
    data.horizontalAlignment = GridData.FILL;
    data.grabExcessHorizontalSpace = true;
    composite.setLayoutData(data);

    return composite;
  }

  public static Label createHorizontalFiller(Composite parent, int horizontalSpan) 
  {
    Label label = new Label(parent, SWT.LEFT);
    GridData data = new GridData();
    data.horizontalAlignment = GridData.FILL;
    data.horizontalSpan = horizontalSpan;
    label.setLayoutData(data);
    return label;
  }

  /**
   * Helper method for creating labels.
   */
  public static Label createLabel(Composite parent, String text) 
  {
    Label label = new Label(parent, SWT.LEFT);
    label.setText(text);

    GridData data = new GridData();
    data.horizontalAlignment = GridData.FILL;
    label.setLayoutData(data);
    return label;
  }

	public Label createLabel(Composite parent, int style, String text)
	{
		Label label = new Label(parent, style);
//		setColor(label);
		label.setText(text);

		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		label.setLayoutData(data);
		return label;
	}
  
  public static Label createLabel(Composite parent, String text, int alignment)
  {
    Label label = new Label(parent, SWT.LEFT);
    label.setText(text);

    GridData data = new GridData();
    data.horizontalAlignment = GridData.FILL;
    data.verticalAlignment = alignment;
    label.setLayoutData(data);
    return label;
  }

 


  /**
   * Create radio button
   */
  public static Button createRadioButton(Composite parent, String label)
  {
    Button button = new Button(parent, SWT.RADIO);
    button.setText(label);

    GridData data = new GridData();
    data.horizontalAlignment = GridData.FILL;
    button.setLayoutData(data);

    return button;
  }

  /**
   * Helper method for creating check box
   */
  public static Button createCheckBox(Composite parent, String label) 
  {
    Button button = new Button(parent, SWT.CHECK);
    button.setText(label);

    GridData data = new GridData();
    data.horizontalAlignment = GridData.FILL;
    button.setLayoutData(data);
    return button;
  }

  public static Combo createComboBox(Composite parent)
  {
    return createComboBox(parent, true);
  }

  public static Combo createComboBox(Composite parent, boolean isReadOnly)
  {
    int style = isReadOnly == true ? SWT.READ_ONLY : SWT.DROP_DOWN;

    Combo combo = new Combo(parent, style);

    GridData data = new GridData();
    data.horizontalAlignment = GridData.FILL;
    data.grabExcessHorizontalSpace = true;
    combo.setLayoutData(data);
    return combo;
  }


  public static Text createTextField(Composite parent, int width)
  {
    Text text = new Text(parent, SWT.SINGLE | SWT.BORDER);

    GridData data = new GridData();
    data.horizontalAlignment = GridData.FILL;
    data.grabExcessHorizontalSpace = true;
    data.widthHint = width;
    text.setLayoutData(data);

    return text;
  }

  /**
   * <code>createWrappedMultiTextField</code> creates a wrapped multitext field
   *
   * @param parent a <code>Composite</code> value
   * @param width an <code>int</code> value
   * @param numLines an <code>int</code> value representing number of characters in height
   * @param verticalFill a <code>boolean</code> value
   * @return a <code>Text</code> value
   */
  public static Text createWrappedMultiTextField(Composite parent, int width, int numLines, boolean verticalFill)
  {
    Text text = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);

    GridData data = new GridData();
    data.horizontalAlignment = GridData.FILL;
    data.grabExcessHorizontalSpace = true;
    if (verticalFill)
    {
      data.verticalAlignment = GridData.FILL;
      data.grabExcessVerticalSpace = true;
    }      
    data.widthHint = width;
    FontData[] fontData = getFont().getFontData();
    // hack for now where on Windows, only 1 fontdata exists
    data.heightHint = numLines * fontData[0].getHeight();
    text.setLayoutData(data);

    return text;
  }

  public static Text createMultiTextField(Composite parent, int width, int height, boolean verticalFill)
  {
    Text text = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);

    GridData data = new GridData();
    data.horizontalAlignment = GridData.FILL;
    data.grabExcessHorizontalSpace = true;
    if (verticalFill)
    {
      data.verticalAlignment = GridData.FILL;
      data.grabExcessVerticalSpace = true;
    }      
    data.widthHint = width;
    data.heightHint = height;
    text.setLayoutData(data);

    return text;
  }
}
