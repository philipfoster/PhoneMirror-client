/*
 * PhoneMirror-client
 * Copyright (C) 2017  Philip Foster
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.phonemirror.util;

/**
 * Simple math utility functions.
 */
public class MathUtils {

    /**
     * Convert a signed 2's complement {@code byte} into an unsigned int
     *
     * @param b the byte value to convert
     *
     * @return a converted
     */
    public static int unsign(byte b) {
        return 255 & b;
    }

}
