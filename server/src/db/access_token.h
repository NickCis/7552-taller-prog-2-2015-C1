#ifndef __ACCESS_TOKEN_H__
#define __ACCESS_TOKEN_H__

#include <string>
#include <rocksdb/db.h>
#include <rocksdb/status.h>

#include "db_entity.h"

/** Clase que maneja los AccessTokens en la db
 */
class AccessToken : public DbEntity{
	DB_ENTITY_CLASS(AccessToken)

	public:
		AccessToken();

		/** Chequea si un token es valido, es decir, si corresponde a un access token conectado.
		 * @param t[in]: Token
		 * @return true: Es valido, false: no lo es
		 */
		static bool IsLoggedIn(const std::string&);

		void setOwner(const std::string&);

		/** Devuelve la representacion en string del token
		 * @return token
		 */
		const std::string& getToken() const;

		/** Devuelve el nombre de usuario
		 * @return Nombre de usuario
		 */
		const std::string& getUsername() const;

		rocksdb::Status put();

		bool unPack(const std::string& key, const std::string& value);

		std::string toJson() const;

	protected:
		void packKey(std::string& key);
		void packValue(std::string& value);

		std::string token; ///< Representacion en string del token
		std::string owner; ///< Nombre de usuario due~no del token

		/** Crea un token apartir de un nombre de usuario. Utiliza numeros random y la hora actual.
		 * @param username[in]: Nombre de usuario
		 * @return Access token
		 */
		static std::string CreateToken(const std::string& username);
};

#endif
