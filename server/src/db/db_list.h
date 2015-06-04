#ifndef __DB_LIST_H__
#define __DB_LIST_H__

#include <string>
#include <vector>
#include <rocksdb/db.h>
#include <rocksdb/status.h>

#include "db_entity.h"

/** Clase que representa una lista serializada a la db
 */
class DbList : public DbEntity  {
	public:
		const std::vector<std::string>& getList() const;

		rocksdb::Status push_back(const std::string&);
		rocksdb::Status erase(const std::string&);

		bool unPack(const std::string& key, const std::string& value);

	protected:
		void packValue(std::string& value);

		std::vector<std::string> list;
};

#endif
