#include "helper.h"
#include "../catch.hpp"
#include "../../src/db/checkin.h"

#include <ctime>

#include <vector>
#include <string>

using namespace std;
using namespace rocksdb;

template <typename T>
T abs(T t){
	if(t < 0)
		return -t;
	return t;
}

TEST_CASE( "La clase Checkin funciona correctamente?", "[Checkin]" ) {
	DB* db = NewDB();
	Checkin::SetDB(db, db->DefaultColumnFamily());

	SECTION( "Checkin" ) {
		time_t status_time = time(NULL);

		{
			Checkin checkin;
			checkin.setOwner("pepe");
			checkin.setName("lugar lindo");
			checkin.setPosition(-34.812085, -58.388592);
			REQUIRE( checkin.put().ok() == true );
		}

		{
			Checkin checkin;
			REQUIRE( checkin.get("pepe").ok() == true );
			REQUIRE( checkin.getOwner() == "pepe" );
			REQUIRE( checkin.getName() == "lugar lindo" );
			REQUIRE( abs(checkin.getLatitude() +34.812085) < 0.1 );
			REQUIRE( abs(checkin.getLongitude() +58.388592) < 0.1 );
		}

		{
			Checkin checkin;
			REQUIRE( checkin.get("no_te_voy_a_encontrar").IsNotFound() == true );
		}

	}
	delete db;
}
