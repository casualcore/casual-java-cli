/*
 * Copyright (c) 2026, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.java.cli.model;

import java.util.Objects;

public class Service {

    String name;
    String category;
    String transactionType;
    long timeout;
    long hops;
    Connection connection;
    ServiceStatistics statistics;

    public Service()
    {
        // no-op
    }

    public String getName()
    {
        return name;
    }

    public String getCategory()
    {
        return category;
    }

    public String getTransactionType()
    {
        return transactionType;
    }

    public long getTimeout()
    {
        return timeout;
    }

    public long getHops()
    {
        return hops;
    }

    public Connection getConnection()
    {
        return connection;
    }

    public ServiceStatistics getStatistics()
    {
        return statistics;
    }

    public void setStatistics(ServiceStatistics statistics)
    {
        this.statistics = statistics;
    }

    public Service(Builder builder)
    {
        this.name = builder.name;
        this.category = builder.category;
        this.transactionType = builder.transactionType;
        this.timeout = builder.timeout;
        this.hops = builder.hops;
        this.connection = builder.connection;
        this.statistics = builder.statistics;

        Objects.requireNonNull(name, "name can not be null");
        Objects.requireNonNull(category, "category can not be null");
        Objects.requireNonNull(transactionType, "transactionType can not be null");
        Objects.requireNonNull(timeout, "timeout can not be null");
        Objects.requireNonNull(hops, "hops can not be null");
        Objects.requireNonNull(connection, "connection can not be null");
        Objects.requireNonNull(statistics, "statistics can not be null");
    }

    @Override
    public boolean equals( Object o )
    {
        if( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        Service service = (Service) o;
        return timeout == service.timeout && hops == service.hops && Objects.equals( name, service.name ) && Objects.equals( category, service.category ) && Objects.equals( transactionType, service.transactionType ) && Objects.equals( connection, service.connection ) && Objects.equals( statistics, service.statistics );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, category, transactionType, timeout, hops, connection, statistics );
    }

    @Override
    public String toString()
    {
        return "Service{" +
                "name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", transactionType=" + transactionType +
                ", timeout=" + timeout +
                ", hops=" + hops +
                ", connection=" + connection +
                ", statistics=" + statistics +
                '}';
    }

    public static class Builder
    {
        String name;
        String category = "";
        String transactionType = "-";
        long timeout = 0;
        long hops;
        Connection connection;
        ServiceStatistics statistics = new ServiceStatistics('-', 0, 0, 0, 0, 0);

        public Service.Builder name(String name)
        {
            this.name = name;
            return this;
        }

        public Service.Builder category(String category)
        {
            this.category = category;
            return this;
        }

        public Service.Builder transactionType(String transactionType)
        {
            this.transactionType = transactionType;
            return this;
        }

        public Service.Builder timeout(long timeout)
        {
            this.timeout = timeout;
            return this;
        }

        public Service.Builder hops(long hops)
        {
            this.hops = hops;
            return this;
        }

        public Service.Builder connection(Connection connection)
        {
            this.connection = connection;
            return this;
        }

        public Service.Builder serviceStatistics(ServiceStatistics serviceStatistics)
        {
            this.statistics = serviceStatistics;
            return this;
        }

        public Service build()
        {
            return new Service(this);
        }
    }
}
