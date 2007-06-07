/**
 * 
 */
package org.eclipse.wst.jsdt.web.ui;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.eclipse.wst.jsdt.core.IJavaElement;

/**
 * @author childsb
 *
 */
public class JsWebElementAdapterFactory implements IAdapterFactory{

	private static Class[] PROPERTIES= new Class[] {IPropertySourceProvider.class};
	
	private static final IPropertySourceProvider provider_instance = new JsWebElementPropertySourceProviderAdapter();
	
	public JsWebElementAdapterFactory() {
		
	}
	
	private static class JsWebElementPropertySourceProviderAdapter implements IPropertySourceProvider{
		public IPropertySource getPropertySource(Object object) {
			// TODO Auto-generated method stub
			System.out.println("Unimplemented method:JsWebElementPropertySourceProvider.getPropertySource");
			return new JsWebElementPropertySource((IJavaElement)object);
		}
	}
	
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		// TODO Auto-generated method stub
		System.out.println("Unimplemented method:JsWebElementAdapterFactory.getAdapter");
		if(adapterType== IPropertySourceProvider.class) {
			return provider_instance;
		}
		return null;
	}

	public Class[] getAdapterList() {
		return PROPERTIES;
	}
	
}
