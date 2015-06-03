package com.webdav.controllers;

import io.milton.annotations.AccessControlList;
import io.milton.annotations.ContentLength;
import io.milton.annotations.ContentType;
import io.milton.annotations.Copy;
import io.milton.annotations.CreatedDate;
import io.milton.annotations.Delete;
import io.milton.annotations.DisplayName;
import io.milton.annotations.Get;
import io.milton.annotations.MaxAge;
import io.milton.annotations.ModifiedDate;
import io.milton.annotations.Move;
import io.milton.annotations.Name;
import io.milton.annotations.Post;
import io.milton.annotations.Principal;
import io.milton.annotations.PutChild;
import io.milton.annotations.ResourceController;
import io.milton.annotations.UniqueId;
import io.milton.common.JsonResult;
import io.milton.resource.AccessControlledResource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.*;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.apache.tika.Tika;
import org.hibernate.Transaction;

import com.webdav.domain.FileResource;
import com.webdav.domain.FolderResource;
import com.webdav.domain.SessionManager;
import com.webdav.util.DataEncryption;
import com.webdav.util.FileSystemUtil;

/**
 * This is the controller for the {@code FileResource} and runs below the configured
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
public class FileResourceController  {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FileResourceController.class);
    
    @Name
    public String getName(FileResourceController frc) {
    	return "FileResourceController";
    }
    
    @Name
    public String getFileName(FileResource source) {
    	return source.getName();
    }
    
    @ContentLength
    public Long getContentLength(FileResource source) {
    	return (long) source.getContentLength();
    }
    
    @ContentType
    public String getContentType(FileResource source) {
    	return source.getContentType();
    }
    
    @CreatedDate
    public Date getCreatedDate(FileResource source) {
    	return source.getCreatedDate();
    }
    
    @ModifiedDate
    public Date getModifedDate(FileResource source) {
    	return source.getModifiedDate();
    }
    
    @MaxAge
    public Long getMaxAge(FileResource source) {
    	return (long) 0;
    }
    
    @UniqueId
    public String getUniqueId(FileResource source) {
    	return source.getId();
    }
    
    /**
     * Instantiates a new {@code FileResource}. Persists this to a database
     * as well as a file system
     * 
     * @param parent	The {@code FolderResource} that will hold the new {@code FileResource}
     * @param name		The name to assign to the new {@code FileResource}
     * @param content	The binary-encoded content of the new {@code FileResource}
     * @return			The newly created {@code FileResource}
     */
    @PutChild
    public FileResource create(FolderResource parent, String name, byte[] content) {
    	System.out.println(parent.getName() + " " + name + " " + content.length);
    	Transaction tx = SessionManager.session().beginTransaction();
    	FileResource fr = new FileResource();
    	
    	try {
	    	fr.setId(UUID.randomUUID().toString());
	    	fr.setName(name);
	    	fr.setParent(parent);
	    	Date now = new Date();
	    	fr.setModifiedDate(now);
	    	fr.setCreatedDate(now);
	    	//fr.setFullPath(parent.getFullPath() + name + File.separator);
	    	fr.setContentType((new Tika()).detect(name));
	    	fr.setContentLength(content.length);
	    	
	    	parent.addChild(fr);
	    	parent.setModifiedDate(now);
	    	
			SessionManager.session().save(fr);
			SessionManager.session().update(parent);
			SessionManager.session().flush();
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
		}
    	
    	Path file = Paths.get(RootController.getWebdavRoot(), fr.resolveFullPath());
    	FileSystemUtil.write(file, content);
    	
    	return fr;
    }
    
    /**
     * Updates an existing FileResource-instance. Persists this to a database
     * as well as a file system
     * 
     * @param source		The {@code FileResource} to modify
     * @param newContent	The binary-encode new content of the {@code FileResource}
     * @return				The modified {@code FileResource}
     */
    @PutChild
    public FileResource update(FileResource source, byte[] newContent) {
    	Transaction tx = SessionManager.session().beginTransaction();
    	source.setContentLength(newContent.length);
    	source.setModifiedDate(new Date());
    	
    	try {
			SessionManager.session().update(source);
			SessionManager.session().flush();
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
		}
    	
    	Path file = Paths.get(RootController.getWebdavRoot(), source.resolveFullPath());
    	FileSystemUtil.write(file, newContent);
    	
    	return source;
    }
    
    /**
     * Deletes an existing {@code FileResource}. Persists this to a database
     * as well as a file system
     * 
     * @param source	The {@code FileResource} to delete
     * @param parent	The {@code FolderResource} containing the {@code FileResource}
     */
    @Delete
    public void delete(FileResource source, FolderResource parent) {
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
    	
    	FileSystemUtil.deleteFile(Paths.get(RootController.getWebdavRoot(), source.resolveFullPath()));
    }
    
    /**
     * Copies a {@code FileResource}. Persists this to a database
     * as well as a file system
     * 
     * @param source		The {@code FileResource} to copy
     * @param newParent		The {@code FolderResource} that will contain the
     * 						copied {@code FileResource}
     * @param newName		Usually the same as the {@code FileResource} being copied
     * @return				The newly created copy
     */
    @Copy
    public FileResource copyTo(FileResource source, FolderResource newParent, String newName) {
		Transaction tx = SessionManager.session().beginTransaction();
		FileResource copy = source.copyTo(newParent, newName);
		
		try {
			SessionManager.session().update(newParent);
			SessionManager.session().save(copy);
			SessionManager.session().flush();
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
		}
		
    	Path filePath = Paths.get(RootController.getWebdavRoot(), source.resolveFullPath());
    	Path dirPath = Paths.get(RootController.getWebdavRoot(), newParent.resolveFullPath(), newName);
    	FileSystemUtil.copyFile(filePath, dirPath);
    	
    	return copy;
    }
    
    /**
     * Moves or renames a {@code FileResource}. Persists this to a database
     * as well as a file system
     * 
     * @param source		The {@code FileResource to move
     * @param newParent		The {@code FolderResource} that will contain the
     * 						{@code FileResource}
     * @param newName		The new name to assign
     */
    @Move
    public void moveTo(FileResource source, FolderResource newParent, String newName) {
    	FolderResource parent = source.getParent();
    	String oldPath = source.resolveFullPath();
    	
    	Transaction tx = SessionManager.session().beginTransaction();
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
    		FileSystemUtil.renameFile(path, newName);
    	} else {
    		try {
    			SessionManager.session().update(source);
    			SessionManager.session().update(newParent);
    			SessionManager.session().update(parent);
    			SessionManager.session().flush();
    			tx.commit();
    		} catch (Exception e) {
    			tx.rollback();
    		}
    		
    		Path path = Paths.get(RootController.getWebdavRoot(), oldPath);
    		Path newPath = Paths.get(RootController.getWebdavRoot(), newParent.resolveFullPath());
    		FileSystemUtil.moveFile(path, newPath);
    	}
    }
    
    /**
     * A gettable representation of this {@code FileResource}.
     * Can be displayed in a web browser
     * 
     * @param source	The {@code FileResource} to represent
     * @return			The content of the {@code FileResource} written to an {@code InputStream}
     */
    @Get
    public byte[] getFileContent(FileResource source) {
    	System.out.println("getting...");
    	Path file = Paths.get(RootController.getWebdavRoot(), source.resolveFullPath());
    	return FileSystemUtil.read(file);
    }
}