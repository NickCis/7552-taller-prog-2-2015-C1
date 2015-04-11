#include "messages_node.h"

#include <iostream>
#include <string>

using std::cout;
using std::endl;
using std::string;

MessagesNode::MessagesNode() : WAMethodNode("messages") {
}

void MessagesNode::executePost(MgConnection& conn, const char* url){
	cout << "messages: estoy ejecutando!" << endl;
	conn.sendStatus(MgConnection::STATUS_CODE_CREATED);
	conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
	conn.printfData("{ \"result\": %d }", 1);
}

void MessagesNode::executeGet(MgConnection& conn, const char* url){
	const string & user = conn.getParameter("user");
	cout << "messages: estoy ejecutando! Get user" << user << endl;
	conn.sendStatus(MgConnection::STATUS_CODE_CREATED);
	conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
	conn.printfData("{ \"result\": %d, \"username\": \"%s\" \"token\": \"%s\"}", 1, user.c_str(), conn.getVarStr("access_token").c_str());
}
