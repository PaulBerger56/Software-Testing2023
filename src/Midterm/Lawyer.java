package Midterm;

import Midterm.Employee;

public class Lawyer extends Employee {

    public Lawyer(String name) {
        super(name);
    }

    public int getVacationDays() {
        return 1;
    }
}
