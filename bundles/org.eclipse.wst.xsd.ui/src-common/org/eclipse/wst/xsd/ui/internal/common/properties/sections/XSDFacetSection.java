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

import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
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
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.xsd.ui.internal.common.commands.ChangeToLocalSimpleTypeCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateNumericBoundsFacetCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateStringLengthFacetCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateXSDWhiteSpaceFacetCommand;
import org.eclipse.wst.xsd.ui.internal.common.util.XSDCommonUIUtils;
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
  String simpleTypeModifierGroupTitle = "";
  Button collapseWhitespaceButton;
  Button useEnumerationsButton, usePatternsButton;
  Button minimumInclusiveCheckbox;
  Button maximumInclusiveCheckbox;
  boolean isNumericBaseType;
  private XSDTypeDefinition typeDefinition;
  private XSDSimpleTypeDefinition xsdSimpleTypeDefinition;
  private XSDElementDeclaration xsdElementDeclaration;
  private XSDAttributeDeclaration xsdAttributeDeclaration;
  private XSDFeature xsdFeature;
  boolean hasMaxMinFacets;

  SpecificConstraintsWidget constraintsWidget;

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
    int height = fontData.getHeight();
    fontData.setHeight(height + 2);
    fontData.setStyle(SWT.BOLD);
    titleFont = new Font(null, fontData);
    title.setFont(titleFont);
    title.setText(titleString + (isReadOnly ? " - ReadOnly" : ""));

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
      minLengthLabel = factory.createLabel(simpleTypeModifierComposite, minLengthString);
      minLengthText = factory.createText(simpleTypeModifierComposite, "");
      applyAllListeners(minLengthText);

      GridData minGridData = new GridData();
      minGridData.widthHint = 100;
      minLengthText.setLayoutData(minGridData);
      minimumInclusiveCheckbox = factory.createButton(simpleTypeModifierComposite, "Inclusive", SWT.CHECK);
      minimumInclusiveCheckbox.addSelectionListener(this);

      maxLengthLabel = factory.createLabel(simpleTypeModifierComposite, maxLengthString);
      maxLengthText = factory.createText(simpleTypeModifierComposite, "");
      applyAllListeners(maxLengthText);

      GridData maxGridData = new GridData();
      maxGridData.widthHint = 100;
      maxLengthText.setLayoutData(maxGridData);

      maximumInclusiveCheckbox = factory.createButton(simpleTypeModifierComposite, "Inclusive", SWT.CHECK);
      maximumInclusiveCheckbox.addSelectionListener(this);

      minimumInclusiveCheckbox.setVisible(isNumericBaseType);
      maximumInclusiveCheckbox.setVisible(isNumericBaseType);
    }
    collapseWhitespaceButton = factory.createButton(simpleTypeModifierComposite, "Collapse whitespace", SWT.CHECK);
    collapseWhitespaceButton.addSelectionListener(this);

    Group specificValueConstraintsGroup = factory.createGroup(facetComposite, "Specific constraint values");
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

    factory.createCLabel(compositeForButtons, "Restrict values by:");
//    useDefinedValuesButton = factory.createButton(compositeForButtons, "Only permit certain values", SWT.CHECK);
//    useDefinedValuesButton.addSelectionListener(this);

    Composite compositeForRadioButtons = factory.createFlatFormComposite(compositeForButtons);
    gridLayout = new GridLayout();
    gridLayout.marginTop = 0;
    gridLayout.marginLeft = 0;
    gridLayout.marginBottom = 0;
    gridLayout.numColumns = 1;
    compositeForRadioButtons.setLayout(gridLayout);
    useEnumerationsButton = factory.createButton(compositeForRadioButtons, "Enumerations", SWT.RADIO);
    useEnumerationsButton.addSelectionListener(this);
    usePatternsButton = factory.createButton(compositeForRadioButtons, "Patterns", SWT.RADIO);
    usePatternsButton.addSelectionListener(this);
    
    constraintsWidget = new SpecificConstraintsWidget(specificValueConstraintsGroup, factory, (input instanceof XSDFeature) ? (XSDFeature)input : null, xsdSimpleTypeDefinition, this);
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.grabExcessVerticalSpace = true;
    data.horizontalAlignment = GridData.FILL;
    data.verticalAlignment = GridData.FILL;
    constraintsWidget.getControl().setLayoutData(data);
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
      title.setText(titleString + (isReadOnly ? " - ReadOnly" : ""));
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
      XSDSimpleTypeDefinition targetST = xsdSimpleTypeDefinition;
      XSDSimpleTypeDefinition basePrimitive = xsdSimpleTypeDefinition.getPrimitiveTypeDefinition();
      if (basePrimitive != null) 
        targetST = basePrimitive;
      else
        targetST = xsdSimpleTypeDefinition.getBaseTypeDefinition();
      
      minLengthString = "";
      maxLengthString = "";
      if (targetST.getValidFacets().contains("length"))
      {
        minLengthString = "Minimum length:";
        maxLengthString = "Maximum length:";
        simpleTypeModifierGroupTitle = "Constraints on length of " + targetST.getName();
        isNumericBaseType = false;
        hasMaxMinFacets = true;
      }
      else if (targetST.getValidFacets().contains("maxInclusive"))
      {
        simpleTypeModifierGroupTitle = "Constraints on value of " + targetST.getName();
        minLengthString = "Minimum value:";
        maxLengthString = "Maximum value:";
        isNumericBaseType = true;
        hasMaxMinFacets = true;
      }
      else
      {
        simpleTypeModifierGroupTitle = "Constraints on " + (basePrimitive != null ? basePrimitive.getName() : "anyType");
      }
    }
    }
    catch(Exception e)
    {
    }
  }
  
  private void updateInput()
  {
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
        titleString = "Type: " + (anonymousTypeDefinition != null ? "(" + xsdElementDeclaration.getResolvedElementDeclaration().getName() + "Type)" : xsdSimpleTypeDefinition.getName())  + " , Base: " + xsdSimpleTypeDefinition.getPrimitiveTypeDefinition().getName();
    }
    else if (input instanceof XSDSimpleTypeDefinition)
    {
      xsdSimpleTypeDefinition = (XSDSimpleTypeDefinition) input;
      titleString = "Type: " + xsdSimpleTypeDefinition.getName() + " , Base: " + xsdSimpleTypeDefinition.getPrimitiveTypeDefinition().getName();
    }
  }

  public void refresh()
  {
    super.refresh();
    init();
    setListenerEnabled(false);
    
    collapseWhitespaceButton.setSelection(false);
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
        }
      }
    }

    if (hasMaxMinFacets)
    {
      minLengthLabel.setText(minLengthString);
      maxLengthLabel.setText(maxLengthString);

      if (!isNumericBaseType)
        refreshStringLength();
      else
        refreshValueLengths();
    }

    if (xsdSimpleTypeDefinition.getEnumerationFacets().size() > 0) 
    {
      usePatternsButton.setSelection(false);
      useEnumerationsButton.setSelection(true);
      constraintsWidget.setConstraintKind(SpecificConstraintsWidget.ENUMERATION);
      constraintsWidget.addButton.setEnabled(true);
    }
    else if (xsdSimpleTypeDefinition.getPatternFacets().size() > 0)
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
    constraintsWidget.setInput(xsdSimpleTypeDefinition);

    setListenerEnabled(true);
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
        if (minLengthValue >= 0)
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
        if (maxLength >= 0)
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
        if (length >= 0)
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

    minimumInclusiveCheckbox.removeSelectionListener(this);
    maximumInclusiveCheckbox.removeSelectionListener(this);
    try
    {
      minLengthText.setText(""); //$NON-NLS-1$
      maxLengthText.setText(""); //$NON-NLS-1$

      minimumInclusiveCheckbox.setSelection(false);
      minimumInclusiveCheckbox.setEnabled(false);
      if (minFacet != null)
      {
        if (minFacet.getElement().getNodeName().equals(XSDConstants.MINEXCLUSIVE_ELEMENT_TAG) ||
            minFacet.getElement().getNodeName().equals(XSDConstants.MININCLUSIVE_ELEMENT_TAG))
        {
          minLengthText.setText(minFacet.getLexicalValue());
          minimumInclusiveCheckbox.setSelection(minFacet.isInclusive());
          minimumInclusiveCheckbox.setEnabled(true);
        }
      }

      maximumInclusiveCheckbox.setSelection(false);
      maximumInclusiveCheckbox.setEnabled(false);
      if (maxFacet != null)
      {
        if (maxFacet.getElement().getNodeName().equals(XSDConstants.MAXEXCLUSIVE_ELEMENT_TAG) ||
            maxFacet.getElement().getNodeName().equals(XSDConstants.MAXINCLUSIVE_ELEMENT_TAG))
        {
          maxLengthText.setText(maxFacet.getLexicalValue());
          maximumInclusiveCheckbox.setSelection(maxFacet.isInclusive());
          maximumInclusiveCheckbox.setEnabled(true);
        }
      }
    }
    finally
    {
      minimumInclusiveCheckbox.addSelectionListener(this);
      maximumInclusiveCheckbox.addSelectionListener(this);
    }
  }

  protected void doHandleEvent(Event event)
  {
    super.doHandleEvent(event);
    Command command = null;
    boolean doUpdateMax = false, doUpdateMin = false;
    
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
            if (xsdSchema.getSchemaForSchema().resolveSimpleTypeDefinition("double").equals(xsdSimpleTypeDefinition) ||
                xsdSchema.getSchemaForSchema().resolveSimpleTypeDefinition("float").equals(xsdSimpleTypeDefinition) ||
                xsdSchema.getSchemaForSchema().resolveSimpleTypeDefinition("decimal").equals(xsdSimpleTypeDefinition))
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
            if (xsdSchema.getSchemaForSchema().resolveSimpleTypeDefinition("double").equals(xsdSimpleTypeDefinition) ||
                xsdSchema.getSchemaForSchema().resolveSimpleTypeDefinition("float").equals(xsdSimpleTypeDefinition) ||
                xsdSchema.getSchemaForSchema().resolveSimpleTypeDefinition("decimal").equals(xsdSimpleTypeDefinition))
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
       
          changeToAnonymousCommand = new ChangeToLocalSimpleTypeCommand("Constrain length", (XSDFeature)input);
          changeToAnonymousCommand.setAnonymousSimpleType(anonymousSimpleType);
          compoundCommand.add(changeToAnonymousCommand);
        }

        if (!isNumericBaseType)
        {
          UpdateStringLengthFacetCommand updateCommand = new UpdateStringLengthFacetCommand("", anonymousSimpleType);
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
          UpdateNumericBoundsFacetCommand updateCommand = new UpdateNumericBoundsFacetCommand("Update bounds", anonymousSimpleType, minimumInclusiveCheckbox.getSelection(), maximumInclusiveCheckbox.getSelection());
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
          UpdateStringLengthFacetCommand updateCommand = new UpdateStringLengthFacetCommand("", xsdSimpleTypeDefinition);
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
          UpdateNumericBoundsFacetCommand updateCommand = new UpdateNumericBoundsFacetCommand("Update bounds", xsdSimpleTypeDefinition, minimumInclusiveCheckbox.getSelection(), maximumInclusiveCheckbox.getSelection());
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
        UpdateStringLengthFacetCommand updateCommand = new UpdateStringLengthFacetCommand("", xsdSimpleTypeDefinition);
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
        UpdateNumericBoundsFacetCommand updateCommand = new UpdateNumericBoundsFacetCommand("Update bounds", xsdSimpleTypeDefinition, minimumInclusiveCheckbox.getSelection(), maximumInclusiveCheckbox.getSelection());
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
           
             ChangeToLocalSimpleTypeCommand changeToAnonymousCommand = new ChangeToLocalSimpleTypeCommand("Constrain length", (XSDFeature)input);
             changeToAnonymousCommand.setAnonymousSimpleType(anonymousSimpleType);
             compoundCommand.add(changeToAnonymousCommand);
           }
  
           UpdateXSDWhiteSpaceFacetCommand whiteSpaceCommand = new UpdateXSDWhiteSpaceFacetCommand("Collapse whitespace", anonymousSimpleType, collapseWhitespaceButton.getSelection());
           compoundCommand.add(whiteSpaceCommand);
            
           getCommandStack().execute(compoundCommand);
        }
         setInput(getPart(), getSelection());
      }
      else
      {
        UpdateXSDWhiteSpaceFacetCommand whiteSpaceCommand = new UpdateXSDWhiteSpaceFacetCommand("Collapse whitespace", xsdSimpleTypeDefinition, collapseWhitespaceButton.getSelection());
        getCommandStack().execute(whiteSpaceCommand);
      }
    }
    else if (e.widget == minimumInclusiveCheckbox)
    {
      String minValue = minLengthText.getText().trim();
      if (minValue.length() == 0) minValue = null;

      UpdateNumericBoundsFacetCommand updateCommand = new UpdateNumericBoundsFacetCommand("Update bounds", xsdSimpleTypeDefinition, minimumInclusiveCheckbox.getSelection(), maximumInclusiveCheckbox.getSelection());
      updateCommand.setMin(minValue);
      
      if (minValue != null)
        getCommandStack().execute(updateCommand);
    }
    else if (e.widget == maximumInclusiveCheckbox)
    {
      String maxValue = maxLengthText.getText().trim();
      if (maxValue.length() == 0) maxValue = null;
      UpdateNumericBoundsFacetCommand updateCommand = new UpdateNumericBoundsFacetCommand("Update bounds", xsdSimpleTypeDefinition, minimumInclusiveCheckbox.getSelection(), maximumInclusiveCheckbox.getSelection());
      updateCommand.setMax(maxValue);
      if (maxValue != null)
        getCommandStack().execute(updateCommand);
    }
    else if (e.widget == useEnumerationsButton)
    {
      constraintsWidget.addButton.setEnabled(true);
      if (isListenerEnabled())
        constraintsWidget.setConstraintKind(SpecificConstraintsWidget.ENUMERATION);
    }
    else if (e.widget == usePatternsButton)
    {
      constraintsWidget.addButton.setEnabled(false);
      if (isListenerEnabled())
        constraintsWidget.setConstraintKind(SpecificConstraintsWidget.PATTERN);
    }
  }
}
