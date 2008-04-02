package org.eclipse.wst.web.internal.facet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.web.internal.WSTWebPlugin;

public class RuntimePresetMappingRegistry {

	static final String EXTENSION_POINT = "RuntimePresetMappings";

	static final String ELEMENT_MAPPING = "Mapping";

	static final String ATTRIBUTE_ID = "id";

	static final String ATTRIBUTE_RUNTIME_TYPE_ID = "runtimeTypeID";

	static final String ATTRIBUTE_FACET_ID = "facetID";

	static final String ATTRIBUTE_FACET_VERSION = "facetVersion";

	static final String ATTRIBUTE_PRESET_ID = "presetID";

	public static RuntimePresetMappingRegistry INSTANCE = new RuntimePresetMappingRegistry();

	private List<MappingDescriptor> descriptors = null;

	public String getPresetID(String runtimeID, String facetID, String facetVersionStr) {
		if (descriptors == null) {
			readDescriptors();
		}
		for(MappingDescriptor descriptor:descriptors){
			if(descriptor.getRuntimeTypeID().equals(runtimeID) && descriptor.getFacetID().equals(facetID) && facetVersionStr.equals(descriptor.getFacetVersion())){
				return descriptor.getPresetID();
			}
		}
		return null;
	}

	private void readDescriptors() {
		descriptors = new ArrayList<MappingDescriptor>();
		IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(WSTWebPlugin.PLUGIN_ID, EXTENSION_POINT);
		if (point == null)
			return;
		IConfigurationElement[] elements = point.getConfigurationElements();
		for (int i = 0; i < elements.length; i++) {
			IConfigurationElement element = elements[i];
			if (ELEMENT_MAPPING.equals(element.getName())) {
				String id = element.getAttribute(ATTRIBUTE_ID);
				if (null == id || id.trim().length() == 0) {
					WSTWebPlugin.logError("Extension: " + EXTENSION_POINT + " Element: " + ELEMENT_MAPPING + " is missing attribute " + ATTRIBUTE_ID);
					continue;
				}
				
				String runtimeID = element.getAttribute(ATTRIBUTE_RUNTIME_TYPE_ID);
				if (null == runtimeID || runtimeID.trim().length() == 0) {
					WSTWebPlugin.logError("Extension: " + EXTENSION_POINT + " Element: " + ELEMENT_MAPPING + " is missing attribute " + ATTRIBUTE_RUNTIME_TYPE_ID);
					continue;
				}

				String facetID = element.getAttribute(ATTRIBUTE_FACET_ID);
				if (null == facetID || facetID.trim().length() == 0) {
					WSTWebPlugin.logError("Extension: " + EXTENSION_POINT + " Element: " + ELEMENT_MAPPING + " is missing attribute " + ATTRIBUTE_FACET_ID);
					continue;
				}
				IProjectFacet facet = null;
				try {
					facet = ProjectFacetsManager.getProjectFacet(facetID);
				} catch (IllegalArgumentException e) {
					WSTWebPlugin.logError("Extension: " + EXTENSION_POINT + " Element: " + ELEMENT_MAPPING + " defined invalid attribute " + ATTRIBUTE_FACET_ID + ": " + facetID, e);
					continue;
				}

				String facetVersionStr = element.getAttribute(ATTRIBUTE_FACET_VERSION);
				if (null == facetVersionStr || facetVersionStr.trim().length() == 0) {
					WSTWebPlugin.logError("Extension: " + EXTENSION_POINT + " Element: " + ELEMENT_MAPPING + " is missing attribute " + ATTRIBUTE_FACET_VERSION);
					continue;
				}
				IProjectFacetVersion facetVersion = facet.getVersion(facetVersionStr);
				if (facetVersion == null) {
					StringBuffer validVersions = new StringBuffer("valid versions include: ");
					for (Iterator<IProjectFacetVersion> iterator = facet.getVersions().iterator(); iterator.hasNext();) {
						validVersions.append(iterator.next().getVersionString());
						if (iterator.hasNext()) {
							validVersions.append(" ");
						}
					}
					WSTWebPlugin.logError("Extension: " + EXTENSION_POINT + " Element: " + ELEMENT_MAPPING + " defined invalid attribute " + ATTRIBUTE_FACET_VERSION + ": " + facetVersionStr
							+ validVersions);
					continue;
				}

				String presetID = element.getAttribute(ATTRIBUTE_PRESET_ID);
				if (null == presetID || presetID.trim().length() == 0) {
					WSTWebPlugin.logError("Extension: " + EXTENSION_POINT + " Element: " + ELEMENT_MAPPING + " is missing attribute " + ATTRIBUTE_PRESET_ID);
					continue;
				}
				try {
					ProjectFacetsManager.getPreset(presetID);
				} catch (IllegalArgumentException e) {
					WSTWebPlugin.logError("Extension: " + EXTENSION_POINT + " Element: " + ELEMENT_MAPPING + " defined invalid attribute " + ATTRIBUTE_PRESET_ID + ": " + presetID, e);
					continue;
				}
				MappingDescriptor descriptor = new MappingDescriptor(element);
				descriptors.add(descriptor);
			} else {
				WSTWebPlugin.logError("Elements must be named: " + ELEMENT_MAPPING + " within the extension: " + EXTENSION_POINT);
				WSTWebPlugin.logError("Element: " + element.getName() + " is invalid within the extension: " + EXTENSION_POINT);
			}
		}
	}

}