package org.eclipse.wst.xsd.ui.internal.common.properties.sections;

import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;

public interface IDocumentChangedNotifier
{
  public void addListener(INodeAdapter adapter);
  public void removeListener(INodeAdapter adapter);  
}
