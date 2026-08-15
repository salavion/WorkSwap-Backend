package org.workswap.task.enums;

public enum TaskStatus {
    NEW("Новая"),
    IN_PROGRESS("В процессе"),
    COMPLETED("Завершена"),
    CANCELED("Отменена");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}