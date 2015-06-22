#include "profile_node.h"
#include <string>
#include <cstdlib>

#include "../db/profile.h"
#include "../util/log.h"
#include "../util/notifier.h"

using std::string;

using rocksdb::Status;

ProfileNode::ProfileNode() : WAMethodAuthNode("profile") {
}

void ProfileNode::executeGet(MgConnection& conn, const char*){
	string user = conn.getParameter("user");

	Profile profile;
	Status s = profile.get(user);
	if(!(s.IsNotFound() || s.ok())){
		conn.sendStatus(MgConnection::STATUS_CODE_INTERNAL_ERROR);
		conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
		conn.printfData("{\"error\":{\"message\":\"%s\",\"code\":1,\"error_user_msg\":\"Ups hubo un error\"}}", s.ToString().c_str());
		Log(Log::LogMsgError) << "Internal server error: " << s.ToString();
		return;
	}

	conn.sendStatus(MgConnection::STATUS_CODE_OK);
	conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
	conn.printfData("%s", profile.toJson().c_str());
}

void ProfileNode::executePost(MgConnection& conn, const char*){
	string user = conn.getParameter("user");
	string loggedUser = conn.getParameter("logged_user");

	if(user != loggedUser){
		conn.sendStatus(MgConnection::STATUS_CODE_FORBIDDEN);
		conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
		conn.printfData("{\"error\":{\"message\":\"No podes modificar un perfil de otro\",\"code\":1,\"error_user_msg\":\"No podes modificar un perfil de otro usuario!\"}}");
		return;
	}

	Profile profile;
	Status s = profile.get(user);
	if(!s.ok() && !s.IsNotFound()){
		conn.sendStatus(MgConnection::STATUS_CODE_INTERNAL_ERROR);
		conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
		conn.printfData("{\"error\":{\"message\":\"%s\",\"code\":1,\"error_user_msg\":\"Ups hubo un error\"}}", s.ToString().c_str());
		Log(Log::LogMsgError) << "Internal server error: " << s.ToString();
		return;
	}

	string nickname = conn.getVarStr("nickname");
	if(nickname != "")
		profile.setNick(nickname);

	string online = conn.getVarStr("online");
	if(online != "")
		profile.setOnline(online == "true" ? true : false);

	string status = conn.getVarStr("status");
	if(status != "")
		profile.setStatus(status);

	s = profile.put();
	if(!s.ok()){
		conn.sendStatus(MgConnection::STATUS_CODE_INTERNAL_ERROR);
		conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
		conn.printfData("{\"error\":{\"message\":\"%s\",\"code\":1,\"error_user_msg\":\"No se pudo guardar el perfil\"}}", s.ToString().c_str());
		Log(Log::LogMsgError) << "Internal server error: " << s.ToString();
		return;
	}

	conn.sendStatus(MgConnection::STATUS_CODE_CREATED);
	conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
	conn.printfData("{\"success\":true}");

	Notifier::OnChangeProfile(profile);
}
