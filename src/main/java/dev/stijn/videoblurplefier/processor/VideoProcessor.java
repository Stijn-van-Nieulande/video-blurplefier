package dev.stijn.videoblurplefier.processor;

import java.io.File;
import java.util.function.Consumer;

public interface VideoProcessor
{
    void setProgressListener(Consumer<Progress> listener);

    void process(File input, File output);
}
