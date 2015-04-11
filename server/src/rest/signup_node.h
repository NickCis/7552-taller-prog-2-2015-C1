#ifndef __SINGUP_NODE_H__
#define __SINGUP_NODE_H__

#include "../node/wa_parent_node.h"

class SignupNode : public WAStrNode {
	public:
		SignupNode();

	protected:
		void execute(MgConnection& conn, const char* url);
};

#endif
