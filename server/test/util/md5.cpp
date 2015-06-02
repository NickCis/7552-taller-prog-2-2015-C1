#include "../catch.hpp"
#include "../../src/util/md5.h"

#include <vector>
#include <string>
#include <cstring>
#include <cstdlib>

using namespace std;

TEST_CASE( "Los hashes de MD5 tiene qe andar bien", "[MD5]" ) {
	const uint8_t result_empty[MD5_BYTE_LENGTH] = {
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

	const uint8_t result_generando[MD5_BYTE_LENGTH] = {
		0x5d,
		0xf9,
		0xf6,
		0x39,
		0x16,
		0xeb,
		0xf8,
		0x52,
		0x86,
		0x97,
		0xb6,
		0x29,
		0x02,
		0x29,
		0x93,
		0xe8
	};

	SECTION( "\"\" - md5()" ) {
		uint8_t digest[MD5_BYTE_LENGTH] = {0};
		md5(digest, (uint8_t*) NULL, 0);
		REQUIRE( memcmp(digest, result_empty, MD5_BYTE_LENGTH) == 0);
	}

	SECTION( "\"\" - md5 input: string" ) {
		string msg = "";

		SECTION( "\"\" - md5(string)" ) {
			vector<char> digest;
			md5(digest, msg);
			REQUIRE( digest.size() == MD5_BYTE_LENGTH);
			REQUIRE( memcmp(digest.data(), result_empty, MD5_BYTE_LENGTH) == 0);
		}

		SECTION( "\"\" - md5Str(string)" ) {
			string digest;
			md5Str(digest, msg);

			REQUIRE( digest.size() == 32 );
			REQUIRE( digest == "d41d8cd98f00b204e9800998ecf8427e" );
		}
	}

	SECTION( "\"\" - md5 input: vector" ) {
		vector<char> msg;
		msg.resize(0);

		SECTION( "\"\" - md5(vector)" ) {
			vector<char> digest;
			md5(digest, msg);
			REQUIRE( digest.size() == MD5_BYTE_LENGTH);
			REQUIRE( memcmp(digest.data(), result_empty, MD5_BYTE_LENGTH) == 0);
		}

		SECTION( "\"\" - md5Str(vector)" ) {
			string digest;
			md5Str(digest, msg);

			REQUIRE( digest.size() == 32 );
			REQUIRE( digest == "d41d8cd98f00b204e9800998ecf8427e" );
		}
	}

	SECTION( "\"Generando un MD5 de un texto\" - md5()" ) {
		char msg[] = { 'G','e','n','e','r','a','n','d','o',' ','u','n',' ','M','D','5',' ','d','e',' ','u','n',' ','t','e','x','t','o' };
		uint8_t digest[MD5_BYTE_LENGTH] = {0};
		md5(digest, (uint8_t*) msg, sizeof(msg));
		REQUIRE( memcmp(digest, result_generando, MD5_BYTE_LENGTH) == 0);
	}

	SECTION( "\"Generando un MD5 de un texto\" - input: vector" ) {
		char _msg[] = { 'G','e','n','e','r','a','n','d','o',' ','u','n',' ','M','D','5',' ','d','e',' ','u','n',' ','t','e','x','t','o' };
		vector<char> msg(_msg, _msg+sizeof(_msg));

		SECTION( "\"Generando un MD5 de un texto\" - md5(vector)" ) {
			vector<char> digest;
			md5(digest, msg);
			REQUIRE( digest.size() == MD5_BYTE_LENGTH);
			REQUIRE( memcmp(digest.data(), result_generando, MD5_BYTE_LENGTH) == 0);
		}

		SECTION( "\"Generando un MD5 de un texto\" - md5Str(vector)" ) {
			string digest;
			md5Str(digest, msg);

			REQUIRE( digest.size() == 32 );
			REQUIRE( digest == "5df9f63916ebf8528697b629022993e8" );
		}
	}

	SECTION( "\"Generando un MD5 de un texto\" - input: string" ) {
		string msg = "Generando un MD5 de un texto";

		SECTION( "\"Generando un MD5 de un texto\" - md5(string)" ) {
			vector<char> digest;
			md5(digest, msg);
			REQUIRE( digest.size() == MD5_BYTE_LENGTH);
			REQUIRE( memcmp(digest.data(), result_generando, MD5_BYTE_LENGTH) == 0);
		}

		SECTION( "\"Generando un MD5 de un texto\" - md5Str(string)" ) {
			string digest;
			md5Str(digest, msg);

			REQUIRE( digest.size() == 32 );
			REQUIRE( digest == "5df9f63916ebf8528697b629022993e8" );
		}
	}
}
