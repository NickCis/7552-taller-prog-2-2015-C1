#include "helper.h"
#include "../catch.hpp"
#include "../../src/db/notification.h"

#include <vector>
#include <string>

using namespace std;
using namespace rocksdb;

TEST_CASE( "La clase Notification funciona correctamente", "[Notification]" ) {
	DB* db = NewDB();
	Notification::SetDB(db, db->DefaultColumnFamily());

	vector<uint64_t> ids;
	vector<string> datas;
	string lastId;

	SECTION( "Notificaciones" ) { 
		{
			for(int i=0; i < 5; i++){
				Notification n = Notification::Now("foo", Notification::NOTIFICATION_MESSAGE, to_string(i));
				Notification nn = Notification::Now("fooo", Notification::NOTIFICATION_MESSAGE, "NO");
				ids.push_back(n.getIdBin());
				datas.push_back(to_string(i));
				lastId = n.getId();
				REQUIRE( n.put().ok() == true );
				REQUIRE( nn.put().ok() == true );
			}
		}

		/*SECTION( "Notificaciones tienen que volver ordedanas" )*/ {
			auto it = Notification::NewIterator();
			it.seek("foo");
			for(int i=0; it.valid(); it.next(), i++){
				REQUIRE( it.value().getIdBin() == ids[i] );
				REQUIRE( it.value().getData() == datas[i] );
			}
		}

		/*SECTION( "Delete up to tiene que poder borrar" )*/ {
			REQUIRE( Notification::DeleteUpTo("foo", lastId).ok() == true );

			auto it = Notification::NewIterator();
			it.seek("foo");
			for(int i=0; it.valid(); it.next(), i++){
				FAIL("No deberia haber ninguna notificacion");
			}
		}
	}

	delete db;
}
