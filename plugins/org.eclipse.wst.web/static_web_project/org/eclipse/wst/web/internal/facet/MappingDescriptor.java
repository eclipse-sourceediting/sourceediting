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
	
	public String getRuntimeTypeID() {
		return element.getAttribute(RuntimePresetMappingRegistry.ATTRIBUTE_RUNTIME_TYPE_ID);
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

	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("id:");
		buff.append(getID());
		buff.append("runtimeTypeID:");
		buff.append(getRuntimeTypeID());
		buff.append(" facetID:");
		buff.append(getFacetID());
		buff.append(" facetVersion:");
		buff.append(getFacetVersion());
		buff.append(" presetID:");
		buff.append(getPresetID());
		return buff.toString();
	}

}
