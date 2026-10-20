//!
//! Copyright (c) 2026, The casual project
//!
//! This software is licensed under the MIT license, https://opensource.org/licenses/MIT
//!

#pragma once

#include <string>

namespace administration::unittest::util::mappings
{
    namespace services
    {
        const std::string services = R"(
          {
            "request": {
              "method": "GET",
              "url": "/__casual/cli/services"
            },
            "response": {
              "status": 200,
              "jsonBody": [
                {
                  "category": "",
                  "hops": 0,
                  "jndiName": "java:global/TestWildflyService!se.laz.casual.test.WildflyService",
                  "name": "callOutboundService",
                  "order": "S",
                  "statistics": {
                    "count": 0,
                    "last": 0,
                    "max": 0,
                    "min": 0,
                    "total": 0
                  },
                  "timeout": 0,
                  "transactionType": "AUTOMATIC",
                  "valid": true
                },
                {
                  "category": "",
                  "hops": 0,
                  "jndiName": "java:global/TestWildflyService!se.laz.casual.test.WildflyService",
                  "name": "testWildflyService",
                  "order": "S",
                  "statistics": {
                    "count": 0,
                    "last": 0,
                    "max": 0,
                    "min": 0,
                    "total": 0
                  },
                  "timeout": 0,
                  "transactionType": "AUTOMATIC",
                  "valid": true
                }
              ],
              "headers": {
                "Content-Type": "application/json"
              }
            }
          }
        )";

      const std::string services_500 = R"(
          {
            "request": {
              "method": "GET",
              "url": "/__casual/cli/services"
            },
            "response": {
              "status": 500,
              "jsonBody": {},
              "headers": {
                "Content-Type": "application/json"
              }
            }
          }
        )";
    }
}
