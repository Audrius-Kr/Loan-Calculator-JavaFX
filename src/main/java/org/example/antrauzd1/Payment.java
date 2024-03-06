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

     DoubleProperty rawPaymentFraction;
     DoubleProperty interestFraction ;
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
        setInterestFraction((((interestRate / PERIOD * principal) / periodicPayment) * updatedPrincipalPayment));
        setRawPaymentFraction(periodicPayment - getInterestFraction());
        setUnpaidLoan(unpaidLoan.getValue() - (periodicPayment - ((interestRate / PERIOD * principal) / periodicPayment) * updatedPrincipalPayment));



    }

    void calculateLinearPayment() {
        double monthlyInterestRate = interestRate / PERIOD;
        double periodicPayment = (principal / duration) + (getUnpaidLoan() * monthlyInterestRate);
        setInterestFraction(((getUnpaidLoan() * monthlyInterestRate) ));
        setTotalPayment(periodicPayment);
        setRawPaymentFraction(periodicPayment - getInterestFraction());
        setUnpaidLoan(unpaidLoan.getValue() - (principal / duration));
    }


    public DoubleProperty interestFractionProperty() {
        if (interestFraction == null) interestFraction = new SimpleDoubleProperty(this, "interestPaid");
        return interestFraction;
    }

    public DoubleProperty rawPaymentFractionProperty() {
        if (rawPaymentFraction == null) rawPaymentFraction = new SimpleDoubleProperty(this, "rawPayment");
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
    public void setInterestFraction(Double value) { interestFractionProperty().set(value); }
    public void setTotalPayment(Double value) { totalPaymentProperty().set(value); }
    public void setRawPaymentFraction(Double value) { rawPaymentFractionProperty().set(value); }
    public void setUnpaidLoan(Double value) { unpaidLoanProperty().set(value); }
    public Double getInterestFraction() { return interestFractionProperty().get(); }
    public Double getTotalPayment() { return totalPaymentProperty().get(); }

    public Double getRawPaymentFraction() { return rawPaymentFractionProperty().get(); }

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
