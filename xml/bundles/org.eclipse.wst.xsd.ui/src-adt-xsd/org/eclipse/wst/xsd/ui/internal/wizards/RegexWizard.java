/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.wizards;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDPatternFacet;
import org.eclipse.xsd.impl.XSDFactoryImpl;


public class RegexWizard extends Wizard 
{
  private RegexCompositionPage compositionPage;
  private RegexTestingPage testingPage;

  /* The original, unchanged pattern. */  
  private XSDPatternFacet originalPattern;
  
  /* A copy of the original pattern that is passed into the wizard. */
  private XSDPatternFacet modifiedPattern;

  String pattern;

  public RegexWizard(String expr)
  {
    super();
    setWindowTitle(XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_TITLE"));
    setDefaultPageImageDescriptor(ImageDescriptor.createFromFile(XSDEditorPlugin.class, "icons/regx_wiz.png"));

    XSDFactoryImpl factory = new XSDFactoryImpl();
    modifiedPattern = factory.createXSDPatternFacet();
    modifiedPattern.setLexicalValue(expr);

    originalPattern = factory.createXSDPatternFacet();
    originalPattern.setLexicalValue(expr);

    compositionPage = new RegexCompositionPage(modifiedPattern);
    addPage(compositionPage);

    testingPage = new RegexTestingPage();
    addPage(testingPage);
  }

  public String getPattern()
  {
    return pattern;
  }
  
  public boolean canFinish()
  {
    return (compositionPage.getValue().length() > 0);
  }

  public boolean performFinish()
  {
    pattern = modifiedPattern.getLexicalValue();
    return true;
  }
}
