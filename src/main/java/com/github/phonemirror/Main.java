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

import com.github.phonemirror.util.Configuration;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.io.IOException;

public class Main extends Application {

    public static AppComponent component;
    @Inject
    Configuration config;

    @Inject
    AppDaemon daemon;

    public Main() {
        component.inject(this);
    }


    public static void main(String[] args) {
        component = DaggerAppComponent.create();
        launch(args);
    }

    @Override
    public void start(Stage mainStage) throws IOException {

        daemon.start();

        component.inject(this);
        Parent root = FXMLLoader.load(config.getMainMenuLayout());
        Scene scene = new Scene(root, config.getDefaultWidth(), config.getDefaultHeight());
        mainStage.setMinWidth(config.getMinWidth());
        mainStage.setMinHeight(config.getMinHeight());

        mainStage.setTitle("Phone Mirror");
        mainStage.setScene(scene);
        mainStage.setOnCloseRequest(e -> Platform.exit());
        mainStage.show();

    }
}
