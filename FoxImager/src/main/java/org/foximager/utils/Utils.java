package org.foximager.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;

public class Utils {
    public static byte[] readFile(File file) throws IOException {
        return Files.readAllBytes(FileSystems.getDefault().getPath(file.getAbsolutePath()));
    }
    
    public static void writeFile(File file, byte[] content) throws IOException {
        Files.write(FileSystems.getDefault().getPath(file.getAbsolutePath()), content);
    }
}
