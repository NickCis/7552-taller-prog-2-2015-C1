#ifndef __USER_H__
#define __USER_H__

#include <string>
#include <memory>
#include <rocksdb/db.h>
#include <rocksdb/status.h>

/** Clase que maneja los Mensajes en la db
 */
class User {
	public:
		/** Constructor vacio
		 */
		User();

		/** Obtiene un usuario
		 * @param username[in]: nombre de usuario
		 * @param u[out]: instancia de usuario
		 * @return Estado de error
		 */
		static rocksdb::Status Get(const std::string& username, User& u);

		/** Metodo que sirve para setear la instancia a la base de datos y a la column family.
		 * @param db[in]: Instancia a la base de datos a usar
		 * @param cf[in]: instancia a la column family a usar
		 */
		static void SetDB(std::shared_ptr<rocksdb::DB> &db, std::shared_ptr<rocksdb::ColumnFamilyHandle> &cf);

		/** Escribe usuario en la db
		 * @param u: nombre de usuario
		 * @param p: password
		 * @param check: true para que haga validacion de usuario y datos, false para que escriba de una
		 * @return Estado de error
		 */
		static rocksdb::Status Put(const std::string& u, const std::string p, bool check=true);

		const std::string& getUsername() const; ///< Devuelve el nombre de usuario

		/** Devuelve la contrase~na
		 * @param forceFetch: fuerza hacer un GET
		 * @return contrase~na
		 */
		const std::string& getPassword(bool forceFetch=false);

		/** Setea el password. Hace PUT en la db despues.
		 * @param p: contrase~na nueva
		 * @return Estado de error
		 */
		rocksdb::Status setPassword(const std::string& p);

	protected:
		std::string username; ///< usuario
		std::string password; ///< contrase~na

		static std::shared_ptr<rocksdb::DB> db; ///< instancia db
		static std::shared_ptr<rocksdb::ColumnFamilyHandle> cf; ///< instancia column family

		static bool IsUsernameValid(const std::string& username); ///< Valida nombre de usuario
		static bool IsPasswordValid(const std::string& password); ///< Valida contrase~na
};

#endif
