/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.dialogs.types;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.wst.xml.uriresolver.util.URIHelper;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.actions.CreateElementAction;
import org.eclipse.wst.xsd.ui.internal.actions.CreateSimpleTypeAction;
import org.eclipse.wst.xsd.ui.internal.actions.DOMAttribute;
import org.eclipse.wst.xsd.ui.internal.refactor.delete.XSDExternalFileCleanup;
import org.eclipse.wst.xsd.ui.internal.util.TypesHelper;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.impl.XSDImportImpl;
import org.eclipse.xsd.util.XSDConstants;
import org.eclipse.xsd.util.XSDParser;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XSDSetTypeDialog extends SetTypeDialog {
	private boolean showAnonymous = true;
	boolean showComplexTypes = true;
	private XSDSchema xsdSchema;
	
	/*
	 * input should be of type Element.....  For example, if our current selection is a GlobalElement, input should
	 * be the Element of the GlobalElement
	 */
	public XSDSetTypeDialog(Shell shell, Element input, String dialogTitle, XSDSchema xsdSchema) {
		super(shell, input, dialogTitle, "type");
		this.xsdSchema = xsdSchema;
	}

	protected void createTreeView(TypesDialogTreeObject root, String filterString) {
		TypesDialogTreeObject builtInRoot = new TypesDialogTreeObject("Built-in simple type");
	  	root.addChild(builtInRoot);
	  	createBuiltInTypes(builtInRoot, filterString);
	  	
		TypesDialogTreeObject typesRoot = new TypesDialogTreeObject("Types");
	  	root.addChild(typesRoot);	  	
	  	createComplexTypes(typesRoot, filterString);
	  	createSimpleTypes(typesRoot, filterString);
	  	createFromImport(root, filterString, false);
	  	createFromInclude(root, filterString, false);
	  	createFromRedefine(root, filterString, false);
	}

	protected void createFlatView(TypesDialogTreeObject root, String filterString) {
		createComplexTypes(treeRootViewerInput, filterString);
		createSimpleTypes(treeRootViewerInput, filterString);
	  	createFromImport(root, filterString, true);
	  	createFromInclude(root, filterString, true);
	  	createFromRedefine(root, filterString, true);
		createBuiltInTypes(treeRootViewerInput, filterString);
	}
	
	public void createBuiltInTypes(TypesDialogTreeObject root, String filterString) {
		Iterator iterator = getBuiltInTypes().iterator();
	  	Pattern regex = Pattern.compile(filterString);
	  	if (iterator.hasNext()) {
	  		while (iterator.hasNext()) {
	  			Object item = iterator.next();
	  			String itemString = item.toString();
    			Matcher m = regex.matcher(itemString.toLowerCase());
				if (itemString.toLowerCase().startsWith(filterString) || m.matches()) {
		  			TypesDialogTreeObject treeObject = new TypesDialogTreeObject(item, TypesDialogTreeObject.BUILT_IN_TYPE);
	  				root.addChild(treeObject);
	  			}
	  		}
	  	}
	}
	
	public void createComplexTypes(TypesDialogTreeObject root, String filterString) {
		TypesHelper typesHelper = new TypesHelper(xsdSchema);
		filterString = filterString.toLowerCase();
	    Pattern regex = java.util.regex.Pattern.compile(filterString);
	    
	    // Add anonymous
	    addAnonymousType(root, regex, filterString);

	    List complexTypes = typesHelper.getUserComplexTypes();
	    createComplexSimpleTreeObject(root, complexTypes, filterString, true);
	}
	
	public void createSimpleTypes(TypesDialogTreeObject root, String filterString) {
		TypesHelper typesHelper = new TypesHelper(xsdSchema);
		filterString = filterString.toLowerCase();
	    Pattern regex = java.util.regex.Pattern.compile(filterString);
	    
	    // Add anonymous
	    addAnonymousType(root, regex, filterString);
	    
	    // Add anyType
	    Matcher m = regex.matcher("anyType");
	    if ("anyType".startsWith(filterString) || m.matches())  {
		    TypesDialogTreeObject simpleTreeObject = new TypesDialogTreeObject("anyType");
			root.addChild(simpleTreeObject);
			simpleTreeObject.setLabel(typesHelper.getPrefix(xsdSchema.getSchemaForSchemaNamespace(), true) + "anyType");
	    }
	    
	    List simpleTypes = typesHelper.getUserSimpleTypes();
		createComplexSimpleTreeObject(root, simpleTypes, filterString, true);
	}
	
	private void addAnonymousType(TypesDialogTreeObject root, Pattern regex, String filterString) {	    
	    if (showAnonymous)
	     {
	    	Matcher m = regex.matcher("**anonymous**");
	    	if ("**anonymous**".startsWith(filterString) || m.matches())  {
	    		TypesDialogTreeObject simpleTreeObject = new TypesDialogTreeObject("**anonymous**");
	    		simpleTreeObject.setType(TypesDialogTreeObject.ANONYMOUS_SIMPLE_TYPE);
	  			root.addChild(simpleTreeObject);
	    	}
	    }
	}
	
	private void createFromImport(TypesDialogTreeObject root, String filterString, boolean isFlatView) {
		Iterator imports = getXSDImports().iterator();
		while (imports.hasNext()) {
			XSDImport importItem = (XSDImport) imports.next();
			if (importItem.getSchemaLocation() != null) {
				((XSDImportImpl) importItem).importSchema();
				TypesHelper helper = new TypesHelper(importItem.getResolvedSchema());
				
				TypesDialogTreeObject importRoot = root;
				if (!isFlatView) {
					// Create the TypesDialogTreeObject
					importRoot = new TypesDialogTreeObject(importItem.getResolvedSchema());
					root.addChild(importRoot);
					importRoot.setAppendLabel(" (Import)");
				}
				
				List types = helper.getUserComplexTypes();
				types.addAll(helper.getUserSimpleTypes());
				createComplexSimpleTreeObject(importRoot, types, filterString, false);
			}
		}
	}
	
	private void createFromInclude(TypesDialogTreeObject root, String filterString, boolean isFlatView) {
		Iterator imports = getXSDIncludes().iterator();
		while (imports.hasNext()) {
			XSDInclude includeItem = (XSDInclude) imports.next();
			if (includeItem.getSchemaLocation() != null) {
				TypesHelper helper = new TypesHelper(includeItem.getResolvedSchema());
				
				TypesDialogTreeObject includeRoot = root;
				if (!isFlatView) {
					// Create the TypesDialogTreeObject
					includeRoot = new TypesDialogTreeObject(includeItem.getResolvedSchema());
					root.addChild(includeRoot);
					includeRoot.setAppendLabel(" (Include)");
				}
				
				List types = helper.getUserComplexTypes();
				types.addAll(helper.getUserSimpleTypes());
				createComplexSimpleTreeObject(includeRoot, types, filterString, false);
			}
		}
	}
	
	private void createFromRedefine(TypesDialogTreeObject root, String filterString, boolean isFlatView) {
		Iterator redefines = getXSDRedefines().iterator();
		while (redefines.hasNext()) {
			XSDRedefine redefineItem = (XSDRedefine) redefines.next();
			if (redefineItem.getSchemaLocation() != null) {
				TypesHelper helper = new TypesHelper(redefineItem.getResolvedSchema());
				
				TypesDialogTreeObject includeRoot = root;
				if (!isFlatView) {
					// Create the TypesDialogTreeObject
					includeRoot = new TypesDialogTreeObject(redefineItem.getResolvedSchema());
					root.addChild(includeRoot);
					includeRoot.setAppendLabel(" (Redefine)");
				}
				
				List types = helper.getUserComplexTypes();
				types.addAll(helper.getUserSimpleTypes());
				createComplexSimpleTreeObject(includeRoot, types, filterString, false);
			}
		}
	}
	
	private void createComplexSimpleTreeObject(TypesDialogTreeObject root, List complexTypes, String filterString, boolean sameNS) {
		Pattern regex = java.util.regex.Pattern.compile(filterString);
		boolean proceed = true;
		
	    for (int i = 0; i < complexTypes.size(); i++) {
	    	XSDNamedComponent item = (XSDNamedComponent) complexTypes.get(i); 
	    
	    	if (sameNS) {
				// We do this check because Types from Includes might show up.  However, we don't want to show them
				String itemLocation = item.getSchema().getSchemaLocation();
				String currentSchemaLocation = xsdSchema.getSchemaLocation();
				if (itemLocation != null) {
					proceed = itemLocation.equals(currentSchemaLocation);
				}
				else {
					proceed = false;
				}
	    	}
	    	
			if (proceed) {
	    		String itemString = item.getName();
		    	Matcher m = regex.matcher(itemString.toLowerCase());
			    	
		    	if (itemString.toLowerCase().startsWith(filterString) || m.matches()) {
		    		TypesDialogTreeObject treeObject = new TypesDialogTreeObject(item);
		  			root.addChild(treeObject);
		  			treeObject.setLabel(itemString);
		    	}
			}
	    }
	}
	
	private List getXSDImports() {
		List imports = new ArrayList();
		
		Iterator contents = xsdSchema.getContents().iterator();
		while (contents.hasNext()) {
			XSDSchemaContent content = (XSDSchemaContent) contents.next();
			if (content instanceof XSDImport) {
				imports.add(content);	          
			}
		}
		
		return imports;
	}
	
	private List getXSDIncludes() {
		List includes = new ArrayList();
		
		Iterator contents = xsdSchema.getContents().iterator();
		while (contents.hasNext()) {
			XSDSchemaContent content = (XSDSchemaContent) contents.next();
			if (content instanceof XSDInclude) {
				includes.add(content);	          
			}
		}
		
		return includes;
	}

	private List getXSDRedefines() {
		List includes = new ArrayList();
		
		Iterator contents = xsdSchema.getContents().iterator();
		while (contents.hasNext()) {
			XSDSchemaContent content = (XSDSchemaContent) contents.next();
			if (content instanceof XSDRedefine) {
				includes.add(content);	          
			}
		}
		
		return includes;
	}
	
	public java.util.List getBuiltInTypes() {
		TypesHelper helper = new TypesHelper(xsdSchema);
	    return helper.getBuiltInTypeNamesList();
	}

	private String getUniqueName(String baseName, List usedNames) {
		String uniqueName = baseName;
		int number = 1;
		boolean foundMatch = true;
		
		while (foundMatch) {
			foundMatch = false;
			Iterator names = usedNames.iterator();
			while (names.hasNext()) {
				String name = (String) names.next();
				
				if (uniqueName.equalsIgnoreCase(name)) {
					foundMatch = true;
					uniqueName = baseName + String.valueOf(number++);
					break;
				}
			}
		}
		
		return uniqueName;
	}

	protected void createButtonPressed() {
		List usedComplexNames = getUsedComplexTypeNames(xsdSchema);
		List usedSimpleNames = getUsedSimpleTypeNames(xsdSchema);
		String newItemName = getUniqueName("NewComplexType", usedComplexNames);
		
		NewTypeDialog dialog = new NewTypeDialog(XSDEditorPlugin.getShell(), newItemName, usedComplexNames);
		dialog.setUsedSimpleTypeNames(usedSimpleNames);
		
		int rc = dialog.createAndOpen();
		if (rc == IDialogConstants.OK_ID) {
			newItemName = dialog.getName();
		    ArrayList attributes = new ArrayList();
	        attributes.add(new DOMAttribute(XSDConstants.NAME_ATTRIBUTE, newItemName));
	        
	        if (dialog.isComplexType()) {
	        	CreateElementAction action = new CreateElementAction(XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_COMPLEX_TYPE"));
			    action.setElementTag(XSDConstants.COMPLEXTYPE_ELEMENT_TAG);
			    action.setAttributes(attributes);
			    action.setParentNode(xsdSchema.getElement());
			    action.setXSDSchema(xsdSchema);
			    action.run();
	        }
	        else {
	        	CreateSimpleTypeAction action = new CreateSimpleTypeAction(XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_COMPLEX_TYPE"));
			    action.setElementTag(XSDConstants.SIMPLETYPE_ELEMENT_TAG);
			    action.setAttributes(attributes);
			    action.setParentNode(xsdSchema.getElement());
			    action.setXSDSchema(xsdSchema);
			    action.run();
	        }
		    
			// Update tree view
			if (viewTypeCheckBox.getSelection()) {
		      	populateTreeViewer(viewTypeCheckBox.getSelection(), textFilter.getText(), false, false);
		      	treeView.selectTreeObject(TypesDialogTreeObject.getTreeObject(treeRootViewerInput, newItemName));
		      }
		      else {
		      	populateTreeViewer(viewTypeCheckBox.getSelection(), textFilter.getText(), true, true);

		      	TypesDialogTreeObject parentTreeObject = TypesDialogTreeObject.getTreeObject(treeRootViewerInput, "Types");
		      	treeView.selectTreeObject(TypesDialogTreeObject.getTreeObject(parentTreeObject, newItemName));
		      }
		}
	}

	protected void importButtonPressed() {        
		ImportTypesDialog dialog = new ImportTypesDialog(XSDEditorPlugin.getShell(), null, false, kind);
	    String [] filters = { "xsd" };
//	    IFile currentWFile = ((IFileEditorInput)editorPart.getEditorInput()).getFile();
//	    IFile [] excludedFiles = { currentFile };
	    IFile[] excludedFiles = new IFile[0];
	    
	    dialog.addFilterExtensions(filters, excludedFiles);
	    dialog.create();

	    // Translate strings below
	    dialog.getShell().setText("Select");
	    dialog.setTitle("Select File");
	    dialog.setMessage("Select a XSD file to import");
	    
	    int rc = dialog.open();
	    if (rc == IDialogConstants.OK_ID) {
		  	  XSDNamedComponent selection = (XSDNamedComponent) dialog.getListSelection();
			  IFile importFile = dialog.getFile();			    
	          XSDSchema externalSchema = doLoadExternalModel(importFile);
	          
	          // Compare namespaces to determine if we should treat this as an Import or Include
	          boolean isInclude = false;
	          String externalNamespace = externalSchema.getTargetNamespace();
	          if (externalNamespace.equalsIgnoreCase(xsdSchema.getTargetNamespace())) {
	          	isInclude = true;
	          }

	          // Determine schemaLocation
	          IFile currentIFile = ((IFileEditorInput)getActiveEditor().getEditorInput()).getFile();
	          String locationAttribute = URIHelper.getRelativeURI(importFile.getLocation(), currentIFile.getLocation());
	          
	          if (externalSchema != null) { // In case we have problems loading the file.... we should display an error message.
				    Element newElement;
			        if (isInclude) {	        	
			        	List attributes = new ArrayList();
			            attributes.add(new DOMAttribute(XSDConstants.SCHEMALOCATION_ATTRIBUTE, locationAttribute));
			            newElement = createElement(XSDConstants.INCLUDE_ELEMENT_TAG, attributes);
			        }
			        else if (!isInclude) {
			        	List attributes = new ArrayList();
			            attributes.add(new DOMAttribute(XSDConstants.NAMESPACE_ATTRIBUTE, externalNamespace));
			            attributes.add(new DOMAttribute(XSDConstants.SCHEMALOCATION_ATTRIBUTE, locationAttribute));
			        	newElement = createElement(XSDConstants.IMPORT_ELEMENT_TAG, attributes);
			        	handleImportNS(newElement, externalSchema);
			        }
	          }
	          
		      // We need to refresh our tree view
		      if (viewTypeCheckBox.getSelection()) {
		      	populateTreeViewer(viewTypeCheckBox.getSelection(), textFilter.getText(), false, false);
		      	treeView.selectTreeObject(TypesDialogTreeObject.getTreeObject(treeRootViewerInput, selection.getName()));
		      }
		      else {
		      	populateTreeViewer(viewTypeCheckBox.getSelection(), textFilter.getText(), true, true);

			    // We now want to expand the newly imported file and selected the proper type/element
		      	TypesDialogTreeObject parentTreeObject = TypesDialogTreeObject.getTreeObject(treeRootViewerInput, dialog.getFile().getName());
		      	treeView.selectTreeObject(TypesDialogTreeObject.getTreeObject(parentTreeObject, selection.getName()));
		      }
	          
	    }
	}
	
	private Element createElement(String elementTag, List attributes) {
		Node relativeNode = XSDDOMHelper.getNextElementNode(xsdSchema.getElement().getFirstChild());
		
		CreateElementAction action = new CreateElementAction("");
	    action.setElementTag(elementTag);
	    action.setAttributes(attributes);
	    action.setParentNode(xsdSchema.getElement());
	    action.setRelativeNode(relativeNode);
	    action.setXSDSchema(xsdSchema);
	    return action.createAndAddNewChildElement();
	}
	
	  private IEditorPart getActiveEditor()
	  {
	    IWorkbench workbench = XSDEditorPlugin.getPlugin().getWorkbench();
	    IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
	    IEditorPart editorPart = workbenchWindow.getActivePage().getActiveEditor();

	    return editorPart;
	  }
	
	  /**
	   * Create a MOF model for the imported file
	   */
	  protected XSDSchema doLoadExternalModel(IFile iFile)
	  { 
	    String xsdModelFile = iFile.getLocation().toOSString();
	    String xsdFileName = iFile.getName();
	  	IProgressMonitor monitor = new NullProgressMonitor();
	    String errorMessage = null;
	    String currentNameSpace = xsdSchema.getTargetNamespace();

	    monitor.beginTask("Loading XML Schema", 100);
	    monitor.worked(50);

	    XSDParser parser = new XSDParser();
	    parser.parse(xsdModelFile);

	    XSDSchema externalSchema = parser.getSchema();
	    if (externalSchema != null)
	    {
	      if (externalSchema.getDiagnostics() != null &&
	          externalSchema.getDiagnostics().size() > 0)
	      {
	        errorMessage = XSDEditorPlugin.getPlugin().getString("_UI_INCORRECT_XML_SCHEMA", xsdFileName);
	      }
	    }
	    else
	    {
	      errorMessage = XSDEditorPlugin.getPlugin().getString("_UI_INCORRECT_XML_SCHEMA", xsdFileName);
	    }
	    if (errorMessage != null) {
//	    	System.out.println("\nError loading XML Schema:\n" + errorMessage);
	    }

	    monitor.subTask("Finish Loading");
	    monitor.worked(80);

	    return externalSchema;
	  }
	
	private void handleImportNS(Element importElement, XSDSchema externalSchema) {
          String namespace = externalSchema.getTargetNamespace();
          if (namespace == null) namespace = "";

          XSDImport xsdImport = (XSDImport) xsdSchema.getCorrespondingComponent(importElement);
          xsdImport.setResolvedSchema(externalSchema);
          
          java.util.Map map = xsdSchema.getQNamePrefixToNamespaceMap();
          
//          System.out.println("changed Import Map is " + map.values());
//          System.out.println("changed import Map keys are " + map.keySet());

          // Referential integrity on old import
          // How can we be sure that if the newlocation is the same as the oldlocation
          // the file hasn't changed
          
          XSDSchema referencedSchema = xsdImport.getResolvedSchema();
          if (referencedSchema != null)
          {
            XSDExternalFileCleanup cleanHelper = new XSDExternalFileCleanup(referencedSchema);
            cleanHelper.visitSchema(xsdSchema);
          }

          Element schemaElement = xsdSchema.getElement();

          // update the xmlns in the schema element first, and then update the import element next
          // so that the last change will be in the import element.  This keeps the selection
          // on the import element
          TypesHelper helper = new TypesHelper(externalSchema);
          String prefix = helper.getPrefix(namespace, false);
          
          if (map.containsKey(prefix))
          {
            prefix = null;
          }

          if (prefix == null || (prefix !=null && prefix.length() == 0))
          {
            StringBuffer newPrefix = new StringBuffer("pref");  //$NON-NLS-1$
            int prefixExtension = 1;
            while (map.containsKey(newPrefix.toString()) && prefixExtension < 100)
            {
              newPrefix = new StringBuffer("pref" + String.valueOf(prefixExtension));
              prefixExtension++;
            }
            prefix = newPrefix.toString();
          }

          if (namespace.length() > 0)
          {
            // if ns already in map, use its corresponding prefix
            if (map.containsValue(namespace))
            {
              TypesHelper typesHelper = new TypesHelper(xsdSchema);
              prefix = typesHelper.getPrefix(namespace, false);
            }
            else // otherwise add to the map
            {
              schemaElement.setAttribute("xmlns:"+prefix, namespace);
            }
          }


//          System.out.println("changed Import Map is " + map.values());
//          System.out.println("changed import Map keys are " + map.keySet());
	}
	
	protected void updateCanFinish(Object object) {
		if (object instanceof StructuredSelection) {
	  		Object selectionObject = ((StructuredSelection) object).getFirstElement();
	  		if (selectionObject instanceof TypesDialogTreeObject) {
	  			TypesDialogTreeObject treeObject = (TypesDialogTreeObject) selectionObject;
	  			if (treeObject.getDataObject() instanceof XSDComplexTypeDefinition ||
	  				treeObject.getDataObject() instanceof XSDSimpleTypeDefinition ||
					(treeObject.getDataObject() instanceof String && treeObject.getDataObject().toString().equals("anyType")) ||
					(treeObject.getDataObject() instanceof String && treeObject.getDataObject().toString().equals("**anonymous**") ||
					(treeObject.getDataObject() instanceof String && treeObject.getType() == TypesDialogTreeObject.BUILT_IN_TYPE))) {
					
	  				getButton(IDialogConstants.OK_ID).setEnabled(true);
	  			}
	  			else {
	  				getButton(IDialogConstants.OK_ID).setEnabled(false);
	  			}
	  		}
	  		else {
  				getButton(IDialogConstants.OK_ID).setEnabled(false);
  			}
	  	}
	}

	protected void okPressed() {
		Element element = (Element) input;
		String typeObject = "";
		TypesDialogTreeObject treeObject = (TypesDialogTreeObject) treeView.getSelection();
		
		// Get the new type --> typeObject
		if (treeObject.getDataObject() instanceof String) {
			// Should be a built-in type or anonymous or anyType
			typeObject = (String) treeObject.getDataObject();
		}
		else {
			XSDNamedComponent item = (XSDNamedComponent) treeObject.getDataObject();
			typeObject = item.getName();
			
			TypesHelper typesHelper = new TypesHelper(item.getSchema());
		    List prefixedNames = typesHelper.getPrefixedNames(item.getTargetNamespace(), item.getName());
		    if (prefixedNames.size() > 0) {
		    	// Grab the first prefixed name
		    	typeObject = (String) prefixedNames.get(0);
		    }
		}
		
		// Get the previous type --> previousStringType
		String previousStringType = "";
		Attr attr = element.getAttributeNode("type");
		if (attr != null) {
			String value = attr.getValue();
		}

	    if (!XSDDOMHelper.inputEquals(element, XSDConstants.UNION_ELEMENT_TAG, false))
	    {
		    if (typeObject.equals("**anonymous**"))
		    {
		      if (treeObject.getType() == TypesDialogTreeObject.ANONYMOUS_SIMPLE_TYPE)
		      {
		        if (!previousStringType.equals("**anonymous**"))
		        {
		          updateElementToAnonymous(element, XSDConstants.SIMPLETYPE_ELEMENT_TAG);
		        }
		      }
		      else
		      {
		        if (!previousStringType.equals("**anonymous**"))
		        {
		          updateElementToAnonymous(element, XSDConstants.COMPLEXTYPE_ELEMENT_TAG);
		        }
		      }
		      // element.removeAttribute(XSDConstants.TYPE_ATTRIBUTE);
		      element.removeAttribute("type");
		    }
		    else
		    {
		      XSDDOMHelper.updateElementToNotAnonymous(element);
		      //element.setAttribute(XSDConstants.TYPE_ATTRIBUTE, typeObject.toString());
		      element.setAttribute("type", typeObject.toString());
		    }
	    }
	    
		super.okPressed();
	}
	
	private void updateElementToAnonymous(Element element, String xsdType) {
		String prefix = element.getPrefix();
		prefix = (prefix == null) ? "" : (prefix + ":");
		XSDDOMHelper.updateElementToNotAnonymous(element);
		boolean hasChildrenElements = hasElementChildren(element);
		Element childNode = null;
		if (xsdType.equals(XSDConstants.COMPLEXTYPE_ELEMENT_TAG)) {
			childNode = element.getOwnerDocument().createElementNS(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, prefix + XSDConstants.COMPLEXTYPE_ELEMENT_TAG);
		}
		else if (xsdType.equals(XSDConstants.SIMPLETYPE_ELEMENT_TAG)) {
			childNode = element.getOwnerDocument().createElementNS(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, prefix + XSDConstants.SIMPLETYPE_ELEMENT_TAG);
		}
		
		if (childNode != null) {
			XSDDOMHelper helper = new XSDDOMHelper();
			Node annotationNode = helper.getChildNode(element, XSDConstants.ANNOTATION_ELEMENT_TAG);
			if (annotationNode == null) {
				Node firstChild = element.getFirstChild();
				element.insertBefore(childNode, firstChild);
			} else {
				Node nextSibling = annotationNode.getNextSibling();
				element.insertBefore(childNode, nextSibling);
			}
			XSDDOMHelper.formatChild(childNode);
		}
	}
	
	private boolean hasElementChildren(Node parentNode) {
		boolean hasChildrenElements = false;
		if (parentNode != null && parentNode.hasChildNodes()) {
			NodeList nodes = parentNode.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				if (nodes.item(i) instanceof Element) {
					hasChildrenElements = true;
					break;
				}
			}
		}
		return hasChildrenElements;
	}
}
