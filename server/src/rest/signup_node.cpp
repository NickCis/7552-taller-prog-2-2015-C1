#include "signup_node.h"

#include <iostream>

using std::cout;
using std::endl;

SignupNode::SignupNode() : WAMethodNode("signup") {
}

void SignupNode::executePost(MgConnection& conn, const char* url){
	cout << "estoy ejecutando!" << endl;
	conn.sendStatus(MgConnection::STATUS_CODE_CREATED);
	conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
	conn.printfData("{ \"result\": %d }", 1);
}

void SignupNode::executeGet(MgConnection& conn, const char* url){
	cout << "estoy ejecutando! Get" << endl;
	conn.sendStatus(MgConnection::STATUS_CODE_CREATED);
	conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
	conn.printfData("{ \"result\": %d }", 1);
}
