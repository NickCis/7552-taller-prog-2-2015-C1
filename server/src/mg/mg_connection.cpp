#include "mg_connection.h"

extern "C" {
	#include <stdarg.h>
}

#include <cstdlib>
#include <cstring>

using std::atoi;
using std::string;
using std::strlen;

static const char* CONTENT_TYPES[] = {
	"application/json", // CONTENT_TYPE_JSON
	"text/html", // CONTENT_TYPE_HTML
	"image/jpg", // CONTENT_TYPE_JPG
	"text/event-stream" // CONTENT_TYPE_JPG
};

#include <iostream>
MgConnection::MgConnection(struct mg_connection *c) :
	conn(c),
	multipartOffset(0),
	response(MG_TRUE)
{
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

size_t MgConnection::send_data(const void* buf, size_t len){
	return mg_send_data(this->conn, buf, len);
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

const std::string& MgConnection::getParameter(const string& key){
	return this->parameters[key];
}

void MgConnection::setParameter(const std::string& key, const string& value){
	this->parameters[key] = value;
}

void MgConnection::setResponse(enum mg_result r){
	this->response = r;
}

enum mg_result MgConnection::getResponse(){
	return this->response;
}

const char* MgConnection::getHeader(const string& name){
	return this->getHeader(name.c_str());
}

const char* MgConnection::getHeader(const char* name){
	return mg_get_header(this->conn, name);
}

string MgConnection::getVarStr(const char* varName, int n, size_t max){
	string value;
	value.resize(max);
	// XXX: fixme es feo esto!,, jaja
	switch(mg_get_var_n(this->conn, varName, (char*) value.data(), max, n)){
		case -2:
			return this->getVarStr(varName, max+max);
			break;

		case -1:
			return string();
			break;

		default:
			break;
	}

	value.resize(strlen(value.data()));

	return value;
}

string MgConnection::getVarStr(const string& varName, int n, size_t max){
	return this->getVarStr(varName.c_str(), n, max);
}


int MgConnection::getVarInt(const char* varName, int n, size_t max){
	return atoi(this->getVarStr(varName, n, max).c_str());
}

int MgConnection::getVarInt(const string& varName, int n, size_t max){
	return this->getVarInt(varName.c_str(), n, max);
}

double MgConnection::getVarDouble(const char* varName, int n, size_t max){
	return atof(this->getVarStr(varName, n, max).c_str());
}

double MgConnection::getVarDouble(const string& varName, int n, size_t max){
	return this->getVarDouble(varName.c_str(), n, max);
}

std::string MgConnection::getMultipartData(string& var_name, string& file_name){
	const char *data = NULL;
	int data_len = 0;

	var_name.resize(100);
	file_name.resize(100);

	var_name[0] = 0;
	file_name[0] = 0;

	this->multipartOffset = mg_parse_multipart(
		this->conn->content + this->multipartOffset,
		this->conn->content_len - this->multipartOffset,
		(char*) var_name.data(), 100,
		(char*) file_name.data(), 100,
		&data, &data_len
	);

	var_name.resize(strlen(var_name.data()));
	file_name.resize(strlen(file_name.data()));

	return string(data, data_len);
}
