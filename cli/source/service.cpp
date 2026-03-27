//!
//! Copyright (c) 2026, The casual project
//!
//! This software is licensed under the MIT license, https://opensource.org/licenses/MIT
//!

#include "service.h"

#include <common/terminal.h>

#include "http.h"
#include "model.h"
#include "common/serialize/json.h"

namespace casual::java::service
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

        namespace list::services
        {
            static constexpr auto format = [](auto& reply)
            {
                using time_type = std::chrono::duration< double>;
                auto format_avg_time = []( const model::api::Service& value)
                {
                    if( value.statistics.count == 0)
                        return 0.0;

                    return std::chrono::duration_cast< time_type>(
                        std::chrono::microseconds(value.statistics.total) / value.statistics.count).count();
                };

                auto format_min_time = []( const model::api::Service& value)
                {
                    return std::chrono::duration_cast< time_type>( std::chrono::microseconds(value.statistics.min)).count();
                };

                auto format_max_time = []( const model::api::Service& value)
                {
                    return std::chrono::duration_cast< time_type>( std::chrono::microseconds(value.statistics.max)).count();
                };

                auto format_last = []( const model::api::Service& value) -> std::string
                {
                    if( value.statistics.last == 0)
                        return "-";
                    auto last = std::chrono::microseconds(value.statistics.last);
                    std::chrono::zoned_time zt{std::chrono::current_zone(),
                        std::chrono::time_point_cast<std::chrono::microseconds>(std::chrono::system_clock::time_point(last))};
                    return std::format("{:%Y-%m-%dT%T%Ez}", zt);
                };

                common::terminal::format::print( reply,
                     common::terminal::format::column( "name", []( auto& service) { return service.name;}, common::terminal::color::yellow, common::terminal::format::Align::left),
                     common::terminal::format::column( "category", []( auto& service) { return service.category;}, common::terminal::color::white, common::terminal::format::Align::right),
                     common::terminal::format::column( "mode", []( auto& service) { return service.transactionType;}, common::terminal::color::white, common::terminal::format::Align::right),
                     // common::terminal::format::column( "hops", []( auto& service) { return service.hops;}, common::terminal::color::white, common::terminal::format::Align::right),
                     common::terminal::format::column( "timeout", []( auto& service) { return service.timeout;}, common::terminal::color::white, common::terminal::format::Align::right),
                     // common::terminal::format::column( "alias", []( auto& service) { return service.connection.jndiName;}, common::terminal::color::white, common::terminal::format::Align::right),
                     // common::terminal::format::custom::column( "valid", format_valid_connection{}),
                     common::terminal::format::column( "order", []( auto& service) { return service.statistics.order;}, common::terminal::color::white, common::terminal::format::Align::right),
                     common::terminal::format::column( "C", []( auto& service) { return service.statistics.count;}, common::terminal::color::white, common::terminal::format::Align::right),
                     common::terminal::format::column( "AT", format_avg_time, common::terminal::color::white, common::terminal::format::Align::right),
                     common::terminal::format::column( "min", format_min_time, common::terminal::color::white, common::terminal::format::Align::right),
                     common::terminal::format::column( "max", format_max_time, common::terminal::color::white, common::terminal::format::Align::right),
                     common::terminal::format::column( "last", format_last, common::terminal::color::blue, common::terminal::format::Align::right));
            };

            auto invoke()
            {
                cli::http::Request request = {
                    cli::http::rest::SERVICES,
                    cli::http::GET
                };
                std::vector<model::api::Service> services;
                auto reply = cli::http::do_curl(request);
                check_error(reply);
                auto reader = common::serialize::json::relaxed::reader(reply.body);
                reader >> services;
                format(services);
            }

            auto option()
            {
                return argument::Option{
                    std::move( invoke),
                    argument::option::Names({ "-ls", "--list-services"}),
                    "list known casual services for casual java"
                 };
            }
        }

        namespace info::services
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
                     common::terminal::format::column( "name", []( auto& service) { return service.name;}, common::terminal::color::yellow, common::terminal::format::Align::left),
                     common::terminal::format::column( "category", []( auto& service) { return service.category;}, common::terminal::color::white, common::terminal::format::Align::right),
                     common::terminal::format::column( "mode", []( auto& service) { return service.transactionType;}, common::terminal::color::white, common::terminal::format::Align::right),
                     common::terminal::format::column( "hops", []( auto& service) { return service.hops;}, common::terminal::color::white, common::terminal::format::Align::right),
                     common::terminal::format::column( "timeout", []( auto& service) { return service.timeout;}, common::terminal::color::white, common::terminal::format::Align::right),
                     common::terminal::format::column( "alias", []( auto& service) { return service.connection.jndiName;}, common::terminal::color::white, common::terminal::format::Align::right),
                     common::terminal::format::custom::column( "valid", format_valid_connection{}));
            };

            auto invoke()
            {
                cli::http::Request request = {
                    cli::http::rest::SERVICES,
                    cli::http::GET
                };
                std::vector<model::api::Service> services;
                auto reply = cli::http::do_curl(request);
                check_error(reply);
                auto reader = common::serialize::json::relaxed::reader(reply.body);
                reader >> services;
                format(services);
            }

            auto option()
            {
                return argument::Option{
                    std::move( invoke),
                    argument::option::Names({ "-i", "--info"}),
                    "list information about services"
                 };
            }
        }
    }

    argument::Option options()
    {
        return argument::Option{ [](){}, argument::option::Names({ "service"}, {}), "get known services"}
        ({
           local::list::services::option(),
            local::info::services::option()
        });
    }
}
