/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.properties.sections;

import org.apache.xerces.util.XMLChar;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.ui.internal.search.dialogs.ComponentSpecification;
import org.eclipse.wst.xsd.ui.internal.adt.edit.ComponentReferenceEditManager;
import org.eclipse.wst.xsd.ui.internal.adt.edit.IComponentDialog;
import org.eclipse.wst.xsd.ui.internal.common.commands.BaseCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateAttributeValueCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateNameCommand;
import org.eclipse.wst.xsd.ui.internal.dialogs.NewTypeDialog;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.XSDAttributeReferenceEditManager;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorCSHelpIds;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.editor.XSDTypeReferenceEditManager;
import org.eclipse.wst.xsd.ui.internal.editor.search.XSDSearchListDialogDelegate;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class XSDAttributeDeclarationSection extends RefactoringSection
{
  protected Text nameText, defaultOrFixedText;
  protected CCombo componentNameCombo, typeCombo, usageCombo, formCombo;
  protected Button defaultButton, fixedButton;
  protected String typeName = "", refName = ""; //$NON-NLS-1$
  boolean isAttributeReference;
  
  public XSDAttributeDeclarationSection()
  {
    super();
  }

  protected void createContents(Composite parent)
  {
    composite = getWidgetFactory().createFlatFormComposite(parent);

    String typeLabel = Messages.UI_LABEL_TYPE; //$NON-NLS-1$

    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;
    composite.setLayout(gridLayout);

    // ------------------------------------------------------------------
    // NameLabel
    // ------------------------------------------------------------------
    GridData data = new GridData();
    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;
    CLabel nameLabel = getWidgetFactory().createCLabel(composite, org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_LABEL_NAME);
    nameLabel.setLayoutData(data);

    // ------------------------------------------------------------------
    // NameText
    // ------------------------------------------------------------------
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;
    nameText = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
    nameText.setLayoutData(data);
    nameText.setEnabled(!isAttributeReference);
    applyAllListeners(nameText);
    
    PlatformUI.getWorkbench().getHelpSystem().setHelp(nameText,
    		XSDEditorCSHelpIds.GENERAL_TAB__ATTRIBUTE__NAME);

    // ------------------------------------------------------------------
    // Refactor/rename hyperlink
    // ------------------------------------------------------------------
    if (!hideHyperLink) 
    {
    	createRenameHyperlink(composite);
		  setRenameHyperlinkEnabled(!isAttributeReference);
    }
    else
    {
   	  getWidgetFactory().createCLabel(composite, "");
    }
    

    // ------------------------------------------------------------------
    // Ref Label
    // ------------------------------------------------------------------
    if (isAttributeReference)
    {
      data = new GridData();
      data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
      data.grabExcessHorizontalSpace = false;
      CLabel refLabel = getWidgetFactory().createCLabel(composite, org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_LABEL_REFERENCE);
      refLabel.setLayoutData(data);

      // ------------------------------------------------------------------
      // Ref Combo
      // ------------------------------------------------------------------

      data = new GridData();
      data.grabExcessHorizontalSpace = true;
      data.horizontalAlignment = GridData.FILL;

      componentNameCombo = getWidgetFactory().createCCombo(composite, SWT.FLAT);
      componentNameCombo.addSelectionListener(this);
      componentNameCombo.addListener(SWT.Traverse, this);
      componentNameCombo.setLayoutData(data);
      
      PlatformUI.getWorkbench().getHelpSystem().setHelp(componentNameCombo,
          XSDEditorCSHelpIds.GENERAL_TAB__ATTRIBUTE__NAME);

      // dummy
      getWidgetFactory().createCLabel(composite, ""); //$NON-NLS-1$
    }
    
    // ------------------------------------------------------------------
    // typeLabel
    // ------------------------------------------------------------------
    getWidgetFactory().createCLabel(composite, typeLabel); //$NON-NLS-1$

    // ------------------------------------------------------------------
    // typeCombo
    // ------------------------------------------------------------------
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;
    typeCombo = getWidgetFactory().createCCombo(composite);
    typeCombo.setLayoutData(data);
    typeCombo.addSelectionListener(this);
    typeCombo.addListener(SWT.Traverse, this);
    
    PlatformUI.getWorkbench().getHelpSystem().setHelp(typeCombo,
    		XSDEditorCSHelpIds.GENERAL_TAB__ATTRIBUTE__TYPE);

    // dummy
    getWidgetFactory().createCLabel(composite, ""); //$NON-NLS-1$
    
    // ------------------------------------------------------------------
    // UsageLabel
    // ------------------------------------------------------------------
    data = new GridData();
    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;
    CLabel useLabel = getWidgetFactory().createCLabel(composite, XSDEditorPlugin.getXSDString("_UI_USAGE"));
    useLabel.setLayoutData(data);

    // ------------------------------------------------------------------
    // UsageCombo
    // ------------------------------------------------------------------
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;
    usageCombo = getWidgetFactory().createCCombo(composite);
    usageCombo.setLayoutData(data);
    usageCombo.addSelectionListener(this);
    usageCombo.add("");
    usageCombo.add("required"); //$NON-NLS-1$
    usageCombo.add("optional"); //$NON-NLS-1$
    usageCombo.add("prohibited"); //$NON-NLS-1$
    usageCombo.addSelectionListener(this);
    
    // dummy
    getWidgetFactory().createCLabel(composite, ""); //$NON-NLS-1$
    
    // ------------------------------------------------------------------
    // defaultLabel
    // ------------------------------------------------------------------
    data = new GridData();
    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;
    CLabel defaultLabel = getWidgetFactory().createCLabel(composite, org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_VALUE_COLON);
    defaultLabel.setLayoutData(data);
    
    Composite radio = getWidgetFactory().createComposite(composite);
    radio.setLayout(new RowLayout());
    
    defaultButton = new Button(radio, SWT.RADIO);
    defaultButton.setText(org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_DEFAULT);
    defaultButton.setBackground(parent.getBackground());
    defaultButton.addSelectionListener(this);
    
    fixedButton = new Button(radio, SWT.RADIO);
    fixedButton.setText(org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_FIXED);  
    fixedButton.setBackground(parent.getBackground());
    fixedButton.addSelectionListener(this);
    
    // dummy
    getWidgetFactory().createCLabel(composite, ""); //$NON-NLS-1$
    
    // dummy
    getWidgetFactory().createCLabel(composite, ""); //$NON-NLS-1$

    // ------------------------------------------------------------------
    // defaultText
    // ------------------------------------------------------------------
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;
    defaultOrFixedText = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
    defaultOrFixedText.setLayoutData(data);
    applyAllListeners(defaultOrFixedText);
    
    //PlatformUI.getWorkbench().getHelpSystem().setHelp(nameText,
    //		XSDEditorCSHelpIds.GENERAL_TAB__ATTRIBUTE__DEFAULT);
    
    // dummy
    getWidgetFactory().createCLabel(composite, ""); //$NON-NLS-1$
    
    // ------------------------------------------------------------------
    // FormLabel
    // ------------------------------------------------------------------
    data = new GridData();
    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;
    CLabel formLabel = getWidgetFactory().createCLabel(composite, org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_FORM);
    formLabel.setLayoutData(data);

    // ------------------------------------------------------------------
    // FormCombo
    // ------------------------------------------------------------------
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;
    formCombo = getWidgetFactory().createCCombo(composite);
    formCombo.setLayoutData(data);
    formCombo.addSelectionListener(this);
    formCombo.add("");
    formCombo.add("qualified"); //$NON-NLS-1$
    formCombo.add("unqualified"); //$NON-NLS-1$
    formCombo.addSelectionListener(this);
    
    // dummy
    getWidgetFactory().createCLabel(composite, ""); //$NON-NLS-1$
  }

  private void fillTypesCombo()
  {
    IEditorPart editor = getActiveEditor();
    XSDTypeReferenceEditManager manager = (XSDTypeReferenceEditManager)editor.getAdapter(XSDTypeReferenceEditManager.class);
    if (manager != null)
    {
      ComponentSpecification[] items = manager.getQuickPicks();

      typeCombo.removeAll();
      typeCombo.add(Messages._UI_COMBO_BROWSE);
      typeCombo.add(Messages._UI_COMBO_NEW);
      for (int i = 0; i < items.length; i++)
      {
        typeCombo.add(items[i].getName());
      }

      XSDAttributeDeclaration namedComponent = ((XSDAttributeDeclaration) input).getResolvedAttributeDeclaration();
      XSDTypeDefinition namedComponentType = namedComponent.getType();
      if (namedComponentType != null)
      {
        String currentTypeName = namedComponentType.getQName(xsdSchema); // no
                                                                          // prefix
        ComponentSpecification ret = getComponentSpecFromQuickPickForValue(currentTypeName, manager);
        if (ret == null) // not in quickPick
          typeCombo.add(currentTypeName);
      }
    }
  }
  
  private ComponentSpecification getComponentSpecFromQuickPickForValue(String value, ComponentReferenceEditManager editManager)
  {
    if (editManager != null)
    {  
      ComponentSpecification[] quickPicks = editManager.getQuickPicks();
      if (quickPicks != null)
      {
        for (int i=0; i < quickPicks.length; i++)
        {
          ComponentSpecification componentSpecification = quickPicks[i];
          if (value !=null && componentSpecification!=null && value.equals(componentSpecification.getName()))
          {
            return componentSpecification;
          }                
        }  
      }
    }
    return null;
  }

  /*
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
   */
  public void refresh()
  {
    super.refresh();

    setListenerEnabled(false);

    // refresh name

    nameText.setText(""); //$NON-NLS-1$
    if (input instanceof XSDAttributeDeclaration)
    {
      XSDAttributeDeclaration namedComponent = ((XSDAttributeDeclaration) input).getResolvedAttributeDeclaration();

      String name = namedComponent.getName();
      if (name != null)
      {
        nameText.setText(name);
      }
    }
    
    if (isAttributeReference)
    {
      refreshRefCombo();
    }

    // refresh type

    typeCombo.setText(""); //$NON-NLS-1$
    if (input != null)
    {
      if (input instanceof XSDAttributeDeclaration)
      {
        XSDAttributeDeclaration xsdAttribute = (XSDAttributeDeclaration) input;
        isAttributeReference = ((XSDAttributeDeclaration)input).isAttributeDeclarationReference();
        XSDTypeDefinition typeDef = xsdAttribute.getResolvedAttributeDeclaration().getTypeDefinition();
        boolean isAnonymous = xsdAttribute.getAnonymousTypeDefinition() != null;

        if (isAnonymous)
        {
          typeCombo.setText("**anonymous**"); //$NON-NLS-1$
        }
        else
        {
          fillTypesCombo();
          if (typeDef != null)
          {
            typeName = typeDef.getQName(xsdSchema);
            if (typeName == null)
            {
              typeName = ""; //$NON-NLS-1$
            }
            typeCombo.setText(typeName);
          }
          else
          {
            typeCombo.setText(Messages.UI_NO_TYPE); //$NON-NLS-1$
          }
        }

        usageCombo.setText("");
        usageCombo.setEnabled(!xsdAttribute.isGlobal());
        
        Element element = xsdAttribute.getElement();
        boolean hasUseAttribute = false;
        if (element != null)
        {
          hasUseAttribute = element.hasAttribute(XSDConstants.USE_ATTRIBUTE);
          if (hasUseAttribute)
          {
            String usage = element.getAttribute(XSDConstants.USE_ATTRIBUTE);
            usageCombo.setText(usage);
          }
        }
        
        defaultOrFixedText.setText(""); //$NON-NLS-1$
       
        boolean hasDefaultAttribute = false, hasFixedAttribute = false;
        if (element != null)
        {
          hasDefaultAttribute = element.hasAttribute(XSDConstants.DEFAULT_ATTRIBUTE);
          hasFixedAttribute = element.hasAttribute(XSDConstants.FIXED_ATTRIBUTE);

          // Case where no fixed or default attributes exist, so ensure one of the radio buttons is selected
          if (!hasDefaultAttribute && !hasFixedAttribute)
          {
            if (!defaultButton.getSelection() && !fixedButton.getSelection()) // if none are selected then pick fixed
              fixedButton.setSelection(true);
          }
          else
          {
            // if both are present in source (an error!), assume that *fixed* takes "precedence"
            defaultButton.setSelection(!hasFixedAttribute && hasDefaultAttribute);
            fixedButton.setSelection(hasFixedAttribute);
            if (hasDefaultAttribute)
            {
              String theDefault = element.getAttribute(XSDConstants.DEFAULT_ATTRIBUTE);
            	defaultOrFixedText.setText(theDefault);
            }
            if (hasFixedAttribute) // will overwrite default if both present
	          {
        	    String fixed = element.getAttribute(XSDConstants.FIXED_ATTRIBUTE);
        	    defaultOrFixedText.setText(fixed);
	          }
          }
        }
        
        formCombo.setText(""); 
        formCombo.setEnabled(!xsdAttribute.isGlobal() && !isAttributeReference);
        boolean hasFormAttribute = false;
        if (element != null)
        {
          hasFormAttribute = element.hasAttribute(XSDConstants.FORM_ATTRIBUTE);
          if (hasFormAttribute)
          {
            String form = element.getAttribute(XSDConstants.FORM_ATTRIBUTE);
            formCombo.setText(form);
          }
        }
      }
    }

    setListenerEnabled(true);
  }

  public boolean shouldUseExtraSpace()
  {
    return false;
  }
  
  public void doWidgetDefaultSelected(SelectionEvent e)
  {
    if (e.widget == typeCombo)
    {
      String selection = typeCombo.getText();
      if (shouldPerformComboSelection(SWT.DefaultSelection, selection))
        handleWidgetSelection(e);
    } else if (e.widget == componentNameCombo)
    {
      String selection = componentNameCombo.getText();
      if (shouldPerformComboSelection(SWT.DefaultSelection, selection))
        handleWidgetSelection(e);
    } else
    {
      handleWidgetSelection(e);
    }
  }

  public void doWidgetSelected(SelectionEvent e)
  {
    if (e.widget == typeCombo)
    {
      String selection = typeCombo.getText();
      if (shouldPerformComboSelection(SWT.Selection, selection))
        handleWidgetSelection(e);
    } else if (e.widget == componentNameCombo)
    {
      String selection = componentNameCombo.getText();
      if (shouldPerformComboSelection(SWT.Selection, selection))
        handleWidgetSelection(e);
    } else
    {
      handleWidgetSelection(e);
    }
  }
  
  private void handleWidgetSelection(SelectionEvent e)
  {
    if (e.widget == typeCombo)
    {
      IEditorPart editor = getActiveEditor();
      if (editor == null) return;
      ComponentReferenceEditManager manager = (ComponentReferenceEditManager)editor.getAdapter(XSDTypeReferenceEditManager.class);

      String selection = typeCombo.getText();
      ComponentSpecification newValue;
      IComponentDialog dialog= null;
      if ( selection.equals(Messages._UI_COMBO_BROWSE))
      {
        dialog = manager.getBrowseDialog();
        ((XSDSearchListDialogDelegate) dialog).showComplexTypes(false);
      }
      else if ( selection.equals(Messages._UI_COMBO_NEW))
      {
        dialog = manager.getNewDialog();
        ((NewTypeDialog) dialog).allowComplexType(false);
      }

      if (dialog != null)
      {
        if (dialog.createAndOpen() == Window.OK)
        {
          newValue = dialog.getSelectedComponent();
          XSDAttributeDeclaration xsdAttribute = ((XSDAttributeDeclaration) input).getResolvedAttributeDeclaration();
          manager.modifyComponentReference(xsdAttribute, newValue);
        }
        else{
        	typeCombo.setText(typeName);
        }
      }
      else //use the value from selected quickPick item
      {
        newValue = getComponentSpecFromQuickPickForValue(selection, manager);
        if (newValue != null)
          manager.modifyComponentReference(input, newValue);
      }
    }
    else if (e.widget == componentNameCombo)
    {
      IEditorPart editor = getActiveEditor();
      if (editor == null) return;
      ComponentReferenceEditManager manager = (ComponentReferenceEditManager)editor.getAdapter(XSDAttributeReferenceEditManager.class);

      String selection = componentNameCombo.getText();
      ComponentSpecification newValue;
      IComponentDialog dialog= null;
      if ( selection.equals(Messages._UI_COMBO_BROWSE))
      {
        dialog = manager.getBrowseDialog();
      }
      else if ( selection.equals(Messages._UI_COMBO_NEW))
      {
        dialog = manager.getNewDialog();
      }

      if (dialog != null)
      {
        if (dialog.createAndOpen() == Window.OK)
        {
          newValue = dialog.getSelectedComponent();
          manager.modifyComponentReference(input, newValue);
        }
        else
        {
          componentNameCombo.setText(refName);
        }
      }
      else //use the value from selected quickPick item
      {
        newValue = getComponentSpecFromQuickPickForValue(selection, manager);
        if (newValue != null)
          manager.modifyComponentReference(input, newValue);
      }
    }
    else 
    {
      XSDAttributeDeclaration xsdAttribute = (XSDAttributeDeclaration) input;
      Element element = xsdAttribute.getElement();
      if (e.widget == usageCombo)
      {	      
        final String newValue = usageCombo.getText();
        if (element != null)
        {
          PropertiesChangeCommand command = new PropertiesChangeCommand(element, org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_USAGE)
          {
            protected void doExecuteSteps()
            {
              if (newValue.length() == 0)
                element.removeAttribute(XSDConstants.USE_ATTRIBUTE);
              else
                element.setAttribute(XSDConstants.USE_ATTRIBUTE, newValue);
            }
          };
          getCommandStack().execute(command);
        }
      }
      else if (e.widget == formCombo)
      {
        final String newValue = formCombo.getText();
        if (element != null)
        {
          PropertiesChangeCommand command = new PropertiesChangeCommand(element, org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_LABEL_FORM)
          {
            protected void doExecuteSteps()
            {
              if (newValue.length() == 0)
                element.removeAttribute(XSDConstants.FORM_ATTRIBUTE);
              else
                element.setAttribute(XSDConstants.FORM_ATTRIBUTE, newValue);
            }
          };
          getCommandStack().execute(command);
        }
      }
      else if (e.widget == defaultButton)
      {
        boolean newValue = defaultButton.getSelection();
        if (element != null)
        {
          if (newValue)
          {
            if (element.hasAttribute(XSDConstants.FIXED_ATTRIBUTE))
            {
              final String value = element.getAttribute(XSDConstants.FIXED_ATTRIBUTE);

              PropertiesChangeCommand command = new PropertiesChangeCommand(element, org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_DEFAULT)
              {
                protected void doExecuteSteps()
                {
                  element.removeAttribute(XSDConstants.FIXED_ATTRIBUTE);
                  element.setAttribute(XSDConstants.DEFAULT_ATTRIBUTE, value);
                }
              };
              getCommandStack().execute(command);
            }
          }
        }
      }
      else if (e.widget == fixedButton)
      {
        boolean newValue = fixedButton.getSelection();
        if (element != null)
        {
          if (newValue)
          {
            if (element.hasAttribute(XSDConstants.DEFAULT_ATTRIBUTE))
            {
              final String value = element.getAttribute(XSDConstants.DEFAULT_ATTRIBUTE);
              PropertiesChangeCommand command = new PropertiesChangeCommand(element, org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_FIXED)
              {
                protected void doExecuteSteps()
                {
                  element.removeAttribute(XSDConstants.DEFAULT_ATTRIBUTE);
                  element.setAttribute(XSDConstants.FIXED_ATTRIBUTE, value);
                }
              };
              getCommandStack().execute(command);
            }
          }
        }
      }
    }
    super.doWidgetSelected(e);
  }

  protected void doHandleEvent(Event event)
  {
    if (event.type == SWT.Traverse) {
      if (event.detail == SWT.TRAVERSE_ARROW_NEXT || event.detail == SWT.TRAVERSE_ARROW_PREVIOUS) {
        isTraversing = true;
        return;
      }
    }
    super.doHandleEvent(event);
    if (event.widget == nameText)
    {
      if (!nameText.getEditable())
        return;

      String newValue = nameText.getText().trim();
      if (input instanceof XSDAttributeDeclaration)
      {
        XSDAttributeDeclaration namedComponent = ((XSDAttributeDeclaration) input).getResolvedAttributeDeclaration();

        if (!validateSection())
          return;

        Command command = null;

        // Make sure an actual name change has taken place
        String oldName = namedComponent.getName();
        if (!newValue.equals(oldName))
        {
          command = new UpdateNameCommand(org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_ACTION_RENAME, namedComponent, newValue);
        }

        if (command != null && getCommandStack() != null)
        {
          getCommandStack().execute(command);
        }
        
        if (isAttributeReference)
        {
          XSDAttributeDeclaration attrRef = (XSDAttributeDeclaration)input;
          String qname = attrRef.getResolvedAttributeDeclaration().getQName();
          attrRef.getElement().setAttribute(XSDConstants.REF_ATTRIBUTE, qname);
          
//          TypesHelper helper = new TypesHelper(xsdSchema);
//          List items = new ArrayList();
//          items = helper.getGlobalElements();
//          items.add(0, "");
//          componentNameCombo.setItems((String [])items.toArray(new String[0]));
//
//          refreshRefCombo();
        }

      }
    }
    else if (event.widget == defaultOrFixedText)
    {
      XSDAttributeDeclaration xsdAttribute = (XSDAttributeDeclaration) input;
      String newValue = defaultOrFixedText.getText();
      Element element = xsdAttribute.getElement();
      if (element != null)
      {
        if (newValue.length() == 0)
        {
          PropertiesChangeCommand command = new PropertiesChangeCommand(element, org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_LABEL_VALUE)
          {
            protected void doExecuteSteps()
            {
              element.removeAttribute(XSDConstants.FIXED_ATTRIBUTE);
              element.removeAttribute(XSDConstants.DEFAULT_ATTRIBUTE);
            }
          };
          getCommandStack().execute(command);
        }
        else
        {
          UpdateAttributeValueCommand command = null;
          if (fixedButton.getSelection())
          {
            command = new UpdateAttributeValueCommand(element, XSDConstants.FIXED_ATTRIBUTE, newValue, org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_FIXED)
            {
              protected void doPostProcessing()
              {
                element.removeAttribute(XSDConstants.DEFAULT_ATTRIBUTE);
              }
            };
          }
          else
          {
            command = new UpdateAttributeValueCommand(element, XSDConstants.DEFAULT_ATTRIBUTE, newValue, org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_DEFAULT)
            {
              protected void doPostProcessing()
              {
                element.removeAttribute(XSDConstants.FIXED_ATTRIBUTE);
              }
            };
          }
          command.setDeleteIfEmpty(true);
          getCommandStack().execute(command);
        }
      }
    }
  }
  
  protected boolean validateSection()
  {
    if (nameText == null || nameText.isDisposed())
      return true;

    setErrorMessage(null);

    String name = nameText.getText().trim();

    // validate against NCName
    if (name.length() < 1 || !XMLChar.isValidNCName(name))
    {
      setErrorMessage(org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_ERROR_INVALID_NAME);
      return false;
    }

    return true;
  }
  
  public void dispose()
  {
    if (componentNameCombo != null && !componentNameCombo.isDisposed())
    {
      componentNameCombo.removeSelectionListener(this);
      componentNameCombo.removeListener(SWT.Traverse, this);
    }
    if (nameText != null && !nameText.isDisposed())
      removeListeners(nameText);
    if (typeCombo != null && !typeCombo.isDisposed())
    {
      typeCombo.removeSelectionListener(this);
      typeCombo.removeListener(SWT.Traverse, this);
    }
    super.dispose();
  }

  public void setInput(IWorkbenchPart part, ISelection selection)
  {
    super.setInput(part, selection);
    setListenerEnabled(false);
    init();
    relayout();
    
    setListenerEnabled(true);
  }
  
  protected void init()
  {
    if (input instanceof XSDAttributeDeclaration)
    {
      XSDAttributeDeclaration xsdAttribute = (XSDAttributeDeclaration) input;
      isAttributeReference = xsdAttribute.isAttributeDeclarationReference();
      hideHyperLink = !xsdAttribute.isGlobal() || isAttributeReference;
    }
  }

  protected void relayout()
  {
    Composite parentComposite = composite.getParent();
    parentComposite.getParent().setRedraw(false);

    if (parentComposite != null && !parentComposite.isDisposed())
    {
      Control[] children = parentComposite.getChildren();
      for (int i = 0; i < children.length; i++)
      {
        children[i].dispose();
      }
    }

    // Now initialize the new handler
    createContents(parentComposite);
    parentComposite.getParent().layout(true, true);

    // Now turn painting back on
    parentComposite.getParent().setRedraw(true);
    refresh();
  }
  
  protected void refreshRefCombo()
  {
    componentNameCombo.setText(""); //$NON-NLS-1$
    fillComponentNameCombo();
  }
  
  private void fillComponentNameCombo()
  {
    IEditorPart editor = getActiveEditor();
    ComponentReferenceEditManager manager = (ComponentReferenceEditManager)editor.getAdapter(XSDAttributeReferenceEditManager.class);    
    
    componentNameCombo.removeAll();
    componentNameCombo.add(Messages._UI_ACTION_BROWSE);
    componentNameCombo.add(Messages._UI_ACTION_NEW);
    ComponentSpecification[] quickPicks = manager.getQuickPicks();
    if (quickPicks != null)
    {
      for (int i=0; i < quickPicks.length; i++)
      {
        ComponentSpecification componentSpecification = quickPicks[i];
        componentNameCombo.add(componentSpecification.getName());
      }  
    }
    ComponentSpecification[] history = manager.getHistory();
    if (history != null)
    {
      for (int i=0; i < history.length; i++)
      {
        ComponentSpecification componentSpecification = history[i];
        componentNameCombo.add(componentSpecification.getName());
      }  
    }
    
    XSDAttributeDeclaration namedComponent = (XSDAttributeDeclaration) input;
    Element element = namedComponent.getElement();
    if (element != null)
    {
      String attrValue = element.getAttribute(XSDConstants.REF_ATTRIBUTE);
      if (attrValue == null)
      {
        attrValue = ""; //$NON-NLS-1$
      }
      ComponentSpecification ret = getComponentSpecFromQuickPickForValue(attrValue, manager);
      if (ret == null)
      {
        componentNameCombo.add(attrValue);
      }
      componentNameCombo.setText(attrValue);
      refName = attrValue;
    } 
  }

  protected class PropertiesChangeCommand extends BaseCommand
  {
    protected Element element;
    public PropertiesChangeCommand(Element element, String label)
    {
      super(NLS.bind(org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_ACTION_CHANGE, label));
      this.element = element;
    }
    
    public void execute()
    {
      try
      {
        beginRecording(element);
        doExecuteSteps();
      }
      finally
      {
        endRecording();
      }
    }
    
    protected void doExecuteSteps()
    {
      
    }
  }
}
