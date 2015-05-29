#ifndef __AVATAR_H__
#define __AVATAR_H__

#include <ctime>
#include <string>
#include <rocksdb/db.h>
#include <rocksdb/status.h>
#include <rocksdb/write_batch.h>
#include <rocksdb/iterator.h>

#include "db_entity.h"

/** Clase que maneja las notificaciones en la db
 */
class Avatar : public DbEntity  {
	DB_ENTITY_CLASS(Avatar)

	public:
		/** Constructor
		 */
		Avatar();

		void setOwner(const std::string&);
		void setData(const std::string&);
		const std::string& getData() const;
		const time_t& getTime() const;

		rocksdb::Status get(const std::string& key);

		std::string toJson() const;

		bool unPack(const std::string& key, const std::string& value);

	protected:
		void packKey(std::string& key);
		void packValue(std::string& value);

		/** Infomacion de la notificacion **/
		std::string owner; ///< a quien le pertenece la notificacion
		time_t t; ///< timestamp de la notificacion
		std::string data; ///< data que guarda la notificacion
};

#endif
