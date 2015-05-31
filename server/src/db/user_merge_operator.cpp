#include "user_merge_operator.h"

#include "profile.h"

#include "../util/serializer.h"

using std::string;

using rocksdb::Slice;
using rocksdb::Logger;

bool has_suffix(const string &str, const string &suffix){
	return str.size() >= suffix.size() && str.compare(str.size()-suffix.size(), suffix.size(), suffix) == 0;
}

bool UserMergeOperator::Merge(const Slice& key,
		const Slice* existing_value,
		const Slice& value,
		string* new_value,
		Logger* logger) const {

	string s = key.ToString();

	if(has_suffix(s, "/profile"))
		return this->profile(key, existing_value, value, new_value, logger);

	return false;
}

bool UserMergeOperator::profile(const Slice& key,
		const Slice* existing_value,
		const Slice& value,
		string* new_value,
		Logger*) const {

	Profile profile;
	profile.unPack(key.ToString(), existing_value ? existing_value->ToString() : "");

	ISerializer(value.ToString()) >> profile.last_activity;

	profile.packValue(*new_value);

	return true;
}

const char* UserMergeOperator::Name() const{
	return "UserMergeOperator";
}
