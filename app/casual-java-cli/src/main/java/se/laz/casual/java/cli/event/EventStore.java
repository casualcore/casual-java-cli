package se.laz.casual.java.cli.event;

import se.laz.casual.event.Order;
import se.laz.casual.event.ServiceCallEvent;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class EventStore
{
    private static final Logger LOG = Logger.getLogger(EventStore.class.getName());
    private static final EventStore instance = new EventStore();
    private final Map<String, EventServiceStatistics> inboundStatistics;
    private final Map<String, EventServiceStatistics> outboundStatistics;

    private EventStore() {
        this.inboundStatistics = new ConcurrentHashMap<>();
        this.outboundStatistics = new ConcurrentHashMap<>();
    }

    public static void store(ServiceCallEvent serviceCallEvent)
    {
        EventServiceStatistics eventServiceStatistics = instance.inboundStatistics.getOrDefault(serviceCallEvent.getService(),
                new EventServiceStatistics.Builder().name(serviceCallEvent.getService())
                        .order(serviceCallEvent.getOrder()).build());
        eventServiceStatistics.increment();
        long executionTime = serviceCallEvent.getEnd() - serviceCallEvent.getStart();
        eventServiceStatistics.setMin(executionTime);
        eventServiceStatistics.setMax(executionTime);
        eventServiceStatistics.setLast(serviceCallEvent.getEnd());
        eventServiceStatistics.increaseTotal(executionTime);

        if(serviceCallEvent.getOrder() == Order.SEQUENTIAL.getValue())
        {
            instance.inboundStatistics.put(serviceCallEvent.getService(), eventServiceStatistics);
        }
        else
        {
            instance.outboundStatistics.put(serviceCallEvent.getService(), eventServiceStatistics);
        }
        LOG.finest(() -> "Stored statistics: '%s'.".formatted(eventServiceStatistics));
    }

    public static Optional<EventServiceStatistics> getInboundStatistic(String serviceName)
    {
        Objects.requireNonNull( serviceName, "serviceName cannot be null" );
        return Optional.ofNullable(instance.inboundStatistics.get(serviceName));
    }

    public static Optional<EventServiceStatistics> getOutboundStatistics(String serviceName)
    {
        Objects.requireNonNull( serviceName, "serviceName cannot be null" );
        return Optional.ofNullable(instance.outboundStatistics.get(serviceName));
    }
}
