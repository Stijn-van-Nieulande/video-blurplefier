package dev.stijn.videoblurplefier;

import dev.stijn.videoblurplefier.binaries.BinaryManager;
import dev.stijn.videoblurplefier.gui.BlurpleDarkTheme;
import dev.stijn.videoblurplefier.gui.MainGui;
import dev.stijn.videoblurplefier.tray.TrayManager;
import mdlaf.MaterialLookAndFeel;
import org.jetbrains.annotations.NotNull;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class VideoBlurplefier
{
    @NotNull
    private final TrayManager trayManager;
    @NotNull
    private final BinaryManager binaryManager;

    public VideoBlurplefier() throws UnsupportedLookAndFeelException
    {
        this.trayManager = new TrayManager(this);
        this.binaryManager = new BinaryManager();

        UIManager.setLookAndFeel(new MaterialLookAndFeel(new BlurpleDarkTheme()));

        MainGui.open(this);

        new Thread(this.binaryManager::installBinaries).start();
    }

    public static void main(final String[] args) throws UnsupportedLookAndFeelException
    {
        new VideoBlurplefier();
    }

    public @NotNull TrayManager getTrayManager()
    {
        return this.trayManager;
    }

    public @NotNull BinaryManager getBinaryManager()
    {
        return this.binaryManager;
    }
}
