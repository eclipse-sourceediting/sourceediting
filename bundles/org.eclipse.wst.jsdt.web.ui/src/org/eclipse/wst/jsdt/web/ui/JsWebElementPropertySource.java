/**
 * 
 */
package org.eclipse.wst.jsdt.web.ui;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.wst.jsdt.core.IJavaElement;



/**
 * @author childsb
 *
 */
public class JsWebElementPropertySource implements IPropertySource {

	private IJavaElement javaElement;
	
	public JsWebElementPropertySource(IJavaElement element) {
		this.javaElement = element;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */
	public Object getEditableValue() {
		// TODO Auto-generated method stub
		System.out.println("Unimplemented method:JsWebElementProperySource.getEditableValue");
		return getDefaultPropertySource().getEditableValue();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		// TODO Auto-generated method stub
		System.out.println("Unimplemented method:JsWebElementProperySource.getPropertyDescriptors");
		return getDefaultPropertySource().getPropertyDescriptors();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		// TODO Auto-generated method stub
		System.out.println("Unimplemented method:JsWebElementProperySource.getPropertyValue");
		return getDefaultPropertySource().getPropertyValue(id);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
	 */
	public boolean isPropertySet(Object id) {
		// TODO Auto-generated method stub
		System.out.println("Unimplemented method:JsWebElementProperySource.isPropertySet");
		return getDefaultPropertySource().isPropertySet(id);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
	 */
	public void resetPropertyValue(Object id) {
		// TODO Auto-generated method stub
		System.out.println("Unimplemented method:JsWebElementProperySource.resetPropertyValue");
		getDefaultPropertySource().resetPropertyValue(id);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) {
		// TODO Auto-generated method stub
		System.out.println("Unimplemented method:JsWebElementProperySource.setPropertyValue");
		getDefaultPropertySource().setPropertyValue(id, value);
	}
	
	private IPropertySource getDefaultPropertySource() {
		return (IPropertySource)((IAdaptable)javaElement).getAdapter(IPropertySource.class);
	}

}
