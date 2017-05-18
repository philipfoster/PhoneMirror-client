package com.github.phonemirror.gui.controller;

import javafx.fxml.FXML;

/**
 * Main menu fxml view controller.
 */
public class MainMenuFxmlController {

    @FXML
    private void onDevicesClicked() {
        System.out.println("Hello World");
    }

    @FXML
    private void onExitClicked() {
        System.exit(0);
    }

}
