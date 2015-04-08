#include "wa_parent_node.h"

WAParentNode::~WAParentNode(){
	for(auto it=children.begin(); it != children.end(); it++)
		delete (*it);
}

void WAParentNode::execute(struct mg_connection* conn, const char* url){
	for(auto it=children.begin(); it != children.end(); it++)
		if((*it)->handle(conn, url))
			break;
}
