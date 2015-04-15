#include "auth_node.h"

#include <iostream>
#include <string>

#include "../db/user.h"
#include "../db/access_token.h"

using std::cout;
using std::endl;
using std::string;

using rocksdb::Status;

AuthNode::AuthNode() : WAMethodNode("auth") {
}

void AuthNode::executePost(MgConnection& conn, const char* url){
	string u = conn.getVarStr("user");
	string p = conn.getVarStr("pass");
	User user;
	Status s = User::Get(u, user);

	conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);

	if(s.ok()){
		if(p == user.getPassword()){
			AccessToken at;
			Status s = AccessToken::Put(u, at);
			if(s.ok()){
				conn.sendStatus(MgConnection::STATUS_CODE_CREATED);
				conn.printfData("{ \"access_token\": \"%s\" }", at.getToken().c_str());
			}
			return;
		}
	}

	conn.sendStatus(MgConnection::STATUS_CODE_BAD_REQUEST);
	conn.printfData("{ \"message\": \"%s\",  \"error_user_msg\": \"Problemas con el logeo\"}", s.ToString().c_str());
}
