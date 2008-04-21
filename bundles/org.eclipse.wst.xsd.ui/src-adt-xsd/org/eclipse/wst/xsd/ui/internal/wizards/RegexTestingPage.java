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
// Based on version 1.6 of original xsdeditor
package org.eclipse.wst.xsd.ui.internal.wizards;

import java.util.regex.Pattern;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.TextProcessor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorCSHelpIds;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.util.ViewUtility;


public class RegexTestingPage extends WizardPage
{
  /* Validator from xerces package. */
//  private RegularExpression validator;
  
  /* Displays the status of the match. */
  private Label matchLabel;


  /* The regex. */
  private Text value;
  
  /* The string the user is trying to match against the regex. */
  private StyledText testString;

  public RegexTestingPage()
  {
    super(XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_TESTING_PAGE_TITLE"));

    setTitle(XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_TESTING_PAGE_TITLE"));
    setDescription(XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_TESTING_PAGE_DESCRIPTION"));
  }


  public void createControl(Composite parent)
  {
    Composite composite = ViewUtility.createComposite(parent, 1);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, XSDEditorCSHelpIds.REGEX_TEST_PAGE);

    matchLabel = new Label(composite, SWT.WRAP);
    matchLabel.setText(XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_TESTING_PAGE_DESCRIPTION"));
    FontData[] fontData = matchLabel.getFont().getFontData();
    GridData dataF = new GridData();
    dataF.widthHint = 400;
    dataF.heightHint = 6 * fontData[0].getHeight();
    matchLabel.setLayoutData(dataF);
    
    Composite controls = new Composite(composite, SWT.NONE);
    GridLayout controlsLayout = new GridLayout();
    controlsLayout.numColumns = 2;
    controls.setLayout(controlsLayout);
    controls.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    new Label(controls, SWT.LEFT).setText(XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_REGEX_LABEL"));
    value = new Text(controls, SWT.BORDER | SWT.READ_ONLY);
    value.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));


    new Label(controls, SWT.LEFT).setText(XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_SAMPLE_TEXT"));
    testString = new StyledText(controls, SWT.SINGLE | SWT.BORDER);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(testString, XSDEditorCSHelpIds.REGEX_SAMPLE_TEXT);
    testString.addListener(SWT.Modify, new TestStringListener());
    testString.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    
    controls.pack();
    
    Label separator = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
    GC gc = new GC(separator);
    Point pointSize = gc.stringExtent(XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_TESTING_PAGE_DESCRIPTION"));
    GridData gd = new GridData();
    gd.widthHint = pointSize.x / 2 + gc.getAdvanceWidth('M')*11;
    gd.horizontalAlignment= GridData.FILL;
    separator.setLayoutData(gd);
    
    composite.pack();

    setControl(composite);
  }


  private String getPatternValue()
  {
    return ( (RegexCompositionPage)getPreviousPage() ).getPattern().getLexicalValue();
  }

  private String getFlags()
  {
    return ( (RegexCompositionPage)getPreviousPage() ).getFlags();
  }

  public void setVisible(boolean visible)
  {
    super.setVisible(visible);

    getFlags();
    value.setText(TextProcessor.process(getPatternValue()));
    updateMatchStatus();
    testString.setFocus();
  }

  class TestStringListener implements Listener
  {
    public void handleEvent(Event e)
    {
      updateMatchStatus();
    }
  }

  private void updateMatchStatus()
  {
    if (Pattern.matches(getPatternValue(), testString.getText()))
    {
//      matchLabel.setText(XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_MATCHES"));
      setMessage(XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_MATCHES"), 1);
    }
    else
    {
      setMessage(XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_DOES_NOT_MATCH"), 2);
//      matchLabel.setText(XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_DOES_NOT_MATCH"));
    }
  }
}
