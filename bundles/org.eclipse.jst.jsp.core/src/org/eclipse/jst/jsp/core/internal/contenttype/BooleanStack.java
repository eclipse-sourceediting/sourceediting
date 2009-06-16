/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.contenttype;

/*
 * 
 * A non-resizable class implementing the behavior of java.util.Stack, but
 * directly for the <code> boolean </code> primitive.
 */
import java.util.EmptyStackException;

public class BooleanStack {
	private boolean[] list = null;

	private int size = 0;
	
	/**
	 * This field is not meant to be modified by anyone! In a typical document, tags should not
	 * be embedded to such a depth that it would cause a stack overflow. Three tests in JSPTokenizerTest (test144807_*)
	 * create embedded tags up to a depth of 400. Because of this test, we allow for modification of the maxDepth as a
	 * static field before the JSPTokenizer is created.
	 * 
	 * Changing this value can impact the tokenizing of a JSP file!
	 */
	public static int maxDepth = 100;

	public BooleanStack() {
		this(maxDepth);
	}

	public BooleanStack(int maxdepth) {
		super();
		list = new boolean[maxdepth];
		initialize();
	}

	public void clear() {
		initialize();
	}

	public boolean empty() {
		return size == 0;
	}

	public boolean get(int slot) {
		return list[slot];
	}

	private void initialize() {
		size = 0;
	}

	/**
	 * Returns the boolean at the top of the stack without removing it
	 * 
	 * @return boolean at the top of this stack.
	 * @exception EmptyStackException
	 *                when empty.
	 */
	public boolean peek() {
		if (size == 0)
			throw new EmptyStackException();
		return list[size - 1];
	}

	/**
	 * Removes and returns the boolean at the top of the stack
	 * 
	 * @return boolean at the top of this stack.
	 * @exception EmptyStackException
	 *                when empty.
	 */
	public boolean pop() {
		boolean value = peek();
		list[size - 1] = false;
		size--;
		return value;
	}

	/**
	 * Pushes an item onto the top of this stack.
	 * 
	 * @param newValue -
	 *            the boolean to be pushed onto this stack.
	 * @return the <code>newValue</code> argument.
	 */
	public boolean push(boolean newValue) {
		if (size == list.length) {
			throw new StackOverflowError();
		}
		list[size++] = newValue;
		return newValue;
	}

	public int size() {
		return size;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer s = new StringBuffer(getClass().getName() + ":" +size + " [");
		for (int i = 0; i < size; i++) {
			s.append(list[i]);
			if(i < size - 1) {
				s.append(", ");
			}
		}
		s.append("]");
		return s.toString();
	}
	
	public static void setMaxDepth(int depth) {
		
	}
}
