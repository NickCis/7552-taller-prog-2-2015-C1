#ifndef __WA_STR_NODE_H__
#define __WA_STR_NODE_H__

#include "wa_node.h"
#include <string>

class WAStrNode : public WANode{
	public:
		WAStrNode(const std::string &str);
		WAStrNode(const char *str);

	protected:
		bool match(const char* url);
		std::string uri;
};

#endif
