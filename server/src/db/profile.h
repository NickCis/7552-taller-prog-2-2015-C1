#ifndef __PROFILE_H__
#define __PROFILE_H__

#include <ctime>
#include <string>
#include <rocksdb/db.h>
#include <rocksdb/status.h>
#include <rocksdb/write_batch.h>
#include <rocksdb/iterator.h>

#include "db_entity.h"
#include "user_merge_operator.h"

/** Clase que maneja las notificaciones en la db
 */
class Profile : public DbEntity  {
	DB_ENTITY_CLASS(Profile)

	friend class UserMergeOperator;

	public:
		/** Constructor
		 */
		Profile();

		void setOwner(const std::string&);
		void setNick(const std::string&);
		void setOnline(const bool&);
		void setStatus(const std::string&);

		const std::string& getOwner() const;
		const std::string& getNick() const;
		const bool& getOnline() const;
		/** Devuelve el estado online del usuario. Es decir, ademas de fijarse si el usuario esta configurado como
		 * conectado o desconectado, utiliza logica de last activity
		 */
		bool getOnlineStatus() const;
		const std::string& getStatus() const;
		const time_t& getStatusTime() const;
		const time_t& getLastActivity() const;

		rocksdb::Status get(const std::string& key);

		std::string toJson() const;

		bool unPack(const std::string& key, const std::string& value);

		static rocksdb::Status UpdateLastActivity(const std::string& user);

	protected:
		using DbEntity::packKey;
		void packKey(std::string& key);

		using DbEntity::packValue;
		void packValue(std::string& value);

		/** Infomacion de la notificacion **/
		std::string owner; ///< a quien le pertenece la notificacion
		std::string nick; ///< nickname del usuario
		bool online; ///< Conectado / desconectado
		std::string status; ///< estado
		time_t status_time; ///< timestamp de la modificacion del estado
		time_t last_activity; ///< timestamp de la ultima modificacion
};

#endif
