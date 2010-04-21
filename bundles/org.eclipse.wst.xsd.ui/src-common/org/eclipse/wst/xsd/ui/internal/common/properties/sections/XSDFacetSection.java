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

import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.xsd.ui.internal.common.commands.ChangeToLocalSimpleTypeCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateNumericBoundsFacetCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateStringLengthFacetCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateXSDWhiteSpaceFacetCommand;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.eclipse.wst.xsd.ui.internal.common.util.XSDCommonUIUtils;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorCSHelpIds;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDFeature;
import org.eclipse.xsd.XSDLengthFacet;
import org.eclipse.xsd.XSDMaxExclusiveFacet;
import org.eclipse.xsd.XSDMaxFacet;
import org.eclipse.xsd.XSDMaxInclusiveFacet;
import org.eclipse.xsd.XSDMaxLengthFacet;
import org.eclipse.xsd.XSDMinExclusiveFacet;
import org.eclipse.xsd.XSDMinFacet;
import org.eclipse.xsd.XSDMinInclusiveFacet;
import org.eclipse.xsd.XSDMinLengthFacet;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDVariety;
import org.eclipse.xsd.XSDWhiteSpace;
import org.eclipse.xsd.XSDWhiteSpaceFacet;
import org.eclipse.xsd.util.XSDConstants;

public class XSDFacetSection extends AbstractSection
{
  private String minLengthString, maxLengthString, titleString;
  Font titleFont;
  CLabel title;
  Label minLengthLabel;
  Text minLengthText;
  Label maxLengthLabel;
  Text maxLengthText;
  Group simpleTypeModifierGroup;
  String simpleTypeModifierGroupTitle = ""; //$NON-NLS-1$
  Button collapseWhitespaceButton;
  Button useEnumerationsButton, usePatternsButton;
  Button minimumInclusiveCheckbox;
  Button maximumInclusiveCheckbox;
  boolean isNumericBaseType;
  boolean isSimpleTypeRestriction;
  private XSDTypeDefinition typeDefinition;
  private XSDSimpleTypeDefinition xsdSimpleTypeDefinition;
  private XSDSimpleTypeDefinition currentPrimitiveType, previousPrimitiveType;
  private XSDElementDeclaration xsdElementDeclaration;
  private XSDAttributeDeclaration xsdAttributeDeclaration;
  private XSDFeature xsdFeature;
  boolean hasMaxMinFacets;

  SpecificConstraintsWidget constraintsWidget;

  private int constraintKind = SpecificConstraintsWidget.ENUMERATION;
  
  public XSDFacetSection()
  {
    super();
  }

  protected void createContents(Composite parent)
  {
    TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
    composite = factory.createFlatFormComposite(parent);

    GridLayout gridLayout = new GridLayout();
    gridLayout.marginTop = 0;
    gridLayout.marginBottom = 0;
    gridLayout.numColumns = 1;
    composite.setLayout(gridLayout);

    title = factory.createCLabel(composite, ""); //$NON-NLS-1$
    FontData fontData = composite.getFont().getFontData()[0];
    title.setFont(JFaceResources.getFontRegistry().getBold(fontData.getName()));
    title.setText(titleString + (isReadOnly ? " - " + Messages._UI_LABEL_READONLY : "")); //$NON-NLS-1$ //$NON-NLS-2$

    Composite facetComposite = factory.createComposite(composite, SWT.FLAT);

    GridData data = new GridData();
    gridLayout = new GridLayout();
    gridLayout.marginTop = 0;
    gridLayout.marginBottom = 0;
    gridLayout.numColumns = 2;
    facetComposite.setLayout(gridLayout);
    data.grabExcessVerticalSpace = true;
    data.grabExcessHorizontalSpace = true;
    data.verticalAlignment = GridData.FILL;
    data.horizontalAlignment = GridData.FILL;
    facetComposite.setLayoutData(data);

    data = new GridData();
    data.grabExcessVerticalSpace = true;
    data.horizontalAlignment = GridData.FILL;
    data.verticalAlignment = GridData.FILL;

    simpleTypeModifierGroup = getWidgetFactory().createGroup(facetComposite, simpleTypeModifierGroupTitle);
    GridLayout groupGrid = new GridLayout();
    groupGrid.marginTop = 0;
    groupGrid.marginBottom = 0;
    groupGrid.numColumns = 1;
    simpleTypeModifierGroup.setLayoutData(data);
    simpleTypeModifierGroup.setLayout(groupGrid);

    Composite simpleTypeModifierComposite = getWidgetFactory().createFlatFormComposite(simpleTypeModifierGroup);
    data = new GridData();
    data.grabExcessVerticalSpace = true;
    data.verticalAlignment = GridData.FILL;
    data.horizontalAlignment = GridData.FILL;

    GridLayout grid = new GridLayout();
    grid.marginTop = 0;
    grid.marginBottom = 0;
    grid.numColumns = 3;
    simpleTypeModifierComposite.setLayout(grid);
    simpleTypeModifierComposite.setLayoutData(data);
    if (hasMaxMinFacets)
    {
      boolean isLinux = java.io.File.separator.equals("/");
      minLengthLabel = factory.createLabel(simpleTypeModifierComposite, minLengthString);
      minLengthText = factory.createText(simpleTypeModifierComposite, ""); //$NON-NLS-1$
      if (isLinux)
      {
      	minLengthText.addListener(SWT.Modify, customListener);
      	minLengthText.addListener(SWT.KeyDown, customListener);
      	minLengthText.addListener(SWT.FocusOut, customListener);
      }
      else
        applyAllListeners(minLengthText);
      
      PlatformUI.getWorkbench().getHelpSystem().setHelp(minLengthText,
      		XSDEditorCSHelpIds.CONSTRAINTS_TAB__MINIMUM_LENGTH);

      GridData minGridData = new GridData();
      minGridData.widthHint = 100;
      minLengthText.setLayoutData(minGridData);
      minimumInclusiveCheckbox = factory.createButton(simpleTypeModifierComposite, Messages._UI_LABEL_INCLUSIVE, SWT.CHECK);
      minimumInclusiveCheckbox.addSelectionListener(this);

      maxLengthLabel = factory.createLabel(simpleTypeModifierComposite, maxLengthString);
      maxLengthText = factory.createText(simpleTypeModifierComposite, ""); //$NON-NLS-1$
      if (isLinux)
      {
    	  maxLengthText.addListener(SWT.Modify, customListener);
    	  maxLengthText.addListener(SWT.KeyDown, customListener);
    	  maxLengthText.addListener(SWT.FocusOut, customListener);
      }
      else
        applyAllListeners(maxLengthText);
      
      PlatformUI.getWorkbench().getHelpSystem().setHelp(maxLengthText,
        		XSDEditorCSHelpIds.CONSTRAINTS_TAB__MAXIMUM_LENGTH);

      GridData maxGridData = new GridData();
      maxGridData.widthHint = 100;
      maxLengthText.setLayoutData(maxGridData);

      maximumInclusiveCheckbox = factory.createButton(simpleTypeModifierComposite, Messages._UI_LABEL_INCLUSIVE, SWT.CHECK);
      maximumInclusiveCheckbox.addSelectionListener(this);

      minimumInclusiveCheckbox.setVisible(isNumericBaseType);
      maximumInclusiveCheckbox.setVisible(isNumericBaseType);
    }
    collapseWhitespaceButton = factory.createButton(simpleTypeModifierComposite, Messages._UI_LABEL_COLLAPSE_WHITESPACE, SWT.CHECK);
    collapseWhitespaceButton.addSelectionListener(this);
    
    PlatformUI.getWorkbench().getHelpSystem().setHelp(collapseWhitespaceButton,
      		XSDEditorCSHelpIds.CONSTRAINTS_TAB__COLLAPSE_WHITESPACE);

    Group specificValueConstraintsGroup = factory.createGroup(facetComposite, Messages._UI_LABEL_SPECIFIC_CONSTRAINT_VALUES);
    gridLayout = new GridLayout();
    gridLayout.marginTop = 0;
    gridLayout.marginBottom = 0;
    gridLayout.numColumns = 2;
    specificValueConstraintsGroup.setLayout(gridLayout);
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.grabExcessVerticalSpace = true;
    data.horizontalAlignment = GridData.FILL;
    data.verticalAlignment = GridData.FILL;
    specificValueConstraintsGroup.setLayoutData(data);

    Composite compositeForButtons = factory.createFlatFormComposite(specificValueConstraintsGroup);
    gridLayout = new GridLayout();
    gridLayout.marginTop = 0;
    gridLayout.verticalSpacing = 1;
    gridLayout.marginBottom = 0;
    gridLayout.numColumns = 1;
    compositeForButtons.setLayout(gridLayout);
    data = new GridData();
    data.verticalAlignment = GridData.BEGINNING;
    compositeForButtons.setLayoutData(data);

    factory.createCLabel(compositeForButtons, Messages._UI_LABEL_RESTRICT_VALUES_BY);
//    useDefinedValuesButton = factory.createButton(compositeForButtons, "Only permit certain values", SWT.CHECK);
//    useDefinedValuesButton.addSelectionListener(this);

    Composite compositeForRadioButtons = factory.createFlatFormComposite(compositeForButtons);
    gridLayout = new GridLayout();
    gridLayout.marginTop = 0;
    gridLayout.marginLeft = 0;
    gridLayout.marginBottom = 0;
    gridLayout.numColumns = 1;
    compositeForRadioButtons.setLayout(gridLayout);
    useEnumerationsButton = factory.createButton(compositeForRadioButtons, Messages._UI_LABEL_ENUMERATIONS, SWT.RADIO);
    useEnumerationsButton.addSelectionListener(this);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(useEnumerationsButton,
      		XSDEditorCSHelpIds.CONSTRAINTS_TAB__ENUMERATIONS);
    usePatternsButton = factory.createButton(compositeForRadioButtons, Messages._UI_LABEL_PATTERNS, SWT.RADIO);
    usePatternsButton.addSelectionListener(this);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(usePatternsButton,
      		XSDEditorCSHelpIds.CONSTRAINTS_TAB__PATTERNS);    
    
    constraintsWidget = new SpecificConstraintsWidget(specificValueConstraintsGroup, factory, (input instanceof XSDFeature) ? (XSDFeature)input : null, xsdSimpleTypeDefinition, this, constraintKind);
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.grabExcessVerticalSpace = true;
    data.horizontalAlignment = GridData.FILL;
    data.verticalAlignment = GridData.FILL;
    constraintsWidget.getControl().setLayoutData(data);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(constraintsWidget.getControl(),
      		XSDEditorCSHelpIds.CONSTRAINTS_TAB__NO_LABEL);
  }
  
  public void doSetInput()
  {
    setInput(getPart(), getSelection());
  }

  public void setInput(IWorkbenchPart part, ISelection selection)
  {
    super.setInput(part, selection);
    init();
    
    XSDSchema schemaOfType = null;

    if (!isReadOnly)
    {
      schemaOfType = xsdSimpleTypeDefinition.getSchema();
    }
    if (schemaOfType == owningEditor.getAdapter(XSDSchema.class))
    {
      isReadOnly = false;
    }
    else
    {
      if (!XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001.equals(xsdSimpleTypeDefinition.getTargetNamespace()))
        isReadOnly = true;
    }
    if (hasMaxMinFacets)
    {
      title.setText(titleString + (isReadOnly ? " - " + Messages._UI_LABEL_READONLY : "")); //$NON-NLS-1$ //$NON-NLS-2$
    }
    relayout();
    constraintsWidget.setCommandStack(getCommandStack());
  }

  protected void init()
  {
    hasMaxMinFacets = false;
    try
    {
    updateInput();
    
    if (xsdSimpleTypeDefinition != null)
    {
      isSimpleTypeRestriction = xsdSimpleTypeDefinition.getVariety().getValue() == XSDVariety.ATOMIC;
      
      XSDSimpleTypeDefinition targetST = xsdSimpleTypeDefinition;
      
      while (!XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001.equals(targetST.getTargetNamespace()) && targetST != null)
      {
        targetST = targetST.getBaseTypeDefinition();
      }
      
      minLengthString = ""; //$NON-NLS-1$
      maxLengthString = ""; //$NON-NLS-1$
      if (targetST.getValidFacets().contains("length")) //$NON-NLS-1$
      {
        minLengthString = Messages._UI_LABEL_MINIMUM_LENGTH;
        maxLengthString = Messages._UI_LABEL_MAXIMUM_LENGTH;
        simpleTypeModifierGroupTitle = Messages._UI_LABEL_CONSTRAINTS_ON_LENGTH_OF + targetST.getName();
        isNumericBaseType = false;
        hasMaxMinFacets = true;
      }
      else if (targetST.getValidFacets().contains("maxInclusive")) //$NON-NLS-1$
      {
        simpleTypeModifierGroupTitle = Messages._UI_LABEL_CONSTRAINTS_ON_VALUE_OF + targetST.getName();
        minLengthString = Messages._UI_LABEL_MINIMUM_VALUE;
        maxLengthString = Messages._UI_LABEL_MAXIMUM_VALUE;
        isNumericBaseType = true;
        hasMaxMinFacets = true;
      }
      else
      {
        simpleTypeModifierGroupTitle = Messages._UI_LABEL_CONTRAINTS_ON + (targetST != null ? targetST.getName() : "anyType"); //$NON-NLS-1$
      }
    }
    }
    catch(Exception e)
    {
    }
  }
  
  private void updateInput()
  {
    previousPrimitiveType = currentPrimitiveType;
    if (input instanceof XSDFeature)
    {
      xsdFeature = (XSDFeature) input;
      typeDefinition = xsdFeature.getResolvedFeature().getType();
      XSDTypeDefinition anonymousTypeDefinition = null;
      if (xsdFeature instanceof XSDElementDeclaration)
      {
        xsdElementDeclaration = (XSDElementDeclaration)xsdFeature;
        anonymousTypeDefinition = xsdElementDeclaration.getResolvedElementDeclaration().getAnonymousTypeDefinition();
      }
      else if (xsdFeature instanceof XSDAttributeDeclaration)
      {
        xsdAttributeDeclaration = (XSDAttributeDeclaration)xsdFeature;
        anonymousTypeDefinition = xsdAttributeDeclaration.getResolvedAttributeDeclaration().getAnonymousTypeDefinition();
      }
      
      if (typeDefinition instanceof XSDSimpleTypeDefinition)
      {
        xsdSimpleTypeDefinition = (XSDSimpleTypeDefinition) typeDefinition;
      }
      
      if (anonymousTypeDefinition instanceof XSDSimpleTypeDefinition)
      {
        xsdSimpleTypeDefinition = (XSDSimpleTypeDefinition)anonymousTypeDefinition;
      }
      
      if (xsdSimpleTypeDefinition != null)
      {
        if (!XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001.equals(xsdSimpleTypeDefinition.getTargetNamespace()))
        {
          XSDSimpleTypeDefinition basePrimitiveType = xsdSimpleTypeDefinition.getBaseTypeDefinition();
          String basePrimitiveTypeString = basePrimitiveType != null ? basePrimitiveType.getName() : "";
          currentPrimitiveType = basePrimitiveType;
          titleString = Messages._UI_LABEL_TYPE + (anonymousTypeDefinition != null ? "(" + xsdFeature.getResolvedFeature().getName() + "Type)" : xsdSimpleTypeDefinition.getName())  + " , " + Messages._UI_LABEL_BASE + ": " + basePrimitiveTypeString; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        }
        else
        {
          currentPrimitiveType = xsdSimpleTypeDefinition;
          titleString = Messages._UI_LABEL_TYPE + (anonymousTypeDefinition != null ? "(" + xsdFeature.getResolvedFeature().getName() + "Type)" : xsdSimpleTypeDefinition.getName());
        }
      }
    }
    else if (input instanceof XSDSimpleTypeDefinition)
    {
      xsdSimpleTypeDefinition = (XSDSimpleTypeDefinition) input;
      currentPrimitiveType = xsdSimpleTypeDefinition;
      titleString = Messages._UI_LABEL_TYPE + (xsdSimpleTypeDefinition.getName() == null ? "(localType)" : xsdSimpleTypeDefinition.getName()) + " , " + Messages._UI_LABEL_BASE + ": " + xsdSimpleTypeDefinition.getBaseTypeDefinition().getName(); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  public void refresh()
  {
    super.refresh();
    init();
    
    if (currentPrimitiveType != previousPrimitiveType)
    {
      relayout();      
    }
    
    setListenerEnabled(false);
    
    XSDWhiteSpaceFacet whitespaceFacet = xsdSimpleTypeDefinition.getWhiteSpaceFacet();
    if (whitespaceFacet != null)
    {
      if (xsdSimpleTypeDefinition.getFacetContents().contains(whitespaceFacet))
      {
        if (XSDWhiteSpace.COLLAPSE_LITERAL.equals(whitespaceFacet.getValue()))
        {
          if (!XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001.equals(xsdSimpleTypeDefinition.getTargetNamespace()))
          {
            collapseWhitespaceButton.setSelection(true);
          }
          else
          {
            collapseWhitespaceButton.setSelection(false);
          }
        }
      }
    }

    if (hasMaxMinFacets && !minLengthLabel.isDisposed() && !maxLengthLabel.isDisposed())
    {
      minLengthLabel.setText(minLengthString);
      maxLengthLabel.setText(maxLengthString);

      if (!isNumericBaseType)
        refreshStringLength();
      else
        refreshValueLengths();
    }

    if ((xsdSimpleTypeDefinition.getEnumerationFacets().size() > 0 
    		&& constraintsWidget.getConstraintKind() == SpecificConstraintsWidget.ENUMERATION)
    		|| xsdSimpleTypeDefinition.getPatternFacets().size() == 0) 
    {
      usePatternsButton.setSelection(false);
      useEnumerationsButton.setSelection(true);
      constraintsWidget.setConstraintKind(SpecificConstraintsWidget.ENUMERATION);
      constraintsWidget.addButton.setEnabled(true);
    }
    else if ((xsdSimpleTypeDefinition.getPatternFacets().size() > 0
    		&& constraintsWidget.getConstraintKind() == SpecificConstraintsWidget.PATTERN)
    		|| xsdSimpleTypeDefinition.getEnumerationFacets().size() == 0)
    {
      usePatternsButton.setSelection(true);
      useEnumerationsButton.setSelection(false);
      constraintsWidget.setConstraintKind(SpecificConstraintsWidget.PATTERN);
      constraintsWidget.addButton.setEnabled(false);
    }
    else
    {
      usePatternsButton.setSelection(false);
      useEnumerationsButton.setSelection(true);
      constraintsWidget.setConstraintKind(SpecificConstraintsWidget.ENUMERATION);
      constraintsWidget.addButton.setEnabled(true);
    }
    constraintKind = constraintsWidget.getConstraintKind();
    constraintsWidget.setInput(xsdSimpleTypeDefinition);

    setWidgetsEnabled(isSimpleTypeRestriction && !isReadOnly);
    composite.setEnabled(isSimpleTypeRestriction && !isReadOnly);
    
    setListenerEnabled(true);
  }
  
  private void setWidgetsEnabled(boolean isEnabled)
  {
    if (collapseWhitespaceButton != null && !collapseWhitespaceButton.isDisposed())
      collapseWhitespaceButton.setEnabled(isEnabled);
    if (useEnumerationsButton != null && !useEnumerationsButton.isDisposed())
      useEnumerationsButton.setEnabled(isEnabled);
    if (usePatternsButton != null && !usePatternsButton.isDisposed())
      usePatternsButton.setEnabled(isEnabled);
    if (minimumInclusiveCheckbox != null && !minimumInclusiveCheckbox.isDisposed())
      minimumInclusiveCheckbox.setEnabled(isEnabled);
    if (maximumInclusiveCheckbox != null && !maximumInclusiveCheckbox.isDisposed())
      maximumInclusiveCheckbox.setEnabled(isEnabled);
    if (constraintsWidget != null && !constraintsWidget.getControl().isDisposed())
    {
      if(constraintsWidget.getConstraintKind() == SpecificConstraintsWidget.PATTERN)
      {
        constraintsWidget.addButton.setEnabled(false);  
      }
      else 
      {
        constraintsWidget.addButton.setEnabled(isEnabled);
      }      
      constraintsWidget.addUsingDialogButton.setEnabled(isEnabled);
    }
  }

  protected void relayout()
  {
    Composite parent = composite.getParent();
    parent.getParent().setRedraw(false);

    if (parent != null && !parent.isDisposed())
    {
      Control[] children = parent.getChildren();
      for (int i = 0; i < children.length; i++)
      {
        children[i].dispose();
      }
    }
    createContents(parent);
    parent.getParent().layout(true, true);
    parent.getParent().setRedraw(true);
    refresh();
  }

  public void dispose()
  {
    if (titleFont != null && !titleFont.isDisposed())
      titleFont.dispose();
    titleFont = null;
    
    if (minimumInclusiveCheckbox != null && !minimumInclusiveCheckbox.isDisposed())
      minimumInclusiveCheckbox.removeSelectionListener(this);
    if (maximumInclusiveCheckbox != null && !maximumInclusiveCheckbox.isDisposed())
      maximumInclusiveCheckbox.removeSelectionListener(this);
    
    if (collapseWhitespaceButton != null && !collapseWhitespaceButton.isDisposed())
      collapseWhitespaceButton.removeSelectionListener(this);

    if (maxLengthText != null && !maxLengthText.isDisposed())
      removeListeners(maxLengthText);
    if (minLengthText != null && !minLengthText.isDisposed())
      removeListeners(minLengthText);
    
    super.dispose();
  }

  public void refreshStringLength()
  {
    XSDMinLengthFacet minLengthFacet = xsdSimpleTypeDefinition.getMinLengthFacet();
    XSDMaxLengthFacet maxLengthFacet = xsdSimpleTypeDefinition.getMaxLengthFacet();
    XSDLengthFacet lengthFacet = xsdSimpleTypeDefinition.getLengthFacet();

    try
    {
      if (minLengthFacet != null)
      {
        int minLengthValue = minLengthFacet.getValue();
        if (minLengthValue >= 0 && minLengthFacet.getRootContainer() == xsdSchema)
        {
          minLengthText.setText(Integer.toString(minLengthValue));
        }
        else
        {
          minLengthText.setText(""); //$NON-NLS-1$
        }
      }
      if (maxLengthFacet != null)
      {
        int maxLength = maxLengthFacet.getValue();
        if (maxLength >= 0 && maxLengthFacet.getRootContainer() == xsdSchema)
        {
          maxLengthText.setText(Integer.toString(maxLength));
        }
        else
        {
          maxLengthText.setText(""); //$NON-NLS-1$
        }
      }
      if (lengthFacet != null)
      {
        int length = lengthFacet.getValue();
        if (length >= 0 && lengthFacet.getRootContainer() == xsdSchema)
        {
          minLengthText.setText(Integer.toString(length));
          maxLengthText.setText(Integer.toString(length));
        }
      }
    }
    catch (Exception e)
    {

    }

  }

  public void refreshValueLengths()
  {
    XSDSimpleTypeDefinition type = xsdSimpleTypeDefinition;
    XSDMinFacet minFacet = type.getMinFacet();
    XSDMaxFacet maxFacet = type.getMaxFacet();

    if (minFacet != null && minFacet.getRootContainer() == xsdSchema)
    {
      if (minFacet.getElement().getLocalName().equals(XSDConstants.MINEXCLUSIVE_ELEMENT_TAG) || minFacet.getElement().getLocalName().equals(XSDConstants.MININCLUSIVE_ELEMENT_TAG))
      {
        minLengthText.setText(minFacet.getLexicalValue());
        minimumInclusiveCheckbox.setSelection(minFacet.isInclusive());
        minimumInclusiveCheckbox.setEnabled(true);
      }
      else
      {
        minLengthText.setText(""); //$NON-NLS-1$
      }
    }
    else
    {
      minimumInclusiveCheckbox.setSelection(false);
      minimumInclusiveCheckbox.setEnabled(false);
    }

    if (maxFacet != null && maxFacet.getRootContainer() == xsdSchema)
    {
      if (maxFacet.getElement().getLocalName().equals(XSDConstants.MAXEXCLUSIVE_ELEMENT_TAG) || maxFacet.getElement().getLocalName().equals(XSDConstants.MAXINCLUSIVE_ELEMENT_TAG))
      {
        maxLengthText.setText(maxFacet.getLexicalValue());
        maximumInclusiveCheckbox.setSelection(maxFacet.isInclusive());
        maximumInclusiveCheckbox.setEnabled(true);
      }
      else
      {
        maxLengthText.setText(""); //$NON-NLS-1$
      }
    }
    else
    {
      maximumInclusiveCheckbox.setSelection(false);
      maximumInclusiveCheckbox.setEnabled(false);
    }
  }

  protected void doHandleEvent(Event event)
  {
    super.doHandleEvent(event);
    Command command = null;
    boolean doUpdateMax = false, doUpdateMin = false;
    boolean doSetInput = false;
    
    String minValue = minLengthText.getText().trim();
    String maxValue = maxLengthText.getText().trim();

    XSDLengthFacet lengthFacet = xsdSimpleTypeDefinition.getLengthFacet();
    XSDMinLengthFacet minLengthFacet = xsdSimpleTypeDefinition.getMinLengthFacet();
    XSDMaxLengthFacet maxLengthFacet = xsdSimpleTypeDefinition.getMaxLengthFacet();
    
    XSDMinInclusiveFacet minInclusiveFacet = xsdSimpleTypeDefinition.getMinInclusiveFacet();
    XSDMinExclusiveFacet minExclusiveFacet = xsdSimpleTypeDefinition.getMinExclusiveFacet();
    XSDMaxInclusiveFacet maxInclusiveFacet = xsdSimpleTypeDefinition.getMaxInclusiveFacet();
    XSDMaxExclusiveFacet maxExclusiveFacet = xsdSimpleTypeDefinition.getMaxExclusiveFacet();

    String currentMinInclusive = null, currentMinExclusive = null, currentMaxInclusive = null, currentMaxExclusive = null;
    if (minInclusiveFacet != null)
    {
      currentMinInclusive = minInclusiveFacet.getLexicalValue();
    }
    if (minExclusiveFacet != null)
    {
      currentMinExclusive = minExclusiveFacet.getLexicalValue();
    }
    if (maxInclusiveFacet != null)
    {
      currentMaxInclusive = maxInclusiveFacet.getLexicalValue();
    }
    if (maxExclusiveFacet != null)
    {
      currentMaxExclusive = maxExclusiveFacet.getLexicalValue();
    }
    
    String currentLength = null, currentMin = null, currentMax = null;
    if (lengthFacet != null)
    {
      currentLength = lengthFacet.getLexicalValue();
    }
    if (minLengthFacet != null)
    {
      currentMin = minLengthFacet.getLexicalValue();
    }
    if (maxLengthFacet != null)
    {
      currentMax = maxLengthFacet.getLexicalValue();
    }
    
    if (event.widget == minLengthText)
    {
      try
      {
        if (minValue.length() > 0)
        {
          if (!isNumericBaseType)
          {
            Number big = new BigInteger(minValue);
            big.toString();
            if (minLengthFacet != null)
            {
              if (minValue.equals(currentMin) || minValue.equals(currentLength))
                return;
            }
            else
            {
              if (maxValue != null && minValue.equals(maxValue) && lengthFacet != null)
              {
                return;
              }
            }
          }
          else
          {
            if (xsdSchema.getSchemaForSchema().resolveSimpleTypeDefinition("double").equals(xsdSimpleTypeDefinition.getBaseType()) || //$NON-NLS-1$
                xsdSchema.getSchemaForSchema().resolveSimpleTypeDefinition("float").equals(xsdSimpleTypeDefinition.getBaseType()) || //$NON-NLS-1$
                xsdSchema.getSchemaForSchema().resolveSimpleTypeDefinition("decimal").equals(xsdSimpleTypeDefinition.getBaseType())) //$NON-NLS-1$
            {
              BigDecimal bigDecimal = new BigDecimal(minValue);
              bigDecimal.toString();
              if ( (currentMinInclusive != null && minValue.equals(currentMinInclusive)) ||
                   (currentMinExclusive != null && minValue.equals(currentMinExclusive)) )
              {
                return;
              }
            }
            else
            {
              Number big = new BigInteger(minValue);
              big.toString();
            }
            minimumInclusiveCheckbox.setEnabled(true);
          }
        }
        else
        {
          if (!isNumericBaseType)
          {
            if (currentMin == null && currentLength == null)
              return;
          }
          else
          {
            if (currentMinInclusive == null && minimumInclusiveCheckbox.getSelection())
            {
              return;
            }
            else if (currentMinExclusive == null && !minimumInclusiveCheckbox.getSelection())
            {
              return;
            }
          }
          minimumInclusiveCheckbox.setEnabled(false);
          minValue = null;
        }
        doUpdateMin = true;
      }
      catch (NumberFormatException e)
      {
        // TODO show error message
        doUpdateMin = false;
      }
    }
    if (event.widget == maxLengthText)
    {
      try
      {
        if (maxValue.length() > 0)
        {
          if (!isNumericBaseType)
          {
            Number big = new BigInteger(maxValue);
            big.toString();
            if (maxLengthFacet != null)
            {
              if (maxValue.equals(currentMax) || maxValue.equals(currentLength))
                return;
            }
            else
            {
              if (minValue != null && maxValue.equals(minValue) && lengthFacet != null)
              {
                return;
              }
            }
          }
          else
          {
            if (xsdSchema.getSchemaForSchema().resolveSimpleTypeDefinition("double").equals(xsdSimpleTypeDefinition.getBaseType()) || //$NON-NLS-1$
                xsdSchema.getSchemaForSchema().resolveSimpleTypeDefinition("float").equals(xsdSimpleTypeDefinition.getBaseType()) || //$NON-NLS-1$
                xsdSchema.getSchemaForSchema().resolveSimpleTypeDefinition("decimal").equals(xsdSimpleTypeDefinition.getBaseType())) //$NON-NLS-1$
            {
              BigDecimal bigDecimal = new BigDecimal(maxValue);
              bigDecimal.toString();
            }
            else
            {
              Number big = new BigInteger(maxValue);
              big.toString();
            }
            maximumInclusiveCheckbox.setEnabled(true);
          }
        }
        else
        {
          if (!isNumericBaseType)
          {
            if (currentMax == null && currentLength == null)
              return;
          }
          else
          {
            if (currentMaxInclusive == null && maximumInclusiveCheckbox.getSelection())
            {
              return;
            }
            else if (currentMaxExclusive == null && !maximumInclusiveCheckbox.getSelection())
            {
              return;
            }
            maximumInclusiveCheckbox.setEnabled(false);
          }
          maxValue = null;
        }

        doUpdateMax = true;
      }
      catch (NumberFormatException e)
      {
        doUpdateMax = false;
        // TODO show error message
      }
    }
    
    if (XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001.equals(xsdSimpleTypeDefinition.getTargetNamespace()) && (doUpdateMax || doUpdateMin))
    {
      XSDSimpleTypeDefinition anonymousSimpleType = null;
      CompoundCommand compoundCommand = new CompoundCommand();
      ChangeToLocalSimpleTypeCommand changeToAnonymousCommand = null;
      if (input instanceof XSDFeature)
      {
        anonymousSimpleType = XSDCommonUIUtils.getAnonymousSimpleType((XSDFeature)input, xsdSimpleTypeDefinition);
        if (anonymousSimpleType == null)
        {
          anonymousSimpleType = XSDFactory.eINSTANCE.createXSDSimpleTypeDefinition();
          anonymousSimpleType.setBaseTypeDefinition(xsdSimpleTypeDefinition);
       
          changeToAnonymousCommand = new ChangeToLocalSimpleTypeCommand(Messages._UI_ACTION_CONSTRAIN_LENGTH, (XSDFeature)input);
          changeToAnonymousCommand.setAnonymousSimpleType(anonymousSimpleType);
          compoundCommand.add(changeToAnonymousCommand);
          doSetInput = true;
        }

        if (!isNumericBaseType)
        {
          UpdateStringLengthFacetCommand updateCommand = new UpdateStringLengthFacetCommand("", anonymousSimpleType); //$NON-NLS-1$
          if (doUpdateMax)
          {
            updateCommand.setMax(maxValue);
          }
          if (doUpdateMin)
          {
            updateCommand.setMin(minValue);
          }
          compoundCommand.add(updateCommand);
        }
        else
        {
          UpdateNumericBoundsFacetCommand updateCommand = new UpdateNumericBoundsFacetCommand(Messages._UI_ACTION_UPDATE_BOUNDS, anonymousSimpleType, minimumInclusiveCheckbox.getSelection(), maximumInclusiveCheckbox.getSelection());
          if (doUpdateMax)
          {
            updateCommand.setMax(maxValue);
          }
          if (doUpdateMin)
          {
            updateCommand.setMin(minValue);
          }
          compoundCommand.add(updateCommand);
        }
        command = compoundCommand;
        getCommandStack().execute(command);
      }
      else if (input instanceof XSDSimpleTypeDefinition)
      {
        if (!isNumericBaseType)
        {
          UpdateStringLengthFacetCommand updateCommand = new UpdateStringLengthFacetCommand("", xsdSimpleTypeDefinition); //$NON-NLS-1$
          if (doUpdateMax)
          {
            updateCommand.setMax(maxValue);
          }
          if (doUpdateMin)
          {
            updateCommand.setMin(minValue);
          }
          command = updateCommand;
        }
        else
        {
          UpdateNumericBoundsFacetCommand updateCommand = new UpdateNumericBoundsFacetCommand(Messages._UI_ACTION_UPDATE_BOUNDS, xsdSimpleTypeDefinition, minimumInclusiveCheckbox.getSelection(), maximumInclusiveCheckbox.getSelection());
          if (doUpdateMax)
          {
            updateCommand.setMax(maxValue);
          }
          if (doUpdateMin)
          {
            updateCommand.setMin(minValue);
          }
          command = updateCommand;
        }
        getCommandStack().execute(command);
      }
    }
    else
    {
      if (!isNumericBaseType)
      {
        UpdateStringLengthFacetCommand updateCommand = new UpdateStringLengthFacetCommand("", xsdSimpleTypeDefinition); //$NON-NLS-1$
        if (doUpdateMax)
        {
          updateCommand.setMax(maxValue);
        }
        if (doUpdateMin)
        {
          updateCommand.setMin(minValue);
        }
        getCommandStack().execute(updateCommand);
      }
      else
      {
        UpdateNumericBoundsFacetCommand updateCommand = new UpdateNumericBoundsFacetCommand(Messages._UI_ACTION_UPDATE_BOUNDS, xsdSimpleTypeDefinition, minimumInclusiveCheckbox.getSelection(), maximumInclusiveCheckbox.getSelection());
        if (doUpdateMax)
        {
          updateCommand.setMax(maxValue);
        }
        if (doUpdateMin)
        {
          updateCommand.setMin(minValue);
        }
        getCommandStack().execute(updateCommand);
      }
      
    }
    refresh();
    if (doSetInput)
      setInput(getPart(), getSelection());
  }
  
  public void widgetSelected(SelectionEvent e)
  {
    if (e.widget == collapseWhitespaceButton)
    {
       CompoundCommand compoundCommand = new CompoundCommand();
       XSDSimpleTypeDefinition anonymousSimpleType = null;
       if (XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001.equals(xsdSimpleTypeDefinition.getTargetNamespace()))
       {
         if (input instanceof XSDFeature)
         {
           anonymousSimpleType = XSDCommonUIUtils.getAnonymousSimpleType((XSDFeature)input, xsdSimpleTypeDefinition);
           if (anonymousSimpleType == null)
           {
             anonymousSimpleType = XSDFactory.eINSTANCE.createXSDSimpleTypeDefinition();
             anonymousSimpleType.setBaseTypeDefinition(xsdSimpleTypeDefinition);
           
             ChangeToLocalSimpleTypeCommand changeToAnonymousCommand = new ChangeToLocalSimpleTypeCommand(Messages._UI_ACTION_CONSTRAIN_LENGTH, (XSDFeature)input);
             changeToAnonymousCommand.setAnonymousSimpleType(anonymousSimpleType);
             compoundCommand.add(changeToAnonymousCommand);
           }
  
           UpdateXSDWhiteSpaceFacetCommand whiteSpaceCommand = new UpdateXSDWhiteSpaceFacetCommand(Messages._UI_ACTION_COLLAPSE_WHITESPACE, anonymousSimpleType, collapseWhitespaceButton.getSelection());
           compoundCommand.add(whiteSpaceCommand);
            
           getCommandStack().execute(compoundCommand);
        }
         setInput(getPart(), getSelection());
      }
      else
      {
        UpdateXSDWhiteSpaceFacetCommand whiteSpaceCommand = new UpdateXSDWhiteSpaceFacetCommand(Messages._UI_ACTION_COLLAPSE_WHITESPACE, xsdSimpleTypeDefinition, collapseWhitespaceButton.getSelection());
        getCommandStack().execute(whiteSpaceCommand);
      }
    }
    else if (e.widget == minimumInclusiveCheckbox)
    {
      String minValue = minLengthText.getText().trim();
      if (minValue.length() == 0) minValue = null;

      UpdateNumericBoundsFacetCommand updateCommand = new UpdateNumericBoundsFacetCommand(Messages._UI_ACTION_UPDATE_BOUNDS, xsdSimpleTypeDefinition, minimumInclusiveCheckbox.getSelection(), maximumInclusiveCheckbox.getSelection());
      updateCommand.setMin(minValue);
      
      if (minValue != null)
        getCommandStack().execute(updateCommand);
    }
    else if (e.widget == maximumInclusiveCheckbox)
    {
      String maxValue = maxLengthText.getText().trim();
      if (maxValue.length() == 0) maxValue = null;
      UpdateNumericBoundsFacetCommand updateCommand = new UpdateNumericBoundsFacetCommand(Messages._UI_ACTION_UPDATE_BOUNDS, xsdSimpleTypeDefinition, minimumInclusiveCheckbox.getSelection(), maximumInclusiveCheckbox.getSelection());
      updateCommand.setMax(maxValue);
      if (maxValue != null)
        getCommandStack().execute(updateCommand);
    }
    else if (e.widget == useEnumerationsButton)
    {
      constraintsWidget.addButton.setEnabled(true);
      if (isListenerEnabled())
      {
        constraintsWidget.setConstraintKind(SpecificConstraintsWidget.ENUMERATION);
        constraintKind = constraintsWidget.getConstraintKind();
      }
    }
    else if (e.widget == usePatternsButton)
    {
      constraintsWidget.addButton.setEnabled(false);
      if (isListenerEnabled())
      {
        constraintsWidget.setConstraintKind(SpecificConstraintsWidget.PATTERN);
        constraintKind = constraintsWidget.getConstraintKind();
      }
    }
  }
  
  public boolean shouldUseExtraSpace()
  {
    return true;
  }

}
