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

import java.io.Writer;


/**
 * Controls the formatting of the JSON output. Use one of the available constants.
 */
public abstract class WriterConfig {

  /**
   * Write JSON in its minimal form, without any additional whitespace. This is the default.
   */
  public static WriterConfig MINIMAL = new WriterConfig() {
    @Override
    JsonWriter createWriter( Writer writer ) {
      return new JsonWriter( writer );
    }
  };

  /**
   * Write JSON in pretty-print, with each value on a separate line and an indentation of two
   * spaces.
   */
  public static WriterConfig PRETTY_PRINT = new WriterConfig() {
    @Override
    JsonWriter createWriter( Writer writer ) {
      return new PrettyPrinter( writer );
    }
  };

  abstract JsonWriter createWriter( Writer writer );

}
