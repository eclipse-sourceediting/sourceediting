package org.eclipse.wst.web.internal.facet;

import org.eclipse.core.runtime.IConfigurationElement;

public class MappingDescriptor {

	protected IConfigurationElement element = null;

	public MappingDescriptor(IConfigurationElement configurationElement) {
		this.element = configurationElement;
	}

	public IConfigurationElement getElement() {
		return element;
	}

	public String getID() {
		return element.getAttribute(RuntimePresetMappingRegistry.ATTRIBUTE_ID);
	}
	
	public String getFacetRuntimeTypeID() {
		return element.getAttribute(RuntimePresetMappingRegistry.ATTRIBUTE_FACET_RUNTIME_TYPE_ID);
	}

	public String getFacetRuntimeVersion() {
		return element.getAttribute(RuntimePresetMappingRegistry.ATTRIBUTE_FACET_RUNTIME_VERSION);
	}
	
	public String getFacetID() {
		return element.getAttribute(RuntimePresetMappingRegistry.ATTRIBUTE_FACET_ID);
	}

	public String getFacetVersion() {
		return element.getAttribute(RuntimePresetMappingRegistry.ATTRIBUTE_FACET_VERSION);
	}

	public String getPresetID() {
		return element.getAttribute(RuntimePresetMappingRegistry.ATTRIBUTE_PRESET_ID);
	}

	@Override
	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("id:"); //$NON-NLS-1$
		buff.append(getID());
		buff.append(" facetRuntimeTypeID:"); //$NON-NLS-1$
		buff.append(getFacetRuntimeTypeID());
		buff.append(" facetRuntimeVersion:"); //$NON-NLS-1$
		buff.append(getFacetRuntimeVersion());
		buff.append(" facetID:"); //$NON-NLS-1$
		buff.append(getFacetID());
		buff.append(" facetVersion:"); //$NON-NLS-1$
		buff.append(getFacetVersion());
		buff.append(" presetID:"); //$NON-NLS-1$
		buff.append(getPresetID());
		return buff.toString();
	}

}
