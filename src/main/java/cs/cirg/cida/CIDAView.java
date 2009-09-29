/*
 * CIDAView.java
 */
package cs.cirg.cida;

import com.google.common.collect.Sets;
import cs.cirg.cida.analysis.MannWhitneyUTest;
import cs.cirg.cida.components.CIlibOutputReader;
import cs.cirg.cida.components.SynopsisTableModel;
import cs.cirg.cida.components.IOBridgeTableModel;
import cs.cirg.cida.components.IntervalXYRenderer;
import cs.cirg.cida.components.SelectionListener;
import cs.cirg.cida.components.SeriesPair;
import cs.cirg.cida.experiment.Experiment;
import cs.cirg.cida.experiment.ExperimentManager;
import cs.cirg.cida.io.CSVFileWriter;
import cs.cirg.cida.io.DataTable;
import cs.cirg.cida.io.DataTableBuilder;
import cs.cirg.cida.io.StandardDataTable;
import cs.cirg.cida.io.exception.CIlibIOException;
import java.awt.Color;
import java.awt.Paint;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.sourceforge.cilib.type.types.Numeric;
import net.sourceforge.cilib.type.types.Real;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.xmlgraphics.java2d.GraphicContext;
import org.apache.xmlgraphics.java2d.ps.EPSDocumentGraphics2D;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * The application's main frame.
 */
public class CIDAView extends FrameView {

    private Object selectedExperimentItem;
    private Experiment selectedExperiment;
    private List<Integer> userSelectedRows;
    private List<Integer> userSelectedColumns;
    private ExperimentManager experimentManager;
    private List<Experiment> testExperiments;
    private List<String> testVariables;
    private String analysisName;
    private String startupDirectory;

    public CIDAView(SingleFrameApplication app) {
        super(app);

        startupDirectory = ((CIDAApplication) app).getStartupDirectory();
        if (startupDirectory.charAt(startupDirectory.length() - 1) != '/') {
            startupDirectory += '/';
        }
        userSelectedRows = new ArrayList<Integer>();
        userSelectedColumns = new ArrayList<Integer>();
        testExperiments = new ArrayList<Experiment>();
        testVariables = new ArrayList<String>();
        experimentManager = ExperimentManager.getInstance();
        analysisName = "";

        initComponents();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
    }

    @Action
    public void loadExperiment() {
        JFileChooser chooser = new JFileChooser(startupDirectory);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Text and CSV files", "txt", "csv");
        chooser.setFileFilter(filter);
        chooser.setMultiSelectionEnabled(true);
        int returnVal = chooser.showOpenDialog(this.getComponent());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                File[] files = chooser.getSelectedFiles();
                for (File dataFile : files) {
                    String experimentName = dataFile.getName().substring(0, dataFile.getName().lastIndexOf("."));
                    if (editResultsNameCheckBox.isSelected()) {
                        CIDADialog dialog = new CIDADialog(this.getFrame(), "Experiment name:", experimentName);
                        experimentName = dialog.getInput();
                    }

                    CIlibOutputReader reader = new CIlibOutputReader();
                    reader.setSourceURL(dataFile.getAbsolutePath());
                    DataTableBuilder dataTableBuilder = new DataTableBuilder(reader);
                    dataTableBuilder.buildDataTable();

                    Experiment experiment = new Experiment(experimentManager.nextExperimentID(), (StandardDataTable<Numeric>) dataTableBuilder.getDataTable());
                    experiment.setDataSource(dataFile.getAbsolutePath());
                    experiment.setName(experimentName);
                    experimentManager.addExperiment(experiment);
                    experimentsComboBox.addItem(experimentName);
                    experimentsComboBox.setSelectedItem(experimentName);

                    this.selectExperiment();
                }
            } catch (CIlibIOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Action
    public void selectExperiment() {
        if (((String) experimentsComboBox.getSelectedItem()).isEmpty()) {
            return;
        }
        String selectedExperimentName = (String) experimentsComboBox.getSelectedItem();
        selectedExperiment = null;
        selectedExperiment = experimentManager.getExperiment(selectedExperimentName);
        List<String> variableNames = selectedExperiment.getVariableNames();
        variablesComboBox.removeAllItems();
        for (String variableName : variableNames) {
            variablesComboBox.addItem(variableName);
        }

        IOBridgeTableModel model = new IOBridgeTableModel();
        model.setDataTable(selectedExperiment.getData());
        rawTable.setModel(model);
    }

    @Action
    public void addVariableToAnalysis() {
        if (variablesComboBox.getSelectedItem() == null) {
            System.out.println("selected item null");
            return;
        }
        if (((String) variablesComboBox.getSelectedItem()).isEmpty()) {
            System.out.println("variable box empty");
            return;
        }

        String variableName = (String) variablesComboBox.getSelectedItem();
        addVariableToAnalysis(selectedExperiment, variableName);
    }

    public void addVariableToAnalysis(Experiment experiment, String variableName) {
        if (!analysisName.contains(experiment.getName())) {
            analysisName = experiment.getName() + "_" + analysisName;
        }
        if (!analysisName.contains(variableName)) {
            analysisName = analysisName + variableName;
        }

        DataTable analysisDataTable = ((IOBridgeTableModel) analysisTable.getModel()).getDataTable();

        List<DescriptiveStatistics> statistics = experiment.getStatistics(variableName);

        int iterationsToAdd = statistics.size();
        if (!addAllRowsCheckBox.isSelected()) {
            CIDADialog dialog = new CIDADialog(this.getFrame(), "Number of rows:", "" + iterationsToAdd);
            iterationsToAdd = Integer.parseInt(dialog.getInput());
        }

        if (analysisDataTable.getNumColums() == 0) {
            List<Numeric> iterations = experiment.getIterationColumn();

            int size = iterations.size();
            for (int i = size - 1; i >= iterationsToAdd; i--) {
                iterations.remove(i);
            }

            analysisDataTable.addColumn(iterations);
            analysisDataTable.setColumnName(0, "Iterations");
        }

        int size = statistics.size();

        List<Real> means = new ArrayList<Real>(size);
        List<Real> stddevs = new ArrayList<Real>(size);

        for (int i = 0; i < size; i++) {
            means.add(new Real(statistics.get(i).getMean()));
            stddevs.add(new Real(statistics.get(i).getStandardDeviation()));
        }

        analysisDataTable.addColumn(means);
        analysisDataTable.setColumnName(analysisDataTable.getNumColums() - 1, experiment.getName() + " " + variableName + " Mean");
        analysisDataTable.addColumn(stddevs);
        analysisDataTable.setColumnName(analysisDataTable.getNumColums() - 1, experiment.getName() + " " + variableName + " Std Dev");

        IOBridgeTableModel model = new IOBridgeTableModel();
        model.setDataTable((StandardDataTable<Numeric>) analysisDataTable);
        analysisTable.setModel(model);

        addVariableToSynopsisTable(experiment, variableName);
    }

    @Action
    public void addVariableToSynopsisTable() {
        if (variablesComboBox.getSelectedItem() == null) {
            return;
        }
        if (((String) variablesComboBox.getSelectedItem()).isEmpty()) {
            return;
        }

        String variableName = (String) variablesComboBox.getSelectedItem();
        addVariableToSynopsisTable(selectedExperiment, variableName);
    }

    public void addVariableToSynopsisTable(Experiment experiment, String variable) {
        SynopsisTableModel model = (SynopsisTableModel) synopsisTable.getModel();

        if (!model.getVariables().contains(variable)) {
            model.getVariables().add(variable);
        }

        if (!model.getExperiments().contains(experiment)) {
            model.getExperiments().add(experiment);
        }

        SynopsisTableModel newModel = new SynopsisTableModel(model);

        synopsisTable.setModel(newModel);
    }

    @Action
    public void addAllToAnalysis() {
        int numVars = variablesComboBox.getItemCount();
        for (int i = 0; i < numVars; i++) {
            String variable = (String) variablesComboBox.getItemAt(i);
            addVariableToAnalysis(selectedExperiment, variable);
        }
    }

    @Action
    public void addAllToSynopsis() {
        int numVars = variablesComboBox.getItemCount();
        for (int i = 0; i < numVars; i++) {
            String variable = (String) variablesComboBox.getItemAt(i);
            addVariableToSynopsisTable(selectedExperiment, variable);
        }
    }

    @Action
    public void addExperimentToTest() {
        if (selectedExperiment == null) {
            return;
        }

        addExperimentToTest(selectedExperiment);
    }

    public void addExperimentToTest(Experiment experiment) {
        if (selectedExperiment == null) {
            return;
        }
        if (!testExperiments.contains(selectedExperiment)) {
            testExperiments.add(selectedExperiment);
        }

        Set<String> variableSet = Sets.newHashSet(testVariables);
        Set<String> newVariables = Sets.newHashSet(selectedExperiment.getVariableNames());
        variableSet = Sets.union(variableSet, newVariables);
        testVariables.clear();
        testVariables.addAll(variableSet);

        variablesTestComboBox.removeAllItems();
        for (String variable : testVariables) {
            variablesTestComboBox.addItem(variable);
        }

        SynopsisTableModel model = (SynopsisTableModel) testExperimentsTable.getModel();
        model.setExperiments(testExperiments);
        model.setVariables(testVariables);

        SynopsisTableModel newModel = new SynopsisTableModel(model);
        testExperimentsTable.setModel(newModel);
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = CIDAApplication.getApplication().getMainFrame();
            aboutBox = new CIDAAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        CIDAApplication.getApplication().show(aboutBox);
    }

    @Action
    public void exportRaw() {
        JFileChooser chooser = new JFileChooser(startupDirectory);
        chooser.setSelectedFile(new File(selectedExperiment.getName() + ".csv"));
        int returnVal = chooser.showOpenDialog(this.getComponent());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                CSVFileWriter writer = new CSVFileWriter();
                writer.setFile(chooser.getSelectedFile());
                writer.open();
                writer.write(((IOBridgeTableModel) rawTable.getModel()).getDataTable());
                writer.close();
            } catch (CIlibIOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Action
    public void exportAnalysis() {
        JFileChooser chooser = new JFileChooser(startupDirectory);
        chooser.setSelectedFile(new File(analysisName + ".csv"));
        int returnVal = chooser.showOpenDialog(this.getComponent());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                CSVFileWriter writer = new CSVFileWriter();
                writer.setFile(chooser.getSelectedFile());
                writer.open();
                writer.write(((IOBridgeTableModel) analysisTable.getModel()).getDataTable());
                writer.close();
            } catch (CIlibIOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Action
    public void clearAnalysisTable() {
        analysisTable.setModel(new IOBridgeTableModel());
    }

    @Action
    public void plotGraph() {
        lineSeriesComboBox.removeAllItems();
        int numSelectedColumns = userSelectedColumns.size();
        int numSelectedRows = userSelectedRows.size();
        if (numSelectedColumns == 0) {
            return;
        }
        StandardDataTable<Numeric> data = (StandardDataTable<Numeric>) ((IOBridgeTableModel) analysisTable.getModel()).getDataTable();
        List<Numeric> iterations = data.getColumn(0);

        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();


        for (int i = 0; i < numSelectedColumns; i++) {
            int selectedColumnIndex = userSelectedColumns.get(i);
            List<Numeric> selectedColumn = data.getColumn(selectedColumnIndex);

            XYSeries series = new XYSeries(data.getColumnName(selectedColumnIndex));

            for (int k = 0; k < numSelectedRows; k++) {
                int selectedRowIndex = userSelectedRows.get(k);
                series.add(iterations.get(selectedRowIndex).getReal(), selectedColumn.get(selectedRowIndex).getReal());
            }
            xySeriesCollection.addSeries(series);
            lineSeriesComboBox.addItem(new SeriesPair(i, (String) series.getKey()));
        }

        JFreeChart chart = ChartFactory.createXYLineChart(analysisName, // Title
                "Iterations", // X-Axis label
                "Value", // Y-Axis label
                xySeriesCollection, // Dataset
                PlotOrientation.VERTICAL,
                true, // Show legend,
                false, //tooltips
                false //urls
                );
        chart.setAntiAlias(true);
        chart.setAntiAlias(true);
        XYPlot plot = (XYPlot) chart.getPlot();
        Paint[] paints = new Paint[7];
        paints[0] = Color.RED;
        paints[1] = Color.BLUE;
        paints[2] = new Color(0.08f, 0.5f, 0.04f);
        paints[3] = new Color(1.0f, 0.37f, 0.0f);
        paints[4] = new Color(0.38f, 0.07f, 0.42f);
        paints[5] = Color.CYAN;
        paints[6] = Color.PINK;
        plot.setDrawingSupplier(new DefaultDrawingSupplier(paints, paints,
                DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE));
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.GRAY);
        plot.setRangeGridlinePaint(Color.GRAY);

        IntervalXYRenderer renderer = new IntervalXYRenderer(true, false);
        plot.setRenderer(renderer);
        lineTickIntervalInput.setText(Integer.toString(renderer.getLineTickInterval()));

        ((ChartPanel) chartPanel).setChart(chart);
    }

    @Action
    public void toggleLineTicks() {
        if (toggleLineTicksButton.isSelected()) {
            JFreeChart chart = ((ChartPanel) chartPanel).getChart();
            XYPlot plot = (XYPlot) chart.getPlot();
            IntervalXYRenderer renderer = (IntervalXYRenderer) plot.getRenderer();
            try {
                renderer.setLineTickInterval(Integer.parseInt(lineTickIntervalInput.getText()));
            } catch (NumberFormatException noe) {
                renderer.setLineTickInterval(IntervalXYRenderer.lineTickIntervalDefault);
            }
            renderer.setBaseShapesVisible(true);
        } else {
            JFreeChart chart = ((ChartPanel) chartPanel).getChart();
            XYPlot plot = (XYPlot) chart.getPlot();
            IntervalXYRenderer renderer = (IntervalXYRenderer) plot.getRenderer();
            renderer.setBaseShapesVisible(false);
        }
    }

    @Action
    public void changeSeriesName() {
        SeriesPair series = (SeriesPair) lineSeriesComboBox.getSelectedItem();
        JFreeChart chart = ((ChartPanel) chartPanel).getChart();
        XYPlot plot = (XYPlot) chart.getPlot();
        XYSeriesCollection xYSeriesCollection = (XYSeriesCollection) plot.getDataset();
        CIDADialog dialog = new CIDADialog(this.getFrame(), "Enter new name: ", (String) xYSeriesCollection.getSeries(series.getValue()).getKey());
        xYSeriesCollection.getSeries(series.getValue()).setKey(dialog.getInput());
        plot.notifyListeners(new PlotChangeEvent(plot));
        lineSeriesComboBox.removeItem(series);
        lineSeriesComboBox.addItem(new SeriesPair(series.getKey(), dialog.getInput()));
    }

    @Action
    public void changeSeriesColor() {
        SeriesPair series = (SeriesPair) lineSeriesComboBox.getSelectedItem();
        JFreeChart chart = ((ChartPanel) chartPanel).getChart();
        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
    }

    @Action
    public void savePlotEPS() {
        JFreeChart chartToSave = ((ChartPanel) chartPanel).getChart();
        String name = chartToSave.getTitle().getText();
        name = name.trim().replaceAll(" ", "");
        try {
            OutputStream out = new FileOutputStream(name + ".eps");
            EPSDocumentGraphics2D g2d = new EPSDocumentGraphics2D(false);
            g2d.setGraphicContext(new GraphicContext());

            g2d.setupDocument(out, 800, 600);
            chartToSave.draw(g2d, new Rectangle2D.Double(0, 0, 800, 600));

            g2d.finish();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Action
    public void savePlotPNG() {
        JFreeChart chartToSave = ((ChartPanel) chartPanel).getChart();
        String name = chartToSave.getTitle().getText();
        name = name.trim().replaceAll(" ", "");
        try {
            ChartUtilities.saveChartAsPNG(new File(name + ".png"), chartToSave, 800, 600);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Action
    public void runMannWhitneyUTest() {
        if (testExperiments.size() > 2) {
            System.out.println("Mann Whitney U test is only applicable to 2 experiments.");
            return;
        }
        MannWhitneyUTest test = new MannWhitneyUTest();
        for (Experiment experiment : testExperiments) {
            test.addExperiment(experiment);
        }
        test.performTest((String) variablesTestComboBox.getSelectedItem());
        DataTable table = test.getResults();
        IOBridgeTableModel model = new IOBridgeTableModel();
        model.setDataTable((StandardDataTable<Numeric>) table);
        testResultsTable.setModel(model);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        testPanel = new javax.swing.JTabbedPane();
        homePanel = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        synopsisTable = new javax.swing.JTable();
        experimentsLabel = new javax.swing.JLabel();
        loadExperimentButton = new javax.swing.JButton();
        experimentsComboBox = new javax.swing.JComboBox();
        editResultsNameCheckBox = new javax.swing.JCheckBox();
        addToTestButton = new javax.swing.JButton();
        variablesLabel = new javax.swing.JLabel();
        variablesComboBox = new javax.swing.JComboBox();
        addToAnalysisButton = new javax.swing.JButton();
        addToSynopsisButton = new javax.swing.JButton();
        addAllRowsCheckBox = new javax.swing.JCheckBox();
        addAllVariablesAnalysisButton = new javax.swing.JButton();
        addAllVariablesSynopsisButton = new javax.swing.JButton();
        rawPanel = new javax.swing.JPanel();
        rawPanelToolbar = new javax.swing.JToolBar();
        exportDataButton = new javax.swing.JButton();
        rawScrollPane = new javax.swing.JScrollPane();
        rawTable = new javax.swing.JTable();
        analysisPanel = new javax.swing.JPanel();
        analysisToolbar = new javax.swing.JToolBar();
        plotButton = new javax.swing.JButton();
        clearAnalysisButton = new javax.swing.JButton();
        exportAnalysisButton = new javax.swing.JButton();
        analysisScrollPane = new javax.swing.JScrollPane();
        analysisTable = new javax.swing.JTable();
        chartHomePanel = new javax.swing.JPanel();
        chartToolbar = new javax.swing.JToolBar();
        toggleLineTicksButton = new javax.swing.JToggleButton();
        lineTickIntervalLabel = new javax.swing.JLabel();
        lineTickIntervalInput = new javax.swing.JTextField();
        lineSeriesComboBox = new javax.swing.JComboBox();
        seriesColorButton = new javax.swing.JButton();
        seriesNameButton = new javax.swing.JButton();
        exportPNGButton = new javax.swing.JButton();
        exportEPSButton = new javax.swing.JButton();
        chartScrollPane = new javax.swing.JScrollPane();
        chartPanel = new ChartPanel(null, true, true, false, true, true);
        jPanel1 = new javax.swing.JPanel();
        testToolbar = new javax.swing.JToolBar();
        jLabel1 = new javax.swing.JLabel();
        variablesTestComboBox = new javax.swing.JComboBox();
        mannWhitneyUTestButton = new javax.swing.JButton();
        testExperimentsScrollPane = new javax.swing.JScrollPane();
        testExperimentsTable = new javax.swing.JTable();
        testResultsScrollPane = new javax.swing.JScrollPane();
        testResultsTable = new javax.swing.JTable();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(1280, 1024));

        testPanel.setName("testPanel"); // NOI18N

        homePanel.setName("homePanel"); // NOI18N

        jSeparator1.setName("jSeparator1"); // NOI18N

        jSeparator2.setName("jSeparator2"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        synopsisTable.setAutoCreateRowSorter(true);
        synopsisTable.setModel(new SynopsisTableModel());
        synopsisTable.setColumnSelectionAllowed(true);
        synopsisTable.setName("synopsisTable"); // NOI18N
        jScrollPane1.setViewportView(synopsisTable);
        synopsisTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(cs.cirg.cida.CIDAApplication.class).getContext().getResourceMap(CIDAView.class);
        experimentsLabel.setText(resourceMap.getString("experimentsLabel.text")); // NOI18N
        experimentsLabel.setName("experimentsLabel"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(cs.cirg.cida.CIDAApplication.class).getContext().getActionMap(CIDAView.class, this);
        loadExperimentButton.setAction(actionMap.get("loadExperiment")); // NOI18N
        loadExperimentButton.setText(resourceMap.getString("loadExperimentButton.text")); // NOI18N
        loadExperimentButton.setMaximumSize(new java.awt.Dimension(110, 29));
        loadExperimentButton.setMinimumSize(new java.awt.Dimension(110, 29));
        loadExperimentButton.setName("loadExperimentButton"); // NOI18N
        loadExperimentButton.setPreferredSize(new java.awt.Dimension(110, 29));

        experimentsComboBox.setModel(new javax.swing.DefaultComboBoxModel());
        experimentsComboBox.setName("experimentsComboBox"); // NOI18N
        experimentsComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                experimentsComboBoxActionPerformed(evt);
            }
        });

        editResultsNameCheckBox.setText(resourceMap.getString("editResultsNameCheckBox.text")); // NOI18N
        editResultsNameCheckBox.setName("editResultsNameCheckBox"); // NOI18N

        addToTestButton.setAction(actionMap.get("addExperimentToTest")); // NOI18N
        addToTestButton.setText(resourceMap.getString("addToTestButton.text")); // NOI18N
        addToTestButton.setMaximumSize(new java.awt.Dimension(110, 29));
        addToTestButton.setMinimumSize(new java.awt.Dimension(110, 29));
        addToTestButton.setName("addToTestButton"); // NOI18N
        addToTestButton.setPreferredSize(new java.awt.Dimension(110, 29));

        variablesLabel.setText(resourceMap.getString("variablesLabel.text")); // NOI18N
        variablesLabel.setName("variablesLabel"); // NOI18N

        variablesComboBox.setName("variablesComboBox"); // NOI18N

        addToAnalysisButton.setAction(actionMap.get("addVariableToAnalysis")); // NOI18N
        addToAnalysisButton.setText(resourceMap.getString("addToAnalysisButton.text")); // NOI18N
        addToAnalysisButton.setName("addToAnalysisButton"); // NOI18N

        addToSynopsisButton.setAction(actionMap.get("addVariableToSynopsisTable")); // NOI18N
        addToSynopsisButton.setText(resourceMap.getString("addToSynopsisButton.text")); // NOI18N
        addToSynopsisButton.setName("addToSynopsisButton"); // NOI18N

        addAllRowsCheckBox.setSelected(true);
        addAllRowsCheckBox.setText(resourceMap.getString("addAllRowsCheckBox.text")); // NOI18N
        addAllRowsCheckBox.setName("addAllRowsCheckBox"); // NOI18N

        addAllVariablesAnalysisButton.setAction(actionMap.get("addAllToAnalysis")); // NOI18N
        addAllVariablesAnalysisButton.setText(resourceMap.getString("addAllVariablesAnalysisButton.text")); // NOI18N
        addAllVariablesAnalysisButton.setName("addAllVariablesAnalysisButton"); // NOI18N

        addAllVariablesSynopsisButton.setText(resourceMap.getString("addAllVariablesSynopsisButton.text")); // NOI18N
        addAllVariablesSynopsisButton.setName("addAllVariablesSynopsisButton"); // NOI18N

        org.jdesktop.layout.GroupLayout homePanelLayout = new org.jdesktop.layout.GroupLayout(homePanel);
        homePanel.setLayout(homePanelLayout);
        homePanelLayout.setHorizontalGroup(
            homePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(homePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(homePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1044, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jSeparator2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1044, Short.MAX_VALUE)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1044, Short.MAX_VALUE)
                    .add(homePanelLayout.createSequentialGroup()
                        .add(variablesLabel)
                        .add(18, 18, 18)
                        .add(variablesComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 350, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(homePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(addToSynopsisButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(addAllVariablesAnalysisButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(addAllVariablesSynopsisButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, addToAnalysisButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(addAllRowsCheckBox))
                    .add(homePanelLayout.createSequentialGroup()
                        .add(experimentsLabel)
                        .add(62, 62, 62)
                        .add(experimentsComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 348, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(homePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, addToTestButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, loadExperimentButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(editResultsNameCheckBox)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 192, Short.MAX_VALUE)))
                .addContainerGap())
        );
        homePanelLayout.setVerticalGroup(
            homePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(homePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(homePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(experimentsLabel)
                    .add(homePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(loadExperimentButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(editResultsNameCheckBox)
                        .add(experimentsComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(addToTestButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(139, 139, 139)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(homePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(variablesLabel)
                    .add(homePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(variablesComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(addToAnalysisButton)
                        .add(addAllRowsCheckBox)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(addToSynopsisButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(addAllVariablesAnalysisButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(addAllVariablesSynopsisButton)
                .add(51, 51, 51)
                .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE)
                .addContainerGap())
        );

        testPanel.addTab(resourceMap.getString("homePanel.TabConstraints.tabTitle"), homePanel); // NOI18N

        rawPanel.setName("rawPanel"); // NOI18N

        rawPanelToolbar.setRollover(true);
        rawPanelToolbar.setName("rawPanelToolbar"); // NOI18N

        exportDataButton.setAction(actionMap.get("exportRaw")); // NOI18N
        exportDataButton.setText(resourceMap.getString("exportDataButton.text")); // NOI18N
        exportDataButton.setFocusable(false);
        exportDataButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        exportDataButton.setName("exportDataButton"); // NOI18N
        exportDataButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        rawPanelToolbar.add(exportDataButton);

        rawScrollPane.setName("rawScrollPane"); // NOI18N

        rawTable.setAutoCreateRowSorter(true);
        rawTable.setModel(new IOBridgeTableModel());
        rawTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        rawTable.setColumnSelectionAllowed(true);
        rawTable.setName("rawTable"); // NOI18N
        rawScrollPane.setViewportView(rawTable);
        rawTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        org.jdesktop.layout.GroupLayout rawPanelLayout = new org.jdesktop.layout.GroupLayout(rawPanel);
        rawPanel.setLayout(rawPanelLayout);
        rawPanelLayout.setHorizontalGroup(
            rawPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(rawPanelToolbar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1056, Short.MAX_VALUE)
            .add(rawScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1056, Short.MAX_VALUE)
        );
        rawPanelLayout.setVerticalGroup(
            rawPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(rawPanelLayout.createSequentialGroup()
                .add(rawPanelToolbar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rawScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 896, Short.MAX_VALUE)
                .addContainerGap())
        );

        testPanel.addTab(resourceMap.getString("rawPanel.TabConstraints.tabTitle"), rawPanel); // NOI18N

        analysisPanel.setName("analysisPanel"); // NOI18N

        analysisToolbar.setRollover(true);
        analysisToolbar.setName("analysisToolbar"); // NOI18N

        plotButton.setAction(actionMap.get("plotGraph")); // NOI18N
        plotButton.setText(resourceMap.getString("plotButton.text")); // NOI18N
        plotButton.setFocusable(false);
        plotButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        plotButton.setName("plotButton"); // NOI18N
        plotButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        analysisToolbar.add(plotButton);

        clearAnalysisButton.setAction(actionMap.get("clearAnalysisTable")); // NOI18N
        clearAnalysisButton.setText(resourceMap.getString("clearAnalysisButton.text")); // NOI18N
        clearAnalysisButton.setFocusable(false);
        clearAnalysisButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        clearAnalysisButton.setName("clearAnalysisButton"); // NOI18N
        clearAnalysisButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        analysisToolbar.add(clearAnalysisButton);

        exportAnalysisButton.setAction(actionMap.get("exportAnalysis")); // NOI18N
        exportAnalysisButton.setText(resourceMap.getString("exportAnalysisButton.text")); // NOI18N
        exportAnalysisButton.setFocusable(false);
        exportAnalysisButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        exportAnalysisButton.setName("exportAnalysisButton"); // NOI18N
        exportAnalysisButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        analysisToolbar.add(exportAnalysisButton);

        analysisScrollPane.setName("analysisScrollPane"); // NOI18N

        ListSelectionModel listSelectionModel = analysisTable.getSelectionModel();
        listSelectionModel.addListSelectionListener(new SelectionListener(userSelectedRows));
        analysisTable.setSelectionModel(listSelectionModel);
        analysisTable.setAutoCreateRowSorter(true);
        analysisTable.setModel(new IOBridgeTableModel());
        analysisTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        analysisTable.setColumnSelectionAllowed(true);
        analysisTable.setName("analysisTable"); // NOI18N
        analysisScrollPane.setViewportView(analysisTable);
        analysisTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listSelectionModel = analysisTable.getColumnModel().getSelectionModel();
        listSelectionModel.addListSelectionListener(new SelectionListener(userSelectedColumns));
        analysisTable.getColumnModel().setSelectionModel(listSelectionModel);

        org.jdesktop.layout.GroupLayout analysisPanelLayout = new org.jdesktop.layout.GroupLayout(analysisPanel);
        analysisPanel.setLayout(analysisPanelLayout);
        analysisPanelLayout.setHorizontalGroup(
            analysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(analysisToolbar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1056, Short.MAX_VALUE)
            .add(analysisScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1056, Short.MAX_VALUE)
        );
        analysisPanelLayout.setVerticalGroup(
            analysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(analysisPanelLayout.createSequentialGroup()
                .add(analysisToolbar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(analysisScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 896, Short.MAX_VALUE)
                .addContainerGap())
        );

        testPanel.addTab(resourceMap.getString("analysisPanel.TabConstraints.tabTitle"), analysisPanel); // NOI18N

        chartHomePanel.setName("chartHomePanel"); // NOI18N

        chartToolbar.setRollover(true);
        chartToolbar.setName("chartToolbar"); // NOI18N

        toggleLineTicksButton.setAction(actionMap.get("toggleLineTicks")); // NOI18N
        toggleLineTicksButton.setText(resourceMap.getString("toggleLineTicksButton.text")); // NOI18N
        toggleLineTicksButton.setFocusable(false);
        toggleLineTicksButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toggleLineTicksButton.setName("toggleLineTicksButton"); // NOI18N
        toggleLineTicksButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        chartToolbar.add(toggleLineTicksButton);

        lineTickIntervalLabel.setText(resourceMap.getString("lineTickIntervalLabel.text")); // NOI18N
        lineTickIntervalLabel.setName("lineTickIntervalLabel"); // NOI18N
        chartToolbar.add(lineTickIntervalLabel);

        lineTickIntervalInput.setText(resourceMap.getString("lineTickIntervalInput.text")); // NOI18N
        lineTickIntervalInput.setMinimumSize(new java.awt.Dimension(60, 27));
        lineTickIntervalInput.setName("lineTickIntervalInput"); // NOI18N
        lineTickIntervalInput.setPreferredSize(new java.awt.Dimension(60, 27));
        chartToolbar.add(lineTickIntervalInput);

        lineSeriesComboBox.setModel(new javax.swing.DefaultComboBoxModel());
        lineSeriesComboBox.setName("lineSeriesComboBox"); // NOI18N
        chartToolbar.add(lineSeriesComboBox);

        seriesColorButton.setAction(actionMap.get("changeSeriesColor")); // NOI18N
        seriesColorButton.setText(resourceMap.getString("seriesColorButton.text")); // NOI18N
        seriesColorButton.setFocusable(false);
        seriesColorButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        seriesColorButton.setName("seriesColorButton"); // NOI18N
        seriesColorButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        chartToolbar.add(seriesColorButton);

        seriesNameButton.setAction(actionMap.get("changeSeriesName")); // NOI18N
        seriesNameButton.setText(resourceMap.getString("seriesNameButton.text")); // NOI18N
        seriesNameButton.setFocusable(false);
        seriesNameButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        seriesNameButton.setName("seriesNameButton"); // NOI18N
        seriesNameButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        chartToolbar.add(seriesNameButton);

        exportPNGButton.setAction(actionMap.get("savePlotPNG")); // NOI18N
        exportPNGButton.setText(resourceMap.getString("exportPNGButton.text")); // NOI18N
        exportPNGButton.setFocusable(false);
        exportPNGButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        exportPNGButton.setName("exportPNGButton"); // NOI18N
        exportPNGButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        chartToolbar.add(exportPNGButton);

        exportEPSButton.setAction(actionMap.get("savePlotEPS")); // NOI18N
        exportEPSButton.setText(resourceMap.getString("exportEPSButton.text")); // NOI18N
        exportEPSButton.setFocusable(false);
        exportEPSButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        exportEPSButton.setName("exportEPSButton"); // NOI18N
        exportEPSButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        chartToolbar.add(exportEPSButton);

        chartScrollPane.setName("chartScrollPane"); // NOI18N

        chartPanel.setName("chartPanel"); // NOI18N

        org.jdesktop.layout.GroupLayout chartPanelLayout = new org.jdesktop.layout.GroupLayout(chartPanel);
        chartPanel.setLayout(chartPanelLayout);
        chartPanelLayout.setHorizontalGroup(
            chartPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 1036, Short.MAX_VALUE)
        );
        chartPanelLayout.setVerticalGroup(
            chartPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 928, Short.MAX_VALUE)
        );

        chartScrollPane.setViewportView(chartPanel);

        org.jdesktop.layout.GroupLayout chartHomePanelLayout = new org.jdesktop.layout.GroupLayout(chartHomePanel);
        chartHomePanel.setLayout(chartHomePanelLayout);
        chartHomePanelLayout.setHorizontalGroup(
            chartHomePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(chartToolbar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1056, Short.MAX_VALUE)
            .add(chartScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1056, Short.MAX_VALUE)
        );
        chartHomePanelLayout.setVerticalGroup(
            chartHomePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(chartHomePanelLayout.createSequentialGroup()
                .add(chartToolbar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(chartScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 908, Short.MAX_VALUE))
        );

        testPanel.addTab(resourceMap.getString("chartHomePanel.TabConstraints.tabTitle"), chartHomePanel); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        testToolbar.setRollover(true);
        testToolbar.setName("testToolbar"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        testToolbar.add(jLabel1);

        variablesTestComboBox.setModel(new javax.swing.DefaultComboBoxModel());
        variablesTestComboBox.setName("variablesTestComboBox"); // NOI18N
        testToolbar.add(variablesTestComboBox);

        mannWhitneyUTestButton.setAction(actionMap.get("runMannWhitneyUTest")); // NOI18N
        mannWhitneyUTestButton.setText(resourceMap.getString("mannWhitneyUTestButton.text")); // NOI18N
        mannWhitneyUTestButton.setFocusable(false);
        mannWhitneyUTestButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        mannWhitneyUTestButton.setName("mannWhitneyUTestButton"); // NOI18N
        mannWhitneyUTestButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        testToolbar.add(mannWhitneyUTestButton);

        testExperimentsScrollPane.setName("testExperimentsScrollPane"); // NOI18N

        testExperimentsTable.setAutoCreateRowSorter(true);
        testExperimentsTable.setModel(new SynopsisTableModel());
        testExperimentsTable.setColumnSelectionAllowed(true);
        testExperimentsTable.setName("testExperimentsTable"); // NOI18N
        testExperimentsScrollPane.setViewportView(testExperimentsTable);
        testExperimentsTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        testResultsScrollPane.setName("testResultsScrollPane"); // NOI18N

        testResultsTable.setAutoCreateRowSorter(true);
        testResultsTable.setModel(new SynopsisTableModel());
        testResultsTable.setName("testResultsTable"); // NOI18N
        testResultsScrollPane.setViewportView(testResultsTable);
        testResultsTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(testToolbar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1056, Short.MAX_VALUE)
            .add(testExperimentsScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1056, Short.MAX_VALUE)
            .add(testResultsScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1056, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(testToolbar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(testExperimentsScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)
                .add(128, 128, 128)
                .add(testResultsScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE))
        );

        testPanel.addTab(resourceMap.getString("jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(testPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1060, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(testPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 970, Short.MAX_VALUE)
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        org.jdesktop.layout.GroupLayout statusPanelLayout = new org.jdesktop.layout.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusPanelSeparator, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1060, Short.MAX_VALUE)
            .add(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(statusMessageLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 876, Short.MAX_VALUE)
                .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusPanelLayout.createSequentialGroup()
                .add(statusPanelSeparator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(statusMessageLabel)
                    .add(statusAnimationLabel)
                    .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(3, 3, 3))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void experimentsComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_experimentsComboBoxActionPerformed
        this.selectExperiment();
    }//GEN-LAST:event_experimentsComboBoxActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox addAllRowsCheckBox;
    private javax.swing.JButton addAllVariablesAnalysisButton;
    private javax.swing.JButton addAllVariablesSynopsisButton;
    private javax.swing.JButton addToAnalysisButton;
    private javax.swing.JButton addToSynopsisButton;
    private javax.swing.JButton addToTestButton;
    private javax.swing.JPanel analysisPanel;
    private javax.swing.JScrollPane analysisScrollPane;
    private javax.swing.JTable analysisTable;
    private javax.swing.JToolBar analysisToolbar;
    private javax.swing.JPanel chartHomePanel;
    private javax.swing.JPanel chartPanel;
    private javax.swing.JScrollPane chartScrollPane;
    private javax.swing.JToolBar chartToolbar;
    private javax.swing.JButton clearAnalysisButton;
    private javax.swing.JCheckBox editResultsNameCheckBox;
    private javax.swing.JComboBox experimentsComboBox;
    private javax.swing.JLabel experimentsLabel;
    private javax.swing.JButton exportAnalysisButton;
    private javax.swing.JButton exportDataButton;
    private javax.swing.JButton exportEPSButton;
    private javax.swing.JButton exportPNGButton;
    private javax.swing.JPanel homePanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JComboBox lineSeriesComboBox;
    private javax.swing.JTextField lineTickIntervalInput;
    private javax.swing.JLabel lineTickIntervalLabel;
    private javax.swing.JButton loadExperimentButton;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton mannWhitneyUTestButton;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JButton plotButton;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JPanel rawPanel;
    private javax.swing.JToolBar rawPanelToolbar;
    private javax.swing.JScrollPane rawScrollPane;
    private javax.swing.JTable rawTable;
    private javax.swing.JButton seriesColorButton;
    private javax.swing.JButton seriesNameButton;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JTable synopsisTable;
    private javax.swing.JScrollPane testExperimentsScrollPane;
    private javax.swing.JTable testExperimentsTable;
    private javax.swing.JTabbedPane testPanel;
    private javax.swing.JScrollPane testResultsScrollPane;
    private javax.swing.JTable testResultsTable;
    private javax.swing.JToolBar testToolbar;
    private javax.swing.JToggleButton toggleLineTicksButton;
    private javax.swing.JComboBox variablesComboBox;
    private javax.swing.JLabel variablesLabel;
    private javax.swing.JComboBox variablesTestComboBox;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;
}
