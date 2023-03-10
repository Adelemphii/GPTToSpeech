package io.github.adelemphii.utility;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUtility {

    /**
     * Copy a file from source to destination.
     *
     * @param source the source
     * @param destination the destination
     * @return True if succeeded, False if not
     */
    public static boolean copy(InputStream source , String destination) {
        System.out.println("Copying ->" + source + "\n\tto ->" + destination);

        try {
            Files.copy(source, Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;

    }

}
