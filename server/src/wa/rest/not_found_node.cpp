#include "not_found_node.h"

NotFoundNode::NotFoundNode(){
}

bool NotFoundNode::match(const char* url){
	return true;
}

void NotFoundNode::execute(MgConnection& conn, const char* url){
	conn.sendStatus(MgConnection::STATUS_CODE_NOT_FOUND);
	conn.sendHeader("Content-Type", "application/json");
	conn.printfData("{ \"message\": \"'%s' no encontrado\", \"code\": 404, \"error_user_msg\": \"Ups... No se encontro!\" }", conn->uri);
}
