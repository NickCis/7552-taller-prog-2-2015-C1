#include "contact_list_merge_operator.h"

#include "../util/serializer.h"

using std::string;

using rocksdb::Slice;
using rocksdb::Logger;

bool ContactListMergeOperator::Merge(const Slice&,
		const Slice* existing_value,
		const Slice& value,
		string* new_value,
		Logger*) const {

	switch(value.ToString()[0]){
		case 'p':
			return this->push_back(existing_value, value, new_value);
			break;
		case 'e':
			return this->erase(existing_value, value, new_value);
			break;

		default:
			break;
	}

	return false;
}

bool ContactListMergeOperator::push_back(const rocksdb::Slice* existing_value,
		const rocksdb::Slice& value,
		std::string* new_value) const {
	OSerializer ser(*new_value);

	if(existing_value)
		ser << ConstStrNoPrefix(existing_value->ToString());

	ser << value.ToString().substr(1);

	return true;
}

bool ContactListMergeOperator::erase(const rocksdb::Slice* existing_value,
		const rocksdb::Slice& value,
		std::string* new_value) const {
	if(!existing_value)
		return false;

	OSerializer oser(*new_value);
	ISerializer iser(existing_value->ToString());

	string user;
	string remove = value.ToString().substr(1);

	while(!((iser >> user).error())){
		if(user != remove)
			oser << user;
	}

	return true;
}

const char* ContactListMergeOperator::Name() const{
	return "ContactListMergeOperator";
}
