//!
//! Copyright (c) 2026, The casual project
//!
//! This software is licensed under the MIT license, https://opensource.org/licenses/MIT
//!

#pragma once

#include <curl/curl.h>
#include <iostream>
#include <gtest/gtest.h>

#include "constants.h"
#include "http.h"

class Wiremock : public testing::Test
{
protected:

    Wiremock()
    {
        curl_global_init(CURL_GLOBAL_ALL);
    }

    ~Wiremock() override
    {
        do_curl("/__admin/requests", casual::java::cli::http::Method::DELETE, "");
        do_curl("/__admin/mappings", casual::java::cli::http::Method::DELETE, "");
    }

    static void create_mapping(const std::string& mapping)
    {
        do_curl("/__admin/mappings", casual::java::cli::http::Method::POST, mapping);
    }

    static void do_curl(const std::string& endpoint, const casual::java::cli::http::Method& http_method, const std::string& request_body)
    {
        std::string port = casual::java::cli::constants::get_env("WIREMOCK_PORT", "8080");
        CURL *curl = curl_easy_init();
        std::string url = std::format("http://localhost:{}{}", port, endpoint);
        std::string response_body;
        long status_code;
        try
        {
            switch (http_method)
            {
                case casual::java::cli::http::Method::POST:
                    curl_easy_setopt(curl, CURLOPT_CUSTOMREQUEST, "POST");
                    break;
                case casual::java::cli::http::Method::PUT:
                    curl_easy_setopt(curl, CURLOPT_CUSTOMREQUEST, "PUT");
                    break;
                case casual::java::cli::http::Method::DELETE:
                    curl_easy_setopt(curl, CURLOPT_CUSTOMREQUEST, "DELETE");
                    break;
                default:
                    break;
            }

            if (http_method == casual::java::cli::http::POST || http_method == casual::java::cli::http::PUT)
            {
                curl_slist *headerList = nullptr;
                headerList = curl_slist_append(headerList, "content-type: application/json");
                curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headerList);
                curl_easy_setopt(curl, CURLOPT_POSTFIELDS, request_body.c_str());
            }
            check_option(curl_easy_setopt(curl, CURLOPT_URL, url.c_str()));
            check_option(curl_easy_setopt(curl, CURLOPT_WRITEDATA, &response_body));
            check_option(curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, write_data));
            check_option(curl_easy_perform(curl));
            curl_easy_getinfo(curl, CURLINFO_RESPONSE_CODE, &status_code);
        }
        catch (std::runtime_error& e)
        {
            std::cerr << e.what() << std::endl;
        }
        curl_easy_cleanup(curl);
    }

private:

    static size_t write_data(void *contents, size_t size, size_t nmemb, void *userp)
    {
        ((std::string*)userp)->append((char*)contents, size * nmemb);
        return size * nmemb;
    }

    static void check_option(CURLcode curl_code)
    {
        if( curl_code != CURLE_OK )
        {
            throw std::runtime_error( curl_easy_strerror( curl_code ) );
        }
    }
};
