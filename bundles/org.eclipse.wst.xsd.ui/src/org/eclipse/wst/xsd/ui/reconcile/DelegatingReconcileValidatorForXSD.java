/*
 * Created on Feb 10, 2005
 */
package org.eclipse.wst.xsd.ui.reconcile;

import org.eclipse.wst.validation.core.IValidator;
import org.eclipse.wst.validation.internal.ValidationRegistryReader;
import org.eclipse.wst.validation.internal.ValidatorMetaData;
import org.eclipse.wst.xml.ui.reconcile.DelegatingReconcileValidator;

/**
 * @author mhutchin
 *
 */
public class DelegatingReconcileValidatorForXSD extends DelegatingReconcileValidator
{

  final private static String VALIDATOR_CLASS = "org.eclipse.wst.xsd.validation.internal.ui.eclipse.Validator"; 

  public DelegatingReconcileValidatorForXSD()
  { super();
  }
  
  protected IValidator getDelegateValidator()
  {
    try
    {
      //Get the validator:
      ValidatorMetaData validatorData = ValidationRegistryReader.getReader().getValidatorMetaData(VALIDATOR_CLASS);
      return validatorData.getValidator();
    }
    catch (Exception e)
    { //
    }
    return null;
  }
}
