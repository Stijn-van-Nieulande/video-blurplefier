package dev.stijn.videoblurplefier.binaries.installer;

import dev.stijn.videoblurplefier.binaries.BinaryManager;
import dev.stijn.videoblurplefier.helper.OSHelper;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

public class FfmpegWindowsBinaryInstaller implements BinaryInstaller
{
    private static final String DOWNLOAD_URL = "https://www.gyan.dev/ffmpeg/builds/ffmpeg-release-essentials.zip";
    private static final String CHECKSUM_URL = "https://www.gyan.dev/ffmpeg/builds/packages/ffmpeg-5.0.1-essentials_build.zip.sha256";

    public FfmpegWindowsBinaryInstaller()
    {
    }

    @Nullable
    private String readChecksum() throws IOException
    {
        BinaryManager.LOGGER.info("Reading checksum.");
        return BinaryInstallerUtils.readFirstLineFromUrl(new URL(CHECKSUM_URL));
    }

    @Override
    public boolean install()
    {
        BinaryManager.LOGGER.info("Starting installation of windows binaries.");
        boolean success = false;

        try {
            final File binariesDirectory = new File(OSHelper.DATA_DIR, "ffmpeg");

            if ((binariesDirectory.exists() && binariesDirectory.isDirectory()) || binariesDirectory.mkdirs()) {
                final String checksum = this.readChecksum();

                if (checksum != null && !checksum.isBlank()) {
                    final File tempFile = new File(OSHelper.DATA_DIR, "temp/ffmpeg");

                    BinaryManager.LOGGER.info("Downloading FFmpeg files (this can take a while).");
                    FileUtils.copyURLToFile(new URL(DOWNLOAD_URL), tempFile);

                    try (final FileInputStream tempFileInputStream = new FileInputStream(tempFile)) {
                        BinaryManager.LOGGER.info("Validating checksum.");
                        if (DigestUtils.sha256Hex(tempFileInputStream).equals(checksum)) {
                            try {
                                BinaryManager.LOGGER.info("Extracting files.");
                                final ZipFile zipFile = new ZipFile(tempFile);
                                zipFile.extractAll(binariesDirectory.getAbsolutePath());

                                BinaryInstallerUtils.moveAllFilesOutOfChildFolder(binariesDirectory.toPath());

                                BinaryManager.LOGGER.info("Successfully installed FFmpeg.");
                                success = true;
                            } catch (final ZipException zipException) {
                                zipException.printStackTrace();
                                BinaryManager.LOGGER.severe(zipException.getMessage());
                            }
                        } else {
                            BinaryManager.LOGGER.severe("Checksum failed.");
                        }
                    }

                    tempFile.delete();
                } else {
                    BinaryManager.LOGGER.severe("Checksum not available!");
                }
            } else {
                BinaryManager.LOGGER.severe("Failed creating FFmpeg folder.");
            }
        } catch (final Exception e) {
            e.printStackTrace();
            BinaryManager.LOGGER.severe(e.getMessage());
        }
        return success;
    }
}
