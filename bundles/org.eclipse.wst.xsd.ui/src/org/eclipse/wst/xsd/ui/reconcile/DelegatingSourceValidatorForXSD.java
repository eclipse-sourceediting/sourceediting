/*
 * Created on Feb 10, 2005
 */
package org.eclipse.wst.xsd.ui.reconcile;

import org.eclipse.wst.validation.core.IValidationRegistry;
import org.eclipse.wst.validation.core.IValidator;
import org.eclipse.wst.validation.internal.ValidationRegistryReader;
import org.eclipse.wst.xml.ui.reconcile.DelegatingSourceValidator;

/**
 * @author mhutchin
 *
 */
public class DelegatingSourceValidatorForXSD extends DelegatingSourceValidator
{

  final private static String VALIDATOR_CLASS = "org.eclipse.wst.xsd.validation.internal.ui.eclipse.Validator"; 

  public DelegatingSourceValidatorForXSD()
  { super();
  }
  
  protected IValidator getDelegateValidator()
  {
    try
    {
		IValidationRegistry registry = ValidationRegistryReader.getReader();
      return registry.getValidator(VALIDATOR_CLASS);
    }
    catch (Exception e)
    { //
    }
    return null;
  }
}
