#include "signup_node.h"

#include <iostream>

using std::cout;
using std::endl;

SignupNode::SignupNode(){
}

bool SignupNode::match(struct mg_connection* conn, const char*& url){
	cout << "singupnode - url '" << url << "'" << endl;
	WA_COMPARE_AND_SUM(url, "signup");
	return false;
}

void SignupNode::execute(struct mg_connection* conn, const char* url){
	cout << "estoy ejecutando!" << endl;
	mg_send_status(conn, 201);
	mg_send_header(conn, "Content-Type", "application/json");
	mg_printf_data(conn, "{ \"result\": %d }", 1);
}
