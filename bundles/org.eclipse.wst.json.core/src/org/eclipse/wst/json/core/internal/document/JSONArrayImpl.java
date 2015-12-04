/**
 *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.wst.json.core.internal.document;

import org.eclipse.wst.json.core.document.IJSONArray;
import org.eclipse.wst.json.core.document.IJSONNode;
import org.eclipse.wst.json.core.document.IJSONValue;
import org.eclipse.wst.json.core.document.JSONException;

public class JSONArrayImpl extends JSONStructureImpl implements IJSONArray {

	public JSONArrayImpl() {
	}

	public JSONArrayImpl(JSONArrayImpl object) {
		super(object);
	}

	@Override
	public IJSONNode cloneNode(boolean deep) {
		JSONArrayImpl cloned = new JSONArrayImpl(this);

		if (deep)
			cloneChildNodes(cloned, deep);

		return cloned;
	}

	@Override
	public short getNodeType() {
		return IJSONNode.ARRAY_NODE;
	}

	@Override
	public String getNodeName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNodeValue() throws JSONException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IJSONArray add(IJSONValue value) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public IJSONArray remove(IJSONValue value) {
		// TODO Auto-generated method stub
		return this;
	}

	// @Override
	// public boolean getBoolean(int paramInt) {
	// // TODO Auto-generated method stub
	// return false;
	// }
	//
	// @Override
	// public boolean getBoolean(int paramInt, boolean paramBoolean) {
	// // TODO Auto-generated method stub
	// return false;
	// }
	//
	// @Override
	// public int getInt(int paramInt) {
	// // TODO Auto-generated method stub
	// return 0;
	// }
	//
	// @Override
	// public int getInt(int paramInt1, int paramInt2) {
	// // TODO Auto-generated method stub
	// return 0;
	// }
	//
	// @Override
	// public JsonArray getJsonArray(int paramInt) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public JsonNumber getJsonNumber(int paramInt) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public JsonObject getJsonObject(int paramInt) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public JsonString getJsonString(int paramInt) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public String getString(int paramInt) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public String getString(int paramInt, String paramString) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public <T extends JsonValue> List<T> getValuesAs(Class<T> paramClass) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public boolean isNull(int paramInt) {
	// // TODO Auto-generated method stub
	// return false;
	// }
	//
	// @Override
	// public ValueType getValueType() {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public boolean add(JsonValue arg0) {
	// // TODO Auto-generated method stub
	// return false;
	// }
	//
	// @Override
	// public void add(int arg0, JsonValue arg1) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public boolean addAll(Collection<? extends JsonValue> arg0) {
	// // TODO Auto-generated method stub
	// return false;
	// }
	//
	// @Override
	// public boolean addAll(int arg0, Collection<? extends JsonValue> arg1) {
	// // TODO Auto-generated method stub
	// return false;
	// }
	//
	// @Override
	// public void clear() {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public boolean contains(Object arg0) {
	// // TODO Auto-generated method stub
	// return false;
	// }
	//
	// @Override
	// public boolean containsAll(Collection<?> arg0) {
	// // TODO Auto-generated method stub
	// return false;
	// }
	//
	// @Override
	// public JsonValue get(int arg0) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public int indexOf(Object arg0) {
	// // TODO Auto-generated method stub
	// return 0;
	// }
	//
	// @Override
	// public boolean isEmpty() {
	// // TODO Auto-generated method stub
	// return false;
	// }
	//
	// @Override
	// public Iterator<JsonValue> iterator() {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public int lastIndexOf(Object arg0) {
	// // TODO Auto-generated method stub
	// return 0;
	// }
	//
	// @Override
	// public ListIterator<JsonValue> listIterator() {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public ListIterator<JsonValue> listIterator(int arg0) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public boolean remove(Object arg0) {
	// // TODO Auto-generated method stub
	// return false;
	// }
	//
	// @Override
	// public JsonValue remove(int arg0) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public boolean removeAll(Collection<?> arg0) {
	// // TODO Auto-generated method stub
	// return false;
	// }
	//
	// @Override
	// public boolean retainAll(Collection<?> arg0) {
	// // TODO Auto-generated method stub
	// return false;
	// }
	//
	// @Override
	// public JsonValue set(int arg0, JsonValue arg1) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public int size() {
	// // TODO Auto-generated method stub
	// return 0;
	// }
	//
	// @Override
	// public List<JsonValue> subList(int arg0, int arg1) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public Object[] toArray() {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public <T> T[] toArray(T[] arg0) {
	// // TODO Auto-generated method stub
	// return null;
	// }

	@Override
	public String getSimpleValue() {
		return null;
	}
	
	@Override
	public String getValueRegionType() {
		return null;
	}
}
