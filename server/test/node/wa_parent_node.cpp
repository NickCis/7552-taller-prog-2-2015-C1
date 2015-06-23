#include "../catch.hpp"
#include "../../src/node/wa_parent_node.h"

#include <string>
#include <cstring>

using namespace std;

/** Mock de MgConnection **/
MgConnection::MgConnection(struct mg_connection*){
}

class MockWAParentNode : public WAParentNode {
	public:
		MockWAParentNode(string s) : WAParentNode(s) {}

		void _execute(MgConnection& conn, const char* url){
			this->execute(conn, url);
		}

		bool _preExecute(MgConnection& conn, const char*& url){
			return this->preExecute(conn, url);
		}

		void addNode(WANode* n){
			this->children.push_back(n);
		}
};

class MockWANode : public WANode {
	public:
		MockWANode() : has_execute(false), has_to_match(true) {}
		bool has_execute;
		bool has_to_match;

	protected:
		virtual bool match(const char*){
			return has_to_match;
		}

		virtual void execute(MgConnection&, const char*){
			has_execute = true;
		}



};

TEST_CASE( "Comprobar el funcionamiento de WAParentNode", "[WAParentNode]" ) {
	MockWAParentNode waParentNode("parent_node/");
	MockWANode *node1 = new MockWANode();
	MockWANode *node2 = new MockWANode();
	waParentNode.addNode(node1);
	waParentNode.addNode(node2);
	MgConnection conn(NULL);

	SECTION( "preExeucte debe adelantar url y devolver true" ) {
		const char * url = "parent_node/pepe";
		REQUIRE( waParentNode._preExecute(conn, url) == true );
		REQUIRE( strcmp(url, "pepe") == 0 );
	}

	SECTION( "Execute recorre a todos los hijos" ) {
		node1->has_to_match = false;
		node2->has_to_match = false;

		waParentNode._execute(conn, "parent_node/pepe");

		REQUIRE( node1->has_execute == false );
		REQUIRE( node2->has_execute == false );
	}

	SECTION( "Execute recorre hasta que un hijo matchee" ) {
		node1->has_to_match = true;
		node2->has_to_match = false;

		waParentNode._execute(conn, "parent_node/pepe");

		REQUIRE( node1->has_execute == true );
		REQUIRE( node2->has_execute == false );
	}
}

