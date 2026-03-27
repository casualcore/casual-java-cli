/*
 * Copyright (c) 2026, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.java.cli;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.ws.rs.core.Application;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import se.laz.casual.java.cli.model.Configuration;
import se.laz.casual.java.cli.model.Connection;
import se.laz.casual.java.cli.model.Queue;
import se.laz.casual.java.cli.model.Service;
import se.laz.casual.network.messages.domain.TransactionType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CLIResourceTest
{
    Service service;
    Connection serviceConnection;
    Connection queueConnection;
    CLIService cliServiceMock;
    HttpClient client = HttpClient.newBuilder().build();
    JerseyTest resource;

    @BeforeEach
    void setUp() throws Exception
    {
        cliServiceMock = Mockito.mock(CLIService.class);
        resource = new JerseyTest(){
            @Override
            protected Application configure(){
                var config = new ResourceConfig(CLIResource.class);
                config.register(CLIExceptionMapper.class);
                config.register(new AbstractBinder() {
                  @Override
                  protected void configure() {
                    bind(cliServiceMock).to(CLIService.class).ranked(1);
                  }
            });
            return config;
            }
        };
        resource.setUp();
        serviceConnection = new Connection("someconnetion", true);
        queueConnection = new Connection("jndi:someconnection", true);
        service = new Service.Builder().name("servicename/test").category("category1")
                .transactionType(TransactionType.JOIN.name()).hops(1).timeout(2).connection(serviceConnection).build();
    }

    @AfterEach
    void tearDown() throws Exception
    {
        resource.tearDown();
    }

    @DisplayName("Test get configuration")
    @Test
    void testGetConfiguration() throws IOException, InterruptedException, URISyntaxException
    {
        Configuration configuration = new Configuration(
                "jndiHost", 1000, true, 1023, "routeName"
        );
        when(cliServiceMock.getConfiguration()).thenReturn(configuration);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(resource.target().getUri().toString() + "configuration"))
                .GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Configuration responseData =new Gson().fromJson(response.body(), Configuration.class);
        Assertions.assertNotNull(responseData);
        Assertions.assertEquals(configuration, responseData);
    }

    @DisplayName("Test get connections")
    @Test
    void testGetConnections() throws IOException, InterruptedException, URISyntaxException
    {
        List<Connection> connections = List.of(serviceConnection, queueConnection);
        when(cliServiceMock.getConnections()).thenReturn(connections);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(resource.target().getUri().toString() + "connections"))
                .GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        List<Connection> responseData = new Gson().fromJson(response.body(), new TypeToken<List<Connection>>() {}.getType());
        Assertions.assertNotNull(responseData);
        Assertions.assertEquals(connections, responseData);
    }

    @DisplayName("Test get services")
    @Test
    void testGetServices() throws IOException, InterruptedException, URISyntaxException
    {
        List<Service> services = List.of(service);
        when(cliServiceMock.getServices()).thenReturn(services);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(resource.target().getUri().toString() + "services"))
                .GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        List<Service> responseData = new Gson().fromJson(response.body(), new TypeToken<List<Service>>() {}.getType());
        Assertions.assertNotNull(responseData);
        Assertions.assertEquals(services, responseData);
    }

    @DisplayName("Test get queues")
    @Test
    void testGetQueues() throws IOException, InterruptedException, URISyntaxException
    {
        List<Queue> queues = List.of();
        when(cliServiceMock.discoverQueues()).thenReturn(queues);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(resource.target().getUri().toString() + "queues"))
                .GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        List<String> responseData = new Gson().fromJson(response.body(), new TypeToken<List<String>>() {}.getType());
        Assertions.assertNotNull(responseData);
        Assertions.assertEquals(queues, responseData);
    }

    @DisplayName("Test discover service")
    @Test
    void testDiscoverService() throws IOException, InterruptedException, URISyntaxException
    {
        when(cliServiceMock.discoverService(any())).thenReturn(List.of(service));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(resource.target().getUri().toString() + "discover/service"))
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(String.format("['%s']", service.getName()))).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type listType = new TypeToken<ArrayList<Service>>(){}.getType();
        List<Service> serviceInfoList = new Gson().fromJson(response.body(), listType);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(service, serviceInfoList.get(0));
    }

    @DisplayName("Test discover queue")
    @Test
    void testDiscoverQueue() throws IOException, InterruptedException, URISyntaxException
    {
        Queue queue = new Queue("q1/queue", new Connection("jndiQueue", true));

        when(cliServiceMock.getQueue(any())).thenReturn(queue);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(resource.target().getUri().toString() + "discover/queue"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(String.format("['%s']", queue.name()))).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Type listType = new TypeToken<ArrayList<Queue>>(){}.getType();
        List<Queue> queues = new Gson().fromJson(response.body(), listType);
        Assertions.assertEquals(1, queues.size());
        Assertions.assertEquals(queue, queues.get(0));
    }

    @DisplayName("Test discover queue, not found")
    @Test
    void testDiscoverQueueNotFound() throws IOException, InterruptedException, URISyntaxException
    {
        Queue queue = new Queue("q1/queue", new Connection("jndiQueue", true));

        when(cliServiceMock.getQueue(any())).thenReturn(null);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(resource.target().getUri().toString() + "discover/queue"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(String.format("['%s']", queue.name()))).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Type listType = new TypeToken<ArrayList<Queue>>(){}.getType();
        List<Queue> queues = new Gson().fromJson(response.body(), listType);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(queues.isEmpty());
    }
}
