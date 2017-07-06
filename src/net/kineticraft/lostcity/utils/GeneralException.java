package net.kineticraft.lostcity.utils;

import lombok.Getter;
import net.kineticraft.lostcity.Core;

import java.io.PrintStream;

/**
 * A general exception that will alert staff when created.
 *
 * Created by Kneesnap on 7/4/2017.
 */
@Getter
public class GeneralException extends RuntimeException {

    private String alert;
    private Exception exception;

    public GeneralException(String alert) {
        this(alert, null);
    }

    public GeneralException(String alert, Exception e) {
        super(getMessage(alert, e), e);
        this.alert = alert;
        this.exception = e;
    }

    @Override
    public void printStackTrace(PrintStream s) {
        Core.warn(getAlert());
    }

    /**
     * Get the displayed error message.
     * @param alert - Alert for this exception.
     * @param e - Exception
     * @return message
     */
    private static String getMessage(String alert, Exception e) {
        return e != null && e.getLocalizedMessage() != null ?
                (e instanceof GeneralException ? e.getLocalizedMessage() : alert + " (" + e.getLocalizedMessage() + ")")
                : alert;
    }
}
