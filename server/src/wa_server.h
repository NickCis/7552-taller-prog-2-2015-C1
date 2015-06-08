#ifndef __WA_SERVER_H__
#define __WA_SERVER_H__

#include "mg/mg_server.h"
#include "rest/root_node.h"

class WAServer : public MgServer {
	public:
		WAServer();

	protected:
		RootNode root;
		enum mg_result handlerRequest(MgConnection& conn);
};
#endif
