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
package org.eclipse.wst.xsd.ui.internal.common.properties.sections;

import java.util.ArrayList;
import java.util.List;

import org.apache.xerces.util.XMLChar;
import org.eclipse.core.resources.IFile;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.common.ui.internal.search.dialogs.ComponentSearchListDialog;
import org.eclipse.wst.common.ui.internal.search.dialogs.ComponentSearchListDialogConfiguration;
import org.eclipse.wst.common.ui.internal.search.dialogs.ComponentSpecification;
import org.eclipse.wst.common.ui.internal.search.dialogs.ScopedComponentSearchListDialog;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateMaxOccursCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateMinOccursCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateNameCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateTypeReferenceCommand;
import org.eclipse.wst.xsd.ui.internal.dialogs.NewTypeButtonHandler;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.editor.search.XSDComponentDescriptionProvider;
import org.eclipse.wst.xsd.ui.internal.editor.search.XSDTypesSearchListProvider;
import org.eclipse.wst.xsd.ui.internal.util.TypesHelper;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class XSDElementDeclarationSection extends MultiplicitySection
{
  protected Text nameText;
  protected Text typeCombo;
  protected Button typesBrowseButton;
  protected CCombo componentNameCombo;
  boolean isElementReference;

  private XSDTypeDefinition typeDefinition;

  public XSDElementDeclarationSection()
  {
    super();
  }

  /**
   * Contents of the property tab
   */
  public void createContents(Composite parent)
  {
    composite = getWidgetFactory().createFlatFormComposite(parent);
    TabbedPropertySheetWidgetFactory factory = getWidgetFactory();

    String typeLabel = Messages.UI_LABEL_TYPE; //$NON-NLS-1$

    GridData data = new GridData();

    GridLayout gridLayout = new GridLayout();
    gridLayout.marginTop = 0;
    gridLayout.marginBottom = 0;
    gridLayout.numColumns = 3;
    composite.setLayout(gridLayout);
    
      // ------------------------------------------------------------------
      // NameLabel
      // ------------------------------------------------------------------

      data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
      data.grabExcessHorizontalSpace = false;
      CLabel nameLabel = factory.createCLabel(composite, "Name:");
      nameLabel.setLayoutData(data);

      // ------------------------------------------------------------------
      // NameText
      // ------------------------------------------------------------------
      data = new GridData();
      data.grabExcessHorizontalSpace = true;
      data.horizontalAlignment = GridData.FILL;
      nameText = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
      nameText.setLayoutData(data);
      applyAllListeners(nameText);   
     
      // ------------------------------------------------------------------
      // DummyLabel
      // ------------------------------------------------------------------
      data = new GridData();
      data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
      data.grabExcessHorizontalSpace = false;
      getWidgetFactory().createCLabel(composite, ""); //$NON-NLS-1$

    // ------------------------------------------------------------------
    // Ref Label
    // ------------------------------------------------------------------
    if (isElementReference)
    {
      data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
      data.grabExcessHorizontalSpace = false;
      CLabel refLabel = getWidgetFactory().createCLabel(composite, "Reference:");
      refLabel.setLayoutData(data);

      // ------------------------------------------------------------------
      // Ref Combo
      // ------------------------------------------------------------------

      data = new GridData();
      data.grabExcessHorizontalSpace = true;
      data.horizontalAlignment = GridData.FILL;

      componentNameCombo = getWidgetFactory().createCCombo(composite, SWT.FLAT);
      componentNameCombo.addSelectionListener(this);
      componentNameCombo.setLayoutData(data);

      data = new GridData();
      data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
      data.grabExcessHorizontalSpace = false;

      getWidgetFactory().createCLabel(composite, ""); //$NON-NLS-1$
    }

    // ------------------------------------------------------------------
    // typeLabel
    // ------------------------------------------------------------------
    getWidgetFactory().createCLabel(composite, typeLabel);

    // ------------------------------------------------------------------
    // typeCombo
    // ------------------------------------------------------------------
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;

    typeCombo = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
    typeCombo.setEditable(false);
    typeCombo.setLayoutData(data);

    // ------------------------------------------------------------------
    // BaseTypeButton
    // ------------------------------------------------------------------
    data = new GridData();
    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;

    typesBrowseButton = getWidgetFactory().createButton(composite, "", SWT.PUSH);
    typesBrowseButton.setImage(XSDEditorPlugin.getXSDImage("icons/browsebutton.gif"));
    typesBrowseButton.addSelectionListener(this);
    typesBrowseButton.setLayoutData(data);

    // ------------------------------------------------------------------
    // min/max button modifiers
    // ------------------------------------------------------------------

    data = new GridData();
    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;

    getWidgetFactory().createCLabel(composite, ""); //$NON-NLS-1$

    Composite modifierComposite = getWidgetFactory().createComposite(composite, SWT.FLAT);
    GridLayout gridLayout2 = new GridLayout();
    gridLayout2.marginLeft = 0;
    gridLayout2.marginRight = 0;
    gridLayout2.marginTop = 0;
    gridLayout2.marginBottom = 0;
    gridLayout2.numColumns = 2;
    modifierComposite.setLayout(gridLayout2);
    modifierComposite.setLayoutData(data);
    requiredButton = getWidgetFactory().createButton(modifierComposite, "Required", SWT.CHECK | SWT.FLAT); //$NON-NLS-1$
    requiredButton.addSelectionListener(this);

    listButton = getWidgetFactory().createButton(modifierComposite, "Array", SWT.CHECK | SWT.FLAT); //$NON-NLS-1$
    listButton.addSelectionListener(this);

    getWidgetFactory().createCLabel(composite, ""); //$NON-NLS-1$

    // ------------------------------------------------------------------
    // min property
    // ------------------------------------------------------------------

    getWidgetFactory().createCLabel(composite, Messages.UI_LABEL_MINOCCURS);

    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;

    minCombo = getWidgetFactory().createCCombo(composite, SWT.FLAT);
    minCombo.setLayoutData(data);
    minCombo.add("0"); //$NON-NLS-1$
    minCombo.add("1"); //$NON-NLS-1$
    applyModifyListeners(minCombo);
    minCombo.addSelectionListener(this);

    // ------------------------------------------------------------------
    // DummyLabel
    // ------------------------------------------------------------------
    data = new GridData();
    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;
    getWidgetFactory().createCLabel(composite, ""); //$NON-NLS-1$

    // ------------------------------------------------------------------
    // max property
    // ------------------------------------------------------------------
    getWidgetFactory().createCLabel(composite, Messages.UI_LABEL_MAXOCCURS);

    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;

    maxCombo = getWidgetFactory().createCCombo(composite, SWT.FLAT);
    maxCombo.setLayoutData(data);
    maxCombo.add("0"); //$NON-NLS-1$
    maxCombo.add("1"); //$NON-NLS-1$
    maxCombo.add("unbounded"); //$NON-NLS-1$
    applyModifyListeners(maxCombo);
    maxCombo.addSelectionListener(this);
  }

  public void setInput(IWorkbenchPart part, ISelection selection)
  {
    super.setInput(part, selection);
    setListenerEnabled(false);
    init();
    relayout();
    
    if (isElementReference)
    {
      TypesHelper helper = new TypesHelper(xsdSchema);
      List items = new ArrayList();
      items = helper.getGlobalElements();
      items.add(0, "");
      componentNameCombo.setItems((String [])items.toArray(new String[0]));
    }
    setListenerEnabled(true);
  }
  
  protected void init()
  {
    if (input instanceof XSDElementDeclaration)
    {
      XSDElementDeclaration xsdElementDeclaration = (XSDElementDeclaration) input;
      isElementReference = xsdElementDeclaration.isElementDeclarationReference();

      typeDefinition = xsdElementDeclaration.getResolvedElementDeclaration().getTypeDefinition();
    }
  }
  
  /*
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
   */
  public void refresh()
  {
    super.refresh();

    setListenerEnabled(false);

    XSDElementDeclaration xsdElementDeclaration = ((XSDElementDeclaration) input).getResolvedElementDeclaration();

    // refresh name
    nameText.setText("");
    typeCombo.setText(""); //$NON-NLS-1$
    typesBrowseButton.setEnabled(true);
    String name = xsdElementDeclaration.getName();
    if (name != null)
    {
      nameText.setText(name);
    }
    
    if (isElementReference)
    {
      refreshRefCombo();
    }

    // refresh type
    if (input != null)
    {
      if (input instanceof XSDElementDeclaration)
      {
        boolean isAnonymous = xsdElementDeclaration.getAnonymousTypeDefinition() != null;
        //XSDTypeDefinition typeDef = XSDUtils.getResolvedType(xsdElementDeclaration);
        XSDTypeDefinition typeDef = xsdElementDeclaration.getResolvedElementDeclaration().getTypeDefinition();
        
        String typeName = ""; //$NON-NLS-1$
        if (typeDef != null)
          typeName = typeDef.getQName(xsdSchema);

        if (typeName == null)
        {
          typeName = ""; //$NON-NLS-1$
        }

        if (isAnonymous)
        {
          typeCombo.setText("**anonymous**"); //$NON-NLS-1$
        }
        else
        {
          if (typeDefinition != null)
          {
            typeCombo.setText(typeName);
          }
          else
          {
            typeCombo.setText(Messages.UI_NO_TYPE); //$NON-NLS-1$
          }
        }
      }
    }

    // refresh min max
    refreshMinMax();

    setListenerEnabled(true);

  }

  public boolean shouldUseExtraSpace()
  {
    return false;
  }

  public void doWidgetSelected(SelectionEvent e)
  {
    if (e.widget == typesBrowseButton)
    {
      Shell shell = Display.getCurrent().getActiveShell();
      IFile currentIFile = ((IFileEditorInput) getActiveEditor().getEditorInput()).getFile();

      /** Initialize the Set types Dialog */
      final XSDTypesSearchListProvider searchListProvider = 
    	  new XSDTypesSearchListProvider(currentIFile, new XSDSchema[] {xsdSchema} );      
      ComponentSearchListDialogConfiguration configuration = new ComponentSearchListDialogConfiguration();
      configuration.setDescriptionProvider(new XSDComponentDescriptionProvider() );
      configuration.setSearchListProvider(searchListProvider);      
      configuration.setNewComponentHandler(new NewTypeButtonHandler());
      ComponentSearchListDialog dialog = new ScopedComponentSearchListDialog(shell, XSDEditorPlugin.getXSDString("_UI_LABEL_SET_TYPE"), configuration);
      //dialog.setFilterLabel("Text:");

      if (input instanceof XSDAttributeDeclaration)
      {
        searchListProvider.showComplexTypes(false);
      }

      dialog.setBlockOnOpen(true);
      dialog.create();

      if (dialog.open() == Window.OK)
      {
        //String newType = (String) dialog.getSelection().getAttributeInfo("name");
        //String nsType = (String) dialog.getSelection().getTargetNamespace();
        
        ComponentSpecification selection = dialog.getSelectedComponent();
        String newType = selection.getName();
        String namespace = selection.getQualifier();
        
        UpdateTypeReferenceCommand command = new UpdateTypeReferenceCommand((XSDElementDeclaration) input, xsdSchema.resolveTypeDefinition(namespace, newType));
        getCommandStack().execute(command);

        init();
        relayout();
      }
    }
    else if (e.widget == listButton)
    {
      int maxOccurs = (listButton.getSelection() ? XSDParticle.UNBOUNDED : 1);
      if (input instanceof XSDElementDeclaration)
      {
        XSDParticle particle = (XSDParticle) ((XSDElementDeclaration) input).eContainer();
        UpdateMaxOccursCommand command = new UpdateMaxOccursCommand("Update Maximum Occurence", particle, maxOccurs);
        getCommandStack().execute(command);
        if (maxOccurs == -1)
          maxCombo.setText("*");
        else
          maxCombo.setText("");
      }
    }
    else if (e.widget == requiredButton)
    {
      int minOccurs = requiredButton.getSelection() ? 1 : 0;
      if (input instanceof XSDElementDeclaration)
      {
        XSDParticle particle = (XSDParticle) ((XSDElementDeclaration) input).eContainer();
        UpdateMinOccursCommand command = new UpdateMinOccursCommand("Update Minimum Occurrence", particle, minOccurs);
        getCommandStack().execute(command);
      }
      minCombo.setText("" + minOccurs);
    }
    else if (e.widget == componentNameCombo)
    {
      String newValue = componentNameCombo.getText();
      String newName = newValue.substring(newValue.indexOf(":") + 1);
      if (isElementReference)
      {
        XSDElementDeclaration elementRef = (XSDElementDeclaration)input;
        elementRef.getElement().setAttribute(XSDConstants.REF_ATTRIBUTE, newValue);
        nameText.setText(newName);
        //refresh();
      }

    }
    else if (e.widget == maxCombo)
    {
      updateMaxAttribute();
    }
    else if (e.widget == minCombo)
    {
      updateMinAttribute();
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

  public void doHandleEvent(Event event)
  {
    super.doHandleEvent(event);
    if (event.widget == nameText)
    {
      String newValue = nameText.getText().trim();
      if (input instanceof XSDElementDeclaration)
      {
        XSDElementDeclaration namedComponent = ((XSDElementDeclaration) input).getResolvedElementDeclaration();
        if (!validateSection())
          return;

        Command command = null;

        // Make sure an actual name change has taken place
        String oldName = namedComponent.getName();
        if (!newValue.equals(oldName))
        {
          command = new UpdateNameCommand("Rename", namedComponent, newValue);
        }

        if (command != null && getCommandStack() != null)
        {
          getCommandStack().execute(command);
        }

        // doReferentialIntegrityCheck(namedComponent, newValue);
      }
    }
    else if (event.widget == minCombo)
    {
      requiredButton.setSelection(isRequired);
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
      setErrorMessage("Invalid name");
      return false;
    }

    return true;
  }

  public void dispose()
  {
    if (typesBrowseButton != null && !typesBrowseButton.isDisposed())
      typesBrowseButton.removeSelectionListener(this);
    if (componentNameCombo != null && !componentNameCombo.isDisposed())
      componentNameCombo.removeSelectionListener(this);
    if (minCombo != null && !minCombo.isDisposed())
      minCombo.removeSelectionListener(this);
    if (maxCombo != null && !maxCombo.isDisposed())
      maxCombo.removeSelectionListener(this);
    if (requiredButton != null && !requiredButton.isDisposed())
      requiredButton.removeSelectionListener(this);
    if (listButton != null && !listButton.isDisposed())
      listButton.removeSelectionListener(this);
    super.dispose();
  }

  protected void refreshRefCombo()
  {
    componentNameCombo.setText("");
    typesBrowseButton.setEnabled(false);

    XSDElementDeclaration namedComponent = (XSDElementDeclaration) input;
    Element element = namedComponent.getElement();
    if (element != null)
    {
      String attrValue = element.getAttribute(XSDConstants.REF_ATTRIBUTE);
      if (attrValue == null)
      {
        attrValue = "";
      }
      componentNameCombo.setText(attrValue);
    }
  }
}
