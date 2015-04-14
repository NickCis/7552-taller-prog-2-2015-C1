#ifndef __NOT_FOUND_NODE_H__
#define __NOT_FOUND_NODE_H__

#include "../node/wa_node.h"

class NotFoundNode : public WANode {
	public:
		NotFoundNode();

	protected:
		bool match(const char* url);
		void execute(MgConnection& conn, const char* url);
};

#endif
