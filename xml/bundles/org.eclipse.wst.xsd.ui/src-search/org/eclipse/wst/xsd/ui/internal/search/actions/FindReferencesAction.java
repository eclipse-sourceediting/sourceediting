/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.search.actions;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.wst.common.core.search.pattern.QualifiedName;
import org.eclipse.wst.common.core.search.scope.SearchScope;
import org.eclipse.wst.common.core.search.scope.WorkspaceSearchScope;
import org.eclipse.wst.xsd.ui.internal.editor.ISelectionMapper;
import org.eclipse.wst.xsd.ui.internal.search.IXSDSearchConstants;
import org.eclipse.wst.xsd.ui.internal.search.XSDSearchQuery;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
public class FindReferencesAction extends FindAction
{
  public FindReferencesAction(IEditorPart editor)
  {
    super(editor);
  }

  public void setActionDefinitionId(String string)
  {
  }

  /**
   * To be used by subclass in its run() Returns the file where the selection of
   * a component (from the user) occurs ie. Returns the file that the user is
   * currently working on.
   * 
   * @return The IFile representation of the current working file.
   */
  protected IFile getCurrentFile()
  {
    if (editor != null)
    {
      IEditorInput input = editor.getEditorInput();
      if (input instanceof IFileEditorInput)
      {
        IFileEditorInput fileEditorInput = (IFileEditorInput) input;
        return fileEditorInput.getFile();
      }
    }
    return null;
  }

  /**
   * To be used by subclass in its run().. Determines the metaName of the XSD
   * component given to this method.
   * 
   * @param component
   *          The component of which we want to determine the name
   * @return
   */
  protected QualifiedName determineMetaName(XSDNamedComponent component)
  {
    QualifiedName metaName = null;
    if (component instanceof XSDComplexTypeDefinition)
    {
      metaName = IXSDSearchConstants.COMPLEX_TYPE_META_NAME;
    }
    else if (component instanceof XSDSimpleTypeDefinition)
    {
      metaName = IXSDSearchConstants.SIMPLE_TYPE_META_NAME;
    }
    else if (component instanceof XSDElementDeclaration)
    {
      metaName = IXSDSearchConstants.ELEMENT_META_NAME;
    }
    else if (component instanceof XSDModelGroupDefinition)
    {
      metaName = IXSDSearchConstants.GROUP_META_NAME;
    }
    else if (component instanceof XSDAttributeGroupDefinition)
    {
      metaName = IXSDSearchConstants.ATTRIBUTE_GROUP_META_NAME;
    }
    else if (component instanceof XSDAttributeDeclaration)
    {
      metaName = IXSDSearchConstants.ATTRIBUTE_META_NAME;
    }
    return metaName;
  }
  
  protected XSDNamedComponent getXSDNamedComponent()
  {
    if (editor != null)
    {
      ISelectionProvider provider = (ISelectionProvider) editor.getAdapter(ISelectionProvider.class);
      ISelectionMapper mapper = (ISelectionMapper) editor.getAdapter(ISelectionMapper.class);
      if (provider != null)
      {
        ISelection selection = provider.getSelection();
        if (mapper != null)
        {
          selection = mapper.mapSelection(selection);
        }
        if (selection != null && selection instanceof IStructuredSelection)
        {
          IStructuredSelection s = (IStructuredSelection) selection;
          Object o = s.getFirstElement();
          if (o != null && o instanceof XSDNamedComponent)
          {
            return (XSDNamedComponent) o;
          }
        }
      }
    }
    // The expected component we get from the editor does not meet
    // our expectation
    return null;
  }  

  public void run()
  {
    String pattern = "";
    XSDNamedComponent component = getXSDNamedComponent();
    IFile file = getCurrentFile();
    if (file != null && component != null)
    {
      QualifiedName metaName = determineMetaName(component);
      QualifiedName elementQName = new QualifiedName(component.getTargetNamespace(), component.getName());
      SearchScope scope = new WorkspaceSearchScope();
      String scopeDescription = "Workspace";
      XSDSearchQuery searchQuery = new XSDSearchQuery(pattern, file, elementQName, metaName, XSDSearchQuery.LIMIT_TO_REFERENCES, scope, scopeDescription);
      NewSearchUI.activateSearchResultView();
      NewSearchUI.runQueryInBackground(searchQuery);
    }
  }
}
