package net.kineticraft.lostcity.utils;

import net.kineticraft.lostcity.Core;

import java.io.PrintStream;

/**
 * A general exception that will alert staff when created.
 *
 * Created by Kneesnap on 7/4/2017.
 */
public class GeneralException extends RuntimeException {

    public GeneralException(String alert) {
        this(alert, null);
    }

    public GeneralException(String alert, Exception e) {
        super(alert + (e != null ? " (" + e.getLocalizedMessage() + ")" : ""));
    }

    @Override
    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
        Core.warn(getLocalizedMessage());
    }
}
