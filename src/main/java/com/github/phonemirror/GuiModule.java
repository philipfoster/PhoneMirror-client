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

package com.github.phonemirror;

import com.github.phonemirror.gui.controller.MainMenuViewController;
import com.github.phonemirror.gui.view.MainMenuView;
import com.github.phonemirror.gui.view.impl.MainMenu;
import dagger.Module;
import dagger.Provides;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * The dagger module for the application
 */
@Module
public class GuiModule {

    @Provides
    public MainMenuViewController provdesMainMenuController() {
        return new MainMenuViewController();
    }

    @Provides
    @Singleton
    @Inject
    public MainMenuView providesMainMenu(MainMenuViewController controller) {
        return new MainMenu(controller);
    }

}
