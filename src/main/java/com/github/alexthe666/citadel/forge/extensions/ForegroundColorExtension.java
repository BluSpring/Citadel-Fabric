package com.github.alexthe666.citadel.forge.extensions;

public interface ForegroundColorExtension {
    int UNSET_FG_COLOR = -1;

    default int citadel$getFGColor() {
        throw new IllegalStateException("supposed to be overridden");
    }
    default void citadel$setFGColor(int value) {
        throw new IllegalStateException("supposed to be overridden");
    }
    default void citadel$clearFGColor() {
        throw new IllegalStateException("supposed to be overridden");
    }
}