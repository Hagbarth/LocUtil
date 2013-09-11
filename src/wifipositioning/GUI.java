package wifipositioning;

import java.awt.*;
import javax.swing.*;

class GUI implements Runnable{
	public static void main(String[] args) {
		GUI se = new GUI();
        // Schedules the application to be run at the correct time in the event queue.
        SwingUtilities.invokeLater(se);
	}

	@Override
    public void run() {
        // Create the window
        JFrame f = new JFrame("Wifipositioning Project");

        // Sets the behavior for when the window is closed
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add a layout manager so that the button is not placed on top of the label
        f.setLayout(new GridLayout(0, 2));

        //Set frame size
        Container c = f.getContentPane();
        Dimension d = new Dimension(400,400);
        c.setPreferredSize(d);
        f.pack();
        f.setResizable(false);
        f.setVisible(true);

        //Fingerprint frame
        JInternalFrame ff = new JInternalFrame();
        ff.setLayout(new FlowLayout());

        // Add a label and a button
       	ff.add(new JLabel("Fingerprinting:"));
        ff.add(new JFormattedTextField("A Number"));
        ff.add(new JButton("Bla"));

        // Arrange the components inside the window
        f.add(ff);
        f.pack();
        // By default, the window is not visible. Make it visible.
        f.setVisible(true);
    }
}