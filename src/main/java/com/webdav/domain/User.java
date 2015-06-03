package com.webdav.domain;

import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.webdav.domain.DbUtils;
import com.webdav.util.PasswordEncryption;

/**
 * The {@code User} class defines the base contract for
 * the in-memory {@code User} objects
 * 
 * @author	M
 * @version	1.0
 * @since
 */

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User {
	private String id;
	private String username;
	private String hashedPassword;
	//private FolderResource homeDirectory;
    
    @Id
	@Column(nullable=false, unique=true, updatable=false)
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	@Column(nullable=false, unique=true)
	public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
	
    @Column(nullable=false)
	public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String password) {
        this.hashedPassword = password;
    }
    
    /*@MapsId("id")
	@OneToOne(optional=true)*/
    public static FolderResource getHomeDirectory(User user, Session session) {
    	return FolderResource.find(user.getId(), session);
    }
    
    /*public void setHomeDirectory(FolderResource homeDirectory) {
    	this.homeDirectory = homeDirectory;
    }*/
    
    /**
     * Finds all {@code User} objects stored in a database
     * 
     * @param session	The current Spring configured session
     * @return			A {@code List} of all {@code User} objects
     */
	public static List<User> findAll(Session session) {
		Criteria crit = session.createCriteria(User.class);
		crit.addOrder(Order.asc("username"));
		return DbUtils.toList(crit, User.class);
	}
	
	/**
	 * Finds a specific {@code User} identified by a username
	 * 
	 * @param username	The username to search for
	 * @param session	The current Spring configured session
	 * @return			A {@code User} if found, otherwise {@code null}
	 */
	public static User find(String username, Session session) {
		Criteria crit = session.createCriteria(User.class);
		crit.add(Restrictions.eq("username", username));
		return DbUtils.unique(crit);
    }
	
	/**
	 * Creates a new {@code User} with the specified username and password
	 * 
	 * @param username	A unique username
	 * @param password	A non-empty and not-null password
	 * @return			The created {@code User}
	 */
    public static User create(String username, String password) {
    	User newUser = new User();
    	newUser.setId(UUID.randomUUID().toString());
    	newUser.setUsername(username);
    	newUser.setHashedPassword(PasswordEncryption.getSaltedHash(password));
    	
    	return newUser;
    }
}