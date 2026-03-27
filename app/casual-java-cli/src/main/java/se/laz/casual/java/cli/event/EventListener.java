package se.laz.casual.java.cli.event;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import se.laz.casual.config.ConfigurationOptions;
import se.laz.casual.config.ConfigurationService;
import se.laz.casual.event.ServiceCallEvent;
import se.laz.casual.event.client.ConnectionObserver;
import se.laz.casual.event.client.EventClient;
import se.laz.casual.event.client.EventClientBuilder;
import se.laz.casual.event.client.EventObserver;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class EventListener implements EventObserver, ConnectionObserver
{
    private static final Logger LOG = Logger.getLogger(EventListener.class.getName());
    private static final String HOST = "127.0.0.1";
    private static final long BACKOFF = 5000;
    private static final TimeUnit BACKOFF_UNIT = TimeUnit.MILLISECONDS;
    private boolean connected = false;
    private final ScheduledExecutorService scheduledExecutor = new ScheduledThreadPoolExecutor( 1 );

    public EventListener()
    {
        // no-op
    }

    public void initOnStartup(@Observes @Initialized(ApplicationScoped.class) Object obj)
    {
        LOG.severe("EventListener initializing");
        scheduledExecutor.schedule(this::tryConnect, 0, BACKOFF_UNIT);
    }

    private void tryConnect()
    {
        try
        {
            connected = connect().join();
        }
        catch(Exception e)
        {
            LOG.log(Level.WARNING, e, () -> "EventListener failed to connect - will reschedule");
        }
        finally
        {
            if(!connected)
            {
                scheduledExecutor.schedule(this::tryConnect, BACKOFF, BACKOFF_UNIT);
            }
        }
    }

    private CompletableFuture<Boolean> connect() {
        return EventClientBuilder.createBuilder()
                .withHost(HOST)
                .withPort(ConfigurationService.getConfiguration(ConfigurationOptions.CASUAL_EVENT_SERVER_PORT))
                .withEventLoopGroup(new NioEventLoopGroup())
                .withChannel(NioSocketChannel.class)
                .withConnectionObserver(this)
                .withEventObserver(this)
                .build().connect();
    }

    @Override
    public void notify(ServiceCallEvent serviceCallEvent)
    {
        LOG.fine(()-> String.format("Event recieved: %s", serviceCallEvent));
        EventStore.store(serviceCallEvent);
    }

    @Override
    public void disconnected(EventClient eventClient)
    {
        LOG.severe(()-> String.format("Disconnected from: %s, retrying in %s %s.", HOST, BACKOFF, BACKOFF_UNIT));
        scheduledExecutor.schedule( this::tryConnect, BACKOFF, TimeUnit.MILLISECONDS );
    }
}