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
import java.awt.*;

/**
 * Created by philip on 5/12/17.
 */
public class MainMenu implements MainMenuView {

    @Inject
    DevicesTab devicesTab;
    private JPanel panel;
    private JPanel buttonPanel;
    private JButton notificationsButton;
    private JButton messagesButton;
    private JButton phoneButton;
    private JButton settingsButton;
    private JButton devicesButton;
    private JPanel contentPanel;
    private MainMenuViewController controller;

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    @Inject
    public MainMenu(MainMenuViewController controller) {
        this.controller = controller;
        controller.registerView(this);

        JFrame frame = new JFrame("Phone Mirror");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
        devicesButton.addActionListener(e -> controller.onDevicesClicked());
        notificationsButton.addActionListener(e -> controller.onNotificationsClicked());
        messagesButton.addActionListener(e -> controller.onMessagesClicked());
        phoneButton.addActionListener(e -> controller.onPhoneClicked());
        settingsButton.addActionListener(e -> controller.onSettingsClicked());
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        contentPanel.add(devicesTab);
        System.out.println(devicesTab.controller);
    }

    @Override
    public void showDevicesTab() {
        System.out.println("MM showing devices tab");
    }

    @Override
    public void showNotificationsTab() {
        System.out.println("MM showing notifications tab");
    }

    @Override
    public void showMessagesTab() {
        System.out.println("MM showing messages tab");
    }

    @Override
    public void showPhoneTab() {
        System.out.println("MM showing phone tab");
    }

    @Override
    public void showSettingsTab() {
        System.out.println("MM showing settings tab");
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 0));
        panel.setEnabled(true);
        panel.setMinimumSize(new Dimension(525, 110));
        panel.setPreferredSize(new Dimension(525, 110));
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        panel.add(buttonPanel, BorderLayout.NORTH);
        notificationsButton = new JButton();
        notificationsButton.setText("Notifications");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(notificationsButton, gbc);
        messagesButton = new JButton();
        messagesButton.setText("Messages");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(messagesButton, gbc);
        phoneButton = new JButton();
        phoneButton.setText("Phone");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(phoneButton, gbc);
        settingsButton = new JButton();
        settingsButton.setText("Settings");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(settingsButton, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.weightx = 0.125;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(spacer1, gbc);
        devicesButton = new JButton();
        devicesButton.setText("Devices");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(devicesButton, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.125;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(spacer2, gbc);
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout(0, 0));
        panel.add(contentPanel, BorderLayout.CENTER);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }
}
