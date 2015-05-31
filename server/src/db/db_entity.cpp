#include "db_entity.h"

DbEntity::DbEntity(){
}

DbEntity::~DbEntity(){
}

rocksdb::Status DbEntity::get(const std::string& key){
	this->key = key;
	rocksdb::Status s = this->getDb()->Get(rocksdb::ReadOptions(), this->getCf(), rocksdb::Slice(this->key), &this->value);
	if(s.ok())
		if(!this->unPack())
			return rocksdb::Status::Corruption(this->key);

	return s;
}

rocksdb::Status DbEntity::put(){
	this->pack();
	return this->put(rocksdb::Slice(this->key), rocksdb::Slice(this->value));
}

rocksdb::Slice DbEntity::pack(){
	return this->pack(this->key, this->value);
}

rocksdb::Slice DbEntity::pack(std::string& key, std::string& value){
	this->packKey(key);
	this->packValue(value);
	return rocksdb::Slice(value);
}

void DbEntity::packKey(){
	this->packKey(this->key);
}

void DbEntity::packValue(){
	this->packValue(this->value);
}

bool DbEntity::unPack(){
	return this->unPack(this->key, this->value);
}

rocksdb::Status DbEntity::put(const rocksdb::Slice& key, const rocksdb::Slice& value){
	return this->getDb()->Put(rocksdb::WriteOptions(), this->getCf(), key, value);
}

rocksdb::Status DbEntity::merge(const std::string& value){
	return this->merge(rocksdb::Slice(this->key), rocksdb::Slice(value));
} 

rocksdb::Status DbEntity::merge(const rocksdb::Slice& key, const rocksdb::Slice& value){
	return this->getDb()->Merge(rocksdb::WriteOptions(), this->getCf(), key, value);
}
