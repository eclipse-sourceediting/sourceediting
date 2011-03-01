/*******************************************************************************
 * Copyright (c) 2011 Mukul Gandhi, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mukul Gandhi - initial API and implementation
 *     Bug 338494   - prohibiting xpath expressions starting with / or // to be parsed.
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal;

public class XPathCupRestricted extends XPathCup {

  /** Default constructor. */
  public XPathCupRestricted() {super();}
  
  /** Constructor which sets the default scanner. */
  public XPathCupRestricted(java_cup.runtime.Scanner s) {super(s);}

  /** Invoke a user supplied parse action. */
  public java_cup.runtime.Symbol do_action(
    int                        act_num,
    java_cup.runtime.lr_parser parser,
    java.util.Stack            stack,
    int                        top)
    throws java.lang.Exception
  {
    if (act_num == 67 || act_num == 68 || act_num == 69) {
    	// these action numbers correspond to following grammar productions,
    	// a) FORWARD_SLASH
    	// b) FORWARD_SLASH RelativePathExpr
    	// c) FORWARD_SLASHSLASH RelativePathExpr
    	throw new Exception("Expression starts with / or //");
    }
    else {
       return action_obj.CUP$XPathCup$do_action(act_num, parser, stack, top);
    }
  }

}

