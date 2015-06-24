#include "broadcast_node.h"

#include "../util/log.h"
#include "../db/profile.h"
#include "../db/message.h"
#include "../util/notifier.h"
#include "../db/contact_list.h"

#include <ctime>
#include <string>
#include <cstdlib>

using std::string;
using std::time;

using rocksdb::Status;

BroadcastNode::BroadcastNode() : WAMethodAuthNode("broadcast") {
}

void BroadcastNode::executePost(MgConnection& conn, const char*){
	string loggedUser = conn.getParameter("logged_user");
	string msgText = conn.getVarStr("message");

	if(msgText.size() == 0){
		conn.sendStatus(MgConnection::STATUS_CODE_BAD_REQUEST);
		conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
		conn.printfData("{\"error\":{\"message\":\"No hay campo 'message'\",\"error_user_msg\":\"No hay campo 'message'\",\"code\":0}}");
		return;
	}

	ContactList cl;

	conn.sendStatus(MgConnection::STATUS_CODE_CREATED);
	conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
	if(! cl.get(loggedUser).ok()){
		conn.printfData("{\"messages\":[],\"time\":%d}", time(NULL));
		return;
	}

	conn.printfData("{\"messages\":[", cl.toJson().c_str());
	bool first = true;
	for(auto it=cl.getList().begin(); it != cl.getList().end(); it++){
		Profile profile;
		profile.get(*it);

		if(!profile.getOnlineStatus())
			continue;

		Message msg = Message::Now(*it, loggedUser, msgText);
		Status s = msg.put();
		if(!s.ok()){
			Log(Log::LogMsgError) << "Fallo enviando msje! :: " << s.ToString();
			continue;
		}

		conn.printfData("%s%s", first ? "" : ",", msg.toJson().c_str());
		Notifier::OnMessage(msg);
		first = false;
	}

	conn.printfData("],\"time\":%d}", time(NULL));
}
