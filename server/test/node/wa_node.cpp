#include "../catch.hpp"
#include "../../src/node/wa_node.h"

/** Mock de MgConnection **/
MgConnection::MgConnection(struct mg_connection*){
}

class MockWaNode : public WANode {
	public:
		MockWaNode() {}
		bool deboMatchear;
		bool ejecuteExecute;

	protected:
		virtual bool match(const char*){
			return deboMatchear;
		}
		virtual void execute(MgConnection&, const char*){
			ejecuteExecute = true;
		}
};

TEST_CASE( "Comprobar el funcionamiento de WANode", "[WANode]" ) {
	MockWaNode waNode;
	MgConnection conn(NULL);

	SECTION( "Si matchea debe ejecutar execute" ) {
		waNode.deboMatchear = true;
		waNode.ejecuteExecute = false;
		bool ret = waNode.handle(conn, "");

		REQUIRE( ret == true );
		REQUIRE( waNode.ejecuteExecute == true );
	}

	SECTION( "Si no matchea debe no ejecutar execute" ) {
		waNode.deboMatchear = false;
		waNode.ejecuteExecute = false;
		bool ret = waNode.handle(conn, "");

		REQUIRE( ret == false );
		REQUIRE( waNode.ejecuteExecute == false );
	}
}

