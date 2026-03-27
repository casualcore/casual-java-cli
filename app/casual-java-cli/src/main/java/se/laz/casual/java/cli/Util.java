package se.laz.casual.java.cli;

import se.laz.casual.java.cli.event.EventServiceStatistics;
import se.laz.casual.java.cli.model.Connection;
import se.laz.casual.java.cli.model.Service;
import se.laz.casual.java.cli.model.ServiceStatistics;

public class Util
{
    public static Service toService(se.laz.casual.info.Service service) {
        return new Service.Builder().name(service.getName())
                .category(service.getCategory())
                .transactionType(service.getTransactionType().name())
                .timeout(service.getTimeout())
                .hops(service.getHops())
                .connection(new Connection(service.getJndiName(), service.isRegistred())).build();
    }

    public static ServiceStatistics toServiceStatistics(EventServiceStatistics eventServiceStatistics)
    {
        return new ServiceStatistics(eventServiceStatistics.getOrder(),
                eventServiceStatistics.getCount(),
                eventServiceStatistics.getMin(),
                eventServiceStatistics.getMax(),
                eventServiceStatistics.getLast(),
                eventServiceStatistics.getTotal()
        );
    }

    public static Service toService( se.laz.casual.connection.caller.info.Service service )
    {
        return new Service.Builder()
                .name(service.getName())
                .connection(new Connection(service.getJndiName(), service.isValid()))
                .build();
    }
}
