#include "notification_node.h"
#include <string>
#include <cstdlib>

#include "../db/notification.h"

using std::string;

using rocksdb::Status;

NotificationNode::NotificationNode() : WAMethodAuthNode("notification") {
}

void NotificationNode::executeGet(MgConnection& conn, const char* url){
	string loggedUser = conn.getParameter("logged_user");
	auto it = Notification::NewIterator();

	conn.sendStatus(MgConnection::STATUS_CODE_OK);
	conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
	conn.printfData("{\"notifications\":[");

	for(int counter = 0; it->valid(); it->next(), counter++)
		conn.printfData("%s%s", counter == 0 ? "" : ",", it->value().toJson().c_str());

	conn.printfData("]}");
}

void NotificationNode::executeDelete(MgConnection& conn, const char* url){
	conn.sendStatus(MgConnection::STATUS_CODE_OK);
	conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
	conn.printfData("{ \"success\": \"true\"}");
}
