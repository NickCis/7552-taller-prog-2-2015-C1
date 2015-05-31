#ifndef __USER_MERGE_OPERATOR_H__
#define __USER_MERGE_OPERATOR_H__

#include <string>

#include <rocksdb/slice.h>
#include <rocksdb/merge_operator.h>

class UserMergeOperator : public rocksdb::AssociativeMergeOperator {
	public:
		bool Merge(const rocksdb::Slice& key,
				const rocksdb::Slice* existing_value,
				const rocksdb::Slice& value,
				std::string* new_value,
				rocksdb::Logger* logger) const;

		const char* Name() const;

	protected:
		bool profile(const rocksdb::Slice& key,
				const rocksdb::Slice* existing_value,
				const rocksdb::Slice& value,
				std::string* new_value,
				rocksdb::Logger* logger) const;
};
#endif
