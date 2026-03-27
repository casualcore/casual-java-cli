//!
//! Copyright (c) 2026, The casual project
//!
//! This software is licensed under the MIT license, https://opensource.org/licenses/MIT
//!

#include <gtest/gtest.h>

#include "mappings.h"
#include "util.h"
#include "wiremock.h"

namespace casual::java::cli::unittest::service
{
    int port = 8080;
    std::string host = std::format("127.0.0.1:{}", port);

    TEST( cli_casual_java, help)
    {
        const auto capture = util::execute(R"(casual-java --help)");
        std::cout << capture.standard.out << std::endl;
        EXPECT_TRUE( !capture.standard.out.empty()) << CASUAL_NAMED_VALUE( capture.standard.out);
    }

    TEST( cli_casual_java, service_help)
    {
        const auto capture = util::execute(R"(casual-java service --help)");
        std::cout << capture.standard.out << std::endl;
        EXPECT_TRUE( capture.standard.out.contains("list-service")) << CASUAL_NAMED_VALUE( capture.standard.out);
        EXPECT_TRUE( capture.standard.out.contains("info")) << CASUAL_NAMED_VALUE( capture.standard.out);
    }

    TEST_F( Wiremock, services_list_services)
    {
        create_mapping(::administration::unittest::util::mappings::services::services);
        const auto capture = util::execute(std::format(R"(casual-java --host {} service -ls )", host));
        const std::string headers = util::get_columns(capture.standard.out);
        const std::vector<std::string> rows = util::get_rows(capture.standard.out);
        EXPECT_EQ( headers, "name category mode timeout order C AT min max last");
        EXPECT_EQ( rows.size(), 2);
        EXPECT_EQ( rows[0], "callOutboundService AUTOMATIC - S 0 0.000 0.000 0.000 -");
    }

    TEST_F( Wiremock, services_info)
    {
        create_mapping(::administration::unittest::util::mappings::services::services);
        const auto capture = util::execute(std::format(R"(casual-java --host {} service -i )", host));
        const std::string headers = util::get_columns(capture.standard.out);
        const std::vector<std::string> rows = util::get_rows(capture.standard.out);
        EXPECT_EQ( headers, "name category mode hops timeout alias valid");
        EXPECT_EQ( rows.size(), 2);
        EXPECT_EQ( rows[0], "callOutboundService AUTOMATIC 0 - java:global/TestWildflyService!se.laz.casual.test.WildflyService true ");
    }

    TEST_F( Wiremock, services_500)
    {
        create_mapping(::administration::unittest::util::mappings::services::services_500);
        const auto capture = util::execute(std::format(R"(casual-java --host {} service -ls )", host));
        EXPECT_TRUE(capture.exit > 0);
        EXPECT_TRUE(capture.standard.error.contains("Error occurred"));
    }
}
