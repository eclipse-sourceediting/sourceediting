package org.eclipse.jst.jsp.ui.internal.validation;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jst.jsp.core.internal.validation.MarkupValidatorDelegate;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

public class MarkupValidatorAdapterFactory implements IAdapterFactory {

	private static final Class[] ADAPTER_LIST = { MarkupValidatorDelegate.class };

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof IDOMModel && MarkupValidatorDelegate.class.equals(adapterType)) {
			return new JSPSourceValidator();
		}
		return null;
	}

	public Class[] getAdapterList() {
		return ADAPTER_LIST;
	}

}
