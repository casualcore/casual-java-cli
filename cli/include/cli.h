//!
//! Copyright (c) 2026, The casual project
//!
//! This software is licensed under the MIT license, https://opensource.org/licenses/MIT
//!

#pragma once

#include <string>
#include <vector>

#include "casual/argument.h"

namespace casual::java::cli
{
    std::vector< argument::Option> options();
    void parse( int argc, const char** argv);
    void parse( std::vector< std::string> options);
} // casual::java::cli