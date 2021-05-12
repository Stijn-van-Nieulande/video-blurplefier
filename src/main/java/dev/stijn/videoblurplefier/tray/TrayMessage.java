package dev.stijn.videoblurplefier.tray;

import dev.stijn.videoblurplefier.VideoBlurplefier;
import org.jetbrains.annotations.NotNull;

import javax.swing.ImageIcon;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class TrayMessage
{
    private final VideoBlurplefier videoBlurplefier;

    public TrayMessage(@NotNull final VideoBlurplefier videoBlurplefier)
    {
        this.videoBlurplefier = Objects.requireNonNull(videoBlurplefier);
    }

    public void displayTray(final String message, final TrayIcon.MessageType type) throws AWTException, MalformedURLException
    {
        if (!SystemTray.isSupported()) throw new RuntimeException("System tray is not supported");

        final URL imageResource = this.videoBlurplefier.getClass().getClassLoader().getResource("trayicon.png");
        if (imageResource == null) return;
        final Image image = new ImageIcon(imageResource).getImage();
        //Obtain only one instance of the SystemTray object
        final SystemTray tray = SystemTray.getSystemTray();
        final TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");

        trayIcon.setImageAutoSize(true);
        //Set tooltip text for the tray icon
        trayIcon.setToolTip("System tray icon demo");
        tray.add(trayIcon);
        trayIcon.displayMessage("Video Blurplefier", message, type);
    }
}
