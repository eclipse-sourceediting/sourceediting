/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Naoki Akiyama, Fujitsu - Bug 244901 - Cannot set xsd:annotation to 
 *                              xsd:redefine by Properties view.
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatProcessor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;
import org.eclipse.wst.xsd.ui.internal.adt.design.ImageOverlayDescriptor;
import org.eclipse.wst.xsd.ui.internal.common.properties.sections.AbstractSection;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDEnumerationFacet;
import org.eclipse.xsd.XSDFacet;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDFeature;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDNotationDeclaration;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDWildcard;
import org.eclipse.xsd.XSDXPathDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XSDCommonUIUtils
{
  public XSDCommonUIUtils()
  {
    super();
  }
  
  public static Image getUpdatedImage(XSDConcreteComponent input, Image baseImage, boolean isReadOnly)
  {
    XSDAnnotation xsdAnnotation = getInputXSDAnnotation(input, false);
    
    if (xsdAnnotation != null)
    {
      if (xsdAnnotation.getApplicationInformation().size() > 0)
      {
        // Will use the class name appended by the read only state as the name of the image.
        // There is a disabled and an enabled version of each baseImage, so we can't simply
        // use the component name as the name of the image
        String imageName = input.getClass().getName() + isReadOnly;
        Image newImage = XSDEditorPlugin.getDefault().getImageRegistry().get(imageName);
        if (newImage == null)
        {
          ImageOverlayDescriptor ovr = new ImageOverlayDescriptor(baseImage, isReadOnly);
          newImage = ovr.getImage();
          XSDEditorPlugin.getDefault().getImageRegistry().put(imageName, newImage);
        }
        return newImage;
      }
    }
    return baseImage;
  }

  public static XSDAnnotation getInputXSDAnnotation(XSDConcreteComponent input, boolean createIfNotExist)
  {
    XSDAnnotation xsdAnnotation = null;
    XSDFactory factory = XSDFactory.eINSTANCE;
    if (input instanceof XSDAttributeDeclaration)
    {
      XSDAttributeDeclaration xsdComp = (XSDAttributeDeclaration) input;
      xsdAnnotation = xsdComp.getAnnotation();
      if (createIfNotExist && xsdAnnotation == null)
      {
        xsdAnnotation = factory.createXSDAnnotation();
        xsdComp.setAnnotation(xsdAnnotation);
      }
    }
    else if (input instanceof XSDAttributeGroupDefinition)
    {
      XSDAttributeGroupDefinition xsdComp = (XSDAttributeGroupDefinition) input;
      xsdAnnotation = xsdComp.getAnnotation();
      if (createIfNotExist && xsdAnnotation == null)
      {
        xsdAnnotation = factory.createXSDAnnotation();
        xsdComp.setAnnotation(xsdAnnotation);
      }
    }
    else if (input instanceof XSDElementDeclaration)
    {
      XSDElementDeclaration xsdComp = (XSDElementDeclaration) input;
      xsdAnnotation = xsdComp.getAnnotation();
      if (createIfNotExist && xsdAnnotation == null)
      {
        xsdAnnotation = factory.createXSDAnnotation();
        xsdComp.setAnnotation(xsdAnnotation);
      }
    }
    else if (input instanceof XSDNotationDeclaration)
    {
      XSDNotationDeclaration xsdComp = (XSDNotationDeclaration) input;
      xsdAnnotation = xsdComp.getAnnotation();
      if (createIfNotExist && xsdAnnotation == null)
      {
        xsdAnnotation = factory.createXSDAnnotation();
        xsdComp.setAnnotation(xsdAnnotation);
      }
    }
    else if (input instanceof XSDXPathDefinition)
    {
      XSDXPathDefinition xsdComp = (XSDXPathDefinition) input;
      xsdAnnotation = xsdComp.getAnnotation();
      if (createIfNotExist && xsdAnnotation == null)
      {
        xsdAnnotation = factory.createXSDAnnotation();
        xsdComp.setAnnotation(xsdAnnotation);
      }
    }
    else if (input instanceof XSDModelGroup)
    {
      XSDModelGroup xsdComp = (XSDModelGroup) input;
      xsdAnnotation = xsdComp.getAnnotation();
      if (createIfNotExist && xsdAnnotation == null)
      {
        xsdAnnotation = factory.createXSDAnnotation();
        xsdComp.setAnnotation(xsdAnnotation);
      }
    }
    else if (input instanceof XSDModelGroupDefinition)
    {
      XSDModelGroupDefinition xsdComp = (XSDModelGroupDefinition) input;
      xsdAnnotation = xsdComp.getAnnotation();
      if (createIfNotExist && xsdAnnotation == null)
      {
        xsdAnnotation = factory.createXSDAnnotation();
        xsdComp.setAnnotation(xsdAnnotation);
      }
    }
    else if (input instanceof XSDIdentityConstraintDefinition)
    {
      XSDIdentityConstraintDefinition xsdComp = (XSDIdentityConstraintDefinition) input;
      xsdAnnotation = xsdComp.getAnnotation();
      if (createIfNotExist && xsdAnnotation == null)
      {
        xsdAnnotation = factory.createXSDAnnotation();
        xsdComp.setAnnotation(xsdAnnotation);
      }
    }
    else if (input instanceof XSDWildcard)
    {
      XSDWildcard xsdComp = (XSDWildcard) input;
      xsdAnnotation = xsdComp.getAnnotation();
      if (createIfNotExist && xsdAnnotation == null)
      {
        xsdAnnotation = factory.createXSDAnnotation();
        xsdComp.setAnnotation(xsdAnnotation);
      }
    }
    else if (input instanceof XSDSchema)
    {
      XSDSchema xsdComp = (XSDSchema) input;
      List list = xsdComp.getAnnotations();
      if (list.size() > 0)
      {
        xsdAnnotation = (XSDAnnotation) list.get(0);
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
      XSDFacet xsdComp = (XSDFacet) input;
      xsdAnnotation = xsdComp.getAnnotation();
      if (createIfNotExist && xsdAnnotation == null)
      {
        xsdAnnotation = factory.createXSDAnnotation();
        xsdComp.setAnnotation(xsdAnnotation);
      }
    }
    else if (input instanceof XSDTypeDefinition)
    {
      XSDTypeDefinition xsdComp = (XSDTypeDefinition) input;
      xsdAnnotation = xsdComp.getAnnotation();
      if (createIfNotExist && xsdAnnotation == null)
      {
        xsdAnnotation = factory.createXSDAnnotation();
        xsdComp.setAnnotation(xsdAnnotation);
      }
    }
    else if (input instanceof XSDInclude)
    {
      XSDInclude xsdComp = (XSDInclude) input;
      xsdAnnotation = xsdComp.getAnnotation();
      if (createIfNotExist && xsdAnnotation == null)
      {
        xsdAnnotation = factory.createXSDAnnotation();
        xsdComp.setAnnotation(xsdAnnotation);
      }
    }
    else if (input instanceof XSDImport)
    {
      XSDImport xsdComp = (XSDImport) input;
      xsdAnnotation = xsdComp.getAnnotation();
      if (createIfNotExist && xsdAnnotation == null)
      {
        xsdAnnotation = factory.createXSDAnnotation();
        xsdComp.setAnnotation(xsdAnnotation);
      }
    }
    else if (input instanceof XSDRedefine)
    {
      XSDRedefine xsdComp = (XSDRedefine) input;
      List contents = xsdComp.getContents(); 
      for (int i = 0; i < contents.size(); i++)
      { 
        Object content = contents.get(i);
        if (content instanceof XSDAnnotation)
        {
          xsdAnnotation = (XSDAnnotation) content;
          break;
        }
      }
      if (createIfNotExist && xsdAnnotation == null)
      {
        xsdAnnotation = factory.createXSDAnnotation();
        contents.add(0, xsdAnnotation);
      }
      return xsdAnnotation;
    }
    else if (input instanceof XSDAnnotation)
    {
      xsdAnnotation = (XSDAnnotation) input;
    }

    if (createIfNotExist)
    {
      formatAnnotation(xsdAnnotation);
    }

    return xsdAnnotation;
  }

  private static void formatAnnotation(XSDAnnotation annotation)
  {
    Element element = annotation.getElement();
    formatChild(element);
  }

  public static void formatChild(Node child)
  {
    if (child instanceof IDOMNode)
    {
      IDOMModel model = ((IDOMNode) child).getModel();
      try
      {
        // tell the model that we are about to make a big model change
        model.aboutToChangeModel();

        IStructuredFormatProcessor formatProcessor = new FormatProcessorXML();
        formatProcessor.formatNode(child);
      }
      finally
      {
        // tell the model that we are done with the big model change
        model.changedModel();
      }
    }
  }

  public static String createUniqueElementName(String prefix, List elements)
  {
    ArrayList usedNames = new ArrayList();
    for (Iterator i = elements.iterator(); i.hasNext();)
    {
      usedNames.add(getDisplayName((XSDNamedComponent) i.next()));
    }

    int i = 1;
    String testName = prefix;
    while (usedNames.contains(testName))
    {
      testName = prefix + i++;
    }
    return testName;
  }
  
  public static String createUniqueEnumerationValue(String prefix, List elements)
  {
    ArrayList usedNames = new ArrayList();
    for (Iterator i = elements.iterator(); i.hasNext();)
    {
      usedNames.add(((XSDEnumerationFacet) i.next()).getLexicalValue());
    }

    int i = 1;
    String testName = prefix;
    while (usedNames.contains(testName))
    {
      testName = prefix + i++;
    }
    return testName;
  }

  public static String getDisplayName(XSDNamedComponent component)
  {
    if (component instanceof XSDTypeDefinition)
      return getDisplayNameFromXSDType((XSDTypeDefinition) component);

    if (component instanceof XSDFeature)
    {
      XSDFeature feature = (XSDFeature) component;
      if (feature.getName() != null)
        return feature.getName();
      else if (feature.getResolvedFeature() != null && feature.getResolvedFeature().getName() != null)
        return feature.getResolvedFeature().getName();
    }

    return component.getName();

  }

  public static String getDisplayNameFromXSDType(XSDTypeDefinition type)
  {
    return getDisplayNameFromXSDType(type, true);
  }

  public static String getDisplayNameFromXSDType(XSDTypeDefinition type, boolean returnPrimitiveParents)
  {
    if (type == null)
      return null;

    if (type.getName() == null || type.getName().length() == 0)
    {
      if (returnPrimitiveParents && isPrimitiveType(type))
      {
        return getDisplayNameFromXSDType(type.getBaseType());
      }

      EObject container = type.eContainer();

      while (container != null)
      {
        if (container instanceof XSDNamedComponent && ((XSDNamedComponent) container).getName() != null)
        {
          return ((XSDNamedComponent) container).getName();
        }
        container = container.eContainer();
      }
      return null;
    }
    else
      return type.getName();
  }

  public static boolean isPrimitiveType(XSDTypeDefinition type)
  {
    if (type instanceof XSDComplexTypeDefinition)
      return false;

    XSDTypeDefinition baseType = null;
    if (type != null)
    {
      baseType = type.getBaseType();
      XSDTypeDefinition origType = baseType; // KC: although invalid, we need to
                                            // prevent cycles and to avoid an
                                            // infinite loop
      while (baseType != null && !XSDConstants.isAnySimpleType(baseType) && !XSDConstants.isAnyType(baseType) && origType != baseType)
      {
        type = baseType;
        baseType = type.getBaseType();
      }
      baseType = type;
    }
    else
    {
      return false;
    }

    return (XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001.equals(baseType.getTargetNamespace()));
  }

  public static XSDSimpleTypeDefinition getAnonymousSimpleType(XSDFeature input, XSDSimpleTypeDefinition xsdSimpleTypeDefinition)
  {
    XSDSimpleTypeDefinition anonymousSimpleType = null;
    XSDTypeDefinition localType = null;

    if (input instanceof XSDElementDeclaration)
    {
      localType = ((XSDElementDeclaration) input).getAnonymousTypeDefinition();
    }
    else if (input instanceof XSDAttributeDeclaration)
    {
      localType = ((XSDAttributeDeclaration) input).getAnonymousTypeDefinition();
    }

    if (localType instanceof XSDSimpleTypeDefinition)
    {
      anonymousSimpleType = (XSDSimpleTypeDefinition) localType;
    }
    return anonymousSimpleType;
  }

  // bug246036 - check for cyclic groups.  We should refactor out this static class.
  private static Stack visitedGroups = new Stack();
  
  public static void resetVisitedGroupsStack()
  {
    visitedGroups.clear();
  }

  public static List getChildElements(XSDModelGroup group)
  {
    List children = new ArrayList();
    visitedGroups.push(group);
    if (group == null) return children;
    
    for (Iterator i = group.getContents().iterator(); i.hasNext();)
    {
      XSDParticle next = (XSDParticle) i.next();
      if (next.getContent() instanceof XSDFeature)
      {
        if (children.contains(next.getContent())) break;
        children.add(next.getContent());
      }
      else if (next.getTerm() instanceof XSDModelGroup)
      {
        if (!visitedGroups.contains(group))
        children.addAll(getChildElements((XSDModelGroup) next.getTerm()));
      }
    }
    visitedGroups.pop();
    return children;
  }

  public static List getAllAttributes(XSDComplexTypeDefinition xsdComplexType)
  {
    List attributes = getChildElements(xsdComplexType);
    attributes.addAll(getChildAttributes(xsdComplexType));

    return attributes;
  }

  public static List getAllAttributes(XSDModelGroupDefinition xsdModelGroupDefinition)
  {
    List attributes = getChildElements(xsdModelGroupDefinition);

    return attributes;
  }
  
  public static List getInheritedAttributes(XSDComplexTypeDefinition ct)
  {
    List attrs = new ArrayList();
    XSDTypeDefinition parent = ct.getBaseTypeDefinition();
    if (parent != null && parent instanceof XSDComplexTypeDefinition && ct.isSetDerivationMethod())
    {
      attrs.addAll(getAllAttributes((XSDComplexTypeDefinition) parent));
      if (! ct.isCircular()) attrs.addAll(getInheritedAttributes((XSDComplexTypeDefinition) parent));
    }

    return attrs;
  }

  public static List getChildElements(XSDComplexTypeDefinition ct)
  {
    return getChildElements(getModelGroup(ct));
  }
  
  public static List getChildElements(XSDModelGroupDefinition xsdModelGroupDefinition)
  {
    return getChildElements(xsdModelGroupDefinition.getModelGroup());
  }
  
  public static XSDModelGroup getModelGroup(XSDComplexTypeDefinition cType)
  {
    XSDParticle particle = cType.getComplexType();

    if (particle == null || particle.eContainer() != cType)
      return null;

    Object particleContent = particle.getContent();
    XSDModelGroup group = null;

    if (particleContent instanceof XSDModelGroupDefinition)
      group = ((XSDModelGroupDefinition) particleContent).getResolvedModelGroupDefinition().getModelGroup();
    else if (particleContent instanceof XSDModelGroup)
      group = (XSDModelGroup) particleContent;

    if (group == null)
      return null;

    if (group.getContents().isEmpty() || group.eResource() != cType.eResource())
    {
      XSDComplexTypeContent content = cType.getContent();
      if (content instanceof XSDParticle)
        group = (XSDModelGroup) ((XSDParticle) content).getContent();
    }

    return group;
  }

  public static List getChildAttributes(XSDComplexTypeDefinition ct)
  {
    EList attrContents = ct.getAttributeContents();
    List attrs = new ArrayList();
    for (int i = 0; i < attrContents.size(); i++)
    {
      Object next = attrContents.get(i);

      if (next instanceof XSDAttributeUse)
      {
        attrs.add(((XSDAttributeUse) next).getContent().getResolvedAttributeDeclaration());
      }
      else if (next instanceof XSDAttributeGroupDefinition)
      {
        //XSDAttributeGroupDefinition attrGroup = (XSDAttributeGroupDefinition) next;
      }
    }
    return attrs;
  }

  public static Image getImageWithErrorOverlay(XSDConcreteComponent input, Image baseImage, boolean isReadOnly)
  {
    Image extensionImage = getUpdatedImage(input, baseImage, isReadOnly);
    ImageDescriptor errorOverlay = XSDEditorPlugin.getImageDescriptor("icons/ovr16/error_ovr.gif");  //$NON-NLS-1$
    if (baseImage == extensionImage)
    {
      String imageName = input.getClass().getName() + "_error_" + isReadOnly;
      Image newImage = XSDEditorPlugin.getDefault().getImageRegistry().get(imageName);
      if (newImage == null)
      {
        DecorationOverlayIcon ovr = new DecorationOverlayIcon(baseImage, errorOverlay, IDecoration.TOP_LEFT);
        newImage = ovr.createImage();
        XSDEditorPlugin.getDefault().getImageRegistry().put(imageName, newImage);
      }
      return newImage;
    }
    else
    {
      String imageName = input.getClass().getName() + "_extension_error_" + isReadOnly;
      Image newImage = XSDEditorPlugin.getDefault().getImageRegistry().get(imageName);
      if (newImage == null)
      {
        DecorationOverlayIcon ovr = new DecorationOverlayIcon(extensionImage, errorOverlay, IDecoration.TOP_LEFT);
        newImage = ovr.createImage();
        XSDEditorPlugin.getDefault().getImageRegistry().put(imageName, newImage);
      }
      return newImage;
    }
  }

  public static CCombo getNewPropertiesCombo(Composite composite, TabbedPropertySheetWidgetFactory widgetFactory,
      AbstractSection listener, String[] items, String helpID)
  {
    CCombo combo = widgetFactory.createCCombo(composite);
    combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    combo.setEditable(false);
    combo.setItems(items);
    combo.addSelectionListener(listener);    
    PlatformUI.getWorkbench().getHelpSystem().setHelp(combo, helpID);
    return combo;
  }
  
  public static CLabel getNewPropertiesLabel(Composite composite, TabbedPropertySheetWidgetFactory widgetFactory,
      String labelText)
  {
    // Create label
    CLabel label;    
    label = widgetFactory.createCLabel(composite, labelText);
    GridData data = new GridData();
    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;
    label.setLayoutData(data);
    
    return label;
  }
}
