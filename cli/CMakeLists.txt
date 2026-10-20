cmake_minimum_required(VERSION 3.26.5)
project(casual_java_cli VERSION 1.0)

set(CMAKE_CXX_STANDARD 23)
set(CMAKE_CXX_STANDARD_REQUIRED True)

find_package(GTest REQUIRED)
find_package(CURL REQUIRED)

# Needed for rapidjson, no version exists that can be built on g++ 14
include(FetchContent)
FetchContent_Declare(
  casual_thirdparty
  GIT_REPOSITORY https://github.com/casualcore/casual-thirdparty.git
  GIT_TAG        1.9 # release-1.9
  SOURCE_DIR     casual-thirdparty
)

FetchContent_MakeAvailable(
  casual_thirdparty
)

set(CASUAL_THIRDPARTY_SOURCE_DIR "${casual_thirdparty_SOURCE_DIR}")
set(CASUAL_THIRDPARTY_BINARY_DIR "${casual_thirdparty_BINARY_DIR}")

# Set compiler options
add_compile_options(
  -Wall
  -Wextra
  -Werror
  -Wsign-compare
  -Wuninitialized
  -Winit-self
  -Wno-unused-parameter
  -Wno-missing-declarations
  -Wno-noexcept-type
  -Wno-implicit-fallthrough
  -Wno-missing-field-initializers
  -Wno-dangling-reference
  -Wno-unused-but-set-variable
  -Wno-return-type
  -Wno-address
  -fPIC
  )

if(NOT DEFINED ENV{CMAKE_SOURCE_DIR})
  message( SEND_ERROR "CMAKE_SOURCE_DIR need to be set" )
endif()

if(NOT DEFINED ENV{CMAKE_BINARY_DIR})
  message( SEND_ERROR "CMAKE_BINARY_DIR need to be set" )
endif()

enable_testing()

# casual-java-cli
# ---------------
set(CMAKE_ARCHIVE_OUTPUT_DIRECTORY ${CMAKE_CURRENT_BINARY_DIR}/bin)
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_CURRENT_BINARY_DIR}/bin)
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_CURRENT_BINARY_DIR}/bin)
set(CASUAL_GIT_HOME ${CMAKE_CURRENT_SOURCE_DIR}/../../casual)

message(CHECK_START "Checking for CLion or conan built Casual")
if (IS_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/cmake-build-debug)
  set(CASUAL_GIT_HOME_SO ${CASUAL_GIT_HOME}/cmake-build-debug)
  message( CHECK_PASS "CMAKE_CURRENT_SOURCE_DIR set to ${CASUAL_GIT_HOME_SO} (CLion)" )
else ()
  set(CASUAL_GIT_HOME_SO ${CASUAL_GIT_HOME}/build/Release)
  message( CHECK_PASS "CMAKE_CURRENT_SOURCE_DIR set to ${CASUAL_GIT_HOME_SO} (Conan)" )
endif()

# Libraries from casual
add_library(casual-common SHARED IMPORTED)
set_target_properties(casual-common PROPERTIES
  INTERFACE_INCLUDE_DIRECTORIES ${CASUAL_GIT_HOME}/middleware/common/include
  IMPORTED_LOCATION ${CASUAL_GIT_HOME_SO}/middleware/common/bin/libcasual-common.so
)

add_library(casual-cli SHARED IMPORTED)
set_target_properties(casual-cli PROPERTIES
  INTERFACE_INCLUDE_DIRECTORIES ${CASUAL_GIT_HOME}/middleware/cli/include
  IMPORTED_LOCATION ${CASUAL_GIT_HOME_SO}/middleware/cli/bin/libcasual-cli.so
)

add_library(casual-xatmi SHARED IMPORTED)
set_target_properties(casual-xatmi PROPERTIES
  INTERFACE_INCLUDE_DIRECTORIES ${CASUAL_GIT_HOME}/middleware/xatmi/include
  IMPORTED_LOCATION ${CASUAL_GIT_HOME_SO}/middleware/xatmi/bin/libcasual-xatmi.so
)

# Casual Java CLI
add_executable(casual-java)

target_include_directories(casual-java PRIVATE
  $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/include>
  $<BUILD_INTERFACE:${CASUAL_THIRDPARTY_SOURCE_DIR}/rapidjson/include>
)

link_directories(
  $<BUILD_INTERFACE:${CMAKE_CURRENT_BINARY_DIR}/bin>
)

target_sources(casual-java
  PRIVATE
  ./source/cli.cpp
  ./source/configuration.cpp
  ./source/discovery.cpp
  ./source/service.cpp
  ./source/queue.cpp
  ./source/http.cpp
)

target_link_libraries(casual-java PRIVATE
  casual-common
  casual-cli
  casual-xatmi
  curl
)

install(
  TARGETS
  casual-java
  DESTINATION
  bin
)

# ---- Test -----
# Wiremock
file(
  DOWNLOAD https://repo1.maven.org/maven2/org/wiremock/wiremock-standalone/3.13.2/wiremock-standalone-3.13.2.jar
  ../unittest/wiremock-standalone.jar
)

add_library(casual-administration-unittest SHARED IMPORTED)
set_target_properties(casual-administration-unittest PROPERTIES
  INTERFACE_INCLUDE_DIRECTORIES ${CASUAL_GIT_HOME}/middleware/administration/unittest/include
  IMPORTED_LOCATION ${CASUAL_GIT_HOME_SO}/middleware/administration/bin/libcasual-administration-unittest.so
)

# Unittest
add_executable(casual-java-cli-unittest)
target_include_directories(casual-java-cli-unittest PRIVATE
  $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/unittest/include>
  $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/unittest/util/include>
  $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/include>
)

target_sources(casual-java-cli-unittest
  PRIVATE
        unittest/source/test_service.cpp
)

target_link_libraries(casual-java-cli-unittest PRIVATE
  casual-common
  casual-xatmi
  casual-administration-unittest
  curl
  gtest::gtest
  gtest_main
)

set(test_environment
  WIREMOCK_PORT "${WIREMOCK_PORT}" 8080
)

enable_testing()
include(GoogleTest)
gtest_discover_tests(casual-java-cli-unittest "" AUTO
  PROPERTIES
  ENVIRONMENT ${test_environment}
)