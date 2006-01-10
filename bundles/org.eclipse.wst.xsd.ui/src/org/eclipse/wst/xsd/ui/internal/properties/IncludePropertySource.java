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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.refactor.rename.XSDExternalFileCleanup;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class IncludePropertySource
  extends SchemaDirectiveHelperPropertySource
  implements IPropertySource
{
  /**
   * 
   */
  public IncludePropertySource(IFile currentIFile)
  {
    super(true);
    this.currentIFile = currentIFile;
  }
  /**
   * @param viewer
   * @param xsdSchema
   */
  public IncludePropertySource(Viewer viewer, XSDSchema xsdSchema, IFile currentIFile)
  {
    super(viewer, xsdSchema, true);
    this.currentIFile = currentIFile;
  }
  /**
   * @param xsdSchema
   */
  public IncludePropertySource(XSDSchema xsdSchema, IFile currentIFile)
  {
    super(xsdSchema, true);
    this.currentIFile = currentIFile;
  }
  /* (non-Javadoc)
   * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
   */
  public Object getEditableValue()
  {
    return null;
  }
  /* (non-Javadoc)
   * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
   */
  public IPropertyDescriptor[] getPropertyDescriptors()
  {
    List list = new ArrayList();
    // Create a descriptor and set a category
    
    SchemaLocationPropertyDescriptor schemaLocationDescriptor =
    new SchemaLocationPropertyDescriptor(
        XSDConstants.SCHEMALOCATION_ATTRIBUTE,
        XSDConstants.SCHEMALOCATION_ATTRIBUTE);
  
    list.add(schemaLocationDescriptor);
    

    IPropertyDescriptor[] result = new IPropertyDescriptor[list.size()];
    list.toArray(result);
    return result;
  }
  /* (non-Javadoc)
   * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
   */
  public Object getPropertyValue(Object id)
  {
    Object result = null;
    if (id instanceof String)
     {
      result = element.getAttribute((String) id);
      if (result == null)
       {
        result = "";
      }
      return result;
    }
    return "";
  }
  /* (non-Javadoc)
   * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
   */
  public boolean isPropertySet(Object id)
  {
    return false;
  }
  /* (non-Javadoc)
   * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
   */
  public void resetPropertyValue(Object id)
  {
  }
  /* (non-Javadoc)
   * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
   */
  public void setPropertyValue(Object id, Object value)
  {
    if (value == null)
    {
      value = "";
    }
    if (value instanceof String)
    {
      if (((String) id).equals(XSDConstants.SCHEMALOCATION_ATTRIBUTE))
      { 
        //element.setAttribute(XSDConstants.SCHEMALOCATION_ATTRIBUTE, (String)value);
        updateExternalModel((String)value, selectedIFile, selectedNamespace, selectedXSDSchema);
      }
    }
    Runnable delayedUpdate = new Runnable()
    {
      public void run()
      {
        if (viewer != null)
          viewer.refresh();
      }
    };
    Display.getCurrent().asyncExec(delayedUpdate);
  }

  protected void updateExternalModel(String newLocation, IFile newFile, String namespace, XSDSchema externalSchema)
  {
    Element includeElement = element;

    String existingSchemaLocation = includeElement.getAttribute(XSDConstants.SCHEMALOCATION_ATTRIBUTE);

    beginRecording(XSDEditorPlugin.getXSDString("_UI_LABEL_INCLUDE_CHANGE"), includeElement);
    includeElement.setAttribute(XSDConstants.SCHEMALOCATION_ATTRIBUTE, newLocation);
    
    // If there is no existing schemaLocation, then just set it
    if (existingSchemaLocation == null)
    {
      return;
    }

    XSDConcreteComponent includeComponent = xsdSchema.getCorrespondingComponent(includeElement);
    if (includeComponent instanceof XSDInclude)
     {
      XSDInclude include = (XSDInclude) includeComponent;

      XSDSchema referencedSchema = include.getResolvedSchema();
      if (referencedSchema != null)
      {        
        XSDExternalFileCleanup cleanHelper = new XSDExternalFileCleanup(referencedSchema);
        cleanHelper.visitSchema(xsdSchema);
        
        xsdSchema.update();
        include.updateElement();
      }
      
    }
    endRecording(includeElement);    
  }

  
// Redefine's version
//  protected void updateExternalModel(IFile newFile, String namespace, XSDSchema externalSchema)
//  {
//    Element redefineElement = (Element) getNode();
//
//    redefineElement.setAttribute(XSDConstants.SCHEMALOCATION_ATTRIBUTE, locationField.getText());
//
//    String existingSchemaLocation = redefineElement.getAttribute(XSDConstants.SCHEMALOCATION_ATTRIBUTE);
//
//    // If there is no existing schemaLocation, then just set it and return
//    if (existingSchemaLocation == null)
//     {
//      return;
//    }
//
//    XSDConcreteComponent redefineComponent = getXSDSchema().getCorrespondingComponent(redefineElement);
//    if (redefineComponent instanceof XSDRedefine)
//     {
//      XSDRedefine redefine = (XSDRedefine) redefineComponent;
//      XSDExternalFileCleanup cleanup = new XSDExternalFileCleanup(redefine.getIncorporatedSchema());
//      
//      cleanup.visitSchema(getXSDSchema());
//      if (getEditor() != null)
//       {
//// DisplayErrorInTaskList task = new DisplayErrorInTaskList(getEditor().getEditorIDocument(), getEditor().getFileResource(), cleanup.getMessages());
//// task.run();
//        
//        // Workaround to reset included elements in XSD model
//        getEditor().reparseSchema();
//        getEditor().getGraphViewer().setSchema(getXSDSchema());
//      }
//    }
//
//    /* since we are reparsing, we don't need this
//
//     Iterator contents = getXSDSchema().getContents().iterator();
//     while (contents.hasNext())
//     {
//     XSDSchemaContent content = (XSDSchemaContent)contents.next();
//     if (content instanceof XSDSchemaDirective)
//     {
//     XSDSchemaDirective directive = (XSDSchemaDirective)content;
//
//     if (directive.getSchemaLocation().equals(oldSchemaLocation) && directive instanceof XSDRedefine)
//     {
//     directive.unsetSchemaLocation();
//     directive.setSchemaLocation(locationField.getText());
//     directive.unsetResolvedSchema();
//     redefineElement.setAttribute(XSDConstants.SCHEMALOCATION_ATTRIBUTE, locationField.getText());
//     getXSDSchema().updateDocument();
//     XSDSchemaHelper.updateElement(directive);
////          directive.updateElement();
//     break;
//     }
//     }
//     }
//     */
//  }
  
}
