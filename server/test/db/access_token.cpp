#include "helper.h"
#include "../catch.hpp"
#include "../../src/db/access_token.h"

#include <vector>
#include <string>

using namespace std;
using namespace rocksdb;

TEST_CASE( "La clase AccessToken funciona correctamente?", "[AccessToken]" ) {
	DB* db = NewDB();
	AccessToken::SetDB(db, db->DefaultColumnFamily());

	SECTION( "AccessToken - Read" ) {
		string t = "";
		{
			AccessToken token;
			token.setOwner("pepe");
			REQUIRE( token.put().ok() == true );
			t = token.getToken();
		}

		{
			REQUIRE( AccessToken::IsLoggedIn(t) == true );
		}
		{
			AccessToken token;
			token.get(t);
			REQUIRE( token.getOwner() == "pepe" );
		}

		{
			REQUIRE( AccessToken::IsLoggedIn("no_existe_este_token") == false );
		}

	}

	delete db;
}
