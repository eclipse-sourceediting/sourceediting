package org.eclipse.wst.project.facet;

import org.eclipse.core.runtime.Platform;

public class ProductManager {

	/**
	 * Default values for WTP level product
	 */
	private static final String APPLICATION_CONTENT_FOLDER = "EarContent"; //$NON-NLS-1$
	private static final String WEB_CONTENT_FOLDER = "WebContent"; //$NON-NLS-1$
	private static final String ADD_TO_EAR_BY_DEFAULT = "false"; //$NON-NLS-1$
	private static final String OUTPUT_FOLDER = "build/classes"; //$NON-NLS-1$
	private static final String FINAL_PERSPECTIVE = "org.eclipse.jst.j2ee.J2EEPerspective"; //$NON-NLS-1$
	
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
	
	
}
