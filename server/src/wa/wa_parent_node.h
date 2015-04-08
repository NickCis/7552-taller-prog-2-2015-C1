#ifndef __WA_PARENT_NODE_H__
#define  __WA_PARENT_NODE_H__

#include "wa_node.h"
#include <vector>

class WAParentNode : public WANode {
	public:
		~WAParentNode();

	protected:
		bool match(struct mg_connection* conn, const char*& url) = 0;
		void execute(struct mg_connection* conn, const char* url);
		std::vector<WANode*> children;
};

#endif
