#include "avatar_node.h"
#include <string>
#include <cstdlib>

#include "../db/avatar.h"
#include "../db/default_avatar.h"
#include "../util/log.h"


using std::string;

using rocksdb::Status;

AvatarNode::AvatarNode() : WAMethodAuthNode("avatar") {
}

void AvatarNode::executeGet(MgConnection& conn, const char* url){
	string user = conn.getParameter("user");

	Avatar avatar;
	Status s = avatar.get(user);
	if(!(s.IsNotFound() || s.ok())){
		conn.sendStatus(MgConnection::STATUS_CODE_INTERNAL_ERROR);
		conn.sendContentType(MgConnection::CONTENT_TYPE_JPG);
		return;
	}

	conn.sendStatus(MgConnection::STATUS_CODE_OK);
	conn.sendContentType(MgConnection::CONTENT_TYPE_JPG);

	if(s.IsNotFound())
		conn.send_data(default_avatar_jpg, default_avatar_jpg_len);
	else
		conn.send_data(avatar.getData().c_str(), avatar.getData().size());
}

void AvatarNode::executePost(MgConnection& conn, const char* url){
	string user = conn.getParameter("user");
	string loggedUser = conn.getParameter("logged_user");

	if(user != loggedUser){
		conn.sendStatus(MgConnection::STATUS_CODE_FORBIDDEN);
		conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
		conn.printfData("{\"error\":{\"message\":\"No podes modificar un avatar de otro\",\"code\":1,\"error_user_msg\":\"No podes modificar un avatar de otro usuario!\"}}");
		return;
	}

	string var_name;
	string file_name;
	string photo;

	while((photo = conn.getMultipartData(var_name, file_name)) != ""){
		if(var_name == "avatar")
			break;
	}

	if(photo.size() <= 0){
		conn.sendStatus(MgConnection::STATUS_CODE_BAD_REQUEST);
		conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
		conn.printfData("{\"error\":{\"message\":\"No se incluyo el parametro avatar\",\"code\":1,\"error_user_msg\":\"No mandaste la foto!\"}}");
		return;
	}

	Avatar avatar;
	avatar.setOwner(loggedUser);
	avatar.setData(photo);

	Status s = avatar.put();
	if(!s.ok()){
		conn.sendStatus(MgConnection::STATUS_CODE_INTERNAL_ERROR);
		conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
		conn.printfData("{\"error\":{\"message\":\"%s\",\"code\":1,\"error_user_msg\":\"No se pudo guardar el avatar\"}}", s.ToString().c_str());
		Log(Log::LogMsgError) << "Internal server error: " << s.ToString();
		return;
	}

	conn.sendStatus(MgConnection::STATUS_CODE_CREATED);
	conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
	conn.printfData("{\"success\":true}");
}
