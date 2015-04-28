# http://www.cmake.org/cmake-tutorial/

cmake_minimum_required (VERSION 2.6)
project (Test)

enable_testing()

file( GLOB MAIN main.cpp)

add_executable(not_found_node ${MAIN} rest/not_found_node.cpp ../src/rest/not_found_node.cpp ../src/node/wa_node.cpp)
add_executable(wa_node ${MAIN} node/wa_node.cpp ../src/node/wa_node.cpp)

include(CheckCXXCompilerFlag)
CHECK_CXX_COMPILER_FLAG("-std=c++11" COMPILER_SUPPORTS_CXX11)
if(COMPILER_SUPPORTS_CXX11)
	set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")
else()
	message(FATAL_ERROR "El compilador ${CMAKE_CXX_COMPILER} no tiene soporte para C++11. Por favor usar otro compilador.")
endif()

CHECK_CXX_COMPILER_FLAG("-pthread" COMPILER_SUPPORTS_PTHREAD)
if(COMPILER_SUPPORTS_PTHREAD)
	set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -pthread -DMONGOOSE_ENABLE_THREADS")
else()
	message(FATAL_ERROR "El compilador ${CMAKE_CXX_COMPILER} no tiene soporte para pthreads. Por favor usar otro compilador.")
endif()

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -W -Wall")

include_directories("${PROJECT_BINARY_DIR}")

# Tests
add_test(WaNode wa_node --force-colour)
add_test(NotFoundNode not_found_node --force-colour)
