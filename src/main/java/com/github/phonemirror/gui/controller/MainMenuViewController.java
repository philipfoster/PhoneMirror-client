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

import com.github.phonemirror.gui.view.MainMenuView;

/**
 * A controller for {@link MainMenuView}
 */
public class MainMenuViewController extends AbstractViewController<MainMenuView> {


    public void onDevicesClicked() {
        System.out.println("MMVC button clicked");
        getView().showDevicesTab();
    }

    public void onNotificationsClicked() {
        System.out.println("MMVC notifications clicked");
        getView().showNotificationsTab();
    }

    public void onMessagesClicked() {
        System.out.println("MMVC messages clicked");
        getView().showMessagesTab();
    }

    public void onPhoneClicked() {
        System.out.println("MMVC phone clicked");
        getView().showPhoneTab();
    }

    public void onSettingsClicked() {
        System.out.println("MMVC settings clicked");
        getView().showSettingsTab();
    }
}
