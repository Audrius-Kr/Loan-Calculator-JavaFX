package org.example.antrauzd1;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class HelloController {
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
    RadioButton linear;
    @FXML
    RadioButton annual;
    @FXML
    TableView<Double> paymentTable;
    ToggleGroup paymentMode;
    ObservableList<Double> paymentList;
    Double loanDuration = 0.0;
    Double interestRate = 0.05;
    Double APR = 0.06;
    Loan loan;
    String method;
    @FXML
    TableColumn<Double, Double> paymentColumn;





    Double loanAmount; //check if not null



    public void initialize() {
        loanAmount = null;
        paymentMode = new ToggleGroup();
        annuity.setId("Annuity");
        linear.setId("Linear");
        annual.setId("APR");
        annuity.setToggleGroup(paymentMode);
        linear.setToggleGroup(paymentMode);
        annual.setToggleGroup(paymentMode);
        paymentMode.selectedToggleProperty().addListener((observable, oldValue, newValue) -> checkIfAllInputProvided());
        loanAmountField.textProperty().addListener((observable, oldValue, newValue) -> checkIfAllInputProvided());
        loanTermSlider.valueProperty().addListener((observable, oldValue, newValue) -> checkIfAllInputProvided());
        loanTermLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            loanDuration = loanTermSlider.getValue();
            int years = (int) (loanTermSlider.getValue() / 12);
            int months = (int) (loanTermSlider.getValue() % 12);
            String yearString = years == 10 ? "metų" : "metai";
            String monthString = months == 1 ? "mėnesis" : "mėnesiai";
            if ((months >= 10) || (months < 1)) {monthString = "mėnesių";}
            return String.format("%d %s %d %s",years, yearString, months, monthString);
        }, loanTermSlider.valueProperty()));
        paymentColumn = new TableColumn<>("Periodic payment");
        paymentColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        List<TableColumn<Double, Double>> columns = new ArrayList<>();
        columns.add(paymentColumn);
        paymentTable.getColumns().setAll(columns);
    }
    void checkIfAllInputProvided() {
        fieldTextToDoubleOrInt();
        boolean paymentModeSelected = paymentMode.getSelectedToggle() != null;
        boolean amountProvided = loanAmount != null;
        boolean termProvided = loanTermSlider.getValue() > 0;

        if (paymentModeSelected && amountProvided && termProvided) {
            System.out.println(loanAmount);
            RadioButton selectedMethod = (RadioButton) paymentMode.getSelectedToggle();
            method = selectedMethod.getId();
            System.out.println(loanTermSlider.getValue());
            loan = new Loan(interestRate, loanAmount, loanTermSlider.getValue());
            paymentList = loan.generatePaymentList(method);
            paymentTable.setItems(paymentList);

        }

    }
    @FXML
    void fieldTextToDoubleOrInt() {
        try {
            this.loanAmount = (double) Integer.parseInt(loanAmountField.getText());
            loanAmountError.setText(" ");
        } catch (NumberFormatException e) {
            try {
                this.loanAmount =  Double.parseDouble(loanAmountField.getText());
                loanAmountError.setText(" ");
            } catch (Exception e1) {
                loanAmountError.setText("Neteisinga paskolos suma!");
                this.loanAmount = null;
                System.out.println(" loan amount set to null");
            }
        }

    }








}
