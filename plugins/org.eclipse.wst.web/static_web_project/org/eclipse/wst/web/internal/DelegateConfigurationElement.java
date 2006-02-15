/*
 * Created on Dec 14, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.wst.web.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.InvalidRegistryObjectException;

/**
 * An object which represents the user-defined contents of an extension in a plug-in manifest.
 */
public class DelegateConfigurationElement implements IConfigurationElement {

	private final IConfigurationElement delegateElement;
	private static final String DELEGATE_NAME = "delegateConfigurationElement"; //$NON-NLS-1$ 
	private static final String DELEGATE_NAMESPACE = "delegateConfigurationElementNamespace"; //$NON-NLS-1$ 
	private static final String DELEGATE_NULL_STRING = "delegateConfigurationElement: NULL"; //$NON-NLS-1$ 

	public DelegateConfigurationElement(IConfigurationElement aDelegate) {
		delegateElement = aDelegate;
	}

	/**
	 * @param propertyName
	 * @return
	 * @throws org.eclipse.core.runtime.CoreException
	 */
	public Object createExecutableExtension(String propertyName) throws CoreException {
		if (delegateElement == null)
			return null;
		return delegateElement.createExecutableExtension(propertyName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (delegateElement == null)
			return false;
		return delegateElement.equals(obj);
	}

	/**
	 * @param name
	 * @return
	 * @throws org.eclipse.core.runtime.InvalidRegistryObjectException
	 */
	public String getAttribute(String name) throws InvalidRegistryObjectException {
		if (delegateElement == null)
			return null;
		return delegateElement.getAttribute(name);
	}

	/**
	 * @param name
	 * @return
	 * @throws org.eclipse.core.runtime.InvalidRegistryObjectException
	 */
	public String getAttributeAsIs(String name) throws InvalidRegistryObjectException {
		if (delegateElement == null)
			return null;
		return delegateElement.getAttributeAsIs(name);
	}

	/**
	 * @return
	 * @throws org.eclipse.core.runtime.InvalidRegistryObjectException
	 */
	public String[] getAttributeNames() throws InvalidRegistryObjectException {
		if (delegateElement == null)
			return new String[0];
		return delegateElement.getAttributeNames();
	}

	/**
	 * @return
	 * @throws org.eclipse.core.runtime.InvalidRegistryObjectException
	 */
	public IConfigurationElement[] getChildren() throws InvalidRegistryObjectException {
		if (delegateElement == null)
			return new IConfigurationElement[0];
		return delegateElement.getChildren();
	}

	/**
	 * @param name
	 * @return
	 * @throws org.eclipse.core.runtime.InvalidRegistryObjectException
	 */
	public IConfigurationElement[] getChildren(String name) throws InvalidRegistryObjectException {
		if (delegateElement == null)
			return new IConfigurationElement[0];
		return delegateElement.getChildren(name);
	}

	/**
	 * @return
	 * @throws org.eclipse.core.runtime.InvalidRegistryObjectException
	 */
	public IExtension getDeclaringExtension() throws InvalidRegistryObjectException {
		if (delegateElement == null)
			throw new InvalidRegistryObjectException();
		return delegateElement.getDeclaringExtension();
	}

	/**
	 * @return
	 * @throws org.eclipse.core.runtime.InvalidRegistryObjectException
	 */
	public String getName() throws InvalidRegistryObjectException {
		if (delegateElement == null)
			return DELEGATE_NAME;
		return delegateElement.getName();
	}

	/**
	 * @return
	 * @throws org.eclipse.core.runtime.InvalidRegistryObjectException
	 */
	public String getNamespace() throws InvalidRegistryObjectException {
		if (delegateElement == null)
			return DELEGATE_NAMESPACE;
		return delegateElement.getNamespace();
	}

	/**
	 * @return
	 * @throws org.eclipse.core.runtime.InvalidRegistryObjectException
	 */
	public Object getParent() throws InvalidRegistryObjectException {
		if (delegateElement == null)
			return null;
		return delegateElement.getParent();
	}

	/**
	 * @return
	 * @throws org.eclipse.core.runtime.InvalidRegistryObjectException
	 */
	public String getValue() throws InvalidRegistryObjectException {
		if (delegateElement == null)
			return null;
		return delegateElement.getValue();
	}

	/**
	 * @return
	 * @throws org.eclipse.core.runtime.InvalidRegistryObjectException
	 */
	public String getValueAsIs() throws InvalidRegistryObjectException {
		if (delegateElement == null)
			return null;
		return delegateElement.getValueAsIs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		if (delegateElement == null)
			return -1;
		return delegateElement.hashCode();
	}

	/**
	 * @return
	 */
	public boolean isValid() {

		if (delegateElement == null)
			return false;
		return delegateElement.isValid();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (delegateElement == null)
			return DELEGATE_NULL_STRING;
		return delegateElement.toString();
	}

	public IConfigurationElement toEquinox() {
		// TODO Auto-generated method stub
		return null;
	}

}