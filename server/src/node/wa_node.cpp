#include "wa_node.h"

bool WANode::handle(MgConnection& conn, const char* url){
	if(this->match(url)){
		this->execute(conn, url);
		return true;
	}

	return false;
}

WANode::~WANode(){
}
