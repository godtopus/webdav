package com.webdav.controllers;

import io.milton.http.Filter;
import io.milton.http.FilterChain;
import io.milton.http.Request;
import io.milton.http.Response;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webdav.domain.SessionManager;

/**
 *
 * @author brad
 */
public class MiltonOpenSessionInViewFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(MiltonOpenSessionInViewFilter.class);
    private final SessionManager sessionManager;

    public MiltonOpenSessionInViewFilter(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void process(final FilterChain chain, final Request request, final Response response) {
        long tm = System.currentTimeMillis();
        try {
            Session s = sessionManager.open();
            chain.process(request, response);
        } finally {
            sessionManager.close();
        }
        tm = System.currentTimeMillis() - tm;
        log.info("Finished request: " + tm + "ms  for " + request.getAbsolutePath() + " method=" + request.getMethod());
    }
}
