package model;

public class DeptEmployeeChangeDTO {
    private String deptName;
    private int inCount;   // Số nhân sự chuyển ĐẾN phòng ban
    private int outCount;  // Số nhân sự chuyển ĐI khỏi phòng ban
    private int netCount;  // Chênh lệch

    public DeptEmployeeChangeDTO(String deptName, int inCount, int outCount) {
        this.deptName = deptName;
        this.inCount = inCount;
        this.outCount = outCount;
        this.netCount = inCount - outCount;
    }

    public String getDeptName() { return deptName; }
    public int getInCount() { return inCount; }
    public int getOutCount() { return outCount; }
    public int getNetCount() { return netCount; }
}