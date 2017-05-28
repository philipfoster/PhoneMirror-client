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

package com.github.phonemirror.gui.controller;

import com.github.phonemirror.Main;
import com.github.phonemirror.pojo.PairingData;
import com.github.phonemirror.util.Configuration;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import javax.inject.Inject;
import java.security.SecureRandom;

/**
 * View controller class for Devices tab
 */
public class DevicesTabFxmlController {

    private static final int QR_SIZE = 300;

    @Inject
    QRCodeWriter qrCodeWriter;

    @Inject
    SecureRandom csprng;

    @Inject
    Configuration config;

    @FXML
    private Text deviceNameText;
    @FXML
    private Text serialNoText;
    @FXML
    private Text deviceStatusText;
    @FXML
    private ListView<String> devicesList;

    public DevicesTabFxmlController() {
//        devicesList.setItems();
        Main.component.inject(this);
    }


    @FXML
    private void showPairingDialog() throws WriterException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        PairingData pd = new PairingData(config, csprng);
        Image image = generatePairingQrCode(pd);

        ImageView iv = new ImageView(image);
        alert.setGraphic(iv);
        alert.setHeaderText(null);
        alert.setContentText("Scan this QR code with the PhoneMirror app to pair.");
        alert.setTitle("Pair new device");
        alert.showAndWait();
    }

    /**
     * Generate an image representing the qr code for a given {@link PairingData}1
     *
     * @throws WriterException if the {@code PairingData} could not be written
     */
    private Image generatePairingQrCode(PairingData pairingData) throws WriterException {
        String data = pairingData.toString();

        WritableImage img = new WritableImage(QR_SIZE, QR_SIZE);
        BitMatrix bitMtx = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE);

        PixelWriter writer = img.getPixelWriter();
        for (int row = 0; row < QR_SIZE; row++) {
            for (int col = 0; col < QR_SIZE; col++) {
                if (bitMtx.get(row, col)) {
                    writer.setColor(row, col, Color.BLACK);
                } else {
                    writer.setColor(row, col, Color.WHITE);
                }
            }
        }

        return img;
    }


}