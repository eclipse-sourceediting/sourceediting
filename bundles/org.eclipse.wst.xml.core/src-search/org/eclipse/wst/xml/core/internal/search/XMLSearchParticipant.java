/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.core.internal.search;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.core.search.SearchMatch;
import org.eclipse.wst.common.core.search.SearchParticipant;
import org.eclipse.wst.common.core.search.SearchRequestor;
import org.eclipse.wst.common.core.search.document.ComponentDeclarationEntry;
import org.eclipse.wst.common.core.search.document.Entry;
import org.eclipse.wst.common.core.search.document.FileReferenceEntry;
import org.eclipse.wst.common.core.search.document.SearchDocument;
import org.eclipse.wst.common.core.search.document.SearchDocumentSet;
import org.eclipse.wst.common.core.search.pattern.ComponentSearchPattern;
import org.eclipse.wst.common.core.search.pattern.FileReferencePattern;
import org.eclipse.wst.common.core.search.pattern.SearchPattern;
import org.eclipse.wst.common.core.search.scope.ContentTypeSearchScope;
import org.eclipse.wst.common.core.search.scope.SearchScope;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverPlugin;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.search.impl.IXMLSearchConstants;
import org.eclipse.wst.xml.core.internal.search.impl.XMLSearchDocument;
import org.eclipse.wst.xml.core.internal.search.matching.PatternMatcher;
import org.eclipse.wst.xml.core.internal.search.matching.XMLSearchPatternMatcher;
import org.eclipse.wst.xml.core.internal.search.quickscan.XMLQuickScan;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 */
public abstract class XMLSearchParticipant extends SearchParticipant {
	
	protected static final boolean debugPerf = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.xml.core.internal.search/perf")); //$NON-NLS-1$ //$NON-NLS-2$

	public XMLSearchParticipant() {
		super();
	}

	
	/*
 public  boolean initialize(SearchPattern pattern, String[] contentTypes){
		
	    super.initialize(pattern, contentTypes);
		this.supportedContentTypes = contentTypes;
		
		if(pattern instanceof XMLComponentSearchPattern){
			return true;
		}
		return false;
	}*/

	

	public SearchDocument createSearchDocument(String documentPath) {

		return new XMLSearchDocument(documentPath, this);

	}

	public String getDescription() {
		return "XML search participant"; //$NON-NLS-1$
	}



	private void locateMatches(SearchPattern pattern, SearchDocument document,
			SearchRequestor requestor, Map searchOptions, IProgressMonitor monitor) {

        // TODO... utilize search options (that should get passed down via the SearchEngine)
        // to specify if accurate source coordinates are req'd if not, simply use the SAX results
        //
        if (pattern.getMatchRule() == SearchPattern.R_PATTERN_MATCH)
        {          
          IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(document.getPath()));
          // TODO.. don't assume the category is COMPONENT_DECL... handle any arbitarty category
          Entry[] entries = document.getEntries(IXMLSearchConstants.COMPONENT_DECL, null, 0);
          for (int i = 0; i < entries.length; i++)
          {
            // TODO.. don't assume this is just a component declaration entry            
            ComponentDeclarationEntry entry = (ComponentDeclarationEntry)entries[i];
            SearchMatch searchMatch = new SearchMatch(null, 0, 0, file);
            searchMatch.map.put("name", entry.getName()); //$NON-NLS-1$
            searchMatch.map.put("metaName", entry.getMetaName()); //$NON-NLS-1$
            try
            {
              requestor.acceptSearchMatch(searchMatch);
            }
            catch (Exception e)
            {              
            }
          }  
        }
        else 
        {  if (document.getModel() instanceof IDOMModel) {
			IDOMModel domModel = (IDOMModel) document.getModel();
			IDOMElement contextNode = (IDOMElement) domModel.getDocument()
					.getDocumentElement();
			DOMVisitor visitor = new DOMVisitor(document.getPath(), pattern,
					requestor);
			visitor.visit(contextNode);
		}
        }
	}
	
	private PatternMatcher getAdapter(Object adaptableObject, Class adapterType) {
		if (PatternMatcher.class.equals(adapterType) &&
				(adaptableObject instanceof XMLSearchPattern ||
				adaptableObject instanceof XMLComponentSearchPattern) ) {
			return new XMLSearchPatternMatcher(this);
		} 
		return null; 
	}

	private class DOMVisitor {

		String path;
		SearchPattern pattern;
		SearchRequestor requestor;
		PatternMatcher matcher;

		
		protected DOMVisitor(String path, SearchPattern pattern,
				SearchRequestor requestor) {
			super();
			this.path = path;
			this.pattern = pattern;
			
			matcher = (PatternMatcher)pattern.getAdapter(PatternMatcher.class);
			if(matcher == null){
				matcher = getAdapter(pattern, PatternMatcher.class);
			}
			this.requestor = requestor;
		}

		private void visit(Node node) {
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				match((Element)node);
				NodeList nodeList = node.getChildNodes();
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node aNode = nodeList.item(i);
					visit(aNode);
				}
			}
		}

		private void match(Element node) {
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(
					new Path(path));
			if(matcher != null){
				matcher.locateMatches(pattern, file, node, requestor);
			}

			
		}

	}

	public SearchScope selectDocumentLocations(SearchPattern pattern, SearchScope scope, Map searchOptions, IProgressMonitor monitor) {
		/*
		 * gate #1: reduce the scope to the files with the content type that
		 * could be searched using this participant
		 */ 
		String[] contentTypes = getSupportedContentTypes();	
		if(contentTypes != null && contentTypes.length > 0){
			scope = new ContentTypeSearchScope(scope, contentTypes);
		}
		return super.selectDocumentLocations(pattern, scope, searchOptions, monitor);
	}
	
	public abstract ComponentSearchContributor getSearchContributor();


	public void beginSearching(SearchPattern pattern, Map searchOptions) {
		
		super.beginSearching(pattern, searchOptions);
		if(pattern instanceof XMLComponentDeclarationPattern){
			XMLComponentDeclarationPattern componentPattern = (XMLComponentDeclarationPattern)pattern;
			XMLSearchPattern childPattern = getSearchContributor().getDeclarationPattern(componentPattern.getMetaName());
			if(childPattern != null){
					childPattern.setSearchName(componentPattern.getName().getLocalName());
					childPattern.setSearchNamespace(componentPattern.getName().getNamespace());
	     			componentPattern.addChildren(this, new XMLSearchPattern[]{childPattern});
			}
			
		}
		else if(pattern instanceof XMLComponentReferencePattern){
			XMLComponentReferencePattern componentPattern = (XMLComponentReferencePattern)pattern;
			XMLSearchPattern[] childPatterns = getSearchContributor().getReferencesPatterns(componentPattern.getMetaName());
			for (int i = 0; i < childPatterns.length; i++) {
				XMLSearchPattern childPattern = childPatterns[i];
				childPattern.setSearchName(componentPattern.getName().getLocalName());
				childPattern.setSearchNamespace(componentPattern.getName().getNamespace());				
			}
			componentPattern.addChildren(this, childPatterns);
			
		}
	}
	
	
	/**
	 * The intend of this method is to limit the search to the files that have content 
	 * which can be searched for the given pattern. It is called from 
	 * {@link #selectDocumentLocations(SearchPattern, SearchScope, IProgressMonitor)}
	 * 
	 * @param pattern the search pattern that is searched for
	 * @return content type's unique identifiers that could be searched for the given pattern.
	 */
	public abstract String[] getSupportedContentTypes();

	public void populateSearchDocument(SearchDocument document, SearchPattern pattern)
	{
		PatternMatcher matcher = (PatternMatcher)pattern.getAdapter(PatternMatcher.class);
		if(matcher == null){
			matcher = getAdapter(pattern, PatternMatcher.class);
		}
		XMLQuickScan.populateSearchDocument(document, matcher, pattern);		
	}

	public void locateMatches(SearchDocumentSet documentSet, SearchPattern pattern, SearchScope scope, SearchRequestor requestor, Map searchOptions, IProgressMonitor monitor) throws CoreException
	{
		long time = System.currentTimeMillis();
		
		// TODO: use the file reference entries in the documents to reduce the scope to the referenced files only
		// SearchDocument[] documents = documentSet.getSearchDocuments(id);
                
        // check to see if the search pattern is qualified by a file location
        // if this is the case then we can use file scoping rules to prune the matches
        IFile targetFile = null;
        if (pattern instanceof ComponentSearchPattern)
        {
          ComponentSearchPattern componentSearchPattern = (ComponentSearchPattern)pattern;
          targetFile = componentSearchPattern.getFile();                                       
        }		
        
		// here we should have in scope only referenced files
		IFile[] files = scope.enclosingFiles();  
		for (int i = 0; i < files.length; i++)
		{
			IFile file = files[i];
			String path = file.getLocation().toString();
			SearchDocument document = documentSet.getSearchDocument(path, id); 
			if (document != null)
			{              
			Entry[] entries = document.getEntries(getSearchEntryCategory(pattern), null, 0);           
			if ((entries != null && entries.length > 0) || (searchOptions != null && searchOptions.get("searchDirtyContent") != null))
            {
              //for (int j = 0; j < entries.length; j++)
              //{
              //  Entry entry = entries[j];
                //System.out.println("entry " + entry.getCategory() + " " + entry.getKey() + " " + entry.getClass().getName());                 
              //}  
              
              boolean isInScope = true;
              if (targetFile != null)
              {
                try
                {
                  isInScope = isLinked(documentSet, "file:///" + path, "file:///" + targetFile.getLocation().toString()); //$NON-NLS-1$ //$NON-NLS-2$
                  //if (path.endsWith("CancelSelection.wsdl")  && path.indexOf("clone1") != -1)
                  //{
                  //  fileReferenceTable.debug(qualifiedPath, 0, 5);
                  //}                   
                }
                catch (Exception e)
                {
                  e.printStackTrace();
                }
              }              
              if (isInScope)
              { 
			    this.locateMatches(pattern, document, requestor, searchOptions, monitor);
              }  
            }
			}
		}
		
		
		if (debugPerf)
		{
			System.out
					.println("" //$NON-NLS-1$
							+ getDescription()
							+ ": " + (System.currentTimeMillis() - time) + "ms for locateMatches"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		
	}
    
    private boolean isLinked(SearchDocumentSet set, String source, String target)
    {
      return isLinked(set, source, target, new HashMap());
    }
    
    private boolean isLinked(SearchDocumentSet set, String source, String target, HashMap visited)
    {
      if (source.equals(target))
        return true;
      
      String fileProtocol = "file:///";             //$NON-NLS-1$
      
      // Fix for bug 204174 - Begin
      if(target.charAt(fileProtocol.length()) == '/')  //$NON-NLS-1$
      {
          target = fileProtocol + target.substring(fileProtocol.length() + 1);
      }
      // Fix for bug 204174 - End
            
      if (source.startsWith(fileProtocol))
      {    
        
      SearchDocument document = set._tempGetSearchDocumetn(source.substring(fileProtocol.length()));      
      if (document != null)
      {        
        URIResolver uriResolver = URIResolverPlugin.createResolver();        
        Entry[] entries = document.getEntries(IXMLSearchConstants.REF, null, 0);
        String[] resolveEntry = new String[entries.length];        
        for (int j = 0; j < entries.length; j++)
        {
          Entry entry = entries[j];
          if (entry instanceof FileReferenceEntry)
          {
            FileReferenceEntry fileReferenceEntry = (FileReferenceEntry)entry;
            // TODO.. record an utilize the public id from the fileReferenceEntry
            //
            if (fileReferenceEntry.getRelativeFilePath() != null)
            {  
              String resolvedURI = uriResolver.resolve(source, null, fileReferenceEntry.getRelativeFilePath());
              resolveEntry[j] = resolvedURI;
              if (resolvedURI.equals(target))
              {
                return true;
              }             
            }  
          }
        }
        // now see if there's an indirect link from the source to the target
        // we keep track of the nodes we've already visited to avoid cycles
        if (visited.get(source) == null)
        {
          visited.put(source, Boolean.TRUE);
          for (int j = 0; j < entries.length; j++)
          {                     
            String resolvedURI = resolveEntry[j];
            if (resolvedURI != null && isLinked(set, resolveEntry[j], target, visited))                
              return true;            
          }    
        }                      
      }            
      } 
      return false;
    }
    
   
    
	public static String getSearchEntryCategory(SearchPattern pattern){
		if(pattern instanceof XMLComponentDeclarationPattern){
			return IXMLSearchConstants.COMPONENT_DECL;
		}
		else if(pattern instanceof XMLComponentReferencePattern){
			return IXMLSearchConstants.COMPONENT_REF;
		}
		else if(pattern instanceof FileReferencePattern){
		   return IXMLSearchConstants.COMPONENT_REF;
		}
		return null;
	}   
}
