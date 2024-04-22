package ru.korobtsov.server.auth.service;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.springframework.stereotype.Component;
import ru.korobtsov.server.auth.AuthenticationResult;
import ru.korobtsov.server.auth.Principal;

@Component
public class NoopAuthenticationService implements AuthenticationService {

    @Override
    public ListenableFuture<AuthenticationResult> authenticate(Principal principal) {
        return Futures.immediateFuture(new AuthenticationResult(true));
    }
}
