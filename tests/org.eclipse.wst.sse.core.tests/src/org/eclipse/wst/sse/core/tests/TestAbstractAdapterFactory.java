package org.eclipse.wst.sse.core.tests;

import junit.framework.TestCase;

import org.eclipse.wst.sse.core.AbstractAdapterFactory;
import org.eclipse.wst.sse.core.INodeAdapter;
import org.eclipse.wst.sse.core.INodeNotifier;

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

	}
}
