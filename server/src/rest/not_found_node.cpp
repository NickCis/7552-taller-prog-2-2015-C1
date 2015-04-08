#include "not_found_node.h"

NotFoundNode::NotFoundNode(){
}

bool NotFoundNode::match(struct mg_connection* conn, const char*& url){
	return true;
}

void NotFoundNode::execute(struct mg_connection* conn, const char* url){
	mg_send_status(conn, 404);
	mg_send_header(conn, "Content-Type", "application/json");
	mg_printf_data(conn, "{ \"message\": \"'%s' no encontrado\", \"code\": 404, \"error_user_msg\": \"Ups... No se encontro!\" }", conn->uri);
}
