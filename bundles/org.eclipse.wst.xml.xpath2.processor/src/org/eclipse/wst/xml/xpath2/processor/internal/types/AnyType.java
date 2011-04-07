/*******************************************************************************
 * Copyright (c) 2005, 2011 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0 
 *     Jesper Steen Moller  - bug 340933 - Migrate to new XPath2 API
 *******************************************************************************/
package org.eclipse.wst.xml.xpath2.processor.internal.types;

import java.util.Collections;
import java.util.Iterator;

import org.eclipse.wst.xml.xpath2.api.Item;
import org.eclipse.wst.xml.xpath2.api.typesystem.ItemType;
import org.eclipse.wst.xml.xpath2.api.typesystem.TypeDefinition;
import org.eclipse.wst.xml.xpath2.processor.internal.types.builtin.SingleItemSequence;

/**
 * Common base for every type
 */
public abstract class AnyType implements SingleItemSequence {
	/**
	 * Retrieves the datatype's full pathname
	 * 
	 * @return Datatype's full pathname
	 */
	public abstract String string_type();

	/**
	 * Retrieves the datatype's name
	 * 
	 * @return Datatype's name
	 */
	public abstract String string_value();
	
	/**
	 * Returns the "new style" of TypeDefinition for this item.
	 * 
	 * @return Type definition (possibly backed by a schema type)
	 */
	public abstract TypeDefinition getTypeDefinition();

	public boolean empty() {
		return false;
	}
	
	public Iterator iterator() {
		return Collections.singletonList(this).iterator();
	}
	
	public ItemType getItemType() {
		return new SimpleAtomicItemTypeImpl(getTypeDefinition());
	}
	
//	abstract public Object getNativeValue();
	public Object getNativeValue() {
		return Boolean.TRUE;
	}
	
	public int size() {
		return 1;
	}

	public Item item(int index) {
		checkIOOB(index);
		return this;
	}

	private void checkIOOB(int index) {
		if (index != 0) throw new IndexOutOfBoundsException("Index out of bounds, index = " + index + ", length = 1");
	}

	public Object value(int index) {
		checkIOOB(index);
		return getNativeValue();
	}
	public ItemType itemType(int index) {
		checkIOOB(index);
		return getItemType();
	}
	
	public AnyType first() {
		return this;
	}

	public Object firstValue() {
		return this.getNativeValue();
	}

	public TypeDefinition sequenceType() {
		return getTypeDefinition();
	}
	

}
