#include "helper.h"
#include "../catch.hpp"
#include "../../src/db/message.h"
#include "../../src/db/db_comparator.h"

#include <vector>
#include <string>

#ifndef _BSD_SOURCE
#define _BSD_SOURCE
#endif
#include <endian.h>

using namespace std;
using namespace rocksdb;

TEST_CASE( "La clase Message funciona correctamente", "[Message]" ) {
	DbComparatorReverse cmp;
	DB* db = NewDB(&cmp, (ListMergeOperator*) NULL);
	Message::SetDB(db, db->DefaultColumnFamily());

	vector<uint64_t> ids;
	vector<string> datas;
	string lastId;

	SECTION( "Basico" ) {
		time_t now = time(NULL);
		string id = "";
		{
			Message msg = Message::Now("to", "from", "mensaje");
			REQUIRE( msg.put().ok() == true );
			id = msg.getId();
		}

		{
			Message msg;
			msg.setTo("to");
			msg.setFrom("from");

			REQUIRE( msg.get(id).ok() == true );
			REQUIRE( msg.getTo() == "to" );
			REQUIRE( msg.getFrom() == "from" );
			REQUIRE( msg.getMsg() == "mensaje" );
			REQUIRE( msg.getTime() == now );
			REQUIRE( msg.getId() == id );
		}

	}

	SECTION( "El orden de los mensajes debe ser del mas nuevo al mas viejo" ) { 
		for(int i=0; i < 5; i++){
			REQUIRE( Message::Now("to", "from", "mensaje").put().ok() == true);
			REQUIRE( Message::Now("pepe", "from", "mensaje").put().ok() == true);
			REQUIRE( Message::Now("from", "to", "mensaje").put().ok() == true);
		}

		{
			uint64_t lastId = 0;
			int i=0;
			auto it=Message::NewIterator();
			for(it.seek("to", "from"); it.valid(); it.next()){
				const Message& msg = it.value();

				uint64_t id = msg.getIdBin();
				id = be64toh(id);

				REQUIRE( id > lastId );
				REQUIRE( (msg.getTo() == "to" || msg.getFrom() == "to") );
				REQUIRE( (msg.getTo() == "from" || msg.getFrom() == "from") );
				REQUIRE( msg.getMsg() == "mensaje");
				i++;
			}

			REQUIRE( i == 10);
		}
	}

	delete db;
}
