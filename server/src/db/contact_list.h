#ifndef __CONTACT_LIST_H__
#define __CONTACT_LIST_H__

#include <string>
#include <vector>
#include <rocksdb/db.h>
#include <rocksdb/status.h>
#include <rocksdb/write_batch.h>
#include <rocksdb/iterator.h>

#include "db_entity.h"

/** Clase que maneja las notificaciones en la db
 */
class ContactList : public DbEntity  {
	DB_ENTITY_CLASS(ContactList)

	public:
		/** Constructor
		 */
		ContactList();

		const std::vector<std::string>& getList() const;
		std::string toJson() const; ///< Representacion en JSON

		rocksdb::Status push_back(const std::string&);
		rocksdb::Status erase(const std::string&);

		const std::string& getOwner() const;
		void setOwner(const std::string&);

		bool unPack(const std::string& key, const std::string& value);

	protected:
		void packKey(std::string& key);
		void packValue(std::string& value);

		/** Infomacion de la notificacion **/
		std::vector<std::string> contactList;
};

#endif
