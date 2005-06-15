package org.eclipse.wst.xml.core.internal.catalog.provisional;


/**
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 */
public interface ICatalogElement
{
    /** Types of the catalog entries */
    /** The PUBLIC, URI or SYSTEM Catalog Entry. */
    public static final int TYPE_ENTRY = 1;

    /** The NEXT_CATALOG Catalog Entry type. */
    public static final int TYPE_NEXT_CATALOG = 10;

    /**
     * Returns the value of the '<em><b>Type</b></em>' attribute.
     * 
     * @return the value of the '<em>Type</em>' attribute.
     */
    int getType();

    /**
     * Returns the value of the attribute with specified name.
     * 
     * @return the value of the attribute with specified name.
     * @see #setAttributeValue(String)
     */
    String getAttributeValue(String name);

    /**
     * Sets the value of the named attribute.
     * 
     * @param name
     *            the name of the attribute that will be set
     * @param value
     *            the new value of the named attribute.
     * @see #getAttributeValue()
     */
    void setAttributeValue(String name, String value);

    /**
     * Returns an array of attribute names for any dynamic attributes that may exist
     * 
     * @return array of attribute names
     * @see #getAttributeValue()
     * @see #setAttributeValue(String)
     */
    String[] getAttributes();
    
    /**
     * Returns element's id string
     * 
     * @return element's id string
     */
    public String getId();
    
    /**
     * Sets element's id string
     * 
     */
    public void setId(String id);
    
    public void setOwnerCatalog(ICatalog catalog);
    
    public ICatalog getOwnerCatalog();
}
