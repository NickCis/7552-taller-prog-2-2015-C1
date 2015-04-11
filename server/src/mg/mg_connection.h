#ifndef __MG_CONECTION_H__
#define __MG_CONECTION_H__

#include "mongoose/mongoose.h"

#include <string>

class MgConnection {
	public:
		MgConnection(struct mg_connection *conn);
		typedef enum StatusCodes {
			// 2xx Success
			STATUS_CODE_OK=200,
			STATUS_CODE_CREATED=201,

			// 4xx Client Error
			STATUS_CODE_BAD_REQUEST=400,
			STATUS_CODE_UNAUTHORIZED=401,
			STATUS_CODE_FORBIDDEN=403,
			STATUS_CODE_NOT_FOUND=404,
			STATUS_CODE_METHOD_NOT_ALLOWED=405
		} StatusCodes;

		typedef enum ContentTypes {
			CONTENT_TYPE_JSON=0,
			CONTENT_TYPE_HTML,
			CONTENT_TYPE_TOTAL
		} ContentTypes;

		void sendStatus(MgConnection::StatusCodes code);
		void sendStatus(int code);

		void sendHeader(const std::string& name, const std::string& val);
		void sendHeader(const char* name, const char* val);

		void sendContentType(const std::string& type);
		void sendContentType(const char* type);
		void sendContentType(MgConnection::ContentTypes type);

		size_t printfData(const char* format, ...);

		struct mg_connection* operator->();

	protected:
		struct mg_connection *conn;
};

#endif
