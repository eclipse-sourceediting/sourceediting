/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.xerces.dom.DocumentImpl;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
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
import org.eclipse.jface.viewers.ViewerFilter;
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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.wst.xsd.contentmodel.internal.XSDImpl;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorCSHelpIds;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AddExtensionsComponentDialog extends SelectionDialog implements ISelectionChangedListener, SelectionListener
{
  protected static final Image DEFAULT_ELEMENT_ICON = XSDEditorPlugin.getXSDImage("icons/XSDElement.gif"); //$NON-NLS-1$
  protected static final Image DEFAULT_ATTRIBUTE_ICON = XSDEditorPlugin.getXSDImage("icons/XSDAttribute.gif"); //$NON-NLS-1$

  /** A temporary Document in which we create temporary DOM element for each element in the
   * Element view. (required by LabelProvider)  */ 
  protected static Document tempDoc = new DocumentImpl();
  
  Button addButton, removeButton, editButton;

  public AddExtensionsComponentDialog(Shell parent, ExtensionsSchemasRegistry schemaRegistry)
  {
    super(parent);
    setTitle(Messages._UI_ACTION_ADD_EXTENSION_COMPONENTS);
    setShellStyle(SWT.APPLICATION_MODAL | SWT.RESIZE | SWT.CLOSE);    
  }

  private List fInput;

  private TableViewer categoryTableViewer, elementTableViewer;
  private ArrayList existingNames;

  private ViewerFilter elementTableViewerFilter;
  private SpecificationForExtensionsSchema currentExtCategory;
  private HashMap specToComponentsList = new HashMap();
  private IPreferenceStore prefStore;
  
  public void setInput(List input)
  {
    this.fInput = input;
  }
  
  public SpecificationForExtensionsSchema getSelectedCategory(){
	return currentExtCategory;
  }
  
  public void setInitialCategorySelection(SpecificationForExtensionsSchema spec){
	  currentExtCategory = spec;
  }
  
  protected IStructuredContentProvider getCategoryContentProvider()
  {
    return new CategoryContentProvider();
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
    label.setText(Messages._UI_LABEL_EXTENSION_CATEGORIES);

    new Label(categoryComposite, SWT.NONE);

    categoryTableViewer = new TableViewer(categoryComposite, getTableStyle());
    categoryTableViewer.setContentProvider(getCategoryContentProvider());
    categoryTableViewer.setLabelProvider(new CategoryLabelProvider());
    categoryTableViewer.setInput(fInput);
    categoryTableViewer.addSelectionChangedListener(this);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(categoryTableViewer.getControl(), XSDEditorCSHelpIds.ADD_EXTENSIONS_COMPONENTS__EXTENSION_CATEGORIES);

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
    addButton.setText(Messages._UI_LABEL_ADD_WITH_DOTS);
    addButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    addButton.addSelectionListener(this);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(addButton,
    		XSDEditorCSHelpIds.ADD_EXTENSIONS_COMPONENTS__ADD);    

    removeButton = new Button(buttonComposite, SWT.PUSH);
    removeButton.setText(Messages._UI_LABEL_DELETE);
    removeButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    removeButton.addSelectionListener(this);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(removeButton,
    		XSDEditorCSHelpIds.ADD_EXTENSIONS_COMPONENTS__DELETE);    

    
    editButton = new Button(buttonComposite, SWT.PUSH);
    editButton.setText(Messages._UI_LABEL_EDIT);
    editButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    editButton.addSelectionListener(this);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(editButton,
    		XSDEditorCSHelpIds.ADD_EXTENSIONS_COMPONENTS__EDIT);    
    
    
    List initialSelection = getInitialElementSelections();
    if (initialSelection != null)
      categoryTableViewer.setSelection(new StructuredSelection(initialSelection));

    Label elementLabel = new Label(categoryComposite, SWT.LEFT);
    elementLabel.setText(Messages._UI_LABEL_AVAILABLE_COMPONENTS_TO_ADD);

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
    if ( elementTableViewerFilter != null){
      elementTableViewer.addFilter(elementTableViewerFilter);
    }
    
    gd = new GridData(GridData.FILL_BOTH);
    table = elementTableViewer.getTable();
    table.setLayoutData(gd);
    table.setFont(container.getFont());

    elementTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
		public void selectionChanged(SelectionChangedEvent event) {
	          getButton(IDialogConstants.OK_ID).setEnabled(true);			
	    }        	  
      });
    PlatformUI.getWorkbench().getHelpSystem().setHelp(elementTableViewer.getControl(), XSDEditorCSHelpIds.ADD_EXTENSIONS_COMPONENTS__AVAILABLE_COMPONENTS_TO_ADD);
    
    return parent;
  }

  public void create()
  {
    super.create();
    if (categoryTableViewer.getTable().getItemCount() > 0)
    {
      categoryTableViewer.getTable().select(0);
      
      StructuredSelection structuredSelection;
      if (currentExtCategory != null){
    	  structuredSelection = new StructuredSelection(currentExtCategory);
      }
      else{
    	  structuredSelection = 
    		  new StructuredSelection(categoryTableViewer.getElementAt(0));
      }
      
	  categoryTableViewer.setSelection(structuredSelection);
    }
    
    // Setup the list of category names that already exist
    // and contructs the XSDSchema for each category
	existingNames = new ArrayList();
	TableItem[] categories = categoryTableViewer.getTable().getItems();
	for (int i = 0; i < categories.length; i++ ){
		existingNames.add(categories[i].getText());

		SpecificationForExtensionsSchema spec = 
			(SpecificationForExtensionsSchema) categories[i].getData();
		XSDSchema schema = getASISchemaModel(spec);
		
		if (schema == null)
		{
			continue;
		}

		List components = buildInput(schema);
		specToComponentsList.put(spec, components);

		// mark category as gray/empty if applicable
		Object[] remains = components.toArray();
		if ( elementTableViewerFilter != null)
			remains = elementTableViewerFilter.filter(elementTableViewer, 
					elementTableViewer.getTable(), remains);
		if ( remains.length == 0)
		{
		  categories[i].setForeground(
			getShell().getDisplay().getSystemColor(SWT.COLOR_GRAY));
		}
	}
	
	getButton(IDialogConstants.OK_ID).setEnabled(false);
  }
  
  public void addElementsTableFilter(ViewerFilter filter){
	elementTableViewerFilter = filter;
  }
  
  public void setPrefStore(IPreferenceStore prefStore)
  {
    this.prefStore = prefStore;
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
    		SpecificationForExtensionsSchema schemaSpec = 
    			new SpecificationForExtensionsSchema();
    		schemaSpec.setDisplayName(addNewCategoryDialog.getNewCategoryName() );
    		schemaSpec.setLocation(addNewCategoryDialog.getCategoryLocation() );
    		schemaSpec.setSourceHint(addNewCategoryDialog.getSource());
    		schemaSpec.setFromCatalog(addNewCategoryDialog.getFromCatalog() );

    		fInput.add(schemaSpec);
    		existingNames.add(schemaSpec.getDisplayName());

    		// refresh without updating labels of existing TableItems    		
    		categoryTableViewer.refresh(false);

    		categoryTableViewer.setSelection(new StructuredSelection(schemaSpec));
    		getButton(IDialogConstants.OK_ID).setEnabled(false);
    		
    		storeSpecInPref(schemaSpec);  // must store after selection changed is called ie. 2 lines above (trung)
    	}
    }
    else if (e.widget == removeButton)
    {
    	TableItem[] selections = categoryTableViewer.getTable().getSelection();
    	for (int i =0; i < selections.length; i++){
    		SpecificationForExtensionsSchema spec = 
    			(SpecificationForExtensionsSchema) selections[i].getData();

			fInput.remove(spec );
    		existingNames.remove(spec.getDisplayName());
    		specToComponentsList.remove(spec);
    		
    		removeFromPref(spec);
    	}
    	categoryTableViewer.refresh(false);

    	elementTableViewer.setInput(null);
    	elementTableViewer.refresh();

        // TODO auto select either the prev category, the next category or the first category in the Table
    	getButton(IDialogConstants.OK_ID).setEnabled(false);
    }
    else if (e.widget == editButton)
    {
        // use this dialog not for adding but for editing purpose.
        AddNewCategoryDialog dialog = new AddNewCategoryDialog(getShell(), Messages._UI_LABEL_EDIT_CATEGORY);
        
    	TableItem[] selections = categoryTableViewer.getTable().getSelection();
    	if (selections.length == 0)
    		return;

    	SpecificationForExtensionsSchema spec = (SpecificationForExtensionsSchema) selections[0].getData();
        
    	String displayName = spec.getDisplayName();

		dialog.setCategoryName(displayName );
    	dialog.setFromCatalog(spec.isFromCatalog() );
    	dialog.setSource(spec.getSourceHint() );
    	dialog.setCategoryLocation(spec.getLocation() );

    	specToComponentsList.remove(spec);
    	dialog.setUnavailableCategoryNames(existingNames);
        
        if ( dialog.open() == Window.OK){
        	existingNames.remove(displayName);
			String newDisplayName = dialog.getNewCategoryName();

			spec.setDisplayName(newDisplayName);
        	spec.setLocation(dialog.getCategoryLocation());
        	spec.setSourceHint(dialog.getSource());
        	spec.setFromCatalog(dialog.getFromCatalog());
        	spec.setNamespaceURI(null);
        	
        	existingNames.add(newDisplayName);

        	categoryTableViewer.update(spec, null);
        	refreshElementsViewer(spec);
        	
        	editSpecInPref(displayName, spec);
        }
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
        if (obj instanceof SpecificationForExtensionsSchema)
        {
          SpecificationForExtensionsSchema spec = (SpecificationForExtensionsSchema) obj;
          currentExtCategory = spec;
          refreshElementsViewer(spec);

          if ( spec.isDefautSchema() ){
        	editButton.setEnabled(false);
        	removeButton.setEnabled(false);
          }
          else{
        	editButton.setEnabled(true);
        	removeButton.setEnabled(true);
          }

          getButton(IDialogConstants.OK_ID).setEnabled(false);
        }
      }
    }
  }
  
  private void refreshElementsViewer(SpecificationForExtensionsSchema spec) {
	  List components = (List) specToComponentsList.get(spec);
	  if ( components != null){
		elementTableViewer.setInput(components);
		return;
	  }
	  
	  XSDSchema xsdSchema = getASISchemaModel(spec);

	  if (xsdSchema == null){
		  MessageBox errDialog = new MessageBox(getShell(), SWT.ICON_ERROR);
		  errDialog.setText(Messages._UI_ERROR_INVALID_CATEGORY);
		  // TODO (trung) I was forced to use a String in Messages to make the error clearer
		  // When we are back to development phase, use another string: "Extension Category: "
		  // not "Extension Categories:" + " " like we are using here
		  errDialog.setMessage(Messages._UI_LABEL_EXTENSION_CATEGORIES + " " + spec.getDisplayName() //$NON-NLS-1$
				  + "\n"   //$NON-NLS-1$
				  + Messages._UI_ERROR_FILE_CANNOT_BE_PARSED
				  + "\n" + Messages._UI_ERROR_VALIDATE_THE_FILE);   //$NON-NLS-1$
		  errDialog.open();
		  
		  elementTableViewer.setInput(new ArrayList(0));
		  return;
	  }
	  
	  components = buildInput(xsdSchema);
      specToComponentsList.put(spec, components);	  
	  
	  List allItems = buildInput(xsdSchema);
	  if ( allItems == null )
		  return;
	  elementTableViewer.setInput(allItems);
  }
  
  private static List buildInput(XSDSchema xsdSchema)
  {
	if ( xsdSchema ==null )
		return null;

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
        if (currentElement.getTargetNamespace() != null)
        {
          if (currentElement.getTargetNamespace().equals(targetNamespace))
            allItems.add(currentElement);
        }
        else
        {
          if (targetNamespace == null)
            allItems.add(currentElement);
        }
      }
      // getAttributeDeclarations also returns a lot of elements from
      // import statement, we
      // only add non-imported elements here. (trung)
      for (int i = 0; i < attributes.size(); i++)
      {
        XSDAttributeDeclaration currentAttribute = (XSDAttributeDeclaration) attributes.get(i);
        if (currentAttribute.getTargetNamespace() != null)
        {
          if (currentAttribute.isGlobal() && currentAttribute.getTargetNamespace().equals(targetNamespace))
            allItems.add(currentAttribute);
        }
        else
        {
          if (targetNamespace == null)
            allItems.add(currentAttribute);
        }
      }
    }
    return allItems;
  }

  
  private static XSDSchema getASISchemaModel(SpecificationForExtensionsSchema extensionsSchemaSpec)
  {
    String location = extensionsSchemaSpec.getLocation();
    if ( location == null)
    	return null;
	XSDSchema xsdSchema = XSDImpl.buildXSDModel(location);
    
    // now that the .xsd file is read, we can retrieve the namespace of this xsd file
    // and set the namespace for 'properties'
    if ( extensionsSchemaSpec.getNamespaceURI() == null){
    	extensionsSchemaSpec.setNamespaceURI( xsdSchema.getTargetNamespace());
    }
    
    return xsdSchema;
  }

  private void storeSpecInPref(SpecificationForExtensionsSchema schemaSpec) {
	if (prefStore == null)
		return;
	
	String currentValue = prefStore.getString(ExtensionsSchemasRegistry.USER_ADDED_EXT_SCHEMAS);
	String specDesc = "  " + "\t" + schemaSpec.getDisplayName() + "\t"+
		schemaSpec.getNamespaceURI() + "\t" + schemaSpec.getLocation() + "\t" +
		schemaSpec.isDefautSchema() + "\t" + schemaSpec.getSourceHint() + "\t" +
		schemaSpec.isFromCatalog();
	currentValue += specDesc + "\n";

	prefStore.setValue(ExtensionsSchemasRegistry.USER_ADDED_EXT_SCHEMAS,
			currentValue);
  }

  private void editSpecInPref(String displayName, SpecificationForExtensionsSchema newSpec) {
	if (prefStore == null)
	  return;
	
	String newValue = "";
	String currentValue = prefStore.getString(ExtensionsSchemasRegistry.USER_ADDED_EXT_SCHEMAS);
	StringTokenizer tokenizer = new StringTokenizer(currentValue, "\n");

	while (tokenizer.hasMoreTokens())
	{
	  String oneSpecDesc = tokenizer.nextToken();
	  // get the Display name
	  StringTokenizer _tokenizer_2 = new StringTokenizer(oneSpecDesc, "\t");
	  _tokenizer_2.nextToken();
	  String dName = _tokenizer_2.nextToken();

	  if ( dName.equals(displayName ) )
	  {
		oneSpecDesc = "  " + "\t" + newSpec.getDisplayName() + "\t"+
			newSpec.getNamespaceURI() + "\t" + newSpec.getLocation() + "\t" +
			newSpec.isDefautSchema() + "\t" + newSpec.getSourceHint() + "\t" +
			newSpec.isFromCatalog();		  
	  }

	  newValue += oneSpecDesc + "\n";
	}
	prefStore.setValue(ExtensionsSchemasRegistry.USER_ADDED_EXT_SCHEMAS, newValue);
	
  }

  private void removeFromPref(SpecificationForExtensionsSchema spec) {
	if (prefStore == null)
	  return;	  
	  
	String newValue = "";
	String currentValue = prefStore.getString(ExtensionsSchemasRegistry.USER_ADDED_EXT_SCHEMAS);
	StringTokenizer tokenizer = new StringTokenizer(currentValue, "\n");

	while (tokenizer.hasMoreTokens())
	{
	  String oneSpecDesc = tokenizer.nextToken();
	  // get the Display name
	  StringTokenizer _tokenizer_2 = new StringTokenizer(oneSpecDesc, "\t");
	  _tokenizer_2.nextToken();
	  String dName = _tokenizer_2.nextToken();

	  if ( dName.equals(spec.getDisplayName() ) )
		continue;
	  newValue += oneSpecDesc + "\n";
	}
	prefStore.setValue(ExtensionsSchemasRegistry.USER_ADDED_EXT_SCHEMAS, newValue);
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
      SpecificationForExtensionsSchema[] extensionsSchemaSpecs = null;
      try
      {
        List inputList = (List) inputElement;
        extensionsSchemaSpecs = (SpecificationForExtensionsSchema[]) inputList.toArray(new SpecificationForExtensionsSchema[0]);
      }
      catch (Exception e)
      {
      }
      return extensionsSchemaSpecs;
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
      return XSDEditorPlugin.getXSDImage("icons/appinfo_category.gif"); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     */
    public String getText(Object element)
    {
      if (element instanceof SpecificationForExtensionsSchema)
        return ((SpecificationForExtensionsSchema) element).getDisplayName();

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
          ILabelProvider lp = XSDEditorPlugin.getDefault().getNodeCustomizationRegistry().getLabelProvider(namespace);
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
  
  protected void enableButtons(boolean value)
  {
    editButton.setEnabled(value);
    addButton.setEnabled(value);
    removeButton.setEnabled(value);
  }
}
