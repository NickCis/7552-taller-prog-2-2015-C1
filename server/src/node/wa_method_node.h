#ifndef __WA_METHOD_NODE_H__
#define __WA_METHOD_NODE_H__

#include "wa_str_node.h"
#include <string>

class WAMethodNode : public WAStrNode {
	public:
		WAMethodNode(const std::string &str);
		WAMethodNode(const char *str);

	protected:
		void execute(MgConnection& conn, const char* url);
		virtual void executePost(MgConnection& conn, const char* url);
		virtual void executeGet(MgConnection& conn, const char* url);
		virtual void executeDelete(MgConnection& conn, const char* url);
		virtual void methodNotAllowed(MgConnection& conn, const char* url);
};

#endif
