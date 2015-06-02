#include "../catch.hpp"
#include "../../src/util/bin2hex.h"

#include <vector>
#include <string>
#include <cstring>
#include <cstdlib>

using namespace std;
TEST_CASE("bin2hex", "[bin2hex]") {
	unsigned char data[] = {
		0xd4,
		0x1d,
		0x8c,
		0xd9,
		0x8f,
		0x00,
		0xb2,
		0x04,
		0xe9,
		0x80,
		0x09,
		0x98,
		0xec,
		0xf8,
		0x42,
		0x7e
	};

	SECTION( "Bin2hex" ) {
		SECTION( "Vector" ) {
			vector<char> _data(data, data+sizeof(data));
			string result = bin2hex(_data);
			REQUIRE( result == "d41d8cd98f00b204e9800998ecf8427e" );
		}

		SECTION( "template start end" ) {
			string result = bin2hex(data, data+sizeof(data));
			REQUIRE( result == "d41d8cd98f00b204e9800998ecf8427e" );
		}

		SECTION( "template start +size" ) {
			string result = bin2hex(data, sizeof(data));
			REQUIRE( result == "d41d8cd98f00b204e9800998ecf8427e" );
		}

		SECTION( "tipe" ) {
			uint32_t _data;
			((unsigned char*) &_data)[0] = 0x14;
			((unsigned char*) &_data)[1] = 0x2a;
			((unsigned char*) &_data)[2] = 0xcb;
			((unsigned char*) &_data)[3] = 0xfd;
			string result = bin2hex(_data);
			REQUIRE( result == "142acbfd" );
		}
	}
	SECTION( "hex2bin" ) {
		vector<char> result = hex2bin("d41d8cd98f00b204e9800998ecf8427e");
		REQUIRE( result.size() == sizeof(data));
		REQUIRE( memcmp(result.data(), data, sizeof(data)) == 0);
	}
}
