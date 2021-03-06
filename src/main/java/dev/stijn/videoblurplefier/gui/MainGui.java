package dev.stijn.videoblurplefier.gui;

import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.NullOutput;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.github.kokorin.jaffree.ffprobe.Stream;
import dev.stijn.videoblurplefier.VideoBlurplefier;
import dev.stijn.videoblurplefier.binaries.BinaryManager;
import dev.stijn.videoblurplefier.gui.elements.ColorPickerButton;
import dev.stijn.videoblurplefier.gui.elements.ColorRadioButton;
import dev.stijn.videoblurplefier.processor.VideoProcessor;
import dev.stijn.videoblurplefier.processor.ffmpeg.FfmpegVideoProcessor;
import dev.stijn.videoblurplefier.util.logging.SimpleLogHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainGui extends JPanel
{
    private static final Logger LOGGER = Logger.getLogger(MainGui.class.getName());

    @NotNull
    private final VideoBlurplefier videoBlurplefier;

    private final Color colorRed = new Color(248, 51, 60);

    private final Color colorGreen = new Color(0, 255, 0);
    private final Color colorOrange = new Color(221, 164, 72);
    private final Color colorBlurple = new Color(114, 137, 218);
    private final Color colorBlurpleNew = new Color(88, 101, 242);
    private final Color colorWhite = new Color(255, 255, 255);

    private final JFrame frame;
    private Color selectedColor = this.colorBlurple;

    private JLabel inputFileLabel;
    private JTextField inputFileField;
    private JButton inputFileButton;
    private JLabel outputFileLabel;
    private JTextField outputFileField;
    private JButton outputFileButton;
    private JLabel outputFilenameLabel;
    private JTextField outputFilenameField;
    private JProgressBar progressbar;
    private JTextPane logArea;
    private JScrollPane logAreaScrollPane;
    private JButton renderButton;
    private JButton cancelButton;
    private Thread renderThread;
    private JLabel colorsLabel;
    private ButtonGroup colorsButtonGroup;
    private JRadioButton colorsButtonBlurple;
    private JRadioButton colorsButtonBlurpleNew;
    private JRadioButton colorsButtonCustom;
    private ColorPickerButton colorsButtonCustomPicker;

    public MainGui(@NotNull final VideoBlurplefier videoBlurplefier, final JFrame frame)
    {
        this.videoBlurplefier = Objects.requireNonNull(videoBlurplefier);
        this.frame = frame;

        final JPanel inputsPanel = new JPanel();
        final JPanel inputsColorsPanel = new JPanel();
        final JPanel progressPanel = new JPanel();

        final ImageIcon logo = new ImageIcon();
        try {
            logo.setImage(ImageIO.read(Objects.requireNonNull(this.getClass().getClassLoader().getResource("logo-small.png"))));
        } catch (final Exception e) {
            e.printStackTrace();
        }

        //construct components
        this.inputFileLabel = new JLabel("Input File");
        this.inputFileField = new JTextField(5);
        this.inputFileButton = new JButton("Select Input File");
        this.outputFileLabel = new JLabel("Output Location ");
        this.outputFileField = new JTextField(5);
        this.outputFileButton = new JButton("Select Output Directory");
        this.outputFilenameLabel = new JLabel("Output File Name");
        this.outputFilenameField = new JTextField(5);
        this.progressbar = new JProgressBar();
        this.logArea = new JTextPane();
        this.logAreaScrollPane = new JScrollPane(this.logArea);
        this.cancelButton = new JButton("Halt Cycle");
        this.renderButton = new JButton("Render!");
        this.colorsLabel = new JLabel("Color");
        this.colorsButtonGroup = new ButtonGroup();
        this.colorsButtonBlurple = new ColorRadioButton("Original Blurple", true, this.colorBlurple);
        this.colorsButtonBlurpleNew = new ColorRadioButton("New Blurple", this.colorBlurpleNew);
        this.colorsButtonCustom = new JRadioButton("Custom");
        final JPanel colorsDisplay = new JPanel();
        this.colorsButtonCustomPicker = new ColorPickerButton(this.frame, color -> {
            this.selectedColor = color;
            colorsDisplay.setBackground(color);
        });

        this.colorsButtonGroup.add(this.colorsButtonBlurple);
        this.colorsButtonGroup.add(this.colorsButtonBlurpleNew);
        this.colorsButtonGroup.add(this.colorsButtonCustom);

        colorsDisplay.setPreferredSize(new Dimension(40, 30));
        colorsDisplay.setBackground(this.selectedColor);

        inputsColorsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        inputsColorsPanel.add(colorsDisplay);
        inputsColorsPanel.add(this.colorsButtonBlurple);
        inputsColorsPanel.add(this.colorsButtonBlurpleNew);
        inputsColorsPanel.add(this.colorsButtonCustom);
        inputsColorsPanel.add(this.colorsButtonCustomPicker);

        final ActionListener actionListener = event -> {
            final AbstractButton button = (AbstractButton) event.getSource();
            if (button instanceof ColorRadioButton) {
                this.selectedColor = ((ColorRadioButton) button).getColor();
            } else {
                this.selectedColor = this.colorsButtonCustomPicker.getCurrentSelectedColor();
            }
            colorsDisplay.setBackground(this.selectedColor);
        };

        this.colorsButtonBlurple.addActionListener(actionListener);
        this.colorsButtonBlurpleNew.addActionListener(actionListener);
        this.colorsButtonCustom.addActionListener(actionListener);

        //set components properties
        this.renderButton.setToolTipText("Render the frames");
        this.inputFileLabel.setToolTipText("The input file (In .mp4 or something like that, must be a video file)");
        this.outputFileField.setToolTipText("The loaction you want to send the finshed file to.");
        this.outputFilenameField.setToolTipText("The name of the outputed file");
        this.logArea.setEditable(false);
        this.inputFileField.setEditable(false);
        this.inputFileField.setText("Select a file below...");
        this.outputFileField.setEditable(false);
        this.outputFileField.setText("Select a file below...");
        this.progressbar.setStringPainted(true);
        this.cancelButton.setEnabled(false);
        this.logArea.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));

        // Set dimensions
        this.inputFileField.setMinimumSize(new Dimension(-1, 40));
        this.outputFileField.setMinimumSize(new Dimension(-1, 40));
        this.outputFilenameField.setMinimumSize(new Dimension(-1, 40));
        inputsColorsPanel.setMinimumSize(new Dimension(200, 40));

        this.logAreaScrollPane.setMinimumSize(new Dimension(-1, 100));
        this.renderButton.setMinimumSize(new Dimension(80, 100));

        // Init inputs grid
        inputsPanel.setLayout(new GridBagLayout());
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.ipady = 30;
        constraints.insets = new Insets(3, 15, 3, 15);

        constraints.gridx = 0;
        constraints.gridy = 0;
        inputsPanel.add(this.inputFileLabel, constraints);
        constraints.gridy = 1;
        inputsPanel.add(this.inputFileField, constraints);
        constraints.gridy = 2;
        inputsPanel.add(this.inputFileButton, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        inputsPanel.add(this.outputFileLabel, constraints);
        constraints.gridy = 1;
        inputsPanel.add(this.outputFileField, constraints);
        constraints.gridy = 2;
        inputsPanel.add(this.outputFileButton, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        inputsPanel.add(inputsColorsPanel, constraints);
        constraints.gridy = 3;
        constraints.insets = new Insets(33, 15, 3, 15);
        inputsPanel.add(this.colorsLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 4;
        constraints.insets = new Insets(3, 15, 3, 15);
        inputsPanel.add(this.outputFilenameField, constraints);
        constraints.gridy = 3;
        constraints.insets = new Insets(33, 15, 3, 15);
        inputsPanel.add(this.outputFilenameLabel, constraints);

        // Init progress grid
        progressPanel.setLayout(new GridBagLayout());
        final Border padding = BorderFactory.createEmptyBorder(30, 12, 15, 12);
        progressPanel.setBorder(padding);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = .8;
        constraints.insets = new Insets(3, 5, 3, 5);

        progressPanel.add(this.progressbar, constraints);
        constraints.gridx = 1;
        constraints.weightx = .2;
        progressPanel.add(this.cancelButton, constraints);
        constraints.gridy = 1;
        constraints.gridx = 0;
        progressPanel.add(this.logAreaScrollPane, constraints);
        constraints.gridx = 1;
        progressPanel.add(this.renderButton, constraints);

        //adjust size and set layout
        this.setPreferredSize(new Dimension(980, 570));
        this.setLayout(new GridLayout(3, 0));

        // Stupid way to show logo....
        final JLabel logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(JLabel.CENTER);
        logoLabel.setIcon(logo);

        this.add(logoLabel);
        this.add(inputsPanel);
        this.add(progressPanel);

        try {
            frame.setIconImage(ImageIO.read(Objects.requireNonNull(this.getClass().getClassLoader().getResource("favicon-rounded.png"))));
        } catch (final Exception e) {
            e.printStackTrace();
        }

        this.initLogger();
        this.setProgressbarText("No action running.");
        this.setBackground(new Color(35, 39, 42));


        this.renderButton.addActionListener(e -> {
            if (this.inputFileField.getText().equals("Select a file below...")) {
                JOptionPane.showMessageDialog(frame,
                        "Please Select an input file!",
                        "Render Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (this.outputFileField.getText().equals("Select a file below...")) {
                JOptionPane.showMessageDialog(frame,
                        "Please Select an output directory!",
                        "Render Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (this.outputFilenameField.getText().equals("")) {
                final int result = JOptionPane.showConfirmDialog(frame, "No File name was given, so output will be used. \n Continue with default file name?", "Render: Warning",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (result != JOptionPane.YES_OPTION) return;
            }

            this.processVideo();
        });

        this.cancelButton.addActionListener(e -> {
            final int result = JOptionPane.showConfirmDialog(frame, "WARNING! by stopping the render process, THE VIDEO WILL NOT BE FULLY RENDERED. Stop Process?", "Render: Warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (result != JOptionPane.YES_OPTION) return;
            this.renderThread.interrupt();
            this.setProgressbarText("Render Stopped.");
            this.setProgressbarPercentage(100);
            this.clearLogbox();

            LOGGER.warning("Render was closed (THREAD_SIGTERM)");
            LOGGER.warning("This video is partial. It may not even play.");
            LOGGER.info("Outputted to: " + this.getOutputLocation());
            LOGGER.info("Now Ready for more jobs");

            this.cancelButton.setEnabled(false);
            this.renderButton.setText("Render!");
            this.renderButton.setEnabled(true);
        });

        this.inputFileButton.addActionListener(e -> {
            final JFileChooser fileChooser = new JFileChooser();
            final int option = fileChooser.showOpenDialog(frame);

            if (option == JFileChooser.APPROVE_OPTION) {
                final File file = fileChooser.getSelectedFile();
                this.inputFileField.setText(file.getPath());
                LOGGER.info("Set Input File to: " + this.getInputfile());
            } else {
                System.out.println("[DEBUG] File Chooser was closed without any file selection, not inputting file.");
            }
        });

        this.outputFileButton.addActionListener(e -> {
            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            final int option = fileChooser.showOpenDialog(frame);

            if (option == JFileChooser.APPROVE_OPTION) {
                final File file = fileChooser.getSelectedFile();
                this.outputFileField.setText(file.getPath());
                LOGGER.info("Set Output Directory to: " + this.getOutputLocation());
            } else {
                System.out.println("[DEBUG] File Chooser was closed without any file selection, not inputting file.");
            }
        });
    }

    public static void open(@NotNull final VideoBlurplefier videoBlurplefier)
    {
        // TODO: Make this less staticy
        final JFrame frame = new JFrame("Video Blurplefier - 2.0.1");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(Color.WHITE);
        frame.getContentPane().add(new MainGui(Objects.requireNonNull(videoBlurplefier), frame));
        frame.pack();
        frame.setVisible(true);
    }

    // util functions
    public void initLogger()
    {
        final Handler logHandler = new SimpleLogHandler(logRecord -> {
            Color levelColor = this.colorBlurple;
            Color messageColor = null;

            if (logRecord.getLevel().equals(Level.SEVERE)) {
                levelColor = this.colorRed;
                messageColor = this.colorRed;
            }
            if (logRecord.getLevel().equals(Level.WARNING)) {
                levelColor = this.colorOrange;
                messageColor = this.colorOrange;
            }

            if(logRecord.getLevel().equals(Level.FINE)) {
                levelColor = this.colorGreen;
                messageColor = this.colorGreen;
            }

            this.loggerAppend("[" + logRecord.getLevel() + "]", levelColor);
            this.loggerAppend(" " + logRecord.getMessage() + "\n", messageColor);
        });

        LOGGER.addHandler(logHandler);
        BinaryManager.LOGGER.addHandler(logHandler);

        LOGGER.info("Application Started Successfully, awaiting input");
        LOGGER.info("This tool was created by sticks#6436 and Stijn | CodingWarrior#0101");
        LOGGER.info("Tool updated by SticksDev on 5/10/2022.\n\n");
    }

    public void loggerAppend(final String message, @Nullable final Color color)
    {
        final Color finalColor = color == null ? Color.WHITE : color;
        final StyleContext styleContext = StyleContext.getDefaultStyleContext();
        AttributeSet attributeSet = styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, finalColor);

        attributeSet = styleContext.addAttribute(attributeSet, StyleConstants.FontFamily, "Lucida Console");
        attributeSet = styleContext.addAttribute(attributeSet, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        final StyledDocument styledDocument = this.logArea.getStyledDocument();
        final int length = this.logArea.getDocument().getLength();

        try {
            styledDocument.insertString(length, message, attributeSet);
        } catch (final BadLocationException e) {
            e.printStackTrace();
        }

        this.logAreaScrollPane.getVerticalScrollBar().setValue(this.logAreaScrollPane.getVerticalScrollBar().getMaximum());
    }

    public void loggerAppend(final String message)
    {
        this.loggerAppend(message, null);
    }

    public void clearLogbox()
    {
        this.logArea.setText("");
    }

    public void setProgressbarPercentage(final Integer args)
    {
        this.progressbar.setValue(args);
    }

    public void setProgressbarText(final String args)
    {
        this.progressbar.setString(String.valueOf(args));
    }

    public String getInputfile()
    {
        return this.inputFileField.getText();
    }

    public String getOutputLocation()
    {
        return this.outputFileField.getText();
    }

    public String getFileName()
    {
        final String fileText = this.outputFilenameField.getText();
        if (fileText.isBlank()) return null;
        return fileText;
    }

    private void processVideo()
    {
        this.renderButton.setText("Rendering...");
        this.renderButton.setEnabled(false);
        this.clearLogbox();
        this.setProgressbarText("Waiting For analyzation to finish... ");
        LOGGER.info("Starting Render, Step 1/2: Analyzing file");

        @Nullable final File binPath = this.videoBlurplefier.getBinaryManager().getFfmpegBinDirectory();

        if (binPath == null || !binPath.exists()) {
            JOptionPane.showMessageDialog(this.frame,
                    "The FFmpeg libraries are not installed!\nIt may be that the installation is still in progress\nor that your OS is not yet supported.",
                    "FFmpeg not found!",
                    JOptionPane.ERROR_MESSAGE);
            LOGGER.severe("Fatal: FFmpeg libraries are not installed. Halting. (-20)");
            return;
        }

        final String pathToVideo = this.inputFileField.getText();
        final FFprobeResult probeOut = FFprobe.atPath(binPath.toPath())
                .setShowStreams(true)
                .setInput(pathToVideo)
                .execute();

        int videoWidth = 0;
        int videoHeight = 0;

        for (final Stream stream : probeOut.getStreams()) {
            if (stream.getCodecType().equals(StreamType.VIDEO)) {
                videoWidth = stream.getCodedWidth();
                videoHeight = stream.getCodedHeight();
                System.out.println("The following is video information, these are not errors, just info.");
                System.out.println("--- Video Information (DEBUG) ---");
                System.out.println("CodecName: " + stream.getCodecName());
                System.out.println("CodecTagString: " + stream.getCodecTagString());
                System.out.println("CodecTag: " + stream.getCodecTag());
                System.out.println("CodecLongName: " + stream.getCodecLongName());
                System.out.println("CodecType: " + stream.getCodecType());
                break;
            }
            LOGGER.info("[FFPROBE] VideoType: " + stream.getCodecType());
            LOGGER.info("[FFPROBE] Video Duration: (In Seconds) " + stream.getDuration());
        }
        this.setProgressbarText("Waiting For render to start... ");

        final File inputFile = new File(pathToVideo);
        final String fileExtension = "mp4"; // TODO: Maybe this can be done without hardcoded
        final String fileName = (this.getFileName() == null ? "output" : this.getFileName()) + "." + fileExtension;
        final File outputFile = new File(this.getOutputLocation(), fileName);

        final long videoDurationMilliseconds = this.getExactVideoDurationMilliseconds(binPath.toPath(), inputFile.toPath());
        LOGGER.info("Starting Render Thread...");
        final VideoProcessor videoProcessor = new FfmpegVideoProcessor(binPath.toPath(), this.selectedColor, videoWidth, videoHeight);

        videoProcessor.setProgressListener(progress -> {
            final int percents = (int) (100 * progress.getTimeInMilliseconds() / videoDurationMilliseconds);
            this.setProgressbarPercentage(percents);
            this.setProgressbarText("Rendering: " + percents + "% complete.");
            LOGGER.info("[RENDER] " + percents + "% complete.");

            if (percents == 100) {
                this.setProgressbarText("Render Complete.");
                this.clearLogbox();

                LOGGER.info("Render Complete!");
                LOGGER.info("Outputted to: " + this.getOutputLocation());
                LOGGER.info("Now Ready for more jobs");

                this.cancelButton.setEnabled(false);
                this.renderButton.setText("Render!");
                this.renderButton.setEnabled(true);

                try {
                    this.videoBlurplefier.getTrayManager().displayTray("Your Render Has Completed!", TrayIcon.MessageType.INFO);
                } catch (AWTException | MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            this.videoBlurplefier.getTrayManager().displayTray("Render started. You can minimize the window, and a notification will appear when done!", TrayIcon.MessageType.INFO);
        } catch (AWTException | MalformedURLException e) {
            e.printStackTrace();
        }
        this.renderThread = new Thread(() -> videoProcessor.process(inputFile, outputFile));
        this.renderThread.start();
        LOGGER.info("Render Thread started.");
        this.cancelButton.setEnabled(true);
    }

    private long getExactVideoDurationMilliseconds(@NotNull final Path binPath, @NotNull final Path videoInputPath)
    {
        Objects.requireNonNull(binPath);
        Objects.requireNonNull(videoInputPath);

        final AtomicLong durationMillis = new AtomicLong();

        FFmpeg.atPath(binPath)
                .addInput(UrlInput.fromPath(videoInputPath))
                .addOutput(new NullOutput())
                .setProgressListener(progress -> durationMillis.set(progress.getTimeMillis()))
                .execute();

        return durationMillis.get();
    }
}
