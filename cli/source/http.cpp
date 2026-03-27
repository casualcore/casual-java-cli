//!
//! Copyright (c) 2026, The casual project
//!
//! This software is licensed under the MIT license, https://opensource.org/licenses/MIT
//!

#include "http.h"

#include <cli.h>
#include "constants.h"
#include <optional>
#include <map>
#include <curl/curl.h>
#include <common/serialize/json.h>
#include <rapidjson/document.h>
#include <rapidjson/writer.h>

namespace casual::java::cli::http
{
    namespace
    {
        std::string http_host = constants::has_env(constants::ENV_CASUAL_JAVA_HOST) ?
         getenv(constants::ENV_CASUAL_JAVA_HOST.c_str()) : "http://localhost:8080";
        const std::string CONTEXT_ROOT = "/__casual/cli";
        std::map<rest::Endpoint, std::string> endpoints = {
            {rest::CONFIGURATION, CONTEXT_ROOT + "/configuration" },
            {rest::DISCOVER_SERVICE, CONTEXT_ROOT + "/discover/service" },
            {rest::DISCOVER_QUEUE, CONTEXT_ROOT + "/discover/queue" },
            {rest::QUEUES, CONTEXT_ROOT + "/queues" },
            {rest::SERVICES, CONTEXT_ROOT + "/services" },
            {rest::CONNECTIONS, CONTEXT_ROOT + "/connections" }
        };

        std::string get_endpoint(const rest::Endpoint& endpoint)
        {
            return endpoints[endpoint];
        }

        CURL *curl = nullptr;
    }

    void init()
    {
        curl_global_init(CURL_GLOBAL_ALL);
    }

    std::string get_host()
    {
        return http_host;
    }

    void set_host( const std::optional<std::string>& host )
    {
        if( host.has_value() )
        {
            http_host = host.value();
        }
    }

    static size_t write_data(void *contents, size_t size, size_t nmemb, void *userp)
    {
        ((std::string*)userp)->append((char*)contents, size * nmemb);
        return size * nmemb;
    }

    void check_option(CURLcode curl_code)
    {
        if( curl_code != CURLE_OK )
        {
            throw std::runtime_error( curl_easy_strerror( curl_code ) );
        }
    }

    Reply do_curl(const Request& request)
    {
        Reply reply;
        curl = curl_easy_init();
        try
        {
            const auto url = http_host + get_endpoint(request.endpoint);
            if (constants::has_env(constants::ENV_CASUAL_JAVA_VERBOSE))
            {
                check_option(curl_easy_setopt(curl, CURLOPT_VERBOSE, 1));
            }
            if (request.method == POST)
            {
                curl_slist *headerList = nullptr;
                headerList = curl_slist_append(headerList, "content-type: application/json");
                curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headerList);
                curl_easy_setopt(curl, CURLOPT_POSTFIELDS, request.body.c_str());
            }
            check_option(curl_easy_setopt(curl, CURLOPT_URL, url.c_str()));
            check_option(curl_easy_setopt(curl, CURLOPT_WRITEDATA, &reply.body));
            check_option(curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, write_data));
            check_option(curl_easy_perform(curl));
            curl_easy_getinfo(curl, CURLINFO_RESPONSE_CODE, &reply.statusCode);
        }
        catch (std::runtime_error& e)
        {
            reply.curlError = e.what();
        }
        curl_easy_cleanup(curl);
        return reply;
    }

    std::string to_json_string(const std::vector<std::string> &values)
    {
        rapidjson::Document document;
        document.SetArray();
        rapidjson::Document::AllocatorType& allocator = document.GetAllocator();
        for (const auto &v : values) {
            document.PushBack(rapidjson::Value().SetString(v.c_str(), v.length(),
                allocator), allocator);
        }
        rapidjson::StringBuffer strbuf;
        rapidjson::Writer<rapidjson::StringBuffer> writer(strbuf);
        document.Accept(writer);
        return strbuf.GetString();
    }

}
