package dev.stijn.videoblurplefier.gui;

import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.github.kokorin.jaffree.ffprobe.Stream;
import dev.stijn.videoblurplefier.processor.VideoProcessor;
import dev.stijn.videoblurplefier.processor.ffmpeg.FfmpegVideoProcessor;

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
    private Integer videoWidth;
    private Integer videoHeight;

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
        this.initlogger();
        this.setProgressbarPercentage(100);
        this.setProgressbarText("No action running.");
        this.setBackground(new Color(35, 39, 42));


        this.renderButton.addActionListener(e -> {
            if (MainGui.this.fileInput.getText().equals("Select a file below...")) {
                JOptionPane.showMessageDialog(frame,
                        "Please Select an input file! (Fatal)",
                        "Render Error",
                        JOptionPane.ERROR_MESSAGE);
            } else if (MainGui.this.outputLocation.getText().equals("Select a file below...")) {
                JOptionPane.showMessageDialog(frame,
                        "Please Select an output directory! (Fatal)",
                        "Render Error",
                        JOptionPane.ERROR_MESSAGE);
            } else if (MainGui.this.nameEntry.getText().equals("")) {
                final int result = JOptionPane.showConfirmDialog(frame, "No File name was given, so blerp-out will be used. \n Continue with default file name?", "Render: Warning",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    MainGui.this.clearLogbox();
                    MainGui.this.setProgressbarText("Waiting For analyzation to finish... ");
                    MainGui.this.loggerAppend("--- Starting Render, Step 1/2: Analyzing file ---");

                    System.out.println(MainGui.this.ffprobe);
                    final String pathToVideo = MainGui.this.fileInput.getText();
                    final FFprobeResult probeout = FFprobe.atPath(MainGui.this.ffprobePath)
                            .setShowStreams(true)
                            .setInput(pathToVideo)
                            .execute();
                    for (final Stream stream : probeout.getStreams()) {
                        MainGui.this.loggerAppend("\n type: " + stream.getCodecType()
                                + "\n duration: " + stream.getDuration() + " seconds");
                        System.out.println("\n type: " + stream.getCodecType()
                                + "\n duration: " + stream.getDuration() + " seconds");
                    }


                    MainGui.this.loggerAppend("\n ---  Step 2/2: Rendering file --- \n This will take awhile, grab a snack while you wait :)");
                    final VideoProcessor videoProcessor = new FfmpegVideoProcessor(MainGui.this.ffmpegPath, 1020, 720);
                    videoProcessor.setProgressListener(System.out::println);
                } else if (result == JOptionPane.NO_OPTION) {
                    return;
                } else {
                    System.out.println("[DEBUG] Render Window was closed without any button selection, stopping render...");
                }
            }
            // call render here
        });

        this.inputSelectButton.addActionListener(e -> {
            final JFileChooser fileChooser = new JFileChooser();
            final int option = fileChooser.showOpenDialog(frame);

            if (option == JFileChooser.APPROVE_OPTION) {
                final File file = fileChooser.getSelectedFile();
                MainGui.this.fileInput.setText(file.getPath());
                MainGui.this.loggerAppend("\n Set input file to: " + file.getPath());
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
                MainGui.this.outputLocation.setText(file.getPath());
                MainGui.this.loggerAppend("\n Set output directory to: " + file.getPath());
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
    public void initlogger()
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

    public String getfileName()
    {
        final String filetext = this.nameEntry.getText();
        if (filetext.length() == 0) {
            return null;
        }
        return filetext;
    }
}
