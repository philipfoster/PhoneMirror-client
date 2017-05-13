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

package com.github.phonemirror.gui.view.impl;

import com.github.phonemirror.gui.controller.MainMenuViewController;
import com.github.phonemirror.gui.view.MainMenuView;

import javax.inject.Inject;
import javax.swing.*;

/**
 * Created by philip on 5/12/17.
 */
public class MainMenu implements MainMenuView {


    private JPanel panel;
    private JPanel buttonPanel;
    private JButton notificationsButton;
    private JButton messagesButton;
    private JButton phoneButton;
    private JButton settingsButton;
    private JButton devicesButton;
    private JPanel contentPanel;

    private MainMenuViewController controller;

    @Inject
    public MainMenu(MainMenuViewController controller) {

        this.controller = controller;
        controller.registerView(this);

        JFrame frame = new JFrame("Phone Mirror");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.pack();
        frame.setVisible(true);

        setupClickListeners();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(frame);
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            // TODO: setup proper logging
            e.printStackTrace();
        }

    }

    private void setupClickListeners() {

    }


    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    @Override
    public void showDevicesTab() {

    }

    @Override
    public void showNotificationsTab() {

    }

    @Override
    public void showMessagesTab() {

    }

    @Override
    public void showPhoneTab() {

    }

    @Override
    public void showSettingsTab() {

    }

}
