#ifndef __SERIALIZER_H__
#define __SERIALIZER_H__

#include <string>
#include <algorithm>
#include <type_traits>

/** Clase de ayuda para especificar elementos a serializar sin prefijo de longitud
 */
template <typename T>
class NoPrefix {
	public:
		/** Constructor.
		 * @param t: buffer
		 * @param s: tama~no, utilizado solo cuando se utiliza la clase para extraer (ISerializer)
		 */
		NoPrefix(T& t, size_t s = 0) :
			str(t),
			_size(s) {}

		/** Operador para acceder al buffer
		 */
		T* operator->(){
			return &this->str;
		}

		/** Tama~no
		 */
		size_t size(){
			return this->_size;
		}

	protected:
		T& str; ///< Referencia al buffer
		size_t _size; ///< Tama~no especificado por el usuario
};

/** Clase usada para extraer de un ISerializer un string sin prefijo de longitud.
 */
typedef NoPrefix<std::string> StrNoPrefix;

/** Clase usada para insertar en un OSerializer un string sin prefijo de longitud
 */
typedef NoPrefix<const std::string> ConstStrNoPrefix;

/** Clase de ayuda para ignorar cosas en el ISerializer
 */
class Ignore {
	public:
		Ignore();

		/** Ignora strlen() del string de c pasado.
		 * Debe terminar en '\0'!
		 */
		Ignore(const char*);

		/**Ignora la cantidad de bytes especificada
		 */
		Ignore(const size_t);

		/** Ignora la cantidad de bytes especificada
		 */
		Ignore(const int);

		/** Ignora un char
		 */
		Ignore(const char);

		/** Devuelve el tama~no a ignorar
		 */
		size_t size();

	protected:
		size_t _size; ///< Tama~no a ignorar
};

/** Output serializer, clase para serializar utilizando un string como buffer.
 */
class OSerializer {
	public:
		/** Constructor.
		 * @param buffer: buffer al cual se serealizara
		 * @param size: tama~no a reservar en el buffer
		 */
		OSerializer(std::string &buffer, size_t size=0);

		/** Inserta un string con prefijo de longitud
		 */
		OSerializer& operator<<(const std::string&);

		/** Inserta un vector<char> con prefijo de longitud
		 */
		OSerializer& operator<<(const std::vector<char>&);

		/** Inserta un string sin prefijo de longitud.
		 * No usar este metodo, usar @see operator<<(ContStrNoPrefix)
		 */
		OSerializer& operator<<(StrNoPrefix) __attribute__ ((deprecated));

		/** Inserta un string sin prefijo de longitud
		 */
		OSerializer& operator<<(ConstStrNoPrefix);

		/** Debe finalizar con '\0' como toda string en c!
		 */
		OSerializer& operator<<(const char* &);

		/** Inserta cualqiercosa sin prefijo de longitud
		 */
		template<typename T>
		OSerializer& operator<<(const T& t){
			buffer.reserve(buffer.size()+sizeof(T));
			buffer.append((char*) &t, ((char*) &t)+sizeof(T));
			return (*this);
		}

	protected:
		std::string& buffer;
};

/** Input serializer, clase para desserializar utilizando un string como buffer.
 *
 */
class ISerializer {
	public:
		/** Constructor.
		 * @param buffer: buffer a deserializar
		 */
		ISerializer(const std::string&);

		/** Extrae un string con prefijo de longitud del buffer
		 */
		ISerializer& operator>>(std::string&);

		/** Extrae un vector<char> con prefijo de longitud del buffer
		 */
		ISerializer& operator>>(std::vector<char>&);

		/** Extrae un char* con prefijo de longitud del buffer.
		 * Pide memoria al sistema, el usuario debe liberar el puntero.
		 */
		ISerializer& operator>>(char* &);

		/** Extrae un string sin prefijo de longitud del buffer.
		 */
		ISerializer& operator>>(StrNoPrefix);

		/** Ignora.
		 */
		ISerializer& operator>>(Ignore);

		/** Extrae cualqiercosa sin prefijo de longitud del buffer.
		 */
		template<typename T>
		ISerializer& operator>>(T& t){
			size_t size = sizeof(t);
			if(! this->errorCheck(size)){
				std::copy(it, it+size, (char*) &t);
				this->advance(size);
			}
			return (*this);
		}

		/** Comprueba si hubo un error al deserializar
		 * @return bool, true: si hubo error, false: si no.
		 */
		bool error();

	protected:
		const std::string& buffer; ///< Referencia al buffer
		std::string::const_iterator it; ///< Iterador de la posicion actual
		size_t bufferSize; ///< tama~no del buffer restante
		bool _error; ///< Si hubo un error al extraer

		/** Cheque si hay suficiente espacio como para extraer.
		 * Comprueba anteriormente si el ISerializer no se enceuntra en un estado de error.
		 * @param espacio a comprobar.
		 * @return bool: true: si hubo error, false: si no.
		 */
		bool errorCheck(size_t);

		/** Mueve el iterador y disminuye bufferSize.
		 * @param cuanto mueve el iterador (en bytes)
		 */
		void advance(size_t);
};

#endif
