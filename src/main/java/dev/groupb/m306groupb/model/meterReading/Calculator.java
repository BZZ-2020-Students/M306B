package dev.groupb.m306groupb.model.meterReading;

import dev.groupb.m306groupb.enums.EconomicActivity;

import java.time.LocalDate;

public class Calculator {
    private LocalDate date;
    private double value;
    private EconomicActivity economicActivity;

    public Calculator() {

    }

    public void calculateValue() {

    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public EconomicActivity getEconomicActivity() {
        return economicActivity;
    }

    public void setEconomicActivity(EconomicActivity economicActivity) {
        this.economicActivity = economicActivity;
    }
}
