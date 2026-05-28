//!
//! Copyright (c) 2026, The casual project
//!
//! This software is licensed under the MIT license, https://opensource.org/licenses/MIT
//!

#pragma once

#include <optional>
#include <string>
#include <vector>

namespace casual::java::cli::http
{
    namespace rest
    {
        enum Endpoint
        {
            CONFIGURATION,
            DISCOVER_SERVICE,
            DISCOVER_QUEUE,
            QUEUES,
            SERVICES,
            CONNECTIONS
        };
    }

    enum Method {
        GET,
        POST
      };

    struct Request
    {
        rest::Endpoint endpoint;
        Method method = GET;
        std::string body;
    };

    struct Reply
    {
        std::string curlError;
        long statusCode = 0;
        std::string body;
    };

    void init();
    void set_host( const std::optional<std::string>& host );
    std::string get_host();
    Reply do_curl(const Request& request);
    std::string to_json_string(const std::vector<std::string> &values);
}
