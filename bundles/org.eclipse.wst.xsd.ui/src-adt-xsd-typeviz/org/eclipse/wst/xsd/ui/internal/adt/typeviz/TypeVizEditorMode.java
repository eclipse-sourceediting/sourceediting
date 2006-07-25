package org.eclipse.wst.xsd.ui.internal.adt.typeviz;

import org.eclipse.gef.EditPartFactory;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.wst.xsd.ui.internal.adt.editor.EditorMode;
import org.eclipse.wst.xsd.ui.internal.adt.outline.ADTContentOutlineProvider;
import org.eclipse.wst.xsd.ui.internal.adt.typeviz.design.figures.TypeVizFigureFactory;
import org.eclipse.wst.xsd.ui.internal.design.editparts.XSDEditPartFactory;

public class TypeVizEditorMode extends EditorMode
{
  private EditPartFactory editPartFactory;
  
  public Object getAdapter(Class adapter)
  {
    return null;
  }

  public String getDisplayName()
  {
    return "Advanced";
  }

  public EditPartFactory getEditPartFactory()
  {
    if (editPartFactory == null)
    {
      editPartFactory = new XSDEditPartFactory(new TypeVizFigureFactory());
    }  
    return editPartFactory;
  }

  public String getId()
  {
    return "org.eclipse.wst.xsd.ui.typeviz";
  }

  public IContentProvider getOutlineProvider()
  {
    return new ADTContentOutlineProvider();
  }
}
