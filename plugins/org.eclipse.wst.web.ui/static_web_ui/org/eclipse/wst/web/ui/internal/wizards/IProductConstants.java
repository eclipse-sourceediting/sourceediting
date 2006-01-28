/******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 ******************************************************************************/

package org.eclipse.wst.web.ui.internal.wizards;

/**
 * These constants define the set of properties that this pluging expects to
 * be available via <code>IProduct.getProperty(String)</code>. The status of
 * this interface and the facilities offered is highly provisional. 
 * Productization support will be reviewed and possibly modified in future 
 * releases.
 * 
 * @see org.eclipse.core.runtime.IProduct#getProperty(String)
 */

public interface IProductConstants 
{
    /**
     * <p>Alters the final perspective used by the following new project 
     * wizards:</p>
     * 
     * <ul>
     *   <li>EJB -> EJB Project</li>
     *   <li>J2EE -> Application Client Project</li>
     *   <li>J2EE -> Connector Project</li>
     *   <li>J2EE -> Enterprise Application Project</li>
     *   <li>J2EE -> Utility Project</li>
     *   <li>Web -> Dynamic Web Project</li>
     *   <li>Web -> Static Web Project</li>
     * </ul>
     * 
     * The default value is: org.eclipse.jst.j2ee.J2EEPerspective.
     */
    
    public static final String FINAL_PERSPECTIVE 
        = "j2eeNewProjectFinalPerspective"; //$NON-NLS-1$
}
