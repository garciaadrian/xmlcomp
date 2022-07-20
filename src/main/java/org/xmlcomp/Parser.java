/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.xmlcomp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public interface Parser {
    Logger logger = LogManager.getLogger();
    default InputStream open(String filename) throws FileNotFoundException {
        InputStream file = null;
        try {
            file = new FileInputStream(filename);
        } catch (FileNotFoundException | SecurityException e) {
            logger.warn("Exception thrown: {}", e.getMessage());
        }
        return file;
    }

    /**
     * Returns an InputStream of a resource.
     * If the resource is not found, throws a NullPointerException.
     *
     * @param resourceName The name of the resource to open.
     * @throws NullPointerException if resource does not exist.
     * @return An InputStream of the resource.
     */
    default InputStream openResource(String resourceName) {
        InputStream resource = Main.class.getResourceAsStream(resourceName);
        if (resource == null) {
            throw new NullPointerException();
        }

        return resource;
    }
}
