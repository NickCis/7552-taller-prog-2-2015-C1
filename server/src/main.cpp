extern "C" {
	#include <signal.h>
	#include <getopt.h>
	#include <unistd.h>
}

#include <iostream>
#include <string>

#include "server_config.h"
#include "wa_server.h"

#include "db/db_manager.h"

using std::cout;
using std::endl;
using std::string;

sig_atomic_t running = 1;

void sig_handler(int){
	running = 0;
}

typedef struct Configuration {
	Configuration() :
		port(8000),
		threads(1),
		logFile("-"),
		logLevel("debug"),
		dbPath("/tmp/test_db")
	{}

	int port;
	int threads;
	string logFile;
	string logLevel;
	string dbPath;
} Configuration;

void usage(char *name){
	Configuration config;
	cout << "Usage: " << name << " [OPTION]\n"
			<< "  -p, --port=puerto\t\tEspecificar el puerto a utilizar [default: " << config.port << "]\n"
			<< "  -l, --log-file=file\t\tEspecificar path de archivo de log '-' para stdout [default: " << config.logFile << "]\n"
			<< "  -L, --log-level=level\t\tEspecificar nivel de log (error, warn, info, debug) [default: " << config.logLevel << "]\n"
			<< "  -t, --threads=num\t\tCantidad de threads [default: " << config.threads << "]\n"
			<< "  -d, --db-path=level\t\tPath de la base de datos [default: " << config.dbPath << "]\n";
}

int parse_cmd(int argc, char *argv[], Configuration& config){
	opterr = 0;
	int c;

	static struct option long_options[] = {
		{"port", required_argument, 0, 'p'},
		{"log-file", required_argument, 0, 'l'},
		{"log-level", required_argument, 0, 'L'},
		{"help", no_argument, 0, 'h'},
		{0, 0, 0, 0}
	};

	int option_index = 0;

	while((c = getopt_long (argc, argv, "hp:l:L:", long_options, &option_index))){
		if (c == -1)
			break;

		switch (c){
			case 'h':
				usage(argv[0]);
				return -1;
				break;

			case 'l':
				config.logFile = optarg;
				break;

			case 'L':
				config.logLevel = optarg;
				break;

			case 't':
				config.threads = atoi(optarg);
				break;

			case 'd':
				config.dbPath = optarg;
				break;

			case 'p':
				config.port = atoi(optarg);
				break;

			case '?':
			default:
				printf("Argumento desconocido '%s'\n", optarg);
				return -1;
				break;
		}
	}

	return 0;
}

int main(int argc, char* argv[]){
	Configuration config;
	if(parse_cmd(argc, argv, config))
		return -1;

	// Registro se~nales para cerrar de manera linda
	signal(SIGHUP, sig_handler);
	signal(SIGQUIT, sig_handler);
	signal(SIGKILL, sig_handler);
	signal(SIGINT, sig_handler);

	rocksdb::Status s;
	DBManager dbm(config.dbPath, s);
	if(!s.ok()){
		cout << "error creando db,, mefui :: " << s.ToString() << endl;
		return -1;
	}
	dbm.setEnviroment();

	cout << "Version: " << SERVER_VERSION_MAJOR << "." << SERVER_VERSION_MINOR << endl;
	//cout << "Mongoose: " << MONGOOSE_VERSION << endl;

	cout << "Run!" << endl;

	WAServer server(config.threads);
	server.setPort(config.port);

	server.run();

	while(running)
		sleep(1);

	server.stop();

	return 0;
}
