package org.eclipse.wst.html.validation;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.validation.internal.operations.AWorkbenchHelper;

public class HTMLValidationWorkbenchHelper extends AWorkbenchHelper{
	/**
	 */
	public HTMLValidationWorkbenchHelper() {
		super();
	}

	/**
	 * When an IValidator associates a target object with an IMessage,
	 * the WorkbenchReporter eventually resolves that target object
	 * with an IResource. Sometimes more than one target object resolves
	 * to the same IResource (usually the IProject, which is the default
	 * IResource when an IFile cannot be found). This method is called,
	 * by the WorkbenchReporter, so that the WorkbenchReporter can 
	 * distinguish between the IMessages which are on the same IResource, 
	 * but refer to different target objects. This is needed for the 
	 * removeAllMessages(IValidator, Object) method, so that when one
	 * target object removes all of its messages, that it doesn't remove
	 * another target object's messages.
	 *
	 * This method may return null only if object is null. Otherwise, an
	 * id which can uniquely identify a particular object must be returned.
	 * The id needs to be unique only within one particular IValidator.
	 */
	public String getTargetObjectName(Object object) {
		if (object == null) return null;
		if (object instanceof IFile) return getPortableName((IFile)object);
		return object.toString();
	}
}