#define CATCH_CONFIG_MAIN
#include "../catch.hpp"
#include "../../src/rest/not_found_node.h"

/** Mock de MgConnection **/
MgConnection::StatusCodes mockCode;
MgConnection::ContentTypes mockType;

MgConnection::MgConnection(struct mg_connection*){
}

void MgConnection::sendStatus(MgConnection::StatusCodes code){
	mockCode = code;
}

size_t MgConnection::printfData(const char*, ...){
	return 0;
}

void MgConnection::sendContentType(MgConnection::ContentTypes type){
	mockType = type;
}

struct mg_connection* MgConnection::operator->(){
	static mg_connection conn;
	static char uri[255] = "pepe";
	conn.uri = uri;
	return &conn;
}

/** Expongo los metodos a probar **/
class NotFoundNodeUnProtected : public NotFoundNode {
	public:
		bool _match(const char* url){
			return this->match(url);
		}
		void _execute(MgConnection& conn, const char* url){
			this->execute(conn, url);
		}
};

TEST_CASE( "Comprobar el funcionamiento de NotFoundNode", "[NotFoundNode]" ) {
	NotFoundNodeUnProtected notFoundNode;

	SECTION( "debe matchear todo" ) {
		REQUIRE( notFoundNode._match("pepe") == true );
		REQUIRE( notFoundNode._match("otracosa") == true );
		REQUIRE( notFoundNode._match("mas texto") == true );
	}

	SECTION( "debe dar respuesta de error en json" ) {
		MgConnection mockConn(NULL);
		notFoundNode._execute(mockConn, "pepe");
		REQUIRE( mockCode == MgConnection::STATUS_CODE_NOT_FOUND );
		REQUIRE( mockType == MgConnection::CONTENT_TYPE_JSON );
	}
}

