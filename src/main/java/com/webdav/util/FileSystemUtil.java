package com.webdav.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileSystemUtil {
	public static String getSystemRoot() {
		return File.listRoots()[0].toString();
	}
	
    public static byte[] read(Path file) {
    	byte[] fileData = null;
    	
    	try {
			fileData = Files.readAllBytes(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return DataEncryption.getInstance().decrypt(fileData);
    }
    
    public static void write(Path file, byte[] content) {
    	try {
    		Files.write(file, DataEncryption.getInstance().encrypt(content));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	public static void deleteFile(Path file) {
    	try {
			Files.delete(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void copyFile(Path file, Path dir) {
    	try {
			Files.copy(file, dir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void renameFile(Path path, String newName) {
		try {
			Files.move(path, path.resolveSibling(newName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void moveFile(Path path, Path newPath) {
		try {
			Files.move(path, newPath.resolve(path.getFileName()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void createDirectory(Path dir) {
    	try {
			Files.createDirectory(dir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void deleteDirectory(Path dir) {
		DeleteFileVisitor deleteDirVisitor = new DeleteFileVisitor();
		
		try{
			Files.walkFileTree(dir, deleteDirVisitor);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void copyDirectory(Path sourceDir, Path targetDir) {
		CopyFileVisitor copyDirVisitor = new CopyFileVisitor(sourceDir, targetDir);
		
    	try {
    		Files.walkFileTree(sourceDir, copyDirVisitor);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void renameDirectory(Path path, String newName) {
		try {
			Files.move(path, path.resolveSibling(newName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void moveDirectory(Path sourceDir, Path targetDir) {
		MoveFileVisitor moveDirVisitor = new MoveFileVisitor(sourceDir.getParent(), targetDir);
		
    	try {
    		Files.walkFileTree(sourceDir, moveDirVisitor);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}