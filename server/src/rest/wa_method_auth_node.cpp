#include "wa_method_auth_node.h"
#include "../db/access_token.h"
#include "../db/profile.h"

#include <string>

using std::string;

using rocksdb::Status;

WAMethodAuthNode::WAMethodAuthNode(const string &str) : WAMethodNode(str) {
}
WAMethodAuthNode::WAMethodAuthNode(const char *str) : WAMethodNode(str){
}

void WAMethodAuthNode::execute(MgConnection& conn, const char* url){
	string at = conn.getVarStr("access_token");
	AccessToken accessToken;
	Status s = accessToken.get(at);
	if(!at.size() || !s.ok()){
		conn.sendStatus(MgConnection::STATUS_CODE_UNAUTHORIZED);
		conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
		conn.printfData("{\"error\":{\"message\": \"%s\", \"code\": %d, \"error_user_msg\": \"Ups... Hubo problemas con las credenciales!.\"}}", s.ToString().c_str(), MgConnection::STATUS_CODE_UNAUTHORIZED);
		return;
	}

	conn.setParameter("logged_user", accessToken.getOwner());
	s = Profile::UpdateLastActivity(accessToken.getOwner());
	WAMethodNode::execute(conn, url);
}
