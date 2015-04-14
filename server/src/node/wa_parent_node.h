#ifndef __WA_PARENT_NODE_H__
#define __WA_PARENT_NODE_H__

#include "wa_str_node.h"
#include <vector>

class WAParentNode : public WAStrNode {
	public:

		WAParentNode(const std::string &str);
		WAParentNode(const char *str);
		~WAParentNode();

	protected:
		void execute(MgConnection& conn, const char* url);
		virtual void preExecute(MgConnection& conn, const char*& url);
		std::vector<WANode*> children;
};

#endif
