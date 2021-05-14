package dev.stijn.videoblurplefier.gui.elements;

import org.jetbrains.annotations.NotNull;

import javax.swing.JRadioButton;
import java.awt.Color;
import java.util.Objects;

public class ColorRadioButton extends JRadioButton
{
    @NotNull
    private final Color color;

    public ColorRadioButton(final String text, @NotNull final Color color)
    {
        this(text, false, color);
    }

    public ColorRadioButton(final String text, final boolean selected, @NotNull final Color color)
    {
        super(text, selected);
        this.color = Objects.requireNonNull(color);
    }

    public @NotNull Color getColor()
    {
        return this.color;
    }
}
