package org.example.antrauzd1;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class HelloController {
    static final double DELAY_RATE = 0.2;
    @FXML
    private Slider loanTermSlider;
    @FXML
    TextField loanAmountField;
    @FXML
    Label loanAmountError;
    @FXML
    private Label loanTermLabel;
    @FXML
    RadioButton annuity;
    @FXML
    Label delayPercentageLabel;
    @FXML
    RadioButton linear;
    @FXML
    Label intervalError;
    @FXML
    TextField intervalFilterField;
    @FXML
    TableView<Payment> paymentTable;
    @FXML
    Pane chartContainer;
    @FXML
    DatePicker delayStart;
    @FXML
    DatePicker delayEnd;
    @FXML
    Button saveButton;
    ToggleGroup paymentMode;
    NumberAxis xAxis;

    NumberAxis yAxis;

    ObservableList<Payment> paymentList;
    Double loanDuration = 0.0;
    Double interestRate = 0.05;
    Payment payment;
    String method;
    boolean inputProvided = false;
    Double unpaidLoan;
    LineChart<Number, Number> paymentChart;
    @FXML
    TableColumn<Payment, Double> paymentColumn;

    Double loanAmount; //check if not null
    Double loanAmountBackup;
    LocalDate currentDate;
    LocalDate delayStartValue;
    LocalDate delayEndValue;



    public void initialize() {
        loanAmount = null;
        loanAmountBackup = null;
        paymentMode = new ToggleGroup();
        annuity.setId("Annuity");
        linear.setId("Linear");
        annuity.setToggleGroup(paymentMode);
        linear.setToggleGroup(paymentMode);
        paymentMode.selectedToggleProperty().addListener((observable, oldValue, newValue) -> mainProcess());
        loanAmountField.textProperty().addListener((observable, oldValue, newValue) -> mainProcess());
        loanTermSlider.valueProperty().addListener((observable, oldValue, newValue) -> mainProcess());
        loanTermLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            loanDuration = loanTermSlider.getValue();
            int years = (int) (loanTermSlider.getValue() / 12);
            int months = (int) (loanTermSlider.getValue() % 12);
            String yearString = years == 10 ? "metų" : "metai";
            String monthString = months == 1 ? "mėnesis" : "mėnesiai";
            if ((months >= 10) || (months < 1)) {monthString = "mėnesių";}
            return String.format("%d %s %d %s",years, yearString, months, monthString);
        }, loanTermSlider.valueProperty()));
        paymentList = FXCollections.observableArrayList();
        xAxis = new NumberAxis();
        yAxis = new NumberAxis();

    }
    void checkIfAllInputProvided() {
        fieldTextToDoubleOrInt();
        boolean paymentModeSelected = paymentMode.getSelectedToggle() != null;
        boolean amountProvided = loanAmount != null;
        boolean termProvided = loanTermSlider.getValue() > 0;

        if (paymentModeSelected && amountProvided && termProvided) inputProvided = true;
    }

    @SuppressWarnings("unchecked")
    @FXML
    void mainProcess() {
        checkIfAllInputProvided();
        if (inputProvided){

                delayStartValue = delayStart.getValue() == null ? (LocalDate.now().minusDays(1)) : delayStart.getValue();
                delayEndValue = delayEnd.getValue();

            delayPercentageLabel.setText("Mėnėsinis mokestis: " + DELAY_RATE + "%");

            currentDate = LocalDate.now();

        loanListGenerator();
        paymentTable.setItems(paymentList);

        TableColumn<Payment, Double> rawPaymentCol = new TableColumn<>("Principal percentage");
        TableColumn<Payment,LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        rawPaymentCol.setCellValueFactory(new PropertyValueFactory<>("rawPaymentFraction"));
        TableColumn<Payment, Double> interestFractionCol = new TableColumn<>("Interest percentage");
        interestFractionCol.setCellValueFactory(new PropertyValueFactory<>("interestFraction"));
        TableColumn<Payment, Double> unpaidLoanCol = new TableColumn<>("UnpaidLoan");
        unpaidLoanCol.setCellValueFactory(new PropertyValueFactory<>("unpaidLoan"));
        unpaidLoanCol.setCellFactory(Utils.getRoundedCellFactory());
        TableColumn<Payment, Double> totalPaymentCol = new TableColumn<>("Payment Sum");
        totalPaymentCol.setCellValueFactory(new PropertyValueFactory<>("totalPayment"));
        totalPaymentCol.setCellFactory(Utils.getRoundedCellFactory());
        rawPaymentCol.setCellFactory(Utils.getRoundedCellFactory());
        rawPaymentCol.setCellFactory(Utils.getRoundedCellFactory());
        interestFractionCol.setCellFactory(Utils.getRoundedCellFactory());


        paymentTable.getColumns().setAll(dateCol,totalPaymentCol, rawPaymentCol, interestFractionCol, unpaidLoanCol);

        xAxis.setLabel("Time (Months");
        yAxis.setLabel("Payment Amount");
        paymentChart = new LineChart<>(xAxis, yAxis);
        paymentChart.setTitle("Anuiteto ir linijinio mokejimu grafiku palyginimas");
        XYChart.Series<Number, Number> annuitySeries = new XYChart.Series<>();
        annuitySeries.setName("Annuity");
        XYChart.Series<Number, Number> linearSeries = new XYChart.Series<>();
        linearSeries.setName("Linear");
        this.unpaidLoan = loanAmount;
        int paymentCount = 0;
        for (int month = 1; month <= loanDuration; month++) {
            payment = new Payment(interestRate, loanAmount, loanDuration, unpaidLoan, paymentCount);
            payment.calculatePaymentGeneral("Annuity");
            double annuityPayment = payment.getTotalPayment();
            annuitySeries.getData().add(new XYChart.Data<>(month, annuityPayment));
            paymentCount = payment.getPaymentNumber();
            this.unpaidLoan = payment.getUnpaidLoan();
        }

        this.unpaidLoan = loanAmount;
        paymentCount = 0;
        for (int month = 1; month <= loanDuration; month++) {
            payment = new Payment(interestRate, loanAmount, loanDuration, unpaidLoan, paymentCount);
            payment.calculatePaymentGeneral("Linear");
            double annuityPayment = payment.getTotalPayment();
            linearSeries.getData().add(new XYChart.Data<>(month, annuityPayment));
            paymentCount = payment.getPaymentNumber();
            this.unpaidLoan = payment.getUnpaidLoan();
        }
        paymentChart.getData().addAll(annuitySeries,linearSeries);
        chartContainer.getChildren().add(paymentChart);

    }
    }

    @FXML
    void fieldTextToDoubleOrInt() {
        try {
            this.loanAmount = (double) Integer.parseInt(loanAmountField.getText());
            loanAmountError.setText(" ");
            unpaidLoan = loanAmount;
        } catch (NumberFormatException e) {
            try {
                this.loanAmount =  Double.parseDouble(loanAmountField.getText());
                loanAmountError.setText(" ");
                unpaidLoan = loanAmount;
            } catch (Exception e1) {
                loanAmountError.setText("Neteisinga paskolos suma!");
                this.loanAmount = null;
                unpaidLoan = null;
            }
        }

    }

    public void loanListGenerator() {
        int paymentCount =0;
        if(unpaidLoan.doubleValue() == loanAmount.doubleValue()) paymentList.clear();
        while (paymentCount < loanTermSlider.getValue()) {
            RadioButton selectedMethod = (RadioButton) paymentMode.getSelectedToggle();
            method = selectedMethod.getId();
            payment = new Payment(interestRate, this.loanAmount, loanDuration, unpaidLoan, paymentCount);
            payment.calculatePaymentGeneral(method);
            this.unpaidLoan = payment.getUnpaidLoan();
            paymentCount = payment.getPaymentNumber();
            payment.setPaymentDate(currentDate);
            currentDate = currentDate.plusMonths(1);
if(delayStartValue != null && delayEndValue != null) {
    if (!currentDate.isBefore(delayStartValue) && !currentDate.isAfter(delayEndValue)) {
        // Skip payments within the pause period
    } else {
        // Normal payment processing
        // Increase payment amount if after the pause period
        if (currentDate.isAfter(delayEndValue)) {
            long monthsDelayed = ChronoUnit.MONTHS.between(delayEndValue, currentDate);
            double adjustmentFactor = Math.pow(DELAY_RATE, monthsDelayed);

            payment.setTotalPayment(payment.getTotalPayment() * (1 + adjustmentFactor));
        }
    }
}
                paymentList.add(payment);

        }

    }

    @FXML
    void applyFilter() {
        int interval;
        try {
            interval = Integer.parseInt(intervalFilterField.getText());
            if (interval > 0) {
                ObservableList<Payment> filteredList = FXCollections.observableArrayList();
                for (int i = 0; i < paymentList.size(); i++) {
                    if ((i % interval) == 0) { // Keep every Nth item
                        filteredList.add(paymentList.get(i));
                    }
                }
                paymentTable.setItems(filteredList);
            } else {
                // Handle invalid input: set back to original list or show an error message
                paymentTable.setItems(paymentList); // Reset to original list if invalid input
                intervalError.setText("Neteisinga reikšmė");
            }
        } catch (NumberFormatException e) {
            // Handle case where input is not a number
            paymentTable.setItems(paymentList); // Reset to original list if invalid input
            // Optionally, show an error message to the user
        }
    }
    @FXML
    void saveToFile() {
        Stage primaryStage = (Stage) saveButton.getScene().getWindow(); // Assumes 'saveButton' is your button's fx:id

        // First, let's save the chart image
        FileChooser imageFileChooser = new FileChooser();
        imageFileChooser.setTitle("Save Chart as Image");
        imageFileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG Files", "*.png"));
        File imageFile = imageFileChooser.showSaveDialog(primaryStage);

        if (imageFile != null) {
            saveNodeAsImage(paymentChart, imageFile.getAbsolutePath());
        }

        // Now, let's save the table data
        FileChooser csvFileChooser = new FileChooser();
        csvFileChooser.setTitle("Save Table Data as CSV");
        csvFileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File csvFile = csvFileChooser.showSaveDialog(primaryStage);

        if (csvFile != null) {
            saveTableDataToCsv(paymentTable, csvFile.getAbsolutePath());
        }
    }

    public void saveNodeAsImage(Node node, String filePath) {
        WritableImage image = node.snapshot(new SnapshotParameters(), null);
        File file = new File(filePath);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveTableDataToCsv(TableView<Payment> tableView, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write column headers
            writer.append("Total Payment,Principal Percentage,Interest Percentage,Unpaid Loan\n");

            for (Payment payment : tableView.getItems()) {
                writer.append(String.format("%f,%s,%s,%f\n", payment.getTotalPayment(), payment.getRawPaymentFraction(), payment.getInterestFraction(), payment.getUnpaidLoan()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
