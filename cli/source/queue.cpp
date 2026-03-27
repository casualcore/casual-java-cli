//!
//! Copyright (c) 2026, The casual project
//!
//! This software is licensed under the MIT license, https://opensource.org/licenses/MIT
//!

#include "queue.h"
#include "http.h"
#include "model.h"
#include "common/serialize/json.h"
#include "common/terminal.h"

namespace casual::java::queue
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

        namespace list::queues
        {
            auto invoke()
            {
                cli::http::Request request = {
                    cli::http::rest::QUEUES,
                    cli::http::GET
                };
                std::vector<model::api::Queue> queues;
                auto reply = cli::http::do_curl(request);
                check_error(reply);
                auto reader = common::serialize::json::relaxed::reader(reply.body);
                reader >> queues;
                format(queues);
            }

            auto option()
            {
                return argument::Option{
                    std::move( invoke),
                    argument::option::Names({ "-lq", "--list-queues"}),
                    "list known casual queues for casual java"
                 };
            }
        }
    }

    argument::Option options()
    {
        return argument::Option{ [](){}, argument::option::Names({ "queue"}, {}), "get known queues"}
        ({
            local::list::queues::option()
        });
    }
}
