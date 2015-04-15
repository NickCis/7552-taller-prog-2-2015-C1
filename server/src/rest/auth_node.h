#ifndef __AUTH_NODE_H__
#define __AUTH_NODE_H__

#include "../node/wa_method_node.h"

class AuthNode : public WAMethodNode {
	public:
		AuthNode();

	protected:
		void executePost(MgConnection& conn, const char* url);
		//void executeDelete(MgConnection& conn, const char* url);
};

#endif
