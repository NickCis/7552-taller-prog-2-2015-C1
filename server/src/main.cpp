extern "C" {
	#include <signal.h>
	#include <getopt.h>
	#include <unistd.h>
}

#include <iostream>
#include <string>
#include <fstream>

#include "server_config.h"
#include "wa_server.h"

#include "db/db_manager.h"
#include "util/log.h"

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
				std::cout << "Argumento desconocido '" << optarg << "'" << std::endl;
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

	Log::setLogLevel(config.logLevel);

	std::ofstream outputLog;
	if(config.logFile != "-"){
		outputLog.open(config.logFile);
		if(outputLog.is_open())
			Log::setOutput(outputLog);
		else
			Log(Log::LogMsgError) << "Error abriendo archivo '" << config.logFile << "'";
	}

	rocksdb::Status s;
	DBManager dbm(config.dbPath, s);
	if(!s.ok()){
		Log(Log::LogMsgError) << "Error iniciando base de datos: " << s.ToString();
		return -1;
	}
	dbm.setEnviroment();

	Log(Log::LogMsgInfo) << "Version: " << SERVER_VERSION_MAJOR << "." << SERVER_VERSION_MINOR;
	//cout << "Mongoose: " << MONGOOSE_VERSION << endl;

	Log(Log::LogMsgInfo) << "Configuracion:";
	Log(Log::LogMsgInfo) << "\t* port: " << config.port;
	Log(Log::LogMsgInfo) << "\t* threads: " << config.threads;
	Log(Log::LogMsgInfo) << "\t* logFile: " << config.logFile;
	Log(Log::LogMsgInfo) << "\t* logLevel: " << config.logLevel;
	Log(Log::LogMsgInfo) << "\t* dbPath: " << config.dbPath;

	WAServer server(config.threads);
	server.setPort(config.port);

	server.run();

	while(running)
		sleep(1);

	server.stop();

	Log(Log::LogMsgInfo) << "Saliendo";

	return 0;
}
