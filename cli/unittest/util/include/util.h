//!
//! Copyright (c) 2026, The casual project
//!
//! This software is licensed under the MIT license, https://opensource.org/licenses/MIT
//!

#pragma once

#include <fstream>
#include <iostream>
#include <filesystem>

#include "administration/unittest/cli/command.h"
#include "common/environment.h"
#include "common/environment/expand.h"

namespace casual::java::cli::unittest::util
{
    inline administration::unittest::cli::command::Capture execute( std::string command)
    {
        auto path = common::environment::expand( common::string::compose( "PATH=",
           "${CMAKE_BINARY_DIR}/bin:"
           "${PATH}"
        ));
        std::cout << path << std::endl;
        const auto shell = common::environment::variable::get( "SHELL").value_or( "sh");

        return administration::unittest::cli::command::non::blocking::capture( common::process::non::blocking::execute( shell, std::vector< std::string>{ "-c", std::move( command)}, { std::move( path)}));
    }

    inline std::string get_columns(const std::string& output)
    {
        const std::regex space_re("\\s{1,}");
        const std::string header_row = output.substr(0, output.find('\n'));
        return std::regex_replace(header_row, space_re, std::string{ " " });
    }

    inline std::vector<std::string> get_rows(const std::string& output)
    {
        const std::regex color_re("\\x1b\\[0;*[0-9]*m");
        const std::regex space_re("\\s{1,}");
        std::stringstream ss(output);
        std::vector<std::string> rows = {};
        std::string row;
        while(std::getline(ss, row, '\n'))
        {
            if (!row.empty())
            {
                auto replaced = std::regex_replace(row, color_re, std::string{ "" });
                rows.push_back(std::regex_replace(replaced, space_re, std::string{ " " }));
            }
        }
        // Remove first two rows (headers and separation lines)
        if (rows.size() > 2)
        {
            return {rows.begin() + 2, rows.end()};
        }
        return {};
    }
}
