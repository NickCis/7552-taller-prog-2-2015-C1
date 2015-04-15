#include "db_manager.h"
#include <rocksdb/options.h>

#include "user.h"
#include "access_token.h"

using std::string;
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

const char* HANDLES[] = {
	"messages",
	"users",
	"notifications",
	"access_tokens",
	NULL
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
		for(int i=0; HANDLES[i]; i++){
			s = _db->CreateColumnFamily(ColumnFamilyOptions(), HANDLES[i], &cf);
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

	for(int i=0; HANDLES[i]; i++){
		column_families.push_back(ColumnFamilyDescriptor(HANDLES[i], ColumnFamilyOptions()));
	}

	std::vector<ColumnFamilyHandle*> handles;
	s = DB::Open(DBOptions(), this->path, column_families, &handles, &_db);

	this->db = shared_ptr<DB>(_db);
	for(auto h : handles)
		cfs.push_back(std::shared_ptr<ColumnFamilyHandle>(h));
}

shared_ptr<DB> DBManager::get(){
	return this->db;
}

shared_ptr<ColumnFamilyHandle> DBManager::getColumnFamily(DBManager::ColumnFamilies c){
	return this->cfs[c];
}

void DBManager::setEnviroment(){
	User::setDB(this->db, this->cfs[DBManager::COLUMN_FAMILY_USERS]);
	AccessToken::setDB(this->db, this->cfs[DBManager::COLUMN_FAMILY_ACCESS_TOKENS]);
}
