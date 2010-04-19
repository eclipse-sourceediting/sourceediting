/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     David Carver - STAR - [205989] - [validation] validate XML after XInclude resolution
 *******************************************************************************/

package org.eclipse.wst.xml.ui.internal.preferences;


import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.dialogs.ControlEnableState;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.sse.ui.internal.preferences.ui.AbstractPreferencePage;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.preferences.XMLCorePreferenceNames;
import org.eclipse.wst.xml.ui.internal.XMLUIMessages;
import org.eclipse.wst.xml.ui.internal.editor.IHelpContextIds;


public class XMLValidatorPreferencePage extends AbstractPreferencePage {
  private Combo fIndicateNoGrammar;
  
  private Button fHonourAllSchemaLocations;

  private Button fUseXinclude;
  
  private Button fExtendedMarkupValidation;
  
  private Combo fEmptyElementTag;
  
  private Combo fEndTagWithAttributes;
  
  private Combo fInvalidWhitespaceBeforeTagname;
  
  private Combo fMissingClosingBracket;
  
  private Combo fMissingClosingQuote;
  
  private Combo fMissingEndTag;
  
  private Combo fMissingStartTag;
  
  private Combo fMissingQuotes;
  
  private Combo fInvalidNamespaceInPI;
  
  private Combo fMissingTagName;
  
  private Combo fInvalidWhitespaceAtStart;

  private Group fMarkupValidationGroup;
  private ControlEnableState fMarkupState;
 
  private static final String[] SEVERITIES = {XMLUIMessages.Indicate_no_grammar_specified_severities_error, XMLUIMessages.Indicate_no_grammar_specified_severities_warning, XMLUIMessages.Indicate_no_grammar_specified_severities_ignore};
  private static final String[] MARKUP_SEVERITIES = {XMLUIMessages.Severity_error, XMLUIMessages.Severity_warning, XMLUIMessages.Severity_ignore};

  protected Control createContents(Composite parent) {
    Composite composite = (Composite)super.createContents(parent);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IHelpContextIds.XML_PREFWEBX_VALIDATOR_HELPID);
    createContentsForValidatingGroup(composite);
    createContentsForMarkupValidationGroup(composite);
    setSize(composite);
    loadPreferences();

    return composite;
  }

  protected void createContentsForValidatingGroup(Composite parent) {
    Group validatingGroup = createGroup(parent, 2);
    ((GridLayout)validatingGroup.getLayout()).makeColumnsEqualWidth = false;
    validatingGroup.setText(XMLUIMessages.Validating_files);

    if (fIndicateNoGrammar == null) {
      createLabel(validatingGroup, XMLUIMessages.Indicate_no_grammar_specified);
      fIndicateNoGrammar = createCombo(validatingGroup, SEVERITIES);
    }
    if (fUseXinclude == null) {
      fUseXinclude = createCheckBox(validatingGroup, XMLUIMessages.Use_XInclude);
      ((GridData)fUseXinclude.getLayoutData()).horizontalSpan = 2;
    }
    if (fHonourAllSchemaLocations == null) {
      fHonourAllSchemaLocations = createCheckBox(validatingGroup, XMLUIMessages.Honour_all_schema_locations);
      ((GridData)fHonourAllSchemaLocations.getLayoutData()).horizontalSpan = 2;
    }
  }
  private void handleMarkupSeveritySelection(boolean selection){
	  if (selection) {
		  fMarkupState.restore();
	  }
	  else {
		  fMarkupState = ControlEnableState.disable(fMarkupValidationGroup);
	  }
  }

  protected void createContentsForMarkupValidationGroup(Composite parent) {
	   
	    if (fExtendedMarkupValidation == null) {
		    fExtendedMarkupValidation = createCheckBox(parent, XMLUIMessages.MarkupValidation_files);
		    ((GridData)fExtendedMarkupValidation.getLayoutData()).horizontalSpan = 2;
		    fExtendedMarkupValidation.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					handleMarkupSeveritySelection(fExtendedMarkupValidation.getSelection());
				}
			});
		}
	    fMarkupValidationGroup = createGroup(parent, 3);
	    ((GridLayout)fMarkupValidationGroup.getLayout()).makeColumnsEqualWidth = false;
	    fMarkupValidationGroup.setText(XMLUIMessages.MarkupValidation_files_label);

	    if (fMissingStartTag == null) {
	        fMissingStartTag = createMarkupCombo(fMarkupValidationGroup, XMLUIMessages.Missing_start_tag, MARKUP_SEVERITIES);
	    }
	    if (fMissingEndTag == null) {
	        fMissingEndTag = createMarkupCombo(fMarkupValidationGroup, XMLUIMessages.Missing_end_tag, MARKUP_SEVERITIES);
	    }
	    if (fMissingTagName == null) {
	        fMissingTagName = createMarkupCombo(fMarkupValidationGroup, XMLUIMessages.Tag_name_missing, MARKUP_SEVERITIES);
	    }
	    if (fMissingQuotes == null) {
	        fMissingQuotes = createMarkupCombo(fMarkupValidationGroup, XMLUIMessages.Missing_quotes, MARKUP_SEVERITIES);
	    }
	    if (fMissingClosingBracket == null) {
	        fMissingClosingBracket = createMarkupCombo(fMarkupValidationGroup, XMLUIMessages.Missing_closing_bracket, MARKUP_SEVERITIES);
	    }
	    if (fMissingClosingQuote == null) {
	        fMissingClosingQuote = createMarkupCombo(fMarkupValidationGroup, XMLUIMessages.Missing_closing_quote, MARKUP_SEVERITIES);
	    }
	    if (fEmptyElementTag == null) {
	        fEmptyElementTag = createMarkupCombo(fMarkupValidationGroup, XMLUIMessages.Empty_element_tag, MARKUP_SEVERITIES);
	    }
	    if (fEndTagWithAttributes == null) {
	        fEndTagWithAttributes = createMarkupCombo(fMarkupValidationGroup, XMLUIMessages.End_tag_with_attributes, MARKUP_SEVERITIES);
	    }
	    if (fInvalidWhitespaceBeforeTagname == null) {
	        fInvalidWhitespaceBeforeTagname = createMarkupCombo(fMarkupValidationGroup, XMLUIMessages.Invalid_whitespace_before_tagname, MARKUP_SEVERITIES);
	    }
	    if (fInvalidNamespaceInPI == null) {
	        fInvalidNamespaceInPI = createMarkupCombo(fMarkupValidationGroup, XMLUIMessages.Namespace_in_pi_target, MARKUP_SEVERITIES);
	    }
	    if (fInvalidWhitespaceAtStart == null) {
	        fInvalidWhitespaceAtStart = createMarkupCombo(fMarkupValidationGroup, XMLUIMessages.Whitespace_at_start, MARKUP_SEVERITIES);
	    }

  }

  /**
   * @param parent 
   * @return
   */
  private Combo createCombo(Composite parent, String[] items) {
    Combo combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
    combo.setItems(items);

    //GridData
    GridData data = new GridData(SWT.FILL, SWT.CENTER, true, true);
    combo.setLayoutData(data);

    return combo;
  }

  private Combo createMarkupCombo(Composite parent, String text, String[] items) {
	  Label label = new Label(parent, SWT.LEFT);
	  GridData gd = new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1);
		label.setFont(JFaceResources.getDialogFont());
		label.setText(text);
		label.setLayoutData(gd);

	  Combo combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
	    combo.setItems(items);

	    //GridData
	   // GridData data = new GridData(SWT.FILL, SWT.CENTER, false, true);
	    combo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

	    return combo;
  }
 
  protected void initializeValues() {
    initializeValuesForValidatingGroup();
    initializeValuesForMarkupValidationGroup();
  }

  protected void initializeValuesForValidatingGroup() {
    Preferences modelPreferences = getModelPreferences();
    int indicateNoGrammarButtonSelected = modelPreferences.getInt(XMLCorePreferenceNames.INDICATE_NO_GRAMMAR);
    boolean useXIncludeButtonSelected = modelPreferences.getBoolean(XMLCorePreferenceNames.USE_XINCLUDE);

    if (fIndicateNoGrammar != null) {
      fIndicateNoGrammar.select(2 - indicateNoGrammarButtonSelected);
      fIndicateNoGrammar.setText(SEVERITIES[2 - indicateNoGrammarButtonSelected]);
    }
    if (fUseXinclude != null) {
      fUseXinclude.setSelection(useXIncludeButtonSelected);
    }

    boolean honourAllSelected = modelPreferences.getBoolean(XMLCorePreferenceNames.HONOUR_ALL_SCHEMA_LOCATIONS);
    if (fHonourAllSchemaLocations != null) {
      fHonourAllSchemaLocations.setSelection(honourAllSelected);
    }
  }
  
  protected void initializeValuesForMarkupValidationGroup() {
	    Preferences modelPreferences = getModelPreferences();
	    boolean useExtendedMarkupValidation = modelPreferences.getBoolean(XMLCorePreferenceNames.MARKUP_VALIDATION);

	    
	    if (fExtendedMarkupValidation != null) {
	    	fExtendedMarkupValidation.setSelection(useExtendedMarkupValidation);
	    }
	    int emptyElementTag = modelPreferences.getInt(XMLCorePreferenceNames.ATTRIBUTE_HAS_NO_VALUE);
	    
	    if (fEmptyElementTag != null) {
	    	fEmptyElementTag.select(2 - emptyElementTag);
	    	fEmptyElementTag.setText(MARKUP_SEVERITIES[2 - emptyElementTag]);
		}
	    
	    int endTagWithAttributes  = modelPreferences.getInt(XMLCorePreferenceNames.END_TAG_WITH_ATTRIBUTES);
	    
	    if (fEndTagWithAttributes != null) {
	    	fEndTagWithAttributes.select(2 - endTagWithAttributes);
	    	fEndTagWithAttributes.setText(MARKUP_SEVERITIES[2 - endTagWithAttributes]);
		}
	    
	    int invalidWhitespaceBeforeTagname  = modelPreferences.getInt(XMLCorePreferenceNames.WHITESPACE_BEFORE_TAGNAME);
	    
	    if (fInvalidWhitespaceBeforeTagname != null) {
	    	fInvalidWhitespaceBeforeTagname.select(2 - invalidWhitespaceBeforeTagname);
	    	fInvalidWhitespaceBeforeTagname.setText(MARKUP_SEVERITIES[2 - invalidWhitespaceBeforeTagname]);
		}
	    
	    int missingClosingBracket  = modelPreferences.getInt(XMLCorePreferenceNames.MISSING_CLOSING_BRACKET);
	    
	    if (fMissingClosingBracket != null) {
	    	fMissingClosingBracket.select(2 - missingClosingBracket);
	    	fMissingClosingBracket.setText(MARKUP_SEVERITIES[2 - missingClosingBracket]);
		}
	    
	    int missingClosingQuote  = modelPreferences.getInt(XMLCorePreferenceNames.MISSING_CLOSING_QUOTE);
	    
	    if (fMissingClosingQuote != null) {
	    	fMissingClosingQuote.select(2 - missingClosingQuote);
	    	fMissingClosingQuote.setText(MARKUP_SEVERITIES[2 - missingClosingQuote]);
		}
	    
	    int missingEndTag  = modelPreferences.getInt(XMLCorePreferenceNames.MISSING_END_TAG);
	    
	    if (fMissingEndTag != null) {
	    	fMissingEndTag.select(2 - missingEndTag);
	    	fMissingEndTag.setText(MARKUP_SEVERITIES[2 - missingEndTag]);
		}
	    
	    int missingStartTag  = modelPreferences.getInt(XMLCorePreferenceNames.MISSING_START_TAG);
	    
	    if (fMissingStartTag != null) {
	    	fMissingStartTag.select(2 - missingStartTag);
	    	fMissingStartTag.setText(MARKUP_SEVERITIES[2 - missingStartTag]);
		}
	    
	    int missingQuotes  = modelPreferences.getInt(XMLCorePreferenceNames.MISSING_QUOTES);
	    
	    if (fMissingQuotes != null) {
	    	fMissingQuotes.select(2 - missingQuotes);
	    	fMissingQuotes.setText(MARKUP_SEVERITIES[2 - missingQuotes]);
		}
	    
	    int invalidNamespaceInPI  = modelPreferences.getInt(XMLCorePreferenceNames.NAMESPACE_IN_PI_TARGET);
	    
	    if (fInvalidNamespaceInPI != null) {
	    	fInvalidNamespaceInPI.select(2 - invalidNamespaceInPI);
	    	fInvalidNamespaceInPI.setText(MARKUP_SEVERITIES[2 - invalidNamespaceInPI]);
		}
	    
	    int tagNameMissing  = modelPreferences.getInt(XMLCorePreferenceNames.MISSING_TAG_NAME);
	    
	    if (fMissingTagName != null) {
	    	fMissingTagName.select(2 - tagNameMissing);
	    	fMissingTagName.setText(MARKUP_SEVERITIES[2 - tagNameMissing]);
		}
	    
	    int invalidWhitespaceAtStart  = modelPreferences.getInt(XMLCorePreferenceNames.WHITESPACE_AT_START);
	    
	    if (fInvalidWhitespaceAtStart != null) {
	    	fInvalidWhitespaceAtStart.select(2 - invalidWhitespaceAtStart);
	    	fInvalidWhitespaceAtStart.setText(MARKUP_SEVERITIES[2 - invalidWhitespaceAtStart]);
		}

	    if (!useExtendedMarkupValidation)
	    	fMarkupState = ControlEnableState.disable(fMarkupValidationGroup);
  }

  protected void performDefaultsForValidatingGroup() {
    Preferences modelPreferences = getModelPreferences();
    int indicateNoGrammarButtonSelected = modelPreferences.getDefaultInt(XMLCorePreferenceNames.INDICATE_NO_GRAMMAR);
    boolean useXIncludeButtonSelected = modelPreferences.getDefaultBoolean(XMLCorePreferenceNames.USE_XINCLUDE);

    if (fIndicateNoGrammar != null) {
      fIndicateNoGrammar.setSelection(new Point(indicateNoGrammarButtonSelected, 2 - indicateNoGrammarButtonSelected));
      fIndicateNoGrammar.setText(SEVERITIES[indicateNoGrammarButtonSelected]);
    }
    if (fUseXinclude != null) {
      fUseXinclude.setSelection(useXIncludeButtonSelected);
    }

    boolean honourAllButtonSelected = modelPreferences.getDefaultBoolean(XMLCorePreferenceNames.HONOUR_ALL_SCHEMA_LOCATIONS);
    if (fHonourAllSchemaLocations != null) {
      fHonourAllSchemaLocations.setSelection(honourAllButtonSelected);
    }
  }
  
  protected void performDefaultsForMarkupValidationGroup() {
	  Preferences modelPreferences = getModelPreferences();
	    boolean useExtendedMarkupValidation = modelPreferences.getDefaultBoolean(XMLCorePreferenceNames.MARKUP_VALIDATION);

	    
	    if (fExtendedMarkupValidation != null) {
	    	if (fExtendedMarkupValidation.getSelection() != useExtendedMarkupValidation) {
	    		handleMarkupSeveritySelection(useExtendedMarkupValidation);
	    	}
	    	fExtendedMarkupValidation.setSelection(useExtendedMarkupValidation);
	    	
	    }
	    int emptyElementTag = modelPreferences.getDefaultInt(XMLCorePreferenceNames.ATTRIBUTE_HAS_NO_VALUE);
	    
	    if (fEmptyElementTag != null) {
	    	fEmptyElementTag.setSelection(new Point(emptyElementTag,2 - emptyElementTag));
	    	fEmptyElementTag.setText(MARKUP_SEVERITIES[2 - emptyElementTag]);
		}
	    
	    int endTagWithAttributes  = modelPreferences.getDefaultInt(XMLCorePreferenceNames.END_TAG_WITH_ATTRIBUTES);
	    
	    if (fEndTagWithAttributes != null) {
	    	fEndTagWithAttributes.setSelection(new Point(endTagWithAttributes,2 - endTagWithAttributes));
	    	fEndTagWithAttributes.setText(MARKUP_SEVERITIES[2 - endTagWithAttributes]);
		}
	    
	    int invalidWhitespaceBeforeTagname  = modelPreferences.getDefaultInt(XMLCorePreferenceNames.WHITESPACE_BEFORE_TAGNAME);
	    
	    if (fInvalidWhitespaceBeforeTagname != null) {
	    	fInvalidWhitespaceBeforeTagname.setSelection(new Point(invalidWhitespaceBeforeTagname,2 - invalidWhitespaceBeforeTagname));
	    	fInvalidWhitespaceBeforeTagname.setText(MARKUP_SEVERITIES[2 - invalidWhitespaceBeforeTagname]);
		}
	    
	    int missingClosingBracket  = modelPreferences.getDefaultInt(XMLCorePreferenceNames.MISSING_CLOSING_BRACKET);
	    
	    if (fMissingClosingBracket != null) {
	    	fMissingClosingBracket.setSelection(new Point(missingClosingBracket,2 - missingClosingBracket));
	    	fMissingClosingBracket.setText(MARKUP_SEVERITIES[2 - missingClosingBracket]);
		}
	    
	    int missingClosingQuote  = modelPreferences.getDefaultInt(XMLCorePreferenceNames.MISSING_CLOSING_QUOTE);
	    
	    if (fMissingClosingQuote != null) {
	    	fMissingClosingQuote.setSelection(new Point(missingClosingQuote,2 - missingClosingQuote));
	    	fMissingClosingQuote.setText(MARKUP_SEVERITIES[2 - missingClosingQuote]);
		}
	    
	    int missingEndTag  = modelPreferences.getDefaultInt(XMLCorePreferenceNames.MISSING_END_TAG);
	    
	    if (fMissingEndTag != null) {
	    	fMissingEndTag.setSelection(new Point(missingEndTag,2 - missingEndTag));
	    	fMissingEndTag.setText(MARKUP_SEVERITIES[2 - missingEndTag]);
		}
	    
	    int missingStartTag  = modelPreferences.getDefaultInt(XMLCorePreferenceNames.MISSING_START_TAG);
	    
	    if (fMissingStartTag != null) {
	    	fMissingStartTag.setSelection(new Point(missingStartTag,2 - missingStartTag));
	    	fMissingStartTag.setText(MARKUP_SEVERITIES[2 - missingStartTag]);
		}
	    
	    int missingQuotes  = modelPreferences.getDefaultInt(XMLCorePreferenceNames.MISSING_QUOTES);
	    
	    if (fMissingQuotes != null) {
	    	fMissingQuotes.setSelection(new Point(missingQuotes,2 - missingQuotes));
	    	fMissingQuotes.setText(MARKUP_SEVERITIES[2 - missingQuotes]);
		}
	    
	    int invalidNamespaceInPI  = modelPreferences.getDefaultInt(XMLCorePreferenceNames.NAMESPACE_IN_PI_TARGET);
	    
	    if (fInvalidNamespaceInPI != null) {
	    	fInvalidNamespaceInPI.setSelection(new Point(invalidNamespaceInPI,2 - invalidNamespaceInPI));
	    	fInvalidNamespaceInPI.setText(MARKUP_SEVERITIES[2 - invalidNamespaceInPI]);
		}
	    
	    int tagNameMissing  = modelPreferences.getDefaultInt(XMLCorePreferenceNames.MISSING_TAG_NAME);
	    
	    if (fMissingTagName != null) {
	    	fMissingTagName.setSelection(new Point(tagNameMissing,2 - tagNameMissing));
	    	fMissingTagName.setText(MARKUP_SEVERITIES[2 - tagNameMissing]);
		}
	    
	    int invalidWhitespaceAtStart  = modelPreferences.getDefaultInt(XMLCorePreferenceNames.WHITESPACE_AT_START);
	    
	    if (fInvalidWhitespaceAtStart != null) {
	    	fInvalidWhitespaceAtStart.setSelection(new Point(invalidWhitespaceAtStart,2 - invalidWhitespaceAtStart));
	    	fInvalidWhitespaceAtStart.setText(MARKUP_SEVERITIES[2 - invalidWhitespaceAtStart]);
		}
	  }

  protected void storeValuesForValidatingGroup()
  {
    Preferences modelPreferences = getModelPreferences();
    if (fIndicateNoGrammar != null) {
      int warnNoGrammarButtonSelected = 2 - fIndicateNoGrammar.getSelectionIndex();
      modelPreferences.setValue(XMLCorePreferenceNames.INDICATE_NO_GRAMMAR, warnNoGrammarButtonSelected);
    }
    if (fUseXinclude != null) {
      boolean useXIncludeButtonSelected = fUseXinclude.getSelection();
      modelPreferences.setValue(XMLCorePreferenceNames.USE_XINCLUDE, useXIncludeButtonSelected);
    }
    if (fHonourAllSchemaLocations != null) {
      boolean honourAllButtonSelected = fHonourAllSchemaLocations.getSelection();
      modelPreferences.setValue(XMLCorePreferenceNames.HONOUR_ALL_SCHEMA_LOCATIONS, honourAllButtonSelected);
    }
  }
  protected void storeValuesForMarkupValidationGroup()
  {
    Preferences modelPreferences = getModelPreferences();
    if (fExtendedMarkupValidation != null) {
        boolean extendedMarkupValidation = fExtendedMarkupValidation.getSelection();
        modelPreferences.setValue(XMLCorePreferenceNames.MARKUP_VALIDATION, extendedMarkupValidation);
    }
    if (fEmptyElementTag != null) {
      int emptyElementTag = 2 - fEmptyElementTag.getSelectionIndex();
      modelPreferences.setValue(XMLCorePreferenceNames.ATTRIBUTE_HAS_NO_VALUE, emptyElementTag);
    }
    if (fEndTagWithAttributes != null) {
        int endTagWithAttributes = 2 - fEndTagWithAttributes.getSelectionIndex();
        modelPreferences.setValue(XMLCorePreferenceNames.END_TAG_WITH_ATTRIBUTES, endTagWithAttributes);
    }
    if (fInvalidWhitespaceBeforeTagname != null) {
        int invalidWhitespaceBeforeTagname = 2 - fInvalidWhitespaceBeforeTagname.getSelectionIndex();
        modelPreferences.setValue(XMLCorePreferenceNames.WHITESPACE_BEFORE_TAGNAME, invalidWhitespaceBeforeTagname);
    }
    if (fMissingClosingBracket != null) {
          int missingClosingBracket = 2 - fMissingClosingBracket.getSelectionIndex();
          modelPreferences.setValue(XMLCorePreferenceNames.MISSING_CLOSING_BRACKET, missingClosingBracket);
    }
    if (fMissingClosingQuote != null) {
        int missingClosingQuote = 2 - fMissingClosingQuote.getSelectionIndex();
        modelPreferences.setValue(XMLCorePreferenceNames.MISSING_CLOSING_BRACKET, missingClosingQuote);
    }
    if (fMissingEndTag != null) {
        int missingEndTag = 2 - fMissingEndTag.getSelectionIndex();
        modelPreferences.setValue(XMLCorePreferenceNames.MISSING_END_TAG, missingEndTag);
        modelPreferences.getInt(XMLCorePreferenceNames.MISSING_END_TAG);
    }
	if (fMissingStartTag != null) {
        int missingStartTag = 2 - fMissingStartTag.getSelectionIndex();
	    modelPreferences.setValue(XMLCorePreferenceNames.MISSING_START_TAG, missingStartTag);
	}
	if (fMissingQuotes != null) {
        int missingQuotes = 2 - fMissingQuotes.getSelectionIndex();
	    modelPreferences.setValue(XMLCorePreferenceNames.MISSING_QUOTES, missingQuotes);
	}
	if (fInvalidNamespaceInPI != null) {
        int invalidNamespaceInPI = 2 - fInvalidNamespaceInPI.getSelectionIndex();
	    modelPreferences.setValue(XMLCorePreferenceNames.NAMESPACE_IN_PI_TARGET, invalidNamespaceInPI);
	}
	if (fMissingTagName != null) {
        int missingTagName = 2 - fMissingTagName.getSelectionIndex();
	    modelPreferences.setValue(XMLCorePreferenceNames.MISSING_TAG_NAME, missingTagName);
	}
	if (fInvalidWhitespaceAtStart != null) {
        int invalidWhitespaceAtStart = 2 - fInvalidWhitespaceAtStart.getSelectionIndex();
	    modelPreferences.setValue(XMLCorePreferenceNames.WHITESPACE_AT_START, invalidWhitespaceAtStart);
	}
    
    
  }
  
  protected void storeValues() {
    storeValuesForValidatingGroup();
    storeValuesForMarkupValidationGroup();
  }

  protected void performDefaults() {
    performDefaultsForValidatingGroup();
    performDefaultsForMarkupValidationGroup();
    super.performDefaults();
  }
  
  protected Preferences getModelPreferences() {
    return XMLCorePlugin.getDefault().getPluginPreferences();
  }  
  
  protected void doSavePreferenceStore() {
      XMLCorePlugin.getDefault().savePluginPreferences(); // model
  }

  public boolean performOk() {
    boolean result = super.performOk();

    doSavePreferenceStore();

    return result;
  }
}
