package org.eclipse.wst.xml.xpath2.processor.util;

import java.net.URI;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

import org.eclipse.wst.xml.xpath2.api.CollationProvider;
import org.eclipse.wst.xml.xpath2.api.Function;
import org.eclipse.wst.xml.xpath2.api.FunctionLibrary;
import org.eclipse.wst.xml.xpath2.api.StaticContext;
import org.eclipse.wst.xml.xpath2.api.StaticVariableResolver;
import org.eclipse.wst.xml.xpath2.api.typesystem.TypeDefinition;
import org.eclipse.wst.xml.xpath2.api.typesystem.TypeModel;
import org.w3c.dom.Node;

public class StaticContextBuilder implements StaticContext {

	public static final String XPATH_FUNCTIONS_NS = "http://www.w3.org/2005/xpath-functions";

	private boolean _xpath1_compatible = false;
	private String _default_namespace = "";
	private String _default_function_namespace = XPATH_FUNCTIONS_NS;
	private TypeDefinition _initialContextType = null; 

	// key: String prefix, contents: String namespace
	private Map/*<String, String>*/ _namespaces = new HashMap/*<String, String>*/();
	private Map/*<String, FunctionLibrary>*/ _functionLibraries;
	private boolean _useDefaultLibraries = true;

	private URI _base_uri;
	private Map/*<String, TypeDefinition>*/ _documents = new HashMap/*<String, TypeDefinition>*/();
	private Map/*<String, TypeDefinition>*/ _variableTypes = new HashMap/*<String, TypeDefinition>*/();
	private Map/*<String, TypeDefinition>*/ _collectionTypes = new HashMap/*<String, TypeDefinition>*/();

	private Set/*<QName>*/ _hiddenFunctions = new HashSet/*<QName>*/();

	private TypeModel _typeModel;
	
	public boolean isXPath1Compatible() {
		return _xpath1_compatible;
	}

	public NamespaceContext getNamespaceContext() {
		return new NamespaceContext() {
			
			public Iterator getPrefixes(String ns) {
				List/*<String>*/ prefixes = new LinkedList/*<String>*/();
				for (Iterator it = _namespaces.entrySet().iterator(); it.hasNext(); ) {
					Map.Entry entry = (Map.Entry)it.next();
					
					if (entry.getValue().equals(ns)) prefixes.add(entry.getKey());
				}
				return prefixes.iterator();
			}
			
			public String getPrefix(String ns) {
				for (Iterator it = _namespaces.entrySet().iterator(); it.hasNext(); ) {
					Map.Entry entry = (Map.Entry)it.next();					
					if (entry.getValue().equals(ns)) {
						return (String)entry.getKey();
					}
				}
				return null;
			}
			
			public String getNamespaceURI(String prefix) {
				return (String)_namespaces.get(prefix);
			}
		};
	}

	public String getDefaultNamespace() {
		return _default_namespace;
	}

	public String getDefaultFunctionNamespace() {
		return _default_function_namespace;
	}

	public TypeModel getTypeModel() {
		if (_typeModel != null) return _typeModel;
		
		return new TypeModel() {

			public TypeDefinition getType(Node node) {
				return null;
			}

			public TypeDefinition lookupType(String namespace, String typeName) {
				return null;
			}

			public TypeDefinition lookupElementDeclaration(String namespace, String elementName) {
				return null;
			}

			public TypeDefinition lookupAttributeDeclaration(String namespace, String attributeName) {
				return null;
			}};
	}

	public Function resolveFunction(QName name, int arity) {
		if (_hiddenFunctions.contains(name)) return null;
		FunctionLibrary flib = (FunctionLibrary) _functionLibraries.get(name.getNamespaceURI());
		if (flib != null) {
			return flib.resolveFunction(name.getLocalPart(), arity);
		}
		return null;
	}

	public URI getBaseUri() {
		return _base_uri;
	}

	public Collection getFunctionLibraries() {
		return _functionLibraries.values();
	}

	public TypeDefinition getCollectionType(String collectionName) {
		return (TypeDefinition) _collectionTypes.get(collectionName);
	}

	public TypeDefinition getInitialContextType() {
		return _initialContextType;
	}

	public StaticContextBuilder withNamespace(String prefix, String uri) {
		_namespaces.put(prefix, uri);
		return this;
	}

	public StaticContextBuilder withDefaultNamespace(String uri) {
		_default_namespace = uri;
		return this;
	}

	public StaticContextBuilder withXPath1Compatibility(boolean compatible) {
		_xpath1_compatible = compatible;
		return this;
	}

	public StaticContextBuilder withTypeModel(TypeModel tm) {
		_typeModel = tm;
		return this;
	}

	public StaticContextBuilder withoutFunction(QName functionToSuppress) {
		_hiddenFunctions.add(functionToSuppress);
		return this;
	}
	
	public TypeDefinition getDefaultCollectionType() {
		// TODO Auto-generated method stub
		return null;
	}

	public StaticVariableResolver getInScopeVariables() {
		// TODO Auto-generated method stub
		return null;
	}

	// We are explicitly NOT using generics here, in anticipation of JDK1.4 compatibility
	private static Comparator CODEPOINT_COMPARATOR = new Comparator() {
		
		public int compare(Object o1, Object o2) {
			return ((String)o1).compareTo((String)o2);
		}
	};
	
	private CollationProvider _collationProvider = new CollationProvider() {
		
		public String getDefaultCollation() {
			return CollationProvider.CODEPOINT_COLLATION;
		}
		
		public Comparator getCollation(String uri) {
			if (CollationProvider.CODEPOINT_COLLATION.equals(uri)) return CODEPOINT_COMPARATOR;
			return null;
		}
	};
	
	public CollationProvider getCollationProvider() {
		return _collationProvider;
	}

	public StaticContextBuilder withCollationProvider(CollationProvider cp) {
		_collationProvider = cp;
		return this;
	}
}
