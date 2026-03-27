/*
 * Copyright (c) 2026, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.java.cli.model;

import java.util.Objects;

public record Queue(String name, Connection connection) {
    public Queue
    {
        Objects.requireNonNull( name, "name can not be null" );
        Objects.requireNonNull( connection, "connection can not be null" );
    }
}
