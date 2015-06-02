#include "user_node.h"
#include "messages_node.h"
#include "avatar_node.h"
#include "profile_node.h"
#include "checkin_node.h"
#include "not_found_node.h"
#include "../db/user.h"

#include <string>
#include <iostream>

using std::string;

UserNode::UserNode() : WAParentNode("user/") {
	children.push_back(new MessagesNode());
	children.push_back(new AvatarNode());
	children.push_back(new ProfileNode());
	children.push_back(new CheckinNode());
	children.push_back(new NotFoundNode());
}

bool UserNode::preExecute(MgConnection& conn, const char*& url){
	WAParentNode::preExecute(conn, url);
	string user;

	for(char c; (c = *url) != '/' && c != 0; url++)
		user.insert(user.end(), c);

	if(*url == '/')
		url++;

	conn.setParameter("user", user);
	User u;
	if(u.get(user).ok())
		return true;

	conn.sendStatus(MgConnection::STATUS_CODE_NOT_FOUND);
	conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
	conn.printfData("{\"error\":{ \"message\": \"'%s' no encontrado\", \"code\": 404, \"error_user_msg\": \"Ups... No se encontro!\" }}", conn->uri);
	return false;
}
