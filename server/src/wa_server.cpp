#include "wa_server.h"
#include "util/log.h"

WAServer::WAServer() {
}

enum mg_result WAServer::handlerRequest(MgConnection& conn){
	Log(Log::LogMsgDebug) << "[" << conn->remote_ip << "] " << conn->request_method << " " << conn->uri << " " << conn->query_string;

	conn.setResponse(MG_TRUE);
	if(root.handle(conn, conn->uri))
		return conn.getResponse();

	return MG_FALSE;
}
