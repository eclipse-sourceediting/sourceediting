package org.eclipse.wst.xml.core.internal.contentmodel.internal.modelqueryimpl;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.extension.ModelQueryExtension;

;
/**
 * Describes an extension to the <code>modelQueryExtension</code> extension
 * point.
 * 
 */
public class ModelQueryExtensionDescriptor
{
  private static final String CONTENT_TYPE_ATTRIBUTE = "contentType"; //$NON-NLS-1$
  private static final String NAMESPACE_ATTRIBUTE = "namespace"; //$NON-NLS-1$
  private static final String CLASS_ATTRIBUTE = "class"; //$NON-NLS-1$
  private IConfigurationElement configuration;
  private String contentTypeId;
  private String namespace;
  private ModelQueryExtension extension;

  public ModelQueryExtensionDescriptor(IConfigurationElement element)
  {
    configuration = element;
  }

  public ModelQueryExtension createModelQueryExtension() throws CoreException
  {
    if (extension == null)
    {  
      extension = (ModelQueryExtension) configuration.createExecutableExtension(CLASS_ATTRIBUTE);
    }  
    return extension;
  }

  public String getContentTypeId()
  {
    if (contentTypeId == null)
    {
      contentTypeId = configuration.getAttribute(CONTENT_TYPE_ATTRIBUTE);
    }
    return contentTypeId;
  }

  public String getNamespace()
  {
    if (namespace == null)
    {
      namespace = configuration.getAttribute(NAMESPACE_ATTRIBUTE);
    }
    return namespace;
  }
}
