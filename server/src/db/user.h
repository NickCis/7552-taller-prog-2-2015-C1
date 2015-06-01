#ifndef __USER_H__
#define __USER_H__

#include <string>
#include <memory>
#include <rocksdb/db.h>
#include <rocksdb/status.h>

#include "db_entity.h"

/** Clase que maneja los Mensajes en la db
 */
class User : public DbEntity {
	DB_ENTITY_CLASS(User)

	public:
		/** Constructor vacio
		 */
		User();

		rocksdb::Status put();

		/** Setea el nombre de usuario
		 * @param usuario
		 * @return true: si se pudo, false: si el usuario ya existe
		 */
		bool setUsername(const std::string&);

		/** Setea la contrase~na.
		 * @param p: contrase~na
		 * @return true: si la contrase~na es valida.
		 */
		bool setPassword(const std::string& p);  ///< Setea la contrase~na

		const std::string& getUsername() const; ///< Devuelve el nombre de usuario

		/** Chequea que la password sea la misma
		 * @param password a comparar;
		 * @return true: si son la misma, false: si no
		 */
		bool isPassword(const std::string&) const;

		bool unPack(const std::string& key, const std::string& value);
		std::string toJson() const;


	protected:
		void packKey(std::string& key);
		void packValue(std::string& value);

		std::string username; ///< usuario
		std::string password; ///< contrase~na

		/** Valida el usuario.
		 * @param usuario a validar
		 * @return true: si es valido, false: si no.
		 */
		static bool IsUsernameValid(const std::string& username); 

		/** Valida la contra~na
		 * @param contrase~na a validar
		 * @return true: si es valida, false: si no.
		 */
		static bool IsPasswordValid(const std::string& password);

		/** Hashea una contrase~na para no guardarla en texto plano.
		 * @param hashed: buffer donde se escribira la contrase~na hasheada
		 * @param password: contrase~na a hashear
		 */
		static void HashPassword(std::string& hashed, const std::string& password);
};

#endif
