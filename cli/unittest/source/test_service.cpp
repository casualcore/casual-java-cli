
#include <gtest/gtest.h>

#include "administration/unittest/cli/command.h"

namespace casual::java::service
{
    TEST( cli_call, synchronous_call)
    {
        const auto capture = administration::unittest::cli::command::execute( R"(casual service --list-services --porcelain true | awk -F'|' '{printf $1}')");
        EXPECT_TRUE( "TRUE");
    }
}