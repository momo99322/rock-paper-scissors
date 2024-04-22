package ru.korobtsov.server.transport.handler;

import com.google.common.util.concurrent.ListenableFuture;
import ru.korobtsov.server.handler.HandlingResult;

public record Response(String instantResponse, ListenableFuture<HandlingResult> futureResponse) {
}