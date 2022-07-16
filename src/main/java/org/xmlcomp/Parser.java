/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.xmlcomp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public interface Parser {
    default InputStream open(String filename) throws FileNotFoundException {
        InputStream file = null;
        try {
            file = new FileInputStream(filename);
        } catch (FileNotFoundException | SecurityException e) {
            e.printStackTrace();
        }
        return file;
    }

    default InputStream openResource(String resource) {
        // #TODO(adrian): this func can fail by returning an empty stream
        return Main.class.getResourceAsStream(resource);
    }
}
