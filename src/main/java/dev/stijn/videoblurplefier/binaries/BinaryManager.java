package dev.stijn.videoblurplefier.binaries;


import dev.stijn.videoblurplefier.binaries.installer.FfmpegWindowsBinaryInstaller;
import dev.stijn.videoblurplefier.helper.OSHelper;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.logging.Logger;

public class BinaryManager
{
    public static final Logger LOGGER = Logger.getLogger(BinaryManager.class.getName());

    @Nullable
    private File ffmpegBinDirectory;

    public BinaryManager()
    {
    }

    public void installBinaries()
    {
        if (OSHelper.isWindows()) {
            this.ffmpegBinDirectory = new File(OSHelper.DATA_DIR, "ffmpeg/bin");

            if (!this.ffmpegBinDirectory.exists() || !this.ffmpegBinDirectory.isDirectory()) {
                new FfmpegWindowsBinaryInstaller().install();
            }
        } else {
            throw new RuntimeException("OS not supported!");
        }
    }

    public @Nullable File getFfmpegBinDirectory()
    {
        return this.ffmpegBinDirectory;
    }
}
