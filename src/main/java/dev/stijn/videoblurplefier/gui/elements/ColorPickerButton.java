package dev.stijn.videoblurplefier.gui.elements;

import com.bric.colorpicker.ColorPicker;
import org.jetbrains.annotations.Nullable;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Frame;
import java.util.function.Consumer;

public class ColorPickerButton extends JButton
{
    private Color currentSelectedColor = new Color(114, 137, 218);

    public ColorPickerButton(final Frame frame, @Nullable final Consumer<Color> colorChangeListener)
    {
        super("Choose Color");

        this.addActionListener(event -> {
            final JDialog dialog = new JDialog(frame, "Choose a color");

            final ColorPicker colorPicker = new ColorPicker(true, false);
            colorPicker.setColor(this.currentSelectedColor);
            colorPicker.addColorListener(colorModel -> {
                this.setCurrentSelectedColor(colorModel.getColor());
                if (colorChangeListener != null) colorChangeListener.accept(colorModel.getColor());
            });
            colorPicker.setBorder(new EmptyBorder(10, 10, 10, 10));

            dialog.add(colorPicker);
            dialog.setSize(640, 410);
            dialog.setVisible(true);
        });
    }

    public Color getCurrentSelectedColor()
    {
        return this.currentSelectedColor;
    }

    public void setCurrentSelectedColor(final Color currentSelectedColor)
    {
        this.currentSelectedColor = currentSelectedColor;
    }
}
