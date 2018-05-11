package ui;

import v13.Day;
import v13.Simulation;

import javax.swing.*;
import java.awt.event.*;

public class MainApplication extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTabbedPane tabContent;
    private Simulation simulation;

    public MainApplication() {
        SwingUtilities.invokeLater(() -> {
            setContentPane(contentPane);
            setModal(true);
            getRootPane().setDefaultButton(buttonOK);

            buttonOK.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    onOK();
                }
            });

            buttonCancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    onCancel();
                }
            });

            // call onCancel() when cross is clicked
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    onCancel();
                }
            });
            setSize(1920, 1080);
            setVisible(true);
            // call onCancel() on ESCAPE
            contentPane.registerKeyboardAction(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    onCancel();
                }
            }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        });
    }

    private void onOK() {
        simulation.market.close();
        // add your code here
        dispose();
    }

    private void onCancel() {
        simulation.market.close();
        // add your code here if necessary
        dispose();
    }

    public int addPanel(JPanel pan, String title) {
        int count = tabContent.getTabCount();
        SwingUtilities.invokeLater(() -> {
            tabContent.add(title, pan);
        });
        return count;
    }

    public void setTabTitle(int tab, String title) {
        tabContent.setTitleAt(tab, title);
    }

    public void runMarket(Simulation sim, Day day, int days) {
        simulation = sim;
        simulation.run(day, days);
        simulation.market.printState();
    }
}
