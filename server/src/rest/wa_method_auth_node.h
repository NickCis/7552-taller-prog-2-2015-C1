#ifndef __WA_METHOD_AUTH_NODE_H__
#define __WA_METHOD_AUTH_NODE_H__

#include "../node/wa_method_node.h"

class WAMethodAuthNode : public WAMethodNode {
	public:
		WAMethodAuthNode(const std::string &str);
		WAMethodAuthNode(const char *str);
	protected:
		void execute(MgConnection& conn, const char* url);
};

#endif
