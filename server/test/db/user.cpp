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
	char path[255];
	struct timeval tv;
	gettimeofday(&tv, NULL);
	sprintf(path, "/tmp/testdb_%ld_%ld", tv.tv_sec, tv.tv_usec);

	DB* db;
	Options options;
	options.create_if_missing = true;
	DB::Open(options, path, &db);

	User::SetDB(db, db->DefaultColumnFamily());

	SECTION( "Get de algo qe no existe debe devolver en error" ) {
		{
			User user;
			Status s = User::Get("usuario", user);
			REQUIRE( s.ok() == false );
			REQUIRE( s.IsNotFound() == true );
		}

		/*SECTION( "Put debe agregar un usuario" )*/ {
			Status s = User::Put("usuario", "password");
			REQUIRE( s.ok() == true );

			User u;
			s = User::Get("usuario", u);
			REQUIRE( s.ok() == true );
			REQUIRE( u.getPassword() == "password" );
		}

		/*SECTION( "Se debe poder cambiar la contrase~na" )*/ {
			User u("usuario");
			Status s = u.setPassword("otracontrasena");
			REQUIRE( s.ok() == true );
		}

		/*SECTION( "No se debe poder crear username vacio" )*/ {
			Status s = User::Put("", "password");
			REQUIRE( s.ok() != true );
			REQUIRE( s.IsInvalidArgument() == true );
		}

		/*SECTION( "No se debe poder crear username = me" ) */{
			Status s = User::Put("me", "password");
			REQUIRE( s.ok() != true );
			REQUIRE( s.IsInvalidArgument() == true );
		}

		/*SECTION( "No se debe poder crear username con / & ? =" )*/ {
			Status s = User::Put("pepe/a", "password");
			REQUIRE( s.ok() != true );
			REQUIRE( s.IsInvalidArgument() == true );

			s = User::Put("pepe?a", "password");
			REQUIRE( s.ok() != true );
			REQUIRE( s.IsInvalidArgument() == true );

			s = User::Put("pepe&a", "password");
			REQUIRE( s.ok() != true );
			REQUIRE( s.IsInvalidArgument() == true );

			s = User::Put("pepe=a", "password");
			REQUIRE( s.ok() != true );
			REQUIRE( s.IsInvalidArgument() == true );

			s = User::Put("pepe/asd?asd=asd&a", "password");
			REQUIRE( s.ok() != true );
			REQUIRE( s.IsInvalidArgument() == true );
		}

		/*SECTION( "Password no debe ser vacia" ) */{
			Status s = User::Put("otrousuario", "");
			REQUIRE( s.ok() != true );
			REQUIRE( s.IsInvalidArgument() == true );
		}

		/*SECTION( "Password no debe tener menos de 6 caracteres" )*/ {
			Status s = User::Put("otrousuario", "12345");
			REQUIRE( s.ok() != true );
			REQUIRE( s.IsInvalidArgument() == true );
		}
	}

	delete db;
}
