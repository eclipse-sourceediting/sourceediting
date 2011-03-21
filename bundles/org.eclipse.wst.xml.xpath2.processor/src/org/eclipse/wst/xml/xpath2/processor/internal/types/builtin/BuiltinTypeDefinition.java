package org.eclipse.wst.xml.xpath2.processor.internal.types.builtin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.wst.xml.xpath2.api.ResultSequence;
import org.eclipse.wst.xml.xpath2.api.typesystem.TypeDefinition;
import org.eclipse.wst.xml.xpath2.processor.internal.XPathError;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

public class BuiltinTypeDefinition implements AtomicTypeDefinition  {
	
	public final static String XS_NS = "http://www.w3.org/2001/XMLSchema"; 
	
	private final QName name;
	private final Class implementationClass;
	private final Class nativeType;
	private final BuiltinTypeDefinition baseType;
	private final Method constructorMethod;

	public BuiltinTypeDefinition(QName name, BuiltinTypeDefinition baseType) {
		this(name, null, null, baseType);
	}

	public BuiltinTypeDefinition(String name, BuiltinTypeDefinition baseType) {
		this(name, null, null, baseType);
	}

	public BuiltinTypeDefinition(QName name, Class implementationClass, Class nativeType, BuiltinTypeDefinition baseType) {
		this.name = name;
		this.implementationClass = implementationClass;
		this.nativeType = nativeType;
		this.baseType = baseType;
		try {
			this.constructorMethod = implementationClass != null ? implementationClass.getMethod("constructor", ResultSequence.class) : null;
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean isAbstract() {
		return implementationClass == null;
	}
	
	public BuiltinTypeDefinition(String name, Class implementationClass, Class nativeType, BuiltinTypeDefinition baseType) {
		this(new QName(XS_NS, name), implementationClass, nativeType, baseType);
	}
	
	public String getNamespace() {
		return name.getNamespaceURI();
	}

	public String getName() {
		return name.getLocalPart();
	}

	public TypeDefinition getBaseType() {
		return baseType;
	}

	public boolean derivedFromType(TypeDefinition ancestorType, short derivationMethod) {
		if (ancestorType == this) return true;
		if (baseType == null) return false;
		return baseType.derivedFromType(ancestorType, derivationMethod);
	}

	public boolean derivedFrom(String namespace, String name, short derivationMethod) {
		if (namespace.equals(getNamespace()) && name.equals(getName())) return true;
		
		if (baseType == null) return false;
		
		return baseType.derivedFrom(namespace, name, derivationMethod);
	}

	public List getSimpleTypes(Attr attr) {
		return Collections.emptyList();
	}

	public List getSimpleTypes(Element attr) {
		return Collections.emptyList();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.xml.xpath2.processor.internal.types.builtin.AtomicTypeDefinition#construct(org.eclipse.wst.xml.xpath2.api.ResultSequence)
	 */
	public ResultSequence construct(ResultSequence rs) {
		try {
			if (implementationClass == null) throw new XPathError("Type " + getName() + " is abstract!");
			return (ResultSequence)constructorMethod.invoke(null, new Object[] { rs });
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Class getNativeType() {
		return nativeType;
	}

}
