package org.eclipse.wst.xml.xpath2.processor.internal.types.builtin;

import org.eclipse.wst.xml.xpath2.api.ResultSequence;
import org.eclipse.wst.xml.xpath2.api.typesystem.TypeDefinition;

public interface AtomicTypeDefinition extends TypeDefinition {

	public abstract ResultSequence construct(ResultSequence rs);

}