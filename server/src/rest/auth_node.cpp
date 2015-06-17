#include "auth_node.h"

#include <string>

#include "../db/user.h"
#include "../db/access_token.h"

using std::string;

using rocksdb::Status;

AuthNode::AuthNode() : WAMethodNode("auth") {
}

void AuthNode::executePost(MgConnection& conn, const char*){
	string u = conn.getVarStr("user");
	string p = conn.getVarStr("pass");
	User user;
	Status s = user.get(u);

	if(s.ok()){
		if(user.isPassword(p)){
			AccessToken at;
			at.setOwner(u);
			s = at.put();
			if(s.ok()){
				conn.sendStatus(MgConnection::STATUS_CODE_CREATED);
				conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
				conn.printfData("{ \"access_token\": \"%s\" }", at.getToken().c_str());
				return;
			}
		}
	}

	conn.sendStatus(MgConnection::STATUS_CODE_BAD_REQUEST);
	conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
	conn.printfData("{\"error\":{\"message\":\"%s\",\"error_user_msg\":\"Problemas con el logeo\",\"code\":400}}", s.ToString().c_str());
}
