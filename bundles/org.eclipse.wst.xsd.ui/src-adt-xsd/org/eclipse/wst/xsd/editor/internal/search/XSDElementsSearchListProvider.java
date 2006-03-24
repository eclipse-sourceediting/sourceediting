package org.eclipse.wst.xsd.editor.internal.search;

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
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.XSDSchemaDirective;

public class XSDElementsSearchListProvider implements
		IComponentSearchListProvider {
	protected XSDSchema[] schemas;

	protected IFile currentFile;

	// TODO (cs) remove these and use proper search scopes!
	//
	public static final int ENCLOSING_PROJECT_SCOPE = 0;

	public static final int ENTIRE_WORKSPACE_SCOPE = 1;

	public XSDElementsSearchListProvider(IFile currentFile,
			XSDSchema[] schemas) {
		this.schemas = schemas;
		this.currentFile = currentFile;
	}

	public void populateComponentList(IComponentList list, SearchScope scope,
			IProgressMonitor pm) {
		// we traverse the elements already defined within the visible
		// schemas
		// we do this in addition to the component search since this should
		// execute
		// very quickly and there's a good chance the user wants to select an
		// element that's
		// already imported/included
		// TODO (cs) ensure we don't add duplicates when we proceed to use the
		// search list
		//
		for (int i = 0; i < schemas.length; i++) {
			XSDSchema schema = schemas[i];
			ComponentCollectingXSDVisitor visitor = 
				new ComponentCollectingXSDVisitor(list);
			visitor.visitSchema(schema, true);
		}

		// finally we call the search API's to do a potentially slow search
		if (scope != null) {
			populateComponentListUsingSearch(list, scope, pm);
		}
	}

	class ComponentCollectingXSDVisitor {
		protected List visitedSchemas = new ArrayList();

		IComponentList list;

		ComponentCollectingXSDVisitor(IComponentList list) {
			this.list = list;
		}

		public void visitSchema(XSDSchema schema, boolean visitImportedSchema) {
			
			visitedSchemas.add(schema);
			for (Iterator contents = schema.getContents().iterator(); contents.hasNext();) {
				XSDSchemaContent content = (XSDSchemaContent) contents.next();
				if (content instanceof XSDSchemaDirective) {
					XSDSchemaDirective schemaDirective = (XSDSchemaDirective) content;
					XSDSchema extSchema = schemaDirective.getResolvedSchema();
					if (extSchema != null && !visitedSchemas.contains(extSchema)){
						if ( schemaDirective instanceof XSDImport && visitImportedSchema){
							visitSchema(extSchema, false);
						}
						else if ( extSchema instanceof XSDInclude || extSchema instanceof XSDImport){
							visitSchema(extSchema, false);
						}
					}
				} else if (content instanceof XSDElementDeclaration) {
					list.add(content);
				}
			}
		}
	}

	private void populateComponentListUsingSearch(IComponentList list,
			SearchScope scope, IProgressMonitor pm) {
		SearchEngine searchEngine = new SearchEngine();
		InternalSearchRequestor requestor = new InternalSearchRequestor(list);
		findTypes(searchEngine, requestor, scope,
				IXSDSearchConstants.ELEMENT_META_NAME);
	}

	class InternalSearchRequestor extends SearchRequestor {
		IComponentList componentList;

		InternalSearchRequestor(IComponentList componentList) {
			this.componentList = componentList;
		}

		public void acceptSearchMatch(SearchMatch match) throws CoreException {
			// we filter out the matches from the current file since we assume
			// the
			// info derived from our schema models is more update to date
			// (in the event that we haven't saved our latest modifications)
			//
			if (match.getFile() != currentFile) {
				// TODO... this ugly qualified name stashing will go away soon
				//
				QualifiedName qualifiedName = null;
				Object o = match.map.get("name");
				if (o != null && o instanceof QualifiedName) {
					qualifiedName = (QualifiedName) o;
				}
				if (qualifiedName != null
						&& qualifiedName.getLocalName() != null) {
					componentList.add(match);
				}
			}
		}
	}

	protected void findTypes(SearchEngine searchEngine,
			SearchRequestor requestor, SearchScope scope, QualifiedName metaName) {
		try {
			XMLComponentDeclarationPattern pattern = new XMLComponentDeclarationPattern(
					new QualifiedName("*", "*"), metaName,
					SearchPattern.R_PATTERN_MATCH);

			// TODO (cs) revist this... we shouldn't be needing to hard-code
			// partipant id's
			// All we're really doing here is trying to avoid finding matches in
			// wsdl's since we don't
			// ever want to import/include a wsdl from a schema! Maybe we should
			// just scope out any file
			// types that aren't xsd's using a custom SearchScope?
			//
			SearchParticipant particpant = SearchPlugin.getDefault()
					.getSearchParticipant(
							"org.eclipse.wst.xsd.search.XSDSearchParticipant");

			Assert.isNotNull(particpant);
			SearchParticipant[] participants = { particpant };
			searchEngine.search(pattern, requestor, participants, scope, null,
					new NullProgressMonitor());
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
