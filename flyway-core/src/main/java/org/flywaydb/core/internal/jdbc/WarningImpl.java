package org.flywaydb.core.internal.jdbc;

import lombok.RequiredArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.flywaydb.core.api.callback.Warning;

@RequiredArgsConstructor
@Getter(onMethod = @__(@Override))
public class WarningImpl implements Warning {
    private final int code;
    private final String state;
    private final String message;
    @Setter(onMethod = @__(@Override))
    private boolean handled;
}