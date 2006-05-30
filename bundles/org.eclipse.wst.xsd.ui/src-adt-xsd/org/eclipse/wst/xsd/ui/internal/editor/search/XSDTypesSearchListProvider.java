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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.core.search.SearchEngine;
import org.eclipse.wst.common.core.search.pattern.QualifiedName;
import org.eclipse.wst.common.core.search.scope.SearchScope;
import org.eclipse.wst.common.ui.internal.search.dialogs.IComponentList;
import org.eclipse.wst.xsd.ui.internal.search.IXSDSearchConstants;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.impl.XSDSchemaImpl;
import org.eclipse.xsd.util.XSDConstants;
public class XSDTypesSearchListProvider extends XSDSearchListProvider
{
  protected IXSDTypesFilter builtInFilter;
  /**
   * Determines if we should use the filter This us used to turn the filter on
   * and off
   */
  protected boolean supportFilter = true;
  private boolean showComplexTypes = true;

  public XSDTypesSearchListProvider(IFile currentFile, XSDSchema[] schemas)
  {
    super(currentFile, schemas);
  }

  public void populateComponentList(IComponentList list, SearchScope scope, IProgressMonitor pm)
  {
    // first we add the 'built in' types
    //
    XSDSchema schemaForSchema = XSDSchemaImpl.getSchemaForSchema(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001);
    for (Iterator i = schemaForSchema.getSimpleTypeIdMap().values().iterator(); i.hasNext();)
    {
      XSDTypeDefinition td = (XSDTypeDefinition) i.next();
      if (builtInFilter == null || !builtInFilter.shouldFilterOut(td))
      {
        list.add(td);
      }
    }
    // now we traverse the types already defined within the visible schemas
    // we do this in addition to the component search since this should execute
    // very quickly and there's a good chance the user wants to select a time
    // that's
    // already imported/included
    // TODO (cs) ensure we don't add duplicates when we proceed to use the
    // search list
    //
    List visitedSchemas = new ArrayList();
    for (int i = 0; i < schemas.length; i++)
    {
      XSDSchema schema = schemas[i];
      QualifiedName kind = showComplexTypes ? IXSDSearchConstants.TYPE_META_NAME : IXSDSearchConstants.SIMPLE_TYPE_META_NAME;
      ComponentCollectingXSDVisitor visitor = new ComponentCollectingXSDVisitor(list, kind);
      visitor.visitSchema(schema, true);
      visitedSchemas.addAll(visitor.getVisitedSchemas());
    }
    // finally we call the search API's to do a potentially slow search
    //   
    if (scope != null)
    {
      populateComponentListUsingSearch(list, scope, pm, createFileMap(visitedSchemas));
    }
  }

  private void populateComponentListUsingSearch(IComponentList list, SearchScope scope, IProgressMonitor pm, HashMap files)
  {
    SearchEngine searchEngine = new SearchEngine();
    InternalSearchRequestor requestor = new InternalSearchRequestor(list, files);
    if (showComplexTypes)
    {
      findMatches(searchEngine, requestor, scope, IXSDSearchConstants.COMPLEX_TYPE_META_NAME);
    }
    findMatches(searchEngine, requestor, scope, IXSDSearchConstants.SIMPLE_TYPE_META_NAME);
  }


  public void _populateComponentListQuick(IComponentList list, IProgressMonitor pm)
  {
  }

  public void turnBuiltInFilterOn(boolean option)
  {
    supportFilter = option;
  }

  public void setBuiltInFilter(IXSDTypesFilter filter)
  {
    this.builtInFilter = filter;
  }

  public void showComplexTypes(boolean show)
  {
    showComplexTypes = show;
  }
}
