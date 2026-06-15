/*
 * Copyright (c) 2026, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.java.cli;

import se.laz.casual.info.EventServiceStatistics;
import se.laz.casual.java.cli.model.Service;
import se.laz.casual.java.cli.model.ServiceStatistics;

import java.util.Objects;
import java.util.Optional;

public class Util
{
    private Util()
    {
        // no-op
    }

    public static Service toService( se.laz.casual.info.Service service, Optional<ServiceStatistics> serviceStatistics )
    {
        Service.Builder builder = new Service.Builder().name( service.getName() )
                .order( service.getOrder().getValue() )
                .category( service.getCategory() )
                .transactionType( service.getTransactionType().name() )
                .timeout( service.getTimeout() )
                .hops( service.getHops() )
                .jndiName( service.getJndiName() )
                .valid( service.isRegistered() );
        if( Objects.nonNull( service.getConnection() ) )
        {
            builder.domainId( service.getConnection().getDomainId().getId().toString() )
                    .protocolVersion( service.getConnection().getProtocolVersion().toString() )
                    .hostName( service.getConnection().getHostName() )
                    .portNumber( service.getConnection().getPortNumber() );
        }
        serviceStatistics.ifPresent( builder::serviceStatistics );
        return builder.build();
    }

    public static ServiceStatistics toServiceStatistics( EventServiceStatistics eventServiceStatistics )
    {
        return new ServiceStatistics(
                eventServiceStatistics.getCount(),
                eventServiceStatistics.getMin(),
                eventServiceStatistics.getMax(),
                eventServiceStatistics.getLast(),
                eventServiceStatistics.getTotal()
        );
    }
}
