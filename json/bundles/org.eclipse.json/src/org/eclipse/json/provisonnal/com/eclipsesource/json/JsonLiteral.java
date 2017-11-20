/**
 *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.json.provisonnal.com.eclipsesource.json;

import java.io.IOException;


@SuppressWarnings( "serial" ) // use default serial UID
class JsonLiteral extends JsonValue {

  static final JsonValue NULL = new JsonLiteral( "null" );
  static final JsonValue TRUE = new JsonLiteral( "true" );
  static final JsonValue FALSE = new JsonLiteral( "false" );

  private final String value;
  private final boolean isNull;
  private final boolean isTrue;
  private final boolean isFalse;

  private JsonLiteral( String value  ) {
    this.value = value;
    isNull = "null".equals( value );
    isTrue = "true".equals( value );
    isFalse = "false".equals( value );
  }

  @Override
  void write( JsonWriter writer ) throws IOException {
    writer.writeLiteral( value );
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public boolean isNull() {
    return isNull;
  }

  @Override
  public boolean isTrue() {
    return isTrue;
  }

  @Override
  public boolean isFalse() {
    return isFalse;
  }

  @Override
  public boolean isBoolean() {
    return isTrue || isFalse;
  }

  @Override
  public boolean asBoolean() {
    return isNull ? super.asBoolean() : isTrue;
  }

  @Override
  public boolean equals( Object object ) {
    if( this == object ) {
      return true;
    }
    if( object == null ) {
      return false;
    }
    if( getClass() != object.getClass() ) {
      return false;
    }
    JsonLiteral other = (JsonLiteral)object;
    return value.equals( other.value );
  }

}
