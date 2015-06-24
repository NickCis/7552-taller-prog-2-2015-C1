#include "messages_node.h"
#include <string>
#include <cstdlib>

#include "../util/log.h"
#include "../db/message.h"
#include "../util/notifier.h"
#include "../util/has_suffix.h"

using std::atoi;
using std::string;
using std::has_suffix;

using rocksdb::Status;

MessagesNode::MessagesNode() : WAMethodAuthNode("messages") {
}

void MessagesNode::executeGet(MgConnection& conn, const char*){
	string user = conn.getParameter("user");
	string loggedUser = conn.getParameter("logged_user");
	auto it = Message::NewIterator();

	int limit = atoi(conn.getVarStr("limit").c_str());
	if(!limit)
		limit = 20;

	string last_id = conn.getVarStr("last_id");
	if(last_id.size()){
		it.seek(user, loggedUser, last_id);
		if(it.valid())
			it.next();
	}else{
		it.seek(user, loggedUser);
	}

	conn.sendStatus(MgConnection::STATUS_CODE_OK);
	conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
	conn.printfData("{\"messages\":[");

	for(int counter = 0; it.valid() && limit > counter ; it.next(), counter++){
	//for(it->seek(user, loggedUser); it->valid() && limit-- > 0; it->next(), first = false)
		conn.printfData("%s%s", counter == 0 ? "" : ",", it.value().toJson().c_str());
		last_id = it.value().getId();
	}

	conn.printfData("],\"next\":\"/user/%s/messages?limit=%d&last_id=%s&access_token=%s\"}", user.c_str(), limit, last_id.c_str(), conn.getVarStr("access_token").c_str());
}


void MessagesNode::executePost(MgConnection& conn, const char* url){
	string user = conn.getParameter("user");
	string loggedUser = conn.getParameter("logged_user");

	if(this->executeAck(conn, url, user, loggedUser))
		return;

	Message msg = Message::Now(user, loggedUser, conn.getVarStr("message"));
	Status s = msg.put();

	if(! s.ok()){
		conn.sendStatus(MgConnection::STATUS_CODE_BAD_REQUEST);
		conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
		conn.printfData("{\"error\":{\"message\": \"%s\",\"error_user_msg\":\"Problemas obteniendo enviando el mensaje\",\"code\":0}}", s.ToString().c_str());
		Log(Log::LogMsgError) << "Error enviando msje! :: " << s.ToString();
		return;
	}

	conn.sendStatus(MgConnection::STATUS_CODE_CREATED);
	conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
	conn.printfData("{ \"id\": \"%s\", \"time\": %d }", msg.getId().c_str(), msg.getTime());

	Notifier::OnMessage(msg);
}

bool MessagesNode::executeAck(MgConnection& conn, const char* u, const string& user, const string& loggedUser){
	string url = u;
	if(url.back() == '/'){
		if(has_suffix(url, "/arrived/")){
			this->executeAckArrivedRead(conn, false, user, loggedUser);
			return true;
		}else if(has_suffix(url, "/read/")){
			this->executeAckArrivedRead(conn, true, user, loggedUser);
			return true;
		}
	}else{
		if(has_suffix(url, "/arrived")){
			this->executeAckArrivedRead(conn, false, user, loggedUser);
			return true;
		}else if(has_suffix(url, "/read")){
			this->executeAckArrivedRead(conn, true, user, loggedUser);
			return true;
		}
	}

	return false;
}

void MessagesNode::executeAckArrivedRead(MgConnection& conn, bool read, const string& user, const string& loggedUser){
	string id;
	Message msg;
	msg.setTo(loggedUser);
	msg.setFrom(user);

	conn.sendStatus(MgConnection::STATUS_CODE_CREATED);
	conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);

	conn.printfData("{\"messages\":[");
	bool first = true;
	for(int n=0; (id = conn.getVarStr("id[]", n)).length(); n++){
		if(msg.get(id).ok()){
			if(msg.getTo() != loggedUser)
				continue;

			msg.setArrived();
			if(read)
				msg.setRead();
			msg.put();
			conn.printfData("%s%s", first ? "" : ",", msg.toJson().c_str());
			first = false;

			Notifier::OnMessageAck(msg);
		}
	}

	conn.printfData("]}");
}
