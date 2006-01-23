package org.eclipse.wst.xsd.ui.internal.search;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.common.core.search.pattern.QualifiedName;
import org.eclipse.wst.common.core.search.pattern.SearchPattern;
import org.eclipse.wst.common.core.search.scope.SearchScope;
import org.eclipse.wst.common.ui.internal.search.AbstractSearchQuery;
import org.eclipse.wst.xml.core.internal.search.XMLComponentDeclarationPattern;
import org.eclipse.wst.xml.core.internal.search.XMLComponentReferencePattern;

public class XSDSearchQuery extends AbstractSearchQuery
{   
  public final static int LIMIT_TO_DECLARATIONS = 1;
  public final static int LIMIT_TO_REFERENCES   = 2;  
  
  int fLimitTo = 0;
  IFile fContextFile;
  QualifiedName fElementQName;
  QualifiedName fTypeName;
  
  public XSDSearchQuery(String pattern, IFile file, QualifiedName elementQName, QualifiedName typeName, int limitTo, SearchScope scope, String scopeDescription)
  {
    super(pattern, scope, scopeDescription);
    fLimitTo = limitTo;
    fContextFile = file;
    fElementQName = elementQName;
    fTypeName = typeName;
  }

  protected SearchPattern createSearchPattern(QualifiedName typeName)
  {
    if (fLimitTo == LIMIT_TO_DECLARATIONS)
    {  
      return new XMLComponentDeclarationPattern(fContextFile, fElementQName, fTypeName);
    }  
    else if (fLimitTo == LIMIT_TO_REFERENCES)
    {
      return new XMLComponentReferencePattern(fContextFile, fElementQName, fTypeName);
    }  
    return null;
  }
}

