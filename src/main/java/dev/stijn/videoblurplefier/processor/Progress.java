package dev.stijn.videoblurplefier.processor;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Progress
{
    private final Long frame;
    private final Double fps;
    private final Long size;
    private final Long timeInMicroseconds;
    private final Long dup;
    private final Long drop;
    private final Double bitrate;
    private final Double speed;

    public Progress(final Long frame, final Double fps, final Long size, final Long timeInMicroseconds, final Long dup, final Long drop, final Double bitrate, final Double speed)
    {
        this.frame = frame;
        this.fps = fps;
        this.size = size;
        this.timeInMicroseconds = timeInMicroseconds;
        this.dup = dup;
        this.drop = drop;
        this.bitrate = bitrate;
        this.speed = speed;
    }

    public Long getFrame()
    {
        return this.frame;
    }

    public Double getFps()
    {
        return this.fps;
    }

    public Long getSize()
    {
        return this.size;
    }

    public Long getTimeInMilliseconds()
    {
        return this.getTime(TimeUnit.MILLISECONDS);
    }

    public Long getTimeInMicroseconds()
    {
        return this.timeInMicroseconds;
    }

    public Long getTime(@NotNull final TimeUnit timeUnit)
    {
        Objects.requireNonNull(timeUnit, "time unit cannot be null");
        return this.timeInMicroseconds == null ? null : timeUnit.convert(this.timeInMicroseconds, TimeUnit.MICROSECONDS);
    }

    public Long getDup()
    {
        return this.dup;
    }

    public Long getDrop()
    {
        return this.drop;
    }

    public Double getBitrate()
    {
        return this.bitrate;
    }

    public Double getSpeed()
    {
        return this.speed;
    }

    @Override
    public String toString()
    {
        return "Progress{" +
                "frame=" + this.frame
                + ", fps=" + this.fps
                + ", size=" + this.size
                + ", timeMicros=" + this.timeInMicroseconds
                + ", dup=" + this.dup
                + ", drop=" + this.drop
                + ", bitrate=" + this.bitrate
                + ", speed=" + this.speed
                + '}';
    }
}
