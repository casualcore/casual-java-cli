//!
//! Copyright (c) 2026, The casual project
//!
//! This software is licensed under the MIT license, https://opensource.org/licenses/MIT
//!

#include "configuration.h"

#include <common/serialize/create.h>
#include <common/serialize/json.h>

#include "http.h"
#include "model.h"
#include "common/terminal.h"

namespace casual::java::configuration
{
    namespace local
    {
        void check_error(cli::http::Reply &reply)
        {
            if (!reply.curlError.empty())
            {
                std::cerr<<std::format("Received error from curl: {}", reply.curlError)<<std::endl;
                throw std::runtime_error("curl error");
            }

            if (reply.statusCode != 200)
            {
                std::cerr<<std::format("Error occurred, statusCode: {}, body: {}", reply.statusCode, reply.body)<<std::endl;
                throw std::runtime_error("Failed rest call");
            }
        }

        auto configuration()
        {
            cli::http::Request request = {
                cli::http::rest::CONFIGURATION,
                cli::http::GET
            };
            model::api::Configuration c;
            auto reply = cli::http::do_curl(request);
            check_error(reply);
            auto reader = common::serialize::json::relaxed::reader(reply.body);
            reader >> c;
            return c;
        }

        static constexpr auto format = [](auto& configuration)
        {
            auto format_jndi_search_root = []( auto& c) { return c.jndiSearchRoot; };
            auto format_validation_interval_millis = []( auto& c) { return c.validationIntervalMillis;};
            auto format_transaction_sticky_enabled = []( auto& c) { return c.transactionStickyEnabled;};
            auto format_topology_change_delay_millis = []( auto& c) { return c.topologyChangeDelayMillis;};
            auto format_route_file_name = []( auto& c) { return c.routeFileName;};

            common::terminal::format::print( std::vector{configuration},
                                             common::terminal::format::column( "jndiSearchRoot", format_jndi_search_root, common::terminal::color::white, common::terminal::format::Align::left),
                                             common::terminal::format::column( "validationIntervalMillis", format_validation_interval_millis, common::terminal::color::white, common::terminal::format::Align::left),
                                             common::terminal::format::column( "transactionStickyEnabled", format_transaction_sticky_enabled, common::terminal::color::white, common::terminal::format::Align::left),
                                             common::terminal::format::column( "topologyChangeDelayMillis", format_topology_change_delay_millis, common::terminal::color::white, common::terminal::format::Align::left),
                                             common::terminal::format::column( "routeFileName", format_route_file_name, common::terminal::color::white, common::terminal::format::Align::left)
            );
        };

        auto invoke = []()
        {
            auto reply = configuration();
            format(reply);
        };

        auto option()
        {
            return argument::Option{
                std::move( invoke),
                argument::option::Names({ "-c", "--caller"}, {}),
                "list configuration for casual caller"
             };
        }
    }

    argument::Option options()
    {
        return argument::Option{ std::move(local::invoke), argument::option::Names({ "configuration"}, {}), "get java jca and/or caller configuration"};
    }
}