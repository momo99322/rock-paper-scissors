package ru.korobtsov.server.handler;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class HandlingResult {

    private final String response;

    private final boolean handled;

    private final Throwable error;

    private final TransitionType transitionType;

    private HandlingResult(String response, boolean handled, Throwable error, TransitionType transitionType) {
        this.response = response;
        this.handled = handled;
        this.error = error;
        this.transitionType = transitionType;
    }

    public static HandlingResult notHandled() {
        return new HandlingResult(null, false, null, TransitionType.NO_TRANSITION);
    }

    public static HandlingResult handledSuccessfully(String response, TransitionType transitionType) {
        return new HandlingResult(response, true, null, transitionType);
    }

    public static HandlingResult handledExceptionally(String response, Throwable error) {
        return new HandlingResult(response, true, error, TransitionType.NO_TRANSITION);
    }

    public static HandlingResult handledQuit() {
        return new HandlingResult(null, true, null, TransitionType.QUIT);
    }
}
