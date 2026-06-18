package util;

public class PayrollCalculator {

    private static final double SOCIAL_INSURANCE_RATE = 0.08;
    private static final double HEALTH_INSURANCE_RATE = 0.015;
    private static final double UNEMPLOYMENT_INSURANCE_RATE = 0.01;

    public double calculateSocialInsurance(double totalIncome) {
        return totalIncome * SOCIAL_INSURANCE_RATE;
    }

    public double calculateHealthInsurance(double totalIncome) {
        return totalIncome * HEALTH_INSURANCE_RATE;
    }

    public double calculateUnemploymentInsurance(double totalIncome) {
        return totalIncome * UNEMPLOYMENT_INSURANCE_RATE;
    }

    public double calculateIncomeTax(double taxableIncome) {
        if (taxableIncome <= 0) return 0.0;

        double tax = 0.0;

        if (taxableIncome <= 10000000) {
            tax = taxableIncome * 0.05;
        }
        else if (taxableIncome <= 30000000) {
            tax = (10000000 * 0.05)
                    + ((taxableIncome - 10000000) * 0.10);
        }
        else if (taxableIncome <= 60000000) {
            tax = (10000000 * 0.05)
                    + (20000000 * 0.10)
                    + ((taxableIncome - 30000000) * 0.20);
        }
        else if (taxableIncome <= 100000000) {
            tax = (10000000 * 0.05)
                    + (20000000 * 0.10)
                    + (30000000 * 0.20)
                    + ((taxableIncome - 60000000) * 0.30);
        }
        else {
            tax = (10000000 * 0.05)
                    + (20000000 * 0.10)
                    + (30000000 * 0.20)
                    + (40000000 * 0.30)
                    + ((taxableIncome - 100000000) * 0.35);
        }

        return tax;
    }

}
