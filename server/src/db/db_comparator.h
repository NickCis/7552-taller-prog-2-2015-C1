#ifndef __DB_COMPARATOR_H__
#define __DB_COMPARATOR_H__

#include <rocksdb/slice.h>
#include <rocksdb/comparator.h>

/** Clase comparadora que implementa un ordenamiento separando el texto por '/' y comparando byte a byte.
 * Es decir, para una clave 't1/t2/.../tn' comparada con 'j1/j2/.../jm', se comparan t1 con j1, t2 con j2, y asi
 * sucesivamente hasta que uno de los dos sea mayor/menor que otro.
 */
class DbComparator : public rocksdb::Comparator {
	public:
		/** Funcion de comparacion (3 vias) "Three-way comparison function":
		 * if a < b: negative result
		 * if a > b: positive result
		 * else: zero result
		 *
		 * @param a: primer termino a comparar
		 * @param b: segundo termino a comparar
		 * @return a < b: valor negativo, a > b: valor positivo, si no: 0
		*/
		int Compare(const rocksdb::Slice& a, const rocksdb::Slice& b) const;

		/** Nombre del comparador. RocksDb lo utiliza para ver si se usa el mismo comparador al levantar la db de archivo.
		 * @return Nombre del comparador
		 */
		const char* Name() const { return "DbComparator"; }

		/** Funcion que obliga a implementar la interfaz, pero la documentacion dice que no hace falta que haga algo.
		 */
		void FindShortestSeparator(std::string*, const rocksdb::Slice&) const { }

		/** Funcion que obliga a implementar la interfaz, pero la documentacion dice que no hace falta que haga algo.
		 */
		void FindShortSuccessor(std::string*) const { }
};

/** Clase comparadora que implementa un ordenamiento separando el texto por '/' y comparando byte a byte.
 * Es decir, para una clave 't1/t2/.../tn' comparada con 'j1/j2/.../jm', se comparan t1 con j1, t2 con j2, y asi
 * sucesivamente hasta que uno de los dos sea mayor/menor que otro. El orden es inverso que DbComparator, salvo
 * que sigue tomando menor cantidad de miembros como menor clave
 */
class DbComparatorReverse : public rocksdb::Comparator {
	public:
		/** Funcion de comparacion (3 vias) "Three-way comparison function":
		 * Compara al inverso que DBComparator, pero, siempre si uno tiene un miembro menos, lo toma como menor
		*/
		int Compare(const rocksdb::Slice& a, const rocksdb::Slice& b) const;

		/** Nombre del comparador. RocksDb lo utiliza para ver si se usa el mismo comparador al levantar la db de archivo.
		 * @return Nombre del comparador
		 */
		const char* Name() const { return "DbComparatorReverse"; }

		/** Funcion que obliga a implementar la interfaz, pero la documentacion dice que no hace falta que haga algo.
		 */
		void FindShortestSeparator(std::string*, const rocksdb::Slice&) const { }

		/** Funcion que obliga a implementar la interfaz, pero la documentacion dice que no hace falta que haga algo.
		 */
		void FindShortSuccessor(std::string*) const { }
};

#endif
