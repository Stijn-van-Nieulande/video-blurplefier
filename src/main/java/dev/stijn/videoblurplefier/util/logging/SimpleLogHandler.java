package dev.stijn.videoblurplefier.util.logging;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class SimpleLogHandler extends Handler
{
    @NotNull
    private final Consumer<LogRecord> logRecordConsumer;

    public SimpleLogHandler(@NotNull final Consumer<LogRecord> logRecordConsumer)
    {
        this.logRecordConsumer = Objects.requireNonNull(logRecordConsumer);
    }

    @Override
    public void publish(final LogRecord record)
    {
        this.logRecordConsumer.accept(record);
    }

    @Override
    public void flush()
    {
    }

    @Override
    public void close() throws SecurityException
    {
    }
}
