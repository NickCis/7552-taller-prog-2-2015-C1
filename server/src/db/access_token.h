#ifndef __ACCESS_TOKEN_H__
#define __ACCESS_TOKEN_H__

#include <string>
#include <memory>
#include <rocksdb/db.h>
#include <rocksdb/status.h>

/** Clase que maneja los AccessTokens en la db
 */
class AccessToken {
	public:
		AccessToken();

		/** Obtiene un access token, hace el Get de RocksDb
		 * @param token[in] : string que representa el access token (id)
		 * @param a[out] : Instacia de access token donde se cargara la informacion de la db
		 * @return Estado de error
		 */
		static rocksdb::Status Get(const std::string& token, AccessToken& a);

		/** Escribe access token en la db. Genera el token, apartir de un nombre de usuario.
		 * @param u[in]: usuario propietario del access token
		 * @param a[out]: Instancia de access token donde se cargara informacion
		 * @return Estado de error
		 */
		static rocksdb::Status Put(const std::string& u, AccessToken& a);

		/** Chequea si un token es valido, es decir, si corresponde a un access token conectado.
		 * @param t[in]: Token
		 * @return true: Es valido, false: no lo es
		 */
		static bool IsLoggedIn(const std::string&t);

		/** Devuelve la representacion en string del token
		 * @return token
		 */
		const std::string& getToken() const;

		/** Devuelve el nombre de usuario
		 * @return Nombre de usuario
		 */
		const std::string& getUsername() const;

		/** Metodo que sirve para setear la instancia a la base de datos y a la column family.
		 * @param db[in]: Instancia a la base de datos a usar
		 * @param cf[in]: instancia a la column family a usar
		 */
		static void SetDB(std::shared_ptr<rocksdb::DB> &db, std::shared_ptr<rocksdb::ColumnFamilyHandle> &cf);

	protected:
		std::string token; ///< Representacion en string del token
		std::string username; ///< Nombre de usuario del token

		/** Crea un token apartir de un nombre de usuario. Utiliza numeros random y la hora actual.
		 * @param username[in]: Nombre de usuario
		 * @return Access token
		 */
		static std::string CreateToken(const std::string& username);

		static std::shared_ptr<rocksdb::DB> db; ///< Instancia de la db
		static std::shared_ptr<rocksdb::ColumnFamilyHandle> cf; ///< Instancia de la column family
};

#endif
