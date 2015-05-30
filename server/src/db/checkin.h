#ifndef __CHECKIN_H__
#define __CHECKIN_H__

#include <ctime>
#include <string>
#include <rocksdb/db.h>
#include <rocksdb/status.h>
#include <rocksdb/write_batch.h>
#include <rocksdb/iterator.h>

#include "db_entity.h"

/** Clase que maneja las notificaciones en la db
 */
class Checkin : public DbEntity  {
	DB_ENTITY_CLASS(Checkin)

	public:
		/** Constructor
		 */
		Checkin();

		void setOwner(const std::string&);
		void setName(const std::string&);
		void setPosition(const double&, const double&);

		const std::string& getOwner() const;
		const std::string& getName() const;
		const double& getLatitude() const;
		const double& getLongitude() const;
		const time_t& getTimestamp() const;

		rocksdb::Status get(const std::string& key);
		rocksdb::Status put();

		std::string toJson() const;

		bool unPack(const std::string& key, const std::string& value);

	protected:
		void packKey(std::string& key);
		void packValue(std::string& value);

		/** Infomacion de la notificacion **/
		std::string owner; ///< a quien le pertenece la notificacion
		std::string name; ///< Nombre del lugar
		double latitude; ///< latitud de la posicion
		double longitude; ///< longitud de la posicion
		time_t timestamp; ///< timestamp de cuando fue
};

#endif
