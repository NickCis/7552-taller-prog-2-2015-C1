# http://www.cmake.org/cmake-tutorial/

cmake_minimum_required (VERSION 2.6)
project (Test)

list(APPEND CMAKE_MODULE_PATH ${PROJECT_SOURCE_DIR}/../CMake)

macro (add_executable _name)
	# invoke built-in add_executable
	_add_executable(${ARGV})
	if (TARGET ${_name})
		target_link_libraries(${_name} Main)
	endif()
endmacro()

enable_testing()

add_library(Main STATIC main.cpp)

add_executable(not_found_node ${MAIN} rest/not_found_node.cpp ../src/rest/not_found_node.cpp ../src/node/wa_node.cpp)
add_executable(wa_node ${MAIN} node/wa_node.cpp ../src/node/wa_node.cpp)
add_executable(wa_str_node ${MAIN} node/wa_str_node.cpp ../src/node/wa_str_node.cpp ../src/node/wa_node.cpp)
add_executable(db_comparator ${MAIN} db/db_comparator.cpp ../src/db/db_comparator.cpp)
add_executable(db_user ${MAIN} db/user.cpp ../src/db/user.cpp)

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

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -W -Wall -fprofile-arcs -ftest-coverage -g -O0")

find_package(RocksDB)
if(ROCKSDB_FOUND)
	include_directories("${ROCKSDB_INCLUDE_DIRS}")
	target_link_libraries(db_user ${ROCKSDB_LIBRARIES})
	target_link_libraries(db_comparator ${ROCKSDB_LIBRARIES})
else()
	message(FATAL_ERROR "No se encuentra RocksDB")
endif()

include_directories("${PROJECT_BINARY_DIR}")

# Tests
add_test(WANode wa_node --force-colour)
add_test(WAStrNode wa_str_node --force-colour)
add_test(NotFoundNode not_found_node --force-colour)
add_test(DbComparator db_comparator --force-colour)
add_test(DbUser db_user --force-colour)
