#ifndef __MG_SERVER_H__
#define __MG_SERVER_H__

#include <string>
#include <vector>

extern "C" {
	#include <pthread.h>
}

#include "mongoose/mongoose.h"
#include "mg_connection.h"

/**
 * Clase que provee una abstraccion a mg_server, da la posibilidad de hacer un server con multiples threads.
 */
class MgServer {
	public:
		MgServer(int threads = 1);
		~MgServer();

		/** 
		 * @return NULL si todo bien, si no error leible
		 */
		const char* setPort(int port=80);

		const char* setOption(const std::string& name, const std::string& value);
		const char* setOption(const char*name, const char*value);

		void run();
		void stop();

	protected:
		static int handlerCaller(struct mg_connection*, enum mg_event);
		static void* threadHandler(void*);
		virtual int handler(MgConnection& conn, enum mg_event ev) = 0;

		std::vector<struct mg_server*> servers;
		std::vector<pthread_t> threads;

		int running;
};

#endif
