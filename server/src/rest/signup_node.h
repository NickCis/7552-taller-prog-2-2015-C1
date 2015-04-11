#ifndef __SINGUP_NODE_H__
#define __SINGUP_NODE_H__

#include "../node/wa_method_node.h"

class SignupNode : public WAMethodNode {
	public:
		SignupNode();

	protected:
		void executePost(MgConnection& conn, const char* url);
		void executeGet(MgConnection& conn, const char* url);
};

#endif
