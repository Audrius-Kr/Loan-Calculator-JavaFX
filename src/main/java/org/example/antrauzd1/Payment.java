package org.example.antrauzd1;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Payment {
    public static final int PERIOD = 12;

    double interestRate;
    double duration;
     double principal;




    LocalDate paymentDate;
     int paymentNumber;

     StringProperty rawPaymentFraction;
     StringProperty interestFraction ;
     DoubleProperty totalPayment;
     DoubleProperty unpaidLoan;


    public Payment(double interestRate, double principal, double months, double unPaidLoan, int paymentNumber) {
        this.interestRate = interestRate;
        this.principal = principal;
        this.duration = months;
        this.paymentNumber = ++paymentNumber;
        setUnpaidLoan(unPaidLoan);
    }

    void calculateAnnuityPayment() {
        double r = interestRate / PERIOD;
        double periodicPayment = (r * principal) / (1 - Math.pow(1 + r, -duration));
        double updatedPrincipalPayment = (r * getUnpaidLoan()) / (1 - Math.pow(1 + r, -duration));
        setTotalPayment(periodicPayment);
        setInterestFraction(NumberFormatter.doubleToPercentageString((((interestRate / PERIOD * principal) / periodicPayment))));
        Double interestFraction = parsePercentage(getInterestFraction());
        setRawPaymentFraction(NumberFormatter.doubleToPercentageString((1- interestFraction)  ));
        setUnpaidLoan(unpaidLoan.getValue() - (periodicPayment - (interestFraction * updatedPrincipalPayment)));



    }

    void calculateLinearPayment() {
        double monthlyInterestRate = interestRate / PERIOD;
        double periodicPayment = (principal / duration) + (getUnpaidLoan() * monthlyInterestRate);
        setInterestFraction(NumberFormatter.doubleToPercentageString(((getUnpaidLoan() * monthlyInterestRate) / periodicPayment) ));
        setTotalPayment((principal / duration) + ((principal - getUnpaidLoan()) * monthlyInterestRate));
        setRawPaymentFraction(NumberFormatter.doubleToPercentageString(((principal / duration) / periodicPayment)));
        setUnpaidLoan(unpaidLoan.getValue() - (principal / duration));
    }


    public StringProperty interestFractionProperty() {
        if (interestFraction == null) interestFraction = new SimpleStringProperty(this, "interestPaid");
        return interestFraction;
    }

    public StringProperty rawPaymentFractionProperty() {
        if (rawPaymentFraction == null) rawPaymentFraction = new SimpleStringProperty(this, "rawPayment");
        return rawPaymentFraction;
    }

    public DoubleProperty unpaidLoanProperty() {
        if (unpaidLoan == null) unpaidLoan = new SimpleDoubleProperty(this, "unpaidLoan");
        return unpaidLoan;
    }
    public DoubleProperty totalPaymentProperty() {
        if (totalPayment == null) totalPayment = new SimpleDoubleProperty(this, "totalPayment");
        return totalPayment;
    }
    public void setInterestFraction(String value) { interestFractionProperty().set(value); }
    public void setTotalPayment(Double value) { totalPaymentProperty().set(value); }
    public void setRawPaymentFraction(String value) { rawPaymentFractionProperty().set(value); }
    public void setUnpaidLoan(Double value) { unpaidLoanProperty().set(value); }
    public String getInterestFraction() { return interestFractionProperty().get(); }
    public Double getTotalPayment() { return totalPaymentProperty().get(); }

    public String getRawPaymentFraction() { return rawPaymentFractionProperty().get(); }

    public Double getUnpaidLoan() { return unpaidLoanProperty().get(); }

    public void calculatePaymentGeneral(String method) {
        switch (method) {
            case "Annuity":
                calculateAnnuityPayment();
                return;
            case "Linear":
                calculateLinearPayment();
        }
    }

    Double parsePercentage(String percentageString) {

        try {


            return NumberParser.percentageStringToDouble(percentageString);

        } catch (NumberFormatException e) {
            System.err.println("Error parsing percentage string to float: " + e.getMessage());
            System.out.println(percentageString);
            return 0.0;
        } catch (Exception e) { // Catch any other exceptions
            System.err.println("Unexpected error during parsing: " + e.getMessage());
        }return 0.0;
    }

    public int getPaymentNumber() {
        return this.paymentNumber;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }
    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

}
