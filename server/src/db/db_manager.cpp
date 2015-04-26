#include "db_manager.h"
#include "user.h"
#include "access_token.h"
#include "message.h"
#include "notification.h"

#include <cstring>

#include <rocksdb/options.h>

#include <iostream>

using std::string;
using std::strcmp;
using std::vector;
using std::shared_ptr;

using rocksdb::DB;
using rocksdb::Status;
using rocksdb::Options;
using rocksdb::DBOptions;
using rocksdb::ColumnFamilyHandle;
using rocksdb::ColumnFamilyOptions;
using rocksdb::ColumnFamilyDescriptor;
using rocksdb::kDefaultColumnFamilyName;

typedef enum Comparator {
	COMPARATOR_DEFAULT=0,
	COMPARATOR_DB_COMPARATOR,
	COMPARATOR_DB_COMPARATOR_REVERSE,
} Comparator;

const struct {
	const char* name;
	const Comparator comparator;
} HANDLES[] = {
	{ "messages", COMPARATOR_DEFAULT },
	{ "users", COMPARATOR_DB_COMPARATOR },
	{ "notifications", COMPARATOR_DB_COMPARATOR },
	{ "access_tokens", COMPARATOR_DEFAULT },
	{ NULL, COMPARATOR_DEFAULT }
};

DBManager::DBManager(const string& p, Status& s) : path(p){
	this->create(s);
	if(s.ok())
		this->open(s);
}
void DBManager::create(Status& s){
	Options options;
	DB* _db;
	options.error_if_exists = true;
	options.create_if_missing = true;
	s = DB::Open(options, path, &_db);

	if(s.ok()){
		ColumnFamilyHandle* cf;
		for(int i=0; HANDLES[i].name; i++){
			ColumnFamilyOptions cfo;
			switch(HANDLES[i].comparator){
				case COMPARATOR_DB_COMPARATOR:
					cfo.comparator = &this->comparator;
					break;
				default:
					break;
			}
			s = _db->CreateColumnFamily(cfo, HANDLES[i].name, &cf);
			delete cf;
			if(!s.ok())
				break;
		}
	}else if(s.IsInvalidArgument() && s.ToString().find("error_if_exists is true") != string::npos){
		s = Status::OK();
	}

	delete _db;
}

void DBManager::open(Status& s){
	DB* _db;
	vector<ColumnFamilyDescriptor> column_families;
	column_families.push_back(ColumnFamilyDescriptor(kDefaultColumnFamilyName, ColumnFamilyOptions()));

	for(int i=0; HANDLES[i].name; i++){
		ColumnFamilyOptions cfo;
		switch(HANDLES[i].comparator){
			case COMPARATOR_DB_COMPARATOR:
				cfo.comparator = &this->comparator;
				break;
			default:
				break;
		}
		column_families.push_back(ColumnFamilyDescriptor(HANDLES[i].name, cfo));
	}

	std::vector<ColumnFamilyHandle*> handles;
	Options options;
	options.comparator = &this->comparator;
	s = DB::Open(DBOptions(options), this->path, column_families, &handles, &_db);
	if(s.ok()){
		this->db = shared_ptr<DB>(_db);
		for(auto h : handles)
			cfs.push_back(std::shared_ptr<ColumnFamilyHandle>(h));
	}
}

shared_ptr<DB> DBManager::get(){
	return this->db;
}

shared_ptr<ColumnFamilyHandle> DBManager::getColumnFamily(DBManager::ColumnFamilies c){
	return this->cfs[c];
}

void DBManager::setEnviroment(){
	User::SetDB(this->db, this->cfs[DBManager::COLUMN_FAMILY_USERS]);
	AccessToken::SetDB(this->db, this->cfs[DBManager::COLUMN_FAMILY_ACCESS_TOKENS]);
	Message::SetDB(this->db, this->cfs[DBManager::COLUMN_FAMILY_MESSAGES]);
	Notification::SetDB(this->db, this->cfs[DBManager::COLUMN_FAMILY_NOTIFICATIONS]);
}
