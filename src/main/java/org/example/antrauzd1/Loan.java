package org.example.antrauzd1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class Loan {
    public static final int PERIOD = 12;

    double interestRate;
    double principal;
    double years;
    double APR;
    ObservableList<Double> payments;

    public Loan(double interestRate, double principal, double years, double APR) {
        this.interestRate = interestRate;
        System.out.println("interest rate " + interestRate);
        this.principal = principal;
        System.out.println("principal " + principal);
        this.years = (years / 12);
        System.out.println("years " + this.years);
        this.APR = APR;
        System.out.println("apr " + APR);
        payments = FXCollections.observableArrayList();
    }

    public ObservableList<Double> generatePaymentList(String method) {
        payments.clear();
        return null;
        /*
        double loanAmount = this.principal;
        switch (method) {
            case "Annuity":
                while (loanAmount > 0) {
                    double payment = calculateAnnuityPayment(loanAmount, this.interestRate, this.years);
                    payments.add(payment);
                    System.out.println(payment + " payment");
                    System.out.println(loanAmount+ "loan amount");
                    double interest = loanAmount * this.interestRate / PERIOD;
                    loanAmount -= (payment - interest);
                }
                return this.payments;
            case "Linear":
                while (loanAmount > 0) {
                    double payment = calculateLinearPayment(loanAmount, this.interestRate, this.years);
                    payments.add(payment);
                    double interest = loanAmount * this.interestRate / PERIOD;
                    loanAmount -= (payment - interest);
                }
                return this.payments;
            case "APR":
                while (loanAmount > 0) {
                    double payment = calculateAPRPayment(loanAmount, this.APR, this.years);
                    payments.add(payment);
                    double interest = loanAmount * this.APR / PERIOD;
                    loanAmount -= (payment - interest);
                }
                return this.payments;
            default:
                return null;
        }

         */
    }

    double calculateAnnuityPayment(double principal, double annualInterestRate, double years) {
        double r = annualInterestRate / PERIOD;
        double n = years * PERIOD;
        return (r * principal) - (1 - Math.pow(1 + r, -n));
    }

    double calculateLinearPayment(double principal, double annualInterestRate, double years) {
        double totalPayments = years * PERIOD;
        double monthlyInterestRate = annualInterestRate / PERIOD;
        return (principal / totalPayments) + (principal * monthlyInterestRate);
    }

    double calculateAPRPayment(double principal, double APR, double years) {

        double compoundInterestFactor = Math.pow((1 + APR / PERIOD), PERIOD * years);
        return principal * (APR / PERIOD) * compoundInterestFactor / (compoundInterestFactor -1);
    }

}
