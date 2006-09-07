package org.eclipse.wst.project.facet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;

public class ProductManager {

	/**
	 * Default values for WTP level product
	 */
	private static final String APPLICATION_CONTENT_FOLDER = "EarContent"; //$NON-NLS-1$
	private static final String WEB_CONTENT_FOLDER = "WebContent"; //$NON-NLS-1$
	private static final String ADD_TO_EAR_BY_DEFAULT = "false"; //$NON-NLS-1$
	private static final String OUTPUT_FOLDER = "build/classes"; //$NON-NLS-1$
	private static final String FINAL_PERSPECTIVE = "org.eclipse.jst.j2ee.J2EEPerspective"; //$NON-NLS-1$
	private static final char RUNTIME_SEPARATOR = ':';
	private static final String[] DEFAULT_RUNTIME_KEYS = 
							new String[]{IProductConstants.DEFAULT_RUNTIME_1,
										IProductConstants.DEFAULT_RUNTIME_2,
										IProductConstants.DEFAULT_RUNTIME_3};
	
	/**
	 * Return the value for the associated key from the Platform Product registry or return the
	 * WTP default for the J2EE cases.
	 * 
	 * @param key
	 * @return String value of product's property
	 */
	public static String getProperty(String key) {
		if (key == null)
			return null;
		String value = null;
		if (Platform.getProduct()!=null)
			value = Platform.getProduct().getProperty(key);
		if (value == null) {
			if (key.equals(IProductConstants.APPLICATION_CONTENT_FOLDER))
				return APPLICATION_CONTENT_FOLDER;
			else if (key.equals(IProductConstants.WEB_CONTENT_FOLDER))
				return WEB_CONTENT_FOLDER;
			else if (key.equals(IProductConstants.ADD_TO_EAR_BY_DEFAULT))
				return ADD_TO_EAR_BY_DEFAULT;
			else if (key.equals(IProductConstants.OUTPUT_FOLDER))
				return OUTPUT_FOLDER;
			else if (key.equals(IProductConstants.FINAL_PERSPECTIVE_APPCLIENT))
				return FINAL_PERSPECTIVE;
			else if (key.equals(IProductConstants.FINAL_PERSPECTIVE_EAR))
				return FINAL_PERSPECTIVE;
			else if (key.equals(IProductConstants.FINAL_PERSPECTIVE_EJB))
				return FINAL_PERSPECTIVE;
			else if (key.equals(IProductConstants.FINAL_PERSPECTIVE_JCA))
				return FINAL_PERSPECTIVE;
			else if (key.equals(IProductConstants.FINAL_PERSPECTIVE_STATICWEB))
				return null;
			else if (key.equals(IProductConstants.FINAL_PERSPECTIVE_UTILITY))
				return FINAL_PERSPECTIVE;
			else if (key.equals(IProductConstants.FINAL_PERSPECTIVE_WEB))
				return FINAL_PERSPECTIVE;
		}
		return value;
	}
	
	public static boolean shouldAddToEARByDefault() {
		String value = getProperty(IProductConstants.ADD_TO_EAR_BY_DEFAULT);
		return Boolean.valueOf(value).booleanValue();
	}

	public static List/*<IRuntime>*/ getDefaultRuntimes() {
		List theRuntimes = null;
		Set runtimes = RuntimeManager.getRuntimes();
		if (!runtimes.isEmpty()) {
			IRuntime defaultRuntime = null;
			//	First check if defaults are defined
			for (int i = 0; i < DEFAULT_RUNTIME_KEYS.length; i++) {
				defaultRuntime = getMatchingRuntime(DEFAULT_RUNTIME_KEYS[i], runtimes);
				if (defaultRuntime != null) {
					if (theRuntimes == null) {
						theRuntimes = new ArrayList(3);
					}
					theRuntimes.add(defaultRuntime);
				}
			}
		}
		if (theRuntimes == null) {
			theRuntimes = Collections.EMPTY_LIST;
		}
		return theRuntimes;
	}
	
	private static IRuntime getMatchingRuntime(String defaultProductRuntimeProperty, Set runtimes) {
		String defaultProductRuntimeKey = getProperty(defaultProductRuntimeProperty);
		if (defaultProductRuntimeKey == null || defaultProductRuntimeKey.length() == 0) {
			return null;
		}
		//The defaultProductRuntimeKey needs to be in the following format
		//<facet runtime id>:<facet version>.
		int seperatorIndex = defaultProductRuntimeKey.indexOf(RUNTIME_SEPARATOR);
		if (seperatorIndex < 0 && seperatorIndex < defaultProductRuntimeKey.length()) {
			//Consider throwing an exception here.
			Logger.getLogger().logError("Invalid default product runtime id.  It should follow the format <facet runtime id>:<facet version>.  Id processed: " + defaultProductRuntimeKey);
			return null;
		}
		String defaultRuntimeID = defaultProductRuntimeKey.substring(0, seperatorIndex);
		String defaultFacetVersion = defaultProductRuntimeKey.substring(seperatorIndex + 1);
		for (Iterator runtimeIt = runtimes.iterator(); runtimeIt.hasNext();) {
			IRuntime runtime = (IRuntime) runtimeIt.next();
			List runtimeComps = runtime.getRuntimeComponents();
			if (!runtimeComps.isEmpty()) {
				for (Iterator compsIter = runtimeComps.iterator(); compsIter.hasNext();) {
					IRuntimeComponent runtimeComp = (IRuntimeComponent) compsIter.next();
					if (defaultRuntimeID.equals(runtimeComp.getRuntimeComponentType().getId()) &&
						(defaultFacetVersion.equals(runtimeComp.getRuntimeComponentVersion().getVersionString()))) {
							return runtime;
					}
				}
			}
		}
		//No matches found.
		return null;
	}
}
