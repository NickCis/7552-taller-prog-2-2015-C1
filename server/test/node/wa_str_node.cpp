#include "../catch.hpp"
#include "../../src/node/wa_str_node.h"

/** Mock de MgConnection **/
MgConnection::MgConnection(struct mg_connection*){
}

class TestWAStrNode : public WAStrNode {
	public:
		bool hasExecute;
		TestWAStrNode(const std::string &str) : WAStrNode(str) { }
		TestWAStrNode(const char *str): WAStrNode(str) { }

		bool handle(MgConnection& conn, const char* url){
			hasExecute = false;
			return WAStrNode::handle(conn, url);
		}

	protected:
		virtual void execute(MgConnection&, const char*){
			hasExecute = true;
		}
};

TEST_CASE( "Comprobar el funcionamiento de WAStrNode", "[WAStrNode]" ) {
	TestWAStrNode waStrNode("home/");
	MgConnection conn(NULL);

	SECTION( "Debe matchear con la uri con que fue contruido" ) {
		REQUIRE( waStrNode.handle(conn, "home/") == true );
	}

	SECTION( "Debe matchear con una uri que comience con la que fue contruido" ) {
		REQUIRE( waStrNode.handle(conn, "home/pepe") == true );
		REQUIRE( waStrNode.handle(conn, "home/otra_cosa?asdasd") == true );
	}

	SECTION( "No debe matchear con otra uri" ) {
		REQUIRE( waStrNode.handle(conn, "otra") == false );
		REQUIRE( waStrNode.handle(conn, "home_otra/") == false );
		REQUIRE( waStrNode.handle(conn, "mas/home/") == false );
	}
}

