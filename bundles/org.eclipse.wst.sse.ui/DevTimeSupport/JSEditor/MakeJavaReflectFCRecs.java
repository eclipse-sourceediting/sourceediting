/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
import java.util.Hashtable;
import java.io.FileWriter;

class MakeJavaReflectFCRecs {
    static Hashtable htClassNames = new Hashtable();

    private static String getShortClassName(Class cls) {
        String retval = cls.toString();
        if (cls.isArray()) retval = cls.getComponentType().toString();
        int i = retval.lastIndexOf('.');
        if (i>0) retval = retval.substring(i+1);
        if (cls.isArray()) retval += "[]";
        return retval;
    }

    static void doClass( Class cls ) throws java.io.IOException {
        String strClassName = cls.getName();
        if (htClassNames.get(strClassName)!=null) return;
        htClassNames.put(strClassName,"8");

        FileWriter fw = new FileWriter( "fc/javaclassreflect."+strClassName+".fcrec" );
        java.lang.reflect.Field fields[] = cls.getFields();
        for (int i=0; i<fields.length; i++) {
            java.lang.reflect.Field field = fields[i];
            int mods = field.getModifiers();
            if (java.lang.reflect.Modifier.isPublic(mods)) {
                fw.write( "#/------------------------------------------\n" );
                fw.write( "#!"+field.getName()+"\n" );
                fw.write( "#@type=field\n" );
                fw.write( "#@wasjspsupport=y\n" );
                String dt = field.getName()+" ";
                Class fcls = field.getType();
                dt += getShortClassName(fcls); 
                // todo: we could designate if it's static
                fw.write( "#@displaytext="+dt+"\n");
                fw.write( "#@fc=javaclassreflect."+field.getType().getName()+"\n" );
                doClass(field.getType());
            }
        }
        java.lang.reflect.Method methods[] = cls.getMethods();
        for (int i=0; i<methods.length; i++) {
            java.lang.reflect.Method method = methods[i];
            int mods = method.getModifiers();
            if (java.lang.reflect.Modifier.isPublic(mods)) {
                fw.write( "#/------------------------------------------\n" );
                fw.write( "#!"+method.getName()+"()\n" );
                fw.write( "#@type=method\n" );
                fw.write( "#@wasjspsupport=y\n");
                String dt = method.getName()+"(";
                Class pclasses[] = method.getParameterTypes();
                if (pclasses.length>0) {
                    for (int j=0; j<pclasses.length;j++) {
                        dt += getShortClassName(pclasses[j])+",";
                    }
                    dt = dt.substring(0,dt.length()-1);
                }
                dt += ") ";
                // todo: we could designate if it's static
                Class fcls = method.getReturnType();
                dt += getShortClassName(fcls); 
                fw.write( "#@displaytext="+dt+"\n" );
                // todo: bug? "getDeclaringClass" ?
                fw.write( "#@fc=javaclassreflect."+method.getReturnType().getName()+"\n");
                doClass(method.getReturnType());
            }
        }
        if (cls.isInterface()) {
            Class interfaces[] = cls.getInterfaces();
            for (int i=interfaces.length; i>0;) {
                Class iface = interfaces[--i];
                fw.write( "#/------------------------------------------\n" );
                fw.write( "#!#isa\n" );
                fw.write( "#@wasjspsupport=y\n" );
                fw.write( "#@fc=javaclassreflect."+iface.getName()+"\n" );
                doClass(iface);
            }
        } else {
            if (cls.isArray()) {
                fw.write( "#/------------------------------------------\n" );
                fw.write( "#!length\n" );
                fw.write( "#@type=field\n" );
                fw.write( "#@wasjspsupport=y\n" );
                fw.write( "#@displaytext=length int\n");
                fw.write( "#@fc=javaclassreflect.int\n" );
                fw.write( "#/------------------------------------------\n" );
                fw.write( "#!/number/\n" );
                fw.write( "#@type=arrayindex\n" );
                fw.write( "#@wasjspsupport=y\n" );
                //fw.write( "#@displaytext=length int\n");
                fw.write( "#@fc=javaclassreflect."+cls.getComponentType().getName()+"\n" );
                doClass(cls.getComponentType());

            } else {
                Class clsUp = cls.getSuperclass();
                if (clsUp!=null) {
                    fw.write( "#/------------------------------------------\n" );
                    fw.write( "#!#isa\n" );
                    fw.write( "#@wasjspsupport=y\n" );
                    fw.write( "#@fc=javaclassreflect."+clsUp.getName()+"\n" );
                    doClass(clsUp);
                }
            }
        }   
        // todo: check exceptions
        fw.close();
        
    }
    public static void main (String args[]) {
        try {
            doClass( javax.servlet.http.HttpServletRequest.class );
            doClass( javax.servlet.http.HttpServletResponse.class );
            doClass( javax.servlet.jsp.JspWriter.class );
            doClass( javax.servlet.http.HttpServletRequest.class );
            doClass( java.lang.Throwable.class );
            doClass( javax.servlet.jsp.PageContext.class );
            doClass( javax.servlet.ServletConfig.class );
            doClass( javax.servlet.ServletContext.class );
            doClass( javax.servlet.http.HttpSession.class );
        } catch (java.io.IOException exc) {
            System.out.println( "exception encountered" );
            exc.printStackTrace();
        }
    }
}
