#ifndef __MG_SERVER_H__
#define __MG_SERVER_H__

#include <string>
#include <vector>
#include <functional>

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
		 */
		MgServer();
		~MgServer();

		/** Setea la configuracion de escucha
		 * @param port: puerto
		 * @param path al certificado ssl
		 * @return NULL si todo bien, si no error leible
		 */
		const char* setListeningConfig(int port=80, const std::string& ssl=std::string(""));

		/** Setea opciones, abstraccion de mg_set_option
		 */
		const char* setOption(const std::string& name, const std::string& value);
		const char* setOption(const char*name, const char*value);

		/** Setear la cantidad de threads a correr
		 */
		void setThreadNumber(size_t);

		/** Arranca servidor.
		 * Es no bloqueante!
		 */
		void run();

		/** Para servidor
		 */
		void stop();

		void doConnection(std::function<bool(MgConnection&)>, std::function<void(MgConnection&)>);

	protected:
		struct mg_server* createInstance();
		/** Handler que usa el mg_create_server para despachar las conecciones
		 */
		static int handlerCaller(struct mg_connection*, enum mg_event);

		/** Funcion de thread
		 */
		static void* threadHandler(void*);

		/** Hanlder que maneja la coneccion
		 */
		virtual enum mg_result handler(MgConnection* conn, enum mg_event ev);
		/** Handler para evento MG_AUTH
		 */
		virtual enum mg_result handlerAuth(MgConnection& conn);
		/** Handler para evento MG_REQUEST
		 */
		virtual enum mg_result handlerRequest(MgConnection& conn) = 0;
		/** Handler para evento MG_CLOSE
		 */
		virtual enum mg_result handlerClose(MgConnection* conn);

		std::vector<struct mg_server*> servers; ///< Vector de servers utilizados (uno por cada thread)
		std::vector<pthread_t> threads; ///< vector de threads corriendo

		int running;
		size_t threadsNumber;
};

#endif
