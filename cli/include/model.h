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

        struct Connection
        {
            std::string jndiName;
            bool valid;

            CASUAL_CONST_CORRECT_SERIALIZE
            (
               CASUAL_SERIALIZE( jndiName);
               CASUAL_SERIALIZE( valid);
            )
        };

        struct Queue
        {
            std::string name;
            Connection connection;

            CASUAL_CONST_CORRECT_SERIALIZE
            (
               CASUAL_SERIALIZE( name);
               CASUAL_SERIALIZE( connection);
            )
        };

        struct Statistics
        {
            char order;
            long count;
            long min;
            long max;
            long last;
            long total;

            CASUAL_CONST_CORRECT_SERIALIZE
            (
                CASUAL_SERIALIZE( order);
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
            std::string category;
            std::string transactionType;
            long timeout;
            long hops;
            Connection connection;
            Statistics statistics;

            CASUAL_CONST_CORRECT_SERIALIZE
            (
               CASUAL_SERIALIZE( name);
               CASUAL_SERIALIZE( category);
               CASUAL_SERIALIZE( transactionType);
               CASUAL_SERIALIZE( timeout);
               CASUAL_SERIALIZE( hops);
               CASUAL_SERIALIZE( connection);
               CASUAL_SERIALIZE( statistics);
            )
        };
    }
}