#include "helper.h"
#include "../catch.hpp"
#include "../../src/db/contact_list.h"
#include "../../src/db/contact_list_merge_operator.h"

#include <vector>
#include <string>

using namespace std;
using namespace rocksdb;

TEST_CASE( "La clase ContactList funciona correctamente?", "[ContactList]" ) {
	DB* db = NewDB(NULL, new ContactListMergeOperator);
	ContactList::SetDB(db, db->DefaultColumnFamily());

	SECTION( "ContactList" ) {
		{
			for(int i=0; i < 5; i++){
				ContactList contactList;
				contactList.setOwner("pepe");
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
