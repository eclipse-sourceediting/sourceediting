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
// Based on version 1.12 of original xsdeditor
package org.eclipse.wst.xsd.ui.internal.wizards;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorCSHelpIds;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.util.ViewUtility;
import org.eclipse.xsd.XSDPatternFacet;



/*
-other regex features (eg case sensitivity, ^ or $, |, etc etc)
-smarter model
-better keyboard navigation
-update list of tokens 
*/

public class RegexCompositionPage extends WizardPage
{
  private static final boolean debug = false;

  /* The text representation of our pattern. */
  private StyledText value; 

  /* The StyleRange used to color code the current parse error. */
  private StyleRange currentError;

  /* The regex terms we can form tokens from. */
  private Combo terms;

  /* The checkbox for activating auto-escape mode. */  
  private Button escapeCheckbox;
  
  /* On/off status of auto-escape mode. */ 
  private boolean autoEscapeStatus;

  /* The Add Token button. */
  private Button add;


  // The following controls are used in the occurrence selection group

  private Text repeatValue;

  private Text rangeMinValue;
  private Text rangeMaxValue;
  private Label rangeToLabel;  

  private Button singleRadio;
  private Button starRadio;
  private Button plusRadio;
  private Button optionalRadio; 
  private Button repeatRadio;
  private Button rangeRadio;

  
  // The following variables used as part of the model. 

  /* Our pattern. */
  private XSDPatternFacet pattern;

  /* Model used to store the current token. */
  private RegexNode node;    

  /* The flags passed to the new RegularExpression object.  Default value includes:
      X = XMLSchema mode    */
  private String regexFlags = "X";
      

  /* Is the current regex token valid? */
  private boolean isValidToken;

  /* The label used to indicate the value's caret position when it looses focus. */
  private Label caretLabel;

  /* The pixel offsets needed to align the label icon with the caret location.
     These are dependent on the icon used. */
  private static final int CARET_LABEL_X_OFFSET = -3;
  private static final int CARET_LABEL_Y_OFFSET = 19;

  
  /* Enumerated constants for specifying the type of an error message. */
  private static final int TOKEN = 0;
  private static final int SELECTION = 1;
  private static final int PARSE = 2;
  
  private static final int NUM_ERROR_MESSAGE_TYPES = 3;
  
  /* The current error message for each type of error.  A value of null indicates no message. 
     The array is indexed according to the above constants.
  */
  private String[] currentErrorMessages;


  public RegexCompositionPage(XSDPatternFacet pattern)
  {
    super(XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_COMPOSITION_PAGE_TITLE"));
    this.pattern = pattern;

    setTitle(XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_COMPOSITION_PAGE_TITLE"));
    setDescription(XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_COMPOSITION_PAGE_DESCRIPTION"));
  }

  public void createControl(Composite parent)
  {
    // Set up our model and validator
    node = new RegexNode();
        
    isValidToken = true;

    currentErrorMessages = new String[NUM_ERROR_MESSAGE_TYPES];

    // The main composite
    Composite composite= new Composite(parent, SWT.NONE);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, XSDEditorCSHelpIds.REGEX_WIZARD_PAGE);
    composite.setLayout(new GridLayout());


    // The composite for the token combo box, label, and auto-escape checkbox
    Composite tokenComposite = new Composite (composite, SWT.NONE);
    GridLayout tokenCompositeLayout = new GridLayout();
    tokenCompositeLayout.numColumns = 3;
    tokenCompositeLayout.marginWidth = 0;
    tokenComposite.setLayout(tokenCompositeLayout);


    new Label(tokenComposite, SWT.LEFT).setText(XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_TOKEN_LABEL"));
    
    terms = new Combo(tokenComposite, SWT.DROP_DOWN);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(terms, XSDEditorCSHelpIds.REGEX_TOKEN_CONTENTS);
    for (int i = 0; i < RegexNode.getNumRegexTerms(); i++)
    {
      terms.add(RegexNode.getRegexTermText(i));
    }
    terms.addListener(SWT.Modify, new ComboListener());
    terms.setToolTipText(XSDEditorPlugin.getXSDString("_UI_TOOLTIP_REGEX_WIZARD_TERMS"));

    escapeCheckbox = new Button(tokenComposite, SWT.CHECK);
    escapeCheckbox.setText(XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_AUTO_ESCAPE_CHECKBOX_LABEL"));
    escapeCheckbox.setToolTipText(XSDEditorPlugin.getXSDString("_UI_TOOLTIP_REGEX_WIZARD_AUTO_ESCAPE_CHECKBOX")); 
    escapeCheckbox.addSelectionListener(new CheckboxListener());
    autoEscapeStatus = false;


    // Set up the composites pertaining to the selection of occurrence quantifiers

    Group occurrenceSelectionArea = new Group(composite, SWT.NONE);
    occurrenceSelectionArea.setText(XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_OCCURENCE_LABEL"));
    PlatformUI.getWorkbench().getHelpSystem().setHelp(occurrenceSelectionArea, XSDEditorCSHelpIds.REGEX_WIZARD_PAGE);
    GridLayout selectionAreaLayout = new GridLayout();
    selectionAreaLayout.numColumns = 2;
    occurrenceSelectionArea.setLayout(selectionAreaLayout);

    occurrenceSelectionArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    // Listener used for all of the text fields
    TextListener textListener = new TextListener();
    

    // Add the radio buttons
    RadioSelectListener radioSelectListener = new RadioSelectListener();

    singleRadio = addOccurenceRadioButton(RegexNode.SINGLE, occurrenceSelectionArea, radioSelectListener);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(singleRadio, XSDEditorCSHelpIds.REGEX_JUST_ONCE);
    ViewUtility.createHorizontalFiller(occurrenceSelectionArea, 1);

    starRadio = addOccurenceRadioButton(RegexNode.STAR, occurrenceSelectionArea, radioSelectListener);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(starRadio, XSDEditorCSHelpIds.REGEX_ZERO_OR_MORE);
    ViewUtility.createHorizontalFiller(occurrenceSelectionArea, 1);

    plusRadio = addOccurenceRadioButton(RegexNode.PLUS, occurrenceSelectionArea, radioSelectListener);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(plusRadio, XSDEditorCSHelpIds.REGEX_ONE_OR_MORE);
    ViewUtility.createHorizontalFiller(occurrenceSelectionArea, 1);

    optionalRadio = addOccurenceRadioButton(RegexNode.OPTIONAL, occurrenceSelectionArea, radioSelectListener);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(optionalRadio, XSDEditorCSHelpIds.REGEX_OPTIONAL);
    ViewUtility.createHorizontalFiller(occurrenceSelectionArea, 1);

    repeatRadio = addOccurenceRadioButton(RegexNode.REPEAT, occurrenceSelectionArea, radioSelectListener);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(repeatRadio, XSDEditorCSHelpIds.REGEX_REPEAT_RADIO);
    
    repeatValue = new Text(occurrenceSelectionArea, SWT.SINGLE | SWT.BORDER);
    repeatValue.addListener(SWT.Modify, textListener);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(repeatValue, XSDEditorCSHelpIds.REGEX_REPEAT_FIELD);
    repeatValue.setToolTipText(XSDEditorPlugin.getXSDString("_UI_TOOLTIP_REGEX_WIZARD_REPEAT"));
    repeatValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    setEnabledStatus(RegexNode.REPEAT, false);
    
    rangeRadio = addOccurenceRadioButton(RegexNode.RANGE, occurrenceSelectionArea, radioSelectListener);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(rangeRadio, XSDEditorCSHelpIds.REGEX_RANGE_RADIO);

    // Add text fields and labels for specifying the range    
    Composite rangeWidgets = new Composite(occurrenceSelectionArea, SWT.NONE);
    GridLayout gridLayout = new GridLayout(3, false);
    gridLayout.marginHeight = 0;
    gridLayout.marginWidth = 0;
    rangeWidgets.setLayout(gridLayout);
    rangeWidgets.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    
    rangeMinValue = new Text(rangeWidgets, SWT.SINGLE | SWT.BORDER);
    rangeMinValue.addListener(SWT.Modify, textListener);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(rangeMinValue, XSDEditorCSHelpIds.REGEX_RANGE_MINIMUM_FIELD);
    rangeMinValue.setToolTipText(XSDEditorPlugin.getXSDString("_UI_TOOLTIP_REGEX_WIZARD_MIN"));
    rangeMinValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    
    rangeToLabel = new Label(rangeWidgets, SWT.NONE);
    rangeToLabel.setText(XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_TO_LABEL"));
    
    rangeMaxValue = new Text(rangeWidgets, SWT.SINGLE | SWT.BORDER);
    rangeMaxValue.addListener(SWT.Modify, textListener);
    rangeMaxValue.setToolTipText(XSDEditorPlugin.getXSDString("_UI_TOOLTIP_REGEX_WIZARD_MAX"));
    PlatformUI.getWorkbench().getHelpSystem().setHelp(rangeMaxValue, XSDEditorCSHelpIds.REGEX_RANGE_MAXIMUM_FIELD);
    rangeMaxValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    setEnabledStatus(RegexNode.RANGE, false);
    
    singleRadio.setSelection(true);
    
    // The add button
    add = new Button(composite, SWT.PUSH);
    add.addSelectionListener(new ButtonSelectListener());
    add.setText(XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_ADD_BUTTON_LABEL"));
    PlatformUI.getWorkbench().getHelpSystem().setHelp(add, XSDEditorCSHelpIds.REGEX_ADD_BUTTON);
    add.setToolTipText(XSDEditorPlugin.getXSDString("_UI_TOOLTIP_REGEX_WIZARD_ADD_BUTTON"));

    
    Label separator = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
    separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    
    // Our main text box

    Label valueLabel= new Label(composite, SWT.LEFT);
    valueLabel.setText(XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_CURRENT_REGEX_LABEL"));
    
    value = new StyledText(composite, SWT.SINGLE | SWT.BORDER);
    value.addListener(SWT.Modify, textListener);
    value.addListener(SWT.Selection, textListener);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(value, XSDEditorCSHelpIds.REGEX_CURRENT_VALUE);
    value.setToolTipText(XSDEditorPlugin.getXSDString("_UI_TOOLTIP_REGEX_WIZARD_CURRENT_REGEX"));
    value.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    value.setFocus();

    // StyleRange used for highlighting parse errors
    currentError = new StyleRange();
    currentError.length = 1;
    currentError.foreground = parent.getDisplay().getSystemColor(SWT.COLOR_RED);

    // The caret label
    caretLabel = new Label(composite, SWT.LEFT);
    caretLabel.setImage(XSDEditorPlugin.getXSDImage("icons/RegexWizardArrow.gif"));
    caretLabel.setToolTipText(XSDEditorPlugin.getXSDString("_UI_TOOLTIP_REGEX_WIZARD_CARET_LABEL"));
    setShowCaretLabel(true);

    value.addFocusListener(new TextFocusListener());

    terms.select(0);


    setControl(composite);
  }


  public void setVisible(boolean visible)
  {
    super.setVisible(visible);

    value.setText(pattern.getLexicalValue());
    value.setCaretOffset(value.getCharCount());
  }

  public void dispose()
  {
    super.dispose();
  }


  /**
   * Sets the visible status of caretLabel to status.  If status is true, then we also update the position
   * of caretLabel in one of two ways.  If there is no active selection in value, we set caretLabel's
   * position to correspond with the position of the actual caret.  Alternatively, if there is an active selection
   * in value, we set caretLabel's position to the beginning of the selection.
   *
   * @param The new visibility status of caretLabel.
   */
  private void setShowCaretLabel(boolean status)
  {
    if (status)
    {
  
      int offset;
      
      if (value.getSelectionText().equals(""))
      {
        offset = value.getCaretOffset();
      }
      else
      {
        offset = value.getSelection().x;
      }
  
      Point p = value.getLocationAtOffset(offset);
      
      p.x += value.getLocation().x;
      p.y = value.getLocation().y;
      
      // Place the label under value, and make sure it is aligned with the caret.
      // The offsets are dependent on the icon used.
      p.x += CARET_LABEL_X_OFFSET;
      p.y += CARET_LABEL_Y_OFFSET;
  
      if (debug)
      {
        System.out.println("POINT: " + p); //$NON-NLS-1$
      }
      
      caretLabel.setLocation(p);
      caretLabel.setVisible(true);
    }
    else
    {
      caretLabel.setVisible(false);
    }
  }


  /**
   * Adds a new radio button to Composite c with SelectionListener l.  The text of the button is the String associated with
   * quantifier.
   *
   * @param quantifier The desired quantifier, as enumerated in RegexNode.
   * @param c The Composite to add the buttons to (normally occurrenceRadioButtons).
   * @param l The SelectionListener (normally radioSelectionListener).
   * @return The newly created button.
   */
  private Button addOccurenceRadioButton(int quantifier, Composite c, SelectionListener l)
  {
    Button result = new Button(c, SWT.RADIO);
    result.setText(RegexNode.getQuantifierText(quantifier));
    result.addSelectionListener(l);
    return result;
  }


  /**
   * Validates the regex in value.  If the regex is valid, clears the Wizard's error message.  If it's not valid,
   *  sets the Wizard's error message accordingly.
   *
   * @return Whether the regex is valid.
   */
  private boolean validateRegex()
  {

    boolean isValid;
    try
    {
      // We validate the regex by checking whether we get a ParseException.
      // By default, we assume that it's valid unless we determine otherwise.
      isValid = true;
      displayRegexErrorMessage(null);
      value.setStyleRange(null);

      Pattern.compile(value.getText());
    }
    catch (PatternSyntaxException pe)
    {
      isValid = false;
      displayRegexErrorMessage(pe.getMessage());

      // An off-by-one bug in the xerces regex parser will sometimes return a location for the parseError that
      //  is off the end of the string.  If this is the case, then we want to highlight the last character.
      if (pe.getIndex() >= value.getText().length())
      {
        currentError.start = value.getText().length() - 1;
      }
      else
      {
        currentError.start = pe.getIndex();
      }

      if (debug)
      {
        System.out.println("Parse Error location: " + pe.getIndex());  //$NON-NLS-1$
        System.out.println("currentError.start: " + currentError.start); //$NON-NLS-1$
      }

      value.setStyleRange(currentError);

    }

    // Another bug in the xerces parser will sometimes throw a RuntimeException instead of a ParseException.
    //  When we get a RuntimeException, we aren't provided with the additional information we need to highlight
    //  the parse error.  So, we merely report that there is an error.
    catch (RuntimeException re)
    {
      displayRegexErrorMessage("");
      value.setStyleRange(null);
      isValid = false;
    }
    
    setPageComplete(isValid);    
    return isValid;
  }

  
  /**
   * Manages the display of error messages.
   * Sets the error message for type to errorMessage.  If errorMessage != null, then we set the Wizard's error message
   * to errorMessage.  If errorMessage == null, then we check whether we have a pending message of another type.
   * If we do, then it is displayed as the Wizard's error message.  If we don't, then the Wizard's error message field
   * is cleared.
   *
   * @param errorMessage The text of the new error message.  A value of null indicates that the error message should
   *  be cleared.
   * @param type The error type, one of PARSE, TOKEN, or SELECTION.
   */ 
  private void displayErrorMessage(String errorMessage, int type)
  {
    String messageToDisplay = null;


    currentErrorMessages[type] = errorMessage;

    messageToDisplay = errorMessage;

    for (int i = 0; i < NUM_ERROR_MESSAGE_TYPES; i++)
    {
      if (messageToDisplay != null)
      {
        break;
      }
      messageToDisplay = currentErrorMessages[i];
    }

    setErrorMessage(messageToDisplay);
  }


  /**
   * Sets the Wizard's error message to message, preceded by a standard prefix.
   *
   * @param message The new error message (or null to clear it).
   */
  private void displayRegexErrorMessage (String errorMessage)
  {
    if (errorMessage == null)
    {
      displayErrorMessage(null, PARSE);
    }
    else
    {
    	if (errorMessage.trim().equals("")) // when there is no error message available.
    	{
        displayErrorMessage(XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_INVALID_REGEX_ERROR"),
                           PARSE);
      }
      else
      {
        displayErrorMessage(errorMessage, PARSE);
      }
    }
  }


  /**
   * Updates the token status.  Sets isValidToken to status && the status of the other error type.
   * If status is true, we clear the wizard's error message for this type; if it is false, we set it to errorMessage.
   *
   * @param status The new isValidToken value.
   * @param errorMessage The new error message.
   * @param type The type of the error (either TOKEN or SELECTION).
   */
  private void setTokenStatus (boolean status, String errorMessage, int type)
  {
    boolean otherTypeStatus =     (type == TOKEN) ? 
                                  currentErrorMessages[SELECTION] == null :
                                  currentErrorMessages[TOKEN] == null;
    
    isValidToken = status && otherTypeStatus;
    add.setEnabled(isValidToken);

    if (status)
    {
      displayErrorMessage(null, type);
    }
    else
    {
    	if (errorMessage != null && errorMessage.trim().equals("")) // when there is no error message available.
    	{
        displayErrorMessage(XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_INVALID_TOKEN_ERROR"),
                           type);
      }
      else
      {
        displayErrorMessage(errorMessage, type);
      }
    }
  }

  
  /**
   * Updates the token status.  Sets isValidToken to status && the status of the other error type.
   * Also clears the wizard's error message for this type.
   * Usually used to set isValidToken to true.
   *
   * @param status The new isValidToken value.
   * @param type The type of the error (either TOKEN or SELECTION).
   */
  private void setTokenStatus(boolean status, int type)
  {
    setTokenStatus(status, null, type);
  }



  /**
   * Sets the enabled status of the text fields and labels associated with the specified quantifier.
   * If status is true, then fields and labels associated with other quantifiers are disabled.
   * @param quantifier The quantifier whose elements' enabled status we wish to change
   *   (as enumerated in RegexNode).
   * @param status The new status of the elements.  If true, then all elements associated with other buttons
   *               are disabled.
   */    
  private void setEnabledStatus(int quantifier, boolean status)
  {
    switch (quantifier)
    {
    
    case RegexNode.REPEAT:
      repeatValue.setEnabled(status);
      if (status)
      {
        rangeMinValue.setEnabled(false);
        rangeMaxValue.setEnabled(false);
        rangeToLabel.setEnabled(false);
      }
      break;

    case RegexNode.RANGE:
      rangeMinValue.setEnabled(status);
      rangeMaxValue.setEnabled(status);
      rangeToLabel.setEnabled(status);
      if (status)
      {
        repeatValue.setEnabled(false);
      }
      break;

    }
  }

  /**
   * Checks to see if there is a selection in value.  If there is not, we set the Wizard's error message accordingly.
   * If there is, we update the contents of node.  If "Current Selection" is not the current token, then
   * we clear the Selection error message.
   */
  private void updateCurrentSelectionStatus()
  {
    if (terms.getSelectionIndex() == RegexNode.SELECTION)
    {
      String selection = value.getSelectionText();
      if (selection.equals(""))
      {
        setTokenStatus(false, XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_INVALID_SELECTION_ERROR"), SELECTION);
      }
      else
      {
        setTokenStatus(true, SELECTION);
        node.setContents(selection);
        node.setHasParens(true);
      }
    }
    else
    {
      setTokenStatus(true, SELECTION);
    }
  }

  /**
   * Updates the enabled status of the auto-escape checkbox.  If status is true, we enable the checkbox, and
   * set its selection status and node's auto-escape status to the value of autoEscapeStatus.  If status is
   * false, then we disable and deselect the checkbox, and set node's status to false.
   *
   * @param status The new enabled status.
   */
  private void setEscapeCheckboxEnabledStatus(boolean status)
  {
    if (status)
    {
      escapeCheckbox.setEnabled(true);
      escapeCheckbox.setSelection(autoEscapeStatus);
      node.setAutoEscapeStatus(autoEscapeStatus);
    }
    else
    {
      escapeCheckbox.setEnabled(false);
      escapeCheckbox.setSelection(false);
      node.setAutoEscapeStatus(false);
    }
  }


  /**
   * Returns the current regex flags.
   */
  String getFlags()
  {
    return regexFlags;
  }

  /**
   * Returns the current XSDPattern model.
   */
  XSDPatternFacet getPattern()
  {
    return pattern;
  }


  /**
   * Returns a string consisting of the values of min, max, and repeat stored in node.
   * Used for debugging purposes only.
   */  
  private String getAllFieldValues()
  {
      String result = "";
      result += "Min: " + node.getMin() + "\n";
      result += "Max: " + node.getMax() + "\n";
      result += "Repeat: " + node.getRepeat() + "\n";
      result += "\n";
      return result;
  }
  

  /* Listener for the add button. */
  class ButtonSelectListener implements SelectionListener
  {
    public void widgetDefaultSelected(SelectionEvent e)
    {
    }

    // Precondition: isValidToken == true  
    public void widgetSelected(SelectionEvent e)
    {
      if (!isValidToken) // should never happen
      {
        return;
      }

      // Whether there is anything selected in value.
      boolean isActiveSelection = value.getSelectionCount() != 0;
      
      value.insert(node.toString());

      if (terms.getSelectionIndex() == RegexNode.SELECTION)
      {
        updateCurrentSelectionStatus();
      }

      // If nothing is selected, then we need to advance the caret location.
      if (!isActiveSelection)
      {
        value.setCaretOffset(value.getCaretOffset() + node.toString().length());
      }

      value.setFocus();

    }

  }
 

  /* Listener for the terms combo box. */
  class ComboListener implements Listener
  {
    public void handleEvent(Event e)
    {
      
      updateCurrentSelectionStatus();

      // If the user has typed in a token
      if (terms.getSelectionIndex() == -1)
      {
        setEscapeCheckboxEnabledStatus(true);
        node.setContents(terms.getText());
        node.setHasParens(true);
        

        if (debug)
        {
          System.out.println(terms.getText());
        }

      }
      else if (terms.getSelectionIndex() == RegexNode.SELECTION)
      {
        setEscapeCheckboxEnabledStatus(false);
      }
      else
      {
        node.setContents(RegexNode.getRegexTermValue(terms.getSelectionIndex()));
        node.setHasParens(false);
        setEscapeCheckboxEnabledStatus(false);
      }
    }
  }


  /* Listener for enabling/disabling caretLabel. */
  class TextFocusListener implements FocusListener
  {
    public void focusGained(FocusEvent e)
    {
      setShowCaretLabel(false);
    }
    public void focusLost(FocusEvent e)
    {
      setShowCaretLabel(true);
    }
  }



  /* Listener for the text fields. */
  class TextListener implements Listener
  {
    public void handleEvent (Event e)
    {

      if (debug)
      {
        System.out.println("Inside TextListener handler");  //$NON-NLS-1$
        System.out.println(e);
        System.out.println(getAllFieldValues());
      }


      if ( (e.widget == value) && (e.type == SWT.Modify) )
      {
        pattern.setLexicalValue(value.getText());
        validateRegex();
      }

      else if (e.widget == value && e.type == SWT.Selection)
      {
        if (terms.getSelectionIndex() == RegexNode.SELECTION)
        {
          updateCurrentSelectionStatus();
        }
      }

      else if (e.widget == rangeMinValue)
      {
        boolean isValid = node.setMin(rangeMinValue.getText());

        if (isValid)
        {
          setTokenStatus(true, null, TOKEN);
        }
        else
        {
          setTokenStatus(false,  XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_INVALID_MIN_ERROR_SUFFIX"), TOKEN);
        }
      }

      else if (e.widget == rangeMaxValue)
      {
        boolean isValid = node.setMax(rangeMaxValue.getText());

        if (node.getMin() == RegexNode.EMPTY)
        {
          setTokenStatus(false, XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_MISSING_MIN_ERROR_SUFFIX"), TOKEN);
        }
        else if (isValid)
        {
          setTokenStatus(true, null, TOKEN);
        }
        else
        {
          setTokenStatus(false, XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_INVALID_MAX_ERROR_SUFFIX"), TOKEN);
        }
      }

      else  // (e.widget == repeatValue)
      {
        boolean isValid = node.setRepeat(repeatValue.getText());
        if (isValid)
        {
          setTokenStatus(true, null, TOKEN);
        }
        else
        {
          setTokenStatus(false, XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_INVALID_REPEAT_ERROR_SUFFIX"), TOKEN);
        }
      }
    }
  }

  /* Listener for the auto-escape checkbox. */
  class CheckboxListener implements SelectionListener
  {
    public void widgetDefaultSelected(SelectionEvent e)
    {
    }

    public void widgetSelected(SelectionEvent e)
    {
      boolean newStatus = !autoEscapeStatus;
      node.setAutoEscapeStatus(newStatus);
      autoEscapeStatus = newStatus;
      
      if (debug)
      {
        System.out.println("AutoEscape Status: " + node.getAutoEscapeStatus());  //$NON-NLS-1$ 
      }
    }

  }


  /* Listener for the radio buttons. */
  class RadioSelectListener implements SelectionListener
  {
    public void widgetDefaultSelected(SelectionEvent e)
    {
    }

    public void widgetSelected(SelectionEvent e)
    {
      if (debug)
      {
        System.out.println(getAllFieldValues());
      }


      int currentQuantifier = getQuantifier(e);

      node.setQuantifier(currentQuantifier);

      switch (currentQuantifier)
      {
      case RegexNode.SINGLE:                     
      case RegexNode.STAR:
      case RegexNode.PLUS:
      case RegexNode.OPTIONAL:
        setEnabledStatus(RegexNode.REPEAT, false);
        setEnabledStatus(RegexNode.RANGE, false);
        setTokenStatus(true, TOKEN);
        break; 

      case RegexNode.REPEAT:
        setEnabledStatus(RegexNode.REPEAT, true);
        setTokenStatus(node.hasValidRepeat(), XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_INVALID_REPEAT_ERROR_SUFFIX"), TOKEN);
        repeatValue.setFocus();
        break;

      case RegexNode.RANGE:
        setEnabledStatus(RegexNode.RANGE, true);
        String error = (node.hasValidMin()) ? 
                            XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_INVALID_MAX_ERROR_SUFFIX") : 
                            XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_INVALID_MIN_ERROR_SUFFIX");

        setTokenStatus( node.hasValidMin() && node.hasValidMax(), error, TOKEN);
        rangeMinValue.setFocus();
        break;
      }
    }

    private int getQuantifier(SelectionEvent e)
    {

      if (e.widget == singleRadio)
      {
        return RegexNode.SINGLE;
      }
      
      else if (e.widget == starRadio)
      {
        return RegexNode.STAR;
      }
      
      else if (e.widget == plusRadio)
      {
        return RegexNode.PLUS;
      }
      
      else if (e.widget == optionalRadio)
      {
        return RegexNode.OPTIONAL;
      }
      
      else if (e.widget == repeatRadio)
      {
        return RegexNode.REPEAT;
      }
      
      else if (e.widget == rangeRadio)
      {
        return RegexNode.RANGE;
      }
      
      else // can't get here
      { 
        return RegexNode.EMPTY;
      }
    } 
  }
  
  public String getValue()
  {
    return value.getText();
  }
}
