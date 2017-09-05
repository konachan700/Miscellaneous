package ru.jneko.puseradmin.MA4000;

class Command {
    private boolean hasReturnData;
    private boolean hasReplacebleNumber;
    private String command;

    public Command(String cmd, boolean isRet, boolean isSlot) {
        hasReturnData = isRet;
        command = cmd;
        hasReplacebleNumber = isSlot;
    }

    public boolean isHasReturnData() {
        return hasReturnData;
    }

    public void setHasReturnData(boolean hasReturnData) {
        this.hasReturnData = hasReturnData;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public boolean isHasReplacebleNumber() {
        return hasReplacebleNumber;
    }

    public void setHasReplacebleNumber(boolean hasReplacebleNumber) {
        this.hasReplacebleNumber = hasReplacebleNumber;
    }
}
