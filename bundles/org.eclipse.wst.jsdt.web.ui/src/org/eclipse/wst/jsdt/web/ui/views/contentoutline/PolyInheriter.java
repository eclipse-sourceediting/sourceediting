package org.eclipse.wst.jsdt.web.ui.views.contentoutline;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Vector;

import org.eclipse.jface.text.Position;

public class PolyInheriter implements InvocationHandler{
    
    private IPolyParents fChild;

    private PolyInheriter(IPolyParents child){
        this.fChild = child;
    }
 
    public static Object getInstance(IPolyParents child){
        /* given a list of already instantiated objects, I'll comebine them
         * into one catable (ie cast to any of their implement interfaces) object
         * and dispatch method invocations accordingly.  Array order will ultimatly
         * determine method 'priority'.
         */
        PolyInheriter me = new PolyInheriter(child);
        Object proxy = null;
        Object[] superClasses = child.getSuperClasses();
        Vector interfaces = new Vector();
        /* add the parent classes methods/interfaces first so they will over ride
         * any '
         */
        interfaces.addAll(Arrays.asList(child.getClass().getInterfaces()));
        for(int i = 0;i< superClasses.length;i++){
            interfaces.addAll(Arrays.asList(superClasses[i].getClass().getInterfaces()));
        }
       
        
         try {
             proxy=  Proxy.newProxyInstance(
                      child.getClass().getClassLoader(),
                     (Class[])interfaces.toArray(new Class[]{}),me);
             
         } catch (Exception e) {
             // TODO Auto-generated catch block
             System.out.println(e);
         }
        return proxy;
    }
    
  
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      
        Object result;
        
        Method[] overRiddenMethods  =  fChild.getClass().getMethods();
        Object[] superClasses       =  fChild.getSuperClasses();
        
        try {
            for(int i = 0;i<overRiddenMethods.length;i++){
                String methodName       = overRiddenMethods[i].getName();
                Class methodReturn      = overRiddenMethods[i].getReturnType();
                Object[] methodParams   = overRiddenMethods[i].getParameterTypes();
                Object[] exceptionTypes = overRiddenMethods[i].getExceptionTypes();
                

                if(method.getName().equals(overRiddenMethods[i].getName()) &&
                   method.getReturnType() == overRiddenMethods[i].getReturnType() && 
                   method.getParameterTypes() == overRiddenMethods[i].getParameterTypes()){
                   return overRiddenMethods[i].invoke(overRiddenMethods[i], args);
                }
            }
            result = method.invoke(parentType, args);
            } catch (InvocationTargetException e) {
            throw e.getTargetException();
            } catch (Exception e) {
            throw new RuntimeException("unexpected invocation exception: " +
                           e.getMessage());
        } 

        return result;
    }
    
}
