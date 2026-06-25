package com.hotel.common;

import lombok.Data;

@Data
public class UserContext {
    private static final ThreadLocal<Integer> currentEmpId = new ThreadLocal<>();
    private static final ThreadLocal<String> currentEmpName = new ThreadLocal<>();
    private static final ThreadLocal<String> currentRole = new ThreadLocal<>();

    public static void set(Integer empId, String empName, String role) {
        currentEmpId.set(empId);
        currentEmpName.set(empName);
        currentRole.set(role);
    }

    public static Integer getEmpId() { return currentEmpId.get(); }
    public static String getEmpName() { return currentEmpName.get(); }
    public static String getRole() { return currentRole.get(); }

    public static void clear() {
        currentEmpId.remove();
        currentEmpName.remove();
        currentRole.remove();
    }
}