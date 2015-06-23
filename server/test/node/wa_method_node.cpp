#include "../catch.hpp"
#include "../../src/node/wa_method_node.h"

#include <string>
#include <cstring>

using namespace std;

/** Mock de MgConnection **/
MgConnection::MgConnection(struct mg_connection* c) : conn(c){}
void MgConnection::sendStatus(MgConnection::StatusCodes){}
void MgConnection::sendContentType(MgConnection::ContentTypes){}
size_t MgConnection::printfData(const char*, ...){ return 0;}
struct mg_connection* MgConnection::operator->(){ return this->conn; }


class MockWAMethodNode : public WAMethodNode {
	public:
		MockWAMethodNode() : WAMethodNode(""), hasExecutePost(false), hasExecuteGet(false), hasExecuteDelete(false), hasExecuteNotAllowed(false) {}

		void _execute(MgConnection& conn, const char* url){
			this->execute(conn, url);
		}

		bool hasExecutePost;
		bool hasExecuteGet;
		bool hasExecuteDelete;
		bool hasExecuteNotAllowed;

	protected:
		virtual void executePost(MgConnection& conn, const char* url){
			hasExecutePost = true;
			WAMethodNode::executePost(conn, url);
		}

		void executeGet(MgConnection& conn, const char* url){
			hasExecuteGet = true;
			WAMethodNode::executeGet(conn, url);
		}

		void executeDelete(MgConnection& conn, const char* url){
			hasExecuteDelete = true;
			WAMethodNode::executeDelete(conn, url);
		}

		void methodNotAllowed(MgConnection& conn, const char* url){
			hasExecuteNotAllowed = true;
			WAMethodNode::methodNotAllowed(conn, url);
		}
};

TEST_CASE( "Comprobar el funcionamiento de WAMethodNode", "[WAMethodNode]" ) {
	MockWAMethodNode waMethodNode;
	struct mg_connection mg_conn;
	MgConnection conn(&mg_conn);

	SECTION( "Se prueba POST" ) {
		mg_conn.request_method = "POST";
		waMethodNode._execute(conn, "pepe");
		REQUIRE( waMethodNode.hasExecutePost == true );
		REQUIRE( waMethodNode.hasExecuteGet == false );
		REQUIRE( waMethodNode.hasExecuteDelete == false );
		REQUIRE( waMethodNode.hasExecuteNotAllowed == true );
	}

	SECTION( "Se prueba GET" ) {
		mg_conn.request_method = "GET";
		waMethodNode._execute(conn, "");
		REQUIRE( waMethodNode.hasExecutePost == false );
		REQUIRE( waMethodNode.hasExecuteGet == true );
		REQUIRE( waMethodNode.hasExecuteDelete == false );
		REQUIRE( waMethodNode.hasExecuteNotAllowed == true );
	}

	SECTION( "Se prueba DELETE" ) {
		mg_conn.request_method = "DELETE";
		waMethodNode._execute(conn, "");
		REQUIRE( waMethodNode.hasExecutePost == false );
		REQUIRE( waMethodNode.hasExecuteGet == false );
		REQUIRE( waMethodNode.hasExecuteDelete == true );
		REQUIRE( waMethodNode.hasExecuteNotAllowed == true );
	}
}
