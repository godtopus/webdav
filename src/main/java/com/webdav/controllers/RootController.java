package com.webdav.controllers;

import java.io.File;
import java.io.UnsupportedEncodingException;

import com.webdav.util.FileSystemUtil;

import io.milton.annotations.Get;
import io.milton.annotations.Name;
import io.milton.annotations.ResourceController;
import io.milton.annotations.Root;
import io.milton.common.ModelAndView;

/**
 * This is the controller for the {@code RootResource}, i.e. runs on the
 * configured webapp context path. It uses the Milton Annotations-framework,
 * to represent a controller,
 * see <a href="http://milton.io/guide/02-implementation/01-annotations/index.html">http://milton.io/guide/02-implementation/01-annotations/index.html</a>.
 * Milton is open source, see source code at <a href="https://github.com/miltonio/milton2">https://github.com/miltonio/milton2</a>
 * 
 * @author M
 * @version	1.0
 * @since
 */

@ResourceController
public class RootController {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(RootController.class);
	private static String WEBDAVROOT = FileSystemUtil.getSystemRoot() + "webdav";
	
    @Root
    public RootController getRoot() {
        return this;
    }
    
    @Name
    public String getName(RootController root) {
        return "";
    }
    
    /**
     * Renders the home page from a template located in /templates/homePage.jsp
     * 
     * @param root	The {@code RootController} that runs on the webapp context root
     * @return		The rendered template
     * @throws UnsupportedEncodingException	If the template uses unsupported encoding
     */
    @Get
    public ModelAndView renderHomePage(RootController root) throws UnsupportedEncodingException {
    	return new ModelAndView("home", this, "homePage");
    }
    
	/**
	 * Gets the defined file system root, where users folders and files are stored
	 * 
	 * @return	The hard coded file system root on the server
	 */
	static String getWebdavRoot() {
		return WEBDAVROOT;
	}
}