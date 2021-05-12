package dev.stijn.videoblurplefier;

import dev.stijn.videoblurplefier.gui.BlurpleDarkTheme;
import dev.stijn.videoblurplefier.gui.MainGui;
import dev.stijn.videoblurplefier.tray.TrayManager;
import mdlaf.MaterialLookAndFeel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class VideoBlurplefier
{
    public static final String OS = System.getProperty("os.name").toLowerCase();
    // public static final File HOME_DIR = new File(System.getProperty("user.home"), "video-blurplefier");

    @NotNull
    private final TrayManager trayManager;
    @Nullable
    public Path executablesPath = null;

    public VideoBlurplefier() throws UnsupportedLookAndFeelException
    {
        this.trayManager = new TrayManager(this);

        //        copyResources(); // TODO: re-enable when fixed
        System.out.println("Loading GUI...");
        UIManager.setLookAndFeel(new MaterialLookAndFeel(new BlurpleDarkTheme()));
        UIManager.put("Button[border].enable", false);
        UIManager.put("Button[border].toAll", false);

        MainGui.open(this);
        System.out.println("GUI Ready.");
    }

    public static void main(final String[] args) throws UnsupportedLookAndFeelException
    {
        new VideoBlurplefier();
    }

    public @NotNull TrayManager getTrayManager()
    {
        return this.trayManager;
    }

    public boolean isWindows()
    {
        return OS.contains("win");
    }

    public boolean isMac()
    {
        return OS.contains("mac");
    }

    public boolean isUnix()
    {
        return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
    }

    public boolean isSolaris()
    {
        return OS.contains("sunos");
    }

    public void copyResources()
    {
        // make sure the bin dir exists
        // new File(HOME_DIR, "bin").mkdirs();

        if (this.isWindows()) {
            final File appdataDir = new File(System.getenv("APPDATA"), "video-blurplefier/bin");
            appdataDir.mkdirs();

            this.copyExecutableResource("bin/win/ffmpeg.exe", new File(appdataDir, "ffmpeg.exe"));
            this.copyExecutableResource("bin/win/ffplay.exe", new File(appdataDir, "ffplay.exe"));
            this.copyExecutableResource("bin/win/ffprobe.exe", new File(appdataDir, "ffprobe.exe"));

            this.executablesPath = appdataDir.toPath();
        } else {
            throw new RuntimeException("OS not supported");
        }
    }

    private void copyExecutableResource(final String resource, final File target)
    {
        try {
            Files.copy(Objects.requireNonNull(VideoBlurplefier.class.getClassLoader().getResource(resource)).openStream(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
