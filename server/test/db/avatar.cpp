#include "helper.h"
#include "../catch.hpp"
#include "../../src/db/avatar.h"

#include <vector>
#include <string>

using namespace std;
using namespace rocksdb;

TEST_CASE( "La clase Avatar funciona correctamente?", "[Avatar]" ) {
	DB* db = NewDB();
	Avatar::SetDB(db, db->DefaultColumnFamily());

	SECTION( "Avatar" ) {
		{
			Avatar avatar;
			avatar.setOwner("pepe");
			avatar.setData("esto va a ser una imagen");
			REQUIRE( avatar.put().ok() == true );
		}

		/*SECTION( "El binario tiene qe estar" ) */{
			Avatar avatar;
			REQUIRE( avatar.get("pepe").ok() == true );
			REQUIRE( avatar.getData() == "esto va a ser una imagen" );
		}

	}
	delete db;
}
