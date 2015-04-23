#ifndef __DB_HANDLE_H__
#define __DB_HANDLE_H__

#include <string>
#include <memory>
#include <vector>

#include <rocksdb/db.h>
#include <rocksdb/status.h>

#include "db_comparator.h"

class DBManager {
	public:
		typedef enum ColumnFamilies {
			COLUMN_FAMILY_DEFAULT=0,
			COLUMN_FAMILY_MESSAGES,
			COLUMN_FAMILY_USERS,
			COLUMN_FAMILY_NOTIFICATIONS,
			COLUMN_FAMILY_ACCESS_TOKENS,
			COLUMN_FAMILY_TOTAL
		} ColumnFamilies;

		DBManager(const std::string& path, rocksdb::Status& s);
		std::shared_ptr<rocksdb::DB> get();
		std::shared_ptr<rocksdb::ColumnFamilyHandle> getColumnFamily(DBManager::ColumnFamilies c);
		void setEnviroment();

	protected:
		std::string path;
		std::shared_ptr<rocksdb::DB> db;
		std::vector < std::shared_ptr < rocksdb::ColumnFamilyHandle > > cfs;
		DbComparator comparator;

		void create(rocksdb::Status& s);
		void open(rocksdb::Status& s);
};

#endif
