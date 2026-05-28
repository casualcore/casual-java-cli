/*
 * Copyright (c) 2026, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.java.cli;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import se.laz.casual.java.cli.model.Configuration;
import se.laz.casual.java.cli.model.Queue;
import se.laz.casual.java.cli.model.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static se.laz.casual.java.cli.CLIService.TEST_CONFIGURATION;
import static se.laz.casual.java.cli.CLIService.TEST_QUEUE_CONNECTION_1;
import static se.laz.casual.java.cli.CLIService.TEST_Q_1;
import static se.laz.casual.java.cli.CLIService.TEST_Q_2;
import static se.laz.casual.java.cli.CLIService.TEST_SERVICE_1;
import static se.laz.casual.java.cli.CLIService.TEST_SERVICE_2;
import static se.laz.casual.java.cli.CLIService.TEST_SERVICE_CONNECTION_1;
import static se.laz.casual.java.cli.CLIService.TEST_SERVICE_CONNECTION_2;

class CLIServiceTest
{
    CLIService cliService = new CLIService();

    @DisplayName("Test get configuration")
    @Test
    void testGetConfiguration()
    {
        Configuration configuration = cliService.getConfiguration();
        Assertions.assertNotNull(configuration);
        Assertions.assertEquals(TEST_CONFIGURATION, configuration);
    }

    @DisplayName("Test get connections")
    @Test
    void testGetConnections()
    {
        Assertions.assertEquals(List.of(TEST_SERVICE_CONNECTION_1, TEST_SERVICE_CONNECTION_2, TEST_QUEUE_CONNECTION_1),
                cliService.getConnections());
    }

    private static Stream<Arguments> services() {
        return Stream.of(
                Arguments.of(TEST_SERVICE_1.name(), List.of(TEST_SERVICE_1)),
                Arguments.of(TEST_SERVICE_2.name(), List.of(TEST_SERVICE_2)),
                Arguments.of("NO_MATCH", new ArrayList<>())
        );
    }

    @DisplayName("Test get service")
    @ParameterizedTest
    @MethodSource("services")
    void testDiscoverService(String serviceName, List<Service> expectedServices)
    {
        Assertions.assertEquals(expectedServices, cliService.discoverService(serviceName));
    }

    @DisplayName("Test get services")
    @Test
    void testDiscoverServices()
    {
        Assertions.assertEquals(List.of(TEST_SERVICE_1, TEST_SERVICE_2), cliService.getServices());
    }

    @DisplayName("Test get queues")
    @Test
    void testGetQueues()
    {
        Assertions.assertEquals(List.of(TEST_Q_1, TEST_Q_2), cliService.discoverQueues());
    }

    private static Stream<Arguments> queues() {
        return Stream.of(
                Arguments.of(TEST_Q_1.name(), TEST_Q_1),
                Arguments.of(TEST_Q_2.name(), TEST_Q_2)
        );
    }

    @DisplayName("Test get queue")
    @ParameterizedTest
    @MethodSource("queues")
    void testGetQueue(String queueName, Queue expectedQueue)
    {
        Assertions.assertEquals(expectedQueue, cliService.getQueue(queueName));
    }

    @DisplayName("Test get queue, no match")
    @Test
    void testGetQueueNoMatch()
    {
        Assertions.assertNull(cliService.getQueue("NO_QUEUE"));
    }
}
