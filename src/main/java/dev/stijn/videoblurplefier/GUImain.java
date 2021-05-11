package dev.stijn.videoblurplefier;

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

public class GUImain extends JPanel
{
    private final File ffprobe = new File(System.getenv("APPDATA"), "video-blurplefier/bin");
    private final File ffmpeg = new File(System.getenv("APPDATA"), "video-blurplefier/bin");
    private final Path ffprobepath = this.ffprobe.toPath();
    private final Path ffmpegpath = this.ffprobe.toPath();

    private JButton renderButton;
    private JLabel inputfilelabel;
    private JTextField filein;
    private JLabel outputfilename;
    private JLabel outputdir;
    private JTextField outputloc;
    private JTextField nameentry;
    private JButton selectoutputfile;
    private JButton inputselectbttn;
    private JProgressBar progressbar;
    private JTextArea logarea;
    private JButton cancelBttn;
    private Integer videoW;
    private Integer videoH;

    public GUImain(final JFrame frame)
    {
        //construct components
        this.renderButton = new JButton("Render!");
        this.inputfilelabel = new JLabel("Input File");
        this.filein = new JTextField(5);
        this.outputfilename = new JLabel("Output File (Name)");
        this.outputdir = new JLabel("Output Location ");
        this.outputloc = new JTextField(5);
        this.nameentry = new JTextField(5);
        this.selectoutputfile = new JButton("Select Output...");
        this.inputselectbttn = new JButton("Select Input...");
        this.progressbar = new JProgressBar();
        this.logarea = new JTextArea(5, 5);
        this.cancelBttn = new JButton("Halt Cycle");

        //set components properties
        this.renderButton.setToolTipText("Render the frames");
        this.inputfilelabel.setToolTipText("The input file (In .mp4 or something like that, must be a video file)");
        this.outputloc.setToolTipText("The loaction you want to send the finshed file to.");
        this.nameentry.setToolTipText("The name of the outputed file");
        this.logarea.setEditable(false);
        this.filein.setEditable(false);
        this.filein.setText("Select a file below...");
        this.outputloc.setEditable(false);
        this.outputloc.setText("Select a file below...");
        this.progressbar.setStringPainted(true);
        this.cancelBttn.setEnabled(false);

        // set COLORS
        this.logarea.setBackground(new Color(114, 137, 218));
        this.logarea.setForeground(new Color(255, 255, 255));
        this.selectoutputfile.setBackground(new Color(114, 137, 218));
        this.selectoutputfile.setForeground(new Color(255, 255, 255));
        this.inputselectbttn.setBackground(new Color(114, 137, 218));
        this.inputselectbttn.setForeground(new Color(255, 255, 255));
        this.cancelBttn.setBackground(new Color(114, 137, 218));
        this.renderButton.setBackground(new Color(114, 137, 218));
        this.renderButton.setForeground(new Color(255, 255, 255));

        this.inputfilelabel.setForeground(new Color(255, 255, 255));
        this.outputfilename.setForeground(new Color(255, 255, 255));
        this.outputdir.setForeground(new Color(255, 255, 255));


        //adjust size and set layout
        this.setPreferredSize(new Dimension(628, 371));
        this.setLayout(null);

        //add components
        this.add(this.renderButton);
        this.add(this.inputfilelabel);
        this.add(this.filein);
        this.add(this.outputfilename);
        this.add(this.outputdir);
        this.add(this.outputloc);
        this.add(this.nameentry);
        this.add(this.selectoutputfile);
        this.add(this.inputselectbttn);
        this.add(this.logarea);
        this.add(this.progressbar);
        this.add(this.cancelBttn);

        //set component bounds (only needed by Absolute Positioning)
        this.renderButton.setBounds(505, 310, 110, 40);
        this.inputfilelabel.setBounds(15, 20, 160, 40);
        this.filein.setBounds(15, 50, 175, 20);
        this.outputfilename.setBounds(290, 30, 130, 25);
        this.outputdir.setBounds(15, 100, 100, 25);
        this.outputloc.setBounds(15, 125, 190, 20);
        this.nameentry.setBounds(290, 50, 170, 20);
        this.selectoutputfile.setBounds(15, 150, 120, 25);
        this.inputselectbttn.setBounds(15, 75, 115, 25);
        this.logarea.setBounds(5, 245, 490, 120);
        this.progressbar.setBounds(5, 215, 405, 25);
        this.cancelBttn.setBounds(420, 215, 100, 25);
        this.logarea.setAutoscrolls(true);
        this.initlogger();
        this.setProgressbarPercentage(100);
        this.setProgressbarText("No action running.");
        this.setBackground(new Color(35, 39, 42));


        this.renderButton.addActionListener(e -> {
            if (GUImain.this.filein.getText().equals("Select a file below...")) {
                JOptionPane.showMessageDialog(frame,
                        "Please Select an input file! (Fatal)",
                        "Render Error",
                        JOptionPane.ERROR_MESSAGE);
            } else if (GUImain.this.outputloc.getText().equals("Select a file below...")) {
                JOptionPane.showMessageDialog(frame,
                        "Please Select an output directory! (Fatal)",
                        "Render Error",
                        JOptionPane.ERROR_MESSAGE);
            } else if (GUImain.this.nameentry.getText().equals("")) {
                final int result = JOptionPane.showConfirmDialog(frame, "No File name was given, so blerp-out will be used. \n Continue with default file name?", "Render: Warning",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    GUImain.this.clearLogbox();
                    GUImain.this.setProgressbarText("Waiting For analyzation to finish... ");
                    GUImain.this.loggerAppend("--- Starting Render, Step 1/2: Analyzing file ---");

                    System.out.println(GUImain.this.ffprobe);
                    final String pathToVideo = GUImain.this.filein.getText();
                    final FFprobeResult probeout = FFprobe.atPath(GUImain.this.ffprobepath)
                            .setShowStreams(true)
                            .setInput(pathToVideo)
                            .execute();
                    for (final Stream stream : probeout.getStreams()) {
                        GUImain.this.loggerAppend("\n type: " + stream.getCodecType()
                                + "\n duration: " + stream.getDuration() + " seconds");
                        System.out.println("\n type: " + stream.getCodecType()
                                + "\n duration: " + stream.getDuration() + " seconds");
                    }


                    GUImain.this.loggerAppend("\n ---  Step 2/2: Rendering file --- \n This will take awhile, grab a snack while you wait :)");
                    final VideoProcessor videoProcessor = new FfmpegVideoProcessor(GUImain.this.ffmpegpath, 1020, 720);
                    videoProcessor.setProgressListener(System.out::println);
                } else if (result == JOptionPane.NO_OPTION) {
                    return;
                } else {
                    System.out.println("[DEBUG] Render Window was closed without any button selection, stopping render...");
                }
            }
            // call render here
        });

        this.inputselectbttn.addActionListener(e -> {
            final JFileChooser fileChooser = new JFileChooser();
            final int option = fileChooser.showOpenDialog(frame);

            if (option == JFileChooser.APPROVE_OPTION) {
                final File file = fileChooser.getSelectedFile();
                GUImain.this.filein.setText(file.getPath());
                GUImain.this.loggerAppend("\n Set input file to: " + file.getPath());
            } else {
                System.out.println("[DEBUG] File Chooser was closed without any file selection, not inputting file.");
            }
        });

        this.selectoutputfile.addActionListener(e -> {
            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            final int option = fileChooser.showOpenDialog(frame);

            if (option == JFileChooser.APPROVE_OPTION) {
                final File file = fileChooser.getSelectedFile();
                GUImain.this.outputloc.setText(file.getPath());
                GUImain.this.loggerAppend("\n Set output directory to: " + file.getPath());
            } else {
                System.out.println("[DEBUG] File Chooser was closed without any file selection, not inputting file.");
            }
        });
    }


    public static void main(final String[] args)
    {
        final JFrame frame = new JFrame("Video Blurplefier - 1.0.0");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(Color.WHITE);
        frame.getContentPane().add(new GUImain(frame));
        frame.pack();
        frame.setVisible(true);
    }

    public static void invoke()
    {
        main(null);
    }

    // util functions
    public void initlogger()
    {
        this.logarea.append("---- Application Started Successfully, awaiting input ---- \n ");
        this.logarea.append("This tool was created by sticks#6436 and Stijn | CodingWarrior#0101");
    }

    public void loggerAppend(final String args)
    {
        this.logarea.append(String.valueOf(args));
    }

    public void clearLogbox()
    {
        this.logarea.setText("");
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
        return this.filein.getText();
    }

    public String getOutputLocation()
    {
        return this.outputloc.getText();
    }

    public String getfileName()
    {
        final String filetext = this.nameentry.getText();
        if (filetext.length() == 0) {
            return null;
        }
        return filetext;
    }
}
