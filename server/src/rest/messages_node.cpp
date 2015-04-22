#include "messages_node.h"
#include <string>

#include "../db/message.h"

using std::stoi;
using std::string;

using rocksdb::Status;

MessagesNode::MessagesNode() : WAMethodAuthNode("messages") {
}

void MessagesNode::executeGet(MgConnection& conn, const char* url){
	string user = conn.getParameter("user");
	string loggedUser = conn.getParameter("logged_user");
	auto it = Message::NewIterator();

	// TODO: hacer limite dinamico
	//int limit = stoi(conn.getParameter("limit"));
	int limit = 20;

	conn.sendStatus(MgConnection::STATUS_CODE_OK);
	conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
	conn.printfData("{\"next\":\"algun dia sera\",[");
	bool first = true;

	for(it->seekToLast(user, loggedUser); it->valid() && limit-- > 0 ; it->prev())
		conn.printfData("%s%s", first ? "" : ",", it->value().toJson().c_str());

	conn.printfData("]}");
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
	conn.printfData("{ \"id\": \"%s\", \"time\": %d }", msg.getId().c_str(), msg.getTime()/1000000);
}
