#include "mg_server.h"

using std::string;
using std::to_string;

MgServer::MgServer() :
	running(0),
	threadsNumber(1)
{
	this->servers.push_back(this->createInstance());
}

MgServer::~MgServer(){
	if(this->running)
		this->stop();
}

const char* MgServer::setListeningConfig(int port, const string& ssl){
	string port_ssl;
	if(ssl.size())
		port_ssl = string("ssl://")+to_string(port)+string(":")+ssl;
	else
		port_ssl = to_string(port);

	return this->setOption("listening_port", port_ssl.c_str());
}

const char* MgServer::setOption(const string& name, const string& value){
	return this->setOption(name.c_str(), value.c_str());
}

const char* MgServer::setOption(const char*name, const char*value){
	if(this->running)
		return "server is already running";

	return mg_set_option(this->servers[0], name, value);
}

void MgServer::run(){
	this->running = 1;

	pthread_t t;
	for(size_t i=0; i < this->threadsNumber; i++){
		if(this->servers.size() < i+1){
			struct mg_server* sv = this->createInstance();
			mg_copy_listeners(this->servers[0], sv);
			this->servers.push_back(sv);
		}
		pthread_create(&t, NULL, MgServer::threadHandler, this->servers[i]);
		this->threads.push_back(t);
	}
}

void MgServer::stop(){
	this->running = 0;

	for(auto it=this->threads.begin(); it != this->threads.end(); it++)
		pthread_join((*it), NULL);

	for(auto it=this->servers.begin(); it != this->servers.end(); it++)
		mg_destroy_server(&(*it));
}

int MgServer::handlerCaller(struct mg_connection *conn, enum mg_event ev){
	MgServer* mgServer = (MgServer*) conn->server_param;

	if(conn->connection_param == NULL)
		conn->connection_param = (void*) new MgConnection(conn);

	return mgServer->handler((MgConnection*) conn->connection_param, ev);
}

enum mg_result MgServer::handler(MgConnection* conn, enum mg_event ev){
	switch(ev){
		case MG_AUTH:
			return this->handlerAuth(*conn);
		case MG_REQUEST:
			return this->handlerRequest(*conn);
		case MG_CLOSE:
			return this->handlerClose(conn);

		default:
			return MG_FALSE;
	}
}

enum mg_result MgServer::handlerAuth(MgConnection&){
	return MG_TRUE;
}

enum mg_result MgServer::handlerClose(MgConnection* conn){
	if(conn){
		(*conn)->connection_param = NULL;
		delete conn;
	}

	return MG_FALSE;
}

void* MgServer::threadHandler(void* arg){
	struct mg_server * server = (struct mg_server*) arg;
	MgServer* mgServer = (MgServer*) mg_get_server_param(server);

	while(mgServer->running)
		mg_poll_server(server, 1000);

	return NULL;
}

struct mg_server* MgServer::createInstance(){
	return mg_create_server((void *) this, MgServer::handlerCaller);
}

void MgServer::setThreadNumber(size_t t){
	if(!this->running && t > 1)
		this->threadsNumber = t;
}

void MgServer::doConnection(std::function<bool(MgConnection&)> check, std::function<void(MgConnection&)> cb){
	for(auto it=servers.begin(); it != servers.end(); it++){
		for(struct mg_connection *conn = NULL; (conn = mg_next(*it, conn)) != NULL;){
			if(check(*((MgConnection*) conn->connection_param)))
				cb(*((MgConnection*) conn->connection_param));
		}
	}
}
