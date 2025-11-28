package com.via.vix.payroll.util;

public class DeductionUtil {

    public static double calculateSss(double salary) {
        if (salary < 4250) {
            return 180.00;
        } else if (salary >= 4250 && salary <= 4749.99) {
            return 202.50;
        } else if (salary >= 4750 && salary <= 5249.99) {
            return 225.00;
        } else if (salary >= 5250 && salary <= 5749.99) {
            return 247.50;
        } else if (salary >= 5750 && salary <= 6249.99) {
            return 270.00;
        } else if (salary >= 6250 && salary <= 6749.99) {
            return 292.50;
        } else if (salary >= 6750 && salary <= 7249.99) {
            return 315.00;
        } else if (salary >= 7250 && salary <= 7749.99) {
            return 337.50;
        } else if (salary >= 7750 && salary <= 8249.99) {
            return 360.00;
        } else if (salary >= 8250 && salary <= 8749.99) {
            return 382.50;
        } else if (salary >= 8750 && salary <= 9249.99) {
            return 405.00;
        } else if (salary >= 9250 && salary <= 9749.99) {
            return 427.50;
        } else if (salary >= 9750 && salary <= 10249.99) {
            return 450.00;
        } else if (salary >= 10250 && salary <= 10749.99) {
            return 472.50;
        } else if (salary >= 10750 && salary <= 11249.99) {
            return 495.00;
        } else if (salary >= 11250 && salary <= 11749.99) {
            return 517.50;
        } else if (salary >= 11750 && salary <= 12249.99) {
            return 540.00;
        } else if (salary >= 12250 && salary <= 12749.99) {
            return 562.50;
        } else if (salary >= 12750 && salary <= 13249.99) {
            return 585.00;
        } else if (salary >= 13250 && salary <= 13749.99) {
            return 607.50;
        } else if (salary >= 13750 && salary <= 14249.99) {
            return 630.00;
        } else if (salary >= 14250 && salary <= 14749.99) {
            return 652.50;
        } else if (salary >= 14750 && salary <= 15249.99) {
            return 675.00;
        } else if (salary >= 15250 && salary <= 15749.99) {
            return 697.50;
        } else if (salary >= 15750 && salary <= 16249.99) {
            return 720.00;
        } else if (salary >= 16250 && salary <= 16749.99) {
            return 742.50;
        } else if (salary >= 16750 && salary <= 17249.99) {
            return 765.00;
        } else if (salary >= 17250 && salary <= 17749.99) {
            return 787.50;
        } else if (salary >= 17750 && salary <= 18249.99) {
            return 810.00;
        } else if (salary >= 18250 && salary <= 18749.99) {
            return 832.50;
        } else if (salary >= 18750 && salary <= 19249.99) {
            return 855.00;
        } else if (salary >= 19250 && salary <= 19749.99) {
            return 877.50;
        } else if (salary >= 19750 && salary <= 20249.99) {
            return 900.00;
        } else if (salary >= 20250 && salary <= 20749.99) {
            return 922.50;
        } else if (salary >= 20750 && salary <= 21249.99) {
            return 945.00;
        } else if (salary >= 21250 && salary <= 21749.99) {
            return 967.50;
        } else if (salary >= 21750 && salary <= 22249.99) {
            return 990.00;
        } else if (salary >= 22250 && salary <= 22749.99) {
            return 1012.50;
        } else if (salary >= 22750 && salary <= 23249.99) {
            return 1035.00;
        } else if (salary >= 23250 && salary <= 23749.99) {
            return 1057.50;
        } else if (salary >= 23750 && salary <= 24249.99) {
            return 1080.00;
        } else if (salary >= 24250 && salary <= 24749.99) {
            return 1102.50;
        } else if (salary >= 24750) {
            return 1125.00;
        }
        return 0;
    }

    public static double calculatePhilhealth(double salary) {
        double premium = salary * 0.05;
        if (salary <= 10000) {
            premium = 500;
        } else if (salary > 100000) {
            premium = 5000;
        }
        return premium / 2;
    }

    public static double calculatePagibig(double salary) {
        if (salary <= 1500) {
            return salary * 0.01;
        } else {
            double contribution = salary * 0.02;
            return Math.min(contribution, 200.0);
        }
    }
}