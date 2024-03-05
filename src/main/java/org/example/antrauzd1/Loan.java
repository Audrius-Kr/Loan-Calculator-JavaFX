package org.example.antrauzd1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Loan {
    public static final int PERIOD = 12;

    double interestRate;
    double principal;
    double years;
    ObservableList<Double> payments;

    public Loan(double interestRate, double principal, double years) {
        this.interestRate = interestRate;
        this.principal = principal;
        this.years = (years / 12);
        payments = FXCollections.observableArrayList();
    }

    public ObservableList<Double> generatePaymentList(String method) {
        payments.clear();
        double loanAmount = this.principal;
        double payment;
        switch (method) {
            case "Annuity":
                    payment = calculateAnnuityPayment(loanAmount, this.interestRate, this.years);
                    for (double i = 0; i < this.years * PERIOD; i++){
                        payments.add(payment);
                    }
                return this.payments;
            case "Linear":
                for (double i = 0; i < this.years * PERIOD; i++) {
                    payment = calculateLinearPayment(this.interestRate, this.years, i);
                    payments.add(payment);
                }
                return this.payments;
            case "APR":
                payment = calculateYearlyPayment(loanAmount, interestRate,years);
                for (double i = 0; i < (int)(this.years); i++) {
                    payments.add(payment);
                }
                return this.payments;
            default:
                return null;
        }


    }

    double calculateAnnuityPayment(double principal, double annualInterestRate, double years) {
        double r = annualInterestRate / PERIOD;
        double n = years * PERIOD;
        return (r * principal) / (1 - Math.pow(1 + r, -n));
    }

    double calculateLinearPayment(double annualInterestRate, double years, double paymentNumber) {
        double totalPayments = years * PERIOD;
        double monthlyInterestRate = annualInterestRate / PERIOD;
        return (this.principal / totalPayments) + ((this.principal - (this.principal / totalPayments) * (paymentNumber)) * monthlyInterestRate);
    }

    double calculateYearlyPayment(double principal, double interest, double years) {
        double compoundInterestFactor = Math.pow((1 + interest), -years);
        return (principal * interest) / (1-compoundInterestFactor);
    }

}
