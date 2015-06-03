package com.webdav.util;

import io.milton.http.Auth;
import io.milton.http.Request;
import io.milton.http.Request.Method;
import io.milton.http.SecurityManager;
import io.milton.http.http11.auth.DigestResponse;
import io.milton.resource.Resource;

public class MySecurityManager implements SecurityManager {
    String realm = "milton";
    
    public Object authenticate(String user, String password) {
        return user;
    }

    public Object authenticate(DigestResponse digestRequest) {
        return "M";
    }

    public boolean authorise(Request request, Method method, Auth auth, Resource resource) {
        return true;
    }

    public String getRealm(String host) {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

	public boolean isDigestAllowed() {
		return true;
	}
}