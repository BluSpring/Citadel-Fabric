package com.github.alexthe666.citadel.fabric.extensions;

public interface ForegroundColorExtension {
    int UNSET_FG_COLOR = -1;

    default int getFGColor() {
        throw new IllegalStateException("supposed to be overridden");
    }
    default void setFGColor(int value) {
        throw new IllegalStateException("supposed to be overridden");
    }
    default void clearFGColor() {
        throw new IllegalStateException("supposed to be overridden");
    }
}
