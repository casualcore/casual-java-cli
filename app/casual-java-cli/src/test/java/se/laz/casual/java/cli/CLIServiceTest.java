/*
 * Copyright (c) 2026, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.java.cli;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.laz.casual.info.CasualInfo;
import se.laz.casual.java.cli.model.Configuration;
import se.laz.casual.java.cli.model.Queue;
import se.laz.casual.java.cli.model.Service;
import se.laz.casual.network.messages.domain.TransactionType;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.mockStatic;
import static se.laz.casual.java.cli.CLIService.TEST_CONFIGURATION;
import static se.laz.casual.java.cli.CLIService.TEST_QUEUE_CONNECTION_1;
import static se.laz.casual.java.cli.CLIService.TEST_Q_1;
import static se.laz.casual.java.cli.CLIService.TEST_Q_2;
import static se.laz.casual.java.cli.CLIService.TEST_SERVICE_CONNECTION_1;
import static se.laz.casual.java.cli.CLIService.TEST_SERVICE_CONNECTION_2;

class CLIServiceTest
{
    @InjectMocks
    CLIService cliService = new CLIService();

    @Mock
    se.laz.casual.connection.caller.info.CasualInfo casualCallerInfo;

    @BeforeEach
    void setUp()
    {
        MockitoAnnotations.openMocks(this);
    }

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

    @DisplayName("Test discover service")
    @Test
    void testDiscoverService()
    {
        Mockito.doNothing().when( casualCallerInfo ).discoverService( "someService" );
        Assertions.assertEquals( Collections.emptyList(), cliService.discoverService("someService"));
    }

    @DisplayName("Test get services")
    @Test
    void testGetServices()
    {
        se.laz.casual.info.Service casualJCAService = new se.laz.casual.info.Service.Builder()
                .name( "someService" )
                .hops( 1 )
                .category( "someCategory" )
                .transactionType( TransactionType.ATOMIC )
                .timeout( 1000 )
                .build();
        se.laz.casual.connection.caller.info.Service casualCallerService = new se.laz.casual.connection.caller.info.Service.Builder()
                .name( "someServiceJCA" )
                .hops( 0 )
                .jndiName( "someJNDIName" )
                .valid( true ).build();
        Service jcaService = Util.toService(casualJCAService);
        Service callerService = Util.toService(casualCallerService);

        // Mock Casual JCA/caller services
        Mockito.when( casualCallerInfo.getServices() ).thenReturn( List.of(casualCallerService ) );
        try ( MockedStatic<CasualInfo> ci = mockStatic( CasualInfo.class))
        {
            ci.when( CasualInfo::getServices ).thenReturn( List.of(casualJCAService ) );
            Assertions.assertEquals(List.of(jcaService, callerService), cliService.getServices());
        }
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
