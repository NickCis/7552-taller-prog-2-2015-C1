extern "C" {
	#include <signal.h>
}

#include <iostream>
#include <unistd.h>

#include "server_config.h"
#include "wa_server.h"

#include "db/db_manager.h"

using std::cout;
using std::endl;

sig_atomic_t running = 1;

void sig_handler(int sig)
{
	running = 0;
}

int main(int, char**){
	// Registro se~nales para cerrar de manera linda
	signal(SIGHUP, sig_handler);
	signal(SIGQUIT, sig_handler);
	signal(SIGKILL, sig_handler);
	signal(SIGINT, sig_handler);

	rocksdb::Status s;
	DBManager dbm("/tmp/test_db", s);
	if(!s.ok()){
		cout << "error creando db,, mefui" << endl;
		return -1;
	}
	dbm.setEnviroment();

	cout << "Version: " << SERVER_VERSION_MAJOR << "." << SERVER_VERSION_MINOR << endl;
	//cout << "Mongoose: " << MONGOOSE_VERSION << endl;

	cout << "Run!" << endl;

	WAServer server(2);
	server.setPort(8000);

	server.run();

	while(running)
		sleep(1);

	server.stop();

	return 0;
}
