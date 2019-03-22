/*******************************************************************************
 * Copyright (c) 2004, 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package test432978;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;


public class AtBeginTag extends BodyTagSupport
{
    private int extra;
    
    public int getExtra() {
        return extra;
    }

    
    public void setExtra( int extra ) {
        this.extra = extra;
    }

    @Override
    public int doStartTag() throws JspException
    {
        pageContext.setAttribute("extra", 10);
        return EVAL_BODY_INCLUDE;
    }
}
