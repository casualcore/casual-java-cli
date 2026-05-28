//!
//! Copyright (c) 2026, The casual project
//!
//! This software is licensed under the MIT license, https://opensource.org/licenses/MIT
//!

#pragma once
#include <cstring>
#include <iostream>

namespace casual::java::cli::constants
{
    inline const std::string ENV_CASUAL_JAVA_HOST = "CASUAL_JAVA_HOST";
    inline const std::string ENV_CASUAL_JAVA_VERBOSE = "CASUAL_JAVA_VERBOSE";

    inline void set_env(const std::string& key, const std::string& value)
    {
        putenv(::strdup((key + "=" + value).c_str()));
    }

    inline std::string get_env(const std::string& key)
    {
        const char* value = getenv(key.c_str());
        return value ? std::string( value ) : std::string();
    }

    inline bool has_env(const std::string& key)
    {
        const char* value = getenv(key.c_str());
        return value ? true : false;
    }

    struct ResponseData
    {
        virtual std::string print() const
        {
            return "ResponseData";
        }

        friend std::ostream& operator<<(std::ostream& out, const ResponseData& b)
        {
            out << b.print();
            return out;
        }

        virtual ~ResponseData() = default;
    };
}
