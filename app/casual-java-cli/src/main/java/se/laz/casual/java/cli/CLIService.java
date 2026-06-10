/*
 * Copyright (c) 2026, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.java.cli;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import se.laz.casual.info.CasualInfo;
import se.laz.casual.info.EventServiceStatistics;
import se.laz.casual.java.cli.model.Configuration;
import se.laz.casual.java.cli.model.Queue;
import se.laz.casual.java.cli.model.Service;
import se.laz.casual.java.cli.model.ServiceStatistics;
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
    private static final Logger LOG = Logger.getLogger( CLIService.class.getName() );
    protected static final Service TEST_SERVICE_1 = new Service.Builder()
            .name( "test" ).category( "1" ).transactionType( TransactionType.ATOMIC.name() ).timeout( 1000 )
            .hops( 1 ).jndiName( "jndi:test" ).valid( true ).build();
    protected static final Service TEST_SERVICE_2 = new Service.Builder()
            .name( "test/service2" ).category( "2" ).transactionType( TransactionType.JOIN.name() ).timeout( 1000 )
            .hops( 3 ).jndiName( "jndi:test-unvalid" ).build();
    protected static final Queue TEST_Q_1 = new Queue( "testQ1", "jndi:test-queue", true );
    protected static final Queue TEST_Q_2 = new Queue( "testQ2", "jndi:test-queue", true );
    protected static final Configuration TEST_CONFIGURATION = new Configuration( "jndi:test", 1000, false, 2000,
            "/test" );

    @EJB( lookup = "java:global/casual-caller-app/casual-caller/CasualInfoImpl!se.laz.casual.connection.caller.info" +
            ".CasualInfo" )
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
        LOG.log( Level.FINE, "called getConfiguration()" );
        return TEST_CONFIGURATION;
    }

    /**
     * Get all known services from Casual jca
     * @return - List of known service names
     */
    public List<Service> getServices()
    {
        LOG.log( Level.FINE, "called getServices()" );
        List<Service> services = new ArrayList<>();
        CasualInfo.getInboundServices().forEach( cs -> {
            Optional<EventServiceStatistics> statistics = CasualInfo.getInboundStatistic( cs.getName() );
            Optional<ServiceStatistics> serviceStatistics = statistics.map( Util::toServiceStatistics );
            services.add( Util.toService( cs, serviceStatistics ) );
        } );
        CasualInfo.getOutboundServices().forEach( cs -> {
            Optional<EventServiceStatistics> statistics = CasualInfo.getOutboundStatistic( cs.getName() );
            Optional<ServiceStatistics> serviceStatistics = statistics.map( Util::toServiceStatistics );
            services.add( Util.toService( cs, serviceStatistics ) );
        } );
        return services;
    }

    /**
     * TODO: Implement discovery for a service name
     *
     * @return - List of specific service and its connections
     */
    public List<Service> discoverService( String serviceName )
    {
        LOG.log( Level.FINE, () -> "called getService(%s)".formatted( serviceName ) );
        casualCallerInfo.discoverService( serviceName );
        return Collections.emptyList();
    }

    /**
     * TODO: Implement discovery for a queue name
     *
     * @return - List of know queues
     */
    public List<Queue> discoverQueues()
    {
        LOG.log( Level.FINE, "called getQueues()" );
        return List.of( TEST_Q_1, TEST_Q_2 );
    }

    /**
     * TODO: Implement
     *
     * @return - A queue connection to specific queue
     */
    public Queue getQueue( String queueName )
    {
        LOG.log( Level.FINE, () -> "called getQueue(%s)".formatted( queueName ) );
        if( queueName.equals( TEST_Q_1.name() ) )
        {
            return TEST_Q_1;
        }
        else if( queueName.equals( TEST_Q_2.name() ) )
        {
            return TEST_Q_2;
        }
        return null;
    }
}
