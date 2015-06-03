package com.webdav.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.criterion.Restrictions;

/**
 * The {@code FolderResource} class defines the base contract for
 * the in-memory folder objects
 * 
 * @author	M
 * @version	1.0
 * @since
 */

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FolderResource extends AbstractResource {
    private List<AbstractResource> children = new ArrayList<AbstractResource>();
    
    public FolderResource() {
    	super();
    }
    
    @OneToMany(mappedBy="parent", cascade=CascadeType.REMOVE)
    public List<AbstractResource> getChildren() {
        return children;
    }
    
    /**
     * Replaces all the children of this {@code FolderResource}.
     * It is up to the caller set the new modified date
     * 
     * @param children	The children to assign to this {@code FolderResource}
     */
    public void setChildren(List<AbstractResource> children) {
    	this.children = children;
    }
    
    public void addChild(AbstractResource newChild) {
    	children.add(newChild);
    	this.setModifiedDate(new Date());
    }
	
	public void deleteChild(AbstractResource child) {
		int i = 0;
		for (AbstractResource ar : children) {
			if (ar == child) {
				children.remove(i);
				return;
			}
			
			i++;
		}
	}
	
	/**
     * Copies this {@code FolderResource} within the parent {@code FolderResource}
     * if {@code newParent} is the same instance as that returned by {@code getParent}.
     * Copies this {@code FolderResource} to {@code newParent} if previous
     * condition holds {@code false}
	 * 
     * @param newParent	The new parent of this {@code FolderResource}.
     * 					May be the same as the current parent
     * @param newName	The name to assign to this {@code FolderResource}.
     * 					Will be different if copying within the same folder, otherwise the same
	 * @return			The newly created copy
	 */
	public FolderResource copyTo(FolderResource newParent, String newName, Session session) {
		FolderResource copy = new FolderResource();
		copy.setId(UUID.randomUUID().toString());
		copy.setName(newName);
		copy.setParent(newParent);
		Date now = new Date();
		copy.setModifiedDate(now);
		copy.setCreatedDate(now);
		copy.setOwner(this.getOwner());
		copy.setChildren(new ArrayList<AbstractResource>());
		
		for (AbstractResource child : this.children) {
			if (child instanceof FolderResource) {
				((FolderResource) child).copyTo(copy, child.getName(), session);
			} else if (child instanceof FileResource) {
				FileResource copiedFile = ((FileResource) child).copyTo(copy, child.getName());
				session.save(copiedFile);
			}
		}
		
		newParent.addChild(copy);
		session.save(copy);
		
        return copy;
	}
	
    /**
     * Renames this {@code FolderResource} if {@code newParent} is the
     * same instance as that returned by {@code getParent}.
     * Moves this {@code FolderResource} to {@code newParent} if previous
     * condition holds {@code false}
     * 
     * @param newParent	The new parent of this {@code FolderResource}.
     * 					May be the same as the current parent
     * @param newName	The name to assign to this {@code FolderResource}.
     * 					Will be different if renaming, otherwise the same
     */
	public void moveTo(FolderResource newParent, String newName) {
		if (this.getParent() != newParent) {
    		FolderResource oldParent = this.getParent();
    		this.setParent(newParent);
    		newParent.addChild(this);
    		oldParent.deleteChild(this);
    	}
		
		this.setName(newName);
	}
	
	/**
	 * Searches the database after the specified {@code FolderResource}
	 * in the specified {@code session}
	 * 
	 * @param name		The unique name of the {@code FolderResource}
	 * @param session	The current session
	 * @return			The found {@code FolderResource}, or {@code null} if not
	 */
	public static FolderResource find(String name, Session session) {
		Criteria crit = session.createCriteria(FolderResource.class);
		crit.add(Restrictions.eq("name", name));
		return DbUtils.unique(crit);
    }
	
	public static FolderResource create(FolderResource parent, String name) {
    	FolderResource fr = new FolderResource();
	    fr.setId(UUID.randomUUID().toString());
	    fr.setName(name);
	    fr.setParent(parent);
	    Date now = new Date();
	    fr.setModifiedDate(now);
	    fr.setCreatedDate(now);
	    fr.setChildren(new ArrayList<AbstractResource>());
	    fr.setOwner(parent.getOwner());
	    	
	    parent.addChild(fr);
	    
	    return fr;
	}
}