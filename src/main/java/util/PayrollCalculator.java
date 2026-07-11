package util;

import model.PitBracket;
import java.util.List;

public class PayrollCalculator {

    public Long calculateInsuranceAmount(long grossIncome, double ratePercentage) {
        return Math.round(grossIncome * (ratePercentage / 100.0));
    }

    public double calculateIncomeTax(long taxableIncome, List<PitBracket> brackets) {
        if (taxableIncome <= 0 || brackets == null || brackets.isEmpty()) return 0.0;

        double tax = 0.0;

        for (PitBracket bracket : brackets) {
            long min = bracket.getMinValue();
            Long max = bracket.getMaxValue();
            double rate = bracket.getTaxRate() / 100.0;

            if (taxableIncome <= min) {
                break;
            }

            long taxableAmountInBracket = 0;

            if (max == null || taxableIncome < max) {
                taxableAmountInBracket = (long) (taxableIncome - min);
                tax += Math.round(taxableAmountInBracket * rate);
                break;
            } else {
                taxableAmountInBracket = max - min;
                tax += Math.round(taxableAmountInBracket * rate);
            }
        }

        return tax;
    }

}
