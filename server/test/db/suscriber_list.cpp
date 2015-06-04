#include "helper.h"
#include "../catch.hpp"
#include "../../src/db/suscriber_list.h"
#include "../../src/db/list_merge_operator.h"

#include <vector>
#include <string>

using namespace std;
using namespace rocksdb;

TEST_CASE( "La clase SuscriberList funciona correctamente?", "[SuscriberList]" ) {
	DB* db = NewDB(NULL, new ListMergeOperator);
	SuscriberList::SetDB(db, db->DefaultColumnFamily());

	SECTION( "SuscriberList" ) {
		{
			SuscriberList suscriberList;
			suscriberList.setOwner("pepe");
			for(int i=0; i < 5; i++){
				REQUIRE( suscriberList.push_back(to_string(i)).ok() == true );
			}
		}

		{
			SuscriberList suscriberList;
			REQUIRE( suscriberList.get("pepe").ok() == true );
			int i=0;
			for(auto it=suscriberList.getList().begin(); it!=suscriberList.getList().end(); it++){
				REQUIRE( (*it) == to_string(i++) );
			}
		}

		{
			SuscriberList suscriberList;
			suscriberList.setOwner("pepe");

			REQUIRE( suscriberList.erase("2").ok() == true );
		}

		{
			SuscriberList suscriberList;
			REQUIRE( suscriberList.get("pepe").ok() == true );
			for(auto it=suscriberList.getList().begin(); it!=suscriberList.getList().end(); it++){
				REQUIRE( (*it) != "2" );
			}
		}

	}
	delete db;
}
