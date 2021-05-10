package dev.stijn.videoblurplefier.processor.ffmpeg;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.FFmpegProgress;
import com.github.kokorin.jaffree.ffmpeg.Filter;
import com.github.kokorin.jaffree.ffmpeg.FilterChain;
import com.github.kokorin.jaffree.ffmpeg.FilterGraph;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
import com.google.inject.internal.util.Preconditions;
import dev.stijn.videoblurplefier.processor.Progress;
import dev.stijn.videoblurplefier.processor.VideoProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;

/*
original commands:

ffmpeg.exe -hwaccel auto -c:v h264 -f lavfi -i "color=0x7289DA:s=1920x1080" -i bee.mkv -vf hue=s=0 output-grayscale.mkv
ffmpeg.exe -f lavfi -i -hwaccel auto -c:v h264 "color=0x7289DA:s=1920x1080" -i output-grayscale.mp4 -filter_complex "blend=shortest=1:all_mode=overlay:all_opacity=1" output.mp4
 */
public class FfmpegVideoProcessor implements VideoProcessor
{
    private static final String BLURPLE_COLOR = "0x7289DA";

    @Nullable
    private final Path executableBinaryFolder;
    private final int videoWidth;
    private final int videoHeight;
    boolean processing = false;
    @Nullable
    private Consumer<Progress> listener;

    public FfmpegVideoProcessor(@Nullable final Path executableBinaryFolder, final int videoWidth, final int videoHeight)
    {
        this.executableBinaryFolder = executableBinaryFolder;
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
    }

    @Override
    public void setProgressListener(@Nullable final Consumer<Progress> listener)
    {
        this.listener = listener;
    }

    @Override
    public void process(@NotNull final File input, @NotNull final File output)
    {
        Objects.requireNonNull(input, "Input file cannot be null");
        Objects.requireNonNull(output, "Output file cannot be null");
        Preconditions.checkState(!this.processing, "Already processing");

        this.processing = true;

        try {
            FFmpeg.atPath(this.executableBinaryFolder)
                    .setOverwriteOutput(true)
                    .addArguments("-hwaccel", "auto")
                    //.addArguments("-c:v", "h264")
                    .addArguments("-f", "lavfi")
                    .addInput(UrlInput.fromPath(input.toPath()))
                    .addArguments("-i", "color=" + BLURPLE_COLOR + ":s=" + this.videoWidth + "x" + this.videoHeight)
                    .setComplexFilter(FilterGraph.of(
                            FilterChain.of(
                                    Filter.fromInputLink("0:v")
                                            .setName("setpts")
                                            .addArgument("PTS-STARTPTS"),
                                    Filter.withName("hue")
                                            .addArgument("s", "0")
                                            .addOutputLink("grayscale")
                            ),
                            FilterChain.of(
                                    Filter.fromInputLink("1:v")
                                            .setName("setpts")
                                            .addArgument("PTS-STARTPTS")
                                            .addOutputLink("solidcolor")
                            ),
                            FilterChain.of(
                                    Filter.fromInputLink("grayscale")
                                            .addInputLink("solidcolor")
                                            .setName("blend")
                                            .addArgument("shortest", "1")
                                            .addArgument("all_mode", "overlay")
                                            .addArgument("all_opacity", "1")
                            )
                    ))
                    .addOutput(UrlOutput.toPath(output.toPath()))
                    .setProgressListener(fFmpegProgress -> {
                        if (this.listener != null)
                            this.listener.accept(this.mapFfmpegProgressToProgress(fFmpegProgress));
                    })
                    .execute();
        } catch (final Exception exception) {
            exception.printStackTrace();
        } finally {
            this.processing = false;
        }
    }

    private Progress mapFfmpegProgressToProgress(@NotNull final FFmpegProgress fFmpegProgress)
    {
        Objects.requireNonNull(fFmpegProgress);
        return new Progress(
                fFmpegProgress.getFrame(),
                fFmpegProgress.getFps(),
                fFmpegProgress.getSize(),
                fFmpegProgress.getTimeMicros(),
                fFmpegProgress.getDup(),
                fFmpegProgress.getDrop(),
                fFmpegProgress.getBitrate(),
                fFmpegProgress.getSpeed()
        );
    }
}
