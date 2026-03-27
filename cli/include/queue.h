//!
//! Copyright (c) 2026, The casual project
//!
//! This software is licensed under the MIT license, https://opensource.org/licenses/MIT
//!

#pragma once

#include <common/terminal.h>

#include "model.h"
#include "casual/argument.h"

namespace casual::java::queue {
    argument::Option options();

    struct format_valid_connection
    {
        static std::size_t width( const model::api::Connection& value, const std::ostream&)
        {
            return 5;
        }

        static std::size_t width( const model::api::Queue& value, const std::ostream& ostream)
        {
            return width(value.connection, ostream);
        }

        void static print( std::ostream& out, const model::api::Connection& value, std::size_t width)
        {
            out << std::setfill( ' ') << std::left << std::setw( width);
            auto c = value.valid ? common::terminal::color::green : common::terminal::color::red;
            common::stream::write( out, c, value.valid);
        }

        void static print( std::ostream& out, const model::api::Queue& value, std::size_t width)
        {
            print(out, value.connection, width);
        }
    };

    static constexpr auto format = [](auto& reply)
    {
        common::terminal::format::print( reply,
            common::terminal::format::column( "name", []( auto& queue) { return queue.name;}, common::terminal::color::white),
            common::terminal::format::column( "connection", []( auto& queue) { return queue.connection.jndiName;}, common::terminal::color::white, common::terminal::format::Align::right),
                 common::terminal::format::custom::column( "valid", format_valid_connection{}));
    };
}
