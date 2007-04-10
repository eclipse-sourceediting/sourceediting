/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.contentmodel;



import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

/**
 */
abstract class DeclCollection implements CMNamedNodeMap {


	protected class DualMap {
		public DualMap() {
			super();
		}

		public DualMap(Object[] objects) {
			super();
			initialize(objects);
		}

		public int size() {
			return table.length;
		}

		public Object getValue(int key) {
			if (!isValidIndex(key))
				return null;
			return table[key];
		}

		public int getKey(Object value) {
			Integer keyObj = (Integer) map.get(value);
			if (keyObj == null)
				return ID_UNKNOWN;
			return keyObj.intValue();
		}

		protected void initialize(Object[] objects) {
			if (objects == null)
				return;
			table = objects;
			map = new HashMap();
			for (int key = 0; key < objects.length; key++) {
				Object value = table[key];
				map.put(value, new Integer(key));
			}
		}

		private Object[] table = null;
		private HashMap map = null;

		private boolean isValidIndex(int index) {
			return index >= 0 && index < table.length;
		}
	}

	protected class TolerantStringDualMap extends DualMap {
		public TolerantStringDualMap(String[] names) {
			super();
			Object[] objects = new Object[names.length];
			for (int i = 0; i < names.length; i++) {
				objects[i] = makeCanonicalForm(names[i]);
			}
			initialize(objects);
		}

		public int getKey(Object value) {
			try {
				String name = (String) value;
				return super.getKey(makeCanonicalForm(name));
			}
			catch (ClassCastException e) {
				return ID_UNKNOWN;
			}
		}

		private String makeCanonicalForm(String raw) {
			// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=100152
			// we are able to "cheat" here a little and use US Locale 
			// to get a good cononical form, since we are using this only 
			// for HTML and JSP standard tags. 
			// Long term, for similar needs with XML 1.1 (for example)
			// we should use a class such as com.ibm.icu.text.Normalizer
			return raw.toUpperCase(Locale.US);
		}
	}

	private class DeclIterator implements Iterator {
		public DeclIterator() {
			maxid = fDecls.length - 1;
		}

		public boolean hasNext() {
			return id < maxid;
		}

		public Object next() {
			if (!hasNext())
				return null;
			return item(++id);
		}

		public void remove() { /* nothing should be done. */
		}

		private int id = -1;
		private int maxid = -1;
	}

	CMNode[] fDecls = null;
	protected final static boolean STRICT_CASE = false;
	protected final static boolean TOLERANT_CASE = true;
	protected final static int ID_UNKNOWN = -1;
	private DualMap fMap = null;

	/**
	 */
	public DeclCollection(String[] names, boolean tolerant) {
		super();
		fDecls = new CMNode[names.length];
		if (tolerant) {
			fMap = new TolerantStringDualMap(names);
		}
		else {
			fMap = new DualMap(names);
		}
	}

	/**
	 * @return org.eclipse.wst.xml.core.internal.contentmodel.CMNode
	 * @param id int
	 */
	protected abstract CMNode create(String name);

	/**
	 */
	public CMNamedNodeMap getDeclarations(String[] names) {
		CMNamedNodeMapImpl map = new CMNamedNodeMapImpl();
		for (int i = 0; i < names.length; i++) {
			String name = names[i];
			CMNode node = getNamedItem(name);
			if (node == null)
				continue;
			map.putNamedItem(name, node);
		}
		return map;
	}

	/**
	 * @param names java.util.Iterator
	 */
	public void getDeclarations(CMGroupImpl group, Iterator names) {
		while (names.hasNext()) {
			String entityName = (String) names.next();
			CMNode dec = getNamedItem(entityName);
			if (dec != null)
				group.appendChild(dec);
		}
	}

	/**
	 * Map name to id.
	 * @return int
	 * @param name java.lang.String
	 */
	protected int getID(String name) {
		return fMap.getKey(name);
	}

	/**
	 * getLength method
	 * @return int
	 */
	public int getLength() {
		return fDecls.length;
	}

	/**
	 * @return java.lang.String
	 * @param id int
	 */
	protected String getName(int id) {
		return (String) fMap.getValue(id);
	}

	/**
	 * getNamedItem method
	 * @return CMNode
	 * @param name java.lang.String
	 */
	public CMNode getNamedItem(String name) {
		int id = getID(name);
		if (!isValidID(id))
			return null;
		return item(id);
	}

	/**
	 * @return boolean
	 * @param id int
	 */
	private boolean isValidID(int id) {
		return id >= 0 && id < fDecls.length;
	}

	/**
	 * item method
	 * @return CMNode
	 * @param index int
	 */
	public CMNode item(int index) {
		if (!isValidID(index))
			return null;
		CMNode decl = fDecls[index];
		if (decl != null)
			return decl; // already exist.

		decl = create(getName(index));
		fDecls[index] = decl;
		return decl;
	}

	/**
	 */
	public Iterator iterator() {
		return new DeclIterator();
	}
}
