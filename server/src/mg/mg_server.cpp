#include "mg_server.h"

using std::string;
using std::to_string;

MgServer::MgServer(int threads) : running(0) {
	struct mg_server *server;

	for(int i=0; i < threads; i++){
		server = mg_create_server((void *) this, MgServer::handlerCaller);
		this->servers.push_back(server);
	}
}

MgServer::~MgServer(){
	if(this->running)
		this->stop();
}

const char* MgServer::setPort(int port){
	auto it = this->servers.begin();
	const char *ret;

	if(it == this->servers.end())
		return NULL;


	if((ret = mg_set_option((*it), "listening_port", to_string(port).c_str())))
		return ret;

	it++;
	for(;it != this->servers.end(); it++){
		mg_copy_listeners(this->servers[0], (*it));
	}

	return NULL;
}

const char* MgServer::setOption(const string& name, const string& value){
	return this->setOption(name.c_str(), value.c_str());
}

const char* MgServer::setOption(const char*name, const char*value){
	const char *ret ;
	for(auto it=this->servers.begin(); it != this->servers.end(); it++){
		if((ret = mg_set_option((*it), name, value)) != NULL)
			return ret;
	}

	return NULL;
}

void MgServer::run(){
	this->running = 1;

	pthread_t t;
	for(auto it=this->servers.begin(); it != this->servers.end(); it++){
		pthread_create(&t, NULL, MgServer::threadHandler, *it);
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
	MgConnection mgConnection(conn);
	return mgServer->handler(mgConnection, ev);
}

void* MgServer::threadHandler(void* arg){
	struct mg_server * server = (struct mg_server*) arg;
	MgServer* mgServer = (MgServer*) mg_get_server_param(server);

	while(mgServer->running)
		mg_poll_server(server, 1000);

	return NULL;
}
