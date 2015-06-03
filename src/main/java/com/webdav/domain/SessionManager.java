package com.webdav.domain;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author M
 */
public class SessionManager {
    
    private static ThreadLocal<Session> tlSession = new ThreadLocal<Session>(); 
    
    public static Session session() {
        return sessionFactory.getCurrentSession();
    }    
    
    @Autowired
    private static SessionFactory sessionFactory;
    
    
    public SessionManager(SessionFactory sf) {
        sessionFactory = sf;
    }

    public Session open() {
        Session session = sessionFactory.openSession();
        tlSession.set(session);
        return session;
    }
    
    public void close() {
        Session s = session();
        if( s != null ) {
            s.close();
        }
        tlSession.remove();
    }   
}