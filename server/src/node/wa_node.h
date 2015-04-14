#ifndef __WA_NODE_H__
#define __WA_NODE_H__

#include "../mg/mg_connection.h"

class WANode {
	public:
		virtual ~WANode();
		bool handle(MgConnection& conn, const char* url);

	protected:
		virtual bool match(const char* url) = 0;
		virtual void execute(MgConnection& conn, const char* url) = 0;
};


#endif
