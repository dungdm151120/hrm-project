package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HRReportDTO {
    private int totalEmployees;
    private int maleCount;
    private int femaleCount;
    private int otherCount;
    private int regularCount;
    private int probationCount;

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

    public int getOtherCount() {
        return otherCount;
    }

    public void setOtherCount(int otherCount) {
        this.otherCount = otherCount;
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
}