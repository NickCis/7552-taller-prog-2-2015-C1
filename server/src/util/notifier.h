#ifndef __NOTIFIER_H__
#define __NOTIFIER_H__

#include <string>
#include "../mg/mg_server.h"
#include "../db/notification.h"
#include "../db/message.h"
#include "../db/profile.h"
#include "../db/checkin.h"

class Notifier {
	public:
		/** Notifica acerca de un nuevo mensaje
		 * @param mensaje nuevo
		 */
		static void OnMessage(const Message&);

		/** Notifica acerca de un ack de mesanje
		 * @param mensaje
		 */
		static void OnMessageAck(const Message& msg);

		/** Notifica acerca de un cambio de avatar
		 * @param usuario que cambio de avatar
		 */
		static void OnChangeAvatar(const std::string&);

		/** Notifica acerca de un cambio en el perfil
		 * @param perfil de usuario qe cambio
		 */
		static void OnChangeProfile(const Profile&);

		/** Notifica acerca de un checkin
		 * @param checkin de usuario
		 */
		static void OnCheckIn(const Checkin&);

		/** Setea la instancia de server para despachar las notificaciones push
		 */
		static void setServer(MgServer*);

	protected:
		static MgServer* mgServer;
		static void add_push_notification(const std::string& u, const Notification::NotificationType& type, const std::string& json);

};

#endif
