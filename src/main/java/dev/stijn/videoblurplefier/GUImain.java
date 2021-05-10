package dev.stijn.videoblurplefier;

import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.github.kokorin.jaffree.ffprobe.Stream;
import dev.stijn.videoblurplefier.processor.VideoProcessor;
import dev.stijn.videoblurplefier.processor.ffmpeg.FfmpegVideoProcessor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import javax.swing.*;

public class GUImain extends JPanel {
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

    public GUImain(final JFrame frame) {
        //construct components
        renderButton = new JButton ("Render!");
        inputfilelabel = new JLabel ("Input File");
        filein = new JTextField (5);
        outputfilename = new JLabel ("Output File (Name)");
        outputdir = new JLabel ("Output Location ");
        outputloc = new JTextField (5);
        nameentry = new JTextField (5);
        selectoutputfile = new JButton ("Select Output...");
        inputselectbttn = new JButton ("Select Input...");
        progressbar = new JProgressBar();
        logarea = new JTextArea (5, 5);
        cancelBttn = new JButton ("Halt Cycle");

        //set components properties
        renderButton.setToolTipText ("Render the frames");
        inputfilelabel.setToolTipText ("The input file (In .mp4 or something like that, must be a video file)");
        outputloc.setToolTipText ("The loaction you want to send the finshed file to.");
        nameentry.setToolTipText ("The name of the outputed file");
        logarea.setEditable(false);
        filein.setEditable(false);
        filein.setText("Select a file below...");
        outputloc.setEditable(false);
        outputloc.setText("Select a file below...");
        progressbar.setStringPainted(true);
        cancelBttn.setEnabled(false);

        // set COLORS
        logarea.setBackground(new Color(114, 137, 218));
        logarea.setForeground(new Color(255, 255, 255));
        selectoutputfile.setBackground(new Color(114, 137, 218));
        selectoutputfile.setForeground(new Color(255, 255, 255));
        inputselectbttn.setBackground(new Color(114, 137, 218));
        inputselectbttn.setForeground(new Color(255, 255, 255));
        cancelBttn.setBackground(new Color(114, 137, 218));
        renderButton.setBackground(new Color(114, 137, 218));
        renderButton.setForeground(new Color(255, 255, 255));

        inputfilelabel.setForeground(new Color(255, 255, 255));
        outputfilename.setForeground(new Color(255, 255, 255));
        outputdir.setForeground(new Color(255, 255, 255));


        //adjust size and set layout
        setPreferredSize (new Dimension (628, 371));
        setLayout (null);

        //add components
        add (renderButton);
        add (inputfilelabel);
        add (filein);
        add (outputfilename);
        add (outputdir);
        add (outputloc);
        add (nameentry);
        add (selectoutputfile);
        add (inputselectbttn);
        add (logarea);
        add (progressbar);
        add (cancelBttn);

        //set component bounds (only needed by Absolute Positioning)
        renderButton.setBounds (505, 310, 110, 40);
        inputfilelabel.setBounds (15, 20, 160, 40);
        filein.setBounds (15, 50, 175, 20);
        outputfilename.setBounds (290, 30, 130, 25);
        outputdir.setBounds (15, 100, 100, 25);
        outputloc.setBounds (15, 125, 190, 20);
        nameentry.setBounds (290, 50, 170, 20);
        selectoutputfile.setBounds (15, 150, 120, 25);
        inputselectbttn.setBounds (15, 75, 115, 25);
        logarea.setBounds (5, 245, 490, 120);
        progressbar.setBounds(5, 215, 405, 25);
        cancelBttn.setBounds (420, 215, 100, 25);
        logarea.setAutoscrolls(true);
        initlogger();
        setProgressbarPercentage(100);
        setProgressbarText("No action running.");
        setBackground(new Color(35, 39, 42));


        renderButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if(filein.getText().equals("Select a file below...")) {
                    JOptionPane.showMessageDialog(frame,
                            "Please Select an input file! (Fatal)",
                            "Render Error",
                            JOptionPane.ERROR_MESSAGE);
                }else if(outputloc.getText().equals("Select a file below...")){
                    JOptionPane.showMessageDialog(frame,
                            "Please Select an output directory! (Fatal)",
                            "Render Error",
                            JOptionPane.ERROR_MESSAGE);
                }else if(nameentry.getText().equals("")) {
                    int result = JOptionPane.showConfirmDialog(frame,"No File name was given, so blerp-out will be used. \n Continue with default file name?", "Render: Warning",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                    if(result == JOptionPane.YES_OPTION){
                        clearLogbox();
                        setProgressbarText("Waiting For analyzation to finish... ");
                        loggerAppend("--- Starting Render, Step 1/2: Analyzing file ---");
                        final Path ffprobe = Path.of(System.getProperty("user.dir") + "/bin/");
                        final Path ffmpeg = Path.of(System.getProperty("user.dir") + "/bin/");
                        System.out.println(ffprobe);
                        String pathToVideo = filein.getText();
                        FFprobeResult probeout = FFprobe.atPath(ffprobe)
                                .setShowStreams(true)
                                .setInput(pathToVideo)
                                .execute();
                        for (Stream stream : probeout.getStreams()) {
                            loggerAppend("\n type: " + stream.getCodecType()
                                    + "\n duration: " + stream.getDuration() + " seconds");
                            System.out.println("\n type: " + stream.getCodecType()
                                    + "\n duration: " + stream.getDuration() + " seconds");
                        }

                        loggerAppend("\n ---  Step 2/2: Rendering file --- \n This will take awhile, grab a snack while you wait :)");
                        final VideoProcessor videoProcessor = new FfmpegVideoProcessor(ffmpeg, 1020, 720);
                        videoProcessor.setProgressListener(System.out::println);
                    }else if (result == JOptionPane.NO_OPTION){
                        return;
                    }else {
                        System.out.println("[DEBUG] Render Window was closed without any button selection, stopping render...");
                    }

                }
                // call render here
            }
        });
        inputselectbttn.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JFileChooser fileChooser = new JFileChooser();
                int option = fileChooser.showOpenDialog(frame);
                if(option == JFileChooser.APPROVE_OPTION){
                    File file = fileChooser.getSelectedFile();
                    filein.setText(file.getPath());
                    loggerAppend("\n Set input file to: " + file.getPath());
                }else{
                    System.out.println("[DEBUG] File Chooser was closed without any file selection, not inputting file.");
                }
            }
        });
        selectoutputfile.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int option = fileChooser.showOpenDialog(frame);
                if(option == JFileChooser.APPROVE_OPTION){
                    File file = fileChooser.getSelectedFile();
                    outputloc.setText(file.getPath());
                    loggerAppend("\n Set output directory to: " + file.getPath());
                }else{
                    System.out.println("[DEBUG] File Chooser was closed without any file selection, not inputting file.");
                }
            }
        });
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame ("Video Blurplefier - 1.0.0");
        frame.setResizable(false);
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(Color.WHITE);
        frame.getContentPane().add (new GUImain(frame));
        frame.pack();
        frame.setVisible (true);
    }

    public static void invoke() {
        main(null);
    }

    // util functions
    public void initlogger() {
        logarea.append("---- Application Started Successfully, awaiting input ---- \n ");
        logarea.append("This tool was created by sticks#6436 and Stijn | CodingWarrior#0101");
    }
    public void loggerAppend(String args) {
        logarea.append(String.valueOf(args));
    }
    public void clearLogbox() {
        logarea.setText("");
    }
    public void setProgressbarPercentage(Integer args) {
        progressbar.setValue(args);
    }
    public void setProgressbarText(String args) {
        progressbar.setString(String.valueOf(args));
    }
    public String getInputfile() {
        return filein.getText();
    }
    public String getOutputLocation() {
        return outputloc.getText();
    }
    public String getfileName() {
        String filetext = nameentry.getText();
        if(filetext.length() == 0) {
            return null;
        }
        return filetext;
    }
}
