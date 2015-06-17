#include "wa_method_node.h"

#include <cstring>

using std::strcmp;

WAMethodNode::WAMethodNode(const std::string &str) : WAStrNode(str){
}

WAMethodNode::WAMethodNode(const char *str) : WAStrNode(str){
}

void WAMethodNode::execute(MgConnection& conn, const char* url){
	const char *method = conn->request_method;
	if(strcmp(method, "GET") == 0)
		this->executeGet(conn, url);
	else if(strcmp(method, "POST") == 0)
		this->executePost(conn, url);
	else if(strcmp(method, "DELETE") == 0)
		this->executeDelete(conn, url);
	else 
		this->methodNotAllowed(conn, url);
}

void WAMethodNode::executePost(MgConnection& conn, const char* url){
	this->methodNotAllowed(conn, url);
}

void WAMethodNode::executeGet(MgConnection& conn, const char* url){
	this->methodNotAllowed(conn, url);
}

void WAMethodNode::executeDelete(MgConnection& conn, const char* url){
	this->methodNotAllowed(conn, url);
}

void WAMethodNode::methodNotAllowed(MgConnection& conn, const char*){
	conn.sendStatus(MgConnection::STATUS_CODE_METHOD_NOT_ALLOWED);
	conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
	conn.printfData("{ \"message\": \" Method: '%s' en '%s' no manejado\", \"code\": 404, \"error_user_msg\": \"Ups... No se encontro!\" }", conn->request_method, conn->uri);
}
