#ifndef __SUSCRIBER_LIST_H__
#define __SUSCRIBER_LIST_H__

#include <string>
#include <vector>
#include <rocksdb/db.h>
#include <rocksdb/status.h>
#include <rocksdb/write_batch.h>
#include <rocksdb/iterator.h>

#include "db_entity.h"
#include "db_list.h"

/** Clase que maneja las suscripciones de usuarios para los cambios de otros
 */
class SuscriberList : public DbList  {
	DB_ENTITY_CLASS(SuscriberList)

	public:
		/** Constructor
		 */
		SuscriberList();

		std::string toJson() const; ///< Representacion en JSON

		const std::string& getOwner() const;
		void setOwner(const std::string&);

		bool unPack(const std::string& key, const std::string& value);

		rocksdb::Status get(const std::string& key);

	protected:
		std::string owner;
		void packKey(std::string& key);
};

#endif
