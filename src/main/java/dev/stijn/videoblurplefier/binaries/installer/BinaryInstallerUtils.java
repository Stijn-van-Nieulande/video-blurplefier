package dev.stijn.videoblurplefier.binaries.installer;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BinaryInstallerUtils
{
    private BinaryInstallerUtils()
    {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static void moveAllFilesOutOfChildFolder(@NotNull final Path parentFolder) throws IOException
    {
        final List<Path> files = Files.list(parentFolder).collect(Collectors.toList());

        if (files.size() == 1) {
            final File parent = files.get(0).toFile();

            if (parent.isDirectory()) {
                final File[] parentFiles = parent.listFiles();

                if (parentFiles != null) {
                    for (final File file : parentFiles) {
                        file.renameTo(new File(parentFolder.toFile(), file.getName()));
                    }
                }
                parent.delete();
            }
        }
    }

    public static String readFirstLineFromUrl(@NotNull final URL url) throws IOException
    {
        Objects.requireNonNull(url);
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return in.readLine();
        }
    }
}
