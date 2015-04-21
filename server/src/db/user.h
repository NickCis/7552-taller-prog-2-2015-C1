#ifndef __USER_H__
#define __USER_H__

#include <string>
#include <memory>
#include <rocksdb/db.h>
#include <rocksdb/status.h>

class User {
	public:
		User();

		static rocksdb::Status Get(const std::string& username, User& u, bool check=true);
		static void SetDB(std::shared_ptr<rocksdb::DB> &db, std::shared_ptr<rocksdb::ColumnFamilyHandle> &cf);
		static rocksdb::Status Put(const std::string& u, const std::string p, bool check=true);

		std::string& getUsername();

		std::string& getPassword(bool forceFetch=false);
		rocksdb::Status setPassword(const std::string& p);

	protected:
		std::string username;
		std::string password;

		static std::shared_ptr<rocksdb::DB> db;
		static std::shared_ptr<rocksdb::ColumnFamilyHandle> cf;

		static bool IsUsernameValid(const std::string& username);
		static bool IsPasswordValid(const std::string& password);
};

#endif