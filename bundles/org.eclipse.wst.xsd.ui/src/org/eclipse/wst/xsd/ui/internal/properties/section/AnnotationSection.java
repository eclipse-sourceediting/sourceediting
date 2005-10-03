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
package org.eclipse.wst.xsd.ui.internal.properties.section;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFacet;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDNotationDeclaration;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDWildcard;
import org.eclipse.xsd.XSDXPathDefinition;
import org.eclipse.xsd.impl.XSDFactoryImpl;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class AnnotationSection extends AbstractSection
{
  DocumentationWorkbookPage documentationPage;
  AppInfoWorkbookPage appInfoPage;
  XSDWorkbook workbook;
  XSDFactory factory;
  
  /**
   * 
   */
  public AnnotationSection()
  {
    super();
    factory = new XSDFactoryImpl();
  }

	/**
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite, org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory)
	 */
	public void createControls(Composite parent, TabbedPropertySheetWidgetFactory factory)
	{
      super.createControls(parent, factory);

      workbook = new XSDWorkbook(parent, SWT.BOTTOM | SWT.FLAT);
    
      documentationPage = new DocumentationWorkbookPage(workbook);
      appInfoPage = new AppInfoWorkbookPage(workbook);

      documentationPage.activate();
      appInfoPage.activate();
      workbook.setSelectedPage(documentationPage);
    }

	/*
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
	 */
	public void refresh()
	{
    if (doRefresh)
    {
      if (documentationPage.getDocumentationText().isFocusControl() || appInfoPage.getAppInfoText().isFocusControl())
      {
        return;
      }
      
      if (isReadOnly)
      {
        documentationPage.setEnabled(false);
        appInfoPage.setEnabled(false);
      }
      else
      {
        documentationPage.setEnabled(true);
        appInfoPage.setEnabled(true);
      }
      setListenerEnabled(false);
	    Object input = getInput();
	    if (input != null)
	    {
	      XSDAnnotation xsdAnnotation = getInputXSDAnnotation(false);
        setInitialText(xsdAnnotation);
      }
      setListenerEnabled(true);
	  }
	}

  
  public void doHandleEvent(Event event)
  {
    Object input = getInput();
    if (input != null)
    {
      XSDAnnotation xsdAnnotation = getInputXSDAnnotation(true);
  
      if (event.widget == documentationPage.getDocumentationText())
      {
        documentationPage.doHandleEvent(xsdAnnotation);
      }
      else if (event.widget == appInfoPage.getAppInfoText())
      {
        appInfoPage.doHandleEvent(xsdAnnotation);
      }
    }
    
  }
  
  protected XSDAnnotation getInputXSDAnnotation(boolean createIfNotExist)
  {
    XSDAnnotation xsdAnnotation = null;
    
    if (input instanceof XSDAttributeDeclaration)
    {
      XSDAttributeDeclaration xsdComp = (XSDAttributeDeclaration)input; 
      xsdAnnotation = xsdComp.getAnnotation();
      if (createIfNotExist && xsdAnnotation == null)
      {
        xsdAnnotation = factory.createXSDAnnotation();
        xsdComp.setAnnotation(xsdAnnotation);
      }
    }
    else if (input instanceof XSDAttributeGroupDefinition)
    {
      XSDAttributeGroupDefinition xsdComp = (XSDAttributeGroupDefinition)input;
      xsdAnnotation = xsdComp.getAnnotation();
      if (createIfNotExist && xsdAnnotation == null)
      {
        xsdAnnotation = factory.createXSDAnnotation();
        xsdComp.setAnnotation(xsdAnnotation);
      }
    }
    else if (input instanceof XSDElementDeclaration)
    {
      XSDElementDeclaration xsdComp = (XSDElementDeclaration)input; 
      xsdAnnotation = xsdComp.getAnnotation();
      if (createIfNotExist && xsdAnnotation == null)
      {
        xsdAnnotation = factory.createXSDAnnotation();
        xsdComp.setAnnotation(xsdAnnotation);
      }
    }
    else if (input instanceof XSDNotationDeclaration)
    {
      XSDNotationDeclaration xsdComp =(XSDNotationDeclaration)input;
      xsdAnnotation = xsdComp.getAnnotation();
      if (createIfNotExist && xsdAnnotation == null)
      {
        xsdAnnotation = factory.createXSDAnnotation();
        xsdComp.setAnnotation(xsdAnnotation);
      }
    }
    else if (input instanceof XSDXPathDefinition)
    {
      XSDXPathDefinition xsdComp = (XSDXPathDefinition)input;
      xsdAnnotation = xsdComp.getAnnotation();
      if (createIfNotExist && xsdAnnotation == null)
      {
        xsdAnnotation = factory.createXSDAnnotation();
        xsdComp.setAnnotation(xsdAnnotation);
      }
    }
    else if (input instanceof XSDModelGroup)
    {
      XSDModelGroup xsdComp = (XSDModelGroup)input;
      xsdAnnotation = xsdComp.getAnnotation();
      if (createIfNotExist && xsdAnnotation == null)
      {
        xsdAnnotation = factory.createXSDAnnotation();
        xsdComp.setAnnotation(xsdAnnotation);
      }
    }
    else if (input instanceof XSDModelGroupDefinition)
    {
      XSDModelGroupDefinition xsdComp = (XSDModelGroupDefinition)input;
      xsdAnnotation = xsdComp.getAnnotation();
      if (createIfNotExist && xsdAnnotation == null)
      {
        xsdAnnotation = factory.createXSDAnnotation();
        xsdComp.setAnnotation(xsdAnnotation);
      }
    }
    else if (input instanceof XSDIdentityConstraintDefinition)
    {
      XSDIdentityConstraintDefinition xsdComp = (XSDIdentityConstraintDefinition)input;
      xsdAnnotation = xsdComp.getAnnotation();
      if (createIfNotExist && xsdAnnotation == null)
      {
        xsdAnnotation = factory.createXSDAnnotation();
        xsdComp.setAnnotation(xsdAnnotation);
      }
    }
    else if (input instanceof XSDWildcard)
    {
      XSDWildcard xsdComp = (XSDWildcard)input;
      xsdAnnotation = xsdComp.getAnnotation();
      if (createIfNotExist && xsdAnnotation == null)
      {
        xsdAnnotation = factory.createXSDAnnotation();
        xsdComp.setAnnotation(xsdAnnotation);
      }
    }
    else if (input instanceof XSDSchema)
    {
      XSDSchema xsdComp = (XSDSchema)input;
      List list = xsdComp.getAnnotations();
      if (list.size() > 0)
      {
        xsdAnnotation = (XSDAnnotation)list.get(0);
      }
      else
      {
        if (createIfNotExist && xsdAnnotation == null)
        {
          xsdAnnotation = factory.createXSDAnnotation();
          if (xsdComp.getContents() != null)
          {
            xsdComp.getContents().add(0, xsdAnnotation);
          }
        }
      }
      return xsdAnnotation;
    }
    else if (input instanceof XSDFacet)
    {
      XSDFacet xsdComp = (XSDFacet)input;
      xsdAnnotation = xsdComp.getAnnotation();
      if (createIfNotExist && xsdAnnotation == null)
      {
        xsdAnnotation = factory.createXSDAnnotation();
        xsdComp.setAnnotation(xsdAnnotation);
      }
    }
    else if (input instanceof XSDTypeDefinition)
    {
      XSDTypeDefinition xsdComp = (XSDTypeDefinition)input;
      xsdAnnotation = xsdComp.getAnnotation();
      if (createIfNotExist && xsdAnnotation == null)
      {
        xsdAnnotation = factory.createXSDAnnotation();
        xsdComp.setAnnotation(xsdAnnotation);
      }
    }
    else if (input instanceof XSDInclude)
    {
      XSDInclude xsdComp = (XSDInclude)input;
      xsdAnnotation = xsdComp.getAnnotation();
      if (createIfNotExist && xsdAnnotation == null)
      {
        xsdAnnotation = factory.createXSDAnnotation();
        xsdComp.setAnnotation(xsdAnnotation);
      }
    }
    else if (input instanceof XSDImport)
    {
      XSDImport xsdComp = (XSDImport)input;
      xsdAnnotation = xsdComp.getAnnotation();
      if (createIfNotExist && xsdAnnotation == null)
      {
        xsdAnnotation = factory.createXSDAnnotation();
        xsdComp.setAnnotation(xsdAnnotation);
      }
    }
    else if (input instanceof XSDRedefine)
    {
      XSDRedefine xsdComp = (XSDRedefine)input;
      List list = xsdComp.getAnnotations();
      if (list.size() > 0)
      {
        xsdAnnotation = (XSDAnnotation)list.get(0);
      }
      else
      {
        if (createIfNotExist && xsdAnnotation == null)
        {
// ?
        }
      }
      return xsdAnnotation;
    }
    else if (input instanceof XSDAnnotation)
    {
      xsdAnnotation = (XSDAnnotation)input;
    }
    
    if (createIfNotExist)
    {
      formatAnnotation(xsdAnnotation);
    }

    return xsdAnnotation;
  }
  
  private void formatAnnotation(XSDAnnotation annotation)
  {
    Element element = annotation.getElement();
    XSDDOMHelper.formatChild(element);
  }
  

  public boolean shouldUseExtraSpace()
  {
    return true;
  }

  public void dispose()
  {
    factory = null;
  }
  
  private void setInitialText(XSDAnnotation an)
  {
    if (documentationPage != null)
    {
      documentationPage.setText(""); //$NON-NLS-1$
    }
    if (appInfoPage != null)
    {
      appInfoPage.setText(""); //$NON-NLS-1$
    }

    if (an != null)
    {
      Element element = an.getElement();
      try
      {
        if (element.hasChildNodes())
         {
          // if the element is Text
          Element docElement = (Element)domHelper.getChildNode(element, XSDConstants.DOCUMENTATION_ELEMENT_TAG);
          if (docElement != null)
          {
            Node node = docElement.getFirstChild();
            if (node instanceof CharacterData)
            {
              documentationPage.setText( ((CharacterData)node).getData());
            }
          }
          
          Element appInfoElement = (Element)domHelper.getChildNode(element, XSDConstants.APPINFO_ELEMENT_TAG);
          if (appInfoElement != null)
          {
            Node node = appInfoElement.getFirstChild();
            if (node instanceof CharacterData)
            {
              appInfoPage.setText( ((CharacterData)node).getData());
            }
          }

        }
      }
      catch (Exception e)
      {
        
      }
    }
  }
  
 
  class DocumentationWorkbookPage extends XSDWorkbookPage
  {
    Text documentationText;
    Composite page1;
    
    public DocumentationWorkbookPage(XSDWorkbook workbook)
    {
      super(workbook);
      this.getTabItem().setText(XSDEditorPlugin.getXSDString("_UI_LABEL_DOCUMENTATION")); 
    }

    public void setText(String value)
    {
      documentationText.setText(value);
    }
    
    public void setEnabled(boolean state)
    {
      page1.setEnabled(state);
    }
    
    public String getText()
    {
      return documentationText.getText();
    }
    
    public Text getDocumentationText()
    {
      return documentationText;
    }
    
    public Control createControl (Composite parent)
    {
      page1 = getWidgetFactory().createFlatFormComposite(parent);
      documentationText = getWidgetFactory().createText(page1, "", SWT.V_SCROLL | SWT.H_SCROLL); //$NON-NLS-1$
      documentationText.addListener(SWT.Modify, AnnotationSection.this);

      FormData data = new FormData();
      data.left = new FormAttachment(0, 0);
      data.right = new FormAttachment(100, 0);
      data.top = new FormAttachment(0, 0);
      data.bottom = new FormAttachment(100, 0);
      documentationText.setLayoutData(data);
                  
      return page1;
    }
    
    public void doHandleEvent(XSDAnnotation xsdAnnotation)
    {
      if (xsdAnnotation != null)
      {
        Element element = xsdAnnotation.getElement();
        List documentationList = xsdAnnotation.getUserInformation();
        Element documentationElement = null;
        if (documentationList.size() > 0)
        {
          documentationElement = (Element)documentationList.get(0);
        }
        
        beginRecording(XSDEditorPlugin.getXSDString("_UI_DOCUMENTATION_COMMENT_CHANGE"), element); //$NON-NLS-1$
        
        if (documentationElement == null)
        {
          documentationElement = xsdAnnotation.createUserInformation(null);
          element.appendChild(documentationElement);
          XSDDOMHelper.formatChild(documentationElement);
          // Defect in model....I create it but the model object doesn't appear to be updated
          // Notice that it is fine for appinfo
          xsdAnnotation.updateElement();
          xsdAnnotation.setElement(element);
        }
        
        String newValue = documentationText.getText();
        if (documentationElement != null)
        {
          try
          {
            if (documentationElement.hasChildNodes())
            {
            // if the element is Text
              Node node = documentationElement.getFirstChild();
              if (node instanceof CharacterData)
              {
                ((CharacterData)node).setData(newValue);
              }
            }
            else
            {
              if (newValue.length() > 0)
              {
                Node childNode = documentationElement.getOwnerDocument().createTextNode(newValue);
                documentationElement.appendChild(childNode);
              }
            }
          }
          catch (Exception e)
          {
          
          }
        }
        endRecording(element);
      }    
    }
  }
  
  class AppInfoWorkbookPage extends XSDWorkbookPage
  {
    Text appInfoText;
    Composite page2;
    
    public AppInfoWorkbookPage(XSDWorkbook workbook)
    {
      super(workbook);
      this.getTabItem().setText(XSDEditorPlugin.getXSDString("_UI_LABEL_APP_INFO"));
    }
    
    public void setText(String value)
    {
      appInfoText.setText(value);
    }

    public String getText()
    {
      return appInfoText.getText();
    }

    public Text getAppInfoText()
    {
      return appInfoText;
    }

    public void setEnabled(boolean state)
    {
      page2.setEnabled(state);
    }

    public Control createControl (Composite parent)
    {
      page2 = getWidgetFactory().createFlatFormComposite(parent);
      appInfoText = getWidgetFactory().createText(page2, "", SWT.V_SCROLL | SWT.H_SCROLL); //$NON-NLS-1$
      appInfoText.addListener(SWT.Modify, AnnotationSection.this);
      
      FormData data = new FormData();
      data.left = new FormAttachment(0, 0);
      data.right = new FormAttachment(100, 0);
      //data.top = new FormAttachment(documentationText, +ITabbedPropertyConstants.HSPACE);
      data.top = new FormAttachment(0, 0);
      data.bottom = new FormAttachment(100, 0);
      appInfoText.setLayoutData(data);

      return page2;
    }
    
    public void doHandleEvent(XSDAnnotation xsdAnnotation)
    {
      if (xsdAnnotation != null)
      {
        Element element = xsdAnnotation.getElement();
        List appInfoList = xsdAnnotation.getApplicationInformation();
        
        Element appInfoElement = null;
        if (appInfoList.size() > 0)
        {
          appInfoElement = (Element)appInfoList.get(0);
        }

        beginRecording(XSDEditorPlugin.getXSDString("_UI_COMMENT_CHANGE"), element); //$NON-NLS-1$
        if (appInfoElement == null)
        {
          appInfoElement = xsdAnnotation.createApplicationInformation(null);
          element.appendChild(appInfoElement);
          XSDDOMHelper.formatChild(appInfoElement);
        }
        
        String newValue = appInfoText.getText();
        if (appInfoElement != null)
        {
          try
          {
            if (appInfoElement.hasChildNodes())
            {
              // if the element is Text
              Node node = appInfoElement.getFirstChild();
              if (node instanceof CharacterData)
              {
                ((CharacterData)node).setData(newValue);
              }
            }
            else
            {
              if (newValue.length() > 0)
              {
                Node childNode = appInfoElement.getOwnerDocument().createTextNode(newValue);
                appInfoElement.appendChild(childNode);
              }
            }
          }
          catch (Exception e)
          {

          }
          endRecording(element);          
        }
      }    
    }
  }

}
