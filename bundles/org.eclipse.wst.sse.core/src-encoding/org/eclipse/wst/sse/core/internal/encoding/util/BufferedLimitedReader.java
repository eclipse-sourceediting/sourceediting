package org.eclipse.wst.sse.core.internal.encoding.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * This is a pretty limited implementation, sort of specific 
 * to the way its used by tokenizers (JFlex). To really 
 * be general purpose, would need more work. 
 *
 */


public class BufferedLimitedReader extends BufferedReader {
	private int limitedCount;
	private int nRead;

	public BufferedLimitedReader(Reader reader, int size) {
		super(reader, size);
		if (reader.markSupported()) {
			try {
				mark(size);
			}
			catch (IOException e) {
				// impossible
				e.printStackTrace();
			}
		}
		limitedCount = size;
	}

	public int read() throws IOException {
		int result = 0;
		nRead++;
		if (nRead > limitedCount) {
			result = -1;
		}
		else {
			result = super.read();
		}
		return result;

	}

	public int read(char cbuf[], int off, int len) throws IOException {
		int result = 0;
		if (nRead + len > limitedCount) {
			result = -1;
		}
		else {
			result = super.read(cbuf, off, len);
			nRead = nRead + result;
		}
		return result;
	}
	
}
