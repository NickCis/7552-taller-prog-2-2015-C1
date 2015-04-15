#ifndef __ACCESS_TOKEN_H__
#define __ACCESS_TOKEN_H__

#include <string>
#include <memory>
#include <rocksdb/db.h>
#include <rocksdb/status.h>

class AccessToken {
	public:
		AccessToken();

		static rocksdb::Status Get(const std::string& token, AccessToken& a);
		static rocksdb::Status Put(const std::string& u, AccessToken& a);

		const std::string& getToken();


		static void setDB(std::shared_ptr<rocksdb::DB> &db, std::shared_ptr<rocksdb::ColumnFamilyHandle> &cf);

	protected:
		std::string token;
		std::string username;

		static std::string createToken(const std::string& username);

		static std::shared_ptr<rocksdb::DB> db;
		static std::shared_ptr<rocksdb::ColumnFamilyHandle> cf;
};

#endif
