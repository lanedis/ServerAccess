/*
 * Copyright (C) 2005-2012 NAUMEN. All rights reserved.
 *
 * This file may be distributed and/or modified under the terms of the
 * GNU General Public License version 2 as published by the Free Software
 * Foundation and appearing in the file LICENSE.GPL included in the
 * packaging of this file.
 *
 */
package ru.naumen.servacc.ui;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Load any image once and store it in cache.
 * <p/>
 * Extracted from {@link UIController}
 *
 * @author Andrey Hitrin
 * @since 21.08.12
 */
public class ImageCache
{
    private static Map<ImageKey, Image> images = new HashMap<ImageKey, Image>();

    public static Image getImage(String name)
    {
        return getImage(name, 0);
    }

    public static Image getImage(String name, int index)
    {
        ImageKey key = new ImageKey(name, index);
        if (images.containsKey(key))
        {
            return images.get(key);
        }
        else
        {
            ImageLoader imageLoader = new ImageLoader();
            InputStream is = ImageCache.class.getResourceAsStream(name);
            ImageData[] data = imageLoader.load(is);
            Image image = new Image(Display.getCurrent(), data[index]);
            images.put(key, image);
            return image;
        }
    }

    private final static class ImageKey implements Comparable<ImageKey>
    {
        private final String name;
        private final int index;

        public ImageKey(String name, int index)
        {
            this.name = name;
            this.index = index;
        }

        public int compareTo(ImageKey other)
        {
            int result = name.compareTo(other.name);
            if (result == 0)
            {
                result = Integer.valueOf(index).compareTo(other.index);
            }
            return result;
        }

        public boolean equals(Object other)
        {
            if (other instanceof ImageKey)
            {
                return compareTo((ImageKey) other) == 0;
            }
            return false;
        }

        public int hashCode()
        {
            return toString().hashCode();
        }

        public String toString()
        {
            return name + ", " + index;
        }
    }
}
