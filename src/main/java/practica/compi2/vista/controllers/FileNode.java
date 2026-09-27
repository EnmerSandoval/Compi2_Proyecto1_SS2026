package practica.compi2.vista.controllers;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileNode {

    private final Path path;

    public FileNode(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    public boolean isFolder() {
        return Files.isDirectory(path);
    }

    @Override
    public String toString() {
        return path.getFileName().toString();
    }
}
