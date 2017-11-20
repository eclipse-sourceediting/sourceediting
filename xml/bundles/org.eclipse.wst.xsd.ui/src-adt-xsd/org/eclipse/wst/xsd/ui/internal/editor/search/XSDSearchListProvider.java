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
package org.eclipse.wst.xsd.ui.internal.editor.search;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.wst.common.core.search.SearchEngine;
import org.eclipse.wst.common.core.search.SearchMatch;
import org.eclipse.wst.common.core.search.SearchParticipant;
import org.eclipse.wst.common.core.search.SearchPlugin;
import org.eclipse.wst.common.core.search.SearchRequestor;
import org.eclipse.wst.common.core.search.pattern.QualifiedName;
import org.eclipse.wst.common.core.search.pattern.SearchPattern;
import org.eclipse.wst.common.core.search.scope.SearchScope;
import org.eclipse.wst.common.ui.internal.search.dialogs.IComponentList;
import org.eclipse.wst.common.ui.internal.search.dialogs.IComponentSearchListProvider;
import org.eclipse.wst.xml.core.internal.search.XMLComponentDeclarationPattern;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.search.IXSDSearchConstants;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;


public abstract class XSDSearchListProvider implements IComponentSearchListProvider
{
  protected XSDSchema[] schemas;
  protected IFile currentFile;
  // TODO (cs) remove these and use proper search scopes!
  //
  public static final int ENCLOSING_PROJECT_SCOPE = 0;
  public static final int ENTIRE_WORKSPACE_SCOPE = 1;

  public XSDSearchListProvider(IFile currentFile, XSDSchema[] schemas)
  {
    this.schemas = schemas;
    this.currentFile = currentFile;
    
    try
    {
      IProject[] refs = currentFile.getProject().getReferencedProjects();
            
      for (int i=0; i < refs.length; i++)
      {
        System.out.println("dep " + refs[i].getName());
      }  
    }
    catch (Exception e)
    {
      e.printStackTrace();
    } 
    
  }
   
  
  class ComponentCollectingXSDVisitor
  {
    protected List visitedSchemas = new ArrayList();
    IComponentList list;
    QualifiedName searchKind;
    
    ComponentCollectingXSDVisitor(IComponentList list, QualifiedName searchKind)
    {
      this.list = list;
      this.searchKind = searchKind;
    }

    public void visitSchema(final XSDSchema schema, boolean visitImportedSchema)
    {
      visitedSchemas.add(schema);
      for (Iterator contents = schema.getContents().iterator(); contents.hasNext();)
      {
        XSDSchemaContent content = (XSDSchemaContent) contents.next();
        if (content instanceof XSDSchemaDirective)
        {
          final XSDSchemaDirective schemaDirective = (XSDSchemaDirective) content;
          XSDSchema extSchema = schemaDirective.getResolvedSchema();
          if (extSchema == null && schemaDirective instanceof XSDImport && visitImportedSchema) 
          {
            // Force the import declaration to resolve. The work must be done in the UI thread
            // because there are UI components listening to changes in the schema content.
            
            UIJob loadImportJob = new UIJob(XSDEditorPlugin.getResourceString("_UI_LABEL_LOADING_XML_SCHEMA")) //$NON-NLS-1$ 
            {
              public IStatus runInUIThread(IProgressMonitor monitor) 
              {
                // The schema model will load the import when trying to resolve any component 
                // in the imported schema's namespace.

                XSDImport xsdImport = (XSDImport)schemaDirective;
                String importNamespace = xsdImport.getNamespace();
                schema.resolveAttributeDeclaration(importNamespace, ""); //$NON-NLS-1$
                return Status.OK_STATUS;
              }
            };
            loadImportJob.setSystem(true);
            loadImportJob.schedule();
            try 
            {
              loadImportJob.join();
            }
            catch (InterruptedException e) 
            {
              // Nothing to do about it.
            }
            extSchema = schemaDirective.getResolvedSchema();
          }
          
          if (extSchema != null && !visitedSchemas.contains(extSchema))
          {
            if (schemaDirective instanceof XSDImport && visitImportedSchema)
            {
              visitSchema(extSchema, false);
            }
            else if (schemaDirective instanceof XSDInclude || schemaDirective instanceof XSDRedefine)
            {
            	visitSchema(extSchema, false);
            }
          }
        }
        else if (content instanceof XSDElementDeclaration && searchKind == IXSDSearchConstants.ELEMENT_META_NAME)
        {
          list.add(content);
        }
        else if (content instanceof XSDAttributeDeclaration && searchKind == IXSDSearchConstants.ATTRIBUTE_META_NAME)
        {
          list.add(content);
        }
        else if (content instanceof XSDSimpleTypeDefinition && searchKind == IXSDSearchConstants.SIMPLE_TYPE_META_NAME)
        {
          // in this case we only want to show simple types
          list.add(content);               
        }
        else if (content instanceof XSDTypeDefinition && searchKind == IXSDSearchConstants.TYPE_META_NAME)
        {     
          // in this case we want to show all types
          list.add(content);
        }        
      }
    }

    public List getVisitedSchemas()
    {
      return visitedSchemas;
    }
  }
   
  
  class InternalSearchRequestor extends SearchRequestor
  {
    IComponentList componentList;
    HashMap files;

    InternalSearchRequestor(IComponentList componentList, HashMap files)
    {
      this.componentList = componentList;
      this.files = files;
    }

    public void acceptSearchMatch(SearchMatch match) throws CoreException
    {
      // we filter out the matches from the current file since we assume the
      // info derived from our schema models is more update to date
      // (in the event that we haven't saved our latest modifications)
      //
      if (files.get(match.getFile()) == null)
      {  
        // TODO... this ugly qualified name stashing will go away soon
        //
        QualifiedName qualifiedName = null;
        Object o = match.map.get("name");
        if (o != null && o instanceof QualifiedName)
        {  
          qualifiedName = (QualifiedName)o;
        } 
        if (qualifiedName != null && qualifiedName.getLocalName() != null)
        {  
          componentList.add(match);
        }
      }  
    }
  }  

  protected void findMatches(SearchEngine searchEngine, SearchRequestor requestor, SearchScope scope, QualifiedName metaName)
  {
    try
    {
      XMLComponentDeclarationPattern pattern = new XMLComponentDeclarationPattern(new QualifiedName("*", "*"), metaName, SearchPattern.R_PATTERN_MATCH);
      // TODO (cs) revist this... we shouldn't be needing to hard-code partipant id's
      // All we're really doing here is trying to avoid finding matches in
      // wsdl's since we don't  ever want to import/include a wsdl from a schema! 
      // Maybe we should just scope out any file types that aren't xsd's using a 
      // custom SearchScope?
      //
      SearchParticipant particpant = SearchPlugin.getDefault().getSearchParticipant("org.eclipse.wst.xsd.search.XSDSearchParticipant");
      Assert.isNotNull(particpant);
      SearchParticipant[] participants = {particpant};
      searchEngine.search(pattern, requestor, participants, scope, null, new NullProgressMonitor());
    }
    catch (CoreException e)
    {
      e.printStackTrace();
    }
  }
  
  
  protected HashMap createFileMap(List visitedSchemas)
  {
    HashMap fileMap = new HashMap();
    for (Iterator i = visitedSchemas.iterator(); i.hasNext(); )
    {
      XSDSchema theSchema = (XSDSchema)i.next();
      String location = theSchema.getSchemaLocation();       
      IFile file = computeFile(location);
      if (file != null)
      {
        fileMap.put(file, Boolean.TRUE);
      }       
    }   
    return fileMap;
  }
  
  private IFile computeFile(String baseLocation)
  {
    IFile file = null;
    if (baseLocation != null)
    {
      String fileScheme = "file:"; //$NON-NLS-1$
      String platformResourceScheme = "platform:/resource";
      if (baseLocation.startsWith(fileScheme))
      {
        baseLocation = baseLocation.substring(fileScheme.length());
        baseLocation = removeLeading(baseLocation, "/");
        IPath path = new Path(baseLocation);
        file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
      }
      else if (baseLocation.startsWith(platformResourceScheme))
      {
        baseLocation = baseLocation.substring(platformResourceScheme.length());
        baseLocation = removeLeading(baseLocation, "/");
        IPath path = new Path(baseLocation);
        file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
      }    
    }
    return file;    
  }  
  
  private String removeLeading(String path, String pattern)
  {
    while (path.startsWith(pattern))
    {
      path = path.substring(pattern.length());
    }  
    return path;
  }    
}
