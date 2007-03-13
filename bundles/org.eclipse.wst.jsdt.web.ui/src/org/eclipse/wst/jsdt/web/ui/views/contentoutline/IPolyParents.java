package org.eclipse.wst.jsdt.web.ui.views.contentoutline;

import java.lang.reflect.*;

public abstract class IPolyParents {
    /*
     * Just return your already instantiated "super" classes in the
     * getSuperClasses() method, and a list of methods
     * the implementing class will over ride.
     * 
     */
    
    /* ORDERED array of all super classes this object would 
     * like to be cast to.  Its important to note that if a class
     * lower in the ordered list needs to override a method from one
     * of the higher ordered parents then this will have to be
     * done manualy within the class, and methods
     * over ridden by the return in overRides().
     */
    public abstract Object[] getSuperClasses();
   
    
}
