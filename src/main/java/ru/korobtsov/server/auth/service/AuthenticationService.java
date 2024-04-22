package ru.korobtsov.server.auth.service;

import com.google.common.util.concurrent.ListenableFuture;
import ru.korobtsov.server.auth.AuthenticationResult;
import ru.korobtsov.server.auth.Principal;

public interface AuthenticationService {

    ListenableFuture<AuthenticationResult> authenticate(Principal principal);
}
