package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HRReportDTO {
    private int totalEmployees;
    private int maleCount;
    private int femaleCount;
    private int managerCount;
    private int employeeCount;
    private int regularCount;
    private int probationCount;
    private int totalIn;      // Tổng tuyển mới / chuyển đến trong kỳ
    private int totalOut;     // Tổng nghỉ việc / chuyển đi trong kỳ
    private int netChange;    // Biến động ròng = totalIn - totalOut

    private Map<String, Integer> contractTypeData = new HashMap<>();
    private Map<String, Integer> departmentData = new HashMap<>();

    public int getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(int totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public int getMaleCount() {
        return maleCount;
    }

    public void setMaleCount(int maleCount) {
        this.maleCount = maleCount;
    }

    public int getFemaleCount() {
        return femaleCount;
    }

    public void setFemaleCount(int femaleCount) {
        this.femaleCount = femaleCount;
    }

    public int getManagerCount() {
        return managerCount;
    }

    public void setManagerCount(int managerCount) {
        this.managerCount = managerCount;
    }

    public int getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(int employeeCount) {
        this.employeeCount = employeeCount;
    }

    public int getRegularCount() {
        return regularCount;
    }

    public void setRegularCount(int regularCount) {
        this.regularCount = regularCount;
    }

    public int getProbationCount() {
        return probationCount;
    }

    public void setProbationCount(int probationCount) {
        this.probationCount = probationCount;
    }

    public Map<String, Integer> getContractTypeData() {
        return contractTypeData;
    }

    public void setContractTypeData(Map<String, Integer> contractTypeData) {
        this.contractTypeData = contractTypeData;
    }

    public Map<String, Integer> getDepartmentData() {
        return departmentData;
    }

    public void setDepartmentData(Map<String, Integer> departmentData) {
        this.departmentData = departmentData;
    }

    public int getTotalIn() {
        return totalIn;
    }

    public void setTotalIn(int totalIn) {
        this.totalIn = totalIn;
    }

    public int getTotalOut() {
        return totalOut;
    }

    public void setTotalOut(int totalOut) {
        this.totalOut = totalOut;
    }

    public int getNetChange() {
        return netChange;
    }

    public void setNetChange(int netChange) {
        this.netChange = netChange;
    }
}