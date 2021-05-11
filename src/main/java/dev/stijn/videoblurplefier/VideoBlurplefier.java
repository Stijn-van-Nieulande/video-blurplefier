package dev.stijn.videoblurplefier;

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
    public static Path executablesPath = null;

    public static void main(final String[] args)
    {
//        copyResources(); // TODO: re-enable when fixed

        System.out.println("Loading GUI...");
        GUImain.invoke();
        System.out.println("GUI Ready.");
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

    public static void copyResources()
    {
        // make sure the bin dir exists
        // new File(HOME_DIR, "bin").mkdirs();

        if (isWindows()) {
            final File appdataDir = new File(System.getenv("APPDATA"), "video-blurplefier/bin");
            appdataDir.mkdirs();

            copyExecutableResource("bin/win/ffmpeg.exe", new File(appdataDir, "ffmpeg.exe"));
            copyExecutableResource("bin/win/ffplay.exe", new File(appdataDir, "ffplay.exe"));
            copyExecutableResource("bin/win/ffprobe.exe", new File(appdataDir, "ffprobe.exe"));

            executablesPath = appdataDir.toPath();
        } else {
            throw new RuntimeException("OS not supported");
        }
    }

    private static void copyExecutableResource(final String resource, final File target)
    {
        try {
            Files.copy(Objects.requireNonNull(VideoBlurplefier.class.getClassLoader().getResource(resource)).openStream(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
