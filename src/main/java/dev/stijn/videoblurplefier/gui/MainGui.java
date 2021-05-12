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
import dev.stijn.videoblurplefier.processor.VideoProcessor;
import dev.stijn.videoblurplefier.processor.ffmpeg.FfmpegVideoProcessor;
import dev.stijn.videoblurplefier.util.logging.SimpleLogHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.TrayIcon;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

public class MainGui extends JPanel
{
    @NotNull
    private final VideoBlurplefier videoBlurplefier;

    private final JFrame frame;
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

    public MainGui(@NotNull final VideoBlurplefier videoBlurplefier, final JFrame frame)
    {
        this.videoBlurplefier = Objects.requireNonNull(videoBlurplefier);
        this.frame = frame;

        final JPanel inputsPanel = new JPanel();
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

        constraints.gridy = 4;
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
                        "Please Select an input file! (Fatal)",
                        "Render Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (this.outputFileField.getText().equals("Select a file below...")) {
                JOptionPane.showMessageDialog(frame,
                        "Please Select an output directory! (Fatal)",
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
            this.loggerAppend("\n --- Render Stopped ---");
            this.loggerAppend("\n Warning: this video is partial. It may not even play. ");
            this.loggerAppend("\n Outputed to: " + this.getOutputLocation());
            this.loggerAppend("\n Now ready for more jobs.");
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
                this.loggerAppend("\n Set input file to: " + file.getPath());
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
                this.loggerAppend("\n Set output directory to: " + file.getPath());
            } else {
                System.out.println("[DEBUG] File Chooser was closed without any file selection, not inputting file.");
            }
        });
    }

    public static void open(@NotNull final VideoBlurplefier videoBlurplefier)
    {
        // TODO: Make this less staticy
        final JFrame frame = new JFrame("Video Blurplefier - 1.0.0");
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
        // this.logArea.getDocument().addDocumentListener(new SimpleDocumentUpdateListener(documentEvent ->
        //         this.logAreaScrollPane.getVerticalScrollBar().setValue(this.logAreaScrollPane.getVerticalScrollBar().getMaximum())
        // ));

        this.loggerAppend("---- Application Started Successfully, awaiting input ----\n");
        this.loggerAppend("This tool was created by sticks#6436 and Stijn | CodingWarrior#0101\n\n\n");

        final Color colorRed = new Color(248, 51, 60);
        final Color colorOrange = new Color(221, 164, 72);
        final Color colorBlurple = new Color(114, 137, 218);

        BinaryManager.LOGGER.addHandler(new SimpleLogHandler(logRecord -> {
            Color levelColor = colorBlurple;
            Color messageColor = null;

            if (logRecord.getLevel().equals(Level.SEVERE)) {
                levelColor = colorRed;
                messageColor = colorRed;
            }
            if (logRecord.getLevel().equals(Level.WARNING)) {
                levelColor = colorOrange;
                messageColor = colorOrange;
            }

            this.loggerAppend("[" + logRecord.getLevel() + "]", levelColor);
            this.loggerAppend(" " + logRecord.getMessage() + "\n", messageColor);
        }));
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
        @Nullable final File binPath = this.videoBlurplefier.getBinaryManager().getFfmpegBinDirectory();

        if (binPath == null || !binPath.exists()) {
            JOptionPane.showMessageDialog(this.frame,
                    "The FFmpeg libraries are not installed!\nIt may be that the installation is still in progress\nor that your OS is not yet supported.",
                    "FFmpeg not found!",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        this.renderButton.setText("Rendering...");
        this.renderButton.setEnabled(false);
        this.clearLogbox();
        this.setProgressbarText("Waiting For analyzation to finish... ");
        this.loggerAppend("--- Starting Render, Step 1/2: Analyzing file ---");

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
                System.out.println("--- DEBUG ---");
                System.out.println("CodecName: " + stream.getCodecName());
                System.out.println("CodecTagString: " + stream.getCodecTagString());
                System.out.println("CodecTag: " + stream.getCodecTag());
                System.out.println("CodecLongName: " + stream.getCodecLongName());
                System.out.println("CodecType: " + stream.getCodecType());
                break;
            }
            this.loggerAppend("\n type: " + stream.getCodecType()
                    + "\n duration: " + stream.getDuration() + " seconds");
            System.out.println("\n type: " + stream.getCodecType()
                    + "\n duration: " + stream.getDuration() + " seconds");
        }
        this.setProgressbarText("Waiting For render to start... ");

        final File inputFile = new File(pathToVideo);
        final String fileExtension = "mp4"; // TODO: Maybe this can be done without hardcoded
        final String fileName = (this.getFileName() == null ? "output" : this.getFileName()) + "." + fileExtension;
        final File outputFile = new File(this.getOutputLocation(), fileName);

        final long videoDurationMilliseconds = this.getExactVideoDurationMilliseconds(binPath.toPath(), inputFile.toPath());
        this.loggerAppend("\n ---  Step 2/2: Rendering file --- \n This will take awhile, grab a snack while you wait :)");
        final VideoProcessor videoProcessor = new FfmpegVideoProcessor(binPath.toPath(), videoWidth, videoHeight);

        videoProcessor.setProgressListener(progress -> {
            final int percents = (int) (100 * progress.getTimeInMilliseconds() / videoDurationMilliseconds);
            this.setProgressbarPercentage(percents);
            this.setProgressbarText("Rendering: " + percents + "% complete.");
            System.out.println(progress);
            if (percents == 100) {
                this.setProgressbarText("Render Complete.");
                this.clearLogbox();
                this.loggerAppend("--- Render Complete! ---");
                this.loggerAppend("\n Outputed to: " + this.getOutputLocation());
                this.loggerAppend("\n Now ready for more jobs.");
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
