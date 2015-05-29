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

	if(s.IsNotFound()){
		Log() << "es not found!";
		conn.send_data(default_avatar_jpg, default_avatar_jpg_len);
	}else{
		conn.send_data(avatar.getData().c_str(), avatar.getData().size());
	}
}

void AvatarNode::executePost(MgConnection& conn, const char* url){
}
