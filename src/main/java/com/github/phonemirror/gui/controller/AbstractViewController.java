/*
 *     Copyright 2017-2017 Philip Foster
 *
 *     This file is part of PhoneMirror.
 *
 *     Foobar is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Foobar is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.phonemirror.gui.controller;

/**
 * This is the base class for all view controllers.
 * Views should call {@link #registerView(Object)} as soon as
 * they are provided with a controller.
 *
 * @param <V> The type of view that this object controls
 */
public abstract class AbstractViewController<V> {

    private V view;

    protected V getView() {
        if (view == null) {
            throw new NullPointerException("view is null. Did you forget register the view with the controller?");
        }
        return view;
    }

    public void registerView(V view) {
        this.view = view;
    }

}
