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

void SignupNode::executePost(MgConnection& conn, const char* url){
	string u = conn.getVarStr("user");
	string p = conn.getVarStr("pass");
	Status s = User::Put(u, p);

	conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);

	if(s.ok()){
		conn.sendStatus(MgConnection::STATUS_CODE_CREATED);
		conn.printfData("{ \"success\": true }");
	}else{
		conn.sendStatus(MgConnection::STATUS_CODE_BAD_REQUEST);
		conn.printfData("{ \"message\": \"%s\",  \"error_user_msg\": \"Problemas registrandose\"}", s.ToString().c_str());
	}
}
