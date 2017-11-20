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

//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//import org.eclipse.jface.viewers.CellEditor;
//import org.eclipse.jface.viewers.ColumnWeightData;
//import org.eclipse.jface.viewers.ICellModifier;
//import org.eclipse.jface.viewers.ISelectionChangedListener;
//import org.eclipse.jface.viewers.IStructuredContentProvider;
//import org.eclipse.jface.viewers.ITableLabelProvider;
//import org.eclipse.jface.viewers.LabelProvider;
//import org.eclipse.jface.viewers.SelectionChangedEvent;
//import org.eclipse.jface.viewers.StructuredSelection;
//import org.eclipse.jface.viewers.TableLayout;
//import org.eclipse.jface.viewers.TextCellEditor;
//import org.eclipse.jface.viewers.Viewer;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.events.MouseEvent;
//import org.eclipse.swt.events.MouseTrackAdapter;
//import org.eclipse.swt.graphics.Image;
//import org.eclipse.swt.graphics.Point;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Table;
//import org.eclipse.swt.widgets.TableColumn;
//import org.eclipse.swt.widgets.TableItem;
//import org.eclipse.wst.common.ui.internal.viewers.NavigableTableViewer;
//import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
//import org.eclipse.wst.xsd.ui.internal.actions.DOMAttribute;
//import org.eclipse.wst.xsd.ui.internal.properties.XSDComboBoxPropertyDescriptor;
//import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
//import org.eclipse.xsd.XSDConstrainingFacet;
//import org.eclipse.xsd.XSDFactory;
//import org.eclipse.xsd.XSDMaxExclusiveFacet;
//import org.eclipse.xsd.XSDMaxFacet;
//import org.eclipse.xsd.XSDMaxInclusiveFacet;
//import org.eclipse.xsd.XSDMinExclusiveFacet;
//import org.eclipse.xsd.XSDMinFacet;
//import org.eclipse.xsd.XSDMinInclusiveFacet;
//import org.eclipse.xsd.XSDSimpleTypeDefinition;
//import org.eclipse.xsd.util.XSDConstants;
//import org.eclipse.xsd.util.XSDSchemaBuildingTools;
//import org.w3c.dom.Element;

public class FacetViewer //extends NavigableTableViewer implements ICellModifier
{
//  public static final String FACET_NAME = XSDEditorPlugin.getXSDString("_UI_FACET_NAME"); // "Name";
//  public static final String FACET_VALUE = XSDEditorPlugin.getXSDString("_UI_FACET_VALUE"); // "Value";
//  public static final String FACET_OTHER = XSDEditorPlugin.getXSDString("_UI_FACET_FIXED"); // "Fixed";
//
//  protected FacetsTableLabelProvider facetsTableLabelProvider = new FacetsTableLabelProvider();
//  protected FacetsTableContentProvider facetsTableContentProvider = new FacetsTableContentProvider();
//  protected String[] columnProperties = { FACET_NAME, FACET_VALUE, FACET_OTHER };
//  protected CellEditor[] cellEditors; // these cellEditors are used when
//                                      // non-whitespace facet is selected
//  protected CellEditor[] altCellEditors; // these cellEditors are used when
//                                          // whitespace facet is selected
//
//  protected String[] whiteSpaceValues = new String[] { "", "preserve", "replace", "collapse" };
//  protected String[] trueFalseValues = new String[] { "", "false", "true" };
//
//  /**
//   * @param parent
//   */
//  public FacetViewer(Composite parent)
//  {
//    super(new Table(parent, SWT.FULL_SELECTION | SWT.SINGLE));
//
//    getTable().setLinesVisible(true);
//    getTable().setHeaderVisible(true);
//
//    addSelectionChangedListener(new SelectionChangedListener());
//    getTable().addMouseTrackListener(new MyMouseTrackListener());
//
//    setContentProvider(facetsTableContentProvider);
//    setLabelProvider(facetsTableLabelProvider);
//    setColumnProperties(columnProperties);
//
//    setCellModifier(this);
//
//    for (int i = 0; i < 3; i++)
//    {
//      TableColumn column = new TableColumn(getTable(), SWT.NONE, i);
//      column.setText(columnProperties[i]);
//      column.setAlignment(SWT.LEFT);
//      column.setResizable(true);
//    }
//
//    cellEditors = new CellEditor[3];
//    altCellEditors = new CellEditor[3];
//
//    TableLayout layout = new TableLayout();
//    ColumnWeightData data = new ColumnWeightData(60, 80, true);
//    layout.addColumnData(data);
//    cellEditors[0] = null;
//
//    ColumnWeightData data2 = new ColumnWeightData(120, 80, true);
//    layout.addColumnData(data2);
//
//    cellEditors[1] = new TextCellEditor(getTable());
//    XSDComboBoxPropertyDescriptor pd = new XSDComboBoxPropertyDescriptor("combo", "whitespace", whiteSpaceValues);
//    altCellEditors[1] = pd.createPropertyEditor(getTable());
//
//    ColumnWeightData data3 = new ColumnWeightData(60, 60, true);
//    layout.addColumnData(data3);
//
//    XSDComboBoxPropertyDescriptor pd2 = new XSDComboBoxPropertyDescriptor("combo", "other", trueFalseValues);
//    cellEditors[2] = pd2.createPropertyEditor(getTable());
//    altCellEditors[2] = pd2.createPropertyEditor(getTable());
//
//    getTable().setLayout(layout);
//    setCellEditors(cellEditors);
//
//  }
//
//  /*
//   * (non-Javadoc)
//   * 
//   * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object,
//   *      java.lang.String)
//   */
//  public boolean canModify(Object element, String property)
//  {
//    return property.equals(FACET_VALUE) || property.equals(FACET_OTHER);
//  }
//
//  /*
//   * (non-Javadoc)
//   * 
//   * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object,
//   *      java.lang.String)
//   */
//  public Object getValue(Object element, String property)
//  {
//    int column = 0;
//    if (property.equals(columnProperties[0]))
//    {
//      column = 0;
//    }
//    else if (property.equals(columnProperties[1]))
//    {
//      column = 1;
//    }
//    else if (property.equals(columnProperties[2]))
//    {
//      column = 2;
//    }
//
//    return facetsTableLabelProvider.getColumnText(element, column);
//  }
//
//  /*
//   * (non-Javadoc)
//   * 
//   * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object,
//   *      java.lang.String, java.lang.Object)
//   */
//  public void modify(Object element, String property, Object value)
//  {
//    XSDSimpleTypeDefinition xsdSimpleType = (XSDSimpleTypeDefinition) getInput();
//    TableItem item = (TableItem) element;
//    if (item != null)
//    {
//      Object o = item.getData();
//      if (o != null)
//      {
//        if (o instanceof String)
//        {
//          String facet = (String) o;
//
//          Element simpleTypeElement = xsdSimpleType.getElement();
//          XSDDOMHelper xsdDOMHelper = new XSDDOMHelper();
//          Element derivedByElement = xsdDOMHelper.getDerivedByElement(simpleTypeElement);
//
//          String prefix = simpleTypeElement.getPrefix();
//          prefix = (prefix == null) ? "" : (prefix + ":");
//
//          Element childNodeElement = null;
//          DOMAttribute valueAttr = null;
//
//          XSDConstrainingFacet targetFacet = getXSDConstrainingFacet(facet);
//
//          String newValue = "";
//          if (value != null && value instanceof String)
//          {
//            newValue = (String) value;
//          }
//
//          if (property.equals(columnProperties[1]))
//          {
//            if (targetFacet == null && newValue.length() > 0)
//            {
//              targetFacet = createFacet(facet);
//              childNodeElement = (derivedByElement.getOwnerDocument()).createElementNS(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, prefix + facet);
//              valueAttr = new DOMAttribute(XSDConstants.VALUE_ATTRIBUTE, newValue);
//              childNodeElement.setAttribute(valueAttr.getName(), valueAttr.getValue());
//              // add and format child
//              derivedByElement.appendChild(childNodeElement);
//              targetFacet.setElement(childNodeElement);
//              XSDDOMHelper.formatChild(childNodeElement);
//
//              // XSDSchemaHelper.updateElement(xsdSimpleType);
//            }
//            if (targetFacet == null)
//            {
//              return;
//            }
//
//            if (newValue.length() > 0)
//            {
//              targetFacet.setLexicalValue(newValue);
//
//              if (targetFacet instanceof XSDMaxFacet || targetFacet instanceof XSDMinFacet)
//              {
//                if (targetFacet instanceof XSDMaxFacet)
//                {
//                  if (targetFacet instanceof XSDMaxExclusiveFacet)
//                  {
//                    XSDMaxInclusiveFacet xsdMaxInclusiveFacet = xsdSimpleType.getMaxInclusiveFacet();
//                    if (xsdMaxInclusiveFacet != null)
//                    {
//                      Element xsdMaxInclusiveFacetElement = xsdMaxInclusiveFacet.getElement();
//                      XSDDOMHelper.removeNodeAndWhitespace(xsdMaxInclusiveFacetElement);
//                    }
//                  }
//                  else if (targetFacet instanceof XSDMaxInclusiveFacet)
//                  {
//                    XSDMaxExclusiveFacet xsdMaxExclusiveFacet = xsdSimpleType.getMaxExclusiveFacet();
//                    if (xsdMaxExclusiveFacet != null)
//                    {
//                      Element xsdMaxExclusiveFacetElement = xsdMaxExclusiveFacet.getElement();
//                      XSDDOMHelper.removeNodeAndWhitespace(xsdMaxExclusiveFacetElement);
//                    }
//                  }
//                }
//                else if (targetFacet instanceof XSDMinFacet)
//                {
//                  if (targetFacet instanceof XSDMinExclusiveFacet)
//                  {
//                    XSDMinInclusiveFacet xsdMinInclusiveFacet = xsdSimpleType.getMinInclusiveFacet();
//                    if (xsdMinInclusiveFacet != null)
//                    {
//                      Element xsdMinInclusiveFacetElement = xsdMinInclusiveFacet.getElement();
//                      XSDDOMHelper.removeNodeAndWhitespace(xsdMinInclusiveFacetElement);
//                    }
//                  }
//                  else if (targetFacet instanceof XSDMinInclusiveFacet)
//                  {
//                    XSDMinExclusiveFacet xsdMinExclusiveFacet = xsdSimpleType.getMinExclusiveFacet();
//                    if (xsdMinExclusiveFacet != null)
//                    {
//                      Element xsdMinExclusiveFacetElement = xsdMinExclusiveFacet.getElement();
//                      XSDDOMHelper.removeNodeAndWhitespace(xsdMinExclusiveFacetElement);
//                    }
//                  }
//                }
//              }
//            }
//            else
//            // newValue.length == 0
//            {
//              Element targetFacetElement = targetFacet.getElement();
//              XSDDOMHelper.removeNodeAndWhitespace(targetFacetElement);
//            }
//          }
//          else if (property.equals(columnProperties[2]))
//          {
//            if (targetFacet != null)
//            {
//              if (newValue.length() > 0)
//              {
//                targetFacet.getElement().setAttribute(XSDConstants.FIXED_ATTRIBUTE, newValue);
//              }
//              else
//              {
//                targetFacet.getElement().removeAttribute(XSDConstants.FIXED_ATTRIBUTE);
//              }
//            }
//          }
//          xsdSimpleType.setElement(simpleTypeElement);
//          // xsdSimpleType.updateElement();
//          refresh();
//        }
//      }
//    }
//  }
//
//  private XSDConstrainingFacet getXSDConstrainingFacet(String facetString)
//  {
//    XSDSimpleTypeDefinition xsdSimpleType = (XSDSimpleTypeDefinition) getInput();
//    List list = xsdSimpleType.getFacetContents();
//    if (list == null)
//    {
//      return null;
//    }
//    Iterator iter = list.iterator();
//    XSDConstrainingFacet targetFacet = null;
//
//    while (iter.hasNext())
//    {
//      XSDConstrainingFacet xsdConstrainingFacet = (XSDConstrainingFacet) iter.next();
//      if (xsdConstrainingFacet.getFacetName().equals(facetString))
//      {
//        targetFacet = xsdConstrainingFacet;
//        break;
//      }
//    }
//    return targetFacet;
//  }
//
//  private XSDConstrainingFacet createFacet(String facet)
//  {
//    XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();
//    XSDConstrainingFacet xsdFacet = null;
//    if (facet.equals("length"))
//    {
//      xsdFacet = factory.createXSDLengthFacet();
//    }
//    else if (facet.equals("minLength"))
//    {
//      xsdFacet = factory.createXSDMinLengthFacet();
//    }
//    else if (facet.equals("maxLength"))
//    {
//      xsdFacet = factory.createXSDMaxLengthFacet();
//    }
//    else if (facet.equals("minInclusive"))
//    {
//      xsdFacet = factory.createXSDMinInclusiveFacet();
//    }
//    else if (facet.equals("minExclusive"))
//    {
//      xsdFacet = factory.createXSDMinExclusiveFacet();
//    }
//    else if (facet.equals("maxInclusive"))
//    {
//      xsdFacet = factory.createXSDMaxInclusiveFacet();
//    }
//    else if (facet.equals("maxExclusive"))
//    {
//      xsdFacet = factory.createXSDMaxExclusiveFacet();
//    }
//
//    else if (facet.equals("totalDigits"))
//    {
//      xsdFacet = factory.createXSDTotalDigitsFacet();
//    }
//    else if (facet.equals("fractionDigits"))
//    {
//      xsdFacet = factory.createXSDFractionDigitsFacet();
//    }
//    else if (facet.equals("whiteSpace"))
//    {
//      xsdFacet = factory.createXSDWhiteSpaceFacet();
//    }
//    return xsdFacet;
//  }
//
//  /**
//   * Get the tooltip for the facet
//   */
//  public String getToolTip(String facet)
//  {
//    String key = "";
//    if (facet.equals("length"))
//    {
//      key = "_UI_TOOLTIP_LENGTH";
//    }
//    else if (facet.equals("minLength"))
//    {
//      key = "_UI_TOOLTIP_MIN_LEN";
//    }
//    else if (facet.equals("maxLength"))
//    {
//      key = "_UI_TOOLTIP_MAX_LEN";
//    }
//
//    else if (facet.equals("minInclusive"))
//    {
//      key = "_UI_TOOLTIP_MIN_INCLUSIVE";
//    }
//    else if (facet.equals("minExclusive"))
//    {
//      key = "_UI_TOOLTIP_MIN_EXCLUSIVE";
//    }
//
//    else if (facet.equals("maxInclusive"))
//    {
//      key = "_UI_TOOLTIP_MAX_INCLUSIVE";
//    }
//    else if (facet.equals("maxExclusive"))
//    {
//      key = "_UI_TOOLTIP_MAX_EXCLUSIVE";
//    }
//
//    else if (facet.equals("totalDigits"))
//    {
//      key = "_UI_TOOLTIP_TOTAL_DIGITS";
//    }
//    else if (facet.equals("fractionDigits"))
//    {
//      key = "_UI_TOOLTIP_FRACTION_DIGITS";
//    }
//
//    else if (facet.equals("whiteSpace"))
//    {
//      key = "_UI_TOOLTIP_WHITE_SPACE";
//    }
//
//    return (key != null) ? XSDEditorPlugin.getXSDString(key) : "";
//  }
//
//  /**
//   * This listener detects which row is selected and add a tool tip for that row
//   */
//  public class MyMouseTrackListener extends MouseTrackAdapter
//  {
//    public void mouseHover(MouseEvent e)
//    {
//      TableItem item = getTable().getItem(new Point(e.x, e.y));
//      if (item != null)
//      {
//        Object o = item.getData();
//        if (o != null)
//        {
//          String facetName = (String) o;
//          getTable().setToolTipText(getToolTip(facetName));
//        }
//      }
//    }
//  }
//
//  /**
//   * Based on the selection, detects if it is a white space or not, and add the
//   * corresponding cell editors
//   */
//  public class SelectionChangedListener implements ISelectionChangedListener
//  {
//    public void selectionChanged(SelectionChangedEvent event)
//    {
//      Object selection = event.getSelection();
//      if (selection instanceof StructuredSelection)
//      {
//        Object o = ((StructuredSelection) selection).getFirstElement();
//        if (o != null)
//        {
//          String facet = (String) o;
//          if (facet.equals("whiteSpace"))
//          {
//            setCellEditors(altCellEditors);
//          }
//          else
//          {
//            setCellEditors(cellEditors);
//          }
//        }
//      }
//    }
//  }
//
//  class FacetsTableContentProvider implements IStructuredContentProvider
//  {
//    protected String facet;
//
//    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
//    {
//    }
//
//    public java.lang.Object[] getElements(java.lang.Object inputElement)
//    {
//      List v = new ArrayList();
//      XSDSimpleTypeDefinition inputXSDSimpleType = (XSDSimpleTypeDefinition) inputElement;
//      XSDSimpleTypeDefinition base = inputXSDSimpleType.getPrimitiveTypeDefinition();
//
//      if (base != null)
//      {
//        Iterator validFacets = inputXSDSimpleType.getValidFacets().iterator();
//        while (validFacets.hasNext())
//        {
//          String aValidFacet = (String) validFacets.next();
//          if (!(aValidFacet.equals("pattern") || aValidFacet.equals("enumeration")))
//          {
//            v.add(aValidFacet);
//          }
//        }
//      }
//      return v.toArray();
//    }
//
//    public void dispose()
//    {
//    }
//  }
//
//  class FacetsTableLabelProvider extends LabelProvider implements ITableLabelProvider
//  {
//    public Image getColumnImage(Object element, int columnIndex)
//    {
//      return null;
//    }
//
//    public String getColumnText(Object element, int columnIndex)
//    {
//      if (element instanceof String)
//      {
//        String value = null;
//        XSDConstrainingFacet targetFacet = getXSDConstrainingFacet((String) element);
//        switch (columnIndex)
//        {
//        case 0:
//        {
//          value = (String) element;
//          break;
//        }
//        case 1:
//        {
//          if (targetFacet == null)
//          {
//            value = "";
//          }
//          else
//          {
//            value = targetFacet.getLexicalValue();
//          }
//
//          break;
//        }
//        case 2:
//        {
//          if (targetFacet == null)
//          {
//            value = "";
//          }
//          else
//          {
//            Element elem = targetFacet.getElement();
//            value = elem.getAttribute(XSDConstants.FIXED_ATTRIBUTE);
//            if (value == null)
//              value = "";
//          }
//        }
//        }
//        return value;
//      }
//      return "";
//    }
//  }
}
