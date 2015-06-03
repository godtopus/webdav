package com.webdav.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hibernate.Transaction;

import com.webdav.domain.AbstractResource;
import com.webdav.domain.FolderResource;
import com.webdav.domain.SessionManager;
import com.webdav.domain.User;
import com.webdav.util.PasswordEncryption;

import io.milton.annotations.AccessControlList;
import io.milton.annotations.Authenticate;
import io.milton.annotations.ChildOf;
import io.milton.annotations.ChildrenOf;
import io.milton.annotations.Delete;
import io.milton.annotations.DisplayName;
import io.milton.annotations.MakeCollection;
import io.milton.annotations.Name;
import io.milton.annotations.Post;
import io.milton.annotations.Principal;
import io.milton.annotations.ResourceController;
import io.milton.annotations.Users;
import io.milton.common.JsonResult;
import io.milton.common.ModelAndView;
import io.milton.http.http11.auth.DigestResponse;
import io.milton.resource.AccessControlledResource;

/**
 * This is the controller for the {@code User} and runs below the configured
 * webapp context path. It uses the Milton Annotations-framework,
 * to represent a controller,
 * see <a href="http://milton.io/guide/02-implementation/01-annotations/index.html">http://milton.io/guide/02-implementation/01-annotations/index.html</a>.
 * Milton is open source, see source code at <a href="https://github.com/miltonio/milton2">https://github.com/miltonio/milton2</a>
 * 
 * @author	M
 * @version	1.0
 * @since
 */

@ResourceController
public class UserController {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(UserController.class);
	
	@Name
	public String getUserRootName(UserController userRoot) {
		return "users";
	}
	
	@Name
	public String getUserName(User user) {
		return user.getUsername();
	}
	
	@ChildrenOf
	public UserController getUserRoot(RootController root) {
		return this;
	}
	
	@ChildrenOf
	//@Users
	public List<User> getUsers(UserController root) {
		List<User> users = User.findAll(SessionManager.session());
		return users;
	}
	
	@ChildOf
	//@Users
	public User findUserByName(UserController root, String name) {
		return User.find(name, SessionManager.session());
	}
	
	/**
	 * Handles creating new users and stores them to a database
	 * 
	 * @param root		The {@code User} root context
	 * @param username	The username to assign. Must be unique and not null
	 * @param password	The password to assign. Is sent in cleartext over SSL
	 * 					and encrypted server side
	 * @return			The newly created {@code User}, or {@code null}
	 */
	@MakeCollection
    public User createAndSaveUser(UserController root, String username, String password) {
		Transaction tx = SessionManager.session().beginTransaction();
		User newUser = User.create(username, password);
    	FolderResource ROOT = FolderResource.find("ROOT", SessionManager.session());
    	FolderResource home = FolderResource.create(ROOT, newUser.getId());
		
		try {
			SessionManager.session().save(newUser);
			SessionManager.session().save(home);
			SessionManager.session().update(home.getParent());
			
			home.setOwner(newUser);
			SessionManager.session().update(home);
			SessionManager.session().flush();
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
		}
		
    	Path path = Paths.get(RootController.getWebdavRoot(), home.resolveFullPath());
    	
    	try {
			Files.createDirectory(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        return newUser;
    }
	
    /**
     * Deletes a {@code User} from the database
     * 
     * @param user	The {@code User} to delete
     */
	@Delete
	public void deleteUser(User user) {
		Transaction tx = SessionManager.session().beginTransaction();
		
		try {
			SessionManager.session().delete(user);
			SessionManager.session().flush();
			tx.commit();
		
		} catch (Exception e) {
			tx.rollback();
		}
    }
	
	/**
	 * Handles the sign in process of a {@code User}. The data must be sent
	 * from the client with AJAX, and the parameters must match
	 * 
	 * @param root		The {@code User} root context
	 * @param params	The client-supplied parameters
	 * @return			The URL to a user page template and the address to the
	 * 					{@code User} home directory, or {@code null} if not able
	 * 					to authenticate the {@code User}
	 */
	@Post(bindData=true, params={"sign_in", "username", "password"})
    public JsonResult signIn(UserController userRoot, Map<String, String> params) {
    	if (validateUser(params).compareTo(Boolean.FALSE) == 0) {
    		return null;
    	}
    	
    	User user = findUserByName(this, params.get("username"));
    	return JsonResult.returnData("https://localhost:8443/templates/userPage.xhtml#home=" + User.getHomeDirectory(user, SessionManager.session()).getName());
    }
	
	/**
	 * Handles the sign up process of a {@code User}. The data must be sent
	 * from the client with AJAX, and the parameters must match
	 * 
	 * @param root		The {@code User} root context
	 * @param params	The client-supplied parameters
	 * @return			The URL to a user page template and the address to the
	 * 					{@code User} home directory, or {@code null} if not able
	 * 					to create the new {@code User}
	 */
    @Post(bindData=true, params={"sign_up", "username", "password"})
    public JsonResult signUp(UserController userRoot, Map<String, String> params) {
    	User newUser = createAndSaveUser(this, params.get("username"), params.get("password"));
    	
    	if (newUser == null) {
    		return null;
    	}
    	
    	return JsonResult.returnData("https://localhost:8443/templates/userPage.xhtml#home=" + User.getHomeDirectory(newUser, SessionManager.session()).getName());
    }
    
    /**
     * Validates the client-supplied credentials
     * 
     * @param params	Validate that these contain a username and cleartext
     * 					password before calling
     * @return			A {@code Boolean} indicating success or failure to
     * 					authenticate
     */
    private Boolean validateUser(Map<String, String> params) {
    	User user = findUserByName(this, params.get("username"));
    	Boolean isVerified = Boolean.FALSE;
    	
    	if (user != null) {
    		isVerified = verifyUserPassword(user, params.get("password"));
    	}
    	
    	return isVerified;
    }
    
    /**
     * Calculates a hash of the supplied password and compares it to one stored
     * in a database for the {@code User}
     * 
     * @param user				The {@code User} to authenticate
     * @param requestedPassword	The cleartext password
     * @return					A {@code Boolean} indicating success or failure to
     * 							authenticate
     */
	//@Authenticate
	public Boolean verifyUserPassword(User user, String requestedPassword) {
		System.out.println("Authenticating in User...  " + (user == null ? null : user.getUsername()));
		return PasswordEncryption.check(requestedPassword, user.getHashedPassword());
	}
	
    /*@Authenticate
    public Boolean verifyDigestPassword(User user, DigestResponse digest) {
    	System.out.println("Authenticating in User...  digest");
        return false;
    }*/
	
    /*@AccessControlList
    public List<AccessControlledResource.Priviledge> getUserPrivs(UserController target, User currentUser) {
    	System.out.println("ACL UserC: " + currentUser == null);
    	if (currentUser == null) {
    		return AccessControlledResource.NONE;
    	}
    	
    	return AccessControlledResource.READ_BROWSE;
    }
    
    @AccessControlList
    public List<AccessControlledResource.Priviledge> getUserPrivs(User target, User currentUser) {
        System.out.println("ACL: " + target.getName());
        System.out.println("ACL: " + currentUser == null);
        if (target == currentUser) {
            return AccessControlledResource.READ_WRITE;
        } else {
        	System.out.println("none");
            return AccessControlledResource.NONE;
        }
    }*/
}