package com.ericsson.oss.mediation.modeling.yangtools.transformer2;

@SuppressWarnings("serial")
public class YangTransformer2Exception extends RuntimeException {

	public YangTransformer2Exception(final String msg) {
		super(msg);
	}

	public YangTransformer2Exception(final String msg, final Exception ex) {
		super(msg, ex);
	}

}
