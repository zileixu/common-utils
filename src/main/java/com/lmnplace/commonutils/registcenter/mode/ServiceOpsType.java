package com.lmnplace.commonutils.registcenter.mode;

public enum ServiceOpsType {
    START("startCmd"), STOP("stopCmd"), RESTART("restartCmd"), STATUS("statusCmd");
    private String val;

    private ServiceOpsType(String val) {
        this.val = val;
    }

    public String val() {
        return this.val;
    }

    public static ServiceOpsType cmdkey(String action) {
        ServiceOpsType[] types = ServiceOpsType.values();
        for (ServiceOpsType type : types) {
            if (type.name().equals(action.toUpperCase())) {
                return type;
            }
        }
        throw new RuntimeException("操作行为不合法，行为：" + action);

    }

    public static void main(String[] args) {
        System.out.println(ServiceOpsType.cmdkey("start"));
    }
}
