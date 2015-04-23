#ifndef __NOTIFICATION_H__
#define __NOTIFICATION_H__

#include <ctime>
#include <string>
#include <memory>
#include <rocksdb/db.h>
#include <rocksdb/status.h>
#include <rocksdb/iterator.h>

class Notification {
	public:
		typedef enum {
			NOTIFICATION_MESSAGE=0,
			NOTIFICATION_ACK
		} NotificationType;
		class NotificationIterator;

		Notification();

		static rocksdb::Status Put(const std::string& to, Notification::NotificationType type, const std::string& data, Notification& n);
		static rocksdb::Status Put(const std::string& to, Notification::NotificationType type, const std::string& data, const uint64_t& tv, Notification& n);
		static std::shared_ptr<Notification::NotificationIterator> NewIterator();
		static void SetDB(std::shared_ptr<rocksdb::DB> &db, std::shared_ptr<rocksdb::ColumnFamilyHandle> &cf);

		const uint64_t& getUTime() const;
		time_t getTime() const;
		std::string toJson() const;

	protected:
		static std::shared_ptr<rocksdb::DB> db;
		static std::shared_ptr<rocksdb::ColumnFamilyHandle> cf;
		static void GetKeyFromUser(std::vector<char>& data, const std::string& from, const uint64_t& tv);

		static rocksdb::Slice Pack(std::string& data, const Notification::NotificationType&, const std::string& n_data);
		static bool UnPack(const std::string& data, Notification& m);

		std::string data;
		Notification::NotificationType type;
		uint64_t t;
};

class Notification::NotificationIterator {
	public:
		void next();
		//rocksdb::Slice key() const;
		//rocksdb::Slice value() const;
		const Notification& value() const;
		rocksdb::Status status() const;
		bool valid() const;
		NotificationIterator(rocksdb::Iterator*);
		void seek(const std::string& from);

	protected:
		std::shared_ptr<rocksdb::Iterator> it;
		Notification notif;
		void unPack();
		std::string prefix;
};

#endif
