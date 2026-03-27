//!
//! Copyright (c) 2026, The casual project
//!
//! This software is licensed under the MIT license, https://opensource.org/licenses/MIT
//!

#include "cli.h"

#include <optional>
#include <string_view>

#include "common/algorithm/container.h"
#include "common/exception/guard.h"
#include "common/terminal.h"
#include "casual/cli/state.h"
#include "http.h"
#include "discovery.h"
#include "configuration.h"
#include "service.h"
#include "queue.h"
#include "constants.h"

namespace casual::java::cli
{
   namespace local
   {
      constexpr std::string_view description = R"(
casual java CLI

To get more detailed help, use any of:
casual-java --help <option>
casual-java <option> --help
casual-java --help <option> <option>

Where <option> is one of the listed below
)";

      namespace host
      {
         auto options()
         {
            auto invoke = [](const std::optional< std::string>& host)
            {
               if ( !host.has_value() )
               {
                  common::terminal::format::print( std::vector{http::get_host()},
                                 common::terminal::format::column( "host", []( auto& h){ return h;},
                                    common::terminal::format::Align::left));
               }
               else
               {
                  http::set_host( host );
               }
            };

            return argument::Option{
               std::move( invoke),
               argument::option::Names({ "--host"}, {}),
               {"url to host (default=" + http::get_host() + ")"}};
         }
      }

      namespace verbose
      {
         auto options()
         {
            auto invoke = []()
            {
               constants::set_env(constants::ENV_CASUAL_JAVA_VERBOSE, "true");
            };

            return argument::Option{
               std::move( invoke),
               {{ "--verbose"}, {}},{"verbose logging"}
            };
         }
      }

      void main( int argc, const char** argv)
      {
         http::init();
         argument::parse(description, options(), argc, argv);
      }

   }

   std::vector< argument::Option> options()
   {
      return common::algorithm::container::compose(
         configuration::options(),
         discovery::options(),
         service::options(),
         queue::options(),
         local::host::options(),
         local::verbose::options()
      );
   }

} // casual

int main( int argc, const char** argv)
{
   return casual::common::exception::main::log::guard( [=]()
   {
      casual::java::cli::local::main( argc, argv);
   });
}
