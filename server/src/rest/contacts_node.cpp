#include "contacts_node.h"
#include <string>
#include <cstdlib>

#include "../db/contact_list.h"
#include "../db/user.h"

using std::string;

using rocksdb::Status;

ContactsNode::ContactsNode() : WAMethodAuthNode("contacts") {
}

void ContactsNode::executeGet(MgConnection& conn, const char* url){
	ContactList cl;
	if(! cl.get(conn.getParameter("logged_user")).ok()){
		/*conn.sendStatus(MgConnection::STATUS_CODE_INTERNAL_ERROR);
		conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
		conn.printfData("{\"error\":{\"message\":\"Error interno\",\"code\":1,\"error_user_msg\":\"Ups Hubo problemas\"}}");*/

		conn.sendStatus(MgConnection::STATUS_CODE_OK);
		conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
		conn.printfData("{\"contacts\":[]}");
		return;
	}

	conn.sendStatus(MgConnection::STATUS_CODE_OK);
	conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);

	conn.printfData("%s", cl.toJson().c_str());
}

void ContactsNode::executeDelete(MgConnection& conn, const char* url){
	string loggedUser = conn.getParameter("logged_user");
	ContactList contactList;
	contactList.setOwner(loggedUser);
	string user;

	string success;
	string error;
	bool firstSuccess = true;
	bool firstError = true;

	User u;

	for(int n=0; (user = conn.getVarStr("users[]", n)).length(); n++){
		if(User::Get(user, u).ok() && contactList.erase(user).ok()){
			if(!firstSuccess)
				success += ",";
			else
				firstSuccess = false;
			success += string("\"")+user+string("\"");
		}else{
			if(!firstError)
				error += ",";
			else
				firstError = false;
			error += string("\"")+user+string("\"");
		}
	}

	conn.sendStatus(MgConnection::STATUS_CODE_OK);
	conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
	conn.printfData("{\"success\":[%s],\"error\":[%s]}", success.c_str(), error.c_str());
}

void ContactsNode::executePost(MgConnection& conn, const char* url){
	string loggedUser = conn.getParameter("logged_user");
	ContactList contactList;
	contactList.setOwner(loggedUser);
	string user;

	string success;
	string error;
	bool firstSuccess = true;
	bool firstError = true;

	User u;

	for(int n=0; (user = conn.getVarStr("users[]", n)).length(); n++){
		if(User::Get(user, u).ok()){
		}
		if(User::Get(user, u).ok() && contactList.push_back(user).ok()){
			if(!firstSuccess)
				success += ",";
			else
				firstSuccess = false;
			success += string("\"")+user+string("\"");
		}else{
			if(!firstError)
				error += ",";
			else
				firstError = false;
			error += string("\"")+user+string("\"");
		}
	}

	conn.sendStatus(MgConnection::STATUS_CODE_CREATED);
	conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
	conn.printfData("{\"success\":[%s],\"error\":[%s]}", success.c_str(), error.c_str());
}
