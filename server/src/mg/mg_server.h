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
		/** Constructor
		 * @param threads: cantidad de threads a utilizar
		 */
		MgServer(int threads = 1);
		~MgServer();

		/** 
		 * @return NULL si todo bien, si no error leible
		 */
		const char* setPort(int port=80);

		/** Setea opciones, abstraccion de mg_set_option
		 */
		const char* setOption(const std::string& name, const std::string& value);
		const char* setOption(const char*name, const char*value);

		/** Arranca servidor
		 */
		void run();

		/** Para servidor
		 */
		void stop();

	protected:
		/** Handler que usa el mg_create_server para despachar las conecciones
		 */
		static int handlerCaller(struct mg_connection*, enum mg_event);

		/** Funcion de thread
		 */
		static void* threadHandler(void*);

		/** Hanlder que maneja la coneccion
		 */
		virtual int handler(MgConnection& conn, enum mg_event ev) = 0;

		std::vector<struct mg_server*> servers; ///< Vector de servers utilizados (uno por cada thread)
		std::vector<pthread_t> threads; ///< vector de threads corriendo

		int running;
};

#endif
