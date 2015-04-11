#include "signup_node.h"

#include <iostream>

using std::cout;
using std::endl;

SignupNode::SignupNode() : WAStrNode("signup") {
}

void SignupNode::execute(MgConnection& conn, const char* url){
	cout << "estoy ejecutando!" << endl;
	conn.sendStatus(MgConnection::STATUS_CODE_CREATED);
	conn.sendHeader("Content-Type", "application/json");
	conn.printfData("{ \"result\": %d }", 1);
}
