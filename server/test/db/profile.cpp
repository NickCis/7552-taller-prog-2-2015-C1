#include "helper.h"
#include "../catch.hpp"
#include "../../src/db/profile.h"

#include <ctime>

#include <vector>
#include <string>

using namespace std;
using namespace rocksdb;

TEST_CASE( "La clase Profile funciona correctamente?", "[Profile]" ) {
	DB* db = NewDB();
	Profile::SetDB(db, db->DefaultColumnFamily());

	SECTION( "Profile" ) {
		time_t status_time = time(NULL);

		{
			Profile profile;
			profile.setOwner("pepe");
			profile.setNick("nickname");
			profile.setOnline(true);
			profile.setStatus("estado");

			REQUIRE( profile.put().ok() == true );
		}

		{
			Profile profile;
			REQUIRE( profile.get("pepe").ok() == true );
			REQUIRE( profile.getOwner() == "pepe" );
			REQUIRE( profile.getNick() == "nickname" );
			REQUIRE( profile.getOnline() == true );
			REQUIRE( profile.getStatus() == "estado" );
			REQUIRE( profile.getStatusTime() == status_time );
		}

		{
			Profile profile;
			REQUIRE( profile.get("no_te_voy_a_encontrar").IsNotFound() == true );
		}

	}
	delete db;
}
