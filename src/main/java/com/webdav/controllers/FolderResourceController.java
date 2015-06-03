package com.webdav.controllers;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import io.milton.annotations.AccessControlList;
import io.milton.annotations.Authenticate;
import io.milton.annotations.ChildrenOf;
import io.milton.annotations.ContentLength;
import io.milton.annotations.ContentType;
import io.milton.annotations.Copy;
import io.milton.annotations.CreatedDate;
import io.milton.annotations.Delete;
import io.milton.annotations.DisplayName;
import io.milton.annotations.DisplayNameSetter;
import io.milton.annotations.Get;
import io.milton.annotations.MakeCollection;
import io.milton.annotations.MaxAge;
import io.milton.annotations.ModifiedDate;
import io.milton.annotations.Move;
import io.milton.annotations.Name;
import io.milton.annotations.Post;
import io.milton.annotations.Principal;
import io.milton.annotations.ResourceController;
import io.milton.annotations.UniqueId;
import io.milton.annotations.Users;
import io.milton.common.JsonResult;
import io.milton.http.http11.auth.DigestResponse;
import io.milton.resource.AccessControlledResource;

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
import com.webdav.domain.FileResource;
import com.webdav.domain.FolderResource;
import com.webdav.domain.SessionManager;
import com.webdav.domain.User;
import com.webdav.util.FileSystemUtil;

/**
 * This is the controller for the {@code FolderResource} and runs below the configured
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
public class FolderResourceController  {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FolderResourceController.class);
    private List<FolderResource> children = new ArrayList<FolderResource>();
    
    @Name
    public String getName(FolderResourceController frc) {
    	return "ROOT";
    }
    
    @Name
    public String getFolderName(FolderResource source) {
    	return source.getName();
    }
    
    @ContentLength
    public Long getContentLength(FolderResource source) {
    	return null;
    }
    
    @ContentType
    public String getContentType(FolderResource source) {
    	return null;
    }
    
    @CreatedDate
    public Date getCreatedDate(FolderResource source) {
    	return source.getCreatedDate();
    }
    
    @ModifiedDate
    public Date getModifedDate(FolderResource source) {
    	return source.getModifiedDate();
    }
    
    @MaxAge
    public Long getMaxAge(FileResource source) {
    	return (long) 0;
    }
    
    @UniqueId
    public String getUniqueId(FolderResource source) {
        return source.getId();
    }
    
    /**
     * Returns the logical {@code FolderResource} child of the RootController.
     * This is required for this to be a collection
     * 
     * @param root	The {@code RootController} that {@code this} is rooted in
     * @return		Returns a {@code FolderResource} that is the root-directory
     */
    @ChildrenOf
    public FolderResource getFolderRoot(RootController root) {
    	return FolderResource.find("ROOT", SessionManager.session());
    }
    
    /**
     * Note! This is possibly not necessary
     * Returns the logical children of the {@code FolderResourceController}.
     * This is required for this to be a collection
     * 
     * @param frc	The {@code FolderResourceController} that holds the logical
     * 				children
     * @return		Returns the children of the root-directory
     */
    @ChildrenOf
    public List<FolderResource> getChildren(FolderResourceController frc) {
        return children;
    }
    
    /**
     * Returns the logical children of a {@code FolderResource}.
     * This is required for this to be a collection
     *
     * @param source	The {@code FolderResource} that will hold the logical children
     * @return			Returns the children of the specified {@code FolderResource}
     * 					i.e. directory
     */
    @ChildrenOf
    public List<AbstractResource> getFolderChildren(FolderResource source) {
    	return source.getChildren();
    }
    
    /**
     * Instantiates a new {@code FolderResource} that acts as a context root for
     * the virtual file system. Should only be done when first setting up the
     * webapp as all usser-directories depend on this. Persists this to a database
     * 
     * @param root	The {@code RootController} that {@code this} is rooted in
     * @param name	The name to assign. Is not used, instead {@code ROOT} is
     * 				used
     * @return		The newly created {@FolderResource}
     */
    @MakeCollection
    public FolderResource createRoot(RootController root, String name) {
    	Transaction tx = SessionManager.session().beginTransaction();
    	FolderResource folderRoot = new FolderResource();
    	
    	try {
	    	folderRoot.setId(UUID.randomUUID().toString());
	    	folderRoot.setName("ROOT");
	    	folderRoot.setParent(null);
	    	Date now = new Date();
	    	folderRoot.setModifiedDate(now);
	    	folderRoot.setCreatedDate(now);
	    	//folderRoot.setFullPath("");
	    	folderRoot.setOwner(null);
	    	
			SessionManager.session().save(folderRoot);
			SessionManager.session().flush();
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
		}
		
    	return folderRoot;
    }
    
    /**
     * Creates a {@code FolderResource} with the given name inside the source
     * object. Persits this to a database as well as a file system
     * 
     * @param parent	The object that will hold the new {@code FolderResource}
     * @param name		The name of the new {@code FolderResource}
     * @return			The newly created {@code FolderResource}
     */
    @MakeCollection
    public FolderResource create(FolderResource parent, String name) {
    	Transaction tx = SessionManager.session().beginTransaction();
    	FolderResource fr = FolderResource.create(parent, name);
    	
    	try {
			SessionManager.session().save(fr);
			SessionManager.session().update(parent);
			SessionManager.session().flush();
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
		}
    	
    	FileSystemUtil.createDirectory(Paths.get(RootController.getWebdavRoot(), fr.resolveFullPath()));
			
		System.out.println(fr.getOwner() == null);
    	return fr;
    }
    
    /**
     * Deletes an existing {@code FolderResource}. Persists this to a database
     * as well as a file system
     * 
     * @param source	The {@code FolderResource} to delete
     * @param parent	The {@code FolderResource} containing the {@code FolderResource}
     */
    @Delete
    public void delete(FolderResource source, FolderResource parent) {
		Transaction tx = SessionManager.session().beginTransaction();
    	parent.deleteChild(source);
    	
		try {
			SessionManager.session().delete(source);
			SessionManager.session().update(parent);
			SessionManager.session().flush();
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
		}
    	
    	FileSystemUtil.deleteDirectory(Paths.get(RootController.getWebdavRoot(), source.resolveFullPath()));
    }
    
    /**
     * Copies a {@code FolderResource}. Persists this to a database
     * as well as a file system
     * 
     * @param source		The {@code FolderResource} to copy
     * @param newParent		The {@code FolderResource} that will contain the
     * 						copied {@code FolderResource}
     * @param newName		Usually the same as the {@code FolderResource} being copied
     * @return				The newly created copy
     */
    @Copy
    public FolderResource copyTo(FolderResource source, FolderResource newParent, String newName) {
		Transaction tx = SessionManager.session().beginTransaction();
		FolderResource copy = source.copyTo(newParent, newName, SessionManager.session());
		
		try {
			SessionManager.session().update(newParent);
			SessionManager.session().flush();
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
		}
    	
    	Path sourceDir = Paths.get(RootController.getWebdavRoot(), source.resolveFullPath());
    	Path dirPath = Paths.get(RootController.getWebdavRoot(), newParent.resolveFullPath(), newName);
    	FileSystemUtil.copyDirectory(sourceDir, dirPath);
    	
    	return copy;
    }
    
    /**
     * Moves or renames a {@code FolderResource}. Persists this to a database
     * as well as a file system
     * 
     * @param source		The {@code FolderResource to move
     * @param newParent		The {@code FolderResource} that will contain the
     * 						{@code FolderResource}
     * @param newName		The new name to assign
     */
    @Move
    public void moveTo(FolderResource source, FolderResource newParent, String newName) {
    	Transaction tx = SessionManager.session().beginTransaction();
    	FolderResource parent = source.getParent();
    	String oldPath = source.resolveFullPath();
    	source.moveTo(newParent, newName);
    	
    	if (parent == newParent) {
    		try {
    			SessionManager.session().update(source);
    			SessionManager.session().flush();
    			tx.commit();
    		} catch (Exception e) {
    			tx.rollback();
    		}
    		
    		Path path = Paths.get(RootController.getWebdavRoot(), oldPath);
    		FileSystemUtil.renameDirectory(path, newName);
    	} else {
    		try {
    			SessionManager.session().update(source);
    			SessionManager.session().update(newParent);
    			SessionManager.session().update(parent);
    			SessionManager.session().flush();
    			tx.commit();
    		} catch (Exception e) {
    			e.printStackTrace();
    			tx.rollback();
    		}
    		
    		Path path = Paths.get(RootController.getWebdavRoot(), oldPath);
    		Path newPath = Paths.get(RootController.getWebdavRoot(), newParent.resolveFullPath());
    		FileSystemUtil.moveDirectory(path, newPath);
    	}
    }
    
    @Post(params={"name"})
    public JsonResult createFolderPost(FolderResource parent, Map<String, String> params) {
    	return JsonResult.returnData(create(parent, params.get("name")));
    }
    
    //@Post
    public JsonResult uploadFiles(FolderResource parent, Map<String, String> params) {
    	System.out.println(params.size());
    	return JsonResult.returnData("success");
    }
    
    /*@AccessControlList
    public List<AccessControlledResource.Priviledge> getUserPrivs(FolderResourceController target, @Principal User user) {
    	return AccessControlledResource.READ_BROWSE;
    }
    
    @AccessControlList
    public List<AccessControlledResource.Priviledge> getUserPrivs(FolderResource target, User user) {
    	if (user == null) {
    		return AccessControlledResource.READ_WRITE;
    	} else if (user.getName().equals("M")) {
    		System.out.println(user.getName());
    		return AccessControlledResource.READ_WRITE;
    	} else {
    		System.out.println(user.getName());
    		return AccessControlledResource.READ_BROWSE;
    	}
    }*/
}