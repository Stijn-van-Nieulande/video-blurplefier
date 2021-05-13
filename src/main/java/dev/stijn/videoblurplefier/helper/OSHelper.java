package dev.stijn.videoblurplefier.helper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class OSHelper
{
    @NotNull
    public static final String OS = System.getProperty("os.name").toLowerCase();
    @Nullable
    public static final File DATA_DIR;

    static {
        if (isWindows()) {
            DATA_DIR = new File(System.getenv("APPDATA"), "video-blurplefier");
            DATA_DIR.mkdirs();
        } else {
            DATA_DIR = null;
        }
    }

    private OSHelper()
    {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static boolean isWindows()
    {
        return OS.contains("win");
    }

    public static boolean isMac()
    {
        return OS.contains("mac");
    }

    public static boolean isUnix()
    {
        return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
    }

    public static boolean isSolaris()
    {
        return OS.contains("sunos");
    }
}
