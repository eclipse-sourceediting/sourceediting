/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.dialogs.types.xsd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.common.core.search.SearchEngine;
import org.eclipse.wst.common.core.search.SearchMatch;
import org.eclipse.wst.common.core.search.SearchParticipant;
import org.eclipse.wst.common.core.search.SearchPlugin;
import org.eclipse.wst.common.core.search.pattern.QualifiedName;
import org.eclipse.wst.common.core.search.pattern.SearchPattern;
import org.eclipse.wst.common.core.search.scope.ProjectSearchScope;
import org.eclipse.wst.common.core.search.scope.SearchScope;
import org.eclipse.wst.common.core.search.scope.WorkspaceSearchScope;
import org.eclipse.wst.common.core.search.util.CollectingSearchRequestor;
import org.eclipse.wst.xml.core.internal.search.XMLComponentDeclarationPattern;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.xml.XMLComponentFinder;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.xml.XMLComponentSpecification;
import org.eclipse.wst.xsd.ui.internal.search.IXSDSearchConstants;

public class XSDComponentFinder extends XMLComponentFinder {    
    public XSDComponentFinder() {
    }
    
    protected void findTypes(SearchEngine searchEngine, List list, int scope, QualifiedName metaName)
    {      
      SearchScope searchScope = new WorkspaceSearchScope();
      if (scope == ENCLOSING_PROJECT_SCOPE)
      {
        searchScope = new ProjectSearchScope(currentIFile.getProject().getLocation());
      }       
      try {
          CollectingSearchRequestor requestor = new CollectingSearchRequestor();
          
          XMLComponentDeclarationPattern pattern = new XMLComponentDeclarationPattern(new QualifiedName("*", "*"), metaName, SearchPattern.R_PATTERN_MATCH);
          SearchParticipant particpant = SearchPlugin.getDefault().getSearchParticipant("org.eclipse.wst.xsd.search.XSDSearchParticipant");
          Assert.isNotNull(particpant);
          
          // for now we assume that we only want to include the xsd related participant
          // that way we don't get SearchMatches for things withing WSDL files
          // TODO... rethink this... since folks should be capable of changing the 'xsd' search participant impl
          // without killing this logic
          SearchParticipant[] participants = { particpant };          
          searchEngine.search(pattern, requestor, participants, searchScope, new NullProgressMonitor());
          
          for (Iterator i = requestor.getResults().iterator(); i.hasNext(); )
          {
            SearchMatch match = (SearchMatch)i.next();
            XMLComponentSpecification spec = new XMLComponentSpecification(metaName);
            spec.setFileLocation(match.getFile().getLocation().toString());
            Object o = match.map.get("name");
            if (o != null && o instanceof QualifiedName)
            {  
              QualifiedName qualifiedName = (QualifiedName)o;
              if (qualifiedName.getLocalName() != null)
              {  
                spec.addAttributeInfo("name", qualifiedName.getLocalName());
                spec.setTargetNamespace(qualifiedName.getNamespace());
                list.add(spec);
              }  
            }  
          }  
      } catch (CoreException e) {
        e.printStackTrace();
          //status.add(e.getStatus());
      }      
    }
    
    public List getWorkbenchResourceComponents(int scope) {
      // TODO... by making this method return a list... we're cutting off any chance provide asynchronous friendly
      // behaviour where we populate the list and refresh the dialog after every few updates or seconds
      // returning a list makes this an all or nothing deal
      //
      List list = new ArrayList();
      SearchEngine searchEngine = new SearchEngine();
      // TODO... we need a way to combine these into a single search!!
      // we can make this search 2X faster by doing so
      // either we should combine COMPLEX_TYPE_META_NAME and SIMPLE_TYPE_META_NAME into a single TYPE_META_NAME
      // or we'll need to introduce a way to 'OR' SearchPatterns ... which may be tricky!
      findTypes(searchEngine, list, scope, IXSDSearchConstants.COMPLEX_TYPE_META_NAME);
      findTypes(searchEngine, list, scope, IXSDSearchConstants.SIMPLE_TYPE_META_NAME);    
      return list;
    }
}
