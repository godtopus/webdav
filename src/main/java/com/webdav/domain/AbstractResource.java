package com.webdav.domain;

import java.io.File;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The {@code AbstractResource} class defines the base contract for
 * the in-memory file system objects
 * 
 * @author	M
 * @version	1.0
 * @since
 */

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public abstract class AbstractResource {
	private String id;
	private String name;
	private FolderResource parent;
	private User owner;
	private Date modifiedDate;
	private Date createdDate;
	
	@Id
	@Column(nullable=false, unique=true)
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	@Column(nullable=false)
    public String getName() {
        return name;
    } 
    
    public void setName(String name) {
    	this.name = name;
    }
    
    @ManyToOne
    public FolderResource getParent() {
    	return parent;
    }
    
    public void setParent(FolderResource parent) {
    	this.parent = parent;
    }
	
	/**
	 * Gets the authenticated <code>User</code> that created this resource.
	 * 
	 * @return	The user that created this resource
	 */
	@OneToOne(optional=true)
    @JoinColumn(name="id")
	public User getOwner() {
		return owner;
	}
	
	/**
	 * Sets the authenticated <code>User</code> that created this resource.
	 * 
	 * @return	The user that created this resource
	 */
	public void setOwner(User owner) {
		this.owner = owner;
	}
    
    @Column(nullable=false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getModifiedDate() {
    	return this.modifiedDate;
    }
    
    public void setModifiedDate(Date modifiedDate) {
    	this.modifiedDate = modifiedDate;
    }
    
    @Column(nullable=false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getCreatedDate() {
    	return this.createdDate;
    }
    
    public void setCreatedDate(Date createdDate) {
    	this.createdDate = createdDate;
    }
    
    /**
     * Gets the relative path to the file system root of this resource,
     * e.g. New Folder/Other Folder or New Folder/New Text Document.txt
     * 
     * @return	The relative path
     */
	public String resolveFullPath() {
		StringBuilder sb = new StringBuilder(name);
		FolderResource temp = this.parent;
		
		while (temp != null && temp.getParent() != null) {
			sb.insert(0, File.separator);
			sb.insert(0, temp.getName());
			temp = temp.getParent();
		}
		
		return this.parent == null ? "" : sb.toString();
	}
}