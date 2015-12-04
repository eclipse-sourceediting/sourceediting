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
import java.io.Writer;


class PrettyPrinter extends JsonWriter {

  private final char[] indentChars = { ' ', ' ' };
  private int indent;

  PrettyPrinter( Writer writer ) {
    super( writer );
  }

  @Override
  protected void writeArrayOpen() throws IOException {
    indent++;
    writer.write( '[' );
    writeNewLine();
  }

  @Override
  protected void writeArrayClose() throws IOException {
    indent--;
    writeNewLine();
    writer.write( ']' );
  }

  @Override
  protected void writeArraySeparator() throws IOException {
    writer.write( ',' );
    writeNewLine();
  }

  @Override
  protected void writeObjectOpen() throws IOException {
    indent++;
    writer.write( '{' );
    writeNewLine();
  }

  @Override
  protected void writeObjectClose() throws IOException {
    indent--;
    writeNewLine();
    writer.write( '}' );
  }

  @Override
  protected void writeMemberSeparator() throws IOException {
    writer.write( ':' );
    writer.write( ' ' );
  }

  @Override
  protected void writeObjectSeparator() throws IOException {
    writer.write( ',' );
    writeNewLine();
  }

  private void writeNewLine() throws IOException {
    writer.write( '\n' );
    for( int i = 0; i < indent; i++ ) {
      writer.write( indentChars );
    }
  }

}
