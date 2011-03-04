package org.eclipse.wst.xml.xpath2.processor.util;
//package org.eclipse.wst.xml.xpath2;
//
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.net.URI;
//import java.net.URL;
//import java.util.Collection;
//import java.util.Comparator;
//import java.util.GregorianCalendar;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.TimeZone;
//
//import javax.xml.namespace.NamespaceContext;
//import javax.xml.namespace.QName;
//
//import org.apache.xerces.xs.XSModel;
//import org.eclipse.wst.xml.xpath2.api.CollationProvider;
//import org.eclipse.wst.xml.xpath2.api.DynamicContext;
//import org.eclipse.wst.xml.xpath2.api.Function;
//import org.eclipse.wst.xml.xpath2.api.ResultSequence;
//import org.eclipse.wst.xml.xpath2.api.StaticContext;
//import org.eclipse.wst.xml.xpath2.api.types.DocType;
//import org.eclipse.wst.xml.xpath2.api.types.NodeType;
//import org.eclipse.wst.xml.xpath2.api.types.XSDuration;
//import org.eclipse.wst.xml.xpath2.api.types.XSQName;
//import org.eclipse.wst.xml.xpath2.api.typesystem.DOMTypeProvider;
//import org.eclipse.wst.xml.xpath2.api.typesystem.TypeDefinition;
//import org.eclipse.wst.xml.xpath2.api.typesystem.TypeModel;
//import org.eclipse.wst.xml.xpath2.processor.DOMLoader;
//import org.eclipse.wst.xml.xpath2.processor.DOMLoaderException;
//import org.eclipse.wst.xml.xpath2.processor.DynamicError;
//import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
//import org.eclipse.wst.xml.xpath2.processor.XercesLoader;
//import org.eclipse.wst.xml.xpath2.processor.internal.Focus;
//import org.eclipse.wst.xml.xpath2.processor.internal.function.AbstractFunction;
//import org.eclipse.wst.xml.xpath2.processor.internal.function.FunctionLibrary;
//import org.eclipse.wst.xml.xpath2.processor.internal.types.AnyType;
//import org.eclipse.wst.xml.xpath2.processor.internal.types.xerces.XercesTypeModel;
//import org.w3c.dom.Document;
//import org.w3c.dom.Node;
//
///**
// * The default implementation of a Dynamic Context.
// * 
// * Initializes and provides functionality of a dynamic context according to the
// * XPath 2.0 specification.
// */
//public class ContextBuilder implements StaticContext, DynamicContext {
//
//	private ResultSequence _focus;
//	private org.eclipse.wst.xml.xpath2.api.types.XSDuration _tz;
//	private Map _loaded_documents;
//	private GregorianCalendar _current_date_time;
//	private String _default_collation_name = CODEPOINT_COLLATION;
//	private CollationProvider _collation_provider;
//
//	/**
//	 * Constructor.
//	 * 
//	 * @param schema
//	 *            Schema information of document. May be null
//	 * @param doc
//	 *            Document [root] node of XML source.
//	 */
//	public ContextBuilder(XSModel schema, Document doc) {
//		this(new XercesTypeModel(schema));
//	}
//
//	/**
//	 * Constructor.
//	 * 
//	 * @param schema
//	 *            Schema information of document. May be null
//	 * @param doc
//	 *            Document [root] node of XML source.
//	 * @since 1.2
//	 */
//	public ContextBuilder(TypeModel schema) {
//
//		_focus = null;
//		_tz = new XSDayTimeDurationImpl(0, 5, 0, 0, true);
//		_loaded_documents = new HashMap();
//	}
//	/**
//	 * Reads the day from a TimeDuration type
//	 * 
//	 * @return an xs:integer _tz
//	 * @since 1.1
//	 */
//	public XSDuration getTimezoneOffset() {
//		return _tz;
//	}
//
//	/**
//	 * Gets the Current stable date time from the dynamic context.
//	 * @since 1.1
//	 * @see org.eclipse.wst.xml.xpath2.api.DynamicContext#get_current_time()
//	 */
//	public GregorianCalendar getCurrentDateTime() {
//		if (_current_date_time == null) {
//			_current_date_time = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
//		}
//		return _current_date_time;
//	}
//	
//	/**
//	 * Changes the current focus.
//	 * 
//	 * @param f
//	 *            focus to set
//	 */
//	public void set_focus(Focus f) {
//		_focus = f;
//	}
//
//	/**
//	 * Return the focus
//	 * 
//	 * @return _focus
//	 */
//	public Focus focus() {
//		return _focus;
//	}
//
//	/**
//	 * Retrieve context item that is in focus
//	 * 
//	 * @return an AnyType result from _focus.context_item()
//	 */
//	public AnyType getContextItem() {
//		return _focus.context_item();
//	}
//
//	/**
//	 * Retrieve the position of the focus
//	 * 
//	 * @return an integer result from _focus.position()
//	 */
//	public int context_position() {
//		return _focus.position();
//	}
//
//	/**
//	 * Retrieve the position of the last focus
//	 * 
//	 * @return an integer result from _focus.last()
//	 */
//	public int last() {
//		return _focus.last();
//	}
//
//	public Object getVariable(XSQName name) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	
//	/**
//	 * Retrieve the variable name
//	 * 
//	 * @return an AnyType result from get_var(name) or return NULL
//	 * @since 1.2
//	 */
//	public Object get_variable2(XSQName name) {
//		// XXX: built-in variables
//		if ("fs".equals(name.prefix())) {
//			if (name.local().equals("dot"))
//				return getContextItem();
//
//			return null;
//		}
//		return get_var(name);
//	}
//
//	/**
//	 * 
//	 * @return a ResultSequence from funct.evaluate(args)
//	 */
//	public ResultSequence evaluateFunction(XSQName name, Collection args)
//			throws DynamicError {
//		AbstractFunction funct = function(name, args.size());
//
//		assert funct != null;
//
//		return funct.evaluate(args);
//	}
//
//	/**
//	 * Adds function definitions.
//	 * 
//	 * @param fl
//	 *            Function library to add.
//	 * 
//	 */
//	@Override
//	public void add_function_library(FunctionLibrary fl) {
//		super.add_function_library(fl);
//		fl.set_dynamic_context(this);
//	}
//
//	/**
//	 * get document
//	 * 
//	 * @return a ResultSequence from ResultSequenceFactory.create_new()
//	 * @since 1.1
//	 */
//	public ResultSequence getDocument(URI resolved) {
//		Document doc = null;
//		if (_loaded_documents.containsKey(resolved)) {
//			 //tried before
//			doc = (Document)_loaded_documents.get(resolved);
//		} else {
//			doc = retrieve_doc(resolved);
//			_loaded_documents.put(resolved, doc);
//		}
//
//		if (doc == null)
//			return null;
//
//		return ResultSequenceFactory.create_new(new DocType(doc, getTypeModel(doc)));
//	}
//	/**
//	 * @since 1.1
//	 */
//	public URI resolveUri(String uri) {
//		try {
//			URI realURI = URI.create(uri);
//			if (realURI.isAbsolute()) {
//				return realURI;
//			} else {
//				URI baseURI = URI.create(base_uri().getStringValue());
//				return baseURI.resolve(uri);
//			}
//		} catch (IllegalArgumentException iae) {
//			return null;
//		}
//	}
//
//	// XXX make it nice, and move it out as a utility function
//	private Document retrieve_doc(URI uri) {
//		try {
//			DOMLoader loader = new XercesLoader();
//			loader.set_validating(false);
//
//			Document doc = loader.load(new URL(uri.toString()).openStream());
//			doc.setDocumentURI(uri.toString());
//			return doc;
//		} catch (DOMLoaderException e) {
//			return null;
//		} catch (FileNotFoundException e) {
//			return null;
//		} catch (MalformedURLException e) {
//			return null;
//		} catch (IOException e) {
//			return null;
//		}
//	}
//
//	/**
//	 * Sets the value of a variable.
//	 * 
//	 * @param var
//	 *            Variable name.
//	 * @param val
//	 *            Variable value.
//	 */
//	@Override
//	public void set_variable(XSQName var, AnyType val) {
//		super.set_variable(var, val);
//	}
//	
//	
//	/*
//	 * Set a XPath2 sequence into a variable.
//	 */
//	/**
//	 * @since 1.2
//	 */
//	public void set_variable(XSQName var, ResultSequence val) {
//		super.set_variable(var, val);
//	}
//
//	/**
//	 * @since 1.1
//	 */
//	public void set_default_collation(String _default_collation) {
//		this._default_collation_name = _default_collation;
//	}
//
//	/**
//	 * @since 1.1
//	 */
//	public String getDefaultCollationName() {
//		return _default_collation_name;
//	}
//
//	// We are explicitly NOT using generics here, in anticipation of JDK1.4 compatibility
//	private static Comparator CODEPOINT_COMPARATOR = new Comparator() {
//		
//		public int compare(Object o1, Object o2) {
//			return ((String)o1).compareTo((String)o2);
//		}
//	};
//	
//	/**
//	 * @since 1.1
//	 * 
//	 */
//	public Comparator<Object> get_collation(String uri) {
//		if (CODEPOINT_COLLATION.equals(uri)) return CODEPOINT_COMPARATOR;
//		
//		return _collation_provider != null ? _collation_provider.get_collation(uri) : null;
//	}
//	
//	/**
//	 * 
//	 * 
//	 * @param provider
//	 * @since 1.1
//	 */
//	public void set_collation_provider(CollationProvider provider) {
//		this._collation_provider = provider;
//	}
//	
//	/**
//	 * Use focus().position() to retrieve the value.
//	 * @deprecated  This will be removed in a future version use focus().position().
//	 */
//	@Deprecated
//	public int node_position(Node node) {
//	  // unused parameter!
//	  return _focus.position();	
//	}
//	
//	/**
//	 * @since 1.2
//	 */
//	@Override
//	public TypeModel getTypeModel(Node node) {
//		return super.getTypeModel(node);
//	}
//
//	public NodeType getLimitNode() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public ResultSequence getVariable(QName name) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public ResultSequence evaluateFunction(String name, Collection args) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public DocType getDocument(String uri) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public boolean isXPath1Compatible() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public TypeDefinition getInitialContextType() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public NamespaceContext getNamespaceContext() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public String getDefaultNamespace() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public String getDefaultFunctionNamespace() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public DOMTypeProvider getTypeProvider() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public Function resolveFunction(String name, int arity) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public URI getBaseUri() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public Collection getFunctionLibraries() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public TypeDefinition getCollectionType(String collectionName) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//}
