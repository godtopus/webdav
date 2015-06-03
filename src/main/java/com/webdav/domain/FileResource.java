package com.webdav.domain;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The {@code FileResource} class defines the base contract for
 * the in-memory file objects
 * 
 * @author	M
 * @version	1.0
 * @since
 */

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FileResource extends AbstractResource {
    private long contentLength;
    private String contentType;
    
    /**
     * Gets the size of the {@code FileResource} in bytes
     * 
     * @return	The size of the {@code FileResource} in bytes
     */
    @Column(nullable=false)
    public long getContentLength() {
    	return this.contentLength;
    }
    
    /**
     * Gets the size of the {@code FileResource} in bytes
     * 
     * @return	The size of the {@code FileResource} in bytes
     */
    public void setContentLength(long contentLength) {
    	this.contentLength = contentLength;
    }
    
    /**
     * Gets the MIME-type for this {@code FileResource}, as interpreted
     * from the file name
     * 
     * @return	The MIME-type for this {@code FileResource}
     */
    @Column(nullable=false)
    public String getContentType() {
    	return this.contentType;
    }
    
    /**
     * Sets the MIME-type for this {@code FileResource}, as interpreted
     * from the file name. Depends on Apche Tika to determine this,
     * see <a href="https://tika.apache.org/">https://tika.apache.org/</a>
     * 
     * @param contentType The MIME-type for this {@code FileResource}
     */
    public void setContentType(String contentType) {
    	this.contentType = contentType;
    }
    
	/**
     * Copies this {@code FileResource} within the parent {@code FolderResource}
     * if {@code newParent} is the same instance as that returned by {@code getParent}.
     * Copies this {@code FileResource} to {@code newParent} if previous
     * condition holds {@code false}
	 * 
     * @param newParent	The new parent of this {@code FileResource}.
     * 					May be the same as the current parent
     * @param newName	The name to assign to this {@code FileResource}.
     * 					Will be different if copying within the same folder, otherwise the same
	 * @return			The newly created copy
	 */
	public FileResource copyTo(FolderResource newParent, String newName) {
		FileResource copy = new FileResource();
		copy.setId(UUID.randomUUID().toString());
		copy.setName(newName);
		copy.setParent(newParent);
		Date now = new Date();
		copy.setModifiedDate(now);
		copy.setCreatedDate(now);
		copy.setOwner(this.getOwner());
		
		copy.setContentLength(this.contentLength);
		copy.setContentType(this.contentType);
		newParent.addChild(copy);
		
        return copy;
	}
    
    /**
     * Renames this {@code FileResource} if {@code newParent} is the
     * same instance as that returned by {@code getParent}.
     * Moves this {@code FileResource} to {@code newParent} if previous
     * condition holds {@code false}
     * 
     * @param newParent	The new parent of this {@code FileResource}.
     * 					May be the same as the current parent
     * @param newName	The name to assign to this {@code FileResource}.
     * 					Will be different if renaming, otherwise the same
     */
	public void moveTo(FolderResource newParent, String newName) {
		if (this.getParent() != newParent) {
    		FolderResource oldParent = this.getParent();
    		this.setParent(newParent);
    		oldParent.deleteChild(this);
    		newParent.addChild(this);
    	}
    	
		//this.setFullPath(newParent.getFullPath() + newName);
    	this.setName(newName);
	}
}