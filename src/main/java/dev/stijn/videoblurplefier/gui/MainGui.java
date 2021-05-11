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

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class MainGui extends JPanel
{
    private final File ffprobe = new File(System.getenv("APPDATA"), "video-blurplefier/bin");
    private final File ffmpeg = new File(System.getenv("APPDATA"), "video-blurplefier/bin");
    private final Path ffprobePath = this.ffprobe.toPath();
    private final Path ffmpegPath = this.ffprobe.toPath();

    private JButton renderButton;
    private JLabel inputFileLabel;
    private JTextField fileInput;
    private JLabel outputFilename;
    private JLabel outputDir;
    private JTextField outputLocation;
    private JTextField nameEntry;
    private JButton selectOutputFile;
    private JButton inputSelectButton;
    private JProgressBar progressbar;
    private JTextArea logArea;
    private JButton cancelButton;
    private Thread renderthread;

    public MainGui(final JFrame frame)
    {
        //construct components
        this.renderButton = new JButton("Render!");
        this.inputFileLabel = new JLabel("Input File");
        this.fileInput = new JTextField(5);
        this.outputFilename = new JLabel("Output File (Name)");
        this.outputDir = new JLabel("Output Location ");
        this.outputLocation = new JTextField(5);
        this.nameEntry = new JTextField(5);
        this.selectOutputFile = new JButton("Select Output...");
        this.inputSelectButton = new JButton("Select Input...");
        this.progressbar = new JProgressBar();
        this.logArea = new JTextArea(5, 5);
        this.cancelButton = new JButton("Halt Cycle");

        //set components properties
        this.renderButton.setToolTipText("Render the frames");
        this.inputFileLabel.setToolTipText("The input file (In .mp4 or something like that, must be a video file)");
        this.outputLocation.setToolTipText("The loaction you want to send the finshed file to.");
        this.nameEntry.setToolTipText("The name of the outputed file");
        this.logArea.setEditable(false);
        this.fileInput.setEditable(false);
        this.fileInput.setText("Select a file below...");
        this.outputLocation.setEditable(false);
        this.outputLocation.setText("Select a file below...");
        this.progressbar.setStringPainted(true);
        this.cancelButton.setEnabled(false);

        // set COLORS
        this.logArea.setBackground(new Color(114, 137, 218));
        this.logArea.setForeground(new Color(255, 255, 255));
        this.selectOutputFile.setBackground(new Color(114, 137, 218));
        this.selectOutputFile.setForeground(new Color(255, 255, 255));
        this.inputSelectButton.setBackground(new Color(114, 137, 218));
        this.inputSelectButton.setForeground(new Color(255, 255, 255));
        this.cancelButton.setBackground(new Color(114, 137, 218));
        this.renderButton.setBackground(new Color(114, 137, 218));
        this.renderButton.setForeground(new Color(255, 255, 255));

        this.inputFileLabel.setForeground(new Color(255, 255, 255));
        this.outputFilename.setForeground(new Color(255, 255, 255));
        this.outputDir.setForeground(new Color(255, 255, 255));


        //adjust size and set layout
        this.setPreferredSize(new Dimension(628, 371));
        this.setLayout(null);

        //add components
        this.add(this.renderButton);
        this.add(this.inputFileLabel);
        this.add(this.fileInput);
        this.add(this.outputFilename);
        this.add(this.outputDir);
        this.add(this.outputLocation);
        this.add(this.nameEntry);
        this.add(this.selectOutputFile);
        this.add(this.inputSelectButton);
        this.add(this.logArea);
        this.add(this.progressbar);
        this.add(this.cancelButton);

        //set component bounds (only needed by Absolute Positioning)
        this.renderButton.setBounds(505, 310, 110, 40);
        this.inputFileLabel.setBounds(15, 20, 160, 40);
        this.fileInput.setBounds(15, 50, 175, 20);
        this.outputFilename.setBounds(290, 30, 130, 25);
        this.outputDir.setBounds(15, 100, 100, 25);
        this.outputLocation.setBounds(15, 125, 190, 20);
        this.nameEntry.setBounds(290, 50, 170, 20);
        this.selectOutputFile.setBounds(15, 150, 120, 25);
        this.inputSelectButton.setBounds(15, 75, 115, 25);
        this.logArea.setBounds(5, 245, 490, 120);
        this.progressbar.setBounds(5, 215, 405, 25);
        this.cancelButton.setBounds(420, 215, 100, 25);
        this.logArea.setAutoscrolls(true);
        this.initLogger();
        this.setProgressbarPercentage(100);
        this.setProgressbarText("No action running.");
        this.setBackground(new Color(35, 39, 42));


        this.renderButton.addActionListener(e -> {
            if (this.fileInput.getText().equals("Select a file below...")) {
                JOptionPane.showMessageDialog(frame,
                        "Please Select an input file! (Fatal)",
                        "Render Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (this.outputLocation.getText().equals("Select a file below...")) {
                JOptionPane.showMessageDialog(frame,
                        "Please Select an output directory! (Fatal)",
                        "Render Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (this.nameEntry.getText().equals("")) {
                final int result = JOptionPane.showConfirmDialog(frame, "No File name was given, so blerp-out will be used. \n Continue with default file name?", "Render: Warning",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (result != JOptionPane.YES_OPTION) return;
            }

            this.processVideo();
        });

        this.cancelButton.addActionListener(e -> {
            final int result = JOptionPane.showConfirmDialog(frame, "WARNING! \n by stopping the render process, THE VIDEO WILL NOT BE FULLY RENDERED. Stop Process?", "Render: Warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (result != JOptionPane.YES_OPTION) return;
            renderthread.interrupt();
            this.setProgressbarText("Render Stopped.");
            this.setProgressbarPercentage(100);
            this.clearLogbox();
            this.loggerAppend("\n --- Render Stopped ---");
            this.loggerAppend("\n Warning: this video is partial. It may not even play. ");
            this.loggerAppend("\n Outputed to: " + this.getOutputLocation());
            this.loggerAppend("\n Now ready for more jobs.");
            this.cancelButton.setEnabled(false);
        });

        this.inputSelectButton.addActionListener(e -> {
            final JFileChooser fileChooser = new JFileChooser();
            final int option = fileChooser.showOpenDialog(frame);

            if (option == JFileChooser.APPROVE_OPTION) {
                final File file = fileChooser.getSelectedFile();
                this.fileInput.setText(file.getPath());
                this.loggerAppend("\n Set input file to: " + file.getPath());
            } else {
                System.out.println("[DEBUG] File Chooser was closed without any file selection, not inputting file.");
            }
        });

        this.selectOutputFile.addActionListener(e -> {
            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            final int option = fileChooser.showOpenDialog(frame);

            if (option == JFileChooser.APPROVE_OPTION) {
                final File file = fileChooser.getSelectedFile();
                this.outputLocation.setText(file.getPath());
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
        return this.fileInput.getText();
    }

    public String getOutputLocation()
    {
        return this.outputLocation.getText();
    }

    public String getFileName()
    {
        final String fileText = this.nameEntry.getText();
        if (fileText.isBlank()) return null;
        return fileText;
    }

    private void processVideo()
    {
        this.clearLogbox();
        this.setProgressbarText("Waiting For analyzation to finish... ");
        this.loggerAppend("--- Starting Render, Step 1/2: Analyzing file ---");

        System.out.println(this.ffprobe);
        final String pathToVideo = this.fileInput.getText();
        final FFprobeResult probeOut = FFprobe.atPath(this.ffprobePath)
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
        final VideoProcessor videoProcessor = new FfmpegVideoProcessor(this.ffmpegPath, videoWidth, videoHeight);

        videoProcessor.setProgressListener(progress -> {
            final int percents = (int) (100 * progress.getTimeInMilliseconds() / videoDurationMilliseconds);
            this.setProgressbarPercentage(percents);
            this.setProgressbarText("Rendering: " + percents + "% complete.");
            System.out.println(progress);
            if(percents == 100) {
                this.setProgressbarText("Render Complete.");
                this.clearLogbox();
                this.loggerAppend("--- Render Complete! ---");
                this.loggerAppend("\n Outputed to: " + this.getOutputLocation());
                this.loggerAppend("\n Now ready for more jobs.");
                this.cancelButton.setEnabled(false);
            }
        });

        renderthread = new Thread(() -> videoProcessor.process(inputFile, outputFile));
        renderthread.start();
        this.cancelButton.setEnabled(true);
    }

    private long getExactVideoDurationMilliseconds(@NotNull final Path videoInputPath)
    {
        Objects.requireNonNull(videoInputPath);

        final AtomicLong durationMillis = new AtomicLong();

        FFmpeg.atPath(this.ffmpegPath)
                .addInput(UrlInput.fromPath(videoInputPath))
                .addOutput(new NullOutput())
                .setProgressListener(progress -> durationMillis.set(progress.getTimeMillis()))
                .execute();

        return durationMillis.get();
    }
}
