# http://www.cmake.org/cmake-tutorial/

cmake_minimum_required (VERSION 2.6)
project (Server)

# The version number.
set (SERVER_VERSION_MAJOR 1)
set (SERVER_VERSION_MINOR 0)

include(CheckCXXCompilerFlag)
CHECK_CXX_COMPILER_FLAG("-std=c++11" COMPILER_SUPPORTS_CXX11)
if(COMPILER_SUPPORTS_CXX11)
	set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")
else()
	message(STATUS "El compilador ${CMAKE_CXX_COMPILER} no tiene soporte para C++11. Por favor usar otro compilador.")
endif()

CHECK_CXX_COMPILER_FLAG("-pthread" COMPILER_SUPPORTS_PTHREAD)
if(COMPILER_SUPPORTS_PTHREAD)
	set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -pthread -DMONGOOSE_ENABLE_THREADS")
else()
	message(STATUS "El compilador ${CMAKE_CXX_COMPILER} no tiene soporte para pthreads. Por favor usar otro compilador.")
endif()

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -W -Wall")

# configure a header file to pass some of the CMake settings
# to the source code
configure_file (
  "${PROJECT_SOURCE_DIR}/src/server_config.h.in"
  "${PROJECT_BINARY_DIR}/server_config.h"
)

include_directories("${PROJECT_BINARY_DIR}")

file( GLOB WA_FILES src/wa_server.cpp src/node/wa_node.cpp src/node/wa_str_node.cpp src/node/wa_method_node.cpp src/node/wa_parent_node.cpp)
file( GLOB REST_FILES src/rest/root_node.cpp src/rest/signup_node.cpp src/rest/not_found_node.cpp)
file( GLOB MONGOOSE_FILES src/mg/mg_server.cpp src/mg/mg_connection.cpp src/mg/mongoose/mongoose.c)

# add the executable
add_executable(server src/main.cpp ${WA_FILES} ${REST_FILES} ${MONGOOSE_FILES})
