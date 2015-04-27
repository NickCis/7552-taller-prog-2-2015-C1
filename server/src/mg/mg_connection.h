#ifndef __MG_CONECTION_H__
#define __MG_CONECTION_H__

#include "mongoose/mongoose.h"

#include <map>
#include <string>

/** Clase que representa una coneccion, abstrae struct mg_connection de mongoose
 */
class MgConnection {
	public:
		/** Constructor
		 * @param coneccion
		 */
		MgConnection(struct mg_connection *conn);

		/** Enum que representa los codigos de estado de html
		 */
		typedef enum StatusCodes {
			// 2xx Success
			STATUS_CODE_OK=200, ///< Todo bien
			STATUS_CODE_CREATED=201, ///< Se creo algo

			// 4xx Client Error
			STATUS_CODE_BAD_REQUEST=400, ///< El cliente no paso toda la informacion, request malo
			STATUS_CODE_UNAUTHORIZED=401, ///< Cliente no esta autorizado para lo que quiere hacer
			STATUS_CODE_FORBIDDEN=403, ///< El cliente tiene prohibido hacer eso (no es un tema de permisos)
			STATUS_CODE_NOT_FOUND=404, ///< No se encuentro
			STATUS_CODE_METHOD_NOT_ALLOWED=405, ///< Se pidio un GET/POST/DELETE y no se implementa ese metodo

			// 5xx Errores de Servidor
			STATUS_CODE_INTERNAL_ERROR=500 ///< Error del servidor

		} StatusCodes;

		/** Enum que representa los content types que puede tener la respuesta
		 */
		typedef enum ContentTypes {
			CONTENT_TYPE_JSON=0, ///< Se envia un JSON
			CONTENT_TYPE_HTML, ///< Se envia un HTML
			CONTENT_TYPE_TOTAL
		} ContentTypes;

		/** Envia el codigo de estado
		 * @param codigo (enum)
		 */
		void sendStatus(MgConnection::StatusCodes code);

		/** Envia el codigo de estado
		 * @param codigo entero
		 */
		void sendStatus(int code);

		/** Setea un header del request
		 * @param name: nombre del header
		 * @param val: valor
		 */
		void sendHeader(const std::string& name, const std::string& val);
		void sendHeader(const char* name, const char* val);

		/** Setea el content type
		 */
		void sendContentType(const std::string& type);
		void sendContentType(const char* type);
		void sendContentType(MgConnection::ContentTypes type);

		/** Printf - abstrae mg_vprintf_data
		 */
		size_t printfData(const char* format, ...);

		/** Operador derreferencia.
		 * @return puntero a mg_connection
		 */
		struct mg_connection* operator->();

		/** Obtiene un parametro que anteriormente fue guardado para esta coneccion
		 * @param key: clave por la cual fue guardado
		 * @return valor del parametro o "" si no existe
		 */
		const std::string& getParameter(const std::string& key);

		/** Guarda un parametro para la coneccion
		 * @param key: clave
		 * @param value: valor
		 */
		void setParameter(const std::string& key, const std::string& value);

		/** Obtiene un parametro de la url o de post (si es url encoded)
		 * @param varName: nombre del parametro
		 * @param max: buffer a allocar
		 * @return valor del parametro o "" si no existe
		 */
		std::string getVarStr(const char* varName, size_t max=64);
		std::string getVarStr(const std::string& varName, size_t max=64);

		/** Obtiene un parametro de la url, lo convierte a entero
		 * @see MgConnection::getVarStr
		 */
		int getVarInt(const char* varName, size_t max=64);
		int getVarInt(const std::string& varName, size_t max=64);


	protected:
		struct mg_connection *conn; ///< Instacia de mg_connection
		std::map<std::string, std::string> parameters; ///< map de los parametros guardados por el usuario
};

#endif
