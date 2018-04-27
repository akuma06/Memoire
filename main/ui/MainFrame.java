package main.ui;

import org.knowm.xchart.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MainFrame {
    private JPanel MainView;
    private JPanel panel1;
    private JTable AskTable;
    private JTable BidTable;
    private JPanel sidePanel;
    private XYChart currentGraph;

    public JPanel getPanel() {
        return panel1;
    }

    public void addGraph() {
        currentGraph = new XYChartBuilder().width(600).height(400).title("Court des prix").xAxisTitle("Ticks").yAxisTitle("Prix").build();
        JPanel pan = new XChartPanel<>(currentGraph);
        MainView.add(pan);
        MainView.revalidate();
        MainView.repaint();

        BidTable.setModel(new DefaultTableModel(new String[]{"Nom", "Prix"}, 10));
        AskTable.setModel(new DefaultTableModel(new String[]{"Nom", "Prix"}, 10));
        sidePanel.revalidate();
        sidePanel.repaint();
    }

    public void replaceSeries(double[] X, double[] Y) {
        if (currentGraph.getSeriesMap().isEmpty()) {
            System.out.println("NEW GRAPH");
            currentGraph.addSeries("graph", X, Y);
        } else {
            currentGraph.updateXYSeries("graph", X, Y, null);
        }
        MainView.revalidate();
        MainView.repaint();
    }

    public JTable getAskTable() {
        return AskTable;
    }

    public JTable getBidTable() {
        return BidTable;
    }
}
