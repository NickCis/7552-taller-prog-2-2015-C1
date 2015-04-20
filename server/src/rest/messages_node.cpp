#include "messages_node.h"


#include <iostream>
#include <string>

#include "../db/message.h"
#include "../util/bin2hex.h"

using std::cout;
using std::endl;
using std::string;

using rocksdb::Status;

MessagesNode::MessagesNode() : WAMethodAuthNode("messages") {
}

void MessagesNode::executeGet(MgConnection& conn, const char* url){
	cout << "messages: estoy ejecutando!" << endl;
	conn.sendStatus(MgConnection::STATUS_CODE_CREATED);
	conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
	conn.printfData("{ \"result\": %d }", 1);
}

void MessagesNode::executePost(MgConnection& conn, const char* url){
	Message msg;
	Status s = Message::Put(conn.getParameter("user"), conn.getParameter("logged_user"), conn.getVarStr("message"), msg);

	if(! s.ok()){
		conn.sendStatus(MgConnection::STATUS_CODE_BAD_REQUEST);
		conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
		conn.printfData("{ \"message\": \"%s\",  \"error_user_msg\": \"Problemas obteniendo enviando el mensaje\"}", s.ToString().c_str());
		return;
	}

	conn.sendStatus(MgConnection::STATUS_CODE_CREATED);
	conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
	conn.printfData("{ \"id\": \"%s\", \"time\": %d }", 1, bin2hex(&msg.getTime(), sizeof(uint64_t)).c_str(), msg.getTime()/1000000);
}
