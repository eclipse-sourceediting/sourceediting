package org.eclipse.wst.xml.core.internal.catalog.provisional;

/**
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 */
public interface ICatalogEntry extends ICatalogElement
{
    /** The SYSTEM Catalog Entry type. */
    public static final int ENTRY_TYPE_PUBLIC = 2;

    /** The SYSTEM Catalog Entry type. */
    public static final int ENTRY_TYPE_SYSTEM = 3;

    /** The URI Catalog Entry type. */
    public static final int ENTRY_TYPE_URI = 4;

    /** Attribute name for Web address of catalog entry */
    public static final String ATTR_WEB_URL = "webURL";

    /**
     * 
     * @param entryType
     */
    public void setEntryType(int entryType);

    /**
     * 
     * @return
     */
    public int getEntryType();

    /**
     * 
     * @param key
     */
    public void setKey(String key);

    /**
     * 
     * @return
     */
    public String getKey();

    /**
     * 
     * @return
     */
    public String getURI();

    /**
     * 
     * @param uri
     */
    public void setURI(String uri);
}
