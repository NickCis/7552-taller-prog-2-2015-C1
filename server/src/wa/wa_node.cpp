#include "wa_node.h"

bool WANode::handle(struct mg_connection* conn, const char* url){
	if(this->match(conn, url)){
		this->execute(conn, url);
		return true;
	}

	return false;
}

WANode::~WANode(){
}
