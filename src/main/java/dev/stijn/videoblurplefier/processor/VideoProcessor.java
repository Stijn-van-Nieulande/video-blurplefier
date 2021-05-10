package dev.stijn.videoblurplefier.processor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.function.Consumer;

public interface VideoProcessor
{
    void setProgressListener(@Nullable Consumer<Progress> listener);

    void process(@NotNull File input, @NotNull File output);
}
