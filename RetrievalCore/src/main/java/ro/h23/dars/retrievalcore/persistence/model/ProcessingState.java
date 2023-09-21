package ro.h23.dars.retrievalcore.persistence.model;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum ProcessingState {
    NEW, IN_PROGRESS, COMPLETE;

    /*
    public static String getStateArrayString() {
        return Arrays.stream(values()).map(state -> "'" + state.name() + "'").collect(Collectors.joining(","));
    }
     */
}
