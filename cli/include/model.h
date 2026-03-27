//!
//! Copyright (c) 2026, The casual project
//!
//! This software is licensed under the MIT license, https://opensource.org/licenses/MIT
//!

#pragma once

#include <any>

#include "casual/argument.h"

namespace casual::java::model {

    namespace api
    {
        struct Configuration
        {
            std::string jndiSearchRoot;
            int validationIntervalMillis;
            bool transactionStickyEnabled;
            long topologyChangeDelayMillis;
            std::string routeFileName;

            CASUAL_CONST_CORRECT_SERIALIZE
            (
               CASUAL_SERIALIZE( jndiSearchRoot);
               CASUAL_SERIALIZE( validationIntervalMillis);
               CASUAL_SERIALIZE( transactionStickyEnabled);
               CASUAL_SERIALIZE( topologyChangeDelayMillis);
               CASUAL_SERIALIZE( routeFileName);
            )
        };

        struct Queue
        {
            std::string name;
            std::string jndiName;
            bool valid;

            CASUAL_CONST_CORRECT_SERIALIZE
            (
               CASUAL_SERIALIZE( name);
               CASUAL_SERIALIZE( jndiName);
               CASUAL_SERIALIZE( valid);
            )
        };

        struct Statistics
        {
            long count;
            long min;
            long max;
            long last;
            long total;

            CASUAL_CONST_CORRECT_SERIALIZE
            (
                CASUAL_SERIALIZE( count);
                CASUAL_SERIALIZE( min);
                CASUAL_SERIALIZE( max);
                CASUAL_SERIALIZE( last);
                CASUAL_SERIALIZE( total);
            )
        };

        struct Service
        {
            std::string name;
            char order;
            std::string category;
            std::string transactionType;
            long timeout;
            long hops;
            bool valid;
            std::string jndiName;
            std::string domainId;
            std::string protocolVersion;
            std::string hostName;
            int portNumber;
            Statistics statistics;

            CASUAL_CONST_CORRECT_SERIALIZE
            (
               CASUAL_SERIALIZE( name);
               CASUAL_SERIALIZE( order);
               CASUAL_SERIALIZE( category);
               CASUAL_SERIALIZE( transactionType);
               CASUAL_SERIALIZE( timeout);
               CASUAL_SERIALIZE( hops);
               CASUAL_SERIALIZE( valid);
               CASUAL_SERIALIZE( jndiName);
               CASUAL_SERIALIZE( domainId);
               CASUAL_SERIALIZE( protocolVersion);
               CASUAL_SERIALIZE( hostName);
               CASUAL_SERIALIZE( portNumber);
               CASUAL_SERIALIZE( statistics);
            )
        };
    }
}