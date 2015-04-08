#ifndef __WA_NODE_H__
#define  __WA_NODE_H__

#include "../mongoose/mongoose.h"

extern "C" {
	#include <string.h>
}

#define WA_COMPARE_AND_SUM(url, str) if(strncmp(url, str, sizeof(str)-1) == 0) { url+=sizeof(str)-1; return true; }

class WANode {
	public:
		virtual ~WANode();
		bool handle(struct mg_connection* conn, const char* url);

	protected:
		virtual bool match(struct mg_connection* conn, const char*& url) = 0;
		virtual void execute(struct mg_connection* conn, const char* url) = 0;
};


#endif
