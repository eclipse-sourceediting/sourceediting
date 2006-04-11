/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
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
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
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
import org.eclipse.wst.xsd.ui.internal.search.IXSDSearchConstants;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.impl.XSDSchemaImpl;
import org.eclipse.xsd.util.XSDConstants;

public class XSDTypesSearchListProvider implements IComponentSearchListProvider
{
  protected XSDSchema[] schemas;
  protected IFile currentFile;
  // TODO (cs) remove these and use proper search scopes!
  //
  public static final int ENCLOSING_PROJECT_SCOPE = 0;
  public static final int ENTIRE_WORKSPACE_SCOPE = 1;
  
  protected IXSDTypesFilter builtInFilter;
  
  /**
   * Determines if we should use the filter
   * This us used to turn the filter on and off 
   */
  protected boolean supportFilter = true;
  private boolean showComplexTypes = true;

  public XSDTypesSearchListProvider(IFile currentFile, XSDSchema[] schemas)
  {
    this.schemas = schemas;
    this.currentFile = currentFile;
  }

  public void populateComponentList(IComponentList list, SearchScope scope, IProgressMonitor pm)
  {	  
    // first we add the 'built in' types
    //
    XSDSchema schemaForSchema = XSDSchemaImpl.getSchemaForSchema(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001);
    for (Iterator i = schemaForSchema.getSimpleTypeIdMap().values().iterator(); i.hasNext();)
    {
    	XSDTypeDefinition td = (XSDTypeDefinition) i.next();
    	if ( builtInFilter == null || !builtInFilter.shouldFilterOut(td) ) {
    		list.add(td);
    	}
    }    
  
    // now we traverse the types already defined within the visible schemas
    // we do this in addition to the component search since this should execute
    // very quickly and there's a good chance the user wants to select a time that's 
    // already imported/included
    // TODO (cs) ensure we don't add duplicates when we proceed to use the search list
    //
    for (int i = 0; i < schemas.length; i++)
    {
      XSDSchema schema = schemas[i];
      ComponentCollectingXSDVisitor visitor = new ComponentCollectingXSDVisitor(list);
      visitor.visitSchema(schema, true);
    }
    
    // finally we call the search API's to do a potentially slow search
    //
    if (scope != null)
    {  
      populateComponentListUsingSearch(list, scope, pm);
    }  
  }
  class ComponentCollectingXSDVisitor
  {
    protected List visitedSchemas = new ArrayList();
    IComponentList list;
    
    ComponentCollectingXSDVisitor(IComponentList list)
    {
      this.list = list;  
    }

    public void visitSchema(XSDSchema schema, boolean visitImportedSchema)
    {
      visitedSchemas.add(schema);
      for (Iterator contents = schema.getContents().iterator(); contents.hasNext();)
      {
        XSDSchemaContent content = (XSDSchemaContent) contents.next();
        if (content instanceof XSDSchemaDirective)
        {
          XSDSchemaDirective schemaDirective = (XSDSchemaDirective) content;
          XSDSchema extSchema = schemaDirective.getResolvedSchema();
          if (extSchema != null && !visitedSchemas.contains(extSchema))
          {
            if ( schemaDirective instanceof XSDImport && visitImportedSchema){
            	visitSchema(extSchema, false);
            }
            else if ( schemaDirective instanceof XSDInclude ||
            		schemaDirective instanceof XSDRedefine){
            	visitSchema(extSchema, false);
            }
          }
        }
        else if (content instanceof XSDTypeDefinition)
        {
          if ( showComplexTypes || ! (content instanceof XSDComplexTypeDefinition) )
        	  list.add(content);
        }
      }
    }
  }

  private void populateComponentListUsingSearch(IComponentList list, SearchScope scope, IProgressMonitor pm)
  {
    SearchEngine searchEngine = new SearchEngine();
    InternalSearchRequestor requestor = new InternalSearchRequestor(list);
    if (showComplexTypes)
    	findTypes(searchEngine, requestor, scope, IXSDSearchConstants.COMPLEX_TYPE_META_NAME);
    findTypes(searchEngine, requestor, scope, IXSDSearchConstants.SIMPLE_TYPE_META_NAME);
  }
  
  class InternalSearchRequestor extends SearchRequestor
  {
    IComponentList componentList;

    InternalSearchRequestor(IComponentList componentList)
    {
      this.componentList = componentList;
    }

    public void acceptSearchMatch(SearchMatch match) throws CoreException
    {
      // we filter out the matches from the current file since we assume the
      // info derived from our schema models is more update to date
      // (in the event that we haven't saved our latest modifications)
      //
      if (match.getFile() != currentFile)
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

  protected void findTypes(SearchEngine searchEngine, SearchRequestor requestor, SearchScope scope, QualifiedName metaName)
  {
    try
    {
      XMLComponentDeclarationPattern pattern = new XMLComponentDeclarationPattern(new QualifiedName("*", "*"), metaName, SearchPattern.R_PATTERN_MATCH);
      
      // TODO (cs) revist this... we shouldn't be needing to hard-code partipant id's
      // All we're really doing here is trying to avoid finding matches in wsdl's since we don't
      // ever want to import/include a wsdl from a schema! Maybe we should just scope out any file 
      // types that aren't xsd's using a custom SearchScope?
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

  public void _populateComponentListQuick(IComponentList list, IProgressMonitor pm)
  {    
  }

  public void turnBuiltInFilterOn(boolean option){
	  supportFilter = option;
  }
  
  public void setBuiltInFilter(IXSDTypesFilter filter) {
 	this.builtInFilter = filter;
  }
  
  public void showComplexTypes(boolean show){
	  showComplexTypes = show;
  }
}
