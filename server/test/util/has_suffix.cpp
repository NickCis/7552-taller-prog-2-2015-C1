#include "../catch.hpp"
#include "../../src/util/has_suffix.h"

#include <vector>
#include <string>

using namespace std;

TEST_CASE( "Has suffix", "[has_suffix]" ) {
	SECTION( "Tiene que dar verdadero cuando un string tiene el sufijo" ) {
		REQUIRE( has_suffix("string_con_sufijo", "sufijo") );
		REQUIRE( has_suffix("pepe/a/hola", "/hola") );
		REQUIRE( has_suffix("pepe/a/hola", "hola") );
	}

	SECTION( "Tiene que dar false cuando un string no tiene el suffijo" ) {
		REQUIRE( has_suffix("string_con_sufijo", "aaaaaaa") == false );
		REQUIRE( has_suffix("pepe/a/hola", "ssss") == false );
		REQUIRE( has_suffix("pepe/a/hola", "/a/") == false );
	}
}
