/*
 * Created on Feb 10, 2005
 */
package org.eclipse.wst.xsd.ui.reconcile;

import org.eclipse.wst.validation.core.IValidator;
import org.eclipse.wst.validation.plugin.ValidationPlugin;
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
	  ValidationPlugin validationPlugin = new ValidationPlugin();
      return validationPlugin.getValidator(VALIDATOR_CLASS);
    }
    catch (Exception e)
    { //
    }
    return null;
  }
}
