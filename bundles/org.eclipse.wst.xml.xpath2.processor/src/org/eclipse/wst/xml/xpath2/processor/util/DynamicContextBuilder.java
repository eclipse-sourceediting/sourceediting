package org.eclipse.wst.xml.xpath2.processor.util;

import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.namespace.QName;

import org.eclipse.wst.xml.xpath2.api.DynamicContext;
import org.eclipse.wst.xml.xpath2.api.ResultSequence;
import org.eclipse.wst.xml.xpath2.api.StaticContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


/**
 * An implementation of a Dynamic Context.
 * 
 * Initializes and provides functionality of a dynamic context according to the
 * XPath 2.0 specification.
 */
public class DynamicContextBuilder implements DynamicContext {

	private static DatatypeFactory _datatypeFactory;
	static {
		try {
			_datatypeFactory = DatatypeFactory.newInstance();
		}
		catch (DatatypeConfigurationException e) {
			throw new RuntimeException("Cannot initialize XML datatypes", e);
		}
	}
	private Duration _tz = _datatypeFactory.newDuration(0);
	private Map/*<String, DocType>*/ documents;
	private GregorianCalendar _currentDateTime;
	
	private Map/*<QName,ResultSequence>*/ _variables = new HashMap/*<QName,ResultSequence>*/();
	private StaticContext _staticContext;
	
	/**
	 * Reads the day from a TimeDuration type
	 * 
	 * @return an xs:integer _tz
	 */
	public Duration getTimezoneOffset() {
		return _tz;
	}

	/**
	 * Gets the Current stable date time from the dynamic context.
	 */
	public GregorianCalendar getCurrentDateTime() {
		if (_currentDateTime == null) {
			_currentDateTime = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
		}
		return _currentDateTime;
	}

	public Node getLimitNode() {
		return null;
	}

	public ResultSequence getVariable(QName name) {
		return (ResultSequence) _variables.get(name);
	}

	public ResultSequence evaluateFunction(QName name, Collection args) {
		
		// TODO Auto-generated method stub
		return null;
	}

	public Document getDocument(String uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public String resolveUri(String uri) {
		return uri;
	}

	public DynamicContextBuilder withStaticContext(StaticContext sc) {
		_staticContext = sc;
		return this;
	}

	public Map getCollections() {
		// TODO Auto-generated method stub
		return null;
	}

	public ResultSequence getDefaultCollection() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
