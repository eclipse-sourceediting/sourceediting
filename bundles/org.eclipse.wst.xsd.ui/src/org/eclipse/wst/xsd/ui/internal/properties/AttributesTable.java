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

//import java.text.Collator;
//import java.util.Comparator;
//import java.util.List;
//
//import org.eclipse.jface.viewers.CellEditor;
//import org.eclipse.jface.viewers.ColumnPixelData;
//import org.eclipse.jface.viewers.ICellModifier;
//import org.eclipse.jface.viewers.ILabelProvider;
//import org.eclipse.jface.viewers.IStructuredContentProvider;
//import org.eclipse.jface.viewers.ITableLabelProvider;
//import org.eclipse.jface.viewers.LabelProvider;
//import org.eclipse.jface.viewers.TableLayout;
//import org.eclipse.jface.viewers.TableViewer;
//import org.eclipse.jface.viewers.TextCellEditor;
//import org.eclipse.jface.viewers.Viewer;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.events.MouseAdapter;
//import org.eclipse.swt.events.MouseEvent;
//import org.eclipse.swt.graphics.Image;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Control;
//import org.eclipse.swt.widgets.Table;
//import org.eclipse.swt.widgets.TableColumn;
//import org.eclipse.swt.widgets.TableItem;
//import org.eclipse.ui.IEditorPart;
//import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
//import org.eclipse.ui.views.properties.IPropertyDescriptor;
//import org.eclipse.ui.views.properties.IPropertySource;
//import org.w3c.dom.Element;

public class AttributesTable // extends TableViewer implements ICellModifier
{
//	protected static final String PROPERTY = "property"; //$NON-NLS-1$
//	protected static final String VALUE = "value"; //$NON-NLS-1$
//
//	protected IEditorPart editorPart;
//	protected String[] columnProperties = {PROPERTY, VALUE};
//	protected PropertyTableProvider tableProvider = new PropertyTableProvider(this);
//	protected CellEditor cellEditor;
//	//protected StringComboBoxCellEditor comboCellEditor;
//	protected IPropertySource propertySource;
//
//	public AttributesTable(IEditorPart editorPart, Composite parent)
//	{
//		super(new Table(parent, SWT.FULL_SELECTION | SWT.MULTI | SWT.FLAT | SWT.H_SCROLL | SWT.V_SCROLL));   
//				 this.editorPart = editorPart;               
//		getTable().setLinesVisible(true);
//		getTable().setHeaderVisible(true);  
//    
//		setContentProvider(tableProvider);
//		setLabelProvider(tableProvider);
//		setColumnProperties(columnProperties);
//
//		for (int i = 0; i < columnProperties.length; i++)
//		{
//			TableColumn column = new TableColumn(getTable(), SWT.NONE, i);
//			column.setText(columnProperties[i]);
//			column.setAlignment(SWT.LEFT);
//		}      
//
////		TableLayout layout = new TableLayout(); 
////		ColumnWeightData data = new ColumnWeightData(40, 40, true);
////		layout.addColumnData(data);
////    
////		ColumnWeightData data2 = new ColumnWeightData(80, 80, true);
////		layout.addColumnData(data2);                       
//
//		TableLayout layout = new TableLayout();
//		layout.addColumnData(new ColumnPixelData(130,true));
//		layout.addColumnData(new ColumnPixelData(130,true));
//		getTable().setLayout(layout);
//		
//         
//		cellEditor = new TextCellEditor(getTable());                            
//		resetCellEditors();           
//
//		setCellModifier(this);    
//	}  
//
//	public void setPropertySource(IPropertySource propertySource)
//	{
//		this.propertySource = propertySource;
//	}
//
//  /* (non-Javadoc)
//   * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object, java.lang.String)
//   */
//  public boolean canModify(Object element, String property)
//  {
//		return property.equals(VALUE);
//  }
//  
//  /* (non-Javadoc)
//   * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object, java.lang.String)
//   */
//  public Object getValue(Object element, String property)
//  {
//		int column = 0;
//		if (property.equals(columnProperties[0]))
//		{
//			column = 0;
//		}
//		else if (property.equals(columnProperties[1]))
//		{
//			column = 1;
//		}    
//		return tableProvider.getColumnValue(element, column);
// }
//
//  /* (non-Javadoc)
//   * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object, java.lang.String, java.lang.Object)
//   */
//  public void modify(Object element, String property, Object value)
//  {
//		TableItem item = (TableItem)element;
//  	IPropertyDescriptor propertyDescriptor = (IPropertyDescriptor)item.getData();
//              
//	// if the new value is the same as the old value, the user has only clicked
//	// on the cell in the course of 'browsing' ... so don't edit the value
//	  Object oldValue = getValue(propertyDescriptor, property);       
//	  if (value != null && !value.equals(oldValue)) 
//	  {      
//				 // we assume the value is empty that the attribute should be removed
//				 // todo... we probably need to look at this in more detail
//				 if (value instanceof String && ((String)value).length() == 0)
//				 {
//								value = null;
//				 }
//				 propertySource.setPropertyValue(propertyDescriptor.getId(), value);    
//	  }                       
//  }
//
//      
//	protected void hookControl(Control control) {
//	  // we need to hook up our own mouse listener first
//				 // so that we can update the cellEditors before
//				 // the 'internal' listener tries to get hold of them
//			 Table tableControl = (Table)control;
//				 tableControl.addMouseListener(new MouseAdapter() {
//								public void mouseDown(MouseEvent e) {
//								  System.out.println("Mouse down");
//										   updateCellEditors();
//								}
//				 });
//		  super.hookControl(control);
//	}                                
//
//	protected void updateCellEditors()
//	{
//		CellEditor[] cellEditors = new CellEditor[2];
//		cellEditors[0] = cellEditor;
//		cellEditors[1] = cellEditor;
//
//		Element element = (Element)getInput();
//              
//		IPropertyDescriptor[] propertyDescriptors = propertySource.getPropertyDescriptors();
//		int index = getTable().getSelectionIndex();
//		if (index >= 0 && index < propertyDescriptors.length)
//		{
//			CellEditor[] oldCellEditors = getCellEditors();
//			CellEditor oldCellEditor = (oldCellEditors.length > 1) ? oldCellEditors[1] : null;
//			if (oldCellEditor != null && oldCellEditor != cellEditor)
//			{
//			 oldCellEditor.deactivate();
//			 oldCellEditor.dispose();
//			}
//			cellEditors[1] = propertyDescriptors[index].createPropertyEditor(getTable());     
//		}  
//		setCellEditors(cellEditors);     
//
////		IPropertyDescriptor[] propertyDescriptors = propertySource.getPropertyDescriptors();
////
//// 		int index = getTable().getSelectionIndex();
//// 		//cellEditor.dispose();
////
////		if (index >= 0 && index < propertyDescriptors.length)
////		{
////      cellEditor = propertyDescriptors[index].createPropertyEditor(getTable());
////			Control control = cellEditor.getControl();
////			if (control == null) {
////				cellEditor.deactivate();
////				cellEditor = null;
////				return;
////			}
////			setCellEditors(new CellEditor[] {null, cellEditor});
////			cellEditor.activate();
////			cellEditor.setFocus();
////		}
//	}
//                 
//
//	public String[] getStringArray(List list)
//	{
//		String[] result = new String[list.size()];
//		for (int i = 0; i < result.length; i++)
//		{
//			result[i] = (String)list.get(i);
//		}
//		return result;
//	}
//                                   
//	protected void resetCellEditors()
//	{
//			 CellEditor[] cellEditors = new CellEditor[2];
//			 cellEditors[0] = null;
//			 cellEditors[1] = cellEditor;
//			 setCellEditors(cellEditors);  
//	}
//
//
//	class PropertyTableProvider extends LabelProvider implements ITableLabelProvider, IStructuredContentProvider
//	{                  
//		protected TableViewer viewer;
//
//		PropertyTableProvider(TableViewer viewer)
//		{
//			this.viewer = viewer;
//		}
//		
//		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)  
//		{
////		 resetCellEditors();                         
////			if (newInput instanceof XMLElement)
////			{      
////					 propertySource = new ExtensiblePropertySource(editorPart, (XMLElement)newInput);
////			} 
////			if (newInput instanceof Element)
////			{
////				if (XSDDOMHelper.inputEquals(newInput, XSDConstants.ELEMENT_ELEMENT_TAG, false))
////				{  
////				  propertySource = new ElementPropertySource((Element)newInput, viewer);
////				}
////				else
////				{  
////  				 propertySource = new ReadOnlyPropertySource(editorPart, (Element)newInput);
////				}
////			}
////			else
////			{
////						 propertySource = null;
////			}          
//		}
//
//		public Image getColumnImage(Object element, int columnIndex)
//		{  
//			return null;
//		}      
//
//		public Object getColumnValue(Object o, int columnIndex)  
//		{ 
//			IPropertyDescriptor propertyDescriptor = (IPropertyDescriptor)o;
//			if (columnIndex == 0)
//			{
//			  return propertyDescriptor.getId();
//			}
//			else
//			{  
//			  return propertySource.getPropertyValue(propertyDescriptor.getId());
//			}
//		}
//		  
//		public String getColumnText(Object o, int columnIndex)  
//		{ 
//			IPropertyDescriptor propertyDescriptor = (IPropertyDescriptor)o;
//			// (columnIndex == 1 && propertyDescriptor instanceof XSDComboBoxPropertyDescriptor)
//			if ((columnIndex == 1 && propertyDescriptor instanceof OptionsComboBoxPropertyDescriptor) ||
//			   (columnIndex == 1 && propertyDescriptor instanceof ComboBoxPropertyDescriptor))
//			{
//			  ILabelProvider lp = propertyDescriptor.getLabelProvider();
//			  if (lp != null)
//		  	{
//			    return lp.getText(propertyDescriptor.getId());
//		  	}
//			}
//			
//			Object id = propertyDescriptor.getId();
//      String attribute = "";
//		  if (id != null && attribute instanceof String)
//      {
//        attribute = (String)id;
//      }
//      Object value = propertySource.getPropertyValue(attribute);
//      String attributeValue = "";
//      if (value != null)
//      {
//        attributeValue = (String)value;
//      }
//
//      return (columnIndex == 0) ? attribute : attributeValue;
//			
////			  return (columnIndex == 0) ? propertyDescriptor.getId().toString() : propertySource.getPropertyValue(propertyDescriptor.getId()).toString();
//		}  
//
//		public Object[] getElements(Object o)
//		{         
//						Object[] result = propertySource.getPropertyDescriptors();
//						// For some strange reson the ViewerSorter doesn't seem to be working for this table
//						// As a workaround we sort them in this method before returning them to the viewer
////						if (result.length > 0)
////						{
////									 Arrays.sort(result, new InternalComparator());
////						}
//						return result;
//		}
//	}
//
//	class InternalComparator implements Comparator
//	{
//			 public int compare(Object e1, Object e2) 
//			 {
//							IPropertyDescriptor p1 = (IPropertyDescriptor)e1;
//							IPropertyDescriptor p2 = (IPropertyDescriptor)e2;
//							String p1Name = p1.getDisplayName();
//							String p2Name = p2.getDisplayName();
//							return Collator.getInstance().compare(p1.getDisplayName(), p2.getDisplayName());
//			 }         
//	}
//
}
