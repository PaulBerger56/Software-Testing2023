package Midterm;

public class Employee {

    private String name;
    private long addTime;

    public Employee(String name) {
        this.name = name;
        this.addTime =System.currentTimeMillis();
    }

    public int getVacationDays() {
        return 20;
    }

    public String getName() {
        return name;
    }

    public long getAddTime() {
        return addTime;
    }

    public String toString() {
        return name + " " + addTime;
    }
}
