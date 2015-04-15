// Copyright (c) 2013, Facebook, Inc.  All rights reserved.
// This source code is licensed under the BSD-style license found in the
// LICENSE file in the root directory of this source tree. An additional grant
// of patent rights can be found in the PATENTS file in the same directory.

// Links
// Column Families: https://github.com/facebook/rocksdb/wiki/Column-Families
//

#include <cstdio>
#include <cstring>
#include <string>
#include <vector>
#include <iostream>

#include <rocksdb/db.h>
#include <rocksdb/slice.h>
#include <rocksdb/options.h>

using namespace rocksdb;

std::string kDBPath = "/tmp/rocksdb_column_families_example";

int main(int argc, const char*argv[]) {
	Status s;
	DB* db;

	if(argc > 1 && std::strcmp(argv[1], "create") == 0){
		// open DB
		Options options;
		options.create_if_missing = true;
		Status s = DB::Open(options, kDBPath, &db);
		assert(s.ok());

		// create column family
		ColumnFamilyHandle* cf;
		s = db->CreateColumnFamily(ColumnFamilyOptions(), "messages", &cf);
		assert(s.ok());

		// close DB
		delete cf;
		delete db;

		return 0;
	}

	// open DB with two column families
	std::vector<ColumnFamilyDescriptor> column_families;
	// have to open default column family
	column_families.push_back(ColumnFamilyDescriptor(
				kDefaultColumnFamilyName, ColumnFamilyOptions()));
	// open the new one, too
	column_families.push_back(ColumnFamilyDescriptor(
				"messages", ColumnFamilyOptions()));
	std::vector<ColumnFamilyHandle*> handles;
	s = DB::Open(DBOptions(), kDBPath, column_families, &handles, &db);
	assert(s.ok());

	if(argc > 4 && std::strcmp(argv[1], "put") == 0){
		// put and get from non-default column family
		int index = 0;
		if(std::strcmp(argv[2], "messages") == 0)
			index = 1;

		s = db->Put(WriteOptions(), handles[index], Slice(argv[3]), Slice(argv[4]));
		assert(s.ok());

		//// atomic write
		//WriteBatch batch;
		//batch.Put(handles[0], Slice("key2"), Slice("value2"));
		//batch.Put(handles[1], Slice("key3"), Slice("value3"));
		////batch.Delete(handles[0], Slice("key"));
		//s = db->Write(WriteOptions(), &batch);
		//assert(s.ok());

	}

	if(argc > 3 && std::strcmp(argv[1], "get") == 0){
		std::string value;
		int index = 0;
		if(std::strcmp(argv[2], "messages") == 0)
			index = 1;
		s = db->Get(ReadOptions(), handles[index], Slice(argv[3]), &value);
		assert(s.ok());
		std::cout << argv[3] << ": '" << value << "'" << std::endl;
	}

	if(argc > 1 && std::strcmp(argv[1], "drop") == 0){
		// drop column family
		s = db->DropColumnFamily(handles[1]);
		assert(s.ok());
	}

	if(argc > 3 && std::strcmp(argv[1], "seek") == 0){
		int index = 0;
		if(std::strcmp(argv[2], "messages") == 0)
			index = 1;
		auto it = db->NewIterator(ReadOptions(), handles[index]);

		for(it->Seek(argv[3]); it->Valid() && it->key().starts_with(argv[3]); it->Next()){
			std::cout << it->key().ToString() << ": '" << it->value().ToString() << "'" << std::endl;
		}

		assert(s.ok());
		delete it;
	}

	if(argc > 3 && std::strcmp(argv[1], "seekprev") == 0){
		int index = 0;
		if(std::strcmp(argv[2], "messages") == 0)
			index = 1;
		auto it = db->NewIterator(ReadOptions(), handles[index]);
		int cant = 5;

		for(it->Seek(argv[3]); cant-- && it->Valid(); it->Prev()){
			std::cout << it->key().ToString() << ": '" << it->value().ToString() << "'" << std::endl;
		}

		assert(s.ok());
		delete it;
	}

	if(argc > 3 && std::strcmp(argv[1], "last") == 0){
		int index = 0;
		if(std::strcmp(argv[2], "messages") == 0)
			index = 1;

		auto it = db->NewIterator(ReadOptions(), handles[index]);
		it->Seek(argv[3]);
		it->SeekToLast();
		if(it->Valid() && it->key().starts_with(argv[3]))
			std::cout << it->key().ToString() << ": '" << it->value().ToString() << "'" << std::endl;

		assert(s.ok());
		delete it;
	}

	if(argc > 3 && std::strcmp(argv[1], "lasts") == 0){
		int index = 0;
		if(std::strcmp(argv[2], "messages") == 0)
			index = 1;

		int cant = 10;

		auto it = db->NewIterator(ReadOptions(), handles[index]);
		it->Seek(argv[3]);
		for(it->SeekToLast(); cant-- && it->Valid() && it->key().starts_with(argv[3]); it->Prev()){
			std::cout << it->key().ToString() << ": '" << it->value().ToString() << "'" << std::endl;
		}

		assert(s.ok());
		delete it;
	}

	// close db
	for (auto handle : handles) {
		delete handle;
	}
	delete db;

	return 0;
}
