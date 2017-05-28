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

package com.github.phonemirror;

import com.github.phonemirror.gui.controller.DevicesTabFxmlController;
import dagger.Component;

import javax.inject.Singleton;

/**
 * The dagger component for the application
 */
@Singleton
@Component(modules = {GuiModule.class})
public interface AppComponent {
    void inject(Main main);

    void inject(DevicesTabFxmlController devicesTabFxmlController);
}
