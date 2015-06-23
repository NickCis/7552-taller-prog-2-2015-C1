#include "signup_node.h"

#include <iostream>
#include <string>

#include "../db/user.h"

using std::cout;
using std::endl;
using std::string;

using rocksdb::Status;

SignupNode::SignupNode() : WAMethodNode("signup") {
}

void SignupNode::executePost(MgConnection& conn, const char*){
	User u;
	u.setUsername(conn.getVarStr("user"));
	u.setPassword(conn.getVarStr("pass"));
	Status s = u.put();

	if(s.ok()){
		conn.sendStatus(MgConnection::STATUS_CODE_CREATED);
		conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
		conn.printfData("{ \"success\": true }");
	}else{
		conn.sendStatus(MgConnection::STATUS_CODE_BAD_REQUEST);
		conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
		conn.printfData("{\"error\":{\"message\":\"%s\",\"error_user_msg\":\"Problemas registrandose\", code:1}}", s.ToString().c_str());
	}
}
