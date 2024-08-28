package com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Layout;
import org.apache.log4j.WriterAppender;

public class TestCaptureAppender extends WriterAppender {

	private final StringBuilder sb = new StringBuilder();

	public TestCaptureAppender(Layout layout) {
		setLayout(layout);
		activateOptions();
	}

	public void activateOptions() {
		setWriter(createWriter(new CaptureStream()));
		super.activateOptions();
	}

	public String getCapturedOutput() {
		return sb.toString();
	}

	public void reset() {
		sb.setLength(0);
	}

	private class CaptureStream extends OutputStream {

		public CaptureStream() {}

		public void close() {}

		public void flush() {}

		public void write(final byte[] b) throws IOException {
			sb.append(new String(b));
		}

		public void write(final byte[] b, final int off, final int len) throws IOException {
			sb.append(new String(b, off, len));
		}

		public void write(final int b) throws IOException {
			sb.append(b);
		}
	}

}