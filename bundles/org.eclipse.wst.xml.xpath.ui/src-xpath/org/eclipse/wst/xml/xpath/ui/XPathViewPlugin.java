/*******************************************************************************
 * Copyright (c) 2005-2007 Orangevolt (www.orangevolt.com)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Orangevolt (www.orangevolt.com) - XSLT support
 *     Jesper Steen Moller - refactored Orangevolt XSLT support into WST
 *     
 *******************************************************************************/

package org.eclipse.wst.xml.xpath.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.xml.xpath.ui.views.XPathNavigator;
import org.osgi.framework.BundleContext;


public class XPathViewPlugin extends AbstractUIPlugin implements IPartListener2
{
    //The shared instance.
	private static XPathViewPlugin plugin;

	//Resource bundle.
	private ResourceBundle resourceBundle;
	
	/**
	 * The constructor.
	 */
	public XPathViewPlugin() {
		super();
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception 
    {
		super.start(context);
		plugin = this;		
        
        IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows(); 
        for( int i=0; i<windows.length; i++)
        {
        	windows[i].getPartService().addPartListener( this); 
        }
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
		resourceBundle = null;
        
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows(); 
        for( int i=0; i<windows.length; i++)
        {
        	windows[i].getPartService().removePartListener( this); 
        }
	}

	/**
	 * Returns the shared instance.
	 */
	public static XPathViewPlugin getDefault() {
		return plugin;
	}

	public static String getPluginId()
	{
		return getDefault().getBundle().getSymbolicName();
	}
	
    public void info( String message) 
    {
        getLog().log(new Status(IStatus.INFO, getPluginId(), IStatus.OK, message, null));
    }

    public void warn( String message) 
    {
        getLog().log(new Status(IStatus.WARNING, getPluginId(), IStatus.ERROR, message, null));
    }

	public void log( String message, Throwable ex) 
	{
		getLog().log(new Status(IStatus.ERROR, getPluginId(), IStatus.ERROR, "Error", ex)); //$NON-NLS-1$
	}
	
	public void log(Throwable ex) 
	{
		log( "Error", ex); //$NON-NLS-1$
	}
	
	public static void throwCoreException( String message, Throwable t) throws CoreException
	{
		throw new CoreException
		(
			new Status(IStatus.ERROR, getPluginId(), IStatus.ERROR, message, t)
		);
	}
	
	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = XPathViewPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/* (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeImageRegistry(org.eclipse.jface.resource.ImageRegistry)
     */
    protected void initializeImageRegistry(ImageRegistry reg)
    {
        super.initializeImageRegistry(reg);

        try
        {
            URL baseURL = getBundle().getEntry( "/icons/"); //$NON-NLS-1$
            reg.put(
                    "refresh", //$NON-NLS-1$
                    ImageDescriptor.createFromURL( new URL( baseURL, "refresh.gif"))); //$NON-NLS-1$

            reg.put(
                    "run", //$NON-NLS-1$
                    ImageDescriptor.createFromURL( new URL( baseURL, "run.gif"))); //$NON-NLS-1$
}
        catch (MalformedURLException e)
        {
            log(e);
        }
    }

	/**
	 * Returns the plugin's resource bundle
	 */
	public ResourceBundle getResourceBundle() {
		try {
			if (resourceBundle == null)
				resourceBundle = ResourceBundle.getBundle(""); //$NON-NLS-1$
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}
    
    private XPathNavigator getXPathNavigatorView( IWorkbenchPage page)
    {
        if( page==null)
            return null;
        
        IViewReference[] viewReferences = page.getViewReferences();
        
        for (int i = 0; i < viewReferences.length; i++)
        {
            IViewReference viewReference = viewReferences[i];
            if( viewReference.getView( false) instanceof XPathNavigator)
                return (XPathNavigator)viewReference.getView( false);
        }
        
        return null;
    }
    
    protected void updateXPathNavigatorView( IWorkbenchPartReference partRef)
    {
        XPathNavigator navigatorView = getXPathNavigatorView( partRef.getPage().getWorkbenchWindow().getActivePage());
        if( navigatorView!=null)
        {
            navigatorView.update();
        }
    }
    
    /* IPartListener1 */
    public void partActivated(IWorkbenchPartReference partRef)
    {
        updateXPathNavigatorView( partRef);
    }
    
    public void partBroughtToTop(IWorkbenchPartReference partRef)
    {
        updateXPathNavigatorView( partRef);
    }
    
    public void partClosed(IWorkbenchPartReference partRef)
    {
        updateXPathNavigatorView( partRef);
    }
    
    public void partDeactivated(IWorkbenchPartReference partRef)
    {
        updateXPathNavigatorView( partRef);
    }
    
    public void partHidden(IWorkbenchPartReference partRef)
    {
        updateXPathNavigatorView( partRef);
    }
    
    public void partInputChanged(IWorkbenchPartReference partRef)
    {
        updateXPathNavigatorView( partRef);
    }
    
    public void partOpened(IWorkbenchPartReference partRef)
    {
        updateXPathNavigatorView( partRef);
    }
    
    public void partVisible(IWorkbenchPartReference partRef)
    {
        updateXPathNavigatorView( partRef);
    }
    /* -- */    
}
