#include "helper.h"
#include "../catch.hpp"
#include "../../src/db/contact_list.h"
#include "../../src/db/suscriber_list.h"
#include "../../src/db/list_merge_operator.h"

#include <vector>
#include <string>

using namespace std;
using namespace rocksdb;


TEST_CASE( "La clase ContactList funciona correctamente?", "[ContactList]" ) {
	DB* db = NewDB(NULL, new ListMergeOperator);
	ContactList::SetDB(db, db->DefaultColumnFamily());
	SuscriberList::SetDB(db, db->DefaultColumnFamily());

	SECTION( "ContactList" ) {
		{
			ContactList contactList;
			contactList.setOwner("pepe");
			for(int i=0; i < 5; i++){
				REQUIRE( contactList.push_back(to_string(i)).ok() == true );
			}
		}

		/*SECTION( "Los contactos tienen que estar" ) */{
			ContactList contactList;
			REQUIRE( contactList.get("pepe").ok() == true );
			int i=0;
			for(auto it=contactList.getList().begin(); it!=contactList.getList().end(); it++){
				REQUIRE( (*it) == to_string(i++) );
			}

			REQUIRE( i == 5 );
		}

		/*SECTION( "Tengo que poder borrar contactos de la lista" )*/ {
			ContactList contactList;
			contactList.setOwner("pepe");

			REQUIRE( contactList.erase("2").ok() == true );
		}

		/*SECTION( "El contacto borrado no tiene que estar" )*/ {
			ContactList contactList;
			REQUIRE( contactList.get("pepe").ok() == true );
			for(auto it=contactList.getList().begin(); it!=contactList.getList().end(); it++){
				REQUIRE( (*it) != "2" );
			}
		}

	}
	delete db;
}
