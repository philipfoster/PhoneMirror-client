package com.github.phonemirror.gui.view.impl;

import com.github.phonemirror.Main;
import com.github.phonemirror.gui.controller.DevicesViewController;
import com.github.phonemirror.gui.view.DevicesView;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;

/**
 * This class contains UI for showing devices.
 */
public class DevicesTab extends JPanel implements DevicesView {

    @Inject
    DevicesViewController controller;

    private JButton addDevicesButton;

    public DevicesTab() {
        Main.component.inject(this);
        controller.registerView(this);

        setupUi();
    }

    private void setupUi() {
        System.out.println("Setting up ui");
        setLayout(new BorderLayout(0, 0));
        setEnabled(true);
        addDevicesButton = new JButton("Add new device");
        add(addDevicesButton);
    }

}
