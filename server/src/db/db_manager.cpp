#include "db_manager.h"
#include "user.h"
#include "access_token.h"
#include "message.h"
#include "notification.h"
#include "contact_list.h"
#include "contact_list_merge_operator.h"

#include <cstring>
#include <iostream>

#include <rocksdb/options.h>

using std::string;
using std::strcmp;
using std::vector;
using std::unique_ptr;

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

typedef enum MergeOperator {
	MERGE_DEFAULT=0,
	MERGE_DB_CONTACT_LIST
} MergeOperator;

static const struct {
	const char* name;
	const Comparator comparator;
	const MergeOperator mergeOperator;
} HANDLES[] = {
	{ "messages", COMPARATOR_DEFAULT, MERGE_DEFAULT },
	{ "users", COMPARATOR_DB_COMPARATOR, MERGE_DEFAULT },
	{ "notifications", COMPARATOR_DB_COMPARATOR, MERGE_DEFAULT },
	{ "access_tokens", COMPARATOR_DEFAULT, MERGE_DEFAULT },
	{ "contact_list", COMPARATOR_DEFAULT, MERGE_DB_CONTACT_LIST },
	{ NULL, COMPARATOR_DEFAULT, MERGE_DEFAULT }
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

			switch(HANDLES[i].mergeOperator){
				case MERGE_DB_CONTACT_LIST:
					cfo.merge_operator.reset(new ContactListMergeOperator);
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
		this->db = unique_ptr<DB>(_db);
		for(auto h : handles)
			cfs.push_back(unique_ptr<ColumnFamilyHandle>(h));
	}
}

DB* DBManager::get(){
	return this->db.get();
}

ColumnFamilyHandle* DBManager::getColumnFamily(DBManager::ColumnFamilies c){
	return this->cfs[c].get();
}

void DBManager::setEnviroment(){
	User::SetDB(this->db.get(), this->cfs[DBManager::COLUMN_FAMILY_USERS].get());
	AccessToken::SetDB(this->db.get(), this->cfs[DBManager::COLUMN_FAMILY_ACCESS_TOKENS].get());
	Message::SetDB(this->db.get(), this->cfs[DBManager::COLUMN_FAMILY_MESSAGES].get());
	Notification::SetDB(this->db.get(), this->cfs[DBManager::COLUMN_FAMILY_NOTIFICATIONS].get());
	ContactList::SetDB(this->db.get(), this->cfs[DBManager::COLUMN_FAMILY_CONTACT_LIST].get());
}
