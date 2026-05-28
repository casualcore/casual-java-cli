/*
 * Copyright (c) 2026, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.java.cli.model;

import java.util.Objects;

public record Connection(String jndiName, boolean valid) {
    public Connection
    {
        Objects.requireNonNull(jndiName, "jndiName can not be null");
    }
}
