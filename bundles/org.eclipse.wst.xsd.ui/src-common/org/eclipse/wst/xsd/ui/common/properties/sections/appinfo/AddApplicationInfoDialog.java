/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.common.properties.sections.appinfo;

import java.util.ArrayList;
import java.util.List;

import org.apache.xerces.dom.DocumentImpl;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.wst.xsd.contentmodel.internal.XSDImpl;
import org.eclipse.wst.xsd.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AddApplicationInfoDialog extends SelectionDialog implements ISelectionChangedListener, SelectionListener
{
  // when this dialog is created it needs to know which registry it is going to use,the WSDL or the XSD,
  // hence we need this field
  ApplicationInformationPropertiesRegistry registry;
  
  protected static final Image DEFAULT_ELEMENT_ICON = XSDEditorPlugin.getXSDImage("icons/XSDElement.gif");
  protected static final Image DEFAULT_ATTRIBUTE_ICON = XSDEditorPlugin.getXSDImage("icons/XSDAttribute.gif");

  /** A temporary Document in which we create temporary DOM element for each element in the
   * Element view. (required by LabelProvider)  */ 
  protected static Document tempDoc = new DocumentImpl();
  
  Button addButton, removeButton;

  public AddApplicationInfoDialog(Shell parent, ApplicationInformationPropertiesRegistry registry)
  {
    super(parent);
    setTitle("Add Extension Components");
    setShellStyle(SWT.APPLICATION_MODAL | SWT.RESIZE | SWT.CLOSE);
    
    this.registry = registry;
  }

  private IStructuredContentProvider categoryContentProvider, elementContentProvider;
  private ILabelProvider categoryLabelProvider, elementLabelProvider;
  private List fInput;

  private TableViewer categoryTableViewer, elementTableViewer;
  private ArrayList existingNames;

  public void setInput(List input)
  {
    this.fInput = input;
  }

  protected Control createDialogArea(Composite container)
  {
    Composite parent = (Composite) super.createDialogArea(container);

    Composite categoryComposite = new Composite(parent, SWT.NONE);
    GridLayout gl = new GridLayout();
    gl.numColumns = 2;
    gl.marginHeight = 0;
    gl.marginWidth = 0;
    GridData data = new GridData(GridData.FILL_BOTH);
    categoryComposite.setLayoutData(data);
    categoryComposite.setLayout(gl);

    Label label = new Label(categoryComposite, SWT.LEFT);
    label.setText("Extension Categories:");

    new Label(categoryComposite, SWT.NONE);

    categoryTableViewer = new TableViewer(categoryComposite, getTableStyle());
    categoryTableViewer.setContentProvider(new CategoryContentProvider());
    categoryTableViewer.setLabelProvider(new CategoryLabelProvider());
    categoryTableViewer.setInput(fInput);
    categoryTableViewer.addSelectionChangedListener(this);

    GridData gd = new GridData(GridData.FILL_BOTH);
    Table table = categoryTableViewer.getTable();
    table.setLayoutData(gd);
    table.setFont(container.getFont());

    Composite buttonComposite = new Composite(categoryComposite, SWT.NONE);
    gl = new GridLayout();
    gl.makeColumnsEqualWidth = true;
    gl.numColumns = 1;
    data = new GridData();
    data.horizontalAlignment = SWT.FILL;
    buttonComposite.setLayoutData(data);
    buttonComposite.setLayout(gl);

    addButton = new Button(buttonComposite, SWT.PUSH);
    addButton.setText("Add...");
    addButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    addButton.addSelectionListener(this);

    removeButton = new Button(buttonComposite, SWT.PUSH);
    removeButton.setText("Remove");
    removeButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    removeButton.addSelectionListener(this);

    List initialSelection = getInitialElementSelections();
    if (initialSelection != null)
      categoryTableViewer.setSelection(new StructuredSelection(initialSelection));

    Label elementLabel = new Label(categoryComposite, SWT.LEFT);
    elementLabel.setText("Available components to Add:");

    new Label(categoryComposite, SWT.NONE);

    elementTableViewer = new TableViewer(categoryComposite, getTableStyle());
    elementTableViewer.setContentProvider(new ElementContentProvider());
    elementTableViewer.setLabelProvider(new ElementLabelProvider());
    elementTableViewer.setInput(null);
    elementTableViewer.addDoubleClickListener(new IDoubleClickListener()
    {
      public void doubleClick(DoubleClickEvent event)
      {
        okPressed();
      }
    });
    
    gd = new GridData(GridData.FILL_BOTH);
    table = elementTableViewer.getTable();
    table.setLayoutData(gd);
    table.setFont(container.getFont());

    elementTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
		public void selectionChanged(SelectionChangedEvent event) {
	          getButton(IDialogConstants.OK_ID).setEnabled(true);			
	    }        	  
      });
    
    return parent;
  }

  public void create()
  {
    super.create();
    if (categoryTableViewer.getTable().getItemCount() > 0)
    {
      categoryTableViewer.getTable().select(0);
      categoryTableViewer.setSelection(new StructuredSelection(categoryTableViewer.getElementAt(0)));
    }
    
    // Setup the list of category names that already exist
	existingNames = new ArrayList();
	TableItem[] categoryNames = categoryTableViewer.getTable().getItems();
	for (int i = 0; i < categoryNames.length; i++ ){
		existingNames.add(categoryNames[i].getText());
	}
	
	getButton(IDialogConstants.OK_ID).setEnabled(false);
  }

  protected Point getInitialSize()
  {
    return getShell().computeSize(400, 300);
  }

  /**
   * Return the style flags for the table viewer.
   * 
   * @return int
   */
  protected int getTableStyle()
  {
    return SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER;
  }

  /*
   * Overrides method from Dialog
   */
  protected void okPressed()
  {
    // Build a list of selected children.
    getShell().setCursor(new Cursor(getShell().getDisplay(), SWT.CURSOR_WAIT));
    IStructuredSelection elementSelection = (IStructuredSelection) elementTableViewer.getSelection();
    IStructuredSelection categorySelection = (IStructuredSelection) categoryTableViewer.getSelection();
    List result = new ArrayList();
    result.add(elementSelection.getFirstElement());
    result.add(categorySelection.getFirstElement());
    if (elementSelection.getFirstElement() != null)
    {
      setResult(result);
    }
    else
    {
      setResult(null);
    }
    super.okPressed();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
   */
  public void widgetSelected(SelectionEvent e)
  {
    if (e.widget == addButton)
    {
    	AddNewCategoryDialog addNewCategoryDialog
    		= new AddNewCategoryDialog(getShell());
    	
    	addNewCategoryDialog.setUnavailableCategoryNames(existingNames);
    	
    	if ( addNewCategoryDialog.open() == Window.OK ){
    		String location = addNewCategoryDialog.getAppInfoSchemaLocation();
    		
    		SpecificationForAppinfoSchema schemaSpec = new SpecificationForAppinfoSchema();
    		schemaSpec.setDisplayName(addNewCategoryDialog.getNewCategoryName());
    		schemaSpec.setLocation(location);
    		
    		fInput.add(schemaSpec);
    		existingNames.add(schemaSpec.getDisplayName());
    		
    		// refresh without updating labels of existing TableItems    		
    		categoryTableViewer.refresh(false);
    		
    		categoryTableViewer.setSelection(new StructuredSelection(schemaSpec));
    		getButton(IDialogConstants.OK_ID).setEnabled(false);
    	}
    }
    else if (e.widget == removeButton)
    {
    	TableItem[] selections = categoryTableViewer.getTable().getSelection();
    	for (int i =0; i < selections.length; i++){
    		fInput.remove(selections[i].getData() );
    	}
    	categoryTableViewer.refresh(false);

    	elementTableViewer.setInput(null);
    	elementTableViewer.refresh();
    	
        // TODO auto select either the prev category, the next category or the first category in the Table
    	getButton(IDialogConstants.OK_ID).setEnabled(false);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
   */
  public void widgetDefaultSelected(SelectionEvent event)
  {

  }

  public void selectionChanged(SelectionChangedEvent event)
  {
    if (event.getSource() == categoryTableViewer)
    {
      ISelection selection = event.getSelection();
      if (selection instanceof StructuredSelection)
      {
        Object obj = ((StructuredSelection) selection).getFirstElement();
        if (obj instanceof SpecificationForAppinfoSchema)
        {
          SpecificationForAppinfoSchema properties = (SpecificationForAppinfoSchema) obj;

          XSDSchema xsdSchema = getASISchemaModel(properties);

          if (xsdSchema == null){
        	  // TODO display an error Dialog telling the user that
        	  // her selected schema file is invalid. 
        	  return;
          }
          
          List allItems = buildInput(xsdSchema);
          elementTableViewer.setInput(allItems);
          getButton(IDialogConstants.OK_ID).setEnabled(false);
        }
      }
    }
  }
  private List buildInput(XSDSchema xsdSchema)
  {
    List elements = xsdSchema.getElementDeclarations();
    List attributes = xsdSchema.getAttributeDeclarations();
    String targetNamespace = xsdSchema.getTargetNamespace();

    // For safety purpose: We don't append 'attributes' to 'elements'
    // ArrayStoreException(or similar one) may occur
    List allItems = new ArrayList(attributes.size() + elements.size());
    {
      // getElementDeclarations returns a lot of elements from import
      // statement, we
      // only add non-imported elements here. (trung)
      for (int i = 0; i < elements.size(); i++)
      {
        XSDElementDeclaration currentElement = (XSDElementDeclaration) elements.get(i);
        if (currentElement.getTargetNamespace().equals(targetNamespace))
          allItems.add(currentElement);
      }
      // getAttributeDeclarations also returns a lot of elements from
      // import statement, we
      // only add non-imported elements here. (trung)
      for (int i = 0; i < attributes.size(); i++)
      {
        XSDAttributeDeclaration currentAttribute = (XSDAttributeDeclaration) attributes.get(i);
        if (currentAttribute.isGlobal() && currentAttribute.getTargetNamespace().equals(targetNamespace))
          allItems.add(currentAttribute);
      }
    }
    return allItems;
  }

  
  private XSDSchema getASISchemaModel(SpecificationForAppinfoSchema appInfoSchemaSpec)
  {
    XSDSchema xsdSchema = XSDImpl.buildXSDModel(appInfoSchemaSpec.getLocation());
    
    // now that the .xsd file is read, we can retrieve the namespace of this xsd file
    // and set the namespace for 'properties'
    if ( appInfoSchemaSpec.getNamespaceURI() == null){
    	appInfoSchemaSpec.setNamespaceURI( xsdSchema.getTargetNamespace());
    }
    
    return xsdSchema;
  }

  static class CategoryContentProvider implements IStructuredContentProvider
  {
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object inputElement)
    {    
      SpecificationForAppinfoSchema[] appInfoSchemaSpecs = null;
      try
      {
        List inputList = (List) inputElement;
        appInfoSchemaSpecs = (SpecificationForAppinfoSchema[]) inputList.toArray(new SpecificationForAppinfoSchema[0]);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      return appInfoSchemaSpecs;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose()
    {
      // Do nothing

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
     *      java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
      // Do nothing

    }
  }

  static class CategoryLabelProvider extends LabelProvider
  {
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     */
    public Image getImage(Object element)
    {
      return XSDEditorPlugin.getXSDImage("icons/appinfo_category.gif");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     */
    public String getText(Object element)
    {
      if (element instanceof SpecificationForAppinfoSchema)
        return ((SpecificationForAppinfoSchema) element).getDisplayName();

      return super.getText(element);
    }
  }

  static class ElementContentProvider implements IStructuredContentProvider
  {
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object inputElement)
    {
      if (inputElement instanceof List)
      {
        return ((List) inputElement).toArray();
      }
      return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose()
    {
      // Do nothing

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
     *      java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
      // Do nothing

    }
  }

  class ElementLabelProvider extends LabelProvider
  {
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     */
    public Image getImage(Object element)
    {
      if ( element instanceof XSDElementDeclaration){
    	  
    	  // Workaround trick: (trung) we create a temporary Dom element and put it in the label provider
    	  // to get the image.
    	  String namespace = ((XSDElementDeclaration) element).getSchema().getTargetNamespace();
    	  String name = ((XSDElementDeclaration) element).getName();
    	  Element tempElement = tempDoc.createElementNS(namespace, name);
    	  ILabelProvider lp = registry.getLabelProvider(tempElement);
    	  if (lp != null){
    		  Image img = lp.getImage(tempElement);
    		  
    		  if (img != null){
    			  return img;
    		  }
    	  }
    	  return DEFAULT_ELEMENT_ICON;
      }
      else if ( element instanceof XSDAttributeDeclaration){
    	  return DEFAULT_ATTRIBUTE_ICON;
      }
      return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     */
    public String getText(Object element)
    {
      if (element instanceof XSDElementDeclaration)
        return ((XSDElementDeclaration) element).getName();
      if (element instanceof XSDAttributeDeclaration )
        return ((XSDAttributeDeclaration) element).getName();
      return super.getText(element);
    }
  }

  public boolean close()
  {
    return super.close();
  }
}
