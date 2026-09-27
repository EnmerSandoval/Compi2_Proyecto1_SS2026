package practica.compi2.vista.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.tree.DefaultMutableTreeNode;

public class FileTreeBuilder {

    public DefaultMutableTreeNode build(File folder) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new FileNode(folder.toPath()));
        for (File child : sortedChildren(folder)) {
            if (child.isDirectory()) {
                node.add(build(child));
            } else {
                node.add(new DefaultMutableTreeNode(new FileNode(child.toPath())));
            }
        }
        return node;
    }

    private List<File> sortedChildren(File folder) {
        Map<String, File> folders = new TreeMap<>();
        Map<String, File> files = new TreeMap<>();
        File[] children = folder.listFiles();
        if (children == null) {
            return new ArrayList<>();
        }
        for (File child : children) {
            if (child.isHidden() || child.getName().startsWith(".")) {
                continue;
            }
            if (child.isDirectory()) {
                folders.put(child.getName().toLowerCase(), child);
            } else {
                files.put(child.getName().toLowerCase(), child);
            }
        }
        List<File> sorted = new ArrayList<>(folders.values());
        sorted.addAll(files.values());
        return sorted;
    }
}
