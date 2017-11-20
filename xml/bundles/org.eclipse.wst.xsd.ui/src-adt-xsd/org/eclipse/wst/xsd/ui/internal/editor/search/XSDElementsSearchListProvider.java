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
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.core.search.SearchEngine;
import org.eclipse.wst.common.core.search.scope.SearchScope;
import org.eclipse.wst.common.ui.internal.search.dialogs.IComponentList;
import org.eclipse.wst.xsd.ui.internal.search.IXSDSearchConstants;
import org.eclipse.xsd.XSDSchema;

public class XSDElementsSearchListProvider extends XSDSearchListProvider
{
  public XSDElementsSearchListProvider(IFile currentFile, XSDSchema[] schemas)
  {
    super(currentFile, schemas);
  }

  public void populateComponentList(IComponentList list, SearchScope scope, IProgressMonitor pm)
  {
    // now we traverse the types already defined within the visible schemas
    // we do this in addition to the component search since this should execute
    // very quickly and there's a good chance the user wants to select a time that's 
    // already imported/included
    // TODO (cs) ensure we don't add duplicates when we proceed to use the search list
    //
    List visitedSchemas = new ArrayList();
    for (int i = 0; i < schemas.length; i++)
    {
      XSDSchema schema = schemas[i];
      ComponentCollectingXSDVisitor visitor = new ComponentCollectingXSDVisitor(list, IXSDSearchConstants.ELEMENT_META_NAME);
      visitor.visitSchema(schema, true);
      visitedSchemas.addAll(visitor.getVisitedSchemas());
    }
    // finally we call the search API's to do a potentially slow search
    if (scope != null)
    {
      populateComponentListUsingSearch(list, scope, pm, createFileMap(visitedSchemas));
    }
  }

  private void populateComponentListUsingSearch(IComponentList list, SearchScope scope, IProgressMonitor pm, HashMap files)
  {
    SearchEngine searchEngine = new SearchEngine();
    InternalSearchRequestor requestor = new InternalSearchRequestor(list, files);
    findMatches(searchEngine, requestor, scope, IXSDSearchConstants.ELEMENT_META_NAME);
  }
}
