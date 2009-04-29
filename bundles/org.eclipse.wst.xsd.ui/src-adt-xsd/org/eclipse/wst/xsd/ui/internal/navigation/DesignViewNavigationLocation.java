/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.navigation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.INavigationLocation;
import org.eclipse.ui.NavigationLocation;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDAdapterFactory;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDVisitor;
import org.eclipse.wst.xsd.ui.internal.adt.design.DesignViewGraphicalViewer;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObject;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import com.ibm.icu.util.StringTokenizer;

/**
 * This class exists to support navigation in a context where there is no text 
 * editor page.  In these cases we can't rely on the TextSelectionNavigationLocations
 * so we this class which is designed to work with just the design view.
 */
public class DesignViewNavigationLocation extends NavigationLocation
{
  protected Path path;

  private static final String PATH_TAG = "path"; //$NON-NLS-1$
  
  public DesignViewNavigationLocation(IEditorPart part)
  {
    super(part);
    this.path = new Path();
  }
  
  public DesignViewNavigationLocation(IEditorPart part, XSDConcreteComponent component)
  {
    super(part);   
    this.path = Path.computePath(component);  
  }

  public boolean mergeInto(INavigationLocation currentLocation)
  {
    boolean result = false;
    if (currentLocation instanceof DesignViewNavigationLocation)
    {
      DesignViewNavigationLocation loc = (DesignViewNavigationLocation) currentLocation;
      result = loc.path.toString().equals(path.toString());
    }
    else
    {
    }
    return result;
  }

  public void restoreLocation()
  {
    XSDSchema schema = (XSDSchema) getEditorPart().getAdapter(XSDSchema.class);
    Object viewer = getEditorPart().getAdapter(GraphicalViewer.class);
    if (viewer instanceof DesignViewGraphicalViewer)
    {
      DesignViewGraphicalViewer graphicalViewer = (DesignViewGraphicalViewer) viewer;
      XSDConcreteComponent component = Path.computeComponent(schema, path);
      if (component != null)
      {
        Adapter adapter = XSDAdapterFactory.getInstance().adapt(component);
        if (adapter instanceof IADTObject)
        {
          graphicalViewer.setInput((IADTObject)adapter);
        }
      }
      else if (path.segments.isEmpty())
      {
        Adapter adapter = XSDAdapterFactory.getInstance().adapt(schema);
        if (adapter instanceof IADTObject)
        {
          graphicalViewer.setInput((IADTObject)adapter);
        }
      }
    }   
  }

  public void restoreState(IMemento memento)
  {
    String string = memento.getString(PATH_TAG);
    path = Path.createPath(string);
  }

  public void saveState(IMemento memento)
  {
    memento.putString(PATH_TAG, path.toString());
  }

  public void update()
  {
    // TODO (cs) not sure what needs to be done here
  }
  static class PathSegment
  {
    final static int ELEMENT = 1;
    final static int TYPE = 2;
    final static int MODEL_GROUP = 3;
    final static int ATTRIBUTE_GROUP = 4;
    final static int REDEFINE = 5;
    int kind;
    String name;

    PathSegment()
    {
    }

    PathSegment(int kind, String name)
    {
      this.kind = kind;
      this.name = name;
    }
  }
  protected static class Path
  {
    private static final String REDEFINE_TOKEN = "redefine"; //$NON-NLS-1$
    private static final String MODEL_GROUP_TOKEN = "modelGroup"; //$NON-NLS-1$
    private static final String ATTRIBUTE_GROUP_TOKEN = "attributeGroup"; //$NON-NLS-1$
    private static final String TYPE_TOKEN = "type"; //$NON-NLS-1$
    private static final String ELEMENT_TOKEN = "element"; //$NON-NLS-1$

    List segments = new ArrayList();

    public static XSDConcreteComponent computeComponent(XSDSchema schema, Path path)
    {
      PathResolvingXSDVisitor visitor = new PathResolvingXSDVisitor(path);
      visitor.visitSchema(schema);
      if (visitor.isDone())
      {
        return visitor.result;
      }
      return null;
    }

    static Path createPath(String pathString)
    {
      Path path = new Path();
      PathSegment segment = null;
      for (StringTokenizer st = new StringTokenizer(pathString, "/"); st.hasMoreTokens();)
      {
        String token = st.nextToken();
        int kind = -1;
        if (token.startsWith(ELEMENT_TOKEN))
        {
          kind = PathSegment.ELEMENT;
        }
        else if (token.startsWith(TYPE_TOKEN))
        {
          kind = PathSegment.TYPE;
        }
        else if (token.startsWith(ATTRIBUTE_GROUP_TOKEN))
        {
        	kind = PathSegment.ATTRIBUTE_GROUP;
        }
        else if (token.startsWith(MODEL_GROUP_TOKEN))
        {
        	kind = PathSegment.MODEL_GROUP;
        }
        else if (token.startsWith(REDEFINE_TOKEN))
        {
        	kind = PathSegment.REDEFINE;
        }
        if (kind != -1)
        {
          segment = new PathSegment();
          segment.kind = kind;
          path.segments.add(segment);
          String namePattern = "[@name='";
          int startIndex = token.indexOf(namePattern);
          if (startIndex != -1)
          {
            startIndex += namePattern.length();
            int endIndex = token.indexOf("']");
            if (endIndex != -1)
            {
              segment.name = token.substring(startIndex, endIndex);
            }
          }
        }
      }
      return path;
    }

    public static Path computePath(XSDConcreteComponent component)
    {
      Path path = new Path();
      for (EObject c = component; c != null; c = c.eContainer())
      {
        if (c instanceof XSDConcreteComponent)
        {
          PathSegment segment = computePathSegment((XSDConcreteComponent) c);
          if (segment != null)
          {
            path.segments.add(0, segment);
          }
        }
      }
      return path;
    }

    static PathSegment computePathSegment(XSDConcreteComponent component)
    {
    	PathSegment pathSegment = null;
    	if (component instanceof XSDElementDeclaration)
    	{
    		XSDElementDeclaration elementDeclaration = (XSDElementDeclaration) component;
    		pathSegment = new PathSegment(PathSegment.ELEMENT, elementDeclaration.getResolvedElementDeclaration().getName());
    	}
    	else if (component instanceof XSDTypeDefinition)
    	{
    		XSDTypeDefinition typeDefinition = (XSDTypeDefinition) component;
    		pathSegment = new PathSegment(PathSegment.TYPE, typeDefinition.getName());
    	}
    	else if (component instanceof XSDModelGroupDefinition)
    	{
    		XSDModelGroupDefinition modelGroupDefinition = (XSDModelGroupDefinition) component;
    		pathSegment = new PathSegment(PathSegment.MODEL_GROUP, modelGroupDefinition.getName());
    	}
    	else if (component instanceof XSDAttributeGroupDefinition)
    	{
    		XSDAttributeGroupDefinition attributeGroupDefinition = (XSDAttributeGroupDefinition) component;
    		pathSegment = new PathSegment(PathSegment.ATTRIBUTE_GROUP, attributeGroupDefinition.getResolvedAttributeGroupDefinition().getName());
    	}
    	else if (component instanceof XSDRedefine)
    	{
    		XSDRedefine redefine = (XSDRedefine) component;
    		pathSegment = new PathSegment(PathSegment.REDEFINE, redefine.getSchemaLocation());
    	}
    	return pathSegment;
    }

    public String toString()
    {
      StringBuffer b = new StringBuffer();
      for (Iterator i = segments.iterator(); i.hasNext();)
      {
        PathSegment segment = (PathSegment) i.next();
        String kind = "";
        if (segment.kind == PathSegment.ELEMENT)
        {
          kind = ELEMENT_TOKEN;
        }
        else if (segment.kind == PathSegment.TYPE)
        {
          kind = TYPE_TOKEN;
        }
        else if (segment.kind == PathSegment.MODEL_GROUP)
        {
        	kind = MODEL_GROUP_TOKEN;
        }
        else if (segment.kind == PathSegment.ATTRIBUTE_GROUP)
        {
        	kind = ATTRIBUTE_GROUP_TOKEN;
        }
        else if (segment.kind == PathSegment.REDEFINE)
        {
        	kind = REDEFINE_TOKEN;
        }
        b.append(kind);
        if (segment.name != null)
        {
          b.append("[@name='" + segment.name + "']");
        }
        if (i.hasNext())
        {
          b.append("/");
        }
      }
      return b.toString();
    }
  }
  
  
  static class PathResolvingXSDVisitor extends XSDVisitor
  {
    Path path;
    int index = -1;
    PathSegment segment;
    XSDConcreteComponent result = null;

    PathResolvingXSDVisitor(Path path)
    {
      this.path = path;
      incrementSegment();
    }

    boolean isDone()
    {
      return index >= path.segments.size();
    }

    void incrementSegment()
    {
      index++;
      if (index < path.segments.size())
      {
        segment = (PathSegment) path.segments.get(index);
      }
      else
      {
        segment = null;
      }
    }

    public void visitSchema(XSDSchema schema)
    {
      if (segment != null)
      {
        if (segment.kind == PathSegment.ELEMENT)
        {
          XSDElementDeclaration elementDeclaration = schema.resolveElementDeclaration(segment.name);
          if (elementDeclaration != null)
          {
            visitElementDeclaration(elementDeclaration);
          }
        }
        else if (segment.kind == PathSegment.TYPE)
        {
          XSDTypeDefinition typeDefinition = schema.resolveTypeDefinition(segment.name);
          if (typeDefinition != null)
          {
            visitTypeDefinition(typeDefinition);
          }
        }
        else if (segment.kind == PathSegment.MODEL_GROUP)
        {
        	XSDModelGroupDefinition modelGroupDefinition = schema.resolveModelGroupDefinition(segment.name);
        	if (modelGroupDefinition != null)
        	{
        		visitModelGroupDefinition(modelGroupDefinition);
        	}
        }
        else if (segment.kind == PathSegment.ATTRIBUTE_GROUP)
        {
        	XSDAttributeGroupDefinition attributeGroupDefinition = schema.resolveAttributeGroupDefinition(segment.name);
        	if (attributeGroupDefinition != null)
        	{
        		visitAttributeGroupDefinition(attributeGroupDefinition);
        	}
        }
        else if (segment.kind == PathSegment.REDEFINE)
        {
        	Iterator iterator = schema.getContents().iterator();
        	while (iterator.hasNext())
        	{
        		Object object = iterator.next();
        		if (object instanceof XSDRedefine)
        		{        			
        			XSDRedefine redefine = (XSDRedefine)object;
        			visitRedefine(redefine);
        		}
        	}
        }
      }
    }

    public void visitElementDeclaration(XSDElementDeclaration element)
    {
      if (segment != null)
      {
        String name = element.getResolvedElementDeclaration().getName();
        if (segment.kind == PathSegment.ELEMENT && isMatch(segment.name, name))
        {
          result = element;
          incrementSegment();
          if (!isDone())
          {
            super.visitElementDeclaration(element);
          }
        }
      }
    }

    public void visitTypeDefinition(XSDTypeDefinition type)
    {
      if (segment != null)
      {
        String name = type.getName();
        if (segment.kind == PathSegment.TYPE && isMatch(segment.name, name))
        {
          result = type;
          incrementSegment();
          if (!isDone())
          {
            super.visitTypeDefinition(type);
          }
        }
      }
    }
    
    public void visitModelGroupDefinition(XSDModelGroupDefinition modelGroup)
    {
    	if (segment != null)
    	{
    		String name = modelGroup.getName();
    		if (segment.kind == PathSegment.MODEL_GROUP && isMatch(segment.name, name))
    		{
    			result = modelGroup;
    			incrementSegment();
    			if (!isDone())
    			{
    				super.visitModelGroupDefinition(modelGroup);
    			}
    		}
    	}
    }

    public void visitAttributeGroupDefinition(XSDAttributeGroupDefinition attributeGroup)
    {
    	if (segment != null)
    	{
    		String name = attributeGroup.getName();
    		if (segment.kind == PathSegment.ATTRIBUTE_GROUP && isMatch(segment.name, name))
    		{
    			result = attributeGroup;
    			incrementSegment();
    			if (!isDone())
    			{
    				super.visitAttributeGroupDefinition(attributeGroup);
    			}
    		}
    	}
    }
    
    public void visitRedefine(XSDRedefine redefine)
    {
    	if (segment != null)
    	{
    		String name = redefine.getSchemaLocation();
    		if (segment.kind == PathSegment.REDEFINE && isMatch(segment.name, name))
    		{
    			result = redefine;
    			incrementSegment();
    			if (!isDone())
    			{
    				visitSchema(redefine.getSchema());
    			}
    		}
    	}
    	
    }
    protected boolean isMatch(String name1, String name2)
    {
      return name1 != null ? name1.equals(name2) : name1 == name2;
    }
  }
}