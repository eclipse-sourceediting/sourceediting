/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
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
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
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
    String string = memento.getString("path");
    path = Path.createPath(string);
  }

  public void saveState(IMemento memento)
  {
    memento.putString("path", path.toString());
  }

  public void update()
  {
    // TODO (cs) not sure what needs to be done here
  }
  static class PathSegment
  {
    final static int ELEMENT = 1;
    final static int TYPE = 2;
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

    static Path createPath(String string)
    {
      Path path = new Path();
      PathSegment segment = null;
      for (StringTokenizer st = new StringTokenizer(string, "/"); st.hasMoreTokens();)
      {
        String token = st.nextToken();
        int kind = -1;
        if (token.equals("element"))
        {
          kind = PathSegment.ELEMENT;
        }
        else if (token.equals("type"))
        {
          kind = PathSegment.TYPE;
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

    static PathSegment computePathSegment(XSDConcreteComponent c)
    {
      if (c instanceof XSDElementDeclaration)
      {
        XSDElementDeclaration ed = (XSDElementDeclaration) c;
        return new PathSegment(PathSegment.ELEMENT, ed.getResolvedElementDeclaration().getName());
      }
      else if (c instanceof XSDTypeDefinition)
      {
        XSDTypeDefinition td = (XSDTypeDefinition) c;
        return new PathSegment(PathSegment.TYPE, td.getName());
      }
      return null;
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
          kind = "element";
        }
        else if (segment.kind == PathSegment.TYPE)
        {
          kind = "type";
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
          XSDElementDeclaration ed = schema.resolveElementDeclaration(segment.name);
          if (ed != null)
          {
            visitElementDeclaration(ed);
          }
        }
        else if (segment.kind == PathSegment.TYPE)
        {
          XSDTypeDefinition td = schema.resolveTypeDefinition(segment.name);
          if (td != null)
          {
            visitTypeDefinition(td);
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

    protected boolean isMatch(String name1, String name2)
    {
      return name1 != null ? name1.equals(name2) : name1 == name2;
    }
  }
}