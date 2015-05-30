#include "user_node.h"
#include "messages_node.h"
#include "avatar_node.h"
#include "profile_node.h"
#include "not_found_node.h"

#include <string>
#include <iostream>

using std::string;

UserNode::UserNode() : WAParentNode("user/") {
	children.push_back(new MessagesNode());
	children.push_back(new AvatarNode());
	children.push_back(new ProfileNode());
	children.push_back(new NotFoundNode());
}

void UserNode::preExecute(MgConnection& conn, const char*& url){
	WAParentNode::preExecute(conn, url);
	string user;

	for(char c; (c = *url) != '/' && c != 0; url++)
		user.insert(user.end(), c);

	if(*url == '/')
		url++;

	conn.setParameter("user", user);
}
