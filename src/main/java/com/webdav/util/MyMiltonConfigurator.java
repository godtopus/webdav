package com.webdav.util;

import io.milton.http.fs.NullSecurityManager;
import io.milton.servlet.DefaultMiltonConfigurator;

public class MyMiltonConfigurator extends DefaultMiltonConfigurator {
	private NullSecurityManager securityManager;
	
	/**
	 * Instantiates a new My milton configurator.
	 */
	public MyMiltonConfigurator() {
		this.securityManager = new NullSecurityManager();
	}
	
	@Override
	protected void build() {
		builder.setSecurityManager(securityManager);
		super.build();
    }
}