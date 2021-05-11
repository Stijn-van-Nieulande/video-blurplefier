package dev.stijn.videoblurplefier.gui;

import mdlaf.shadows.DropShadowBorder;
import mdlaf.themes.AbstractMaterialTheme;
import mdlaf.utils.MaterialBorders;
import mdlaf.utils.MaterialColors;
import mdlaf.utils.MaterialImageFactory;
import mdlaf.utils.icons.MaterialIconFont;

import javax.swing.BorderFactory;
import javax.swing.UIDefaults;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import java.awt.Color;

public class BlurpleDarkTheme extends AbstractMaterialTheme
{
    public BlurpleDarkTheme()
    {
    }

    protected void installIcons()
    {
        super.installIcons();
        this.selectedCheckBoxIconSelectionRowTable = MaterialImageFactory.getInstance().getImage(MaterialIconFont.CHECK_BOX, MaterialColors.WHITE);
        this.unselectedCheckBoxIconSelectionRowTable = MaterialImageFactory.getInstance().getImage(MaterialIconFont.CHECK_BOX_OUTLINE_BLANK, MaterialColors.WHITE);
        this.selectedCheckBoxIconTable = MaterialImageFactory.getInstance().getImage(MaterialIconFont.CHECK_BOX, this.highlightBackgroundPrimary);
        this.unselectedCheckBoxIconTable = MaterialImageFactory.getInstance().getImage(MaterialIconFont.CHECK_BOX_OUTLINE_BLANK, this.highlightBackgroundPrimary);
    }

    protected void installBorders()
    {
        super.installBorders();
        this.borderMenuBar = new BorderUIResource(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(114, 137, 218)));
        this.borderPopupMenu = new BorderUIResource(BorderFactory.createLineBorder(this.backgroundPrimary));
        this.borderSpinner = new BorderUIResource(BorderFactory.createLineBorder(this.backgroundTextField));
        this.borderSlider = new BorderUIResource(BorderFactory.createCompoundBorder(this.borderSpinner, BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        this.cellBorderTableHeader = new BorderUIResource(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(this.backgroundTableHeader), BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        this.borderToolBar = this.borderSpinner;
        this.borderDialogRootPane = MaterialBorders.LIGHT_SHADOW_BORDER;
        this.borderProgressBar = this.borderSpinner;
        this.borderTable = this.borderSpinner;
        this.borderTableHeader = new BorderUIResource(new DropShadowBorder(this.backgroundPrimary, 5, 3, 0.4F, 12, true, true, true, true));
        super.borderTitledBorder = new BorderUIResource(BorderFactory.createLineBorder(MaterialColors.WHITE));
        super.titleColorTaskPane = MaterialColors.BLACK;
    }

    protected void installColor()
    {
        this.backgroundPrimary = new ColorUIResource(35, 39, 42);
        this.highlightBackgroundPrimary = new ColorUIResource(114, 137, 218);
        this.textColor = new ColorUIResource(255, 255, 255);
        this.disableTextColor = new ColorUIResource(170, 170, 170);
        this.buttonBackgroundColor = new ColorUIResource(45, 48, 56);
        this.buttonBackgroundColorMouseHover = new ColorUIResource(81, 86, 101);
        this.buttonDefaultBackgroundColorMouseHover = new ColorUIResource(78, 93, 148);
        this.buttonDefaultBackgroundColor = new ColorUIResource(114, 137, 218);
        this.buttonDisabledBackground = new ColorUIResource(66, 69, 76);
        this.buttonFocusColor = this.buttonDefaultBackgroundColor;
        this.buttonDefaultFocusColor = MaterialColors.WHITE;
        this.buttonBorderColor = MaterialColors.WHITE;
        this.buttonColorHighlight = this.buttonBackgroundColorMouseHover;
        this.selectedInDropDownBackgroundComboBox = new ColorUIResource(114, 137, 218);
        this.selectedForegroundComboBox = MaterialColors.BLACK;
        this.menuBackground = new ColorUIResource(59, 62, 69);
        this.menuBackgroundMouseHover = new ColorUIResource(114, 137, 218);
        this.trackColorScrollBar = new ColorUIResource(81, 86, 101);
        this.thumbColorScrollBar = new ColorUIResource(155, 155, 155);
        this.trackColorSlider = new ColorUIResource(119, 119, 119);
        this.haloColorSlider = MaterialColors.bleach(new Color(114, 137, 218), 0.2F);
        this.mouseHoverButtonColorSpinner = this.backgroundPrimary;
        this.highlightColorTabbedPane = new ColorUIResource(45, 48, 56);
        this.borderHighlightColorTabbedPane = new ColorUIResource(45, 48, 56);
        this.focusColorLineTabbedPane = new ColorUIResource(114, 137, 218);
        this.disableColorTabTabbedPane = new ColorUIResource(170, 170, 170);
        this.backgroundTable = new ColorUIResource(45, 48, 56);
        this.backgroundTableHeader = new ColorUIResource(114, 137, 218);
        this.selectionBackgroundTable = new ColorUIResource(126, 132, 153);
        this.gridColorTable = new ColorUIResource(151, 151, 151);
        this.alternateRowBackgroundTable = new ColorUIResource(59, 62, 69);
        this.backgroundTextField = new ColorUIResource(81, 86, 101);
        this.inactiveForegroundTextField = MaterialColors.WHITE;
        this.inactiveBackgroundTextField = new ColorUIResource(81, 86, 101);
        this.selectionBackgroundTextField = new ColorUIResource(114, 137, 218);
        super.disabledBackgroudnTextField = new ColorUIResource(94, 94, 94);
        super.disabledForegroundTextField = new ColorUIResource(170, 170, 170);
        this.selectionForegroundTextField = MaterialColors.BLACK;
        this.inactiveColorLineTextField = MaterialColors.WHITE;
        this.activeColorLineTextField = new ColorUIResource(114, 137, 218);
        this.titleBackgroundGradientStartTaskPane = MaterialColors.GRAY_300;
        this.titleBackgroundGradientEndTaskPane = MaterialColors.GRAY_500;
        this.titleOverTaskPane = new ColorUIResource(114, 137, 218);
        this.specialTitleOverTaskPane = MaterialColors.WHITE;
        this.selectionBackgroundList = new ColorUIResource(114, 137, 218);
        this.selectionForegroundList = MaterialColors.BLACK;
        this.backgroundProgressBar = new ColorUIResource(66, 69, 76);
        this.foregroundProgressBar = new ColorUIResource(114, 137, 218);
        this.withoutIconSelectedForegoundToggleButton = MaterialColors.BLACK;
        this.withoutIconForegroundToggleButton = MaterialColors.WHITE;
        this.colorDividierSplitPane = MaterialColors.COSMO_DARK_GRAY;
        this.colorDividierFocusSplitPane = new ColorUIResource(114, 137, 218);
        super.backgroundSeparator = MaterialColors.GRAY_300;
        super.foregroundSeparator = MaterialColors.GRAY_300;
        super.backgroundToolTip = this.backgroundPrimary;
    }

    protected void installDefaultColor()
    {
        super.installDefaultColor();
    }

    public void installUIDefault(final UIDefaults table)
    {
        super.installUIDefault(table);
        table.put("Menu.selectionForeground", MaterialColors.BLACK);
        table.put("MenuItem.selectionForeground", MaterialColors.BLACK);
    }

    public String getName()
    {
        return "Blurple Dark";
    }

    public int getArcButton()
    {
        return 0;
    }

    public int getArchBorderComboBox()
    {
        return 0;
    }
}
