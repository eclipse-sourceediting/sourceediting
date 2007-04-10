/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.contentassist;

import java.beans.Introspector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import com.ibm.icu.util.StringTokenizer;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jst.jsp.ui.internal.Logger;

/**
 * Navigates the IJavaProject classpath (incl. source) on a given resource and infers bean properties
 * given a fully qualified beanname. Bean properties can be retrieved using:
 * <code>getRuntimeProperties(IResource baseResource, String typeName)</code>
 * 
 * @plannedfor 1.0
 */
public class BeanInfoProvider implements IBeanInfoProvider {

	public class JavaPropertyDescriptor implements IJavaPropertyDescriptor {
		String fType = null;
		String fName = null;
		boolean fReadable = true;
		boolean fWritable = true;

		public JavaPropertyDescriptor(String name, String type, boolean readable, boolean writable) {
			fName = name;
			fType = type;
			fReadable = readable;
			fWritable = writable;
		}

		public String getDeclaredType() {
			return fType;
		}

		public String getDisplayName() {
			return fName;
		}

		public String getName() {
			return fName;
		}

		public boolean getReadable() {
			return fReadable;
		}

		public boolean getWriteable() {
			return fWritable;
		}
	}

	// looks up encoded type (see Class.getName), and gives you a displayable string
	private HashMap fEncodedTypeMap = null;
	// to avoid repeat properties from showing up
	private HashSet fRepeatMethods = null;

	public BeanInfoProvider() {
		fRepeatMethods = new HashSet();
	}

	/**
	 * Returns the inferred properties of a bean based on the project from the baseResource,
	 * and the fully qualified name of the bean.
	 * 
	 * @param baseResource the base resource where the bean is being used
	 * @param typeName the <i>fully qualified</i> type name (eg. javax.swing.JButton) of the bean
	 */
	public IJavaPropertyDescriptor[] getRuntimeProperties(IResource baseResource, String typeName) {
		IJavaProject javaProject = JavaCore.create(baseResource.getProject());
		QualifiedName typeQualifiedName = getTypeQualifiedName(typeName);
		List getMethodResults = new ArrayList();
		List isMethodResults = new ArrayList();
		List setMethodResults = new ArrayList();
		List descriptorResults = new ArrayList();
		try {
			IType type = javaProject.findType(typeQualifiedName.getQualifier() + "." + typeQualifiedName.getLocalName()); //$NON-NLS-1$
			// type must exist
			if(type != null) {
				ITypeHierarchy hierarchy = type.newTypeHierarchy(null);
				IType[] supers = hierarchy.getAllSuperclasses(type);
	
				IMethod[] methods = type.getMethods();
				// iterate the bean's methods
				for (int i = 0; i < methods.length; i++)
					acceptMethod(getMethodResults, isMethodResults, setMethodResults, methods[i]);
				// the bean hierarchy's methods
				for (int i = 0; i < supers.length; i++) {
					methods = supers[i].getMethods();
					for (int j = 0; j < methods.length; j++)
						acceptMethod(getMethodResults, isMethodResults, setMethodResults, methods[j]);
				}
				adaptMethodsToPropertyDescriptors(getMethodResults, isMethodResults, setMethodResults, descriptorResults);
			}
		}
		catch (JavaModelException jmex) {
			Logger.logException("Problem navigating JavaProject in BeanInfoProvider", jmex); //$NON-NLS-1$
		}

		IJavaPropertyDescriptor[] finalResults = new IJavaPropertyDescriptor[descriptorResults.size()];
		System.arraycopy(descriptorResults.toArray(), 0, finalResults, 0, descriptorResults.size());
		return finalResults;
	}

	/**
	 * Retrieves the necessary information from method declaration lists, creates and fills a list of JavaPropertyDescriptors.
	 * @param getMethods
	 * @param isMethods
	 * @param setMethods
	 * @param descriptorResults
	 */
	private void adaptMethodsToPropertyDescriptors(List getMethods, List isMethods, List setMethods, List descriptors) throws JavaModelException {
		List readable = new ArrayList();
		HashMap types = new HashMap();

		// iterate through get* and is* methods, updating 'readable' list and 'types' map
		filterGetMethods(getMethods, readable, types);
		filterIsMethods(isMethods, readable, types);

		// iterate set* methods, checking overlap w/ readable
		Iterator it = setMethods.iterator();
		IMethod temp = null;
		String name = ""; //$NON-NLS-1$
		String type = ""; //$NON-NLS-1$
		String[] encodedParams = null;
		String returnType = ""; //$NON-NLS-1$
		String param0 = ""; //$NON-NLS-1$

		while (it.hasNext()) {
			temp = (IMethod) it.next();
			name = createPropertyNameFromMethod(temp);
			// invalid naming convention
			if (name == null)
				continue;

			returnType = getDecodedTypeName(temp.getReturnType());
			// setter should have no return type
			if (!returnType.equals("void")) //$NON-NLS-1$
				continue;

			// need to get type from parameter
			encodedParams = temp.getParameterTypes();
			if (encodedParams != null && encodedParams.length > 0) {
				if (encodedParams.length > 1) {
					// multiple params
					param0 = getDecodedTypeName(encodedParams[0]);
					if (!param0.equals("int")) //$NON-NLS-1$
						// not a valid indexed property
						continue;
					
					type = getDecodedTypeName(encodedParams[1]);
				}
				else {
					// one param, regular setter
					if (isArray(encodedParams[0]))
						type = getDecodedTypeName(encodedParams[0]);
				}
			}

			if (readable.contains(name)) {
				// writable and readable
				if (!fRepeatMethods.contains(name)) {
					descriptors.add(new JavaPropertyDescriptor(name, (String) types.get(name), true, true));
					readable.remove(name);
					fRepeatMethods.add(name);
				}
			}
			else {
				// wasn't readable, just writable
				String[] params = temp.getParameterTypes();
				// can't be setProperty if no parameters
				if (!(params.length > 0))
					continue;
				if (!fRepeatMethods.contains(name)) {
					type = getDecodedTypeName(params[0]);
					descriptors.add(new JavaPropertyDescriptor(name, type, false, true));
					fRepeatMethods.add(name);
				}
			}
		}
		// add leftover from readable, get* and is* methods (readable = true, writable = false)
		it = readable.iterator();
		while (it.hasNext()) {
			name = (String) it.next();
			if (!fRepeatMethods.contains(name)) {
				descriptors.add(new JavaPropertyDescriptor(name, (String) types.get(name), true, false));
				fRepeatMethods.add(name);
			}
		}
	}

	private void filterGetMethods(List getMethods, List readable, HashMap types) throws JavaModelException {
		IMethod temp;
		String name;
		String encodedReturnType;
		String returnType;
		Iterator it = getMethods.iterator();
		String[] encodedParams;
		String paramType;
		// iterate get* methods
		while (it.hasNext()) {
			temp = (IMethod) it.next();
			name = createPropertyNameFromMethod(temp);
			// invalid bean naming convention
			if (name == null)
				continue;

			encodedReturnType = temp.getReturnType();
			returnType = getDecodedTypeName(encodedReturnType);

			//  can't get be a getProperty if returns void
			if (returnType.equals("void")) //$NON-NLS-1$
				continue;

			// check params in case it's indexed propety
			encodedParams = temp.getParameterTypes();
			if (encodedParams != null && encodedParams.length == 1) {
				paramType = getDecodedTypeName(encodedParams[0]);
				// syntax is > Type getter(int);
				if (!paramType.equals("int")) { //$NON-NLS-1$
					//it's not an indexed property
					continue;
				}
				// it is indexed, prop type is an ARRAY
				returnType += "[]"; //$NON-NLS-1$
			}

			readable.add(name);
			types.put(name, returnType);
		}

	}

	private void filterIsMethods(List isMethodResults, List readable, HashMap types) throws JavaModelException {
		IMethod temp;
		String name;
		String encodedReturnType;
		String returnType;
		String[] encodedParams;
		String paramType;
		// iterate is* methods
		Iterator it = isMethodResults.iterator();
		while (it.hasNext()) {
			temp = (IMethod) it.next();
			name = createPropertyNameFromMethod(temp);
			// invalid bean naming convention
			if (name == null)
				continue;
			encodedReturnType = temp.getReturnType();
			returnType = getDecodedTypeName(encodedReturnType);

			// isProperty only valid for boolean
			if (!returnType.equals("boolean")) //$NON-NLS-1$
				continue;

			// check params in case it's indexed propety
			encodedParams = temp.getParameterTypes();
			if (encodedParams != null && encodedParams.length == 1) {
				paramType = getDecodedTypeName(encodedParams[0]);
				// syntax is > Type getter(int);
				if (!paramType.equals("int")) { //$NON-NLS-1$
					//it's not a valid indexed property
					continue;
				}
			}

			readable.add(name);
			types.put(name, returnType);
		}
	}

	/**
	 * Pass in a get*|set*|is* method and it will return an inferred property name using <code>Introspector.decapitalize(String)</code>
	 * @param temp
	 * @return an inferred property name based on the IMethod name, null if the name is not valid according to bean spec
	 */
	private String createPropertyNameFromMethod(IMethod temp) {
		String name = temp.getElementName();
		if (name.startsWith("is")) //$NON-NLS-1$
			name = Introspector.decapitalize(name.substring(2));
		else
			// must be get or set
			name = Introspector.decapitalize(name.substring(3));
		return name;
	}

	/**
	 * Initial filtering of methods.  Checks prefix if it's valid length.  If the prefix is "get" the  method name 
	 * is placed in the getMethodResults List.  If the prefix is "is", the name is added to the isMethodResults list.  If the
	 * prefix is "set", it's added to the setMethodResultsList.
	 * 
	 * @param getMethodResults
	 * @param isMethodResults
	 * @param setMethodResults
	 * @param method
	 */
	private void acceptMethod(List getMethodResults, List isMethodResults, List setMethodResults, IMethod method) throws JavaModelException {
		if (!fRepeatMethods.contains(method.getElementName())) {
			fRepeatMethods.add(method.getElementName());
			int flags = method.getFlags();
			String methodName = method.getElementName();
			if (Flags.isPublic(flags)) {
				if (methodName.length() > 3 && methodName.startsWith("get")) //$NON-NLS-1$
					getMethodResults.add(method);
				else if (methodName.length() > 2 && methodName.startsWith("is")) //$NON-NLS-1$
					isMethodResults.add(method);
				else if (methodName.length() > 3 && methodName.startsWith("set")) //$NON-NLS-1$
					setMethodResults.add(method);
			}
		}
	}

	/**
	 * @param typeName
	 * @return a Qualified name with the package as the qualifier, and class name as LocalName
	 */
	private QualifiedName getTypeQualifiedName(String typeName) {
		StringTokenizer st = new StringTokenizer(typeName, ".", false); //$NON-NLS-1$
		int length = st.countTokens();
		int count = 0;
		StringBuffer root = new StringBuffer();
		while (count++ < length - 1) {
			root.append(st.nextToken());
			if (count < length - 1)
				root.append('.');
		}
		return new QualifiedName(root.toString(), st.nextToken());
	}

	/**
	 * Checks if encodedTypeName is an array
	 * @param encodedTypeName
	 * @return true if encodedTypeName is an array, false otherwise.
	 */
	private boolean isArray(String encodedTypeName) {
		if (encodedTypeName != null && encodedTypeName.length() > 0) {
			if (encodedTypeName.charAt(0) == '[')
				return true;
		}
		return false;
	}

	/**
	 * Returns the decoded (displayable) name fo the type.
	 * Either a primitive type (int, long, float...) Object (String)
	 * @param type
	 * @return decoded name for the encoded string
	 */
	private String getDecodedTypeName(String encoded) {
		HashMap map = getEncodedTypeMap();

		StringBuffer decoded = new StringBuffer();
		char BRACKET = '[';
		String BRACKETS = "[]"; //$NON-NLS-1$
		char identifier = ' ';
		int last = 0;
		// count brackets
		while (encoded.indexOf(BRACKET, last) != -1) {
			last++;
		}
		identifier = encoded.charAt(last);
		Object primitiveType = map.get(String.valueOf(identifier));
		// L > binary type name, Q > source type name
		if (identifier == 'L' || identifier == 'Q') {
			// handle object
			String classname = encoded.substring(last + 1, encoded.length() - 1);
			decoded.append(classname);
		}
		else if (primitiveType != null) {
			// handle primitive type (from IField.getSignature())
			decoded.append((String) primitiveType);
		}
		else {
			// handle primitive type (from Class.getName())
			decoded.append(encoded);
		}
		// handle arrays
		if (last > 0) {
			for (int i = 0; i < last; i++) {
				decoded.append(BRACKETS);
			}
		}
		return decoded.toString();
	}

	/**
	 *	from Class.getName() javadoc
	 *	also see Signature in jdt.core api
	 *<pre>
	 *			B            byte
	 *			C            char
	 *			D            double
	 *			F            float
	 *			I            int
	 *			J            long
	 *			Lclassname;  class or interface
	 *			Qsourcename; source
	 *			S            short
	 *			Z            boolean
	 *			V	   		 void
	 *</pre>
	 *
	 * @return the "encoding letter" to "type" map.
	 */
	private HashMap getEncodedTypeMap() {
		if (fEncodedTypeMap == null) {
			fEncodedTypeMap = new HashMap();
			fEncodedTypeMap.put("B", "byte"); //$NON-NLS-1$ //$NON-NLS-2$
			fEncodedTypeMap.put("C", "char"); //$NON-NLS-1$ //$NON-NLS-2$
			fEncodedTypeMap.put("D", "double"); //$NON-NLS-1$ //$NON-NLS-2$
			fEncodedTypeMap.put("F", "float"); //$NON-NLS-1$ //$NON-NLS-2$
			fEncodedTypeMap.put("I", "int"); //$NON-NLS-1$ //$NON-NLS-2$
			fEncodedTypeMap.put("J", "long"); //$NON-NLS-1$ //$NON-NLS-2$
			fEncodedTypeMap.put("S", "short"); //$NON-NLS-1$ //$NON-NLS-2$
			fEncodedTypeMap.put("Z", "boolean"); //$NON-NLS-1$ //$NON-NLS-2$
			fEncodedTypeMap.put("V", "void"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return fEncodedTypeMap;
	}
}
