#include "wa_method_auth_node.h"
#include "../db/access_token.h"

#include <string>

using std::string;

WAMethodAuthNode::WAMethodAuthNode(const string &str) : WAMethodNode(str) {
}
WAMethodAuthNode::WAMethodAuthNode(const char *str) : WAMethodNode(str){
}

void WAMethodAuthNode::execute(MgConnection& conn, const char* url){
	string at = conn.getVarStr("access_token");
	AccessToken accessToken;
	if(at.size() && !(AccessToken::Get(at, accessToken).ok())){
		conn.sendStatus(MgConnection::STATUS_CODE_UNAUTHORIZED);
		conn.sendContentType(MgConnection::CONTENT_TYPE_JSON);
		conn.printfData("{ \"message\": \"no hay access token o es invalido\", \"code\": %d, \"error_user_msg\": \"Ups... Hubo problemas con las credenciales!.\" }", MgConnection::STATUS_CODE_UNAUTHORIZED);
		return;
	}

	conn.setParameter("logged_user", accessToken.getUsername());
	WAMethodNode::execute(conn, url);
}
