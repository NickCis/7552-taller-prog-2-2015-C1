#ifndef __USER_NODE_H__
#define __USER_NODE_H__

#include "../node/wa_parent_node.h"

class UserNode : public WAParentNode {
	public:
		UserNode();

	protected:
		void preExecute(MgConnection& conn, const char*& url);
};

#endif
