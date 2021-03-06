package com.webdav.util;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class CopyFileVisitor extends SimpleFileVisitor<Path> {
	private final Path sourcePath;
	private final Path targetPath;
	
	public CopyFileVisitor(Path sourcePath, Path targetPath) {
		this.sourcePath = sourcePath;
		this.targetPath = targetPath;
	}

	@Override
	public FileVisitResult preVisitDirectory(final Path dir,
			final BasicFileAttributes attrs) throws IOException {
		
		Files.createDirectories(targetPath.resolve(sourcePath.relativize(dir)));
		
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(final Path file,
			final BasicFileAttributes attrs) throws IOException {
		
		Files.copy(file, targetPath.resolve(sourcePath.relativize(file)));
		
		return FileVisitResult.CONTINUE;
	}
}