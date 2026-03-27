/*
 * Copyright (c) 2026, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.java.cli;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import se.laz.casual.java.cli.model.Queue;
import se.laz.casual.java.cli.model.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Path("/")
public class CLIResource
{
    @Inject
    private CLIService cliService;

    /***
     * Get all configuration for Casual JCA and Casual caller
     * @return Configuration
     */
    @Path("/configuration")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConfiguration()
    {
        return Response.ok(cliService.getConfiguration()).build();
    }

    /***
     * List all known connections for services and queues.
     * @return List of connections
     */
    @Path("/connections")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConnections()
    {
        return Response.ok(cliService.getConnections()).build();
    }

    /***
     * Get service names of cached services
     * @return List of service names
     */
    @Path("/services")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServices()
    {
        return Response.ok(cliService.getServices()).build();
    }

    /***
     * Get queue names of cached queues
     * @return List of queue names
     */
    @Path("/queues")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQueues()
    {
        return Response.ok(cliService.discoverQueues()).build();
    }

    /***
     * Do discovery on specified service names
     * @return Service information for services
     */
    @Path("/discover/service")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response getService(List<String> services)
    {
        List<Service> serviceDiscoveryList = new ArrayList<>();
        services.forEach(s -> serviceDiscoveryList.addAll(cliService.discoverService(s)));
        return Response.ok(serviceDiscoveryList).build();
    }

    /***
     * Do discovery on specified queue names
     * @return Queue information for queues
     */
    @Path("/discover/queue")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQueues(List<String> queues)
    {
        List<Queue> queueDiscoveryList = new ArrayList<>();
        queues.forEach(s -> {
            Queue queue = cliService.getQueue(s);
            if (Objects.nonNull(queue)) {
                queueDiscoveryList.add(queue);
            }
        });
        return Response.ok(queueDiscoveryList).build();
    }
}
