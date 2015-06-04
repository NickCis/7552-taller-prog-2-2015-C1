#include "notifier.h"

#include "../db/suscriber_list.h"

#include "../db/notification.h"
#include "../db/profile.h"
#include "../db/checkin.h"

#include <sstream>
#include <ctime>

using std::time;
using std::string;
using std::stringstream;


void Notifier::OnChangeAvatar(const string& u){
	SuscriberList sl;
	sl.get(u);

	stringstream ss;
	ss << "{\"username\":\"" << u << "\",\"time\":" << time(NULL) << "}";
	for(auto it=sl.getList().begin(); it != sl.getList().end(); it++)
		Notification::Now(*it, Notification::NOTIFICATION_AVATAR, ss.str()).put();
}

void Notifier::OnChangeProfile(const string& u){
	SuscriberList sl;
	sl.get(u);

	Profile p;
	p.get(u);
	string json = p.toJson();

	for(auto it=sl.getList().begin(); it != sl.getList().end(); it++)
		Notification::Now(*it, Notification::NOTIFICATION_PROFILE, json).put();
}

void Notifier::OnCheckIn(const string& u){
	SuscriberList sl;
	sl.get(u);

	Checkin c;
	c.get(u);
	string json = c.toJson();
	for(auto it=sl.getList().begin(); it != sl.getList().end(); it++)
		Notification::Now(*it, Notification::NOTIFICATION_CHECKIN, json).put();
}
