package dev.stijn.videoblurplefier;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

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

    public GUImain() {
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

        //set components properties
        renderButton.setToolTipText ("Render the frames");
        inputfilelabel.setToolTipText ("The input file (In .mp4 or something like that, must be a video file)");
        outputloc.setToolTipText ("The loaction you want to send the finshed file to.");
        nameentry.setToolTipText ("The name of the outputed file");

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
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame ("Video Blurplefier - 1.0.0");
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add (new GUImain());
        frame.pack();
        frame.setVisible (true);
    }

    public static void invoke() {
        main(null);
    }
}
