#include "mg_connection.h"

extern "C" {
	#include <stdarg.h>
}

using std::string;

static const char* CONTENT_TYPES[] = {
	"application/json", // CONTENT_TYPE_JSON
	"text/html" // CONTENT_TYPE_HTML
};

MgConnection::MgConnection(struct mg_connection *c) : conn(c) {

}

void MgConnection::sendStatus(MgConnection::StatusCodes code){
	this->sendStatus( (int) code);
}

void MgConnection::sendStatus(int code){
	mg_send_status(this->conn, code);
}

void MgConnection::sendHeader(const string& name, const string& val){
	this->sendHeader(name.c_str(), val.c_str());
}

void MgConnection::sendHeader(const char* name, const char* val){
	mg_send_header(this->conn, name, val);
}

size_t MgConnection::printfData(const char* fmt, ...){
	va_list ap;
	va_start(ap, fmt);
	size_t ret = mg_vprintf_data(this->conn, fmt, ap);
	va_end(ap);
	return ret;
}

struct mg_connection* MgConnection::operator->(){
	return this->conn;
}

void MgConnection::sendContentType(const std::string& type){
	this->sendContentType(type.c_str());
}

void MgConnection::sendContentType(const char* type){
	this->sendHeader("Content-Type", type);
}

void MgConnection::sendContentType(MgConnection::ContentTypes type){
	if(type >= MgConnection::CONTENT_TYPE_TOTAL)
		return;
	this->sendContentType(CONTENT_TYPES[type]);
}
