#include "user_merge_operator.h"

#include "profile.h"

#include "../util/serializer.h"
#include "../util/has_suffix.h"

using std::string;
using std::has_suffix;

using rocksdb::Slice;
using rocksdb::Logger;

bool UserMergeOperator::Merge(const Slice& key,
		const Slice* existing_value,
		const Slice& value,
		string* new_value,
		Logger* logger) const {

	string s = key.ToString();

	if(has_suffix(s, "/profile"))
		return this->profile(key, existing_value, value, new_value, logger);
	else if(has_suffix(s, "/suscriber"))
		return ListMergeOperator::Merge(key, existing_value, value, new_value, logger);

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
