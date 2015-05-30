#include "checkin_node.h"
#include <string>
#include <cstdlib>

#include "../db/checkin.h"
#include "../util/log.h"

using std::string;

using rocksdb::Status;

CheckinNode::CheckinNode() : WAMethodAuthNode("checkin") {
}

void CheckinNode::executeGet(MgConnection& conn, const char* url){
	string user = conn.getParameter("user");

	Checkin checkin;
	Status s = checkin.get(user);
	if(!(s.IsNotFound() || s.ok())){
		conn.sendStatus(MgConnection::STATUS_CODE_INTERNAL_ERROR);
		conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
		conn.printfData("{\"error\":{\"message\":\"%s\",\"code\":1,\"error_user_msg\":\"Ups hubo un error\"}}", s.ToString().c_str());
		Log(Log::LogMsgError) << "Internal server error: " << s.ToString();
		return;
	}

	conn.printfData("%s", checkin.toJson().c_str());
}

void CheckinNode::executePost(MgConnection& conn, const char* url){
	string user = conn.getParameter("user");
	string loggedUser = conn.getParameter("logged_user");

	if(user != loggedUser){
		conn.sendStatus(MgConnection::STATUS_CODE_FORBIDDEN);
		conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
		conn.printfData("{\"error\":{\"message\":\"No podes modificar el checkin de otro\",\"code\":1,\"error_user_msg\":\"No podes modificar el checkin de otro usuario!\"}}");
		return;
	}

	Checkin checkin;
	checkin.setOwner(loggedUser);

	string name = conn.getVarStr("name");
	if(name != "")
		checkin.setName(name);

	double latitude = conn.getVarDouble("latitude");
	double longitude = conn.getVarDouble("longitude");
	checkin.setPosition(latitude, longitude);

	Status s = checkin.put();
	if(!s.ok()){
		conn.sendStatus(MgConnection::STATUS_CODE_INTERNAL_ERROR);
		conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
		conn.printfData("{\"error\":{\"message\":\"%s\",\"code\":1,\"error_user_msg\":\"No se pudo guardar el checkin\"}}", s.ToString().c_str());
		Log(Log::LogMsgError) << "Internal server error: " << s.ToString();
		return;
	}

	conn.sendStatus(MgConnection::STATUS_CODE_CREATED);
	conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
	conn.printfData("{\"success\":true}");
}
