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
		static std::shared_ptr<Notification::NotificationIterator> NewIterator();
		static void SetDB(std::shared_ptr<rocksdb::DB> &db, std::shared_ptr<rocksdb::ColumnFamilyHandle> &cf);

		const uint64_t& getIdBin() const;
		std::string getId() const;
		const time_t& getTime() const;
		std::string toJson() const;

		static std::string TypeToStr(const NotificationType& type);

		static rocksdb::Status DeleteUpTo(const std::string& from, const std::string& id);
		static rocksdb::Status DeleteUpTo(const std::string& from, const uint64_t& id);

	protected:
		static std::shared_ptr<rocksdb::DB> db;
		static std::shared_ptr<rocksdb::ColumnFamilyHandle> cf;
		static void GetKeyFromUser(std::vector<char>& data, const std::string& from, const uint64_t& tv);

		static rocksdb::Slice Pack(std::string& data, const Notification::NotificationType&, const std::string& n_data);
		static bool UnPack(const std::string& data, Notification& m);

		std::string data;
		Notification::NotificationType type;
		time_t t;
		uint64_t id;
};

class Notification::NotificationIterator {
	public:
		void next();
		//rocksdb::Slice key() const;
		//rocksdb::Slice value() const;
		const Notification& value() const;
		rocksdb::Slice key() const;
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
