#include "helper.h"
#include "../catch.hpp"
#include "../../src/db/user.h"
extern "C" {
	#include <sys/time.h>
}
#include <cstdio>
#include <map>

using namespace std;
using namespace rocksdb;

TEST_CASE( "La clase User funciona correctamente", "[User]" ) {
	DB* db = NewDB();
	User::SetDB(db, db->DefaultColumnFamily());

	SECTION( "Test de Usuario" ) {
		{
			User user;
			Status s = user.get("usuario");
			REQUIRE( s.ok() == false );
			REQUIRE( s.IsNotFound() == true );
		}

		{
			User user;
			user.setUsername("usuario");
			user.setPassword("password");
			Status s = user.put();
			REQUIRE( s.ok() == true );
		}

		{
			User user;
			REQUIRE( user.setUsername("usuario") == false);
			user.setPassword("password");
			Status s = user.put();
			REQUIRE( s.ok() != true );
		}

		{
			User u;
			Status s = u.get("usuario");
			REQUIRE( s.ok() == true );
			REQUIRE( u.isPassword("password"));
		}

		{
			User u;
			u.get("usuario");
			u.setPassword("otracontrasena");
			Status s = u.put();
			REQUIRE( s.ok() == true );
		}

		{
			User u;
			Status s = u.get("usuario");
			REQUIRE( s.ok() == true );
			REQUIRE( u.isPassword("otracontrasena"));
		}

		{
			User u;
			u.setUsername("");
			u.setPassword("password");
			Status s = u.put();
			REQUIRE( s.ok() != true );
			REQUIRE( s.IsInvalidArgument() == true );
		}

		{
			User u;
			u.setUsername("me");
			u.setPassword("password");
			Status s = u.put();
			REQUIRE( s.ok() != true );
			REQUIRE( s.IsInvalidArgument() == true );
		}

		{
			User u;
			u.setPassword("password");
			u.setUsername("pepe/a");
			Status s = u.put();
			REQUIRE( s.ok() != true );
			REQUIRE( s.IsInvalidArgument() == true );

			u.setUsername("pepe?a");
			REQUIRE( s.ok() != true );
			REQUIRE( s.IsInvalidArgument() == true );

			u.setUsername("pepe&a");
			REQUIRE( s.ok() != true );
			REQUIRE( s.IsInvalidArgument() == true );

			u.setUsername("pepe=a");
			REQUIRE( s.ok() != true );
			REQUIRE( s.IsInvalidArgument() == true );

			u.setUsername("pepe/asd?asd=asd&a");
			REQUIRE( s.ok() != true );
			REQUIRE( s.IsInvalidArgument() == true );
		}

		{
			User u;
			u.setUsername("otrousuario");
			REQUIRE( u.setPassword("") == false);

			Status s = u.put();
			REQUIRE( s.ok() != true );
			REQUIRE( s.IsInvalidArgument() == true );
		}

		{
			User u;
			u.setUsername("otrousuario");
			REQUIRE( u.setPassword("12345") == false);

			Status s = u.put();
			REQUIRE( s.ok() != true );
			REQUIRE( s.IsInvalidArgument() == true );
		}
	}

	delete db;
}
