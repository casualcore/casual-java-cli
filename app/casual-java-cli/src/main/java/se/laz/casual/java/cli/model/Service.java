/*
 * Copyright (c) 2026, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.java.cli.model;

import java.util.Objects;

public class Service
{
    String name;
    char order;
    String category;
    String transactionType;
    long timeout;
    long hops;
    boolean valid;
    // Only for inbound
    String jndiName;
    // Only for outbound
    String domainId;
    String protocolVersion;
    String hostName;
    Integer portNumber;
    ServiceStatistics statistics;

    public Service()
    {
        // no-op
    }

    public Service( Builder builder )
    {
        this.name = builder.name;
        this.order = builder.order;
        this.category = builder.category;
        this.transactionType = builder.transactionType;
        this.timeout = builder.timeout;
        this.hops = builder.hops;
        this.valid = builder.valid;
        this.jndiName = builder.jndiName;
        this.domainId = builder.domainId;
        this.protocolVersion = builder.protocolVersion;
        this.hostName = builder.hostName;
        this.portNumber = builder.portNumber;
        this.statistics = builder.statistics;

        Objects.requireNonNull( name, "name can not be null" );
        Objects.requireNonNull( category, "category can not be null" );
        Objects.requireNonNull( transactionType, "transactionType can not be null" );
        Objects.requireNonNull( statistics, "statistics can not be null" );
    }

    public String getName()
    {
        return name;
    }

    public char getOrder()
    {
        return order;
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

    public boolean isValid()
    {
        return valid;
    }

    public String getJndiName()
    {
        return jndiName;
    }

    public String getDomainId()
    {
        return domainId;
    }

    public String getProtocolVersion()
    {
        return protocolVersion;
    }

    public String getHostName()
    {
        return hostName;
    }

    public Integer getPortNumber()
    {
        return portNumber;
    }

    public ServiceStatistics getStatistics()
    {
        return statistics;
    }

    public void setStatistics( ServiceStatistics statistics )
    {
        this.statistics = statistics;
    }

    @Override
    public boolean equals( Object o )
    {
        if( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        Service service = (Service) o;
        return order == service.order && timeout == service.timeout && hops == service.hops && valid == service.valid && Objects.equals( name, service.name ) && Objects.equals( category, service.category ) && Objects.equals( transactionType, service.transactionType ) && Objects.equals( jndiName, service.jndiName ) && Objects.equals( domainId, service.domainId ) && Objects.equals( protocolVersion, service.protocolVersion ) && Objects.equals( hostName, service.hostName ) && Objects.equals( portNumber, service.portNumber ) && Objects.equals( statistics, service.statistics );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, order, category, transactionType, timeout, hops, valid, jndiName, domainId, protocolVersion, hostName, portNumber, statistics );
    }

    @Override
    public String toString()
    {
        return "Service{" +
                "name='" + name + '\'' +
                ", order=" + order +
                ", category='" + category + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", timeout=" + timeout +
                ", hops=" + hops +
                ", valid=" + valid +
                ", jndiName='" + jndiName + '\'' +
                ", domainId='" + domainId + '\'' +
                ", protocolVersion='" + protocolVersion + '\'' +
                ", hostName='" + hostName + '\'' +
                ", portNumber=" + portNumber +
                ", statistics=" + statistics +
                '}';
    }

    public static class Builder
    {
        String name;
        char order;
        String category = "";
        String transactionType = "-";
        long timeout = 0;
        long hops;
        boolean valid = false;
        String jndiName;
        String domainId;
        String protocolVersion;
        String hostName;
        Integer portNumber;
        ServiceStatistics statistics = new ServiceStatistics( 0, 0, 0, 0, 0 );

        public Builder name( String name )
        {
            this.name = name;
            return this;
        }

        public Builder order( char order )
        {
            this.order = order;
            return this;
        }

        public Builder category( String category )
        {
            this.category = category;
            return this;
        }

        public Builder transactionType( String transactionType )
        {
            this.transactionType = transactionType;
            return this;
        }

        public Builder timeout( long timeout )
        {
            this.timeout = timeout;
            return this;
        }

        public Builder hops( long hops )
        {
            this.hops = hops;
            return this;
        }

        public Builder valid( boolean valid )
        {
            this.valid = valid;
            return this;
        }

        public Builder jndiName( String jndiName )
        {
            this.jndiName = jndiName;
            return this;
        }

        public Builder domainId( String domainId )
        {
            this.domainId = domainId;
            return this;
        }

        public Builder protocolVersion( String protocolVersion )
        {
            this.protocolVersion = protocolVersion;
            return this;
        }

        public Builder hostName( String hostName )
        {
            this.hostName = hostName;
            return this;
        }

        public Builder portNumber( Integer portNumber )
        {
            this.portNumber = portNumber;
            return this;
        }

        public Builder serviceStatistics( ServiceStatistics serviceStatistics )
        {
            this.statistics = serviceStatistics;
            return this;
        }

        public Service build()
        {
            return new Service( this );
        }
    }
}
