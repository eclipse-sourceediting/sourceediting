package org.eclipse.wst.web.internal.facet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentType;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentVersion;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.eclipse.wst.web.internal.WSTWebPlugin;

public class RuntimePresetMappingRegistry {

	static final String EXTENSION_POINT = "runtimePresetMappings"; //$NON-NLS-1$

	static final String ELEMENT_MAPPING = "mapping"; //$NON-NLS-1$

	static final String ATTRIBUTE_ID = "id"; //$NON-NLS-1$

	static final String ATTRIBUTE_FACET_RUNTIME_TYPE_ID = "facetRuntimeTypeID"; //$NON-NLS-1$

	static final String ATTRIBUTE_FACET_RUNTIME_VERSION = "facetRuntimeVersion"; //$NON-NLS-1$

	static final String ATTRIBUTE_FACET_ID = "facetID"; //$NON-NLS-1$

	static final String ATTRIBUTE_FACET_VERSION = "facetVersion"; //$NON-NLS-1$

	static final String ATTRIBUTE_PRESET_ID = "presetID"; //$NON-NLS-1$

	public static RuntimePresetMappingRegistry INSTANCE = new RuntimePresetMappingRegistry();

	private List<MappingDescriptor> descriptors = null;

	public String getPresetID(String facetRuntimeTypeID,
			String facetRuntimeVersion, String facetID, String facetVersion) {
		if (descriptors == null) {
			readDescriptors();
		}
		for (MappingDescriptor descriptor : descriptors) {
			if(matches(facetRuntimeTypeID, descriptor.getFacetRuntimeTypeID()) &&
			   matches(facetRuntimeVersion, descriptor.getFacetRuntimeVersion()) &&
			   matches(facetID, descriptor.getFacetID()) &&
			   matches(facetVersion, descriptor.getFacetVersion())){
				return descriptor.getPresetID();
			}
		}
		return null;
	}

	/**
	 * Returns true if the value is matched by the pattern The pattern consists
	 * of a common deliminated list of simple patterns Each simple pattern has
	 * an optional starting or ending * so a String.startsWith() or
	 * String.endsWith(). Both may be combined to compute a String.indexOf() !=
	 * -1
	 *
	 * @param value
	 * @param pattern
	 * @return
	 */
	private static boolean matches(String value, String pattern){
		StringTokenizer strTok = new StringTokenizer(pattern, ","); //$NON-NLS-1$
		while(strTok.hasMoreTokens()){
			String simplePattern = strTok.nextToken().trim();
			if(simplePattern.startsWith("*")){ //$NON-NLS-1$
				if(simplePattern.length() < 2){
					return true; // i.e. *
				}
				if(simplePattern.endsWith("*")){ //$NON-NLS-1$
					if(simplePattern.length() < 3){ 
						return true; // i.e. **
					}
					if(value.indexOf(simplePattern.substring(1, simplePattern.length()-2)) != -1){
						return true;
					}
				} else {
					if(value.endsWith(simplePattern.substring(1))){
						return true;
					}
				}
			} else if(simplePattern.endsWith("*")){ //$NON-NLS-1$
				if(value.startsWith(simplePattern.substring(0, simplePattern.length()-2))){
					return true;
				}
			} else if(value.equals(simplePattern)){
				return true;
			}
		}
		return false;
	}
	
	private static List <String> getStaticTokens(String pattern){
		List <String> staticTokens = new ArrayList <String> ();
		StringTokenizer strTok = new StringTokenizer(pattern, ","); //$NON-NLS-1$
		while(strTok.hasMoreTokens()){
			String simplePattern = strTok.nextToken().trim();
			if(!simplePattern.startsWith("*") && !simplePattern.endsWith("*")){ //$NON-NLS-1$ //$NON-NLS-2$
				staticTokens.add(simplePattern);
			}
		}
		return staticTokens;
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
					WSTWebPlugin.logError("Extension: " + EXTENSION_POINT + " Element: " + ELEMENT_MAPPING + " is missing attribute " + ATTRIBUTE_ID); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					continue;
				}

				String runtimeID = element.getAttribute(ATTRIBUTE_FACET_RUNTIME_TYPE_ID);
				if (null == runtimeID || runtimeID.trim().length() == 0) {
					WSTWebPlugin.logError("Extension: " + EXTENSION_POINT + " Element: " + ELEMENT_MAPPING + " is missing attribute " + ATTRIBUTE_FACET_RUNTIME_TYPE_ID); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					continue;
				}

				List <String> staticRuntimeIDs = getStaticTokens(runtimeID);
				List <IRuntimeComponentType> staticRuntimeTypes = new ArrayList<IRuntimeComponentType>();
				for(String staticRuntimeID : staticRuntimeIDs){
					try {
						IRuntimeComponentType runtimeType = RuntimeManager.getRuntimeComponentType(staticRuntimeID);
						if(runtimeType != null){
							staticRuntimeTypes.add(runtimeType);
						}
					} catch (IllegalArgumentException e) {
						WSTWebPlugin.logError("Extension: " + EXTENSION_POINT + " Element: " + ELEMENT_MAPPING + " defined invalid attribute " + ATTRIBUTE_FACET_RUNTIME_TYPE_ID + ": " + runtimeID +" unable to resolve runtime: "+staticRuntimeID, e); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					}
				}
					
				String runtimeVersionStr = element.getAttribute(ATTRIBUTE_FACET_RUNTIME_VERSION);
				if (null == runtimeVersionStr || runtimeVersionStr.trim().length() == 0) {
					WSTWebPlugin.logError("Extension: " + EXTENSION_POINT + " Element: " + ELEMENT_MAPPING + " is missing attribute " + ATTRIBUTE_FACET_RUNTIME_VERSION); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					continue;
				}
				if(!staticRuntimeTypes.isEmpty()){
					List <String> staticRuntimeVersions = getStaticTokens(runtimeVersionStr);
					for(String staticVersion : staticRuntimeVersions){
						boolean foundVersion = false;
						for(int k=0;k<staticRuntimeTypes.size() && !foundVersion;k++){
							IRuntimeComponentType runtimeType = staticRuntimeTypes.get(k);
							try {
								runtimeType.getVersion(staticVersion);
								foundVersion = true;
							} catch (IllegalArgumentException e) {
								//eat it
							}
						}
						if(!foundVersion){
							StringBuffer validVersions = new StringBuffer(" valid versions include: "); //$NON-NLS-1$
							for(IRuntimeComponentType runtimeType : staticRuntimeTypes) {
								validVersions.append("\n"); //$NON-NLS-1$
								validVersions.append(runtimeType.getId());
								validVersions.append(": "); //$NON-NLS-1$
								for (Iterator<IRuntimeComponentVersion> iterator = runtimeType.getVersions().iterator(); iterator.hasNext();) {
									validVersions.append(iterator.next().getVersionString());
									if (iterator.hasNext()) {
										validVersions.append(" "); //$NON-NLS-1$
									}
								}
							}
							WSTWebPlugin.logError("Extension: " + EXTENSION_POINT + " Element: " + ELEMENT_MAPPING + " defined invalid attribute " + ATTRIBUTE_FACET_RUNTIME_VERSION + ": " + staticVersion //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
									+ validVersions);
						}
					}
				}
				
				String facetID = element.getAttribute(ATTRIBUTE_FACET_ID);
				if (null == facetID || facetID.trim().length() == 0) {
					WSTWebPlugin.logError("Extension: " + EXTENSION_POINT + " Element: " + ELEMENT_MAPPING + " is missing attribute " + ATTRIBUTE_FACET_ID); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					continue;
				}
				
				List <String> staticFacetIDs = getStaticTokens(facetID);
				List <IProjectFacet> staticFacets = new ArrayList <IProjectFacet>();
				for(String staticFacetID:staticFacetIDs){
					try {
						IProjectFacet facet = ProjectFacetsManager.getProjectFacet(staticFacetID);
						if(null != facet){
							staticFacets.add(facet);
						}
					} catch (IllegalArgumentException e) {
						WSTWebPlugin.logError("Extension: " + EXTENSION_POINT + " Element: " + ELEMENT_MAPPING + " defined invalid attribute " + ATTRIBUTE_FACET_ID + ": " + staticFacetID, e); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					}
				}
				
				String facetVersionStr = element.getAttribute(ATTRIBUTE_FACET_VERSION);
				if (null == facetVersionStr || facetVersionStr.trim().length() == 0) {
					WSTWebPlugin.logError("Extension: " + EXTENSION_POINT + " Element: " + ELEMENT_MAPPING + " is missing attribute " + ATTRIBUTE_FACET_VERSION); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					continue;
				}
				List <String>staticFacetVersionStrs = getStaticTokens(facetVersionStr);
				if(!staticFacets.isEmpty() && !staticFacetVersionStrs.isEmpty()){
					for(String staticFacetVersion:staticFacetVersionStrs){
						boolean foundFacetVersion = false;
						for(int k=0;k< staticFacets.size() && !foundFacetVersion; k++) {
							IProjectFacet staticFacet = staticFacets.get(k);
							try {
								IProjectFacetVersion staticVersion = staticFacet.getVersion(staticFacetVersion);
								if(staticVersion != null){
									foundFacetVersion = true;
								}
							} catch (IllegalArgumentException e) {
								//eat it
							}
						}
						if(!foundFacetVersion){
							StringBuffer validVersions = new StringBuffer(" valid versions include: "); //$NON-NLS-1$
							for(IProjectFacet staticFacet:staticFacets){
								validVersions.append("\n"); //$NON-NLS-1$
								validVersions.append(staticFacet.getId());
								validVersions.append(": "); //$NON-NLS-1$
								for (Iterator<IProjectFacetVersion> iterator = staticFacet.getVersions().iterator(); iterator.hasNext();) {
									validVersions.append(iterator.next().getVersionString());
									if (iterator.hasNext()) {
										validVersions.append(" "); //$NON-NLS-1$
									}
								}
							}
							WSTWebPlugin.logError("Extension: " + EXTENSION_POINT + " Element: " + ELEMENT_MAPPING + " defined invalid attribute " + ATTRIBUTE_FACET_VERSION + ": " + staticFacetVersion //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
									+ validVersions);
							continue;
						}
					}	
				}
				String presetID = element.getAttribute(ATTRIBUTE_PRESET_ID);
				if (null == presetID || presetID.trim().length() == 0) {
					WSTWebPlugin.logError("Extension: " + EXTENSION_POINT + " Element: " + ELEMENT_MAPPING + " is missing attribute " + ATTRIBUTE_PRESET_ID); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					continue;
				}
				try {
					ProjectFacetsManager.getPreset(presetID);
				} catch (IllegalArgumentException e) {
					WSTWebPlugin.logError("Extension: " + EXTENSION_POINT + " Element: " + ELEMENT_MAPPING + " defined invalid attribute " + ATTRIBUTE_PRESET_ID + ": " + presetID, e); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					continue;
				}
				MappingDescriptor descriptor = new MappingDescriptor(element);
				descriptors.add(descriptor);
			} else {
				WSTWebPlugin.logError("Elements must be named: " + ELEMENT_MAPPING + " within the extension: " + EXTENSION_POINT); //$NON-NLS-1$ //$NON-NLS-2$
				WSTWebPlugin.logError("Element: " + element.getName() + " is invalid within the extension: " + EXTENSION_POINT); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}

}