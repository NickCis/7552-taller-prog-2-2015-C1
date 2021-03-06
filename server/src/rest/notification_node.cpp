#include "notification_node.h"
#include <string>
#include <cstdlib>

#include "../db/notification.h"
#include "../util/log.h"

using std::string;

using rocksdb::Status;

NotificationNode::NotificationNode() : WAMethodAuthNode("notification") {
}

void NotificationNode::executeGet(MgConnection& conn, const char*){
	string loggedUser = conn.getParameter("logged_user");

	auto it = Notification::NewIterator();

	if(conn.getHeader("Accept") != NULL && strcmp("text/event-stream", conn.getHeader("Accept")) == 0){
		conn.setParameter("sse", "yes");
		if(conn.getHeader("Last-Event-ID") != NULL){
			string lastId = conn.getHeader("Last-Event-ID");
			Notification::DeleteUpTo(loggedUser, lastId);
		}

		conn.sendStatus(MgConnection::STATUS_CODE_OK);
		conn.sendContentType(MgConnection::CONTENT_TYPE_EVENT_STREAM);
		for(it.seek(loggedUser); it.valid(); it.next())
			conn.printfData("id: %s\ndata: %s\n\n", it.value().getId().c_str(), it.value().toJson().c_str());

		conn.setResponse(MG_MORE);
		return;
	}

	conn.sendStatus(MgConnection::STATUS_CODE_OK);
	conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
	conn.printfData("{\"notifications\":[");

	it.seek(loggedUser);
	for(int counter = 0; it.valid(); it.next(), counter++)
		conn.printfData("%s%s", counter == 0 ? "" : ",", it.value().toJson().c_str());

	conn.printfData("]}");
}

void NotificationNode::executeDelete(MgConnection& conn, const char*){
	string loggedUser = conn.getParameter("logged_user");
	string lastId = conn.getVarStr("id");
	if(lastId.size() < sizeof(uint64_t) * 2){ // TODO: este control lo deberia hacer messages!
		conn.sendStatus(MgConnection::STATUS_CODE_BAD_REQUEST);
		conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
		conn.printfData("{ \"message\": \"Id invalida\", \"code\":%d, \"error_user_msg\":\"Id invalida\"}", MgConnection::STATUS_CODE_BAD_REQUEST);
		return;
	}

	Status s = Notification::DeleteUpTo(loggedUser, lastId);
	if(!s.ok()){
		conn.sendStatus(MgConnection::STATUS_CODE_INTERNAL_ERROR);
		conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
		conn.printfData("{ \"message\": \"Error de db %s\", \"code\":%d, \"error_user_msg\":\"Ups.. No se pudo completar, por favor intenta mas tarde\"}", MgConnection::STATUS_CODE_INTERNAL_ERROR);
		return;
	}

	conn.sendStatus(MgConnection::STATUS_CODE_OK);
	conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
	conn.printfData("{ \"success\": \"true\"}");
}
