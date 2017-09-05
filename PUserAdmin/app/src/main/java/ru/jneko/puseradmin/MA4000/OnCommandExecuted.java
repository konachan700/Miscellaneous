package ru.jneko.puseradmin.MA4000;

/**
 * Created by misaki on 29.06.17.
 */

interface OnCommandExecuted {
    public void commandExecuted(Commands group, Command current);
    public void completed();
}
