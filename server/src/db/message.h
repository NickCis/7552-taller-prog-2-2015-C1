#ifndef __MESSAGE_H__
#define __MESSAGE_H__

#include <ctime>
#include <string>
#include <memory>
#include <rocksdb/db.h>
#include <rocksdb/status.h>
#include <rocksdb/iterator.h>

/** Clase que maneja los Mensajes en la db
 */
class Message {
	public:
		/** Clase para iterar por los mensajes
		 */
		class MessageIterator;

		/** Constructor vacio
		 */
		Message();

		/* Escribe un mensaje en la db. Genera el id correspondiente.
		 * @param to[in]: Destinatario del mensaje
		 * @param from[in]: Emisor del mensaje
		 * @param msg[in]: Mensaje
		 * @param a[out]: instancia de Message que se rellenara con la informacion del mensaje creado
		 * @return Estado de error
		*/
		static rocksdb::Status Put(const std::string& to, const std::string& from, const std::string& msg, Message& a);

		/** Obtiene un msje. (Si, uno solo, no es un metodo muy util, hasta se podria decir qe no se usa en ningun lado)
		 * @param to[in]: Destino
		 * @param from[in]: Emisor
		 * @param id[in]: id del msje (en formato binario)
		 * @param a[out]: mensaje encontrado
		 * @return Estado de error
		 */
		static rocksdb::Status Get(const std::string& to, const std::string& from, const uint64_t& id, Message& a);

		/** Devuelve un nuevo iterador
		 */
		static std::shared_ptr<Message::MessageIterator> NewIterator();

		/** Metodo que sirve para setear la instancia a la base de datos y a la column family.
		 * @param db[in]: Instancia a la base de datos a usar
		 * @param cf[in]: instancia a la column family a usar
		 */
		static void SetDB(rocksdb::DB* db, rocksdb::ColumnFamilyHandle* cf);

		const std::string& getFrom() const; ///< Emisor del mensaje
		//const std::string& getTo() const;
		const std::string& getMsg() const; ///< Mensaje
		const time_t& getTime() const; ///< Hora de cuando el msje fue enviado
		std::string getId() const; ///< Id en formato hexa
		//const uint64_t& getId() const;
		std::string toJson() const; ///< Representacion en JSON del mensaje

	protected:
		/** Cabecera del mensaje
		 */
		typedef struct MessageHeader {
			time_t arrived; ///< Hora donde el mesje fue recibido
			time_t read; ///< Hora donde el receptor leyo el mensaje
			int has_file; ///< flag que especifica si el msje tiene un attachment
		} MessageHeader;

		static rocksdb::DB* db; ///< Instacia de la db
		static rocksdb::ColumnFamilyHandle* cf; ///< instancia de la column family

		/** Genera Key (incompleta) de la db apartir de emisor y receptor. 
		 * salida del tipo: emisor/receptor/
		 * @param data[out]: buffer donde se escribira
		 * @param to[in]: receptor
		 * @param from[in]: emisor
		 */
		static void GetConversationFromUser(std::vector<char>& data, const std::string& to, const std::string& from);

		/** Genera Key (completa) de la db apartir de emisor, receptor e id.
		 * salida del tipo: emisor/receptor/id
		 * @param data[out]: buffer donde se escribira
		 * @param to[in]: receptor
		 * @param from[in]: emisor
		 * @param id[in]: id en formato binario
		 */
		static void GetKeyFromUser(std::vector<char>& data, const std::string& to, const std::string& from, const uint64_t& id);

		/** Serializa el mensaje
		 * @param data[out]: buffer destino
		 * @param [in] Header
		 * @param from[in]: 
		 * @param msg[in]
		 * @return Slice de Rocksdb que apunta al buffer data
		 */
		static rocksdb::Slice Pack(std::vector<char>& data, const Message::MessageHeader&, const std::string& from, const std::string& msg);

		/** Deserializa el mensaje
		 * @param data[in]: mensaje serializado
		 * @param m[out]: instacia donde se escribiran los datos
		 * @return true: si data esta bien, false: si no se pudo deserializar
		 */
		static bool UnPack(const std::string& data, Message& m);

		//std::string to;
		std::string from; ///< emisor
		std::string msg; ///< mensjae
		time_t arrived; ///< hora de arrivo (a destino)
		time_t read; ///< hora de lectura
		time_t t; ///< tiempo de servidor
		uint64_t id; ///< id [ tiempo en usecs en big endian ]
		int has_file; ///< tiene attachment?
};


/** Clase para iterar por los mensajes
*/
class Message::MessageIterator {
	public:
		/** Busca mensajes apartir de. (mueve el iterador)
		 * Emisor y receptor son intercambiables, en realidad hace referencia la conversacion en la cual esos dos
		 * usuarios forman parte
		 * @param to: receptor
		 * @param from: emisor
		 */
		void seek(const std::string& to, const std::string& from);

		/** Busca mensajes apartir de. (mueve el iterador)
		 * Emisor y receptor son intercambiables, en realidad hace referencia la conversacion en la cual esos dos
		 * usuarios forman parte
		 * @param to: receptor
		 * @param from: emisor
		 * @param id: en formato hexa
		 */
		void seek(const std::string& to, const std::string& from, const std::string& id);

		/** Busca mensajes apartir de. (mueve el iterador)
		 * Emisor y receptor son intercambiables, en realidad hace referencia la conversacion en la cual esos dos
		 * usuarios forman parte
		 * @param to: receptor
		 * @param from: emisor
		 * @param id: en formato binario
		 */
		void seek(const std::string& to, const std::string& from, const uint64_t& id);

		/** Va al primer mensaje. La verdad no tiene mucha utilidad, se puede usar para listar todos los mensajes
		 * de todas las conversaciones. (mueve el iterador)
		 */
		void seekToFirst();

		/** Mueve el iterador al ultimo mensaje de la conversacion de los usuarios especificados
		 * Emisor y receptor son intercambiables, en realidad hace referencia la conversacion en la cual esos dos
		 * @param to: receptor
		 * @param from: emisor
		 */
		rocksdb::Status seekToLast(const std::string& to, const std::string& from);

		/** Mueve el iterador para atras
		 */
		void prev();

		/** mueve el iterador para adelante
		 */
		void next();
		//rocksdb::Slice key() const;
		//rocksdb::Slice value() const;
		const Message& value() const; ///< Devuelve el Message correspondiste a la posicion actual
		rocksdb::Status status() const; ///< Estado del iterador
		bool valid() const; ///< Devuelve si el iterador esta en una posicion valida

		/** Constructor
		 * @param iterador de rocksdb asociado a la db
		 */
		MessageIterator(rocksdb::Iterator*);

	protected:
		std::shared_ptr<rocksdb::Iterator> it; ///< Instancia al iterador de rocksdb
		Message msg; ///< mensaje actual
		void unPack(); ///< deserializador del mensaje
		std::vector<char> prefix; ///< Prefijo de las Keys buscadas
		bool _valid() const; ///< Verifica la validez del interador
};

#endif
