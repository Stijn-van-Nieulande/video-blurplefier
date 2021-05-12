package dev.stijn.videoblurplefier.gui;

import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.NullOutput;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.github.kokorin.jaffree.ffprobe.Stream;
import dev.stijn.videoblurplefier.processor.VideoProcessor;
import dev.stijn.videoblurplefier.processor.ffmpeg.FfmpegVideoProcessor;
import org.jetbrains.annotations.NotNull;

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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class MainGui extends JPanel
{
    private static final Path FFMPEG_BIN_PATH = new File(System.getenv("APPDATA"), "video-blurplefier/bin").toPath();

    private JLabel inputFileLabel;
    private JTextField inputFileField;
    private JButton inputFileButton;
    private JLabel outputFileLabel;
    private JTextField outputFileField;
    private JButton outputFileButton;
    private JLabel outputFilenameLabel;
    private JTextField outputFilenameField;
    private JProgressBar progressbar;
    private JTextArea logArea;
    private JButton renderButton;
    private JButton cancelButton;
    private Thread renderThread;

    public MainGui(final JFrame frame)
    {
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
        this.logArea = new JTextArea(5, 5);
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

        // Set dimensions
        this.inputFileField.setMinimumSize(new Dimension(-1, 40));
        this.outputFileField.setMinimumSize(new Dimension(-1, 40));
        this.outputFilenameField.setMinimumSize(new Dimension(-1, 40));

        this.renderButton.setMinimumSize(new Dimension(80, 100));
        this.logArea.setMinimumSize(new Dimension(-1, 100));

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
        progressPanel.add(this.logArea, constraints);
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
            frame.setIconImage(ImageIO.read(Objects.requireNonNull(this.getClass().getClassLoader().getResource("favicon.png"))));
        } catch (final Exception e) {
            e.printStackTrace();
        }

        this.logArea.setAutoscrolls(true);
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

    public static void open()
    {
        final JFrame frame = new JFrame("Video Blurplefier - 1.0.0");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(Color.WHITE);
        frame.getContentPane().add(new MainGui(frame));
        frame.pack();
        frame.setVisible(true);
    }

    // util functions
    public void initLogger()
    {
        this.logArea.append("---- Application Started Successfully, awaiting input ---- \n ");
        this.logArea.append("This tool was created by sticks#6436 and Stijn | CodingWarrior#0101");
    }

    public void loggerAppend(final String args)
    {
        this.logArea.append(String.valueOf(args));
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
        this.clearLogbox();
        this.setProgressbarText("Waiting For analyzation to finish... ");
        this.loggerAppend("--- Starting Render, Step 1/2: Analyzing file ---");

        final String pathToVideo = this.inputFileField.getText();
        final FFprobeResult probeOut = FFprobe.atPath(FFMPEG_BIN_PATH)
                .setShowStreams(true)
                .setInput(pathToVideo)
                .execute();

        int videoWidth = 0;
        int videoHeight = 0;

        for (final Stream stream : probeOut.getStreams()) {
            if (stream.getCodecType().equals(StreamType.VIDEO)) {
                videoWidth = stream.getCodedWidth();
                videoHeight = stream.getCodedHeight();
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

        final long videoDurationMilliseconds = this.getExactVideoDurationMilliseconds(inputFile.toPath());
        this.loggerAppend("\n ---  Step 2/2: Rendering file --- \n This will take awhile, grab a snack while you wait :)");
        final VideoProcessor videoProcessor = new FfmpegVideoProcessor(FFMPEG_BIN_PATH, videoWidth, videoHeight);

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
            }
        });

        this.renderThread = new Thread(() -> videoProcessor.process(inputFile, outputFile));
        this.renderThread.start();
        this.cancelButton.setEnabled(true);
    }

    private long getExactVideoDurationMilliseconds(@NotNull final Path videoInputPath)
    {
        Objects.requireNonNull(videoInputPath);

        final AtomicLong durationMillis = new AtomicLong();

        FFmpeg.atPath(FFMPEG_BIN_PATH)
                .addInput(UrlInput.fromPath(videoInputPath))
                .addOutput(new NullOutput())
                .setProgressListener(progress -> durationMillis.set(progress.getTimeMillis()))
                .execute();

        return durationMillis.get();
    }
}
