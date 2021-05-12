package dev.stijn.videoblurplefier.tray;

import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;


public class TrayMessage
{
    public static void main(String[] args, TrayIcon.MessageType type, String message) throws AWTException, MalformedURLException {
        if (SystemTray.isSupported()) {
            displayTray(message, type);
        } else {
            System.err.println("System tray not supported!");
        }
    }

    public static void displayTray(String message, TrayIcon.MessageType type) throws AWTException, MalformedURLException {

        URL imageres = TrayMessage.class.getClassLoader().getResource("resources/trayicon.png");
        Image image = new ImageIcon(imageres).getImage();
        //Obtain only one instance of the SystemTray object
        SystemTray tray = SystemTray.getSystemTray();
        TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");

        trayIcon.setImageAutoSize(true);
        //Set tooltip text for the tray icon
        trayIcon.setToolTip("System tray icon demo");
        tray.add(trayIcon);
        trayIcon.displayMessage("Video Blurplefier", message, type);
    }
}
