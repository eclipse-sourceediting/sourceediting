/*******************************************************************************
 * Copyright (c) 2004, 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package test432978;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;


public class AtBeginTei extends TagExtraInfo
{
    @Override
    public VariableInfo[] getVariableInfo( TagData data )
    {
        return new VariableInfo[] {
            new VariableInfo(
                "extra", Integer.class.getName(), true, VariableInfo.AT_BEGIN)
        };
    }
}