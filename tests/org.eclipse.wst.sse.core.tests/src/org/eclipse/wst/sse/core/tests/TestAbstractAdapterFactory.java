/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.sse.core.tests;

import junit.framework.TestCase;

import org.eclipse.wst.sse.core.internal.provisional.AbstractAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;

/**
 * 
 * @author pavery
 */
public class TestAbstractAdapterFactory extends TestCase {
	
	private AbstractAdapterFactory fFactory = null;
	
	class MyClass implements INodeAdapter {
		public boolean isAdapterForType(Object type) {
			return type instanceof MyClass;
		}
		public void notifyChanged(INodeNotifier notifier,int eventType,Object changedFeature,Object oldValue,Object newValue,int pos) {
			// noop
		}
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		setUpAdapterFactory();
	}
	
	private void setUpAdapterFactory() {
		fFactory = new AbstractAdapterFactory(TestAbstractAdapterFactory.MyClass.class, false) {
			protected INodeAdapter createAdapter(INodeNotifier target) {
				return new MyClass();
			}
		};
	}
	
	public void testAdapt() {
		fFactory.adapt(null);
	}
	
//	public void testAdaptNew() {
//		fFactory.adaptNew(null);
//	}
	
//	public void testCopy() {
//		AdapterFactory f = fFactory.copy();
//		assertNotNull(f);
//	}
	
	public void testCreate() {
		// TODO: create unit test

	}
}
