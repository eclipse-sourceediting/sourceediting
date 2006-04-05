package org.eclipse.wst.project.facet;

/**
 * These constants define the set of properties that this pluging expects to
 * be available via <code>IProduct.getProperty(String)</code>. The status of
 * this interface and the facilities offered is highly provisional. 
 * Productization support will be reviewed and possibly modified in future 
 * releases.
 * 
 * @see org.eclipse.core.runtime.IProduct#getProperty(String)
 */

public interface IProductConstants {   
    
    public static final String APPLICATION_CONTENT_FOLDER = "earContent"; //$NON-NLS-1$
	public static final String WEB_CONTENT_FOLDER = "webContent"; //$NON-NLS-1$
	public static final String ADD_TO_EAR_BY_DEFAULT = "addToEarByDefault"; //$NON-NLS-1$
	public static final String OUTPUT_FOLDER = "outputFolder"; //$NON-NLS-1$
	
	/**
     * Alters the final perspective used by the following new project wizards
     */
	public static final String FINAL_PERSPECTIVE_WEB = "finalPerspectiveWeb"; //$NON-NLS-1$
	public static final String FINAL_PERSPECTIVE_EJB = "finalPerspectiveEjb"; //$NON-NLS-1$
	public static final String FINAL_PERSPECTIVE_EAR = "finalPerspectiveEar"; //$NON-NLS-1$
	public static final String FINAL_PERSPECTIVE_APPCLIENT = "finalPerspectiveAppClient"; //$NON-NLS-1$
	public static final String FINAL_PERSPECTIVE_JCA = "finalPerspectiveJca"; //$NON-NLS-1$
	public static final String FINAL_PERSPECTIVE_UTILITY = "finalPerspectiveUtility"; //$NON-NLS-1$
	public static final String FINAL_PERSPECTIVE_STATICWEB = "finalPerspectiveStaticWeb"; //$NON-NLS-1$
	
}
