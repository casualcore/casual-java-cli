//!
//! Copyright (c) 2026, The casual project
//!
//! This software is licensed under the MIT license, https://opensource.org/licenses/MIT
//!

#include <vector>

#include "discovery.h"

#include <common/serialize/json.h>

#include "http.h"
#include "queue.h"
#include "service.h"

#include "common/terminal.h"

namespace casual::java::discovery
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

        // sub options
        namespace services
        {
            struct format_valid_connection
            {
                static std::size_t width( const model::api::Connection& value, const std::ostream&)
                {
                    return 5;
                }

                static std::size_t width( const model::api::Service& value, const std::ostream& ostream)
                {
                    return width(value.connection, ostream);
                }

                void static print( std::ostream& out, const model::api::Connection& value, std::size_t width)
                {
                    out << std::setfill( ' ') << std::left << std::setw( width);
                    auto c = value.valid ? common::terminal::color::green : common::terminal::color::red;
                    common::stream::write( out, c, value.valid);
                }

                void static print( std::ostream& out, const model::api::Service& value, std::size_t width)
                {
                    print(out, value.connection, width);
                }
            };

            static constexpr auto format = [](auto& reply)
            {
                common::terminal::format::print( reply,
                     common::terminal::format::column( "name", []( auto& service) { return service.name;}, common::terminal::color::white, common::terminal::format::Align::left),
                     common::terminal::format::column( "hops", []( auto& service) { return service.hops;}, common::terminal::color::white, common::terminal::format::Align::right),
                     common::terminal::format::column( "category", []( auto& service) { return service.category;}, common::terminal::color::white, common::terminal::format::Align::right),
                     common::terminal::format::column( "transactionType", []( auto& service) { return service.transactionType;}, common::terminal::color::white, common::terminal::format::Align::right),
                     common::terminal::format::column( "timeout", []( auto& service) { return service.timeout;}, common::terminal::color::white, common::terminal::format::Align::right),
                     common::terminal::format::column( "connection", []( auto& service) { return service.connection.jndiName;}, common::terminal::color::white, common::terminal::format::Align::right),
                     common::terminal::format::custom::column( "valid", format_valid_connection{}));
            };

            auto invoke = [](std::vector<std::string> serviceNames)
            {
                cli::http::Request request = {
                    cli::http::rest::DISCOVER_SERVICE,
                    cli::http::POST,
                    cli::http::to_json_string(serviceNames)
                };
                std::vector<model::api::Service> services;
                auto reply = cli::http::do_curl(request);
                auto reader = common::serialize::json::relaxed::reader(reply.body);
                check_error(reply);
                reader >> services;
                format(services);
            };

            auto option()
            {
                return argument::Option{
                    std::move( invoke),
                    argument::option::Names({ "-s", "--services"}),
                    "discover casual services"
                 };
            }
        }

        namespace queues
        {
            auto invoke = [](std::vector<std::string> queueNames)
            {
                cli::http::Request request = {
                    cli::http::rest::DISCOVER_QUEUE,
                    cli::http::POST,
                    cli::http::to_json_string(queueNames)
                };
                std::vector<model::api::Queue> queues;
                auto reply = cli::http::do_curl(request);
                auto reader = common::serialize::json::relaxed::reader(reply.body);
                check_error(reply);
                reader >> queues;
                queue::format(queues);
            };

            auto option()
            {
                return argument::Option{
                    std::move( invoke),
                    argument::option::Names({ "-q", "--queues"}),
                    "discover casual queues"
                 };
            }
        }

    }

    argument::Option options()
    {
        return argument::Option
        { [](){}, argument::option::Names({ "discovery"}, {}), "discover java services",}
        ({
           local::services::option(),
           local::queues::option()
        });
    }
}
