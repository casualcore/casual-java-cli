/*
 * Copyright (c) 2026, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.java.cli;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import se.laz.casual.info.CasualInfo;
import se.laz.casual.java.cli.event.EventServiceStatistics;
import se.laz.casual.java.cli.event.EventStore;
import se.laz.casual.java.cli.model.Configuration;
import se.laz.casual.java.cli.model.Connection;
import se.laz.casual.java.cli.model.Queue;
import se.laz.casual.java.cli.model.Service;
import se.laz.casual.network.messages.domain.TransactionType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class CLIService
{
    private static final Logger LOG = Logger.getLogger(CLIService.class.getName());
    protected static final Connection TEST_SERVICE_CONNECTION_1 = new Connection("jndi:test", true);
    protected static final Connection TEST_SERVICE_CONNECTION_2 = new Connection("jndi:test-unvalid", false);
    protected static final Service TEST_SERVICE_1 = new Service.Builder()
            .name("test").category("1").transactionType(TransactionType.ATOMIC.name()).timeout(1000)
            .hops(1).connection(TEST_SERVICE_CONNECTION_1).build();
    protected static final Service TEST_SERVICE_2 = new Service.Builder()
            .name("test/service2").category("2").transactionType(TransactionType.JOIN.name()).timeout(1000)
            .hops(3).connection(TEST_SERVICE_CONNECTION_2).build();
    protected static final Connection TEST_QUEUE_CONNECTION_1 = new Connection("jndi:test-queue", true);
    protected static final Queue TEST_Q_1 = new Queue("testQ1", TEST_QUEUE_CONNECTION_1);
    protected static final Queue TEST_Q_2 = new Queue("testQ2", TEST_QUEUE_CONNECTION_1);
    protected static final Configuration TEST_CONFIGURATION = new Configuration("jndi:test", 1000, false, 2000, "/test");

    @EJB(lookup = "java:global/casual-caller-app/casual-caller/CasualInfoImpl!se.laz.casual.connection.caller.info.CasualInfo")
    se.laz.casual.connection.caller.info.CasualInfo casualCallerInfo;

    protected CLIService()
    {
        // CDI
    }

    /**
     * TODO: Implement
     *
     * @return - Return configuration for casual-jca and casual-caller
     */
    public Configuration getConfiguration()
    {
        LOG.log(Level.FINE, "called getConfiguration()");
        return TEST_CONFIGURATION;
    }

    /**
     * TODO: ConnectionFactoriesByPriority from casual-caller does not save ServiceDetails for cached services.
     * Category, transactionType and timeout cannot be added from cached services.
     *
     * @return - List of known service names
     */
    public List<Service> getServices()
    {
        LOG.log(Level.FINE, "called getServices()");
        List<Service> services = new ArrayList<>();
        // Add services from casual jca
        CasualInfo.getServices().forEach(cs -> services.add(Util.toService(cs)));
        services.forEach(service -> {
            // Append service statistics if there are any in local cache
            Optional<EventServiceStatistics> statistics = EventStore.getInboundStatistic(service.getName());
            statistics.ifPresent(eventServiceStatistics -> service.setStatistics(Util.toServiceStatistics(eventServiceStatistics)));
        });
        // Add services from casual caller cache
        casualCallerInfo.getServices().forEach( service -> {
            Service cliService = Util.toService( service );
            // Append service statistics if there are any in local cache
            Optional<EventServiceStatistics> statistics = EventStore.getOutboundStatistics(service.getName());
            statistics.ifPresent(eventServiceStatistics -> cliService.setStatistics(Util.toServiceStatistics(eventServiceStatistics)));
            services.add(cliService);
        });
        return services;
    }

    /**
     * TODO: Implement
     *
     * @return - Return all known connections
     */
    public List<Connection> getConnections()
    {
        LOG.log(Level.FINE, "called getConnections()");
        return List.of(TEST_SERVICE_CONNECTION_1, TEST_SERVICE_CONNECTION_2, TEST_QUEUE_CONNECTION_1);
    }

    /**
     * TODO: Implement discovery for a service name
     *
     * @return - List of specific service and its connections
     */
    public List<Service> discoverService(String serviceName)
    {
        LOG.log(Level.FINE, "called getService({})", serviceName);
        casualCallerInfo.discoverService(serviceName);
        return Collections.emptyList();
    }

    /**
     * TODO: Implement discovery for a queue name
     *
     * @return - List of know queues
     */
    public List<Queue> discoverQueues()
    {
        LOG.log(Level.FINE, "called getQueues()");
        return List.of(TEST_Q_1, TEST_Q_2);
    }

    /**
     * TODO: Implement
     *
     * @return - A queue connection to specific queue
     */
    public Queue getQueue(String queueName)
    {
        LOG.log(Level.FINE, "called getQueue({})", queueName);
        if (queueName.equals(TEST_Q_1.name())) {
            return TEST_Q_1;
        } else if (queueName.equals(TEST_Q_2.name())) {
            return TEST_Q_2;
        }
        return null;
    }
}
