/*
 * Copyright (c) 2026, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.java.cli.model;

import java.util.List;
import java.util.Objects;

public record Configuration(String jndiSearchRoot, int validationIntervalMillis, boolean transactionStickyEnabled,
                            long topologyChangeDelayMillis, String routeFileName) {
    public Configuration
    {
        Objects.requireNonNull(jndiSearchRoot, "jndiSearchRoot can not be null");
        Objects.requireNonNull(routeFileName, "routeFileName can not be null");
    }
}
