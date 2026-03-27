/*
 * Copyright (c) 2026, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.java.cli.model;

import se.laz.casual.network.messages.domain.TransactionType;

import java.util.Objects;

public record Service(String name, String category, TransactionType transactionType, long timeout, long hops,
                      Connection connection) {
    public Service
    {
        Objects.requireNonNull(name, "name can not be null");
        Objects.requireNonNull(category, "category can not be null");
        Objects.requireNonNull(transactionType, "transactionType can not be null");
        Objects.requireNonNull(connection, "connection can not be null");
    }
}
