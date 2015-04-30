#include "wa_server.h"
#include "util/log.h"

WAServer::WAServer(int threads) : MgServer(threads) {
}

int WAServer::handler(MgConnection& conn, enum mg_event ev){
	switch (ev) {
		case MG_AUTH:
			return MG_TRUE;

		case MG_REQUEST:
			Log(Log::LogMsgDebug) << "[" << conn->remote_ip << "] " << conn->request_method << " " << conn->uri << " " << conn->query_string;

			if(root.handle(conn, conn->uri))
				return MG_TRUE;
			return MG_FALSE;
			break;

		default:
			return MG_FALSE;
			break;
	}
}
