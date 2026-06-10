/*
 * Copyright (c) 2026, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.java.cli.model;

public record ServiceStatistics(char order, long count, long min, long max, long last, long total)
{
}
